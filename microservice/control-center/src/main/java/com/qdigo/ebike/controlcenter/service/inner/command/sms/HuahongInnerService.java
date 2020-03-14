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
import com.qdigo.ebike.api.service.third.devicesms.HuahongService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.SMSPackage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static com.qdigo.ebike.controlcenter.service.inner.command.sms.IDevSMSService.huahong;

/**
 * 服务商:华虹
 */
@Slf4j
@Service(huahong)
public class HuahongInnerService implements IDevSMSService {

    @Resource
    private HuahongService huahongService;
    @Resource
    private MongoTemplate mongoTemplate;


    @Override
    public boolean smsOpen(String imei, String mobileNo, SimDto sim) {
        val content = "#TURN;ON";//返回  #TURN;1
        return sendSMS(imei, content, mobileNo, sim.getSimNO());
    }

    @Override
    public boolean smsClose(String imei, String mobileNo, SimDto sim) {
        val content = "#TURN;OFF"; //返回   #TURN;0
        return sendSMS(imei, content, mobileNo, sim.getSimNO());
    }

    @Override
    public boolean smsSetImei(String imei, String newImei, String mobileNo, SimDto sim) {
        val content = "#IMEI;" + newImei;
        return sendSMS(imei, content, mobileNo, sim.getSimNO());
    }

    @Override
    public boolean smsSetHost(String imei, String domain, int port, String mobileNo, SimDto sim) {
        val content = "#NEWLINK;CMIOT;TCP;" + domain + ";" + port;
        return sendSMS(imei, content, mobileNo, sim.getSimNO());
    }

    @Override
    public Optional<Location> smsLoc(String imei, String mobileNo, SimDto sim) {
        try {
            val content = "#LOCA;"; //返回  ps: loc=31606490,120464310
            val sendSMS = sendSMS(imei, content, mobileNo, sim.getSimNO());
            if (!sendSMS) {
                return Optional.empty();
            } else {
                Optional<SMSPackage> beanOptional = this.waitForResult(imei, mobileNo, sim, smsPackage -> smsPackage.getContent().startsWith("loc="), false);
                if (beanOptional.isPresent()) {
                    val str = beanOptional.get().getContent();
                    double lat = Double.parseDouble(StringUtils.substringBetween(str, "=", ","));
                    double lng = Double.parseDouble(StringUtils.substringAfterLast(str, ","));
                    //long timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(beanOptional.get().getTime()).getTime();
                    final Location location = new Location().setPgLatitude(lat).setPgLongitude(lng);
                    return Optional.of(location);
                } else {
                    return Optional.empty();
                }
            }
        } catch (Exception e) {
            log.error("短信获取位置异常:" + e.getMessage());
            return Optional.empty();
        }
    }

    //异步接受短信
    @Async
    @Override
    public Future<Boolean> receiveSMSAsync(String imei, String mobileNo, SimDto sim, String reply) {
        return this.receiveSMSAsync(imei, mobileNo, sim, reply, false);
    }

    @Async
    @Override
    public Future<Boolean> receiveSMSAsync(String imei, String mobileNo, SimDto sim, String reply, boolean fast) {
        val optional = this.waitForResult(imei, mobileNo, sim, smsPackage -> smsPackage.getContent().equals(reply), fast);
        return new AsyncResult<>(optional.isPresent());
    }

    private Optional<SMSPackage> waitForResult(String imei, String mobileNo, SimDto simDto, Predicate<SMSPackage> predicate, boolean fast) {
        try {
            val count = 4;
            int beforeWait = fast ? 3000 : 6000; //统计而来
            double mills = fast ? 3000 : 6000;
            long start = System.currentTimeMillis() - 2000;
            TimeUnit.MILLISECONDS.sleep(beforeWait);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            for (int i = 0; i < count; i++) {
                val result = this.receiveSMS(imei, mobileNo, simDto);
                if (result.getStatus() == 0) {
                    log.debug("短信第{}次成功获取:{}", i, result);
                    Optional<SMSPackage> optional = this.findSmsList(start, imei).stream()
                            .filter(predicate)
                            .findAny();
                    if (optional.isPresent()) {
                        return optional;
                    } else {
                        mills /= (i + 1);
                        TimeUnit.MILLISECONDS.sleep((long) mills);
                    }
                } else if (result.getStatus() == 2) {
                    log.debug("短信第{}次没有获取:{}", i, result);
                    mills /= (i + 1);
                    TimeUnit.MILLISECONDS.sleep((long) mills);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("短信获取异常:" + e.getMessage());
        }
        return Optional.empty();
    }

    private HuahongService.Result receiveSMS(String imei, String mobileNo, SimDto sim) {

        val simNO = sim.getSimNO();
        val result = huahongService.receive(String.valueOf(simNO));
        if (result.getStatus() == 0) {
            //成功收到信息
            val resultBeans = result.getResult();
            if (resultBeans != null) {
                resultBeans.forEach(resultBean -> {
                    long timestamp = resultBean.getAddTime();
                    SMSPackage smsPackage = new SMSPackage().setTimestamp(timestamp).setSimNo(Long.parseLong(resultBean.getCardID()))
                            .setMobileNo(mobileNo).setImei(imei).setContent(resultBean.getContent()).setDirection(Const.direction.in)
                            .setTransactionalId(resultBean.getTransactionId());
                    //这里list的size很小就不批量插入了
                    mongoTemplate.insert(smsPackage, this.getCollectionName());
                });
            }
        }
        return result;
    }

    private boolean sendSMS(String imei, String content, String mobileNo, long simNO) {
        String transactionalId = huahongService.send(String.valueOf(simNO), content);

        //记录
        val smsPackage = new SMSPackage().setContent(content).setImei(imei).setMobileNo(mobileNo)
                .setSimNo(simNO).setTimestamp(System.currentTimeMillis()).setDirection(Const.direction.out)
                .setTransactionalId(transactionalId);
        mongoTemplate.insert(smsPackage, this.getCollectionName());
        return transactionalId != null;
    }


    private String getCollectionName() {
        return "SMSPackage";
    }

    private List<SMSPackage> findSmsList(long start, String imei) {
        Query query = new Query(Criteria.where("imei").is(imei)
                .and("direction").is(Const.direction.in)
                .and("timestamp").gte(start).lte(System.currentTimeMillis()));
        return mongoTemplate.find(query, SMSPackage.class, this.getCollectionName());
    }

}

