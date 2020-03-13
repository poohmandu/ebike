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
import com.qdigo.ebike.api.domain.dto.third.wx.wxpush.*;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonconfig.configuration.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * Created by niezhao on 2017/9/18.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WxlitePush {

    private final RedisTemplate<String, String> redisTemplate;
    private final WxliteServiceImpl wxliteService;
    private final UserService userService;

    public void send(String mobileNo, String title, Const.PushType pushType, Object data) {
        if (pushType == Const.PushType.autoReturn) {
            String deviceId = (String) ((Map) data).get("deviceId");
            Double consume = (Double) ((Map) data).get("consume");
            ReturnNotice returnNotice = ReturnNotice.builder().content(title).deviceId(deviceId)
                    .consume("¥ " + String.valueOf(consume)).build();
            this.send(mobileNo, returnNotice, "consume");
        } else if (pushType == Const.PushType.stuAuth) {
            Map<String, Object> map = (Map<String, Object>) data;
            StudentAuthNotice authNotice = StudentAuthNotice.builder().schoolName((String) map.get("schoolName"))
                    .studentNo((String) map.get("studentNo"))
                    .time(FormatUtil.yMdHms.format((Date) map.get("time")))
                    .build();
            if ((boolean) map.get("success")) {
                authNotice.setResult("认证通过");
                authNotice.setFailMsg("无");
            } else {
                authNotice.setResult("审核不通过");
                authNotice.setFailMsg((String) map.get("failMsg"));
            }
            this.send(mobileNo, authNotice, null);
        } else if (pushType == Const.PushType.consumeWarn) {
            DriveNotice notice = (DriveNotice) data;
            this.send(mobileNo, notice, null);
        } else if (pushType == Const.PushType.inviteFinished) {
            InviteResult inviteResult = (InviteResult) data;
            this.send(mobileNo, inviteResult, null);
        }
    }

    private void send(String mobileNo, PushTemp pushTemp, String emphasis) {
        UserDto userDto = userService.findByMobileNo(mobileNo);
        val openId = userDto.getWxliteOpenId();
        String accessToken = wxliteService.getAccessToken();
        String sendResult;
        if (accessToken.isEmpty()) {
            sendResult = "获取accessToken失败";
        } else {
            val formId = this.getFormId(mobileNo);
            if (formId.isEmpty()) {
                sendResult = "获取formId失败";
            } else {
                String json = pushTemp.buildJson(openId, formId, emphasis);
                Integer errcode = this.httpRequest(accessToken, json);
                sendResult = "状态码" + errcode;
                if (errcode != null && (errcode == 40001 || errcode == 40003)) {
                    redisTemplate.delete(Keys.wxliteAccessToken.getKey());
                    accessToken = wxliteService.getAccessToken();
                    errcode = this.httpRequest(accessToken, json);
                    sendResult = "重新获取accessToken后状态码" + errcode;
                }
            }
        }
        log.debug("{}微信小程序模版推送的结果:{},推送内容:{}", mobileNo, sendResult, pushTemp);
    }

    public String getFormId(String mobileNo) {
        val key = Keys.wxliteFormId.getKey(mobileNo);
        ListOperations<String, String> forList = redisTemplate.opsForList();
        if (!redisTemplate.hasKey(key)) {
            return "";
        }
        Long size = forList.size(key);
        for (int i = 0; i < size; i++) {
            String right = forList.rightPop(key);
            if (StringUtils.contains(right, ":")) {
                long timestamp = Long.parseLong(StringUtils.substringBefore(right, ":"));
                long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - timestamp);
                if (days < 7) {
                    return StringUtils.substringAfter(right, ":");
                }
            }
        }
        return "";

    }

    public void saveFormId(String mobileNo, String formId) {
        val key = Keys.wxliteFormId.getKey(mobileNo);
        redisTemplate.opsForList().leftPush(key, System.currentTimeMillis() + ":" + formId);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    private Integer httpRequest(String accessToken, String json) {

        HttpUriRequest request = RequestBuilder.post("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send")
                .addParameter("access_token", accessToken)
                .setEntity(new StringEntity(json, APPLICATION_JSON))
                .setConfig(Config.REQUEST_CONFIG)
                .build();

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {

            val entity = response.getEntity();
            val body = EntityUtils.toString(entity);
            log.debug("微信小程序模版推送:{}", FormatUtil.formatJson(body));

            return JSON.parseObject(body).getInteger("errcode");

        } catch (Exception e) {
            log.debug("发送小程序模版异常:", e);
            return null;
        }
    }


}
