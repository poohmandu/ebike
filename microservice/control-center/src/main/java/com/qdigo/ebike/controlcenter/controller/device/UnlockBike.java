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

import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.controlcenter.domain.dto.EBikeIdentifyForm;
import com.qdigo.ebike.controlcenter.service.inner.command.DeviceService;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PXService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@RestController
@RequestMapping("/v1.0/ebike")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UnlockBike {

    private final BikeStatusService bikeStatusService;
    private final DeviceService deviceService;
    private final OrderRideService rideService;
    private final PXService pxService;

    /**
     * 扫码,发送解锁请求
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param form
     * @return
     * @author niezhao
     */
    @AccessValidate
    @PostMapping(value = "/unlockBike", produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<R> eBikeUnlock(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody EBikeIdentifyForm form) {
        log.debug("开始解锁请求的用户:{},请求的标识号:{}", mobileNo, form.getImeiIdOrDeviceId());

        return () -> {
            RideDto rideDto = rideService.findRidingByMobileNo(mobileNo);

            String imei = rideDto.getImei();
            if (rideDto == null) {
                log.debug("user:{},bike:{}没有车辆控制权在unlockBike", mobileNo, imei);
                return R.ok(402, "没有车辆控制权");
            }

            // 开始解锁过程
            log.debug("user:{}待发送的send imei号为{}", mobileNo, imei);
            if (!deviceService.unLock(imei, mobileNo)) {
                log.debug("user:{}解锁失败:{}", mobileNo, imei);
                return R.ok(404, "撤防失败");
            }
            log.debug("user:{}解锁成功:{}", mobileNo, imei);
            return R.ok(200, "成功完成解锁请求", imei);
        };

    }

}
