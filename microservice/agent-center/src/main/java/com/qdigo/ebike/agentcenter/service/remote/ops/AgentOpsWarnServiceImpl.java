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

package com.qdigo.ebike.agentcenter.service.remote.ops;

import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.agentcenter.domain.entity.opsuser.OpsUser;
import com.qdigo.ebike.agentcenter.domain.entity.opsuser.record.WarnRecord;
import com.qdigo.ebike.agentcenter.repository.OpsUserRepository;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.agent.ops.OpsWarnService;
import com.qdigo.ebike.api.service.third.push.PushService;
import com.qdigo.ebike.common.core.constants.Const;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description: 
 *
 * date: 2020/3/13 10:43 AM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AgentOpsWarnServiceImpl implements OpsWarnService {

    private final MongoTemplate mongoTemplate;
    private final PushService pushService;
    private final OpsUserRepository opsUserRepository;

    @Override
    public void pushWarn(WarnParam warnParam) {
        val imei = warnParam.getImei();
        val mailType = warnParam.getMailType();
        val alert = warnParam.getAlert();
        final Map<String, Double> map = ImmutableMap.of("longitude", warnParam.getLongitude(), "latitude", warnParam.getLatitude());

        List<String> aliases = opsUserRepository.findByAgentId(warnParam.getAgentId()).stream()
                .map(OpsUser::getUserName)
                .collect(Collectors.toList());
        PushService.WarnParam param = PushService.WarnParam.builder().alert(alert).data(map).mobiles(aliases).build();

        String msgId = pushService.pushWarn(param);
        if (msgId != null) {
            this.insertWarnRecords(aliases, imei, warnParam.getDeviceId(), alert, mailType, msgId);
        }
    }

    public void insertWarnRecords(List<String> userNames, String imei, String deviceId, String alert, Const.MailType type, String messageId) {
        List<WarnRecord> warnRecords = userNames.stream()
                .map(userName -> new WarnRecord()
                        .setAlert(alert)
                        .setDeviceId(deviceId)
                        .setImei(imei)
                        .setPushTarget(userName)
                        .setType(type)
                        .setPushTime(new Date())
                        .setMessageId(messageId))
                .collect(Collectors.toList());
        mongoTemplate.insert(warnRecords, WarnRecord.class);
    }
}
