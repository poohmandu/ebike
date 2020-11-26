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

package com.qdigo.ebike.third.service.insurance.picc.util;

import java.util.UUID;

/**
 * 产生的UUID有这么长,通过截取和替换"-"后得到不同长度的UUID
 * 056085ce-8e46-492a-bcec-9a4d3690ce83
 * 8 + 4 + 4 + 4 + 12
 *
 * @author jiangchen
 * @date 2018/03/07
 */
public class UUIDUtil {

    /**
     * 获得16个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get16UUID() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[2];
    }

    /**
     * 获得20个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get20UUID() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[2] + idd[3];
    }

    /**
     * 获得24个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get24UUID() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[4];
    }

    /**
     * 获得32个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get32UUID() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[2] + idd[3] + idd[4];
    }

}
