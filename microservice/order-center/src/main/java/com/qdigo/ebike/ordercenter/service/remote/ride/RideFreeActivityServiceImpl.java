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

package com.qdigo.ebike.ordercenter.service.remote.ride;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.ConsumeDetail;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.FreeActivityDto;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.FreeType;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.activity.coupon.CouponService;
import com.qdigo.ebike.api.service.order.ride.RideFreeActivityService;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.errors.exception.runtime.DataConflictException;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.ordercenter.domain.entity.UserLongRent;
import com.qdigo.ebike.ordercenter.domain.entity.ride.RideFreeActivity;
import com.qdigo.ebike.ordercenter.repository.RideFreeActivityRepository;
import com.qdigo.ebike.ordercenter.repository.dao.UserLongRentDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2018/3/31.
 * <p>
 * 骑行订单活动减免、额外支付费用
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RideFreeActivityServiceImpl implements RideFreeActivityService {

    private final RideFreeActivityRepository rideFreeActivityRepository;
    private final UserLongRentDao userLongRentDao;
    private final CouponService couponService;

    @Override
    public ConsumeDetail getConsumeDetail(DetailParam param) {
        RideDto rideDto = param.getRideDto();
        AgentCfg config = param.getAgentCfg();
        UserDto userDto = param.getUserDto();
        UserAccountDto account = param.getAccountDto();

        // 已结束订单
        if (rideDto.getRideStatus() == Status.RideStatus.end.getVal()) {
            List<RideFreeActivity> freeActivities = rideFreeActivityRepository.findByRideRecordId(rideDto.getRideRecordId());
            return this.getConsumeDetail(freeActivities, rideDto.getConsume());
        }
        // 未结束订单
        Date now;
        if (rideDto.getEndTime() == null) {
            now = new Date();
        } else {
            now = rideDto.getEndTime();
        }
        long curStart = rideDto.getStartTime().getTime();

        List<RideFreeActivity> freeActivityList = new ArrayList<>();


        int freeSeconds = config.getFreeSeconds();
        if (freeSeconds > 0) {
            RideFreeActivity freeActivity = new RideFreeActivity();
            freeActivity.setFreeActivity(Status.FreeActivity.xxSecondsDrive);
            freeActivity.setFreeType(FreeType.time);
            freeActivity.setRideRecordId(rideDto.getRideRecordId());
            freeActivity.setFreeConsume(0);
            freeActivity.setFreeTime(TimeUnit.SECONDS.toMillis(freeSeconds));
            freeActivity.setNote("前" + freeSeconds + "秒免费骑行");
            curStart += freeActivity.getFreeTime();
            freeActivityList.add(freeActivity);
            if (curStart >= now.getTime()) {
                return this.getConsumeDetail(freeActivityList, 0);
            }
        }

        UserLongRent lastOne = userLongRentDao.findLastOne(userDto.getUserId());
        if (lastOne != null && lastOne.getEndTime().after(rideDto.getStartTime())) {
            RideFreeActivity freeActivity = new RideFreeActivity();
            freeActivity.setFreeActivity(Status.FreeActivity.longRent);
            freeActivity.setFreeType(FreeType.time);
            freeActivity.setRideRecordId(rideDto.getRideRecordId());
            freeActivity.setFreeConsume(0);
            freeActivity.setNote("骑行卡的有效时间内(" + FormatUtil.MdHm.format(lastOne.getEndTime()) + ")免费");
            long freeEnd = Math.min(lastOne.getEndTime().getTime(), now.getTime());
            long time = freeEnd - curStart;
            freeActivity.setFreeTime(time);
            curStart += freeActivity.getFreeTime();
            freeActivityList.add(freeActivity);
            if (curStart >= now.getTime()) {
                return this.getConsumeDetail(freeActivityList, 0);
            }
        }

        //时间减免结束,计算金钱减免
        double curConsume = this.calConsume(new Date(curStart), now, rideDto.getUnitMinutes(), rideDto.getPrice(), config);

        //calRideReduction 会产生实际消费
        //double couponFreeConsume = activityService.calRideReduction(rideRecord, curConsume);
        double couponFreeConsume = FormatUtil.getMoney(couponService.getFreeConsume(userDto.getUserId()));
        if (couponFreeConsume > 0) {
            RideFreeActivity freeActivity = new RideFreeActivity();
            freeActivity.setFreeActivity(Status.FreeActivity.coupon);
            freeActivity.setFreeType(FreeType.money);
            freeActivity.setRideRecordId(rideDto.getRideRecordId());
            freeActivity.setFreeTime(0);
            freeActivity.setFreeConsume(couponFreeConsume);
            freeActivity.setNote("使用优惠券减免" + freeActivity.getFreeConsume() + "元");
            curConsume -= freeActivity.getFreeConsume();
            freeActivityList.add(freeActivity);
            if (curConsume <= 0) {
                return this.getConsumeDetail(freeActivityList, 0);
            }
        }

        double giftBalance = account.getGiftBalance();
        if (giftBalance > 0) {
            double freeConsume = FormatUtil.getMoney(Math.min(giftBalance, curConsume));
            RideFreeActivity freeActivity = new RideFreeActivity();
            freeActivity.setFreeActivity(Status.FreeActivity.giftBalance);
            freeActivity.setFreeType(FreeType.money);
            freeActivity.setRideRecordId(rideDto.getRideRecordId());
            freeActivity.setFreeTime(0);
            freeActivity.setFreeConsume(freeConsume);
            freeActivity.setNote("使用赠送余额" + freeActivity.getFreeConsume() + "元");
            curConsume -= freeActivity.getFreeConsume();
            freeActivityList.add(freeActivity);
            if (curConsume <= 0) {
                return this.getConsumeDetail(freeActivityList, 0);
            }
        }

        curConsume = FormatUtil.getMoney(curConsume);
        log.debug("{}用户一番减免后仍需支付余额{}元", userDto.getMobileNo(), curConsume);
        return this.getConsumeDetail(freeActivityList, curConsume);
    }

    private ConsumeDetail getConsumeDetail(List<RideFreeActivity> freeActivityList, double consume) {
        List<FreeActivityDto> freeActivities = ConvertUtil.to(freeActivityList, FreeActivityDto.class);
        StringBuilder consumeNote = new StringBuilder();
        for (int i = 0; i < freeActivities.size(); i++) {
            consumeNote.append(i + 1).append(".").append(freeActivities.get(i).getNote()).append(";");
        }
        ConsumeDetail consumeDetail = new ConsumeDetail();
        consumeDetail.setFreeActivities(freeActivities);
        consumeDetail.setConsumeNote(consumeNote.toString());
        consume = consume > 0 ? consume : 0;
        consumeDetail.setConsume(consume);
        log.debug("用户需支付余额{}元,减免活动有:{}", consume, consumeNote);
        return consumeDetail;
    }

    private double calConsume(Date start, Date end, int unitMinutes, double price, AgentCfg config) {
        if (start.getTime() > end.getTime()) {
            throw new DataConflictException("开始时间不得晚于结束时间");
        }
        long seconds = Duration.between(start.toInstant(), end.toInstant()).getSeconds();
        //int minutes = FormatUtil.minutes(seconds);
        //int hours = FormatUtil.hours(seconds);

        long secondsPerDay = 24 * 60 * 60;

        int days = (int) (seconds / secondsPerDay);
        int secondsOpt = (int) (seconds % secondsPerDay);

        int dayMax = config.getDayMaxHours();

        double dayConsume = price * FormatUtil.units(TimeUnit.HOURS.toSeconds(dayMax), unitMinutes);
        double curDayConsume = price * FormatUtil.units(secondsOpt, unitMinutes);

        double consume = 0;
        consume += days * dayConsume;
        consume += Math.min(curDayConsume, dayConsume);

        log.debug("开始时间到结束时间:【{} -> {}】,单价为:{},日上限:{},计算消费:{}元", start, end, price, dayMax, consume);
        return consume;
    }

}
