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

import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.controlcenter.domain.dto.EBikeIdentifyForm;
import com.qdigo.ebike.controlcenter.service.inner.command.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@Slf4j
@RestController
@RequestMapping("/v1.0/ebike")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CloseBike {

    private final BikeService bikeService;
    private final DeviceService deviceService;


    /**
     * 断电请求
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param form
     * @return
     */
    @AccessValidate
    @PostMapping(value = "/closeBike", produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<R> eBikeClose(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody EBikeIdentifyForm form) {
        log.debug("user:{},app断电发送过来的imei号为{}", mobileNo, form.getImeiIdOrDeviceId());
        return () -> {
            BikeDto bikeDto = bikeService.findByImeiOrDeviceId(form.getImeiIdOrDeviceId());
            if (bikeDto == null) {
                return R.ok(400, "车辆标识格式错误");
            } else if (bikeDto.getBikeId() == null) {
                return R.ok(401, "数据库不存在该车辆");
            }
            String imei = bikeDto.getImeiId();
            log.debug("user:{}待发送的send imei号为{}", mobileNo, imei);
            if (!deviceService.shutdown(imei, mobileNo)) {
                log.debug("user:{}断电失败:{}", mobileNo, imei);
                return R.ok(402, "断电失败");
            }
            log.debug("user:{}断电成功:{}", mobileNo, imei);
            return R.ok(200, "电动车已断电");
        };
    }


}
