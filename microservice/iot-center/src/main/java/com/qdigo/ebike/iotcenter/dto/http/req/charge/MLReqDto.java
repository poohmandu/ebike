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

public class MLReqDto {

    private int mlImei;
    private int mlLAC;
    private int mlCellid;
    private int mlSingal;
    private int mlTemperature;
    private Long mlImsi;
    private String mlServer;
    private String mlClient;

    public String getMlServer() {
        return mlServer;
    }

    public MLReqDto setMlServer(String mlServer) {
        this.mlServer = mlServer;
        return this;
    }

    public String getMlClient() {
        return mlClient;
    }

    public MLReqDto setMlClient(String mlClient) {
        this.mlClient = mlClient;
        return this;
    }

    public int getMlImei() {
        return mlImei;
    }

    public void setMlImei(int mlImei) {
        this.mlImei = mlImei;
    }

    public int getMlLAC() {
        return mlLAC;
    }

    public void setMlLAC(int mlLAC) {
        this.mlLAC = mlLAC;
    }

    public int getMlCellid() {
        return mlCellid;
    }

    public void setMlCellid(int mlCellid) {
        this.mlCellid = mlCellid;
    }

    public int getMlSingal() {
        return mlSingal;
    }

    public void setMlSingal(int mlSingal) {
        this.mlSingal = mlSingal;
    }

    public int getMlTemperature() {
        return mlTemperature;
    }

    public void setMlTemperature(int mlTemperature) {
        this.mlTemperature = mlTemperature;
    }

    public Long getMlImsi() {
        return mlImsi;
    }

    public void setMlImsi(Long mlImsi) {
        this.mlImsi = mlImsi;
    }

}
