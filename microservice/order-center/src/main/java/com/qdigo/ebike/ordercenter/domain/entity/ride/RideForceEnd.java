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

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ride_force_end", indexes = {@Index(columnList = "ride_record_id", unique = true)})
public class RideForceEnd {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long forceEndId;

    @Column(nullable = false, unique = true, name = "ride_record_id")
    private long rideRecordId;

    private long agentId;

    private double amount;

    private int distanceMeter;

    private double latitude;

    private double longitude;

    @Column(nullable = false)
    private String amountNote;

}
