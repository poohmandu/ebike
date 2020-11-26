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

package com.qdigo.ebike.third.service.insurance.hmb;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAEncrypt {

    /**
     * 开发者私钥 (注意：JAVA使用的是PKCS8的编码，如果私钥不是PKCK8的编码，请使用Openssl进行PKCS8编码)
     */
    public final static String DEV_PRIVATE_KEY =
        "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJJ9e/d8m8IhZu4P" +
            "YD4XhERJd1Cqy5qco3mSwklsnPWyay6Ey7H/94ToF+R3u1ix6ce9Z5zdDK5R8gNA" +
            "5xn9DVPr1WmAKcLO1FwqlljJ5pUqqNX9VLp7qa+gMyVrexH+DZG2U5onia18kwpl" +
            "dk6RKeChqoJ2f2BdZp7RvUMBDcOPAgMBAAECgYBMdnNemwwyKH0xhZUW0++WNUKy" +
            "OHROH7S0oIYJDUhgduC7R1WLc7AfD4ZpGpYPlc4RFCwhFMCpvNw2FoqKZiut+CDy" +
            "6ePaL6AYIaXz7RJDH+Dj8QtTRkH6JZlUrvgkseAtNSxjNH7gT9vtL7Bwl+oZrZZu" +
            "zDiwaSbeRVF9mbUfAQJBAMKUDZUv7qLPu3+YA3ApjOs+fA1k4iWd/233Qj64j80i" +
            "30BtPyNfqHNT/OwF6bf/ftXetR1dLzeYO7IlMtT7hRsCQQDAu2jEupObatNsgSpX" +
            "iWxz1KZvdqNHwe00uO/OEhthfnXz6YaYJRMOEGB8VJzSvUnpIY/IbxbKoYyzeOjQ" +
            "c4adAkEAq/gyMQKWBKtYaaKulzkB5P/qn+Pjw59qm4QGtmxkG8eQTN7BCMCInrVC" +
            "Ok/Xitly/g7BP9yV2KrhR8d8r6REtQJAR+rmylow0FLJd/iu2yFNld9pB7jGbvVf" +
            "VUgZO9Un4HZ2/0BJ2CYleR8FUf+k0UKO/O5oH8vMPmqP/TyF65rgRQJBAJMyUTAG" +
            "Y+cTlxPDYaJiWOcXDlGK2qrbBuGRi6MYsfvxvlErhSQsQdu0KAcqLVj4DB8wh7r1" +
            "RQXv3nn4VnLtfgc=";

    /**
     * 公钥
     **/
    public final static String HMB_PUBLIC_KEY =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzLDrEccy9nb2OtCAOOc/xMv6c" +
            "QLK+8JtzKgEzNqYwYoCkVpMXBphSWBTkUYjIJJUFl8a9C9ykQu6tuTuG4Ylqk7D0" +
            "n1i3HjkCAcmjg7/dYji5EtoXsIHuMDAT7Y+FuikA2VKtcCMheSIKuBZAZXLHlWms" +
            "+GCaH4IfD4wIEfUYRwIDAQAB";

    /**
     * RSA加密方法
     *
     * @param data      待加密数据
     * @param publicKey 公钥字符串
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(byte[] data, String publicKey)
        throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > 117) {
                cache = cipher.doFinal(data, offSet, 117);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * 117;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * RSA解密方法
     *
     * @param encryptedString
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String encryptedString, String privateKey)
        throws Exception {
        byte[] encryptedData = Base64.getDecoder().decode(encryptedString);
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > 128) {
                cache = cipher.doFinal(encryptedData, offSet, 128);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * 128;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData, "UTF-8");
    }

}
