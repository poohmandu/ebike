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
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.usercenter.domain.entity.UserAccount;
import com.qdigo.ebike.usercenter.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

/**
 * Description: 
 * date: 2020/1/24 11:12 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserAccountDto findByMobileNo(String mobileNo) {
        return userAccountRepository.findByUserMobileNo(mobileNo)
                .map(userAccount -> ConvertUtil.to(userAccount, UserAccountDto.class))
                .orElse(null);
    }

    @Override
    public UserAccountDto findByUserId(long userId) {
        return userAccountRepository.findByUserUserId(userId)
                .map(userAccount -> ConvertUtil.to(userAccount, UserAccountDto.class))
                .orElse(null);
    }

    @Override
    public void update(UserAccountDto userAccountDto) {
        UserAccount userAccount = userAccountRepository.findById(userAccountDto.getUserAccountId()).get();
        userAccountDto.updated(userAccount);
        userAccountRepository.save(userAccount);
    }


}
