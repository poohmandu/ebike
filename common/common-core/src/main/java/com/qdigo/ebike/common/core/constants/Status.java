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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by niezhao on 2017/4/1.
 */
public final class Status {

    //认证状态
    @Getter
    @AllArgsConstructor
    public enum Step {
        login(0, "未登录"),
        deposit(1, "押金认证"),
        balance(2, "余额未充值"),
        finished(3, "完成认证");
        private int val;
        private String desc;
    }

    // 车辆逻辑状态
    public enum BikeLogicStatus {
        available(0, "车辆可用"),
        subscribe(1, "预约状态"),
        inUse(2, "车辆使用中");

        private int val;
        private String des;

        BikeLogicStatus(int val, String des) {
            this.val = val;
            this.des = des;
        }

        public int getVal() {
            return val;
        }

        public String getDes() {
            return des;
        }
    }

    // 车辆实际状态
    public enum BikeActualStatus {
        // e.g.  cannotEndBike,userReport,locationFail
        ok("ok", "好的"),
        cannotOps("cannotOps", "用户操作失败"),
        smsCannotOps("smsCannotOps", "用户短信操作失败"),
        pgNotFound("pgNotFound", "pg有很长时间未上传"),
        locationFail("locationFail", "定位失败,经纬度为0,0"),
        internalError("internalError", "根据ph等检查到故障,如转把故障,刹车故障等等"),
        noPower("noPower", "electric位检查到无外接电源"),
        userReport("userReport", "用户上报的故障"),
        other("other", "其他未知故障");

        private String val;
        private String des;

        BikeActualStatus(String val, String des) {
            this.val = val;
            this.des = des;
        }

        public String getVal() {
            return val;
        }

        public String getDes() {
            return des;
        }
    }

    //骑行状态
    public enum RideStatus {
        invalid(0, "未生效期"),
        running(1, "骑行状态"),
        end(2, "骑行正常结束"),
        error(3, "异常状态");
        //free(4, "免费");

        private int val;
        private String des;

        RideStatus(int val, String des) {
            this.val = val;
            this.des = des;
        }

        public int getVal() {
            return val;
        }

        public String getDes() {
            return des;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum FreeActivity {
        //按优先权从 1到n
        noFree(0, "无免费"),
        firstRegister(1, "首次注册赠送免费骑行时间"),
        xxSecondsDrive(2, "骑行前xx秒免费"),
        longRent(3, "骑行卡用户免费骑行"),
        coupon(4, "优惠券减免"),
        giftBalance(5, "使用电滴赠送余额");

        private int val;
        private String des;
    }

    // order状态
    public enum OrderStatus {
        invalid(0, "无效订单"),
        valid(1, "有效订单"),
        unpaid(2, "待支付订单"),
        paid(3, "已支付订单"),
        free(4, "免费订单");
        private int val;
        private String des;

        OrderStatus(int val, String des) {
            this.val = val;
            this.des = des;
        }

        public int getVal() {
            return val;
        }

        public String getDes() {
            return des;
        }
    }

    //退款状态
    public enum RefundStatus {
        not("", "没有发起退款请求"),
        pending("pending", "受理中"),
        success("success", "退款成功"),
        fail("fail", "退款失败");
        private String val;
        private String des;

        RefundStatus(String val, String des) {
            this.val = val;
            this.des = des;
        }

        public String getVal() {
            return val;
        }

        public String getDes() {
            return des;
        }
    }

    //支付类型
    public enum PayType {
        deposit(1, "支付押金"),
        rent(2, "支付钱包余额"), // 进账
        consume(3, "每次骑行 入账"), // 出账
        longRent(4, "包时间段 租车"), // 出账
        other(6, "其他");
        private int val;
        private String des;

        PayType(int val, String des) {
            this.val = val;
            this.des = des;
        }

        public int getVal() {
            return val;
        }

        public String getDes() {
            return des;
        }
    }

    //支付渠道
    public enum PayChannel {
        alipay("alipay", "支付宝app支付"),
        wx("wx", "微信支付"),
        wx_pub("wx_pub", "微信小程序"),
        wx_lite("wx_lite", "微信小程序"),
        alipay_lite("alipay_lite", "支付宝小程序");
        private String val;
        private String des;

        PayChannel(String val, String des) {
            this.val = val;
            this.des = des;
        }

        public String getVal() {
            return val;
        }

        public String getDes() {
            return des;
        }
    }

    //故障上报状态
    public enum ReportStatus {
        pending("pending", "受理中"),
        success("success", "经过审查"),
        fail("fail", "未经过审查"),
        finish("finish", "车辆处理完，恢复正常");
        private String val;
        private String des;

        ReportStatus(String val, String des) {
            this.val = val;
            this.des = des;
        }

        public String getVal() {
            return val;
        }

        public String getDes() {
            return des;
        }
    }

    //用户信用even类型
    public enum CreditEvent {
        login("首次登录", 70, "首次登录加分"),
        normalDrive("正常骑行", 1, "正常骑行"),
        reportFault("上报故障", 1, "上报故障且核实有效"),
        test("扣分", -1, "以后用来扣分的");
        private String val;
        private int score;
        private String des;

        CreditEvent(String val, int score, String des) {
            this.val = val;
            this.score = score;
            this.des = des;
        }

        public String getVal() {
            return val;
        }

        public int getScore() {
            return score;
        }

        public String getDes() {
            return des;
        }
    }

    //充电桩故障状态
    public enum ChargerErrorStatus {
        noError(0, "没有故障"),
        overTemperature(1, "过温保护"),
        overVoltage(2, "过高压保护"),
        lessThanVoltage(3, "欠压保护"),
        blowerError(4, "风扇故障"),
        outControl(5, "热失控"),
        communicationError(6, "与电动车通讯错误");

        private int val;
        private String des;

        ChargerErrorStatus(int val, String des) {
            this.val = val;
            this.des = des;
        }

        public int getVal() {
            return val;
        }

        public String getDes() {
            return des;
        }
    }

    //学生认证状态
    public enum StuAuthStatus {
        pending, success, fail, discard
    }

}
