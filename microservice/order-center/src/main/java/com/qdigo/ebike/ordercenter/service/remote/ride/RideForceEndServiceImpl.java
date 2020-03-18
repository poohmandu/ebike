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

package com.qdigo.ebike.ordercenter.service.remote.ride;

import com.google.common.collect.Lists;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.agent.forceend.AgentForceEndConfigDto;
import com.qdigo.ebike.api.domain.dto.agent.forceend.ForceEndType;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.rideforceend.ForceEndInfo;
import com.qdigo.ebike.api.domain.dto.station.StationDto;
import com.qdigo.ebike.api.service.agent.AgentForceEndConfigService;
import com.qdigo.ebike.api.service.order.ride.RideForceEndService;
import com.qdigo.ebike.api.service.station.StationGeoService;
import com.qdigo.ebike.api.service.station.StationService;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.commonaop.annotations.ThreadCache;
import com.qdigo.ebike.ordercenter.domain.entity.ride.RideForceEnd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * description: 
 *
 * date: 2020/3/18 3:48 PM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RideForceEndServiceImpl implements RideForceEndService {

    private final AgentForceEndConfigService forceEndConfigService;
    private final StationGeoService geoService;
    private final StationService stationService;

    @Transactional
    public void insert(long rideRecordId, long agentId, double lat, double lng, ForceEndInfo forceEndInfo) {
        RideForceEnd rideForceEnd = new RideForceEnd();
        rideForceEnd.setRideRecordId(rideRecordId);
        rideForceEnd.setAgentId(agentId);
        rideForceEnd.setAmount(forceEndInfo.getAmount());
        rideForceEnd.setAmountNote(forceEndInfo.getAmountNote());
        rideForceEnd.setDistanceMeter(forceEndInfo.getDistanceMeter());
        rideForceEnd.setLatitude(lat);
        rideForceEnd.setLongitude(lng);
        forceEndRepository.save(rideForceEnd);
    }

    // 车辆的经纬度
    //@ThreadCache(key = "bikeId")
    public ForceEndInfo getForceEndInfo(Param param) {
        BikeStatusDto status = param.getStatusDto();
        Long agentId = param.getAgentId();
        ForceEndInfo forceEndInfo = new ForceEndInfo();
        double lat = status.getLatitude();
        double lng = status.getLongitude();

        String actualStatus = status.getActualStatus();
        boolean notOk = StringUtils.containsAny(actualStatus, Status.BikeActualStatus.pgNotFound.getVal(),
                Status.BikeActualStatus.locationFail.getVal());
        if (notOk) {
            forceEndInfo.setValid(false);
            forceEndInfo.setCause("车辆无法定位");
            return forceEndInfo;
        }
        //agentId为null 查询为空
        AgentForceEndConfigDto forceEndConfig = forceEndConfigService.findByAgentId(agentId);
        if (forceEndConfig == null) {
            forceEndInfo.setValid(false);
            forceEndInfo.setCause("该地区运营商未配置该功能");
            return forceEndInfo;
        } else {
            forceEndInfo.setConfig(forceEndConfig);
            ForceEndType type = forceEndConfig.getType();
            if (!forceEndConfig.isValid() || Objects.isNull(agentId)) {
                forceEndInfo.setValid(false);
                forceEndInfo.setCause("该地区运营商取消了该功能");
                return forceEndInfo;
            }

            if (geoService.isAtArea(lat, lng, agentId) == null) {
                forceEndInfo.setValid(false);
                forceEndInfo.setCause("服务区外无法强制还车");
                return forceEndInfo;
            }

            ArrayList<Long> agentIds = Lists.newArrayList(agentId);
            // 取出最近的还车点
            StationService.Param stationParam = StationService.Param.builder().agentIds(agentIds).lat(lat).lng(lng).build();
            StationDto station = stationService.getNearestStationByAgents(stationParam);
            if (station == null) {
                forceEndInfo.setValid(false);
                forceEndInfo.setCause("与还车点距离太远");
                return forceEndInfo;
            }
            forceEndInfo.setValid(true);

            int distanceMeter = (int) GeoUtil.getDistanceForMeter(lat, lng, station.getLatitude(), station.getLongitude());

            forceEndInfo.setDistanceMeter(distanceMeter);

            if (type == ForceEndType.linear) {
                int lineMeter = forceEndConfig.getLineMeter();
                double linePrice = forceEndConfig.getLinePrice();
                double amount = FormatUtil.getMoney((double) distanceMeter / lineMeter * linePrice);
                forceEndInfo.setAmount(amount);
                String format = MessageFormat.format("计费为{0}元/{1}米,距离最近还车点{2}米,需额外支付{3}元",
                        linePrice, lineMeter, distanceMeter, amount);
                forceEndInfo.setAmountNote(format);
            } else if (type == ForceEndType.ladder) {
                double amount;
                if (distanceMeter < forceEndConfig.getLevelOneKm() * 1000) {
                    amount = forceEndConfig.getLevelOne();
                } else if (distanceMeter < forceEndConfig.getLevelTwoKm() * 1000) {
                    amount = forceEndConfig.getLevelTwo();
                } else {
                    amount = forceEndConfig.getLevelThree();
                }
                forceEndInfo.setAmount(amount);
                String format = MessageFormat.format("计费为还车点外小于{0}千米,收费{1}元;大于{0}千米,小于{2}千米,收费{3}元;大于{2}千米且服务区内,收费{4}元。距离最近还车点{5}米,需额外支付{6}元",
                        forceEndConfig.getLevelOneKm(), forceEndConfig.getLevelOne(), forceEndConfig.getLevelTwoKm(),
                        forceEndConfig.getLevelTwo(), forceEndConfig.getLevelThree(), distanceMeter, amount);
                forceEndInfo.setAmountNote(format);
            } else {
                forceEndInfo.setValid(false);
                forceEndInfo.setCause("该地区运营商配置错误");
                return forceEndInfo;
            }
            return forceEndInfo;

        }


    }

}
