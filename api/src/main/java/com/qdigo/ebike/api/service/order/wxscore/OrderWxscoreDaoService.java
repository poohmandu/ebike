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
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * description: 
 *
 * date: 2020/3/19 1:12 AM
 * @author niezhao
 */
@FeignClient(name = "order-center", contextId = "order-wxscore-dao")
public interface OrderWxscoreDaoService {

    @PostMapping(ApiRoute.OrderCenter.Wxscore.finishOrder)
    void finishOrder(@RequestBody WxscoreOrder dto);

}

