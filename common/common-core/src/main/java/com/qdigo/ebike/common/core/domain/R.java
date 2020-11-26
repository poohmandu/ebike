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

package com.qdigo.ebike.common.core.domain;

/**
 * Description:
 * date: 2019/12/10 5:22 PM
 *
 * @author niezhao
 * @since JDK 1.8
 */

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author lengleng
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private int statusCode;

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private T data;

    public static <T> R<T> ok(int code, String msg, T data) {
        return restResult(code, msg, data);
    }

    public static <T> R<T> ok(int code, String msg) {
        return restResult(code, msg, null);
    }

    public static <T> R<T> ok(int code) {
        return restResult(code, "成功", null);
    }

    public static <T> R<T> ok(String msg) {
        return restResult(200, msg, null);
    }

    public static <T> R<T> ok() {
        return restResult(200, "成功", null);
    }

    public static <T> R<T> failed(int code, String msg, T data) {
        return restResult(code, msg, data);
    }

    public static <T> R<T> failed(int code, String msg) {
        return restResult(code, msg, null);
    }

    private static <T> R<T> restResult(int code, String msg, T data) {
        R<T> apiResult = new R<>();
        apiResult.setStatusCode(code);
        apiResult.setData(data);
        apiResult.setMessage(msg);
        return apiResult;
    }
}
