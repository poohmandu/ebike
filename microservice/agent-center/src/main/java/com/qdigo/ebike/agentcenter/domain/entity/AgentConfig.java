/*
 * Copyright 2020 聂钊 nz@qdigo.com
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

package com.qdigo.ebike.agentcenter.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qdigo.ebike.common.core.constants.Const;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by niezhao on 2017/11/29.
 */
@Data
@Entity
@Table(name = "agent_config")
public class AgentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", unique = true, nullable = false)
    private Agent agent;

    ////////////////////////////////////////真正的配置从这开始

    //该代理商是否限速
    private Boolean speedLimit = false; //该代理商车是否限速

    private Integer dayMaxHours = 8; //每天封顶骑行小时

    private Boolean longRentDisplay = true; //是否显示长租

    private Boolean takeawayDisplay = true; //是否显示外卖卡

    private Boolean depositDisplay = true; //是否显示押金

    private Boolean zmScoreDisplay = true; //是否显示芝麻信用分

    private Boolean studentAuthDisplay = true; //是否显示学生认证

    private Boolean wxscoreDisplay = true; //是否显示微信支付分

    private Boolean shareStation = false; //和同父代理商是否公用还车点

    private Integer batteryBan = 10; //低于多少电量不能使用

    private Integer compensateMeter = 10; //还车点范围补偿,为0代表每一个还车点独立设计

    private Integer bikeUserNearMeter = 80; //还车时人和车距离限制

    private Double requireDeposit = 299.0; // 用户押金

    private Boolean lowInsurance = false; //低价保险,人保

    private Boolean highInsurance = true; //高价保险,平安保险

    private Integer requireScore = 700; // 芝麻信用准入分

    private Double noneDepositFirstCharge = 30.0; // 非押金用户首次充值

    private Double depositFirstCharge = 10.0; // 押金用户首次充值

    private Double minCharge = 10.0; // 最小充值

    private Boolean inviteDisplay = true; // 是否开放邀请入口

    private Integer autoReturnMinutes = Const.autoReturnMinutes;// 为小于等于0代表不自动还车

    private Integer freeSeconds = Const.freeSeconds;//免费骑行时间,0代表不免费

    private Boolean insuranceSms = false; //是否推送保险短信

    private String inputPrefix = ""; //手动输入前面补全内容

    private Double allowArrears = -1.0; //还车时允许欠款金额,为负数代表关闭功能

    @Enumerated(EnumType.STRING)
    private Const.IdentifyType identifyType = Const.IdentifyType.none; //实名认证方式:身份证号(idCard)、人脸比对(face)、活体检测

    private Integer allowAge = 18;

    private Boolean forceOff = false;//是否强制断电


}
