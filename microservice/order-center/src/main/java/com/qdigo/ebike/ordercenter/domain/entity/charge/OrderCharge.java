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

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by niezhao on 2017/3/10.
 */
@Data
@Entity
@Accessors(chain = true)
@Table(name = "order_charge", indexes = @Index(unique = true, columnList = "order_no"))
@EqualsAndHashCode(callSuper = true)
public class OrderCharge extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1662127441736383372L;
    //基本数据类型映射为not null ; 包装类型映射为 null

    @Id
    @Column(name = "charge_id")
    private String chargeId;

    private boolean livemode;

    private boolean paid;//是否已付款

    private boolean refunded; //是否存在退款信息，无论退款是否成功

    private String app;

    @NotNull
    private String channel;
    @NotNull
    @Column(unique = true, name = "order_no")
    private String orderNo;//商户订单号
    @NotNull
    private String clientIp;//发起支付请求客户端的 IP 地址，格式为 IPv4 整型，如 127.0.0.1

    private int amount;//订单总金额,单位分

    private int amountSettle;//清算金额，单位为对应币种的最小货币单位，人民币为分
    @NotNull
    private String currency;//3 位 ISO 货币代码，人民币为  cny
    @NotNull
    private String subject;//商品标题
    @NotNull
    private String body;//商品描述信息

    private Long timePaid;//订单支付完成时的 Unix 时间戳

    //订单失效时的 Unix 时间戳。默认为 1 天微信对该参数的有效值限制为 2 小时内
    private long timeExpire;

    private String transactionNo;

    private int amountRefunded;

    private String failureCode;

    private String failureMsg;

    private int payType; //租金 还是 押金

    private String description;

    private long created;

    private Long timeSettle;

    @Column(name = "user_account_id", nullable = false)
    private Long userAccountId;

    @Column(length = 100, columnDefinition = "varchar(100) default ''", nullable = false)
    private String payAccount; //退款账户
}

