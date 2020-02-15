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

package com.qdigo.ebike.iotcenter.service.impl;


import com.qdigo.ebike.iotcenter.dto.DatagramPacketBasicDto;
import com.qdigo.ebike.iotcenter.service.api.ParsePackageService;

public class ParsePackageServiceImpl implements ParsePackageService {


    public DatagramPacketBasicDto parseUpBytes(byte[] bytes, char header0, char header1, int imei, String client) {
        return null;
    }

    public DatagramPacketBasicDto parseDownBytes(byte[] bytes, char header0, char header1, int imei, String client) {
        return null;
    }

}

