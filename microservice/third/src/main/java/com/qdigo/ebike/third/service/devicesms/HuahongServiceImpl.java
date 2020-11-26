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
import com.qdigo.ebike.api.service.third.devicesms.HuahongService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.common.core.util.security.SecurityUtil;
import com.qdigo.ebike.commonconfig.configuration.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * description: 
 *
 * date: 2020/3/13 8:49 PM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HuahongServiceImpl implements HuahongService {

    private final RedisTemplate<String, String> redisTemplate;

    private String getToken(String transactionalId) {
        val str = Const.DeviceSMS.appId + Const.DeviceSMS.pwd + transactionalId;
        return SecurityUtil.getSHA256(str);
    }

    @Override
    public String send(String simNo, String content) {
        val transactionalId = this.getTransactionalId();
        val token = getToken(transactionalId);
        String url = "http://www.iot-cmcc.com/api/v1/sendsms?appid=" + Const.DeviceSMS.appId + "&transid=" + transactionalId + "&token=" + token;
        HttpPost post = new HttpPost(url);
        post.setConfig(Config.REQUEST_CONFIG);
        final Map<String, String> map = ImmutableMap.of("msisdn", simNo, "content", content);
        post.setEntity(new StringEntity(JSON.toJSONString(map), APPLICATION_JSON));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            val entity = response.getEntity();
            val str = EntityUtils.toString(entity);
            val json = JSON.parseObject(str);
            final Integer status = json.getInteger("status");
            if (status != 0) {
                log.debug("发送华虹短信失败:{}", str);
                return null;
            } else {
                log.debug("发送华虹短信成功:{}", str);
                return transactionalId;
            }

        } catch (IOException e) {
            log.error("HttpClient发送过程中出现异常:" + e.getMessage());
            return null;
        }
    }

    @Override
    public Result receive(String simNo) {
        val transactionalId = getTransactionalId();
        val token = getToken(transactionalId);
        HttpUriRequest request = RequestBuilder.post("http://www.iot-cmcc.com/api/v1/recvsms")
                .addParameter("appid", Const.DeviceSMS.appId)
                .addParameter("transid", transactionalId)
                .addParameter("token", token)
                .addParameter("msisdn", simNo)
                .setConfig(Config.REQUEST_CONFIG)
                .build();
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            val entity = response.getEntity();
            val str = EntityUtils.toString(entity);
            log.debug("接受华虹短信响应:{}", str);
            return JSON.parseObject(str, Result.class);
        } catch (IOException e) {
            log.error("Huahong-HttpClient发送过程中出现异常:" + e.getMessage());
            throw new RuntimeException("HttpClient发送过程中出现异常:" + e.getMessage());
        }
    }

    private String getTransactionalId() {
        final DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = Keys.deviceSmsTransactionId.getKey(FormatUtil.getCurDate());

        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().increment(key, 1);
        } else {
            redisTemplate.opsForValue().set(key, "1", 24, TimeUnit.HOURS);
        }

        String num = redisTemplate.opsForValue().get(key);
        log.info("今日的生成的第{}个 transaction_id", num);

        DecimalFormat df = new DecimalFormat("00000000");
        String transactionalId = Const.DeviceSMS.appId + format.format(new Date()) + df.format(Integer.parseInt(num));

        log.info("生成的transaction_id为" + transactionalId);
        return transactionalId;
    }

}
