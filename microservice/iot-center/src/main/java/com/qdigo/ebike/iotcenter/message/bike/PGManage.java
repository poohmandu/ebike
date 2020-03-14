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

package com.qdigo.ebike.iotcenter.message.bike;

import com.qdigo.ebike.api.domain.dto.iot.datagram.PGPackage;
import com.qdigo.ebike.common.core.constants.MQ;
import com.qdigo.ebike.commonconfig.configuration.properties.QdigoOnOffProperties;
import com.qdigo.ebike.iotcenter.constants.BikeStatusEnum;
import com.qdigo.ebike.iotcenter.dto.gprs.pg.PGPacketDto;
import com.qdigo.ebike.iotcenter.exception.IotServiceBizException;
import com.qdigo.ebike.iotcenter.exception.IotServiceExceptionEnum;
import com.qdigo.ebike.iotcenter.netty.SocketServer;
import com.qdigo.ebike.iotcenter.service.api.PackageManageStrateyg;
import com.qdigo.ebike.iotcenter.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.qdigo.ebike.iotcenter.service.api.PackageManageStrateyg.PGStratrgy;

@Slf4j
@Service(PGStratrgy)
public class PGManage implements PackageManageStrateyg<PGPacketDto> {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private QdigoOnOffProperties onOffProperties;

    public void sendMsg(PGPacketDto pgPacketDto) {
        try {
            PGPackage pgPackage = buildPGPackage(pgPacketDto);
            if (onOffProperties.isIotMqSend()) {
                rabbitTemplate.convertAndSend(MQ.Topic.Exchange.pg, MQ.Topic.Key.up_pg, pgPackage);
            }
        } catch (Exception e) {
            log.error("发送上行PG包http请求异常 header0:" + pgPacketDto.getHeader0() + ",header1:" + pgPacketDto.getHeader1() + ",imei:" + pgPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SEND_UP_PG_HTTP_ERROR.getCode(), IotServiceExceptionEnum.SEND_UP_PG_HTTP_ERROR.getMsg());
        }
    }

    public void saveInfo(PGPacketDto pgPacketDto) {
        try {
            String imei = String.valueOf(pgPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = BikeStatusEnum.MONITOR_ALLBIKE_STATUS.getBikeStatus() + model;
            String motitorValue = BikeStatusEnum.MONITOR_BIKE_STATUS.getBikeStatus() + imei;
            redisTemplate.opsForHash().put(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePGMaP = getBikeStatus(pgPacketDto);
            redisTemplate.opsForHash().putAll(motitorValue, bikePGMaP);
        } catch (Exception e) {
            log.error("保存上行PG包到缓存异常异常 header0:" + pgPacketDto.getHeader0() + ",header1:" + pgPacketDto.getHeader1() + ",imei:" + pgPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SAVE_UP_PG_REDIS_ERROR.getCode(), IotServiceExceptionEnum.SAVE_UP_PG_REDIS_ERROR.getMsg());
        }
    }

    private Map<String, String> getBikeStatus(PGPacketDto pgPacketDto) {
        Map<String, String> bikePGMap = new HashMap<>();
        bikePGMap.put(BikeStatusEnum.IMEI.getBikeStatus(), pgPacketDto.getImei() + "");
        bikePGMap.put(BikeStatusEnum.PG_LASTTIME.getBikeStatus(), DateUtil.format(new Date(), DateUtil.DEFAULT_PATTERN));
        bikePGMap.put(BikeStatusEnum.AVAILABLE_SLAVE.getBikeStatus(), SocketServer.NET_IP);
        return bikePGMap;
    }

    private PGPackage buildPGPackage(PGPacketDto pgPacketDto) {
        return new PGPackage()
                .setPgAutoLocked((int) pgPacketDto.getPgSubStatus().getAutoLockStatus())
                .setPgDoorLock((int) pgPacketDto.getPgSubStatus().getSwitchStatus())
                .setPgElectric((int) pgPacketDto.getPgSubStatus().getCommunicationStatus())
                .setPgError((int) pgPacketDto.getPgSubStatus().getTroubleStatus())
                .setPgHight((int) pgPacketDto.getHight())
                .setPgImei(String.valueOf(pgPacketDto.getImei()))
                .setPgLatitude((double) pgPacketDto.getLat())
                .setPgLocked((int) pgPacketDto.getPgSubStatus().getLockStatus())
                .setPgLongitude((double) pgPacketDto.getLng())
                .setPgShaked((int) pgPacketDto.getPgSubStatus().getShockStatus())
                .setPgSpeed((int) pgPacketDto.getSpeed())
                .setPgStar((int) pgPacketDto.getStar())
                .setPgTumble((int) pgPacketDto.getPgSubStatus().getFallStatus())
                .setPgWheelInput((int) pgPacketDto.getPgSubStatus().getInputStatus())
                .setPgClient(pgPacketDto.getClient())
                .setPgServer(pgPacketDto.getServer());
    }


}
