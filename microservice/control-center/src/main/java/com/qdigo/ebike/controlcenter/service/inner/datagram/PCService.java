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

import com.qdigo.ebicycle.aop.cat.CatAnnotation;
import com.qdigo.ebicycle.constants.BikeCfg;
import com.qdigo.ebicycle.constants.Keys;
import com.qdigo.ebicycle.domain.bike.Bike;
import com.qdigo.ebicycle.domain.bike.BikeGpsStatus;
import com.qdigo.ebicycle.domain.mongo.device.PCPackage;
import com.qdigo.ebicycle.repository.bikeRepo.BikeGpsStatusRepository;
import com.qdigo.ebicycle.repository.bikeRepo.BikeRepository;
import com.qdigo.ebicycle.service.bike.BikeService;
import com.qdigo.ebicycle.service.cammand.DeviceService;
import com.qdigo.ebicycle.service.util.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by niezhao on 2016/12/26.
 */
@Slf4j
@Service
public class PCService {

    @Inject
    private BikeGpsStatusRepository gpsStatusRepository;
    @Inject
    private BikeRepository bikeRepository;
    @Inject
    private BikeService bikeService;
    @Inject
    private DeviceService deviceService;
    @Inject
    private RedisTemplate<String, String> redisTemplate;

    @Transactional
    //@CatAnnotation
    public void bikeCommandService(PCPackage pc) {
        String imei = pc.getPcImei();
        //保存最新pcTime;
        BikeGpsStatus gpsStatus = gpsStatusRepository.findByImei(imei).orElseThrow(() -> new NullPointerException("GPS表没有" + imei + "车辆"));
        gpsStatus.setPcTime(FormatUtil.getCurTime());
        gpsStatusRepository.save(gpsStatus);

        int cmd = pc.getPcCmd();
        String param = pc.getPcParam();

        if (cmd < 64) {
            switch (cmd) {
                case 3:
                    //点火 熄火
                    break;
                case 7:
                    //PTIME HTIME
                    break;
                case 8:
                    //硬重启
                    break;
                case 12:
                    // 设置灵敏度
                    break;
                case 24:
                    // 撤防布防
                    break;
                case 27:
                    // 蓝牙
                    break;
                case 30:
                    //请求上报心跳
                    break;
                case 33:
                    break;
                case 41:
                    break;
                case 42:
                    //上电断电
                    break;
            }
        } else {
            switch (cmd) {
                case 64:
                    // 1、休眠 2、电池没电 3、短信上电 4、短信断电
                    switch (param) {
                        case "1":
                            break;
                        case "2":
                            break;
                        case "3": //毛毛莫工确认
                            this.onSmsOn(imei);
                            break;
                        case "4": //经测试,短信断电确认回传
                            break;
                    }
                    break;
                case 65:
                    //20191202
                    //Bit0:1=断电报警，Bit1:1=非法打开电门锁，
                    //Bit2:1=轮动报警，Bit3:1=震动，Bit4:1=低电压报警
                    //a:1010 e:1110 0:0000 6:0110 8:1000
                    switch (param) {
                        case "1":
                            //"断电报警";
                            break;
                        case "2":
                            //"非法打开电门锁";
                            break;
                        case "3":
                            //"轮动报警";
                            break;
                        case "4":
                            //"震动";
                            break;
                        case "5":
                            //"低电压报警";
                            break;
                    }
                    break;
                case 66:
                    break;
                case 68:
                    //充电
                    break;
                case 69:
                    //关机
                    break;
            }
        }


    }

    public void onSmsOn(String imei) {
        Bike bike = bikeRepository.findByImeiId(imei).orElseGet(null);
        if (bike == null) {
            return;
        }
        if (bike.isDeleted()) {
            return;
        }
        if (bikeService.inUse(bike)) {
            return;
        }
        if (bikeService.opsUserInUse(imei)) {
            return;
        }
        log.debug("{}在无人借车情况下短信延迟上电", imei);
        deviceService.lock(imei, "system_PCCheck");
        String key = Keys.flagOps.getKey(imei);
        redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());
    }


}


