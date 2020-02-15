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

package com.qdigo.ebike.third.domain.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "dh_sms_record")
public class SmsRecord {

    public enum Type {
        login, noPowerWarn, insurance, joint
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long smsRecordId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String target;

    private Long agentId;

    @Column(nullable = false)
    private Date sendTime;

    @Column(nullable = false)
    private Boolean succeed;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    private Double amount;

    public Double getAmount() {
        return amount;
    }

    public SmsRecord setAmount(Double amount) {
        this.amount = amount;
        return this;
    }

    public Long getSmsRecordId() {
        return smsRecordId;
    }

    public SmsRecord setSmsRecordId(Long smsRecordId) {
        this.smsRecordId = smsRecordId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SmsRecord setContent(String content) {
        this.content = content;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public SmsRecord setTarget(String target) {
        this.target = target;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public SmsRecord setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public SmsRecord setSendTime(Date sendTime) {
        this.sendTime = sendTime;
        return this;
    }

    public Boolean getSucceed() {
        return succeed;
    }

    public SmsRecord setSucceed(Boolean succeed) {
        this.succeed = succeed;
        return this;
    }

    public Type getType() {
        return type;
    }

    public SmsRecord setType(Type type) {
        this.type = type;
        return this;
    }
}
