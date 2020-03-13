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

package com.qdigo.ebike.ordercenter.service.remote.ride;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.PageDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.ordercenter.domain.entity.ride.RideOrder;
import com.qdigo.ebike.ordercenter.domain.entity.ride.RideRecord;
import com.qdigo.ebike.ordercenter.repository.RideRecordRepository;
import com.qdigo.ebike.ordercenter.repository.dao.RideRecordDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.inject.Inject;
import java.util.List;

/**
 * Description: 
 * date: 2020/1/15 7:41 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OrderRideServiceImpl implements OrderRideService {

    private final RideRecordDao rideRecordDao;
    private final RideRecordRepository rideRecordRepository;

    @Override
    public RideDto findRidingByImei(String imei) {
        RideOrder rideOrder = rideRecordDao.findByRidingBike(imei);
        return ConvertUtil.to(rideOrder, RideDto.class);
    }

    @Override
    public RideDto findRidingByMobileNo(String mobileNo) {
        RideOrder rideOrder = rideRecordDao.findByRidingUser(mobileNo);
        return ConvertUtil.to(rideOrder, RideDto.class);
    }

    @Override
    public RideDto findById(long rideRecordId) {
        RideRecord rideRecord = rideRecordRepository.findById(rideRecordId).orElse(null);
        return ConvertUtil.to(rideRecord, RideDto.class);
    }

    @Override
    public RideDto findAnyByMobileNo(String mobileNo) {
        RideRecord rideRecord = rideRecordRepository.findOneByMobileNo(mobileNo);
        return ConvertUtil.to(rideRecord, RideDto.class);
    }

    @Override
    public List<RideDto> findEndByMobileNo(String mobileNo) {
        List<RideRecord> rideRecords = rideRecordRepository.findByMobileNoAndRideStatus(mobileNo, Status.RideStatus.end.getVal());
        return ConvertUtil.to(rideRecords, RideDto.class);
    }

    @Override
    public PageDto<RideDto> findEndPageByMobileNo(String mobileNo, Pageable pageable) {
        Page<RideRecord> rideRecordPage = rideRecordRepository.findByMobileNoAndRideStatus(mobileNo, Status.RideStatus.end.getVal(), pageable);
        List<RideDto> rideDtos = ConvertUtil.to(rideRecordPage.getContent(), RideDto.class);
        return new PageDto<>(rideDtos, rideRecordPage.getTotalElements());
    }

}
