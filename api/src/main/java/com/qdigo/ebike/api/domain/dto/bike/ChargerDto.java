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

import lombok.Data;

/**
 * Description: 
 * date: 2020/1/10 1:56 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Data
public class ChargerDto {
    private Long chargerId;
    private String chargerImei;
    private String chargerName = "";
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private String address = "";
    private String note;
    private Integer status = 0;
    private Integer portNumber = 0; //可用充电口数量
    private Integer usedPortNumber = 0;
}


