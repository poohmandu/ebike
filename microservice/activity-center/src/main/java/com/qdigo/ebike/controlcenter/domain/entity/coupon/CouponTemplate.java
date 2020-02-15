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

package com.qdigo.ebike.controlcenter.domain.entity.coupon;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by niezhao on 2017/11/30.
 * 在向用户发放优惠券之前，需要通过 coupon_template 对象创建优惠券模板（领取中心的"骑行券"）
 * 优惠券模板记录了骑行券的折扣规则和有效期等信息。
 */
@Data
@Entity
@Table(name = "ride_coupon_template")
public class CouponTemplate {// 静态数据

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name; //优惠券模板名称

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Destination dst;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Type type; //优惠类型

    private double amountOff = 0; //折扣金额,现金券类型下生效。

    private double percentOff = 0; //折扣百分比, 折扣券类型下生效。例如值为 "20" 表示 8 折，值为 "100" 表示免费。

    private double amountAvailable = 0; //订单金额大于等于该值时,优惠券有效（用于设置满减券）。"0" 表示无限制。

    private Integer maxCirculation; //优惠券最大生成数量，当已生成数量达到最大值时，不能再生成优惠券；取值范围为 1-1000000 或 不填，表示可以无限生成。

    private int maxUserCirculation = 1; //单个用户优惠券最大生成数量，当已生成数量达到最大值时，不能再生成优惠券，删除优惠券不会影响该值；取值范围为 1 - 100，默认值为 1。

    private int timesCirculated = 0; //优惠券生成数量(当前)

    private int timesRedeemed = 0; //优惠券核销数量(当前)

    private Date startTime; //优惠券可用的开始时间

    private Date endTime; //优惠券可用的结束时间

    private Long duration; //优惠券创建后的过期时间，单位为豪秒;不能和time_start、time_end 同时使用

    private long created;

    @Version
    private int version;

    public enum Type {
        cash, //现金券
        discount //折扣券
    }

    public enum Destination {
        invite("好友邀请"), //好友邀请
        compensate("系统补偿"), //补偿受损用户
        activity("活动"); //代理商活动
        public String val;

        Destination(String dst) {
            this.val = dst;
        }
    }

}
