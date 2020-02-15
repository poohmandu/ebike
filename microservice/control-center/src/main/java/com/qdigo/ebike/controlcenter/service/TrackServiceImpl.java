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

package com.qdigo.ebike.controlcenter.service;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.control.Location;
import com.qdigo.ebike.api.service.control.TrackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Description: 
 * date: 2020/1/15 12:29 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TrackServiceImpl implements TrackService {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Location> getTrackByPeriod(String imei, Date start, Date end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        String collectionName = "PG" + sdf.format(end);
        Query query = new Query(Criteria.where("pgImei").is(imei)
                .and("timestamp").gte(start.getTime()).lte(end.getTime())
                .and("pgLongitude").ne(0.0)
                .and("pgLatitude").ne(0.0));
        log.debug("查询的BSON语句:" + query.toString());

        return mongoTemplate.find(query, Location.class, collectionName);
    }

    @Override
    public List<Location> getMoveTrackByTime(String imei, Date start, Date end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String collectionName = "PG" + sdf.format(end);
        Query query = new Query(Criteria.where("pgImei").is(imei)
                .and("timestamp").gte(start.getTime()).lte(end.getTime())
                .and("pgLongitude").ne(0.0)
                .and("pgLatitude").ne(0.0)
                .and("pgWheelInput").is(1));

        log.debug("查询的BSON语句:" + query.toString());

        return mongoTemplate.find(query, Location.class, collectionName);
    }

    @Override
    public Location getLocationByTime(String imei, long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String collectionName = "PG" + sdf.format(new Date(timestamp));
        Query query = new Query(Criteria.where("pgImei").is(imei).and("timestamp").is(timestamp));
        log.debug("查询的BSON语句:" + query);
        return mongoTemplate.findOne(query, Location.class, collectionName);
    }
}
