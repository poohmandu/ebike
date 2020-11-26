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

package com.qdigo.ebike.controlcenter.domain.entity.device;

import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ml_package")
public class MLSqlPackage extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    private String mlImei;

    private String mlLAC;
    private String mlCellid;
    private String mlSingal;
    private Double mlTemperature;
    private Long mlImsi;

    public String getMlImei() {
        return mlImei;
    }

    public void setMlImei(String mlImei) {
        this.mlImei = mlImei;
    }

    public String getMlLAC() {
        return mlLAC;
    }

    public void setMlLAC(String mlLAC) {
        this.mlLAC = mlLAC;
    }

    public String getMlCellid() {
        return mlCellid;
    }

    public void setMlCellid(String mlCellid) {
        this.mlCellid = mlCellid;
    }

    public String getMlSingal() {
        return mlSingal;
    }

    public void setMlSingal(String mlSingal) {
        this.mlSingal = mlSingal;
    }

    public Double getMlTemperature() {
        return mlTemperature;
    }

    public void setMlTemperature(Double mlTemperature) {
        this.mlTemperature = mlTemperature;
    }

    public Long getMlImsi() {
        return mlImsi;
    }

    public void setMlImsi(Long mlImsi) {
        this.mlImsi = mlImsi;
    }
}
