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

package com.qdigo.ebike.controlcenter.service.inner.rent;

import com.alibaba.fastjson.JSON;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.iot.datagram.PCPackage;
import com.qdigo.ebike.api.domain.dto.iot.datagram.PGPackage;
import com.qdigo.ebike.api.service.agent.AgentService;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.domain.entity.BikeGpsStatus;
import com.qdigo.ebike.bike.domain.entity.BikeStatus;
import com.qdigo.ebike.bike.repository.BikeRepository;
import com.qdigo.ebike.bike.repository.BikeStatusRepository;
import com.qdigo.ebike.bike.repository.dao.BikeDao;
import com.qdigo.ebike.bike.service.BikeStatusDaoService;
import com.qdigo.ebike.common.core.constants.*;
import com.qdigo.ebike.common.core.util.Ctx;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.common.core.util.geo.LocationConvert;
import com.qdigo.ebike.commonconfig.configuration.ThreadPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeInnerService {




    @Transactional
    public boolean gpsSmsEndBikeAsync(RideRecord rideRecord) {
        val mobileNo = rideRecord.getUser().getMobileNo();
        val bike = rideRecord.getBike();
        val bikeStatus = bike.getBikeStatus();
        val imei = bike.getImeiId();
        bikeStatus.setStatus(Status.BikeLogicStatus.available.getVal());

        deviceService.gpsSmsEndAsync0(imei, mobileNo).addCallback(result -> {
            Bike b = bikeRepository.findByImeiId(imei).orElseThrow(() -> new NullPointerException("没有查询到bike" + imei));
            BikeStatus status = b.getBikeStatus();
            log.debug("user:{},bike:{}进行gps和sms一起还车,还车结果:{}", mobileNo, imei, result);
            if (result) {
                bikeStatusService.removeActualStatus(status, Status.BikeActualStatus.cannotOps.getVal());
                bikeStatusService.removeActualStatus(status, Status.BikeActualStatus.smsCannotOps.getVal());
            } else {
                bikeStatusService.setActualStatus(status, Status.BikeActualStatus.cannotOps.getVal());
                bikeStatusService.setActualStatus(status, Status.BikeActualStatus.smsCannotOps.getVal());
            }
            if (status.getActualStatus().contains(Status.BikeActualStatus.locationFail.getVal())) {
                deviceService.rebootGPSAsync(imei);
            }
            log.debug("user:{},bike:{},同时使用定位和短信还车bikeStatus保存为,Status:{},ActualStatus:{}", mobileNo, imei, status.getStatus(), status.getActualStatus());
            bikeRepository.save(b);
        }, ex -> log.error("异步线程中还车发生异常:", ex));

        String key = Keys.flagOps.getKey(imei);
        redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());
        bikeRepository.save(bike);
        return true;
    }







    @Transactional
    public void endWithOutDevice(RideRecord rideRecord, String userName) {
        val bike = rideRecord.getBike();
        val user = rideRecord.getUser();
        val mobileNo = user.getMobileNo();
        val bikeStatus = bike.getBikeStatus();
        val imei = bike.getImeiId();
        val end = deviceService.gpsEnd(imei, userName);
        if (end) {
            log.debug("adminName:{},user:{},bike:{}硬件成功完成熄火,并上锁", userName, mobileNo, imei);
            bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.cannotOps.getVal());
        } else {
            log.debug("adminName:{},user:{},bike:{}未能成功断电并上锁", userName, mobileNo, imei);
            bikeStatusService.setActualStatus(bikeStatus, Status.BikeActualStatus.cannotOps.getVal());
        }
        val key = Keys.flagOps.getKey(imei);
        redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());
        // 逻辑还车,不管硬件是否真的还车
        bikeStatus.setStatus(Status.BikeLogicStatus.available.getVal()); // 电动车状态设为_可用
        bikeRepository.save(bike);
    }







    //@Async  被在同一个类的方法调用不起作用
    public void confirmBikeStatusAsync(RideRecord rideRecord, Consumer<RideRecord> callback) {
        val mobileNo = rideRecord.getUser().getMobileNo();
        val bikeStatus = rideRecord.getBike().getBikeStatus();
        val imei = rideRecord.getBike().getImeiId();
        val rideRecordId = rideRecord.getRideRecordId();
        if (bikeStatus.getStatus() == Status.BikeLogicStatus.available.getVal()) {
            //别的线程中，多次查询没有缓存
            ThreadPool.cachedThreadPool().submit(() -> {
                try {
                    TimeUnit.SECONDS.sleep(Const.pushRideDelay);
                    log.debug("user:{},bike:{}异步确认还车真实情况", mobileNo, imei);
                    for (int i = 0; i < 3; i++) {
                        final boolean beat = deviceService.getHearBeat(imei, mobileNo) != null;
                        final Bike bike = bikeRepository.findByImeiId(imei).orElseThrow(() -> new NullPointerException("没有查询到bike" + imei));
                        final BikeStatus status = bike.getBikeStatus();
                        log.debug("user:{},bike:{}请求上报心跳结果:{},确认还车后,bikeStatus:{},index:{}", mobileNo, imei, beat, status.getStatus(), i);
                        final RideOrder ridingRecord = rideRecordDao.findByRidingBike(bike);
                        log.debug("user:{},bike:{},ridingRecord:{}", mobileNo, imei, ridingRecord);
                        if (ridingRecord == null) {
                            final BikeGpsStatus gps = bike.getGpsStatus();
                            boolean doorLockOk = gps.getDoorLock() == 0;
                            boolean lockOk = gps.getLocked() == 1;
                            log.debug("user:{},bike:{},gps的状态:电门锁:{},上锁:{},index:{}", mobileNo, imei, doorLockOk, lockOk, i);
                            doorLockOk = doorLockOk || deviceService.shutdown(imei, mobileNo);
                            lockOk = lockOk || deviceService.lock(imei, mobileNo);
                            log.debug("user:{},bike:{},重新还车后gps的状态:电门锁:{},上锁:{},index:{}", mobileNo, imei, doorLockOk, lockOk, i);
                            if (status.getStatus() == Status.BikeLogicStatus.inUse.getVal()) {
                                status.setStatus(Status.BikeLogicStatus.available.getVal());
                                bikeRepository.save(bike);
                            } else if (doorLockOk && lockOk) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    callback.accept(rideRecord);
                } catch (Exception e) {
                    log.error("bikeService的executorService被中断:" + e.getMessage());
                    Thread.currentThread().interrupt();
                }

            });
        }
        val key = Keys.flagReboot.getKey(imei);
        if (redisTemplate.hasKey(key)) {
            log.debug("还车后车辆状态延迟确认:{}车辆GPS需要重启", imei);
            deviceService.rebootGPSAsync(imei);
        }
    }
    /*
    new Thread(() -> {}).start();
    */

    //新方式:尽量在mysql筛选
    public List<Bike> findAutoReturnBikesByStation() {
        long timer = System.currentTimeMillis();
        List<RideOrder> rideOrders = rideRecordDao.findAtStationRide();

        Date curDate = new Date();
        long end = curDate.getTime();

        log.debug("{}个车辆符合初期条件,用时{}毫秒", rideOrders.size(), System.currentTimeMillis() - timer);

        List<Bike> bikes = rideOrders.stream().map(RideOrder::getBike)
                .filter(bike -> redisTemplate.hasKey(Keys.up_pg_latest.getKey(FormatUtil.getCurDate(), bike.getImeiId())))
                .filter(bike -> {
                    AgentCfg config = agentService.getAgentConfig(bike.getAgent().getAgentId());
                    int autoReturnMinutes = config.getAutoReturnMinutes();
                    if (autoReturnMinutes <= 0) {
                        log.debug("{}车辆的所属代理商配置的自动还车时长为{}分钟,所以不自动还车", bike.getImeiId(), autoReturnMinutes);
                        return false;
                    }
                    String collectionName = pgMongoService.getCollectionName();
                    //查X分钟内 ,电门锁是否一直关着
                    long start = end - TimeUnit.MINUTES.toMillis(autoReturnMinutes);

                    Query query = new Query(Criteria.where("pgImei").is(bike.getImeiId())
                            .and("timestamp").gte(start).lte(end)
                            .orOperator(Criteria.where("pgWheelInput").is(1),
                                    Criteria.where("pgShaked").is(1)));

                    int size = mongoTemplate.find(query, PGPackage.class, collectionName).size();
                    log.debug("车辆{},{}分钟内收到的PG包有{}个为震动状态,代理商为:{}", bike.getImeiId(), autoReturnMinutes, size, bike.getAgent().getAgentMerchantName());
                    return size == 0;
                }).collect(Collectors.toList());

        log.debug("共有{}辆车符合自动还车条件,用时{}毫秒", bikes.size(), System.currentTimeMillis() - timer);

        return bikes;
    }

    //旧方式:通过java运算得到
    @Transactional
    public List<Bike> getBikesForPushByStation() {

        List<RideRecord> rideRecords = rideRecordRepository.findByRideStatus(Status.RideStatus.running.getVal());

        Date curDate = new Date();
        log.debug("{}个订单有效且未结算", rideRecords.size());

        //取出在还车点附近200米的车辆列表,且所关联订单正在计时
        List<Bike> bikes = rideRecords.stream()
                .filter(rideRecord -> {
                    long minutes = Duration.between(rideRecord.getStartTime().toInstant(), curDate.toInstant()).toMinutes();
                    return minutes > Const.noAutoReturnMinutes;
                })
                .map(RideRecord::getBike)
                .filter(bike -> bike.getBikeStatus().getStationId() != null)
                .filter(bike -> bike.getBikeStatus().getStatus() == Status.BikeLogicStatus.inUse.getVal())
                .filter(bike -> redisTemplate.hasKey(Keys.up_pg_latest.getKey(FormatUtil.getCurDate(), bike.getImeiId())))
                .filter(bike -> {
                    AgentCfg config = agentService.getAgentConfig(bike.getAgent().getAgentId());
                    int autoReturnMinutes = config.getAutoReturnMinutes();
                    if (autoReturnMinutes <= 0) {
                        log.debug("{}车辆的所属代理商配置的自动还车时长为{}分钟,所以不自动还车", bike.getImeiId(), autoReturnMinutes);
                        return false;
                    }
                    String collectionName = pgMongoService.getCollectionName();
                    //查X分钟内 ,电门锁是否一直关着
                    long end = curDate.getTime();
                    long start = end - TimeUnit.MINUTES.toMillis(autoReturnMinutes);

                    Query query = new Query(Criteria.where("pgImei").is(bike.getImeiId())
                            .and("timestamp").gte(start).lte(end)
                            .orOperator(Criteria.where("pgWheelInput").is(1),
                                    Criteria.where("pgShaked").is(1)));

                    int size = mongoTemplate.find(query, PGPackage.class, collectionName).size();
                    log.debug("车辆{},{}分钟内收到的PG包有{}个为震动状态", bike.getImeiId(), autoReturnMinutes, size);
                    return size == 0;
                }).collect(Collectors.toList());

        log.debug("共有{}辆车符合自动还车条件", bikes.size());

        return bikes;
    }

    @Transactional
    public boolean inUse(Bike bike) {
        BikeStatus bikeStatus = bike.getBikeStatus();
        int status = bikeStatus.getStatus();
        boolean inUse;
        if (rideRecordDao.findByRidingBike(bike) != null) {
            inUse = true;
            if (status != Status.BikeLogicStatus.inUse.getVal()) {
                bikeStatus.setStatus(Status.BikeLogicStatus.inUse.getVal());
                bikeStatusRepository.save(bikeStatus);
            }
        } else {
            inUse = false;
            if (status == Status.BikeLogicStatus.inUse.getVal()) {
                bikeStatus.setStatus(Status.BikeLogicStatus.available.getVal());
                bikeStatusRepository.save(bikeStatus);
            }
        }
        return inUse;
    }

    @Transactional
    public boolean inUse(String imei) {
        return bikeRepository.findByImeiId(imei)
                .map(this::inUse).orElse(false);
    }

    public boolean opsUserInUse(String imei) {
        return opsUseRecordRepository.findByUsingBike(imei).isPresent();
    }

    @CatAnnotation
    public boolean pgNotFound(Bike bike) {
        PGPackage pgPackage = pgMongoService.pgNotFound(bike.getImeiId());
        BikeStatus status = bike.getBikeStatus();
        if (pgPackage == null) {
            bikeStatusService.setActualStatus(status, Status.BikeActualStatus.pgNotFound.getVal());
        } else {
            bikeStatusService.removeActualStatus(status, Status.BikeActualStatus.pgNotFound.getVal());
        }
        bikeStatusDaoService.updateActualStatus(status.getBikeStatusId(), status.getActualStatus());
        return pgPackage == null;
    }

}
