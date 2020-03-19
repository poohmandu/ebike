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

import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.wxscore.WxscoreDto;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.control.RideTrackService;
import com.qdigo.ebike.api.service.order.wxscore.OrderWxscoreBizService;
import com.qdigo.ebike.api.service.order.wxscore.OrderWxscoreDaoService;
import com.qdigo.ebike.api.service.third.push.PushService;
import com.qdigo.ebike.api.service.third.wxlite.WxscoreService;
import com.qdigo.ebike.api.service.user.UserCreditService;
import com.qdigo.ebike.api.service.user.UserRecordService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.controlcenter.domain.dto.rent.EndDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by niezhao on 2018/1/10.
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EndBikeEventListener {

    private final PushService pushService;
    private final UserRecordService userRecordService;
    private final UserCreditService creditService;
    private final OrderWxscoreBizService wxscoreBizService;
    private final WxscoreService wxscoreService;
    private final RideTrackService rideTrackService;
    private final OrderWxscoreDaoService wxscoreDaoService;

    private RedisTemplate<String, String> redisTemplate;


    @Async
    @EventListener
    public void onApplicationEvent(EndBikeSuccessEvent event) {
        try {
            EndDTO endDTO = event.getEndDTO();
            RideDto rideRecord = endDTO.getRideDto();
            UserDto userDto = endDTO.getUserDto();
            if (rideRecord == null) {
                return;
            }
            String mobileNo = rideRecord.getMobileNo();

            MDC.put("mobileNo", mobileNo);

            //  插入用户信息记录
            this.insertUserRecord(rideRecord, userDto);
            //  记录用户信用分
            this.updateCreditInfo(rideRecord, userDto);
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
            this.pushEndBikeMessage(rideRecord, userDto);

            log.debug("{}用户在监听器里触发完成还车的其他流程", userDto.getMobileNo());
        } catch (Exception e) {
            log.error("还车后置流程发生异常:", e);
            throw e;
        } finally {
            MDC.remove("mobileNo");
        }
    }

    private void pushEndBikeMessage(RideDto rideRecord, UserDto user) {
        String alert;
        if (rideRecord.getRideStatus() == Status.RideStatus.end.getVal()) {
            alert = "成功还车,此次骑行花费" + rideRecord.getConsume() + "元";
        } else {
            alert = "成功还车,订单状态异常";
        }
        PushService.Param param = PushService.Param.builder().mobileNo(user.getMobileNo())
                .deviceId(user.getDeviceId()).pushType(Const.PushType.appEndSuccess).alert(alert).build();
        pushService.pushNotation(param);
    }

    private void insertUserRecord(RideDto rideRecord, UserDto userDto) {
        String alert;
        if (rideRecord.getRideStatus() == Status.RideStatus.end.getVal()) {
            alert = "成功还车,此次骑行花费" + rideRecord.getConsume() + "元";
        } else {
            alert = "成功还车,订单状态异常";
        }
        userRecordService.insertUserRecord(userDto.getUserId(), alert);
    }

    private void updateCreditInfo(RideDto rideRecord, UserDto userDto) {
        if (rideRecord.getRideStatus() == Status.RideStatus.end.getVal()) {
            creditService.updateCreditInfo(userDto.getUserId(), Status.CreditEvent.normalDrive.getVal(), Status.CreditEvent.normalDrive.getScore());
        }
    }

    private void updateWxscore(RideDto rideRecord) {
        try {
            long rideRecordId = rideRecord.getRideRecordId();
            WxscoreDto wxscore = wxscoreBizService.hasRideWxscoreOrder(rideRecordId);
            if (wxscore != null) {
                if (wxscore.getState() == WxscoreService.State.USER_ACCEPTED) {
                    ResponseDTO<WxscoreOrder> dto = wxscoreService.queryByOrderNo(wxscore.getOutOrderNo(), wxscore.getAppId());
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
