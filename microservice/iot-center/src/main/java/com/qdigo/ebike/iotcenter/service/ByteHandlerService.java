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

package com.qdigo.ebike.iotcenter.service;

import com.qdigo.ebike.common.core.util.SpringContextHolder;
import com.qdigo.ebike.iotcenter.client.SendDownPackage;
import com.qdigo.ebike.iotcenter.client.SendSpecialPackage;
import com.qdigo.ebike.iotcenter.client.SendUpPackage;
import com.qdigo.ebike.iotcenter.dto.DatagramPacketBasicDto;
import com.qdigo.ebike.iotcenter.factory.PackageManageServiceContext;
import com.qdigo.ebike.iotcenter.factory.ParsePackageServiceContext;
import com.qdigo.ebike.iotcenter.service.api.PackageManageStrateyg;
import com.qdigo.ebike.iotcenter.service.api.ParsePackageStrategy;
import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import com.qdigo.ebike.iotcenter.util.SocketChannelMap;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Description: iot-center的主线程
 * date: 2020/2/20 4:28 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@Service
public class ByteHandlerService {
    @Resource
    private ParsePackageServiceContext parsePackageServiceContext;
    @Resource
    private PackageManageServiceContext packageManageServiceContext;

    @SuppressWarnings("unchecked")
    public void parse(byte[] bytes, ChannelHandlerContext ctx) {
        char header0 = ByteArrayToNumber.byteToChar(bytes[0]);
        char header1 = ByteArrayToNumber.byteToChar(bytes[1]);
        int imei = ByteArrayToNumber.byteArrayToInt(bytes, 2);
        String imeiKey = imei + "";
        final String client = ctx.channel().remoteAddress() + "";

        log.info("header0:" + header0 + ",header1:" + header1 + ",imei:" + imei);

        // PX MX包为自定义下行包
        if ('X' == header1) {
            // 保存与服务端socket链接关系
            SocketChannelMap.downConcurrentMap.put(imeiKey, ctx);
            //发送下行命令包
            SendDownPackage.sendDownCmd(bytes, imeiKey);
            if (SpringContextHolder.testEnv("test")) {
                SendUpPackage.sendTestResp(imeiKey);
            }
        } else {
            // 保存与设备socket链接关系
            SocketChannelMap.upConcurrentMap.put(imeiKey, ctx);
            // 保存设备id与imei的关系
            SocketChannelMap.put(ctx.channel().id().asLongText(), imeiKey);

            ChannelHandlerContext old_ctx = SocketChannelMap.upConcurrentMap.get(imeiKey);
            if (old_ctx != null && !old_ctx.channel().id().equals(ctx.channel().id())) {
                log.info("{}设备的连接切换,由{}变为{}", imeiKey, old_ctx.channel().id(), ctx.channel().id());
            }

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
        ParsePackageStrategy parsePackageStrategy = parsePackageServiceContext.getParsePackageStrategy(header0, header1);
        PackageManageStrateyg manageStrateyg = packageManageServiceContext.getParsePackageStrategy(header0, header1);
        //来自设备
        if (parsePackageStrategy == null) {
            return;
        }
        DatagramPacketBasicDto packetDto;
        if ('X' != header1) {
            packetDto = parsePackageStrategy.parseUpBytes(bytes, header0, header1, imei, client);
        } else {
            //来自服务端
            packetDto = parsePackageStrategy.parseDownBytes(bytes, header0, header1, imei, client);
        }
        try {
            manageStrateyg.saveInfo(packetDto);
            manageStrateyg.sendMsg(packetDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
