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

package com.qdigo.ebike.controlcenter.service.inner;

import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * description: 
 *
 * date: 2020/3/13 9:34 AM
 * @author niezhao
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WarnService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MongoTemplate mongoTemplate;

    public void warn(boolean bln, Const.MailType mailType, String alert, String imei,) {
        val imei = bike.getImeiId();
        val key = Keys.warnOps.getKey(mailType.name(), imei);
        if (bln) {
            log.debug(alert);
            if (!redisTemplate.hasKey(key)) {
                redisTemplate.opsForValue().set(key, "1", 12, TimeUnit.HOURS);
            } else {
                redisTemplate.opsForValue().increment(key, 1);
                val count = Integer.parseInt(redisTemplate.opsForValue().get(key));
                if (count == 2) {
                    val status = bike.getBikeStatus();
                    final Map<String, Double> map = ImmutableMap.of("longitude", status.getLongitude(), "latitude", status.getLatitude());

                    pushService.pushWarn(bike, alert, map).ifPresent(pushResult ->
                            this.insertWarnRecords(bike, alert, mailType, String.valueOf(pushResult.msg_id)));
                }
            }
        } else {
            redisTemplate.delete(key);
        }
    }

    public void insertWarnRecords(BikeDto bike, String alert, Const.MailType type, String messageId) {
        val agent = bike.getAgent();
        val deviceId = bike.getDeviceId();
        val imei = bike.getImeiId();
        List<WarnRecord> warnRecords = opsUserRepository.findByAgent(agent).stream()
                .map(opsUser -> new WarnRecord()
                        .setAlert(alert)
                        .setDeviceId(deviceId)
                        .setImei(imei)
                        .setPushTarget(opsUser.getUserName())
                        .setType(type)
                        .setPushTime(new Date())
                        .setMessageId(messageId))
                .collect(Collectors.toList());
        mongoTemplate.insert(warnRecords, WarnRecord.class);
    }
}
