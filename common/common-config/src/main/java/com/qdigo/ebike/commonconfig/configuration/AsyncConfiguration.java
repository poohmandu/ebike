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

package com.qdigo.ebike.commonconfig.configuration;

import com.qdigo.ebike.commonconfig.configuration.properties.QdigoAsyncProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@Slf4j
@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfiguration implements AsyncConfigurer {

    @Resource
    private QdigoAsyncProperties asyncProperties;

    @Override
    @Bean(name = "taskExecutor")
    @ConditionalOnMissingBean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        log.debug("创建一个异步任务执行器");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperties.getCorePoolSize());
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());
        executor.setThreadNamePrefix("qdigo-async-");
        executor.afterPropertiesSet();
        return new ExceptionHandlingAsyncTaskExecutor(executor);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            if (log.isErrorEnabled()) {
                log.error("在异步调用过程中发生未捕获异常: " + method, ex);
            }
        };
    }

    @Slf4j
    public static class ExceptionHandlingAsyncTaskExecutor implements AsyncTaskExecutor, InitializingBean, DisposableBean {

        private final AsyncTaskExecutor executor;

        public ExceptionHandlingAsyncTaskExecutor(AsyncTaskExecutor executor) {
            this.executor = executor;
        }

        @Override
        public void execute(Runnable task) {
            executor.execute(createWrappedRunnable(task));
        }

        @Override
        public void execute(Runnable task, long startTimeout) {
            executor.execute(createWrappedRunnable(task), startTimeout);
        }

        private <T> Callable<T> createCallable(final Callable<T> task) {
            return () -> {
                try {
                    return task.call();
                } catch (Exception e) {
                    handle(e);
                    throw e;
                }
            };
        }

        private Runnable createWrappedRunnable(final Runnable task) {
            return () -> {
                try {
                    task.run();
                } catch (Exception e) {
                    handle(e);
                }
            };
        }

        protected void handle(Exception e) {
            log.error("捕获到异步异常:", e);
        }

        @Override
        public Future<?> submit(Runnable task) {
            return executor.submit(createWrappedRunnable(task));
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return executor.submit(createCallable(task));
        }

        @Override
        public void destroy() throws Exception {
            if (executor instanceof DisposableBean) {
                DisposableBean bean = (DisposableBean) executor;
                bean.destroy();
            }
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            if (executor instanceof InitializingBean) {
                InitializingBean bean = (InitializingBean) executor;
                bean.afterPropertiesSet();
            }
        }
    }


}
