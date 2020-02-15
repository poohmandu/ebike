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

package com.qdigo.ebike.api.service.bike;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.bike.BikeLoc;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 
 * date: 2019/12/30 4:36 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "bike-center", contextId = "bike-loc")
public interface BikeLocService {

    @PostMapping(ApiRoute.BikeCenter.BikeLoc.insertBikeLoc)
    void insertBikeLoc(@RequestParam("imei") String imei, @RequestParam("mobileNo") String mobileNo,
                       @RequestParam("event") LBSEvent event, @RequestParam("latitude") double latitude,
                       @RequestParam("longitude") double longitude);

    @PostMapping(ApiRoute.BikeCenter.BikeLoc.insertBikeLoc + "withAgentId")
    void insertBikeLoc(@RequestParam("imei") String imei, @RequestParam("mobileNo") String mobileNo,
                       @RequestParam("event") LBSEvent event, @RequestParam("latitude") double latitude,
                       @RequestParam("longitude") double longitude, @RequestParam("agentId") Long agentId);

    @PostMapping(ApiRoute.BikeCenter.BikeLoc.deleteCacheScanLoc)
    void deleteCacheScanLoc(@RequestParam("mobileNo") String mobileNo);

    @PostMapping(ApiRoute.BikeCenter.BikeLoc.findLastScanLoc)
    BikeLoc findLastScanLoc(@RequestParam("mobileNo") String mobileNo);

    enum LBSEvent {
        scanImei, scanDeviceId, end, start, close, lock, unlock, pl, faultReport, notAtStation,
        pgNotFound, timeout, scanTimeout, scanError, scanBattery, scanOnline, scanInUse
    }

}


