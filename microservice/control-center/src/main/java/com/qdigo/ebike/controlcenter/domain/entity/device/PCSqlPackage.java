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
@Table(name = "pc_package")
public class PCSqlPackage extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    private String pcImei;

    private Integer pcCmd;

    private String pcParam;

    private Long pcSequence;

    public String getPcImei() {
        return pcImei;
    }

    public void setPcImei(String pcImei) {
        this.pcImei = pcImei;
    }

    public Integer getPcCmd() {
        return pcCmd;
    }

    public void setPcCmd(Integer pcCmd) {
        this.pcCmd = pcCmd;
    }

    public String getPcParam() {
        return pcParam;
    }

    public void setPcParam(String pcParam) {
        this.pcParam = pcParam;
    }

    public Long getPcSequence() {
        return pcSequence;
    }

    public void setPcSequence(Long pcSequence) {
        this.pcSequence = pcSequence;
    }
}
