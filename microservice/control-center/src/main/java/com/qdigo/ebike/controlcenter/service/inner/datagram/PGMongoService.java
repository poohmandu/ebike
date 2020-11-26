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

import com.alibaba.fastjson.JSON;
import com.mongodb.DBCollection;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PGPackage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2017/11/7.
 */
@Service
@Slf4j
public class PGMongoService {

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public String getCollectionName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "PG" + ZonedDateTime.now().format(formatter);
    }

    public List<PGPackage> findPGList(String imei, int limit) {
        long end = System.currentTimeMillis();
        long start = end - TimeUnit.MINUTES.toMillis(8);
        Query query = new Query(Criteria.where("pgImei").is(imei)
                .and("timestamp").gte(start).lte(end));
        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        query.limit(limit);
        return mongoTemplate.find(query, PGPackage.class, this.getCollectionName());
    }

    //@CatAnnotation
    public PGPackage getLast(String imei) {
        String key = Keys.up_pg_latest.getKey(FormatUtil.getCurDate(), imei);
        String last = redisTemplate.opsForValue().get(key);
        if (last == null) {
            return this.findLatestPG(imei);
        } else {
            return JSON.parseObject(last, PGPackage.class);
        }
    }

    public PGPackage findLatestPG(String imei) {
        Query query = new Query(Criteria.where("pgImei").is(imei));
        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        query.limit(1);
        Date now = new Date();

        for (int i = 0; i < Const.dataCleanDays; i++) {
            String collectionName = "PG" + FormatUtil.yMd.format(DateUtils.addDays(now, -i));
            PGPackage one = mongoTemplate.findOne(query, PGPackage.class, collectionName);
            if (one != null) {
                return one;
            }
        }
        log.warn("{}设备失去联系超过{}天", imei, Const.dataCleanDays);
        return null;
    }

    //@CatAnnotation
    public void insertPG(PGPackage pg, PGPackage old) {
        DBCollection collection;
        String collectionName = this.getCollectionName();
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
            IndexOperations indexOps = mongoTemplate.indexOps(collectionName);
            Index index = new Index().on("pgImei", Sort.Direction.ASC)
                    .on("timestamp", Sort.Direction.DESC);
            indexOps.ensureIndex(index);
        }
        if (old != null) {
            long timestamp = old.getTimestamp();
            long now = pg.getTimestamp();
            int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(now - timestamp);
            pg.setSeconds(seconds);
            if (old.getPgLatitude() != 0.0 && old.getPgLongitude() != 0.0 && pg.getPgLatitude() != 0.0 && pg.getPgLongitude() != 0.0) {
                double meter = GeoUtil.getDistanceForMeter(pg.getPgLatitude(), pg.getPgLongitude(), old.getPgLatitude(), old.getPgLongitude());
                pg.setDistance((int) meter);
            } else {
                int distance = (int) ((pg.getPgSpeed() + old.getPgSpeed()) / 2 / 3.6 * seconds);
                pg.setDistance(distance);
            }
        } else {
            int seconds;
            int distance;
            if (pg.getPgDoorLock() == 1) {
                seconds = 15;
                distance = (int) (pg.getPgSpeed() / 3.6 * seconds);
            } else {
                seconds = 60;
                distance = 0;
            }
            pg.setSeconds(seconds);
            pg.setDistance(distance);
        }
        mongoTemplate.insert(pg, collectionName);
    }

    public boolean sleep(String imei) {
        Query query = new Query(Criteria.where("pgImei").is(imei)
                .and("pgWheelInput").is(1))
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(10);
        List<PGPackage> list = mongoTemplate.find(query, PGPackage.class, this.getCollectionName());
        return list.stream().allMatch(pgPackage -> pgPackage.getPgStar() == 0 && pgPackage.getPgHight() == 0);
    }

    public boolean sleep(PGPackage pg, PGPackage old) {
        val now = pg.getPgStar() == 0 && pg.getPgHight() == 0;
        val history = old.getPgStar() == 0 && pg.getPgHight() == 0;
        return now && history;
    }

    public boolean locationFail(PGPackage pg, PGPackage old) {
        val now = pg.getPgLatitude() == 0.0 && pg.getPgLongitude() == 0.0;
        val history = old.getPgLatitude() == 0.0 && old.getPgLongitude() == 0.0;
        return now && history;
    }

    public boolean canMove(PGPackage pg) {
        return pg.getPgLocked() == 0 || pg.getPgDoorLock() == 1 || pg.getPgShaked() == 1 || pg.getPgWheelInput() == 1;
    }

    public PGPackage pgNotFound(String imei) {
        val key = Keys.up_pg_latest.getKey(FormatUtil.getCurDate(), imei);
        String s = redisTemplate.opsForValue().get(key);
        if (s == null) {
            return null;
        }
        return JSON.parseObject(s, PGPackage.class);
    }

}
