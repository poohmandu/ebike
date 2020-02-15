/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.bike.repository;

import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.domain.entity.BikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BikeStatusRepository extends JpaRepository<BikeStatus, Serializable> {

    //JPQL语句 画方框查询车辆
    @Query(value = "select b from BikeStatus b where b.longitude between ?1 and ?2 and b.latitude between ?3 and ?4")
    List<BikeStatus> findByLocation(Double minLng, Double maxLng, Double minLat, Double maxLat);

    Optional<BikeStatus> findByBike(Bike bike);

    List<BikeStatus> findByStationId(Long stationId);

}
