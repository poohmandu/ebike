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

import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.service.station.StationGeoService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.common.core.util.geo.LocationConvert;
import com.qdigo.ebike.controlcenter.domain.dto.rent.EndDTO;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PGPackage;
import com.qdigo.ebike.controlcenter.service.inner.command.DeviceService;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PGMongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * description: 
 *
 * date: 2020/3/17 9:52 AM
 * @author niezhao
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AtStationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final StationGeoService geoService;
    private final DeviceService deviceService;
    private final PGMongoService pgMongoService;

    public Long atStation(EndDTO endDTO) {
        Long deviceAtStation;
        val rideDto = endDTO.getRideDto();
        val lat = endDTO.getLatitude();
        val lng = endDTO.getLongitude();
        val deviceMode = endDTO.getDeviceMode();

        val LbsValid = GeoUtil.isValid(lat, lng);
        if (deviceMode == Const.DeviceMode.GPS) {
            deviceAtStation = this.gpsAtStation(endDTO);
        } else if (deviceMode == Const.DeviceMode.SMS) {
            deviceAtStation = this.smsAtStation(endDTO);
        } else if (deviceMode == Const.DeviceMode.BLE) {
            deviceAtStation = this.gpsAtStation(endDTO);
            if (deviceAtStation == null && LbsValid) {
                log.debug("蓝牙模式不用判断人车距离");
                deviceAtStation = this.lbsAtStation(endDTO);
            }
        } else if (deviceMode == Const.DeviceMode.GPS_SMS) {
            deviceAtStation = this.gpsAtStation(endDTO);
            if (deviceAtStation == null && LbsValid && this.bikeUserNear(endDTO)) {
                deviceAtStation = this.lbsAtStation(endDTO);
            }
        } else {
            throw new RuntimeException("不支持模式" + deviceMode);
        }
        return deviceAtStation;
    }

    private Long gpsAtStation(EndDTO endDTO) {
        PGPackage pg = endDTO.getPgPackage();
        RideDto rideDto = endDTO.getRideDto();
        Long agentId = rideDto.getAgentId();
        Long atStation = null;

        int meter;
        int meter0 = 0;
        int meter1 = 0;
        int time = 0;
        if (pg != null) {
            int seconds = pg.getSeconds();
            if (seconds < 10) { // 小于10秒是异常的
                if (pg.getPgDoorLock() == 1) {
                    seconds = 15;
                } else {
                    seconds = 55;
                }
            }

            time = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - pg.getTimestamp());
            time = time < 15 ? time : 13;
            time -= 3; //掏出手机到按下按钮还车时间
            time = time < 0 ? 0 : time;

            meter0 = (int) ((double) pg.getDistance() * time / seconds / 2);
            meter1 = (int) (pg.getPgSpeed() / 3.6 * time) / 2;
            if (meter0 == 0)
                meter = meter1;
            else if (meter1 == 0)
                meter = meter0;
            else {
                meter = Math.min(meter0, meter1);
            }

            meter = Math.min(meter, 15);
            val geoDto = geoService.isAtStationWithCompensate(pg.getPgLatitude(), pg.getPgLongitude(), meter, agentId);
            if (geoDto != null) {
                atStation = geoDto.getStationId();
            }
        }
        log.debug("user:{},bike:{},判断GPS是否在还车点atStation:{},GPS是否有发数据PGPackage={}。计时时间:{},应该补偿范围:{},{}",
                rideDto.getMobileNo(), rideDto.getImei(), atStation, pg, time, meter0, meter1);
        return atStation;
    }

    private Long lbsAtStation(EndDTO endDTO) {
        RideDto rideDto = endDTO.getRideDto();
        val mobileNo = rideDto.getMobileNo();
        val imei = rideDto.getImei();
        val agentId = rideDto.getAgentId();
        var lat = endDTO.getLatitude();
        var lng = endDTO.getLongitude();
        val accuracy = endDTO.getAccuracy();
        val provider = endDTO.getProvider();
        Map<String, Double> gps = LocationConvert.fromAmapToGps(lat, lng);
        lat = gps.get("lat");
        lng = gps.get("lng");
        StationGeoService.StationGeoDto geoDto;
        if (accuracy > 0 && accuracy < 15 && StringUtils.isNotEmpty(provider) && provider.equalsIgnoreCase("gps")) {
            geoDto = geoService.isAtStationWithCompensate(lat, lng, (int) accuracy, agentId);
        } else {
            geoDto = geoService.isAtStation(lat, lng, true, agentId);
        }
        if (geoDto != null) {
            log.debug("user:{},bike{},LBS定位在还车点,手机GPS经纬度:({},{})", mobileNo, imei, lat, lng);
            return geoDto.getStationId();
        } else {
            log.debug("user:{},bike{},LBS定位不在还车点,手机GPS经纬度:({},{})", mobileNo, imei, lat, lng);
            return null;
        }
    }

    //保证优先使用GPS位置
    private Long smsAtStation(EndDTO endDTO) {
        Long atStation = null;
        val rideDto = endDTO.getRideDto();
        val imei = rideDto.getImei();
        val optional = deviceService.smsLocation(imei, rideDto.getMobileNo());
        if (optional.isPresent()) {
            val loc = optional.get();
            val geoDto = geoService.isAtStation(loc.getPgLatitude(), loc.getPgLongitude(), true, rideDto.getAgentId());
            if (geoDto != null) {
                atStation = geoDto.getStationId();
            }
        }
        return atStation;
    }

    private boolean bikeUserNear(EndDTO endDTO) {
        val rideDto = endDTO.getRideDto();
        var lbsLat = endDTO.getLatitude();
        var lbsLng = endDTO.getLongitude();
        Map<String, Double> gps = LocationConvert.fromAmapToGps(lbsLat, lbsLng);
        lbsLat = gps.get("lat");
        lbsLng = gps.get("lng");
        val status = endDTO.getBikeStatusDto();
        val mobileNo = rideDto.getMobileNo();
        val imei = rideDto.getImei();
        //将可能无法获取位置的情况剔除
        boolean gpsNotOk = StringUtils.containsAny(status.getActualStatus(),
                Status.BikeActualStatus.locationFail.getVal(),
                Status.BikeActualStatus.pgNotFound.getVal());
        if (gpsNotOk) {
            log.debug("user:{},bike:{},该车辆硬件有故障:{}", mobileNo, imei, status.getActualStatus());
            return true;
        } else {
            val sleep = pgMongoService.sleep(imei);
            val meter = GeoUtil.getDistanceForMeter(status.getLatitude(), status.getLongitude(), lbsLat, lbsLng);
            val config = endDTO.getAgentCfg();
            log.debug("user:{},bike:{}用户与车辆相隔{}米,GPS是否休眠:{},代理商配置:{}米", mobileNo, imei, meter, sleep, config.getBikeUserNearMeter());
            if (sleep) {
                deviceService.rebootGPSAsync(imei);
            }
            return meter < config.getBikeUserNearMeter();
        }
    }

}
