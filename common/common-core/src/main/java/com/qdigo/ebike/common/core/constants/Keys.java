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
import org.springframework.util.StringUtils;

/**
 * Description:
 * date: 2019/12/10 4:12 PM
 *
 * @author niezhao
 * @since JDK 1.8
 */
@Getter
@AllArgsConstructor
public enum Keys {
    AccessToken("AccessToken:{0}", "用户授权口令"),
    PinCode("PinCode:{0}", "登录验证码"),

    //counter,lock
    lockJob("aop:lockJob:{0}", "job的分布式锁"),
    lockScanBike("lock:scan:bike:{0}", "防止同一辆车被多人同时扫码"),
    lockPush("lock:push:{0}:{1}", "同一类型推送间隔时间"),
    //flag
    flagJointSms("flag:jointSms:{0}", "一个IP短时间只能发送一条短信"),
    flagReboot("flag:reboot:{0}", "用户是否需要重启"),
    flagOps("flag:ops:{0}", "记录车辆状态"),
    flagAreaWarn("flag:area:{0}", "系统是否处理过"),
    flagArrearsCharge("flag:arrears:{0}", "记录用户欠费充值过"),
    flagWxscoreCreate("flag:wxscoreCreate:{0}", "用户创建了微信支付分订单"),

    //cache
    cacheMongoPGList("cache:mongo:PGList:{0}", "PG上行时缓存"),

    cacheMongoBikeLocScan("cache:mongo:bikeLoc:scan:{0}", "用户扫码最新位置"),

    //各种id
    OrderNo("OrderSeqId{0}", "商家交易号"),

    SesameTransactionId("SesameTransactionId{0}", "芝麻第三方调用时的交易号"),

    JournalAccount("JournalAccount{0}", "流水账id"),

    deviceSmsTransactionId("DeviceSmsTransactionId{0}", "设备短信TransactionId"),

    BGBOrderSn("BGBOrderSn:{0}", "白鸽宝流水号"),

    //第三方凭据
    SesameOpenId("third:sesame:openId:{0}", "芝麻信用的openid"),

    SesameZmScore("third:sesame:zmScore:{0}", "芝麻信用分"),

    SesameCredit("third:sesame:zmCredit:{0}", "是否达到指定信用分"),

    //wxlite
    wxliteAccessToken("wxlite:accessToken", ""),

    wxliteSessionKey("wxlite:sessionKey:{0}", "微信登录后session与openId的对应关系"),

    webSocketMessageQueue("wxlite:webSocket:{0}:message", "webSocket,当session关闭的时候，消息保存"),

    webSocketSessionAddress("wxlite:webSocket:{0}:host", "存储webSocket的session地址"),

    wxliteFormId("wxlite:formId:{0}", "用户表单提交的id"),

    wxlitePayScore("wxlite:score:requestNo:{0}", "out_request_no"),

    //硬件
    available_slave("Monitor:Bike_Status:{0}", "socket硬件连接设备的ip"),

    available_slave_charger("Monitor:ChargerPile_Status:{0}", "socket充电桩连接设备的ip"),

    up_MD_chk("up_MD_check_{0}", "bike插上充电桩后记录"),

    up_pc_notice("up:pc:{0}:{1}:notice:{2}", "pc通知"),

    up_pc_response("up:pc:{0}:{1}:response:{2}", "pc上行"),

    up_pg_event("up:pg:{0}:{1}:event:{2}", "pg包上行,有状态位改变 e.g. up:pg:20170612:imei:event:电门锁"),

    up_pg_latest("up:pg:{0}:{1}:latest", "最新的pg包状态,有效期xx分钟"),

    // 报警
    lowPower("warn:user:lowPower:{0}", "warn:user:lowPower:mobileNo"),

    warnOps("warn:ops:{0}:{1}", "warn:{报警类型}:{imei号}"),

    // 后台管理平台
    ops_auth("backManage:control:{0}", "运维人员控制车辆授权校验"),
    seat_cushion("backManage:seatCushion:{0}", "管理员开坐垫的记录");

    private String val;
    private String des;

    public String getKey(String... markers) {
        String key = this.getVal();
        for (int i = 0; i < markers.length; i++) {
            key = StringUtils.replace(key, "{" + i + "}", markers[i]);
        }
        return key;
    }

    public static String getKey(Keys enm, String... markers) {
        String key = enm.getVal();
        for (int i = 0; i < markers.length; i++) {
            key = StringUtils.replace(key, "{" + i + "}", markers[i]);
        }
        return key;
    }

}


