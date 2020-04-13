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
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Description: 
 * date: 2020/1/6 2:03 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "user-center", contextId = "user")
public interface UserService {

    @PostMapping(ApiRoute.UserCenter.User.findByMobileNo)
    UserDto findByMobileNo(@RequestParam("mobileNo") String mobileNo);

    @PostMapping(ApiRoute.UserCenter.User.findById)
    UserDto findById(@RequestParam("userId") Long userId);

    @PostMapping(ApiRoute.UserCenter.User.getOpenInfo)
    List<OpenInfo> getOpenInfo(@RequestBody UserDto userDto);

    @PostMapping(ApiRoute.UserCenter.User.findWithAccountByMobileNo)
    UserAndAccount findWithAccountByMobileNo(@RequestParam("mobileNo") String mobileNo);

    @Data
    class OpenInfo {
        private String appId;
        private String openId;
    }

    @Data
    @Accessors(chain = true)
    class UserAndAccount {
        private UserDto userDto;
        private UserAccountDto accountDto;
    }
}
