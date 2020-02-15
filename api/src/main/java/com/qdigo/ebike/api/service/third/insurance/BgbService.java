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

package com.qdigo.ebike.api.service.third.insurance;

import com.alibaba.fastjson.JSONObject;
import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.third.FraudVerify;
import com.qdigo.ebike.api.domain.dto.third.insurance.InsuranceParam;
import com.qdigo.ebike.api.domain.dto.third.insurance.InsuranceRecordDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 
 * date: 2020/1/9 11:50 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "third", contextId = "bgb")
public interface BgbService {

    @PostMapping(ApiRoute.Third.Bgb.insure)
    InsuranceRecordDto insure(@RequestBody InsuranceParam param);

    @PostMapping(ApiRoute.Third.Bgb.identifyIdCard)
    FraudVerify identifyIdCard(@RequestParam("mobileNo") String mobileNo, @RequestParam("name") String name,
                               @RequestParam("certNo") String certNo);

    @PostMapping(ApiRoute.Third.Bgb.policyQuery)
    JSONObject policyQuery(@RequestParam("unlockTime") String unlockTime, @RequestParam("orderSn") String orderSn);

}
