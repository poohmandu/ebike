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

import com.qdigo.ebike.api.domain.dto.station.StationDto;
import com.qdigo.ebike.api.service.station.StationService;
import com.qdigo.ebike.api.service.station.StationStatusService;
import com.qdigo.ebike.bike.domain.entity.BikeStatus;
import com.qdigo.ebike.bike.repository.BikeStatusRepository;
import com.qdigo.ebike.bike.service.ChargerServiceImpl;
import com.qdigo.ebike.common.core.util.R;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1.0/geo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetBikeStationInfo {

    private final StationService stationService;
    private final BikeStatusRepository bikeStatusRepository;
    private final ChargerServiceImpl chargerService;
    private final StationStatusService stationStatusService;


    /**
     * 根据还车点ID展示车辆详细信息
     *
     * @param deviceId
     * @param mobileNo
     * @param body
     * @return
     * @author niezhao
     */
    @PostMapping(value = "/getBikeStationInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getBikeStationInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body body) {

        StationDto stationDto = stationService.findByStationId(body.getStationId());

        val infoList = bikeStatusRepository.findByStationId(body.getStationId()).stream()
                .filter(bikeStatus -> bikeStatus.getBike().isOnline())
                .sorted(Comparator.comparingInt(BikeStatus::getBattery))
                .map(bikeStatus -> Res.BikeInfo.builder()
                        .battery(bikeStatus.getBattery())
                        .deviceId(bikeStatus.getBike().getDeviceId())
                        .status(bikeStatus.getStatus())
                        .address(bikeStatus.getAddress())
                        .type(bikeStatus.getBike().getType())
                        .price(bikeStatus.getBike().getPrice())
                        .unitMinutes(bikeStatus.getBike().getUnitMinutes())
                        .kilometer(bikeStatus.getKilometer())
                        .latitude(bikeStatus.getLatitude())
                        .longitude(bikeStatus.getLongitude())
                        .build())
                .collect(Collectors.toList());
        val bikeCount = infoList.size();

        val chargerInfoList = chargerService.findChargersInStation(body.getStationId()).stream()
                .map(charger -> Res.ChargerInfo.builder().chargerName(charger.getChargerName()).status(charger.getStatus())
                        .portNumber(charger.getPortNumber()).usedPortNumber(charger.getUsedPortNumber()).build())
                .collect(Collectors.toList());

        val chargerCount = chargerInfoList.size();

        StationStatusService.Param param = new StationStatusService.Param(body.getStationId(), bikeCount, chargerCount);
        stationStatusService.update(param);


        Res p = Res.builder().stationId(body.getStationId())
                .stationName(stationDto.getStationName())
                .address(stationDto.getAddress())
                .picUrl(stationDto.getPicUrl())
                .note(stationDto.getNote())
                .latitude(stationDto.getLatitude())
                .longitude(stationDto.getLongitude())
                .bikeCount(bikeCount)
                .chargeStationsCount(chargerCount)
                .bikeInfoList(infoList)
                .chargerInfoList(chargerInfoList)
                .build();

        return R.ok(200, "成功获取该还车点详细信息", p);
    }

    @Data
    private static class Body {
        private long stationId;
    }

    @Data
    @Builder
    private static class Res {
        private long stationId;
        private String stationName;
        private String address;
        private String picUrl;
        private String note;
        private int bikeCount;
        private int chargeStationsCount;
        private double latitude;
        private double longitude;

        private List<BikeInfo> bikeInfoList;
        private List<ChargerInfo> chargerInfoList;

        @Data
        @Builder
        private static class BikeInfo {
            private String deviceId;
            private int battery;
            private int status;
            private double kilometer;
            private int unitMinutes;
            private double price;
            private String type;
            private String address;
            private double latitude;
            private double longitude;
        }

        @Data
        @Builder
        private static class ChargerInfo {
            private String chargerName;
            private int status;
            private int portNumber;
            private int usedPortNumber;
        }

    }


}
