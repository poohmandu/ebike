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
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeGpsStatusDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.rideforceend.ForceEndInfo;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.ConsumeDetail;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.bike.BikeGpsStatusService;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.api.service.order.ride.RideForceEndService;
import com.qdigo.ebike.api.service.order.ride.RideFreeActivityService;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
    private final BikeService bikeService;
    private final BikeStatusService statusService;
    private final RideForceEndService forceEndService;
    private final RideFreeActivityService freeActivityService;
    private final UserService userService;
    private final AgentConfigService agentConfigService;
    private final UserAccountService accountService;
    private final BikeGpsStatusService gpsStatusService;


    @AccessValidate
    @GetMapping(value = "/getForceEndInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getForceEndInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        RideDto rideOrder = rideService.findRidingByMobileNo(mobileNo);
        if (rideOrder == null) {
            log.debug("user:{}没有车辆控制权,getForceEndInfo", mobileNo);
            return R.ok(400, mobileNo + "用户没有任何车辆控制权");
        }
        BikeStatusDto statusDto = statusService.findByImei(rideOrder.getImei());
        RideForceEndService.Param param = new RideForceEndService.Param()
                .setStatusDto(statusDto).setAgentId(rideOrder.getAgentId());
        ForceEndInfo forceEndInfo = this.forceEndService.getForceEndInfo(param);
        log.debug("获取强制还车信息:{}", forceEndInfo);
        return R.ok(200, "成功返回强制还车信息", forceEndInfo);
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

        UserDto userDto = userService.findByMobileNo(mobileNo);
        UserAccountDto accountDto = accountService.findByUserId(userDto.getUserId());
        AgentCfg config = agentConfigService.getAgentConfig(rideDto.getAgentId());
        BikeStatusDto bikeStatus = statusService.findByImei(rideDto.getImei());

        long time = System.currentTimeMillis() - rideDto.getStartTime().getTime();
        long min = FormatUtil.minutes(time / 1000);

        RideFreeActivityService.DetailParam detailParam = new RideFreeActivityService.DetailParam()
                .setUserDto(userDto).setRideDto(rideDto).setAgentCfg(config).setAccountDto(accountDto);
        ConsumeDetail consumeDetail = freeActivityService.getConsumeDetail(detailParam);

        Map<String, Object> res = new ImmutableMap.Builder<String, Object>()
                .put("battery", bikeStatus.getBattery())
                .put("time", min)
                .put("consume", FormatUtil.getMoney(consumeDetail.getConsume()))
                .put("consumeNote", consumeDetail.getConsumeNote())
                .build();

        return R.ok(200, "获取车辆骑行详情", res);
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

        RideDto rideOrder = rideService.findRidingByMobileNo(mobileNo);
        if (rideOrder == null) {
            log.debug("user:{}没有车辆控制权,getControlInfo", mobileNo);
            return R.ok(400, mobileNo + "用户没有任何车辆控制权");
        }

        BikeDto bike = bikeService.findByImei(rideOrder.getImei());
        BikeGpsStatusDto gps = gpsStatusService.findByImei(rideOrder.getImei());
        AgentCfg config = agentConfigService.getAgentConfig(rideOrder.getAgentId());

        Map<String, Object> res = new ImmutableMap.Builder<String, Object>()
                .put("imei", rideOrder.getImei())
                .put("deviceId", bike.getDeviceId())
                .put("locked", gps.getLocked() == 1)
                .put("on", gps.getDoorLock() == 1)
                .put("freeSeconds", config.getFreeSeconds())
                .put("scanTime", rideOrder.getStartTime().getTime())
                .put("mac", bike.getBleMac())
                .build();

        return R.ok(200, "获取车辆控制信息", res);
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
            BikeDto bike = bikeService.findByImei(rideDto.getImei());
            BikeStatusDto status = statusService.findStatusByBikeIId(bike.getBikeId());
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
