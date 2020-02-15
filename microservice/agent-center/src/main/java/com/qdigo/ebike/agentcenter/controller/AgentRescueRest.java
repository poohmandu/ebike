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

package com.qdigo.ebike.agentcenter.controller;

import com.qdigo.ebike.agentcenter.domain.entity.AgentAdminConfig;
import com.qdigo.ebike.agentcenter.repository.AgentAdminConfigRepository;
import com.qdigo.ebike.agentcenter.service.AgentInnerService;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.util.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1.0/agent/rescue")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AgentRescueRest {

    private final AgentAdminConfigRepository adminConfigRepository;
    private final UserService userService;
    private final AgentInnerService agentInnerService;

    @AccessValidate
    @GetMapping(value = "/getPhoneNum", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<List<Vo>> getPhoneNum(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        List<Vo> list = new ArrayList<>();

        UserDto userDto = userService.findByMobileNo(mobileNo);
        if (userDto != null) {
            Long agentId = userDto.getAgentId();
            agentInnerService.allowAgents(agentId).forEach(agent -> {
                AgentAdminConfig agentAdminConfig = adminConfigRepository.findByAgentId(agent.getAgentId());
                String name = StringUtils.defaultIfBlank(agent.getOperationDistrict(), "暂无名称");
                if (agentAdminConfig != null && StringUtils.isNotEmpty(agentAdminConfig.getRescueMobile())) {
                    String[] mobileArr = StringUtils.split(agentAdminConfig.getRescueMobile(), ",");
                    Arrays.stream(mobileArr).forEach(s -> list.add(new Vo(name, s)));
                } else {
                    list.add(new Vo(name, "4001787007"));
                }
            });
        }
        return R.ok(200, "成功获取救援电话", list);
    }

    @Value
    private class Vo {
        private String name;
        private String phoneNum;
    }

}
