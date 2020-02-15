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
import com.qdigo.ebike.iotcenter.dto.gprs.pg.PGPacketDto;
import com.qdigo.ebike.iotcenter.message.bike.PGManage;
import com.qdigo.ebike.iotcenter.service.impl.ParsePackageServiceImpl;
import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class ParsePGPackageService extends ParsePackageServiceImpl {
    private Logger logger = LoggerFactory.getLogger(ParsePGPackageService.class);


    @Override
    public PGPacketDto parseUpBytes(byte[] bytes, char header0, char header1, int imei, String client) {
        logger.debug("开始解析PG数据包");
        int lng = ByteArrayToNumber.byteArrayToInt(bytes, 6);
        int lat = ByteArrayToNumber.byteArrayToInt(bytes, 10);
        short hight = ByteArrayToNumber.byteArrayToShort(bytes, 14);
        short speed = ByteArrayToNumber.byteArrayToShort(bytes, 16);
        byte status = bytes[18];
        byte star = bytes[19];
        GPRSSubStatus pgSubStatus = buildPGSubStatus(status);
        PGPacketDto pgPacketDto = buildPGPacketDto(header0, header1, imei, client, lng, lat, hight, speed, status, star, pgSubStatus);
        PGManage pgManage = new PGManage();
        pgManage.savePGInfo(pgPacketDto);
        pgManage.sendMsg(pgPacketDto);
        return pgPacketDto;
    }

    //原先的offset为0-1-2-3-4-5-6-7 改为 7-6-5-4-3-2-1-0 ;位移搞反了
    // 解析GPS数据包状态
    private GPRSSubStatus buildPGSubStatus(byte status) {
        GPRSSubStatus pgSubStatus = new GPRSSubStatus();
        //串口通讯 bit0
        byte communicationStatus = ByteArrayToNumber.biteToByte(status, 7);
        //����״̬ bit1
        byte switchStatus = ByteArrayToNumber.biteToByte(status, 6);
        //�Ƿ���bit2
        byte lockStatus = ByteArrayToNumber.biteToByte(status, 5);
        // �Ƿ���bit3
        byte shockStatus = ByteArrayToNumber.biteToByte(status, 4);
        //�Ƿ��ֳ�����bit4
        byte inputStatus = ByteArrayToNumber.biteToByte(status, 3);
        // �Ƿ��Զ���bit5
        byte autoLockStatus = ByteArrayToNumber.biteToByte(status, 2);
        // �Ƿ�� bit6
        byte fallStatus = ByteArrayToNumber.biteToByte(status, 1);
        // �Ƿ����bit7
        byte troubleStatus = ByteArrayToNumber.biteToByte(status, 0);
        pgSubStatus.setCommunicationStatus(communicationStatus);
        pgSubStatus.setSwitchStatus(switchStatus);
        pgSubStatus.setLockStatus(lockStatus);
        pgSubStatus.setShockStatus(shockStatus);
        pgSubStatus.setInputStatus(inputStatus);
        pgSubStatus.setAutoLockStatus(autoLockStatus);
        pgSubStatus.setFallStatus(fallStatus);
        pgSubStatus.setTroubleStatus(troubleStatus);
        logger.debug("pgSubStatus[{}]", pgSubStatus);
        return pgSubStatus;
    }

    private PGPacketDto buildPGPacketDto(char header0, char header1, int imei, String client,
                                         int lng, int lat, short hight, short speed,
                                         byte status, byte star, GPRSSubStatus pgSubStatus) {
        PGPacketDto pgPacketDto = new PGPacketDto();
        pgPacketDto.setHeader0(header0);
        pgPacketDto.setHeader1(header1);
        pgPacketDto.setImei(imei);
        pgPacketDto.setClient(client);
        pgPacketDto.setLng(new BigDecimal(lng / 1000000f).setScale(6, RoundingMode.HALF_UP).floatValue());
        pgPacketDto.setLat(new BigDecimal(lat / 1000000f).setScale(6, RoundingMode.HALF_UP).floatValue());
        pgPacketDto.setHight(hight);
        pgPacketDto.setSpeed(new BigDecimal(speed / 100f).setScale(2, RoundingMode.HALF_UP).floatValue());
        pgPacketDto.setStatus(status);
        pgPacketDto.setStar(star);
        pgPacketDto.setPgSubStatus(pgSubStatus);
        logger.debug("pgPacketDto[" + pgPacketDto + "]");
        return pgPacketDto;
    }
}
