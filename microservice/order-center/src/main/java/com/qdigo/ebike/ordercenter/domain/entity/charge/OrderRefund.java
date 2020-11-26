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

package com.qdigo.ebike.ordercenter.domain.entity.charge;

import com.qdigo.ebike.common.core.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by niezhao on 2016/12/2.
 */
@Data
@Entity
@Accessors(chain = true)
@Table(name = "order_refund")
@EqualsAndHashCode(callSuper = true)
public class OrderRefund extends AbstractAuditingEntity {

    private static final long serialVersionUID = -2694118137569955030L;

    @Id
    @Column(length = 40)
    private String orderRefundId;
    @Column(unique = true, nullable = false)
    private String orderNo; //退款的订单号，由 Ping++ 生成。
    private int amount; //退款金额 //单位: 分
    private boolean succeed;//是否成功
    @Column(length = 20)
    private String status; //pending(处理中),succeeded ,failed"
    private String description = ""; //描述
    @Column(length = 1000)
    private String failureMsg; //失败原因
    private String failureCode = "";//成功时为null
    private long created;  //单位:秒  时间戳要 *1000
    private Long timeSucceed;

    @Column(name = "charge_id", nullable = false)
    private String chargeId;

}

