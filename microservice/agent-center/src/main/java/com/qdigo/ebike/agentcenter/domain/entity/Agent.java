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

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Created by niezhao on 2016/11/26.
 * 代理商表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "agent", indexes = {@Index(columnList = "agent_code", unique = true)})
public class Agent extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "agent_id")
    private Long agentId;

    private String agentMerchantName; //代理商商户名

    private String agentName; //代理商责任人姓名

    @Column(nullable = false, unique = true, name = "agent_code")
    private String agentCode;     //代理商编号

    private String address;      //代理商地址

    private String mobileNo;     //代理商电话号码

    private String agentProvince;//代理商所在省份

    @Column(nullable = false)
    private String city;         //代理商所在城市

    private String agentIdNo;    //代理商身份证号

    private String agentLoginNo;  //代理商登录账号

    private String agentPassword; //代理商登录密码

    //@JsonManagedReference
    //@JSONField(serialize = false)
    //@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "agent")
    //private List<Bike> bikeList;

    //@JsonManagedReference
    //@JSONField(serialize = false)
    //@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "agent")
    //private List<BikeStation> bikeStationList;

    //@JsonManagedReference
    //@JSONField(serialize = false)
    //@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "agent")
    //private List<AgentOrder> agentOrderList;

    //骑行卡配置
    //@JsonManagedReference
    //@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "agent")
    //private LongTermRent longTermRent;

    private String company;  //代理商单位名称

    private Double profit;   //利润率

    @Column(length = 20, columnDefinition = "varchar(20) default 'own'", nullable = false)
    @Enumerated(EnumType.STRING)
    private Const.AgentType AgentType;

    private Long parentId;

    private String operationDistrict = ""; //运营区域

    private Boolean isDeleted = false;  //代理商启用和停用

    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.ALL}, mappedBy = "agent", fetch = FetchType.LAZY)
    private AgentConfig config;

    //@ManyToMany(mappedBy = "agentList")
    //@JSONField(serialize = false)
    //private List<AgentNotice> noticeList;

}
