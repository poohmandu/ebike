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

package com.qdigo.ebike.commonaop.aspects;

import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Constants;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by niezhao on 2017/6/6.
 */
@Slf4j
@Aspect
@Component
@Order(Const.AopOrder.AccessValidateAspect)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AccessAspect {

    private final RedisTemplate<String, String> redisTemplate;
    private final Environment env;

    @Pointcut("@annotation(com.qdigo.ebike.commonaop.annotations.AccessValidate)")
    public void accessPointcut() {
    }

    @Around("accessPointcut()")
    public Object accessAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final AccessValidate annotation = signature.getMethod().getAnnotation(AccessValidate.class);
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();
        String mobileNo = null;
        String accessToken = null;
        boolean dev_test = env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_DEVELOPMENT, Constants.SPRING_PROFILE_LOCAL, Constants.SPRING_PROFILE_TEST));
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(annotation.mobileNo())) {
                mobileNo = (String) args[i];
            }
            if (parameterNames[i].equals(annotation.accessToken())) {
                accessToken = (String) args[i];
            }
        }
        if (mobileNo == null || accessToken == null) {
            throw new RuntimeException("@AccessValidate注解的方法必须有字符串参数:" + annotation.mobileNo() + "," + annotation.accessToken());
        }
        if (validateAccessToken(mobileNo, accessToken) || dev_test) {
            return joinPoint.proceed();
        } else {
            return R.ok(405, "没有登录或者登录已过期,请先登录");
        }
    }


    public boolean validateAccessToken(String mobileNo, String accessToken) {
        if ("[object Undefined]".equals(mobileNo)) {
            return false;
        }
        String key = Keys.AccessToken.getKey(mobileNo);
        String tokenStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(tokenStr)) {
            return tokenStr.equals(accessToken.trim());
        } else {
            return false;
        }
    }

}
