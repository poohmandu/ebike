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

package com.qdigo.ebike.api.domain.dto.bike;

import com.qdigo.ebike.api.domain.Dto;
import com.qdigo.ebike.common.core.constants.BikeCfg;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Description: 
 * date: 2020/2/23 6:01 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Data
@Accessors(chain = true)
public class BikeStatusDto implements Dto {
    private long bikeStatusId;
    private double longitude = 0.0;
    private double latitude = 0.0;
    private int battery = 100;
    private double kilometer = 30.0;
    private int status;
    private String actualStatus;
    private String address;
    private Long stationId;
    private Long areaId;
    private Long parkStationId;
    private BikeCfg.LocationType locationType;
}
