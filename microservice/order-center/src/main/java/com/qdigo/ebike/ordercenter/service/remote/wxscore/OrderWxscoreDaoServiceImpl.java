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
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import com.qdigo.ebike.api.service.order.wxscore.OrderWxscoreDaoService;
import com.qdigo.ebike.ordercenter.service.inner.wxscore.WxscoreDaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/19 1:14 AM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OrderWxscoreDaoServiceImpl implements OrderWxscoreDaoService {

    private final WxscoreDaoService wxscoreDaoService;

    @Override
    public void finishOrder(WxscoreOrder dto) {
        wxscoreDaoService.finishOrder(dto);
    }
}
