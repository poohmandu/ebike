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
import com.qdigo.ebike.common.core.constants.Status;
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 
 * date: 2020/1/25 12:34 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "user-center", contextId = "user-status")
public interface UserStatusService {

    @PostMapping(ApiRoute.UserCenter.UserStatus.getUserWxscoreEnable)
    Boolean getUserWxscoreEnable(@RequestParam("mobileNo") String mobileNo);

    @PostMapping(ApiRoute.UserCenter.UserStatus.getStep)
    Status.Step getStep(@RequestBody StepParam param);

    @PostMapping(ApiRoute.UserCenter.UserStatus.hasNoFinishedWxscore)
    String hasNoFinishedWxscore(@RequestBody UserDto userDto);

    @Data
    @Builder
    class StepParam {
        private UserDto userDto;
        private UserAccountDto userAccountDto;
    }

}
