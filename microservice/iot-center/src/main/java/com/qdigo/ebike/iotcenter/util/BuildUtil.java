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

package com.qdigo.ebike.iotcenter.util;

import com.qdigo.ebike.api.domain.dto.iot.datagram.PCPackage;

/**
 * Created by niezhao on 2017/6/24.
 */
public class BuildUtil {

    public static PCPackage buildPC(byte[] bytes) {

        int imei = ByteArrayToNumber.byteArrayToInt(bytes, 2);
        byte seq = bytes[6];
        byte cmd = bytes[7];
        byte[] params = new byte[bytes.length - 8];
        System.arraycopy(bytes, 8, params, 0, bytes.length - 8);
        String param = ByteArrayToNumber.bytesToString(params);

        return new PCPackage().setPcCmd(cmd)
                .setPcImei(String.valueOf(imei))
                .setPcParam(param)
                .setPcSequence(seq)
                .setTimestamp(System.currentTimeMillis());
    }

}
