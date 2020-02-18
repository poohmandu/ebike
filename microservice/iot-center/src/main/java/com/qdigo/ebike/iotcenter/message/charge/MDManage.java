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

import com.qdigo.ebike.iotcenter.netty.SocketServer;
import com.qdigo.ebike.iotcenter.constants.ChargeStatusEnum;
import com.qdigo.ebike.iotcenter.dto.baseStation.md.MDPacketDto;
import com.qdigo.ebike.iotcenter.dto.http.req.charge.MDReqDto;
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


public class MDManage {
    private Logger logger = LoggerFactory.getLogger(MDManage.class);
    private static final String url = "http://api.qdigo.net/v1.0/chargerProtocol/MD";
//	private static final String url = "http://192.168.0.101/v1.0/chargerProtocol/MD";

    public void sendMsg(MDPacketDto mdPacketDto) {
        try {
            MDReqDto pcReqDto = buildMDReqDto(mdPacketDto);
            HttpClient.sendMsg(url, pcReqDto);
        } catch (Exception e) {
            logger.error("发送上行MD包http请求异常 header0:" + mdPacketDto.getHeader0() + ",header1:" + mdPacketDto.getHeader1() + ",imei:" + mdPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SEND_UP_MD_HTTP_ERROR.getCode(), IotServiceExceptionEnum.SEND_UP_MD_HTTP_ERROR.getMsg());
        }

    }

    public void saveMDInfo(MDPacketDto mdPacketDto) {
        try {
            RedisUtil redisUtil = new RedisUtil();
            String imei = String.valueOf(mdPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = ChargeStatusEnum.MONITOR_ALLCHARGERPILE_STATUS.getChargeStatus() + model;
            String motitorValue = ChargeStatusEnum.MONITOR_CHARGERPILE_STATUS.getChargeStatus() + imei;
            redisUtil.hset(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePGMaP = getChargeStatus(mdPacketDto);
            redisUtil.hmSet(motitorValue, bikePGMaP);
        } catch (Exception e) {
            logger.error("保存上行MD包到缓存异常 header0:" + mdPacketDto.getHeader0() + ",header1:" + mdPacketDto.getHeader1() + ",imei:" + mdPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SAVE_UP_MD_REDIS_ERROR.getCode(), IotServiceExceptionEnum.SAVE_UP_MD_REDIS_ERROR.getMsg());
        }
    }

    private Map<String, String> getChargeStatus(MDPacketDto mdPacketDto) {
        Map<String, String> chargeMDMap = new HashMap<String, String>();
        chargeMDMap.put(ChargeStatusEnum.IMEI.getChargeStatus(), mdPacketDto.getImei() + "");
        chargeMDMap.put(ChargeStatusEnum.MD_LASTTIME.getChargeStatus(), DateUtil.format(new Date(), DateUtil.DEFAULT_PATTERN));
        chargeMDMap.put(ChargeStatusEnum.AVAILABLE_SLAVE.getChargeStatus(), SocketServer.NET_IP);
        return chargeMDMap;
    }

    public MDReqDto buildMDReqDto(MDPacketDto mdPacketDto) {
        MDReqDto mdReqDto = new MDReqDto();
        mdReqDto.setMdImei(mdPacketDto.getImei());
        mdReqDto.setMdClient(mdPacketDto.getClient());
        mdReqDto.setMdServer(mdPacketDto.getServer());

        mdReqDto.setMdVoltage(mdPacketDto.getVoltage());
        mdReqDto.setMdCurrent(mdPacketDto.getElectric());
        mdReqDto.setMdState(mdPacketDto.getStatus());
        mdReqDto.setMdPortNumber(mdPacketDto.getChargePortNo());
        mdReqDto.setMdChargeError(mdPacketDto.getChargeFail());
        mdReqDto.setMdPortBikeNumber(mdPacketDto.getCarIdNo());
        return mdReqDto;
    }
}
