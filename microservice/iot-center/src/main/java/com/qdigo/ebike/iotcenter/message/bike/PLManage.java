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
import com.qdigo.ebike.iotcenter.constants.BikeStatusEnum;
import com.qdigo.ebike.iotcenter.dto.gprs.pl.PLPacketDto;
import com.qdigo.ebike.iotcenter.dto.http.req.gprs.PLReqDto;
import com.qdigo.ebike.iotcenter.dto.mongo.PLPackage;
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


public class PLManage {
    private Logger logger = LoggerFactory.getLogger(PLManage.class);
    private static final String url = "http://api.qdigo.net/v1.0/bikeProtocol/GPSLocation";
//	private static final String url = "http://192.168.0.101/v1.0/bikeProtocol/GPSLocation";

    private RabbitTemplate rabbit = SpringUtil.getBean(RabbitTemplate.class);

    public void sendMsg(PLPacketDto plPacketDto) {
        try {
            PLReqDto plReqDto = buildPLReqDto(plPacketDto);
            PLPackage plPackage = buildPLPackage(plReqDto);
            //HttpClient.sendMsg(url, plReqDto);
            rabbit.convertAndSend("pl", "up.pl", plPackage);
        } catch (Exception e) {
            logger.error("发送上行PL包http请求异常 header0:" + plPacketDto.getHeader0() + ",header1:" + plPacketDto.getHeader1() + ",imei:" + plPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SEND_UP_PL_HTTP_ERROR.getCode(), IotServiceExceptionEnum.SEND_UP_PL_HTTP_ERROR.getMsg());
        }

    }

    public void savePLInfo(PLPacketDto plPacketDto) {
        try {
            RedisUtil redisUtil = new RedisUtil();
            String imei = String.valueOf(plPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = BikeStatusEnum.MONITOR_ALLBIKE_STATUS.getBikeStatus() + model;
            String motitorValue = BikeStatusEnum.MONITOR_BIKE_STATUS.getBikeStatus() + imei;
            redisUtil.hset(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePLMaP = getBikeStatus(plPacketDto);
            redisUtil.hmSet(motitorValue, bikePLMaP);
        } catch (Exception e) {
            logger.error("保存上行PL包到缓存异常异常 header0:" + plPacketDto.getHeader0() + ",header1:" + plPacketDto.getHeader1() + ",imei:" + plPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SAVE_UP_PL_REDIS_ERROR.getCode(), IotServiceExceptionEnum.SAVE_UP_PL_REDIS_ERROR.getMsg());
        }
    }

    private Map<String, String> getBikeStatus(PLPacketDto plPacketDto) {
        Map<String, String> bikePGMap = new HashMap<String, String>();
        bikePGMap.put(BikeStatusEnum.IMEI.getBikeStatus(), plPacketDto.getImei() + "");
        bikePGMap.put(BikeStatusEnum.PL_LASTTIME.getBikeStatus(), DateUtil.format(new Date(), DateUtil.DEFAULT_PATTERN));
        bikePGMap.put(BikeStatusEnum.AVAILABLE_SLAVE.getBikeStatus(), SocketServer.NET_IP);
        return bikePGMap;
    }

    private PLPackage buildPLPackage(PLReqDto plReqDto) {
        return new PLPackage()
                .setPlAutoLocked(plReqDto.getPlAutoLocked())
                .setPlCellid(String.valueOf(plReqDto.getPlCellid()))
                .setPlDoorLock(plReqDto.getPlDoorLock())
                .setPlElectric(plReqDto.getPlElectric())
                .setPlError(plReqDto.getPlError())
                .setPlImei(String.valueOf(plReqDto.getPlImei()))
                .setPlLac(String.valueOf(plReqDto.getPlLac()))
                .setPlLocked(plReqDto.getPlLocked())
                .setPlShaked(plReqDto.getPlShaked())
                .setPlSingal(String.valueOf(plReqDto.getPlSingal()))
                .setPlTumble(plReqDto.getPlTumble())
                .setPlWheelInput(plReqDto.getPlWheelInput())
                .setPlServer(plReqDto.getPlServer())
                .setPlClient(plReqDto.getPlClient());
    }

    private PLReqDto buildPLReqDto(PLPacketDto plPacketDto) {
        PLReqDto pgReqDto = new PLReqDto();
        pgReqDto.setPlImei(plPacketDto.getImei());

        pgReqDto.setPlClient(plPacketDto.getClient());
        pgReqDto.setPlServer(plPacketDto.getServer());

        pgReqDto.setPlLac(plPacketDto.getLac());
        pgReqDto.setPlCellid(plPacketDto.getCellid());
        pgReqDto.setPlSingal(plPacketDto.getSignal());

        pgReqDto.setPlElectric(plPacketDto.getPgSubStatus().getCommunicationStatus());
        pgReqDto.setPlDoorLock(plPacketDto.getPgSubStatus().getSwitchStatus());
        pgReqDto.setPlLocked(plPacketDto.getPgSubStatus().getLockStatus());
        pgReqDto.setPlShaked(plPacketDto.getPgSubStatus().getShockStatus());
        pgReqDto.setPlWheelInput(plPacketDto.getPgSubStatus().getInputStatus());
        pgReqDto.setPlTumble(plPacketDto.getPgSubStatus().getFallStatus());
        pgReqDto.setPlError(plPacketDto.getPgSubStatus().getTroubleStatus());
        return pgReqDto;
    }
}
