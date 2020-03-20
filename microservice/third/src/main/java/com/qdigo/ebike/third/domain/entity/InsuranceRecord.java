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

package com.qdigo.ebike.third.domain.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 保单与骑行记录关联表
 * Created by jiangchen on 2017/9/1.
 */
@Entity
@Table(name = "insurance_record")
public class InsuranceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long rideRecordId;

    //姓名
    @Column(nullable = false)
    private String name;
    //身份证号
    @Column(nullable = false)
    private String idNo;
    //手机号
    @Column(nullable = false)
    private String mobileNo;
    //商户订单号，海绵保所传
    @Column(nullable = false)
    private String orderSn;
    //保单号，海绵保所传
    @Column(nullable = false)
    private String policyNo = "";
    //错误码，海绵保所传
    @Column(nullable = false)
    private String errorCode = "";
    //错误信息，海绵保所传
    @Column(nullable = false)
    private String errorMsg = "";
    @Column(nullable = false)
    private String productCode;
    @Column(nullable = false)
    private Date startTime;
    @Column(nullable = false)
    private Date endTime;
    @Column(nullable = false, columnDefinition = "varchar(50) default '平安意外伤害险'")
    private String insureType;

    public String getProductCode() {
        return productCode;
    }

    public InsuranceRecord setProductCode(String productCode) {
        this.productCode = productCode;
        return this;
    }

    public long getId() {
        return id;
    }

    public InsuranceRecord setId(long id) {
        this.id = id;
        return this;
    }

    public long getRideRecordId() {
        return rideRecordId;
    }

    public InsuranceRecord setRideRecordId(long rideRecordId) {
        this.rideRecordId = rideRecordId;
        return this;
    }

    public String getName() {
        return name;
    }

    public InsuranceRecord setName(String name) {
        this.name = name;
        return this;
    }

    public String getIdNo() {
        return idNo;
    }

    public InsuranceRecord setIdNo(String idNo) {
        this.idNo = idNo;
        return this;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public InsuranceRecord setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public InsuranceRecord setOrderSn(String orderSn) {
        this.orderSn = orderSn;
        return this;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public InsuranceRecord setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public InsuranceRecord setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public InsuranceRecord setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public InsuranceRecord setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getInsureType() {
        return insureType;
    }

    public void setInsureType(String insureType) {
        this.insureType = insureType;
    }
}
