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

import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.util.R;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.stationcenter.domain.entity.BikeStation;
import com.qdigo.ebike.stationcenter.service.StationDaoService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1.0/geo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetBikeStationPage {

    private final StationDaoService stationDaoService;
    private final AgentConfigService agentConfigService;
    private final UserService userService;

    @AccessValidate
    @GetMapping(value = "/getBikeStationPage", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getBikeStationPage(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @PageableDefault Pageable pageable, Body body) {

        UserDto userDto = userService.findByMobileNo(mobileNo);
        Long agentId = userDto.getAgentId();
        List<Long> agentIds = null;
        if (agentId != null) {
            agentIds = agentConfigService.allowAgents(agentId);
        }
        Page<BikeStation> bikeStationPage = stationDaoService.findPageByLocation(body.getLatitude(), body.getLongitude(), agentIds, pageable);

        Page<ImmutableMap<String, Object>> page = bikeStationPage.map(bikeStation -> new ImmutableMap.Builder<String, Object>()
                .put("stationId", bikeStation.getStationId())
                .put("picUrl", bikeStation.getPicUrl())
                .put("longitude", bikeStation.getLongitude())
                .put("latitude", bikeStation.getLatitude())
                .put("stationName", bikeStation.getStationName())
                .put("address", bikeStation.getAddress())
                .put("distance", (int) GeoUtil.getDistanceForMeter(body.getLatitude(), body.getLongitude(),
                        bikeStation.getLatitude(), bikeStation.getLongitude()))
                .build());

        return R.ok(200, "获取分页还车点信息列表", page);
    }

    @Data
    private static class Body {
        private Double longitude;// 经度
        private Double latitude;// 纬度
    }


}
