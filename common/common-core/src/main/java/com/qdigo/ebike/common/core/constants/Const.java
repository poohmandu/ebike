/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.common.core.constants;

import com.qdigo.ebike.common.core.util.http.NetUtil;

/**
 * Description: 
 * date: 2019/12/23 5:54 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
public final class Const {

    public static final String host = NetUtil.getIp() + ":9001";

    public static final int deviceIdLength = 10; //设备deviceId长度
    public static final int imeiLength = 15; //imei长度
    public static final String wxlite = "weixin"; //小程序 deviceId
    public static final String zfblite = "zfblite"; //支付宝小程序 deviceId
    public static final int dataCleanDays = 20;

    public static final int zmScoreExpireDays = 20;

    public static final int highGearDelaySeconds = 5 * 60; //高档位延时时间
    public static final int freeSeconds = 60; //免费骑行时间
    public static final int pushRideDelay = 5;  //推送延时
    public static final int noAutoReturnMinutes = 20;//借车的xx分钟内不会自动还车
    public static final int autoReturnMinutes = 10;//还车点自动还车时间
    public static final int arrearsChargeMinutes = 3; //欠款充值标志失效时间

    //device
    public static final int pgNotFoundSeconds = 150; // xx秒没有pg包视为异常
    public static final int deviceTimeout = 5000; //设备socket超时时间
    public static final int notAtStationMinutes = 30; //xx分钟不在还车点报警

    public static final int redisKeyExpireDays = 7;//redis key默认过期时间
    public static final String MQPorts = "9001"; //MQ允许端口

    //活动
    public static final int firstRegisterFreeMinutes = 120;
    public static final int inviteReward = 2;

    public static class DeviceSMS {
        public static final String appId = "100005";
        public static final String pwd = "3b6L2jb8";
    }

    //查询车辆与还车点关系
    public static class StationGeo {
        public static final int distance = 1000; //(米) 第一次粗略查询范围
        public static final int deviation = 5; //(米) 半径加多少米
        public static final int polygon = 10;
    }

    public enum MailType {
        //1           2              3                5         6                 8
        ElectricWarn, DoorLockWarn, WheelInputWarn, ShakeWarn, PowerVoltageWarn, OutOfSpeedWarn,
        NotAtStation
    }

    public static class AopOrder {
        // ->(1(2(3(4(5 (process) 5)4)3)2)1)->
        private static final int Base = -1000;
        public static final int CatAspect = Base + 1;
        public static final int LogAspect = Base + 2;
        public static final int JobLockAspect = Base + 3;
        public static final int TokenAspect = Base + 4;
        public static final int AccessValidateAspect = Base + 5;
        public static final int RetryAspect = Base + 6;
        public static final int ThreadCache = Base + 7;
    }

    public enum LongRentType {
        day, week, season, month, takeaway
    }

    public enum AppType {
        qdigo, ops
    }

    public enum direction {
        in, out
    }

    public enum DeviceMode {
        GPS, SMS, BLE, GPS_SMS
    }

    public enum AgentType {
        own, cooperation
    }

    public enum IdentifyType {
        none, idCard, face
    }

    public enum PushType {
        display, //只做展示
        autoReturn, //自动还车,app据此判断退出控制界面
        warn, consumeWarn, areaWarn, // 警告,比如低电量报警
        buttonEndFail, buttonEndSuccess, //长按车上按钮，成功或失败
        appEndSuccess, //app 还车成功
        refundSuccess, //退款成功
        atStation, //在还车点
        stuAuth, //学生认证成功,失败
        inviteFinished
    }

    private static class Cmd {
        public static final int openPort = 1;
        public static final int closePort = 1;
        public static final int openBlower = 2;
        public static final int closeBlower = 2;

        public static final int ignition = 3;
        public static final int flameOut = 3;
        public static final int setPTime = 7;
        public static final int setHTime = 7;
        public static final int reboot = 8;
        public static final int updateImei = 11;
        public static final int setSensitivity = 12;
        public static final int lock = 24;
        public static final int unlock = 24;
        public static final int autoLockOn = 27;
        public static final int autoLockOff = 27;
        public static final int reqHearBeat = 30;
        public static final int seekStart = 41;
        public static final int seekEnd = 41;
        public static final int fire = 42;
        public static final int shutdown = 42;
        public static final int remoteLearn = 43;
        public static final int highGear = 44;
        public static final int lowGear = 44;
        public static final int seatCushion = 45;

        public static final int buttonEnd = 70;

    }

}
