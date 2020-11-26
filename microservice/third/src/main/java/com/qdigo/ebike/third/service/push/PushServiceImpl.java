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

package com.qdigo.ebike.third.service.push;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.third.push.PushService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Constants;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.third.service.push.wxpush.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.redis.core.RedisTemplate;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Description: 
 * date: 2020/2/14 12:42 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PushServiceImpl implements PushService {

    private final Environment env;
    private final RedisTemplate<String, String> redisTemplate;
    private final WebSocketService webSocketService;

    private boolean isDev() {
        return env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_DEVELOPMENT));
    }

    @Override
    public String pushWarn(WarnParam warnParam) {
        if (this.isDev()) {
            return null;
        }
        return JPush.opsPushNotations(warnParam.getMobiles(), warnParam.getAlert(),
                Const.PushType.warn, warnParam.getData())
                .map(pushResult -> String.valueOf(pushResult.msg_id))
                .orElse(null);
    }

    @Override
    public boolean pushTimeNotation(TimeParam timeParam) {
        Param param = timeParam.getParam();
        if (this.isDev()) {
            return false;
        }
        String key = Keys.lockPush.getKey(param.getMobileNo(), param.getPushType().name());
        if (redisTemplate.hasKey(key)) {
            return false;
        }
        redisTemplate.opsForValue().set(key, FormatUtil.getCurTime(), timeParam.getTimeMinutes(), TimeUnit.MINUTES);
        return this.pushNotation(param);
    }

    @Override
    public boolean pushNotation(Param param) {
        if (this.isDev()) {
            return false;
        }
        try {
            val mobileNo = param.getMobileNo();
            if (Const.wxlite.equals(param.getDeviceId())) {
                webSocketService.pushMessage(mobileNo, param.getAlert(), param.getPushType(), param.getData());
                return true;
            } else {
                return JPush.pushNotation(mobileNo, param.getAlert(), param.getPushType(), param.getData()).isPresent();
            }
        } catch (Exception e) {
            log.error("推送时抛出异常:{}", e.getMessage());
            return false;
        }
    }
}
