/*
 * Copyright 2020 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
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


import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.common.core.util.page.PageUtil;
import com.qdigo.ebike.stationcenter.domain.entity.BikeStation;
import com.qdigo.ebike.stationcenter.mapper.BikeStationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class StationDaoService {

    private final BikeStationMapper stationMapper;

    public List<BikeStation> findByLocationOrdered(double lat, double lng, double meter, List<Long> agentIds) {
        val around = GeoUtil.getAround(lng, lat, meter);
        return stationMapper.findByLocationOrdered(around.minY, around.maxY, around.minX, around.maxX, lat, lng, agentIds, 5);
    }

    public BikeStation findNearestStation(double lat, double lng, List<Long> agentIds) {
        val around = GeoUtil.getAround(lng, lat, 100 * 1000);
        List<BikeStation> stations = stationMapper.findByLocationOrdered(around.minY, around.maxY, around.minX, around.maxX, lat, lng, agentIds, 1);
        return stations.isEmpty() ? null : stations.get(0);
    }

    public List<BikeStation> findByLocation(double lat, double lng, double meter, List<Long> agentIds) {
        val around = GeoUtil.getAround(lng, lat, meter);
        return stationMapper.findByLocation(around.minY, around.maxY, around.minX, around.maxX, agentIds);
    }

    public Page<BikeStation> findPageByLocation(double lat, double lng, List<Long> agentIds, Pageable pageable) {
        PageUtil.startPage(pageable);
        List<BikeStation> bikeStations = stationMapper.findOrderByLocation(lat, lng, agentIds);
        return PageUtil.of(bikeStations, pageable);
    }


}
