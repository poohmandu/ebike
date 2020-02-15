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

package com.qdigo.ebike.api.service.third.wxlite;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.common.core.errors.exception.NoRequestCtxException;
import com.qdigo.ebike.common.core.util.http.NetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Value;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 
 * date: 2019/12/25 4:14 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "third", contextId = "wxlite")
public interface WxliteService {

    Logger log = LoggerFactory.getLogger(WxliteService.class);

    default boolean isWxlite(String deviceId) {
        return EnumUtils.isValidEnum(WX.class, deviceId);
    }

    default Referer getReferer(String referer) {
        String between = StringUtils.substringBetween(referer, "https://servicewechat.com/", "/page-frame.html");
        String appId = StringUtils.substringBeforeLast(between, "/");
        String version = StringUtils.substringAfterLast(between, "/");
        return new Referer(appId, version);
    }

    default String getAppId(String deviceId) {
        HttpServletRequest request;
        try {
            request = NetUtil.getRequest();
        } catch (NoRequestCtxException e) {
            WX anEnum = EnumUtils.getEnum(WX.class, deviceId);
            if (anEnum == null) {
                return WX.weixin.getAppId();
            }
            return anEnum.getAppId();
        }
        String refererStr = request.getHeader("referer");
        if (refererStr == null) {
            log.warn("请求线程内没有referer请求头,appId使用默认的电滴");
            return WX.weixin.getAppId();
        }
        Referer referer = getReferer(refererStr);
        return referer.getAppId();
    }

    @PostMapping(ApiRoute.Third.Wxlite.getOpenId)
    LoginRes getOpenId(@RequestParam("postCode") String postCode, @RequestParam("appId") String appId);

    @PostMapping(ApiRoute.Third.Wxlite.getAccessToken)
    String getAccessToken();

    @Value
    class Referer {
        private String appId;
        private String wxliteVersion;
    }

    @Data
    class LoginRes {
        private String sessionKey;
        private String openId;
        private String unionId;
    }

    @Getter
    @AllArgsConstructor
    enum WX { // key为deviceId
        weixin("wx8b40d25493fc47e0", "827d18f28eaac51868f68c46c72f0faa"),
        weixin_zhima("wx5da5daffa0b2ec3a", "95852e714407bbf14115be535a1f44d2"); //支码
        private String appId;
        private String appSecret;
    }

}
