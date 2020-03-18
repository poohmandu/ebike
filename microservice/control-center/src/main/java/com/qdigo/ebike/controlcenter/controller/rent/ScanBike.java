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

package com.qdigo.ebike.controlcenter.controller.rent;

import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.commonaop.annotations.Token;
import com.qdigo.ebike.controlcenter.domain.dto.rent.StartDto;
import com.qdigo.ebike.controlcenter.service.inner.rent.start.RentStartService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Slf4j
@RestController
@RequestMapping("/v1.0/ebike")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ScanBike {

    private final RentStartService rentStartService;

    @Token
    @AccessValidate
    @PostMapping(value = "/scanBike", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> bikeScan(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body body) {
        log.debug("扫码请求的用户:{},ble:{}", mobileNo, body.isBle());

        StartDto startInfo = rentStartService.getStartInfo(mobileNo, body.getImeiIdOrDeviceId(),
                body.getLatitude(), body.getLongitude(), body.isBle());

        final ResponseDTO responseDTO = rentStartService.rentValidate(startInfo);

        if (!responseDTO.isSuccess()) {
            return responseDTO.toResponse();
        } else {
            rentStartService.startRent(startInfo);
            return responseDTO.toResponse();
        }

    }


    @Data
    private static class Body {
        private String imeiIdOrDeviceId;
        private double longitude;// 经度
        private double latitude;// 纬度
        private boolean ble = false;
    }

}
