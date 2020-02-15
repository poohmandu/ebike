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

package com.qdigo.ebike.iotcenter.dto.gprs.pc;


import com.qdigo.ebike.iotcenter.dto.DatagramPacketBasicDto;

/**
 * PC ����Ӧ�Ĳ���
 * @author yudengqiu
 *
 */
public class PCPacketDto extends DatagramPacketBasicDto {

	private static final long serialVersionUID = -8597161566517930453L;
	//���ݰ��ĳ���
	private int length;
	private byte seq;
	private byte cmd;
	private String param;
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
	public byte getCmd() {
		return cmd;
	}
	public void setCmd(byte cmd) {
		this.cmd = cmd;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	@Override
	public String toString() {
		return "PCPacketDto [length=" + length + ", seq=" + seq + ", cmd=" + cmd + ", param=" + param
				+ ", getHeader0()=" + getHeader0() + ", getHeader1()=" + getHeader1() + ", getImei()=" + getImei()
				+ "]";
	}
	
	
	
}
