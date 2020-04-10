/*
 * Copyright 2020 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.ordercenter.controller.payment;

import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Refund;
import com.pingplusplus.model.Transfer;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.commonaop.annotations.Token;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderCharge;
import com.qdigo.ebike.ordercenter.repository.charge.OrderChargeRepository;
import com.qdigo.ebike.ordercenter.service.inner.payment.RefundService;
import com.qdigo.ebike.ordercenter.service.inner.payment.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2016/12/2.
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/payment")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RefundMoney {

    private final RefundService refundService;
    private final UserAccountService accountService;
    private final OrderChargeRepository chargeRepository;
    private final TransferService transferService;

    @Token
    @AccessValidate
    @PostMapping(value = "/refund", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> refunds(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        // 是否达到退款条件的逻辑判断
        UserAccountDto account = accountService.findByMobileNo(mobileNo);

        List<OrderCharge> chargesNotRefunds = chargeRepository.findDepositNotRefund(account.getUserAccountId());
        if (chargesNotRefunds == null || chargesNotRefunds.isEmpty()) {
            return R.ok(400, "未查询到指定orderCharge");
        }
        OrderCharge orderCharge = chargesNotRefunds.get(0);

        ResponseDTO res = refundService.parseCondition(mobileNo, account, orderCharge);
        if (res.isNotSuccess()) {
            return res.toResponse();
        }

        if (this.timeout(orderCharge)) {
            log.debug("{}退款超时,超时订单为:{}", mobileNo, orderCharge.getOrderNo());
            // 20190612 增加参数chargeId,减去重复查询过程 又取消了
            Transfer transfer = transferService.createTransfer(mobileNo, account, Status.PayType.deposit.getVal(), "退还骑行前支付的押金");
            if (transfer.getStatus().equals("pending")) {
                account.setRefundStatus(Status.RefundStatus.pending.getVal());
                accountService.update(account);
                return R.ok(200, "退款请求成功");
            } else {
                account.setRefundStatus(Status.RefundStatus.fail.getVal());
                accountService.update(account);
                return R.ok(401, "退款请求失败:" + transfer.getFailureMsg());
            }
        }
        // 退款回调由deposit清0改为减去数额，所以这里也要将金额改为charge数额而不是用户总押金数
        Map<String, Object> result = refundService.createRefund(account, orderCharge.getChargeId(), FormatUtil.fenToYuan(orderCharge.getAmount()));

        if (!"ok".equals(result.get("result"))) {
            return R.ok(401, "退款请求失败:" + result.get("result"));
        }
        Charge charge = (Charge) result.get("charge");
        Refund refund = (Refund) result.get("refund");
        log.debug("{}发起退款请求成功得到的refund:{}", mobileNo, refund);

        if (refund.getStatus().equals("failed")) {
            val failMsg = StringUtils.substringAfterLast(refund.getFailureMsg(), "信息：");
            account.setRefundStatus(Status.RefundStatus.fail.getVal());

            refundService.updateRefundStatus(charge, refund, account);
            return R.ok(401, "退款请求失败:" + failMsg);
        }
        // refund.getStatus():1.可能立马返回succeeded 2.可能返回pending.这里都为pending,在回调判断
        account.setRefundStatus(Status.RefundStatus.pending.getVal());

        refundService.updateRefundStatus(charge, refund, account);
        log.debug("修改用户押金状态为:" + account.getRefundStatus());

        //执行业务逻辑
        //更新order , orderRefund表数据
        String prefix = "需要打开地址进行下一步退款操作:";
        String failureMsg = refund.getFailureMsg();
        if (failureMsg != null && failureMsg.contains(prefix)) {
            //支付宝退款
            String url = failureMsg.substring(failureMsg.indexOf(":"));
            return R.ok(201, "支付宝退款请求成功,但需要后台人工确认", url);
        } else {
            return R.ok(200, "退款请求成功");
        }
    }

    private boolean timeout(OrderCharge orderCharge) {
        long created = orderCharge.getCreated() * 1000;
        long now = System.currentTimeMillis();
        long days = TimeUnit.MILLISECONDS.toDays(now - created);
        if (orderCharge.getChannel().equals(Status.PayChannel.alipay.getVal())) {
            return days >= 90;
        } else if (orderCharge.getChannel().equals(Status.PayChannel.wx.getVal()) ||
                orderCharge.getChannel().equals(Status.PayChannel.wx_pub.getVal()) ||
                orderCharge.getChannel().equals(Status.PayChannel.wx_lite.getVal())) {
            return days >= 365;
        } else if (EnumUtils.isValidEnum(Status.PayChannel.class, orderCharge.getChannel())) {
            return false;
        } else {
            throw new RuntimeException("未知的支付渠道:" + orderCharge.getChannel());
        }
    }
}
