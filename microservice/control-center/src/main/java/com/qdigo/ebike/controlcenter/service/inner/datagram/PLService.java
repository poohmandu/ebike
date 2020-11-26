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

import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.service.bike.BikeLocService;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.api.service.third.address.AmapService;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PLPackage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by niezhao on 2016/12/14.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PLService {

    private final BikeStatusService bikeStatusService;
    private final AmapService amapService;
    private final BikeLocService bikeLocService;
    private final BikeService bikeService;

    //@Async
    @Transactional
    public void updateStatus(PLPackage pl) {
        String imei = pl.getPlImei();

        // 对bike,bikestatus表进行更新

        BikeStatusDto bikeStatus = bikeStatusService.findByImei(imei);
        String cellid = pl.getPlCellid();
        String lac = pl.getPlLac();
        String singal = pl.getPlSingal();
        //高德 基站定位
        Map<String, String> result = amapService.baseStationLocation(imei, lac, cellid, null, singal);
        if (result != null) {
            String address = result.get("desc");
            double longitude = Double.parseDouble(result.get("longitude"));
            double latitude = Double.parseDouble(result.get("latitude"));
            //将相关信息 持久化到对应mongo内
            BikeDto bikeDto = bikeService.findByImei(imei);
            bikeLocService.insertBikeLoc(imei, "", BikeLocService.LBSEvent.pl, latitude, longitude, bikeDto.getAgentId());

            bikeStatusService.update(bikeStatus);
        }

    }

}



