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

package com.qdigo.ebike.iotcenter.dto.gprs.pl;


import com.qdigo.ebike.iotcenter.dto.DatagramPacketBasicDto;
import com.qdigo.ebike.iotcenter.dto.gprs.GPRSSubStatus;

/**
 * PL ����Ӧ�Ĳ���
 * @author yudengqiu
 *
 */
public class PLPacketDto extends DatagramPacketBasicDto {

	private static final long serialVersionUID = 2152662457803329906L;
	//���ݰ��ĳ���
	private int length;
	//λ��������
	private int lac;
	//��վС�����
	private int cellid;
	// �ź�ǿ��
	private short signal;
	// ��״̬ --��ÿһbit����һ��״̬
	private byte status;
	
	private GPRSSubStatus pgSubStatus;
	
	
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getLac() {
		return lac;
	}
	public void setLac(int lac) {
		this.lac = lac;
	}
	public int getCellid() {
		return cellid;
	}
	public void setCellid(int cellid) {
		this.cellid = cellid;
	}
	public short getSignal() {
		return signal;
	}
	public void setSignal(short signal) {
		this.signal = signal;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public GPRSSubStatus getPgSubStatus() {
		return pgSubStatus;
	}
	public void setPgSubStatus(GPRSSubStatus pgSubStatus) {
		this.pgSubStatus = pgSubStatus;
	}
	@Override
	public String toString() {
		return "PLPacketDto [length=" + length + ", getHeader1()=" + getHeader1() + ", getImei()=" + getImei()+ ", lac=" + lac + ", cellid=" + cellid + ", signal=" + signal
				+ ", status=" + status + ", pgSubStatus=" + pgSubStatus.toString() + ", getHeader0()=" + getHeader0()
				 + "]";
	}
	
	
	
}
