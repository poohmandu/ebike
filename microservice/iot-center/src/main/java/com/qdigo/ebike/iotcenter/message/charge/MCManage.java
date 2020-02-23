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

package com.qdigo.ebike.iotcenter.message.charge;

import com.qdigo.ebike.iotcenter.constants.BikeStatusEnum;
import com.qdigo.ebike.iotcenter.constants.ChargeStatusEnum;
import com.qdigo.ebike.iotcenter.dto.baseStation.mc.MCPacketDto;
import com.qdigo.ebike.iotcenter.dto.http.req.charge.MCReqDto;
import com.qdigo.ebike.iotcenter.exception.IotServiceBizException;
import com.qdigo.ebike.iotcenter.exception.IotServiceExceptionEnum;
import com.qdigo.ebike.iotcenter.netty.SocketServer;
import com.qdigo.ebike.iotcenter.service.api.PackageManageStrateyg;
import com.qdigo.ebike.iotcenter.util.DateUtil;
import com.qdigo.ebike.iotcenter.util.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.qdigo.ebike.iotcenter.service.api.PackageManageStrateyg.MCStratrgy;

@Slf4j
@Service(MCStratrgy)
public class MCManage implements PackageManageStrateyg<MCPacketDto> {

    private static final String url = "http://api.qdigo.net/v1.0/chargerProtocol/MC";
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public void sendMsg(MCPacketDto mcPacketDto) {
        try {
            MCReqDto mcReqDto = buildPGReqDto(mcPacketDto);
            HttpClient.sendMsg(url, mcReqDto);
        } catch (Exception e) {
            log.error("发送上行MC包http请求异常 header0:" + mcPacketDto.getHeader0() + ",header1:" + mcPacketDto.getHeader1() + ",imei:" + mcPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SEND_UP_MD_HTTP_ERROR.getCode(), IotServiceExceptionEnum.SEND_UP_MD_HTTP_ERROR.getMsg());
        }
    }

    @Override
    public void saveInfo(MCPacketDto dataPackDto) {

    }

    public void saveUpMCInfo(MCPacketDto mcPacketDto) {
        try {
            //RedisUtil redisUtil = new RedisUtil();
            String imei = String.valueOf(mcPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = ChargeStatusEnum.MONITOR_ALLCHARGERPILE_STATUS.getChargeStatus() + model;
            String motitorValue = ChargeStatusEnum.MONITOR_CHARGERPILE_STATUS.getChargeStatus() + imei;
            //redisUtil.hset(monitorAllBikeKey, imei, motitorValue);
            redisTemplate.opsForHash().put(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePGMaP = getChargeUpStatus(mcPacketDto);
            //redisUtil.hmSet(motitorValue, bikePGMaP);
            redisTemplate.opsForHash().putAll(motitorValue, bikePGMaP);
        } catch (Exception e) {
            log.error("保存上行MC包到缓存异常 header0:" + mcPacketDto.getHeader0() + ",header1:" + mcPacketDto.getHeader1() + ",imei:" + mcPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SAVE_UP_MC_REDIS_ERROR.getCode(), IotServiceExceptionEnum.SAVE_UP_MC_REDIS_ERROR.getMsg());
        }
    }

    public void saveDownMCInfo(MCPacketDto mcPacketDto) {
        try {
            //RedisUtil redisUtil = new RedisUtil();
            String imei = String.valueOf(mcPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = ChargeStatusEnum.MONITOR_ALLCHARGERPILE_STATUS.getChargeStatus() + model;
            String motitorValue = ChargeStatusEnum.MONITOR_CHARGERPILE_STATUS.getChargeStatus() + imei;
            //redisUtil.hset(monitorAllBikeKey, imei, motitorValue);
            redisTemplate.opsForHash().put(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePGMaP = getChargeDownStatus(mcPacketDto);
            //redisUtil.hmSet(motitorValue, bikePGMaP);
            redisTemplate.opsForHash().putAll(motitorValue, bikePGMaP);
        } catch (Exception e) {
            log.error("保存下行MC包到缓存异常 header0:" + mcPacketDto.getHeader0() + ",header1:" + mcPacketDto.getHeader1() + ",imei:" + mcPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SAVE_DOWN_MC_REDIS_ERROR.getCode(), IotServiceExceptionEnum.SAVE_DOWN_MC_REDIS_ERROR.getMsg());
        }
    }

    public MCReqDto buildPGReqDto(MCPacketDto mcPacketDto) {
        MCReqDto mcReqDto = new MCReqDto();
        mcReqDto.setMcImei(mcPacketDto.getImei());
        mcReqDto.setMcCmd(mcPacketDto.getCmd());
        mcReqDto.setMcSequence(mcPacketDto.getSeq());
        mcReqDto.setMcParam(mcPacketDto.getParam());
        mcReqDto.setMcClient(mcPacketDto.getClient());
        mcReqDto.setMcServer(mcPacketDto.getServer());
        return mcReqDto;
    }


    private Map<String, String> getChargeDownStatus(MCPacketDto pcPacketDto) {
        Map<String, String> chargeMap = new HashMap<String, String>();
        String upPCType = "CMD_" + pcPacketDto.getCmd() + " _SEQ_" + pcPacketDto.getSeq() + "_para_" + pcPacketDto.getParam();
        chargeMap.put(BikeStatusEnum.IMEI.getBikeStatus(), pcPacketDto.getImei() + "");
        chargeMap.put(ChargeStatusEnum.DOWN_MC_LASTTIME.getChargeStatus(), DateUtil.format(new Date(), DateUtil.DEFAULT_PATTERN));
        chargeMap.put(ChargeStatusEnum.DOWN_MC_TYPE.getChargeStatus(), upPCType);
        chargeMap.put(ChargeStatusEnum.AVAILABLE_SLAVE.getChargeStatus(), SocketServer.NET_IP);
        return chargeMap;
    }

    private Map<String, String> getChargeUpStatus(MCPacketDto pcPacketDto) {
        Map<String, String> chargeMap = new HashMap<String, String>();
        String upPCType = "CMD_" + pcPacketDto.getCmd() + " _SEQ_" + pcPacketDto.getSeq() + "_para_" + pcPacketDto.getParam();
        chargeMap.put(BikeStatusEnum.IMEI.getBikeStatus(), pcPacketDto.getImei() + "");
        chargeMap.put(ChargeStatusEnum.UP_MC_LASTTIME.getChargeStatus(), DateUtil.format(new Date(), DateUtil.DEFAULT_PATTERN));
        chargeMap.put(ChargeStatusEnum.UP_MC_TYPE.getChargeStatus(), upPCType);
        chargeMap.put(ChargeStatusEnum.AVAILABLE_SLAVE.getChargeStatus(), SocketServer.NET_IP);
        return chargeMap;
    }

}
