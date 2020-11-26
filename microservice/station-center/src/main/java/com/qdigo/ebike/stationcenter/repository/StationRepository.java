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

package com.qdigo.ebike.stationcenter.repository;

import com.qdigo.ebike.stationcenter.domain.entity.BikeStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface StationRepository extends JpaRepository<BikeStation, Long>, JpaSpecificationExecutor<BikeStation> {

    //JPQL语句 画方框查询车辆
    //@Query(value = "select s from BikeStation s where s.longitude between ?1 and ?2 and  s.latitude between ?3 and ?4")
    //List<BikeStation> findByLocation(Double minLng, Double maxLng, Double minLat, Double maxLat);

    Optional<BikeStation> findByStationFenceGid(String gid);

}
