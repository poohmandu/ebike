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

package com.qdigo.ebike.third.controller.wxlite;

import com.alibaba.fastjson.JSON;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.util.security.SecurityUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Description: 
 * date: 2020/1/6 11:57 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/wxlite")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DecryptData {

    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping(value = "/decryptData", produces = MediaType.APPLICATION_JSON_VALUE)
    public R decryptData(@RequestBody Body body) {

        val sessionKey = redisTemplate.opsForValue().get(Keys.wxliteSessionKey.getKey(body.getOpenId()));

        val decrypt = SecurityUtil.AesCbcDecrypt(body.getEncryptedData(), sessionKey, body.getIv(), "UTF-8");

        return R.ok(200, "获得解密数据", JSON.parse(decrypt));
    }

    @Data
    private static class Body {
        private String openId;
        private String encryptedData;
        private String iv;
    }

}

