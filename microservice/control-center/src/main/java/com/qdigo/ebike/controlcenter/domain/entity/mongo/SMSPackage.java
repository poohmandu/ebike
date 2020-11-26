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

import com.qdigo.ebike.common.core.constants.Const;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

/**
 * Created by niezhao on 2017/7/12.
 */
@Document(collection = "SMSPackage")
public class SMSPackage {

    @Id
    private String id;

    private String imei;
    private String mobileNo;
    private String content;

    private long simNo;
    private long timestamp;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Const.direction direction;

    private String transactionalId;

    public String getTransactionalId() {
        return transactionalId;
    }

    public SMSPackage setTransactionalId(String transactionalId) {
        this.transactionalId = transactionalId;
        return this;
    }

    public Const.direction getDirection() {
        return direction;
    }

    public SMSPackage setDirection(Const.direction direction) {
        this.direction = direction;
        return this;
    }

    public String getId() {
        return id;
    }

    public SMSPackage setId(String id) {
        this.id = id;
        return this;
    }

    public String getImei() {
        return imei;
    }

    public SMSPackage setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public SMSPackage setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SMSPackage setContent(String content) {
        this.content = content;
        return this;
    }

    public long getSimNo() {
        return simNo;
    }

    public SMSPackage setSimNo(long simNo) {
        this.simNo = simNo;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public SMSPackage setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

}
