/*
 * Copyright 2020 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.api.service.order.longrent;

import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.Dto;
import com.qdigo.ebike.common.core.constants.Const;
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Description: 
 * date: 2020/1/2 5:37 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "order-center", contextId = "long-rent")
public interface OrderLongRentService {

    @PostMapping(ApiRoute.OrderCenter.LongRent.findValidByUserId)
    LongRentDto findValidByUserId(@RequestParam("userId") long userId);

    @PostMapping(ApiRoute.OrderCenter.LongRent.hasLongRent)
    boolean hasLongRent(@RequestParam("userId") long userId);

    @PostMapping(ApiRoute.OrderCenter.LongRent.findLastOne)
    LongRentDto findLastOne(@RequestParam("userId") long userId);

    @PostMapping(ApiRoute.OrderCenter.LongRent.create)
    LongRentDto create(@RequestBody LongRentDto longRentDto);

    @Data
    @Builder
    class LongRentDto implements Dto {
        private Long id;
        private long userId;
        private long agentId;
        private double price;
        private double consume;
        private Date startTime;
        private Date endTime;
        private Const.LongRentType longRentType;
        private String imei;
    }
}
