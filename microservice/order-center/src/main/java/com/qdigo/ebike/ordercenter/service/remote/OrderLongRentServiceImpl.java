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

package com.qdigo.ebike.ordercenter.service.remote;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.order.longrent.OrderLongRentService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.ordercenter.domain.entity.UserLongRent;
import com.qdigo.ebike.ordercenter.repository.UserLongRentRepository;
import com.qdigo.ebike.ordercenter.repository.dao.UserLongRentDao;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

/**
 * Description: 
 * date: 2020/1/2 5:42 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OrderLongRentServiceImpl implements OrderLongRentService {

    private final UserLongRentDao userLongRentDao;
    private final UserLongRentRepository userLongRentRepository;

    @Override
    public LongRentDto findValidByUserId(long userId) {
        UserLongRent longRent = userLongRentDao.findValidByUserId(userId);
        return ConvertUtil.to(longRent, LongRentDto.class);
    }

    @Override
    public boolean hasLongRent(long userId) {
        return userLongRentDao.hasLongRent(userId);
    }

    @Override
    public LongRentDto findLastOne(long userId) {
        UserLongRent longRent = userLongRentDao.findLastOne(userId);
        return ConvertUtil.to(longRent, LongRentDto.class);
    }

    @Override
    public LongRentDto create(LongRentDto longRentDto) {
        UserLongRent userLongRent = ConvertUtil.to(longRentDto, UserLongRent.class);
        userLongRent.setId(null);
        userLongRent = userLongRentRepository.save(userLongRent);
        return ConvertUtil.to(userLongRent, LongRentDto.class);
    }

}
