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

package com.qdigo.ebike.ordercenter.service.inner.webhooks.chargesucceed;

import com.pingplusplus.model.Charge;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.order.journal.OrderJournalAccountService;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.api.service.user.UserRecordService;
import com.qdigo.ebike.common.core.util.ArithUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.ordercenter.domain.dto.PayBizType;
import com.qdigo.ebike.ordercenter.message.charge.ArrearsChargeEvent;
import com.qdigo.ebike.ordercenter.message.charge.EntityCardChargeEvent;
import com.qdigo.ebike.ordercenter.message.charge.LongRentChargeEvent;
import com.qdigo.ebike.ordercenter.message.charge.TakeawayChargeEvent;
import com.qdigo.ebike.ordercenter.service.inner.JournalAccountInnerService;
import com.qdigo.ebike.ordercenter.service.inner.payment.ChargeService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

/**
 * description: 
 *
 * date: 2020/4/9 12:12 AM
 * @author niezhao
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ChargeSucceedBizService {

    private final JournalAccountInnerService journalAccountInnerService;
    private final ChargeService chargeService;
    private final ApplicationContext context;
    private final UserRecordService userRecordService;
    private final UserAccountService accountService;

    @Transactional(rollbackFor = Throwable.class)
    @GlobalTransactional(rollbackFor = Throwable.class)
    public void rentChargeBiz(BizParam bizParam) {
        UserDto user = bizParam.getUser();
        UserAccountDto account = bizParam.getAccount();
        Charge charge = bizParam.getCharge();
        double startBalance = account.getBalance();
        double amount = FormatUtil.fenToYuan(charge.getAmount());
        String mobileNo = user.getMobileNo();

        account.setBalance(ArithUtil.add(startBalance, amount));

        val param = new OrderJournalAccountService.Param().setAmount(amount).setStartAccount(startBalance)
                .setMobileNo(mobileNo).setAgentId(user.getAgentId()).setOrderNo(charge.getOrderNo());
        journalAccountInnerService.insert4Charge(param);

        Map<String, Object> metadata = charge.getMetadata();
        String bizType = (String) metadata.getOrDefault("bizType", "");

        if (StringUtils.isEmpty(bizType) || PayBizType.balance.name().equals(bizType)) {
            String option = "";
            if (startBalance < 0) {
                option = ",补欠款" + (-startBalance) + "元";
            }
            userRecordService.insertUserRecord(user.getUserId(), "成功支付租金" + amount + "元" + option);
        } else if (PayBizType.entityCard.name().equals(bizType)) {
            String entityCardNo = (String) metadata.get("entityCardNo");

            context.publishEvent(new EntityCardChargeEvent(this, user, account, entityCardNo));
            userRecordService.insertUserRecord(user.getUserId(), "成功支付实体卡" + amount + "元");
        } else if (PayBizType.takeaway.name().equals(bizType)) {
            String id = (String) metadata.get("id");
            String deviceId = (String) metadata.get("deviceId");

            context.publishEvent(new TakeawayChargeEvent(this, user, account, id, deviceId));
            userRecordService.insertUserRecord(user.getUserId(), "成功支付外卖卡" + amount + "元");
        } else if (PayBizType.arrears.name().equals(bizType)) {

            context.publishEvent(new ArrearsChargeEvent(this, user, account));
            userRecordService.insertUserRecord(user.getUserId(), "成功支付骑行欠款" + amount + "元");
        } else if (PayBizType.longRent.name().equals(bizType)) {
            String longRentType = metadata.get("longRentType").toString();
            Double price = Double.parseDouble(metadata.get("price").toString());

            context.publishEvent(new LongRentChargeEvent(this, user, account, longRentType, price));
            userRecordService.insertUserRecord(user.getUserId(), "成功支付长租卡" + amount + "元");
        }

        chargeService.saveOrderCharge(charge, account.getUserAccountId());
        accountService.update(account);
    }

    @Transactional(rollbackFor = Throwable.class)
    @GlobalTransactional(rollbackFor = Throwable.class)
    public void depositChargeBiz(BizParam bizParam) {
        UserDto user = bizParam.getUser();
        UserAccountDto account = bizParam.getAccount();
        Charge charge = bizParam.getCharge();
        double amount = FormatUtil.fenToYuan(charge.getAmount());

        account.setDeposit(ArithUtil.add(account.getDeposit(), amount));
        //记录用户消息
        userRecordService.insertUserRecord(user.getUserId(), "成功支付押金" + amount + "元,用户押金总额为" + account.getDeposit() + "元");
        accountService.update(account);
    }


}

@Value
class BizParam {
    private UserDto user;
    private UserAccountDto account;
    private Charge charge;
}
