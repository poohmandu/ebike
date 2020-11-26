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

package com.qdigo.ebike.api.domain;

import com.qdigo.ebike.common.core.util.ConvertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * Description: 
 * date: 2020/1/25 5:45 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
public interface Dto {

    default <T> T toEntity(Class<T> targetClass) {
        return ConvertUtil.to(this, targetClass);
    }

    //空值会忽略
    default <T> void updated(T t) {
        copyIgnoreNull(this, t);
    }

    static String[] getNoValuePropertyNames(Object source) {
        Assert.notNull(source, "传递的参数对象不能为空");
        final BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();

        Set<String> noValuePropertySet = new HashSet<>();
        Arrays.stream(pds).forEach(pd -> {
            Object propertyValue = beanWrapper.getPropertyValue(pd.getName());
            if (Objects.isNull(propertyValue)) {
                noValuePropertySet.add(pd.getName());
            } else {
                if (Iterable.class.isAssignableFrom(propertyValue.getClass())) {
                    Iterable iterable = (Iterable) propertyValue;
                    Iterator iterator = iterable.iterator();
                    if (!iterator.hasNext()) noValuePropertySet.add(pd.getName());
                }
                if (Map.class.isAssignableFrom(propertyValue.getClass())) {
                    Map map = (Map) propertyValue;
                    if (map.isEmpty()) noValuePropertySet.add(pd.getName());
                }
            }
        });
        String[] result = new String[noValuePropertySet.size()];
        return noValuePropertySet.toArray(result);
    }

    static void copyIgnoreNull(Dto source, Object target) {
        Assert.notNull(source, "传递的参数对象不能为空");
        Assert.notNull(target, "传递的参数对象不能为空");
        BeanUtils.copyProperties(source, target, getNoValuePropertyNames(source));
    }

}
