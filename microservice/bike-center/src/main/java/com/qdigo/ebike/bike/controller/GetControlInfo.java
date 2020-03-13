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

package com.qdigo.ebike.bike.controller;

import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.domain.entity.BikeGpsStatus;
import com.qdigo.ebike.bike.domain.entity.BikeStatus;
import com.qdigo.ebike.bike.repository.BikeGpsStatusRepository;
import com.qdigo.ebike.bike.repository.BikeRepository;
import com.qdigo.ebike.bike.repository.BikeStatusRepository;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by niezhao on 2017/3/31.
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/ebike")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetControlInfo {

    private final OrderRideService rideService;
    private final BikeRepository bikeRepository;
    private final BikeStatusRepository statusRepository;
    private final BikeGpsStatusRepository gpsStatusRepository;


    @AccessValidate
    @GetMapping(value = "/getForceEndInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getForceEndInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        RideOrder rideOrder = rideRecordDao.findByRidingUser(mobileNo);
        if (rideOrder == null) {
            log.debug("user:{}没有车辆控制权,getForceEndInfo", mobileNo);
            return ResponseEntity.ok(new BaseResponse(400, mobileNo + "用户没有任何车辆控制权"));
        }

        Bike bike = rideOrder.getBike();
        ForceEndService.ForceEndInfo forceEndInfo = this.forceEndService.getForceEndInfo(bike);
        log.debug("获取强制还车信息:{}", forceEndInfo);
        return ResponseEntity.ok(new BaseResponse(200, "成功返回强制还车信息", forceEndInfo));
    }


    @AccessValidate
    @GetMapping(value = "/getBikeDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getBikeDetail(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        RideDto rideDto = rideService.findRidingByMobileNo(mobileNo);
        if (rideDto == null) {
            log.debug("user:{}没有车辆控制权,getBikeDetail", mobileNo);
            return R.ok(400, mobileNo + "用户没有任何车辆控制权");
        }

        Bike bike = bikeRepository.findByImeiId(rideDto.getImei()).get();
        BikeStatus bikeStatus = bike.getBikeStatus();
        long time = System.currentTimeMillis() - rideDto.getStartTime().getTime();
        long min = FormatUtil.minutes(time / 1000);
        RideActivityService.ConsumeDetail consumeDetail = rideActivityService.getConsumeDetail(rideRecord);

        Map<String, Object> res = new ImmutableMap.Builder<String, Object>()
                .put("battery", bikeStatus.getBattery())
                .put("time", min)
                .put("consume", FormatUtil.getMoney(consumeDetail.getConsume()))
                .put("consumeNote", consumeDetail.getConsumeNote())
                .build();

        return ResponseEntity.ok(new BaseResponse(200, "获取车辆骑行详情", res));
    }

    /**
     * doorLock(0|1)(关|开)  locked(0|1)(解锁|上锁)
     * 1--1   报警
     * 1--0   上电
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @return
     */
    @AccessValidate
    @GetMapping(value = "/getControlInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getControlInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        RideOrder rideOrder = rideRecordDao.findByRidingUser(mobileNo);
        if (rideOrder == null) {
            log.debug("user:{}没有车辆控制权,getControlInfo", mobileNo);
            return ResponseEntity.ok(new BaseResponse(400, mobileNo + "用户没有任何车辆控制权", null));
        }

        Bike bike = rideOrder.getBike();
        BikeGpsStatus gps = bike.getGpsStatus();
        Agent agent = bike.getAgent();
        AgentCfg config = agentService.getAgentConfig(agent.getAgentId());

        Map<String, Object> res = new ImmutableMap.Builder<String, Object>()
                .put("imei", bike.getImeiId())
                .put("deviceId", bike.getDeviceId())
                .put("locked", gps.getLocked() == 1)
                .put("on", gps.getDoorLock() == 1)
                .put("freeSeconds", config.getFreeSeconds())
                .put("scanTime", rideOrder.getStartTime().getTime())
                .put("mac", bike.getBleMac())
                .build();

        return ResponseEntity.ok(new BaseResponse(200, "获取车辆控制信息", res));
    }

    @AccessValidate
    @GetMapping(value = "/isControl", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> canControl(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        final RideDto rideDto = rideService.findRidingByMobileNo(mobileNo);
        if (rideDto == null) {
            log.debug("user:{}没有车辆控制权,isControl", mobileNo);
            return R.ok(400, "没有车辆控制权");
        } else {
            log.debug("user:{}拥有车辆控制信息,isControl", mobileNo);
            Bike bike = bikeRepository.findByImeiId(rideDto.getImei()).get();
            BikeStatus status = bike.getBikeStatus();
            final Map<String, Object> map = new ImmutableMap.Builder<String, Object>()
                    .put("scanTime", rideDto.getStartTime().getTime())
                    .put("imei", bike.getImeiId())
                    .put("latitude", status.getLatitude())
                    .put("longitude", status.getLongitude())
                    .put("bikeType", bike.getType())
                    .put("status", status.getStatus())
                    .put("mac", bike.getBleMac())
                    .build();
            return R.ok(200, "获取车辆控制信息", map);
        }

    }

}
