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

package com.qdigo.ebike.usercenter.controller;

import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.api.service.third.wxlite.WxliteService;
import com.qdigo.ebike.api.service.user.UserWxService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoneMatchException;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.commonaop.aspects.AccessAspect;
import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.domain.vo.UserResponse;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import com.qdigo.ebike.usercenter.service.inner.UserInnerService;
import com.qdigo.ebike.usercenter.service.remote.UserWxInfoService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Description: 
 * date: 2020/1/6 5:58 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/wxlite")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WxliteLogin {

    private final WxliteService wxliteService;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserWxInfoService userWxService;
    private final UserRepository userRepository;
    private final AccessAspect accessAspect;
    private final UserInnerService userInnerService;


    //@Transactional
    @PostMapping(value = "/wxLogin", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> wxLogin(
            @RequestHeader(value = "referer") String referer,
            @RequestHeader("code") String code,
            @RequestBody UserWxliteInfoBody userWxliteInfo) throws NoneMatchException {

        WxliteService.Referer ref = wxliteService.getReferer(referer);
        String appId = ref.getAppId();
        WxliteService.LoginRes loginRes = wxliteService.getOpenId(code, appId);
        String openId = loginRes.getOpenId();
        String unionId = loginRes.getUnionId();
        redisTemplate.opsForValue().set(Keys.wxliteSessionKey.getKey(openId), loginRes.getSessionKey(), Const.redisKeyExpireDays, TimeUnit.DAYS);

        UserWxService.WxOpenInfoDto wxOpenInfoDto = userWxService.findByWxId(appId, openId, unionId);
        if (wxOpenInfoDto == null) {
            return R.ok(400, "该微信小程序还未绑定用户", ImmutableMap.of("openId", openId));
        }
        User user = userRepository.findById(wxOpenInfoDto.getUserId()).get();
        // unionId有更新,update自身openInfo
        if (StringUtils.isEmpty(wxOpenInfoDto.getUnionId()) && StringUtils.isNotEmpty(unionId)) {
            wxOpenInfoDto.setUnionId(unionId);
            userWxService.saveUserWxOpenInfo(wxOpenInfoDto.getUserId(), wxOpenInfoDto.getAppId(),
                    wxOpenInfoDto.getOpenId(), wxOpenInfoDto.getUnionId(), wxOpenInfoDto.getVersion());
        }
        // 通过别的应用(unionId)获取的user,create新openInfo
        if (!wxOpenInfoDto.getAppId().equals(appId)) {
            userWxService.saveUserWxOpenInfo(user.getUserId(), appId, openId, unionId, ref.getWxliteVersion());
        }
        val mobileNo = user.getMobileNo();
        val key = Keys.AccessToken.getKey(mobileNo);
        String accessToken = user.getAccessToken();

        // 登录过期,或切换设备需重新登录

        UserWxService.WxInfoDto oldWxliteInfo = userWxService.findByMobileNo(mobileNo);
        String model = userWxliteInfo.getModel();
        boolean defModel = oldWxliteInfo != null && !oldWxliteInfo.getModel().equals(model) &&
                !oldWxliteInfo.getWxliteVersion().equals("devtools") && !oldWxliteInfo.getWxliteVersion().equals("0");

        if (!accessAspect.validateAccessToken(mobileNo, accessToken) || defModel) {
            String msg;
            if (defModel) log.debug(msg = "【{}】设备更换【{}】=>【{}】", mobileNo, oldWxliteInfo.getModel(), model);
            else log.debug(msg = ("【" + mobileNo + "】用户登录过期"));
            UserWxService.WxInfoDto wxInfoDto = ConvertUtil.to(userWxliteInfo, UserWxService.WxInfoDto.class);
            userWxService.updateUserWxliteInfo(new UserWxService.Param(wxInfoDto, ref));
            return R.ok(400, "需要重新登录:" + msg, ImmutableMap.of("openId", openId));
        }
        //用户换手机号了也永远不会重新登录
        //redisTemplate.opsForValue().set(key, accessToken, Long.parseLong(ConfigConstants.validity_period.getConstant()), TimeUnit.DAYS);
        log.debug("redis里用户【{}】冷启动时刷新口令时间:{}", mobileNo, accessToken);

        user.setAccessToken(accessToken)
                .setDeviceId(WxliteService.WX.weixin.name())
                .setActive(true)
                .setWxliteOpenId(openId);

        userRepository.save(user);

        UserWxService.WxInfoDto wxInfoDto = ConvertUtil.to(userWxliteInfo, UserWxService.WxInfoDto.class);
        userWxService.updateUserWxliteInfo(new UserWxService.Param(wxInfoDto, ref));

        UserResponse userResponse = userInnerService.getUserResponse(user);
        return R.ok(200, "已绑定手机号码,登录成功", userResponse);
    }

    @Data
    private static class UserWxliteInfoBody {
        private String mobileNo;
        private String sdkVersion = "";
        private String version = "";
        private String platform = "";
        private String system = "";
        private String brand = "";
        private String model = "";
        private String pixelRatio = "";
        private String language = "";
        private String fontSizeSetting = "";
        private String wxliteVersion;
        private String appId;
    }

}


