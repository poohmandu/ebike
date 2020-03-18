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

package com.qdigo.ebike.ordercenter.service.remote.wxscore;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.wxscore.WxscoreDto;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.StartOrderParam;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.order.wxscore.OrderWxscoreBizService;
import com.qdigo.ebike.api.service.third.wxlite.WxliteService;
import com.qdigo.ebike.api.service.third.wxlite.WxscoreService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.ordercenter.domain.entity.ride.RideRecord;
import com.qdigo.ebike.ordercenter.domain.entity.wxscore.OrderWxscore;
import com.qdigo.ebike.ordercenter.repository.OrderWxscoreRepository;
import com.qdigo.ebike.ordercenter.service.inner.wxscore.WxscoreDaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.MessageFormat;

/**
 * description: 
 *
 * date: 2020/3/17 11:39 PM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OrderWxscoreBizServiceImpl implements OrderWxscoreBizService {

    private final OrderWxscoreRepository orderWxscoreRepository;
    private final WxscoreService wxscoreService;
    private final WxscoreDaoService wxscoreDaoService;
    private final WxliteService wxliteService;
    private final UserService userService;

    @Override
    public WxscoreDto hasRideWxscoreOrder(Long rideRecordId) {
        OrderWxscore orderWxscore = orderWxscoreRepository.findByRideRecordId(rideRecordId);
        return ConvertUtil.to(orderWxscore, WxscoreDto.class);
    }

    private boolean wxscoreEnable(String userDeviceId, String wxscoreState) {
        if (!wxliteService.isWxlite(userDeviceId))
            return false;
        return "AVAILABLE".equals(wxscoreState);
    }

    @Transactional
    @Override
    public void startWxscoreOrder(WxsocreStart wxsocreStart) {
        try {
            UserDto userDto = wxsocreStart.getUserDto();
            AgentCfg config = wxsocreStart.getAgentCfg();
            RideDto rideDto = wxsocreStart.getRideDto();
            Long agentId = config.getAgentId();
            boolean wxscoreEnable = wxscoreEnable(userDto.getDeviceId(), wxsocreStart.getWxscoreEnable());
            if (!wxscoreEnable) {
                return;
            }
            String appId = wxliteService.getAppId(userDto.getDeviceId());
            String openId = userService.getOpenInfo(userDto).stream()
                    .filter(openInfo -> openInfo.getAppId().equals(appId))
                    .map(UserService.OpenInfo::getOpenId)
                    .findAny().get();

            String feeDesc = MessageFormat.format("每{0}分钟{1}元,每天{2}小时封顶",
                    rideDto.getUnitMinutes(), rideDto.getPrice(), config.getDayMaxHours());


            StartOrderParam param = StartOrderParam.builder()
                    .agentId(agentId)
                    .appId(appId)
                    .feeDesc(feeDesc)
                    .openId(openId)
                    .rideRecordId(rideDto.getRideRecordId())
                    .userId(userDto.getUserId())
                    .build();
            ResponseDTO<String> startOrder = wxscoreService.startOrder(param);
            String errMessage;
            if (startOrder.isNotSuccess()) {
                return;
            }
            String outOrderNo = startOrder.getData();
            ResponseDTO<WxscoreOrder> queryOrder = wxscoreService.queryByOrderNo(outOrderNo, appId);
            if (queryOrder.isNotSuccess()) {
                return;
            }
            //事务传播REQUIRES_NEW才能在这抛出异常否则在大事务抛出
            WxscoreOrder wxscoreOrder = queryOrder.getData();
            wxscoreDaoService.createOrder(wxscoreOrder);

        } catch (Exception e) {
            log.warn("微信支付分创单时发生错误并忽略:{}", e.getMessage());
        }

    }
}
