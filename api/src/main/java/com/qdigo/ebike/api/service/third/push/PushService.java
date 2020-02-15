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

package com.qdigo.ebike.api.service.third.push;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.common.core.constants.Const;
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Description: 
 * date: 2020/2/14 11:08 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "third", contextId = "push")
public interface PushService {

    @PostMapping(ApiRoute.Third.Push.pushTimeNotation)
    boolean pushTimeNotation(@RequestBody TimeParam timeParam);

    @PostMapping(ApiRoute.Third.Push.pushNotation)
    boolean pushNotation(@RequestBody Param param);

    @Data
    @Builder
    class TimeParam {
        private long timeMinutes;
        private Param param;
    }

    @Data
    @Builder
    class Param {
        private String mobileNo;
        private String deviceId;
        private String alert;
        private Const.PushType pushType;
        private Object data;
    }

}
