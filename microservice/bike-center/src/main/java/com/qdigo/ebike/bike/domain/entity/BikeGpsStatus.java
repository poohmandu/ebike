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

import javax.persistence.*;

/**
 * Created by niezhao on 2017/3/9.
 */

@Entity //车辆的物理状态
@Table(name = "bike_gps_status", indexes = {@Index(columnList = "bike_id")})
public class BikeGpsStatus {

    @Id
    private String imei;

    private Long imsi;
    private Integer powerVoltage; //车辆的电瓶电压，数值为实际值*100取整型(据此算电量)
    private Integer batteryVoltage; //设备内置电池电压
    private Integer sensitivity;
    private Integer star;
    private Integer electric; // (0:无外界电源  1:有外接电源)
    private Integer doorLock; // (0:电门关，1:电门开)
    private Integer locked;    //(0:没锁车  1:锁车)
    private Integer shaked;    //(0:无震动，1:震动)
    private Integer wheelInput;//(0:不是轮车输入模式  1:是轮车输入模式)//轮子是否转动
    private Integer autoLocked;  //(0:不是自动锁车 1：自动锁车)
    private Integer tumble;    //(0:没跌倒 1:跌倒)
    private Integer error;     //(0:无故障 1:有故障)
    private Integer machineError;  //有无电机故障   0: 无
    private Integer brakeError;  //有无刹车故障 0:无
    private Integer handleBarError;   //有无转把故障 0:无
    private Integer controlError;     //有无控制器故障 0：无
    private Integer soc;          //gps锂电池 剩余电量

    private Integer hight; // 海拔高度
    private Integer speed; // 实际速度

    private String lac; // location area code 位置区域码
    private String cellid; // 基站小区编号
    private String singal;// 信号强度

    private String pgTime;
    private String phTime;
    private String plTime;
    private String pcTime;

    @JsonBackReference
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Bike bike;

    public String getImei() {
        return imei;
    }

    public BikeGpsStatus setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public Long getImsi() {
        return imsi;
    }

    public BikeGpsStatus setImsi(Long imsi) {
        this.imsi = imsi;
        return this;
    }

    public Integer getPowerVoltage() {
        return powerVoltage;
    }

    public BikeGpsStatus setPowerVoltage(Integer powerVoltage) {
        this.powerVoltage = powerVoltage;
        return this;
    }

    public Integer getBatteryVoltage() {
        return batteryVoltage;
    }

    public BikeGpsStatus setBatteryVoltage(Integer batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
        return this;
    }

    public Integer getSensitivity() {
        return sensitivity;
    }

    public BikeGpsStatus setSensitivity(Integer sensitivity) {
        this.sensitivity = sensitivity;
        return this;
    }

    public Integer getStar() {
        return star;
    }

    public BikeGpsStatus setStar(Integer star) {
        this.star = star;
        return this;
    }

    public Integer getElectric() {
        return electric;
    }

    public BikeGpsStatus setElectric(Integer electric) {
        this.electric = electric;
        return this;
    }

    public Integer getDoorLock() {
        return doorLock;
    }

    public BikeGpsStatus setDoorLock(Integer doorLock) {
        this.doorLock = doorLock;
        return this;
    }

    public Integer getLocked() {
        return locked;
    }

    public BikeGpsStatus setLocked(Integer locked) {
        this.locked = locked;
        return this;
    }

    public Integer getShaked() {
        return shaked;
    }

    public BikeGpsStatus setShaked(Integer shaked) {
        this.shaked = shaked;
        return this;
    }

    public Integer getWheelInput() {
        return wheelInput;
    }

    public BikeGpsStatus setWheelInput(Integer wheelInput) {
        this.wheelInput = wheelInput;
        return this;
    }

    public Integer getAutoLocked() {
        return autoLocked;
    }

    public BikeGpsStatus setAutoLocked(Integer autoLocked) {
        this.autoLocked = autoLocked;
        return this;
    }

    public Integer getTumble() {
        return tumble;
    }

    public BikeGpsStatus setTumble(Integer tumble) {
        this.tumble = tumble;
        return this;
    }

    public Integer getError() {
        return error;
    }

    public BikeGpsStatus setError(Integer error) {
        this.error = error;
        return this;
    }

    public Integer getMachineError() {
        return machineError;
    }

    public BikeGpsStatus setMachineError(Integer machineError) {
        this.machineError = machineError;
        return this;
    }

    public Integer getBrakeError() {
        return brakeError;
    }

    public BikeGpsStatus setBrakeError(Integer brakeError) {
        this.brakeError = brakeError;
        return this;
    }

    public Integer getHandleBarError() {
        return handleBarError;
    }

    public BikeGpsStatus setHandleBarError(Integer handleBarError) {
        this.handleBarError = handleBarError;
        return this;
    }

    public Integer getControlError() {
        return controlError;
    }

    public BikeGpsStatus setControlError(Integer controlError) {
        this.controlError = controlError;
        return this;
    }

    public Integer getSoc() {
        return soc;
    }

    public BikeGpsStatus setSoc(Integer soc) {
        this.soc = soc;
        return this;
    }

    public Integer getHight() {
        return hight;
    }

    public BikeGpsStatus setHight(Integer hight) {
        this.hight = hight;
        return this;
    }

    public Integer getSpeed() {
        return speed;
    }

    public BikeGpsStatus setSpeed(Integer speed) {
        this.speed = speed;
        return this;
    }

    public String getLac() {
        return lac;
    }

    public BikeGpsStatus setLac(String lac) {
        this.lac = lac;
        return this;
    }

    public String getCellid() {
        return cellid;
    }

    public BikeGpsStatus setCellid(String cellid) {
        this.cellid = cellid;
        return this;
    }

    public String getSingal() {
        return singal;
    }

    public BikeGpsStatus setSingal(String singal) {
        this.singal = singal;
        return this;
    }

    public String getPgTime() {
        return pgTime;
    }

    public BikeGpsStatus setPgTime(String pgTime) {
        this.pgTime = pgTime;
        return this;
    }

    public String getPhTime() {
        return phTime;
    }

    public BikeGpsStatus setPhTime(String phTime) {
        this.phTime = phTime;
        return this;
    }

    public String getPlTime() {
        return plTime;
    }

    public BikeGpsStatus setPlTime(String plTime) {
        this.plTime = plTime;
        return this;
    }

    public Bike getBike() {
        return bike;
    }

    public BikeGpsStatus setBike(Bike bike) {
        this.bike = bike;
        return this;
    }

    public String getPcTime() {
        return pcTime;
    }

    public BikeGpsStatus setPcTime(String pcTime) {
        this.pcTime = pcTime;
        return this;
    }
}
