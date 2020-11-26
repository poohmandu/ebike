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

package com.qdigo.ebike.api.domain.dto.iot.datagram;


import java.util.Date;

public class MDPackage {

	private String id;
	private String mdImei;
	private Double mdVoltage;//电压
	private Double mdCurrent;//电流
	private Integer mdState;//充电状态:0不在充电 1:恒流充电
	private Integer mdPortNumber; //一个充电桩有6个充电口,该充电口的编号
	private Integer mdChargeError; // 0 没有故障 ,其他为故障 20161208
	private String mdPortBikeNumber; //正在充电的车辆imei号
    private Long  timestamp=new Date().getTime();


    public String getId() {
        return id;
    }

    public MDPackage setId(String id) {
        this.id = id;
        return this;
    }

    public String getMdImei() {
        return mdImei;
    }

    public MDPackage setMdImei(String mdImei) {
        this.mdImei = mdImei;
        return this;
    }

    public Double getMdVoltage() {
        return mdVoltage;
    }

    public MDPackage setMdVoltage(Double mdVoltage) {
        this.mdVoltage = mdVoltage;
        return this;
    }

    public Double getMdCurrent() {
        return mdCurrent;
    }

    public MDPackage setMdCurrent(Double mdCurrent) {
        this.mdCurrent = mdCurrent;
        return this;
    }

    public Integer getMdState() {
        return mdState;
    }

    public MDPackage setMdState(Integer mdState) {
        this.mdState = mdState;
        return this;
    }

    public Integer getMdPortNumber() {
        return mdPortNumber;
    }

    public MDPackage setMdPortNumber(Integer mdPortNumber) {
        this.mdPortNumber = mdPortNumber;
        return this;
    }

    public Integer getMdChargeError() {
        return mdChargeError;
    }

    public MDPackage setMdChargeError(Integer mdChargeError) {
        this.mdChargeError = mdChargeError;
        return this;
    }

    public String getMdPortBikeNumber() {
        return mdPortBikeNumber;
    }

    public MDPackage setMdPortBikeNumber(String mdPortBikeNumber) {
        this.mdPortBikeNumber = mdPortBikeNumber;
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public MDPackage setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

}
