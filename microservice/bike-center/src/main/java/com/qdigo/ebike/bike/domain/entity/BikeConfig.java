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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bike_config")
public class BikeConfig {

    @Id
    private String bikeType;

    @Column(nullable = false, length = 20)
    private Integer maxPowerVoltage; // 满电电压

    @Column(nullable = false, length = 20)
    private Integer minPowerVoltage; // 0电电压

    @Column(nullable = false, length = 20)
    private Integer lowBattery = 15; // 报警电压

    @Column(nullable = false, length = 20)
    private Integer enoughBattery = 80; // 刷新电压

    @Column(nullable = false, length = 20)
    private Double maxKilometer; // 满电里程

    @Column(nullable = false)
    private String note; //备注

    public String getBikeType() {
        return bikeType;
    }

    public BikeConfig setBikeType(String bikeType) {
        this.bikeType = bikeType;
        return this;
    }

    public Integer getMaxPowerVoltage() {
        return maxPowerVoltage;
    }

    public BikeConfig setMaxPowerVoltage(Integer maxPowerVoltage) {
        this.maxPowerVoltage = maxPowerVoltage;
        return this;
    }

    public Integer getMinPowerVoltage() {
        return minPowerVoltage;
    }

    public BikeConfig setMinPowerVoltage(Integer minPowerVoltage) {
        this.minPowerVoltage = minPowerVoltage;
        return this;
    }

    public Integer getLowBattery() {
        return lowBattery;
    }

    public BikeConfig setLowBattery(Integer lowBattery) {
        this.lowBattery = lowBattery;
        return this;
    }

    public Integer getEnoughBattery() {
        return enoughBattery;
    }

    public BikeConfig setEnoughBattery(Integer enoughBattery) {
        this.enoughBattery = enoughBattery;
        return this;
    }

    public Double getMaxKilometer() {
        return maxKilometer;
    }

    public BikeConfig setMaxKilometer(Double maxKilometer) {
        this.maxKilometer = maxKilometer;
        return this;
    }

    public String getNote() {
        return note;
    }

    public BikeConfig setNote(String note) {
        this.note = note;
        return this;
    }
}
