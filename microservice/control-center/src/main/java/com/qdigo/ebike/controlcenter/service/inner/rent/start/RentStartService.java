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

package com.qdigo.ebike.controlcenter.service.inner.rent.start;

import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.api.service.order.ride.RideBizService;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.controlcenter.domain.dto.rent.StartDto;
import com.qdigo.ebike.controlcenter.listener.rent.ScanSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.inject.Inject;

/**
 * Created by niezhao on 2017/6/24.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RentStartService {

    private final RentStartValidateService validateService;
    private final RedisTemplate<String, String> redisTemplate;
    private final AgentConfigService agentConfigService;
    private final BikeService bikeService;
    private final BikeStatusService statusService;
    private final UserService userService;
    private final UserAccountService accountService;
    private final ApplicationContext context;
    private final RideBizService rideBizService;


    public StartDto getStartInfo(String mobileNo, String num, double lat, double lng, boolean ble) {
        BikeDto bikeDto = bikeService.findByImeiOrDeviceId(num);
        BikeStatusDto bikeStatusDto = null;
        AgentCfg agentCfg = null;
        if (bikeDto != null) {
            bikeStatusDto = statusService.findStatusByBikeIId(bikeDto.getBikeId());
            agentCfg = agentConfigService.getAgentConfig(bikeDto.getAgentId());
        }
        UserDto userDto = userService.findByMobileNo(mobileNo);
        UserAccountDto accountDto = accountService.findByUserId(userDto.getUserId());

        return StartDto.builder().inputNumber(num).lat(lat).lng(lng).ble(ble)
                .agentCfg(agentCfg).bikeDto(bikeDto).bikeStatusDto(bikeStatusDto)
                .userDto(userDto).userAccountDto(accountDto).build();
    }


    //@Transactional
    //@CatAnnotation
    public ResponseDTO rentValidate(StartDto startDto) {

        ResponseDTO bikeDto = validateService.QRCodeValidate(startDto);
        if (bikeDto.isNotSuccess()) {
            return bikeDto;
        }
        ResponseDTO<String> lockScanKey = validateService.concurrencyValidate(startDto);
        if (lockScanKey.isNotSuccess()) {
            return lockScanKey;
        }
        try {
            bikeDto = validateService.bikeValidate(startDto);
            if (bikeDto.isNotSuccess()) {
                redisTemplate.delete(lockScanKey.getData());
                return bikeDto;
            }
            ResponseDTO userDto = validateService.userValidate(startDto);
            if (userDto.isNotSuccess()) {
                redisTemplate.delete(lockScanKey.getData());
                return userDto;
            }
            userDto = validateService.accountValidate(startDto);
            if (userDto.isNotSuccess()) {
                redisTemplate.delete(lockScanKey.getData());
                return userDto;
            }
            ResponseDTO<String> imeiDto = validateService.deviceValidate(startDto);
            if (imeiDto.isNotSuccess()) {
                redisTemplate.delete(lockScanKey.getData());
                return imeiDto;
            }
            return imeiDto;
        } catch (Exception e) {
            redisTemplate.delete(lockScanKey.getData());
            throw e;
        }
    }

    //跨服务情况下无效了
    //@RetryOnOptimistic //用在有事务的service里
    //@CatAnnotation
    @Transactional(rollbackFor = Throwable.class)
    public void startRent(StartDto startDto) {
        try {

            //(1) 创建骑行记录
            RideBizService.StartParam startParam = new RideBizService.StartParam().setBikeDto(startDto.getBikeDto())
                    .setBikeStatusDto(startDto.getBikeStatusDto()).setLat(startDto.getLat()).setLng(startDto.getLng())
                    .setUserDto(startDto.getUserDto());
            RideDto rideDto = rideBizService.createRide(startParam);

            //(2) 更新bike
            startDto.getBikeStatusDto().setStatus(Status.BikeLogicStatus.inUse.getVal());
            statusService.update(startDto.getBikeStatusDto());

            //(4) 扫码成功事件，异步触发
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    //(4) 扫码成功事件，异步触发
                    context.publishEvent(new ScanSuccessEvent(this, startDto, rideDto));
                }

                @Override
                public void afterCompletion(int status) {
                    log.debug("借车事务完成:{}", status);
                }
            });

        } finally {
            redisTemplate.delete(Keys.lockScanBike.getKey(startDto.getBikeDto().getImeiId()));
        }

    }

}
