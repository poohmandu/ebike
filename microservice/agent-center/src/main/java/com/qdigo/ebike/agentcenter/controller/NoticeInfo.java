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

package com.qdigo.ebike.agentcenter.controller;

import com.qdigo.ebike.agentcenter.domain.entity.AgentNotice;
import com.qdigo.ebike.agentcenter.service.AgentNoticeService;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by niezhao on 2017/12/24.
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/ebike/notice")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NoticeInfo {

    private final AgentNoticeService noticeService;
    private final UserService userService;

    @AccessValidate
    @GetMapping(value = "/getLastNotice", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getLastNotice(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {
        UserDto userDto = userService.findByMobileNo(mobileNo);
        AgentNotice agentNotice = noticeService.getLastNotice(userDto.getAgentId());
        Res res = null;
        if (agentNotice != null) {
            res = new Res();
            res.setTitle(agentNotice.getTitle());
            res.setContent(agentNotice.getContent());
            res.setType(agentNotice.getType().name());
            String url = agentNotice.getRedirectUrl();
            if (StringUtils.isNotEmpty(url)) {
                res.setRedirectUrl(url + "?mobileNo=" + mobileNo);
            }
        }

        return R.ok(200, "获得最新公告", res);
    }

    @AccessValidate
    @GetMapping(value = "/getValidNotice", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getValidNotice(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {
        UserDto userDto = userService.findByMobileNo(mobileNo);
        List<Res> resList = noticeService.getValidNotices(userDto.getAgentId()).stream()
                .map(agentNotice -> {
                    Res res = new Res();
                    res.setTitle(agentNotice.getTitle());
                    res.setContent(agentNotice.getContent());
                    res.setType(agentNotice.getType().name());
                    String url = agentNotice.getRedirectUrl();
                    if (StringUtils.isNotEmpty(url)) {
                        res.setRedirectUrl(agentNotice.getRedirectUrl() + "?mobileNo=" + mobileNo);
                    }
                    return res;
                }).collect(Collectors.toList());

        return R.ok(200, "获得最新公告", resList);
    }

    @AccessValidate
    @GetMapping(value = "/getNoticeList", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getNoticeList(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        UserDto userDto = userService.findByMobileNo(mobileNo);
        List<Res> resList = noticeService.getNoticeList(userDto.getAgentId()).stream()
                .map(agentNotice -> {
                    Res res = new Res();
                    res.setTitle(agentNotice.getTitle());
                    res.setContent(agentNotice.getContent());
                    res.setRedirectUrl(agentNotice.getRedirectUrl());
                    res.setStartTime(agentNotice.getStartTime());
                    res.setEndTime(agentNotice.getEndTime());
                    res.setType(agentNotice.getType().name());
                    return res;
                }).collect(Collectors.toList());

        return R.ok(200, "获得最新公告", resList);
    }

    @Data
    private static class Res {
        private String title;
        private String content;
        private String redirectUrl;
        private Date startTime;
        private Date endTime;
        private String type;
    }

}
