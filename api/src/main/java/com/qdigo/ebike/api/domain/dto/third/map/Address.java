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

package com.qdigo.ebike.api.domain.dto.third.map;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.MappedSuperclass;

/**
 * Description: 
 * date: 2019/12/27 3:19 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Data
@MappedSuperclass
@Accessors(chain = true)
public class Address {

    private double longitude;
    private double latitude;

    private String province;
    private String city;
    private String district;

    private String cityCode;
    private String adCode;

    private String address;

}
