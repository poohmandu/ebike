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

package com.qdigo.ebike.ordercenter.repository.charge;

import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Created by niezhao on 2017/3/10.
 */
public interface OrderChargeRepository extends JpaRepository<OrderCharge, Serializable>, JpaSpecificationExecutor<OrderCharge> {

    List<OrderCharge> findByPaid(boolean paid);

    List<OrderCharge> findByUserAccountId(Long userAccountId);

    @Query("select o from OrderCharge o where o.userAccountId = ?1 and o.payType = ?2 and o.paid = true and o.amountRefunded = 0")
    List<OrderCharge> findNoRefundByUserAccountIdAndPayType(Long userAccountId, int payType);

    @Query("select o from OrderCharge o where o.userAccountId=?1 and o.paid=true and o.amountRefunded=0 and o.payType=1")
    List<OrderCharge> findDepositNotRefund(Long userAccountId);

    @Query("select count(o) > 1 from OrderCharge o where o.userAccountId =?1 and o.payType = ?2 and o.paid = true")
    boolean hasCharge(Long userAccountId, int payType);

    Optional<OrderCharge> findByOrderNo(String orderNo);

}
