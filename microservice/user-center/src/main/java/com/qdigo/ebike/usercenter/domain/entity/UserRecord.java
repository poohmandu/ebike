/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.usercenter.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by niezhao on 2016/11/27.
 */
@Entity
@Table(name = "user_record")
public class UserRecord extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userRecordId;
    @NotNull
    private String record;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date curTime = new Date();

    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Long getUserRecordId() {
        return userRecordId;
    }

    public UserRecord setUserRecordId(Long userRecordId) {
        this.userRecordId = userRecordId;
        return this;
    }

    public String getRecord() {
        return record;
    }

    public UserRecord setRecord(String record) {
        this.record = record;
        return this;
    }

    public Date getCurTime() {
        return curTime;
    }

    public UserRecord setCurTime(Date curTime) {
        this.curTime = curTime;
        return this;
    }

    public User getUser() {
        return user;
    }

    public UserRecord setUser(User user) {
        this.user = user;
        return this;
    }
}
