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

package com.qdigo.ebike.third.service.wxlite;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.third.wxlite.WxliteService;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoSuchTypeException;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonconfig.configuration.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.EnumUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Description: 
 * date: 2019/12/25 4:51 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class WxliteServiceImpl implements WxliteService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public LoginRes getOpenId(String postCode, String appId) {
        Optional<WX> wx = EnumUtils.getEnumList(WX.class)
                .stream().filter(w -> w.getAppId().equals(appId)).findAny();

        if (!wx.isPresent()) {
            throw new NoSuchTypeException("无效的appId" + appId);
        }

        HttpUriRequest request = RequestBuilder.get("https://api.weixin.qq.com/sns/jscode2session")
                .addParameter("appid", wx.get().getAppId())
                .addParameter("secret", wx.get().getAppSecret())
                .addParameter("grant_type", "authorization_code")
                .addParameter("js_code", postCode)
                .setConfig(Config.REQUEST_CONFIG)
                .build();
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String string = EntityUtils.toString(entity);
            log.debug("登录时返回的json字符串为:\n" + FormatUtil.formatJson(string));
            return JSON.parseObject(string, LoginRes.class);
        } catch (IOException e) {
            log.error("WX-HttpClient发送过程中出现异常;{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAccessToken() {
        val key = Keys.wxliteAccessToken.getKey();
        if (redisTemplate.hasKey(key)) {
            return redisTemplate.opsForValue().get(key);
        }
        HttpGet get = new HttpGet(MessageFormat.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}",
                ConfigConstants.wxlite_appId.getConstant(), ConfigConstants.wxlite_appSecret.getConstant()));
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {

            val entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            log.debug("获取的AccessToken为:" + FormatUtil.formatJson(jsonStr));
            JSONObject json = JSON.parseObject(jsonStr);

            val accessToken = json.getString("access_token");
            val expiresIn = json.getIntValue("expires_in");  // seconds
            redisTemplate.opsForValue().set(key, accessToken, expiresIn, TimeUnit.SECONDS);

            return accessToken;
        } catch (IOException e) {
            log.debug("wxlite getAccessToken异常:", e);
            return "";
        }
    }
}
