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
import com.google.common.collect.ImmutableList;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.third.FraudVerify;
import com.qdigo.ebike.api.service.third.insurance.DataPayService;
import com.qdigo.ebike.commonconfig.configuration.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
@RemoteService
public class DataPayServiceImpl implements DataPayService {

    private final static String ID_CARD = "e4fd8e433f77bbc7ba364d42218c4fe0";
    private final static String FACE = "0c0f056edbf88f63a5c86d560fb116ac";

    private final static String BASE_URL = "http://api.chinadatapay.com/communication/personal";
    private final static String ID_CARD_URI = "/1882";
    private final static String FACE_URI = "/2061";

    private final static double requireScore = 0.75;

    public FraudVerify identifyFace(String mobileNo, String name, String certNo, String imageId) {
        log.debug("数据宝人脸比对用户:{},姓名:{},身份证号:{},上传图片:{}", mobileNo, name, certNo, imageId);
        ImmutableList<NameValuePair> params = ImmutableList.<NameValuePair>builder()
            .add(new BasicNameValuePair("key", FACE))
            .add(new BasicNameValuePair("name", name))
            .add(new BasicNameValuePair("idcard", certNo))
            .add(new BasicNameValuePair("imageId", imageId))
            .build();
        HttpEntity requestEntity;
        try {
            requestEntity = new UrlEncodedFormEntity(params, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new FraudVerify(false, true, e.getMessage());
        }
        HttpUriRequest request = RequestBuilder.post(BASE_URL + FACE_URI).setEntity(requestEntity).setConfig(Config.REQUEST_CONFIG).build();

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            //得到响应体
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            JSONObject json = JSON.parseObject(jsonStr);
            log.debug("数据宝人脸比对接口返回数据为:{}", jsonStr);
            String code = json.getString("code");
            if (!code.equals("10000")) {
                if (code.equals("SYSTEM_002")) {
                    log.warn("数据宝接口identifyFace调用次数已用完"); //特殊情况通过
                    FraudVerify fraudVerify = new FraudVerify(false, true, json.getString("message"));
                    fraudVerify.setBizCode(code);
                    return fraudVerify;
                }
                return new FraudVerify(false, true, json.getString("message"));
            }
            double score = json.getJSONObject("data").getDoubleValue("score");
            log.debug("人脸比对相似度为:{}", score);
            if (score < requireScore) {
                return new FraudVerify(false, false, "人脸比对相似度低,验证不通过");
            }
            return new FraudVerify(true, false, "人脸验证通过");
        } catch (IOException e) {
            log.error("数据宝人脸比对接口异常", e);
            return new FraudVerify(false, true, e.getMessage());
        }
    }

    @Override
    public FraudVerify identifyIdCard(String mobileNo, String name, String certNo) {
        log.debug("数据宝验证用户:{},姓名:{},身份证号:{}", mobileNo, name, certNo);
        //HttpUriRequest request = RequestBuilder.post(BASE_URL + ID_CARD_URI + "?key=" + ID_CARD + "&name=" + name + "&idcard=" + certNo)
        ImmutableList<NameValuePair> params = ImmutableList.of(new BasicNameValuePair("key", ID_CARD),
            new BasicNameValuePair("name", name), new BasicNameValuePair("idcard", certNo));
        HttpEntity requestEntity;
        try {
            requestEntity = new UrlEncodedFormEntity(params, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new FraudVerify(false, true, e.getMessage());
        }
        HttpUriRequest request = RequestBuilder.post(BASE_URL + ID_CARD_URI).setEntity(requestEntity).setConfig(Config.REQUEST_CONFIG).build();

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            //得到响应体
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            JSONObject json = JSON.parseObject(jsonStr);
            log.debug("数据宝接口返回数据为:{}", jsonStr);
            String code = json.getString("code");
            if (!code.equals("10000")) {
                if (code.equals("SYSTEM_002")) {
                    log.warn("数据宝接口identifyIdCard调用次数已用完"); //特殊情况通过
                    FraudVerify fraudVerify = new FraudVerify(false, true, json.getString("message"));
                    fraudVerify.setBizCode(code);
                    return fraudVerify;
                }
                return new FraudVerify(false, true, json.getString("message"));
            }
            String result = json.getJSONObject("data").getString("result");
            if (!result.equals("1")) {
                return new FraudVerify(false, false, "身份信息验证不通过");
            }
            return new FraudVerify(true, false, "身份信息验证通过");
        } catch (IOException e) {
            return new FraudVerify(false, true, e.getMessage());
        }
    }
}
