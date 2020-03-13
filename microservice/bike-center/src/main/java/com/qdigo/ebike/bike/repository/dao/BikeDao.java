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

package com.qdigo.ebike.bike.repository.dao;

import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.repository.BikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by niezhao on 2017/7/11.
 */
@Repository
@Slf4j
public class BikeDao {

    @Inject
    private BikeRepository bikeRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public List<Bike> findByIMSI(List<String> imsiList) {
        return bikeRepository.findAll((root, query, cb) -> root.get("gpsStatus").get("imsi").in(imsiList));
    }

    public List<Bike> findOutBike() {
        Query query = entityManager.createNativeQuery("SELECT b.* FROM bike b LEFT JOIN bike_status s ON b.bike_id =s.bike_id WHERE s.station_id IS NULL AND b.is_deleted = FALSE AND s.status = 0 ",
            Bike.class);
        return query.getResultList();
    }

    public List<Bike> findNotFoundBike() {
        String sql = "SELECT b.*,s.* FROM bike b LEFT JOIN bike_gps_status g ON b.bike_id=g.bike_id LEFT JOIN bike_status s ON b.bike_id=s.bike_id " +
            "WHERE b.is_deleted = FALSE and b.online=TRUE AND locate('pgNotFound', s.actual_status) < 1 AND g.pg_time < date_sub(now(),INTERVAL :min MINUTE)";
        Query query = entityManager.createNativeQuery(sql, Bike.class)
            .setParameter("min", 10);
        return query.getResultList();
    }

    public List<Object[]> findByAgent(long agentId) {
        Query query = entityManager.createNativeQuery("SELECT b.imei_id AS imei, b.device_id AS deviceId, " +
            "s.actual_status AS status, s.longitude AS lng, s.latitude AS lat, s.battery AS battery, s.address AS address " +
            "FROM agent a, bike b, bike_status s WHERE b.agent_id = a.agent_id AND b.bike_id = s.bike_id " +
            "AND b.is_deleted = FALSE AND a.agent_id = :agentId")
            .setParameter("agentId", agentId);
        return (List<Object[]>) query.getResultList();
    }

}
