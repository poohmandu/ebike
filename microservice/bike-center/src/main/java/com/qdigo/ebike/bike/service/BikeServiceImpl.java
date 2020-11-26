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
import com.qdigo.ebike.api.domain.dto.bike.BikeConfigDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.domain.entity.BikeConfig;
import com.qdigo.ebike.bike.repository.BikeConfigRepository;
import com.qdigo.ebike.bike.repository.BikeRepository;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * Description: 
 * date: 2020/1/3 12:01 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeServiceImpl implements BikeService {

    private final BikeRepository bikeRepository;
    private final BikeConfigRepository bikeConfigRepository;

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

    /**
     * @param imeiOrDeviceId
     * @return null:格式错误 new Bike():没有该车
     */
    @Override
    public BikeDto findByImeiOrDeviceId(String imeiOrDeviceId) {
        log.debug("获取车辆:{}", imeiOrDeviceId);
        Bike bike = null;
        if (imeiOrDeviceId.length() == Const.imeiLength && imeiOrDeviceId.startsWith(ConfigConstants.imei.getConstant())) {
            bike = bikeRepository.findTopByImeiId(imeiOrDeviceId).orElse(new Bike());
        } else if (imeiOrDeviceId.length() == Const.deviceIdLength) {
            bike = bikeRepository.findTopByDeviceId(imeiOrDeviceId).orElse(new Bike());
        }
        return ConvertUtil.to(bike, BikeDto.class);

    }

    @Override
    public BikeConfigDto findConfigByType(String type) {
        BikeConfig bikeConfig = bikeConfigRepository.findById(type).orElse(null);
        return ConvertUtil.to(bikeConfig, BikeConfigDto.class);
    }
}
