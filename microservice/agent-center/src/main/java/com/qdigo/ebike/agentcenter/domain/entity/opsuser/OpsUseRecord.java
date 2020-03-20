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
import java.util.Date;

/**
 * Created by niezhao on 2017/11/17.
 */
@Entity
@Table(name = "ops_use_record")
public class OpsUseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "ops_user", nullable = false)
    private String opsUser;

    @Column(name = "imei", nullable = false)
    private String imei;

    @Column(nullable = false, length = 25)
    private String useStatus;

    @Column(nullable = false)
    private Date startTime;

    private Date endTime;

    public long getId() {
        return id;
    }

    public OpsUseRecord setId(long id) {
        this.id = id;
        return this;
    }

    public String getOpsUser() {
        return opsUser;
    }

    public OpsUseRecord setOpsUser(String opsUser) {
        this.opsUser = opsUser;
        return this;
    }

    public String getImei() {
        return imei;
    }

    public OpsUseRecord setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public String getUseStatus() {
        return useStatus;
    }

    public OpsUseRecord setUseStatus(String useStatus) {
        this.useStatus = useStatus;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public OpsUseRecord setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public OpsUseRecord setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }
}
