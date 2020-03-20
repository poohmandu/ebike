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

package com.qdigo.ebike.usercenter.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Description: 
 * date: 2019/12/18 5:21 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Entity
@Table(name = "user", indexes = {@Index(columnList = "mobile_no", unique = true)})
@JsonIgnoreProperties(value = {"userRecord", "userCredit"})
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, length = 20, unique = true, name = "user_id")
    private long userId;

    @Column(length = 30, nullable = false, unique = true, name = "mobile_no")
    private String mobileNo;

    private boolean isActive = true;

    // 全名
    private String fullName = "小滴";

    //真实姓名
    private String realName = "";

    // 身份证号码
    @Column(length = 30, nullable = false, unique = true)
    private String idNo = "";

    // 头像id
    private String profileImageId = "";
    // 设备号绑定
    private String deviceId = "";

    @Column(nullable = false, columnDefinition = "varchar(20) default '86'", length = 20)
    private String countryCode = "86";

    //口令
    @NotNull
    private String accessToken = "";

    //账户
    @OneToOne(mappedBy = "user", optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserAccount account;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "user")
    @OrderBy(value = "curTime DESC")
    private List<UserRecord> userRecord;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private UserCredit userCredit;

    @Column(nullable = false, length = 100)
    private String wxliteOpenId = ""; //微信小程序的openid

    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    public long getUserId() {
        return userId;
    }

    public User setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public User setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public boolean isActive() {
        return isActive;
    }

    public User setActive(boolean active) {
        isActive = active;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public User setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public User setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getIdNo() {
        return idNo;
    }

    public User setIdNo(String idNo) {
        this.idNo = idNo;
        return this;
    }

    public String getProfileImageId() {
        return profileImageId;
    }

    public User setProfileImageId(String profileImageId) {
        this.profileImageId = profileImageId;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public User setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public User setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public User setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public UserAccount getAccount() {
        return account;
    }

    public User setAccount(UserAccount account) {
        this.account = account;
        return this;
    }

    public List<UserRecord> getUserRecord() {
        return userRecord;
    }

    public User setUserRecord(List<UserRecord> userRecord) {
        this.userRecord = userRecord;
        return this;
    }

    public UserCredit getUserCredit() {
        return userCredit;
    }

    public User setUserCredit(UserCredit userCredit) {
        this.userCredit = userCredit;
        return this;
    }

    public String getWxliteOpenId() {
        return wxliteOpenId;
    }

    public User setWxliteOpenId(String wxliteOpenId) {
        this.wxliteOpenId = wxliteOpenId;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public User setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }
}
