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

package com.qdigo.ebike.agentcenter.domain.entity.opsuser.record;

import com.qdigo.ebike.common.core.constants.Const;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * Created by niezhao on 2017/8/28.
 */
@Document(collection = "WarnRecord")
public class WarnRecord {

    @Id
    private String id;

    @NotNull
    private String messageId;

    @NotNull
    private String imei;

    @NotNull
    private String deviceId;

    @NotNull
    private String alert;

    @NotNull
    private Date pushTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Const.MailType type;

    private String pushTarget;

    public String getId() {
        return id;
    }

    public WarnRecord setId(String id) {
        this.id = id;
        return this;
    }

    public String getMessageId() {
        return messageId;
    }

    public WarnRecord setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public String getImei() {
        return imei;
    }

    public WarnRecord setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public WarnRecord setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getAlert() {
        return alert;
    }

    public WarnRecord setAlert(String alert) {
        this.alert = alert;
        return this;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public WarnRecord setPushTime(Date pushTime) {
        this.pushTime = pushTime;
        return this;
    }

    public Const.MailType getType() {
        return type;
    }

    public WarnRecord setType(Const.MailType type) {
        this.type = type;
        return this;
    }

    public String getPushTarget() {
        return pushTarget;
    }

    public WarnRecord setPushTarget(String pushTarget) {
        this.pushTarget = pushTarget;
        return this;
    }
}
