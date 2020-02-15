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

package com.qdigo.ebike.controlcenter.domain.entity.mongo;

import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString
@Document
public class PGPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotBlank
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
    private Long timestamp = System.currentTimeMillis();
    private Integer seconds;
    private Integer distance;

    @NotNull
    private String pgClient;
    @NotNull
    private String pgServer;

    public Integer getDistance() {
        return distance;
    }

    public PGPackage setDistance(Integer distance) {
        this.distance = distance;
        return this;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public PGPackage setSeconds(Integer seconds) {
        this.seconds = seconds;
        return this;
    }

    public String getPgClient() {
        return pgClient;
    }

    public PGPackage setPgClient(String pgClient) {
        this.pgClient = pgClient;
        return this;
    }

    public String getPgServer() {
        return pgServer;
    }

    public PGPackage setPgServer(String pgServer) {
        this.pgServer = pgServer;
        return this;
    }

    public String getId() {
        return id;
    }

    public PGPackage setId(String id) {
        this.id = id;
        return this;
    }

    public String getPgImei() {
        return pgImei;
    }

    public PGPackage setPgImei(String pgImei) {
        this.pgImei = pgImei;
        return this;
    }

    public Double getPgLongitude() {
        return pgLongitude;
    }

    public PGPackage setPgLongitude(Double pgLongitude) {
        this.pgLongitude = pgLongitude;
        return this;
    }

    public Double getPgLatitude() {
        return pgLatitude;
    }

    public PGPackage setPgLatitude(Double pgLatitude) {
        this.pgLatitude = pgLatitude;
        return this;
    }

    public Integer getPgHight() {
        return pgHight;
    }

    public PGPackage setPgHight(Integer pgHight) {
        this.pgHight = pgHight;
        return this;
    }

    public Integer getPgSpeed() {
        return pgSpeed;
    }

    public PGPackage setPgSpeed(Integer pgSpeed) {
        this.pgSpeed = pgSpeed;
        return this;
    }

    public Integer getPgStar() {
        return pgStar;
    }

    public PGPackage setPgStar(Integer pgStar) {
        this.pgStar = pgStar;
        return this;
    }

    public Integer getPgElectric() {
        return pgElectric;
    }

    public PGPackage setPgElectric(Integer pgElectric) {
        this.pgElectric = pgElectric;
        return this;
    }

    public Integer getPgDoorLock() {
        return pgDoorLock;
    }

    public PGPackage setPgDoorLock(Integer pgDoorLock) {
        this.pgDoorLock = pgDoorLock;
        return this;
    }

    public Integer getPgLocked() {
        return pgLocked;
    }

    public PGPackage setPgLocked(Integer pgLocked) {
        this.pgLocked = pgLocked;
        return this;
    }

    public Integer getPgShaked() {
        return pgShaked;
    }

    public PGPackage setPgShaked(Integer pgShaked) {
        this.pgShaked = pgShaked;
        return this;
    }

    public Integer getPgWheelInput() {
        return pgWheelInput;
    }

    public PGPackage setPgWheelInput(Integer pgWheelInput) {
        this.pgWheelInput = pgWheelInput;
        return this;
    }

    public Integer getPgAutoLocked() {
        return pgAutoLocked;
    }

    public PGPackage setPgAutoLocked(Integer pgAutoLocked) {
        this.pgAutoLocked = pgAutoLocked;
        return this;
    }

    public Integer getPgTumble() {
        return pgTumble;
    }

    public PGPackage setPgTumble(Integer pgTumble) {
        this.pgTumble = pgTumble;
        return this;
    }

    public Integer getPgError() {
        return pgError;
    }

    public PGPackage setPgError(Integer pgError) {
        this.pgError = pgError;
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public PGPackage setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
