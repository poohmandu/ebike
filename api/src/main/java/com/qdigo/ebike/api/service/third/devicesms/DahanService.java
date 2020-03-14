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

package com.qdigo.ebike.api.service.third.devicesms;

import com.qdigo.ebike.api.ApiRoute;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * description: 
 *
 * date: 2020/3/14 10:55 AM
 * @author niezhao
 */
@FeignClient(name = "third", contextId = "device-sms-dahan")
public interface DahanService {

    @PostMapping(ApiRoute.Third.DeviceSms.Dahan.httpSend)
    boolean httpSend(@RequestParam("simNo") Long simNo, @RequestParam("content") String content);

    @Data
    class Result {
        private Integer code;
        private String message;
        private List<ResultBean> data;

        @Data
        public static class ResultBean {
            private Data data;
            private Integer totalNum;

            @lombok.Data
            private static class Data {
                private String iccid;
                private String msisdn;
                private String smsId;
                private String smsContent;
                private String sendDate;
            }

        }
    }

}
