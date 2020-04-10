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

package com.qdigo.ebike.api.service.user;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 
 * date: 2020/1/24 11:09 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "user-center", contextId = "user-account")
public interface UserAccountService {

    @PostMapping(ApiRoute.UserCenter.UserAccount.findById)
    UserAccountDto findById(@RequestParam("id") Long userAccountId);

    @PostMapping(ApiRoute.UserCenter.UserAccount.findByMobileNo)
    UserAccountDto findByMobileNo(@RequestParam("mobileNo") String mobileNo);

    @PostMapping(ApiRoute.UserCenter.UserAccount.findByUserId)
    UserAccountDto findByUserId(@RequestParam("userId") long userId);

    @PostMapping(ApiRoute.UserCenter.UserAccount.update)
    void update(@RequestBody UserAccountDto userAccountDto);

}
