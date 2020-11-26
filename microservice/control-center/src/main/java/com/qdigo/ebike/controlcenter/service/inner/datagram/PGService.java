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

package com.qdigo.ebike.controlcenter.service.inner.datagram;

import com.alibaba.fastjson.JSON;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.bike.BikeGpsStatusDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.bike.BikeAddressService;
import com.qdigo.ebike.api.service.bike.BikeGpsStatusService;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.api.service.station.StationGeoService;
import com.qdigo.ebike.api.service.third.push.PushService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.BikeCfg;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.commonaop.annotations.Token;
import com.qdigo.ebike.commonaop.constants.DB;
import com.qdigo.ebike.controlcenter.domain.dto.BikePgInfo;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PGPackage;
import com.qdigo.ebike.controlcenter.service.inner.command.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2017/11/6.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PGService {

    private final BikeStatusService bikeStatusService;
    private final BikeAddressService bikeAddressService;
    private final StationGeoService geoService;
    private final DeviceService deviceService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PGMongoService pgMongoService;
    private final PGCheckService pgCheckService;
    private final PushService pushService;
    private final UserService userService;
    private final BikeGpsStatusService bikeGpsStatusService;

    //@CatAnnotation
    public void saveLatestPG(PGPackage pg) {
        String imei = pg.getPgImei();
        try {
            String key = Keys.up_pg_latest.getKey(FormatUtil.getCurDate(), imei);
            redisTemplate.opsForValue().set(key, JSON.toJSONString(pg), Const.pgNotFoundSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("保存最新pg包失败:", e);
        }
    }

    //各个sql操作相对独立,无需事务
    //@CatAnnotation
    public void updateStatusLocation(PGPackage pg, BikePgInfo bikePgInfo) {
        try {
            val imei = pg.getPgImei();

            BikeStatusDto bikeStatusDto = bikePgInfo.getBikeStatusDto();
            val lng = pg.getPgLongitude();
            val lat = pg.getPgLatitude();
            if (lat == 0.0 || lng == 0.0) {
                //以防随时可以还车 stationId,areaId 都为空
                bikeStatusDto.setAreaId(null).setStationId(null);
                return;
            }
            BikeCfg.LocationType locationType = BikeCfg.LocationType.gps;
            String address = bikeStatusDto.getAddress();
            Double updateLat = lat, updateLng = lng;
            Long stationId, areaId;

            val meter = GeoUtil.getDistanceForMeter(lat, lng, bikeStatusDto.getLatitude(), bikeStatusDto.getLongitude());
            if (meter > 150) {
                BikeAddressService.BikeAddressDto bikeAddressDto = bikeAddressService.updateBikeAddress(lat, lng, imei);
                if (bikeAddressDto != null) {
                    address = bikeAddressDto.getAddress();
                }
            }

            AgentCfg agentCfg = bikePgInfo.getAgentCfg();

            boolean move;
            boolean inUse;
            move = pgMongoService.canMove(pg);
            inUse = (bikePgInfo.getRideDto() != null);

            //更新车辆位置与还车点的关系
            val oldStationId = bikeStatusDto.getStationId();

            val newStation = geoService.isAtStation(lat, lng, inUse, agentCfg.getAgentId());

            if (newStation != null) {
                stationId = newStation.getStationId();

                // 从外到外
            } else if (oldStationId == null) {
                stationId = null;
            } else if (move || inUse) {
                stationId = null;
            } else { // 从内到外;静止时,飘移到还车点外面
                updateLat = bikeStatusDto.getLatitude();
                updateLng = bikeStatusDto.getLongitude();
                stationId = oldStationId;
            }

            //更新车辆位置与服务区的关系
            areaId = geoService.isAtArea(lat, lng, agentCfg.getAgentId());

            bikeStatusDto.setLatitude(updateLat).setLongitude(updateLng).setLocationType(locationType)
                    .setStationId(stationId).setAreaId(areaId).setAddress(address);

        } catch (Exception e) {
            log.error("检查车辆位置出错", e);
        }
    }


    //@CatAnnotation
    @Token(key = "pgImei", expireSeconds = 60, DB = DB.Memory)
    public void checkBikeStatus(PGPackage pg, PGPackage old, BikePgInfo bikePgInfo) {
        try {
            if (old == null) {
                return;
            }
            val imei = pg.getPgImei();
            if (this.historyPGNotFound(pg, old)) {
                return;
            }
            //保证是在GPS稳定的时候检测它的状态
            BikeStatusDto bikeStatus = bikePgInfo.getBikeStatusDto();
            if (bikeStatus == null) {
                log.debug("checkBikeStatus未查询到imei号为{}的车辆", imei);
                return;
            }

            this.checkActualStatus(bikeStatus, pg, bikePgInfo.getBikeGpsStatusDto());
            this.checkSleep(pg, old);
            this.checkOps(imei, bikeStatus, pg);
            this.checkAreaWarn(imei, bikePgInfo);

        } catch (Exception e) {
            log.error("检查车辆状态出错", e);
        }
    }

    public void updateBikeInfo(BikePgInfo bikePgInfo, PGPackage pgPackage) {
        val pgDto = ConvertUtil.to(pgPackage, com.qdigo.ebike.api.domain.dto.iot.datagram.PGPackage.class);
        bikeGpsStatusService.updatePg(pgDto);

        BikeStatusDto bikeStatusDto = bikePgInfo.getBikeStatusDto();
        bikeStatusService.update(bikeStatusDto);
    }

    public void checkActualStatus(BikeStatusDto bikeStatus, PGPackage pg, BikeGpsStatusDto gpsStatus) {
        if (gpsStatus == null) {
            log.warn("bikeGpsStatusDto在上行业务为null");
            return;
        }
        // pgNotFound
        bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.pgNotFound.getVal());
        // locationFail
        if (pg.getPgLatitude() == 0.0 && pg.getPgLongitude() == 0.0) {
            bikeStatusService.setActualStatus(bikeStatus, Status.BikeActualStatus.locationFail.getVal());
        } else {
            bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.locationFail.getVal());
        }
        // internalError
        boolean internalOk = gpsStatus.getError() == 0 && gpsStatus.getMachineError() == 0 && gpsStatus.getBrakeError() == 0
                && gpsStatus.getHandleBarError() == 0 && gpsStatus.getControlError() == 0;
        if (internalOk) {
            bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.internalError.getVal());
        } else {
            bikeStatusService.setActualStatus(bikeStatus, Status.BikeActualStatus.internalError.getVal());
        }
        // noPower
        if (pg.getPgElectric() == 0 || gpsStatus.getPowerVoltage() == 0) {
            bikeStatusService.setActualStatus(bikeStatus, Status.BikeActualStatus.noPower.getVal());
        } else {
            bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.noPower.getVal());
        }
        // cannotOps 由操作车辆后决定
        // userReport
        // other

    }


    public void checkSleep(PGPackage pg, PGPackage old) {
        if (!this.wheelInput(pg, old)) {
            return;
        }
        val imei = pg.getPgImei();
        if (pgMongoService.sleep(pg, old)) {
            log.debug("{}车辆检测到轮动过程中GPS休眠", imei);
            deviceService.rebootGPSAsync(imei);
        } else if (pgMongoService.locationFail(pg, old)) {
            log.debug("{}车辆检测到轮动过程中GPS定位为0", imei);
            deviceService.rebootGPSAsync(imei);
        }
    }


    public void checkOps(String imei, BikeStatusDto bikeStatus, PGPackage pg) {
        if (pg.getPgWheelInput() == 1) {
            return;
        }
        //需要改成先发现lock == 0
        val opsKey = Keys.flagOps.getKey(imei);
        String opsValue = redisTemplate.opsForValue().get(opsKey);
        if (opsValue == null) {
            return;
        }
        log.debug("检查到车辆存在操作标记:{},imei={}", opsValue, imei);

        if (opsValue.equals(BikeCfg.OpsType.end.name())) {
            pgCheckService.onEndOps(imei, bikeStatus, pg);
        } else if (opsValue.equals(BikeCfg.OpsType.lock.name())) {
            pgCheckService.onLockOps(imei, bikeStatus, pg);
        } else if (opsValue.equals(BikeCfg.OpsType.on.name())) {
            pgCheckService.onStartOps(imei, bikeStatus, pg);
        } else if (opsValue.equals(BikeCfg.OpsType.off.name())) {
            pgCheckService.onCloseOps(imei, bikeStatus, pg);
        } else if (opsValue.equals(BikeCfg.OpsType.unlock.name())) {
            pgCheckService.onUnlockOps(imei, bikeStatus, pg);
        }
    }

    public void checkAreaWarn(String imei, BikePgInfo bikePgInfo) {
        val key = Keys.flagAreaWarn.getKey(imei);
        boolean hasKey = redisTemplate.hasKey(key);
        BikeStatusDto bikeStatus = bikePgInfo.getBikeStatusDto();
        if (bikeStatus.getAreaId() != null) {
            if (hasKey) {
                //前面的车总是返回失败
                deviceService.closeAreaWarn(imei, "system", success -> {
                    //if (success) {
                    redisTemplate.delete(key);
                    //}
                });
            }
            return;
        }
        // agentId = null;
        if (hasKey) {
            return;
        }
        AgentCfg agentCfg = bikePgInfo.getAgentCfg();
        if (agentCfg == null) {
            return;
        }

        if (bikeStatus.getStatus() == Status.BikeLogicStatus.available.getVal()) {
            return;
        }
        RideDto rideDto = bikePgInfo.getRideDto();
        if (rideDto == null) {
            return;
        }
        UserDto userDto = userService.findByMobileNo(rideDto.getMobileNo());

        deviceService.openAreaWarn(imei, "system", success -> {
            if (agentCfg.isForceOff()) {
                log.debug("{}服务区外强制断电", imei);
                deviceService.forceEnable(imei, "system");
                deviceService.shutdown(imei, "system");
            }
            PushService.Param param = new PushService.Param().setMobileNo(userDto.getMobileNo()).setDeviceId(userDto.getDeviceId())
                    .setPushType(Const.PushType.areaWarn).setAlert("你已骑出规定的服务区,请骑回服务区");
            pushService.pushNotation(param);
            redisTemplate.opsForValue().set(key, FormatUtil.getCurTime());
        });

    }

    //public static final List<Map> points = new CopyOnWriteArrayList<>();

    @Async
    public void bdEntityService(PGPackage pg, PGPackage old) {
        //try {
        //    String imei = pg.getPgImei();
        //    Date now = new Date();
        //    String dateStr = FormatUtil.y_M_d.format(now) + " 23:58:00";
        //    Date date = FormatUtil.yMdHms.parse(dateStr);
        //    if (now.after(date)) {
        //        bdMapService.addEntity(imei);
        //    }
        //    if (pg.getPgLatitude() == 0.0 || pg.getPgLongitude() == 0.0) {
        //        return;
        //    }
        //    if (Objects.equals(pg.getPgLatitude(), old.getPgLatitude()) &&
        //            Objects.equals(pg.getPgLongitude(), old.getPgLongitude())) {
        //        return;
        //    }
        //    if (pg.getPgWheelInput() == 0) {
        //        return;
        //    }
        //    val bike = bikeRepository.findByImeiId(imei).orElse(null);
        //    if (bike == null) {
        //        return;
        //    }
        //    if (bike.isDeleted() || !bike.isOnline()) {
        //        return;
        //    }
        //    if (rideRecordDao.findByRidingBike(bike) == null) {
        //        return;
        //    }
        //    Map point = ImmutableMap.builder().put("imei", imei)
        //            .put("latitude", pg.getPgLatitude())
        //            .put("longitude", pg.getPgLongitude())
        //            .put("time", System.currentTimeMillis())
        //            .build();
        //    points.add(point);
        //    if (points.size() >= 100) {
        //        bdMapService.addPoints(points);
        //        points.clear();
        //    }
        //} catch (Exception e) {
        //    log.error("添加百度地图鹰眼服务实体失败:{}", e.getMessage());
        //}
    }

    private boolean historyPGNotFound(PGPackage pg, PGPackage old) {
        return (pg.getSeconds() != null && pg.getSeconds() > Const.pgNotFoundSeconds) ||
                (old.getSeconds() != null && old.getSeconds() > Const.pgNotFoundSeconds);
    }

    private boolean wheelInput(PGPackage pg, PGPackage old) {
        return pg.getPgWheelInput() == 1 && old.getPgWheelInput() == 1;
    }


}
