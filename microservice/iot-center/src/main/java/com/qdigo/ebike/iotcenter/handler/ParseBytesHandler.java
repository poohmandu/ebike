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

import com.qdigo.ebike.common.core.util.SpringContextHolder;
import com.qdigo.ebike.iotcenter.dto.other.Connection;
import com.qdigo.ebike.iotcenter.service.ByteHandlerService;
import com.qdigo.ebike.iotcenter.util.SocketChannelMap;
import com.qdigo.ebike.iotcenter.util.StandardThreadExecutor;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.Executor;

@Slf4j
@Component
@Sharable
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ParseBytesHandler extends ChannelInboundHandlerAdapter {

    private final RabbitTemplate rabbit;
    private final ByteHandlerService byteHandlerService;

    //自己的业务线程池
    private final static Executor executor = new StandardThreadExecutor();

    //耗时操作如何处理
    //https://www.zhihu.com/question/35487154
    //github: https://github.com/dempeZheng/forest

    /**
     * @author niezhao
     *
     * @description iot-center的主线程
     *
     * @date 2020/2/19 10:11 PM
     * @param msg
     * @param ctx
     * @return void
     *
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof byte[]) {
                byte[] bytes = (byte[]) msg;
                executor.execute(() -> byteHandlerService.parse(bytes, ctx));
            } else {
                log.error("传入的msg类型错误");
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
        if (imei != null && SpringContextHolder.isProd()) {
            Connection connection = Connection.builder().connected(false)
                    .imei(imei).timestamp(System.currentTimeMillis()).build();
            rabbit.convertAndSend("device.connect", connection);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //超时事件
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if (idleEvent.state() == IdleState.READER_IDLE) { //读
                ctx.channel().close();
                String imei = SocketChannelMap.getImei(ctx.channel().id().asLongText());
                log.debug("{}设备断线", imei);
            } else if (idleEvent.state() == IdleState.WRITER_IDLE) {//写
                ctx.channel().close();
            } else if (idleEvent.state() == IdleState.ALL_IDLE) {//全部
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
