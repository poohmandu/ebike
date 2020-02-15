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
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.domain.vo.UserResponse;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import com.qdigo.ebike.usercenter.service.inner.UserInnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

/**
 * Description: 
 * date: 2020/1/6 2:21 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserRemoteServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserInnerService userInnerService;

    @Override
    public UserDto findByMobileNo(String mobileNo) {
        return userRepository.findOneByMobileNo(mobileNo)
                .map(user -> ConvertUtil.to(user, UserDto.class))
                .orElse(null);
    }

    @Override
    public UserDto findById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> ConvertUtil.to(user, UserDto.class))
                .orElse(null);
    }

    @Override
    public List<OpenInfo> getOpenInfo(UserDto userDto) {
        User user = userDto.toEntity(User.class);
        List<UserResponse.OpenInfo> openInfo = userInnerService.getOpenInfo(user);
        return ConvertUtil.to(openInfo, OpenInfo.class);
    }


}
