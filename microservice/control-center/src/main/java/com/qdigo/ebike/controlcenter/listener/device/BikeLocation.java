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


import com.qdigo.ebike.api.service.bike.BikeGpsStatusService;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.MQ;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.controlcenter.domain.entity.device.PLSqlPackage;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PLPackage;
import com.qdigo.ebike.controlcenter.repository.PLSqlRepository;
import com.qdigo.ebike.controlcenter.repository.mongo.PLMongoRepository;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PLService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ConditionalOnProperty(name = "qdigo.on-off.mq-listener", havingValue = "true")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeLocation {

    private final MongoTemplate mongoTemplate;
    private final PLSqlRepository plSqlRepository;
    private final PLMongoRepository plMongoRepository;
    private final PLService plService;
    private final BikeGpsStatusService bikeGpsStatusService;

    /**
     * GPS基站定位包接口
     *
     * @param form
     * @return
     * @throws IOException
     */
    //@RequestMapping(value = "/GPSLocation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "getGPSLocation", autoDelete = "true", durable = "true"),
        exchange = @Exchange(value = MQ.Topic.Exchange.pl, type = ExchangeTypes.TOPIC),
        key = MQ.Topic.Key.up_pl, ignoreDeclarationExceptions = "true"))
    public void getGPSLocation(PLPackage form) {
        // 对imei号进行修正
        String imei = ConfigConstants.imei.getConstant() + form.getPlImei();
        form.setPlImei(imei);
        try {

            // 对mongoDB的PLPackage文档进行新增
            plMongoRepository.insert(form);

            if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - form.getTimestamp()) < Const.pgNotFoundSeconds) {
                //启一个异步调用 完成业务逻辑
                plService.updateStatus(form);
            }

            // 对mySql的PL表进行更新
            PLSqlPackage plSqlPackage = formToSqlDomain(form);
            plSqlRepository.save(plSqlPackage);

            val plPackage = ConvertUtil.to(form, com.qdigo.ebike.api.domain.dto.iot.datagram.PLPackage.class);
            bikeGpsStatusService.updatePl(plPackage);

        } catch (Exception e) {
            log.error("PL在MQ的消费过程中异常:", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

    }


    private static PLSqlPackage formToSqlDomain(PLPackage f) {
        PLSqlPackage l = new PLSqlPackage();
        l.setPlAutoLocked(f.getPlAutoLocked());
        l.setPlCellid(f.getPlCellid());
        l.setPlDoorLock(f.getPlDoorLock());
        l.setPlElectric(f.getPlElectric());
        l.setPlError(f.getPlError());
        l.setPlImei(f.getPlImei());
        l.setPlLac(f.getPlLac());
        l.setPlLocked(f.getPlLocked());
        l.setPlShaked(f.getPlShaked());
        l.setPlSingal(f.getPlSingal());
        l.setPlTumble(f.getPlTumble());
        l.setPlWheelInput(f.getPlWheelInput());
        return l;
    }

}
