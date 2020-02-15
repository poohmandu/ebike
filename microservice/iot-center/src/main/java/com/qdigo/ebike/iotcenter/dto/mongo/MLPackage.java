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

package com.qdigo.ebike.iotcenter.dto.mongo;


import java.util.Date;


public class MLPackage {

	private String id;


	private String mlImei;
	private String mlLAC;
	private String mlCellid;
	private String mlSingal;
	private Double mlTemperature;
	private Long mlImsi;
    private Long  timestamp=new Date().getTime();

    public String getId() {
        return id;
    }

    public MLPackage setId(String id) {
        this.id = id;
        return this;
    }

    public String getMlImei() {
        return mlImei;
    }

    public MLPackage setMlImei(String mlImei) {
        this.mlImei = mlImei;
        return this;
    }

    public String getMlLAC() {
        return mlLAC;
    }

    public MLPackage setMlLAC(String mlLAC) {
        this.mlLAC = mlLAC;
        return this;
    }

    public String getMlCellid() {
        return mlCellid;
    }

    public MLPackage setMlCellid(String mlCellid) {
        this.mlCellid = mlCellid;
        return this;
    }

    public String getMlSingal() {
        return mlSingal;
    }

    public MLPackage setMlSingal(String mlSingal) {
        this.mlSingal = mlSingal;
        return this;
    }

    public Double getMlTemperature() {
        return mlTemperature;
    }

    public MLPackage setMlTemperature(Double mlTemperature) {
        this.mlTemperature = mlTemperature;
        return this;
    }

    public Long getMlImsi() {
        return mlImsi;
    }

    public MLPackage setMlImsi(Long mlImsi) {
        this.mlImsi = mlImsi;
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public MLPackage setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
