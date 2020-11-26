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

import com.qdigo.ebike.api.domain.Dto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Description: 
 * date: 2020/1/15 7:23 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Data
@Accessors(chain = true)
public class RideDto implements Dto {
    private long rideRecordId;
    private String mobileNo;
    private String imei;
    private double price;
    private int unitMinutes;
    private Date startTime;
    private Date endTime;
    private int rideStatus;
    private Integer distance;
    private String startLoc;
    private String endLoc;
    private double consume;
    private double actualConsume;
    private int freeActivity;
    private Long agentId;
    private int version;
}
