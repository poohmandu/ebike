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

package com.qdigo.ebike.iotcenter.client;

import com.qdigo.ebike.iotcenter.dto.mongo.PCPackage;
import com.qdigo.ebike.iotcenter.util.BuildUtil;
import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import com.qdigo.ebike.iotcenter.util.SocketChannelMap;
import com.qdigo.ebike.iotcenter.util.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 需要经过web后台判断再回传给设备的命令
 * <p>
 * Created by niezhao on 2017/6/24.
 */
public class SendSpecialPackage {

    private static Logger logger = LoggerFactory.getLogger(SendDownPackage.class);

    private static String result(byte[] bytes) {
        RabbitTemplate rabbitTemplate = SpringUtil.getBean(RabbitTemplate.class);
        final PCPackage pcPackage = BuildUtil.buildPC(bytes);
        return (String) rabbitTemplate.convertSendAndReceive("pc", "up.pc.special", pcPackage);
    }

    public static void specialPCBusiness(byte[] bytes) {
        // 8位
        String imei = String.valueOf(ByteArrayToNumber.byteArrayToInt(bytes, 2));
        byte cmd = bytes[7];
        final ChannelHandlerContext ctx = SocketChannelMap.upConcurrentMap.get(imei);
        final String result = result(bytes);
        final char[] chars = result.toCharArray();
        logger.info("imei:{}发出特殊命令{},结果为:{}", imei, cmd, result);
        byte[] send = new byte[8 + chars.length + 1];
        System.arraycopy(bytes, 0, send, 0, 8);
        for (int i = 0; i < chars.length; i++) {
            send[8 + i] = (byte) chars[i];
        }
        send[send.length - 1] = '$';
        ByteBuf encoded = ctx.alloc().buffer(send.length);
        encoded.writeBytes(send);
        ctx.writeAndFlush(encoded);
    }


}
