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

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description: 骑行提醒
 *
 * date: 2020/2/24 9:09 PM
 * @author niezhao
 */
@Data
@Accessors(chain = true)
public class DriveNotice implements PushTemp {
    private static final String tempId = "Wj1gjCAqM6S8CZWfpq9gd5YKDzrxCRMU-Jc3R47C3NE";
    //车辆编号
    private String deviceId;
    //开始时间
    private String startTime;
    //提醒原因
    private String reason;

}
