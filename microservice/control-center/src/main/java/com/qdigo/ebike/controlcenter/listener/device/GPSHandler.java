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

package com.qdigo.ebike.controlcenter.listener.device;

import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeGpsStatusDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.bike.BikeGpsStatusService;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.Ctx;
import com.qdigo.ebike.controlcenter.domain.dto.BikePgInfo;
import com.qdigo.ebike.controlcenter.domain.entity.device.PGSqlPackage;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PGPackage;
import com.qdigo.ebike.controlcenter.repository.PGSqlRepository;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PGMongoService;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2018/3/25.
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GPSHandler {

    private final PGMongoService pgMongoService;
    private final PGSqlRepository pgSqlRepository;
    private final PGService pgService;
    private final BikeStatusService bikeStatusService;
    private final AgentConfigService agentConfigService;
    private final OrderRideService rideService;
    private final BikeGpsStatusService bikeGpsStatusService;
    private final BikeService bikeService;

    //@Async
    //@Transactional
    //Transactional太多耗费性能
    //rebuild 探究mq不异步会阻塞的原因
    public ListenableFuture<Long> handlePG(PGPackage pg) {
        long start = System.currentTimeMillis();
        try {
            Ctx.init("handlePG");

            //mongodb
            val imei = pg.getPgImei();
            val old = pgMongoService.getLast(imei);
            pgMongoService.insertPG(pg, old);

            if (TimeUnit.MILLISECONDS.toSeconds(start - pg.getTimestamp()) < Const.pgNotFoundSeconds) {

                //百度地图
                //百度已不给额度
                //pgService.bdEntityService(pg, old);

                //最新PG包
                pgService.saveLatestPG(pg);

                BikeStatusDto bikeStatusDto = bikeStatusService.findByImei(imei);
                RideDto rideDto = null;
                if (bikeStatusDto == null) {
                    log.debug("未查询到imei号为{}的车辆", imei);
                    return new AsyncResult<>(System.currentTimeMillis() - start);
                } else if (bikeStatusDto.getStatus() != Status.BikeLogicStatus.available.getVal()) {
                    rideDto = rideService.findRidingByImei(imei);
                }

                BikeDto bikeDto = bikeService.findByImei(imei);
                AgentCfg agentCfg = agentConfigService.getAgentConfig(bikeDto.getAgentId());
                BikeGpsStatusDto bikeGpsStatusDto = bikeGpsStatusService.findByImei(imei);
                BikePgInfo bikePgInfo = BikePgInfo.builder().bikeStatusDto(bikeStatusDto).bikeDto(bikeDto)
                        .rideDto(rideDto).agentCfg(agentCfg).bikeGpsStatusDto(bikeGpsStatusDto).build();

                //biz 更新位置
                pgService.updateStatusLocation(pg, bikePgInfo);

                //biz 检测车辆状态
                pgService.checkBikeStatus(pg, old, bikePgInfo);

                //更新各种状态
                pgService.updateBikeInfo(bikePgInfo, pg);

                PGSqlPackage pgSqlPackage = formToSqlDomain(pg);
                pgSqlRepository.save(pgSqlPackage);

            }
        } catch (Exception e) {
            log.error("PG在MQ的异步消费过程中异常:", e);
        } finally {
            Ctx.clear();
        }
        return new AsyncResult<>(System.currentTimeMillis() - start);
    }

    private PGSqlPackage formToSqlDomain(PGPackage f) {
        PGSqlPackage s = new PGSqlPackage();
        s.setPgAutoLocked(f.getPgAutoLocked());
        s.setPgDoorLock(f.getPgDoorLock());
        s.setPgElectric(f.getPgElectric());
        s.setPgError(f.getPgError());
        s.setPgHight(f.getPgHight());
        s.setPgImei(f.getPgImei());
        s.setPgLatitude(f.getPgLatitude());
        s.setPgLocked(f.getPgLocked());
        s.setPgLongitude(f.getPgLongitude());
        s.setPgShaked(f.getPgShaked());
        s.setPgSpeed(f.getPgSpeed());
        s.setPgStar(f.getPgStar());
        s.setPgTumble(f.getPgTumble());
        s.setPgWheelInput(f.getPgWheelInput());
        s.setTimestamp(f.getTimestamp());
        return s;
    }

}
