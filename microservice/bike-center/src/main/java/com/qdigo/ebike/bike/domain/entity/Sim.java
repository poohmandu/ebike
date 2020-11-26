/*
 * Copyright 2019 聂钊 nz@qdigo.com
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

package com.qdigo.ebike.bike.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by niezhao on 2017/7/11.
 */
@Entity
@Table(name = "sim")
public class Sim {

    @Id
    private long imsi;

    @Column(unique = true, nullable = false)
    private long simNO;

    @Column(unique = true, nullable = false, length = 50)
    private String ICCID;

    private String simType;

    public String getSimType() {
        return simType;
    }

    public Sim setSimType(String simType) {
        this.simType = simType;
        return this;
    }

    public long getImsi() {
        return imsi;
    }

    public Sim setImsi(long imsi) {
        this.imsi = imsi;
        return this;
    }

    public long getSimNO() {
        return simNO;
    }

    public Sim setSimNO(long simNO) {
        this.simNO = simNO;
        return this;
    }

    public String getICCID() {
        return ICCID;
    }

    public Sim setICCID(String ICCID) {
        this.ICCID = ICCID;
        return this;
    }
}
