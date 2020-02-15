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

import javax.persistence.*;

/**
 * Created by niezhao on 2017/8/1.
 */
@Entity
@Table(name = "station_point", indexes = {@Index(columnList = "fence_id"), @Index(columnList = "point_index")})
public class StationPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, name = "point_index")
    private int pointIndex;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "fence_id", nullable = false,
        foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private StationFence stationFence;

    private double longitude;

    private double latitude;

    public long getId() {
        return id;
    }

    public StationPoint setId(long id) {
        this.id = id;
        return this;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public StationPoint setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
        return this;
    }

    public StationFence getStationFence() {
        return stationFence;
    }

    public StationPoint setStationFence(StationFence stationFence) {
        this.stationFence = stationFence;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public StationPoint setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public StationPoint setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }
}
