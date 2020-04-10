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

package com.qdigo.ebike.ordercenter.service.inner.payment;

import com.google.common.collect.ImmutableMap;
import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.*;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Refund;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderCharge;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderRefund;
import com.qdigo.ebike.ordercenter.repository.charge.OrderChargeRepository;
import com.qdigo.ebike.ordercenter.repository.charge.OrderRefundRepository;
import com.qdigo.ebike.ordercenter.service.inner.webhooks.RefundSucceed;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by niezhao on 2016/12/13.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RefundService {

    private final OrderRefundRepository refundRepository;
    private final OrderChargeRepository chargeRepository;
    private final ChargeService chargeService;
    private final RefundSucceed refundSucceed;
    private final UserAccountService accountService;
    private final OrderRideService rideService;

    @Resource
    private RefundService self;


    public ResponseDTO parseCondition(String mobileNo, UserAccountDto userAccount, OrderCharge orderCharge) {

        final String refundStatus = userAccount.getRefundStatus();

        if (userAccount.getDeposit() == 0.0) {
            return new ResponseDTO(400, "用户已经成功退款,当前押金为0元");
        }

        if (refundStatus.equals(Status.RefundStatus.pending.getVal()) ||
                refundStatus.equals(Status.RefundStatus.fail.getVal())) {

            Optional<OrderRefund> optional = refundRepository.findByChargeId(orderCharge.getChargeId()).stream()
                    .filter(r -> !r.isSucceed()).findAny();

            if (optional.isPresent()) {
                OrderRefund orderRefund = optional.get();
                Refund re = retrieve(orderRefund, userAccount.getUserAccountId());
            } else {
                Map<String, Object> result = this.createRefund(userAccount, orderCharge.getChargeId(), FormatUtil.fenToYuan(orderCharge.getAmount()));
                if (!"ok".equals(result.get("result"))) {

                    return new ResponseDTO(400, "退款请求失败:" + result.get("result"));
                } else {
                    Charge charge = (Charge) result.get("charge");
                    Refund refund = (Refund) result.get("refund");
                    //退款请求成功,受理中
                    userAccount.setRefundStatus(Status.RefundStatus.pending.getVal());

                    self.updateRefundStatus(charge, refund, userAccount);
                }
            }
            return new ResponseDTO(400, "退款正在受理中，请勿重复提交。若超过三个工作日未到账，请拨打客服热线。");
        }

        RideDto rideDto = rideService.findRidingByMobileNo(mobileNo);
        if (rideDto != null) {
            return new ResponseDTO(400, "请先还车,再进行退款操作");
        }

        if (userAccount.getBalance() < 0) {
            return new ResponseDTO(400, "您的余额存在欠款，请先充值补回余额");
        }
        return new ResponseDTO(200, "ok");
    }

    @Transactional
    @GlobalTransactional
    public void updateRefundStatus(Charge charge, Refund refund, UserAccountDto account) {
        chargeService.saveOrderCharge(charge, account.getUserAccountId());
        self.saveOrderRefund(refund);
        accountService.update(account);
    }

    public String getRefundUrl(OrderRefund orderRefund, Long accountId) {
        if (!orderRefund.isSucceed() && "refund_wait_operation".equals(orderRefund.getFailureCode())) {
            String date1 = FormatUtil.yMd.format(new Date(orderRefund.getCreated() * 1000));
            String date2 = FormatUtil.getCurDate();
            if (date2.equals(date1)) {//当天的有效
                return StringUtils.substringAfter(orderRefund.getFailureMsg(), ":");
            } else {
                Refund re = retrieve(orderRefund, accountId);
                if (re == null) {
                    return "Exception";
                }
                return StringUtils.substringAfter(re.getFailureMsg(), ":");
            }
        } else {
            String code = orderRefund.getFailureCode();
            if ("failed".equals(orderRefund.getStatus()) && code != null) {
                //特殊原因失败的订单
                Refund re = retrieve(orderRefund, accountId);
            }
            return "";
        }
    }

    public Refund retrieve(OrderRefund orderRefund, Long accountId) {
        Pingpp.apiKey = ConfigConstants.apiKey.getConstant();
        try {
            Charge ch = Charge.retrieve(orderRefund.getChargeId());
            Refund re = ch.getRefunds().retrieve(orderRefund.getOrderRefundId());

            chargeService.saveOrderCharge(ch, accountId);
            if (orderRefund.isSucceed()) {
                if (re.getSucceed()) {
                    orderRefund = saveOrderRefund(re);
                    return re;
                } else {
                    String mobileNo = (String) ch.getMetadata().getOrDefault("mobileNo", "");
                    log.error("{}的ping++数据与商户的server端数据不一致:{}", mobileNo, re);
                    return re;
                }
            } else {
                if (re.getSucceed()) {
                    log.debug("查询到orderRefund退款未成功但是refund退款成功,订单号为{}", ch.getOrderNo());
                    refundSucceed.refundSucceed(re);
                    return re;
                } else {
                    orderRefund = saveOrderRefund(re);
                    return re;
                }
            }
        } catch (AuthenticationException | InvalidRequestException | APIConnectionException | APIException | ChannelException | RateLimitException e) {
            log.error("查询Charge发生异常:", e);
            return null;
        }
    }

    public Map<String, Object> createRefund(UserAccountDto account, String chargeId, double amount) {

        // 设置 API Key
        Pingpp.apiKey = ConfigConstants.apiKey.getConstant();
        Charge charge;
        Refund refund;
        Function<Exception, Map<String, Object>> onFail = e -> {
            log.error("请求ping++失败:", e);
            String err = StringUtils.substringAfter(e.getMessage(), "Error message:");
            chargeRepository.findById(chargeId).ifPresent(oc -> {
                account.setRefundStatus(Status.RefundStatus.fail.getVal());
                accountService.update(account);
            });
            return ImmutableMap.of("result", err);
        };

        try {
            Map<String, Object> params = new HashMap<>();
            //退还用户的所有押金金额
            params.put("amount", FormatUtil.yuanToFen(amount));//单位:分
            params.put("description", "退还用户预留的押金");
            charge = Charge.retrieve(chargeId);
            refund = Refund.create(chargeId, params);
        } catch (Exception er) {
            log.error("第一次请求ping++失败:", er);
            if (er instanceof InvalidRequestException) {
                Pingpp.apiKey = ConfigConstants.apiKey.getConstant();
                try {
                    Map<String, Object> params = new HashMap<>();
                    //退还用户的所有押金金额
                    params.put("amount", FormatUtil.yuanToFen(amount));//单位:分
                    params.put("description", "退还用户预留的押金");
                    charge = Charge.retrieve(chargeId);
                    refund = Refund.create(chargeId, params);
                } catch (Exception e) {
                    return onFail.apply(e);
                }
            } else {
                return onFail.apply(er);
            }
        }
        return ImmutableMap.of("result", "ok", "charge", charge, "refund", refund);
    }

    public OrderRefund fromRefund(Refund refund) {
        return new OrderRefund()
                .setChargeId(refund.getCharge())
                .setAmount(refund.getAmount())
                .setCreated(refund.getCreated())
                .setDescription(refund.getDescription())
                .setFailureCode(refund.getFailureCode())
                .setFailureMsg(refund.getFailureMsg())
                .setOrderNo(refund.getOrderNo())
                .setOrderRefundId(refund.getId())
                .setStatus(refund.getStatus())
                .setSucceed(refund.getSucceed())
                .setTimeSucceed(refund.getTimeSucceed());
    }

    public OrderRefund saveOrderRefund(Refund refund) {
        return refundRepository.save(fromRefund(refund));
    }

}
