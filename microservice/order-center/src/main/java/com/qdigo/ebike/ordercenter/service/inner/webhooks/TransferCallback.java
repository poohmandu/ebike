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

import com.pingplusplus.model.Transfer;
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
import com.qdigo.ebike.common.core.errors.exception.runtime.NoneMatchException;
import com.qdigo.ebike.common.core.util.ArithUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonaop.annotations.Token;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderCharge;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderTransfer;
import com.qdigo.ebike.ordercenter.repository.charge.OrderChargeRepository;
import com.qdigo.ebike.ordercenter.repository.charge.OrderTransferRepository;
import com.qdigo.ebike.ordercenter.service.inner.JournalAccountInnerService;
import com.qdigo.ebike.ordercenter.service.inner.payment.TransferService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.inject.Inject;

/**
 * Created by niezhao on 2017/10/18.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TransferCallback {

    private final OrderTransferRepository transferRepository;
    private final UserAccountService accountService;
    private final TransferService transferService;
    private final UserRecordService userRecordService;
    private final PushService pushService;
    private final UserService userService;
    private final OrderChargeRepository chargeRepository;
    private final JournalAccountInnerService journalAccountInnerService;
    @Resource
    private TransferCallback self;

    @Token(key = {"orderNo"}, expireSeconds = 60)
    public void transferCallback(Transfer transfer, boolean isSucceed) throws NoneMatchException {
        OrderTransfer one = transferRepository.findById(transfer.getId()).orElse(null);
        if (one != null) {
            log.debug("isSucceed:{}企业付款回调前,查询orderTransfer是否已经成功:{}", isSucceed, one.getStatus());
            if (isSucceed && one.getStatus().equals("paid")) {
                return;
            } else if (!isSucceed && one.getStatus().equals("failed")) {
                return;
            }
        }
        val payType = Integer.parseInt(transfer.getMetadata().get("payType"));
        val mobileNo = transfer.getMetadata().get("mobileNo"); //或者 one.getUserAccount().getUser();
        val amount = FormatUtil.fenToYuan(transfer.getAmount());

        val user = userService.findByMobileNo(mobileNo);
        val account = accountService.findByUserId(user.getUserId());

        if (isSucceed) {
            if (payType == Status.PayType.deposit.getVal()) {
                account.setDeposit(FormatUtil.getMoney(ArithUtil.sub(account.getDeposit(), amount)));
                account.setRefundStatus(Status.RefundStatus.success.getVal());

                self.depositCommit(user, account, one, transfer, amount);

                PushService.Param param = new PushService.Param().setMobileNo(user.getMobileNo())
                        .setDeviceId(user.getDeviceId()).setPushType(Const.PushType.refundSuccess)
                        .setAlert("退款成功,此次退还押金" + amount + "元");
                pushService.pushNotation(param);
            } else if (payType == Status.PayType.rent.getVal()) {
                val startBalance = account.getBalance();
                account.setBalance(FormatUtil.getMoney(ArithUtil.sub(startBalance, amount)));
                OrderCharge orderCharge = chargeRepository.findById(one.getChargeId())
                        .orElseThrow(() -> new NoSuchEntityException("企业付款没有对应的charge:" + one.getChargeId()));

                self.rentCommit(user, account, one, transfer, startBalance, amount, orderCharge);

                PushService.Param param = new PushService.Param().setMobileNo(user.getMobileNo())
                        .setDeviceId(user.getDeviceId()).setPushType(Const.PushType.refundSuccess)
                        .setAlert("退款成功,退还钱包余额" + amount + "元");
                pushService.pushNotation(param);
            } else {
                throw new RuntimeException("支付类型(payType)未知");
            }
            log.debug("user{}成功完成企业付款回调函数,实际支付金额为{}元", mobileNo, amount);
        } else {
            if (payType == Status.PayType.rent.getVal()) {

            } else if (payType == Status.PayType.deposit.getVal()) {
                account.setRefundStatus(Status.RefundStatus.fail.getVal());
                accountService.update(account);
            } else {
                throw new RuntimeException("支付类型(payType)未知");
            }
            log.debug("user{}失败完成企业付款回调函数,实际支付金额为{}元", mobileNo, amount);
        }
    }

    @Transactional
    @GlobalTransactional
    public void depositCommit(UserDto user, UserAccountDto account, OrderTransfer orderTransfer, Transfer transfer, double amount) {

        userRecordService.insertUserRecord(user.getUserId(), "通过企业付款,退还押金" + amount + "元,渠道为" + transfer.getChannel());
        transferService.updateOrderTransfer(orderTransfer, transfer);
        accountService.update(account);

    }

    @Transactional
    @GlobalTransactional
    public void rentCommit(UserDto user, UserAccountDto account, OrderTransfer orderTransfer, Transfer transfer,
                           double startBalance, double amount, OrderCharge orderCharge) {

        val param = new OrderJournalAccountService.Param().setAmount(-amount).setStartAccount(startBalance)
                .setMobileNo(user.getMobileNo()).setAgentId(user.getAgentId()).setOrderNo(orderCharge.getOrderNo());
        journalAccountInnerService.insert4Charge(param);

        userRecordService.insertUserRecord(user.getUserId(), "通过企业付款，退还余额" + amount + "元,渠道为" + transfer.getChannel() + ",此时余额为" + account.getBalance() + "元");
        transferService.updateOrderTransfer(orderTransfer, transfer);
        accountService.update(account);
    }

}
