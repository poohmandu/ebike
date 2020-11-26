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

package com.qdigo.ebike.ordercenter.service.remote;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.order.thirdrecord.OrderThirdRecordService;
import com.qdigo.ebike.common.core.util.Ctx;
import com.qdigo.ebike.ordercenter.domain.entity.ThirdOrderRecord;
import com.qdigo.ebike.ordercenter.repository.ThirdOrderRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@RemoteService
public class OrderThirdRecordServiceImpl implements OrderThirdRecordService {

    @Resource
    private ThirdOrderRecordRepository thirdOrderRecordRepository;

    @Override
    @Transactional
    public void insert(Param param) {
        Long agentId = param.getAgentId();
        boolean succeed = param.isSucceed();
        API api = param.getApi();
        ThirdOrderRecord thirdOrderRecord = new ThirdOrderRecord();
        if (agentId == null) {
            agentId = 1L;
        }
        thirdOrderRecord.setAgentId(agentId);
        thirdOrderRecord.setAmount(api.getAmount());
        thirdOrderRecord.setApiName(api.getApiName());
        thirdOrderRecord.setServiceName(api.getServiceName());
        thirdOrderRecord.setSucceed(succeed);
        thirdOrderRecord.setTime(new Date(Ctx.now()));
        thirdOrderRecordRepository.save(thirdOrderRecord);
    }

}
