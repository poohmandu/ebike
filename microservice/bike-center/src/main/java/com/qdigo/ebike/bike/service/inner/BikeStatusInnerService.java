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

package com.qdigo.ebike.bike.service.inner;

import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.domain.entity.BikeGpsStatus;
import com.qdigo.ebike.bike.domain.entity.BikeStatus;
import com.qdigo.ebike.bike.domain.entity.fault.FaultReport;
import com.qdigo.ebike.bike.repository.fault.FaultReportRepository;
import com.qdigo.ebike.bike.service.BikeDaoService;
import com.qdigo.ebike.bike.service.BikeStatusDaoService;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by niezhao on 2017/4/13.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeStatusInnerService {

    private final RedisTemplate<String, String> redisTemplate;
    private final FaultReportRepository faultReportRepository;
    private final UserService userService;
    private final AgentConfigService agentConfigService;
    private final BikeStatusDaoService bikeStatusDaoService;
    private final BikeDaoService bikeDaoService;

    public void setActualStatus(BikeStatus bikeStatus, String actualStatus) {
        if (Status.BikeActualStatus.ok.getVal().equals(actualStatus)) {
            bikeStatus.setActualStatus(actualStatus);
        } else {
            if (Status.BikeActualStatus.ok.getVal().equals(bikeStatus.getActualStatus())) {
                bikeStatus.setActualStatus(actualStatus);
            } else {
                if (!StringUtils.contains(bikeStatus.getActualStatus(), actualStatus)) {
                    bikeStatus.setActualStatus(bikeStatus.getActualStatus() + "," + actualStatus);
                }
            }
        }
    }

    public void removeActualStatus(BikeStatus bikeStatus, String actualStatus) {
        if (!StringUtils.contains(bikeStatus.getActualStatus(), ",")) {
            if (StringUtils.contains(bikeStatus.getActualStatus(), actualStatus)) {
                bikeStatus.setActualStatus(Status.BikeActualStatus.ok.getVal());
            } else {
                bikeStatus.setActualStatus(bikeStatus.getActualStatus());
            }
        } else {
            if (StringUtils.contains(bikeStatus.getActualStatus(), "," + actualStatus)) {
                bikeStatus.setActualStatus(StringUtils.remove(bikeStatus.getActualStatus(), "," + actualStatus));
            } else {
                bikeStatus.setActualStatus(StringUtils.remove(bikeStatus.getActualStatus(), actualStatus + ","));
            }
        }
    }

    // 隔离级别为REQUIRES_NEW,独立的事务
    // @Transactional(propagation = Propagation.REQUIRES_NEW)
    //@Transactional
    //@CatAnnotation
    public String queryActualStatus(Bike bike) {
        //长时间未上传pg包
        BikeStatus bikeStatus = bike.getBikeStatus();
        BikeGpsStatus gpsStatus = bike.getGpsStatus();

        if (!redisTemplate.hasKey(Keys.up_pg_latest.getKey(FormatUtil.getCurDate(), bike.getImeiId()))) {
            setActualStatus(bikeStatus, Status.BikeActualStatus.pgNotFound.getVal());
        } else {
            removeActualStatus(bikeStatus, Status.BikeActualStatus.pgNotFound.getVal());
        }
        boolean phOk = gpsStatus.getError() == 0 && gpsStatus.getMachineError() == 0 && gpsStatus.getBrakeError() == 0
                && gpsStatus.getHandleBarError() == 0 && gpsStatus.getControlError() == 0;
        if (phOk) {
            removeActualStatus(bikeStatus, Status.BikeActualStatus.internalError.getVal());
        } else {
            setActualStatus(bikeStatus, Status.BikeActualStatus.internalError.getVal());
        }
        //是否有外接电源
        if (gpsStatus.getElectric() == 0 || gpsStatus.getPowerVoltage() == 0) {
            log.debug("{}无外接电源", bike.getImeiId());
            setActualStatus(bikeStatus, Status.BikeActualStatus.noPower.getVal());
        } else {
            log.debug("{}有外接电源", bike.getImeiId());
            removeActualStatus(bikeStatus, Status.BikeActualStatus.noPower.getVal());
        }

        Optional<FaultReport> faultReport = faultReportRepository.findTopByBikeIdOrderByLastModifiedDateDesc(bike.getBikeId());
        if (faultReport.isPresent() && faultReport.get().getReportStatus().equals(Status.ReportStatus.success.getVal())) {
            setActualStatus(bikeStatus, Status.BikeActualStatus.userReport.getVal());
        } else {
            removeActualStatus(bikeStatus, Status.BikeActualStatus.userReport.getVal());
        }
        log.debug("{}获取车辆的ActualStatus为:{}", bike.getImeiId(), bikeStatus.getActualStatus());

        bikeStatusDaoService.updateActualStatus(bikeStatus.getBikeStatusId(), bikeStatus.getActualStatus());

        return bikeStatus.getActualStatus();
    }

    public List<Bike> getBikeStatusNearBy(String mobileNo, double GPSLng, double GPSLat, double radius) {
        final Long agentId;
        if (mobileNo.isEmpty()) {
            agentId = null;
        }
        //else if (opsUserRepository.findByUserName(mobileNo).isPresent()) {
        //    agentId = null;
        //}
        else {
            agentId = userService.findByMobileNo(mobileNo).getAgentId();
        }

        List<Long> agentIds;
        if (agentId == null) {
            agentIds = new ArrayList<>();
        } else {
            agentIds = agentConfigService.allowAgents(agentId);
        }

        List<Bike> bikeList = bikeDaoService.findOnlineByLocation(GPSLat, GPSLng, radius * 1000, agentIds);

        List<Bike> bikes = bikeList.stream()
                .filter(bike -> bike.getBikeStatus().getStatus() == Status.BikeLogicStatus.available.getVal())
                .filter(bike -> {
                    BikeStatus bikeStatus = bike.getBikeStatus();
                    return !StringUtils.contains(bikeStatus.getActualStatus(), Status.BikeActualStatus.pgNotFound.getVal());
                })
                //经测试跟排序无关
                .sorted((o1, o2) -> {
                    BikeStatus s1 = o1.getBikeStatus();
                    BikeStatus s2 = o2.getBikeStatus();
                    double distance1 = GeoUtil.getDistanceForMeter(GPSLat, GPSLng, s1.getLatitude(), s1.getLongitude());
                    double distance2 = GeoUtil.getDistanceForMeter(GPSLat, GPSLng, s2.getLatitude(), s2.getLongitude());
                    return Double.compare(distance1, distance2);
                })
                .collect(Collectors.toList());

        log.debug("取出{}千米内的{}辆电动车", radius, bikes.size());
        return bikes;
    }


}
