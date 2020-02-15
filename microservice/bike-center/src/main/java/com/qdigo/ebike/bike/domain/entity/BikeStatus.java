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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qdigo.ebike.common.core.constants.BikeCfg;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity //车辆的逻辑状态
@Table(name = "bike_status", indexes = {@Index(columnList = "bike_id", unique = true)})
public class BikeStatus extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long bikeStatusId;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_id", unique = true, nullable = false,
        foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Bike bike;

    @NotNull
    private double longitude = 0.0;
    @NotNull
    private double latitude = 0.0;
    @NotNull
    private int battery = 100; //剩余电量
    @NotNull
    private double kilometer = 30.0; //剩余里程
    @NotNull
    private int status = Status.BikeLogicStatus.available.getVal(); // 0:可用 1:已预约 2:使用中
    @NotNull
    //0:好的 1:无法还车 有故障 2:pg 长时间未上传PG 3:无gps,可用性未知 4:(自动还车无法还车),不可用
    private String actualStatus = Status.BikeActualStatus.ok.getVal();
    @NotNull
    private String address = ""; //长宁区福泉路59号

    private Long stationId;//据此判断是否在还车点,null表示不在还车点

    private Long areaId; //据此判断是否在服务区,null表示不在服务区

    @Column(name = "park_station_id", length = 20)
    private Long parkStationId;

    @Column(length = 20, columnDefinition = "varchar(20) default 'gps'", nullable = false)
    @Enumerated(EnumType.STRING)
    private BikeCfg.LocationType locationType = BikeCfg.LocationType.gps;

    public Long getAreaId() {
        return areaId;
    }

    public BikeStatus setAreaId(Long areaId) {
        this.areaId = areaId;
        return this;
    }

    public Long getParkStationId() {
        return parkStationId;
    }

    public BikeStatus setParkStationId(Long parkStationId) {
        this.parkStationId = parkStationId;
        return this;
    }

    public BikeCfg.LocationType getLocationType() {
        return locationType;
    }

    public BikeStatus setLocationType(BikeCfg.LocationType locationType) {
        this.locationType = locationType;
        return this;
    }

    public Long getStationId() {
        return stationId;
    }

    public BikeStatus setStationId(Long stationId) {
        this.stationId = stationId;
        return this;
    }

    public long getBikeStatusId() {
        return bikeStatusId;
    }

    public BikeStatus setBikeStatusId(long bikeStatusId) {
        this.bikeStatusId = bikeStatusId;
        return this;
    }

    public Bike getBike() {
        return bike;
    }

    public BikeStatus setBike(Bike bike) {
        this.bike = bike;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public BikeStatus setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public BikeStatus setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public int getBattery() {
        return battery;
    }

    public BikeStatus setBattery(int battery) {
        this.battery = battery;
        return this;
    }

    public double getKilometer() {
        return kilometer;
    }

    public BikeStatus setKilometer(double kilometer) {
        this.kilometer = kilometer;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public BikeStatus setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getActualStatus() {
        return actualStatus;
    }

    public BikeStatus setActualStatus(String actualStatus) {
        this.actualStatus = actualStatus;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public BikeStatus setAddress(String address) {
        this.address = address;
        return this;
    }

}
