/*
 * Copyright 2020 聂钊 nz@qdigo.com
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

package com.qdigo.ebike.common.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 高性能的属性复制
 *
 *  名称相同而类型不同的属性不会被拷贝,即使源类型是原始类型(int, short和char等)，目标类型是其包装类型(Integer, Short和Character等)
 *  property少，写起来也不麻烦，就直接用传统的getter/setter，性能最好
 *  property多，转换不频繁，那就省点事吧，使用BeanUtils.copyProperties
 *  property多，转换很频繁，为性能考虑，使用net.sf.cglib.beans.BeanCopier.BeanCopier，性能近乎getter/setter。
 *
 * date: 2020/1/2 6:23 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
public final class ConvertUtil {

    /**
     * BeanCopier的缓存
     */
    private static final ConcurrentHashMap<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();
    private static Lock initLock = new ReentrantLock();

    /**
     * @author niezhao
     * @description 获取BeanCopier
     *
     * @date 2020/1/2 6:38 PM
     * @param source
     * @param target
     * @return org.springframework.cglib.beans.BeanCopier
     **/
    private static BeanCopier getBeanCopier(Class source, Class target) {
        return getBeanCopier(source, target, false);
    }

    public static BeanCopier getBeanCopier(Class source, Class target, boolean useConverter) {
        String key = genKey(source, target);
        BeanCopier beanCopier;
        initLock.lock();
        if (BEAN_COPIER_CACHE.containsKey(key)) {
            beanCopier = BEAN_COPIER_CACHE.get(key);
            initLock.unlock();
            return beanCopier;
        }
        beanCopier = BeanCopier.create(source, target, useConverter);
        BEAN_COPIER_CACHE.put(key, beanCopier);
        initLock.unlock();
        return beanCopier;
    }

    /**
     * @author niezhao
     * @description BeanCopier的copy（浅复制，字段名&类型相同则被复制）
     *
     * @date 2020/1/2 6:39 PM
     * @param source
     * @param targetClass
     * @return T
     **/
    public static <T> T to(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        BeanCopier beanCopier = getBeanCopier(source.getClass(), targetClass);
        try {
            T target = targetClass.newInstance();
            beanCopier.copy(source, target, null);
            return target;
        } catch (Exception e) {
            log.error("对象拷贝失败,{}", e);
            throw new RuntimeException("对象拷贝失败:" + source + "_" + targetClass);
        }
    }

    /**
     * @author niezhao
     * @description BeanCopier的copy（浅复制，字段名&类型相同则被复制）
     *
     * @date 2020/1/2 6:47 PM
     * @param source
     * @param targetClass
     * @return java.util.List<E>
     *
     **/
    @SuppressWarnings("unchecked")
    public static <E, T> List<E> to(List<T> source, Class<E> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            if (source.isEmpty()) {
                try {
                    return source.getClass().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.debug("实例化失败:", e);
                    return new ArrayList<>();
                }
            }
            List<E> result;
            try {
                result = source.getClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                log.debug("实例化失败:", e);
                result = new ArrayList<>();
            }
            for (Object each : source) {
                result.add(to(each, targetClass));
            }
            return result;
        } catch (Exception e) {
            log.error("对象队列拷贝失败,{}", e);
            throw new RuntimeException("对象队列拷贝失败:" + source + "_" + targetClass);
        }
    }

    /**
     * 生成key
     * @param srcClazz 源文件的class
     * @param tgtClazz 目标文件的class
     * @return string
     */
    private static String genKey(Class<?> srcClazz, Class<?> tgtClazz) {
        return srcClazz.getName() + "_" + tgtClazz.getName();
    }


}
