/*
 * Copyright 2020 聂钊 nz@qdigo.com
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

package com.qdigo.ebike.agentcenter.domain.entity;

import javax.persistence.*;

@Entity
@Table(name = "agent_joint")
public class AgentJoint {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "agent_joint_id")
    private Long agentJointId;

    private String name;

    private String mobileNo;

    private String city;

    private String type;

    private String note;

    private Long createTime;

    private Integer amount;//单位:分

    @Column(nullable = false, columnDefinition = "bit(1) default 0")
    private Boolean isDeleted;

    private String record;

    public Integer getAmount() {
        return amount;
    }

    public AgentJoint setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public AgentJoint setDeleted(Boolean deleted) {
        isDeleted = deleted;
        return this;
    }

    public String getRecord() {
        return record;
    }

    public AgentJoint setRecord(String record) {
        this.record = record;
        return this;
    }

    public String getType() {
        return type;
    }

    public AgentJoint setType(String type) {
        this.type = type;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public AgentJoint setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public Long getAgentJointId() {
        return agentJointId;
    }

    public AgentJoint setAgentJointId(Long agentJointId) {
        this.agentJointId = agentJointId;
        return this;
    }

    public String getName() {
        return name;
    }

    public AgentJoint setName(String name) {
        this.name = name;
        return this;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public AgentJoint setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public String getCity() {
        return city;
    }

    public AgentJoint setCity(String city) {
        this.city = city;
        return this;
    }

    public String getNote() {
        return note;
    }

    public AgentJoint setNote(String note) {
        this.note = note;
        return this;
    }
}
