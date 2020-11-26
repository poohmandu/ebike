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
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public final class CompleteOrderParam {
    private String appId;
    private String outOrderNo;
    //实际开始时间就是用户扫码时间,不需要特殊计算
    //微信不允许startTime和endTime相等、而且会给用户造成误会
    private long realStartTime;
    private String finishTicket;
    private List<WxscoreOrder.Discount> discounts;
    private List<WxscoreOrder.Fee> fees;
}
