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

package com.qdigo.ebike.agentcenter.repository;

import com.qdigo.ebike.agentcenter.domain.entity.opsuser.OpsUseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Created by niezhao on 2017/11/17.
 */
public interface OpsUseRecordRepository extends JpaRepository<OpsUseRecord, Long> {

    @Query(value = "SELECT r.* FROM ops_use_record r WHERE r.ops_user=?1 AND r.use_status='inUse' limit 1", nativeQuery = true)
    Optional<OpsUseRecord> findByUsingUser(String userName);

    @Query(value = "SELECT r.* FROM ops_use_record r WHERE r.imei=?1 AND r.use_status='inUse' limit 1", nativeQuery = true)
    Optional<OpsUseRecord> findByUsingBike(String imei);

}
