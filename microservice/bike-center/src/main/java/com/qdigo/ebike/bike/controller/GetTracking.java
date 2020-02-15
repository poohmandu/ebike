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

import com.google.common.collect.Lists;
import com.qdigo.ebike.api.domain.dto.control.Location;
import com.qdigo.ebike.api.domain.dto.third.map.RideTrackDto;
import com.qdigo.ebike.api.service.control.RideTrackService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.common.core.util.R;
import com.qdigo.ebike.common.core.util.geo.LocationConvert;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1.0/ebike")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetTracking {

    private final OrderRideService rideService;
    private final RideTrackService rideTrackService;

    /**
     * 行驶轨迹请求
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param form
     * @return point array
     * @throws Exception
     */
    @PostMapping(value = "/tracking", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> tracking(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body form) {

        log.debug("user:{}获取轨迹", mobileNo);

        // 根据用户选定的订单号获取所对应的车辆号

        val rideRecord = rideService.findById(form.getOrderId());
        if (rideRecord == null) {
            return R.ok(400, "没有找到该订单");
        }
        val start = rideRecord.getStartTime();
        val end = rideRecord.getEndTime();
        List<RideTrackDto> rideTrack = rideTrackService.getRideTrack(rideRecord.getRideRecordId());
        List<Location> points = Lists.newArrayList();
        if (rideTrack.isEmpty()) {
            Map<String, Double> startLoc = FormatUtil.strToLoc(rideRecord.getStartLoc());
            Map<String, Double> endLoc = FormatUtil.strToLoc(rideRecord.getEndLoc());
            if (startLoc.get("lat") != 0 && startLoc.get("lng") != 0) {
                startLoc = LocationConvert.fromGPSToAmap(startLoc);
                points.add(new Location().setPgLatitude(startLoc.get("lat"))
                        .setPgLongitude(startLoc.get("lng"))
                        .setTimestamp(rideRecord.getStartTime().getTime()));
            }
            if (endLoc.get("lat") != 0 && endLoc.get("lng") != 0) {
                endLoc = LocationConvert.fromGPSToAmap(startLoc);
                points.add(new Location().setPgLatitude(endLoc.get("lat"))
                        .setPgLongitude(endLoc.get("lng"))
                        .setTimestamp(rideRecord.getEndTime().getTime()));
            }
        } else {
            points = rideTrack.stream().map(track -> new Location().setPgLatitude(track.getLatitude())
                    .setPgLongitude(track.getLongitude()).setTimestamp(track.getTimestamp()))
                    .collect(Collectors.toList());
        }
        return R.ok(200, "成功获取到轨迹列表", points);
    }

    @Data
    private static class Body {
        private long orderId;
    }

}
