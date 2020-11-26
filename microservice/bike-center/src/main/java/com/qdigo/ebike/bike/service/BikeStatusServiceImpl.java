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

package com.qdigo.ebike.bike.service;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.domain.entity.BikeStatus;
import com.qdigo.ebike.bike.repository.BikeRepository;
import com.qdigo.ebike.bike.repository.BikeStatusRepository;
import com.qdigo.ebike.bike.repository.dao.BikeStatusDao;
import com.qdigo.ebike.bike.service.inner.BikeStatusInnerService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * Description: 
 * date: 2020/2/23 6:07 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeStatusServiceImpl implements BikeStatusService {

    private final BikeStatusRepository statusRepository;
    private final BikeStatusDao bikeStatusDao;
    private final BikeStatusInnerService bikeStatusInnerService;
    private final BikeRepository bikeRepository;

    @Override
    public BikeStatusDto findStatusByBikeIId(Long bikeId) {
        return statusRepository.findByBikeBikeId(bikeId)
                .map(bikeStatus -> ConvertUtil.to(bikeStatus, BikeStatusDto.class))
                .orElse(null);
    }

    @Override
    public BikeStatusDto findByImei(String imei) {
        BikeStatus bikeStatus = bikeStatusDao.findByImei(imei);
        return ConvertUtil.to(bikeStatus, BikeStatusDto.class);
    }

    @Override
    public void update(BikeStatusDto bikeStatusDto) {
        BikeStatus bikeStatus = statusRepository.findById(bikeStatusDto.getBikeStatusId()).get();
        bikeStatusDto.updated(bikeStatus);
        statusRepository.save(bikeStatus);
    }

    @Override
    public String queryActualStatus(Long bikeId) {
        Bike bike = bikeRepository.findById(bikeId).orElse(null);
        return bikeStatusInnerService.queryActualStatus(bike);
    }

}
