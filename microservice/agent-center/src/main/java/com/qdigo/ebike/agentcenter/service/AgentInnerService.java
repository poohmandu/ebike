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

package com.qdigo.ebike.agentcenter.service;

import com.google.common.collect.Lists;
import com.qdigo.ebike.agentcenter.domain.entity.Agent;
import com.qdigo.ebike.agentcenter.domain.entity.AgentConfig;
import com.qdigo.ebike.agentcenter.repository.AgentRepository;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.common.core.constants.CacheKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 最多是二级代理
 * Created by niezhao on 2017/11/17.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AgentInnerService {

    private final AgentRepository agentRepository;
    @Resource
    private AgentInnerService self;

    private static final long cacheTime = 10 * 60 * 1000;
    private static final ConcurrentMap<Long, AgentConfig> CONFIG_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, Long> CONFIG_TIME = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Long, Agent> AGENT_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, Long> AGENT_TIME = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Long, List<Agent>> PARENT_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, Long> PARENT_TIME = new ConcurrentHashMap<>();


    private AgentConfig getAgentConfigFromDB(long agentId) {
        AgentConfig agentConfig = null;
        Agent agent = this.findByAgentId(agentId);
        if (agent.getConfig() != null) {
            agentConfig = agent.getConfig();
        } else if (agent.getParentId() != null) {
            Agent parent = this.findByAgentId(agent.getParentId());
            agentConfig = parent.getConfig();
        }
        return agentConfig;
    }

    /**
     * 调用最多的方法，存在内存缓存
     * 如果有二级代理配置就取二级代理配置，
     * 没有则用一级代理
     *
     * @param agentId
     * @return 不能有null
     */
    @Cacheable(CacheKey.AgentConfig)
    //@ThreadCache(key = "agentId") 已缓存,不需要再缓存,而且非web环境会错乱
    public AgentCfg getAgentConfig(Long agentId) {
        AgentConfig agentConfig = null;
        if (agentId != null) {
            Long time = CONFIG_TIME.getOrDefault(agentId, 0L);
            AgentConfig cacheConfig = CONFIG_CACHE.get(agentId);
            if (System.currentTimeMillis() - time < cacheTime && cacheConfig != null) {
                agentConfig = cacheConfig;
            } else {
                agentConfig = ObjectUtils.defaultIfNull(getAgentConfigFromDB(agentId), new AgentConfig());
                //log.debug("agentConfig缓存已过期或无效,重新获取:{},size:{}", agentId, CONFIG_CACHE.size());
                CONFIG_TIME.put(agentId, System.currentTimeMillis());
                CONFIG_CACHE.put(agentId, agentConfig);
            }
        }

        // 后续对agentConfig的转化
        if (agentConfig == null) {
            agentConfig = new AgentConfig();
        }
        AgentCfg config = new AgentCfg(); // 没有id 和 agentId
        config.setSpeedLimit(agentConfig.getSpeedLimit() == null ? config.isSpeedLimit() : agentConfig.getSpeedLimit());
        config.setShareStation(agentConfig.getShareStation() == null ? config.isShareStation() : agentConfig.getShareStation());
        config.setLongRentDisplay(agentConfig.getLongRentDisplay() == null ? config.isLongRentDisplay() : agentConfig.getLongRentDisplay());
        config.setDayMaxHours(agentConfig.getDayMaxHours() == null ? config.getDayMaxHours() : agentConfig.getDayMaxHours());
        config.setBatteryBan(agentConfig.getBatteryBan() == null ? config.getBatteryBan() : agentConfig.getBatteryBan());
        config.setCompensateMeter(agentConfig.getCompensateMeter() == null ? config.getCompensateMeter() : agentConfig.getCompensateMeter());
        config.setBikeUserNearMeter(agentConfig.getBikeUserNearMeter() == null ? config.getBikeUserNearMeter() : agentConfig.getBikeUserNearMeter());
        config.setRequireDeposit(agentConfig.getRequireDeposit() == null ? config.getRequireDeposit() : agentConfig.getRequireDeposit());
        config.setLowInsurance(agentConfig.getLowInsurance() == null ? config.isLowInsurance() : agentConfig.getLowInsurance());
        config.setHighInsurance(agentConfig.getHighInsurance() == null ? config.isHighInsurance() : agentConfig.getHighInsurance());
        config.setRequireScore(agentConfig.getRequireScore() == null ? config.getRequireScore() : agentConfig.getRequireScore());
        config.setNoneDepositFirstCharge(agentConfig.getNoneDepositFirstCharge() == null ? config.getNoneDepositFirstCharge() : agentConfig.getNoneDepositFirstCharge());
        config.setDepositFirstCharge(agentConfig.getDepositFirstCharge() == null ? config.getDepositFirstCharge() : agentConfig.getDepositFirstCharge());
        config.setMinCharge(agentConfig.getMinCharge() == null ? config.getMinCharge() : agentConfig.getMinCharge());
        config.setDepositDisplay(agentConfig.getDepositDisplay() == null ? config.isDepositDisplay() : agentConfig.getDepositDisplay());
        config.setZmScoreDisplay(agentConfig.getZmScoreDisplay() == null ? config.isZmScoreDisplay() : agentConfig.getZmScoreDisplay());
        config.setStudentAuthDisplay(agentConfig.getStudentAuthDisplay() == null ? config.isStudentAuthDisplay() : agentConfig.getStudentAuthDisplay());
        config.setWxscoreDisplay(agentConfig.getWxscoreDisplay() == null ? config.isWxscoreDisplay() : agentConfig.getWxscoreDisplay());
        config.setInviteDisplay(agentConfig.getInviteDisplay() == null ? config.isInviteDisplay() : agentConfig.getInviteDisplay());
        config.setAutoReturnMinutes(agentConfig.getAutoReturnMinutes() == null ? config.getAutoReturnMinutes() : agentConfig.getAutoReturnMinutes());
        config.setFreeSeconds(agentConfig.getFreeSeconds() == null ? config.getFreeSeconds() : agentConfig.getFreeSeconds());
        config.setInsuranceSms(agentConfig.getInsuranceSms() == null ? config.isInsuranceSms() : agentConfig.getInsuranceSms());
        config.setInputPrefix(agentConfig.getInputPrefix() == null ? config.getInputPrefix() : agentConfig.getInputPrefix());
        config.setTakeawayDisplay(agentConfig.getTakeawayDisplay() == null ? config.isTakeawayDisplay() : agentConfig.getTakeawayDisplay());
        config.setAllowArrears(agentConfig.getAllowArrears() == null ? config.getAllowArrears() : agentConfig.getAllowArrears());
        config.setIdentifyType(agentConfig.getIdentifyType() == null ? config.getIdentifyType() : agentConfig.getIdentifyType());
        config.setAllowAge(agentConfig.getAllowAge() == null ? config.getAllowAge() : agentConfig.getAllowAge());
        config.setForceOff(agentConfig.getForceOff() == null ? config.isForceOff() : agentConfig.getForceOff());
        return config;
    }

    private Agent findByAgentId(long agentId) {
        Long time = AGENT_TIME.getOrDefault(agentId, 0L);
        Agent agent = AGENT_CACHE.get(agentId);
        if (System.currentTimeMillis() - time < cacheTime && agent != null) {
            return agent;
        } else {
            agent = agentRepository.findById(agentId).orElse(null);
            AGENT_TIME.put(agentId, System.currentTimeMillis());
            AGENT_CACHE.put(agentId, agent);
            return agent;
        }
    }

    private List<Agent> findByParentId(long agentId) {
        Long time = PARENT_TIME.getOrDefault(agentId, 0L);
        List<Agent> agents = PARENT_CACHE.get(agentId);
        if (System.currentTimeMillis() - time < cacheTime && agents != null) {
            return agents;
        } else {
            agents = agentRepository.findByParentId(agentId);
            PARENT_TIME.put(agentId, System.currentTimeMillis());
            PARENT_CACHE.put(agentId, agents);
            return agents;
        }
    }

    private List<Agent> allowAgentsByShare(long agentId) {
        Agent agent = this.findByAgentId(agentId);
        List<Agent> agents;
        if (agent.getParentId() == null) {
            agents = agentRepository.findByParentId(agent.getAgentId());
            agents.add(agent);
        } else {
            agents = agentRepository.findByParentId(agent.getParentId());
            agents.add(this.findByAgentId(agent.getParentId()));
        }
        return agents;
    }

    @Cacheable(CacheKey.AgentConfig)
    public List<Agent> allowAgents(long agentId) {
        AgentCfg config = this.getAgentConfig(agentId);
        List<Agent> agents;
        if (config.isShareStation()) {
            agents = this.allowAgentsByShare(agentId);
        } else {
            agents = Lists.newArrayList(this.findByAgentId(agentId));
        }
        return agents.stream()
                .filter(agent -> !agent.getIsDeleted())
                .collect(Collectors.toList());
    }

}
