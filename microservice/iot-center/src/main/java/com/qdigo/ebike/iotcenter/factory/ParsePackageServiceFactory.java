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

package com.qdigo.ebike.iotcenter.factory;


import com.qdigo.ebike.iotcenter.exception.IotServiceBizException;
import com.qdigo.ebike.iotcenter.exception.IotServiceExceptionEnum;
import com.qdigo.ebike.iotcenter.service.api.ParsePackageService;
import com.qdigo.ebike.iotcenter.service.bike.ParsePCPackageService;
import com.qdigo.ebike.iotcenter.service.bike.ParsePGPackageService;
import com.qdigo.ebike.iotcenter.service.bike.ParsePHPackageService;
import com.qdigo.ebike.iotcenter.service.bike.ParsePLPackageService;
import com.qdigo.ebike.iotcenter.service.chargePile.ParseMCPackageService;
import com.qdigo.ebike.iotcenter.service.chargePile.ParseMDPackageService;
import com.qdigo.ebike.iotcenter.service.chargePile.ParseMLPackageService;

import java.text.MessageFormat;

public class ParsePackageServiceFactory {

    public static ParsePackageService getParsePackageService(char header0, char header1) {
        ParsePackageService parsePackageService = null;
        if ('P' == header0 && 'L' == header1) {
            parsePackageService = new ParsePLPackageService();
        } else if ('P' == header0 && 'G' == header1) {
            parsePackageService = new ParsePGPackageService();
        } else if ('P' == header0 && 'H' == header1) {
            parsePackageService = new ParsePHPackageService();
        } else if ('P' == header0 && 'C' == header1) {
            parsePackageService = new ParsePCPackageService();
        } else if ('P' == header0 && 'X' == header1) {
            parsePackageService = new ParsePCPackageService();
        } else if ('M' == header0 && 'D' == header1) {
            parsePackageService = new ParseMDPackageService();
        } else if ('M' == header0 && 'L' == header1) {
            parsePackageService = new ParseMLPackageService();
        } else if ('M' == header0 && 'C' == header1) {
            parsePackageService = new ParseMCPackageService();
        } else if ('M' == header0 && 'X' == header1) {
            parsePackageService = new ParseMCPackageService();
        } else {
            String errorMsg = MessageFormat.format("header0={0},header1={1}", header0, header1);
            throw new IotServiceBizException(IotServiceExceptionEnum.NOT_SUPPORT_SOCKET_DATA.getCode(), IotServiceExceptionEnum.NOT_SUPPORT_SOCKET_DATA.getMsg() + errorMsg);
        }
        return parsePackageService;
    }
}
