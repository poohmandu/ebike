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

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 存储socketChannel
 *
 * @author yudengqiu
 */
@Slf4j
public class SocketChannelMap {

    //存储终端上行socket链接关系 (本服务与设备)
    public static ConcurrentMap<String, ChannelHandlerContext> upConcurrentMap = new ConcurrentHashMap<>();

    //存储服务下行socket链接关系 (本服务与web服务)
    public static ConcurrentMap<String, ChannelHandlerContext> downConcurrentMap = new ConcurrentHashMap<>();

    //存储netty连接id 与 imei 的对应关系
    private static ConcurrentMap<String, String> id2ImeiMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, String> imei2IdMap = new ConcurrentHashMap<>();

    public static void put(String id, String imei) {
        id2ImeiMap.put(id, imei);
        String old_id = imei2IdMap.get(imei);
        if (old_id == null) {
            log.info("size:({},{}),id:({})<==>imei:({}) socket发现新连接:old_id为空", id2ImeiMap.size(), imei2IdMap.size(), id, imei);
            imei2IdMap.put(imei, id);
        } else {
            if (!old_id.equals(id)) {
                //老连接可以关闭
                log.info("size:({},{}),id:({})<==>imei:({}) socket发现新连接:old_id与id不一致", id2ImeiMap.size(), imei2IdMap.size(), id, imei);
                imei2IdMap.put(imei, id);
                id2ImeiMap.remove(old_id);
            }
        }
    }

    public static String getImei(String id) {
        return id2ImeiMap.get(id);
    }

}
