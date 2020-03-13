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

package com.qdigo.ebike.activitycenter.domain.entity.coupon;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by niezhao on 2017/11/30.
 * 通过一个 coupon_template 对象在指定用户下创建一个 coupon 对象用于生成一张骑行券，
 * coupon 对象的有效时间根据关联的 coupon_template 对象由系统自动计算得出，可以在有效时间内在订单创建时使用
 */
@Data
@Entity
@Table(name = "ride_coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private boolean valid = true; //优惠券过期、优惠券已使用、关联的优惠券模板删除会使该优惠券变为不可用

    private boolean redeemed = false; //是否已经使用（核销）

    private long userId; // 用户id

    private Long rideRecordId; // 消费记录，使用后生效

    private Long agentId; // 所属代理商,领券时决定

    private double originAmount; //原始金额;

    @Column(nullable = false)
    private Date startTime; //可用的开始时间

    @Column(nullable = false)
    private Date endTime; //可用的结束时间

    private int userTimesCirculated; //优惠券在该用户下当前生成次数。

    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_template_id", nullable = false)
    private CouponTemplate couponTemplate;

    private long created = System.currentTimeMillis();
}
