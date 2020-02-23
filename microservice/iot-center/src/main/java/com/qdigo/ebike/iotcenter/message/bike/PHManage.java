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

import com.qdigo.ebike.api.domain.dto.iot.datagram.PHPackage;
import com.qdigo.ebike.common.core.constants.MQ;
import com.qdigo.ebike.iotcenter.constants.BikeStatusEnum;
import com.qdigo.ebike.iotcenter.dto.gprs.ph.PHPacketDto;
import com.qdigo.ebike.iotcenter.dto.http.req.gprs.PHReqDto;
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

import static com.qdigo.ebike.iotcenter.service.api.PackageManageStrateyg.PHStratrgy;

@Slf4j
@Service(PHStratrgy)
public class PHManage implements PackageManageStrateyg<PHPacketDto> {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public void sendMsg(PHPacketDto phPacketDto) {
        try {
            PHReqDto phReqDto = buildPHReqDto(phPacketDto);
            PHPackage phPackage = buildPHPackage(phReqDto);
            rabbitTemplate.convertAndSend(MQ.Topic.Exchange.ph, MQ.Topic.Key.up_ph, phPackage);
        } catch (Exception e) {
            log.error("发送上行PH包http请求异常 header0:" + phPacketDto.getHeader0() + ",header1:" + phPacketDto.getHeader1() + ",imei:" + phPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SEND_UP_PH_HTTP_ERROR.getCode(), IotServiceExceptionEnum.SEND_UP_PH_HTTP_ERROR.getMsg());
        }

    }

    public void saveInfo(PHPacketDto phPacketDto) {
        try {
            String imei = String.valueOf(phPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = BikeStatusEnum.MONITOR_ALLBIKE_STATUS.getBikeStatus() + model;
            String motitorValue = BikeStatusEnum.MONITOR_BIKE_STATUS.getBikeStatus() + imei;
            redisTemplate.opsForHash().put(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePHMaP = getBikeStatus(phPacketDto);
            redisTemplate.opsForHash().putAll(motitorValue, bikePHMaP);
        } catch (Exception e) {
            log.error("保存上行PH包到缓存异常异常 header0:" + phPacketDto.getHeader0() + ",header1:" + phPacketDto.getHeader1() + ",imei:" + phPacketDto.getImei(), e);
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
        phReqDto.setPhBrakeErroe(phPacketDto.getPhErrorCode().getPhBrakeError());
        phReqDto.setPhControlError(phPacketDto.getPhErrorCode().getPhControlError());
        phReqDto.setPhHandleBarError(phPacketDto.getPhErrorCode().getPhHandleBarError());
        return phReqDto;
    }
}
