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

package com.qdigo.ebike.bike.domain.entity.charger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by niezhao on 2016/11/30.
 */
@Entity
@Table(name = "charger_port")
public class ChargerPort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chargerPortId;

    @NotNull
    private Integer chargerPortNo = 0;

    @Column(length = 20)
    private String status; //  可使用;充电中;已故障

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "charger_id")
    private Charger charger;


    public Long getChargerPortId() {
        return chargerPortId;
    }

    public void setChargerPortId(Long chargerPortId) {
        this.chargerPortId = chargerPortId;
    }

    public Integer getChargerPortNo() {
        return chargerPortNo;
    }

    public void setChargerPortNo(Integer chargerPortNo) {
        this.chargerPortNo = chargerPortNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Charger getCharger() {
        return charger;
    }

    public void setCharger(Charger charger) {
        this.charger = charger;
    }

}
