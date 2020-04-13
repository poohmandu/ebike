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

package com.qdigo.ebike.ordercenter.controller.rideorder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.ConsumeDetail;
import com.qdigo.ebike.api.domain.dto.order.wxscore.WxscoreDto;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.api.service.order.ride.RideFreeActivityService;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderCharge;
import com.qdigo.ebike.ordercenter.repository.charge.OrderChargeRepository;
import com.qdigo.ebike.ordercenter.service.inner.payment.ChargeService;
import com.qdigo.ebike.ordercenter.service.remote.wxscore.OrderWxscoreBizServiceImpl;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by niezhao on 2016/11/28.
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/payment")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OrderInfo {

    private final OrderRideService rideService;
    private final RideFreeActivityService freeActivityService;
    private final UserService userService;
    private final AgentConfigService agentConfigService;
    private final OrderWxscoreBizServiceImpl wxscoreBizService;
    private final BikeService bikeService;
    private final UserAccountService accountService;
    private final OrderChargeRepository chargeRepository;
    private final ChargeService chargeService;

    /**
     * 某个订单的详情
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param orderId
     * @return
     */
    @AccessValidate
    @GetMapping(value = "/getOrderInfo/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getOrderInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @PathVariable Long orderId) {

        RideDto rideRecord = rideService.findById(orderId);

        if (rideRecord == null) {
            return R.ok(400, "不存在该骑行记录");
        }
        String consumeNote = "无";
        if (rideRecord.getRideStatus() == Status.RideStatus.end.getVal()) {
            AgentCfg config = agentConfigService.getAgentConfig(rideRecord.getAgentId());
            UserService.UserAndAccount userAndAccount = userService.findWithAccountByMobileNo(mobileNo);

            val detailParam = new RideFreeActivityService.DetailParam().setAccountDto(userAndAccount.getAccountDto())
                    .setAgentCfg(config).setRideDto(rideRecord).setUserDto(userAndAccount.getUserDto());
            ConsumeDetail consumeDetail = freeActivityService.getConsumeDetail(detailParam);
            consumeNote = consumeDetail.getConsumeNote();
        }
        String outOrderNo = "";
        WxscoreDto wxscoreDto = wxscoreBizService.hasRideWxscoreOrder(rideRecord.getRideRecordId());
        if (wxscoreDto != null) {
            outOrderNo = wxscoreDto.getOutOrderNo();
        }
        BikeDto bikeDto = bikeService.findByImei(rideRecord.getImei());
        return R.ok(200, "成功获得已支付订单的详细信息", Res.build(rideRecord, bikeDto, consumeNote, outOrderNo));
    }

    /**
     * 获取充值(余额) 列表
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @return
     */
    @GetMapping(value = "/getCharges", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getChargeList(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {
        log.debug("getCharges获取用户{}的充值余额记录", mobileNo);

        UserAccountDto userAccount = accountService.findByMobileNo(mobileNo);
        if (userAccount == null) {
            return R.ok(400, "不存在该用户");
        }

        val res = chargeRepository.findByUserAccountIdAndPayType(userAccount.getUserAccountId(), Status.PayType.rent.getVal())
                .stream()
                .sorted(Comparator.comparing(OrderCharge::getTimePaid))
                .map(orderCharge -> ImmutableMap.of(
                        "amount", String.valueOf(FormatUtil.fenToYuan(orderCharge.getAmount())),
                        "time", FormatUtil.yMdHms.format(new Date(orderCharge.getTimePaid() * 1000)),
                        "channel", orderCharge.getChannel()))
                .collect(Collectors.toList());

        return R.ok(200, "成功获得支付明细", res);
    }


    /**
     * 是否有过租金充值
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @return
     */
    @GetMapping(value = "/hasRentCharge", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> hasRentCharge(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        UserAccountDto userAccount = accountService.findByMobileNo(mobileNo);
        if (userAccount == null) {
            return R.ok(400, "无效用户");
        }
        boolean hasRentCharges = chargeService.hasRentCharges(userAccount.getUserAccountId());
        if (hasRentCharges) {
            return R.ok(200, "有过租金支付");
        } else {
            return R.ok(201, "没有支付过");
        }
    }

    @Data
    @Builder
    private static class Res {
        private String orderNo;
        private double consume;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date startTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date endTime;
        private int minutes;
        private String type;
        private String price;
        private String deviceId;
        private String consumeNote;
        private String outOrderNo;

        public static Res build(RideDto rideRecord, BikeDto bikeDto, String consumeNote, String outOrderNo) {
            return Res.builder().consume(rideRecord.getConsume())
                    .deviceId(bikeDto.getDeviceId())
                    .endTime(rideRecord.getEndTime())
                    .minutes(FormatUtil.minutes(Duration.between(rideRecord.getStartTime().toInstant(),
                            rideRecord.getEndTime().toInstant()).getSeconds()))
                    .orderNo("")
                    .price(rideRecord.getPrice() + "元/" + rideRecord.getUnitMinutes() + "分钟")
                    .startTime(rideRecord.getStartTime())
                    .type(bikeDto.getType())
                    .consumeNote(consumeNote)
                    .outOrderNo(outOrderNo)
                    .build();
        }

    }

}
