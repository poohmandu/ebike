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

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.third.sms.SmsService;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.http.NetUtil;
import com.qdigo.ebike.third.domain.entity.SmsRecord;
import com.qdigo.ebike.third.service.inner.SmsInnerService;
import com.qdigo.ebike.third.service.inner.SmsRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Description: 
 * date: 2020/1/20 6:45 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SmsServiceImpl implements SmsService {

    private final SmsInnerService smsInnerService;
    private final RedisTemplate<String, String> redisTemplate;
    private final SmsRecordService smsRecordService;

    @Override
    public String sendPinCodeSMS(String mobileNo, String countryCode) {
        return smsInnerService.sendPinCodeSMS(mobileNo, countryCode);
    }

    @Override
    public void sendInsuranceSms(String mobileNo, String policyNo, Long agentId) {
        String content = smsInnerService.sendInsuranceSms(mobileNo, policyNo);
        smsRecordService.insert(mobileNo, content, agentId, SmsRecord.Type.insurance);
    }

    @Override
    public void sendJointTipSms(String name, String jointMobile, String city, String jointType) {
        String ip = NetUtil.getRemoteIp();
        String key = Keys.flagJointSms.getKey(ip);
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(key, jointMobile);
        if (absent) {
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
            String content = smsInnerService.sendJointSms(name, jointMobile, city, jointType);
            smsRecordService.insert("13918789869", content, 1L, SmsRecord.Type.joint);
        }
    }

    @Override
    public void sendBatchSMS(Iterable<String> strings, String content) {
        smsInnerService.sendBatchSMS(strings, content);
    }

    @Override
    public Reports getReport(String mobileNo) {
        return smsInnerService.getReport(mobileNo);
    }
}
