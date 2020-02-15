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

import com.qdigo.ebike.usercenter.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Description: 
 * date: 2019/12/26 2:24 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
//@CacheConfig(cacheNames="User")
public interface UserRepository extends JpaRepository<User, Serializable>, JpaSpecificationExecutor<User> {

    //@Cacheable
    //@ThreadCache(key = "mobileNo")
    Optional<User> findOneByMobileNo(String mobileNo);

    Optional<User> findTopByWxliteOpenId(String wxliteOpenId);

    List<User> findByWxliteOpenId(String wxliteOpenId);

    List<User> findByMobileNoLike(String mobileNo);

    List<User> findByIdNo(String idNo);

}
