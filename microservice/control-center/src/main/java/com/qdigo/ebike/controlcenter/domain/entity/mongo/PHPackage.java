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

package com.qdigo.ebike.controlcenter.domain.entity.mongo;

import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@ToString
@Document(collection = "PHPackage")
public class PHPackage {

    @Id
    private String id;

    @NotBlank
    private String phImei;
    private long phSequence;
    private long phImsi;
    private int phPowerVoltage; //车辆的电瓶电压，数值为实际值*100取整型(据此算电量)
    private int phBatteryVoltage; //设备内置电池电压
    private int phSentity;
    private int phAutoLock;
    private int phStar;
    private int phElectric; // (0:无外界电源  1:有外接电源)
    private int phDoorLock; // (0:电门锁关，1:电门锁开)
    private int phLocked;    //(0:没锁车  1:锁车)
    private int phShaked;    //(0:无震动，1:震动)
    private int phWheelInput;//(0:不是轮车输入模式  1:是轮车输入模式)
    private int phAutoLocked;  //(0:不是自动锁车 1：自动锁车)
    private int phTumble;    //(0:没跌倒 1:跌倒)
    private int phError;     //(0:无故障 1:有故障)
    private int phMachineError;  //有无电机故障   0: 无
    private int phBrakeErroe;  //有无刹车故障 0:无
    private int phHandleBarError;   //有无转把故障 0:无
    private int phControlError;     //有无控制器故障 0：无
    private Integer phHold;             //保留
    private int phSoc;          //gps锂电池 剩余电量
    private long timestamp = System.currentTimeMillis();
    @NotNull
    private String phServer;
    @NotNull
    private String phClient;

    public String getPhServer() {
        return phServer;
    }

    public PHPackage setPhServer(String phServer) {
        this.phServer = phServer;
        return this;
    }

    public String getPhClient() {
        return phClient;
    }

    public PHPackage setPhClient(String phClient) {
        this.phClient = phClient;
        return this;
    }

    public String getId() {
        return id;
    }

    public PHPackage setId(String id) {
        this.id = id;
        return this;
    }

    public String getPhImei() {
        return phImei;
    }

    public PHPackage setPhImei(String phImei) {
        this.phImei = phImei;
        return this;
    }

    public long getPhSequence() {
        return phSequence;
    }

    public PHPackage setPhSequence(long phSequence) {
        this.phSequence = phSequence;
        return this;
    }

    public long getPhImsi() {
        return phImsi;
    }

    public PHPackage setPhImsi(long phImsi) {
        this.phImsi = phImsi;
        return this;
    }

    public int getPhPowerVoltage() {
        return phPowerVoltage;
    }

    public PHPackage setPhPowerVoltage(int phPowerVoltage) {
        this.phPowerVoltage = phPowerVoltage;
        return this;
    }

    public int getPhBatteryVoltage() {
        return phBatteryVoltage;
    }

    public PHPackage setPhBatteryVoltage(int phBatteryVoltage) {
        this.phBatteryVoltage = phBatteryVoltage;
        return this;
    }

    public int getPhSentity() {
        return phSentity;
    }

    public PHPackage setPhSentity(int phSentity) {
        this.phSentity = phSentity;
        return this;
    }

    public int getPhAutoLock() {
        return phAutoLock;
    }

    public PHPackage setPhAutoLock(int phAutoLock) {
        this.phAutoLock = phAutoLock;
        return this;
    }

    public int getPhStar() {
        return phStar;
    }

    public PHPackage setPhStar(int phStar) {
        this.phStar = phStar;
        return this;
    }

    public int getPhElectric() {
        return phElectric;
    }

    public PHPackage setPhElectric(int phElectric) {
        this.phElectric = phElectric;
        return this;
    }

    public int getPhDoorLock() {
        return phDoorLock;
    }

    public PHPackage setPhDoorLock(int phDoorLock) {
        this.phDoorLock = phDoorLock;
        return this;
    }

    public int getPhLocked() {
        return phLocked;
    }

    public PHPackage setPhLocked(int phLocked) {
        this.phLocked = phLocked;
        return this;
    }

    public int getPhShaked() {
        return phShaked;
    }

    public PHPackage setPhShaked(int phShaked) {
        this.phShaked = phShaked;
        return this;
    }

    public int getPhWheelInput() {
        return phWheelInput;
    }

    public PHPackage setPhWheelInput(int phWheelInput) {
        this.phWheelInput = phWheelInput;
        return this;
    }

    public int getPhAutoLocked() {
        return phAutoLocked;
    }

    public PHPackage setPhAutoLocked(int phAutoLocked) {
        this.phAutoLocked = phAutoLocked;
        return this;
    }

    public int getPhTumble() {
        return phTumble;
    }

    public PHPackage setPhTumble(int phTumble) {
        this.phTumble = phTumble;
        return this;
    }

    public int getPhError() {
        return phError;
    }

    public PHPackage setPhError(int phError) {
        this.phError = phError;
        return this;
    }

    public int getPhMachineError() {
        return phMachineError;
    }

    public PHPackage setPhMachineError(int phMachineError) {
        this.phMachineError = phMachineError;
        return this;
    }

    public int getPhBrakeErroe() {
        return phBrakeErroe;
    }

    public PHPackage setPhBrakeErroe(int phBrakeErroe) {
        this.phBrakeErroe = phBrakeErroe;
        return this;
    }

    public int getPhHandleBarError() {
        return phHandleBarError;
    }

    public PHPackage setPhHandleBarError(int phHandleBarError) {
        this.phHandleBarError = phHandleBarError;
        return this;
    }

    public int getPhControlError() {
        return phControlError;
    }

    public PHPackage setPhControlError(int phControlError) {
        this.phControlError = phControlError;
        return this;
    }

    public Integer getPhHold() {
        return phHold;
    }

    public PHPackage setPhHold(Integer phHold) {
        this.phHold = phHold;
        return this;
    }

    public int getPhSoc() {
        return phSoc;
    }

    public PHPackage setPhSoc(int phSoc) {
        this.phSoc = phSoc;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public PHPackage setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
