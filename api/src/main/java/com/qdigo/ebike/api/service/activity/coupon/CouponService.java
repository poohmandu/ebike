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

package com.qdigo.ebike.api.service.activity.coupon;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.activity.coupon.CouponDto;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * description: 
 *
 * date: 2020/3/18 1:44 PM
 * @author niezhao
 */
@FeignClient(name = "activity-center", contextId = "activity-coupon")
public interface CouponService {

    @PostMapping(ApiRoute.ActivityCenter.Coupon.getFreeConsume)
    Double getFreeConsume(@RequestParam("userId") Long userId);

    @PostMapping(ApiRoute.ActivityCenter.Coupon.getConsumeCoupon)
    CouponDto getConsumeCoupon(@RequestParam("userId") Long userId);

    @PostMapping(ApiRoute.ActivityCenter.Coupon.consumeCashCoupon)
    void consumeCashCoupon(@RequestBody ConsumeCashParam param);

    @Data
    @Accessors(chain = true)
    class ConsumeCashParam {
        private CouponDto couponDto;
        private double originAmount;
        private long rideRecordId;
    }
}
