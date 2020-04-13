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

package com.qdigo.ebike.ordercenter.controller.webhooks;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.EncryptResource;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxCallbackResponse;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxEvent;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.service.third.wxlite.WxscoreService;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.api.service.user.UserWxService;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoneMatchException;
import com.qdigo.ebike.commonaop.annotations.Token;
import com.qdigo.ebike.ordercenter.domain.entity.wxscore.OrderWxscore;
import com.qdigo.ebike.ordercenter.repository.OrderWxscoreRepository;
import com.qdigo.ebike.ordercenter.service.inner.wxscore.WxscoreDaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@Controller
@RequestMapping("/v1.0/payment/wxPay")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WxScoreCallback {

    private final WxscoreService wxscoreService;
    private final UserWxService userWxService;
    private final UserAccountService accountService;
    private final OrderWxscoreRepository orderWxscoreRepository;
    private final WxscoreDaoService wxscoreDaoService;


    /**
     * 开启成功通知
     * UNAVAILABLE  /  AVAILABLE
     *
     * @param event
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @Token(key = "id")
    @PostMapping("/openService")
    public ResponseEntity<WxCallbackResponse> openService(@RequestBody WxEvent event) throws GeneralSecurityException, IOException, NoneMatchException {
        MDC.put("mobileNo", event.getId());

        String decryptToStr = decryptToStr(event.getResource());
        log.debug("先享后付开启服务回调解密后数据为:{}", decryptToStr);
        JSONObject json = JSON.parseObject(decryptToStr);
        String openid = json.getString("openid");
        String appid = json.getString("appid");
        String serviceStatus = json.getString("user_service_status");// USER_OPEN_SERVICE  USER_CLOSE_SERVICE

        UserWxService.WxOpenInfoDto userWxOpenInfo = userWxService.findByWxId(appid, openid, null);
        if (userWxOpenInfo == null) {
            return ResponseEntity.badRequest().body(new WxCallbackResponse("SYSTEM_ERR", "找不到对应用户"));
        }

        UserAccountDto account = accountService.findByUserId(userWxOpenInfo.getUserId());
        if ("USER_OPEN_SERVICE".equals(serviceStatus)) {
            account.setWxscore("AVAILABLE");
            accountService.update(account);
        }
        if ("USER_CLOSE_SERVICE".equals(serviceStatus)) {
            account.setWxscore("UNAVAILABLE");
            accountService.update(account);
        }

        return ResponseEntity.ok(new WxCallbackResponse("1000", "成功处理通知"));

    }

    @Token(key = "id")
    @PostMapping("/checkOrder")
    public ResponseEntity<WxCallbackResponse> checkOrder(@RequestBody WxEvent event) throws GeneralSecurityException, IOException {
        MDC.put("mobileNo", event.getId());

        String decryptToStr = decryptToStr(event.getResource());
        log.debug("先享后付确认订单解密后数据为:{}", decryptToStr);
        WxscoreOrder wxscoreOrder = JSON.parseObject(decryptToStr, WxscoreOrder.class);
        // 创建订单时一定要保存
        OrderWxscore one = orderWxscoreRepository.findById(wxscoreOrder.getOut_order_no()).orElse(null);
        if (one == null) {
            try {
                wxscoreDaoService.createOrder(wxscoreOrder);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(new WxCallbackResponse("DB_ERROR", "创建微信支付分订单失败"));
            }
        }
        return ResponseEntity.ok(new WxCallbackResponse("1000", "成功处理通知"));
    }


    @Token(key = "id")
    @PostMapping("/payOrder")
    public ResponseEntity<WxCallbackResponse> payOrder(@RequestBody WxEvent event) throws GeneralSecurityException, IOException {
        MDC.put("mobileNo", event.getId());
        String decryptToStr = decryptToStr(event.getResource());

        log.debug("先享后付完结订单解密后数据为:{}", decryptToStr);
        WxscoreOrder wxscoreOrder = JSON.parseObject(decryptToStr, WxscoreOrder.class);
        wxscoreDaoService.finishOrder(wxscoreOrder);

        return ResponseEntity.ok(new WxCallbackResponse("1000", "成功处理通知"));
    }


    private String decryptToStr(EncryptResource resource) throws GeneralSecurityException, IOException {
        return wxscoreService.decryptAes256ToString(resource.getAssociated_data(), resource.getCiphertext(), resource.getNonce());
    }

}
