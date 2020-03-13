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

package com.qdigo.ebike.bike.domain.entity;

import com.qdigo.ebike.api.domain.dto.third.map.Address;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by niezhao on 2017/7/22.
 */
@Entity
@Table(name = "bike_address")
public class BikeAddress extends Address {

    @Id
    private String imei;

    public String getImei() {
        return imei;
    }

    public BikeAddress setImei(String imei) {
        this.imei = imei;
        return this;
    }

}
