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

package com.qdigo.ebike.iotcenter.service.chargePile;


import com.qdigo.ebike.iotcenter.dto.baseStation.md.MDPacketDto;
import com.qdigo.ebike.iotcenter.message.charge.MDManage;
import com.qdigo.ebike.iotcenter.service.impl.ParsePackageServiceImpl;
import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import com.qdigo.ebike.iotcenter.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class ParseMDPackageService extends ParsePackageServiceImpl {
    private Logger logger = LoggerFactory.getLogger(ParseMDPackageService.class);

    @Override
    public MDPacketDto parseUpBytes(byte[] bytes, char header0, char header1, int imei, String client) {
        logger.debug("MD system:" + DateUtil.format(new Date(), DateUtil.YMDHMS_PATTERN));
        int length = bytes.length;
        short voltage = ByteArrayToNumber.byteArrayToShort(bytes, 6);
        byte electric = bytes[8];
        byte status = bytes[9];
        byte chargePortNo = bytes[10];
        byte chargeFail = bytes[11];
        int carIdNo = ByteArrayToNumber.byteArrayToInt(bytes, 12);
        MDPacketDto mdPacketDto = buildMDPacketDto(length, header0, header1, imei, client, voltage, electric, status,
                chargePortNo, chargeFail, carIdNo);
        MDManage mdManage = new MDManage();
        mdManage.saveMDInfo(mdPacketDto);
        mdManage.sendMsg(mdPacketDto);
        return mdPacketDto;
    }


    private MDPacketDto buildMDPacketDto(int length, char header0, char header1, int imei, String client,
                                         short voltage, byte electric, byte status, byte chargePortNo, byte chargeFail, int carIdNo) {
        MDPacketDto mdPacketDto = new MDPacketDto();
        mdPacketDto.setLength(length);
        mdPacketDto.setHeader0(header0);
        mdPacketDto.setHeader1(header1);
        mdPacketDto.setImei(imei);
        mdPacketDto.setClient(client);
        mdPacketDto.setVoltage(voltage);
        mdPacketDto.setElectric(electric);
        mdPacketDto.setStatus(status);
        mdPacketDto.setChargePortNo(chargePortNo);
        mdPacketDto.setChargeFail(chargeFail);
        mdPacketDto.setCarIdNo(carIdNo);
        logger.debug("mdPacketDto[" + mdPacketDto + "]");
        return mdPacketDto;
    }
}
