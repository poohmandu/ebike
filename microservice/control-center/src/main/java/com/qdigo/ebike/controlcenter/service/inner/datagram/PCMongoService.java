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


import com.qdigo.ebike.controlcenter.domain.entity.mongo.PCPackage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by niezhao on 2019/08/06.
 */
@Slf4j
@Service
public class PCMongoService {

    @Resource
    private MongoTemplate mongoTemplate;

    private final static String collectionName = "PCPackage";

    public PCPackage findLockPC(String imei, long start) {
        return this.findPCGte(imei, 24, "1", start);
    }

    public PCPackage findPCGte(String imei, int cmd, String param, long start) {
        Query query = new Query(Criteria.where("pcImei").is(imei)
                .and("timestamp").gte(start).lte(System.currentTimeMillis())
                .and("pcCmd").is(cmd)
                .and("pcParam").is(param));
        
        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        query.limit(1);
        return mongoTemplate.findOne(query, PCPackage.class, collectionName);
    }

}
