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

package com.qdigo.ebike.controlcenter.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.third.map.Point;
import com.qdigo.ebike.api.domain.dto.third.map.RideTrackDto;
import com.qdigo.ebike.api.domain.dto.third.map.TrackDto;
import com.qdigo.ebike.api.service.control.RideTrackService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.api.service.third.address.BdMapService;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.RideTrack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description: 
 * date: 2020/1/19 12:03 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RideTrackServiceImpl implements RideTrackService {

    private final OrderRideService rideService;
    private final BdMapService bdMapService;
    private final MongoTemplate mongoTemplate;

    private final static String collectionName = "RideTrack";

    @Override
    public RideTrackDto getLastRideTrack(long rideRecordId) {
        Query query = new Query(Criteria.where("rideRecordId").is(rideRecordId))
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(1);
        RideTrack rideTrack = mongoTemplate.findOne(query, RideTrack.class, collectionName);
        return ConvertUtil.to(rideTrack, RideTrackDto.class);
    }

    @Override
    public RideTrackDto getOneWithCursor(long rideRecordId) {
        Query query = new Query(Criteria.where("rideRecordId").is(rideRecordId).and("cursor").ne(null))
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(1);
        RideTrack rideTrack = mongoTemplate.findOne(query, RideTrack.class, collectionName);
        return ConvertUtil.to(rideTrack, RideTrackDto.class);
    }

    @Override
    public void insertRideTracks(long rideRecordId) {
        //cursor的值是什么无所谓,null 或其他
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
            IndexOperations indexOps = mongoTemplate.indexOps(collectionName);
            Index index = new Index().on("rideRecordId", Sort.Direction.ASC);
            indexOps.ensureIndex(index);
        }
        RideDto rideRecord = rideService.findById(rideRecordId);
        if (rideRecord == null) {
            return;
        }
        String imei = rideRecord.getImei();

        RideTrackDto rideTrack = this.getLastRideTrack(rideRecord.getRideRecordId());
        long start;
        if (rideTrack == null) {
            start = rideRecord.getStartTime().getTime();
        } else {
            start = rideTrack.getTimestamp();
        }
        long end;
        if (rideRecord.getRideStatus() != Status.RideStatus.running.getVal() &&
                rideRecord.getRideStatus() != Status.RideStatus.invalid.getVal()) {
            log.debug("该骑行记录已完成");
            end = rideRecord.getEndTime().getTime();
        } else {
            end = System.currentTimeMillis();
        }
        if (start >= end) {
            return;
        }

        TrackDto track = this.getTrack(imei, start, end);
        if (track == null || track.getPoints().isEmpty()) {
            return;
        }
        List<RideTrack> rideTracks = track.getPoints().stream()
                .filter(point -> point.getLatitude() != 0 && point.getLongitude() != 0)
                .filter(point -> point.getTimestamp() > start && point.getTimestamp() <= end)
                .map(point -> {
                    RideTrack trac = new RideTrack();
                    trac.setImei(imei);
                    trac.setLatitude(point.getLatitude());
                    trac.setLongitude(point.getLongitude());
                    trac.setRideRecordId(rideRecord.getRideRecordId());
                    trac.setTimestamp(point.getTimestamp());
                    return trac;
                })
                .collect(Collectors.toList());
        if (rideTracks.isEmpty()) {
            return;
        }
        RideTrack first = rideTracks.get(0);
        RideTrack last = rideTracks.get(rideTracks.size() - 1);
        if (first.getTimestamp() == last.getTimestamp() &&
                first.getLatitude() == last.getLatitude() &&
                first.getLongitude() == last.getLongitude()) {
            // 首尾完全一样
            return;
        }
        mongoTemplate.insert(rideTracks, collectionName);
    }

    @Override
    public List<RideTrackDto> getRideTrack(long rideRecordId) {
        Query query = new Query(Criteria.where("rideRecordId").is(rideRecordId));
        List<RideTrack> rideTracks = mongoTemplate.find(query, RideTrack.class, collectionName);
        return ConvertUtil.to(rideTracks, RideTrackDto.class);
    }

    @Override
    public List<RideTrackDto> getRideTrackAfter(long rideRecordId, long timestamp) {
        Query query = new Query(Criteria.where("rideRecordId").is(rideRecordId)
                .and("timestamp").gt(timestamp));
        List<RideTrack> rideTracks = mongoTemplate.find(query, RideTrack.class, collectionName);
        return ConvertUtil.to(rideTracks, RideTrackDto.class);
    }

    @Override
    public List<RideTrackDto> getRideTrackAfterAndCursor(long rideRecordId, long start) {
        Query query = new Query(Criteria.where("rideRecordId").is(rideRecordId)
                .and("timestamp").gt(start));
        List<RideTrack> rideTracks = mongoTemplate.find(query, RideTrack.class, collectionName);
        if (rideTracks.isEmpty()) {
            return ConvertUtil.to(rideTracks, RideTrackDto.class);
        }
        RideTrack rideTrack = rideTracks.get(rideTracks.size() - 1);
        RideTrackDto rideTrackDto = ConvertUtil.to(rideTrack, RideTrackDto.class);
        this.saveCursorRideTrack(rideTrackDto);
        return ConvertUtil.to(rideTracks, RideTrackDto.class);
    }

    @Override
    public void saveCursorRideTrack(RideTrackDto rideTrackdto) {
        RideTrack rideTrack = ConvertUtil.to(rideTrackdto, RideTrack.class);
        rideTrack.setCursor(rideTrack.getTimestamp());
        mongoTemplate.save(rideTrack, collectionName);
    }

    private TrackDto getTrack(String entityName, long start, long end) {
        //起始和结束时间必须在24小时之内,将时间拆分成多个时间段
        long hours = 10;
        long timeUnit = TimeUnit.HOURS.toMillis(hours);
        if (end - start < timeUnit) {
            JSONObject json = bdMapService.getTrack(entityName, new Date(start), new Date(end));
            if (json == null) {
                return null;
            }
            return this.getOne(json);
        }
        log.debug("骑行轨迹查询,时长超过{}小时,为{}小时", hours, TimeUnit.MILLISECONDS.toHours(end - start));
        long count = (end - start) / timeUnit + 1;
        long curStart = start;

        TrackDto track = new TrackDto();
        for (int i = 0; i < count; i++) {
            long curEnd;
            if (i == (count - 1)) {
                curEnd = end;
            } else {
                curEnd = curStart + timeUnit;
            }
            JSONObject json = bdMapService.getTrack(entityName, new Date(curStart), new Date(curEnd));
            if (json == null) {
                return null;
            }
            TrackDto trac = this.getOne(json);
            track.setDistance(track.getDistance() + trac.getDistance());
            track.setSize(track.getSize() + trac.getSize());
            track.getPoints().addAll(trac.getPoints());

            curStart = curEnd;
        }
        return track;
    }

    private TrackDto getOne(JSONObject json) {
        TrackDto track = new TrackDto();
        List<Point> points = Lists.newArrayList();
        double distance = json.getDoubleValue("distance");
        track.setDistance(distance);
        JSONObject start_point = json.getJSONObject("start_point");
        Point startP = new Point().setLatitude(start_point.getDoubleValue("latitude"))
                .setLongitude(start_point.getDoubleValue("longitude"))
                .setTimestamp(start_point.getLongValue("loc_time") * 1000);
        JSONObject end_point = json.getJSONObject("end_point");
        Point endP = new Point().setLatitude(end_point.getDoubleValue("latitude"))
                .setLongitude(end_point.getDoubleValue("longitude"))
                .setTimestamp(end_point.getLongValue("loc_time") * 1000);
        JSONArray array = json.getJSONArray("points");
        if (startP.getTimestamp() == endP.getTimestamp()) {
            return track;
        }
        if (array.size() == 0) {
            return track;
        }
        points.add(startP);
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            double latitude = obj.getDoubleValue("latitude");
            double longitude = obj.getDoubleValue("longitude");
            long timestamp = obj.getLongValue("loc_time") * 1000;
            Point point = new Point().setLatitude(latitude).setLongitude(longitude).setTimestamp(timestamp);
            points.add(point);
        }
        points.add(endP);

        track.setSize(points.size());
        track.setPoints(points);
        return track;
    }
}
