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

package com.qdigo.ebike.ordercenter.repository.ride;

import com.qdigo.ebike.ordercenter.domain.entity.ride.RideRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by niezhao on 2017/4/19.
 */
public interface RideRecordRepository extends JpaRepository<RideRecord, Long>, JpaSpecificationExecutor<RideRecord> {

    List<RideRecord> findByImei(String imei);

    List<RideRecord> findByMobileNo(String mobileNo);

    @Query(value = "SELECT * FROM ride_record WHERE mobile_no = ?1 ORDER BY ride_record_id DESC LIMIT 1", nativeQuery = true)
    RideRecord findOneByMobileNo(String mobileNo);

    List<RideRecord> findByRideStatus(int rideStatus);

    List<RideRecord> findByRideStatusIn(int... rideStatus);

    List<RideRecord> findByMobileNoAndRideStatus(String mobileNo, int rideStatus);

    Page<RideRecord> findByMobileNoAndRideStatus(String mobileNo, int rideStatus, Pageable pageable);

    List<RideRecord> findByMobileNoAndRideStatusIn(String mobileNo, int... rideStatus);

    List<RideRecord> findByImeiAndRideStatusIn(String imei, int... rideStatus);

    List<RideRecord> findByImeiAndRideStatus(String imei, int rideStatus);

    @Query(value = "SELECT count(start_time) FROM ride_record WHERE imei = ?1 AND ride_status = '2'" +
            " AND TO_DAYS(NOW()) - TO_DAYS(start_time) <= 1", nativeQuery = true)
    BigInteger orderAmountPerDayfindByImei(String imei);

}
