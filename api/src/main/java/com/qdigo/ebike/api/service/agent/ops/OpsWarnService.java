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

package com.qdigo.ebike.api.service.agent.ops;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.common.core.constants.Const;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * description: 
 *
 * date: 2020/3/13 9:51 AM
 * @author niezhao
 */
@FeignClient(name = "agent-center", contextId = "agent-ops-warn")
public interface OpsWarnService {

    @PostMapping(ApiRoute.AgentCenter.Ops.Warn.pushWarn)
    void pushWarn(@RequestBody WarnParam warnParam);

    @Data
    @Accessors(chain = true)
    class WarnParam {
        private boolean bln;
        private Const.MailType mailType;
        private String alert;
        private String imei;
        private String deviceId;
        private Long agentId;
        private double longitude;
        private double latitude;
    }
}
