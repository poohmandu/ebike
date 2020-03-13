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

package com.qdigo.ebike.controlcenter.service.inner.datagram;

import com.qdigo.ebicycle.constants.Const;
import com.qdigo.ebicycle.domain.bike.Bike;
import com.qdigo.ebicycle.domain.bike.BikeStatus;
import com.qdigo.ebicycle.domain.mongo.device.PLPackage;
import com.qdigo.ebicycle.repository.bikeRepo.BikeRepository;
import com.qdigo.ebicycle.service.bike.BikeLocService;
import com.qdigo.ebicycle.service.geo.AmapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by niezhao on 2016/12/14.
 */
@Service
public class PLService {
    private final Logger logger = LoggerFactory.getLogger(PLService.class);

    @Inject
    private BikeRepository bikeRepository;
    @Inject
    private AmapService amapService;
    @Inject
    private RedisTemplate<String, String> redisTemplate;
    @Inject
    private BikeLocService bikeLocService;

    //@Async
    @Transactional
    public void updateStatus(PLPackage pl) {
        String imei = pl.getPlImei();

        // 对bike,bikestatus表进行更新
        Bike bike = bikeRepository.findByImeiId(imei).orElse(null);
        if (bike == null) {
            logger.debug("PLService:bike表未查询到imei号为{}的车辆", imei);
            return;
        }
        BikeStatus bikeStatus = bike.getBikeStatus();
        String cellid = pl.getPlCellid();
        String lac = pl.getPlLac();
        String singal = pl.getPlSingal();
        //高德 基站定位
        amapService.baseStationLocation(imei, lac, cellid, null, singal)
            .ifPresent(result -> {
                String address = result.get("desc");
                double longitude = Double.parseDouble(result.get("longitude"));
                double latitude = Double.parseDouble(result.get("latitude"));
                //将相关信息 持久化到对应mongo内
                bikeLocService.insertBikeLoc(imei, "", Const.LBSEvent.pl, latitude, longitude, bike.getAgent().getAgentId());

                bikeRepository.save(bike);
            });

    }

}



