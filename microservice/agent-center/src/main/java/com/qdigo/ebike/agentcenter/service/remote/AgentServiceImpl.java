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
import com.qdigo.ebike.agentcenter.repository.AgentRepository;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.agent.AgentDto;
import com.qdigo.ebike.api.service.agent.AgentService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

/**
 * Description: 
 * date: 2020/1/3 3:32 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;

    @Override
    public List<AgentDto> findByCity(String agentCity) {
        List<Agent> agentList = agentRepository.findByCity(agentCity);
        return ConvertUtil.to(agentList, AgentDto.class);
    }

    @Override
    public AgentDto findById(Long agentId) {
        Agent agent = agentRepository.findById(agentId).orElse(null);
        return ConvertUtil.to(agent, AgentDto.class);
    }
}
