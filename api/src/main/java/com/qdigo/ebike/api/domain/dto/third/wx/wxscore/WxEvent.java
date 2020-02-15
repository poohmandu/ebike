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

package com.qdigo.ebike.api.domain.dto.third.wx.wxscore;

import lombok.Data;

@Data
public class WxEvent {

    private String id; //通知的唯一ID

    private String create_time; //通知创建的时间，格式为yyyyMMddHHmmss

    private String event_type; //通知的类型

    private String resource_type; //通知的资源数据类型

    private EncryptResource resource;

}
