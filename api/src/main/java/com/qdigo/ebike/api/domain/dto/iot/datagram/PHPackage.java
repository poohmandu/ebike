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

package com.qdigo.ebike.api.domain.dto.iot.datagram;

import java.io.Serializable;

public class PHPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String phImei;
    private Long phSequence;
    private Long phImsi;
    private Integer phPowerVoltage;
    private Integer phBatteryVoltage;
    private Integer phSentity;
    private Integer phAutoLock;
    private Integer phStar;
    private Integer phElectric; // (0:无外界电源  1:有外接电源)
    private Integer phDoorLock; // (0:电门锁关，1:电门锁开)
    private Integer phLocked;    //(0:没锁车  1:锁车)
    private Integer phShaked;    //(0:无震动，1:震动)
    private Integer phWheelInput;//(0:不是轮车输入模式  1:是轮车输入模式)
    private Integer phAutoLocked;  //(0:不是自动锁车 1：自动锁车)
    private Integer phTumble;    //(0:没跌倒 1:跌倒)
    private Integer phError;     //(0:无故障 1:有故障)
    private Integer phMachineError;  //有无电机故障   0: 无
    private Integer phBrakeErroe;  //有无刹车故障 0:无
    private Integer phHandleBarError;   //有无转把故障 0:无
    private Integer phControlError;     //有无控制器故障 0：无
    private Integer phHold;             //保留
    private Integer phSoc;       //gps锂电池 剩余电量
    private Long timestamp = System.currentTimeMillis();

    private String phServer;
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

    public Long getPhSequence() {
        return phSequence;
    }

    public PHPackage setPhSequence(Long phSequence) {
        this.phSequence = phSequence;
        return this;
    }

    public Long getPhImsi() {
        return phImsi;
    }

    public PHPackage setPhImsi(Long phImsi) {
        this.phImsi = phImsi;
        return this;
    }

    public Integer getPhPowerVoltage() {
        return phPowerVoltage;
    }

    public PHPackage setPhPowerVoltage(Integer phPowerVoltage) {
        this.phPowerVoltage = phPowerVoltage;
        return this;
    }

    public Integer getPhBatteryVoltage() {
        return phBatteryVoltage;
    }

    public PHPackage setPhBatteryVoltage(Integer phBatteryVoltage) {
        this.phBatteryVoltage = phBatteryVoltage;
        return this;
    }

    public Integer getPhSentity() {
        return phSentity;
    }

    public PHPackage setPhSentity(Integer phSentity) {
        this.phSentity = phSentity;
        return this;
    }

    public Integer getPhAutoLock() {
        return phAutoLock;
    }

    public PHPackage setPhAutoLock(Integer phAutoLock) {
        this.phAutoLock = phAutoLock;
        return this;
    }

    public Integer getPhStar() {
        return phStar;
    }

    public PHPackage setPhStar(Integer phStar) {
        this.phStar = phStar;
        return this;
    }

    public Integer getPhElectric() {
        return phElectric;
    }

    public PHPackage setPhElectric(Integer phElectric) {
        this.phElectric = phElectric;
        return this;
    }

    public Integer getPhDoorLock() {
        return phDoorLock;
    }

    public PHPackage setPhDoorLock(Integer phDoorLock) {
        this.phDoorLock = phDoorLock;
        return this;
    }

    public Integer getPhLocked() {
        return phLocked;
    }

    public PHPackage setPhLocked(Integer phLocked) {
        this.phLocked = phLocked;
        return this;
    }

    public Integer getPhShaked() {
        return phShaked;
    }

    public PHPackage setPhShaked(Integer phShaked) {
        this.phShaked = phShaked;
        return this;
    }

    public Integer getPhWheelInput() {
        return phWheelInput;
    }

    public PHPackage setPhWheelInput(Integer phWheelInput) {
        this.phWheelInput = phWheelInput;
        return this;
    }

    public Integer getPhAutoLocked() {
        return phAutoLocked;
    }

    public PHPackage setPhAutoLocked(Integer phAutoLocked) {
        this.phAutoLocked = phAutoLocked;
        return this;
    }

    public Integer getPhTumble() {
        return phTumble;
    }

    public PHPackage setPhTumble(Integer phTumble) {
        this.phTumble = phTumble;
        return this;
    }

    public Integer getPhError() {
        return phError;
    }

    public PHPackage setPhError(Integer phError) {
        this.phError = phError;
        return this;
    }

    public Integer getPhMachineError() {
        return phMachineError;
    }

    public PHPackage setPhMachineError(Integer phMachineError) {
        this.phMachineError = phMachineError;
        return this;
    }

    public Integer getPhBrakeErroe() {
        return phBrakeErroe;
    }

    public PHPackage setPhBrakeErroe(Integer phBrakeErroe) {
        this.phBrakeErroe = phBrakeErroe;
        return this;
    }

    public Integer getPhHandleBarError() {
        return phHandleBarError;
    }

    public PHPackage setPhHandleBarError(Integer phHandleBarError) {
        this.phHandleBarError = phHandleBarError;
        return this;
    }

    public Integer getPhControlError() {
        return phControlError;
    }

    public PHPackage setPhControlError(Integer phControlError) {
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

    public Integer getPhSoc() {
        return phSoc;
    }

    public PHPackage setPhSoc(Integer phSoc) {
        this.phSoc = phSoc;
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public PHPackage setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

}
