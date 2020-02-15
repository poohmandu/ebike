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
public enum BikeStatusEnum {
	IMEI("IMEI","设备唯一编号后8位"),
	PG_LASTTIME("PG_LastTime","设备唯一编号后8位"),
	PH_LASTTIME("PH_LastTime","设备唯一编号后8位"),
	PL_LASTTIME("PL_LastTime","设备唯一编号后8位"),
	UP_PC_LASTTIME("UP_PC_LastTime","设备唯一编号后8位"),
	UP_PC_TYPE("UP_PC_TYPE","设备唯一编号后8位"),
	DOWN_PC_LASTTIME("DOWN_PC_LastTime","设备唯一编号后8位"),
	DOWN_PC_TYPE("DOWN_PC_TYPE","设备唯一编号后8位"),
	AVAILABLE_SLAVE("AVAILABLE_SLAVE","设备通信所在socket服务的ip地址"),

	MONITOR_ALLBIKE_STATUS("Monitor:AllBike_Status:","车列表标识"),
	
	MONITOR_BIKE_STATUS("Monitor:Bike_Status:","车明细标识"),
	;
	
	private String bikeStatus;
	private String dec;
	private BikeStatusEnum(String bikeStatus,String dec){
		this.bikeStatus = bikeStatus;
		this.dec = dec;
	}
	public String getBikeStatus() {
		return bikeStatus;
	}
	public void setBikeStatus(String bikeStatus) {
		this.bikeStatus = bikeStatus;
	}
	public String getDec() {
		return dec;
	}
	public void setDec(String dec) {
		this.dec = dec;
	}
	
	
}
