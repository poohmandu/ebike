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

package com.qdigo.ebike.iotcenter.service.api;


import com.qdigo.ebike.iotcenter.dto.DatagramPacketBasicDto;

/**
 * 解析数据包
 *
 * @author yudengqiu
 * @since 2016-11-12
 */
public interface ParsePackageStrategy {
    // 解析上行数据包
    DatagramPacketBasicDto parseUpBytes(byte[] bytes, char header0, char header1, int imei, String client);

    //解析下行数据包
    DatagramPacketBasicDto parseDownBytes(byte[] bytes, char header0, char header1, int imei, String client);

    String PGStratrgy = "PGStratrgy";
    String PHStratrgy = "PHStratrgy";
    String PLStratrgy = "PLStratrgy";
    String PCStratrgy = "PCStratrgy";
    String MDStratrgy = "MDStratrgy";
    String MLStratrgy = "MLStratrgyv";
    String MCStratrgy = "MCStratrgy";
}
