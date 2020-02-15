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

import com.qdigo.ebike.agentcenter.domain.entity.AgentJoint;
import com.qdigo.ebike.agentcenter.repository.AgentJointRepository;
import com.qdigo.ebike.api.service.third.sms.SmsService;
import com.qdigo.ebike.common.core.util.R;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Slf4j
@RestController
@RequestMapping("/v1.0/agent/joint")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AgentJointRest {

    private final AgentJointRepository agentJointRepository;
    private final SmsService smsService;

    @PostMapping(value = "/postJointInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> postJointInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body body) {

        if (StringUtils.isBlank(body.getName())) {
            return R.ok(400, "姓名不能为空");
        }
        if (StringUtils.isBlank(body.getMobileNo())) {
            return R.ok(400, "联系方式不能为空");
        }
        if (StringUtils.isBlank(body.getCity())) {
            return R.ok(400, "加盟城市不能为空");
        }

        AgentJoint agentJoint = agentJointRepository.findTopByMobileNo(body.getMobileNo())
                .orElseGet(() -> new AgentJoint().setDeleted(false));

        agentJoint.setCity(body.getCity());
        agentJoint.setCreateTime(System.currentTimeMillis());
        agentJoint.setMobileNo(body.getMobileNo());
        agentJoint.setName(body.getName());
        agentJoint.setNote(body.getNote());
        agentJoint.setType(body.getType());
        agentJoint.setAmount(body.getAmount());

        agentJointRepository.save(agentJoint);
        smsService.sendJointTipSms(body.getName(), body.getMobileNo(), body.getCity(), body.getType());

        return R.ok(200, "提交成功");
    }

    @Data
    private static class Body {
        private String name;
        private String mobileNo;
        private String city;
        private String type;
        private String note;
        private Integer amount;
    }

}
