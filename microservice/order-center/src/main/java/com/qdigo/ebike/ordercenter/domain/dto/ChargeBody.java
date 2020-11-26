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

package com.qdigo.ebike.ordercenter.domain.dto;

import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ChargeBody {
    private UserDto userDto;
    private UserAccountDto userAccountDto;
    private int amount;//分
    private String channel;
    private String openId; //不同平台的唯一ID
    private int payType;
    private String clientIp;
    private PayBizType bizType;

    private Map<String, String> extra;
}