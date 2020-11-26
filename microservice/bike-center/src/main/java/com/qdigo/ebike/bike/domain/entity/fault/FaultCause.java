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

package com.qdigo.ebike.bike.domain.entity.fault;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by niezhao on 2017/2/21.
 */
@Entity
@Table(name = "fault_cause")
public class FaultCause {
    @Id
    private long faultCauseId;
    @Column(length = 50)
    private String cause;

    @ManyToMany(mappedBy = "faultCauseSet")
    private Set<FaultReport> faultReports;

    public long getFaultCauseId() {
        return faultCauseId;
    }

    public FaultCause setFaultCauseId(long faultCauseId) {
        this.faultCauseId = faultCauseId;
        return this;
    }

    public String getCause() {
        return cause;
    }

    public FaultCause setCause(String cause) {
        this.cause = cause;
        return this;
    }

    public Set<FaultReport> getFaultReports() {
        return faultReports;
    }

    public FaultCause setFaultReports(Set<FaultReport> faultReports) {
        this.faultReports = faultReports;
        return this;
    }
}
