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

import com.qdigo.ebike.iotcenter.constants.ChargeStatusEnum;
import com.qdigo.ebike.iotcenter.dto.baseStation.ml.MLPacketDto;
import com.qdigo.ebike.iotcenter.dto.http.req.charge.MLReqDto;
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

import static com.qdigo.ebike.iotcenter.service.api.PackageManageStrateyg.MLStratrgy;

@Slf4j
@Service(MLStratrgy)
public class MLManage implements PackageManageStrateyg<MLPacketDto> {

    private static final String url = "http://api.qdigo.net/v1.0/chargerProtocol/ML";
    //	private static final String url = "http://192.168.0.101/v1.0/chargerProtocol/ML";
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public void sendMsg(MLPacketDto mlPacketDto) {
        try {
            MLReqDto pcReqDto = buildMLReqDto(mlPacketDto);
            HttpClient.sendMsg(url, pcReqDto);
        } catch (Exception e) {
            log.error("发送上行ML包http请求异常 header0:" + mlPacketDto.getHeader0() + ",header1:" + mlPacketDto.getHeader1() + ",imei:" + mlPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SEND_UP_ML_HTTP_ERROR.getCode(), IotServiceExceptionEnum.SEND_UP_ML_HTTP_ERROR.getMsg());
        }
    }

    public void saveInfo(MLPacketDto mlPacketDto) {
        try {
            //RedisUtil redisUtil = new RedisUtil();
            String imei = String.valueOf(mlPacketDto.getImei());
            String model = imei.substring(imei.length() - 1);
            String monitorAllBikeKey = ChargeStatusEnum.MONITOR_ALLCHARGERPILE_STATUS.getChargeStatus() + model;
            String motitorValue = ChargeStatusEnum.MONITOR_CHARGERPILE_STATUS.getChargeStatus() + imei;
            //redisUtil.hset(monitorAllBikeKey, imei, motitorValue);
            redisTemplate.opsForHash().put(monitorAllBikeKey, imei, motitorValue);
            Map<String, String> bikePGMaP = getChargeStatus(mlPacketDto);
            //redisUtil.hmSet(motitorValue, bikePGMaP);
            redisTemplate.opsForHash().putAll(motitorValue, bikePGMaP);
        } catch (Exception e) {
            log.error("保存上行ML包到缓存异常 header0:" + mlPacketDto.getHeader0() + ",header1:" + mlPacketDto.getHeader1() + ",imei:" + mlPacketDto.getImei(), e);
            throw new IotServiceBizException(IotServiceExceptionEnum.SAVE_UP_ML_REDIS_ERROR.getCode(), IotServiceExceptionEnum.SAVE_UP_ML_REDIS_ERROR.getMsg());
        }
    }

    private Map<String, String> getChargeStatus(MLPacketDto mlPacketDto) {
        Map<String, String> chargeMDMap = new HashMap<String, String>();
        chargeMDMap.put(ChargeStatusEnum.IMEI.getChargeStatus(), mlPacketDto.getImei() + "");
        chargeMDMap.put(ChargeStatusEnum.ML_LASTTIME.getChargeStatus(), DateUtil.format(new Date(), DateUtil.DEFAULT_PATTERN));
        chargeMDMap.put(ChargeStatusEnum.AVAILABLE_SLAVE.getChargeStatus(), SocketServer.NET_IP);
        return chargeMDMap;
    }

    public MLReqDto buildMLReqDto(MLPacketDto mlPacketDto) {
        MLReqDto mlReqDto = new MLReqDto();
        mlReqDto.setMlImei(mlPacketDto.getImei());
        mlReqDto.setMlCellid(mlPacketDto.getCellid());
        mlReqDto.setMlLAC(mlPacketDto.getLac());
        mlReqDto.setMlSingal(mlPacketDto.getSignal());
        mlReqDto.setMlTemperature(mlPacketDto.getTemperature());
        mlReqDto.setMlImsi(mlPacketDto.getImsi());
        mlReqDto.setMlServer(mlPacketDto.getServer());
        mlReqDto.setMlClient(mlPacketDto.getClient());
        return mlReqDto;
    }
}
