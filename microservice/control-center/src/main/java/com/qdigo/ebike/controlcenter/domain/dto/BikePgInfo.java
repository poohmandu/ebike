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

package com.qdigo.ebike.controlcenter.domain.dto;

import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeGpsStatusDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;

/**
 * description: 
 *
 * date: 2020/3/12 10:32 AM
 * @author niezhao
 */
@Data
@Builder
public class BikePgInfo {
    private BikeDto bikeDto;
    private BikeStatusDto bikeStatusDto;
    private BikeGpsStatusDto bikeGpsStatusDto;
    private AgentCfg agentCfg;
    @Nullable
    private RideDto rideDto;
}
