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

package com.qdigo.ebike.stationcenter.domain.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "agent_area", indexes = {@Index(columnList = "agent_id")})
public class AgentArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long areaId;

    @Column(nullable = false, length = 50)
    private String areaName;

    @Column(nullable = false, length = 20, name = "agent_id")
    private Long agentId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agentArea")
    @OrderBy(value = "pointIndex ASC ")
    private List<AgentAreaPoint> points;

    public long getAreaId() {
        return areaId;
    }

    public AgentArea setAreaId(long areaId) {
        this.areaId = areaId;
        return this;
    }

    public String getAreaName() {
        return areaName;
    }

    public AgentArea setAreaName(String areaName) {
        this.areaName = areaName;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public AgentArea setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public List<AgentAreaPoint> getPoints() {
        return points;
    }

    public AgentArea setPoints(List<AgentAreaPoint> points) {
        this.points = points;
        return this;
    }
}
