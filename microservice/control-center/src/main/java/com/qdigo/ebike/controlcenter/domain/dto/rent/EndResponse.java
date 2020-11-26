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

package com.qdigo.ebike.controlcenter.domain.dto.rent;

import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.ConsumeDetail;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.common.core.util.FormatUtil;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class EndResponse {
    private int type; // 计费类型
    private String startTime;
    private String endTime;
    private double orderAmount;// 账单金额
    private double price; // 单价
    private int countTime; // 1.5小时 改为 xxx分钟
    private double balance;
    private long rideRecordId;
    private int unitMinutes;

    private String consumeNote;

    public static EndResponse build(EndDTO endDTO) {
        RideDto rideRecord = endDTO.getRideDto();
        UserAccountDto accountDto = endDTO.getUserAccountDto();
        ConsumeDetail consumeDetail = endDTO.getOut().getConsumeDetail();

        long seconds = Duration.between(rideRecord.getStartTime().toInstant(), rideRecord.getEndTime().toInstant()).getSeconds();
        return EndResponse.builder()
                .rideRecordId(rideRecord.getRideRecordId())
                .balance(FormatUtil.getMoney(accountDto.getBalance()))
                .startTime(FormatUtil.yMdHms.format(rideRecord.getStartTime()))
                .endTime(FormatUtil.yMdHms.format(rideRecord.getEndTime()))
                .countTime(FormatUtil.minutes(seconds)) //分钟
                .orderAmount(FormatUtil.getMoney(rideRecord.getConsume()))
                .price(rideRecord.getPrice())
                .unitMinutes(rideRecord.getUnitMinutes())
                .consumeNote(consumeDetail.getConsumeNote())
                .type(0)
                .build();
    }
}