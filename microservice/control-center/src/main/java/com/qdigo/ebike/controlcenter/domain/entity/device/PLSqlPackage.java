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
@Table(name="pl_package")
public class PLSqlPackage extends AbstractAuditingEntity {

	private static final long serialVersionUID = 1L;

	@Id
	private String plImei;
	
	private String plLac; // location area code 位置区域码
	private String plCellid; // 基站小区编号
	private String plSingal;// 信号强度
	private Integer plElectric; // (0:无外界电源 1:有外接电源)
	private Integer plDoorLock; // (0:电门锁关，1:电门锁开)
	private Integer plLocked; // (0:没锁车 1:锁车)
	private Integer plShaked; // (0:无震动，1:震动)
	private Integer plWheelInput;// (0:不是轮车输入模式 1:是轮车输入模式)
	private Integer plAutoLocked; // (0:不是自动锁车 1：自动锁车)
	private Integer plTumble; // (0:没跌倒 1:跌倒)
	private Integer plError; // (0:无故障 1:有故障)

	public String getPlImei() {
		return plImei;
	}

	public void setPlImei(String plImei) {
		this.plImei = plImei;
	}

	public String getPlLac() {
		return plLac;
	}

	public void setPlLac(String plLac) {
		this.plLac = plLac;
	}

	public String getPlCellid() {
		return plCellid;
	}

	public void setPlCellid(String plCellid) {
		this.plCellid = plCellid;
	}

	public String getPlSingal() {
		return plSingal;
	}

	public void setPlSingal(String plSingal) {
		this.plSingal = plSingal;
	}

	public Integer getPlElectric() {
		return plElectric;
	}

	public void setPlElectric(Integer plElectric) {
		this.plElectric = plElectric;
	}

	public Integer getPlDoorLock() {
		return plDoorLock;
	}

	public void setPlDoorLock(Integer plDoorLock) {
		this.plDoorLock = plDoorLock;
	}

	public Integer getPlLocked() {
		return plLocked;
	}

	public void setPlLocked(Integer plLocked) {
		this.plLocked = plLocked;
	}

	public Integer getPlShaked() {
		return plShaked;
	}

	public void setPlShaked(Integer plShaked) {
		this.plShaked = plShaked;
	}

	public Integer getPlWheelInput() {
		return plWheelInput;
	}

	public void setPlWheelInput(Integer plWheelInput) {
		this.plWheelInput = plWheelInput;
	}

	public Integer getPlAutoLocked() {
		return plAutoLocked;
	}

	public void setPlAutoLocked(Integer plAutoLocked) {
		this.plAutoLocked = plAutoLocked;
	}

	public Integer getPlTumble() {
		return plTumble;
	}

	public void setPlTumble(Integer plTumble) {
		this.plTumble = plTumble;
	}

	public Integer getPlError() {
		return plError;
	}

	public void setPlError(Integer plError) {
		this.plError = plError;
	}
	
}
