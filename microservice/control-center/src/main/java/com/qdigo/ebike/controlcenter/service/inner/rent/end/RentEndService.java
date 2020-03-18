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

package com.qdigo.ebike.controlcenter.service.inner.rent.end;


import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.ConsumeDetail;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.api.service.order.ride.RideFreeActivityService;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.common.core.errors.exception.QdigoBizException;
import com.qdigo.ebike.controlcenter.domain.dto.rent.EndDTO;
import com.qdigo.ebike.controlcenter.domain.dto.rent.EndResponse;
import com.qdigo.ebike.controlcenter.listener.rent.EndBikeSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationContext;
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
public class RentEndService {

    private final RentEndValidateService endValidateService;
    private final OrderRideService rideService;
    private final BikeStatusService bikeStatusService;
    private final AgentConfigService agentConfigService;
    private final UserAccountService userAccountService;
    private final UserService userService;
    private final RideFreeActivityService freeActivityService;


    private final ApplicationContext context;

    //@CatAnnotation
    @Transactional
    //@RetryOnOptimistic //乐观锁的开始和事务的开始要一致,所以rideRecord要重新查询 (刷新version)
    public EndResponse endRideRecord(EndDTO endDTO, boolean isGPS) throws QdigoBizException {
        val rideDto = endDTO.getRideDto();
        val rideRecordId = rideDto.getRideRecordId();
        log.debug("开始结束骑行订单:{}", rideRecordId);

        //(1) 获取消费详情
        ConsumeDetail consumeDetail = endDTO.getOut().getConsumeDetail();
        //(2) 保存消费减免信息
        rideActivityService.createRideFreeActivities(consumeDetail.getFreeActivities());
        //(3) 完成消费
        rideRecordService.finishConsume(endDTO, consumeDetail);
        //（3）更新骑行订单:rideRecord、rideRoute、coupon、userAccount、journalAccount
        rideRecordService.finishRide(endDTO, consumeDetail, isGPS);
        // (5) 更新车辆状态
        bikeStatusDaoService.updateStatus(bikeStatus.getBikeStatusId(), Status.BikeLogicStatus.available);

        EndResponse endResponse = EndResponse.build(rideRecord, consumeDetail.getConsumeNote());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                log.debug("完成相关数据库更新,准备包装返回,并提交事务");
                context.publishEvent(new EndBikeSuccessEvent(this, rideRecord, endResponse));
            }

