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

import com.qdigo.ebike.common.core.constants.ConfigConstants;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Arrays;
import java.util.Random;

/**
 * Description:
 * date: 2019/12/11 5:06 PM
 *
 * @author niezhao
 * @since JDK 1.8
 */
public class SecurityUtil {


    static {
        //AesCbc解密初始化
        //BouncyCastle是一个开源的加解密解决方案，主页在http://www.bouncycastle.org/
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * @return 生成6位数字的随机验证码
     */
    public static String generatePinCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }

    public static String randomNum() {
        //10位
        return String.valueOf(new Random().nextInt(89999) + 10000) + new Random().nextInt(99999);
    }

    public static String generateAccessToken() {
        return MD5Util.computeMD5(String.valueOf(new Random().nextLong()));
    }

    //加密后的sha
    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodeStr = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256"); //apache的工具
            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
            encodeStr = Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    //加密后的sha
    public static String getSHA1(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成 HMACSHA256
     *
     * @param data 待处理数据
     * @param key  密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String getHMACSHA256(String data, String key) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Decrypt 解密
    public static String AesCbcDecrypt(String data, String key, String iv, String encodingFormat) {
        try {
            //被加密的数据
            byte[] dataByte = Base64.decodeBase64(data);
            //加密秘钥
            byte[] keyByte = Base64.decodeBase64(key);
            //偏移量
            byte[] ivByte = Base64.decodeBase64(iv);

            int base = 16; //如果密钥不足16位，就补足
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                return new String(resultByte, encodingFormat);
            } else {
                return null;
            }
        } catch (Exception e) {
            Security.addProvider(new BouncyCastleProvider());
            throw new RuntimeException(e);
        }

    }

    /**
     * 蓝牙的加密方法
     *
     * @param imei
     * @return
     */
    public static int[] bleCode(String imei) {
        imei = StringUtils.substringAfter(imei, ConfigConstants.imei.getConstant());
        // 1 0 0 8 8 8 8 8
        int[] ints = new int[8];
        for (int i = 0; i < imei.length(); i++) {
            int t = Integer.parseInt(String.valueOf(imei.charAt(i)));
            ints[i] = t;
        }
        int[] checkInt = new int[6];

        checkInt[5] = (ints[0] ^ ints[1] ^ ints[2] ^ ints[3] + (ints[4] * ints[5] + ints[6] * ints[7])) & 0x00ff;
        checkInt[4] = (ints[0] * ints[1] * ints[2] * ints[3] + ints[4] + ints[5] + ints[6] + ints[7]) & 0x00ff;
        checkInt[3] = ((ints[0] + ints[1] + ints[2] + ints[3]) * (ints[4] + ints[5] + ints[6] + ints[7])) & 0xff;
        checkInt[2] = ((ints[0] + ints[1] + ints[2] + ints[3]) | (ints[4] * ints[5] * ints[6] * ints[7])) & 0xff;
        checkInt[1] = (ints[0] ^ ints[1] ^ ints[2] ^ ints[3] ^ ints[4] ^ ints[5] ^ ints[6] ^ ints[7]) & 0xff;
        checkInt[0] = (ints[0] + ints[1] + ints[2] + ints[3] + ints[4] + ints[5] + ints[6] + ints[7]) & 0xff;
        return checkInt;
    }


}
