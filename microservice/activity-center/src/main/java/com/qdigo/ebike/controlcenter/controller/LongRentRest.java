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

package com.qdigo.ebike.controlcenter.controller;

import com.google.common.collect.ImmutableMap;
import com.qdigo.ebicycle.aop.token.AccessValidate;
import com.qdigo.ebicycle.aop.token.Token;
import com.qdigo.ebicycle.constants.Const;
import com.qdigo.ebicycle.domain.agent.Agent;
import com.qdigo.ebicycle.domain.user.UserLongRent;
import com.qdigo.ebicycle.o.ro.BaseResponse;
import com.qdigo.ebicycle.repository.agent.AgentRepository;
import com.qdigo.ebicycle.repository.agent.AgentTakeawayConfigRepository;
import com.qdigo.ebicycle.repository.dao.RideRecordDao;
import com.qdigo.ebicycle.repository.dao.UserLongRentDao;
import com.qdigo.ebicycle.repository.userRepo.UserAccountRepository;
import com.qdigo.ebicycle.repository.userRepo.UserLongRentRepository;
import com.qdigo.ebicycle.repository.userRepo.UserRepository;
import com.qdigo.ebicycle.service.pay.JournalAccountService;
import com.qdigo.ebicycle.service.user.UserService;
import com.qdigo.ebicycle.service.util.FormatUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by niezhao on 2017/7/22.
 */
@RestController
@RequestMapping("/v1.0/ebike/longRent")
@Slf4j
public class LongRentRest {

    @Inject
    private UserRepository userRepository;
    @Inject
    private UserService userService;
    @Inject
    private UserLongRentDao userLongRentDao;
    @Inject
    private AgentRepository agentRepository;
    @Inject
    private UserAccountRepository accountRepository;
    @Inject
    private JournalAccountService journalAccountService;
    @Inject
    private UserLongRentRepository longRentRepository;
    @Inject
    private RideRecordDao rideRecordDao;
    @Inject
    private AgentTakeawayConfigRepository takeawayConfigRepository;

    private static final double dayPrice = 60;

    private static final double weekPrice = 300;

    private static final double monthPrice = 600;

    @AccessValidate
    @GetMapping(value = "/hasLongRent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLongRentAuth(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        val user = userRepository.findOneByMobileNo(mobileNo).get();
        UserLongRent longRent = userLongRentDao.findLastOne(user.getUserId());
        if (longRent != null && longRent.getEndTime().getTime() > System.currentTimeMillis()) {
            Map<String, Object> res = ImmutableMap.of("endTime", FormatUtil.yMdHms.format(longRent.getEndTime()));
            return ResponseEntity.ok(new BaseResponse(400, "已经购买过了", res));
        } else {
            Map<String, Object> res = null;
            if (longRent != null) {
                res = ImmutableMap.of("lastEndTimestamp", longRent.getEndTime().getTime());
            }
            return ResponseEntity.ok(new BaseResponse(200, "可以买骑行卡", res));
        }
    }

    @AccessValidate
    @GetMapping(value = "/longRentList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> longRentList(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        val res = new Res();
        res.setDayPrice(this.getPrice(mobileNo, Const.LongRentType.day));
        res.setWeekPrice(this.getPrice(mobileNo, Const.LongRentType.week));
        res.setMonthPrice(this.getPrice(mobileNo, Const.LongRentType.month));
        return ResponseEntity.ok(new BaseResponse(200, "获得骑行卡价格表", res));
    }

    @AccessValidate
    @GetMapping(value = "/takeawayConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> takeawayConfig(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        log.debug("获取外卖卡价格表");
        Long agentId = userService.getAgentId(mobileNo);
        List<ImmutableMap<Object, Object>> configs = takeawayConfigRepository.findByAgentId(agentId).stream().map(config -> ImmutableMap.builder()
            .put("day", config.getDay())
            .put("price", config.getPrice())
            .put("id", config.getId())
            .build()).collect(Collectors.toList());

        return ResponseEntity.ok(new BaseResponse(200, "获得外卖价格配置", configs));
    }


    @Data
    private static class Res {
        private double dayPrice;
        private double weekPrice;
        private double monthPrice;
    }

    @Token
    @AccessValidate
    @Transactional
    @PostMapping(value = "longRentConsume", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> longRentConsume(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body body) {
        val user = userRepository.findOneByMobileNo(mobileNo).get();
        val hasLongRent = userLongRentDao.hasLongRent(user.getUserId());
        if (hasLongRent) {
            return ResponseEntity.ok(new BaseResponse(400, "已经购买过了"));
        }
        val account = user.getAccount();
        val longRentType = body.getLongRentType();
        val price = this.getPrice(mobileNo, longRentType);
        double consume = FormatUtil.getMoney(1 * price);
        if (account.getBalance() >= consume) {
            if (rideRecordDao.findByRidingUser(mobileNo) != null) {
                return ResponseEntity.ok(new BaseResponse(402, "正在骑行中,请先还车再购买"));
            }
            UserLongRent longRent = new UserLongRent();
            longRent.setAgentId(user.getAgent().getAgentId()).setConsume(consume)
                .setEndTime(new Date(System.currentTimeMillis() + this.milliseconds(longRentType)))
                .setLongRentType(longRentType).setPrice(price).setStartTime(new Date()).setUserId(user.getUserId());
            longRent = longRentRepository.save(longRent);

            val startBalance = account.getBalance();
            journalAccountService.insertJournalAccount(user, startBalance, longRent, -consume);

            account.setBalance(FormatUtil.getMoney(startBalance - consume));
            accountRepository.save(account);
            return ResponseEntity.ok(new BaseResponse(200, "成功购买骑行卡"));

        } else {
            Map<String, Double> res = ImmutableMap.of("consume", consume);
            return ResponseEntity.ok(new BaseResponse(401, "钱包余额不足,请先充值钱包", res));
        }

    }

    @Data
    private static class Body {
        private Const.LongRentType longRentType;
    }

    private long milliseconds(Const.LongRentType longRentType) {
        if (longRentType == Const.LongRentType.day) {
            return 24 * 60 * 60 * 1000;
        } else if (longRentType == Const.LongRentType.week) {
            return 7 * 24 * 60 * 60 * 1000;
        } else if (longRentType == Const.LongRentType.month) {
            return 30 * 24 * 60 * 60 * 1000L;
        } else {
            throw new RuntimeException("未知长租卡" + longRentType);
        }
    }

    private double getPrice(String mobileNo, Const.LongRentType longRentType) {
        val user = userRepository.findOneByMobileNo(mobileNo).get();
        Long agentId = userService.getAgentId(mobileNo);
        Agent agent = null;
        if (agentId != null) {
            agent = agentRepository.findOne(agentId);
        }
        if (longRentType == Const.LongRentType.day) {
            return (agent == null || agent.getLongTermRent() == null) ? dayPrice : agent.getLongTermRent().getDayCard();
        } else if (longRentType == Const.LongRentType.week) {
            return (agent == null || agent.getLongTermRent() == null) ? weekPrice : agent.getLongTermRent().getWeekCard();
        } else if (longRentType == Const.LongRentType.month) {
            return (agent == null || agent.getLongTermRent() == null) ? monthPrice : agent.getLongTermRent().getMonthCard();
        } else {
            throw new RuntimeException("未知长租卡" + longRentType);
        }
    }

}
