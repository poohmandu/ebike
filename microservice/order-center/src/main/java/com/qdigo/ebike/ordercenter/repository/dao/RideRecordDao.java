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
import com.qdigo.ebike.ordercenter.domain.entity.ride.RideOrder;
import com.qdigo.ebike.ordercenter.domain.entity.ride.RideRecord;
import com.qdigo.ebike.ordercenter.repository.RideOrderRepository;
import com.qdigo.ebike.ordercenter.repository.RideRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by niezhao on 2017/6/22.
 */
@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RideRecordDao {

    private final RideRecordRepository rideRecordRepository;
    private final RideOrderRepository rideOrderRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public RideOrder findByRidingBike(String imei) {
        return rideOrderRepository.findByImei(imei);
    }

    public Optional<RideRecord> findRecordByRidingBike(String imei) {
        return rideRecordRepository.findOne((root, query, cb) -> {
            final Predicate predicate = cb.equal(root.get("imei"), imei);
            final Predicate in = root.get("rideStatus").in(Status.RideStatus.invalid.getVal(), Status.RideStatus.running.getVal());
            query.where(predicate, in);
            return query.getRestriction();
        });
    }

    public RideOrder findByRidingUser(String mobileNo) {
        return rideOrderRepository.findByMobileNo(mobileNo);
    }

    //@CatAnnotation
    public Optional<RideRecord> findRecordByRidingUser(String mobileNo) {
        return rideRecordRepository.findOne((root, query, cb) -> {
            final Predicate predicate = cb.equal(root.get("mobileNo"), mobileNo);
            final Predicate in = root.get("rideStatus").in(Status.RideStatus.invalid.getVal(), Status.RideStatus.running.getVal());
            return query.where(predicate, in).getRestriction();
        });
    }

    public RideOrder findRideOrder(String mobileNo, String imei) {
        return rideOrderRepository.findByMobileNoAndImei(mobileNo, imei);
    }

    public List<RideOrder> findRidingBefore(Date start) {
        String sql = "select o.* from ride_order o where o.start_time < :startTime";
        return entityManager.createNativeQuery(sql, RideOrder.class)
                .setParameter("startTime", start)
                .getResultList();
    }

}
