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

import com.qdigo.ebike.agentcenter.domain.entity.Agent;
import com.qdigo.ebike.agentcenter.repository.dao.AgentDao;
import com.qdigo.ebike.agentcenter.service.AgentInnerService;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 
 * date: 2019/12/31 6:33 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AgentConfigServiceImpl implements AgentConfigService {

    private final AgentInnerService agentInnerService;
    private final AgentDao agentDao;

    @Override
    public AgentCfg getAgentConfig(Long agentId) {
        return agentInnerService.getAgentConfig(agentId);
    }

    @Override
    public List<Long> allowAgents(long agentId) {
        List<Agent> agents = agentInnerService.allowAgents(agentId);
        return agents.stream().map(Agent::getAgentId).collect(Collectors.toList());
    }

    @Override
    public AgentCfg findByImei(String imei) {
        Agent agent = agentDao.findByImei(imei);
        return agentInnerService.getAgentConfig(agent.getAgentId());
    }

}
