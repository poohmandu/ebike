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

package com.qdigo.ebike.api.domain.dto.order.rideforceend;

import com.qdigo.ebike.api.domain.dto.agent.forceend.AgentForceEndConfigDto;
import lombok.Builder;
import lombok.Data;

/**
 * description: 
 *
 * date: 2020/3/18 3:55 PM
 * @author niezhao
 */
@Data
public class ForceEndInfo {
    private boolean valid;
    private String cause;
    private double amount;
    private int distanceMeter;
    private String amountNote;

    private AgentForceEndConfigDto config;
}
