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

package com.qdigo.ebike.stationcenter.controller;

import com.google.common.collect.Lists;
import com.qdigo.ebike.common.core.util.R;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.common.core.util.geo.LocationConvert;
import com.qdigo.ebike.stationcenter.service.StationInnerService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1.0/geo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetBikeStationsNearBy {

    private final StationInnerService stationInnerService;

    /**
     * 查询附近还车点
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param form
     * @return
     * @author niezhao
     */
    //TODO: 优化响应速度
    @PostMapping(value = "/getBikeStations", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getPointsNearBy(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body form) {

        if (StringUtils.isBlank(mobileNo)) {
            return R.ok(200, "获取附近车辆:手机号为空", Lists.newArrayList());
        }

        //Optional<User> userOptional = userRepository.findOneByMobileNo(mobileNo);
        //if (userOptional.isPresent() && userOptional.get().getAccount().getBalance() == 0) {
        //    return ResponseEntity.ok().body(new BaseResponse(200, "获取附近车辆:未完成支付环节", Lists.newArrayList()));
        //}

        //将GCJ_02坐标转换为GPS坐标
        Map<String, Double> delta = LocationConvert.fromAmapToGps(form.getLatitude(), form.getLongitude());
        double lng = delta.get("lng");
        double lat = delta.get("lat");

        double radius = form.getRadius() > 3 ? 3 : form.getRadius();

        List<Res> resList = stationInnerService.getStationsNearBy(mobileNo, lng, lat, radius).stream().map(bikeStation -> {
            double distance = GeoUtil.getDistanceForMeter(lat, lng, bikeStation.getLatitude(), bikeStation.getLongitude());
            List<Res.Point> points = Lists.newArrayList();
            if (bikeStation.getStationFence() != null) {
                points = bikeStation.getStationFence().getPoints().stream().map(stationPoint ->
                        new Res.Point(stationPoint.getLatitude(), stationPoint.getLongitude()))
                        .collect(Collectors.toList());
            }
            return Res.builder().bikeCount(bikeStation.getStationStatus().getBikeCount())
                    .distance(distance).latitude(bikeStation.getLatitude()).longitude(bikeStation.getLongitude())
                    .radius(bikeStation.getRadius()).stationId(bikeStation.getStationId())
                    .stationName(bikeStation.getStationName()).points(points)
                    .build();
        }).collect(Collectors.toList());

        return R.ok(200, "成功获取附近换车点", resList);
    }

    @Data
    private static class Body {
        private Double longitude;// 经度
        private Double latitude;// 纬度
        private Double radius;// 半径(千米)
    }

    @Data
    @Builder
    private static class Res {
        private long stationId; // 还车点编号
        private String stationName; // 还车点编号
        private double longitude;// 经度
        private double latitude;// 纬度
        private int bikeCount; // 可用数量
        private int radius;
        private double distance;
        private List<Point> points;

        @Data
        @AllArgsConstructor
        private static class Point {
            private double latitude;
            private double longitude;
        }
    }

}
