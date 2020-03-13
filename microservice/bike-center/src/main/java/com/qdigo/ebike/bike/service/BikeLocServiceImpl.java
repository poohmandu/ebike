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

package com.qdigo.ebike.bike.service;

import com.alibaba.fastjson.JSON;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.bike.BikeLoc;
import com.qdigo.ebike.api.service.bike.BikeLocService;
import com.qdigo.ebike.api.service.station.StationGeoService;
import com.qdigo.ebike.bike.domain.entity.Bike;
import com.qdigo.ebike.bike.domain.entity.BikeStatus;
import com.qdigo.ebike.bike.repository.BikeRepository;
import com.qdigo.ebike.bike.service.inner.BikeStatusInnerService;
import com.qdigo.ebike.common.core.constants.BikeCfg;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoneMatchException;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.common.core.util.geo.LocationConvert;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

/**
 * Created by niezhao on 2017/11/1.
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BikeLocServiceImpl implements BikeLocService {

    private final MongoTemplate mongoTemplate;
    private final BikeRepository bikeRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final StationGeoService geoService;
    private final BikeStatusInnerService statusService;

    private final static String collectionName = "BikeLoc";

    /**
     * @param imei
     * @param mobileNo
     * @param event
     * @param latitude
     * @param longitude
     * @return void
     * @author niezhao
     * @description 将各种时刻 【手机】的坐标记录存储
     * @date 2019/11/27 4:39 PM
     **/
    @Override
    @Transactional
    public void insertBikeLoc(String imei, String mobileNo, BikeLocService.LBSEvent event, double latitude, double longitude, Long agentId) {
        try {
            if (StringUtils.isBlank(imei)) {
                return;
            }
            if (!mongoTemplate.collectionExists(collectionName)) {
                mongoTemplate.createCollection(collectionName);
                IndexOperations indexOps = mongoTemplate.indexOps(collectionName);
                indexOps.ensureIndex(new Index("imei", Sort.Direction.ASC));
                indexOps.ensureIndex(new Index("mobileNo", Sort.Direction.ASC));
            }
            log.debug("保存车辆{},手机号{}的AMAP坐标:({},{})事件是:{}", imei, mobileNo, longitude, latitude, event);
            if (!GeoUtil.isValid(latitude, longitude)) {
                log.debug("{}用户客户端上传的经纬度无效({},{})", mobileNo, longitude, latitude);
                return;
            }

            Map<String, Double> delta = LocationConvert.fromAmapToGps(latitude, longitude);
            latitude = delta.get("lat");
            longitude = delta.get("lng");

            BikeLoc bikeLoc = new BikeLoc();
            bikeLoc.setEvent(event);
            bikeLoc.setImei(imei);
            bikeLoc.setLatitude(latitude);
            bikeLoc.setLongitude(longitude);
            bikeLoc.setMobileNo(mobileNo);
            bikeLoc.setTime(new Date());
            bikeLoc.setAgentId(agentId);
            mongoTemplate.insert(bikeLoc, collectionName);


            if (BikeLocService.LBSEvent.scanImei.equals(event) || BikeLocService.LBSEvent.scanDeviceId.equals(event))
                scan:{
                    redisTemplate.opsForValue().set(Keys.cacheMongoBikeLocScan.getKey(mobileNo), JSON.toJSONString(bikeLoc));
                    log.debug("{}用户保存最新的扫码位置{},实践类型:{}", mobileNo, imei, event);

                    if (BikeLocService.LBSEvent.scanDeviceId.equals(event))
                        break scan;

                    Bike bike = bikeRepository.findByImeiId(imei).orElseThrow(() -> new NoneMatchException("没有入库的IMEI号"));
                    String actualStatus = statusService.queryActualStatus(bike);

                    boolean notOk = StringUtils.containsAny(actualStatus, Status.BikeActualStatus.pgNotFound.getVal(),
                            Status.BikeActualStatus.locationFail.getVal());
                    if (notOk) {
                        log.debug("用户{}将扫码时的经纬度更新到车辆位置上{}", mobileNo, imei);

                        BikeStatus bikeStatus = bike.getBikeStatus();
                        bikeStatus.setLongitude(longitude).setLatitude(latitude).setLocationType(BikeCfg.LocationType.scan);

                        Long stationId = null;
                        StationGeoService.StationGeoDto stationGeoDto = geoService.isAtStation(latitude, longitude, false, bike.getAgentId());
                        if (stationGeoDto != null) {
                            stationId = stationGeoDto.getStationId();
                        }
                        bikeStatus.setStationId(stationId);
                        bikeStatus.setAreaId(geoService.isAtArea(latitude, longitude, bike.getAgentId()));

                        bikeRepository.save(bike);

                    }
                }

        } catch (Exception e) {
            log.error("持久化BikeLoc发生错误", e);
        }
    }

    @Override
    @SneakyThrows
    @Transactional
    public void insertBikeLoc(String imei, String mobileNo, LBSEvent event, double latitude, double longitude) {
        Bike bike = bikeRepository.findByImeiId(imei).orElseThrow(() -> new NoneMatchException("没有入库的IMEI号"));
        this.insertBikeLoc(imei, mobileNo, event, latitude, longitude, bike.getAgentId());
    }

    public void deleteCacheScanLoc(String mobileNo) {
        String key = Keys.cacheMongoBikeLocScan.getKey(mobileNo);
        redisTemplate.delete(key);
    }

    public BikeLoc findLastScanLoc(String mobileNo) {
        String key = Keys.cacheMongoBikeLocScan.getKey(mobileNo);
        String jsonStr = redisTemplate.opsForValue().get(key);
        if (jsonStr != null) {
            return JSON.parseObject(jsonStr, BikeLoc.class);
        }

        Query query = new Query(Criteria.where("mobileNo").is(mobileNo).and("event").is("scanImei"))
                .with(Sort.by("time").descending())
                .limit(1);

        BikeLoc bikeLoc = mongoTemplate.findOne(query, BikeLoc.class, collectionName);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(bikeLoc));
        return bikeLoc;
    }

}
