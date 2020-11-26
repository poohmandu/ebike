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
import java.util.Date;

/**
 * Created by niezhao on 2017/2/23.
 */
@Entity
@Table(name = "user_credit_record")
public class UserCreditRecord extends AbstractAuditingEntity {

    private static final long serialVersionUID = -4302558477433716740L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userCreditRecordId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(length = 20, nullable = false)
    private Date eventTime;

    @Column(length = 20, nullable = false)
    private String eventInfo; //见枚举

    @Column(length = 5, nullable = false)
    private int scoreChange;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_credit_id", nullable = false)
    private UserCredit userCredit;

    public long getUserCreditRecordId() {
        return userCreditRecordId;
    }

    public UserCreditRecord setUserCreditRecordId(long userCreditRecordId) {
        this.userCreditRecordId = userCreditRecordId;
        return this;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public UserCreditRecord setEventTime(Date eventTime) {
        this.eventTime = eventTime;
        return this;
    }

    public String getEventInfo() {
        return eventInfo;
    }

    public UserCreditRecord setEventInfo(String eventInfo) {
        this.eventInfo = eventInfo;
        return this;
    }

    public int getScoreChange() {
        return scoreChange;
    }

    public UserCreditRecord setScoreChange(int scoreChange) {
        this.scoreChange = scoreChange;
        return this;
    }

    public UserCredit getUserCredit() {
        return userCredit;
    }

    public UserCreditRecord setUserCredit(UserCredit userCredit) {
        this.userCredit = userCredit;
        return this;
    }
}
