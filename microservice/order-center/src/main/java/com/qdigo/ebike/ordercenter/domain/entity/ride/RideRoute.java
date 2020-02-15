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

package com.qdigo.ebike.ordercenter.domain.entity.ride;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

/**
 * Created by niezhao on 2017/12/20.
 */
@Entity
@Table(name = "ride_route")
public class RideRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_record_id", unique = true, nullable = false)
    private RideRecord rideRecord;

    @Column(nullable = false, name = "start_lng")
    private double startLng;

    @Column(nullable = false, name = "start_lat")
    private double startLat;

    @Column(nullable = false, name = "end_lng")
    private double endLng;

    @Column(nullable = false, name = "end_lat")
    private double endLat;

    private Long StartStationId;

    private Long EndStationId;

    private Long agentId;

    public long getId() {
        return id;
    }

    public RideRoute setId(long id) {
        this.id = id;
        return this;
    }

    public RideRecord getRideRecord() {
        return rideRecord;
    }

    public RideRoute setRideRecord(RideRecord rideRecord) {
        this.rideRecord = rideRecord;
        return this;
    }

    public double getStartLng() {
        return startLng;
    }

    public RideRoute setStartLng(double startLng) {
        this.startLng = startLng;
        return this;
    }

    public double getStartLat() {
        return startLat;
    }

    public RideRoute setStartLat(double startLat) {
        this.startLat = startLat;
        return this;
    }

    public double getEndLng() {
        return endLng;
    }

    public RideRoute setEndLng(double endLng) {
        this.endLng = endLng;
        return this;
    }

    public double getEndLat() {
        return endLat;
    }

    public RideRoute setEndLat(double endLat) {
        this.endLat = endLat;
        return this;
    }

    public Long getStartStationId() {
        return StartStationId;
    }

    public RideRoute setStartStationId(Long startStationId) {
        StartStationId = startStationId;
        return this;
    }

    public Long getEndStationId() {
        return EndStationId;
    }

    public RideRoute setEndStationId(Long endStationId) {
        EndStationId = endStationId;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public RideRoute setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }
}