            @Override
            public void afterCompletion(int status) {
                if (STATUS_ROLLED_BACK == status) {
                    log.debug("事务需要回滚");
                }
            }
        });

        return endResponse;
    }

    public EndDTO getEndInfo(String mobileNo, String inputNumber, double longitude, double latitude,
                             double accuracy, String provider, boolean ble, boolean forceEnd) {
        RideDto rideDto = rideService.findRidingByMobileNo(mobileNo);
        UserDto userDto = userService.findByMobileNo(mobileNo);
        UserAccountDto accountDto = userAccountService.findByUserId(userDto.getUserId());

        AgentCfg agentCfg = null;
        BikeStatusDto bikeStatusDto = null;
        if (rideDto != null) {
            agentCfg = agentConfigService.findByImei(rideDto.getImei());
            bikeStatusDto = bikeStatusService.findByImei(rideDto.getImei());
        }
        return EndDTO.builder()
                .mobileNo(mobileNo)
                .provider(provider)
                .accuracy(accuracy)
                .forceEnd(forceEnd)
                .inputNumber(inputNumber)
                .latitude(latitude)
                .longitude(longitude)
                .deviceMode(ble ? Const.DeviceMode.BLE : Const.DeviceMode.GPS_SMS)
                .rideDto(rideDto)
                .agentCfg(agentCfg)
                .bikeStatusDto(bikeStatusDto)
                .userDto(userDto)
                .userAccountDto(accountDto)
                .build();
    }


    //@Transactional
    //@CatAnnotation
    public ResponseDTO<EndDTO> appEndValidate(
            String mobileNo, String imeiIdOrDeviceId, double longitude, double latitude,
            double accuracy, String provider, boolean ble, boolean forceEnd) {

        EndDTO endInfo = this.getEndInfo(mobileNo, imeiIdOrDeviceId, longitude, latitude, accuracy, provider, ble, forceEnd);

        if (endInfo.getRideDto() == null) {
            return new ResponseDTO<>(400, "用户已不在借车状态中");
        } else {
            return this.endValidate(endInfo);
        }
    }

    //@Transactional
    public ResponseDTO<EndDTO> buttonEndValidate(String imei) {
        RideRecord rideRecord = rideRecordDao.findRecordByRidingBike(imei);
        log.debug("车辆:{}通过按钮还车", imei);
        if (rideRecord == null) {
            log.debug("bike:{}已经还过车了", imei);
            return new ResponseDTO<>(400, "该车已经还车");
        } else {
            val endDTO = EndDTO.builder()
                    .longitude(0)
                    .latitude(0)
                    .imeiIdOrDeviceId(imei)
                    .forceEnd(false)
                    .deviceMode(Const.DeviceMode.GPS)
                    .accuracy(-1)
                    .mobileNo(rideRecord.getUser().getMobileNo())
                    .rideRecord(rideRecord).build();
            return this.endValidate(endDTO);
        }
    }

    public ResponseDTO<EndDTO> autoEndValidate(final String imei) {
        //因为 bike 队列中可能已经等待了很久，到来时bike已经改变了

        return null;
        //
        //val bike = bikeRepository.findByImeiId(imei).orElseThrow(() -> new NullPointerException("没有imei为" + imei + "的车辆"));
        //val bikeStatus = bike.getBikeStatus();
        //val record = rideRecordDao.findByRidingBike(bike);
        //log.debug("bike:{}自动还车时,bikeStatus.status:{}", imei, bikeStatus.getStatus());
        //if (record == null) {
        //    return "重复自动还车了";
        //}
        //if (record.getRideStatus() != Status.RideStatus.running.getVal()) {
        //    return "车辆还未进入计费期";
        //}
        //val user = record.getUser();
        //val mobileNo = user.getMobileNo();
        //
        //try {
        //    MDC.put("mobileNo", mobileNo);
        //
        //    val endBike = bikeService.endBike(record, Const.DeviceMode.GPS);
        //
        //    log.debug("user:{},bike:{}自动还车时,是否断电并上锁成功:{}", mobileNo, imei, endBike);
        //    if (!endBike) {
        //        return "断电上锁失败";
        //    }
        //    rentEndService.appEndValidate();
        //    val gpsStatus = bike.getGpsStatus();
        //    if (gpsStatus.getLocked() != 1) {
        //        return "检查到GPS状态并没有上锁";
        //    }
        //    EndDTO endDTO = EndDTO.builder()
        //        .rideRecord(record)
        //        .latitude(bikeStatus.getLatitude())
        //        .longitude(bikeStatus.getLongitude())
        //        .forceEnd(false)
        //        .build();
        //
        //    rentEndService.endRideRecord(endDTO, true);
        //    Map<String, Object> map = ImmutableMap.of("deviceId", bike.getDeviceId(), "consume", record.getConsume());
        //    pushService.pushNotation(user, alert, Const.PushType.autoReturn, map);
        //    return "成功";
        //
        //} finally {
        //    MDC.clear();
        //}

    }

    private ResponseDTO<EndDTO> endValidate(EndDTO endDTO) {
        val mobileNo = endDTO.getMobileNo();
        val imei = endDTO.getRideDto().getImei();
        val deviceMode = endDTO.getDeviceMode();

        log.debug("用户{}归还车辆{}时,硬件还车模式:{},gcj02高德坐标:({},{}),forceEnd={}", mobileNo, imei,
                deviceMode, endDTO.getLongitude(), endDTO.getLatitude(), endDTO.isForceEnd());

        return endValidateService.forceEndValidate(new RentEndValidateService.ForceEndValidateResult(endDTO) {

            @Override
            ResponseDTO<EndDTO> forceEnd() {
                ResponseDTO<EndDTO> responseDTO = endValidateService.accountValidate(endDTO);
                if (responseDTO.isNotSuccess()) {
                    return responseDTO;
                }
                responseDTO = endValidateService.endBikeValidate(endDTO);
                if (responseDTO.isNotSuccess()) {
                    return responseDTO;
                }
                return new ResponseDTO<>(200, "成功还车", endDTO);
            }

            @Override
            ResponseDTO<EndDTO> notForceEnd() {
                ResponseDTO<EndDTO> responseDTO = endValidateService.atStationValidate(endDTO);
                if (responseDTO.isNotSuccess()) {
                    return responseDTO;
                }
                responseDTO = endValidateService.accountValidate(endDTO);
                if (responseDTO.isNotSuccess()) {
                    return responseDTO;
                }
                responseDTO = endValidateService.endBikeValidate(endDTO);
                if (responseDTO.isNotSuccess()) {
                    return responseDTO;
                }
                return new ResponseDTO<>(200, "成功还车", endDTO);
            }

        });
    }


}
