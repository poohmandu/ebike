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

package com.qdigo.ebike.api.domain.dto.order;

import com.qdigo.ebike.common.core.constants.Status;
import lombok.Data;

import java.util.Date;

/**
 * description: 
 *
 * date: 2020/3/2 12:27 PM
 * @author niezhao
 */
@Data
public class JournalAccountDto {
    private long journalAccountId;
    private String mobileNo;
    private Status.PayType payType;
    private Double amount;
    private double startAccount;
    private Double endAccount;
    private Date startTime;
    private Date endTime;
    private Long agentId;
    private Long rideRecordId;
    private String orderNo;
    private Long longRentId;
}
