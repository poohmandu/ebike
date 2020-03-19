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

package com.qdigo.ebike.activitycenter.service.remote.coupon;

import com.qdigo.ebike.activitycenter.domain.entity.coupon.Coupon;
import com.qdigo.ebike.activitycenter.service.inner.coupon.CouponInnerService;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.activity.coupon.CouponDto;
import com.qdigo.ebike.api.service.activity.coupon.CouponService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/18 2:48 PM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CouponServiceImpl implements CouponService {

    private final CouponInnerService couponInnerService;

    @Override
    public Double getFreeConsume(Long userId) {
        return couponInnerService.getFreeConsume(userId);
    }

    @Override
    public CouponDto getConsumeCoupon(Long userId) {
        Coupon coupon = couponInnerService.getConsumeCoupon(userId);
        return ConvertUtil.to(coupon, CouponDto.class);
    }

    @Override
    public void consumeCashCoupon(ConsumeCashParam param) {
        Coupon coupon = ConvertUtil.to(param.getCouponDto(), Coupon.class);
        couponInnerService.consumeCashCoupon(coupon, param.getOriginAmount(), param.getRideRecordId());
    }

}
