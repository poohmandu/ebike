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

package com.qdigo.ebike.third.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.third.zfblite.ZfbliteService;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * Description: 
 * date: 2019/12/26 4:18 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ZfbliteServiceImpl implements ZfbliteService {

    //线程安全的
    private final static AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
            ConfigConstants.alipayliteAppId.getConstant(), ConfigConstants.alipaylitePrivateKey.getConstant(), "json",
            AlipayConstants.CHARSET_UTF8, ConfigConstants.alipaylitePublicKey.getConstant(), AlipayConstants.SIGN_TYPE_RSA2);

    @Override
    public LoginRes getOpenId(String postCode) {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code"); //值为authorization_code时，代表用code换取；为refresh_token时，代表用refresh_token换取
        request.setCode(postCode);
        //request.setRefreshToken("201208134b203fe6c11548bcabd8da5bb087a83b");

        try {
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            return new LoginRes().setAccessToken(response.getAccessToken()).setAlipayUserId(response.getUserId())
                    .setExpiresIn(response.getExpiresIn()).setReExpiresIn(response.getReExpiresIn())
                    .setRefreshToken(response.getRefreshToken()).setUserId(response.getUserId());
        } catch (AlipayApiException e) {
            log.error("支付宝小程序调用失败:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
