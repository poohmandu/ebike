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
import com.google.common.collect.Lists;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.third.map.Point;
import com.qdigo.ebike.api.service.control.RideTrackService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.domain.entity.BikeStatus;
import com.qdigo.ebike.bike.repository.BikeRepository;
import com.qdigo.ebike.bike.service.BikeStatusService;
import com.qdigo.ebike.bike.service.inner.BikeInnerService;
import com.qdigo.ebike.common.core.util.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1.0/ebike")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetEBikeInfo {

    private final OrderRideService rideService;
    private final BikeRepository bikeRepository;
    private final RideTrackService rideTrackService;
    private final BikeInnerService bikeInnerService;
    private final BikeStatusService bikeStatusService;

    /**
     * 我的电滴
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @return
     */
    @AccessValidate
    @GetMapping(value = "/getEBikeInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> eBikeInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        RideDto rideDto = rideService.findRidingByMobileNo(mobileNo);
        if (rideDto == null) {
            return R.ok(401, "你不在借车中");
        }
        Bike bike = bikeRepository.findByImeiId(rideDto.getImei()).get();
        BikeStatus status = bike.getBikeStatus();
        MyRide myRide = MyRide.builder().battery(status.getBattery())
                .bkId(bike.getBikeId())
                .deviceId(bike.getDeviceId())
                .imeiId(bike.getImeiId())
                .latitude(status.getLatitude())
                .longitude(status.getLongitude())
                .type(bike.getType())
                .build();

        return R.ok(200, "电滴相关信息已返回", myRide);
    }

    @Data
    @Builder
    private static class MyRide {
        private long bkId;
        private String deviceId;// 车架号（6位）
        private String imeiId;// 车辆imei号
        private String type;// A-A型车,B-B型车
        private int battery;
        private double longitude;
        private double latitude;
    }

    @Transactional
    @GetMapping(value = "/getRidingPolyline/{timestamp}", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getRidingPolyline(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @PathVariable Long timestamp) {

        RideDto rideDto = rideService.findRidingByMobileNo(mobileNo);
        List<Point> points = Lists.newArrayList();

        Map<String, Object> res;
        if (rideDto != null && timestamp != null) {
            rideTrackService.insertRideTracks(rideDto.getRideRecordId());
            points = rideTrackService.getRideTrackAfterAndCursor(rideDto.getRideRecordId(), timestamp).stream()
                    .filter(rideTrack -> rideTrack.getLatitude() != 0 && rideTrack.getLongitude() != 0)
                    .map(rideTrack -> new Point().setTimestamp(rideTrack.getTimestamp())
                            .setLongitude(rideTrack.getLongitude())
                            .setLatitude(rideTrack.getLatitude()))
                    .collect(Collectors.toList());
        }
        res = ImmutableMap.of("points", points, "size", points.size());
        return R.ok(200, "成功返回该车行驶路线", res);
    }

    @GetMapping(value = "/getEBikeInfo/{imei}", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getBikeInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @PathVariable String imei) {

        Bike bike = bikeInnerService.findOneByImeiIdOrDeviceId(imei);
        if (bike == null) {
            return R.ok(400, "车辆标识格式错误");
        }
        if (bike.getBikeId() == null) {
            return R.ok(401, "数据库不存在该车辆");
        }
        val actualStatus = bikeStatusService.queryActualStatus(bike);

        Map<String, Object> res = new ImmutableMap.Builder<String, Object>()
                .put("imei", bike.getImeiId())
                .put("bikeId", bike.getBikeId())
                .put("deviceId", bike.getDeviceId())
                .put("address", bike.getBikeStatus().getAddress())
                .put("battery", bike.getBikeStatus().getBattery())
                .put("kilometer", new DecimalFormat("#0.0").format(bike.getBikeStatus().getKilometer()))
                .put("type", bike.getType())
                .put("price", bike.getPrice())
                .put("actualStatus", actualStatus)
                .put("unitMinutes", bike.getUnitMinutes())
                .put("mac", bike.getBleMac())
                .build();

        return R.ok(200, "成功返回该车信息", res);
    }

    @GetMapping(value = "/uploadMAC", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> uploadMAC(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            String imei, String mac) {

        Bike bike = bikeRepository.findByImeiId(imei).get();
        if (StringUtils.isNotEmpty(mac) && !StringUtils.equals(mac, bike.getBleMac())) {
            log.debug("更新车辆的蓝牙MAC地址({}=>{})", bike.getBleMac(), mac);
            bike.setBleMac(mac);
            bikeRepository.save(bike);
        }
        return R.ok(200, "更新车辆蓝牙MAC地址");
    }

}
