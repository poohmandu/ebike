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

package com.qdigo.ebike.third.service.inner;


import com.alibaba.fastjson.JSON;
import com.dahantc.api.sms.json.JSONHttpClient;
import com.dahantc.api.sms.json.SmsData;
import com.google.common.collect.Lists;
import com.qdigo.ebike.api.service.third.sms.SmsService;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * date: 2019/12/11 3:50 PM
 *
 * @author niezhao
 * @since JDK 1.8
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SmsInnerService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.pinCode.expireInSeconds}")
    private int pinCodeExpireInSeconds;

    @Value("${spring.smsGateway.account}")
    private String smsGatewayAccount;

    @Value("${spring.smsGateway.password}")
    private String smsGatewayPassword;

    @Value("${spring.smsGateway.sign}")
    private String smsGatewaySign;


    private final static String PIN_CODE = "您本次请求的验证码为:{0},该验证码将在60秒后失效, 谢谢您的使用!";
    private final static String INSURANCE = "尊敬的客户,您已付款的保单已成功承保，保单号为{0}，您可登陆我们的官网 http://dwz.cn/pa2015 查询保单详情。";
    private final static String JOINT = "您好,客户{0},向公司提出加盟意向申请,手机号{1},加盟城市为{2},加盟类型为{3},请及时联系。";

    private String getTotalMobileNo(String mobileNo, String countryCode) {
        if (StringUtils.isEmpty(countryCode) || "86".equals(countryCode)) {
            return mobileNo;
        } else {
            return "+" + countryCode + mobileNo;
        }
    }

    public String sendPinCodeSMS(String mobileNo, String countryCode) {
        String pinCode = SecurityUtil.generatePinCode();
        String totalMobileNo = this.getTotalMobileNo(mobileNo, countryCode);
        //调用短信网关
        String content = MessageFormat.format(PIN_CODE, pinCode);
        this.sendSMS(totalMobileNo, content);

        //翻入缓冲中
        String key = Keys.PinCode.getKey(mobileNo);
        redisTemplate.opsForValue().set(key, pinCode, pinCodeExpireInSeconds, TimeUnit.SECONDS);
        log.debug("redis里用户{}的验证码为{}", mobileNo, redisTemplate.opsForValue().get(key));

        return pinCode;
    }

    public String sendInsuranceSms(String mobileNo, String policyNo) {
        String content = MessageFormat.format(INSURANCE, policyNo);
        this.sendSMS(mobileNo, content);

        log.debug("{},给用户发送保险信息:{}", mobileNo, content);
        return content;

    }

    public String sendJointSms(String name, String jointMobile, String city, String jointType) {
        String content = MessageFormat.format(JOINT, name, jointMobile, city, jointType);
        this.sendSMS("13918789869", content);
        log.debug("给业务人员发送加盟信息提醒:{}", content);
        return content;
    }

    public void sendBatchSMS(Iterable<String> strings, String content) {
        try {
            JSONHttpClient jsonHttpClient = new JSONHttpClient("http://www.dh3t.com");
            List<SmsData> list = Lists.newArrayList();
            strings.forEach(s -> {
                final SmsData smsData = new SmsData();
                smsData.setContent(content);
                smsData.setMsgid("");
                smsData.setPhones(s);
                smsData.setSendtime("");
                smsData.setSign(smsGatewaySign);
                smsData.setSubcode("");
                list.add(smsData);
            });
            jsonHttpClient.sendBatchSms(smsGatewayAccount, smsGatewayPassword, list);
        } catch (Exception e) {
            log.debug("发送短信失败:{}", e.getMessage());
        }
    }

    public SmsService.Reports getReport(String mobileNo) {
        try {
            JSONHttpClient jsonHttpClient = new JSONHttpClient("http://www.dh3t.com");
            final String reports = jsonHttpClient.getReport(smsGatewayAccount, smsGatewayPassword, "", mobileNo);
            return JSON.parseObject(reports, SmsService.Reports.class);
        } catch (Exception e) {
            log.debug("获取report失败:{}", e.getMessage());
            return null;
        }
    }

    private String sendSMS(String mobileNo, String content) {
        String sendResponse = "";
        try {
            JSONHttpClient jsonHttpClient = new JSONHttpClient("http://www.dh3t.com");
            jsonHttpClient.setRetryCount(1);
            String msg_id = SecurityUtil.randomNum();
            sendResponse = jsonHttpClient.sendSms(smsGatewayAccount, smsGatewayPassword, mobileNo, content, smsGatewaySign, "", msg_id);
            log.debug("大汉三通短信服务sendResponse:{}", sendResponse);
        } catch (Exception e) {
            log.debug("发送短信失败:{}", e.getMessage());
        }
        log.debug("向{}用户发送短信:{}", mobileNo, content);
        return sendResponse;
    }
}
