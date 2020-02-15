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

package com.qdigo.ebike.usercenter.controller;

import com.qdigo.ebike.api.domain.dto.PageDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.common.core.util.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1.0/userInfo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AccountInfo {

    private final UserRepository userRepository;
    private final OrderRideService rideService;

    /**
     * 获取账单列表
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @return
     */
    @AccessValidate
    @GetMapping(value = "/getAccounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getAccounts(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        List<Res> resList = new ArrayList<>();

        rideService.findEndByMobileNo(mobileNo).stream()
                .sorted((e1, e2) -> e2.getStartTime().compareTo(e1.getStartTime()))
                .forEach(rideRecord -> resList.add(Res.build(rideRecord)));

        return R.ok(200, "成功获取用户账单列表", resList);
    }

    @AccessValidate
    @GetMapping(value = "/getAccountsPage", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getAccountsPage(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @PageableDefault(size = 5, sort = {"rideRecordId"}, direction = Sort.Direction.DESC) Pageable pageable) {

        //Pageable pageable = new PageRequest(body.getIndex(), body.getSize(), Sort.Direction.DESC, "rideRecordId");
        PageDto<RideDto> page = rideService.findEndPageByMobileNo(mobileNo, pageable);

        Page<Res> res = page.toPage(pageable).map(Res::build);
        return R.ok(200, "成功获取用户账单列表", res);
    }

    @Getter
    @Setter
    @Builder
    private static class Res {
        private long orderId;
        private int orderStatus;
        private String startTime;
        private String endTime;
        private int minutes;
        private double consume;

        static Res build(RideDto rideRecord) {
            return Res.builder().consume(rideRecord.getConsume())
                    .startTime(FormatUtil.yMdHms.format(rideRecord.getStartTime()))
                    .endTime(FormatUtil.yMdHms.format(rideRecord.getEndTime()))
                    .minutes(FormatUtil.minutes(Duration.between(rideRecord.getStartTime().toInstant(),
                            rideRecord.getEndTime().toInstant()).getSeconds()))
                    .orderId(rideRecord.getRideRecordId())
                    .orderStatus(rideRecord.getRideStatus())
                    .build();
        }
    }


}
