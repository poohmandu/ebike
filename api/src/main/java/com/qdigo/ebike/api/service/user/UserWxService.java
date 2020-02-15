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
import com.qdigo.ebike.api.service.third.wxlite.WxliteService;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoneMatchException;
import lombok.Data;
import lombok.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 
 * date: 2020/1/6 2:43 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "user-center", contextId = "user-wx")
public interface UserWxService {

    @PostMapping(ApiRoute.UserCenter.UserWx.findByWxId)
    WxOpenInfoDto findByWxId(@RequestParam("appId") String appId, @RequestParam("openId") String openId,
                             @RequestParam("unionId") String unionId) throws NoneMatchException;

    @PostMapping(ApiRoute.UserCenter.UserWx.saveUserWxOpenInfo)
    void saveUserWxOpenInfo(@RequestParam("userId") long userId, @RequestParam("appId") String appId,
                            @RequestParam("openId") String openId, @RequestParam("unionId") String unionId,
                            @RequestParam("version") String version);

    @PostMapping(ApiRoute.UserCenter.UserWx.updateUserWxliteInfo)
    void updateUserWxliteInfo(@RequestBody Param param);

    @PostMapping(ApiRoute.UserCenter.UserWx.findByMobileNo)
    WxInfoDto findByMobileNo(@RequestParam("mobileNo") String mobileNo);


    @Data
    class WxOpenInfoDto {
        private long id;
        private long userId;
        private String appId;
        private String openId;
        private String unionId;
        private String version;
    }

    @Data
    class WxInfoDto {
        private String mobileNo;
        private String sdkVersion = "";
        private String version = "";
        private String platform = "";
        private String system = "";
        private String brand = "";
        private String model = "";
        private String pixelRatio = "";
        private String language = "";
        private String fontSizeSetting = "";
        private String wxliteVersion;
        private String appId;
    }

    @Value
    class Param {
        private WxInfoDto wxInfoDto;
        private WxliteService.Referer referer;
    }
}
