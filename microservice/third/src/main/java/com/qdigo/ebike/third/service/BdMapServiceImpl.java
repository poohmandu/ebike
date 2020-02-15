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
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.third.address.BdMapService;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.commonconfig.configuration.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: 
 * date: 2020/1/18 12:31 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RemoteService
public class BdMapServiceImpl implements BdMapService {

    private final static String ak = ConfigConstants.ak.getConstant();
    private final static String serviceId = ConfigConstants.yingyanServiceId.getConstant();
    private final static String baseUrl = "http://yingyan.baidu.com/api/v3";

    @Override
    public JSONObject addEntity(String entityName) {
        HttpUriRequest request = RequestBuilder.post(baseUrl + "/entity/add")
                .addParameter("ak", ak)
                .addParameter("service_id", serviceId)
                .addParameter("entity_name", entityName)
                .setConfig(Config.REQUEST_CONFIG)
                .build();
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);

            return JSON.parseObject(jsonStr);
        } catch (IOException e) {
            log.debug("BDMap的IO异常", e);
            return null;
        }
    }

    @Override
    public JSONObject updateEntity(String entityName, String entityDesc) {
        HttpUriRequest request = RequestBuilder.post(baseUrl + "/entity/update")
                .addParameter("ak", ak)
                .addParameter("service_id", serviceId)
                .addParameter("entity_name", entityName)
                .addParameter("entity_desc", entityDesc)
                .addParameter("city", "待定")
                .setConfig(Config.REQUEST_CONFIG)
                .build();
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            return JSON.parseObject(jsonStr);
        } catch (IOException e) {
            log.debug("BDMap的IO异常", e);
            return null;
        }
    }

    @Override
    public JSONObject addPoints(List<Map> points) {
        List<Map> list = points.stream().map(map -> (Map) ImmutableMap.builder()
                .put("entity_name", map.get("imei"))
                .put("latitude", map.get("latitude"))
                .put("longitude", map.get("longitude"))
                .put("loc_time", this.toUnixTimestamp((Long) map.get("time")))
                .put("coord_type_input", "wgs84")
                .build()).collect(Collectors.toList());

        HttpUriRequest request = RequestBuilder.post(baseUrl + "/track/addpoints")
                .addParameter("ak", ak)
                .addParameter("service_id", serviceId)
                .addParameter("point_list", JSON.toJSONString(list))
                .setConfig(Config.REQUEST_CONFIG)
                .build();
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            JSONObject json = JSON.parseObject(jsonStr);
            Integer status = json.getInteger("status");
            if (status != null && status == 0) {
                return json;
            } else {
                log.warn("BDMap批量增加点异常:{}", json);
                return null;
            }
        } catch (IOException e) {
            log.debug("BDMap的IO异常", e);
            return null;
        }

    }

    @Override
    public JSONObject addPoint(String entityName, double latitude, double longitude) {
        HttpUriRequest request = RequestBuilder.post(baseUrl + "/track/addpoint")
                .addParameter("ak", ak)
                .addParameter("service_id", serviceId)
                .addParameter("entity_name", entityName)
                .addParameter("coord_type_input", "wgs84")
                .addParameter("latitude", String.valueOf(latitude))
                .addParameter("longitude", String.valueOf(longitude))
                .addParameter("loc_time", this.toUnixTimestamp(System.currentTimeMillis()))
                .setConfig(Config.REQUEST_CONFIG)
                .build();
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            JSONObject json = JSON.parseObject(jsonStr);
            Integer status = json.getInteger("status");
            if (status != null && status == 0) {
                return json;
            } else {
                log.warn("BDMap增加点异常:{}", json);
                return null;
            }
        } catch (IOException e) {
            log.debug("BDMap的IO异常", e);
            return null;
        }
    }

    private String toUnixTimestamp(long timestamp) {
        return String.valueOf((int) (timestamp / 1000));
    }

    @Override
    public JSONObject getTrack(String entityName, Date start, Date end) {
        HttpUriRequest request = RequestBuilder.get(baseUrl + "/track/gettrack")
                .addParameter("ak", ak)
                .addParameter("service_id", serviceId)
                .addParameter("entity_name", entityName)
                .addParameter("start_time", this.toUnixTimestamp(start.getTime()))
                .addParameter("end_time", this.toUnixTimestamp(end.getTime()))
                .addParameter("is_processed", "1")
                .addParameter("process_option", "need_denoise=1,radius_threshold=50,need_vacuate=1,need_mapmatch=1,transport_mode=riding")
                .addParameter("coord_type_output", "gcj02") //存的是高德坐标
                .addParameter("page_size", "5000") //24*60*4=5760
                .setConfig(Config.REQUEST_CONFIG)
                .build();

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            JSONObject json = JSON.parseObject(jsonStr);
            Integer status = json.getInteger("status");
            if (status != null && status == 0) {
                return json;
            } else {
                log.warn("BDMap发生异常:{}", json);
                return null;
            }
        } catch (IOException e) {
            log.debug("BDMap的IO异常", e);
            return null;
        }
    }

}
