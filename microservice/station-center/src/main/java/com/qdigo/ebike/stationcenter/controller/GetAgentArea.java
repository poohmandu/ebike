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

package com.qdigo.ebike.stationcenter.controller;

import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.stationcenter.domain.entity.AgentArea;
import com.qdigo.ebike.stationcenter.repository.AgentAreaRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/v1.0/geo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetAgentArea {

    private final UserService userService;
    private final AgentAreaRepository agentAreaRepository;

    @PostMapping(value = "/getAgentArea", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getAgentArea(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        UserDto userDto = userService.findByMobileNo(mobileNo);
        if (userDto == null || userDto.getAgentId() == null) {
            return R.ok(400, "该用户未匹配到代理商");
        }
        List<AgentArea> areas = agentAreaRepository.findByAgentId(userDto.getAgentId());

        List<Res> resList = new ArrayList<>();
        areas.forEach(agentArea -> {
            List<Res.Point> points = agentArea.getPoints().stream().map(agentAreaPoint -> Res.Point.builder()
                    .latitude(agentAreaPoint.getLatitude())
                    .longitude(agentAreaPoint.getLongitude()).build())
                    .collect(Collectors.toList());
            Res res = new Res();
            res.setPoints(points);
            resList.add(res);
        });

        return R.ok(200, "成功用户合法服务区", resList);
    }

    @Data
    private static class Res {
        private List<Point> points;

        @Data
        @Builder
        private static class Point {
            private double latitude;
            private double longitude;
        }
    }

}
