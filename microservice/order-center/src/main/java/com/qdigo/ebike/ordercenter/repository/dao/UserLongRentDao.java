/*
 * Copyright 2020 聂钊 nz@qdigo.com
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

package com.qdigo.ebike.ordercenter.repository.dao;

import com.qdigo.ebike.ordercenter.domain.entity.UserLongRent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by niezhao on 2017/7/24.
 */
@Repository
@Slf4j
public class UserLongRentDao {

    @PersistenceContext
    private EntityManager entityManager;

    //@CatAnnotation
    public UserLongRent findValidByUserId(long userId) {
        Query query = entityManager.createNativeQuery(
            "SELECT u.* FROM user_long_rent u WHERE u.user_id  = ?1 AND u.end_time > now() LIMIT 1",
            UserLongRent.class)
            .setParameter(1, userId);
        final List<UserLongRent> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return resultList.get(0);
        }
    }

    public boolean hasLongRent(long userId) {
        Query query = entityManager.createNativeQuery(
            "SELECT COUNT(*) FROM user_long_rent u WHERE u.user_id = ?1 AND u.end_time > now() LIMIT 1")
            .setParameter(1, userId);
        BigInteger result = (BigInteger) query.getSingleResult();
        return result.intValue() > 0;
    }

    //@CatAnnotation
    public UserLongRent findLastOne(long userId) {
        Query query = entityManager.createNativeQuery(
            "SELECT u.* FROM user_long_rent u WHERE u.user_id = ?1 ORDER BY u.end_time DESC LIMIT 1",
            UserLongRent.class)
            .setParameter(1, userId);
        List<UserLongRent> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return resultList.get(0);
        }
    }

}
