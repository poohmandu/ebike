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

package com.qdigo.ebike.common.core.util.geo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by niezhao on 2016/12/21.
 */
public class LocationConvert {

    // WGS-84：是国际标准，GPS坐标（Google Earth使用、或者GPS模块）
    // GCJ-02：中国坐标偏移标准，Google Map、高德、腾讯使用
    // BD-09：百度坐标偏移标准，Baidu Map使用
    private static double PI = Math.PI;
    private static double a = 6378245.0;
    private static double ee = 0.00669342162296594323;

    public static class Loc {
        public double lat;
        public double lng;
    }

    /**
     * 方法描述:方法可以将高德地图SDK获取到的GPS经纬度转换为真实的经纬度
     * GCJ_02_To_WGS_84
     *
     * @param lat lng 需要转换的经纬度
     * @return 转换为真实GPS坐标后的经纬度
     * @throws <异常类型> {@inheritDoc} 异常描述
     */
    public static Map<String, Double> fromAmapToGps(double lat, double lng) {
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLon = transformLon(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);

        Map<String, Double> hm = new HashMap<>();
        hm.put("lat", lat - dLat);
        hm.put("lng", lng - dLon);
        return hm;
    }


    /**
     * GPS坐标转火星坐标
     * WGS_84_To_GCJ_02
     *
     * @param wgLoc
     * @return
     */
    public static Map<String, Double> fromGPSToAmap(Map<String, Double> location) {
        double lng = location.get("lng");
        double lat = location.get("lat");

        double dLat = transformLat(lng - 105.0,
            lat - 35.0);
        double dLon = transformLon(lng - 105.0,
            lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);

        HashMap<String, Double> map = new HashMap<>();
        map.put("lat", lat + dLat);
        map.put("lng", lng + dLon);
        return map;
    }

    public static Map<String, Double> fromGPSToAmap(double lng, double lat) {
        Map<String, Double> map = new HashMap<>();
        map.put("lng", lng);
        map.put("lat", lat);
        return fromGPSToAmap(map);
    }

    //转换经度
    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    //转换纬度
    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

//    public static void main(String... arg){
//        double lng = 121.3531182;
//        double lat = 31.2188881;
//        HashMap<String, Double> map = fromAmapToGps(lat, lng);
//        //31.2207838900,121.3485318700
//        System.out.println(map);
//    }

}
