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

package com.qdigo.ebike.iotcenter.dto.gprs;

import java.io.Serializable;

public class GPRSSubStatus implements Serializable {
	private static final long serialVersionUID = -5371736380827965009L;
	//外接电源（串口通讯） bit0
	private byte communicationStatus;
	//电门锁开关  bit1
	private byte switchStatus;
	//是否锁车 bit2
	private byte LockStatus;
	// 是否有震动 bit3
	private byte shockStatus;
	//是否轮车输入 bit4
	private byte inputStatus;
	// 是否自动 bit5
	private byte autoLockStatus;
	// 是否跌倒  bit6
	private byte fallStatus;
	// 是否故障 bit7
	private byte troubleStatus;
	public byte getCommunicationStatus() {
		return communicationStatus;
	}
	public void setCommunicationStatus(byte communicationStatus) {
		this.communicationStatus = communicationStatus;
	}
	public byte getSwitchStatus() {
		return switchStatus;
	}
	public void setSwitchStatus(byte switchStatus) {
		this.switchStatus = switchStatus;
	}
	public byte getLockStatus() {
		return LockStatus;
	}
	public void setLockStatus(byte lockStatus) {
		LockStatus = lockStatus;
	}
	public byte getShockStatus() {
		return shockStatus;
	}
	public void setShockStatus(byte shockStatus) {
		this.shockStatus = shockStatus;
	}
	public byte getInputStatus() {
		return inputStatus;
	}
	public void setInputStatus(byte inputStatus) {
		this.inputStatus = inputStatus;
	}
	public byte getAutoLockStatus() {
		return autoLockStatus;
	}
	public void setAutoLockStatus(byte autoLockStatus) {
		this.autoLockStatus = autoLockStatus;
	}
	public byte getFallStatus() {
		return fallStatus;
	}
	public void setFallStatus(byte fallStatus) {
		this.fallStatus = fallStatus;
	}
	public byte getTroubleStatus() {
		return troubleStatus;
	}
	public void setTroubleStatus(byte troubleStatus) {
		this.troubleStatus = troubleStatus;
	}
	@Override
	public String toString() {
		return "PGSubStatus [communicationStatus=" + communicationStatus + ", switchStatus=" + switchStatus
				+ ", LockStatus=" + LockStatus + ", shockStatus=" + shockStatus + ", inputStatus=" + inputStatus
				+ ", autoLockStatus=" + autoLockStatus + ", fallStatus=" + fallStatus + ", troubleStatus="
				+ troubleStatus + "]";
	}
	
	
}