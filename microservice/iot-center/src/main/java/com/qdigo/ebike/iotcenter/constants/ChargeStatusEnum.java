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

package com.qdigo.ebike.iotcenter.constants;

/**
 * 监控车通信信息
 * @author yudengqiu
 *
 */
public enum ChargeStatusEnum {
	IMEI("IMEI","充电桩唯一编号后8位"),
	MD_LASTTIME("MD_LastTime","最近一次上报的MD包时间"),
	ML_LASTTIME("ML_LastTime","最近一次上报的ML包时间"),
	UP_MC_LASTTIME("UP_MC_LastTime","最近一次上报的MC包时间"),
	UP_MC_TYPE("UP_MC_TYPE","最近一次上报的MC指令"),
	DOWN_MC_LASTTIME("DOWN_MC_LastTime","桩发送的下行指令的时间"),
	DOWN_MC_TYPE("DOWN_MC_TYPE","桩发送的下行指令"),
	CHARGING_BIKE_IMEILIST("ChargingBikeIMEIList","正在充电的车的IMEI号"),
	AVAILABLE_SLAVE("AVAILABLE_SLAVE","设备通信所在socket服务的ip地址"),
	
	MONITOR_ALLCHARGERPILE_STATUS("Monitor:AllChargerPile_Status:","充电桩列表标识"),
	MONITOR_CHARGERPILE_STATUS("Monitor:ChargerPile_Status:","充电桩明细标识"),
	;
	
	private String chargeStatus;
	private String dec;
	private ChargeStatusEnum(String chargeStatus,String dec){
		this.chargeStatus = chargeStatus;
		this.dec = dec;
	}
	
	public String getChargeStatus() {
		return chargeStatus;
	}

	public void setChargeStatus(String chargeStatus) {
		this.chargeStatus = chargeStatus;
	}

	public String getDec() {
		return dec;
	}
	public void setDec(String dec) {
		this.dec = dec;
	}
	
	
}
