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

package com.qdigo.ebike.iotcenter.service.bike;


import com.qdigo.ebike.iotcenter.dto.gprs.GPRSSubStatus;
import com.qdigo.ebike.iotcenter.dto.gprs.pl.PLPacketDto;
import com.qdigo.ebike.iotcenter.service.impl.ParsePackageStrategyImpl;
import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import com.qdigo.ebike.iotcenter.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.qdigo.ebike.iotcenter.service.api.ParsePackageStrategy.PLStratrgy;

@Slf4j
@Service(PLStratrgy)
public class ParsePLPackageStrategy extends ParsePackageStrategyImpl {

    @Override
    public PLPacketDto parseUpBytes(byte[] bytes, char header0, char header1, int imei, String client) {
        log.debug("PL system:" + DateUtil.format(new Date(), DateUtil.YMDHMS_PATTERN));
        int lac = ByteArrayToNumber.byteArrayToInt(bytes, 6);
        int cellid = ByteArrayToNumber.byteArrayToInt(bytes, 10);
        short signal = ByteArrayToNumber.byteArrayToShort(bytes, 14);
        byte status = bytes[16];
        GPRSSubStatus pgSubStatus = buildPLSubStatus(status);

        return buildPLPacketDto(header0, header1, imei, client, lac, cellid, signal, status, pgSubStatus);
    }

    //原先的offset为0-1-2-3-4-5-6-7 改为 7-6-5-4-3-2-1-0 ;位移搞反了
    private GPRSSubStatus buildPLSubStatus(byte status) {
        GPRSSubStatus plSubStatus = new GPRSSubStatus();
        // bit0
        byte communicationStatus = ByteArrayToNumber.biteToByte(status, 7);
        // bit1
        byte switchStatus = ByteArrayToNumber.biteToByte(status, 6);
        //bit2
        byte lockStatus = ByteArrayToNumber.biteToByte(status, 5);
        //bit3
        byte shockStatus = ByteArrayToNumber.biteToByte(status, 4);
        //bit4
        byte inputStatus = ByteArrayToNumber.biteToByte(status, 3);
        //bit5
        byte autoLockStatus = ByteArrayToNumber.biteToByte(status, 2);
        //bit6
        byte fallStatus = ByteArrayToNumber.biteToByte(status, 1);
        //bit7
        byte troubleStatus = ByteArrayToNumber.biteToByte(status, 0);
        plSubStatus.setCommunicationStatus(communicationStatus);
        plSubStatus.setSwitchStatus(switchStatus);
        plSubStatus.setLockStatus(lockStatus);
        plSubStatus.setShockStatus(shockStatus);
        plSubStatus.setInputStatus(inputStatus);
        plSubStatus.setAutoLockStatus(autoLockStatus);
        plSubStatus.setFallStatus(fallStatus);
        plSubStatus.setTroubleStatus(troubleStatus);
        log.debug("plSubStatus[" + plSubStatus + "]");
        return plSubStatus;
    }

    private PLPacketDto buildPLPacketDto(char header0, char header1, int imei, String client,
                                         int lac, int cellid, short signal, byte status, GPRSSubStatus pgSubStatus) {
        PLPacketDto plPacketDto = new PLPacketDto();
        plPacketDto.setHeader0(header0);
        plPacketDto.setHeader1(header1);
        plPacketDto.setImei(imei);
        plPacketDto.setClient(client);
        plPacketDto.setLac(lac);
        plPacketDto.setCellid(cellid);
        plPacketDto.setSignal(signal);
        plPacketDto.setStatus(status);
        plPacketDto.setPgSubStatus(pgSubStatus);
        log.debug("plPacketDto[" + plPacketDto + "]");
        return plPacketDto;
    }

}
