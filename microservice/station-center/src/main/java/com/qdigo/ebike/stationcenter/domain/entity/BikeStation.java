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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "bike_station")
@JsonIgnoreProperties(value = {"agent"})
public class BikeStation extends AbstractAuditingEntity {

    private static final long serialVersionUID = -6756278994264180674L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "station_id")
    private long stationId;
    @NotNull
    private String stationName = "";

    private double longitude = 0;

    private double latitude = 0;

    private String address = "";

    private int radius = 150;//米

    @Column(name = "pic_url")
    private String picUrl;
    private String note;

    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.MERGE}, optional = false, mappedBy = "bikeStation", fetch = FetchType.LAZY)
    private StationStatus stationStatus;

    //@JsonBackReference
    //@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    //@JoinColumn(name = "agent_id", nullable = false,
    //        foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @Column(name = "agent_id")
    private Long agentId;

    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.ALL}, mappedBy = "station", fetch = FetchType.LAZY)
    private StationFence stationFence;

    @Column(nullable = false, columnDefinition = "bit(1) default 1")
    private boolean online = true; //上下线

    @Column(length = 20, columnDefinition = "int(11) default 0", nullable = false)
    private Integer compensate = 10; //还车点补偿范围

    public long getStationId() {
        return stationId;
    }

    public BikeStation setStationId(long stationId) {
        this.stationId = stationId;
        return this;
    }

    public String getStationName() {
        return stationName;
    }

    public BikeStation setStationName(String stationName) {
        this.stationName = stationName;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public BikeStation setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public BikeStation setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public BikeStation setAddress(String address) {
        this.address = address;
        return this;
    }

    public int getRadius() {
        return radius;
    }

    public BikeStation setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public BikeStation setPicUrl(String picUrl) {
        this.picUrl = picUrl;
        return this;
    }

    public String getNote() {
        return note;
    }

    public BikeStation setNote(String note) {
        this.note = note;
        return this;
    }

    public StationStatus getStationStatus() {
        return stationStatus;
    }

    public BikeStation setStationStatus(StationStatus stationStatus) {
        this.stationStatus = stationStatus;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public BikeStation setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public StationFence getStationFence() {
        return stationFence;
    }

    public BikeStation setStationFence(StationFence stationFence) {
        this.stationFence = stationFence;
        return this;
    }

    public boolean isOnline() {
        return online;
    }

    public BikeStation setOnline(boolean online) {
        this.online = online;
        return this;
    }

    public Integer getCompensate() {
        return compensate;
    }

    public BikeStation setCompensate(Integer compensate) {
        this.compensate = compensate;
        return this;
    }
}
