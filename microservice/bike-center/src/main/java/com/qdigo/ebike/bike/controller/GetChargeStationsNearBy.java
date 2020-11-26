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

import com.qdigo.ebike.bike.domain.entity.charger.Charger;
import com.qdigo.ebike.bike.repository.charger.ChargerRepository;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1.0/geo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetChargeStationsNearBy {

    private final ChargerRepository chargerRepository;


    /**
     * 查询附近充电桩
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param form
     * @return
     * @author niezhao
     */
    @PostMapping(value = "/getChargeStations", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getChargeStationsNearBy(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body form) {

        log.debug("REST to get the Charger for user:{}", mobileNo);

        // 半径最大40km
        form.setRadius(form.getRadius() > 40 ? 40 : form.getRadius());
        // 取距离在半径以内，且取20辆以下
        List<Charger> templist = chargerRepository.findAll();
        List<Charger> chargerlist = new ArrayList<>();
        for (int i = 0; chargerlist.size() < 20 && i < templist.size(); i++) {
            double distance = GeoUtil.getDistanceForMeter(form.getLatitude(), form.getLongitude(),
                    templist.get(i).getLatitude(), templist.get(i).getLongitude());
            if (distance <= form.getRadius()) {
                chargerlist.add(templist.get(i));
            }
        }

        // 按距离排序
        chargerlist.sort((o1, o2) -> {
            if (GeoUtil.getDistanceForMeter(form.getLatitude(), form.getLongitude(), o1.getLatitude(),
                    o1.getLongitude()) > GeoUtil.getDistanceForMeter(form.getLatitude(), form.getLongitude(),
                    o2.getLatitude(), o2.getLongitude())) {
                return 1;
            } else if (GeoUtil.getDistanceForMeter(form.getLatitude(), form.getLongitude(), o1.getLatitude(),
                    o1.getLongitude()) == GeoUtil.getDistanceForMeter(form.getLatitude(), form.getLongitude(),
                    o2.getLatitude(), o2.getLongitude())) {
                return 0;
            }
            return -1;
        });

        // 对应数据填充进response
        List<Res> responseList = new ArrayList<>();
        chargerlist.forEach((charger) -> {
            Res response = new Res();
            response.setStationId(charger.getChargerId());
            response.setStatus(charger.getStatus());
            response.setLatitude(charger.getLatitude());
            response.setLongitude(charger.getLongitude());
            responseList.add(response);
        });

        log.debug("返回的充电桩的数量:{}", responseList.size());
        return R.ok(200, "成功获取附近充电桩", responseList);
    }

    @Data
    private static class Body {
        private Double longitude;// 经度
        private Double latitude;// 纬度
        private Double radius;// 半径(千米)
    }

    @Data
    private static class Res {
        private Long stationId; // 充电桩编号
        private Integer status;// 0:可用 1:不可用 2:维修中
        private Double longitude;// 经度
        private Double latitude;// 纬度
    }

}

