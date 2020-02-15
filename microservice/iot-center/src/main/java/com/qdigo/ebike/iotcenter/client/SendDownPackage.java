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


import com.qdigo.ebike.iotcenter.constants.DownCMDResultEnum;
import com.qdigo.ebike.iotcenter.exception.IotServiceBizException;
import com.qdigo.ebike.iotcenter.exception.IotServiceExceptionEnum;
import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import com.qdigo.ebike.iotcenter.util.SocketChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发送下行数据包
 *
 * @author yudengqiu
 */
public class SendDownPackage {

    private static Logger logger = LoggerFactory.getLogger(SendDownPackage.class);

    /**
     * 发送下行心跳响应包
     * 截取上行心跳包 前8个字节
     *
     * @param bytes 上行心跳包
     * @param ctx
     */
    public static void sendPHeartbeat(byte[] bytes, ChannelHandlerContext ctx) {
        char header0 = ByteArrayToNumber.byteToChar(bytes[0]);
        char header1 = ByteArrayToNumber.byteToChar(bytes[1]);
        int imei = ByteArrayToNumber.byteArrayToInt(bytes, 2);
        try {
            logger.info("发送下行心跳响应包");
            byte[] pheart = new byte[bytes.length + 1];
            System.arraycopy(bytes, 0, pheart, 0, bytes.length);
            pheart[pheart.length - 1] = 36; // "$"
            ByteBuf encoded = ctx.alloc().buffer(pheart.length);
            encoded.writeBytes(pheart);
            ctx.writeAndFlush(encoded);
        } catch (Exception e) {
            logger.error("发送下行心跳包异常 header0:" + header0 + ",header1:" + header1 + ",imei:" + imei, e);
            throw new IotServiceBizException(IotServiceExceptionEnum.PH_HEART_ERROR.getCode(), IotServiceExceptionEnum.PH_HEART_ERROR.getMsg());
        }

    }


    /**
     * 发送下行命令响应包
     *
     * @param bytes   下行命令响应包
     * @param imeiKey
     */
    public static void sendCMDResp(byte[] bytes, String imeiKey) {
        char header0 = ByteArrayToNumber.byteToChar(bytes[0]);
        char header1 = ByteArrayToNumber.byteToChar(bytes[1]);
        ChannelHandlerContext upCtx = SocketChannelMap.upConcurrentMap.get(imeiKey);
        try {
            sendCMDResp(bytes, upCtx, DownCMDResultEnum.SUCCESS.getResult()); //0
        } catch (Exception e) {
            sendCMDResp(bytes, upCtx, DownCMDResultEnum.FAIL.getResult()); //1
            logger.error("发送下行命令响应包异常 header0:" + header0 + ",header1:" + header1 + ",imei:" + imeiKey, e);
            throw new IotServiceBizException(IotServiceExceptionEnum.CMD_DOWN_RESP_ERROR.getCode(), IotServiceExceptionEnum.CMD_DOWN_RESP_ERROR.getMsg());
        }
    }

    private static void sendCMDResp(byte[] bytes, ChannelHandlerContext ctx, byte result) {
        logger.info("发送下行命令响应包");
        byte[] params = new byte[10];
        System.arraycopy(bytes, 0, params, 0, 8);
        params[8] = result;
        params[9] = 36; // "$"
        ByteBuf encoded = ctx.alloc().buffer(params.length);
        encoded.writeBytes(params);
        ctx.writeAndFlush(encoded);
    }

    /**
     * 发送下行命令包
     *
     * @param bytes
     * @param imeiKey
     */
    public static void sendDownCmd(byte[] bytes, String imeiKey) {
        char header0 = ByteArrayToNumber.byteToChar(bytes[0]);
        char header1 = ByteArrayToNumber.byteToChar(bytes[1]);
        int imei = ByteArrayToNumber.byteArrayToInt(bytes, 2);
        try {
            if (bytes.length < 9) {
                logger.error("下行命令数据接口格式错误 bytes length={},imeiKey={},byte={}", bytes.length, imeiKey, bytes);
                return;
            }
            ChannelHandlerContext imeiCtx = SocketChannelMap.upConcurrentMap.get(imeiKey);
            bytes[1] = 'C';
            sendDownCMD(bytes, imeiCtx);
        } catch (Exception e) {
            logger.error("发送下行命令异常 header0:" + header0 + ",header1:" + header1 + ",imei:" + imei, e);
            throw new IotServiceBizException(IotServiceExceptionEnum.CMD_DOWN_ERROR.getCode(), IotServiceExceptionEnum.CMD_DOWN_ERROR.getMsg());
        }
    }

    /**
     * 发送下行命令包
     *
     * @param bytes
     * @param ctx   和设备IMEI号 绑定
     */
    private static void sendDownCMD(byte[] bytes, ChannelHandlerContext ctx) {
        char header0 = ByteArrayToNumber.byteToChar(bytes[0]);
        char header1 = ByteArrayToNumber.byteToChar(bytes[1]);
        int imei = ByteArrayToNumber.byteArrayToInt(bytes, 2);
        byte seq = bytes[6];
        byte cmd = bytes[7];
        byte[] params = new byte[bytes.length - 8]; //用于log
        byte[] sendBytes = new byte[bytes.length + 1]; //多一个$
        System.arraycopy(bytes, 8, params, 0, bytes.length - 8);
        System.arraycopy(bytes, 0, sendBytes, 0, bytes.length);
        sendBytes[sendBytes.length - 1] = 36; //$
        String param = ByteArrayToNumber.bytesToString(params);
        logger.info("发送下行命令包 header0=" + header0 + ",header1=" + header1 + ",ime1=" + imei + ",seq=" + seq + ",cmd=" + cmd + ",param=" + param);
        ByteBuf encoded = ctx.alloc().buffer(sendBytes.length);
        encoded.writeBytes(sendBytes);
        ctx.writeAndFlush(encoded);
    }

}
