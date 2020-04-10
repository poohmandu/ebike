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
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.commonaop.annotations.Token;
import com.qdigo.ebike.commonaop.constants.DB;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2017/6/1.
 */
@Slf4j
@Aspect
@Component
@Order(Const.AopOrder.TokenAspect)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TokenAspect {

    private final RedisTemplate<String, String> redisTemplate;

    private static final ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> tokenMap = new ConcurrentHashMap<>();
    private static final long MethodMaxTime = 5000;

    private static final String lockToken = "aop:token";

    @Pointcut("@annotation(com.qdigo.ebike.commonaop.annotations.Token)")
    public void tokenPointcut() {
    }

    @Around("tokenPointcut()")
    public Object tokenAround(ProceedingJoinPoint joinPoint) throws Throwable {
        val signature = (MethodSignature) joinPoint.getSignature();
        Token token = signature.getMethod().getAnnotation(Token.class);
        String key = this.getKey(signature, joinPoint, token);
        boolean hasTokenKey = false;
        try {
            synchronized (tokenMap) {
                Long keyVal;
                hasTokenKey = ((keyVal = tokenMap.get(key)) != null &&
                        (System.currentTimeMillis() - keyVal < MethodMaxTime)); //防止前面的程序死循环后面的还要等待
                if (!hasTokenKey)
                    tokenMap.put(key, System.currentTimeMillis());
            }
            if (hasTokenKey) {
                return this.fastErr(key, token, signature, "synchronized本地同步锁");
            }
            if (!tryLock(key, token)) {
                return this.fastErr(key, token, signature, "分布式锁");
            }
            try {
                if (token.DB() != DB.Memory) {
                    log.debug("{}没有重复提交", key);
                }
                return joinPoint.proceed();
            } finally {
                this.unlock(key, token);
            }
        } finally {
            if (!hasTokenKey) {
                tokenMap.remove(key);
            }
        }
    }

    private Object fastErr(String key, Token token, MethodSignature signature, String cause) {
        Class returnType = signature.getReturnType();
        log.debug("{}请求重复提交,ttl:{}s,快速失败原因:{}", key, this.getExpireSeconds(key, token), cause);
        if (returnType == R.class) {
            return R.ok(501, "请求处理中,请耐心等待,勿重复提交");
        } else if (returnType == ResponseEntity.class) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(R.ok(501, "请求处理中,请耐心等待,勿重复提交"));
        } else if (returnType == ResponseDTO.class) {
            return new ResponseDTO<>(501, "请求处理中,请耐心等待,勿重复提交");
        } else if ("void".equals(returnType.getName()) || Void.class == returnType) {
            return null;
        } else if (returnType == String.class) {
            return MessageFormat.format("{0}重复进入了方法{1}内", key, signature.toShortString());
        } else if ("boolean".equals(returnType.getName()) || Boolean.class == returnType) {
            return false;
        } else {
            try {
                return returnType.newInstance();
            } catch (Exception e) {
                log.error("无法实例化{}:{}", returnType.getName(), e.getMessage());
                return null;
            }
        }
    }

    private String getKey(MethodSignature signature, JoinPoint joinPoint, Token token) throws IllegalAccessException {
        String[] keyNames = token.key();
        String[] parameterNames = signature.getParameterNames();//形参名
        Object[] args = joinPoint.getArgs(); //实参

        StringBuilder builder = new StringBuilder(lockToken).append(":").append(signature.toShortString());
        String init = builder.toString();

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
            throw new RuntimeException("@Token注解的方法必须有字符串参数:" + Arrays.toString(keyNames));
        }
        return string;
    }

    /**
     * 尝试持有锁
     *
     * @param key
     * @param token
     * @return
     */
    private boolean tryLock(String key, Token token) {
        if (token.DB() == DB.Redis) {
            return redisTemplate.execute(new SessionCallback<Boolean>() {
                @Override
                public Boolean execute(RedisOperations operations) throws DataAccessException {
                    val ops = redisTemplate.opsForValue();
                    try {
                        redisTemplate.watch(key);
                        String val = ops.get(key);
                        boolean bln;
                        if (bln = (val == null)) {
                            redisTemplate.multi();
                            ops.set(key, FormatUtil.getCurTime(), token.expireSeconds(), TimeUnit.SECONDS);
                            List<Object> exec = redisTemplate.exec();//:为事务被修改
                            log.debug("ToKenAspect里获取事务结果为:{}", exec == null ? null : exec.size());
                            //null是watch-key被外部修改
                            if (exec == null) {
                                bln = false;
                            }
                        }
                        return bln;
                    } finally {
                        redisTemplate.unwatch();
                    }
                }
            });
        } else if (token.DB() == DB.Memory) {
            synchronized (map) {
                Long value = map.get(key);
                if (value != null) {
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - value);
                    if (seconds < token.expireSeconds()) {
                        return false;
                    } else {
                        map.put(key, System.currentTimeMillis());
                        return true;
                    }
                } else {
                    map.put(key, System.currentTimeMillis());
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    private void unlock(String key, Token token) {
        if (token.DB() == DB.Redis) {
            redisTemplate.delete(key);
        } else if (token.DB() == DB.Memory) {
            map.remove(key);
        } else {
            //ignore
        }
    }


    private long getExpireSeconds(String key, Token token) {
        if (token.DB() == DB.Redis) {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } else if (token.DB() == DB.Memory) {
            Long value = map.getOrDefault(key, System.currentTimeMillis());
            return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - value);
        } else {
            return 0;
        }
    }


    @AfterThrowing(pointcut = "tokenPointcut()", throwing = "e")
    public void tokenAfterThrowing(JoinPoint joinPoint, Throwable e) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.error("TokenAspect捕获到{}抛出的异常为:{}", signature.toShortString(), e.getMessage());
        //Token annotation = signature.getMethod().getAnnotation(Token.class);

        //val keyName = annotation.key();
        //try {
        //    val key = this.getKey(signature, joinPoint, annotation);
        //    this.deleteKey(key, annotation);
        //} catch (IllegalAccessException e1) {
        //    log.error("获取Token的key失败:", e1);
        //}
    }


}


