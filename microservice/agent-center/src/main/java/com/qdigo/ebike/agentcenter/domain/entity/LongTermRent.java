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

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

/**
 * Created by niezhao on 2017/7/20.
 *  一般骑行卡配置
 */
@Entity
@Table(name = "long_term_rent")
public class LongTermRent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(unique = true, nullable = false, name = "agent_id",
        foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Agent agent;

    private double dayCard;

    private double weekCard;

    private double monthCard;

    public double getDayCard() {
        return dayCard;
    }

    public LongTermRent setDayCard(double dayCard) {
        this.dayCard = dayCard;
        return this;
    }

    public long getId() {
        return id;
    }

    public LongTermRent setId(long id) {
        this.id = id;
        return this;
    }

    public Agent getAgent() {
        return agent;
    }

    public LongTermRent setAgent(Agent agent) {
        this.agent = agent;
        return this;
    }

    public double getWeekCard() {
        return weekCard;
    }

    public LongTermRent setWeekCard(double weekCard) {
        this.weekCard = weekCard;
        return this;
    }

    public double getMonthCard() {
        return monthCard;
    }

    public LongTermRent setMonthCard(double monthCard) {
        this.monthCard = monthCard;
        return this;
    }

}
