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

package com.qdigo.ebike.iotcenter.handler;

import com.qdigo.ebike.iotcenter.config.ConfigConst;
import com.qdigo.ebike.iotcenter.dto.other.Connection;
import com.qdigo.ebike.iotcenter.util.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executor;

public class ParseBytesHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(ParseBytesHandler.class);
    private RabbitTemplate rabbit = SpringUtil.getBean(RabbitTemplate.class);
    private final static DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    //自己的业务线程池
    private final static Executor executor = new StandardThreadExecutor();

    //耗时操作如何处理
    //https://www.zhihu.com/question/35487154
    //github: https://github.com/dempeZheng/forest

    /**
     * 原始数据到redis
     *
     * @param bytes 原始数据
     */
    private void persist(byte[] bytes) {
        try {
            int imei = ByteArrayToNumber.byteArrayToInt(bytes, 2);
            final String byteStr = Arrays.toString(bytes);
            final Date date = new Date();
            final String key = "socket:log:" + DateUtil.format(date) + ":" + imei;
            RedisUtil redisUtil = new RedisUtil();
            redisUtil.opsForJedis(jedis -> {
                if (!jedis.exists(key)) {
                    jedis.hset(key, df.format(new Date()), byteStr);
                    jedis.expire(key, 24 * 60 * 60);
                } else {
                    jedis.hset(key, df.format(new Date()), byteStr);
                }
            });
        } catch (Exception e) {
            log.error("原始日志保存在redis失败:{}", e.getMessage());
        }
    }

    private void task(byte[] bytes, ChannelHandlerContext ctx) {
        long start = System.currentTimeMillis();

        this.persist(bytes);//持久化
        ParseByteUtil.iteratorBytes(bytes, ctx);

        long end = System.currentTimeMillis();
        if (end - start > 200) {
            String id = ctx.channel().id().asLongText();
            String imei = SocketChannelMap.getImei(id);
            log.info("id:({})<==>imei:({}) socket完成一次数据解析用时:{}毫秒", id, imei, end - start);
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            long start = System.currentTimeMillis();
            if (msg instanceof byte[]) {
                byte[] bytes = (byte[]) msg;
                executor.execute(() -> {
                    try {
                        this.task(bytes, ctx);
                    } catch (Throwable e) {
                        String id = ctx.channel().id().asLongText();
                        String imei = SocketChannelMap.getImei(id);
                        log.error("id:({})<==>imei:({}) socket数据解析时发生错误{}", id, imei, e);
                    }
                });
            } else {
                log.error("传入的msg类型错误");
            }
            long end = System.currentTimeMillis();
            if (end - start > 200) {
                String id = ctx.channel().id().asLongText();
                String imei = SocketChannelMap.getImei(id);
                log.info("id:({})<==>imei:({}) socket完成一次数据解析用时:{}毫秒", id, imei, end - start);
            }
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        // 将消息发送队列中的消息写入到SocketChannel中发送给对方。
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String id = ctx.channel().id().asLongText();
        String imei = SocketChannelMap.getImei(id);
        String host = ctx.channel().remoteAddress() + "";
        if (!StringUtils.startsWith(host, "/100.116")) {
            log.error("id:({})<==>imei:({}) socket发生错误:", id, imei, cause);
        }
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String id = ctx.channel().id().asLongText();
        String host = ctx.channel().remoteAddress() + "";
        if (!StringUtils.startsWith(host, "/100.116")) {
            log.info("id:({}) (channelActive)socket建立连接:{}", id, ctx.channel().remoteAddress());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String id = ctx.channel().id().asLongText();
        String imei = SocketChannelMap.getImei(id);
        String host = ctx.channel().remoteAddress() + "";
        if (!StringUtils.startsWith(host, "/100.116")) {
            log.info("id:({})<==>imei:({}) (channelInactive)socket断开连接:{}", id, imei, ctx.channel().remoteAddress());
        }
        if (imei != null && !ConfigConst.env.equals("test")) {
            Connection connection = new Connection().setConnected(false)
                    .setImei(imei).setTimestamp(System.currentTimeMillis());
            rabbit.convertAndSend("device.connect", connection);
        }
        super.channelInactive(ctx);
    }
}
