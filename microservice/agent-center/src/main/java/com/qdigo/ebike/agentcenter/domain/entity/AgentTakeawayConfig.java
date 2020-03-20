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

/**
 * 外卖配置
 */
@Entity
@Table(name = "agent_takeaway_config", indexes = {@Index(columnList = "agent_id")})
public class AgentTakeawayConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "agent_id")
    private long agentId;

    private double price; //元

    private int day; //以天为单位

    public long getId() {
        return id;
    }

    public AgentTakeawayConfig setId(long id) {
        this.id = id;
        return this;
    }

    public long getAgentId() {
        return agentId;
    }

    public AgentTakeawayConfig setAgentId(long agentId) {
        this.agentId = agentId;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public AgentTakeawayConfig setPrice(double price) {
        this.price = price;
        return this;
    }

    public int getDay() {
        return day;
    }

    public AgentTakeawayConfig setDay(int day) {
        this.day = day;
        return this;
    }
}
