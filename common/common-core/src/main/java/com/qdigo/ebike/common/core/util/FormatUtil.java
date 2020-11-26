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

package com.qdigo.ebike.common.core.util;


import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by niezhao on 2016/12/13.
 */
public class FormatUtil {

    public final static DateFormat yMd = new SimpleDateFormat("yyyyMMdd");
    public final static DateFormat y_M_d = new SimpleDateFormat("yyyy-MM-dd");
    public final static DateFormat MdHm = new SimpleDateFormat("MM-dd HH:mm");
    public final static DateFormat yMdHms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //到毫秒
    public final static DateFormat yMdhmsS = new SimpleDateFormat("yyyyMMddhhmmssSSS");
    public final static DateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    public final static DecimalFormat moneyFormat = new DecimalFormat("#.00");

    public final static DateFormat HH = new SimpleDateFormat("HH");

    //将json字符串格式化输出,便于调试
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    //添加space
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }

    public static String getCurDate() {
        return yMd.format(new Date());
    }

    public static String getToday() {
        return y_M_d.format(new Date());
    }

    public static String getTomorrowDate() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = c.getTime();
        return y_M_d.format(tomorrow);
    }

    public static String getCurTime() {
        return yMdHms.format(new Date());
    }

    public static String getCurHour() {
        return HH.format(new Date());
    }

    public static double getMoney(double money) {
        return Double.parseDouble(moneyFormat.format(money));
    }

    public static int yuanToFen(double amount) {
        return (int) (amount * 100);
    }

    public static double fenToYuan(int amount) {
        return getMoney(((double) amount) / 100);
    }

    public static Map<String, String> queryStrToMap(String str) {
        String[] split = StringUtils.split(str, "&");
        Map<String, String> map = new HashMap<>();
        for (String s : split) {
            String k = StringUtils.substringBefore(s, "=");
            String v = StringUtils.substringAfter(s, "=");
            map.put(k, v);
        }
        return map;
    }

    public static String locStr(double lng, double lat) {
        return String.format("%s,%s", lng, lat);
    }

    public static Map<String, Double> strToLoc(String locStr) {
        Map<String, Double> map = new HashMap<>();
        double lng = Double.parseDouble(StringUtils.substringBefore(locStr, ","));
        double lat = Double.parseDouble(StringUtils.substringAfter(locStr, ","));
        return ImmutableMap.of("lng", lng, "lat", lat);
    }

    public static String formatMobileNo(String mobileNo) {
        if (mobileNo.length() == 11) {
            return mobileNo.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        } else {
            return mobileNo;
        }
    }

    public static String formatIdNo(String idNo) {
        if (StringUtils.isBlank(idNo)) {
            return "";
        }
        return idNo.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1*****$2");
    }

    public static String formatChineseName(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return "";
        }
        String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
    }

    public static int hours(long seconds) {
        double minute = Math.ceil((double) seconds / 60);
        return (int) Math.ceil(minute / 60);
    }

    public static int units(long secondSum, int unitMinutes) {
        double minute = Math.ceil((double) secondSum / 60);
        return (int) Math.ceil(minute / (double) unitMinutes);
    }

    public static int minutes(long seconds) {
        return (int) Math.ceil((double) seconds / 60);
    }

}
