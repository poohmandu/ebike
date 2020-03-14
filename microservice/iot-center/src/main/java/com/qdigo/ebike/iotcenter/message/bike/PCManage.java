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

import com.qdigo.ebike.api.domain.dto.iot.datagram.PCPackage;
import com.qdigo.ebike.common.core.constants.MQ;
import com.qdigo.ebike.commonconfig.configuration.properties.QdigoOnOffProperties;
import com.qdigo.ebike.iotcenter.constants.BikeStatusEnum;
import com.qdigo.ebike.iotcenter.dto.gprs.pc.PCPacketDto;
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

import static com.qdigo.ebike.iotcenter.service.api.PackageManageStrateyg.PCStratrgy;

@Slf4j
@Service(PCStratrgy)
public class PCManage implements PackageManageStrateyg<PCPacketDto> {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private QdigoOnOffProperties onOffProperties;

    public void sendMsg(PCPacketDto pcPacketDto) {
        try {
            PCPackage pcPackage = buildPCPackage(pcPacketDto);
            if (onOffProperties.isIotMqSend()) {
                rabbitTemplate.convertAndSend(MQ.Topic.Exchange.pc, MQ.Topic.Key.up_pc, pcPackage);
            }
        } catch (Exception e) {
            log.error("发送上行PC包http请求异常 header0:" + pcPacketDto.getHeader0() + ",header1:" + pcPacketDto.getHeader1() + ",imei:" + pcPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SEND_UP_PC_HTTP_ERROR.getCode(), IotServiceExceptionEnum.SEND_UP_PC_HTTP_ERROR.getMsg());
        }
    }

    public void saveInfo(PCPacketDto dataPackDto) {

    }

    public void saveUpPCInfo(PCPacketDto pcPacketDto) {
        try {
            //RedisUtil redisUtil = new RedisUtil();
            String imei = String.valueOf(pcPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = BikeStatusEnum.MONITOR_ALLBIKE_STATUS.getBikeStatus() + model;
            String motitorValue = BikeStatusEnum.MONITOR_BIKE_STATUS.getBikeStatus() + imei;
            //redisUtil.hset(monitorAllBikeKey, imei, motitorValue);
            redisTemplate.opsForHash().put(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePCMaP = getBikeUpStatus(pcPacketDto);
            //redisUtil.hmSet(motitorValue, bikePCMaP);
            redisTemplate.opsForHash().putAll(motitorValue, bikePCMaP);
        } catch (Exception e) {
            log.error("保存上行pc包到缓存异常异常 header0:" + pcPacketDto.getHeader0() + ",header1:" + pcPacketDto.getHeader1() + ",imei:" + pcPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SEND_UP_PC_HTTP_ERROR.getCode(), IotServiceExceptionEnum.SEND_UP_PC_HTTP_ERROR.getMsg());
        }

    }

    private Map<String, String> getBikeUpStatus(PCPacketDto pcPacketDto) {
        Map<String, String> bikePGMap = new HashMap<String, String>();
        String upPCType = "CMD_" + pcPacketDto.getCmd() + " _SEQ_" + pcPacketDto.getSeq() + "_para_" + pcPacketDto.getParam();
        bikePGMap.put(BikeStatusEnum.IMEI.getBikeStatus(), pcPacketDto.getImei() + "");
        bikePGMap.put(BikeStatusEnum.UP_PC_LASTTIME.getBikeStatus(), DateUtil.format(new Date(), DateUtil.DEFAULT_PATTERN));
        bikePGMap.put(BikeStatusEnum.UP_PC_TYPE.getBikeStatus(), upPCType);
        bikePGMap.put(BikeStatusEnum.AVAILABLE_SLAVE.getBikeStatus(), SocketServer.NET_IP);
        return bikePGMap;
    }

    public void saveDownPCInfo(PCPacketDto pcPacketDto) {
        try {
            //RedisUtil redisUtil = new RedisUtil();
            String imei = String.valueOf(pcPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = BikeStatusEnum.MONITOR_ALLBIKE_STATUS.getBikeStatus() + model;
            String motitorValue = BikeStatusEnum.MONITOR_BIKE_STATUS.getBikeStatus() + imei;
            //redisUtil.hset(monitorAllBikeKey, imei, motitorValue);
            redisTemplate.opsForHash().put(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePCMaP = getBikeDownStatus(pcPacketDto);
            //redisUtil.hmSet(motitorValue, bikePCMaP);
            redisTemplate.opsForHash().putAll(motitorValue, bikePCMaP);
        } catch (Exception e) {
            log.error("保存下行pc包到缓存异常异常 header0:" + pcPacketDto.getHeader0() + ",header1:" + pcPacketDto.getHeader1() + ",imei:" + pcPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SAVE_DOWN_PC_REDIS_ERROR.getCode(), IotServiceExceptionEnum.SAVE_DOWN_PC_REDIS_ERROR.getMsg());
        }

    }

    private Map<String, String> getBikeDownStatus(PCPacketDto pcPacketDto) {
        Map<String, String> bikePGMap = new HashMap<String, String>();
        String upPCType = "CMD_" + pcPacketDto.getCmd() + " _SEQ_" + pcPacketDto.getSeq() + "_para_" + pcPacketDto.getParam();
        bikePGMap.put(BikeStatusEnum.IMEI.getBikeStatus(), pcPacketDto.getImei() + "");
        bikePGMap.put(BikeStatusEnum.DOWN_PC_LASTTIME.getBikeStatus(), DateUtil.format(new Date(), DateUtil.DEFAULT_PATTERN));
        bikePGMap.put(BikeStatusEnum.DOWN_PC_TYPE.getBikeStatus(), upPCType);
        bikePGMap.put(BikeStatusEnum.AVAILABLE_SLAVE.getBikeStatus(), SocketServer.NET_IP);
        return bikePGMap;
    }

    private PCPackage buildPCPackage(PCPacketDto pcPacketDto) {
        return new PCPackage().setPcCmd(pcPacketDto.getCmd())
                .setPcImei(String.valueOf(pcPacketDto.getImei()))
                .setPcParam(pcPacketDto.getParam())
                .setPcSequence(pcPacketDto.getSeq())
                .setTimestamp(System.currentTimeMillis())
                .setPcClient(pcPacketDto.getClient())
                .setPcServer(pcPacketDto.getServer());
    }

}
