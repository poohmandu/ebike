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
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.third.devicesms.YouyunService;
import com.qdigo.ebike.commonconfig.configuration.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.inject.Inject;
import java.io.IOException;

/**
 * description: 
 *
 * date: 2020/3/14 11:47 AM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class YouyunServiceImpl implements YouyunService {

    private final static String ACCESS_TOKEN = "54466be70fcdf5293a37429bfcaf5d670f14d3a7";
    private final static String TEMP_ON = "0013";
    private final static String TEMP_OFF = "0014";
    private final static String TEMP_IMEI = "0015";
    private final static String TEMP_LOC = "0016";
    private final static String BASE_URL = "https://console.ucloudy.cn/doc/api/sms";

    @Override
    public boolean httpSend(Long simNo, String temp, String params) {
        HttpUriRequest request = RequestBuilder.get(BASE_URL)
                .setConfig(Config.REQUEST_CONFIG)
                .addParameter("accessToken", ACCESS_TOKEN)
                .addParameter("numbers", String.valueOf(simNo))
                .addParameter("temp", temp)
                .addParameter("params", params)
                .addParameter("encode", "1")
                .build();
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            val entity = response.getEntity();
            val str = EntityUtils.toString(entity);
            val json = JSON.parseObject(str);
            final Integer status = json.getInteger("status");
            if (status != 0) {
                log.debug("发送佑云短信失败:{}", str);
                return false;
            } else {
                log.debug("发送佑云短信成功:{}", str);
                return true;
            }
        } catch (IOException e) {
            log.warn("Youyun-HttpClient发送过程中出现异常:" + e.getMessage());
            return false;
            //throw new RuntimeException("HttpClient发送过程中出现异常:" + e.getMessage());
        }
    }


}
