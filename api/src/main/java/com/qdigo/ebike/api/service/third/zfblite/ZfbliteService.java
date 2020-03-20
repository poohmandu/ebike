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

package com.qdigo.ebike.api.service.third.zfblite;

import com.qdigo.ebike.api.ApiRoute;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 
 * date: 2019/12/26 4:07 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "third", contextId = "zfblite")
public interface ZfbliteService {

    @PostMapping(ApiRoute.Third.Zfblite.getOpenId)
    LoginRes getOpenId(@RequestParam("deviceId") String postCode);


    @Data
    @Accessors(chain = true)
    class LoginRes {
        private String accessToken;
        private String alipayUserId;
        private String expiresIn;
        private String reExpiresIn;
        private String refreshToken;
        private String userId;
    }

}