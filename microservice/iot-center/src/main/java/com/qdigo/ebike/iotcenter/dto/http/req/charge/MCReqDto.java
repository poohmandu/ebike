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

public class MCReqDto {

    private int mcImei;
    private int mcCmd;
    private int mcSequence;
    private String mcParam;
    private String mcServer;
    private String mcClient;

    public String getMcServer() {
        return mcServer;
    }

    public MCReqDto setMcServer(String mcServer) {
        this.mcServer = mcServer;
        return this;
    }

    public String getMcClient() {
        return mcClient;
    }

    public MCReqDto setMcClient(String mcClient) {
        this.mcClient = mcClient;
        return this;
    }

    public int getMcImei() {
        return mcImei;
    }

    public void setMcImei(int mcImei) {
        this.mcImei = mcImei;
    }

    public int getMcCmd() {
        return mcCmd;
    }

    public void setMcCmd(int mcCmd) {
        this.mcCmd = mcCmd;
    }

    public int getMcSequence() {
        return mcSequence;
    }

    public void setMcSequence(int mcSequence) {
        this.mcSequence = mcSequence;
    }

    public String getMcParam() {
        return mcParam;
    }

    public void setMcParam(String mcParam) {
        this.mcParam = mcParam;
    }


}
