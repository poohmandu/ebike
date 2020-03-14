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

package com.qdigo.ebike.controlcenter.listener.device;

import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.MQ;
import com.qdigo.ebike.controlcenter.domain.entity.device.PCSqlPackage;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PCPackage;
import com.qdigo.ebike.controlcenter.repository.PCSqlRepository;
import com.qdigo.ebike.controlcenter.repository.mongo.PCMongoRepository;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/14 4:41 PM
 * @author niezhao
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "qdigo.on-off.mq-listener", havingValue = "true")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeCommand {

    private final PCService pcService;
    private final PCSqlRepository pcSqlRepository;
    private final PCMongoRepository pcMongoRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "parseUpCommand", autoDelete = "false", durable = "true"),
            exchange = @Exchange(value = MQ.Topic.Exchange.pc, type = ExchangeTypes.TOPIC),
            key = MQ.Topic.Key.up_pc, ignoreDeclarationExceptions = "true"))
    public void parseUpCommand(PCPackage pc) {
        // 对imei号进行修正
        String imei = ConfigConstants.imei.getConstant() + pc.getPcImei();
        pc.setPcImei(imei);

        //pc 的业务逻辑
        pcService.bikeCommandService(pc);

        // 对mySql的PC表进行更新
        PCSqlPackage pcSqlPackage = formToSqlDomain(pc);
        pcSqlRepository.save(pcSqlPackage);

        // 对mongoDB的PCPackage文档进行新增
        pcMongoRepository.insert(pc);
    }

    private static PCSqlPackage formToSqlDomain(PCPackage f) {
        PCSqlPackage c = new PCSqlPackage();
        c.setPcCmd(f.getPcCmd());
        c.setPcImei(f.getPcImei());
        c.setPcParam(f.getPcParam());
        c.setPcSequence(f.getPcSequence());
        return c;
    }

}
