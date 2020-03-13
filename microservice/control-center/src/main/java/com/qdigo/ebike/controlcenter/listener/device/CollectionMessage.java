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

package com.qdigo.ebike.controlcenter.listener.device;

import com.alibaba.fastjson.JSON;
import com.qdigo.ebicycle.constants.ConfigConstants;
import com.qdigo.ebicycle.constants.Const;
import com.qdigo.ebicycle.constants.Keys;
import com.qdigo.ebicycle.constants.MQ;
import com.qdigo.ebicycle.domain.mongo.device.PGPackage;
import com.qdigo.ebicycle.service.util.FormatUtil;
import com.qdigo.ebicycle.service.util.ThreadPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2018/1/2.
 */
@Component
@Slf4j
@ConditionalOnExpression("'${my.env}'=='prod' and ${server.port}==${my.mq-port}")
public class CollectionMessage {

    @Inject
    private RedisTemplate<String, String> redisTemplate;

    @Data
    private static class Collection {
        private String imei;
        private boolean collected;
        private long timestamp;
    }

    @RabbitListener(queues = {MQ.Direct.device_connect})
    public void onCollectionMessage(Collection collection) {
        String imei = ConfigConstants.imei.getConstant() + collection.getImei();
        collection.setImei(imei);
        log.debug("GSM数据连接断开:{}", collection);
        String key = Keys.up_pg_latest.getKey(FormatUtil.getCurDate(), imei);
        if (!redisTemplate.hasKey(key)) {
            return;
        }
        long waitSeconds = 60;

        // 最新的key是否在过去的waitSeconds的时间里  -- 过去
        PGPackage pg = JSON.parseObject(redisTemplate.opsForValue().get(key), PGPackage.class);
        if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - pg.getTimestamp()) > waitSeconds) {
            log.error("{}的imei车辆GSM连接断开1", imei);
            redisTemplate.delete(key);
            return;
        }

        ThreadPool.cachedThreadPool().submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(waitSeconds);
            } catch (InterruptedException e) {
                log.error("连接监听异常", e);
            }
            // 这waitSeconds的时间里key是否有更新 -- 未来
            if (Const.pgNotFoundSeconds - redisTemplate.getExpire(key, TimeUnit.SECONDS) > waitSeconds) {
                log.error("{}的imei车辆GSM连接断开2", imei);
                redisTemplate.delete(key);
            }
        });

    }
}
