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

package com.qdigo.ebike.stationcenter.repository.dao;

import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.stationcenter.domain.entity.BikeStation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by niezhao on 2017/10/20.
 */
@Repository
@Slf4j
public class StationDao {

    @PersistenceContext
    private EntityManager entityManager;

    //  r*arccos[cos(y1)*cos(y2)*cos(x1-x2)+sin(y1)*sin(y2)]
    public List<BikeStation> getNearStations(double lng, double lat, double km, int limit) {
        //0.01 => 1000米
        // 1度 <==> 111公里
        GeoUtil.Around around = GeoUtil.getAround(lng, lat, km * 1000);
        Query query = entityManager.createNativeQuery("SELECT s.* FROM bike_station s " +
            "WHERE latitude BETWEEN :minLat AND :maxLat " +
            "AND longitude BETWEEN :minLng AND :maxLng " +
            "ORDER BY ACOS(SIN((:lat * :pi) / 180) * SIN((latitude * :pi) / 180) + " +
            "COS((:lat * :pi) / 180) * COS((latitude * :pi) / 180) * " +
            "COS((:lng * :pi) / 180 - (longitude * :pi) / 180)) * 6378.137 ASC LIMIT :lim", BikeStation.class)
            .setParameter("lat", lat)
            .setParameter("lng", lng)
            .setParameter("pi", Math.PI)
            .setParameter("lim", limit)
            .setParameter("minLat", around.minY)
            .setParameter("maxLat", around.maxY)
            .setParameter("minLng", around.minX)
            .setParameter("maxLng", around.maxX);
        return query.getResultList();
    }


}
