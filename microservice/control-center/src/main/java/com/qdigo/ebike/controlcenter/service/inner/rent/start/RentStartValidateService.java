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
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.bike.BikeLocService;
import com.qdigo.ebike.api.service.bike.BikeStatusService;
import com.qdigo.ebike.api.service.order.longrent.OrderLongRentService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.api.service.user.UserBlackListService;
import com.qdigo.ebike.api.service.user.UserStatusService;
import com.qdigo.ebike.common.core.constants.BikeCfg;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.controlcenter.domain.dto.rent.StartDto;
import com.qdigo.ebike.controlcenter.service.inner.command.DeviceService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2017/9/7.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RentStartValidateService {

    @AllArgsConstructor
    public enum StatusCode {
        _200(200, "成功"), _400(400, "格式错误"), _401(401, "不存在该车"), _402(402, "车辆预约"), _403(403, "信用认证"),
        _404(404, "故障"), _406(406, "车辆使用中"), _407(407, "先还车"), _408(408, "余额不足"), _409(409, "退款"), _410(410, "多用户扫车"), _411(411, "外卖"), _412(412, "黑名单用户"), _413(413, "管理员下线"),
        _414(414, "电量告急"), _415(415, "微信订单待结算");
        public int val;
        public String desc;
    }

    private final BikeLocService bikeLocService;
    private final RedisTemplate<String, String> redisTemplate;
    private final OrderRideService rideService;
    private final UserStatusService userStatusService;
    private final UserBlackListService blackListService;
    private final OrderLongRentService longRentService;
    private final AgentConfigService agentConfigService;
    private final DeviceService deviceService;
    private final BikeStatusService bikeStatusService;

    //@CatAnnotation
    public ResponseDTO QRCodeValidate(StartDto startDto) {
        String num = startDto.getInputNumber();
        String mobileNo = startDto.getUserDto().getMobileNo();
        double lat = startDto.getLat();
        double lng = startDto.getLng();

        BikeDto bike = startDto.getBikeDto();
        if (bike == null) {
            return new ResponseDTO(StatusCode._400.val, "车辆标识格式错误");
        } else if (bike.getBikeId() == null) {
            return new ResponseDTO(StatusCode._401.val, "数据库不存在该车辆");
        } else {
            // 记录扫码位置
            if (num.length() == Const.imeiLength) {
                //独立更新事务,独立回滚
                bikeLocService.insertBikeLoc(bike.getImeiId(), mobileNo, BikeLocService.LBSEvent.scanImei, lat, lng, bike.getAgentId());
            } else if (num.length() == Const.deviceIdLength) {
                bikeLocService.insertBikeLoc(bike.getImeiId(), mobileNo, BikeLocService.LBSEvent.scanDeviceId, lat, lng, bike.getAgentId());
            }
            return new ResponseDTO(StatusCode._200.val, "根据二维码获得车辆");
        }
    }

    //@CatAnnotation
    public ResponseDTO<String> concurrencyValidate(StartDto startDto) {
        String imei = startDto.getBikeDto().getImeiId();
        String mobileNo = startDto.getUserDto().getMobileNo();
        val key = Keys.lockScanBike.getKey(imei);
        if (redisTemplate.hasKey(key)) {
            log.debug("用户{}扫码，车辆{}被重复扫码,ttl:{}s", mobileNo, imei, redisTemplate.getExpire(key, TimeUnit.SECONDS));
            return new ResponseDTO<>(StatusCode._410.val, "多个用户短时间内都在扫码该车,请过一会再扫");
        } else {
            log.debug("用户{}扫码，车辆{}没有重复扫码", mobileNo, imei);
            redisTemplate.opsForValue().set(key, FormatUtil.getCurTime(), 60, TimeUnit.SECONDS);
            return new ResponseDTO<>(StatusCode._200.val, "车辆无重复扫码", key);
        }
    }

    //@CatAnnotation
    public ResponseDTO bikeValidate(StartDto startDto) {
        BikeDto bike = startDto.getBikeDto();
        String mobileNo = startDto.getUserDto().getMobileNo();
        BikeStatusDto bikeStatus = startDto.getBikeStatusDto();
        AgentCfg config = startDto.getAgentCfg();

        double lat = startDto.getLat();
        double lng = startDto.getLng();
        String imei = bike.getImeiId();

        if (bikeStatus.getBattery() < config.getBatteryBan()) {
            bikeLocService.insertBikeLoc(imei, mobileNo, BikeLocService.LBSEvent.scanBattery, lat, lng, bike.getAgentId());
            return new ResponseDTO(StatusCode._414.val, "电量告急,请换车使用");
        } else if (!bike.isOnline()) {
            bikeLocService.insertBikeLoc(imei, mobileNo, BikeLocService.LBSEvent.scanOnline, lat, lng, bike.getAgentId());
            return new ResponseDTO().setStatusCode(StatusCode._413.val).setMessage("该车辆已被管理员下线,请换车使用");
        } else if (bikeStatus.getStatus() == Status.BikeLogicStatus.subscribe.getVal()) {
            return new ResponseDTO().setStatusCode(StatusCode._402.val).setMessage("该车辆已被预约");
        } else if (bikeStatus.getStatus() != Status.BikeLogicStatus.available.getVal() ||
                rideService.findRidingByImei(imei) != null) {
            bikeLocService.insertBikeLoc(imei, mobileNo, BikeLocService.LBSEvent.scanInUse, lat, lng, bike.getAgentId());
            log.debug("user:{},bike:{}该车辆正被使用中", mobileNo, bike.getImeiId());
            return new ResponseDTO(StatusCode._406.val, "该车辆正被使用中,请换车使用");
        } else {
            return new ResponseDTO(StatusCode._200.val, "该车逻辑上可用");
        }
    }

    //@CatAnnotation
    public ResponseDTO userValidate(StartDto startDto) {
        UserDto user = startDto.getUserDto();
        String mobileNo = user.getMobileNo();
        String imei = startDto.getBikeDto().getImeiId();

        RideDto rideOrder = rideService.findRidingByMobileNo(mobileNo);

        if (rideOrder != null) {//412
            log.debug("user:{},bike:{}您已开锁车辆,请先还车", mobileNo, imei);
            return new ResponseDTO(StatusCode._407.val, "您已开锁车辆,请先还车");
        } else {
            UserBlackListService.BlackListDto blackListDto = blackListService.findByUserId(user.getUserId());
            if (blackListDto != null) {
                return new ResponseDTO(StatusCode._412.val, "黑名单用户:" + blackListDto.getCause());
            }
            return new ResponseDTO(StatusCode._200.val, "该用户可用车");
        }
    }

    //@CatAnnotation
    public ResponseDTO accountValidate(StartDto startDto) {
        UserDto user = startDto.getUserDto();
        BikeDto bike = startDto.getBikeDto();
        val mobileNo = user.getMobileNo();
        val account = startDto.getUserAccountDto();
        val imei = bike.getImeiId();

        val stepParam = UserStatusService.StepParam.builder().userDto(user).userAccountDto(account).build();
        Status.Step step = userStatusService.getStep(stepParam);

        if (step == Status.Step.deposit) {
            log.debug("user:{},bike:{} 请先完成信用认证", mobileNo, imei);
            return new ResponseDTO<>(StatusCode._403.val, "请先完成信用认证");
        }

        if (account.getRefundStatus().equals(Status.RefundStatus.pending.getVal())) {
            log.debug("user:{},bike:{}退款正在受理中，不能借车", mobileNo, imei);
            return new ResponseDTO<>(StatusCode._409.val, "退款正在受理中，不能借车");
        }

        if (step == Status.Step.balance) {
            log.debug("user:{},bike:{}余额不足，请及时充值,余额:{}元,赠送余额:{}元", mobileNo, imei, account.getBalance(), account.getGiftBalance());
            return new ResponseDTO<>(StatusCode._408.val, "余额不足,请及时充值");
        }

        String outOrderNo = userStatusService.hasNoFinishedWxscore(user);
        if (outOrderNo != null) {
            return new ResponseDTO<>(StatusCode._415.val, "微信支付分有订单待结算", outOrderNo);
        }

        val longRentDto = longRentService.findValidByUserId(user.getUserId());

        // zmScoreEnable  hasLongRent
        if (longRentDto != null) {
            if (longRentDto.getLongRentType() == Const.LongRentType.takeaway) {
                if (!longRentDto.getImei().equals(imei)) {
                    return new ResponseDTO(StatusCode._411.val, "外卖车用户只能使用绑定的相应车辆");
                }
            } else {
                if (bike.getOperationType() == BikeCfg.OperationType.takeaway) {
                    return new ResponseDTO(StatusCode._411.val, "一般长租用户无法使用外卖专用车");
                }
                boolean noneMatch = agentConfigService.allowAgents(longRentDto.getAgentId()).stream()
                        .noneMatch(aLong -> aLong.equals(bike.getAgentId()));
                if (noneMatch) {
                    log.debug("user:{},bike:{}长租用户不能骑行其他运营商车辆", mobileNo, imei);
                    return new ResponseDTO(StatusCode._411.val, "长租用户不能骑行其他运营商车辆");
                }
            }
        } else if (bike.getOperationType() == BikeCfg.OperationType.takeaway) {
            return new ResponseDTO(StatusCode._411.val, "普通用户无法使用外卖专用车");
        }

        return new ResponseDTO(StatusCode._200.val, "用户账户可用");
    }

    //长事务
    //@Transactional
    //@CatAnnotation
    public ResponseDTO<String> deviceValidate(StartDto startDto) {
        UserDto user = startDto.getUserDto();
        BikeDto bike = startDto.getBikeDto();
        BikeStatusDto statusDto = startDto.getBikeStatusDto();
        double lat = startDto.getLat();
        double lng = startDto.getLng();
        boolean ble = startDto.isBle();
        val imei = bike.getImeiId();
        if (ble) {
            log.debug("用户可使用蓝牙模式借车,不验证GPS连通性");
            return new ResponseDTO<>(StatusCode._200.val, "蓝牙模式,扫码成功", imei);
        }
        //硬件操作，测试gps连通性
        val mobileNo = user.getMobileNo();

        if (deviceService.openBle(imei, mobileNo)) {
            log.debug("user:{},bike:{},打开蓝牙成功", mobileNo, imei);
            bikeStatusService.removeActualStatus(statusDto, Status.BikeActualStatus.cannotOps.getVal());
        } else {
            log.debug("user:{},bike:{},打开蓝牙失败", mobileNo, imei);
            bikeStatusService.setActualStatus(statusDto, Status.BikeActualStatus.cannotOps.getVal());
        }

        AgentCfg config = startDto.getAgentCfg();

        if (config.isSpeedLimit()) {
            //首次骑行设置低档位
            if (rideService.findAnyByMobileNo(mobileNo) == null) {
                val gear = deviceService.lowGear(imei, mobileNo);
            } else {
                val gear = deviceService.highGear(imei, mobileNo);
            }
        } else {
            deviceService.highGear(imei, mobileNo);
        }

        val actualStatus = bikeStatusService.queryActualStatus(bike.getBikeId());

        val actualStatusNotOk = StringUtils.containsAny(actualStatus,
                Status.BikeActualStatus.cannotOps.getVal(),
                Status.BikeActualStatus.internalError.getVal(),
                Status.BikeActualStatus.noPower.getVal(),
                Status.BikeActualStatus.pgNotFound.getVal(),
                Status.BikeActualStatus.userReport.getVal());

        if (actualStatusNotOk) {
            val cannotOps = actualStatus.contains(Status.BikeActualStatus.cannotOps.getVal()) && !actualStatus.contains(Status.BikeActualStatus.noPower.getVal());
            val message = cannotOps ? "扫码失败,可再试一次" : "硬件故障,可换一辆车扫码";
            log.debug("user:{},bike:{},{}", mobileNo, imei, message);
            val lbsEvent = cannotOps ? BikeLocService.LBSEvent.scanTimeout : BikeLocService.LBSEvent.scanError;
            bikeLocService.insertBikeLoc(imei, mobileNo, lbsEvent, lat, lng, bike.getAgentId());
            return new ResponseDTO<>(StatusCode._404.val, message);
        } else {
            return new ResponseDTO<>(StatusCode._200.val, "扫码成功", imei);
        }
    }


}
