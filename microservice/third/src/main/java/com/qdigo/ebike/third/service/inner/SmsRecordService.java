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

package com.qdigo.ebike.third.service.inner;

import com.qdigo.ebike.third.domain.entity.SmsRecord;
import com.qdigo.ebike.third.repository.SmsRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;

@Slf4j
@Service
public class SmsRecordService {

    @Inject
    private SmsRecordRepository smsRecordRepository;

    @Transactional
    public void insert(String target, String content, Long agentId, SmsRecord.Type type) {
        SmsRecord smsRecord = new SmsRecord();
        smsRecord.setAgentId(agentId)
            .setContent(content)
            .setSendTime(new Date())
            .setSucceed(true)
            .setTarget(target)
            .setType(type)
            .setAmount(0.04);
        smsRecordRepository.save(smsRecord);
    }

}
