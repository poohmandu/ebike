/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.api.domain.dto.bike;

import com.qdigo.ebike.api.service.bike.BikeLocService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Description: 
 * date: 2019/12/30 4:41 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class BikeLoc {
    private String imei;
    private String mobileNo;
    private double latitude;
    private double longitude;
    private BikeLocService.LBSEvent event;
    private Date time;
    private Long agentId;
}
