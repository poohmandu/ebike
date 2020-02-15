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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "ride_order", indexes = {@Index(columnList = "imei", unique = true), @Index(columnList = "mobile_no", unique = true)})
public class RideOrder {

    @Id
    @Column(name = "ride_record_id")
    private long rideRecordId; //主键依赖于rideRecord

    //CascadeType.REMOVE会级联删除
    //@OneToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE}, optional = false, fetch = FetchType.LAZY)
    //@JoinColumn(name = "mobile_no", referencedColumnName = "mobile_no",
    //    foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    //private User user;
    @Column(nullable = false, name = "mobile_no")
    private String mobileNo;


    //@OneToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE}, optional = false, fetch = FetchType.LAZY)
    //@JoinColumn(name = "imei", referencedColumnName = "imei_id",
    //    foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    //private Bike bike;
    @Column(name = "imei", nullable = false)
    private String imei;

    @Column(scale = 2)
    private double price;//扫码时决定    // 按半小时算

    private int unitMinutes;

    private long agentId;

    @NotNull
    private Date startTime = new Date();

    @Column(nullable = false, name = "ride_status")
    private int rideStatus;

    @NotNull
    private String startLoc; //lng,lat

    @Version
    private int version;

    public long getRideRecordId() {
        return rideRecordId;
    }

    public RideOrder setRideRecordId(long rideRecordId) {
        this.rideRecordId = rideRecordId;
        return this;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public RideOrder setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public String getImei() {
        return imei;
    }

    public RideOrder setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public RideOrder setPrice(double price) {
        this.price = price;
        return this;
    }

    public int getUnitMinutes() {
        return unitMinutes;
    }

    public RideOrder setUnitMinutes(int unitMinutes) {
        this.unitMinutes = unitMinutes;
        return this;
    }

    public long getAgentId() {
        return agentId;
    }

    public RideOrder setAgentId(long agentId) {
        this.agentId = agentId;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public RideOrder setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public int getRideStatus() {
        return rideStatus;
    }

    public RideOrder setRideStatus(int rideStatus) {
        this.rideStatus = rideStatus;
        return this;
    }

    public String getStartLoc() {
        return startLoc;
    }

    public RideOrder setStartLoc(String startLoc) {
        this.startLoc = startLoc;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public RideOrder setVersion(int version) {
        this.version = version;
        return this;
    }
}
