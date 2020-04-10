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

package com.qdigo.ebike.ordercenter.controller.payment;

import com.pingplusplus.exception.*;
import com.pingplusplus.model.Charge;
import com.qdigo.ebicycle.aop.token.AccessValidate;
import com.qdigo.ebicycle.o.ro.BaseResponse;
import com.qdigo.ebicycle.repository.orderRepo.OrderChargeRepository;
import com.qdigo.ebicycle.service.pay.webHooks.ChargeSucceed;
import com.qdigo.ebicycle.web.errors.exception.runtime.NoneMatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 * Created by niezhao on 2017/10/9.
 */
@RestController
@RequestMapping("/v1.0/payment")
@Slf4j
public class PayCallbackSync {

    @Inject
    private ChargeSucceed chargeSucceed;
    @Inject
    private OrderChargeRepository chargeRepository;

    @AccessValidate
    @Transactional
    @PostMapping(value = "/retrieve/{orderNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> retrieveCharge(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @PathVariable String orderNo) {

        return chargeRepository.findByOrderNo(orderNo).map(orderCharge -> {
            if (orderCharge.isPaid()) {
                log.debug("同步的方式支付回调时发现已经回调成功orderCharge:{}", orderCharge);
                return ResponseEntity.ok(new BaseResponse(201, "已回调成功"));
            } else {
                try {
                    Charge charge = Charge.retrieve(orderCharge.getChargeId());
                    log.debug("同步的方式进行支付回调charge:{}", charge);
                    chargeSucceed.chargeSucceed(charge);
                    return ResponseEntity.ok(new BaseResponse(201, "已回调成功"));
                } catch (AuthenticationException | InvalidRequestException | APIConnectionException | APIException | ChannelException | RateLimitException e) {
                    log.debug("ping++接口出现异常:", e);
                    return ResponseEntity.ok(new BaseResponse(401, "ping++接口出现异常"));
                } catch (NoneMatchException e) {
                    log.debug("ping++接口出现业务异常:", e);
                    return ResponseEntity.ok(new BaseResponse(401, e.getMessage()));
                }
            }
        }).orElseGet(() -> {
            log.debug("不存在该订单号:{}", orderNo);
            return ResponseEntity.ok(new BaseResponse(400, "不存在该订单号"));
        });
    }

}
