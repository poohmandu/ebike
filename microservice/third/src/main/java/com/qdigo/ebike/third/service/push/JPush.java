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

package com.qdigo.ebike.third.service.push;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.report.ReceivedsResult;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Const;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class JPush {

    /**
     * 推送给Android和ios,以通知的形式 (电滴出行的)
     *
     * @param aliases
     * @param alert
     * @param pushType
     * @param jsonStr
     * @return
     */
    public static PushPayload buildPushPayload(Collection<String> aliases, String alert, Const.PushType pushType, String jsonStr) {
        return PushPayload.newBuilder()
            .setPlatform(Platform.all())
            .setAudience(Audience.alias(aliases))
            .setNotification(Notification.newBuilder()
                .addPlatformNotification(IosNotification.newBuilder()
                    .setAlert(alert)
                    .setBadge(1)//ios图标上的气泡,不要自动加1,而是一直为1
                    .setSound("happy")
                    .addExtra("pushType", pushType.name())
                    .addExtra("data", jsonStr)
                    .build())
                .addPlatformNotification(AndroidNotification.newBuilder()
                    .setTitle("电滴出行")//可自定义 alert = "电滴出行"
                    .setAlert(alert)
                    .addExtra("pushType", pushType.name())
                    .addExtra("data", jsonStr)
                    .build())
                .build())
            .setOptions(Options.newBuilder()
                .setApnsProduction(true) //ios 线上环境
                .setTimeToLive(86400 * 9) // 推送当前用户不在线时，为该用户保留多长时间的离线消息(9天)
                .build())
            .build();
    }


    public static Optional<PushResult> pushNotation(String alias, String alert, Const.PushType pushType, Object data) {
        return pushNotations(Lists.newArrayList(alias), alert, pushType, data, Const.AppType.qdigo);
    }

    public static Optional<PushResult> pushNotations(Collection<String> aliases, String alert, Const.PushType pushType, Object data) {
        return pushNotations(aliases, alert, pushType, data, Const.AppType.qdigo);
    }

    public static Optional<PushResult> opsPushNotation(String alias, String alert, Const.PushType pushType, Object data) {
        return pushNotations(Lists.newArrayList(alias), alert, pushType, data, Const.AppType.ops);
    }

    public static Optional<PushResult> opsPushNotations(Collection<String> aliases, String alert, Const.PushType pushType, Object data) {
        return pushNotations(aliases, alert, pushType, data, Const.AppType.ops);
    }

    public static Optional<PushResult> pushNotations(Collection<String> aliases, String alert, Const.PushType pushType, Object data, Const.AppType appType) {
        JPushClient client;
        if (appType == Const.AppType.qdigo) {
            client = new JPushClient(ConfigConstants.masterSecret.getConstant(), ConfigConstants.appKey.getConstant());
        } else if (appType == Const.AppType.ops) {
            client = new JPushClient(ConfigConstants.ops_jpush_secret.getConstant(), ConfigConstants.ops_jpush_appkey.getConstant());
        } else {
            throw new RuntimeException("未知appType");
        }
        String jsonStr = JSON.toJSONString(data);
        PushResult result;
        PushPayload payload = buildPushPayload(aliases, alert, pushType, jsonStr);
        try {
            result = client.sendPush(payload);
            if (result != null && result.isResultOK()) {
                return Optional.of(result);
            }
        } catch (APIConnectionException | APIRequestException e) {
            log.error("极光推送发生异常:{}", e.getMessage());
        }
        return Optional.empty();
    }

    public static PushReport received(Iterable<Long> msg_ids, Const.AppType appType) {
        JPushClient client;
        if (appType == Const.AppType.qdigo) {
            client = new JPushClient(ConfigConstants.masterSecret.getConstant(), ConfigConstants.appKey.getConstant());
        } else if (appType == Const.AppType.ops) {
            client = new JPushClient(ConfigConstants.ops_jpush_secret.getConstant(), ConfigConstants.ops_jpush_appkey.getConstant());
        } else {
            throw new RuntimeException("未知appType");
        }
        //msd_id 最多100个
        final String msgIds = StringUtils.join(msg_ids, ',');
        final PushReport report = new PushReport();
        try {
            final ReceivedsResult result = client.getReportReceiveds(msgIds);
            if (!result.isResultOK()) {
                report.setOk(false);
                return report;
            } else {
                final List<ReceivedsResult.Received> received_list = result.received_list;
                Map<Long, Boolean> map = Maps.newHashMap();
                received_list.forEach(received -> {
                    if (received.android_received == 0 && received.ios_msg_received == 0) {
                        map.put(received.msg_id, false);
                    } else {
                        map.put(received.msg_id, true);
                    }
                });
                report.setOk(true);
                report.setResults(map);
                return report;
            }
        } catch (APIConnectionException | APIRequestException e) {
            log.error("极光推送发生异常:{}", e);
            report.setOk(false);
            return report;
        }
    }

    public static PushReport receivedQdigo(long msg_id) {
        return received(Lists.newArrayList(msg_id), Const.AppType.qdigo);
    }

    @Data
    public static class PushReport {
        private boolean isOk;
        private Map<Long, Boolean> results;
    }


}



