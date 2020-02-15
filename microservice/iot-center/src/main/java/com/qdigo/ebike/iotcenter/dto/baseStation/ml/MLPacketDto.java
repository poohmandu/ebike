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

package com.qdigo.ebike.iotcenter.dto.baseStation.ml;

import com.qdigo.ebike.iotcenter.dto.DatagramPacketBasicDto;

/**
 * ��վ��λ��ݰ�
 * ML ���Ӧ�Ĳ���
 * @author yudengqiu
 *
 */
public class MLPacketDto extends DatagramPacketBasicDto {

	private static final long serialVersionUID = -378595322683420831L;
	//��ݰ�ĳ���
	private int length;
	// λ��������
	private short lac;
	// ��վС����
	private int cellid;
	//�ź�ǿ��
	private byte signal;
	//�¶�
	private byte temperature;
	// ���׮IMSI
	private Long imsi;
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public short getLac() {
		return lac;
	}
	public void setLac(short lac) {
		this.lac = lac;
	}
	public int getCellid() {
		return cellid;
	}
	public void setCellid(int cellid) {
		this.cellid = cellid;
	}
	public byte getSignal() {
		return signal;
	}
	public void setSignal(byte signal) {
		this.signal = signal;
	}
	public byte getTemperature() {
		return temperature;
	}
	public void setTemperature(byte temperature) {
		this.temperature = temperature;
	}
	public Long getImsi() {
		return imsi;
	}
	public void setImsi(Long imsi) {
		this.imsi = imsi;
	}
	@Override
	public String toString() {
		return "MLPacketDto [length=" + length + ", lac=" + lac + ", cellid=" + cellid + ", signal=" + signal
				+ ", temperature=" + temperature + ", imsi=" + imsi + ", getHeader0()=" + getHeader0()
				+ ", getHeader1()=" + getHeader1() + ", getImei()=" + getImei() + "]";
	}
	
	
}
