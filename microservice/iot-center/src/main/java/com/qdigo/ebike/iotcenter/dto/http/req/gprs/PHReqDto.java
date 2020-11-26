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

package com.qdigo.ebike.iotcenter.dto.http.req.gprs;

public class PHReqDto {
    private int phImei;
    private int phSequence;
    private long phImsi;
    private int phPowerVoltage;
    private int phBatteryVoltage;
    private int phSentity;
    private int phAutoLock;
    private int phStar;
    private int phSoc;

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

    private String phServer;
    private String phClient;

    public String getPhServer() {
        return phServer;
    }

    public PHReqDto setPhServer(String phServer) {
        this.phServer = phServer;
        return this;
    }

    public String getPhClient() {
        return phClient;
    }

    public PHReqDto setPhClient(String phClient) {
        this.phClient = phClient;
        return this;
    }

    public int getPhImei() {
        return phImei;
    }

    public void setPhImei(int phImei) {
        this.phImei = phImei;
    }

    public int getPhSequence() {
        return phSequence;
    }

    public void setPhSequence(int phSequence) {
        this.phSequence = phSequence;
    }

    public long getPhImsi() {
        return phImsi;
    }

    public void setPhImsi(long phImsi) {
        this.phImsi = phImsi;
    }

    public int getPhPowerVoltage() {
        return phPowerVoltage;
    }

    public void setPhPowerVoltage(int phPowerVoltage) {
        this.phPowerVoltage = phPowerVoltage;
    }

    public int getPhBatteryVoltage() {
        return phBatteryVoltage;
    }

    public void setPhBatteryVoltage(int phBatteryVoltage) {
        this.phBatteryVoltage = phBatteryVoltage;
    }

    public int getPhSentity() {
        return phSentity;
    }

    public void setPhSentity(int phSentity) {
        this.phSentity = phSentity;
    }

    public int getPhAutoLock() {
        return phAutoLock;
    }

    public void setPhAutoLock(int phAutoLock) {
        this.phAutoLock = phAutoLock;
    }

    public int getPhStar() {
        return phStar;
    }

    public void setPhStar(int phStar) {
        this.phStar = phStar;
    }

    public int getPhElectric() {
        return phElectric;
    }

    public void setPhElectric(int phElectric) {
        this.phElectric = phElectric;
    }

    public int getPhDoorLock() {
        return phDoorLock;
    }

    public void setPhDoorLock(int phDoorLock) {
        this.phDoorLock = phDoorLock;
    }

    public int getPhLocked() {
        return phLocked;
    }

    public void setPhLocked(int phLocked) {
        this.phLocked = phLocked;
    }

    public int getPhShaked() {
        return phShaked;
    }

    public void setPhShaked(int phShaked) {
        this.phShaked = phShaked;
    }

    public int getPhWheelInput() {
        return phWheelInput;
    }

    public void setPhWheelInput(int phWheelInput) {
        this.phWheelInput = phWheelInput;
    }

    public int getPhAutoLocked() {
        return phAutoLocked;
    }

    public void setPhAutoLocked(int phAutoLocked) {
        this.phAutoLocked = phAutoLocked;
    }

    public int getPhTumble() {
        return phTumble;
    }

    public void setPhTumble(int phTumble) {
        this.phTumble = phTumble;
    }

    public int getPhError() {
        return phError;
    }

    public void setPhError(int phError) {
        this.phError = phError;
    }

    public int getPhMachineError() {
        return phMachineError;
    }

    public void setPhMachineError(int phMachineError) {
        this.phMachineError = phMachineError;
    }

    public int getPhBrakeErroe() {
        return phBrakeErroe;
    }

    public void setPhBrakeErroe(int phBrakeErroe) {
        this.phBrakeErroe = phBrakeErroe;
    }

    public int getPhHandleBarError() {
        return phHandleBarError;
    }

    public void setPhHandleBarError(int phHandleBarError) {
        this.phHandleBarError = phHandleBarError;
    }

    public int getPhControlError() {
        return phControlError;
    }

    public void setPhControlError(int phControlError) {
        this.phControlError = phControlError;
    }

    public int getPhSoc() {
        return phSoc;
    }

    public void setPhSoc(int phSoc) {
        this.phSoc = phSoc;
    }

}
