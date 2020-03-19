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

package com.qdigo.ebike.api.service.order.wxscore;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.ConsumeDetail;
import com.qdigo.ebike.api.domain.dto.order.wxscore.WxscoreDto;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.common.core.errors.exception.QdigoBizException;
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * description: 
 *
 * date: 2020/3/17 11:35 PM
 * @author niezhao
 */
@FeignClient(name = "order-center", contextId = "order-wxscore-biz")
public interface OrderWxscoreBizService {

    @PostMapping(ApiRoute.OrderCenter.Wxscore.hasRideWxscoreOrder)
    WxscoreDto hasRideWxscoreOrder(@RequestParam("rideRecordId") Long rideRecordId);

    @PostMapping(ApiRoute.OrderCenter.Wxscore.startWxscoreOrder)
    void startWxscoreOrder(@RequestBody WxsocreStart wxsocreStart);

    //rebuild 抛qdigobiz异常
    @PostMapping(ApiRoute.OrderCenter.Wxscore.completeWxscoreOrder)
    void completeWxscoreOrder(@RequestBody WxscoreComplete wxscoreComplete) throws QdigoBizException;

    @Data
    @Builder
    class WxsocreStart {
        private UserDto userDto;
        private String wxscoreEnable;

        private AgentCfg agentCfg;
        private RideDto rideDto;
    }

    @Data
    @Builder
    class WxscoreComplete {
        private UserDto userDto;
        private RideDto rideDto;
        private WxscoreDto wxscoreDto;
        private ConsumeDetail consumeDetail;
        private List<WxscoreOrder.Discount> otherDiscounts;

        private Integer totalAmount;

    }
}
