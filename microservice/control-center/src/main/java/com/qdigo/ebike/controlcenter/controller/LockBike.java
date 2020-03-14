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

package com.qdigo.ebike.controlcenter.controller;

import com.qdigo.ebicycle.aop.token.AccessValidate;
import com.qdigo.ebicycle.domain.bike.Bike;
import com.qdigo.ebicycle.o.ro.BaseResponse;
import com.qdigo.ebicycle.repository.bikeRepo.BikeRepository;
import com.qdigo.ebicycle.service.bike.BikeService;
import com.qdigo.ebicycle.service.cammand.DeviceService;
import com.qdigo.ebicycle.web.rest.form.EBikeIdentifyForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@Slf4j
@RestController
@RequestMapping("/v1.0/ebike")
public class LockBike {

    @Inject
    private BikeRepository bikeRepository;
    @Inject
    private BikeService BikeService;
    @Inject
    private DeviceService deviceService;

    /**
     * 上锁请求
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param form
     * @return
     * @author niezhao
     */
    @AccessValidate
    @PostMapping(value = "/lockBike", produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<?> eBikeLock(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody EBikeIdentifyForm form) {
        log.debug("user:{} app发送过来的上锁请求imei号为{}", mobileNo, form.getImeiIdOrDeviceId());
        return () -> {
            Bike bike = BikeService.findOneByImeiIdOrDeviceId(form.getImeiIdOrDeviceId());
            if (bike == null) {
                return new BaseResponse(400, "车辆标识格式错误");
            } else if (bike.getBikeId() == null) {
                return new BaseResponse(401, "数据库不存在该车辆");
            }
            String imei = bike.getImeiId();
            log.debug("user:{}待发送的send imei号为{}", mobileNo, imei);
            if (!deviceService.lock(imei, mobileNo)) {
                log.debug("user:{}上锁请求失败:{}", mobileNo, imei);
                return new BaseResponse(402, "布防失败");
            }
            log.debug("user:{}上锁请求成功:{}", mobileNo, imei);

            return new BaseResponse(200, "成功完成上锁请求");
        };
    }

}
