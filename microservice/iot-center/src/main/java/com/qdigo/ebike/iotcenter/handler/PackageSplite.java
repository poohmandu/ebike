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

package com.qdigo.ebike.iotcenter.handler;

import com.qdigo.ebike.iotcenter.util.ByteArrayToNumber;
import io.netty.buffer.ByteBuf;

/**
 * Created by niezhao on 2017/1/12.
 */
public class PackageSplite {

    public static int getLength(ByteBuf buf) {
        byte b0 = buf.getByte(0);
        byte b1 = buf.getByte(1);
        char header0 = ByteArrayToNumber.byteToChar(b0);
        char header1 = ByteArrayToNumber.byteToChar(b1);
        if ('X' == header1) {
            return 9;
        } else if ('P' == header0 && 'H' == header1) {
            return 24;
        } else if ('P' == header0 && 'C' == header1) {
            return 20;
        } else if ('P' == header0 && 'L' == header1) {
            return 17;
        } else if ('P' == header0 && 'G' == header1) {
            return 20;
        } else if ('M' == header0 && 'D' == header1) {
            return 16;
        } else if ('M' == header0 && 'L' == header1) {
            return 17;
        } else if ('M' == header0 && 'C' == header1) {
            return 20;
        } else {
            return 0;
        }
    }

//    public static byte[] getBytes(ByteBuf buf) {
//        byte[] bytes = new byte[buf.readableBytes()];
//        buf.readBytes(bytes);
//        byte[] tmp = new byte[32];
//        for (int i = 0; i < bytes.length; i++) {
//            if ('P' == bytes[i]) {
//                if ('H' == bytes[i + 1]) {
//                    System.arraycopy(bytes, i, tmp, 0, 24);
//                } else if ('L' == bytes[i + 1]) {
//                    System.arraycopy(bytes, i, tmp, 0, 17);
//                } else if ('G' == bytes[i + 1]) {
//                    System.arraycopy(bytes, i, tmp, 0, 20);
//                } else if ('C' == bytes[i + 1]) {
//                    int min = Math.min(tmp.length, bytes.length - i);
//                    System.arraycopy(bytes, i, tmp, 0, min);
//                }
//            } else if ('M' = bytes[i]) {
//
//            }
//
//        }
//
//    }


}
