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

import com.qdigo.ebike.iotcenter.dto.baseStation.mc.MCPacketDto;
import com.qdigo.ebike.iotcenter.message.charge.MCManage;
import com.qdigo.ebike.iotcenter.service.impl.ParsePackageStrategyImpl;
import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import com.qdigo.ebike.iotcenter.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

import static com.qdigo.ebike.iotcenter.service.api.ParsePackageStrategy.MCStratrgy;

@Slf4j
@Service(MCStratrgy)
public class ParseMCPackageStrategy extends ParsePackageStrategyImpl {

    @Resource
    private MCManage mcManage;

    @Override
    public MCPacketDto parseUpBytes(byte[] bytes, char header0, char header1, int imei, String client) {
        log.debug("MC system:" + DateUtil.format(new Date(), DateUtil.YMDHMS_PATTERN));
        byte seq = bytes[6];
        byte cmd = bytes[7];
        byte[] params = new byte[bytes.length - 8];
        System.arraycopy(bytes, 8, params, 0, bytes.length - 8);
        String param;
        if (cmd < 64) {
            param = ByteArrayToNumber.bytesToString(params);
        } else if (cmd == 66) {
            param = ByteArrayToNumber.bytesToHexString(params, 0, params.length);
        } else {
            //都是一字节
            param = String.valueOf(params[0]);
        }
        MCPacketDto mcPacketDto = buildMCPacketDto(header0, header1, imei, client, seq, cmd, param);
        mcManage.saveUpMCInfo(mcPacketDto);
        mcManage.sendMsg(mcPacketDto);
        return mcPacketDto;
    }

    @Override
    public MCPacketDto parseDownBytes(byte[] bytes, char header0, char header1, int imei, String client) {
        byte seq = bytes[6];
        byte cmd = bytes[7];
        byte[] params = new byte[bytes.length - 8];
        System.arraycopy(bytes, 8, params, 0, bytes.length - 8);
        String param = ByteArrayToNumber.bytesToString(params);
        MCPacketDto mcPacketDto = buildMCPacketDto(header0, header1, imei, client, seq, cmd, param);
        MCManage pcManage = new MCManage();
        pcManage.saveDownMCInfo(mcPacketDto);
        return mcPacketDto;
    }

    private MCPacketDto buildMCPacketDto(char header0, char header1, int imei, String client,
                                         byte seq, byte cmd, String param) {
        MCPacketDto mcPacketDto = new MCPacketDto();
        mcPacketDto.setHeader0(header0);
        mcPacketDto.setHeader1(header1);
        mcPacketDto.setImei(imei);
        mcPacketDto.setClient(client);
        mcPacketDto.setSeq(seq);
        mcPacketDto.setCmd(cmd);
        mcPacketDto.setParam(param);
        log.debug("mcPacketDto[" + mcPacketDto + "]");
        return mcPacketDto;
    }
}
