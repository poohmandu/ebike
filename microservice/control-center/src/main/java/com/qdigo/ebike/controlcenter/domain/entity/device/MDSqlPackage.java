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
@Table(name = "md_package")
public class MDSqlPackage extends AbstractAuditingEntity {

	private static final long serialVersionUID = 1L;

	@Id
	private String mdImei;

	private Double mdVoltage;//电压
	private Double mdCurrent;//电流
	private Integer mdState;//充电状态:0不在充电 1:恒流充电
	private Integer mdPortNumber;
	private Integer mdChargeError;
	private String mdPortBikeNumber;

	public String getMdImei() {
		return mdImei;
	}

	public void setMdImei(String mdImei) {
		this.mdImei = mdImei;
	}

	public Double getMdVoltage() {
		return mdVoltage;
	}

	public void setMdVoltage(Double mdVoltage) {
		this.mdVoltage = mdVoltage;
	}

	public Double getMdCurrent() {
		return mdCurrent;
	}

	public void setMdCurrent(Double mdCurrent) {
		this.mdCurrent = mdCurrent;
	}

	public Integer getMdState() {
		return mdState;
	}

	public void setMdState(Integer mdState) {
		this.mdState = mdState;
	}

	public Integer getMdPortNumber() {
		return mdPortNumber;
	}

	public void setMdPortNumber(Integer mdPortNumber) {
		this.mdPortNumber = mdPortNumber;
	}

	public Integer getMdChargeError() {
		return mdChargeError;
	}

	public void setMdChargeError(Integer mdChargeError) {
		this.mdChargeError = mdChargeError;
	}

	public String getMdPortBikeNumber() {
		return mdPortBikeNumber;
	}

	public void setMdPortBikeNumber(String mdPortBikeNumber) {
		this.mdPortBikeNumber = mdPortBikeNumber;
	}

}
