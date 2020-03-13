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

package com.qdigo.ebike.commonaop.annotations;

import com.qdigo.ebike.commonaop.constants.DB;

import java.lang.annotation.*;

/**
 * 防止请求重入问题
 * Created by niezhao on 2017/6/1.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {

    long expireSeconds() default 120;

    //boolean isExpire() default true;

    String[] key() default {"mobileNo"};

    DB DB() default DB.Redis;//memory

}
