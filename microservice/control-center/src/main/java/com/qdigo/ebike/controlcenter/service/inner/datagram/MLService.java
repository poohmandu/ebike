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

import org.springframework.stereotype.Service;

/**
 * Created by niezhao on 2016/12/12.
 */
@Service
public class MLService {
    //
    //private final Logger logger = LoggerFactory.getLogger(MLService.class);
    //
    //@Inject
    //private MLSqlRepository mlSqlRepository;
    //@Inject
    //private ChargerRepository chargerRepository;
    //@Inject
    //private AmapService amapService;
    //
    //@Async
    //public void updateTable(MLPackage ml) {
    //
    //    String imei = ml.getMlImei();
    //
    //    val charger = chargerRepository.findByChargerImei(imei);
    //    if (!charger.isPresent()) {
    //        logger.warn("MLService:charger表未查询到imei号为{}的充电桩", imei);
    //        return;
    //    }
    //
    //
    //    String cellid = ml.getMlCellid();
    //    String imsi = String.valueOf(ml.getMlImsi());
    //    String lac = ml.getMlLAC();
    //    String singal = ml.getMlSingal();
    //
    //    //高德 基站定位
    //    amapService.baseStationLocation(imei, lac, cellid, imsi, singal)
    //        .ifPresent(result -> {
    //            String address = result.get("desc");
    //            double longitude = Double.parseDouble(result.get("longitude"));
    //            double latitude = Double.parseDouble(result.get("latitude"));
    //
    //            //将相关信息 持久化到对应表内
    //            charger.get().setAddress(address);
    //            charger.get().setLongitude(longitude);
    //            charger.get().setLatitude(latitude);
    //            chargerRepository.save(charger.get());
    //
    //        });
    //
    //
    //}

}

