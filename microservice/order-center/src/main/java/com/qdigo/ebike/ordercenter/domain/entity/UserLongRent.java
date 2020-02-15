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

package com.qdigo.ebike.ordercenter.domain.entity;

import com.qdigo.ebike.common.core.constants.Const;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by niezhao on 2017/7/20.
 */
@Entity
@ToString
@Table(name = "user_long_rent", indexes = {@Index(columnList = "user_id"), @Index(columnList = "end_time")})
public class UserLongRent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(nullable = false)
    private long agentId;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private double consume;

    @Column(nullable = false, name = "startTime")
    private Date startTime;

    @Column(nullable = false, name = "end_time")
    private Date endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Const.LongRentType longRentType;

    private String imei;//longRentType为外卖时需要

    public String getImei() {
        return imei;
    }

    public UserLongRent setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public UserLongRent setPrice(double price) {
        this.price = price;
        return this;
    }

    public Long getId() {
        return id;
    }

    public UserLongRent setId(Long id) {
        this.id = id;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public UserLongRent setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public long getAgentId() {
        return agentId;
    }

    public UserLongRent setAgentId(long agentId) {
        this.agentId = agentId;
        return this;
    }

    public double getConsume() {
        return consume;
    }

    public UserLongRent setConsume(double consume) {
        this.consume = consume;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public UserLongRent setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public UserLongRent setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Const.LongRentType getLongRentType() {
        return longRentType;
    }

    public UserLongRent setLongRentType(Const.LongRentType longRentType) {
        this.longRentType = longRentType;
        return this;
    }

}
