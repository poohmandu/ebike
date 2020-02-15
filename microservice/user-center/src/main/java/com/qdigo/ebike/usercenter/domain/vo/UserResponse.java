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

package com.qdigo.ebike.usercenter.domain.vo;

import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.usercenter.domain.entity.User;
import lombok.*;

import java.util.List;

/**
 * Created by niezhao on 2017/3/27.
 */
@Getter
@Setter
@Builder
public class UserResponse {
    private Long userId;
    private String mobileNo;
    private String fullName;

    private String realName;

    private String idNo;

    private String profileImageId;
    private String accessToken; //比userInfo多
    private String wxliteOpenId;
    private String studentAuth;
    private String city;
    private Long agentId;
    private Boolean hasLongRent;
    private AgentConfig config;
    private List<OpenInfo> openInfo;

    private UserAccount account;


    @Data
    @Builder
    public static class UserAccount {

        private double deposit;
        private double requireDeposit;
        private String refundStatus;
        private String zmScore;
        private double balance;
        private double giftBalance;
        private String wxscore;
    }

    @Data
    @Builder
    public static class AgentConfig {
        private boolean longRentDisplay;
        private boolean takeawayDisplay;
        private boolean depositDisplay;
        private boolean zmScoreDisplay;
        private boolean studentAuthDisplay;
        private boolean wxscoreDisplay;
        private int batteryBan;
        private double requireDeposit;
        private int requireScore;
        private double noneDepositFirstCharge;
        private double depositFirstCharge;
        private double minCharge;
        private boolean inviteDisplay;
        private int dayMaxHours;
        private String inputPrefix;
        private String identifyType;
    }

    @Data
    @Builder
    public static class OpenInfo {
        private String appId;
        private String openId;
    }

    public static UserResponse build(User user, String studentAuth, String city, Long agentId, Boolean hasLongRent, AgentCfg config, List<OpenInfo> openInfo) {
        com.qdigo.ebike.usercenter.domain.entity.UserAccount account = user.getAccount();
        val mobileNo = user.getMobileNo();
        return UserResponse.builder()
                .userId(user.getUserId())
                .accessToken(user.getAccessToken())
                .account(UserAccount.builder()
                        .deposit(FormatUtil.getMoney(account.getDeposit()))
                        .requireDeposit(config.getRequireDeposit())
                        .refundStatus(account.getRefundStatus())
                        .zmScore(account.getZmScore())
                        .balance(FormatUtil.getMoney(account.getBalance()))
                        .giftBalance(account.getGiftBalance())
                        .wxscore(account.getWxscore())
                        .build())
                .config(AgentConfig.builder()
                        .longRentDisplay(config.isLongRentDisplay())
                        .takeawayDisplay(config.isTakeawayDisplay())
                        .depositDisplay(config.isDepositDisplay())
                        .zmScoreDisplay(config.isZmScoreDisplay())
                        .studentAuthDisplay(config.isStudentAuthDisplay())
                        .wxscoreDisplay(config.isWxscoreDisplay())
                        .batteryBan(config.getBatteryBan())
                        .requireDeposit(config.getRequireDeposit())
                        .requireScore(config.getRequireScore())
                        .noneDepositFirstCharge(config.getNoneDepositFirstCharge())
                        .depositFirstCharge(config.getDepositFirstCharge())
                        .minCharge(config.getMinCharge())
                        .inviteDisplay(config.isInviteDisplay())
                        .dayMaxHours(config.getDayMaxHours())
                        .inputPrefix(config.getInputPrefix())
                        .identifyType(config.getIdentifyType().name())
                        .build())
                .fullName(user.getFullName())
                .idNo(user.getIdNo())
                .mobileNo(user.getMobileNo())
                .profileImageId(user.getProfileImageId())
                .realName(user.getRealName())
                .wxliteOpenId(user.getWxliteOpenId())
                .studentAuth(studentAuth)
                .city(city)
                .agentId(agentId)
                .hasLongRent(hasLongRent)
                .openInfo(openInfo)
                .build();
    }

    public UserInfo toUserInfo() {
        return UserInfo.builder().realName(realName)
                .mobileNo(mobileNo)
                .idNo(idNo)
                .deposit(FormatUtil.getMoney(account.deposit))
                .balance(FormatUtil.getMoney(account.balance))
                .giftBalance(FormatUtil.getMoney(account.giftBalance))
                .refundStatus(account.refundStatus)
                .requireDeposit(account.requireDeposit)
                .userId(userId)
                .userImgurl(profileImageId)
                .userName(fullName)
                .wxliteOpenId(wxliteOpenId)
                .zmScore(account.zmScore)
                .wxscore(account.wxscore)
                .studentAuth(studentAuth)
                .city(city)
                .agentId(agentId)
                .hasLongRent(hasLongRent)
                .config(config)
                .openInfo(openInfo)
                .build();
    }

    @Data
    @Builder
    public static class UserInfo {
        // 无accessToken
        private long userId;
        private String userName;
        private String userImgurl;
        private double balance;
        private double giftBalance;
        private double deposit;   //押金
        private double requireDeposit;  //应付押金
        private String mobileNo;
        private String realName;
        private String idNo;
        private String refundStatus;
        private String zmScore; //""代表没有查询过
        private String wxscore;
        private String wxliteOpenId; // ""代表不是微信登录
        private String studentAuth;
        private String city;
        private Long agentId;
        private boolean hasLongRent;
        private AgentConfig config; //配置表
        private List<OpenInfo> openInfo; //代表不同平台需要的openId
    }

}
