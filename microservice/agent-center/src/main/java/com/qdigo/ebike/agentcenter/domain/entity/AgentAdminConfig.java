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

import javax.persistence.*;

@Entity
@Table(name = "agent_admin_config")
public class AgentAdminConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Long agentId;

    private Integer lowBatteryDisPlay = 40; //后台低电量显示

    private Integer longRent = 300; //长时间未还时间设置（单位：分钟）

    private Boolean opsHtReturnBike = false; //管理员手动还车是否给代理商开通

    private Boolean opsBikeBinding = false;  //管理员车辆扫码入库功能

    private String rescueMobile = "4001787007";   //运维救援专用号

    private String customerNo = "4001787007"; //客服号

    private Boolean blueTooth = false;

    public Boolean getBlueTooth() {
        return blueTooth;
    }

    public AgentAdminConfig setBlueTooth(Boolean blueTooth) {
        this.blueTooth = blueTooth;
        return this;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public AgentAdminConfig setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
        return this;
    }

    public String getRescueMobile() {
        return rescueMobile;
    }

    public AgentAdminConfig setRescueMobile(String rescueMobile) {
        this.rescueMobile = rescueMobile;
        return this;
    }

    public Boolean getOpsHtReturnBike() {
        return opsHtReturnBike;
    }

    public AgentAdminConfig setOpsHtReturnBike(Boolean opsHtReturnBike) {
        this.opsHtReturnBike = opsHtReturnBike;
        return this;
    }

    public Integer getLongRent() {
        return longRent;
    }

    public AgentAdminConfig setLongRent(Integer longRent) {
        this.longRent = longRent;
        return this;
    }

    public long getId() {
        return id;
    }

    public AgentAdminConfig setId(long id) {
        this.id = id;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public AgentAdminConfig setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public Integer getLowBatteryDisPlay() {
        return lowBatteryDisPlay;
    }

    public AgentAdminConfig setLowBatteryDisPlay(Integer lowBatteryDisPlay) {
        this.lowBatteryDisPlay = lowBatteryDisPlay;
        return this;
    }

    public Boolean getOpsBikeBinding() {
        return opsBikeBinding;
    }

    public AgentAdminConfig setOpsBikeBinding(Boolean opsBikeBinding) {
        this.opsBikeBinding = opsBikeBinding;
        return this;
    }
}
