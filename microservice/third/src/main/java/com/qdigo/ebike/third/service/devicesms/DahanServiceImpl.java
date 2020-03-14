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

package com.qdigo.ebike.third.service.devicesms;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.third.devicesms.DahanService;
import com.qdigo.ebike.common.core.util.security.SecurityUtil;
import com.qdigo.ebike.commonconfig.configuration.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.inject.Inject;
import java.io.IOException;

/**
 * description: 
 *
 * date: 2020/3/14 10:58 AM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DahanServiceImpl implements DahanService {

    private final static String APP_ID = "Qidi";
    private final static String APP_KEY = "2bf0f0f1a54847d4bd4ff1cec17e41a5";

    private final static String BASE_URL = "http://118.31.48.5:18089";
    private final static String SEND_SMS = "/api/v1/card/operate/sms/send";

    @Override
    public boolean httpSend(Long simNo, String content) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String sha1 = SecurityUtil.getSHA1(SEND_SMS + timestamp + APP_ID + APP_KEY);
        HttpUriRequest request = RequestBuilder.post(BASE_URL + SEND_SMS)
                .setConfig(Config.REQUEST_CONFIG)
                .addHeader("appid", APP_ID)
                .addHeader("sign", sha1)
                .addHeader("timestamp", timestamp)
                .setEntity(new StringEntity(JSON.toJSONString(
                        ImmutableMap.of("numbers", simNo, "content", content)),
                        ContentType.APPLICATION_JSON))
                .build();
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            val entity = response.getEntity();
            val str = EntityUtils.toString(entity);
            val json = JSON.parseObject(str);
            final Integer code = json.getInteger("code");
            if (code != 0) {
                log.debug("发送大汉短信失败:{}", str);
                return false;
            } else {
                log.debug("发送大汉短信成功:{}", str);
                return true;
            }
        } catch (IOException e) {
            log.error("dahan-HttpClient发送过程中出现异常:" + e.getMessage());
            throw new RuntimeException("HttpClient发送过程中出现异常:" + e.getMessage());
        }
    }
}
