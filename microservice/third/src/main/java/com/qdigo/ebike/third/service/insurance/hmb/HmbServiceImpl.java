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

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.third.FraudVerify;
import com.qdigo.ebike.api.domain.dto.third.insurance.InsuranceParam;
import com.qdigo.ebike.api.domain.dto.third.insurance.InsuranceRecordDto;
import com.qdigo.ebike.api.service.third.insurance.HmbService;
import com.qdigo.ebike.common.core.constants.BikeCfg;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoSuchTypeException;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonconfig.configuration.Config;
import com.qdigo.ebike.third.domain.entity.InsuranceRecord;
import com.qdigo.ebike.third.repository.InsuranceRecordRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Description: 保险服务类
 * Author: jiangchen
 * Date: 2017/9/7
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HmbServiceImpl implements HmbService {

    //http://open.bigins.cn/wiki/index/?id=28

    private final static String APPID = "10421";
    private final static String COMPANY_CODE = "qdigo";  //公司编码
    private final static String USER_CERTTYPE = "1";    //投保人证件类型，1为身份证，2为护照
    private final static String SERVICE_NAME = "hmb.ebike.orders.add";
    private final static String SURL = "http://open.bigins.cn/api/open_biz/index";  //海绵保的生产环境地址
    private final static String SDK_VER = "1.0.0";
    private final static String STUDENT_PLAN = "1005080";  //学生车辆保险方案
    private final static String SCENIC_PLAN = "1005081";  //景区车辆保险方案

    private final InsuranceRecordRepository insuranceRecordRepository;

    @Override
    @Transactional
    public InsuranceRecordDto insure(InsuranceParam param) {
        InsuranceRecord record = this.transfer(param);
        if (record != null) {
            insuranceRecordRepository.save(record);
            return ConvertUtil.to(record, InsuranceRecordDto.class);
        } else {
            return null;
        }
    }

    private InsuranceRecord transfer(InsuranceParam param) {
        String realName = param.getRealName();
        String idNo = param.getIdNo();
        if (StringUtils.isEmpty(realName) || StringUtils.isEmpty(idNo)) {
            return null;
        }
        BikeCfg.OperationType operationType = param.getOperationType();
        String productCode;
        //若是B型车，即为景区的车辆，走景区的方案
        if (operationType == BikeCfg.OperationType.scenic) {
            productCode = SCENIC_PLAN;
        } else if (operationType == BikeCfg.OperationType.school) {
            productCode = STUDENT_PLAN;
        } else if (operationType == BikeCfg.OperationType.city ||
                operationType == BikeCfg.OperationType.takeaway ||
                operationType == BikeCfg.OperationType.community) {
            log.debug("{}类型的车辆不会投保", operationType);
            return null;
        } else {
            throw new NoSuchTypeException("未知类型(" + operationType + ")的车辆");
        }
        BizContent bizContent = new BizContent();
        bizContent.setProductCode(productCode);
        bizContent.setBizOrderSn(String.valueOf(param.getRideRecordId()));
        bizContent.setPolicyUserName(realName);
        bizContent.setPolicyUserCertNo(idNo);
        bizContent.setPolicyUserMobile(param.getMobileNo());

        Map<String, String> map = this.post(bizContent);
        //若调用保险接口返回错误码为20040，可能为海绵保调取平安接口失败，修改订单号之后，再尝试调用一次
        if (map == null || "20040".equals(map.get("errorCode"))) {
            bizContent.setBizOrderSn(bizContent.getBizOrderSn() + "R");
            map = this.post(bizContent);
        }

        if (map != null) {
            InsuranceRecord insurance = new InsuranceRecord();
            insurance.setPolicyNo(map.getOrDefault("policyNo", ""));
            insurance.setOrderSn(map.getOrDefault("orderSn", ""));
            insurance.setErrorCode(map.getOrDefault("errorCode", ""));
            insurance.setErrorMsg(map.getOrDefault("errorMsg", ""));
            insurance.setName(realName.trim());
            insurance.setIdNo(idNo);
            insurance.setMobileNo(param.getMobileNo());
            insurance.setStartTime(new Date());
            insurance.setInsureType("平安意外伤害险");
            try {
                insurance.setEndTime(FormatUtil.yMdHms.parse(FormatUtil.y_M_d.format(new Date()) + " 23:59:59"));
            } catch (ParseException e) {
                log.error("解析时间异常:", e);
            }
            insurance.setProductCode(productCode);
            insurance.setRideRecordId(param.getRideRecordId());
            return insurance;
        } else {
            return null;
        }
    }

    @Override
    public FraudVerify identifyIdCard(String mobileNo, String name, String certNo) {
        log.debug("hmb验证用户:{},姓名:{},身份证号:{}", mobileNo, name, certNo);
        DateFormat dateFormat = FormatUtil.yMdhmsS;
        String timestamp = dateFormat.format(DateUtils.addSeconds(new Date(), 20));// 502=保险起期,未超过产品规则最早生效的时间

        //对入参进行处理
        String jsonContent = JSON.toJSONString(ImmutableMap.builder()
                .put("product_code", STUDENT_PLAN)
                .put("biz_company_code", COMPANY_CODE)
                .put("biz_order_sn", mobileNo + System.currentTimeMillis())
                .put("policy_user_name", name)
                .put("policy_user_certtype", USER_CERTTYPE)
                .put("policy_user_certno", certNo)
                .put("policy_user_mobile", mobileNo)
                .put("policy_start_time", FormatUtil.yMdHms.format(new Date()))
                .put("policy_end_time", FormatUtil.y_M_d.format(new Date()) + " 23:59:59")
                .build());

        //基本信息加密
        String resContent;
        try {
            resContent = RSAEncrypt.encryptByPublicKey(jsonContent.getBytes("UTF-8"), RSAEncrypt.HMB_PUBLIC_KEY);
        } catch (Exception e) {
            log.error("hmb实名认证加密失败:" + e.getMessage());
            return new FraudVerify(false, true, e.getMessage());
        }
        Map<String, String> postData = new HashMap<>();
        postData.put("appId", APPID);
        postData.put("serviceName", SERVICE_NAME);
        postData.put("timestamp", timestamp);
        postData.put("version", SDK_VER);
        postData.put("format", "Json");
        postData.put("charset", "UTF-8");
        postData.put("bContent", resContent);
        Map<String, String> sortMap = new TreeMap<>(String::compareTo);
        sortMap.putAll(postData);
        postData = sortMap;
        StringBuilder signBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : postData.entrySet()) {
            signBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String sign = signBuilder.toString();
        sign = sign.substring(0, sign.length() - 1);
        //用商户自己私钥进行签名
        sign = RSASignature.sign(sign, RSAEncrypt.DEV_PRIVATE_KEY, "UTF-8");
        postData.put("sign", sign);
        postData.put("signType", "RSA");
        //发送请求组装数据
        RequestBuilder builder = RequestBuilder.post(SURL).setConfig(Config.REQUEST_CONFIG);
        for (Map.Entry<String, String> entry : postData.entrySet()) {
            builder.addParameter(entry.getKey(), entry.getValue());
        }
        HttpUriRequest post = builder.build();

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = HttpClients.createDefault().execute(post)) {
            HttpEntity resEntity = response.getEntity();
            String body = EntityUtils.toString(resEntity, "UTF-8");
            Res resp = JSON.parseObject(body, Res.class);
            if (resp.getErrorCode() == null) {
                // 把返回中的sign和signType去除，然后用=号把值连接起来，接着用&符号拼接
                String unSign = "bContent=" + resp.getBContent() + "&charset=" + resp.getCharset() + "&format="
                        + resp.getFormat() + "&timestamp=" + resp.getTimestamp() + "&version=" + resp.getVersion();
                // 验证签名，使用HMB的公钥
                if (RSASignature.doCheck(unSign, resp.getSign(), RSAEncrypt.HMB_PUBLIC_KEY)) {
                    String decryptContent = RSAEncrypt.decryptByPrivateKey(resp.getBContent(), RSAEncrypt.DEV_PRIVATE_KEY);
                    BContent b = new Gson().fromJson(decryptContent, BContent.class);
                    log.info("用户[{}]调用海绵保接口成功，保单号[{}]", mobileNo, b.getPolicy_no());
                }
                return new FraudVerify(true, false, "");
            }
            log.debug("用户[{}]使用海绵保接口进行实名认证,错误码[{}],错误原因[{}]", mobileNo, resp.getErrorCode(), resp.getErrorMsg());
            /*
             * 20005  投保人身份证不正确
             * 20006   投保人电话不正确
             * 20012   投保人年龄不能小于16周岁或大于65岁
             * 20040.  姓名校验不通过,当证件类型为非护照时姓名允许录入的规则组合为：纯中文
             */
            if (resp.getErrorCode().equals("20005")) {
                return new FraudVerify(false, true, "身份证不正确");
            } else if (resp.getErrorCode().equals("20006")) {
                return new FraudVerify(false, true, "身份证与注册电话不匹配");
            } else if (resp.getErrorCode().equals("20040")) {
                if (StringUtils.containsAny(resp.getErrorMsg(), "姓名校验不通过", "不可包含数字"))
                    return new FraudVerify(false, true, "请输入真实姓名");
                    //20190725 海绵宝已停止合作;临时放行
                else return new FraudVerify(true, false, "");
            } else if (resp.getErrorCode().equals("20012") && resp.getErrorMsg().contains("投保人年龄不能小于16周岁或大于65岁")) {
                return new FraudVerify(false, true, "小于16周岁或大于65岁禁止使用该产品");
            } else {
                return new FraudVerify(false, true, "");
            }
        } catch (Exception e) {
            log.error("调用海绵宝接口实名认证异常:", e);
            return new FraudVerify(false, true, e.getMessage());
        }
    }


    private Map<String, String> post(BizContent dto) {
        Map<String, String> postData = new HashMap<>();
        DateFormat dateFormat = FormatUtil.yMdhmsS;
        String timestamp = dateFormat.format(DateUtils.addSeconds(new Date(), 20));// 502=保险起期,未超过产品规则最早生效的时间
        postData.put("appId", APPID);
        postData.put("serviceName", SERVICE_NAME);
        postData.put("timestamp", timestamp);
        postData.put("version", SDK_VER);
        postData.put("format", "Json");
        postData.put("charset", "UTF-8");
        //对入参进行处理
        String jsonContent = new Gson().toJson(dto.toMap());
        //基本信息加密
        String resContent;
        try {
            resContent = RSAEncrypt.encryptByPublicKey(jsonContent.getBytes("UTF-8"), RSAEncrypt.HMB_PUBLIC_KEY);
        } catch (Exception e) {
            return null;
        }
        postData.put("bContent", resContent);
        Map<String, String> sortMap = new TreeMap<>(String::compareTo);
        sortMap.putAll(postData);
        postData = sortMap;
        StringBuilder signBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : postData.entrySet()) {
            signBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String sign = signBuilder.toString();
        sign = sign.substring(0, sign.length() - 1);
        //用商户自己私钥进行签名
        sign = RSASignature.sign(sign, RSAEncrypt.DEV_PRIVATE_KEY, "UTF-8");
        postData.put("sign", sign);
        postData.put("signType", "RSA");
        //发送请求组装数据
        RequestBuilder builder = RequestBuilder.post(SURL).setConfig(Config.REQUEST_CONFIG);
        for (Map.Entry<String, String> entry : postData.entrySet()) {
            builder.addParameter(entry.getKey(), entry.getValue());
        }
        HttpUriRequest post = builder.build();

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = HttpClients.createDefault().execute(post)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity resEntity = response.getEntity();
                String body = EntityUtils.toString(resEntity, "UTF-8");
                Gson gson = new Gson();
                Res resp = gson.fromJson(body, Res.class);
                if (resp.getErrorCode() == null) {
                    // 把返回中的sign和signType去除，然后用=号把值连接起来，接着用&符号拼接
                    String unSign = "bContent=" + resp.getBContent() + "&charset=" + resp.getCharset() + "&format="
                            + resp.getFormat() + "&timestamp=" + resp.getTimestamp() + "&version=" + resp.getVersion();
                    // 验证签名，使用HMB的公钥
                    if (RSASignature.doCheck(unSign, resp.getSign(), RSAEncrypt.HMB_PUBLIC_KEY)) {
                        String decryptContent = RSAEncrypt.decryptByPrivateKey(resp.getBContent(), RSAEncrypt.DEV_PRIVATE_KEY);
                        BContent b = new Gson().fromJson(decryptContent, BContent.class);
                        Map<String, String> map = new HashMap<>();
                        map.put("policyNo", b.getPolicy_no());
                        map.put("orderSn", b.getOrder_sn());
                        log.info("用户[{}]调用海绵保接口成功，流水号[{}]，保单号[{}]", dto.getPolicyUserMobile(), dto.getBizOrderSn(), b.getPolicy_no());
                        return map;
                    }
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("errorCode", resp.getErrorCode());
                    map.put("errorMsg", resp.getErrorMsg());
                    log.debug("用户[{}]调用海绵保接口失败，错误码[{}]，错误原因[{}]", dto.getPolicyUserMobile(), resp.getErrorCode(), resp.getErrorMsg());
                    return map;
                }
            } else {
                log.error("调用海绵保接口网络连接失败");
            }
        } catch (Exception e) {
            log.error("调用海绵宝接口投保抛出异常:", e);
        }
        return null;
    }

    @Data
    private static class BizContent {
        private String productCode;
        private String bizCompanyCode;
        private String bizOrderSn;
        private String policyUserName;
        private String policyUserCerttype;
        private String policyUserCertNo;
        private String policyUserMobile;
        private String policyStartTime;
        private String policyEndTime;

        public Map<String, String> toMap() {
            Map<String, String> bzContent = new HashMap<>();
            bzContent.put("product_code", this.getProductCode());
            bzContent.put("biz_company_code", COMPANY_CODE);
            //订单号/流水号 唯一值
            bzContent.put("biz_order_sn", this.getBizOrderSn());
            bzContent.put("policy_user_name", this.getPolicyUserName());
            bzContent.put("policy_user_certtype", USER_CERTTYPE);
            bzContent.put("policy_user_certno", "" + this.getPolicyUserCertNo());
            bzContent.put("policy_user_mobile", this.getPolicyUserMobile());
            bzContent.put("policy_start_time", FormatUtil.yMdHms.format(new Date()));
            bzContent.put("policy_end_time", FormatUtil.y_M_d.format(new Date()) + " 23:59:59");
            return bzContent;
        }
    }

    @Data
    private static class BContent {
        private String policy_no;
        private String order_sn;
    }

    @Data
    private static class Res {
        private String bContent;
        private String charset;
        private String errorCode;
        private String errorMsg;
        private String format;
        private String sign;
        private String signType;
        private String timestamp;
        private String version;
    }

}
