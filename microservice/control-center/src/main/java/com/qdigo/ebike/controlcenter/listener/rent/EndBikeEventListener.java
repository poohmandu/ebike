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

import com.qdigo.ebicycle.constants.Const;
import com.qdigo.ebicycle.constants.Keys;
import com.qdigo.ebicycle.constants.Status;
import com.qdigo.ebicycle.daoService.wxscore.WxscoreDaoService;
import com.qdigo.ebicycle.domain.order.OrderWxscore;
import com.qdigo.ebicycle.domain.ride.RideRecord;
import com.qdigo.ebicycle.domain.user.User;
import com.qdigo.ebicycle.o.dto.ResponseDTO;
import com.qdigo.ebicycle.o.dto.payment.wxscore.WxscoreOrder;
import com.qdigo.ebicycle.service.credit.CreditService;
import com.qdigo.ebicycle.service.push.PushService;
import com.qdigo.ebicycle.service.ride.RideTrackService;
import com.qdigo.ebicycle.service.ride.RideWxscoreService;
import com.qdigo.ebicycle.service.third.WxScoreService;
import com.qdigo.ebicycle.service.user.UserRecordService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2018/1/10.
 */
@Slf4j
@Component
public class EndBikeEventListener {

    @Inject
    private PushService pushService;
    @Inject
    private RideTrackService rideTrackService;
    @Inject
    private UserRecordService userRecordService;
    @Inject
    private CreditService creditService;
    @Inject
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private RideWxscoreService rideWxscoreService;
    @Resource
    private WxScoreService wxScoreService;
    @Resource
    private WxscoreDaoService wxscoreDaoService;

    @Async
    @EventListener
    public void onApplicationEvent(EndBikeSuccessEvent event) {
        try {
            RideRecord rideRecord = event.getRideRecord();
            if (rideRecord == null) {
                return;
            }
            User user = rideRecord.getUser();
            String mobileNo = user.getMobileNo();

            MDC.put("mobileNo", mobileNo);

            //  插入用户信息记录
            this.insertUserRecord(rideRecord);
            //  记录用户信用分
            this.updateCreditInfo(rideRecord);
            //  清理redis相关key
            this.clearRedis(mobileNo);

            try { //5秒左右
                TimeUnit.SECONDS.sleep(Const.pushRideDelay);
            } catch (InterruptedException e) {
                log.error(mobileNo + "的onApplicationEvent被中断:" + e.getMessage());
                Thread.currentThread().interrupt();
            }
            //更新微信支付分
            this.updateWxscore(rideRecord);

            //  完成骑行轨迹的存储
            rideTrackService.insertRideTracks(rideRecord.getRideRecordId());

            // 推送还车成功消息
            this.pushEndBikeMessage(rideRecord);

            log.debug("{}用户在监听器里触发完成还车的其他流程", user.getMobileNo());
        } catch (Exception e) {
            log.error("还车后置流程发生异常:", e);
            throw e;
        } finally {
            MDC.remove("mobileNo");
        }
    }

    private void pushEndBikeMessage(RideRecord rideRecord) {
        String alert;
        if (rideRecord.getRideStatus() == Status.RideStatus.end.getVal()) {
            alert = "成功还车,此次骑行花费" + rideRecord.getConsume() + "元";
        } else {
            alert = "成功还车,订单状态异常";
        }
        pushService.pushNotation(rideRecord.getUser(), alert, Const.PushType.appEndSuccess, "");
    }

    private void insertUserRecord(RideRecord rideRecord) {
        String alert;
        if (rideRecord.getRideStatus() == Status.RideStatus.end.getVal()) {
            alert = "成功还车,此次骑行花费" + rideRecord.getConsume() + "元";
        } else {
            alert = "成功还车,订单状态异常";
        }
        userRecordService.insertUserRecord(rideRecord.getUser(), alert);
    }

    private void updateCreditInfo(RideRecord rideRecord) {
        if (rideRecord.getRideStatus() == Status.RideStatus.end.getVal()) {
            creditService.updateCreditInfo(rideRecord.getUser(), Status.CreditEvent.normalDrive.getVal(), Status.CreditEvent.normalDrive.getScore());
        }
    }

    private void updateWxscore(RideRecord rideRecord) {
        try {
            long rideRecordId = rideRecord.getRideRecordId();
            Optional<OrderWxscore> optional = rideWxscoreService.hasRideWxscoreOrder(rideRecord);
            if (optional.isPresent()) {
                OrderWxscore wxscore = optional.get();
                if (wxscore.getState() == OrderWxscore.State.USER_ACCEPTED) {
                    ResponseDTO<WxscoreOrder> dto = wxScoreService.queryByOrderNo(wxscore.getOutOrderNo(), wxscore.getAppId());
                    if (dto.isSuccess()) {
                        WxscoreOrder wxscoreOrder = dto.getData();
                        wxscoreDaoService.finishOrder(wxscoreOrder);
                    }
                }
            }
        } catch (Exception e) {
            log.error("还车后置流程更新微信支付分订单失败:", e);
        }
    }

    private void clearRedis(String mobileNo) {
        String arrearsChargeKey = Keys.flagArrearsCharge.getKey(mobileNo);
        redisTemplate.delete(arrearsChargeKey);
        log.debug("{}还车后清理redis", mobileNo);
    }

}
