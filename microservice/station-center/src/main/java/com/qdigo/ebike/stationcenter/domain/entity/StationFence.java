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
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by niezhao on 2017/8/1.
 */
@Entity
@Table(name = "station_fence", indexes = {@Index(columnList = "gid", unique = true)})
public class StationFence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fenceId;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "station_id", name = "station_stationId", unique = true, nullable = false,
            foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    //@JoinColumn(unique = true, nullable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private BikeStation station;

    @NotNull
    private String fenceName;

    @Column(name = "gid", nullable = false, length = 50, unique = true)
    private String gid;

    private int id;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "stationFence")
    @OrderBy(value = "pointIndex ASC ")
    private List<StationPoint> points;

    public long getFenceId() {
        return fenceId;
    }

    public StationFence setFenceId(long fenceId) {
        this.fenceId = fenceId;
        return this;
    }

    public BikeStation getStation() {
        return station;
    }

    public StationFence setStation(BikeStation station) {
        this.station = station;
        return this;
    }

    public String getFenceName() {
        return fenceName;
    }

    public StationFence setFenceName(String fenceName) {
        this.fenceName = fenceName;
        return this;
    }

    public String getGid() {
        return gid;
    }

    public StationFence setGid(String gid) {
        this.gid = gid;
        return this;
    }

    public int getId() {
        return id;
    }

    public StationFence setId(int id) {
        this.id = id;
        return this;
    }

    public List<StationPoint> getPoints() {
        return points;
    }

    public StationFence setPoints(List<StationPoint> points) {
        this.points = points;
        return this;
    }
}
