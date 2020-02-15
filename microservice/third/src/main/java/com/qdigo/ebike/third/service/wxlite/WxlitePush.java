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
import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonconfig.configuration.Config;
import lombok.Data;
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
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
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
            ReturnNotice notice = new ReturnNotice();
            notice.setContent(title);
            notice.setDeviceId(deviceId);
            notice.setConsume("¥ " + String.valueOf(consume));
            this.send(mobileNo, notice, "consume");
        } else if (pushType == Const.PushType.stuAuth) {
            Map<String, Object> map = (Map<String, Object>) data;
            StudentAuthNotice authNotice = new StudentAuthNotice();
            authNotice.setSchoolName((String) map.get("schoolName"));
            authNotice.setStudentNo((String) map.get("studentNo"));
            authNotice.setTime(FormatUtil.yMdHms.format((Date) map.get("time")));
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

    public static abstract class PushTemp {
        public String buildJson(String openId, String formId, String emphasis) {
            Class<? extends PushTemp> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();
            Map<String, Object> map = new HashMap<>();
            map.put("touser", openId);
            map.put("form_id", formId);
            Map<String, Object> data = new HashMap<>();
            int count = 0;
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(this);
                    if ("tempId".equals(field.getName())) {
                        map.put("template_id", value);
                    } else if ("page".equals(field.getName())) {
                        map.put("page", value);
                    } else {
                        data.put("keyword" + (++count), ImmutableMap.of("value", value));
                    }
                    if (emphasis != null && emphasis.equals(field.getName())) {
                        map.put("emphasis_keyword", "keyword" + count + ".DATA");
                    }
                } catch (IllegalAccessException e) {
                    log.error("错误:", e);
                }
            }
            map.put("data", data);
            map.putIfAbsent("page", "pages/map/map");
            return JSON.toJSONString(map);
        }
    }

    @Data // 还车提醒
    public final static class ReturnNotice extends PushTemp {
        private static final String tempId = "2ygeypXs6cvJakbWBhMT9tY2CD0PCEt2BIodKcbG0kk";
        private String consume; //消费金额
        private String deviceId; //车辆编号
        private String content; //提醒原因
    }

    @Data // 认证结果通知
    public final static class StudentAuthNotice extends PushTemp {
        private static final String tempId = "zQftjE6n7T3oJF6jD3ukqefzA2t_qu3W-SnSQ3jay94";
        private String result; //认证结果
        private String studentNo; //学号
        private String time; //认证时间
        private String schoolName; //学校名称
        private String failMsg; //备注
    }

    @Data // 骑行提醒
    public final static class DriveNotice extends PushTemp {
        private static final String tempId = "Wj1gjCAqM6S8CZWfpq9gd5YKDzrxCRMU-Jc3R47C3NE";
        private String deviceId; //车辆编号
        private String startTime; //开始时间
        private String reason; //提醒原因
    }

    @Data // 邀请结果提醒
    public final static class InviteResult extends PushTemp {
        private static final String tempId = "Tqx8DSgQKzfOwfebZbrqzb_V-BxkjIBcYKlELoPta7U";
        private String reward; //奖励
        private String invitee; //受邀者
        private String inviter; //邀请人
        private String validDate; //有效期
        private String note; //备注
    }


}
