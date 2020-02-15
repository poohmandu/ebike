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

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "PLPackage")
public class PLPackage {

    @Id
    private String id;
    @NotBlank
    private String plImei;
    private String plLac; // location area code 位置区域码
    private String plCellid; // 基站小区编号
    private String plSingal;// 信号强度
    private int plElectric; // (0:无外界电源 1:有外接电源)
    private int plDoorLock; // (0:电门锁关，1:电门锁开)
    private int plLocked; // (0:没锁车 1:锁车)
    private int plShaked; // (0:无震动，1:震动)
    private int plWheelInput;// (0:不是轮车输入模式 1:是轮车输入模式)
    private int plAutoLocked; // (0:不是自动锁车 1：自动锁车)
    private int plTumble; // (0:没跌倒 1:跌倒)
    private int plError; // (0:无故障 1:有故障)
    private long timestamp = System.currentTimeMillis();
    @NotNull
    private String plServer;
    @NotNull
    private String plClient;

    public String getPlServer() {
        return plServer;
    }

    public PLPackage setPlServer(String plServer) {
        this.plServer = plServer;
        return this;
    }

    public String getPlClient() {
        return plClient;
    }

    public PLPackage setPlClient(String plClient) {
        this.plClient = plClient;
        return this;
    }

    public String getId() {
        return id;
    }

    public PLPackage setId(String id) {
        this.id = id;
        return this;
    }

    public String getPlImei() {
        return plImei;
    }

    public PLPackage setPlImei(String plImei) {
        this.plImei = plImei;
        return this;
    }

    public String getPlLac() {
        return plLac;
    }

    public PLPackage setPlLac(String plLac) {
        this.plLac = plLac;
        return this;
    }

    public String getPlCellid() {
        return plCellid;
    }

    public PLPackage setPlCellid(String plCellid) {
        this.plCellid = plCellid;
        return this;
    }

    public String getPlSingal() {
        return plSingal;
    }

    public PLPackage setPlSingal(String plSingal) {
        this.plSingal = plSingal;
        return this;
    }

    public int getPlElectric() {
        return plElectric;
    }

    public PLPackage setPlElectric(int plElectric) {
        this.plElectric = plElectric;
        return this;
    }

    public int getPlDoorLock() {
        return plDoorLock;
    }

    public PLPackage setPlDoorLock(int plDoorLock) {
        this.plDoorLock = plDoorLock;
        return this;
    }

    public int getPlLocked() {
        return plLocked;
    }

    public PLPackage setPlLocked(int plLocked) {
        this.plLocked = plLocked;
        return this;
    }

    public int getPlShaked() {
        return plShaked;
    }

    public PLPackage setPlShaked(int plShaked) {
        this.plShaked = plShaked;
        return this;
    }

    public int getPlWheelInput() {
        return plWheelInput;
    }

    public PLPackage setPlWheelInput(int plWheelInput) {
        this.plWheelInput = plWheelInput;
        return this;
    }

    public int getPlAutoLocked() {
        return plAutoLocked;
    }

    public PLPackage setPlAutoLocked(int plAutoLocked) {
        this.plAutoLocked = plAutoLocked;
        return this;
    }

    public int getPlTumble() {
        return plTumble;
    }

    public PLPackage setPlTumble(int plTumble) {
        this.plTumble = plTumble;
        return this;
    }

    public int getPlError() {
        return plError;
    }

    public PLPackage setPlError(int plError) {
        this.plError = plError;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public PLPackage setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
