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

import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PGPackage;
import com.qdigo.ebike.controlcenter.service.inner.command.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created by niezhao on 2017/12/21.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PGCheckService {

    private final RedisTemplate<String, String> redisTemplate;
    private final DeviceService deviceService;
    private final BikeStatusService bikeStatusService;

    public void onEndOps(String imei, BikeStatusDto bikeStatus, PGPackage pg) {
        val endKey = Keys.flagOps.getKey(imei);
        if (pg.getPgLocked() == 1) {
            log.debug("检测到车辆还车后已经锁车,删除key:{}", endKey);
            redisTemplate.delete(endKey);
            return;
        }
        if (bikeStatus.getStatus() != Status.BikeLogicStatus.available.getVal()) {
            log.debug("检测到车辆不在空闲中,删除key={}", endKey);
            redisTemplate.delete(endKey);
            return;
        }
        val result = deviceService.gpsEnd(imei, "system");
        if (result) {
            bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.cannotOps.getVal());
        }
        log.debug("{}车辆检查还有操作未完成,type:end,result={}", imei, result);
    }

    public void onLockOps(String imei, BikeStatusDto bikeStatus, PGPackage pg) {
        val key = Keys.flagOps.getKey(imei);
        if (pg.getPgLocked() == 1) {
            log.debug("检测到车辆已经锁车,删除key:{}", key);
            redisTemplate.delete(key);
            return;
        }
        //lock 比较特殊是否在使用中 都给它上锁
        val result = deviceService.lock(imei, "system");
        if (result) {
            bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.cannotOps.getVal());
        }
        log.debug("{}车辆检查还有操作未完成,type:lock,result={}", imei, result);
    }

    public void onCloseOps(String imei, BikeStatusDto bikeStatus, PGPackage pg) {
        val key = Keys.flagOps.getKey(imei);
        if (pg.getPgDoorLock() == 0) {
            log.debug("检测到车辆已经断电,删除key:{}", key);
            redisTemplate.delete(key);
            return;
        }
        boolean result;
        if (bikeStatus.getStatus() != Status.BikeLogicStatus.available.getVal()) {
            result = deviceService.shutdown(imei, "system");
        } else {
            log.debug("检测到车辆不在使用中,删除key={}", key);
            result = deviceService.lock(imei, "system");
        }
        if (result) {
            bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.cannotOps.getVal());
        }
        log.debug("{}车辆检查还有操作未完成,type:close,result={}", imei, result);
    }

    public void onStartOps(String imei, BikeStatusDto bikeStatus, PGPackage pg) {
        val key = Keys.flagOps.getKey(imei);
        if (pg.getPgDoorLock() == 1) {
            log.debug("检测到车辆已经上电,删除key:{}", key);
            redisTemplate.delete(key);
            return;
        }
        //不要帮忙操作
    }

    public void onUnlockOps(String imei, BikeStatusDto bikeStatus, PGPackage pg) {
        val key = Keys.flagOps.getKey(imei);
        if (pg.getPgLocked() == 0) {
            log.debug("检测到车辆已经解锁,删除key:{}", key);
            redisTemplate.delete(key);
            return;
        }
    }


}
