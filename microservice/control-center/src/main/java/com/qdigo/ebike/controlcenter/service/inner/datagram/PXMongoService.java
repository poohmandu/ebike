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
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PXPackage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class PXMongoService {

    @Resource
    private MongoTemplate mongoTemplate;

    private final static String collectionName = "PXPackage";

    public PXPackage findLockPC(String imei, long start) {
        return null;
    }

    public PCPackage findPCGte(String imei, int cmd, String param, long start) {
        return null;
    }

}
