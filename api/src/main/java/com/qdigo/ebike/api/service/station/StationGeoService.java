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

package com.qdigo.ebike.api.service.station;

import com.qdigo.ebike.api.ApiRoute;
import lombok.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 
 * date: 2020/1/2 1:25 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "station-center", contextId = "station-geo")
public interface StationGeoService {

    @PostMapping(ApiRoute.StationCenter.StationGeo.isAtStation)
    StationGeoDto isAtStation(@RequestParam("lat") double lat, @RequestParam("lng") double lng,
                              @RequestParam("compensate") boolean compensate, @RequestParam("addCompensateMeter") int addCompensateMeter,
                              @RequestParam("agentId") long agentId);

    @PostMapping(ApiRoute.StationCenter.StationGeo.isAtStation + "withAgentId")
    StationGeoDto isAtStation(@RequestParam("lat") double lat, @RequestParam("lng") double lng,
                              @RequestParam("compensate") boolean compensate, @RequestParam("agentId") long agentId);

    @PostMapping(ApiRoute.StationCenter.StationGeo.isAtStationWithCompensate)
    StationGeoDto isAtStationWithCompensate(@RequestParam("lat") double lat, @RequestParam("lng") double lng,
                                            @RequestParam("addCompensateMeter") int addCompensateMeter, @RequestParam("agentId") long agentId);

    @PostMapping(ApiRoute.StationCenter.StationGeo.isAtArea)
    Long isAtArea(@RequestParam("lat") double lat, @RequestParam("lng") double lng, @RequestParam("agentId") long agentId);

    @Value
    class StationGeoDto {
        private Long stationId;
        private Long agentId;
    }
}
