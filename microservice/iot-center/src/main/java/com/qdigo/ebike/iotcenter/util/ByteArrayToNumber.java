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


import java.io.UnsupportedEncodingException;

/**
 * 字节数组转java基本类型
 *
 * @author yudengqiu
 */
public class ByteArrayToNumber {

    //int-4字节  short-2字节 long-8字节 char-1字节

    public static char byteToChar(byte pgByte) {
        return (char) pgByte;
    }

    public static int byteArrayToInt(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24) //最高位
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

    public static short byteArrayToShort(byte[] src, int offset) {
        short value = 0;
        value = (short) (((src[offset] & 0xFF) << 8) //最高位
                | ((src[offset + 1] & 0xFF)));
        return value;
    }

    public static long bytesToLong(byte[] bytes, int offset) {
        long s = 0;
        long s0 = bytes[offset] & 0xff;// 最低位
        long s1 = bytes[offset + 1] & 0xff;
        long s2 = bytes[offset + 2] & 0xff;
        long s3 = bytes[offset + 3] & 0xff;
        long s4 = bytes[offset + 4] & 0xff;
        long s5 = bytes[offset + 5] & 0xff;
        long s6 = bytes[offset + 6] & 0xff;
        long s7 = bytes[offset + 7] & 0xff;

        s0 <<= 8 * 7;
        s1 <<= 8 * 6;
        s2 <<= 8 * 5;
        s3 <<= 8 * 4;
        s4 <<= 8 * 3;
        s5 <<= 8 * 2;
        s6 <<= 8;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    /**
     * 取字节第几bit的值(如offset为0,就是取bit7;offset为7,就是取bit0;by niezhao 2017/1/6)
     *
     * @param b
     * @param offset 0-7
     * @return
     */
    public static byte biteToByte(byte b, int offset) {
        byte destByte;
        destByte = (byte) ((b >> (7 - offset)) & 0x1);
        return destByte;
    }

    /**
     * 4bit表示一个16进制
     * 一个字节表示2个16进制
     *
     * @param b
     * @param b 0-7
     * @return
     */
    public static String biteToHexString(byte b) {
        String hexString = null;
        hexString = Integer.toHexString(((b >> 4) & 0x0f));
        hexString += Integer.toHexString((b & 0x0f));
        return hexString;
    }

    /**
     * 字节数组转字符串
     *
     * @param bytes
     * @return
     */
    public static String bytesToString(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");//若用utf-8,解码无法复原
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字节转换为浮点
     *
     * @param b     字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byteArrayfloat(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    /**
     * @param bytes
     * @param offset 字节数组开始位置
     * @param length 长度
     * @return
     */
    public static String bytesToHexString(byte[] bytes, int offset, int length) {
        StringBuffer buffer = new StringBuffer("");
        for (int i = 0; i < length; i++) {
            buffer.append(ByteArrayToNumber.biteToHexString(bytes[offset + i]));
        }
        return buffer.toString();

    }

    public static byte halfOneBytesToByte(byte b) {
        /*
        byte b1 = (byte) (b & 0xf0);
        byte a1 = (byte) ((b1 >> 7) & 0x1);
        byte a2 = (byte) ((b1 >> 6) & 0x1);
        byte a3 = (byte) ((b1 >> 5) & 0x1);
        byte a4 = (byte) ((b1 >> 4) & 0x1);
        byte c1 = (byte) ((a1 << 0) & 0x01);
        byte c2 = (byte) ((a2 << 1) & 0x02);
        byte c3 = (byte) ((a3 << 2) & 0x04);
        byte c4 = (byte) ((a1 << 3) & 0x08);
        byte b2 = (byte) (c1 | c2 | c3 | c4);
        return b2;*/
        return (byte) ((b & 0xf0) >> 4);
    }

    public static byte halfTwoBytesToByte(byte b) {
        /*
        byte b3 = (byte) (b & 0x0f);
        byte d1 = (byte) ((b3 >> 3) & 0x1);
        byte d2 = (byte) ((b3 >> 2) & 0x1);
        byte d3 = (byte) ((b3 >> 1) & 0x1);
        byte d4 = (byte) ((b3 >> 0) & 0x1);
        byte e1 = (byte) ((d1 << 0) & 0x01);
        byte e2 = (byte) ((d2 << 1) & 0x02);
        byte e3 = (byte) ((d3 << 2) & 0x04);
        byte e4 = (byte) ((d4 << 3) & 0x08);
        byte b4 = (byte) (e1 | e2 | e3 | e4);
        return b4;*/
        return (byte) (b & 0x0f);
    }

    public static String bytesToStringByHalfByte(byte[] bytes, int offset, int byteLength) {
        String s = "";
        for (int i = 0; i < byteLength; i++) {
            byte b = halfOneBytesToByte(bytes[offset + i]);
            byte b1 = halfTwoBytesToByte(bytes[offset + i]);
            s += b;
            s += b1;
        }
        return s;
    }

//    public static void main(String[] arg) {
//        byte[] bytes = {77, 76, 2, 64, 122, -81, 82, -40, 72, 53, 25,
//                70, 0, 121, -126, -126, 52, 71, 80};
//        String s = bytesToStringByHalfByte(bytes, 11, 8);
//        System.out.println(s);
//    }
}


