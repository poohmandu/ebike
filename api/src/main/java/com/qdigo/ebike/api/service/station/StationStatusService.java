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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Description: 
 * date: 2020/1/10 7:43 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "station-center", contextId = "station-status")
public interface StationStatusService {

    @RequestMapping(method = RequestMethod.POST, value = ApiRoute.StationCenter.StationStatus.update)
    void update(@RequestBody Param param);

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class Param {
        private long stationId;
        private int bikeCount;
        private int chargeStationsCount;
    }
}
