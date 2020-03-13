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

package com.qdigo.ebike.third.service.push.wxpush;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.http.NetUtil;
import com.qdigo.ebike.commonconfig.configuration.Config;
import com.qdigo.ebike.third.domain.dto.WsMessage;
import com.qdigo.ebike.third.service.wxlite.WxlitePush;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2017/4/6.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WebSocketService extends TextWebSocketHandler {

    private static ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final RedisTemplate<String, String> redisTemplate;
    private final WxlitePush wxlitePush;
    @Resource
    private WebSocketHandler webSocketHandler;


    @Value("${server.port}")
    private String port;
    private final String ip = NetUtil.getIp();

    private String getLocalHost() {
        return ip + ":" + port;
    }

    private String getHost(String mobileNo) {
        val key = Keys.webSocketSessionAddress.getKey(mobileNo);
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String mobileNo = this.getMobileNo(session);
        if (mobileNo != null) {
            this.setSession(mobileNo, session);
        }
        log.debug("服务端感知webSocket建立连接,mobileNo:{},size:{}", mobileNo, sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String mobileNo = this.getMobileNo(session);
        log.debug("服务端感知webSocket连接关闭:{},mobileNo:{}", status, mobileNo);
        if (mobileNo != null) {
            this.removeSession(mobileNo);
        }
    }

    private String getMobileNo(WebSocketSession session) {
        List<String> list = session.getHandshakeHeaders().get("mobileNo");
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    private void setSession(String mobileNo, WebSocketSession session) {
        val key = Keys.webSocketSessionAddress.getKey(mobileNo);
        val host = this.getLocalHost();
        redisTemplate.opsForValue().set(key, host, Const.redisKeyExpireDays, TimeUnit.DAYS);
        sessions.put(mobileNo, session);
    }

    private void removeSession(String mobileNo) {
        val key = Keys.webSocketSessionAddress.getKey(mobileNo);
        val localHost = this.getLocalHost();
        val host = this.getHost(mobileNo);
        if (localHost.equals(host)) {
            redisTemplate.delete(key);
        }
        sessions.remove(mobileNo);
    }

    private WebSocketSession getLocalSession(String mobileNo) {
        return sessions.get(mobileNo);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        StopWatch watch = new StopWatch("handleMessage");
        watch.start("json");

        String mobileNo = this.getMobileNo(session);
        if (mobileNo == null || mobileNo.isEmpty()) {
            return;
        }
        this.setSession(mobileNo, session);
        WsMessage.Request request = JSON.parseObject(message.getPayload(), WsMessage.Request.class);
        watch.stop();
        watch.start("handle");
        if (request.getRequestType() == WsMessage.RequestType.heartBeat) {
            WsMessage.Message heartBeatMsg = new WsMessage.Message();
            heartBeatMsg.setMobileNo(mobileNo);
            heartBeatMsg.setTimestamp(System.currentTimeMillis());
            heartBeatMsg.setMessageType(WsMessage.MessageType.heartBeat);
            this.sendMessage(heartBeatMsg);

            //发送滞留消息
            this.sendStrandPushMessage(mobileNo);
            //心跳处理
            webSocketHandler.onHeartBeat(mobileNo, request.getTimestamp());
        } else if (request.getRequestType() == WsMessage.RequestType.biz) {
            //消息处理
            webSocketHandler.onMessage(mobileNo, request.getBizType(), request.getData(), request.getTimestamp());
        }
        watch.stop();
        if (watch.getTotalTimeMillis() > 50) {
            Map<String, Object> contextMap = ImmutableMap.of("duration", watch.getTotalTimeMillis(), "uri", "handleWebSocket");
            log.debug("{}用户处理消息时间过长,类型为:{},监控为:{}", mobileNo, request.getRequestType(), watch.toString(), contextMap);
        }

    }

    /**
     * 发送websocket消息,如果本地发送失败返回false，
     * 否则调用redis保存的最后机器尝试发送
     *
     * @param message
     * @return
     */
    public boolean sendMessage(WsMessage.Message message) {
        String mobileNo = message.getMobileNo();
        WsMessage.MessageType type = message.getMessageType();
        WsMessage.BizType bizType = message.getResponseType();
        WebSocketSession session = this.getLocalSession(mobileNo);
        val jsonStr = JSON.toJSONString(message);
        if (session != null && session.isOpen()) {
            try {
                if (type == WsMessage.MessageType.push || (type == WsMessage.MessageType.response && bizType == WsMessage.BizType.ridingTrack)) {
                    log.debug("用户{}准备发送消息为{},会话打开", mobileNo, jsonStr);
                }
                session.sendMessage(new TextMessage(jsonStr));
                return true;
            } catch (IOException e) {
                log.error("webSocket发送消息异常:{}", e.getMessage());
            }
        }
        this.removeSession(mobileNo);
        // 如果在本机器host则已经被删除
        String host = this.getHost(mobileNo);
        if (StringUtils.isEmpty(host)) {
            if (type == WsMessage.MessageType.push || (type == WsMessage.MessageType.response && bizType == WsMessage.BizType.ridingTrack)) {
                log.debug("用户{}准备发送消息为{},会话关闭:empty", mobileNo, jsonStr);
            }
            return false;
        }
        // 说明不在本机器,尝试其他机器发送
        if (host.equals(this.getLocalHost())) {
            // 防止无限循环
            log.debug("用户{}准备发送消息为{},会话关闭:local eq host", mobileNo, jsonStr);
            return false;
        }
        val http = MessageFormat.format("http://{0}/v1.0/operation/webSocket/sendMessage", host);
        HttpUriRequest request = RequestBuilder.post(http)
                .setEntity(new StringEntity(jsonStr, ContentType.APPLICATION_JSON))
                .setConfig(Config.REQUEST_CONFIG)
                .build();
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            log.debug("{},websocket消息发送http接口返回:{}", host, response.getStatusLine());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
        } catch (IOException e) {
            log.error("HttpClient发送过程中出现异常;{}", e.getMessage());
        }
        return false;
    }

    //调用时保证该主机session 处于open状态
    public boolean sendStrandPushMessage(String mobileNo) {
        val key = Keys.webSocketMessageQueue.getKey(mobileNo);
        if (redisTemplate.hasKey(key)) {
            val jsonStr = redisTemplate.opsForValue().get(key);
            boolean send = this.sendMessage(JSON.parseObject(jsonStr, WsMessage.Message.class));
            redisTemplate.delete(key);
            return send;
        } else {
            return false;
        }
    }

    public void pushMessage(String mobileNo, String title, Const.PushType pushType, Object data) {

        WsMessage.PushMessage pushMessage = new WsMessage.PushMessage();
        pushMessage.setPushType(pushType);
        pushMessage.setTitle(title);
        pushMessage.setData(data);
        WsMessage.Message message = pushMessage.toMessage(mobileNo);

        boolean send = this.sendMessage(message);
        if (send) {
            return;
        }

        //最多滞留20分钟,再次期间有心跳依旧会收到
        val jsonStr = JSON.toJSONString(message);
        val key = Keys.webSocketMessageQueue.getKey(mobileNo);
        redisTemplate.opsForValue().set(key, jsonStr, 20, TimeUnit.MINUTES);

        wxlitePush.send(mobileNo, title, pushType, data);

    }


}
