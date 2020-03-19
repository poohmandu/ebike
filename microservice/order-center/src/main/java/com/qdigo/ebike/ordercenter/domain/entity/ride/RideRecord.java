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

import com.qdigo.ebike.common.core.constants.Status;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by niezhao on 2017/4/18.
 */
@Entity
@Table(name = "ride_record", indexes = {@Index(columnList = "mobile_no")})
public class RideRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ride_record_id")
    private long rideRecordId;

    //单向的
    //@ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    //@JoinColumn(name = "mobile_no", referencedColumnName = "mobile_no",
    //        foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    //private User user;
    @Column(nullable = false, name = "mobile_no")
    private String mobileNo;

    //@ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    //@JoinColumn(name = "imei", referencedColumnName = "imei_id",
    //        foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    //private Bike bike;

    @Column(name = "imei", nullable = false)
    private String imei;

    @Column(scale = 2)
    private double price;//扫码时决定    // 按半小时算

    private int unitMinutes;

    @NotNull
    private Date startTime = new Date();
    private Date endTime;

    @Column(nullable = false, name = "ride_status")
    private int rideStatus;

    private Integer distance;

    @NotNull
    private String startLoc; //lng,lat
    private String endLoc;

    @Column(nullable = false)
    private double consume = 0; //骑行花费

    @Column(nullable = false)
    private double actualConsume = 0;

    @Column(nullable = false)
    private int freeActivity = Status.FreeActivity.noFree.getVal(); // rideStatus为end 的各种免费细则

    //@JsonManagedReference
    //@OneToMany(mappedBy = "rideRecord")
    //private List<RideFreeActivity> freeActivities;

    @Column(name = "agent_id")
    private Long agentId;

    @Version
    private int version;

    public long getRideRecordId() {
        return rideRecordId;
    }

    public RideRecord setRideRecordId(long rideRecordId) {
        this.rideRecordId = rideRecordId;
        return this;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public RideRecord setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public String getImei() {
        return imei;
    }

    public RideRecord setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public RideRecord setPrice(double price) {
        this.price = price;
        return this;
    }

    public int getUnitMinutes() {
        return unitMinutes;
    }

    public RideRecord setUnitMinutes(int unitMinutes) {
        this.unitMinutes = unitMinutes;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public RideRecord setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public RideRecord setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public int getRideStatus() {
        return rideStatus;
    }

    public RideRecord setRideStatus(int rideStatus) {
        this.rideStatus = rideStatus;
        return this;
    }

    public Integer getDistance() {
        return distance;
    }

    public RideRecord setDistance(Integer distance) {
        this.distance = distance;
        return this;
    }

    public String getStartLoc() {
        return startLoc;
    }

    public RideRecord setStartLoc(String startLoc) {
        this.startLoc = startLoc;
        return this;
    }

    public String getEndLoc() {
        return endLoc;
    }

    public RideRecord setEndLoc(String endLoc) {
        this.endLoc = endLoc;
        return this;
    }

    public double getConsume() {
        return consume;
    }

    public RideRecord setConsume(double consume) {
        this.consume = consume;
        return this;
    }

    public double getActualConsume() {
        return actualConsume;
    }

    public RideRecord setActualConsume(double actualConsume) {
        this.actualConsume = actualConsume;
        return this;
    }

    public int getFreeActivity() {
        return freeActivity;
    }

    public RideRecord setFreeActivity(int freeActivity) {
        this.freeActivity = freeActivity;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public RideRecord setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public RideRecord setVersion(int version) {
        this.version = version;
        return this;
    }


}
