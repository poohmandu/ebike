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

package com.qdigo.ebike.controlcenter.domain.entity.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "MCPackage")
public class MCPackage {
    @Id
    private String id;

    private String mcImei;
    private Integer mcCmd;
    private Integer mcSequence;
    private String mcParam;
    private Long timestamp = System.currentTimeMillis();
    @NotNull
    private String mcServer;
    @NotNull
    private String mcClient;

    public String getMcServer() {
        return mcServer;
    }

    public MCPackage setMcServer(String mcServer) {
        this.mcServer = mcServer;
        return this;
    }

    public String getMcClient() {
        return mcClient;
    }

    public MCPackage setMcClient(String mcClient) {
        this.mcClient = mcClient;
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMcImei() {
        return mcImei;
    }

    public void setMcImei(String mcImei) {
        this.mcImei = mcImei;
    }

    public Integer getMcCmd() {
        return mcCmd;
    }

    public void setMcCmd(Integer mcCmd) {
        this.mcCmd = mcCmd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMcSequence() {
        return mcSequence;
    }

    public void setMcSequence(Integer mcSequence) {
        this.mcSequence = mcSequence;
    }

    public String getMcParam() {
        return mcParam;
    }

    public void setMcParam(String mcParam) {
        this.mcParam = mcParam;
    }

}
