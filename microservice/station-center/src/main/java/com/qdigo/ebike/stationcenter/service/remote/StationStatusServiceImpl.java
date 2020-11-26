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
import com.qdigo.ebike.api.service.station.StationStatusService;
import com.qdigo.ebike.stationcenter.domain.entity.StationStatus;
import com.qdigo.ebike.stationcenter.repository.StationStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Description: 
 * date: 2020/1/10 7:49 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class StationStatusServiceImpl implements StationStatusService {

    private final StationStatusRepository statusRepository;

    @Override
    @Transactional
    public void update(Param param) {
        StationStatus stationStatus = statusRepository.findByBikeStationStationId(param.getStationId());
        if (stationStatus != null) {
            stationStatus.setBikeCount(param.getBikeCount());
            stationStatus.setChargeStationsCount(param.getChargeStationsCount());
            statusRepository.save(stationStatus);
        }
    }
}
