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

package com.qdigo.ebike.ordercenter.message.charge;

import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import lombok.Getter;

/**
 * Created by yz on 2020/1/7.
 */
@Getter
public class LongRentChargeEvent extends ChargeSuccessEvent {

    private String longRentType;
    private Double price;

    public LongRentChargeEvent(Object source, UserDto user, UserAccountDto account, String longRentType, Double price) {
        super(source, user, account);
        this.longRentType = longRentType;
        this.price = price;
    }
}
