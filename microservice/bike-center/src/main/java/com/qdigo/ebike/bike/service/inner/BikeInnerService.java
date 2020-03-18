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

package com.qdigo.ebike.bike.service.inner;

import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.repository.BikeRepository;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Const;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/17 9:48 AM
 * @author niezhao
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeInnerService {

    private final BikeRepository bikeRepository;

    /**
     * @param imeiIdOrDeviceId
     * @return null:格式错误 new Bike():没有该车
     */
    public Bike findOneByImeiIdOrDeviceId(String imeiIdOrDeviceId) {
        log.debug("获取车辆:{}", imeiIdOrDeviceId);
        Bike bike = null;
        if (imeiIdOrDeviceId.length() == Const.imeiLength && imeiIdOrDeviceId.startsWith(ConfigConstants.imei.getConstant())) {
            bike = bikeRepository.findTopByImeiId(imeiIdOrDeviceId).orElse(new Bike());
        } else if (imeiIdOrDeviceId.length() == Const.deviceIdLength) {
            bike = bikeRepository.findTopByDeviceId(imeiIdOrDeviceId).orElse(new Bike());
        }
        return bike;
    }

}
