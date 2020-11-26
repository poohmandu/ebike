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

package com.qdigo.ebike.controlcenter.listener.rent;

import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.agent.AgentDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.activity.invite.InviteService;
import com.qdigo.ebike.api.service.agent.AgentService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.api.service.order.wxscore.OrderWxscoreBizService;
import com.qdigo.ebike.api.service.user.UserRecordService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.commonconfig.configuration.ThreadPool;
import com.qdigo.ebike.controlcenter.domain.dto.rent.StartDto;
import com.qdigo.ebike.controlcenter.service.inner.command.DeviceService;
import com.qdigo.ebike.controlcenter.service.inner.rent.start.InsuranceBizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2018/1/26.
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ScanBikeEventListener {

    private final InviteService inviteService;
    private final DeviceService deviceService;
    private final UserRecordService userRecordService;
    private final OrderRideService rideService;
    private final InsuranceBizService insuranceBizService;
    private final AgentService agentService;
    private final OrderWxscoreBizService wxscoreBizService;


    @Async
    @EventListener
    public void onApplicationEvent(ScanSuccessEvent event) {
        try {
            StartDto startDto = event.getStartDto();
            RideDto rideDto = event.getRideDto();
            UserDto user = startDto.getUserDto();
            UserAccountDto accountDto = startDto.getUserAccountDto();
            BikeDto bikeDto = startDto.getBikeDto();
            AgentCfg config = startDto.getAgentCfg();
            String mobileNo = user.getMobileNo();
            String imei = bikeDto.getImeiId();
            MDC.put("mobileNo", mobileNo);

            long rideRecordId = rideDto.getRideRecordId();

            //(3) 微信支付分订单逻辑,rideRecord提交后
            val wxsocreStart = new OrderWxscoreBizService.WxsocreStart().setRideDto(rideDto)
                    .setWxscoreEnable(accountDto.getWxscore()).setAgentCfg(config).setUserDto(user);
            wxscoreBizService.startWxscoreOrder(wxsocreStart);

            // 1.用户记录扫码
            userRecordService.insertUserRecord(user.getUserId(), "扫码成功,拥有了对设备" + bikeDto.getDeviceId() + "的控制权");

            // 2.完成邀请闭环
            inviteService.finishInvite(user);

            // 3.第一次骑行限速
            if (config.isSpeedLimit()) {
                ThreadPool.scheduledThreadPool().schedule(() -> {
                    RideDto record = rideService.findById(rideRecordId);
                    if (record != null && (record.getRideStatus() == Status.RideStatus.running.getVal() || record.getRideStatus() == Status.RideStatus.invalid.getVal())) {
                        log.debug("user{},bike{}延时触发,设置高档位", mobileNo, imei);
                        boolean gear = deviceService.highGear(imei, mobileNo);
                        log.debug("user{},bike{}延时触发,第一次是否成功设置高档位:{}", mobileNo, imei, gear);
                        gear = gear || deviceService.highGear(imei, mobileNo);
                        log.debug("user{},bike{}延时触发,第二次是否成功设置高档位:{}", mobileNo, imei, gear);
                    } else {
                        log.debug("user{},bike{}不用设置高档位", mobileNo, imei);
                    }
                }, Const.highGearDelaySeconds, TimeUnit.SECONDS);
            }

            // 4.免费时间过后
            int freeSeconds = config.getFreeSeconds();
            if (freeSeconds > 0) {
                ThreadPool.scheduledThreadPool().schedule(() -> {
                    log.debug("扫码后,延时触发,骑行记录为{},time:{}", rideRecordId, freeSeconds);
                    this.freeEndProcess(rideRecordId, config, bikeDto, user);
                }, freeSeconds, TimeUnit.SECONDS);
            } else {
                log.debug("freeSeconds小于0,骑行记录直接有效:{}", rideRecordId);
                this.freeEndProcess(rideRecordId, config, bikeDto, user);
            }

        } finally {
            MDC.remove("mobileNo");
        }

    }

    private void freeEndProcess(long rideRecordId, AgentCfg agentCfg, BikeDto bikeDto, UserDto userDto) {
        RideDto record = rideService.findById(rideRecordId);
        if (record != null && record.getRideStatus() == Status.RideStatus.invalid.getVal() && record.getRideStatus() != Status.RideStatus.end.getVal()) {
            //将订单状态变为不免费
            record.setRideStatus(Status.RideStatus.running.getVal());
            rideService.updateRideOrder(record);
            rideService.updateRideRecord(record);

            log.debug("rideRecord{}的status置为{}", record.getRideRecordId(), record.getRideStatus());
            //订单开始时的时候，调用保险接口生成保单
            if (insuranceBizService.validateInsurance(userDto.getMobileNo())) {
                AgentDto agentDto = agentService.findById(agentCfg.getAgentId());
                insuranceBizService.createInsurance(rideRecordId, agentCfg, agentDto, userDto, bikeDto);
            }
        }

    }


}
