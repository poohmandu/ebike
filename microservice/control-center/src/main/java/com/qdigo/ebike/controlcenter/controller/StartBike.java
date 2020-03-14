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
import com.qdigo.ebicycle.constants.Status;
import com.qdigo.ebicycle.domain.bike.Bike;
import com.qdigo.ebicycle.domain.bike.BikeStatus;
import com.qdigo.ebicycle.domain.mongo.device.PXPackage;
import com.qdigo.ebicycle.domain.ride.RideOrder;
import com.qdigo.ebicycle.o.ro.BaseResponse;
import com.qdigo.ebicycle.repository.dao.RideRecordDao;
import com.qdigo.ebicycle.service.bike.BikeService;
import com.qdigo.ebicycle.service.cammand.DeviceService;
import com.qdigo.ebicycle.service.device.PXService;
import com.qdigo.ebicycle.web.rest.form.EBikeIdentifyForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;


@RestController
@RequestMapping("/v1.0/ebike")
public class StartBike {

    private final Logger log = LoggerFactory.getLogger(StartBike.class);

    @Inject
    private BikeService BikeService;
    @Inject
    private DeviceService deviceService;
    @Inject
    private RideRecordDao rideRecordDao;
    @Inject
    private PXService pxService;

    /**
     * 上电请求
     *
     * @param mobileNo 手机号
     * @param deviceId 设备号
     * @return ResponseEntity
     */
    @AccessValidate
    @PostMapping(value = "/startBike", produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<BaseResponse> bikeStart(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody EBikeIdentifyForm form) {
        log.debug("user:{},app上电发送过来的imei号为{}", mobileNo, form.getImeiIdOrDeviceId());

        return () -> {

            Bike bike = BikeService.findOneByImeiIdOrDeviceId(form.getImeiIdOrDeviceId());
            if (bike == null) {
                return new BaseResponse(401, "车辆标识格式错误");
            } else if (bike.getBikeId() == null) {
                return new BaseResponse(401, "数据库不存在该车辆");
            }
            String imei = bike.getImeiId();
            RideOrder rideOrder = rideRecordDao.findByRidingBike(bike);
            if (rideOrder == null) {
                log.debug("user:{},bike:{}没有车辆控制权在startBike", mobileNo, imei);
                return new BaseResponse(402, "没有车辆控制权");
            }
            Bike rideBike = rideOrder.getBike();
            if (!rideBike.getImeiId().equals(imei)) {
                log.warn("请求了错误的车辆编号:{}!={}", bike.getImeiId(), rideBike.getImeiId());
                return new BaseResponse(403, "请求了错误的车辆编号:" + bike.getDeviceId());
            }

            BikeStatus status = bike.getBikeStatus();
            if (status.getActualStatus().contains(Status.BikeActualStatus.locationFail.getVal())) {
                deviceService.rebootGPSAsync(imei);
            }

            log.debug("user:{},待发送的send imei号为{}", mobileNo, imei);
            if (!deviceService.fire(imei, mobileNo)) {
                log.debug("user:{}上电失败:{}", mobileNo, imei);
                // 短信上电是一个不稳定操作所以需加判断
                List<PXPackage> pxList = pxService.getFirePxAfter(imei, System.currentTimeMillis() - 5 * 60 * 1000);// 5分钟
                boolean smsOpen;
                log.debug("{},{}用户5分钟内上电PX次数:{}", mobileNo, imei, pxList.size());
                if (pxList.size() <= 6) {
                    smsOpen = false;
                } else {
                    smsOpen = deviceService.smsOpen(imei, mobileNo);
                    log.debug("{},{}用户使用短信上电结果:{}", mobileNo, imei, smsOpen);
                }
                if (!smsOpen) {
                    return new BaseResponse(400, "上电失败");
                }
            }
            log.debug("user:{}上电成功:{}", mobileNo, imei);
            return new BaseResponse(200, "电动车已上电");

        };


    }

}
