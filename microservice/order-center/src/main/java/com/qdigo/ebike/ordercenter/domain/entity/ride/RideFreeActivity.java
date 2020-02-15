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

package com.qdigo.ebike.ordercenter.domain.entity.ride;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qdigo.ebike.common.core.constants.Status;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by niezhao on 2018/3/30.
 */
@Data
@Entity
@Table(name = "ride_free_activity")
public class RideFreeActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_record_id", referencedColumnName = "ride_record_id",
        foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private RideRecord rideRecord;

    @Column(nullable = false, scale = 2, name = "free_consume")
    private double freeConsume; //免费金额

    @Column(nullable = false, name = "free_time")
    private long freeTime; //免费时间

    @Enumerated(EnumType.STRING)
    private FreeType freeType;

    @Enumerated(EnumType.STRING)
    private Status.FreeActivity freeActivity;

    @Column(nullable = false, name = "note")
    private String note; //说明

    //免费类型
    public enum FreeType {
        time, money
    }

    public long getId() {
        return id;
    }

    public RideFreeActivity setId(long id) {
        this.id = id;
        return this;
    }

    public RideRecord getRideRecord() {
        return rideRecord;
    }

    public RideFreeActivity setRideRecord(RideRecord rideRecord) {
        this.rideRecord = rideRecord;
        return this;
    }

    public double getFreeConsume() {
        return freeConsume;
    }

    public RideFreeActivity setFreeConsume(double freeConsume) {
        this.freeConsume = freeConsume;
        return this;
    }

    public long getFreeTime() {
        return freeTime;
    }

    public RideFreeActivity setFreeTime(long freeTime) {
        this.freeTime = freeTime;
        return this;
    }

    public FreeType getFreeType() {
        return freeType;
    }

    public RideFreeActivity setFreeType(FreeType freeType) {
        this.freeType = freeType;
        return this;
    }

    public Status.FreeActivity getFreeActivity() {
        return freeActivity;
    }

    public RideFreeActivity setFreeActivity(Status.FreeActivity freeActivity) {
        this.freeActivity = freeActivity;
        return this;
    }

    public String getNote() {
        return note;
    }

    public RideFreeActivity setNote(String note) {
        this.note = note;
        return this;
    }
}
