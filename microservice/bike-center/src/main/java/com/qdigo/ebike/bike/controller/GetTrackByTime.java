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

package com.qdigo.ebike.bike.controller;

import brave.httpclient.TracingHttpClientBuilder;
import com.qdigo.ebike.api.domain.dto.control.Location;
import com.qdigo.ebike.api.service.control.TrackService;
import com.qdigo.ebike.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by niezhao on 2016/12/8.
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/geo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetTrackByTime {

    private final TrackService trackService;

    /**
     *  获得一整天的轨迹
     * @param imei
     * @return
     * @throws ParseException
     *
     */
    @GetMapping(value = "/getTrackByTime/{imei}", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getTrackByTime(@PathVariable String imei) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        Date start = df.parse(dateStr + " 00:00:00");
        Date end = df.parse(dateStr + " 23:59:59");

        List<Location> track = trackService.getTrackByPeriod(imei, start, end);
        TracingHttpClientBuilder.create().build();
        return R.ok(200, "成功获得当天轨迹", track);
    }

}
