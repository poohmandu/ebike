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

package com.qdigo.ebike.commonaop.aspects;

import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.util.Ctx;
import com.qdigo.ebike.commonaop.annotations.ThreadCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;

@Component
@Aspect
@Slf4j
@Order(Const.AopOrder.ThreadCache)
public class CacheAspect {

    // 建议只用在web请求环境
    @Around("@annotation(cache)")
    public Object threadCacheAround(ProceedingJoinPoint joinPoint, ThreadCache cache) throws Throwable {
        String key = getKey(joinPoint, cache);
        //一定要Ctx在请求结束时会清除
        Object o = Ctx.get(key);
        if (o != null) {
            log.debug("缓存的key为:{}", key);
            return o;
        }
        Object result = joinPoint.proceed();
        Ctx.put(key, result);
        return result;
    }

    private String getKey(ProceedingJoinPoint joinPoint, ThreadCache threadCache) throws IllegalAccessException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] keyNames = threadCache.key();
        String[] parameterNames = signature.getParameterNames();//形参名
        Object[] args = joinPoint.getArgs(); //实参
        StringBuilder builder = new StringBuilder("cache:").append(signature.toShortString());
        String init = builder.toString();
        if (args.length == 0) {
            return init;
        }
        for (String keyName : keyNames) {
            for (int i = 0; i < parameterNames.length; i++) {
                if (keyName.equals(parameterNames[i])) {
                    if (args[i] instanceof String || args[i] instanceof Number) {
                        builder.append(":").append(args[i]);
                        break;
                    }
                } else {
                    if (args[i] instanceof Object && !(args[i] instanceof String)) {
                        Class clazz = args[i].getClass();
                        Field[] fields = clazz.getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            if (keyName.equals(field.getName())) {
                                Object o = field.get(args[i]);
                                if (o instanceof String || o instanceof Number) {
                                    builder.append(":").append(o);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        String string = builder.toString();
        if (init.equals(string)) {
            log.warn("@Cache注解的方法必须要有参数:{},参数值为:{}", Arrays.toString(keyNames), Arrays.toString(args));
        }
        return string;
    }

}
