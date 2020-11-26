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

package com.qdigo.ebike.usercenter.service.remote;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.user.UserBlackListService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.usercenter.repository.UserBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/16 9:31 AM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserBlackListServiceImpl implements UserBlackListService {

    private final UserBlacklistRepository blacklistRepository;

    @Override
    public BlackListDto findByUserId(Long userId) {
        return blacklistRepository.findByUserId(userId)
                .map(userBlacklist -> ConvertUtil.to(userBlacklist, BlackListDto.class))
                .orElse(null);
    }
}
