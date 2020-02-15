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

import com.qdigo.ebike.iotcenter.dto.gprs.pc.PCPacketDto;
import com.qdigo.ebike.iotcenter.message.bike.PCManage;
import com.qdigo.ebike.iotcenter.service.impl.ParsePackageServiceImpl;
import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParsePCPackageService extends ParsePackageServiceImpl {
    private Logger logger = LoggerFactory.getLogger(ParsePCPackageService.class);

    @Override
    public PCPacketDto parseUpBytes(byte[] bytes, char header0, char header1, int imei, String client) {
        byte seq = bytes[6];
        byte cmd = bytes[7];
        byte[] params = new byte[bytes.length - 8];
        System.arraycopy(bytes, 8, params, 0, bytes.length - 8);
        String param = ByteArrayToNumber.bytesToString(params);
        PCPacketDto pcPacketDto = buildPGPacketDto(header0, header1, imei, client, seq, cmd, param);
        PCManage pcManage = new PCManage();
        pcManage.saveUpPCInfo(pcPacketDto);
        pcManage.sendMsg(pcPacketDto);
        return pcPacketDto;
    }

    @Override
    public PCPacketDto parseDownBytes(byte[] bytes, char header0, char header1, int imei, String client) {
        byte seq = bytes[6];
        byte cmd = bytes[7];
        byte[] params = new byte[bytes.length - 8];
        System.arraycopy(bytes, 8, params, 0, bytes.length - 8);
        String param = ByteArrayToNumber.bytesToString(params);
        PCPacketDto pcPacketDto = buildPGPacketDto(header0, header1, imei, client, seq, cmd, param);
        PCManage pcManage = new PCManage();
        pcManage.saveDownPCInfo(pcPacketDto);
        return pcPacketDto;
    }

    private PCPacketDto buildPGPacketDto(char header0, char header1, int imei, String client,
                                         byte seq, byte cmd, String param) {
        PCPacketDto pgPacketDto = new PCPacketDto();
        pgPacketDto.setHeader0(header0);
        pgPacketDto.setHeader1(header1);
        pgPacketDto.setImei(imei);
        pgPacketDto.setClient(client);

        pgPacketDto.setSeq(seq);
        pgPacketDto.setCmd(cmd);
        pgPacketDto.setParam(param);
        logger.debug("pgPacketDto[" + pgPacketDto + "]");
        return pgPacketDto;
    }

}
