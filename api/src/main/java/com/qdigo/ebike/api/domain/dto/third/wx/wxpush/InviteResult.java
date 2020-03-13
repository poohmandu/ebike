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

import lombok.Builder;
import lombok.Data;

/**
 * description: 邀请结果提醒
 *
 * date: 2020/2/24 9:13 PM
 * @author niezhao
 */
@Data
@Builder
public class InviteResult implements PushTemp {
    private static final String tempId = "Tqx8DSgQKzfOwfebZbrqzb_V-BxkjIBcYKlELoPta7U";
    //奖励
    private String reward;
    //受邀者
    private String invitee;
    //邀请人
    private String inviter;
    //有效期
    private String validDate;
    //备注
    private String note;
}
