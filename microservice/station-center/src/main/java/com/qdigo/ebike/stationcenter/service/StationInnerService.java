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

package com.qdigo.ebike.stationcenter.service;

import com.google.common.collect.Lists;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.stationcenter.domain.entity.BikeStation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 
 * date: 2020/1/12 12:46 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class StationInnerService {

    private final UserService userService;
    private final AgentConfigService agentConfigService;
    private final StationDaoService stationDaoService;

    public List<BikeStation> getStationsNearBy(String mobileNo, double GPSLng, double GPSLat, double radius) {
        final Long agentId;
        if (mobileNo.isEmpty()) {
            agentId = null;
        }
        //else if (opsUserRepository.findByUserName(mobileNo).isPresent()) {
        //    agentId = null;
        //}
        else {
            agentId = userService.findByMobileNo(mobileNo).getAgentId();
        }

        List<Long> agentIds;
        if (agentId == null) {
            agentIds = Lists.newArrayList();
        } else {
            agentIds = agentConfigService.allowAgents(agentId);
        }

        List<BikeStation> stations = stationDaoService.findByLocation(GPSLat, GPSLng, radius * 1000, agentIds);
        stations = stations.stream()
                .filter(bikeStation -> bikeStation.getStationStatus() != null)
                .sorted((o1, o2) -> {
                    double distance1 = GeoUtil.getDistanceForMeter(GPSLat, GPSLng, o1.getLatitude(), o1.getLongitude());
                    double distance2 = GeoUtil.getDistanceForMeter(GPSLat, GPSLng, o2.getLatitude(), o2.getLongitude());
                    return Double.compare(distance1, distance2);
                })
                .collect(Collectors.toList());
        log.debug("取得半径在{}千米以内的还车点数量为{}", radius, stations.size());
        return stations;
    }

}
