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

package com.qdigo.ebike.activitycenter.repository.dao;

import com.qdigo.ebike.activitycenter.domain.entity.scenic.EntityCard;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class EntityCardDao {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityCard findLastOne() {
        List<EntityCard> list = entityManager.createNativeQuery("select e.* from scenic_entity_card e order by e.entity_card_id desc limit 1", EntityCard.class)
            .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }


}
