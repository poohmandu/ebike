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

import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.repository.BikeRepository;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Const;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeInnerService {

    private final BikeRepository bikeRepository;

    //@Inject
    //private MongoTemplate mongoTemplate;
    //@Inject
    //private BikeStatusService bikeStatusService;
    //@Inject
    //private RideRecordRepository rideRecordRepository;
    //@Inject
    //private GeoService geoService;
    //@Inject
    //private DeviceService deviceService;
    //@Inject
    //private RedisTemplate<String, String> redisTemplate;
    //@Inject
    //private RideRecordDao rideRecordDao;
    //@Inject
    //private PGMongoService pgMongoService;
    //@Inject
    //private StationRepository stationRepository;
    //@Inject
    //private AgentService agentService;
    //@Inject
    //private OpsUseRecordRepository opsUseRecordRepository;
    //@Inject
    //private BikeDao bikeDao;
    //@Inject
    //private BikeStatusRepository bikeStatusRepository;
    //@Inject
    //private BikeStatusDaoService bikeStatusDaoService;
    //@Resource
    //private PCMongoService pcMongoService;

    /**
     * @param imeiIdOrDeviceId
     * @return null:格式错误 new Bike():没有该车
     */
    public Bike findOneByImeiIdOrDeviceId(String imeiIdOrDeviceId) {
        log.debug("获取车辆:{}", imeiIdOrDeviceId);
        Bike bike = null;
        if (imeiIdOrDeviceId.length() == Const.imeiLength && imeiIdOrDeviceId.startsWith(ConfigConstants.imei.getConstant())) {
            bike = bikeRepository.findTopByImeiId(imeiIdOrDeviceId).orElse(new Bike());
        } else if (imeiIdOrDeviceId.length() == Const.deviceIdLength) {
            bike = bikeRepository.findTopByDeviceId(imeiIdOrDeviceId).orElse(new Bike());
        }
        return bike;
    }

    //@CatAnnotation
    //public boolean endBike(RideRecord rideRecord, Const.DeviceMode deviceMode) {
    //    if (deviceMode == Const.DeviceMode.GPS) {
    //        return this.gpsEndBike(rideRecord);
    //    } else if (deviceMode == Const.DeviceMode.SMS) {
    //        return this.smsEndBike(rideRecord);
    //    } else if (deviceMode == Const.DeviceMode.BLE) {
    //        return this.bleEndBike(rideRecord);
    //    } else if (deviceMode == Const.DeviceMode.GPS_SMS) {
    //        return this.gpsSmsEndBike(rideRecord);
    //    } else {
    //        throw new RuntimeException("设备还不支持模式:" + deviceMode);
    //    }
    //}
    //
    //@Transactional
    //public boolean gpsSmsEndBikeAsync(RideRecord rideRecord) {
    //    val mobileNo = rideRecord.getUser().getMobileNo();
    //    val bike = rideRecord.getBike();
    //    val bikeStatus = bike.getBikeStatus();
    //    val imei = bike.getImeiId();
    //    bikeStatus.setStatus(Status.BikeLogicStatus.available.getVal());
    //
    //    deviceService.gpsSmsEndAsync0(imei, mobileNo).addCallback(result -> {
    //        Bike b = bikeRepository.findByImeiId(imei).orElseThrow(() -> new NullPointerException("没有查询到bike" + imei));
    //        BikeStatus status = b.getBikeStatus();
    //        log.debug("user:{},bike:{}进行gps和sms一起还车,还车结果:{}", mobileNo, imei, result);
    //        if (result) {
    //            bikeStatusService.removeActualStatus(status, Status.BikeActualStatus.cannotOps.getVal());
    //            bikeStatusService.removeActualStatus(status, Status.BikeActualStatus.smsCannotOps.getVal());
    //        } else {
    //            bikeStatusService.setActualStatus(status, Status.BikeActualStatus.cannotOps.getVal());
    //            bikeStatusService.setActualStatus(status, Status.BikeActualStatus.smsCannotOps.getVal());
    //        }
    //        if (status.getActualStatus().contains(Status.BikeActualStatus.locationFail.getVal())) {
    //            deviceService.rebootGPSAsync(imei);
    //        }
    //        log.debug("user:{},bike:{},同时使用定位和短信还车bikeStatus保存为,Status:{},ActualStatus:{}", mobileNo, imei, status.getStatus(), status.getActualStatus());
    //        bikeRepository.save(b);
    //    }, ex -> log.error("异步线程中还车发生异常:", ex));
    //
    //    String key = Keys.flagOps.getKey(imei);
    //    redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());
    //    bikeRepository.save(bike);
    //    return true;
    //}
    //
    ////逻辑状态之后统一更新
    //public boolean gpsSmsEndBike(RideRecord rideRecord) {
    //    val mobileNo = rideRecord.getUser().getMobileNo();
    //    val bike = rideRecord.getBike();
    //    val status = bike.getBikeStatus();
    //    val imei = bike.getImeiId();
    //
    //    boolean end;
    //    Boolean pgNotFound = Ctx.get("pgNotFound", () -> this.pgNotFound(bike));
    //    Assert.notNull(pgNotFound);
    //    if (pgNotFound) {
    //        end = deviceService.gpsSmsEndFast(imei, mobileNo);
    //    } else {
    //        end = deviceService.gpsSmsEnd0(imei, mobileNo);
    //    }
    //
    //    log.debug("user:{},bike:{}进行gps和sms一起还车,还车结果:{}", mobileNo, imei, end);
    //    if (end) {
    //        bikeStatusService.removeActualStatus(status, Status.BikeActualStatus.cannotOps.getVal());
    //        bikeStatusService.removeActualStatus(status, Status.BikeActualStatus.smsCannotOps.getVal());
    //    } else {
    //        bikeStatusService.setActualStatus(status, Status.BikeActualStatus.cannotOps.getVal());
    //        bikeStatusService.setActualStatus(status, Status.BikeActualStatus.smsCannotOps.getVal());
    //    }
    //    if (status.getActualStatus().contains(Status.BikeActualStatus.locationFail.getVal())) {
    //        deviceService.rebootGPSAsync(imei);
    //    }
    //    bikeStatusDaoService.updateActualStatus(status.getBikeStatusId(), status.getActualStatus());
    //    log.debug("user:{},bike:{},同时使用定位和短信还车bikeStatus保存为,Status:{},ActualStatus:{}", mobileNo, imei, status.getStatus(), status.getActualStatus());
    //
    //    String key = Keys.flagOps.getKey(imei);
    //    redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());
    //
    //    return end;
    //}
    //
    //private boolean smsEndBike(RideRecord rideRecord) {
    //    val mobileNo = rideRecord.getUser().getMobileNo();
    //    val bike = rideRecord.getBike();
    //    val bikeStatus = bike.getBikeStatus();
    //    val imei = bike.getImeiId();
    //    val end = deviceService.smsClose(imei, mobileNo);
    //    log.debug("user:{},bike:{}通过短信还车结果:{}", mobileNo, imei, end);
    //    if (end) {
    //        bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.smsCannotOps.getVal());
    //    } else {
    //        bikeStatusService.setActualStatus(bikeStatus, Status.BikeActualStatus.smsCannotOps.getVal());
    //    }
    //    log.debug("user:{},bike:{},bikeStatus保存为,Status:{},ActualStatus:{}", mobileNo, imei, bikeStatus.getStatus(), bikeStatus.getActualStatus());
    //    bikeStatusDaoService.updateActualStatus(bikeStatus.getBikeStatusId(), bikeStatus.getActualStatus());
    //
    //    val key = Keys.flagOps.getKey(imei);
    //    redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());
    //    return end;
    //}
    //
    //private boolean bleEndBike(RideRecord rideRecord) {
    //    // 蓝牙模式默认都成功了
    //    val mobileNo = rideRecord.getUser().getMobileNo();
    //    val bike = rideRecord.getBike();
    //    val bikeStatus = bike.getBikeStatus();
    //    val imei = bike.getImeiId();
    //    log.debug("user:{},bike:{}进入BleEndBike", mobileNo, imei);
    //
    //    val key = Keys.flagOps.getKey(imei);
    //    redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());
    //
    //    return true;
    //}
    //
    //private boolean gpsEndBike(RideRecord rideRecord) {
    //    val mobileNo = rideRecord.getUser().getMobileNo();
    //    val bike = rideRecord.getBike();
    //    val bikeStatus = bike.getBikeStatus();
    //    val imei = bike.getImeiId();
    //    log.debug("user:{},bike:{}进入GPSEndBike", mobileNo, imei);
    //    val end = deviceService.gpsEnd(imei, mobileNo);
    //    if (end) {
    //        bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.cannotOps.getVal());
    //    } else {
    //        bikeStatusService.setActualStatus(bikeStatus, Status.BikeActualStatus.cannotOps.getVal());
    //    }
    //    log.debug("user:{},bike:{},bikeStatus保存为,Status:{},ActualStatus:{}", mobileNo, imei, bikeStatus.getStatus(), bikeStatus.getActualStatus());
    //    bikeStatusDaoService.updateActualStatus(bikeStatus.getBikeStatusId(), bikeStatus.getActualStatus());
    //
    //    val key = Keys.flagOps.getKey(imei);
    //    redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());
    //    return end;
    //}
    //
    ////TODO 先放宽策略，有反馈还车未断电再查询PX_SEQ
    //@CatAnnotation
    //public boolean confirmByPC(String imei) {
    //    PCPackage pcPackage = pcMongoService.findLockPC(imei, Ctx.now());
    //    boolean confirmByPC = pcPackage != null;
    //    log.debug("通过PC包确认是否已经还车:{}", confirmByPC);
    //    return confirmByPC;
    //}
    //
    //@Transactional
    //public void endWithOutDevice(RideRecord rideRecord, String userName) {
    //    val bike = rideRecord.getBike();
    //    val user = rideRecord.getUser();
    //    val mobileNo = user.getMobileNo();
    //    val bikeStatus = bike.getBikeStatus();
    //    val imei = bike.getImeiId();
    //    val end = deviceService.gpsEnd(imei, userName);
    //    if (end) {
    //        log.debug("adminName:{},user:{},bike:{}硬件成功完成熄火,并上锁", userName, mobileNo, imei);
    //        bikeStatusService.removeActualStatus(bikeStatus, Status.BikeActualStatus.cannotOps.getVal());
    //    } else {
    //        log.debug("adminName:{},user:{},bike:{}未能成功断电并上锁", userName, mobileNo, imei);
    //        bikeStatusService.setActualStatus(bikeStatus, Status.BikeActualStatus.cannotOps.getVal());
    //    }
    //    val key = Keys.flagOps.getKey(imei);
    //    redisTemplate.opsForValue().set(key, BikeCfg.OpsType.end.name());
    //    // 逻辑还车,不管硬件是否真的还车
    //    bikeStatus.setStatus(Status.BikeLogicStatus.available.getVal()); // 电动车状态设为_可用
    //    bikeRepository.save(bike);
    //}
    //
    //public Long atStation(RideRecord rideRecord, double lat, double lng, Const.DeviceMode deviceMode, double accuracy, String provider) {
    //    Long deviceAtStation;
    //    val imei = rideRecord.getBike().getImeiId();
    //    val mobileNo = rideRecord.getUser().getMobileNo();
    //
    //    val LbsValid = GeoUtil.isValid(lat, lng);
    //    if (deviceMode == Const.DeviceMode.GPS) {
    //        deviceAtStation = this.gpsAtStation(rideRecord);
    //    } else if (deviceMode == Const.DeviceMode.SMS) {
    //        deviceAtStation = this.smsAtStation(rideRecord);
    //    } else if (deviceMode == Const.DeviceMode.BLE) {
    //        deviceAtStation = this.gpsAtStation(rideRecord);
    //        if (deviceAtStation == null && LbsValid) {
    //            log.debug("蓝牙模式不用判断人车距离");
    //            deviceAtStation = this.lbsAtStation(rideRecord, lat, lng, accuracy, provider);
    //        }
    //    } else if (deviceMode == Const.DeviceMode.GPS_SMS) {
    //        deviceAtStation = this.gpsAtStation(rideRecord);
    //        if (deviceAtStation == null && LbsValid && this.bikeUserNear(rideRecord, lat, lng)) {
    //            deviceAtStation = this.lbsAtStation(rideRecord, lat, lng, accuracy, provider);
    //        }
    //    } else {
    //        throw new RuntimeException("不支持模式" + deviceMode);
    //    }
    //    return deviceAtStation;
    //}
    //
    //private boolean bikeUserNear(RideRecord rideRecord, double lbsLat, double lbsLng) {
    //    Map<String, Double> gps = LocationConvert.fromAmapToGps(lbsLat, lbsLng);
    //    lbsLat = gps.get("lat");
    //    lbsLng = gps.get("lng");
    //    val bike = rideRecord.getBike();
    //    val status = bike.getBikeStatus();
    //    val mobileNo = rideRecord.getUser().getMobileNo();
    //    val imei = bike.getImeiId();
    //    //将可能无法获取位置的情况剔除
    //    boolean gpsNotOk = StringUtils.containsAny(status.getActualStatus(),
    //            Status.BikeActualStatus.locationFail.getVal(),
    //            Status.BikeActualStatus.pgNotFound.getVal());
    //    if (gpsNotOk) {
    //        log.debug("user:{},bike:{},该车辆硬件有故障:{}", mobileNo, imei, status.getActualStatus());
    //        return true;
    //    } else {
    //        val sleep = pgMongoService.sleep(imei);
    //        val meter = GeoUtil.getDistanceForMeter(status.getLatitude(), status.getLongitude(), lbsLat, lbsLng);
    //        val config = Ctx.get("config", () -> agentService.getAgentConfig(bike.getAgent().getAgentId()));
    //        Assert.notNull(config);
    //        log.debug("user:{},bike:{}用户与车辆相隔{}米,GPS是否休眠:{},代理商配置:{}米", mobileNo, imei, meter, sleep, config.getBikeUserNearMeter());
    //        if (sleep) {
    //            deviceService.rebootGPSAsync(imei);
    //        }
    //        return meter < config.getBikeUserNearMeter();
    //    }
    //}
    //
    //private Long lbsAtStation(RideRecord rideRecord, double lat, double lng, double accuracy, String provider) {
    //    val mobileNo = rideRecord.getUser().getMobileNo();
    //    val bike = rideRecord.getBike();
    //    val imei = rideRecord.getBike().getImeiId();
    //    val agentId = bike.getAgent().getAgentId();
    //    Map<String, Double> gps = LocationConvert.fromAmapToGps(lat, lng);
    //    lat = gps.get("lat");
    //    lng = gps.get("lng");
    //    Optional<BikeStation> optional;
    //    if (accuracy > 0 && accuracy < 15 && StringUtils.isNotEmpty(provider) && provider.equalsIgnoreCase("gps")) {
    //        optional = geoService.isAtStationWithCompensate(lat, lng, (int) accuracy, agentId);
    //    } else {
    //        optional = geoService.isAtStation(lat, lng, true, agentId);
    //    }
    //    if (optional.isPresent()) {
    //        log.debug("user:{},bike{},LBS定位在还车点,手机GPS经纬度:({},{})", mobileNo, imei, lat, lng);
    //        return optional.get().getStationId();
    //    } else {
    //        log.debug("user:{},bike{},LBS定位不在还车点,手机GPS经纬度:({},{})", mobileNo, imei, lat, lng);
    //        return null;
    //    }
    //}
    //
    ////保证优先使用GPS位置
    //private Long smsAtStation(RideRecord rideRecord) {
    //    Long atStation;
    //    val bike = rideRecord.getBike();
    //    val imei = bike.getImeiId();
    //    val optional = deviceService.smsLocation(imei, rideRecord.getUser().getMobileNo());
    //    if (optional.isPresent()) {
    //        val loc = optional.get();
    //        atStation = geoService.isAtStation(loc.getPgLatitude(), loc.getPgLongitude(), true, bike.getAgent().getAgentId())
    //                .map(BikeStation::getStationId).orElse(null);
    //    } else {
    //        atStation = null;
    //    }
    //    return atStation;
    //}
    //
    //private Long gpsAtStation(RideRecord rideRecord) {
    //    Long atStation;
    //    val bike = rideRecord.getBike();
    //    val mobileNo = rideRecord.getUser().getMobileNo();
    //    val imei = bike.getImeiId();
    //    val status = bike.getBikeStatus();
    //    val agentId = bike.getAgent().getAgentId();
    //    Boolean pgNotFound = Ctx.get("pgNotFound", () -> this.pgNotFound(bike));
    //    Assert.notNull(pgNotFound);
    //    val key = Keys.up_pg_latest.getKey(FormatUtil.getCurDate(), imei);
    //    PGPackage pg = null;
    //    int meter = 0;
    //    int meter0 = 0;
    //    int meter1 = 0;
    //    int time = 0;
    //    if (!pgNotFound) {
    //        pg = JSON.parseObject(redisTemplate.opsForValue().get(key), PGPackage.class);
    //        int seconds = pg.getSeconds();
    //        if (seconds < 10) { // 小于10秒是异常的
    //            if (pg.getPgDoorLock() == 1) {
    //                seconds = 15;
    //            } else {
    //                seconds = 55;
    //            }
    //        }
    //
    //        time = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - pg.getTimestamp());
    //        time = time < 15 ? time : 13;
    //        time -= 3; //掏出手机到按下按钮还车时间
    //        time = time < 0 ? 0 : time;
    //
    //        meter0 = (int) ((double) pg.getDistance() * time / seconds / 2);
    //        meter1 = (int) (pg.getPgSpeed() / 3.6 * time) / 2;
    //        if (meter0 == 0)
    //            meter = meter1;
    //        else if (meter1 == 0)
    //            meter = meter0;
    //        else {
    //            meter = Math.min(meter0, meter1);
    //        }
    //
    //        meter = Math.min(meter, 15);
    //        Optional<BikeStation> optional = geoService.isAtStationWithCompensate(pg.getPgLatitude(), pg.getPgLongitude(), meter, agentId);
    //        atStation = optional.map(BikeStation::getStationId).orElse(null);
    //
    //    } else {
    //        atStation = null;
    //    }
    //    log.debug("user:{},bike:{},判断GPS是否在还车点atStation:{},GPS是否有发数据PGPackage={}。计时时间:{},应该补偿范围:{},{}",
    //            mobileNo, imei, atStation, pg, time, meter0, meter1);
    //    return atStation;
    //}
    //
    ////@Async  被在同一个类的方法调用不起作用
    //public void confirmBikeStatusAsync(RideRecord rideRecord, Consumer<RideRecord> callback) {
    //    val mobileNo = rideRecord.getUser().getMobileNo();
    //    val bikeStatus = rideRecord.getBike().getBikeStatus();
    //    val imei = rideRecord.getBike().getImeiId();
    //    val rideRecordId = rideRecord.getRideRecordId();
    //    if (bikeStatus.getStatus() == Status.BikeLogicStatus.available.getVal()) {
    //        //别的线程中，多次查询没有缓存
    //        ThreadPool.cachedThreadPool().submit(() -> {
    //            try {
    //                TimeUnit.SECONDS.sleep(Const.pushRideDelay);
    //                log.debug("user:{},bike:{}异步确认还车真实情况", mobileNo, imei);
    //                for (int i = 0; i < 3; i++) {
    //                    final boolean beat = deviceService.getHearBeat(imei, mobileNo) != null;
    //                    final Bike bike = bikeRepository.findByImeiId(imei).orElseThrow(() -> new NullPointerException("没有查询到bike" + imei));
    //                    final BikeStatus status = bike.getBikeStatus();
    //                    log.debug("user:{},bike:{}请求上报心跳结果:{},确认还车后,bikeStatus:{},index:{}", mobileNo, imei, beat, status.getStatus(), i);
    //                    final RideOrder ridingRecord = rideRecordDao.findByRidingBike(bike);
    //                    log.debug("user:{},bike:{},ridingRecord:{}", mobileNo, imei, ridingRecord);
    //                    if (ridingRecord == null) {
    //                        final BikeGpsStatus gps = bike.getGpsStatus();
    //                        boolean doorLockOk = gps.getDoorLock() == 0;
    //                        boolean lockOk = gps.getLocked() == 1;
    //                        log.debug("user:{},bike:{},gps的状态:电门锁:{},上锁:{},index:{}", mobileNo, imei, doorLockOk, lockOk, i);
    //                        doorLockOk = doorLockOk || deviceService.shutdown(imei, mobileNo);
    //                        lockOk = lockOk || deviceService.lock(imei, mobileNo);
    //                        log.debug("user:{},bike:{},重新还车后gps的状态:电门锁:{},上锁:{},index:{}", mobileNo, imei, doorLockOk, lockOk, i);
    //                        if (status.getStatus() == Status.BikeLogicStatus.inUse.getVal()) {
    //                            status.setStatus(Status.BikeLogicStatus.available.getVal());
    //                            bikeRepository.save(bike);
    //                        } else if (doorLockOk && lockOk) {
    //                            break;
    //                        }
    //                    } else {
    //                        break;
    //                    }
    //                }
    //                callback.accept(rideRecord);
    //            } catch (Exception e) {
    //                log.error("bikeService的executorService被中断:" + e.getMessage());
    //                Thread.currentThread().interrupt();
    //            }
    //
    //        });
    //    }
    //    val key = Keys.flagReboot.getKey(imei);
    //    if (redisTemplate.hasKey(key)) {
    //        log.debug("还车后车辆状态延迟确认:{}车辆GPS需要重启", imei);
    //        deviceService.rebootGPSAsync(imei);
    //    }
    //}
    ///*
    //new Thread(() -> {}).start();
    //*/
    //
    ////新方式:尽量在mysql筛选
    //public List<Bike> findAutoReturnBikesByStation() {
    //    long timer = System.currentTimeMillis();
    //    List<RideOrder> rideOrders = rideRecordDao.findAtStationRide();
    //
    //    Date curDate = new Date();
    //    long end = curDate.getTime();
    //
    //    log.debug("{}个车辆符合初期条件,用时{}毫秒", rideOrders.size(), System.currentTimeMillis() - timer);
    //
    //    List<Bike> bikes = rideOrders.stream().map(RideOrder::getBike)
    //            .filter(bike -> redisTemplate.hasKey(Keys.up_pg_latest.getKey(FormatUtil.getCurDate(), bike.getImeiId())))
    //            .filter(bike -> {
    //                AgentCfg config = agentService.getAgentConfig(bike.getAgent().getAgentId());
    //                int autoReturnMinutes = config.getAutoReturnMinutes();
    //                if (autoReturnMinutes <= 0) {
    //                    log.debug("{}车辆的所属代理商配置的自动还车时长为{}分钟,所以不自动还车", bike.getImeiId(), autoReturnMinutes);
    //                    return false;
    //                }
    //                String collectionName = pgMongoService.getCollectionName();
    //                //查X分钟内 ,电门锁是否一直关着
    //                long start = end - TimeUnit.MINUTES.toMillis(autoReturnMinutes);
    //
    //                Query query = new Query(Criteria.where("pgImei").is(bike.getImeiId())
    //                        .and("timestamp").gte(start).lte(end)
    //                        .orOperator(Criteria.where("pgWheelInput").is(1),
    //                                Criteria.where("pgShaked").is(1)));
    //
    //                int size = mongoTemplate.find(query, PGPackage.class, collectionName).size();
    //                log.debug("车辆{},{}分钟内收到的PG包有{}个为震动状态,代理商为:{}", bike.getImeiId(), autoReturnMinutes, size, bike.getAgent().getAgentMerchantName());
    //                return size == 0;
    //            }).collect(Collectors.toList());
    //
    //    log.debug("共有{}辆车符合自动还车条件,用时{}毫秒", bikes.size(), System.currentTimeMillis() - timer);
    //
    //    return bikes;
    //}
    //
    ////旧方式:通过java运算得到
    //@Transactional
    //public List<Bike> getBikesForPushByStation() {
    //
    //    List<RideRecord> rideRecords = rideRecordRepository.findByRideStatus(Status.RideStatus.running.getVal());
    //
    //    Date curDate = new Date();
    //    log.debug("{}个订单有效且未结算", rideRecords.size());
    //
    //    //取出在还车点附近200米的车辆列表,且所关联订单正在计时
    //    List<Bike> bikes = rideRecords.stream()
    //            .filter(rideRecord -> {
    //                long minutes = Duration.between(rideRecord.getStartTime().toInstant(), curDate.toInstant()).toMinutes();
    //                return minutes > Const.noAutoReturnMinutes;
    //            })
    //            .map(RideRecord::getBike)
    //            .filter(bike -> bike.getBikeStatus().getStationId() != null)
    //            .filter(bike -> bike.getBikeStatus().getStatus() == Status.BikeLogicStatus.inUse.getVal())
    //            .filter(bike -> redisTemplate.hasKey(Keys.up_pg_latest.getKey(FormatUtil.getCurDate(), bike.getImeiId())))
    //            .filter(bike -> {
    //                AgentCfg config = agentService.getAgentConfig(bike.getAgent().getAgentId());
    //                int autoReturnMinutes = config.getAutoReturnMinutes();
    //                if (autoReturnMinutes <= 0) {
    //                    log.debug("{}车辆的所属代理商配置的自动还车时长为{}分钟,所以不自动还车", bike.getImeiId(), autoReturnMinutes);
    //                    return false;
    //                }
    //                String collectionName = pgMongoService.getCollectionName();
    //                //查X分钟内 ,电门锁是否一直关着
    //                long end = curDate.getTime();
    //                long start = end - TimeUnit.MINUTES.toMillis(autoReturnMinutes);
    //
    //                Query query = new Query(Criteria.where("pgImei").is(bike.getImeiId())
    //                        .and("timestamp").gte(start).lte(end)
    //                        .orOperator(Criteria.where("pgWheelInput").is(1),
    //                                Criteria.where("pgShaked").is(1)));
    //
    //                int size = mongoTemplate.find(query, PGPackage.class, collectionName).size();
    //                log.debug("车辆{},{}分钟内收到的PG包有{}个为震动状态", bike.getImeiId(), autoReturnMinutes, size);
    //                return size == 0;
    //            }).collect(Collectors.toList());
    //
    //    log.debug("共有{}辆车符合自动还车条件", bikes.size());
    //
    //    return bikes;
    //}
    //
    //@Transactional
    //public boolean inUse(Bike bike) {
    //    BikeStatus bikeStatus = bike.getBikeStatus();
    //    int status = bikeStatus.getStatus();
    //    boolean inUse;
    //    if (rideRecordDao.findByRidingBike(bike) != null) {
    //        inUse = true;
    //        if (status != Status.BikeLogicStatus.inUse.getVal()) {
    //            bikeStatus.setStatus(Status.BikeLogicStatus.inUse.getVal());
    //            bikeStatusRepository.save(bikeStatus);
    //        }
    //    } else {
    //        inUse = false;
    //        if (status == Status.BikeLogicStatus.inUse.getVal()) {
    //            bikeStatus.setStatus(Status.BikeLogicStatus.available.getVal());
    //            bikeStatusRepository.save(bikeStatus);
    //        }
    //    }
    //    return inUse;
    //}
    //
    //@Transactional
    //public boolean inUse(String imei) {
    //    return bikeRepository.findByImeiId(imei)
    //            .map(this::inUse).orElse(false);
    //}
    //
    //public boolean opsUserInUse(String imei) {
    //    return opsUseRecordRepository.findByUsingBike(imei).isPresent();
    //}
    //
    //@CatAnnotation
    //public boolean pgNotFound(Bike bike) {
    //    PGPackage pgPackage = pgMongoService.pgNotFound(bike.getImeiId());
    //    BikeStatus status = bike.getBikeStatus();
    //    if (pgPackage == null) {
    //        bikeStatusService.setActualStatus(status, Status.BikeActualStatus.pgNotFound.getVal());
    //    } else {
    //        bikeStatusService.removeActualStatus(status, Status.BikeActualStatus.pgNotFound.getVal());
    //    }
    //    bikeStatusDaoService.updateActualStatus(status.getBikeStatusId(), status.getActualStatus());
    //    return pgPackage == null;
    //}

}
