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

import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by niezhao on 2017/2/21.
 */
@Entity
@Table(name = "fault_report")
public class FaultReport extends AbstractAuditingEntity {

    private static final long serialVersionUID = 6090057048066807964L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long faultReportId;

    //@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, optional = false)
    //@JoinColumn(name = "bike_id", nullable = false)
    private Long bikeId;

    //@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, optional = false)
    //@JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "fault_report_cause",
            joinColumns = {
                    @JoinColumn(name = "fault_report_id", referencedColumnName = "fault_report_id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "fault_cause_id", referencedColumnName = "fault_cause_id")})
    private Set<FaultCause> faultCauseSet;

    private String picture;

    private String note;

    private double longitude;// 经度  //存储为gcj_02坐标 ; 即高德坐标系

    private double latitude;// 纬度

    private String reportStatus; // 见枚举类

    public long getFaultReportId() {
        return faultReportId;
    }

    public FaultReport setFaultReportId(long faultReportId) {
        this.faultReportId = faultReportId;
        return this;
    }

    public Long getBikeId() {
        return bikeId;
    }

    public FaultReport setBikeId(Long bikeId) {
        this.bikeId = bikeId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public FaultReport setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Set<FaultCause> getFaultCauseSet() {
        return faultCauseSet;
    }

    public FaultReport setFaultCauseSet(Set<FaultCause> faultCauseSet) {
        this.faultCauseSet = faultCauseSet;
        return this;
    }

    public String getPicture() {
        return picture;
    }

    public FaultReport setPicture(String picture) {
        this.picture = picture;
        return this;
    }

    public String getNote() {
        return note;
    }

    public FaultReport setNote(String note) {
        this.note = note;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public FaultReport setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public FaultReport setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public FaultReport setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
        return this;
    }
}
