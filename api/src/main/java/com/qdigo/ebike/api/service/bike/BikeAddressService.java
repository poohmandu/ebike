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
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * description: 
 *
 * date: 2020/3/12 9:50 AM
 * @author niezhao
 */
@FeignClient(name = "bike-center", contextId = "bike-adress")
public interface BikeAddressService {

    @PostMapping(ApiRoute.BikeCenter.BikeAddress.updateBikeAddress)
    BikeAddressDto updateBikeAddress(@RequestParam("lat") double lat, @RequestParam("lng") double lng, @RequestParam("imei") String imei);

    @Data
    @Builder
    class BikeAddressDto {
        private String imei;
        private double longitude;
        private double latitude;
        private String province;
        private String city;
        private String district;
        private String cityCode;
        private String adCode;
        private String address;
    }
}
