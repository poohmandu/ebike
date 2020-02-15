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
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.bike.repository.BikeRepository;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

/**
 * Description: 
 * date: 2020/1/3 12:01 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeServiceImpl implements BikeService {

    private final BikeRepository bikeRepository;

    @Override
    public BikeDto findByImei(String imeiId) {
        return bikeRepository.findByImeiId(imeiId)
                .map(bike -> ConvertUtil.to(bike, BikeDto.class))
                .orElse(null);
    }

    @Override
    public BikeDto findByDeviceId(String deviceId) {
        return bikeRepository.findByDeviceId(deviceId)
                .map(bike -> ConvertUtil.to(bike, BikeDto.class))
                .orElse(null);
    }
}
