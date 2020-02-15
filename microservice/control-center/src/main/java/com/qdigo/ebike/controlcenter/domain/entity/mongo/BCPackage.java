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

import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by niezhao on 2017/11/21.
 */
@ToString
@Document(collection = "BCPackage")
public class BCPackage {

    @Id
    private String id;

    private String bcImei;

    private String mobileNo;

    private String cmd;

    private boolean success;

    private String failMsg;

    private long timestamp = System.currentTimeMillis();

    public long getTimestamp() {
        return timestamp;
    }

    public BCPackage setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getId() {
        return id;
    }

    public BCPackage setId(String id) {
        this.id = id;
        return this;
    }

    public String getBcImei() {
        return bcImei;
    }

    public BCPackage setBcImei(String bcImei) {
        this.bcImei = bcImei;
        return this;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public BCPackage setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public String getCmd() {
        return cmd;
    }

    public BCPackage setCmd(String cmd) {
        this.cmd = cmd;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public BCPackage setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public BCPackage setFailMsg(String failMsg) {
        this.failMsg = failMsg;
        return this;
    }

}
