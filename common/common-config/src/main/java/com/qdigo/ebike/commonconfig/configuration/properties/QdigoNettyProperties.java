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

package com.qdigo.ebike.commonconfig.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description: 
 * date: 2020/2/19 9:54 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Data
@ConfigurationProperties(prefix = "qdigo.netty", ignoreUnknownFields = false)
public class QdigoNettyProperties {
    private int port = 13078;
    private int readerIdle = 120;
    private String iotServiceName = "iot-center";
}
