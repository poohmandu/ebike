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

import com.qdigo.ebike.bike.domain.entity.BikeStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Slf4j
@Repository
public class BikeStatusDao {

    @PersistenceContext
    private EntityManager entityManager;

    //@CatAnnotation
    public BikeStatus findByImei(String imei) {
        String sql = "select s.* from bike_status s left join bike b on s.bike_id = b.bike_id where b.imei_id=:imei limit 1";
        Query query = entityManager.createNativeQuery(sql, BikeStatus.class)
            .setParameter("imei", imei);
        List<BikeStatus> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }
}
