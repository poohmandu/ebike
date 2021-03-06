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

package com.qdigo.ebike.usercenter.repository;


import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.domain.entity.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by niezhao on 2016/11/28.
 */
public interface UserRecordRepository extends JpaRepository<UserRecord, Long> {
    List<UserRecord> findByUser(User user);
}
