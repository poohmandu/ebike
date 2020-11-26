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

package com.qdigo.ebike.api.domain.dto.order.wxscore;

import com.qdigo.ebike.api.service.third.wxlite.WxscoreService;
import lombok.Data;

/**
 * description: 
 *
 * date: 2020/3/17 11:50 AM
 * @author niezhao
 */
@Data
public class WxscoreDto {
    private String outOrderNo;
    private String appId;
    private String mchId;
    private String serviceId;
    private WxscoreService.State state;
    private Long startTime;
    private Long endTime;
    private int riskAmount; //单位为分
    private int totalAmount; //单位为分
    private String transactionId; //finish_transaction_id退款需要用到的字段
    private Long AgentId;
    private Long userId;
    private Long rideRecordId;
}
