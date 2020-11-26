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

package com.qdigo.ebike.api.service.third.sms;

import com.qdigo.ebike.api.ApiRoute;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * date: 2019/12/11 3:09 PM
 *
 * @author niezhao
 * @since JDK 1.8
 */
@FeignClient(name = "third", contextId = "sms")
public interface SmsService {

    /**
     * @param mobileNo
     * @param countryCode
     * @return java.lang.String
     * @author niezhao
     * @description 发送验证码短信
     * @date 2019/12/11 9:34 PM
     **/
    @PostMapping(ApiRoute.Third.Sms.pincode)
    String sendPinCodeSMS(@RequestParam("mobileNo") String mobileNo,
                          @RequestParam("countryCode") String countryCode);


    /**
     * @param mobileNo
     * @param policyNo
     * @return java.lang.String
     * @author niezhao
     * @description 给用户发送投保信息
     * @date 2019/12/11 9:35 PM
     **/
    @PostMapping(ApiRoute.Third.Sms.insurance)
    void sendInsuranceSms(@RequestParam("mobileNo") String mobileNo, @RequestParam("policyNo") String policyNo,
                          @RequestParam(value = "agentId", required = false) Long agentId);

    /**
     * @param name
     * @param jointMobile
     * @param city
     * @param jointType
     * @return java.lang.String
     * @author niezhao
     * @description 加盟提醒
     * @date 2019/12/12 10:41 AM
     **/
    @PostMapping(ApiRoute.Third.Sms.joint)
    void sendJointTipSms(@RequestParam("name") String name, @RequestParam("jointMobile") String jointMobile,
                         @RequestParam("city") String city, @RequestParam("jointType") String jointType);

    /**
     * @param strings
     * @param content
     * @return void
     * @author niezhao
     * @description 批量发送
     * @date 2019/12/12 10:41 AM
     **/
    @PostMapping(ApiRoute.Third.Sms.batch)
    void sendBatchSMS(@RequestBody Iterable<String> strings, @RequestParam("content") String content);

    /**
     * @param mobileNo
     * @return com.qdigo.ebike.api.service.third.sms.SmsApi.Reports
     * @author niezhao
     * @description 发送报告
     * @date 2019/12/12 10:42 AM
     **/
    @PostMapping(ApiRoute.Third.Sms.report)
    Reports getReport(@RequestParam("mobileNo") String mobileNo);

    @Data
    class Reports {
        private int result;
        private String desc;
        private List<Report> reports;

        @Data
        public static class Report {
            private String msgId;
            private String phone;
            private int status;
            private String desc;
            private String wgcode;
            private Date time;
            private int smsCount;
            private int smsIndex;
        }
    }

}
