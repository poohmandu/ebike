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

import com.pingplusplus.model.Charge;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoneMatchException;
import com.qdigo.ebike.common.core.util.http.NetUtil;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.commonaop.annotations.Token;
import com.qdigo.ebike.ordercenter.domain.dto.ChargeBody;
import com.qdigo.ebike.ordercenter.domain.dto.PayBizType;
import com.qdigo.ebike.ordercenter.service.inner.payment.ChargeService;
import com.qdigo.ebike.ordercenter.service.inner.payment.PaymentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1.0/payment")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CreatePayment {

    private final PaymentService paymentService;
    private final ChargeService chargeService;
    private final UserAccountService accountService;
    private final UserService userService;

    /**
     * 获取支付凭据
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param form
     * @return
     * @throws URISyntaxException
     * @author niezhao
     */
    @Token
    @AccessValidate
    @PostMapping(value = "/getCharge", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getPaymentCharge(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body form) throws NoneMatchException {

        val user = userService.findByMobileNo(mobileNo);
        val accountDto = accountService.findByUserId(user.getUserId());
        val clientIp = NetUtil.getRemoteIp();

        log.debug("user:{}获取支付凭证参数信息:{}", mobileNo, form);

        ChargeBody chargeBody = ChargeBody.builder().amount(form.getAmount())
                .channel(form.getChannel()).clientIp(clientIp).userDto(user).userAccountDto(accountDto)
                .openId(form.getOpenId()).payType(form.getPayType()).extra(form.getExtra()).bizType(form.getBizType())
                .build();

        val responseDTO = paymentService.validateCreateCharge(chargeBody);
        if (!responseDTO.isSuccess()) {
            return responseDTO.toResponse();
        }

        Charge charge = chargeService.createCharge(chargeBody);

        return R.ok(200, "支付发起成功", charge);
    }

    @Data
    private static class Body {
        private int amount;//单位:分
        private String channel;
        private int payType; //Integer默认为null，而不是0 // 1:付押金 2:付租金 入账的方式只有两种
        private String openId; //buyer_user_id  用户开放平台ID
        private PayBizType bizType = PayBizType.balance;
        private Map<String, String> extra = new HashMap<>();//不能为null
    }

}
