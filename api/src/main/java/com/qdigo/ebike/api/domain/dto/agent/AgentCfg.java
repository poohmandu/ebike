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

package com.qdigo.ebike.api.domain.dto.agent;

import com.qdigo.ebike.common.core.constants.Const;
import lombok.Data;

/**
 * Created by niezhao on 2017/12/6.
 */
@Data
public class AgentCfg {
    private boolean speedLimit = false; //该代理商车是否限速
    private int dayMaxHours = 8; //每天封顶骑行小时
    private boolean longRentDisplay = true; //是否显示长租
    private boolean takeawayDisplay = true; //是否显示外卖卡
    private boolean depositDisplay = true; //是否显示押金
    private boolean zmScoreDisplay = true; //是否显示芝麻信用分
    private boolean studentAuthDisplay = true; //是否显示学生认证
    private boolean wxscoreDisplay = true; //是否显示微信支付分
    private boolean shareStation = false; //和同父代理商是否公用还车点
    private int batteryBan = 10; //低于多少电量不能使用
    private int compensateMeter = 10; //还车点范围补偿,为0代表每一个还车点独立设计
    private int bikeUserNearMeter = 80; //还车时人和车距离限制
    private double requireDeposit = 299.0; // 用户押金
    private boolean lowInsurance = false; //低价保险
    private boolean highInsurance = true; //高价保险
    private int requireScore = 700; // 芝麻信用准入分
    private double noneDepositFirstCharge = 30.0; // 非押金用户首次充值
    private double depositFirstCharge = 10.0; // 押金用户首次充值
    private double minCharge = 10.0; // 最小充值
    private boolean inviteDisplay = true; // 是否开放邀请入口
    private int autoReturnMinutes = Const.autoReturnMinutes;// 为小于等于0代表不自动还车
    private int freeSeconds = Const.freeSeconds;// 免费骑行时间,0代表不免费
    private boolean insuranceSms = false; //是否推送短信
    private String inputPrefix = ""; //手动输入前面补全内容
    private double allowArrears = -1.0; //还车时允许欠款金额,为负数代表关闭功能
    private Const.IdentifyType identifyType = Const.IdentifyType.none; //实名认证方式:无实名认证(none)、身份证号(idCard)、人脸比对(face)、活体检测
    private int allowAge = 18;
    private boolean forceOff = false;//是否强制断电
}
