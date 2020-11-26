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
@Table(name = "pg_package")
public class PGSqlPackage extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    private String pgImei; // imei号

    private Double pgLongitude; // gps经度
    private Double pgLatitude; // gps纬度
    private Integer pgHight; // 海拔高度
    private Integer pgSpeed; // 实际速度
    private Integer pgStar; // 卫星数量
    private Integer pgElectric; // (0:无外界电源 1:有外接电源)
    private Integer pgDoorLock; // (0:电门锁关，1:电门锁开)
    private Integer pgLocked; // (0:没锁车 1:锁车)
    private Integer pgShaked; // (0:无震动，1:震动)
    private Integer pgWheelInput;// (0:不是轮车输入模式 1:是轮车输入模式)
    private Integer pgAutoLocked; // (0:不是自动锁车 1：自动锁车)
    private Integer pgTumble; // (0:没跌倒 1:跌倒)
    private Integer pgError; // (0:无故障 1:有故障)
    private long timestamp;

    public String getPgImei() {
        return pgImei;
    }

    public PGSqlPackage setPgImei(String pgImei) {
        this.pgImei = pgImei;
        return this;
    }

    public Double getPgLongitude() {
        return pgLongitude;
    }

    public PGSqlPackage setPgLongitude(Double pgLongitude) {
        this.pgLongitude = pgLongitude;
        return this;
    }

    public Double getPgLatitude() {
        return pgLatitude;
    }

    public PGSqlPackage setPgLatitude(Double pgLatitude) {
        this.pgLatitude = pgLatitude;
        return this;
    }

    public Integer getPgHight() {
        return pgHight;
    }

    public PGSqlPackage setPgHight(Integer pgHight) {
        this.pgHight = pgHight;
        return this;
    }

    public Integer getPgSpeed() {
        return pgSpeed;
    }

    public PGSqlPackage setPgSpeed(Integer pgSpeed) {
        this.pgSpeed = pgSpeed;
        return this;
    }

    public Integer getPgStar() {
        return pgStar;
    }

    public PGSqlPackage setPgStar(Integer pgStar) {
        this.pgStar = pgStar;
        return this;
    }

    public Integer getPgElectric() {
        return pgElectric;
    }

    public PGSqlPackage setPgElectric(Integer pgElectric) {
        this.pgElectric = pgElectric;
        return this;
    }

    public Integer getPgDoorLock() {
        return pgDoorLock;
    }

    public PGSqlPackage setPgDoorLock(Integer pgDoorLock) {
        this.pgDoorLock = pgDoorLock;
        return this;
    }

    public Integer getPgLocked() {
        return pgLocked;
    }

    public PGSqlPackage setPgLocked(Integer pgLocked) {
        this.pgLocked = pgLocked;
        return this;
    }

    public Integer getPgShaked() {
        return pgShaked;
    }

    public PGSqlPackage setPgShaked(Integer pgShaked) {
        this.pgShaked = pgShaked;
        return this;
    }

    public Integer getPgWheelInput() {
        return pgWheelInput;
    }

    public PGSqlPackage setPgWheelInput(Integer pgWheelInput) {
        this.pgWheelInput = pgWheelInput;
        return this;
    }

    public Integer getPgAutoLocked() {
        return pgAutoLocked;
    }

    public PGSqlPackage setPgAutoLocked(Integer pgAutoLocked) {
        this.pgAutoLocked = pgAutoLocked;
        return this;
    }

    public Integer getPgTumble() {
        return pgTumble;
    }

    public PGSqlPackage setPgTumble(Integer pgTumble) {
        this.pgTumble = pgTumble;
        return this;
    }

    public Integer getPgError() {
        return pgError;
    }

    public PGSqlPackage setPgError(Integer pgError) {
        this.pgError = pgError;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public PGSqlPackage setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
