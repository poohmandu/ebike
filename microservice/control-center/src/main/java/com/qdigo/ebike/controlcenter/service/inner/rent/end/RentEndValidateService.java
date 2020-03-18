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
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.rideforceend.ForceEndInfo;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.ConsumeDetail;
import com.qdigo.ebike.api.domain.dto.order.wxscore.WxscoreDto;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.service.bike.BikeLocService;
import com.qdigo.ebike.api.service.order.ride.RideForceEndService;
import com.qdigo.ebike.api.service.order.ride.RideFreeActivityService;
import com.qdigo.ebike.api.service.order.wxscore.OrderWxscoreBizService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.common.core.util.Ctx;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.controlcenter.domain.dto.rent.EndDTO;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PGPackage;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PHPackage;
import com.qdigo.ebike.controlcenter.service.inner.command.DeviceService;
import com.qdigo.ebike.controlcenter.service.inner.datagram.PGMongoService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2017/11/16.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RentEndValidateService {

    private final RedisTemplate<String, String> redisTemplate;
    private final AtStationService atStationService;
    private final BikeLocService bikeLocService;
    private final OrderWxscoreBizService wxscoreBizService;
    private final LockBikeService lockBikeService;
    private final DeviceService deviceService;
    private final PGMongoService pgMongoService;
    private final RideFreeActivityService freeActivityService;
    private final RideForceEndService forceEndService;

    @AllArgsConstructor
    public static abstract class ForceEndValidateResult {
        private EndDTO endDTO;

        abstract ResponseDTO<EndDTO> forceEnd();

        abstract ResponseDTO<EndDTO> notForceEnd();
    }

    public ResponseDTO<EndDTO> forceEndValidate(ForceEndValidateResult forceEndValidateResult) {
        EndDTO endDTO = forceEndValidateResult.endDTO;
        if (endDTO.isForceEnd()) {

            val forceEndParam = RideForceEndService.Param.builder().agentId(endDTO.getRideDto().getAgentId())
                    .statusDto(endDTO.getBikeStatusDto()).build();
            ForceEndInfo forceEndInfo = forceEndService.getForceEndInfo(forceEndParam);
            log.debug("还车点外强制还车时信息:{}", forceEndInfo);
            if (!forceEndInfo.isValid()) {
                log.debug("强制还车失败:" + forceEndInfo.getCause());
                return new ResponseDTO<>(404, forceEndInfo.getCause());
            }
            endDTO.getOut().setForceEndInfo(forceEndInfo);
            return forceEndValidateResult.forceEnd();
        } else {
            return forceEndValidateResult.notForceEnd();
        }

    }

    //@CatAnnotation
    public ResponseDTO<EndDTO> accountValidate(EndDTO endDTO) {
        RideDto rideDto = endDTO.getRideDto();
        AgentCfg config = endDTO.getAgentCfg();
        UserAccountDto account = endDTO.getUserAccountDto();

        double allowArrears = config.getAllowArrears();
        if (allowArrears < 0) {
            return new ResponseDTO<>(200, "账户验证通过");
        }
        String key = Keys.flagArrearsCharge.getKey(endDTO.getMobileNo());
        String val = redisTemplate.opsForValue().get(key);
        if (val != null && TimeUnit.MILLISECONDS.toMinutes(Ctx.now() - Long.parseLong(val)) < Const.arrearsChargeMinutes) {
            log.debug("获得用户已经充值欠款的标志");
            return new ResponseDTO<>(200, "账户验证通过");
        }

        WxscoreDto wxscoreDto = wxscoreBizService.hasRideWxscoreOrder(rideDto.getRideRecordId());
        if (wxscoreDto != null) {
            log.debug("微信支付分渠道用户直接通过账户验证");
            return new ResponseDTO<>(200, "账户验证通过");
        }

        //获取消费详情
        RideFreeActivityService.DetailParam param = RideFreeActivityService.DetailParam.builder()
                .accountDto(account).agentCfg(config).rideDto(rideDto).userDto(endDTO.getUserDto()).build();
        ConsumeDetail consumeDetail = freeActivityService.getConsumeDetail(param);
        endDTO.getOut().setConsumeDetail(consumeDetail);

        double consume = consumeDetail.getConsume();
        double finalConsume = consume;
        if (endDTO.isForceEnd()) {
            val forceEndParam = RideForceEndService.Param.builder().agentId(config.getAgentId())
                    .statusDto(endDTO.getBikeStatusDto()).build();
            ForceEndInfo forceEndInfo = forceEndService.getForceEndInfo(forceEndParam);
            if (forceEndInfo.isValid())
                finalConsume += forceEndInfo.getAmount();
        }

        log.debug("验证用户账户是否具有还车资格,allowArrears:{},balance:{},giftBalance:{},consume:{},finalConsume:{}",
                allowArrears, account.getBalance(), account.getGiftBalance(), consume, finalConsume);

        //赠送余额只能消费骑行费用;且getConsumeDetail已计算giftBalance
        if (FormatUtil.getMoney(account.getBalance() - finalConsume + allowArrears) < 0) {
            EndDTO.EndOrderTipDTO orderTipDTO = EndDTO.EndOrderTipDTO.builder()
                    .accountBalance(FormatUtil.getMoney(account.getBalance()))
                    .allowArrears(FormatUtil.getMoney(allowArrears))
                    .finalConsume(FormatUtil.getMoney(finalConsume)).build();
            endDTO.getOut().setOrderTipDTO(orderTipDTO);
            return new ResponseDTO<>(405, "账户余额不足", endDTO);
        }
        return new ResponseDTO<>(200, "账户验证通过");


    }

    //@CatAnnotation
    public ResponseDTO<EndDTO> atStationValidate(EndDTO endDTO) {
        val lat = endDTO.getLatitude();
        val lng = endDTO.getLongitude();
        val rideDto = endDTO.getRideDto();
        val mobileNo = endDTO.getMobileNo();
        val imei = rideDto.getImei();
        val deviceMode = endDTO.getDeviceMode();

        val bikeAtStation = atStationService.atStation(endDTO);

        log.debug("user:{},bike:{}通过模式{},还车时是否在还车点:{}", mobileNo, imei, deviceMode, bikeAtStation);
        if (bikeAtStation == null) {
            bikeLocService.insertBikeLoc(imei, mobileNo, BikeLocService.LBSEvent.notAtStation, lat, lng, rideDto.getAgentId());
            return new ResponseDTO<>(401, "不在还车点,请到还车点还车");
        } else {
            endDTO.getOut().setStationId(bikeAtStation);
            bikeLocService.insertBikeLoc(imei, mobileNo, BikeLocService.LBSEvent.end, lat, lng, rideDto.getAgentId());
            return new ResponseDTO<>(200, "还车点验证通过");
        }
    }

    //@CatAnnotation
    public ResponseDTO<EndDTO> endBikeValidate(EndDTO endDTO) {
        val rideRecord = endDTO.getRideDto();
        val mobileNo = rideRecord.getMobileNo();
        val deviceMode = endDTO.getDeviceMode();
        val lat = endDTO.getLatitude();
        val lng = endDTO.getLongitude();

        val imei = rideRecord.getImei();
        val endBike = lockBikeService.endBike(endDTO);
        log.debug("user:{},bike{},{}模式还车操作是否成功:{}", mobileNo, imei, deviceMode, endBike);
        if (!endBike) {
            // 失败两种原因：1、网络gps故障 2、轮子在转
            PGPackage pg = endDTO.getPgPackage();
            if (pg != null) {
                PHPackage ph = deviceService.getHearBeat(imei, mobileNo);
                if (ph == null) {
                    PGPackage pgPackage = pgMongoService.pgNotFound(imei);
                    if (pgPackage != null && pgPackage.getTimestamp() > Ctx.now() && pgPackage.getPgLocked() == 1) {
                        log.debug("{}用户的{}车辆还车失败,但是还车开始时PG包已上锁", mobileNo, imei);
                        return new ResponseDTO<>(200, "设备锁车验证通过");
                    } else if (lockBikeService.confirmByPC(imei)) {
                        return new ResponseDTO<>(200, "设备锁车验证通过");
                    } else {
                        bikeLocService.insertBikeLoc(imei, mobileNo, BikeLocService.LBSEvent.timeout, lat, lng, rideRecord.getAgentId());
                        log.debug("{}用户的{}车辆还车失败,请再试一次:获取心跳失败", mobileNo, imei);
                        return new ResponseDTO<>(403, "还车失败,请再试一次。\n如果多次还车失败，请联系客服热线");
                    }
                } else if (ph.getPhWheelInput() == 1) {
                    log.debug("{}用户的{}车辆请在车辆静止后还车", mobileNo, imei);
                    return new ResponseDTO<>(403, "请让车辆静止后再还车");
                } else if (ph.getPhLocked() == 0) {
                    bikeLocService.insertBikeLoc(imei, mobileNo, BikeLocService.LBSEvent.timeout, lat, lng, rideRecord.getAgentId());
                    log.debug("{}用户的{}车辆还车失败,请再试一次:上锁位为0", mobileNo, imei);
                    return new ResponseDTO<>(403, "还车失败,请再试一次。\n如果多次还车失败，请联系客服热线");
                } else {
                    log.debug("{}用户的{}车辆没有还车成功仍然结束订单", mobileNo, imei);
                }
            } else {
                bikeLocService.insertBikeLoc(imei, mobileNo, BikeLocService.LBSEvent.pgNotFound, lat, lng, rideRecord.getAgentId());
                log.debug("user:{},bike:{},因为PG包断了所以结束订单", mobileNo, imei);
            }
            return new ResponseDTO<>(200, "设备锁车验证通过");
        } else {
            return new ResponseDTO<>(200, "设备锁车验证通过");
        }
    }

}
