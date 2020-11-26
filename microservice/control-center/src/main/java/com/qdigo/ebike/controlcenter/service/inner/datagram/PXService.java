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

import com.qdigo.ebike.controlcenter.domain.entity.mongo.PXPackage;
import com.qdigo.ebike.controlcenter.repository.mongo.PXMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by niezhao on 2017/6/2.
 */
@Service
@Slf4j
public class PXService {

    @Inject
    private PXMongoRepository pxMongoRepository;
    @Inject
    private MongoTemplate mongoTemplate;

    private static final String collectionName = "PXPackage";

    @Async
    public void insertPXAsync(PXPackage px) {
        pxMongoRepository.insert(px);
    }

    public PXPackage insertPX(PXPackage px) {
        return pxMongoRepository.insert(px);
    }

    public List<PXPackage> getFirePxAfter(String imei, long timestamp) {
        return this.getPxAfter(imei, 42, "1", timestamp);
    }

    public List<PXPackage> getPxAfter(String imei, int cmd, String param, long timestamp) {
        Query query = new Query(Criteria.where("pxImei").is(imei)
            .and("pxCmd").is(cmd)
            .and("pxParam").is(param)
            .and("timestamp").gt(timestamp));
        return mongoTemplate.find(query, PXPackage.class, collectionName);
    }

}
