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

package com.qdigo.ebike.api.domain.dto.third.wx.wxpush;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:  微信模版推送
 * date: 2020/2/24 8:55 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
public interface PushTemp {

    Logger log = org.slf4j.LoggerFactory.getLogger(PushTemp.class);

    default String buildJson(String openId, String formId, String emphasis) {
        Class<? extends PushTemp> clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("form_id", formId);
        Map<String, Object> data = new HashMap<>();
        int count = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if ("tempId".equals(field.getName())) {
                    map.put("template_id", value);
                } else if ("page".equals(field.getName())) {
                    map.put("page", value);
                } else {
                    data.put("keyword" + (++count), ImmutableMap.of("value", value));
                }
                if (emphasis != null && emphasis.equals(field.getName())) {
                    map.put("emphasis_keyword", "keyword" + count + ".DATA");
                }
            } catch (IllegalAccessException e) {
                log.error("错误:", e);
            }
        }
        map.put("data", data);
        map.putIfAbsent("page", "pages/map/map");
        return JSON.toJSONString(map);
    }

}
