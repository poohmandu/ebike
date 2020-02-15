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

package com.qdigo.ebike.common.core.errors.exception.runtime;

import com.qdigo.ebike.common.core.errors.exception.QdigoRuntimeException;

/**
 * Created by niezhao on 2018/1/10.
 */
public class ChargeNotFoundException extends QdigoRuntimeException {

    public ChargeNotFoundException(String message) {
        super(message);
    }
}
