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

package com.qdigo.ebike.ordercenter.domain.entity;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "third_order_record")
public class ThirdOrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long thirdOrderRecordId;

    @Column(nullable = false)
    private String serviceName; //服务方名称

    @Column(nullable = false)
    private String apiName; //接口名称

    @Column(nullable = false)
    private Long agentId;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private Boolean succeed;

    @Column(nullable = false)
    private Double amount;

    public Long getThirdOrderRecordId() {
        return thirdOrderRecordId;
    }

    public ThirdOrderRecord setThirdOrderRecordId(Long thirdOrderRecordId) {
        this.thirdOrderRecordId = thirdOrderRecordId;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ThirdOrderRecord setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getApiName() {
        return apiName;
    }

    public ThirdOrderRecord setApiName(String apiName) {
        this.apiName = apiName;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public ThirdOrderRecord setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public Date getTime() {
        return time;
    }

    public ThirdOrderRecord setTime(Date time) {
        this.time = time;
        return this;
    }

    public Boolean getSucceed() {
        return succeed;
    }

    public ThirdOrderRecord setSucceed(Boolean succeed) {
        this.succeed = succeed;
        return this;
    }

    public Double getAmount() {
        return amount;
    }

    public ThirdOrderRecord setAmount(Double amount) {
        this.amount = amount;
        return this;
    }
}
