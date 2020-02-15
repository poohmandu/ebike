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

package com.qdigo.ebike.iotcenter.dto.gprs.ph;


import com.qdigo.ebike.iotcenter.dto.DatagramPacketBasicDto;
import com.qdigo.ebike.iotcenter.dto.gprs.GPRSSubStatus;

/**
 * PH ���Ӧ�Ĳ���
 * @author yudengqiu
 *
 */
public class PHPacketDto extends DatagramPacketBasicDto {

	private static final long serialVersionUID = 2152662457803329906L;
	//��ݰ�ĳ���
	private int length;
	
	private byte seq;
	private byte status;
	private long imsi;
	private short powerVoltage;
	private short batteryVotage;
	private byte sensity;
	private byte star;
	private byte ecode;
	private byte soc;
	
	private GPRSSubStatus gprsSubStatus;
	private PHErrorCode phErrorCode;
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public byte getSeq() {
		return seq;
	}
	public void setSeq(byte seq) {
		this.seq = seq;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public long getImsi() {
		return imsi;
	}
	public void setImsi(long imsi) {
		this.imsi = imsi;
	}
	public short getPowerVoltage() {
		return powerVoltage;
	}
	public void setPowerVoltage(short powerVoltage) {
		this.powerVoltage = powerVoltage;
	}
	public short getBatteryVotage() {
		return batteryVotage;
	}
	public void setBatteryVotage(short batteryVotage) {
		this.batteryVotage = batteryVotage;
	}
	public byte getSensity() {
		return sensity;
	}
	public void setSensity(byte sensity) {
		this.sensity = sensity;
	}
	public byte getStar() {
		return star;
	}
	public void setStar(byte star) {
		this.star = star;
	}
	public byte getEcode() {
		return ecode;
	}
	public void setEcode(byte ecode) {
		this.ecode = ecode;
	}
	public byte getSoc() {
		return soc;
	}
	public void setSoc(byte soc) {
		this.soc = soc;
	}
	
	public GPRSSubStatus getGprsSubStatus() {
		return gprsSubStatus;
	}
	public void setGprsSubStatus(GPRSSubStatus gprsSubStatus) {
		this.gprsSubStatus = gprsSubStatus;
	}
	public PHErrorCode getPhErrorCode() {
		return phErrorCode;
	}
	public void setPhErrorCode(PHErrorCode phErrorCode) {
		this.phErrorCode = phErrorCode;
	}
	@Override
	public String toString() {
		return "PHPacketDto [length=" + length + ", seq=" + seq + ", status=" + status + ", imsi=" + imsi
				+ ", powerVoltage=" + powerVoltage + ", batteryVotage=" + batteryVotage + ", sensity=" + sensity
				+ ", star=" + star + ", ecode=" + ecode + ", soc=" + soc + ", gprsSubStatus=" + gprsSubStatus.toString()
				+ ", phErrorCode=" + phErrorCode.toString() + "]";
	}
	
}
