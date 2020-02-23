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


import com.qdigo.ebike.iotcenter.dto.baseStation.ml.MLPacketDto;
import com.qdigo.ebike.iotcenter.service.impl.ParsePackageStrategyImpl;
import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import com.qdigo.ebike.iotcenter.util.DateUtil;
import com.qdigo.ebike.iotcenter.util.GetUnsigned;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.qdigo.ebike.iotcenter.service.api.ParsePackageStrategy.MLStratrgy;

@Slf4j
@Service(MLStratrgy)
public class ParseMLPackageStrategy extends ParsePackageStrategyImpl {

    @Override
    public MLPacketDto parseUpBytes(byte[] bytes, char header0, char header1, int imei, String client) {
        log.debug("ML system:" + DateUtil.format(new Date(), DateUtil.YMDHMS_PATTERN));
        int length = bytes.length;
        short lac = ByteArrayToNumber.byteArrayToShort(bytes, 6);
        //excel 协议里定的都是无符号数
        int cellid = GetUnsigned.getUnsignedByte(ByteArrayToNumber.byteArrayToShort(bytes, 8));
        byte signal = bytes[10];
        byte temperature = bytes[11];
        //Long imsi = ByteArrayToNumber.bytesToLong(bytes, 12);
        //末尾的0去掉
        String imsi = ByteArrayToNumber.bytesToStringByHalfByte(bytes, 12, 8);
        imsi = imsi.substring(0, imsi.length() - 1);

        return buildMLPacketDto(length, header0, header1, imei, client,
                lac, cellid, signal, temperature, Long.valueOf(imsi));
    }


    private MLPacketDto buildMLPacketDto(int length, char header0, char header1, int imei, String client,
                                         short lac, int cellid, byte signal, byte temperature, Long imsi) {
        MLPacketDto mlPacketDto = new MLPacketDto();
        mlPacketDto.setLength(length);
        mlPacketDto.setHeader0(header0);
        mlPacketDto.setHeader1(header1);
        mlPacketDto.setImei(imei);
        mlPacketDto.setClient(client);

        mlPacketDto.setLac(lac);
        mlPacketDto.setCellid(cellid);
        mlPacketDto.setSignal(signal);
        mlPacketDto.setTemperature(temperature);
        mlPacketDto.setImsi(imsi);
        log.debug("mlPacketDto[" + mlPacketDto + "]");
        return mlPacketDto;
    }
}
