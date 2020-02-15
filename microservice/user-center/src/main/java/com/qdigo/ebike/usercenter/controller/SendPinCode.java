/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.usercenter.controller;

import com.qdigo.ebike.api.service.third.sms.SmsService;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.R;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 * Description:
 * date: 2019/12/10 4:08 PM
 *
 * @author niezhao
 * @since JDK 1.8
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/user")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SendPinCode {

    private final RedisTemplate<String, String> redisTemplate;
    private final SmsService smsService;

    @GetMapping(value = "/getPinCode", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> sendPinCodeSMS(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId) {
        String key = Keys.PinCode.getKey(mobileNo);
        if (redisTemplate.hasKey(key)) {
            log.debug("用户:{}验证码重复", mobileNo);
            return R.failed(400, "请勿重复点击获取验证码", redisTemplate.opsForValue().get(key));
        }
        log.debug("获取用户{}的短信验证码", mobileNo);
        String pinCode = smsService.sendPinCodeSMS(mobileNo, "86");
        return R.ok(200, "The pinCode " + pinCode + " was sent to your mobile via SMS!", pinCode);
    }

    @PostMapping(value = "/getInterPinCode", produces = MediaType.APPLICATION_JSON_VALUE)
    public R sendInterPinCode(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId,
            @RequestBody Body body) {

        String key = Keys.PinCode.getKey(mobileNo);

        if (redisTemplate.hasKey(key)) {
            log.debug("用户:{}验证码重复", mobileNo);
            return R.failed(400, "请勿重复点击获取验证码", redisTemplate.opsForValue().get(key));
        }
        log.debug("获取用户{}的短信验证码", mobileNo);
        String pinCode = smsService.sendPinCodeSMS(mobileNo, body.getCountryCode());

        return R.ok(200, "The pinCode " + pinCode + " was sent to your mobile via SMS!", pinCode);

    }

    @Data
    private static class Body {
        private String countryCode;
    }

}


