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

package com.qdigo.ebike.agentcenter.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by niezhao on 2017/12/22.
 */
@Data
@Entity
@Table(name = "agent_notice")
public class AgentNotice {

    public enum NoticeType {
        activity, update, picture
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "agent_to_agent_notice",
        //当前实体
        joinColumns = {@JoinColumn(name = "notice_id", referencedColumnName = "id")},
        //关联实体
        inverseJoinColumns = {@JoinColumn(name = "agent_id", referencedColumnName = "agent_id")})
    private List<Agent> agentList;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NoticeType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "redirect_url", nullable = false)
    private String redirectUrl;

    @Column(name = "publish_time", nullable = false)
    private Date publishTime;

    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "end_time", nullable = false)
    private Date endTime;

    @Column(nullable = false)
    private boolean deleted;

}
