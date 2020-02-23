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


public class PCPackage {

    private String id;

    private String pcImei;
    private int pcCmd;
    private String pcParam;
    private long pcSequence;
    private long timestamp = System.currentTimeMillis();

    public String getId() {
        return id;
    }

    public PCPackage setId(String id) {
        this.id = id;
        return this;
    }

    public String getPcImei() {
        return pcImei;
    }

    public PCPackage setPcImei(String pcImei) {
        this.pcImei = pcImei;
        return this;
    }

    public int getPcCmd() {
        return pcCmd;
    }

    public PCPackage setPcCmd(int pcCmd) {
        this.pcCmd = pcCmd;
        return this;
    }

    public String getPcParam() {
        return pcParam;
    }

    public PCPackage setPcParam(String pcParam) {
        this.pcParam = pcParam;
        return this;
    }

    public long getPcSequence() {
        return pcSequence;
    }

    public PCPackage setPcSequence(long pcSequence) {
        this.pcSequence = pcSequence;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public PCPackage setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
