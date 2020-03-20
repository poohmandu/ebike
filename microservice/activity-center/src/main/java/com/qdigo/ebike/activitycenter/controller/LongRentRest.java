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

package com.qdigo.ebike.activitycenter.controller;

import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.api.domain.dto.agent.AgentLongRentConfigDto;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.agent.AgentLongRentConfigService;
import com.qdigo.ebike.api.service.agent.AgentTakeawayConfigService;
import com.qdigo.ebike.api.service.order.journal.OrderJournalAccountService;
import com.qdigo.ebike.api.service.order.longrent.OrderLongRentService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.commonaop.annotations.Token;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
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
@Slf4j
@RestController
@RequestMapping("/v1.0/ebike/longRent")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LongRentRest {

    private final UserService userService;
    private final UserAccountService userAccountService;
    private final OrderLongRentService longRentService;
    private final AgentLongRentConfigService longRentConfigService;
    private final AgentTakeawayConfigService agentTakeawayConfigService;
    private final OrderRideService rideService;
    private final OrderJournalAccountService journalAccountService;


    private static final double dayPrice = 60;

    private static final double weekPrice = 300;

    private static final double monthPrice = 600;

    @AccessValidate
    @GetMapping(value = "/hasLongRent", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getLongRentAuth(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        UserDto userDto = userService.findByMobileNo(mobileNo);

        val longRentDto = longRentService.findLastOne(userDto.getUserId());

        if (longRentDto != null && longRentDto.getEndTime().getTime() > System.currentTimeMillis()) {
            Map<String, Object> res = ImmutableMap.of("endTime", FormatUtil.yMdHms.format(longRentDto.getEndTime()));
            return R.ok(400, "已经购买过了", res);
        } else {
            Map<String, Object> res = null;
            if (longRentDto != null) {
                res = ImmutableMap.of("lastEndTimestamp", longRentDto.getEndTime().getTime());
            }
            return R.ok(200, "可以买骑行卡", res);
        }
    }

    @AccessValidate
    @GetMapping(value = "/longRentList", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> longRentList(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        val res = new Res();
        res.setDayPrice(this.getPrice(mobileNo, Const.LongRentType.day));
        res.setWeekPrice(this.getPrice(mobileNo, Const.LongRentType.week));
        res.setMonthPrice(this.getPrice(mobileNo, Const.LongRentType.month));
        return R.ok(200, "获得骑行卡价格表", res);
    }

    @AccessValidate
    @GetMapping(value = "/takeawayConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> takeawayConfig(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        log.debug("获取外卖卡价格表");
        UserDto userDto = userService.findByMobileNo(mobileNo);
        Long agentId = userDto.getAgentId();
        List<ImmutableMap<Object, Object>> configs = agentTakeawayConfigService.findByAgentId(agentId).stream().map(config -> ImmutableMap.builder()
                .put("day", config.getDay())
                .put("price", config.getPrice())
                .put("id", config.getId())
                .build()).collect(Collectors.toList());

        return R.ok(200, "获得外卖价格配置", configs);
    }


    @Data
    private static class Res {
        private double dayPrice;
        private double weekPrice;
        private double monthPrice;
    }

    //rebuild:分布式事务
    @Token
    @AccessValidate
    @Transactional
    @PostMapping(value = "longRentConsume", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> longRentConsume(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body body) {

        UserDto userDto = userService.findByMobileNo(mobileNo);
        boolean hasLongRent = longRentService.hasLongRent(userDto.getUserId());
        if (hasLongRent) {
            return R.ok(400, "已经购买过了");
        }
        UserAccountDto accountDto = userAccountService.findByUserId(userDto.getUserId());
        val longRentType = body.getLongRentType();
        val price = this.getPrice(mobileNo, longRentType);
        double consume = FormatUtil.getMoney(1 * price);
        if (accountDto.getBalance() >= consume) {
            if (rideService.findRidingByMobileNo(mobileNo) != null) {
                return R.ok(402, "正在骑行中,请先还车再购买");
            }
            OrderLongRentService.LongRentDto longRentDto = new OrderLongRentService.LongRentDto()
                    .setAgentId(userDto.getAgentId()).setConsume(consume)
                    .setEndTime(new Date(System.currentTimeMillis() + this.milliseconds(longRentType)))
                    .setLongRentType(longRentType).setPrice(price).setStartTime(new Date()).setUserId(userDto.getUserId());
            longRentDto = longRentService.create(longRentDto);

            val startBalance = accountDto.getBalance();
            OrderJournalAccountService.Param param = new OrderJournalAccountService.Param().setMobileNo(userDto.getMobileNo())
                    .setStartAccount(startBalance).setLongRentId(longRentDto.getId()).setAmount(-consume);
            journalAccountService.insert4LongRent(param);

            accountDto.setBalance(FormatUtil.getMoney(startBalance - consume));
            userAccountService.update(accountDto);
            return R.ok(200, "成功购买骑行卡");

        } else {
            Map<String, Double> res = ImmutableMap.of("consume", consume);
            return R.ok(401, "钱包余额不足,请先充值钱包", res);
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
        UserDto userDto = userService.findByMobileNo(mobileNo);

        Long agentId = userDto.getAgentId();

        if (longRentType == Const.LongRentType.day) {
            return new DayPrice(longRentConfigService, agentId).price();
        } else if (longRentType == Const.LongRentType.week) {
            return new WeekPrice(longRentConfigService, agentId).price();
        } else if (longRentType == Const.LongRentType.month) {
            return new MonthPrice(longRentConfigService, agentId).price();
        } else {
            throw new RuntimeException("未知长租卡" + longRentType);
        }
    }

    @RequiredArgsConstructor
    private abstract class PriceTemplate {

        private final AgentLongRentConfigService longRentConfigService;
        private final Long agentId;

        private AgentLongRentConfigDto longRentConfigDto;

        private boolean isNull() {
            if (agentId == null) {
                return true;
            }
            longRentConfigDto = longRentConfigService.findByAgentId(agentId);
            return longRentConfigDto == null;
        }

        protected abstract double getDefault();

        protected abstract double getPrice();

        //模板模式
        public final double price() {
            if (isNull()) {
                return getDefault();
            }
            return getPrice();
        }
    }

    private class DayPrice extends PriceTemplate {
        public DayPrice(AgentLongRentConfigService longRentConfigService, Long agentId) {
            super(longRentConfigService, agentId);
        }


        @Override
        protected double getDefault() {
            return dayPrice;
        }

        @Override
        protected double getPrice() {
            return super.longRentConfigDto.getDayCard();
        }
    }

    private class WeekPrice extends PriceTemplate {

        public WeekPrice(AgentLongRentConfigService longRentConfigService, Long agentId) {
            super(longRentConfigService, agentId);
        }

        @Override
        protected double getDefault() {
            return weekPrice;
        }

        @Override
        protected double getPrice() {
            return super.longRentConfigDto.getWeekCard();
        }
    }

    private class MonthPrice extends PriceTemplate {

        public MonthPrice(AgentLongRentConfigService longRentConfigService, Long agentId) {
            super(longRentConfigService, agentId);
        }

        @Override
        protected double getDefault() {
            return monthPrice;
        }

        @Override
        protected double getPrice() {
            return super.longRentConfigDto.getMonthCard();
        }
    }

}
