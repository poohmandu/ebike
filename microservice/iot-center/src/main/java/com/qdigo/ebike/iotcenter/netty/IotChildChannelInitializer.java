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

package com.qdigo.ebike.iotcenter.netty;

import com.qdigo.ebike.iotcenter.config.NettyServerProperties;
import com.qdigo.ebike.iotcenter.handler.GSMDataDecoder;
import com.qdigo.ebike.iotcenter.handler.ParseBytesHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Description: 
 * date: 2020/2/19 10:02 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Component
public class IotChildChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private ParseBytesHandler parseBytesHandler;
    @Resource
    private NettyServerProperties properties;

    @Override
    protected void initChannel(SocketChannel ch) {
        //pipeline管理channel中的Handler，在channel队列中添加一个handler来处理业务
        ByteBuf delimiter_$ = Unpooled.copiedBuffer("$".getBytes());
        DelimiterBasedFrameDecoder decoder = new DelimiterBasedFrameDecoder(1024, delimiter_$);
        //DelimiterBasedFrameDecoder默认会去掉分隔符
        ch.pipeline()
                //.addLast(new FixedLengthFrameDecoder(30))
                .addLast(new IdleStateHandler(properties.getReaderIdle(), 0, 0))
                .addLast("iot-decode", new GSMDataDecoder())
                .addLast("parse-handler", parseBytesHandler);
    }

}
