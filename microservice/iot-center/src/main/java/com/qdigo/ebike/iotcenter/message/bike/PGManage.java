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

import com.qdigo.ebike.iotcenter.netty.SocketServer;
import com.qdigo.ebike.iotcenter.config.ConfigConst;
import com.qdigo.ebike.iotcenter.constants.BikeStatusEnum;
import com.qdigo.ebike.iotcenter.dto.gprs.pg.PGPacketDto;
import com.qdigo.ebike.iotcenter.dto.mongo.PGPackage;
import com.qdigo.ebike.iotcenter.exception.IotServiceBizException;
import com.qdigo.ebike.iotcenter.exception.IotServiceExceptionEnum;
import com.qdigo.ebike.iotcenter.util.DateUtil;
import com.qdigo.ebike.iotcenter.util.RedisUtil;
import com.qdigo.ebike.iotcenter.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class PGManage {
    private Logger logger = LoggerFactory.getLogger(PGManage.class);
    private static final String url = "http://api.qdigo.net/v1.0/bikeProtocol/pg";
    //private static final String url = "http://192.168.0.101/v1.0/bikeProtocol/GPS";

    private RabbitTemplate rabbit = SpringUtil.getBean(RabbitTemplate.class);

    public void sendMsg(PGPacketDto pgPacketDto) {
        try {
            PGPackage pgPackage = buildPGPackage(pgPacketDto);
            //PGReqDto pgReqDto = buildPGReqDto(pgPacketDto);
            //PGPackage pgPackage = buildPGPackage(pgReqDto);
            //HttpClient.sendMsg(url, pgPackage);
            if (!ConfigConst.env.equals("test")) {
                rabbit.convertAndSend("pg", "up.pg", pgPackage);
            }
        } catch (Exception e) {
            logger.error("发送上行PG包http请求异常 header0:" + pgPacketDto.getHeader0() + ",header1:" + pgPacketDto.getHeader1() + ",imei:" + pgPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SEND_UP_PG_HTTP_ERROR.getCode(), IotServiceExceptionEnum.SEND_UP_PG_HTTP_ERROR.getMsg());
        }
    }

    public void savePGInfo(PGPacketDto pgPacketDto) {
        try {
            RedisUtil redisUtil = new RedisUtil();
            String imei = String.valueOf(pgPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = BikeStatusEnum.MONITOR_ALLBIKE_STATUS.getBikeStatus() + model;
            String motitorValue = BikeStatusEnum.MONITOR_BIKE_STATUS.getBikeStatus() + imei;
            redisUtil.hset(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePGMaP = getBikeStatus(pgPacketDto);
            redisUtil.hmSet(motitorValue, bikePGMaP);
        } catch (Exception e) {
            logger.error("保存上行PG包到缓存异常异常 header0:" + pgPacketDto.getHeader0() + ",header1:" + pgPacketDto.getHeader1() + ",imei:" + pgPacketDto.getImei(), e);
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

    //
    //private PGPackage buildPGPackage(PGReqDto g) {
    //    return new PGPackage()
    //            .setPgDoorLock(g.getPgDoorLock())
    //            .setPgError(g.getPgError())
    //            .setPgAutoLocked(g.getPgAutoLocked())
    //            .setPgTumble(g.getPgTumble())
    //            .setPgStar(g.getPgStar())
    //            .setPgSpeed((int) g.getPgSpeed())
    //            .setPgElectric(g.getPgElectric())
    //            .setPgHight(g.getPgHight())
    //            .setPgImei(String.valueOf(g.getPgImei()))
    //            .setPgLatitude(g.getPgLatitude())
    //            .setPgLocked(g.getPgLocked())
    //            .setPgLongitude(g.getPgLongitude())
    //            .setPgShaked(g.getPgShaked())
    //            .setPgWheelInput(g.getPgWheelInput())
    //            .setPgServer(g.getPgServer())
    //            .setPgClient(g.getPgClient());
    //}
    //
    //private PGReqDto buildPGReqDto(PGPacketDto pgPacketDto) {
    //    PGReqDto pgReqDto = new PGReqDto();
    //    pgReqDto.setPgImei(pgPacketDto.getImei());
    //    pgReqDto.setPgClient(pgPacketDto.getClient());
    //    pgReqDto.setPgServer(pgPacketDto.getServer());
    //    pgReqDto.setPgLongitude(pgPacketDto.getLng());
    //    pgReqDto.setPgLatitude(pgPacketDto.getLat());
    //    pgReqDto.setPgHight(pgPacketDto.getHight());
    //    pgReqDto.setPgSpeed(pgPacketDto.getSpeed());
    //    pgReqDto.setPgStar(pgPacketDto.getStar());
    //    pgReqDto.setPgElectric(pgPacketDto.getPgSubStatus().getCommunicationStatus());
    //    pgReqDto.setPgDoorLock(pgPacketDto.getPgSubStatus().getSwitchStatus());
    //    pgReqDto.setPgLocked(pgPacketDto.getPgSubStatus().getLockStatus());
    //    pgReqDto.setPgShaked(pgPacketDto.getPgSubStatus().getShockStatus());
    //    pgReqDto.setPgWheelInput(pgPacketDto.getPgSubStatus().getInputStatus());
    //    pgReqDto.setPgAutoLocked(pgPacketDto.getPgSubStatus().getAutoLockStatus());
    //    pgReqDto.setPgTumble(pgPacketDto.getPgSubStatus().getFallStatus());
    //    pgReqDto.setPgError(pgPacketDto.getPgSubStatus().getTroubleStatus());
    //    return pgReqDto;
    //}


}
