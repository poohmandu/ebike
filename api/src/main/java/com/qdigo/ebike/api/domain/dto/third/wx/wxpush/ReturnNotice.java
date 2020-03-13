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

import com.qdigo.ebike.api.domain.dto.user.UserDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * description: 还车提醒
 *
 * date: 2020/2/24 9:03 PM
 * @author niezhao
 */
@Data
@Builder
public class ReturnNotice implements PushTemp {
    private static final String tempId = "2ygeypXs6cvJakbWBhMT9tY2CD0PCEt2BIodKcbG0kk";
    //消费金额
    private String consume;
    //车辆编号
    private String deviceId;
    //提醒原因
    private String content;
}
