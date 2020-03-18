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

package com.qdigo.ebike.ordercenter.service.inner.ride;

import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.service.station.StationGeoService;
import com.qdigo.ebike.ordercenter.domain.entity.ride.RideRecord;
import com.qdigo.ebike.ordercenter.domain.entity.ride.RideRoute;
import com.qdigo.ebike.ordercenter.repository.RideRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/16 5:08 PM
 * @author niezhao
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RideRouteInnerService {

    private final RideRouteRepository rideRouteRepository;
    private final StationGeoService geoService;

    @Transactional
    public void createRideRoute(RideRecord rideRecord, Long agentId, BikeStatusDto bikeStatusDto, double gpsLat, double gpsLng) {
        val rideRoute = new RideRoute()
                .setRideRecord(rideRecord)
                .setAgentId(agentId)
                .setEndLat(0.0)
                .setEndLng(0.0)
                .setEndStationId(null)
                .setStartLat(bikeStatusDto.getLatitude()) //手机位置
                .setStartLng(bikeStatusDto.getLongitude())
                .setStartStationId(bikeStatusDto.getStationId());
        if (bikeStatusDto.getStationId() == null) {
            StationGeoService.StationGeoDto geoDto = geoService.isAtStation(gpsLat, gpsLng, true, agentId);
            if (geoDto != null) {
                rideRoute.setStartLat(gpsLat).setStartLng(gpsLng).setStartStationId(geoDto.getStationId());
            }
        }
        rideRouteRepository.save(rideRoute);
    }

    //@Transactional
    //public void updateRideRoute(RideRecord rideRecord, double gpsLat, double gpsLng) {
    //    val rideRoute = rideRecord.getRideRoute();
    //    if (rideRoute != null) {
    //        rideRoute.setEndLat(gpsLat)
    //                .setEndLng(gpsLng);
    //        if (rideRoute.getEndStationId() == null) {
    //            rideRoute.setEndStationId(rideRecord.getBike().getBikeStatus().getStationId());
    //        }
    //        rideRouteRepository.save(rideRoute);
    //    }
    //}


}
