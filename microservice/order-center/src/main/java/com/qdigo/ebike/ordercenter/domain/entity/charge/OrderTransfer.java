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

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by niezhao on 2017/10/18.
 */

@Entity
@Data
@Table(name = "order_transfer")
@Accessors(chain = true)
public class OrderTransfer {

    @Id
    @Column(name = "transferId")
    private String transferId;

    @Column(nullable = false)
    private String type = "";

    @Column(nullable = false)
    private int payType; //租金 还是 押金

    @Column(nullable = false)
    private long created = 0;

    @Column(nullable = false)
    private long timeTransferred = 0;

    @Column(nullable = false)
    private boolean livemode = true;

    @Column(nullable = false)
    private String status = "";

    @Column(nullable = false)
    private String app = "";

    @Column(nullable = false)
    private String channel = "";

    @Column(nullable = false)
    private String orderNo = "";

    @Column(nullable = false)
    private int amount = 0;

    @Column(nullable = false)
    private int amountSettle = 0;

    @Column(nullable = false)
    private String currency = "";

    @Column(nullable = false)
    private String recipient = "";

    @Column(nullable = false)
    private String description = "";

    @Column(nullable = false)
    private String failureMsg = "";

    @Column(nullable = false)
    private String transactionNo = "";

    @Column(nullable = false, name = "user_account_id")
    private Long userAccountId;

    @Column(nullable = false)
    private String chargeId = "";
}
