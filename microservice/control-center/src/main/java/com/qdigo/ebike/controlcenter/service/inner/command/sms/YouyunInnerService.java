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

package com.qdigo.ebike.controlcenter.service.inner.command.sms;

import com.qdigo.ebike.api.domain.dto.bike.SimDto;
import com.qdigo.ebike.api.domain.dto.control.Location;
import com.qdigo.ebike.api.service.third.devicesms.YouyunService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.SMSPackage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.Future;

import static com.qdigo.ebike.controlcenter.service.inner.command.sms.IDevSMSService.youyun;

/**
 * 服务商:佑云
 */
@Slf4j
@Service(youyun)
public class YouyunInnerService implements IDevSMSService {

    private final static String ACCESS_TOKEN = "54466be70fcdf5293a37429bfcaf5d670f14d3a7";
    private final static String TEMP_ON = "0013";
    private final static String TEMP_OFF = "0014";
    private final static String TEMP_IMEI = "0015";
    private final static String TEMP_LOC = "0016";
    private final static String BASE_URL = "https://console.ucloudy.cn/doc/api/sms";

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private YouyunService youyunService;

    @Override
    public boolean smsOpen(String imei, String mobileNo, SimDto sim) {
        return sendSMS(imei, mobileNo, sim.getSimNO(), TEMP_ON, null);
    }

    @Override
    public boolean smsClose(String imei, String mobileNo, SimDto sim) {
        return sendSMS(imei, mobileNo, sim.getSimNO(), TEMP_OFF, null);
    }

    @Override
    public boolean smsSetImei(String imei, String newImei, String mobileNo, SimDto sim) {
        return false;
    }

    @Override
    public boolean smsSetHost(String imei, String domain, int port, String mobileNo, SimDto sim) {
        return false;
    }

    @Override
    public Optional<Location> smsLoc(String imei, String mobileNo, SimDto sim) {
        return Optional.empty();
    }

    @Override
    public Future<Boolean> receiveSMSAsync(String imei, String mobileNo, SimDto sim, String reply) {
        return receiveSMSAsync(imei, mobileNo, sim, reply, false);
    }

    @Override
    public Future<Boolean> receiveSMSAsync(String imei, String mobileNo, SimDto sim, String reply, boolean fast) {
        return new AsyncResult<>(false);
    }

    private boolean sendSMS(String imei, String mobileNo, long simNo, String temp, String params) {
        val isOk = youyunService.httpSend(simNo, temp, params);

        //记录
        val smsPackage = new SMSPackage().setContent(temp).setImei(imei).setMobileNo(mobileNo)
                .setSimNo(simNo).setTimestamp(System.currentTimeMillis()).setDirection(Const.direction.out)
                .setTransactionalId("youyun");
        mongoTemplate.insert(smsPackage, this.getCollectionName());
        return isOk;
    }

    private String getCollectionName() {
        return "SMSPackage";
    }


}
