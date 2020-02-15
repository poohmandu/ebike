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

package com.qdigo.ebike.common.core.util.page;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qdigo.ebike.common.core.util.SpringContextHolder;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Description: 
 * date: 2020/1/23 10:01 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
//@Service
//@DependsOn("springContextHolder")
//@Configuration
public class PageUtil {

    private static SpringDataWebProperties properties;

    private static SpringDataWebProperties getProperties() {
        if (properties == null) {
            properties = SpringContextHolder.getBean(SpringDataWebProperties.class);
        }
        return properties;
    }


    public static void startPage(Pageable pageable) {
        startPage(pageable, true);
    }

    public static void startPage(Pageable pageable, boolean count) {
        SpringDataWebProperties properties = getProperties();
        boolean oneIndexedParameters = properties.getPageable().isOneIndexedParameters();
        int pageNum = pageable.getPageNumber();
        PageHelper.startPage(oneIndexedParameters ? pageNum : pageNum + 1, pageable.getPageSize(), count);
    }

    public static <T> Page<T> of(List<T> list, Pageable pageable) {
        PageInfo<T> pageInfo = PageInfo.of(list);
        return new PageImpl<>(list, pageable, pageInfo.getTotal());
    }

}
