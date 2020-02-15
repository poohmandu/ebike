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

package com.qdigo.ebike.third.service.push.wxpush;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qdigo.ebicycle.domain.bike.Bike;
import com.qdigo.ebicycle.domain.bike.BikeStatus;
import com.qdigo.ebicycle.domain.mongo.RideTrack;
import com.qdigo.ebicycle.domain.ride.RideOrder;
import com.qdigo.ebicycle.o.dto.Point;
import com.qdigo.ebicycle.o.dto.WsMessage;
import com.qdigo.ebicycle.repository.bikeRepo.BikeRepository;
import com.qdigo.ebicycle.repository.dao.RideRecordDao;
import com.qdigo.ebicycle.service.ride.RideTrackService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by niezhao on 2017/12/13.
 */
@Slf4j
@Service
public class WebSocketHandler {

    @Inject
    private WebSocketService webSocketService;
    @Inject
    private RideRecordDao rideRecordDao;
    @Inject
    private BikeRepository bikeRepository;
    @Inject
    private RideTrackService rideTrackService;
    @Inject
    private MongoTemplate mongoTemplate;

    @Async
    public void onHeartBeat(String mobileNo, long timestamp) {
        if (!this.isAvailable(timestamp)) {
            return;
        }
        //this.bikeInfoServer(mobileNo);
        //this.ridingRouteServer(mobileNo);
    }

    public void onMessage(String mobileNo, WsMessage.BizType bizType, JSONObject json, long timestamp) {
        log.debug("{}接受到websocket的消息,类型为:{},数据为:{}", mobileNo, bizType, json);
        if (!this.isAvailable(timestamp)) {
            return;
        }
        RequestBody requestBody = JSON.toJavaObject(json, RequestBody.class);
        if (bizType == WsMessage.BizType.bikeInfo) {
            this.bikeInfoServer(mobileNo);
        } else if (bizType == WsMessage.BizType.ridingTrack) {
            this.ridingRouteServer(mobileNo);
        }
    }

    private boolean isAvailable(long timestamp) {
        return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - timestamp) < 5;
    }


    private void bikeInfoServer(String mobileNo) {
        try {
            RideOrder rideOrder = rideRecordDao.findByRidingUser(mobileNo);
            if (rideOrder == null) {
                return;
            }
            Bike bike = rideOrder.getBike();
            BikeStatus status = bike.getBikeStatus();
            ResponseBody body = new ResponseBody();
            body.setImei(bike.getImeiId());
            body.setLatitude(status.getLatitude());
            body.setLongitude(status.getLongitude());
            body.setBikeType(bike.getType());
            body.setStatus(status.getStatus());
            body.setMac(bike.getBleMac());
            WsMessage.ResponseMessage responseMessage = body.toResponseMessage("获得车辆信息");
            WsMessage.Message message = responseMessage.toMessage(mobileNo, WsMessage.BizType.bikeInfo);
            webSocketService.sendMessage(message);
        } catch (Exception err) {
            log.debug("bikeInfoServer发生异常", err);
        }
    }

    private void ridingRouteServer(String mobileNo) {
        try {
            RideOrder rideOrder = rideRecordDao.findByRidingUser(mobileNo);
            if (rideOrder == null) {
                return;
            }
            RideTrack trackCursor = rideTrackService.getOneWithCursor(rideOrder.getRideRecordId());
            long start;
            if (trackCursor == null) {
                start = rideOrder.getStartTime().getTime();
            } else {
                start = trackCursor.getTimestamp();
            }
            if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) < 50) {
                return;
            }
            rideTrackService.insertRideTracks(rideOrder.getRideRecordId());
            List<RideTrack> rideTracks = rideTrackService.getRideTrackAfter(rideOrder.getRideRecordId(), start);
            List<Point> points = rideTracks.stream()
                .filter(rideTrack -> rideTrack.getLatitude() != 0 && rideTrack.getLongitude() != 0 && rideTrack.getTimestamp() != 0)
                .map(rideTrack -> new Point().setTimestamp(rideTrack.getTimestamp())
                    .setLongitude(rideTrack.getLongitude())
                    .setLatitude(rideTrack.getLatitude()))
                .collect(Collectors.toList());
            if (points.isEmpty()) {
                return;
            }
            ResponseBody body = new ResponseBody();
            body.setPoints(points);
            WsMessage.ResponseMessage responseMessage = body.toResponseMessage("获得实时轨迹");
            WsMessage.Message message = responseMessage.toMessage(mobileNo, WsMessage.BizType.ridingTrack);
            boolean send = webSocketService.sendMessage(message);
            if (!send) {
                return;
            }
            RideTrack last = rideTracks.get(rideTracks.size() - 1);
            rideTrackService.saveCursorRideTrack(last);
        } catch (Exception err) {
            log.error("ridingRouteServer发生异常", err);
        }
    }


    @Data
    private static class RequestBody {
        private String imei;
    }

    @Data
    private static class ResponseBody {
        // bikeInfo
        private String imei;
        private Double latitude;
        private Double longitude;
        private String bikeType;
        private Integer status;
        private String mac;

        // ridingTrack
        private List<Point> points;

        public WsMessage.ResponseMessage toResponseMessage() {
            return this.toResponseMessage(200, "success");
        }

        public WsMessage.ResponseMessage toResponseMessage(String message) {
            return this.toResponseMessage(200, message);
        }

        public WsMessage.ResponseMessage toResponseMessage(int statusCode, String message) {
            val responseMessage = new WsMessage.ResponseMessage();
            responseMessage.setMessage(message);
            responseMessage.setStatusCode(200);
            responseMessage.setData(this);
            return responseMessage;
        }
    }

}
