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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "charger")
public class Charger implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long chargerId;

    @Column(length = 20, nullable = false)
    private String chargerImei;

    @NotNull
    private String chargerName = "";
    @NotNull
    private Double longitude = 0.0;
    @NotNull
    private Double latitude = 0.0;
    @NotNull
    private String address = "";
    @NotNull
    private String note;
    @NotNull
    private Integer status = 0;
    @NotNull
    private Integer portNumber = 0; //可用充电口数量
    @NotNull
    private Integer usedPortNumber = 0;

    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "charger")
    private List<ChargerPort> chargerPortList = new ArrayList<>();


    public String getChargerImei() {
        return chargerImei;
    }

    public void setChargerImei(String chargerImei) {
        this.chargerImei = chargerImei;
    }

    public List<ChargerPort> getChargerPortList() {
        return chargerPortList;
    }

    public void setChargerPortList(List<ChargerPort> chargerPortList) {
        this.chargerPortList = chargerPortList;
    }

    public Integer getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }

    public Integer getUsedPortNumber() {
        return usedPortNumber;
    }

    public void setUsedPortNumber(Integer usedPortNumber) {
        this.usedPortNumber = usedPortNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getChargerId() {
        return chargerId;
    }

    public void setChargerId(Long chargerId) {
        this.chargerId = chargerId;
    }

    public String getChargerName() {
        return chargerName;
    }

    public void setChargerName(String chargerName) {
        this.chargerName = chargerName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
