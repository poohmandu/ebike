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

import com.qdigo.ebike.common.core.constants.Status;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by niezhao on 2017/9/5.
 */
@Entity
@Table(name = "user_student")
public class UserStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "mobile_no", nullable = false, unique = true, length = 20)
    private String mobileNo;

    @Column(nullable = false)
    private String studentNo;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String schoolName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status.StuAuthStatus authStatus;

    private Date startTime;

    private Date endTime;

    private Date applyStartTime;

    private Date applyEndTime;

    @Column(nullable = false)
    private String stuIdImg;

    @NotNull
    private String failMsg;

    public long getId() {
        return id;
    }

    public UserStudent setId(long id) {
        this.id = id;
        return this;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public UserStudent setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public UserStudent setStudentNo(String studentNo) {
        this.studentNo = studentNo;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public UserStudent setProvince(String province) {
        this.province = province;
        return this;
    }

    public String getCity() {
        return city;
    }

    public UserStudent setCity(String city) {
        this.city = city;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public UserStudent setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public UserStudent setSchoolName(String schoolName) {
        this.schoolName = schoolName;
        return this;
    }

    public Status.StuAuthStatus getAuthStatus() {
        return authStatus;
    }

    public UserStudent setAuthStatus(Status.StuAuthStatus authStatus) {
        this.authStatus = authStatus;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public UserStudent setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public UserStudent setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Date getApplyStartTime() {
        return applyStartTime;
    }

    public UserStudent setApplyStartTime(Date applyStartTime) {
        this.applyStartTime = applyStartTime;
        return this;
    }

    public Date getApplyEndTime() {
        return applyEndTime;
    }

    public UserStudent setApplyEndTime(Date applyEndTime) {
        this.applyEndTime = applyEndTime;
        return this;
    }

    public String getStuIdImg() {
        return stuIdImg;
    }

    public UserStudent setStuIdImg(String stuIdImg) {
        this.stuIdImg = stuIdImg;
        return this;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public UserStudent setFailMsg(String failMsg) {
        this.failMsg = failMsg;
        return this;
    }
}
