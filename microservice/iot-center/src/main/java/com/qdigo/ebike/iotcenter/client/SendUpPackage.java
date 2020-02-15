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

import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import com.qdigo.ebike.iotcenter.util.SocketChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by niezhao on 2017/5/8.
 */
public class SendUpPackage {

    private static Logger logger = LoggerFactory.getLogger(SendUpPackage.class);

    public static void sendTestResp(String imeiKey) {
        //回给服务器
        String response = imeiKey + System.lineSeparator() +
                "success" + System.lineSeparator() +
                "bye" + System.lineSeparator();
        byte[] bytesToWeb = response.getBytes();
        ChannelHandlerContext downCtx = SocketChannelMap.downConcurrentMap.get(imeiKey);
        ByteBuf encoded = downCtx.alloc().buffer(bytesToWeb.length);
        encoded.writeBytes(bytesToWeb);
        downCtx.writeAndFlush(encoded);
    }

    public static void sendServerResp(byte[] bytes, String imeiKey) {

        char header0 = ByteArrayToNumber.byteToChar(bytes[0]);//如果为充电桩 默认成功
        //区分设备主动上报还是响应
        byte cmd = bytes[7];
        byte[] params = new byte[bytes.length - 8];
        System.arraycopy(bytes, 8, params, 0, bytes.length - 8);
        String param = ByteArrayToNumber.bytesToString(params);
        if (cmd < 64) {
            //回给服务器
            StringBuilder response = new StringBuilder();
            response.append(imeiKey).append(System.lineSeparator());
            if ("1".equals(param) || header0 == 'M') {
                response.append("success").append(System.lineSeparator());
            } else {
                response.append("fail").append(System.lineSeparator());
            }
            response.append("bye").append(System.lineSeparator());
            logger.info("{}收到硬件响应,准备向webServer发送socket响应:{}", imeiKey, response);
            byte[] bytesToWeb = response.toString().getBytes();
            ChannelHandlerContext downCtx = SocketChannelMap.downConcurrentMap.get(imeiKey);
            ByteBuf encoded = downCtx.alloc().buffer(bytesToWeb.length);
            encoded.writeBytes(bytesToWeb);
            downCtx.writeAndFlush(encoded);
        }
    }

}
