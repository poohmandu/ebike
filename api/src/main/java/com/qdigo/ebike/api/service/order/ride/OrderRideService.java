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

package com.qdigo.ebike.api.service.order.ride;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.PageDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Description: 
 * date: 2020/1/15 7:17 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "order-center", contextId = "ride")
public interface OrderRideService {

    @PostMapping(ApiRoute.OrderCenter.Ride.findRidingByImei)
    RideDto findRidingByImei(@RequestParam("imei") String imei);

    @PostMapping(ApiRoute.OrderCenter.Ride.findRidingByMobileNo)
    RideDto findRidingByMobileNo(@RequestParam("mobileNo") String mobileNo);

    @PostMapping(ApiRoute.OrderCenter.Ride.findById)
    RideDto findById(@RequestParam("rideRecordId") long rideRecordId);

    @PostMapping(ApiRoute.OrderCenter.Ride.findAnyByMobileNo)
    RideDto findAnyByMobileNo(@RequestParam("mobileNo") String mobileNo);

    @PostMapping(ApiRoute.OrderCenter.Ride.findEndByMobileNo)
    List<RideDto> findEndByMobileNo(@RequestParam("mobileNo") String mobileNo);

    @PostMapping(ApiRoute.OrderCenter.Ride.findEndPageByMobileNo)
    PageDto<RideDto> findEndPageByMobileNo(@RequestParam("mobileNo") String mobileNo, Pageable pageable);

}
