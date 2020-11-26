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


import com.qdigo.ebike.api.domain.dto.bike.BikeConfigDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.service.bike.BikeGpsStatusService;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.MQ;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.controlcenter.domain.dto.BikePhInfo;
import com.qdigo.ebike.controlcenter.domain.entity.device.PHSqlPackage;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PHPackage;
import com.qdigo.ebike.controlcenter.repository.PHSqlRepository;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PHMongoService;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PHService;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ConditionalOnProperty(name = "qdigo.on-off.mq-listener", havingValue = "true")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeHeart {

    private final PHSqlRepository phSqlRepository;
    private final PHService phService;
    private final BikeStatusService bikeStatusService;
    private final BikeGpsStatusService bikeGpsStatusService;
    private final PHMongoService phMongoService;
    private final BikeService bikeService;

    /**
     * 心跳包接口
     *
     * @param ph
     * @return
     */
    //@RequestMapping(value = "/heart", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "getHeart", autoDelete = "false", durable = "true"),
            exchange = @Exchange(value = MQ.Topic.Exchange.ph, type = ExchangeTypes.TOPIC),
            key = MQ.Topic.Key.up_ph, ignoreDeclarationExceptions = "true"))
    public void getHeart(PHPackage ph) {
        // 对imei号进行修正
        final String imei = ConfigConstants.imei.getConstant() + ph.getPhImei();
        ph.setPhImei(imei);
        try {
            //(1)保存最新ph到mongo
            phMongoService.insertPH(ph);

            //(2)检查、处理PH包
            if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - ph.getTimestamp()) < Const.pgNotFoundSeconds) {
                //(2.1) 最新5个心跳（10～25分钟）
                List<PHPackage> phList = phMongoService.findPHList(imei, 5);

                BikeStatusDto bikeStatusDto = bikeStatusService.findByImei(imei);
                if (bikeStatusDto == null) {
                    log.debug("未查询到imei号为{}的车辆", imei);
                    return;
                }
                BikeDto bikeDto = bikeService.findByImei(imei);
                if (bikeDto == null) {
                    log.debug("PHService:bike表未查询到imei号为{}的车辆", imei);
                    return;
                }
                BikeConfigDto bikeConfigDto = bikeService.findConfigByType(bikeDto.getType());

                BikePhInfo bikePhInfo = BikePhInfo.builder().bikeStatusDto(bikeStatusDto)
                        .bikeDto(bikeDto).bikeConfigDto(bikeConfigDto).build();

                phService.updateStatus(ph, phList, bikePhInfo);

                phService.checkBikeStatus(ph, phList);

            }

            val phPackage = ConvertUtil.to(ph, com.qdigo.ebike.api.domain.dto.iot.datagram.PHPackage.class);
            bikeGpsStatusService.updatePh(phPackage);

            PHSqlPackage phSqlPackage = formToSqlDomain(ph);
            phSqlRepository.save(phSqlPackage);
        } catch (Exception e) {
            log.error("PH在MQ的消费过程中异常:", e);
        }
    }


    private static PHSqlPackage formToSqlDomain(PHPackage f) {
        PHSqlPackage h = new PHSqlPackage();
        h.setPhAutoLock(f.getPhAutoLock());
        h.setPhAutoLocked(f.getPhAutoLocked());
        h.setPhBatteryVoltage(f.getPhBatteryVoltage());
        h.setPhBrakeErroe(f.getPhBrakeErroe());
        h.setPhControlError(f.getPhControlError());
        h.setPhDoorLock(f.getPhDoorLock());
        h.setPhElectric(f.getPhElectric());
        h.setPhError(f.getPhError());
        h.setPhHandleBarError(f.getPhHandleBarError());
        h.setPhHold(f.getPhHold());
        h.setPhImei(f.getPhImei());
        h.setPhImsi(f.getPhImsi());
        h.setPhLocked(f.getPhLocked());
        h.setPhMachineError(f.getPhMachineError());
        h.setPhPowerVoltage(f.getPhPowerVoltage());
        h.setPhSentity(f.getPhSentity());
        h.setPhSequence(f.getPhSequence());
        h.setPhShaked(f.getPhShaked());
        h.setPhSoc(f.getPhSoc());
        h.setPhStar(f.getPhStar());
        h.setPhTumble(f.getPhTumble());
        h.setPhWheelInput(f.getPhWheelInput());
        return h;
    }

}
