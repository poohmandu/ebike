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
import com.qdigo.ebicycle.o.ro.BaseResponse;
import com.qdigo.ebicycle.service.cammand.DeviceService;
import com.qdigo.ebicycle.web.rest.form.EBikeIdentifyForm;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Slf4j
@RestController
@RequestMapping("/v1.0/ebike")
public class FindBike {

    @Inject
    private com.qdigo.ebicycle.service.bike.BikeService BikeService;
    @Inject
    private DeviceService deviceService;


    /**
     * 寻车请求
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param form
     * @return
     */
    @AccessValidate
    @PostMapping(value = "/findBike", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findBike(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody EBikeIdentifyForm form) {
        log.debug("user:{},app发送过来的imei号为{}", mobileNo, form.getImeiIdOrDeviceId());
        val bike = BikeService.findOneByImeiIdOrDeviceId(form.getImeiIdOrDeviceId());
        if (bike == null) {
            return ResponseEntity.ok(new BaseResponse(400, "车辆标识格式错误", null));
        } else if (bike.getBikeId() == null) {
            return ResponseEntity.ok(new BaseResponse(401, "数据库不存在该车辆", null));
        }
        val imei = bike.getImeiId();
        log.debug("user:{}待发送的send imei号为{}", mobileNo, imei);
        if (!deviceService.seekStart(imei, mobileNo)) {
            log.debug("user:{}寻车失败:{}", mobileNo, imei);
            return ResponseEntity.ok().body(new BaseResponse(402, "寻车失败" + imei, null));
        }
        log.debug("user:{}寻车成功:{}", mobileNo, imei);
        return ResponseEntity.ok().body(new BaseResponse(200, "寻车成功" + imei, null));
    }

}
