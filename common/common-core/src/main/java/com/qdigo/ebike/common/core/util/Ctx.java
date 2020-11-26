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

package com.qdigo.ebike.common.core.util;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class Ctx {

    private static final String now = "now";
    private static final String uri = "uri";
    private static final String curTime = "curTime";

    private static final ThreadLocal<Map<String, Object>> ctx = ThreadLocal.withInitial(HashMap::new);

    public static void put(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (value == null) {
            return;
        }
        Map<String, Object> map = ctx.get();
        if (map == null) {
            map = new ConcurrentHashMap<>();
            ctx.set(map);
        }
        map.put(key, value);
    }

    public static <T> T get(String key) {
        return get(key, () -> null);
    }

    //TODO  此处应该锁住(对数据有put) synchronized
    public static <T> T get(String key, Supplier<T> supplier) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, Object> map = ctx.get();
        if (map == null || map.get(key) == null) {
            T val = supplier.get();
            if (val == null) {
                return null;
            } else {
                put(key, val);
                return val;
            }
        } else {
            if (map.get("init") == null) {
                //在未初始化的环境
                return supplier.get();
            }
            return (T) map.get(key);
        }
    }

    public static void clear() {
        Map<String, Object> map = ctx.get();
        if (map != null) {
            map.clear();
            ctx.remove();
        }
    }

    public static void init(String uri) {
        put(now, System.currentTimeMillis());
        put(Ctx.uri, uri);
        put("init", true);
    }

    public static Long now() {
        return get(now, System::currentTimeMillis);
    }

    public static String uri() {
        return get(uri);
    }

    public static Map<String, Object> timerMap(String step) {
        String subUri = get(uri, () -> null) + ":" + step;
        Long cur = get(curTime, Ctx::now);
        Long now = System.currentTimeMillis();
        Long duration = now - cur;
        put(curTime, now); //更新游标
        return ImmutableMap.of("duration", duration, "uri", uri);
    }

}
