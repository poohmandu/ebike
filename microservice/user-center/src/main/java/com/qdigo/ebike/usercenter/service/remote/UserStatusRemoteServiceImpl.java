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
import com.qdigo.ebike.api.service.user.UserStatusService;
import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import com.qdigo.ebike.usercenter.service.inner.UserStatusInnerService;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

/**
 * Description: 
 * date: 2020/1/25 12:59 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserStatusRemoteServiceImpl implements UserStatusService {

    private final UserStatusInnerService userStatusInnerService;
    private final UserRepository userRepository;

    @Override
    public Boolean getUserWxscoreEnable(String mobileNo) {
        User user = userRepository.findOneByMobileNo(mobileNo).get();
        return userStatusInnerService.getUserWxscoreEnable(user);
    }
}
