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

package com.qdigo.ebike.agentcenter.repository.dao;

import com.qdigo.ebike.agentcenter.domain.entity.Agent;
import com.qdigo.ebike.agentcenter.domain.entity.AgentNotice;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

/**
 * Description: 
 * date: 2020/2/12 9:48 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Repository
public class AgentDao {

    @PersistenceContext
    private EntityManager entityManager;

    //@CatAnnotation
    public Agent findByImei(String imei) {
        String sql = "select a.* from agent a left join bike b on a.agent_id = b.agent_id where b.imei_id =:imei limit 1";
        Query query = entityManager.createNativeQuery(sql, Agent.class)
                .setParameter("imei", imei);
        List<Agent> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<AgentNotice> findValidNotice(Long agentId, int limit) {
        if (agentId == null) {
            return Collections.emptyList();
        }
        Query query = entityManager.createNativeQuery(
                "SELECT n.* FROM agent_notice n LEFT JOIN agent_to_agent_notice m ON n.id=m.notice_id LEFT JOIN agent a ON a.agent_id =m.agent_id WHERE " +
                        "m.agent_id=:agentId AND n.deleted =FALSE AND now() BETWEEN  n.start_time AND n.end_time ORDER BY n.publish_time DESC LIMIT :lim", AgentNotice.class)
                .setParameter("agentId", agentId)
                .setParameter("lim", limit);
        return query.getResultList();
    }

    public List<AgentNotice> findNoticeList(Long agentId) {
        if (agentId == null) {
            return Collections.emptyList();
        }
        Query query = entityManager.createNativeQuery(
                "SELECT n.* FROM agent_notice n LEFT JOIN agent_to_agent_notice m ON n.id=m.notice_id LEFT JOIN agent a ON a.agent_id =m.agent_id WHERE " +
                        "m.agent_id=:agentId AND n.deleted=FALSE ORDER BY n.publish_time DESC ", AgentNotice.class)
                .setParameter("agentId", agentId);
        return query.getResultList();
    }


}
