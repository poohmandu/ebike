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

package com.qdigo.ebike.agentcenter.controller;

import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.station.StationDto;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.bike.BikeLocService;
import com.qdigo.ebike.api.service.station.StationService;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Slf4j
@RestController
@RequestMapping("/v1.0/agent/config")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AgentConfigRest {

    private final StationService stationService;
    private final AgentConfigService agentService;
    private final BikeLocService bikeLocService;

    @AccessValidate
    @PostMapping(value = "/byLocation", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> byLocation(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body body) {

        bikeLocService.deleteCacheScanLoc(mobileNo);
        StationDto nearestStation = stationService.getNearestStation(body.getLongitude(), body.getLatitude(), 50);
        Long agentId;
        if (nearestStation == null) {
            log.debug("用户50公里附近无还车点");
            agentId = null;
        } else {
            agentId = nearestStation.getAgentId();
        }

        AgentCfg agentConfig = agentService.getAgentConfig(agentId);

        Vo vo = Vo.builder().inputPrefix(agentConfig.getInputPrefix()).build();
        return R.ok(200, "成功获取配置", vo);
    }


    @Data
    private static class Body {
        private double longitude;// 经度
        private double latitude;// 纬度
    }

    @Data
    @Builder
    private static class Vo {
        private String inputPrefix;
    }

}
