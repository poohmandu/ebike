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
import com.qdigo.ebicycle.repository.bikeRepo.BikeRepository;
import com.qdigo.ebicycle.service.bike.BikeService;
import com.qdigo.ebicycle.web.rest.form.EBikeIdentifyForm;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 * Created by niezhao on 2016/12/2.
 */
@RestController
@RequestMapping("/v1.0/ebike")
public class FaultDetect {

    private final Logger log = LoggerFactory.getLogger(FaultDetect.class);

    @Inject
    private BikeService BikeService;
    @Inject
    private BikeRepository bikeRepository;

    @AccessValidate
    @PostMapping(value = "/faultDetect", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> faultDetect(
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
        val gpsStatus = bike.getGpsStatus();

        Boolean isOk = gpsStatus.getError() == 0 && gpsStatus.getMachineError() == 0 && gpsStatus.getBrakeError() == 0
            && gpsStatus.getBrakeError() == 0 && gpsStatus.getControlError() == 0;

        if (!isOk) {
            return ResponseEntity.ok().body(new BaseResponse(402, "电动车存在故障,请处理", null));
        }
        return ResponseEntity.ok().body(new BaseResponse(200, "未检查到故障", null));

    }

}
