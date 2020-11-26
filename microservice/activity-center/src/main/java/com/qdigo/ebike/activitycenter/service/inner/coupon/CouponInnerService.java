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

package com.qdigo.ebike.activitycenter.service.inner.coupon;

import com.google.common.collect.Lists;
import com.qdigo.ebike.activitycenter.domain.entity.coupon.Coupon;
import com.qdigo.ebike.activitycenter.domain.entity.coupon.CouponTemplate;
import com.qdigo.ebike.activitycenter.repository.CouponRepository;
import com.qdigo.ebike.activitycenter.repository.CouponTemplateRepository;
import com.qdigo.ebike.activitycenter.repository.dao.CouponDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * Created by niezhao on 2018/1/19.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CouponInnerService {

    private final CouponDao couponDao;
    private final CouponRepository couponRepository;
    private final CouponTemplateRepository couponTemplateRepository;

    //现在只有现金券，只能这样
    //@CatAnnotation
    public double getFreeConsume(long userId) {
        Coupon coupon = this.getConsumeCoupon(userId);
        if (coupon == null) {
            return 0;
        }
        CouponTemplate template = coupon.getCouponTemplate();
        return template.getAmountOff();
    }

    public Coupon getConsumeCoupon(long userId) {
        return couponDao.findCashCoupon(userId);
    }

    //消费现金券
    @Transactional
    public void consumeCashCoupon(Coupon coupon, double originAmount, long rideRecordId) {
        CouponTemplate template = coupon.getCouponTemplate();
        int timesRedeemed = template.getTimesRedeemed();
        timesRedeemed++;
        template.setTimesRedeemed(timesRedeemed);
        coupon.setRideRecordId(rideRecordId);
        coupon.setOriginAmount(originAmount);
        coupon.setValid(false);
        coupon.setRedeemed(true);
        couponRepository.save(coupon);
    }

    public List<Coupon> findInviteCoupons(long userId) {
        List<Coupon> couponList;
        List<CouponTemplate> list = couponTemplateRepository.findByDstAndType(CouponTemplate.Destination.invite, CouponTemplate.Type.cash);
        if (list.isEmpty()) {
            return null;
        }
        CouponTemplate couponTemplate = list.get(0);
        couponList = couponRepository.findByUserIdAndCouponTemplateId(userId, couponTemplate.getId());
        return couponList;
    }

    //这个只能用于创建邀请券
    @Transactional
    public void createInviteCoupons(long userId, Long agentId, int number) {
        List<CouponTemplate> list = couponTemplateRepository.findByDstAndType(CouponTemplate.Destination.invite, CouponTemplate.Type.cash);
        if (list.isEmpty()) {
            return;
        }
        CouponTemplate couponTemplate = list.get(0);
        if (couponTemplate.getType() != CouponTemplate.Type.cash) {
            return;
        } else if (couponTemplate.getDst() != CouponTemplate.Destination.invite) {
            return;
        }
        int timesCirculated = couponTemplate.getTimesCirculated();
        Long duration = couponTemplate.getDuration();
        List<Coupon> coupons = Lists.newArrayList();
        for (int i = 0; i < number; i++) {
            Coupon coupon = new Coupon();
            coupon.setUserId(userId);
            coupon.setAgentId(agentId);
            coupon.setStartTime(new Date());
            coupon.setEndTime(new Date(System.currentTimeMillis() + duration));
            coupon.setUserTimesCirculated(timesCirculated + i);
            coupon.setCouponTemplate(couponTemplate);
            coupons.add(coupon);
        }
        timesCirculated += number;
        couponTemplate.setTimesCirculated(timesCirculated);
        couponRepository.saveAll(coupons);
    }

}
