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

package com.qdigo.ebike.controlcenter.service.inner.datagram;

import com.qdigo.ebike.api.domain.dto.bike.BikeConfigDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.api.service.third.push.PushService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.controlcenter.domain.dto.BikePhInfo;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PHPackage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2016/12/8.
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PHService {

    private final PHMongoService phMongoService;
    private final OrderRideService rideService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PushService pushService;
    private final UserService userService;

    @Transactional
    public void updateStatus(PHPackage ph, List<PHPackage> list, BikePhInfo bikePhInfo) {
        val imei = ph.getPhImei();
        val bike = bikePhInfo.getBikeDto();

        BikeStatusDto bikeStatus = bikePhInfo.getBikeStatusDto();
        BikeConfigDto bikeConfig = bikePhInfo.getBikeConfigDto();
        Assert.notNull(bikeConfig, imei + "设备无对应配置");

        val deviceId = bike.getDeviceId();
        int enoughBattery = bikeConfig.getEnoughBattery();
        int lowBattery = bikeConfig.getLowBattery();

        val oldBattery = bikeStatus.getBattery();
        val nowBattery = this.getBattery(bikeConfig, ph.getPhPowerVoltage());
        val avgBattery = this.getBattery(bikeConfig, phMongoService.getAvgPowerVoltage(ph, list));

        val lowPowerVoltageBln = ph.getPhElectric() == 1 && avgBattery <= lowBattery;

        if ((avgBattery < oldBattery && avgBattery > 0) || nowBattery > enoughBattery) {
            if (nowBattery > enoughBattery) {
                bikeStatus.setBattery(nowBattery);
            } else {
                bikeStatus.setBattery(avgBattery);
            }
            val kilometer = getKilometer(bikeConfig.getMaxKilometer(), bikeStatus.getBattery());
            bikeStatus.setKilometer(kilometer);

            if (lowPowerVoltageBln) {
                if (bikeStatus.getStatus() != Status.BikeLogicStatus.available.getVal()) {
                    RideDto rideDto = rideService.findRidingByImei(imei);
                    if (rideDto != null) {
                        val mobileNo = rideDto.getMobileNo();
                        val key = Keys.lowPower.getKey(mobileNo);
                        if (!redisTemplate.hasKey(key)) {
                            redisTemplate.opsForValue().set(key, "1", 5, TimeUnit.MINUTES);
                        } else {
                            redisTemplate.opsForValue().increment(key, 1);
                            redisTemplate.expire(key, 20, TimeUnit.MINUTES);
                            val count = Integer.parseInt(redisTemplate.opsForValue().get(key));
                            if (count == 5) {
                                UserDto userDto = userService.findByMobileNo(mobileNo);

                                final String alert = "您正在骑行的车辆,电量过低，请注意及时充电";
                                PushService.Param param = PushService.Param.builder().alert(alert).pushType(Const.PushType.warn)
                                        .mobileNo(mobileNo).deviceId(userDto.getDeviceId()).build();
                                pushService.pushNotation(param);
                            }
                        }
                    }
                }
            }

        }

        if (bike.isDeleted()) {
            return;
        }

        String seatCushionTimeStr = redisTemplate.opsForValue().get(Keys.seat_cushion.getKey(imei));

        if (seatCushionTimeStr == null ||
                TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Long.parseLong(seatCushionTimeStr)) > 5) {

            val electricBln = ph.getPhElectric() == 0;
            warnService.warn(electricBln, Const.MailType.ElectricWarn, MessageFormat.format("deviceId:{0},没有外接电源,IMEI:{1}", deviceId, imei), bike);
            warnService.warn(lowPowerVoltageBln, Const.MailType.PowerVoltageWarn, MessageFormat.format("deviceId:{0},电压过低,为{1}伏,IMEI:{2}", deviceId, ph.getPhPowerVoltage() / 100, imei), bike);
        } else {
            log.debug("【{}】5分钟内有管理员开过坐垫,屏蔽报警", imei);
        }

        val doorLockBln = ph.getPhDoorLock() == 1 && ph.getPhLocked() == 1;
        warnService.warn(doorLockBln, Const.MailType.DoorLockWarn, MessageFormat.format("deviceId:{0},锁车情况下打开了电门锁,IMEI:{1}", deviceId, imei), bike);

        val wheelInputBln = ph.getPhWheelInput() == 1 && ph.getPhLocked() == 1;
        warnService.warn(wheelInputBln, Const.MailType.WheelInputWarn, MessageFormat.format("deviceId:{0},锁车情况下有轮车输入,IMEI:{1}", deviceId, imei), bike);

        val shakeBln = ph.getPhShaked() == 1 && ph.getPhLocked() == 1;
        warnService.warn(shakeBln, Const.MailType.ShakeWarn, MessageFormat.format("deviceId:{0},锁车情况下有震动,IMEI:{1}", deviceId, imei), bike);

    }

    //rebuild
    public void checkBikeStatus(PHPackage ph, List<PHPackage> phList) {
        //val imei = ph.getPhImei();
        //val bike = bikeRepository.findByImeiId(imei).orElse(null);
        //if (bike == null) {
        //    log.debug("PHService:bike表未查询到imei号为{}的车辆", imei);
        //    return;
        //}
        //if (bike.isDeleted()) {
        //    return;
        //}
        //try {
        //    checkWorkOrder(bike);
        //} catch (Exception e) {
        //    log.error("PH中检查车辆状态出错");
        //}
    }

    //rebuild
    private void checkWorkOrder(BikeDto bike) {
        //val imei = bike.getImeiId();
        //val status = bike.getBikeStatus();
        //if (workOrderService.checkCreateWorkOrder(imei, WorkOrder.WorkOrderType.lowPower)) {
        //    workOrderService.createWorkOrder(imei, WorkOrder.WorkOrderType.lowPower,
        //            MessageFormat.format("编号{0}车辆电量过低,只剩{1}%", bike.getDeviceId(), status.getBattery()));
        //}
        //if (workOrderService.checkApproveWorkOrder(imei, WorkOrder.WorkOrderType.lowPower)) {
        //    workOrderService.approveWorkOrder(imei, WorkOrder.WorkOrderType.lowPower);
        //}
    }

    private int getBattery(BikeConfigDto bikeConfig, double powerVoltage) {
        double maxPower = bikeConfig.getMaxPowerVoltage();
        double minPower = bikeConfig.getMinPowerVoltage();
        int battery = (int) ((powerVoltage - minPower) / (maxPower - minPower) * 100);
        battery = battery < 0 ? 0 : (battery > 100 ? 100 : battery); //电量在1-100
        return battery;
    }

    private double getKilometer(double maxKilometer, int battery) {
        return maxKilometer * ((double) battery) / 100;
    }

}
