/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.api.service.agent;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: 
 * date: 2019/12/31 6:33 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "agent-center", contextId = "agent-config")
public interface AgentConfigService {

    @PostMapping(ApiRoute.AgentCenter.Config.getAgentConfig)
    AgentCfg getAgentConfig(@RequestParam(value = "agentId", required = false) Long agentId);

    @PostMapping(ApiRoute.AgentCenter.Config.allowAgents)
    List<Long> allowAgents(@NotNull @RequestParam("agentId") long agentId);

}
