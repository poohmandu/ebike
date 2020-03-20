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

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.control.Location;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.control.TrackService;
import com.qdigo.ebike.api.service.order.ride.RideBizService;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import com.qdigo.ebike.common.core.util.geo.LocationConvert;
import com.qdigo.ebike.ordercenter.domain.entity.ride.RideOrder;
import com.qdigo.ebike.ordercenter.domain.entity.ride.RideRecord;
import com.qdigo.ebike.ordercenter.repository.RideOrderRepository;
import com.qdigo.ebike.ordercenter.repository.RideRecordRepository;
import com.qdigo.ebike.ordercenter.service.inner.ride.RideRouteInnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * description: 
 *
 * date: 2020/3/16 5:03 PM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RideBizServiceImpl implements RideBizService {

    private final RideRecordRepository rideRecordRepository;
    private final RideRouteInnerService rideRouteService;
    private final RideOrderRepository rideOrderRepository;
    private final TrackService trackService;


    @Override
    @Transactional
    public RideDto createRide(StartParam param) {
        UserDto user = param.getUserDto();
        BikeDto bike = param.getBikeDto();
        Double lat = param.getLat();
        Double lng = param.getLng();
        String mobileNo = user.getMobileNo();
        log.debug("用户{}开始创建rideRecord", user.getMobileNo());
        Map<String, Double> toGps = LocationConvert.fromAmapToGps(lat, lng);
        lng = toGps.get("lng");
        lat = toGps.get("lat");
        //防止并发请求同时创建rideRecord

        List<RideRecord> rideRecords = rideRecordRepository.findByMobileNoAndRideStatusIn(mobileNo,
                Status.RideStatus.invalid.getVal(), Status.RideStatus.running.getVal());
        if (!rideRecords.isEmpty()) {
            throw new RuntimeException(user.getMobileNo() + "已存在运行态rideRecord情况下创建entity");
        }
        RideRecord rideRecord = new RideRecord()
                .setRideStatus(Status.RideStatus.invalid.getVal())
                //.setFreeActivity(Status.FreeActivity.noFree.getVal()) //已废弃
                .setImei(bike.getImeiId())
                .setConsume(0)
                .setActualConsume(0)
                .setPrice(bike.getPrice())
                .setUnitMinutes(bike.getUnitMinutes())
                .setStartLoc(FormatUtil.locStr(lng, lat))
                .setStartTime(new Date())
                .setMobileNo(mobileNo)
                .setAgentId(bike.getAgentId());
        rideRecord = rideRecordRepository.save(rideRecord);
        rideRouteService.createRideRoute(rideRecord.getRideRecordId(), bike.getAgentId(), param.getBikeStatusDto(), lat, lng);

        RideOrder rideOrder = RideOrder.fromRideRecord(rideRecord, bike.getAgentId());
        rideOrderRepository.save(rideOrder);
        return ConvertUtil.to(rideRecord, RideDto.class);

    }

    //最新的结束订单方式
    //@CatAnnotation
    @Transactional
    public RideDto finishRide(FinishParam finishParam) {
        double lat = finishParam.getLat();
        double lng = finishParam.getLng();
        Boolean isGPSLoc = finishParam.getIsGPSLoc();
        Long stationId = finishParam.getStationId();

        RideRecord rideRecord = ConvertUtil.to(finishParam.getRideDto(), RideRecord.class);
        if (!isGPSLoc) {
            val toGps = LocationConvert.fromAmapToGps(lat, lng);
            lng = toGps.get("lng");
            lat = toGps.get("lat");
        }
        val mobileNo = rideRecord.getMobileNo();

        rideRecord.setEndLoc(FormatUtil.locStr(lng, lat))
                .setEndTime(new Date())
                .setRideStatus(Status.RideStatus.end.getVal());

        this.updateDistance(rideRecord);

        rideRouteService.updateRideRoute(rideRecord.getRideRecordId(), lat, lng, stationId);

        rideRecordRepository.save(rideRecord);

        rideOrderRepository.findById(rideRecord.getRideRecordId())
                .ifPresent(rideOrder -> rideOrderRepository.deleteById(rideRecord.getRideRecordId()));
        log.debug("user:{}成功更新rideRecord:{}的数据,rideRecord.rideStatus:{}", mobileNo, rideRecord.getRideRecordId(), rideRecord.getRideStatus());
        return ConvertUtil.to(rideRecord, RideDto.class);
    }

    private void updateDistance(RideRecord rideRecord) {
        log.debug("开始计算rideRecord:{}的骑行路程", rideRecord.getRideRecordId());

        val start = rideRecord.getStartTime();
        val end = rideRecord.getEndTime();
        val imei = rideRecord.getImei();

        List<Location> points = trackService.getMoveTrackByTime(imei, start, end);
        points = trackService.averagePoints(points);

        double distance = 0; // 单位:米
        if (!points.isEmpty()) {
            for (int i = 1; i < points.size(); i++) {
                val lng1 = points.get(i - 1).getPgLongitude();
                val lat1 = points.get(i - 1).getPgLatitude();
                val lng2 = points.get(i).getPgLongitude();
                val lat2 = points.get(i).getPgLatitude();
                distance += GeoUtil.getDistanceForMeter(lat1, lng1, lat2, lng2);
            }
        }
        rideRecord.setDistance((int) distance);
        log.debug("rideRecord:{}成功计算并存储路程,为{}米", rideRecord.getRideRecordId(), distance);
    }


}
