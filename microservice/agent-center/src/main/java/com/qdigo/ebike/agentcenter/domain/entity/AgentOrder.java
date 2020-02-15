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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yz on 2017/5/19.
 * 代理商订单表
 */
@Entity
@Table(name = "agent_order")
@JsonIgnoreProperties(value = {"agent"})
public class AgentOrder extends AbstractAuditingEntity {

    private static final long serialVersionUID = 4914546031111204672L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long agentOrderId;

    private Date  refDate;    //提交日期

    private String productNo;  //所选产品编号

    private Integer quantity; //数量

    private String city;     //投放城市

    private String receiver;  //收货人姓名

    private String address ; //收货地址

    private String phone ;   //收货人联系电话

    private String agentOrderNo; // 订单编号

    @JsonBackReference
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Agent agent;

    public Agent getAgent() {
        return agent;
    }

    public String getAgentOrderNo() {
        return agentOrderNo;
    }

    public AgentOrder setAgentOrderNo(String agentOrderNo) {
        this.agentOrderNo = agentOrderNo;
        return this;
    }

    public AgentOrder setAgent(Agent agent) {
        this.agent = agent;
        return this;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getAgentOrderId() {
        return agentOrderId;
    }

    public AgentOrder setAgentOrderId(Long agentOrderId) {
        this.agentOrderId = agentOrderId;
        return this;
    }

    public Date getRefDate() {
        return refDate;
    }

    public AgentOrder setRefDate(Date refDate) {
        this.refDate = refDate;
        return this;
    }

    public String getProductNo() {
        return productNo;
    }

    public AgentOrder setProductNo(String productNo) {
        this.productNo = productNo;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public AgentOrder setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getCity() {
        return city;
    }

    public AgentOrder setCity(String city) {
        this.city = city;
        return this;
    }

    public String getReceiver() {
        return receiver;
    }

    public AgentOrder setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public AgentOrder setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public AgentOrder setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
