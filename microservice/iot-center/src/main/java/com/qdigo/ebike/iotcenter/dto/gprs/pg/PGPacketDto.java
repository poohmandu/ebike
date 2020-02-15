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

package com.qdigo.ebike.iotcenter.dto.gprs.pg;


import com.qdigo.ebike.iotcenter.dto.DatagramPacketBasicDto;
import com.qdigo.ebike.iotcenter.dto.gprs.GPRSSubStatus;


public class PGPacketDto extends DatagramPacketBasicDto {

	private static final long serialVersionUID = -8597161566517930453L;
	//??????????
	private int length;
	// ????(6��)
	private float lng;
	// ?????6��??
	private float lat;
	// ???��??
	private short hight;
	// ????(2��)
	private float speed;
	// ???? --????bit?????????
	private byte status;
	
	private byte star;
	
	private GPRSSubStatus pgSubStatus;

	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public float getLng() {
		return lng;
	}
	public void setLng(float lng) {
		this.lng = lng;
	}
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public short getHight() {
		return hight;
	}
	public void setHight(short hight) {
		this.hight = hight;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public byte getStar() {
		return star;
	}
	public void setStar(byte star) {
		this.star = star;
	}
	public GPRSSubStatus getPgSubStatus() {
		return pgSubStatus;
	}
	public void setPgSubStatus(GPRSSubStatus pgSubStatus) {
		this.pgSubStatus = pgSubStatus;
	}
	@Override
	public String toString() {
		return "PGPacketDto [length=" + length +", getHeader1()=" + getHeader1() + ", getImei()=" + getImei()+ ", lng=" + lng + ", lat=" + lat + ", hight=" + hight + ", speed="
				+ speed + ", status=" + status + ", star=" + star + ", pgSubStatus=" + pgSubStatus.toString() + "]";
	}
	
	
	
}
