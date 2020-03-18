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

package com.qdigo.ebike.controlcenter.controller.device;

import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.controlcenter.domain.dto.EBikeIdentifyForm;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PXPackage;
import com.qdigo.ebike.controlcenter.service.inner.command.DeviceService;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PXService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RestController
@RequestMapping("/v1.0/ebike")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class StartBike {

    private final BikeStatusService bikeStatusService;
    private final DeviceService deviceService;
    private final OrderRideService rideService;
    private final PXService pxService;

    /**
     * 上电请求
     *
     * @param mobileNo 手机号
     * @param deviceId 设备号
     * @return ResponseEntity
     */
    @AccessValidate
    @PostMapping(value = "/startBike", produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<R> bikeStart(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody EBikeIdentifyForm form) {
        log.debug("user:{},app上电发送过来的imei号为{}", mobileNo, form.getImeiIdOrDeviceId());

        return () -> {
            RideDto rideDto = rideService.findRidingByMobileNo(mobileNo);

            String imei = rideDto.getImei();
            if (rideDto == null) {
                log.debug("user:{},bike:{}没有车辆控制权在startBike", mobileNo, imei);
                return R.ok(402, "没有车辆控制权");
            }
            BikeStatusDto statusDto = bikeStatusService.findByImei(imei);

            if (statusDto.getActualStatus().contains(Status.BikeActualStatus.locationFail.getVal())) {
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
                    return R.ok(400, "上电失败");
                }
            }
            log.debug("user:{}上电成功:{}", mobileNo, imei);
            return R.ok(200, "电动车已上电");
        };


    }

}
