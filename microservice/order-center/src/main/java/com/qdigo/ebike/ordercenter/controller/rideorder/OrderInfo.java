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
import com.qdigo.ebicycle.constants.Status;
import com.qdigo.ebicycle.domain.order.OrderCharge;
import com.qdigo.ebicycle.domain.order.OrderWxscore;
import com.qdigo.ebicycle.domain.ride.RideRecord;
import com.qdigo.ebicycle.domain.user.UserAccount;
import com.qdigo.ebicycle.o.ro.BaseResponse;
import com.qdigo.ebicycle.repository.bikeRepo.BikeRepository;
import com.qdigo.ebicycle.repository.orderRepo.OrderChargeRepository;
import com.qdigo.ebicycle.repository.ride.RideRecordRepository;
import com.qdigo.ebicycle.repository.userRepo.UserAccountRepository;
import com.qdigo.ebicycle.repository.userRepo.UserRepository;
import com.qdigo.ebicycle.service.pay.ChargeService;
import com.qdigo.ebicycle.service.ride.RideActivityService;
import com.qdigo.ebicycle.service.ride.RideWxscoreService;
import com.qdigo.ebicycle.service.util.FormatUtil;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by niezhao on 2016/11/28.
 */
@RestController
@RequestMapping("/v1.0/payment")
public class OrderInfo {

    private final Logger logger = LoggerFactory.getLogger(OrderInfo.class);
    @Inject
    private RideRecordRepository rideRecordRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private BikeRepository bikeRepository;
    @Inject
    private UserAccountRepository userAccountRepository;
    @Inject
    private OrderChargeRepository chargeRepository;
    @Inject
    private ChargeService chargeService;
    @Inject
    private RideActivityService rideActivityService;
    @Resource
    private RideWxscoreService rideWxscoreService;


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
    public ResponseEntity<?> getOrderInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @PathVariable Long orderId) {

        RideRecord rideRecord = rideRecordRepository.findOne(orderId);

        if (rideRecord == null) {
            return ResponseEntity.ok(new BaseResponse(400, "不存在该骑行记录", null));
        }
        String consumeNote = "无";
        if (rideRecord.getRideStatus() == Status.RideStatus.end.getVal()) {
            RideActivityService.ConsumeDetail consumeDetail = rideActivityService.getConsumeDetail(rideRecord);
            consumeNote = consumeDetail.getConsumeNote();
        }
        String outOrderNo = rideWxscoreService.hasRideWxscoreOrder(rideRecord)
            .map(OrderWxscore::getOutOrderNo)
            .orElse("");

        return ResponseEntity.ok(new BaseResponse(200, "成功获得已支付订单的详细信息", Res.build(rideRecord, consumeNote, outOrderNo)));
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
    public ResponseEntity<?> getChargeList(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {
        logger.debug("getCharges获取用户{}的充值余额记录", mobileNo);
        Optional<UserAccount> userAccount = userAccountRepository.findByUserMobileNo(mobileNo);
        if (!userAccount.isPresent()) {
            return ResponseEntity.ok(new BaseResponse(400, "不存在该用户", null));
        }
        val res = chargeRepository.findByUserAccountAndPayType(userAccount.get(), Status.PayType.rent.getVal())
            .stream()
            .sorted(Comparator.comparing(OrderCharge::getTimePaid))
            .map(orderCharge -> ImmutableMap.of(
                "amount", String.valueOf(FormatUtil.fenToYuan(orderCharge.getAmount())),
                "time", FormatUtil.yMdHms.format(new Date(orderCharge.getTimePaid() * 1000)),
                "channel", orderCharge.getChannel()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(new BaseResponse(200, "成功获得支付明细", res));
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
    public ResponseEntity<?> hasRentCharge(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {
        return userRepository.findOneByMobileNo(mobileNo).map(user -> {
            boolean hasRentCharges = chargeService.hasRentCharges(user);
            if (hasRentCharges) {
                return ResponseEntity.ok(new BaseResponse(200, "有过租金支付"));
            } else {
                return ResponseEntity.ok(new BaseResponse(201, "没有支付过"));
            }
        }).orElse(ResponseEntity.ok(new BaseResponse(400, "无效用户")));
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

        public static Res build(RideRecord rideRecord, String consumeNote, String outOrderNo) {
            return Res.builder().consume(rideRecord.getConsume())
                .deviceId(rideRecord.getBike().getDeviceId())
                .endTime(rideRecord.getEndTime())
                .minutes(FormatUtil.minutes(Duration.between(rideRecord.getStartTime().toInstant(),
                    rideRecord.getEndTime().toInstant()).getSeconds()))
                .orderNo("")
                .price(rideRecord.getPrice() + "元/" + rideRecord.getUnitMinutes() + "分钟")
                .startTime(rideRecord.getStartTime())
                .type(rideRecord.getBike().getType())
                .consumeNote(consumeNote)
                .outOrderNo(outOrderNo)
                .build();
        }

    }

}
