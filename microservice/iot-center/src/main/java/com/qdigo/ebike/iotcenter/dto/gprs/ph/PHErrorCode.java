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

public class PHErrorCode {
	private byte phMachineError;
	private byte phBrakeErroe;
	private byte phHandleBarError;
	private byte phControlError;
	private byte phHold;
	public byte getPhMachineError() {
		return phMachineError;
	}
	public void setPhMachineError(byte phMachineError) {
		this.phMachineError = phMachineError;
	}
	public byte getPhBrakeErroe() {
		return phBrakeErroe;
	}
	public void setPhBrakeErroe(byte phBrakeErroe) {
		this.phBrakeErroe = phBrakeErroe;
	}
	public byte getPhHandleBarError() {
		return phHandleBarError;
	}
	public void setPhHandleBarError(byte phHandleBarError) {
		this.phHandleBarError = phHandleBarError;
	}
	public byte getPhControlError() {
		return phControlError;
	}
	public void setPhControlError(byte phControlError) {
		this.phControlError = phControlError;
	}
	public byte getPhHold() {
		return phHold;
	}
	public void setPhHold(byte phHold) {
		this.phHold = phHold;
	}
	@Override
	public String toString() {
		return "PHErrorCode [phMachineError=" + phMachineError + ", phBrakeErroe=" + phBrakeErroe
				+ ", phHandleBarError=" + phHandleBarError + ", phControlError=" + phControlError + ", phHold=" + phHold
				+ "]";
	}
	
	
}
