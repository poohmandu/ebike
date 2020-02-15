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

package com.qdigo.ebike.iotcenter.dto.http.req.charge;

public class MDReqDto {
    private int mdImei;
    // 电压
    private double mdVoltage;
    // 电流
    private double mdCurrent;
    private int mdState;
    // 充电口编号
    private int mdPortNumber;
    // 充电器故障代号
    private int mdChargeError;
    // 充电口车辆ID号
    private int mdPortBikeNumber;
    private String mdServer;
    private String mdClient;

    public String getMdServer() {
        return mdServer;
    }

    public MDReqDto setMdServer(String mdServer) {
        this.mdServer = mdServer;
        return this;
    }

    public String getMdClient() {
        return mdClient;
    }

    public MDReqDto setMdClient(String mdClient) {
        this.mdClient = mdClient;
        return this;
    }

    public int getMdImei() {
        return mdImei;
    }

    public void setMdImei(int mdImei) {
        this.mdImei = mdImei;
    }

    public double getMdVoltage() {
        return mdVoltage;
    }

    public void setMdVoltage(double mdVoltage) {
        this.mdVoltage = mdVoltage;
    }

    public double getMdCurrent() {
        return mdCurrent;
    }

    public void setMdCurrent(double mdCurrent) {
        this.mdCurrent = mdCurrent;
    }

    public int getMdState() {
        return mdState;
    }

    public void setMdState(int mdState) {
        this.mdState = mdState;
    }

    public int getMdPortNumber() {
        return mdPortNumber;
    }

    public void setMdPortNumber(int mdPortNumber) {
        this.mdPortNumber = mdPortNumber;
    }

    public int getMdChargeError() {
        return mdChargeError;
    }

    public void setMdChargeError(int mdChargeError) {
        this.mdChargeError = mdChargeError;
    }

    public int getMdPortBikeNumber() {
        return mdPortBikeNumber;
    }

    public void setMdPortBikeNumber(int mdPortBikeNumber) {
        this.mdPortBikeNumber = mdPortBikeNumber;
    }

}
