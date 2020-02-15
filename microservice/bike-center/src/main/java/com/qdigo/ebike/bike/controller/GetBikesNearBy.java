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
import com.qdigo.ebike.bike.domain.entity.BikeStatus;
import com.qdigo.ebike.bike.service.BikeStatusService;
import com.qdigo.ebike.common.core.util.R;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.common.core.util.geo.LocationConvert;
import lombok.*;
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
public class GetBikesNearBy {

    private final BikeStatusService statusService;

    /**
     * 查询附近车辆
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param form
     * @return
     * @author niezhao
     */
    @RequestMapping(value = "/getBikes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getBikesNearBy(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body form) {

        if (StringUtils.isBlank(mobileNo)) {
            return R.ok(200, "获取附近车辆:手机号为空", Lists.newArrayList());
        }

        //Optional<User> userOptional = userRepository.findOneByMobileNo(mobileNo);
        //UserAccount account;
        //if (userOptional.isPresent() && (account = userOptional.get().getAccount()).getBalance() + account.getGiftBalance() <= 0) {
        //    return ResponseEntity.ok().body(new BaseResponse(200, "获取附近车辆:未完成支付环节", Lists.newArrayList()));
        //}

        //将GCJ_02坐标转换为GPS坐标
        Map<String, Double> delta = LocationConvert.fromAmapToGps(form.getLatitude(), form.getLongitude());
        double lng = delta.get("lng");
        double lat = delta.get("lat");

        double radius = form.getRadius() > 2 ? 2 : form.getRadius();

        List<Res> resList = statusService.getBikeStatusNearBy(mobileNo, lng, lat, radius).stream().map(bike -> {
            BikeStatus bikeStatus = bike.getBikeStatus();
            double distance = GeoUtil.getDistanceForMeter(lat, lng, bikeStatus.getLatitude(), bikeStatus.getLongitude());
            return Res.builder().battery(bikeStatus.getBattery()).bikeId(bike.getBikeId()).deviceNo(bike.getDeviceId())
                    .distance(distance).imei(bike.getImeiId()).latitude(bikeStatus.getLatitude()).status(bikeStatus.getStatus())
                    .longitude(bikeStatus.getLongitude()).type(bike.getType()).build();
        }).collect(Collectors.toList());

        return R.ok(200, "成功获取附近车辆", resList);
    }

    @Data
    private static class Body {
        private Double longitude;// 经度
        private Double latitude;// 纬度
        private Double radius;// 半径(千米)
    }

    @Getter
    @Setter
    @Builder
    private static class Res {
        private long bikeId;
        private String imei;//车辆imei号
        private String deviceNo;// 车架号
        private int battery;// 电量百分比
        private String type;// 类型 0:普通车 1:豪华车
        private double longitude;// 经度
        private double latitude;// 纬度
        private double distance; //距离
        private int status;
    }

}
