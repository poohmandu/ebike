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
import com.qdigo.ebike.api.domain.dto.station.StationDto;
import com.qdigo.ebike.api.service.station.StationService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.stationcenter.domain.entity.BikeStation;
import com.qdigo.ebike.stationcenter.repository.StationRepository;
import com.qdigo.ebike.stationcenter.repository.dao.StationDao;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

/**
 * Description: 
 * date: 2020/1/3 6:07 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class StationServiceImpl implements StationService {

    private final StationDao stationDao;
    private final StationRepository stationRepository;

    @Override
    public StationDto getNearestStation(double GPSLng, double GPSLat, double radius) {
        // 根据查询一般城市最大半径15km,我们因为涉及郊区所以取 15*2
        // limit 想限制建议mysql排序，否则建议java排序
        List<BikeStation> stations = stationDao.getNearStations(GPSLng, GPSLat, radius, 1);
        if (stations.isEmpty()) {
            return null;
        } else {
            BikeStation bikeStation = stations.get(0);
            return ConvertUtil.to(bikeStation, StationDto.class);
        }
    }

    @Override
    public StationDto findByStationId(long stationId) {
        return stationRepository.findById(stationId)
                .map(bikeStation -> ConvertUtil.to(bikeStation, StationDto.class))
                .orElse(null);
    }
}
