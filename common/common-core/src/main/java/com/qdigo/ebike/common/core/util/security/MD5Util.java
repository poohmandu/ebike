/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.common.core.util.security;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Description:
 * date: 2019/12/11 5:07 PM
 *
 * @author niezhao
 * @since JDK 1.8
 */
@Slf4j
public class MD5Util {
    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_LOWER = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String computeMD5(String input) {
        if (input == null) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        byte[] digestBytes = digest.digest();

        String returnString = new String(encodeHex(digestBytes));

        String inputToLog = input;
        if (inputToLog.length() > 500) {
            inputToLog = inputToLog.substring(0, 500) + "... [truncated in log]";
        }
        log.debug("Computed checksum for " + inputToLog + " as " + returnString);
        return returnString;

    }

    public static String computeMD5(InputStream stream) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");

            DigestInputStream digestStream = new DigestInputStream(stream, digest);
            byte[] buf = new byte[20480];
            while (digestStream.read(buf) != -1) {
                ; //digest is updating
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        byte[] digestBytes = digest.digest();

        String returnString = new String(encodeHex(digestBytes));

        log.debug("Computed checksum for inputStream as " + returnString);
        return returnString;
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data a byte[] to to to Hex characters
     * @return A char[] containing hexadecimal characters
     */
    private static char[] encodeHex(byte[] data) {

        int l = data.length;

        char[] out = new char[l << 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }

        return out;
    }


}
