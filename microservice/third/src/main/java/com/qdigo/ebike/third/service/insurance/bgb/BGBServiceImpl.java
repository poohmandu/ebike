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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.third.FraudVerify;
import com.qdigo.ebike.api.domain.dto.third.insurance.InsuranceParam;
import com.qdigo.ebike.api.domain.dto.third.insurance.InsuranceRecordDto;
import com.qdigo.ebike.api.service.third.insurance.BgbService;
import com.qdigo.ebike.common.core.constants.BikeCfg;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.third.domain.entity.InsuranceRecord;
import com.qdigo.ebike.third.repository.InsuranceRecordRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.qdigo.ebike.third.service.insurance.bgb.BGBUtil.getNonce;
import static com.qdigo.ebike.third.service.insurance.bgb.BGBUtil.getSign;

/**
 * 白鸽宝投保
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BGBServiceImpl implements BgbService {

    private static final String PROMOTE_ID = "diandi";

    private final RedisTemplate<String, String> redisTemplate;
    private final InsuranceRecordRepository insuranceRecordRepository;

    @AllArgsConstructor
    private enum Product {
        school("PK00005927", "平安学校骑行人员意外险", "保险费0.045元/人/次;按承保失败返回信息价格是0.05"),
        scenic1("PK00005925", "平安酒店骑行人员意外险", "保险费0.2元/人/次;按承保失败返回信息价格是0.25"),
        scenic2("PK00005926", "平安酒店骑行人员意外险", "保险费0.2元/人/次;按承保失败返回信息价格是0.12");

        private String productCode;
        private String productName;
        private String desc;
    }

    @Override
    public FraudVerify identifyIdCard(String mobileNo, String name, String certNo) {
        log.debug("bgb验证用户:{},姓名:{},身份证号:{}", mobileNo, name, certNo);
        return new FraudVerify(true, false, "");
    }

    @Override
    @Transactional
    public InsuranceRecordDto insure(InsuranceParam param) {
        InsuranceRecord insure = this.insureReq(param);
        if (insure != null) {
            insuranceRecordRepository.save(insure);
            return ConvertUtil.to(insure, InsuranceRecordDto.class);
        }
        return null;
    }

    private InsuranceRecord insureReq(InsuranceParam param) {
        String realName = param.getRealName();
        String idNo = param.getIdNo();
        if (StringUtils.isEmpty(realName) || StringUtils.isEmpty(idNo)) {
            return null;
        }
        BikeCfg.OperationType operationType = param.getOperationType();
        Product product;
        if (operationType == BikeCfg.OperationType.school ||
                operationType == BikeCfg.OperationType.takeaway ||
                operationType == BikeCfg.OperationType.city ||
                operationType == BikeCfg.OperationType.community) {
            product = Product.school;
        } else {
            product = Product.scenic2;
        }

        BGBUtil.IdentityInfo info = BGBUtil.identityInfo(idNo);
        String orderSn = orderSn();

        BaigeRequestBody baigeRequestBody = new BaigeRequestBody();
        baigeRequestBody.setProduct_code(product.productCode);
        baigeRequestBody.setPolicy_user_name(realName);
        baigeRequestBody.setPolicy_user_certno(idNo);
        baigeRequestBody.setPolicy_user_certtype("1");
        baigeRequestBody.setPolicy_user_sex(info.getSex());
        baigeRequestBody.setPolicy_user_birthday(info.getBirthday());
        baigeRequestBody.setUnlock_time(FormatUtil.getCurTime());
        baigeRequestBody.setPolicy_user_mobile(param.getMobileNo());
        baigeRequestBody.setBiz_order_sn(orderSn);
        baigeRequestBody.setProvince(param.getProvince());
        baigeRequestBody.setCity(param.getCity());
        baigeRequestBody.setPromote_id(PROMOTE_ID);

        Date da = new Date();
        String requestbody = JSON.toJSONString(baigeRequestBody);

        String nonce = getNonce();
        String timestamp = String.valueOf(da.getTime() / 1000);
        String s = getSign(nonce, timestamp, requestbody);
        String body = "{\"requestBody\":" + requestbody + "," +
                "\"requestHead\":{\"timestamp\":" + timestamp + ",\"nonce\":\"" + nonce + "\",\"sign\":\"" + s + "\"}" +
                "}";
        HttpUriRequest request = RequestBuilder.post("http://diandi.baigebao.com/api/Diandi/insure")
                .setEntity(new StringEntity(body, ContentType.APPLICATION_JSON))
                .build();


        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            log.debug("白鸽宝投保接口返回:{}", jsonStr);
            JSONObject json = JSON.parseObject(jsonStr);
            String code = json.getString("code");
            String msg = json.getString("msg");
            if (!code.equals("1000")) {
                log.warn("白鸽宝投保失败:{}", jsonStr);
                return null;
            }

            InsuranceRecord insuranceRecord = new InsuranceRecord();

            insuranceRecord.setPolicyNo("");
            insuranceRecord.setOrderSn(orderSn);
            insuranceRecord.setErrorCode(code);
            insuranceRecord.setErrorMsg(msg);
            insuranceRecord.setName(realName.trim());
            insuranceRecord.setIdNo(idNo);
            insuranceRecord.setMobileNo(param.getMobileNo());
            insuranceRecord.setStartTime(new Date());
            insuranceRecord.setInsureType(product.productName);
            try {
                insuranceRecord.setEndTime(FormatUtil.yMdHms.parse(FormatUtil.y_M_d.format(new Date()) + " 23:59:59"));
            } catch (ParseException e) {
                log.error("解析时间异常:{}", e.getMessage());
            }
            insuranceRecord.setProductCode(product.productCode);
            insuranceRecord.setRideRecordId(param.getRideRecordId());
            return insuranceRecord;

        } catch (IOException e) {
            log.error("白鸽宝投保接口请求异常", e);
            return null;
        }
    }

    @Override
    public JSONObject policyQuery(String unlockTime, String orderSn) {

        Map<String, String> data = ImmutableMap.of("unlock_time", unlockTime, "biz_order_sn", orderSn);
        String requestBody = JSON.toJSONString(data);

        String nonce = getNonce();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String s = getSign(nonce, timestamp, requestBody);
        String body = "{\"requestBody\":" + requestBody + "," +
                "\"requestHead\":{\"timestamp\":" + timestamp + ",\"nonce\":\"" + nonce + "\",\"sign\":\"" + s + "\"}" +
                "}";

        HttpUriRequest request = RequestBuilder.post("http://diandi.baigebao.com/api/Diandi/policy_query")
                .setEntity(new StringEntity(body, ContentType.APPLICATION_JSON))
                .build();

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(entity);
            log.debug("白鸽宝保单查询接口返回:{}", jsonStr);
            return JSON.parseObject(jsonStr);
        } catch (IOException e) {
            log.error("白鸽宝投保接口请求异常", e);
            return null;
        }
    }


    private String orderSn() {
        String curTime = FormatUtil.yyyyMMddHHmmss.format(new Date());//精确到秒就行了
        String key = Keys.BGBOrderSn.getKey(FormatUtil.getCurDate());
        Long num = redisTemplate.opsForValue().increment(key, 1);
        if (num <= 2) {
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }
        DecimalFormat df = new DecimalFormat("00000000");
        // 20151210093658804 12345678
        return curTime + df.format(num);
    }

}
