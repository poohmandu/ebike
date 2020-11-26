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

package com.qdigo.ebike.agentcenter.service.remote.forceend;

import com.qdigo.ebike.agentcenter.domain.entity.AgentForceEndConfig;
import com.qdigo.ebike.agentcenter.repository.ForceEndConfigRepository;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.agent.forceend.AgentForceEndConfigDto;
import com.qdigo.ebike.api.service.agent.AgentForceEndConfigService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/18 4:55 PM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AgentForceEndConfigServiceImpl implements AgentForceEndConfigService {

    private final ForceEndConfigRepository forceEndConfigRepository;

    @Override
    public AgentForceEndConfigDto findByAgentId(Long agentId) {
        AgentForceEndConfig forceEndConfig = forceEndConfigRepository.findByAgentId(agentId).orElse(null);
        return ConvertUtil.to(forceEndConfig, AgentForceEndConfigDto.class);
    }

}
