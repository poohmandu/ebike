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

package com.qdigo.ebike.iotcenter.util;


import com.qdigo.ebike.iotcenter.client.SendDownPackage;
import com.qdigo.ebike.iotcenter.client.SendSpecialPackage;
import com.qdigo.ebike.iotcenter.client.SendUpPackage;
import com.qdigo.ebike.iotcenter.config.ConfigConst;
import com.qdigo.ebike.iotcenter.factory.ParsePackageServiceFactory;
import com.qdigo.ebike.iotcenter.service.api.ParsePackageService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseByteUtil {

    private static Logger logger = LoggerFactory.getLogger(ParseByteUtil.class);


    public static void iteratorBytes(byte[] bytes, ChannelHandlerContext ctx) {
        char header0 = ByteArrayToNumber.byteToChar(bytes[0]);
        char header1 = ByteArrayToNumber.byteToChar(bytes[1]);
        int imei = ByteArrayToNumber.byteArrayToInt(bytes, 2);
        String imeiKey = imei + "";
        final String client = ctx.channel().remoteAddress() + "";

        logger.info("header0:" + header0 + ",header1:" + header1 + ",imei:" + imei);

        // PX MX包为自定义下行包
        if ('X' == header1) {
            // 保存与服务端socket链接关系
            SocketChannelMap.downConcurrentMap.put(imeiKey, ctx);
            //发送下行命令包
            SendDownPackage.sendDownCmd(bytes, imeiKey);
            if (ConfigConst.env.equals("test")) {
                SendUpPackage.sendTestResp(imeiKey);
            }
        } else {
            // 保存设备id与imei的关系
            SocketChannelMap.put(ctx.channel().id().asLongText(), imeiKey);
            // 保存与设备socket链接关系

            ChannelHandlerContext old_ctx = SocketChannelMap.upConcurrentMap.get(imeiKey);
            if (old_ctx != null && !old_ctx.channel().id().equals(ctx.channel().id())) {
                logger.info("{}设备的连接切换,由{}变为{}", imeiKey, old_ctx.channel().id(), ctx.channel().id());
            }

            SocketChannelMap.upConcurrentMap.put(imeiKey, ctx);
        }

        //心跳响应
        if ('P' == header0 && 'H' == header1) {
            //发送下行心跳包
            SendDownPackage.sendPHeartbeat(bytes, ctx);
        }

        //命令包 MC  PC
        if ('C' == header1) {
            //区分设备主动上报还是响应
            byte cmd = bytes[7];
            if (cmd < 64) {
                SendUpPackage.sendServerResp(bytes, imeiKey);
            } else if (cmd == 70) {
                //需要服务端判断的命令
                SendSpecialPackage.specialPCBusiness(bytes);
            } else {
                //回给设备/
                SendDownPackage.sendCMDResp(bytes, imeiKey);
            }
        }

        //数据上行
        ParsePackageService parsePackageService = ParsePackageServiceFactory.getParsePackageService(header0, header1);
        //来自设备
        if (parsePackageService != null && 'X' != header1) {
            parsePackageService.parseUpBytes(bytes, header0, header1, imei, client);
        }

        //来自服务端
        if (parsePackageService != null && 'X' == header1) {
            parsePackageService.parseDownBytes(bytes, header0, header1, imei, client);
        }
    }


}
