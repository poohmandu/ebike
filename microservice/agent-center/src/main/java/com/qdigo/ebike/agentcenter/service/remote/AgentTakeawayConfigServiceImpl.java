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

package com.qdigo.ebike.agentcenter.service.remote;

import com.qdigo.ebike.agentcenter.domain.entity.AgentTakeawayConfig;
import com.qdigo.ebike.agentcenter.repository.AgentTakeawayConfigRepository;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.agent.AgentTakeawayConfigDto;
import com.qdigo.ebike.api.service.agent.AgentTakeawayConfigService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

/**
 * description: 
 *
 * date: 2020/3/1 8:47 PM
 * @author niezhao
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AgentTakeawayConfigServiceImpl implements AgentTakeawayConfigService {

    private final AgentTakeawayConfigRepository agentTakeawayConfigRepository;

    @Override
    public List<AgentTakeawayConfigDto> findByAgentId(Long agentId) {
        List<AgentTakeawayConfig> takeawayConfigs = agentTakeawayConfigRepository.findByAgentId(agentId);
        return ConvertUtil.to(takeawayConfigs, AgentTakeawayConfigDto.class);
    }

    @Override
    public AgentTakeawayConfigDto findById(Long id) {
        return agentTakeawayConfigRepository.findById(id)
                .map(agentTakeawayConfig -> ConvertUtil.to(agentTakeawayConfig, AgentTakeawayConfigDto.class))
                .orElse(null);
    }

}
