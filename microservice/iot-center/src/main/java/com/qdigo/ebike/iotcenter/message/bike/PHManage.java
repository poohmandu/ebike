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

import com.qdigo.ebike.iotcenter.SocketServer;
import com.qdigo.ebike.iotcenter.constants.BikeStatusEnum;
import com.qdigo.ebike.iotcenter.dto.gprs.ph.PHPacketDto;
import com.qdigo.ebike.iotcenter.dto.http.req.gprs.PHReqDto;
import com.qdigo.ebike.iotcenter.dto.mongo.PHPackage;
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


public class PHManage {
    private Logger logger = LoggerFactory.getLogger(PHManage.class);
    private static final String url = "http://api.qdigo.net/v1.0/bikeProtocol/heart";
//  private static final String url = "http://192.168.0.101/v1.0/bikeProtocol/heart";

    private RabbitTemplate rabbit = SpringUtil.getBean(RabbitTemplate.class);

    public void sendMsg(PHPacketDto phPacketDto) {
        try {
            PHReqDto phReqDto = buildPHReqDto(phPacketDto);
            PHPackage phPackage = buildPHPackage(phReqDto);
            //HttpClient.sendMsg(url, phReqDto);
            rabbit.convertAndSend("ph", "up.ph", phPackage);
        } catch (Exception e) {
            logger.error("发送上行PH包http请求异常 header0:" + phPacketDto.getHeader0() + ",header1:" + phPacketDto.getHeader1() + ",imei:" + phPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SEND_UP_PH_HTTP_ERROR.getCode(), IotServiceExceptionEnum.SEND_UP_PH_HTTP_ERROR.getMsg());
        }

    }

    public void savePHInfo(PHPacketDto phPacketDto) {
        try {
            RedisUtil redisUtil = new RedisUtil();
            String imei = String.valueOf(phPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = BikeStatusEnum.MONITOR_ALLBIKE_STATUS.getBikeStatus() + model;
            String motitorValue = BikeStatusEnum.MONITOR_BIKE_STATUS.getBikeStatus() + imei;
            redisUtil.hset(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePHMaP = getBikeStatus(phPacketDto);
            redisUtil.hmSet(motitorValue, bikePHMaP);
        } catch (Exception e) {
            logger.error("保存上行PH包到缓存异常异常 header0:" + phPacketDto.getHeader0() + ",header1:" + phPacketDto.getHeader1() + ",imei:" + phPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SAVE_UP_PH_REDIS_ERROR.getCode(), IotServiceExceptionEnum.SAVE_UP_PH_REDIS_ERROR.getMsg());
        }

    }

    private Map<String, String> getBikeStatus(PHPacketDto phPacketDto) {
        Map<String, String> bikePGMap = new HashMap<String, String>();
        bikePGMap.put(BikeStatusEnum.IMEI.getBikeStatus(), phPacketDto.getImei() + "");
        bikePGMap.put(BikeStatusEnum.PH_LASTTIME.getBikeStatus(), DateUtil.format(new Date(), DateUtil.DEFAULT_PATTERN));
        bikePGMap.put(BikeStatusEnum.AVAILABLE_SLAVE.getBikeStatus(), SocketServer.NET_IP);
        return bikePGMap;
    }

    private PHPackage buildPHPackage(PHReqDto phReqDto) {
        return new PHPackage()
                .setPhAutoLock(phReqDto.getPhAutoLock())
                .setPhAutoLocked(phReqDto.getPhAutoLocked())
                .setPhBatteryVoltage(phReqDto.getPhBatteryVoltage())
                .setPhBrakeErroe(phReqDto.getPhBrakeErroe())
                .setPhControlError(phReqDto.getPhControlError())
                .setPhDoorLock(phReqDto.getPhDoorLock())
                .setPhElectric(phReqDto.getPhElectric())
                .setPhError(phReqDto.getPhError())
                .setPhHandleBarError(phReqDto.getPhHandleBarError())
                .setPhHold(0)
                .setPhImei(String.valueOf(phReqDto.getPhImei()))
                .setPhImsi(phReqDto.getPhImsi())
                .setPhLocked(phReqDto.getPhLocked())
                .setPhMachineError(phReqDto.getPhMachineError())
                .setPhPowerVoltage(phReqDto.getPhPowerVoltage())
                .setPhSentity(phReqDto.getPhSentity())
                .setPhSequence((long) phReqDto.getPhSequence())
                .setPhShaked(phReqDto.getPhShaked())
                .setPhSoc(String.valueOf(phReqDto.getPhSoc()))
                .setPhStar(phReqDto.getPhStar())
                .setPhTumble(phReqDto.getPhTumble())
                .setPhWheelInput(phReqDto.getPhWheelInput())
                .setPhServer(phReqDto.getPhServer())
                .setPhClient(phReqDto.getPhClient());
    }

    private PHReqDto buildPHReqDto(PHPacketDto phPacketDto) {
        PHReqDto phReqDto = new PHReqDto();

        phReqDto.setPhServer(phPacketDto.getServer());
        phReqDto.setPhClient(phPacketDto.getClient());

        phReqDto.setPhImei(phPacketDto.getImei());
        phReqDto.setPhImsi(phPacketDto.getImsi());
        phReqDto.setPhSequence(phPacketDto.getSeq());
        phReqDto.setPhPowerVoltage(phPacketDto.getPowerVoltage());
        phReqDto.setPhBatteryVoltage(phPacketDto.getBatteryVotage());
        phReqDto.setPhSentity(phPacketDto.getSensity());
        phReqDto.setPhAutoLocked(phPacketDto.getGprsSubStatus().getAutoLockStatus());

        phReqDto.setPhStar(phPacketDto.getStar());
        phReqDto.setPhSoc(phPacketDto.getSoc());

        phReqDto.setPhElectric(phPacketDto.getGprsSubStatus().getCommunicationStatus());
        phReqDto.setPhDoorLock(phPacketDto.getGprsSubStatus().getSwitchStatus());
        phReqDto.setPhLocked(phPacketDto.getGprsSubStatus().getLockStatus());
        phReqDto.setPhShaked(phPacketDto.getGprsSubStatus().getShockStatus());
        phReqDto.setPhWheelInput(phPacketDto.getGprsSubStatus().getInputStatus());
        phReqDto.setPhAutoLock(phPacketDto.getGprsSubStatus().getAutoLockStatus());
        phReqDto.setPhTumble(phPacketDto.getGprsSubStatus().getFallStatus());
        phReqDto.setPhError(phPacketDto.getGprsSubStatus().getTroubleStatus());

        phReqDto.setPhMachineError(phPacketDto.getPhErrorCode().getPhMachineError());
        phReqDto.setPhBrakeErroe(phPacketDto.getPhErrorCode().getPhBrakeErroe());
        phReqDto.setPhControlError(phPacketDto.getPhErrorCode().getPhControlError());
        phReqDto.setPhHandleBarError(phPacketDto.getPhErrorCode().getPhHandleBarError());
        return phReqDto;
    }
}
