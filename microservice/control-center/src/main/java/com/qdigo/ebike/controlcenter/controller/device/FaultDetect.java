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

import com.qdigo.ebike.api.domain.dto.bike.BikeGpsStatusDto;
import com.qdigo.ebike.api.service.bike.BikeGpsStatusService;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.controlcenter.domain.dto.EBikeIdentifyForm;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by niezhao on 2016/12/2.
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/ebike")
public class FaultDetect {

    @Resource
    private BikeService bikeService;
    @Resource
    private BikeGpsStatusService gpsStatusService;

    @AccessValidate
    @PostMapping(value = "/faultDetect", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> faultDetect(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody EBikeIdentifyForm form) {
        log.debug("user:{},app发送过来的imei号为{}", mobileNo, form.getImeiIdOrDeviceId());

        val bike = bikeService.findByImeiOrDeviceId(form.getImeiIdOrDeviceId());
        if (bike == null) {
            return R.ok(400, "车辆标识格式错误");
        } else if (bike.getBikeId() == null) {
            return R.ok(401, "数据库不存在该车辆");
        }
        val imei = bike.getImeiId();
        BikeGpsStatusDto gpsStatus = gpsStatusService.findByImei(imei);

        Boolean isOk = gpsStatus.getError() == 0 && gpsStatus.getMachineError() == 0 && gpsStatus.getBrakeError() == 0
                && gpsStatus.getBrakeError() == 0 && gpsStatus.getControlError() == 0;

        if (!isOk) {
            return R.ok(402, "电动车存在故障,请处理");
        }
        return R.ok(200, "未检查到故障");
    }

}
