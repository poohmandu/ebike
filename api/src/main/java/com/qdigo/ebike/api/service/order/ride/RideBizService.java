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
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * description: 
 *
 * date: 2020/3/16 4:31 PM
 * @author niezhao
 */
@FeignClient(name = "order-center", contextId = "ride-biz")
public interface RideBizService {

    @PostMapping(ApiRoute.OrderCenter.Ride.RideBiz.createRide)
    RideDto createRide(@RequestBody StartParam param);

    @Data
    @Builder
    class StartParam {
        private Double lat;
        private Double lng;
        private BikeDto bikeDto;
        private BikeStatusDto bikeStatusDto;
        private UserDto userDto;

    }
}
