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

package com.qdigo.ebike.api.service.station;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.station.StationDto;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Description:
 * date: 2020/1/3 4:04 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "station-center", contextId = "station")
public interface StationService {

    @PostMapping(ApiRoute.StationCenter.Station.getNearestStation)
    StationDto getNearestStation(@RequestParam("GPSLng") double GPSLng, @RequestParam("GPSLat") double GPSLat, @RequestParam("radius") double radius);

    @PostMapping(ApiRoute.StationCenter.Station.findByStationId)
    StationDto findByStationId(@RequestParam("stationId") long stationId);

    @PostMapping(ApiRoute.StationCenter.Station.getNearestStationByAgents)
    StationDto getNearestStationByAgents(@RequestBody Param param);

    @Data
    @Accessors(chain = true)
    class Param {
        private Double lat;
        private Double lng;
        private List<Long> agentIds;
    }

}

