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

package com.qdigo.ebike.controlcenter.domain.entity.device;

import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ph_package")
public class PHSqlPackage extends AbstractAuditingEntity {

	private static final long serialVersionUID = 1L;

	@Id
	private String phImei;

	private long phSequence;
	private long phImsi;
	private int phPowerVoltage;
	private int phBatteryVoltage;
	private int phSentity;
	private int phAutoLock;
	private int phStar;
	private int phElectric; // (0:无外界电源  1:有外接电源)
	private int phDoorLock; // (0:电门锁关，1:电门锁开) 上电 熄火
	private int phLocked;   // (0:没锁车  1:锁车)
	private int phShaked;   // (0:无震动，1:震动)
	private int phWheelInput; //(0:不是轮车输入模式  1:是轮车输入模式)
	private int phAutoLocked; //(0:不是自动锁车 1：自动锁车)
	private int phTumble;    //(0:没跌倒 1:跌倒)
	private int phError;     //(0:无故障 1:有故障)
	private int phMachineError;  //有无电机故障   0: 无
	private int phBrakeErroe;  //有无刹车故障 0:无
	private int phHandleBarError;   //有无转故障 0:无
	private int phControlError;     //有无控制器故障 0：无
	private Integer phHold ;             //保留
	private int  phSoc;

    public String getPhImei() {
        return phImei;
    }

    public PHSqlPackage setPhImei(String phImei) {
        this.phImei = phImei;
        return this;
    }

    public long getPhSequence() {
        return phSequence;
    }

    public PHSqlPackage setPhSequence(long phSequence) {
        this.phSequence = phSequence;
        return this;
    }

    public long getPhImsi() {
        return phImsi;
    }

    public PHSqlPackage setPhImsi(long phImsi) {
        this.phImsi = phImsi;
        return this;
    }

    public int getPhPowerVoltage() {
        return phPowerVoltage;
    }

    public PHSqlPackage setPhPowerVoltage(int phPowerVoltage) {
        this.phPowerVoltage = phPowerVoltage;
        return this;
    }

    public int getPhBatteryVoltage() {
        return phBatteryVoltage;
    }

    public PHSqlPackage setPhBatteryVoltage(int phBatteryVoltage) {
        this.phBatteryVoltage = phBatteryVoltage;
        return this;
    }

    public int getPhSentity() {
        return phSentity;
    }

    public PHSqlPackage setPhSentity(int phSentity) {
        this.phSentity = phSentity;
        return this;
    }

    public int getPhAutoLock() {
        return phAutoLock;
    }

    public PHSqlPackage setPhAutoLock(int phAutoLock) {
        this.phAutoLock = phAutoLock;
        return this;
    }

    public int getPhStar() {
        return phStar;
    }

    public PHSqlPackage setPhStar(int phStar) {
        this.phStar = phStar;
        return this;
    }

    public int getPhElectric() {
        return phElectric;
    }

    public PHSqlPackage setPhElectric(int phElectric) {
        this.phElectric = phElectric;
        return this;
    }

    public int getPhDoorLock() {
        return phDoorLock;
    }

    public PHSqlPackage setPhDoorLock(int phDoorLock) {
        this.phDoorLock = phDoorLock;
        return this;
    }

    public int getPhLocked() {
        return phLocked;
    }

    public PHSqlPackage setPhLocked(int phLocked) {
        this.phLocked = phLocked;
        return this;
    }

    public int getPhShaked() {
        return phShaked;
    }

    public PHSqlPackage setPhShaked(int phShaked) {
        this.phShaked = phShaked;
        return this;
    }

    public int getPhWheelInput() {
        return phWheelInput;
    }

    public PHSqlPackage setPhWheelInput(int phWheelInput) {
        this.phWheelInput = phWheelInput;
        return this;
    }

    public int getPhAutoLocked() {
        return phAutoLocked;
    }

    public PHSqlPackage setPhAutoLocked(int phAutoLocked) {
        this.phAutoLocked = phAutoLocked;
        return this;
    }

    public int getPhTumble() {
        return phTumble;
    }

    public PHSqlPackage setPhTumble(int phTumble) {
        this.phTumble = phTumble;
        return this;
    }

    public int getPhError() {
        return phError;
    }

    public PHSqlPackage setPhError(int phError) {
        this.phError = phError;
        return this;
    }

    public int getPhMachineError() {
        return phMachineError;
    }

    public PHSqlPackage setPhMachineError(int phMachineError) {
        this.phMachineError = phMachineError;
        return this;
    }

    public int getPhBrakeErroe() {
        return phBrakeErroe;
    }

    public PHSqlPackage setPhBrakeErroe(int phBrakeErroe) {
        this.phBrakeErroe = phBrakeErroe;
        return this;
    }

    public int getPhHandleBarError() {
        return phHandleBarError;
    }

    public PHSqlPackage setPhHandleBarError(int phHandleBarError) {
        this.phHandleBarError = phHandleBarError;
        return this;
    }

    public int getPhControlError() {
        return phControlError;
    }

    public PHSqlPackage setPhControlError(int phControlError) {
        this.phControlError = phControlError;
        return this;
    }

    public Integer getPhHold() {
        return phHold;
    }

    public PHSqlPackage setPhHold(Integer phHold) {
        this.phHold = phHold;
        return this;
    }

    public int getPhSoc() {
        return phSoc;
    }

    public PHSqlPackage setPhSoc(int phSoc) {
        this.phSoc = phSoc;
        return this;
    }
}
