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
 * description: 认证结果通知
 *
 * date: 2020/2/24 9:08 PM
 * @author niezhao
 */
@Data
@Builder
public class StudentAuthNotice implements PushTemp {
    private static final String tempId = "zQftjE6n7T3oJF6jD3ukqefzA2t_qu3W-SnSQ3jay94";
    //认证结果
    private String result;
    //学号
    private String studentNo;
    //认证时间
    private String time;
    //学校名称
    private String schoolName;
    //备注
    private String failMsg;
}
