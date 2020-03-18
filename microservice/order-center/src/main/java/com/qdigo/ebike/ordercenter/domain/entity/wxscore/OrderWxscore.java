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

package com.qdigo.ebike.ordercenter.domain.entity.wxscore;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 每一笔订单必须与骑车挂钩
 */
@Data
@Entity
@Table(name = "order_wxscore", indexes = {@Index(columnList = "ride_record_id", unique = true)})
public class OrderWxscore {

    @Id
    @Column(name = "out_order_no")
    private String outOrderNo; //商户服务订单号
    @Column(nullable = false, length = 32)
    private String appId;
    @Column(nullable = false, length = 32)
    private String mchId;
    @Column(nullable = false, length = 32)
    private String serviceId;

    @Enumerated(EnumType.STRING)
    private State state;

    @Column(nullable = false)
    private Long startTime;
    @Column(nullable = false)
    private Long endTime;
    @Column(nullable = false)
    private int riskAmount; //单位为分
    @Column(nullable = false)
    private int totalAmount; //单位为分
    @Column(nullable = false)
    private String transactionId; //finish_transaction_id退款需要用到的字段

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderWxscoreFee> fees;
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderWxscoreDiscount> discounts;

    @Column(nullable = false)
    private Long AgentId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false, unique = true, name = "ride_record_id") //一对一
    private Long rideRecordId;

    @Version
    private int version;

    public enum State {
        CREATED, //商户下单已受理
        USER_ACCEPTED,//用户成功使用服务
        FINISHED, //商户完结订单
        USER_PAID, //用户订单支付成功
        REVOKED, //商户撤销订单
        EXPIRED //订单已失效. “商户下单已受理”状态超过1小时未变动，则订单失效
    }

}
