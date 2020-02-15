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

package com.qdigo.ebike.iotcenter.dto.http.req.gprs;

public class PCReqDto {

    private int pcImei;

    private int pcCmd;

    private String pcParam;

    private int pcSequence;

    private String pcClient;
    private String pcServer;

    public String getPcClient() {
        return pcClient;
    }

    public PCReqDto setPcClient(String pcClient) {
        this.pcClient = pcClient;
        return this;
    }

    public String getPcServer() {
        return pcServer;
    }

    public PCReqDto setPcServer(String pcServer) {
        this.pcServer = pcServer;
        return this;
    }

    public int getPcImei() {
        return pcImei;
    }

    public void setPcImei(int pcImei) {
        this.pcImei = pcImei;
    }

    public int getPcCmd() {
        return pcCmd;
    }

    public void setPcCmd(int pcCmd) {
        this.pcCmd = pcCmd;
    }

    public String getPcParam() {
        return pcParam;
    }

    public void setPcParam(String pcParam) {
        this.pcParam = pcParam;
    }

    public int getPcSequence() {
        return pcSequence;
    }

    public void setPcSequence(int pcSequence) {
        this.pcSequence = pcSequence;
    }


}
