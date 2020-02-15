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

package com.qdigo.ebike.bike.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.qdigo.ebike.common.core.constants.BikeCfg;
import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "bike", indexes = {@Index(columnList = "imei_id", unique = true)})
//@JsonIgnoreProperties(value = {"agent"})
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Bike extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bikeId;
    @Column(length = 20, nullable = false, unique = true)
    private String deviceId = "";// 车架号（10位）

    @Column(length = 20, nullable = false, unique = true, name = "imei_id")
    private String imeiId;// 车辆imei号

    private double price = 5;

    private int unitMinutes = 30;

    @NotNull
    @Column(length = 10)
    private String type = "A"; // A-小盛铃,B-小Q国际 ,C-C-One

    @Column(length = 20, columnDefinition = "varchar(20) default 'school'", nullable = false)
    @Enumerated(EnumType.STRING)
    private BikeCfg.OperationType operationType = BikeCfg.OperationType.school;

    //@JsonManagedReference 和 @JsonBackReference
    //一对注解相较于 @JsonIgnore bike里依然有属性 bikeStatus
    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.ALL}, optional = false, mappedBy = "bike", fetch = FetchType.LAZY)
    //lazy去掉 任务调度时 no session
    private BikeStatus bikeStatus;

    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "bike")
    private BikeGpsStatus gpsStatus;

    @Column(nullable = false, columnDefinition = "bit(1) default 1")
    private boolean online = true;

    private boolean isDeleted = false;

    private String licence = ""; //车牌号

    @Column(nullable = false)
    private String bleMac = "";

    public Long getBikeId() {
        return bikeId;
    }

    public Bike setBikeId(Long bikeId) {
        this.bikeId = bikeId;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Bike setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getImeiId() {
        return imeiId;
    }

    public Bike setImeiId(String imeiId) {
        this.imeiId = imeiId;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Bike setPrice(double price) {
        this.price = price;
        return this;
    }

    public int getUnitMinutes() {
        return unitMinutes;
    }

    public Bike setUnitMinutes(int unitMinutes) {
        this.unitMinutes = unitMinutes;
        return this;
    }

    public String getType() {
        return type;
    }

    public Bike setType(String type) {
        this.type = type;
        return this;
    }

    public BikeCfg.OperationType getOperationType() {
        return operationType;
    }

    public Bike setOperationType(BikeCfg.OperationType operationType) {
        this.operationType = operationType;
        return this;
    }

    public BikeStatus getBikeStatus() {
        return bikeStatus;
    }

    public Bike setBikeStatus(BikeStatus bikeStatus) {
        this.bikeStatus = bikeStatus;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public Bike setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public BikeGpsStatus getGpsStatus() {
        return gpsStatus;
    }

    public Bike setGpsStatus(BikeGpsStatus gpsStatus) {
        this.gpsStatus = gpsStatus;
        return this;
    }

    public boolean isOnline() {
        return online;
    }

    public Bike setOnline(boolean online) {
        this.online = online;
        return this;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Bike setDeleted(boolean deleted) {
        isDeleted = deleted;
        return this;
    }

    public String getLicence() {
        return licence;
    }

    public Bike setLicence(String licence) {
        this.licence = licence;
        return this;
    }

    public String getBleMac() {
        return bleMac;
    }

    public Bike setBleMac(String bleMac) {
        this.bleMac = bleMac;
        return this;
    }
}
