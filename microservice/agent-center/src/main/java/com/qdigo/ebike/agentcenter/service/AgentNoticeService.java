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

package com.qdigo.ebike.agentcenter.service;

import com.qdigo.ebike.agentcenter.domain.entity.Agent;
import com.qdigo.ebike.agentcenter.domain.entity.AgentNotice;
import com.qdigo.ebike.agentcenter.repository.AgentNoticeRepository;
import com.qdigo.ebike.agentcenter.repository.dao.AgentDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * Created by niezhao on 2017/12/22.
 */
@Slf4j
@Service
public class AgentNoticeService {

    @Inject
    private AgentNoticeRepository agentNoticeRepository;
    @Inject
    private AgentDao agentDao;

    @Transactional
    public AgentNotice publishNotice(List<Agent> agentList, AgentNotice.NoticeType type, String title, String content,
                                     String redirectUrl, Date startTime, Date endTime) {
        AgentNotice agentNotice = new AgentNotice();
        agentNotice.setAgentList(agentList);
        agentNotice.setContent(content);
        agentNotice.setEndTime(endTime);
        agentNotice.setPublishTime(new Date());
        agentNotice.setRedirectUrl(redirectUrl);
        agentNotice.setStartTime(startTime);
        agentNotice.setTitle(title);
        agentNotice.setType(type);
        agentNotice.setDeleted(false);
        return agentNoticeRepository.save(agentNotice);
    }

    public AgentNotice getLastNotice(Long agentId) {
        List<AgentNotice> list = agentDao.findValidNotice(agentId, 1);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<AgentNotice> getValidNotices(Long agentId) {
        return agentDao.findValidNotice(agentId, 1000);
    }

    public List<AgentNotice> getNoticeList(Long agentId) {
        return agentDao.findNoticeList(agentId);
    }

}
