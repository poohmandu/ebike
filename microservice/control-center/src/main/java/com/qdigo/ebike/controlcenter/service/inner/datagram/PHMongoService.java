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

import com.qdigo.ebike.controlcenter.domain.entity.mongo.PHPackage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by niezhao on 2017/11/23.
 */
@Service
@Slf4j
public class PHMongoService {

    @Inject
    private MongoTemplate mongoTemplate;

    public String getCollectionName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "PH" + ZonedDateTime.now().format(formatter);
    }

    public PHPackage findLatestPH(String imei) {
        Query query = new Query(Criteria.where("phImei").is(imei));
        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        query.limit(1);
        return mongoTemplate.findOne(query, PHPackage.class, this.getCollectionName());
    }

    public PHPackage findLatestPH(String imei, long start) {
        Query query = new Query(Criteria.where("phImei").is(imei)
                .and("timestamp").gte(start).lte(System.currentTimeMillis()));
        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        query.limit(1);
        return mongoTemplate.findOne(query, PHPackage.class, this.getCollectionName());
    }

    public List<PHPackage> findPHList(String imei, int limit) {
        long end = System.currentTimeMillis();
        long start = end - TimeUnit.MINUTES.toMillis(30);
        Query query = new Query(Criteria.where("phImei").is(imei)
                .and("timestamp").gte(start).lte(end));
        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        query.limit(5);
        return mongoTemplate.find(query, PHPackage.class, this.getCollectionName());
    }

    public void insertPH(PHPackage ph) {
        String collectionName = this.getCollectionName();
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
            IndexOperations indexOps = mongoTemplate.indexOps(collectionName);
            Index index = new Index().on("phImei", Sort.Direction.ASC)
                    .on("timestamp", Sort.Direction.DESC);
            indexOps.ensureIndex(index);
        }
        mongoTemplate.insert(ph, collectionName);
    }

    //TODO: 是否只计算轮动时
    public int getAvgPowerVoltage(PHPackage ph, List<PHPackage> list) {
        List<PHPackage> collect = list.stream().filter(phPackage -> ph.getPhElectric() == 1)
                .collect(Collectors.toList());
        if (collect.isEmpty() && ph.getPhElectric() == 0) {
            return 0;
        }
        int sum = 0;
        for (PHPackage phPackage : collect) {
            sum += phPackage.getPhPowerVoltage();
        }
        if (ph.getPhElectric() == 0) {
            return sum / collect.size();
        }
        sum += ph.getPhPowerVoltage();
        return sum / (collect.size() + 1);
    }

}
