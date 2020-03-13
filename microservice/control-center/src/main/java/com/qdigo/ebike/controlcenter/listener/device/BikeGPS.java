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
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PGPackage;
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


@Slf4j
@Component
@ConditionalOnProperty(name = "qdigo.on-off.mq-listener", havingValue = "true")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeGPS {

    private final GPSHandler gpsHandler;

    /**
     * GPS包消费
     *
     * @param pg
     * @return
     */
    // 取消事务
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "getGPS", autoDelete = "false", durable = "true"),
                    exchange = @Exchange(value = MQ.Topic.Exchange.pg, type = ExchangeTypes.TOPIC),
                    key = MQ.Topic.Key.up_pg, ignoreDeclarationExceptions = "true"),
            @QueueBinding(
                    value = @Queue(value = "getGPS_bak", autoDelete = "false", durable = "true"),
                    exchange = @Exchange(value = MQ.Topic.Exchange.pg, type = ExchangeTypes.TOPIC),
                    key = MQ.Topic.Key.up_pg_bak, ignoreDeclarationExceptions = "true")})
    public void getGPS(PGPackage pg) {
        // 对imei号进行修正
        String imei = ConfigConstants.imei.getConstant() + pg.getPgImei();
        pg.setPgImei(imei);
        try {
            gpsHandler.handlePG(pg);
            //rebuild warmup重新构建
        } catch (Exception e) {
            log.error("PG在MQ的同步消费过程中异常:", e);
        }
    }

}
