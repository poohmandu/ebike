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

package com.qdigo.ebike.ordercenter.domain.entity;

import com.qdigo.ebike.common.core.constants.Status;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by niezhao on 2017/6/9.
 */
@Entity
@Table(name = "journal_account")
@ToString
public class JournalAccount {

    @Id  // 1|2|3 170610 00000001
    private long journalAccountId;

    @Column(name = "mobile_no")
    private String mobileNo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status.PayType payType;

    private Double amount;

    private double startAccount;

    private Double endAccount;

    @NotNull
    private Date startTime;

    private Date endTime;

    @Column(name = "agent_agent_id")
    private Long agentId;

    @Column(name = "ride_record_ride_record_id")
    private Long rideRecordId;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "long_rent_id")
    private Long longRentId;

    public long getJournalAccountId() {
        return journalAccountId;
    }

    public JournalAccount setJournalAccountId(long journalAccountId) {
        this.journalAccountId = journalAccountId;
        return this;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public JournalAccount setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public Status.PayType getPayType() {
        return payType;
    }

    public JournalAccount setPayType(Status.PayType payType) {
        this.payType = payType;
        return this;
    }

    public Double getAmount() {
        return amount;
    }

    public JournalAccount setAmount(Double amount) {
        this.amount = amount;
        return this;
    }

    public double getStartAccount() {
        return startAccount;
    }

    public JournalAccount setStartAccount(double startAccount) {
        this.startAccount = startAccount;
        return this;
    }

    public Double getEndAccount() {
        return endAccount;
    }

    public JournalAccount setEndAccount(Double endAccount) {
        this.endAccount = endAccount;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public JournalAccount setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public JournalAccount setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public JournalAccount setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public Long getRideRecordId() {
        return rideRecordId;
    }

    public JournalAccount setRideRecordId(Long rideRecordId) {
        this.rideRecordId = rideRecordId;
        return this;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public JournalAccount setOrderNo(String orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public Long getLongRentId() {
        return longRentId;
    }

    public JournalAccount setLongRentId(Long longRentId) {
        this.longRentId = longRentId;
        return this;
    }
}
