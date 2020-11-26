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
import com.qdigo.ebike.api.service.third.devicesms.DahanService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.SMSPackage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.Future;

import static com.qdigo.ebike.controlcenter.service.inner.command.sms.IDevSMSService.dahan;

@Slf4j
@Service(dahan)
public class DahanInnerService implements IDevSMSService {

    private final static String APP_ID = "Qidi";
    private final static String APP_KEY = "2bf0f0f1a54847d4bd4ff1cec17e41a5";

    private final static String BASE_URL = "http://118.31.48.5:18089";
    private final static String SEND_SMS = "/api/v1/card/operate/sms/send";

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private DahanService dahanService;


    @Override
    public boolean smsOpen(String imei, String mobileNo, SimDto sim) {
        return sendSMS(imei, mobileNo, sim.getSimNO(), "#TURN;ON");
    }

    @Override
    public boolean smsClose(String imei, String mobileNo, SimDto sim) {
        return sendSMS(imei, mobileNo, sim.getSimNO(), "#TURN;OFF");
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

    @Async
    @Override
    public Future<Boolean> receiveSMSAsync(String imei, String mobileNo, SimDto sim, String reply) {
        return receiveSMSAsync(imei, mobileNo, sim, reply, false);
    }

    @Async
    @Override
    public Future<Boolean> receiveSMSAsync(String imei, String mobileNo, SimDto sim, String reply, boolean fast) {
        return null;
    }

    private boolean sendSMS(String imei, String mobileNo, long simNo, String content) {
        val isOk = dahanService.httpSend(simNo, content);

        //记录
        val smsPackage = new SMSPackage().setContent(content).setImei(imei).setMobileNo(mobileNo)
                .setSimNo(simNo).setTimestamp(System.currentTimeMillis()).setDirection(Const.direction.out)
                .setTransactionalId("dahan");
        mongoTemplate.insert(smsPackage, this.getCollectionName());
        return isOk;
    }

    private String getCollectionName() {
        return "SMSPackage";
    }

}
