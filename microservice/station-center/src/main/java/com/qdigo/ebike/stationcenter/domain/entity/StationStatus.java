/*
 * Copyright 2020 聂钊 nz@qdigo.com
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

package com.qdigo.ebike.stationcenter.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "station_status")
public class StationStatus extends AbstractAuditingEntity {

    private static final long serialVersionUID = -7032007903206018555L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long stationStatusId;
    @NotNull
    private int bikeCount = 0;
    @NotNull
    private int chargeStationsCount = 0;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_station_id")
    private BikeStation bikeStation;

    public long getStationStatusId() {
        return stationStatusId;
    }

    public StationStatus setStationStatusId(long stationStatusId) {
        this.stationStatusId = stationStatusId;
        return this;
    }

    public int getBikeCount() {
        return bikeCount;
    }

    public StationStatus setBikeCount(int bikeCount) {
        this.bikeCount = bikeCount;
        return this;
    }

    public int getChargeStationsCount() {
        return chargeStationsCount;
    }

    public StationStatus setChargeStationsCount(int chargeStationsCount) {
        this.chargeStationsCount = chargeStationsCount;
        return this;
    }

    public BikeStation getBikeStation() {
        return bikeStation;
    }

    public StationStatus setBikeStation(BikeStation bikeStation) {
        this.bikeStation = bikeStation;
        return this;
    }
}
