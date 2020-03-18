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

import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.ConsumeDetail;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.FreeActivityDto;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

/**
 * description: 
 *
 * date: 2020/3/18 10:49 AM
 * @author niezhao
 */
@FeignClient(name = "order-center", contextId = "ride-free-activity")
public interface RideFreeActivityService {

    void consumeFreeActivities();

    ConsumeDetail getConsumeDetail(DetailParam param);

    @Data
    @Builder
    class ConsumeParam {
        private UserDto userDto;
        private UserAccountDto accountDto;
        private List<FreeActivityDto> freeActivities;
    }

    @Data
    @Builder
    class DetailParam {
        private UserDto userDto;
        private RideDto rideDto;
        private UserAccountDto accountDto;
        private AgentCfg agentCfg;
    }

}
