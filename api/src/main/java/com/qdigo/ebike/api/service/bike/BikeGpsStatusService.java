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

package com.qdigo.ebike.api.service.bike;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.bike.BikeGpsStatusDto;
import com.qdigo.ebike.api.domain.dto.iot.datagram.PGPackage;
import com.qdigo.ebike.api.domain.dto.iot.datagram.PHPackage;
import com.qdigo.ebike.api.domain.dto.iot.datagram.PLPackage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * description: 
 *
 * date: 2020/3/12 5:27 PM
 * @author niezhao
 */
@FeignClient(name = "bike-center", contextId = "bike-gps-status")
public interface BikeGpsStatusService {

    @PostMapping(ApiRoute.BikeCenter.BikeGpsStatus.bikeGpsStatus)
    BikeGpsStatusDto findByImei(@RequestParam("imei") String imei);

    @PostMapping(ApiRoute.BikeCenter.BikeGpsStatus.updatePg)
    void updatePg(@RequestBody PGPackage pgPackage);

    @PostMapping(ApiRoute.BikeCenter.BikeGpsStatus.updatePh)
    void updatePh(@RequestBody PHPackage phPackage);

    @PostMapping(ApiRoute.BikeCenter.BikeGpsStatus.updatePl)
    void updatePl(@RequestBody PLPackage plPackage);

    @PostMapping(ApiRoute.BikeCenter.BikeGpsStatus.update)
    void update(@RequestBody BikeGpsStatusDto bikeGpsStatusDto);

}
