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
import com.qdigo.ebike.common.core.errors.exception.QdigoBizException;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PCPackage;
import com.qdigo.ebike.controlcenter.service.inner.rent.ButtonEndService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by niezhao on 2017/6/27.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "qdigo.on-off.mq-listener", havingValue = "true")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SpecialCommand {

    @Inject
    private ButtonEndService buttonEndService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "specialPC", autoDelete = "true", durable = "false"),
            exchange = @Exchange(value = MQ.Topic.Exchange.pc, type = ExchangeTypes.TOPIC),
            key = MQ.Topic.Key.up_pc_special, ignoreDeclarationExceptions = "true"))
    public String specialPC(PCPackage pc) throws QdigoBizException {
        pc.setPcImei(ConfigConstants.imei.getConstant() + pc.getPcImei());
        log.debug("接收到specialPC:{}", pc);
        val cmd = pc.getPcCmd();

        switch (cmd) {
            case 70:
                //成功:2   失败:3
                return buttonEndService.executeButtonEnd(pc);
            default:
                throw new RuntimeException("未知的命令号:" + cmd);
        }

    }


}
