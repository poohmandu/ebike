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

package com.qdigo.ebike.agentcenter.domain.entity.opsuser;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by niezhao on 2017/3/10.
 */
@Entity
@Table(name = "ops_user")
public class OpsUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long opsUserId;

    @Column(length = 50, nullable = false, unique = true, name = "user_name")
    private String userName;

    @Column(length = 50, nullable = false)
    private String password;

    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    public long getOpsUserId() {
        return opsUserId;
    }

    public OpsUser setOpsUserId(long opsUserId) {
        this.opsUserId = opsUserId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public OpsUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public OpsUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public OpsUser setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

}
