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

package com.qdigo.ebike.controlcenter.config;

import com.qdigo.ebike.commonconfig.configuration.properties.QdigoAsyncProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * description: 
 *
 * date: 2020/3/6 10:13 AM
 * @author niezhao
 */
@Configuration
public class RequestAsyncPoolConfig implements WebMvcConfigurer {

    @Resource
    private WebMvcProperties mvcProperties;
    @Resource
    private QdigoAsyncProperties asyncProperties;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        Duration timeout = this.mvcProperties.getAsync().getRequestTimeout();
        if (timeout != null) {
            configurer.setDefaultTimeout(timeout.toMillis());
        }
        configurer.registerCallableInterceptors(new TimeoutCallableInterceptor(), new CallableProcessInterceptor());
        configurer.setTaskExecutor(requestAsyncThreadPoolTaskExecutor());
    }

    @Bean(name = "requestAsyncExecutor")
    public ThreadPoolTaskExecutor requestAsyncThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperties.getCorePoolSize());
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());
        executor.setThreadNamePrefix("req-async-");
        executor.afterPropertiesSet();
        return executor;
    }


}
