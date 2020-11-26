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

package com.qdigo.ebike.api.domain.dto.third.wx.wxscore;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class WxscoreOrder {

    @NotNull
    private String appid;
    @NotNull
    private String mchid;
    @NotNull
    private String out_order_no;
    @NotNull
    private String service_id;
    @NotNull
    private String state;

    private Integer finish_type;
    @NotNull
    private String service_start_time; //20190705173549  yyyyMMddHHmmss

    private String service_end_time;

    private String real_service_start_time;

    private String real_service_end_time;

    private String pay_succ_time;
    @NotNull
    private String service_start_location;

    private String service_end_location;
    @NotNull
    private String service_introduction;
    @NotNull
    private List<Fee> fees;
    private List<Discount> discounts;
    @NotNull
    private Integer risk_amount;
    private Integer total_amount;
    private String attach; //商户数据包,可存放本订单所需信息. 需要先urlencode后传入. 总长度不大于200字符,超出报错处理.
    @NotNull
    private String finish_ticket; //完结凭据，用于完结订单时传入,确保订单完结时数据完整. 只有单据状态为USER_ACCEPTED才返回完结凭证

    private String finish_transaction_id; //结单交易单号,等于普通支付接口中的transaction_id，
    // 可以使用该订单号在APP支付->API列表->查询订单进行查询订单、申请退款，只有单据状态为USER_PAID才会返回结单交易单号

    private String pay_type; //MchChannelPayType

    @Data
    public static class Fee {
        @NotNull
        private String fee_name;
        private Integer fee_count;
        private Integer fee_amount;
        private String fee_desc;
    }

    @Data
    public static class Discount {
        @NotNull
        private String discount_name;
        private Integer discount_amount;
        private String discount_desc;
    }
}
