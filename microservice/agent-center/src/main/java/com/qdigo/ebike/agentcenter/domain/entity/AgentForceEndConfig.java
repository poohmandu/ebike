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

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "agent_force_end_config", indexes = {@Index(columnList = "agent_id", unique = true)})
public class AgentForceEndConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true, name = "agent_id")
    private long agentId;

    private boolean valid; // 是否生效

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Type type; // 计费类型

    private double levelOne = 20; //元,5公里内

    private int levelOneKm = 5;

    private double levelTwo = 40; //元,15公里内

    private int levelTwoKm = 15;

    private double levelThree = 80; //超过15公里,且在服务区内

    private double linePrice = 0.5; //linePrice=0.5,lineMeter=100 每100米需支付0.5元

    private int lineMeter = 100; //米,非0

    public enum Type {
        ladder, linear
    }
}
