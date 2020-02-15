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

import com.qdigo.ebike.iotcenter.SocketServer;
import com.qdigo.ebike.iotcenter.constants.BikeStatusEnum;
import com.qdigo.ebike.iotcenter.constants.ChargeStatusEnum;
import com.qdigo.ebike.iotcenter.dto.baseStation.mc.MCPacketDto;
import com.qdigo.ebike.iotcenter.dto.http.req.charge.MCReqDto;
import com.qdigo.ebike.iotcenter.exception.IotServiceBizException;
import com.qdigo.ebike.iotcenter.exception.IotServiceExceptionEnum;
import com.qdigo.ebike.iotcenter.util.DateUtil;
import com.qdigo.ebike.iotcenter.util.HttpClient;
import com.qdigo.ebike.iotcenter.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MCManage {
    private Logger logger = LoggerFactory.getLogger(MCManage.class);
    private static final String url = "http://api.qdigo.net/v1.0/chargerProtocol/MC";
//	private static final String url = "http://192.168.0.101/v1.0/chargerProtocol/MC";

    public void sendMsg(MCPacketDto mcPacketDto) {
        try {
            MCReqDto mcReqDto = buildPGReqDto(mcPacketDto);
            HttpClient.sendMsg(url, mcReqDto);
        } catch (Exception e) {
            logger.error("发送上行MC包http请求异常 header0:" + mcPacketDto.getHeader0() + ",header1:" + mcPacketDto.getHeader1() + ",imei:" + mcPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SEND_UP_MD_HTTP_ERROR.getCode(), IotServiceExceptionEnum.SEND_UP_MD_HTTP_ERROR.getMsg());
        }
    }

    public void saveUpMCInfo(MCPacketDto mcPacketDto) {
        try {
            RedisUtil redisUtil = new RedisUtil();
            String imei = String.valueOf(mcPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = ChargeStatusEnum.MONITOR_ALLCHARGERPILE_STATUS.getChargeStatus() + model;
            String motitorValue = ChargeStatusEnum.MONITOR_CHARGERPILE_STATUS.getChargeStatus() + imei;
            redisUtil.hset(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePGMaP = getChargeUpStatus(mcPacketDto);
            redisUtil.hmSet(motitorValue, bikePGMaP);
        } catch (Exception e) {
            logger.error("保存上行MC包到缓存异常 header0:" + mcPacketDto.getHeader0() + ",header1:" + mcPacketDto.getHeader1() + ",imei:" + mcPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SAVE_UP_MC_REDIS_ERROR.getCode(), IotServiceExceptionEnum.SAVE_UP_MC_REDIS_ERROR.getMsg());
        }
    }

    public void saveDownMCInfo(MCPacketDto mcPacketDto) {
        try {
            RedisUtil redisUtil = new RedisUtil();
            String imei = String.valueOf(mcPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = ChargeStatusEnum.MONITOR_ALLCHARGERPILE_STATUS.getChargeStatus() + model;
            String motitorValue = ChargeStatusEnum.MONITOR_CHARGERPILE_STATUS.getChargeStatus() + imei;
            redisUtil.hset(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePGMaP = getChargeDownStatus(mcPacketDto);
            redisUtil.hmSet(motitorValue, bikePGMaP);
        } catch (Exception e) {
            logger.error("保存下行MC包到缓存异常 header0:" + mcPacketDto.getHeader0() + ",header1:" + mcPacketDto.getHeader1() + ",imei:" + mcPacketDto.getImei(), e);
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
