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

package com.qdigo.ebike.ordercenter.repository.dao;

import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderCharge;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by niezhao on 2017/12/9.
 */
@Repository
public class OrderChargeDao {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean hasRentCharges(Long userAccountId) {
        Query query = entityManager.createNativeQuery("SELECT o.* FROM order_charge o WHERE o.user_account_id =?1 " +
                "AND o.pay_type = ?2 AND o.paid=TRUE LIMIT 1", OrderCharge.class);
        query.setParameter(1, userAccountId)
                .setParameter(2, Status.PayType.rent.getVal());
        return query.getResultList().size() > 0;
    }

}
