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

package com.qdigo.ebike.third.service.insurance.bgb;

import com.qdigo.ebike.common.core.util.FormatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BGBUtil {

    /**
     *  白鸽宝投保
     *  http://apidoc.baigebaodev.com/39?page_id=1647
     */

    private static final String TOKEN = "5ae6479a319d4aba8ebc76ec44bdd23c";
    private static final String TEST_TOKEN = "a8591dc7964716755f1fe34a8c936a54";
    private static final Pattern pattern = Pattern.compile("^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");


    private static String encode(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] buffer = digest.digest(text.getBytes());
            // byte -128 ---- 127
            StringBuilder sb = new StringBuilder();
            for (byte b : buffer) {
                int a = b & 0xff;
                String hex = Integer.toHexString(a);
                if (hex.length() == 1) {
                    hex = 0 + hex;
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    static String getSign(String nonce, String timestamp, String requestBody) {
        String md5requestbody = encode(requestBody);
        String[] arrTem = {TOKEN, timestamp, nonce, md5requestbody};
        Arrays.sort(arrTem);
        StringBuilder param = new StringBuilder();
        for (String anArrTem : arrTem) {
            param.append(anArrTem);
        }
        return encode(param.toString());
    }

    static String getNonce() {
        String s = encode(String.valueOf(System.currentTimeMillis()));
        assert s != null;
        return s.substring(0, 8) + "-" + s.substring(8, 12) + "-" + s.substring(12, 16)
                + "-" + s.substring(16, 20) + "-" + s.substring(20);
    }


    static IdentityInfo identityInfo(String id) {
        String exceptionMsg = Objects.requireNonNull(id, "身份证号不能为空");
        if (Objects.equals(id, "")) {
            throw new IllegalArgumentException(exceptionMsg);
        }
        Matcher matcher = pattern.matcher(id);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("身份证号码不合法");
        }

        String birthdayStr = id.substring(6, 14);
        String birthday = null;
        try {
            birthday = FormatUtil.y_M_d.format(FormatUtil.yMd.parse(birthdayStr));
        } catch (ParseException ignored) {
        }
        String sex;
        if (Integer.parseInt(id.substring(16).substring(0, 1)) % 2 == 0)
            sex = "2"; // 女=2
        else
            sex = "1"; // 男=1
        return new IdentityInfo(sex, birthday);
    }

    @Getter
    @AllArgsConstructor
    static class IdentityInfo {
        private String sex;
        private String birthday;
    }


}