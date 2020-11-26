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

package com.qdigo.ebike.third.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.third.map.Address;
import com.qdigo.ebike.api.domain.dto.third.map.Point;
import com.qdigo.ebike.api.service.third.address.AmapService;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.util.geo.LocationConvert;
import com.qdigo.ebike.commonconfig.configuration.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * Description: 
 * date: 2019/12/27 6:53 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AmapServiceImpl implements AmapService {


    @Override
    public Map<String, String> baseStationLocation(String imei, String lac, String cellid, String imsi, String singal) {


        //信号强度判断矫正
        int si = Integer.parseInt(singal);
        singal = String.valueOf(si > 0 ? si * 2 - 113 : si);

        String jsonStr;
        //创建http客户端 java7
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            //封装请求参数
            List<NameValuePair> params = Lists.newArrayList();
            params.add(new BasicNameValuePair("key", ConfigConstants.iotKey.getConstant()));//用户唯一标识
            params.add(new BasicNameValuePair("accesstype", "0")); // 0:移动;1:wifi
            params.add(new BasicNameValuePair("imei", imei));//
            if (imsi != null)
                params.add(new BasicNameValuePair("imsi", imsi));//移动用户识别码
            params.add(new BasicNameValuePair("cdma", "0")); //0:否 1:是
            params.add(new BasicNameValuePair("network", "GPRS")); //GSM/GPRS/EDGE/HSUPA/HSDPA/WCDMA
            params.add(new BasicNameValuePair("bts", "460,1," + lac + "," + cellid + "," + singal)); //非cdma格式为:mcc,mnc,lac,cellid,signal
            params.add(new BasicNameValuePair("output", "json")); //返回数据类型
            params.add(new BasicNameValuePair("mcc", "460")); //移动用户所属国家代码
            params.add(new BasicNameValuePair("mnc", "1")); //0:中国移动  1:中国联通
            params.add(new BasicNameValuePair("lac", lac)); //未知区域码
            params.add(new BasicNameValuePair("cellid", cellid)); //基站小区编号
            params.add(new BasicNameValuePair("signal", singal)); //信号强度;为正时:强度*2-113
            //转换为键值对 queryString
            String paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(params, "UTF-8"));
            //创建Get请求
            HttpGet request = new HttpGet("http://apilocate.amap.com/position?" + paramsStr);
            //log.debug("发出的请求为" + request);
            //执行Get请求
            HttpResponse response;
            response = client.execute(request);

            //得到响应体
            HttpEntity entity = response.getEntity();
            //log.debug("\n得到的响应为" + response);
            //json 解析
            jsonStr = EntityUtils.toString(entity);
        } catch (IOException e) {
            log.error("HttpClient发送过程中出现异常");
            return null;
        }
        final JSONObject json = JSON.parseObject(jsonStr);
        String info = json.getString("info");
        //log.debug("\n响应体的json字符串为:\n" + FormatUtil.formatJson(jsonStr));
        //INVALID_USER_KEY:用户key非法或过期, OVER_QUOTA:超出配额, INVALID_PARAMS:参数非法
        if ("OK".equals(info)) {
            JSONObject result = json.getJSONObject("result");
            int type = result.getInteger("type");
            if (type != 0) { //0:没有得到定位结果
                //有返回结果
                //解析 经纬度
                String location = result.getString("location");
                double longitude = Double.parseDouble(StringUtils.substringBefore(location, ","));
                double latitude = Double.parseDouble(StringUtils.substringAfter(location, ","));
                String address = result.getString("desc");

                //将 高德坐标转化为 GPS坐标
                Map<String, Double> delta = LocationConvert.fromAmapToGps(latitude, longitude);

                Map<String, String> map = new HashMap<>();
                map.put("desc", address);
                map.put("longitude", Double.toString(delta.get("lng")));
                map.put("latitude", Double.toString(delta.get("lat")));
                //log.debug("基站位置查询返回:" + map);
                return map;
            } else {
                //无返回结果
                log.info("基站定位:没有从高德接口得到result");
            }
        }
        return null;

    }

    @Override
    public Address getAddress(double lat, double lng, boolean amapLoc) {

        final JSONObject json = this.getAddressJson(lat, lng, amapLoc);
        String info = json.getString("info");
        if ("OK".equals(info)) {
            val regeo = json.getJSONObject("regeocode");
            val addressComponent = regeo.getJSONObject("addressComponent");
            if (amapLoc) {
                val map = LocationConvert.fromAmapToGps(lat, lng);
                lat = map.get("lat");
                lng = map.get("lng");
            }
            Address address = new Address()
                    .setAdCode(addressComponent.getString("adcode"))
                    .setAddress(regeo.getString("formatted_address"))
                    .setCity(addressComponent.getString("city"))
                    .setCityCode(addressComponent.getString("citycode"))
                    .setDistrict(addressComponent.getString("district"))
                    .setProvince(addressComponent.getString("province"))
                    .setLatitude(lat).setLongitude(lng);
            return address;
        } else {
            return null;
        }
    }

    private JSONObject getAddressJson(double lat, double lng, boolean amapLoc) {
        if (!amapLoc) {
            val map = LocationConvert.fromGPSToAmap(lng, lat);
            lat = map.get("lat");
            lng = map.get("lng");
        }
        //经度在前，纬度在后
        DecimalFormat df = new DecimalFormat("#.000000");
        String location = df.format(lng) + "," + df.format(lat);
        //创建http客户端
        HttpUriRequest request = RequestBuilder.get("http://restapi.amap.com/v3/geocode/regeo")
                .addParameter("key", ConfigConstants.amap_server.getConstant())
                .addParameter("location", location)
                .addParameter("radius", "0")
                .addParameter("homeorcorp", "1")
                .addParameter("output", "JSON")
                .setConfig(Config.REQUEST_CONFIG)
                .build();
        //log.debug("高德请求为:{}", request);
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            //得到响应体
            HttpEntity entity = response.getEntity();
            //log.debug("得到的响应为" + response);
            String jsonStr = EntityUtils.toString(entity);
            //log.debug("响应体的json字符串为:{}", FormatUtil.formatJson(jsonStr));
            return JSON.parseObject(jsonStr);
        } catch (IOException e) {
            log.error("HttpClient发送过程中出现异常:", e);
            return null;
        }
    }

    @Override
    public JSONObject getIPAddress(String ip) {
        if (ip == null) {
            return null;
        }
        HttpUriRequest request = RequestBuilder.get("http://restapi.amap.com/v3/ip")
                .addParameter("key", ConfigConstants.amap_server.getConstant())
                .addParameter("output", "JSON")
                .addParameter("ip", ip)
                .setConfig(Config.REQUEST_CONFIG)
                .build();
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (!"OK".equals(jsonObject.getString("info"))) {
                log.error("获取ip地址失败:{}", jsonStr);
                return null;
            }
            return jsonObject;
        } catch (IOException e) {
            log.error("HttpClient发送过程中出现异常", e);
            return null;
        }
    }

    private String getPointsStr(List<Point> points) {
        StringBuilder pointsStr = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            pointsStr.append(point.getLongitude()).append(",").append(point.getLatitude());
            if (i != (points.size() - 1)) {
                pointsStr.append(";");
            }
        }
        return pointsStr.toString();
    }

    @Override
    public JSONObject createFence(String name, List<Point> points, String desc) {
        try {
            val post = new HttpPost("http://restapi.amap.com/v4/geofence/meta?key=" + ConfigConstants.amap_fence.getConstant());
            val map = new ImmutableMap.Builder<String, String>()
                    .put("name", name)
                    .put("points", this.getPointsStr(points))
                    .put("valid_time", "2030-01-01")
                    .put("desc", desc)
                    .put("alert_condition", "enter;leave")
                    .put("repeat", "Mon,Tues,Wed,Thur,Fri,Sat,Sun")
                    .build();
            post.setEntity(new StringEntity(JSON.toJSONString(map), APPLICATION_JSON));

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(post)) {
                val entity = response.getEntity();
                val str = EntityUtils.toString(entity);
                log.debug("createFence返回消息:{}", str);
                val json = JSON.parseObject(str);
                val errmsg = json.getString("errmsg");
                if (errmsg.equals("OK")) {
                    val data = json.getJSONObject("data");
                    val status = data.getInteger("status");
                    if (status == 0) {
                        return json;
                    }
                }
                log.error("创建围栏失败{}", str);
            }
        } catch (Exception e) {
            log.error("创建围栏失败:", e);
        }
        return null;
    }

    /**
     * @author niezhao
     * @description 查询单个点和围栏的关系
     *
     * @date 2019/12/28 1:53 AM
     * @param imei
     * @param lat
     * @param lng
     * @return com.alibaba.fastjson.JSONObject
     **/
    @Override
    public JSONObject fenceStatus(String imei, double lat, double lng) {
        try {
            List<NameValuePair> params = Lists.newArrayList();
            params.add(new BasicNameValuePair("key", ConfigConstants.amap_fence.getConstant()));
            params.add(new BasicNameValuePair("imei", imei));
            params.add(new BasicNameValuePair("diu", imei));
            params.add(new BasicNameValuePair("locations", lng + "," + lat + "," + System.currentTimeMillis() / 1000));
            val format = URLEncodedUtils.format(params, "UTF-8");
            val url = "http://restapi.amap.com/v4/geofence/status?" + format;
            val get = new HttpGet(url);
            log.debug("高德-围栏设备监控接口请求:{}", get);
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(get)) {
                val str = EntityUtils.toString(response.getEntity());
                val json = JSON.parseObject(str);
                if ("OK".equals(json.getString("errmsg"))) {
                    val data = json.getJSONObject("data");
                    if (data.getInteger("status") == 0) {
                        return json;
                    }
                }
                log.error("围栏设备监控失败:{}", str);
            }
        } catch (Exception e) {
            log.error("围栏设备监控异常:", e);
        }
        return null;
    }

    @Override
    public boolean deleteFence(String gid) {
        try {
            val url = "http://restapi.amap.com/v4/geofence/meta?key=" + ConfigConstants.amap_fence.getConstant() + "&gid=" + gid;
            val delete = new HttpDelete(url);
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(delete)) {
                val str = EntityUtils.toString(response.getEntity());
                val json = JSON.parseObject(str);
                if ("OK".equals(json.getString("errmsg"))) {
                    val data = json.getJSONObject("data");
                    if (data.getInteger("status") == 0) {
                        return true;
                    }
                }
                log.error("删除围栏失败:{}", str);
            }
        } catch (Exception e) {
            log.error("删除围栏异常:", e);
        }
        return false;
    }
}
