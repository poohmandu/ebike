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

package com.qdigo.ebike.usercenter.repository;

import com.qdigo.ebike.usercenter.domain.entity.UserWxOpenInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by niezhao on 2018/3/1.
 */
public interface UserWxOpenInfoRepository extends JpaRepository<UserWxOpenInfo, Long> {

    Optional<UserWxOpenInfo> findFirstByAppIdAndOpenId(String appId, String openId);

    Optional<UserWxOpenInfo> findFirstByUnionId(String unionId);

    List<UserWxOpenInfo> findByUserIdAndAppId(long userId, String appId);

}
