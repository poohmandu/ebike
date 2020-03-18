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

package com.qdigo.ebike.controlcenter.domain.dto.rent;

import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.bike.BikeStatusDto;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.rideforceend.ForceEndInfo;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.ConsumeDetail;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.controlcenter.domain.entity.mongo.PGPackage;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class EndDTO {
    @NotNull(message = "骑行订单不能为空")
    private RideDto rideDto;
    private AgentCfg agentCfg;
    private UserDto userDto;
    private UserAccountDto userAccountDto;
    private BikeStatusDto bikeStatusDto;
    private PGPackage pgPackage;

    private String mobileNo;
    private String inputNumber;

    private Const.DeviceMode deviceMode;
    //private boolean withLBS; //可根据经纬度判断
    private double longitude;// 经度 withLBS=true时有意义
    private double latitude;// 纬度 withLBS=true时有意义
    private double accuracy = -1.0; //精度 withLBS=true时有意义
    private String provider;

    private boolean forceEnd;

    private Out out = new Out();

    //输出参数
    @Data
    public static class Out {
        private EndOrderTipDTO orderTipDTO;
        private ForceEndInfo forceEndInfo;
        private ConsumeDetail consumeDetail;
        private Long stationId;
    }

    @Data
    @Builder
    public static class EndOrderTipDTO {
        //消费详情
        //没通过accountValidate时为null
        //最好finish的时候重新查询
        //private RideActivityService.ConsumeDetail consumeDetail;

        private double finalConsume;

        private double accountBalance;

        private double allowArrears;

    }

}

