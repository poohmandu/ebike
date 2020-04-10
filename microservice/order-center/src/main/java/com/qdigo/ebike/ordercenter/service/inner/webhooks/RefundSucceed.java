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

package com.qdigo.ebike.ordercenter.service.inner.webhooks;

import com.pingplusplus.exception.*;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Refund;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.order.journal.OrderJournalAccountService;
import com.qdigo.ebike.api.service.third.push.PushService;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.api.service.user.UserRecordService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoSuchEntityException;
import com.qdigo.ebike.common.core.util.ArithUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonaop.annotations.Token;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderCharge;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderRefund;
import com.qdigo.ebike.ordercenter.repository.charge.OrderChargeRepository;
import com.qdigo.ebike.ordercenter.repository.charge.OrderRefundRepository;
import com.qdigo.ebike.ordercenter.service.inner.JournalAccountInnerService;
import com.qdigo.ebike.ordercenter.service.inner.payment.ChargeService;
import com.qdigo.ebike.ordercenter.service.inner.payment.RefundService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.inject.Inject;

/**
 * Created by niezhao on 2017/3/10.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RefundSucceed {

    private final OrderRefundRepository refundRepository;
    private final OrderChargeRepository chargeRepository;
    private final RefundService refundService;
    private final ChargeService chargeService;
    private final UserAccountService accountService;
    private final UserService userService;
    private final UserRecordService userRecordService;
    private final PushService pushService;
    private final JournalAccountInnerService journalAccountInnerService;

    @Resource
    private RefundSucceed self;

    @Token(key = {"orderNo"}, expireSeconds = 60)
    public boolean refundSucceed(Refund refund) {
        //对 orderRefund 表进行更新
        OrderRefund orderRefund = refundRepository.findById(refund.getId()).orElse(null);
        Charge charge;
        try {
            charge = Charge.retrieve(refund.getCharge());
        } catch (AuthenticationException | InvalidRequestException | APIConnectionException | APIException | ChannelException | RateLimitException e) {
            throw new NoSuchEntityException("refundSucceed里接收到没有充值的refund");
        }
        OrderCharge orderCharge = chargeRepository.findById(refund.getCharge())
                .orElseThrow(() -> new NoSuchEntityException("refundSucceed里接收到没有充值的refund"));

        UserAccountDto account = accountService.findById(orderCharge.getUserAccountId());

        if (orderRefund == null) {
            log.warn("数据库没有refund:{},可能为ping++后台退款", refund.getId());
        } else if (orderRefund.isSucceed()) {
            log.debug("退款回调重复了,之前已成功过:{},用户退款状态:{}", orderRefund.getOrderRefundId(), account.getRefundStatus());
            if (Status.RefundStatus.success.getVal().equals(account.getRefundStatus())) {
                return true;
            }
        }
        UserDto user = userService.findById(account.getUserId());

        val payType = orderCharge.getPayType();
        val amount = FormatUtil.fenToYuan(orderRefund.getAmount());

        if (payType == Status.PayType.deposit.getVal()) {
            //更新 userAccount 表数据 这些动作,应该在回调函数里做
            account.setRefundStatus(Status.RefundStatus.success.getVal());
            account.setDeposit(FormatUtil.getMoney(ArithUtil.sub(account.getDeposit(), amount)));

            self.depositCommit(charge, refund, account, amount);

            PushService.Param param = new PushService.Param().setMobileNo(user.getMobileNo())
                    .setDeviceId(user.getDeviceId()).setPushType(Const.PushType.refundSuccess)
                    .setAlert("退款成功,此次退还押金" + amount + "元");
            pushService.pushNotation(param);
        } else if (payType == Status.PayType.rent.getVal()) {
            //租金计算流水
            val startBalance = account.getBalance();
            account.setBalance(FormatUtil.getMoney(ArithUtil.sub(startBalance, amount)));

            self.rentCommit(charge, refund, account, user, startBalance, amount);

            PushService.Param param = new PushService.Param().setMobileNo(user.getMobileNo())
                    .setDeviceId(user.getDeviceId()).setPushType(Const.PushType.refundSuccess)
                    .setAlert("退款成功,退还钱包余额" + amount + "元");
            pushService.pushNotation(param);
        }
        return true;
    }


    @Transactional
    @GlobalTransactional
    public void depositCommit(Charge charge, Refund refund, UserAccountDto account, double amount) {
        //记录用户消息
        userRecordService.insertUserRecord(account.getUserId(), "成功退款" + amount + "元,此时押金为" + account.getDeposit() + "元");
        chargeService.saveOrderCharge(charge, account.getUserAccountId());
        refundService.saveOrderRefund(refund);
        accountService.update(account);
    }


    @Transactional
    @GlobalTransactional
    public void rentCommit(Charge charge, Refund refund, UserAccountDto account, UserDto user, double startBalance, double amount) {

        val param = new OrderJournalAccountService.Param().setAmount(-amount).setStartAccount(startBalance)
                .setMobileNo(user.getMobileNo()).setAgentId(user.getAgentId()).setOrderNo(charge.getOrderNo());
        journalAccountInnerService.insert4Charge(param);

        userRecordService.insertUserRecord(account.getUserId(), "由于特殊情况,退还余额" + amount + "元,此时余额为" + account.getBalance() + "元");
        chargeService.saveOrderCharge(charge, account.getUserAccountId());
        refundService.saveOrderRefund(refund);
        accountService.update(account);
    }

}
