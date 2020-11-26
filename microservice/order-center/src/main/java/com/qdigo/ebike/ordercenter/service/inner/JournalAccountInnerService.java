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

package com.qdigo.ebike.ordercenter.service.inner;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.order.JournalAccountDto;
import com.qdigo.ebike.api.service.order.journal.OrderJournalAccountService;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.ordercenter.domain.entity.JournalAccount;
import com.qdigo.ebike.ordercenter.repository.JournalAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * description: 
 *
 * date: 2020/4/8 7:02 PM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class JournalAccountInnerService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JournalAccountRepository journalAccountRepository;

    private long createId(Status.PayType payType) {
        StringBuilder id = new StringBuilder();
        id.append(payType.getVal());
        String dateStr = FormatUtil.getCurDate();
        String key = Keys.JournalAccount.getKey(dateStr);

        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().increment(key, 1);
        } else {
            redisTemplate.opsForValue().set(key, "1", 1, TimeUnit.DAYS);
        }
        final int num = Integer.parseInt(redisTemplate.opsForValue().get(key));
        log.info("今日的第{}个流水账", num);
        DecimalFormat df = new DecimalFormat("00000000");
        //0 161127 12345678
        id.append(dateStr.substring(2)).append(df.format(num));
        log.debug("生成的订单号为" + id);
        return Long.parseLong(id.toString());
    }

    public JournalAccountDto insert4Charge(OrderJournalAccountService.Param param) {
        if (param.getMobileNo() == null) {
            throw new NullPointerException("user的mobileNo不能为null");
        }
        val payType = Status.PayType.rent;
        JournalAccount journalAccount = new JournalAccount().setMobileNo(param.getMobileNo()).setPayType(payType)
                .setStartAccount(param.getStartAccount()).setOrderNo(param.getOrderNo()).setJournalAccountId(createId(payType))
                .setStartTime(new Date()).setAgentId(param.getAgentId());
        journalAccount.setAmount(param.getAmount()).setEndAccount(param.getStartAccount() + param.getAmount()).setEndTime(new Date());
        log.debug("user:{}创建流水账:{},", param.getMobileNo(), journalAccount);
        journalAccountRepository.save(journalAccount);
        return ConvertUtil.to(journalAccount, JournalAccountDto.class);
    }

    public JournalAccountDto insert4Ride(OrderJournalAccountService.Param param) {
        if (param.getMobileNo() == null) {
            throw new NullPointerException("user的mobileNo不能为null");
        }
        val payType = Status.PayType.consume;
        JournalAccount journalAccount = new JournalAccount().setMobileNo(param.getMobileNo()).setPayType(payType)
                .setStartAccount(param.getStartAccount()).setRideRecordId(param.getRideRecordId()).setJournalAccountId(createId(payType))
                .setStartTime(new Date()).setAgentId(param.getAgentId());
        journalAccount.setAmount(param.getAmount()).setEndAccount(param.getStartAccount() + param.getAmount()).setEndTime(new Date());
        log.debug("user:{}创建流水账:{}", param.getMobileNo(), journalAccount.getJournalAccountId());
        journalAccountRepository.save(journalAccount);
        return ConvertUtil.to(journalAccount, JournalAccountDto.class);
    }

    public JournalAccountDto insert4LongRent(OrderJournalAccountService.Param param) {
        if (param.getMobileNo() == null) {
            throw new NullPointerException("user的mobileNo不能为null");
        }
        if (param.getLongRentId() == null) {
            throw new NullPointerException("longRent的id不能为null");
        }
        val payType = Status.PayType.longRent;
        JournalAccount journalAccount = new JournalAccount().setMobileNo(param.getMobileNo()).setPayType(payType)
                .setStartAccount(param.getStartAccount()).setLongRentId(param.getLongRentId()).setJournalAccountId(createId(payType))
                .setStartTime(new Date()).setAgentId(param.getAgentId());
        journalAccount.setAmount(param.getAmount()).setEndAccount(param.getStartAccount() + param.getAmount()).setEndTime(new Date());
        log.debug("user:{}创建流水账:{}", param.getMobileNo(), journalAccount.getJournalAccountId());
        journalAccountRepository.save(journalAccount);
        return ConvertUtil.to(journalAccount, JournalAccountDto.class);
    }

}
