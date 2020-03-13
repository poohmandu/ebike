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
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.third.map.Point;
import com.qdigo.ebike.api.domain.dto.third.map.RideTrackDto;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.api.service.control.RideTrackService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.third.domain.dto.WsMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by niezhao on 2017/12/13.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WebSocketHandler {

    @Resource
    private WebSocketService webSocketService;

    private final OrderRideService rideService;
    private final BikeService bikeService;
    private final RideTrackService rideTrackService;
    private final BikeStatusService bikeStatusService;



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
            RideDto rideDto = rideService.findRidingByMobileNo(mobileNo);
            if (rideDto == null) {
                return;
            }
            String imei = rideDto.getImei();
            BikeDto bikeDto = bikeService.findByImei(imei);
            BikeStatusDto statusDto = bikeStatusService.findStatusByBikeIId(bikeDto.getBikeId());

            ResponseBody body = new ResponseBody();
            body.setImei(bikeDto.getImeiId());
            body.setLatitude(statusDto.getLatitude());
            body.setLongitude(statusDto.getLongitude());
            body.setBikeType(bikeDto.getType());
            body.setStatus(statusDto.getStatus());
            body.setMac(bikeDto.getBleMac());
            WsMessage.ResponseMessage responseMessage = body.toResponseMessage("获得车辆信息");
            WsMessage.Message message = responseMessage.toMessage(mobileNo, WsMessage.BizType.bikeInfo);
            webSocketService.sendMessage(message);
        } catch (Exception err) {
            log.debug("bikeInfoServer发生异常", err);
        }
    }

    private void ridingRouteServer(String mobileNo) {
        try {
            RideDto rideDto = rideService.findRidingByMobileNo(mobileNo);
            if (rideDto == null) {
                return;
            }
            RideTrackDto rideTrackDto = rideTrackService.getOneWithCursor(rideDto.getRideRecordId());
            long start;
            if (rideTrackDto == null) {
                start = rideDto.getStartTime().getTime();
            } else {
                start = rideTrackDto.getTimestamp();
            }
            if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) < 50) {
                return;
            }
            rideTrackService.insertRideTracks(rideDto.getRideRecordId());
            List<RideTrackDto> rideTracks = rideTrackService.getRideTrackAfter(rideDto.getRideRecordId(), start);
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
            RideTrackDto last = rideTracks.get(rideTracks.size() - 1);
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
