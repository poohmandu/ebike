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
import com.qdigo.ebike.api.service.third.wxlite.WxliteService;
import com.qdigo.ebike.api.service.user.UserWxService;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoneMatchException;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.usercenter.domain.entity.UserWxOpenInfo;
import com.qdigo.ebike.usercenter.domain.entity.UserWxliteInfo;
import com.qdigo.ebike.usercenter.repository.UserWxOpenInfoRepository;
import com.qdigo.ebike.usercenter.repository.UserWxliteInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserWxInfoService implements UserWxService {

    private final UserWxOpenInfoRepository userWxOpenInfoRepository;
    private final UserWxliteInfoRepository userWxliteInfoRepository;

    @Override
    public WxOpenInfoDto findByWxId(String appId, String openId, String unionId) throws NoneMatchException {
        // 需要修改
        if (StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(openId)) {
            Optional<UserWxOpenInfo> optional = userWxOpenInfoRepository.findFirstByAppIdAndOpenId(appId, openId);
            if (optional.isPresent()) {
                return optional.map(userWxOpenInfo -> ConvertUtil.to(userWxOpenInfo, WxOpenInfoDto.class)).orElse(null);
            } else if (StringUtils.isNotEmpty(unionId)) {
                return userWxOpenInfoRepository.findFirstByUnionId(unionId)
                        .map(userWxOpenInfo -> ConvertUtil.to(userWxOpenInfo, WxOpenInfoDto.class))
                        .orElse(null);
            } else {
                return null;
            }
        } else {
            throw new NoneMatchException("appId,openId不能为空");
        }
    }

    @Override
    @Transactional
    public void saveUserWxOpenInfo(long userId, String appId, String openId, String unionId, String version) {
        Optional<UserWxOpenInfo> optional = userWxOpenInfoRepository.findFirstByAppIdAndOpenId(appId, openId);
        UserWxOpenInfo userWxOpenInfo = optional.orElseGet(UserWxOpenInfo::new);
        userWxOpenInfo.setAppId(appId);
        userWxOpenInfo.setOpenId(openId);
        if (StringUtils.isNotEmpty(unionId)) {
            userWxOpenInfo.setUnionId(unionId);
        }
        userWxOpenInfo.setUserId(userId);
        userWxOpenInfo.setVersion(version);
        userWxOpenInfoRepository.save(userWxOpenInfo);
    }

    @Override
    @Transactional
    public void updateUserWxliteInfo(Param param) {
        try {
            WxInfoDto wxInfoDto = param.getWxInfoDto();
            WxliteService.Referer referer = param.getReferer();
            if (StringUtils.isEmpty(wxInfoDto.getMobileNo())) {
                return;
            }
            wxInfoDto.setWxliteVersion(referer.getWxliteVersion());
            wxInfoDto.setAppId(referer.getAppId());
            UserWxliteInfo userWxliteInfo = ConvertUtil.to(wxInfoDto, UserWxliteInfo.class);
            userWxliteInfoRepository.save(userWxliteInfo);
        } catch (Exception e) {
            log.error("保存小程序基本信息失败", e);
        }
    }

    @Override
    public WxInfoDto findByMobileNo(String mobileNo) {
        return userWxliteInfoRepository.findById(mobileNo)
                .map(userWxliteInfo -> ConvertUtil.to(userWxliteInfo, WxInfoDto.class))
                .orElse(null);
    }

}
