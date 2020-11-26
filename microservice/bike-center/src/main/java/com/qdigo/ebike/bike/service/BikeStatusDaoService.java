/*
 * Copyright 2019 聂钊 nz@qdigo.com
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

package com.qdigo.ebike.bike.service;

import com.qdigo.ebike.bike.domain.entity.BikeStatus;
import com.qdigo.ebike.bike.repository.BikeStatusRepository;
import com.qdigo.ebike.common.core.constants.BikeCfg;
import com.qdigo.ebike.common.core.constants.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeStatusDaoService {

    private final BikeStatusRepository statusRepository;


    @Transactional
    //@CatAnnotation
    public void updateLocation(long statusId, Double latitude, Double longitude, BikeCfg.LocationType type, Long stationId,
                               Long areaId, String address) {
        BikeStatus status = statusRepository.findById(statusId).orElse(null);
        if (status == null) {
            return;
        }
        if (Objects.equals(status.getLatitude(), latitude) &&
                Objects.equals(status.getLongitude(), longitude) &&
                Objects.equals(status.getLocationType(), type) &&
                Objects.equals(status.getStationId(), stationId) &&
                Objects.equals(status.getAreaId(), areaId) &&
                Objects.equals(status.getAddress(), address)) {
            return;
        }
        if (latitude != null) {
            status.setLatitude(latitude);
        }
        if (longitude != null) {
            status.setLongitude(longitude);
        }
        if (type != null) {
            status.setLocationType(type);
        }
        if (address != null) {
            status.setAddress(address);
        }
        status.setAreaId(areaId);
        status.setStationId(stationId);
        statusRepository.save(status);
    }

    @Transactional
    //@CatAnnotation
    public void updateActualStatus(long statusId, String actualStatus) {
        BikeStatus status = statusRepository.findById(statusId).orElse(null);
        if (status == null) {
            return;
        }
        if (actualStatus != null && !actualStatus.equals(status.getActualStatus())) {
            status.setActualStatus(actualStatus);
            statusRepository.save(status);
        }
    }

    @Transactional
    //@CatAnnotation
    public void updateStatus(long statusId, Status.BikeLogicStatus logicStatus) {
        BikeStatus status = statusRepository.findById(statusId).orElse(null);
        if (status == null) {
            return;
        }
        if (logicStatus != null && logicStatus.getVal() != status.getStatus()) {
            status.setStatus(logicStatus.getVal());
            statusRepository.save(status);
        }
    }

}
