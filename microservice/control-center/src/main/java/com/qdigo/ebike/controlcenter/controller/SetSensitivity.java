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
import com.qdigo.ebicycle.service.bike.BikeService;
import com.qdigo.ebicycle.service.cammand.DeviceService;
import lombok.Data;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 * Created by niezhao on 2016/12/8.
 */
@RestController
@RequestMapping("/v1.0/ebike")
public class SetSensitivity {

    private final Logger log = LoggerFactory.getLogger(SetSensitivity.class);

    @Inject
    private BikeService BikeService;
    @Inject
    private DeviceService deviceService;

    @AccessValidate
    @PostMapping(value = "/setSensitivity", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setSensitivity(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body form) {

        log.debug("user:{}app发送过来的imei号为{}", mobileNo, form.getImeiIdOrDeviceId());
        val bike = BikeService.findOneByImeiIdOrDeviceId(form.getImeiIdOrDeviceId());

        if (bike == null) {
            return ResponseEntity.ok(new BaseResponse(400, "车辆标识格式错误", null));
        } else if (bike.getBikeId() == null) {
            return ResponseEntity.ok(new BaseResponse(401, "数据库不存在该车辆", null));
        }

        val imei = bike.getImeiId();
        log.debug("user:{}待发送的send imei号为{}", mobileNo, imei);
        if (!deviceService.setSensitivity(imei, form.getGrade(), mobileNo)) {
            log.debug("user:{}设置灵敏度失败:{}", mobileNo, imei);
            return ResponseEntity.ok().body(new BaseResponse(400, "设置灵敏度失败", null));
        }
        log.debug("user:{}设置灵敏度成功:{}", mobileNo, imei);
        return ResponseEntity.ok().body(new BaseResponse(200, "成功设置灵敏度", form.getGrade()));
    }

    @Data
    private static class Body {
        private String imeiIdOrDeviceId;
        private int grade;
    }

}
