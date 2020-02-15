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

package com.qdigo.ebike.bike.service;

import com.qdigo.ebike.api.domain.dto.station.StationDto;
import com.qdigo.ebike.api.service.station.StationService;
import com.qdigo.ebike.bike.domain.entity.charger.Charger;
import com.qdigo.ebike.bike.repository.charger.ChargerRepository;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by niezhao on 2017/7/17.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ChargerServiceImpl {

    private final StationService stationService;
    private final ChargerRepository chargerRepository;

    @Transactional
    public List<Charger> findChargersInStation(long stationId) {
        StationDto stationDto = stationService.findByStationId(stationId);
        int radius = stationDto.getRadius();
        val around = GeoUtil.getAround(String.valueOf(stationDto.getLongitude()), String.valueOf(stationDto.getLatitude()), String.valueOf(radius));

        return chargerRepository.findByLocation(around.minX, around.maxX, around.minY, around.maxY);

    }

}
