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

@Data
@Entity
@Table(name = "order_wxscore_fee")
public class OrderWxscoreFee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "out_order_no", referencedColumnName = "out_order_no")
    private OrderWxscore order;

    @Column(nullable = false, length = 50)
    private String feeName;

    private Integer feeCount;

    private Integer feeAmount; //单位为分

    @Column(nullable = false, length = 100)
    private String feeDesc;

}
