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
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.rideforceend.ForceEndInfo;
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * description: 
 *
 * date: 2020/3/18 3:47 PM
 * @author niezhao
 */
@FeignClient(name = "order-center", contextId = "ride-force-end")
public interface RideForceEndService {

    @PostMapping(ApiRoute.OrderCenter.Ride.RideForceEnd.insert)
    void insert(@RequestBody CreateParam createParam);

    @PostMapping(ApiRoute.OrderCenter.Ride.RideForceEnd.getForceEndInfo)
    ForceEndInfo getForceEndInfo(@RequestBody Param param);

    @Data
    @Builder
    class CreateParam {
        private long rideRecordId;
        private long agentId;
        private double lat;
        private double lng;
        private ForceEndInfo forceEndInfo;
    }

    @Data
    @Builder
    class Param {
        private BikeStatusDto statusDto;
        private Long agentId;
    }

}
