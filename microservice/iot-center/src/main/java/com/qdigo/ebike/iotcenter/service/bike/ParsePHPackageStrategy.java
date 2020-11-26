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
import com.qdigo.ebike.iotcenter.dto.gprs.ph.PHErrorCode;
import com.qdigo.ebike.iotcenter.dto.gprs.ph.PHPacketDto;
import com.qdigo.ebike.iotcenter.service.impl.ParsePackageStrategyImpl;
import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import com.qdigo.ebike.iotcenter.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.qdigo.ebike.iotcenter.service.api.ParsePackageStrategy.PHStratrgy;

@Slf4j
@Service(PHStratrgy)
public class ParsePHPackageStrategy extends ParsePackageStrategyImpl {

    @Override
    public PHPacketDto parseUpBytes(byte[] bytes, char header0, char header1, int imei, String client) {
        log.debug("PH system:" + DateUtil.format(new Date(), DateUtil.YMDHMS_PATTERN));
        byte seq = bytes[6];
        byte status = bytes[7];
        long imsi = ByteArrayToNumber.bytesToLong(bytes, 8);
        if (imsi < 0) {

            imsi = Long.parseLong(Long.toHexString(imsi).substring(1));
        }
        short powerVoltage = ByteArrayToNumber.byteArrayToShort(bytes, 16);
        short batteryVotage = ByteArrayToNumber.byteArrayToShort(bytes, 18);
        byte sensity = bytes[20];
        byte star = bytes[21];
        byte ecode = bytes[22];
        byte soc = bytes[23];
        GPRSSubStatus gprsSubStatus = buildPHSubStatus(status);
        PHErrorCode errorCode = buildPHErrorCode(ecode);
        return buildPHPacketDto(header0, header1, imei, client, seq, status, imsi,
                powerVoltage, batteryVotage, sensity, star, ecode, soc, gprsSubStatus, errorCode);
    }

    //原先的offset为0-1-2-3-4-5-6-7 改为 7-6-5-4-3-2-1-0 ;位移搞反了
    // 解析GPS数据包状态
    private GPRSSubStatus buildPHSubStatus(byte status) {
        GPRSSubStatus pgSubStatus = new GPRSSubStatus();
        //串口通讯 bit0
        byte communicationStatus = ByteArrayToNumber.biteToByte(status, 7);
        //电门锁开关 bit1
        byte switchStatus = ByteArrayToNumber.biteToByte(status, 6);
        //是否锁车 bit2
        byte lockStatus = ByteArrayToNumber.biteToByte(status, 5);
        // 是否有震动  bit3
        byte shockStatus = ByteArrayToNumber.biteToByte(status, 4);
        //是否轮车输入 bit4
        byte inputStatus = ByteArrayToNumber.biteToByte(status, 3);
        // 是否自动 bit5
        byte autoLockStatus = ByteArrayToNumber.biteToByte(status, 2);
        // 是否跌倒  bit6
        byte fallStatus = ByteArrayToNumber.biteToByte(status, 1);
        // 是否故障 bit7
        byte troubleStatus = ByteArrayToNumber.biteToByte(status, 0);
        pgSubStatus.setCommunicationStatus(communicationStatus);
        pgSubStatus.setSwitchStatus(switchStatus);
        pgSubStatus.setLockStatus(lockStatus);
        pgSubStatus.setShockStatus(shockStatus);
        pgSubStatus.setInputStatus(inputStatus);
        pgSubStatus.setAutoLockStatus(autoLockStatus);
        pgSubStatus.setFallStatus(fallStatus);
        pgSubStatus.setTroubleStatus(troubleStatus);
        log.debug("pgSubStatus[" + pgSubStatus + "]");
        return pgSubStatus;
    }

    private PHErrorCode buildPHErrorCode(byte ecode) {
        PHErrorCode errorCode = new PHErrorCode();
        //无电机故障 bit0
        byte phMachineError = ByteArrayToNumber.biteToByte(ecode, 0);
        //刹车故障 bit1
        byte phBrakeErroe = ByteArrayToNumber.biteToByte(ecode, 1);
        //无转把故障 bit2
        byte phHandleBarError = ByteArrayToNumber.biteToByte(ecode, 2);
        // 无控制器故障 bit3
        byte phControlError = ByteArrayToNumber.biteToByte(ecode, 3);

        errorCode.setPhMachineError(phMachineError);
        errorCode.setPhBrakeError(phBrakeErroe);
        errorCode.setPhControlError(phControlError);
        errorCode.setPhHandleBarError(phHandleBarError);
        return errorCode;
    }

    private PHPacketDto buildPHPacketDto(char header0, char header1, int imei, String client,
                                         byte seq, byte status, long imsi, short powerVoltage, short batteryVotage,
                                         byte sensity, byte star, byte ecode, byte soc, GPRSSubStatus gprsSubStatus, PHErrorCode phErrorCode) {
        PHPacketDto phPacketDto = new PHPacketDto();
        phPacketDto.setHeader0(header0);
        phPacketDto.setHeader1(header1);
        phPacketDto.setImei(imei);
        phPacketDto.setClient(client);
        phPacketDto.setSeq(seq);
        phPacketDto.setStatus(status);
        phPacketDto.setImsi(imsi);
        phPacketDto.setPowerVoltage(powerVoltage);
        phPacketDto.setBatteryVotage(batteryVotage);
        phPacketDto.setSensity(sensity);
        phPacketDto.setStar(star);
        phPacketDto.setEcode(ecode);
        phPacketDto.setSoc(soc);
        phPacketDto.setGprsSubStatus(gprsSubStatus);
        phPacketDto.setPhErrorCode(phErrorCode);
        log.debug("phPacketDto[" + phPacketDto + "]");
        return phPacketDto;
    }
}
