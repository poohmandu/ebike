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
@Table(name = "mc_package")
public class MCSqlPackage extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    private String mcImei;

    private Integer mcCmd;
    private Integer mcSequence;
    private String mcParam;

    public String getMcImei() {
        return mcImei;
    }

    public void setMcImei(String mcImei) {
        this.mcImei = mcImei;
    }

    public Integer getMcCmd() {
        return mcCmd;
    }

    public void setMcCmd(Integer mcCmd) {
        this.mcCmd = mcCmd;
    }

    public Integer getMcSequence() {
        return mcSequence;
    }

    public void setMcSequence(Integer mcSequence) {
        this.mcSequence = mcSequence;
    }

    public String getMcParam() {
        return mcParam;
    }

    public void setMcParam(String mcParam) {
        this.mcParam = mcParam;
    }
}
