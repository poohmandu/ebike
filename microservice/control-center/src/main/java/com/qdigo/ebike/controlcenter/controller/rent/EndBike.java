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
import com.qdigo.ebike.common.core.errors.exception.QdigoBizException;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.commonaop.annotations.Token;
import com.qdigo.ebike.controlcenter.domain.dto.rent.EndDTO;
import com.qdigo.ebike.controlcenter.domain.dto.rent.EndResponse;
import com.qdigo.ebike.controlcenter.service.inner.rent.end.RentEndService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Slf4j
@RestController
@RequestMapping("/v1.0/ebike")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EndBike {

    private final RentEndService rentEndService;

    //在一个 @Transactional 事务里,多次save,
    //实际上只有一个commit动作

    /**
     * 还车请求,生成账单
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @return
     * @author niezhao
     */
    @Token
    //@RetryOnOptimistic
    @AccessValidate
    //@Transactional(rollbackFor = Throwable.class)
    @PostMapping(value = "/endBike", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> eBikeEnd(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Param param) {
        val forceEnd = param.isForceEnd();
        val ble = param.isBle();
        val accuracy = param.getAccuracy();
        log.debug("{}用户准备还车,ble:{},accuracy:{},provider:{},forceEnd={}", mobileNo, param.ble, param.accuracy, param.provider, forceEnd);

        ResponseDTO<EndDTO> resDTO = rentEndService.appEndValidate(mobileNo, param.imeiIdOrDeviceId,
                param.longitude, param.latitude, accuracy, param.provider, ble, forceEnd);

        if (resDTO.isSuccess()) {
            EndDTO endDTO = resDTO.getData();

            EndResponse response;
            try {
                response = rentEndService.endRideRecord(endDTO, false);
            } catch (QdigoBizException e) {
                // 406 微信支付分完结订单异常
                log.debug("微信支付分完结订单异常:{}", e.getMessage());
                return e.toResponse();
            }

            return R.ok(200, "成功还车,此次骑行花费" + response.getOrderAmount() + "元", response);
        } else if (resDTO.getStatusCode() == 405) {
            EndDTO endDTO = resDTO.getData();
            EndDTO.EndOrderTipDTO orderTipDTO = endDTO.getOut().getOrderTipDTO();

            return R.ok(resDTO.getStatusCode(), resDTO.getMessage(), orderTipDTO);
        } else {
            // 400(rideRecord=null) 401(atStation)
            // 403(pgNotFound) 404(forceEnd)
            // 405(account) 406(wxscore)
            resDTO.setData(null);
            return resDTO.toResponse();
        }
    }

    @Token
    @AccessValidate
    @PostMapping(value = "/bleEndValidate", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> bleEndValidate(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Param param) {
        log.debug("{}用户蓝牙还车前置条件判断,ble:{},accuracy:{},provider:{},forceEnd={}", mobileNo, param.ble, param.accuracy, param.provider, param.forceEnd);

        ResponseDTO<EndDTO> resDTO = rentEndService.appEndValidate(mobileNo, param.imeiIdOrDeviceId,
                param.longitude, param.latitude, param.accuracy, param.provider, param.ble, param.forceEnd);

        if (resDTO.isSuccess()) {
            return ResponseEntity.ok(new BaseResponse(200, "还车前条件检验成功"));
        }
        if (resDTO.getStatusCode() == 405) {
            EndDTO endDTO = resDTO.getData();
            EndDTO.EndOrderTipDTO orderTipDTO = endDTO.getOrderTipDTO();

            return ResponseEntity.ok(new BaseResponse(resDTO.getStatusCode(), resDTO.getMessage(), orderTipDTO));
        }
        resDTO.setData(null);
        return resDTO.toResponse();
    }

    @Data
    private static class Param {
        private String imeiIdOrDeviceId;
        private double longitude;// 经度
        private double latitude;// 纬度
        private double accuracy = -1; //精度
        private String provider = "";
        private boolean ble = false;
        private boolean forceEnd = false;
    }

}
