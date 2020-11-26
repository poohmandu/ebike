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

package com.qdigo.ebike.api.service.control;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.control.Location;
import com.qdigo.ebike.common.core.util.geo.GeoUtil;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 
 * date: 2020/1/15 12:29 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "control-center", contextId = "track")
public interface TrackService {

    @PostMapping(ApiRoute.ControlCenter.Track.getTrackByPeriod)
    List<Location> getTrackByPeriod(@RequestParam("imei") String imei, @RequestParam("start") Date start, @RequestParam("end") Date end);

    @PostMapping(ApiRoute.ControlCenter.Track.getMoveTrackByTime)
    List<Location> getMoveTrackByTime(@RequestParam("imei") String imei, @RequestParam("start") Date start, @RequestParam("end") Date end);

    @PostMapping(ApiRoute.ControlCenter.Track.getLocationByTime)
    Location getLocationByTime(@RequestParam("imei") String imei, @RequestParam("timestamp") long timestamp);

    default List<Location> filterPoints(List<Location> points) {
        if (!points.isEmpty()) {
            Location point = new Location()
                    .setPgLatitude(points.get(0).getPgLatitude())
                    .setPgLongitude(points.get(0).getPgLongitude());
            List<Location> list = points.stream().filter(dto -> {
                double lat = dto.getPgLatitude();
                double lng = dto.getPgLongitude();
                double distanceForMeter = GeoUtil.getDistanceForMeter(point.getPgLatitude(), point.getPgLongitude(), lat, lng);
                point.setPgLatitude(lat).setPgLongitude(lng);
                return distanceForMeter > 8; //8米的gps漂移
            }).collect(Collectors.toList());
            if (list.size() > 1) {
                return list;
            } else {
                list.add(points.get(0));
                list.add(points.get(points.size() - 1));
                return list;
            }
        } else {
            return null;
        }
    }

    /**
     * 使轨迹看起来更平滑,每5个点求平均
     *
     * @param points
     * @return
     */
    default List<Location> averagePoints(List<Location> points) {
        if (!points.isEmpty()) {
            int c = 5;
            Location point;
            List<Location> list = new ArrayList<>();
            int count = 0;
            for (int i = 0; i < points.size(); i++, count++) {
                point = new Location();
                double lng = 0;
                double lat = 0;
                if (i < c) {
                    for (int j = 0; j <= i; j++) {
                        lng += points.get(j).getPgLongitude();
                        lat += points.get(j).getPgLatitude();
                    }
                    lng /= (i + 1);
                    lat /= (i + 1);
                    point.setPgLatitude(lat).setPgLongitude(lng).setTimestamp(points.get(i).getTimestamp());
                    list.add(point);
                } else {
                    for (int j = 0; j < c; j++) {
                        lng += points.get(i - (c - 1) + j).getPgLongitude();
                        lat += points.get(i - (c - 1) + j).getPgLatitude();
                    }
                    lng /= c;
                    lat /= c;
                    point.setPgLatitude(lat).setPgLongitude(lng).setTimestamp(points.get(i).getTimestamp());
                    list.add(point);
                }
            }
            if (list.size() > 1) {
                return list;
            } else {
                list.add(points.get(0));
                list.add(points.get(points.size() - 1));
                return list;
            }
        } else {
            return points;
        }
    }

}
