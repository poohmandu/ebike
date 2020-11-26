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
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * description: 
 *
 * date: 2020/3/17 6:21 PM
 * @author niezhao
 */
@FeignClient(name = "user-center", contextId = "user-record")
public interface UserRecordService {

    @PostMapping(ApiRoute.UserCenter.UserRecord.insertUserRecord)
    void insertUserRecord(@RequestParam("userId") Long userId, @RequestParam("record") String record);

}
