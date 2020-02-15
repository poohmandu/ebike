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

package com.qdigo.ebike.stationcenter.service.remote;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.station.StationGeoService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.common.core.util.geo.PolygonUtil;
import com.qdigo.ebike.stationcenter.domain.entity.AgentArea;
import com.qdigo.ebike.stationcenter.domain.entity.BikeStation;
import com.qdigo.ebike.stationcenter.domain.entity.StationFence;
import com.qdigo.ebike.stationcenter.mapper.AgentAreaMapper;
import com.qdigo.ebike.stationcenter.service.StationDaoService;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 
 * date: 2020/1/2 1:06 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class StationGeoServiceImpl implements StationGeoService {

    private final AgentConfigService configService;
    private final StationDaoService stationDaoService;
    private final AgentAreaMapper agentAreaMapper;

    @Override
    public StationGeoDto isAtStation(double lat, double lng, boolean compensate, int addCompensateMeter, long agentId) {
        List<Long> agentIds = configService.allowAgents(agentId);

        final int compensateMeter = configService.getAgentConfig(agentId).getCompensateMeter();

        List<BikeStation> stations = stationDaoService.findByLocationOrdered(lat, lng, Const.StationGeo.distance, agentIds);

        if (stations.isEmpty()) {
            return null;
        }
        for (BikeStation station : stations) {
            int m_compensate = compensateMeter + station.getCompensate();
            m_compensate += addCompensateMeter;

            StationFence fence = station.getStationFence();
            if (fence != null) {
                List<Point2D.Double> list = fence.getPoints().stream()
                        .map(stationPoint -> new Point.Double(stationPoint.getLongitude(), stationPoint.getLatitude()))
                        .collect(Collectors.toList());
                if (compensate) {
                    list = PolygonUtil.run(list, m_compensate);
                }
                if (GeoUtil.isInPolygon(lat, lng, list)) {
                    return new StationGeoDto(station.getStationId(), station.getAgentId());
                }
            } else {
                double radius = station.getRadius();
                double meter = GeoUtil.getDistanceForMeter(lat, lng, station.getLatitude(), station.getLongitude());
                if (compensate) {
                    radius += m_compensate;
                }
                if (meter < radius) {
                    return new StationGeoDto(station.getStationId(), station.getAgentId());
                }
            }
        }
        return null;
    }


    @Override
    public StationGeoDto isAtStation(double lat, double lng, boolean compensate, long agentId) {
        return this.isAtStation(lat, lng, compensate, 0, agentId);
    }

    @Override
    public StationGeoDto isAtStationWithCompensate(double lat, double lng, int addCompensateMeter, long agentId) {
        return this.isAtStation(lat, lng, true, addCompensateMeter, agentId);
    }

    @Override
    public Long isAtArea(double lat, double lng, long agentId) {
        List<Long> agentIds = configService.allowAgents(agentId);
        if (agentIds.isEmpty()) {
            return null;
        }
        List<AgentArea> agentAreas = agentAreaMapper.findByAgentIds(agentIds);

        if (agentAreas.isEmpty()) {
            return null;
        }
        return agentAreas.stream().filter(agentArea -> {
            List<Point2D.Double> points = agentArea.getPoints().stream()
                    .map(agentAreaPoint -> new Point.Double(agentAreaPoint.getLongitude(), agentAreaPoint.getLatitude()))
                    .collect(Collectors.toList());
            return GeoUtil.isInPolygon(lat, lng, points);
        }).map(AgentArea::getAreaId)
                .findAny()
                .orElse(null);
    }
}
