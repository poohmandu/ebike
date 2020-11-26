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

package com.qdigo.ebike.controlcenter.service.inner.rent.end;

import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.common.core.constants.BikeCfg;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.Ctx;
import com.qdigo.ebike.controlcenter.domain.dto.rent.EndDTO;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PCPackage;
import com.qdigo.ebike.controlcenter.service.inner.command.DeviceService;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PCMongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/17 9:52 AM
 * @author niezhao
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LockBikeService {

    private final DeviceService deviceService;
    private final BikeStatusService bikeStatusService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PCMongoService pcMongoService;

    //@CatAnnotation
    public boolean endBike(EndDTO endDTO) {
        Const.DeviceMode deviceMode = endDTO.getDeviceMode();
        if (deviceMode == Const.DeviceMode.GPS) {
            return this.gpsEndBike(endDTO);
        } else if (deviceMode == Const.DeviceMode.SMS) {
            return this.smsEndBike(endDTO);
        } else if (deviceMode == Const.DeviceMode.BLE) {
            return this.bleEndBike(endDTO);
        } else if (deviceMode == Const.DeviceMode.GPS_SMS) {
            return this.gpsSmsEndBike(endDTO);
        } else {
            throw new RuntimeException("设备还不支持模式:" + deviceMode);
        }
    }

    private boolean gpsEndBike(EndDTO endDTO) {
        val rideDto = endDTO.getRideDto();
        val mobileNo = rideDto.getMobileNo();
        val bikeStatus = endDTO.getBikeStatusDto();
        val imei = rideDto.getImei();
        log.debug("user:{},bike:{}进入GPSEndBike", mobileNo, imei);
        val end = deviceService.gpsEnd(imei, mobileNo);
        if (end) {
            bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.cannotOps.getVal());
        } else {
            bikeStatusService.setActualStatus(bikeStatus, Status.BikeActualStatus.cannotOps.getVal());
        }
        log.debug("user:{},bike:{},bikeStatus保存为,Status:{},ActualStatus:{}", mobileNo, imei, bikeStatus.getStatus(), bikeStatus.getActualStatus());
        val key = Keys.flagOps.getKey(imei);
        redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());
        return end;
    }

    private boolean bleEndBike(EndDTO endDTO) {
        val rideDto = endDTO.getRideDto();
        // 蓝牙模式默认都成功了
        val mobileNo = rideDto.getMobileNo();
        val imei = rideDto.getImei();
        log.debug("user:{},bike:{}进入BleEndBike", mobileNo, imei);

        val key = Keys.flagOps.getKey(imei);
        redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());

        return true;
    }

    private boolean smsEndBike(EndDTO endDTO) {
        val rideDto = endDTO.getRideDto();
        val mobileNo = rideDto.getMobileNo();
        val bikeStatus = endDTO.getBikeStatusDto();
        val imei = rideDto.getImei();
        val end = deviceService.smsClose(imei, mobileNo);
        log.debug("user:{},bike:{}通过短信还车结果:{}", mobileNo, imei, end);
        if (end) {
            bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.smsCannotOps.getVal());
        } else {
            bikeStatusService.setActualStatus(bikeStatus, Status.BikeActualStatus.smsCannotOps.getVal());
        }
        log.debug("user:{},bike:{},bikeStatus保存为,Status:{},ActualStatus:{}", mobileNo, imei, bikeStatus.getStatus(), bikeStatus.getActualStatus());

        val key = Keys.flagOps.getKey(imei);
        redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());
        return end;
    }


    //逻辑状态之后统一更新
    public boolean gpsSmsEndBike(EndDTO endDTO) {
        val rideDto = endDTO.getRideDto();
        val status = endDTO.getBikeStatusDto();
        val mobileNo = rideDto.getMobileNo();
        val pg = endDTO.getPgPackage();
        val imei = rideDto.getImei();

        boolean end;

        if (pg == null) {
            end = deviceService.gpsSmsEndFast(imei, mobileNo);
        } else {
            end = deviceService.gpsSmsEnd0(imei, mobileNo);
        }

        log.debug("user:{},bike:{}进行gps和sms一起还车,还车结果:{}", mobileNo, imei, end);
        if (end) {
            bikeStatusService.removeActualStatus(status, Status.BikeActualStatus.cannotOps.getVal());
            bikeStatusService.removeActualStatus(status, Status.BikeActualStatus.smsCannotOps.getVal());
        } else {
            bikeStatusService.setActualStatus(status, Status.BikeActualStatus.cannotOps.getVal());
            bikeStatusService.setActualStatus(status, Status.BikeActualStatus.smsCannotOps.getVal());
        }
        if (status.getActualStatus().contains(Status.BikeActualStatus.locationFail.getVal())) {
            deviceService.rebootGPSAsync(imei);
        }
        log.debug("user:{},bike:{},同时使用定位和短信还车bikeStatus保存为,Status:{},ActualStatus:{}", mobileNo, imei, status.getStatus(), status.getActualStatus());

        String key = Keys.flagOps.getKey(imei);
        redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());

        return end;
    }

    //TODO 先放宽策略，有反馈还车未断电再查询PX_SEQ
    //@CatAnnotation
    public boolean confirmByPC(String imei) {
        PCPackage pcPackage = pcMongoService.findLockPC(imei, Ctx.now());
        boolean confirmByPC = pcPackage != null;
        log.debug("通过PC包确认是否已经还车:{}", confirmByPC);
        return confirmByPC;
    }

}
