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

@Entity
@Table(name = "agent_area_point")
public class AgentAreaPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "point_index")
    private int pointIndex;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, name = "area_id")
    private AgentArea agentArea;

    private double longitude;

    private double latitude;

    public long getId() {
        return id;
    }

    public AgentAreaPoint setId(long id) {
        this.id = id;
        return this;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public AgentAreaPoint setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
        return this;
    }

    public AgentArea getAgentArea() {
        return agentArea;
    }

    public AgentAreaPoint setAgentArea(AgentArea agentArea) {
        this.agentArea = agentArea;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public AgentAreaPoint setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public AgentAreaPoint setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }
}
