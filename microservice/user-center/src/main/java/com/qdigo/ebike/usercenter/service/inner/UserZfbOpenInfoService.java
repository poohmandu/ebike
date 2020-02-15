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

package com.qdigo.ebike.usercenter.service.inner;

import com.qdigo.ebike.common.core.errors.exception.runtime.NoneMatchException;
import com.qdigo.ebike.usercenter.domain.entity.UserZfbInfo;
import com.qdigo.ebike.usercenter.domain.entity.UserZfbOpenInfo;
import com.qdigo.ebike.usercenter.repository.UserZfbInfoRepository;
import com.qdigo.ebike.usercenter.repository.UserZfbOpenInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@Service
public class UserZfbOpenInfoService {

    @Inject
    private UserZfbOpenInfoRepository userZfbOpenInfoRepository;
    @Inject
    private UserZfbInfoRepository userZfbInfoRepository;

    public Optional<UserZfbOpenInfo> findByOpenId(String appId, String openId) throws NoneMatchException {
        if (StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(openId)) {
            return userZfbOpenInfoRepository.findFirstByOpenIdAndAppId(openId, appId);
        } else {
            throw new NoneMatchException("appId,openId不能为空");
        }
    }


    @Transactional
    public void saveUserZfbOpenInfo(long userId, String appId, String openId) {
        Optional<UserZfbOpenInfo> optional = userZfbOpenInfoRepository.findFirstByOpenIdAndAppId(openId, appId);
        UserZfbOpenInfo userZfbOpenInfo = optional.orElseGet(UserZfbOpenInfo::new);
        userZfbOpenInfo.setAppId(appId);
        userZfbOpenInfo.setOpenId(openId);
        userZfbOpenInfo.setUserId(userId);
        userZfbOpenInfoRepository.save(userZfbOpenInfo);
    }

    @Transactional
    public void saveUserZfbInfo(UserZfbInfo userZfbInfo) {
        try {
            if (StringUtils.isEmpty(userZfbInfo.getMobileNo())) {
                return;
            }
            userZfbInfoRepository.save(userZfbInfo);
        } catch (Exception e) {
            log.error("保存小程序基本信息失败", e);
        }
    }


}
