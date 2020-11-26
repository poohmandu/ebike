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
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by niezhao on 2017/8/31.
 */
@ToString
@Document(collection = "BTPackage")
public class BTPackage {

    @Id
    private String id;

    private String btImei; // imei号
    private String deviceId;
    private String mobileNo;

    private double btLongitude; // 经度
    private double btLatitude; // 纬度
    private int state; //原始状态字节

    private int btSingal; // 信号强度
    private int btElectric; // (0:无外界电源 1:有外接电源)
    private int btDoorLock; // (0:电门锁关，1:电门锁开)
    private int btLocked; // (0:没锁车 1:锁车) => (0:锁车 1:没锁车)
    private int btShaked; // (0:无震动，1:震动)
    private int btWheelInput;// (0:不是轮车输入模式 1:是轮车输入模式)
    private int btBleEnable;
    //private int btAutoLocked; // (0:不是自动锁车 1：自动锁车)

    private long timestamp = System.currentTimeMillis();

    public String getId() {
        return id;
    }

    public BTPackage setId(String id) {
        this.id = id;
        return this;
    }

    public String getBtImei() {
        return btImei;
    }

    public BTPackage setBtImei(String btImei) {
        this.btImei = btImei;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public BTPackage setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public int getState() {
        return state;
    }

    public BTPackage setState(int state) {
        this.state = state;
        return this;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public BTPackage setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public double getBtLongitude() {
        return btLongitude;
    }

    public BTPackage setBtLongitude(double btLongitude) {
        this.btLongitude = btLongitude;
        return this;
    }

    public double getBtLatitude() {
        return btLatitude;
    }

    public BTPackage setBtLatitude(double btLatitude) {
        this.btLatitude = btLatitude;
        return this;
    }

    public int getBtSingal() {
        return btSingal;
    }

    public BTPackage setBtSingal(int btSingal) {
        this.btSingal = btSingal;
        return this;
    }

    public int getBtElectric() {
        return btElectric;
    }

    public BTPackage setBtElectric(int btElectric) {
        this.btElectric = btElectric;
        return this;
    }

    public int getBtDoorLock() {
        return btDoorLock;
    }

    public BTPackage setBtDoorLock(int btDoorLock) {
        this.btDoorLock = btDoorLock;
        return this;
    }

    public int getBtLocked() {
        return btLocked;
    }

    public BTPackage setBtLocked(int btLocked) {
        this.btLocked = btLocked;
        return this;
    }

    public int getBtShaked() {
        return btShaked;
    }

    public BTPackage setBtShaked(int btShaked) {
        this.btShaked = btShaked;
        return this;
    }

    public int getBtWheelInput() {
        return btWheelInput;
    }

    public BTPackage setBtWheelInput(int btWheelInput) {
        this.btWheelInput = btWheelInput;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BTPackage setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getBtBleEnable() {
        return btBleEnable;
    }

    public BTPackage setBtBleEnable(int btBleEnable) {
        this.btBleEnable = btBleEnable;
        return this;
    }
}
