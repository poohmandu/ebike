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

package com.qdigo.ebike.controlcenter.service.inner.command;

import com.qdigo.ebike.api.domain.dto.bike.SimDto;
import com.qdigo.ebike.api.domain.dto.control.Location;
import com.qdigo.ebike.api.service.bike.sms.SimCardService;
import com.qdigo.ebike.controlcenter.domain.entity.device.PHSqlPackage;
import com.qdigo.ebike.controlcenter.repository.PHSqlRepository;
import com.qdigo.ebike.controlcenter.service.inner.command.sms.IDevSMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * description: 
 *
 * date: 2020/3/13 9:02 PM
 * @author niezhao
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DevSMSService {

    private final SimCardService simCardService;
    private final PHSqlRepository phSqlRepository;
    private final Map<String, IDevSMSService> strategys;


    private SimDto getSimInfo(String imei) {
        PHSqlPackage phSqlPackage = phSqlRepository.findByPhImei(imei).orElse(null);
        Long imsi = phSqlPackage.getPhImsi();
        if (imsi == null || imsi == 0L) {
            log.debug(imei + "发送短信时imsi为null");
            return null;
        }
        SimDto sim = simCardService.findByImsi(imsi);
        if (sim == null) {
            log.debug("sim表里没有imsi为" + imsi + "的卡");
            return null;
        }
        return sim;
    }

    private IDevSMSService getSmsService(String type) {
        if ("huahong".equals(type)) {
            return strategys.get(IDevSMSService.huahong);
        } else if ("youyun".equals(type)) {
            return strategys.get(IDevSMSService.youyun);
        } else if ("dahan".equals(type)) {
            return strategys.get(IDevSMSService.dahan);
        } else {
            return null;
        }
    }

    public boolean smsOpen(String imei, String mobileNo) {
        SimDto simInfo = getSimInfo(imei);
        IDevSMSService smsService;
        if (simInfo != null && (smsService = getSmsService(simInfo.getSimType())) != null) {
            return smsService.smsOpen(imei, mobileNo, simInfo);
        } else {
            log.error("{}设备短信上电时sim卡的服务商未找到", imei);
            return false;
        }
    }

    //@CatAnnotation
    public boolean smsClose(String imei, String mobileNo) {
        SimDto simInfo = getSimInfo(imei);
        IDevSMSService smsService;
        if (simInfo != null && (smsService = getSmsService(simInfo.getSimType())) != null) {
            return smsService.smsOpen(imei, mobileNo, simInfo);
        } else {
            log.error("{}设备短信断电时sim卡的服务商未找到", imei);
            return false;
        }
    }

    public Optional<Location> smsLoc(String imei, String mobileNo) {
        SimDto simInfo = getSimInfo(imei);
        IDevSMSService smsService;
        if (simInfo != null && (smsService = getSmsService(simInfo.getSimType())) != null) {
            return smsService.smsLoc(imei, mobileNo, simInfo);
        } else {
            log.error("{}设备短信获取位置时sim卡的服务商未找到", imei);
            return Optional.empty();
        }
    }

    public Future<Boolean> receiveSMSAsync(String imei, String mobileNo, String reply) {
        return receiveSMSAsync(imei, mobileNo, reply, false);
    }

    public Future<Boolean> receiveSMSAsync(String imei, String mobileNo, String reply, boolean fast) {
        SimDto simInfo = getSimInfo(imei);
        IDevSMSService smsService;
        if (simInfo != null && (smsService = getSmsService(simInfo.getSimType())) != null) {
            return smsService.receiveSMSAsync(imei, mobileNo, simInfo, reply, fast);
        } else {
            log.error("{}设备短信等待回复时sim卡的服务商未找到", imei);
            return new AsyncResult<>(false);
        }
    }


}
