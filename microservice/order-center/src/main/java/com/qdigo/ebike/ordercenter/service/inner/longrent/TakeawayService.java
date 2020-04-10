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

package com.qdigo.ebike.ordercenter.service.inner.longrent;

import com.qdigo.ebike.api.domain.dto.agent.AgentTakeawayConfigDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.agent.AgentTakeawayConfigService;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.api.service.order.longrent.OrderLongRentService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.common.core.constants.BikeCfg;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TakeawayService {

    private final BikeService bikeService;
    private final OrderRideService rideService;
    private final OrderLongRentService longRentService;
    private final AgentTakeawayConfigService takeawayConfigService;

    public ResponseDTO validateTakeawayService(UserDto user, Double balance, String deviceId, Long id, Double price) {
        BikeDto bike = bikeService.findByDeviceId(deviceId);
        if (bike == null) {
            return new ResponseDTO<>(403, "绑定车辆不存在");
        }
        if (bike.isDeleted() || !bike.isOnline()) {
            return new ResponseDTO<>(403, "绑定车辆已下或未运营");
        }
        if (bike.getOperationType() != BikeCfg.OperationType.takeaway) {
            return new ResponseDTO<>(403, "该车辆运营类型不是外卖类型");
        }
        String imeiId = bike.getImeiId();

        if (rideService.findByImeiAndMobileNo(imeiId, user.getMobileNo()) != null) {
            return new ResponseDTO<>(403, "用户或者车辆正在使用状态");
        }
        if (longRentService.hasLongRent(user.getUserId())) {
            return new ResponseDTO<>(403, "该用户之前已经购买过其他骑行卡");
        }
        AgentTakeawayConfigDto config = takeawayConfigService.findById(id);
        if (config == null) {
            return new ResponseDTO<>(403, "该外卖卡骑行不存在");
        }
        double shouldBe = config.getPrice();
        if (balance < 0) {
            shouldBe += Math.abs(balance);
        }
        if (shouldBe != price) {
            return new ResponseDTO<>(403, "支付金额不正确,应为" + shouldBe + "元");
        }
        return new ResponseDTO(200);
    }



}
