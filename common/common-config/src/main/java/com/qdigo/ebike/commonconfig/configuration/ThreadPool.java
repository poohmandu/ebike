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

package com.qdigo.ebike.commonconfig.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class ThreadPool {

    private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();//控制任务数量,否则oom

    private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(
        Runtime.getRuntime().availableProcessors() * 2);

    private static final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public static ExecutorService cachedThreadPool() {
        return cachedThreadPool;
    }

    public static ScheduledExecutorService scheduledThreadPool() {
        return scheduledThreadPool;
    }

    public static ExecutorService fixedThreadPool() {
        return fixedThreadPool;
    }

}
