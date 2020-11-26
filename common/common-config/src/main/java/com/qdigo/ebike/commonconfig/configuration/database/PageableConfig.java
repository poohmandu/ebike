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

package com.qdigo.ebike.commonconfig.configuration.database;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

import javax.annotation.Resource;

/**
 * Description: https://stackoverflow.com/questions/52120070/spring-data-web-pageable-one-indexed-parameters-true-does-not-work
 * date: 2020/1/22 9:13 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Configuration
@AutoConfigureBefore(RepositoryRestMvcAutoConfiguration.class)
public class PageableConfig {

    @Resource
    private SpringDataWebProperties properties;

    // web的page与repository的page会混乱
    @Bean
    PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
        return (resolver) -> {
            SpringDataWebProperties.Pageable pageable = this.properties.getPageable();
            resolver.setPageParameterName(pageable.getPageParameter());
            resolver.setSizeParameterName(pageable.getSizeParameter());
            resolver.setOneIndexedParameters(pageable.isOneIndexedParameters());
            resolver.setPrefix(pageable.getPrefix());
            resolver.setQualifierDelimiter(pageable.getQualifierDelimiter());
            resolver.setFallbackPageable(PageRequest.of(pageable.isOneIndexedParameters() ? 1 : 0, pageable.getDefaultPageSize()));
            resolver.setMaxPageSize(pageable.getMaxPageSize());
        };
    }

}
