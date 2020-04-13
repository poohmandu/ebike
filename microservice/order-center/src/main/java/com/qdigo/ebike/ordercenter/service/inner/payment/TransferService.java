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

import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.*;
import com.pingplusplus.model.Transfer;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoSuchEntityException;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoneMatchException;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderCharge;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderTransfer;
import com.qdigo.ebike.ordercenter.repository.charge.OrderChargeRepository;
import com.qdigo.ebike.ordercenter.repository.charge.OrderTransferRepository;
import com.qdigo.ebike.ordercenter.service.inner.webhooks.TransferCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by niezhao on 2017/10/18.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TransferService {

    private final OrderTransferRepository transferRepository;
    private final OrderChargeRepository chargeRepository;
    private final ChargeService chargeService;
    @Resource
    private TransferCallback transferCallback;

    public Transfer createTransfer(String mobileNo, UserAccountDto account, int payType, String description) {
        OrderCharge orderCharge = chargeRepository.findNoRefundByUserAccountIdAndPayType(account.getUserAccountId(), payType).stream()
                .filter(o -> {
                    Optional<OrderTransfer> optional = transferRepository.findByChargeId(o.getChargeId());
                    return !optional.isPresent() || !optional.get().getStatus().equals("paid");
                })
                .findAny().orElseThrow(() -> new NoSuchEntityException("未查询到指定orderCharge"));

        return this.createTransfer(mobileNo, account, payType, description, orderCharge);
    }

    public Transfer createTransfer(String mobileNo, UserAccountDto account, int payType, String description, OrderCharge orderCharge) {
        String channel = "";
        String recipient = "";
        int amount;
        if (orderCharge != null) {
            channel = orderCharge.getChannel();
            recipient = orderCharge.getPayAccount();
        }
        if (payType == Status.PayType.deposit.getVal()) {
            amount = FormatUtil.yuanToFen(account.getDeposit());
        } else if (payType == Status.PayType.rent.getVal()) {
            amount = FormatUtil.yuanToFen(account.getBalance());
        } else {
            throw new RuntimeException("payType为位置类型:" + payType);
        }
        return createTransfer(mobileNo, account.getUserAccountId(), amount, channel, recipient, payType, description, orderCharge.getChargeId());
    }

    public Transfer createTransfer(String mobileNo, Long userAccountId, int amount, String channel,
                                   String recipient, int payType, String description, String chargeId) {
        if (StringUtils.isEmpty(recipient)) {
            throw new RuntimeException("企业付款接口recipient不能为null");
        }
        Pingpp.apiKey = ConfigConstants.apiKey.getConstant();
        Pingpp.privateKeyPath = null;
        Map<String, Object> transferMap = new HashMap<>();
        transferMap.put("channel", channel); //单笔转账到支付宝账户
        val orderNo = chargeService.createOrderNo(payType);
        transferMap.put("order_no", orderNo);
        transferMap.put("amount", amount); //订单总金额, 人民币单位：分（如订单总金额为 1 元，此处请填 100）
        transferMap.put("type", "b2c");
        transferMap.put("currency", "cny");

        transferMap.put("recipient", recipient);//若 type 为 b2c，为个人支付宝账号，若 type 为 b2b，为企业支付宝账号。
        transferMap.put("description", description != null ? description : "电滴出行的企业付款");
        Map<String, String> app = new HashMap<>();
        app.put("id", ConfigConstants.appId.getConstant());
        transferMap.put("app", app);
        Map<String, Object> extra = new HashMap<>();
        // 20190612阿里user_id各平台相同
        if (Status.PayChannel.alipay.getVal().equals(channel) || Status.PayChannel.alipay_lite.getVal().equals(channel)) {
            extra.put("recipient_account_type", "ALIPAY_USERID"); //2088 开头的 16 位纯数字组成
        }
        transferMap.put("extra", extra);
        // metadata 为用户自己定义 key-value
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("payType", payType);
        metadata.put("mobileNo", mobileNo);
        transferMap.put("metadata", metadata);

        Transfer transfer;
        try {
            transfer = Transfer.create(transferMap);
        } catch (AuthenticationException | InvalidRequestException | APIConnectionException | APIException | ChannelException | RateLimitException e) {
            log.error("recipient:{},调用ping++生成transfer对象异常:{}", recipient, e.getMessage());
            throw new RuntimeException(e);
        }
        log.debug("调用ping++返回参数transfer:{}", transfer);

        this.createOrderTransfer(transfer, chargeId, userAccountId);
        return transfer;
    }

    public Transfer retrieveTransfer(OrderTransfer orderTransfer) throws NoneMatchException {
        Pingpp.apiKey = ConfigConstants.apiKey.getConstant();
        try {
            Transfer tr = Transfer.retrieve(orderTransfer.getTransferId());
            if (tr.getStatus().equals("paid")) {
                transferCallback.transferCallback(tr, true);
            } else if (tr.getStatus().equals("failed")) {
                transferCallback.transferCallback(tr, false);
            } else {
                updateOrderTransfer(orderTransfer, tr);
                return tr;
            }
            return tr;
        } catch (AuthenticationException | InvalidRequestException | APIConnectionException | APIException | ChannelException | RateLimitException e) {
            log.error("查询Transfer发生异常" + e.getMessage());
            return null;
        }
    }

    public OrderTransfer createOrderTransfer(Transfer transfer, String chargeId, Long userAccountId) {
        Map<String, String> metadata = transfer.getMetadata();
        OrderTransfer orderTransfer = new OrderTransfer()
                .setAmount(transfer.getAmount())
                .setAmountSettle(transfer.getAmountSettle())
                .setApp(transfer.getApp().toString())
                .setChannel(transfer.getChannel())
                .setCreated(transfer.getCreated())
                .setCurrency(transfer.getCurrency())
                .setDescription(transfer.getDescription())
                .setFailureMsg(transfer.getFailureMsg() != null ? transfer.getFailureMsg() : "")
                .setLivemode(transfer.getLivemode())
                .setOrderNo(transfer.getOrderNo())
                .setRecipient(transfer.getRecipient())
                .setStatus(transfer.getStatus())
                .setTimeTransferred(transfer.getTimeTransferred() != null ? transfer.getTimeTransferred() : 0)
                .setTransactionNo(transfer.getTransaction_no() != null ? transfer.getTransaction_no() : "")
                .setTransferId(transfer.getId())
                .setType(transfer.getType())
                .setPayType(Integer.parseInt(metadata.get("payType")))
                .setUserAccountId(userAccountId)
                .setChargeId(chargeId != null ? chargeId : "");
        return transferRepository.save(orderTransfer);
    }

    @Transactional
    public OrderTransfer updateOrderTransfer(OrderTransfer orderTransfer, Transfer transfer) {
        orderTransfer.setTransactionNo(transfer.getTransaction_no() != null ? transfer.getTransaction_no() : "")
                .setType(transfer.getType())
                .setTransferId(transfer.getId())
                .setTimeTransferred(transfer.getTimeTransferred())
                .setStatus(transfer.getStatus())
                .setRecipient(transfer.getRecipient())
                .setOrderNo(transfer.getOrderNo())
                .setLivemode(transfer.getLivemode())
                .setFailureMsg(transfer.getFailureMsg() != null ? transfer.getFailureMsg() : "")
                .setDescription(transfer.getDescription())
                .setCurrency(transfer.getCurrency())
                .setCreated(transfer.getCreated())
                .setChannel(transfer.getChannel())
                .setApp(transfer.getApp().toString())
                .setAmountSettle(transfer.getAmountSettle())
                .setAmount(transfer.getAmount());
        //.setPayType()
        //.setUserAccount();
        return transferRepository.save(orderTransfer);
    }


}
