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

package com.qdigo.ebike.activitycenter.repository.dao;

import com.qdigo.ebike.activitycenter.domain.entity.coupon.Coupon;
import com.qdigo.ebike.activitycenter.domain.entity.coupon.CouponTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by niezhao on 2018/1/22.
 */
@Repository
public class CouponDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Coupon findCashCoupon(long userId) {
        String sql = "SELECT c.* FROM ride_coupon c LEFT JOIN ride_coupon_template t ON c.coupon_template_id = t.id " +
            "WHERE c.valid=TRUE AND c.redeemed=FALSE AND now() BETWEEN c.start_time AND c.end_time AND t.type=:type AND c.user_id=:userId " +
            "ORDER BY t.amount_off DESC ,c.end_time ASC LIMIT 1";
        Query query = entityManager.createNativeQuery(sql, Coupon.class)
            .setParameter("type", CouponTemplate.Type.cash.name())
            .setParameter("userId", userId);
        List<Coupon> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
