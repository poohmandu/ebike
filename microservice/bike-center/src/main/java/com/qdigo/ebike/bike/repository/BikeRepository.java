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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BikeRepository extends JpaRepository<Bike, Long>, JpaSpecificationExecutor<Bike> {

    Optional<Bike> findTopByDeviceId(Serializable deviceId);

    Optional<Bike> findTopByImeiId(Serializable imeiId);

    Optional<Bike> findByImeiId(String imeiId);

    Optional<Bike> findByDeviceId(String deviceId);

    List<Bike> findByDeviceIdLike(String deviceId);

}
