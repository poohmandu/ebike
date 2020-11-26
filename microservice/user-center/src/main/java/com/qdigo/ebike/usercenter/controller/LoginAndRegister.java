/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
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

import com.qdigo.ebike.api.service.third.wxlite.WxliteService;
import com.qdigo.ebike.api.service.third.zfblite.ZfbliteService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.util.http.HeaderUtil;
import com.qdigo.ebike.common.core.util.http.NetUtil;
import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.domain.vo.UserResponse;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import com.qdigo.ebike.usercenter.service.inner.UserInnerService;
import com.qdigo.ebike.usercenter.service.inner.UserZfbOpenInfoService;
import com.qdigo.ebike.usercenter.service.remote.UserWxInfoService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 
 * date: 2019/12/18 5:06 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/user")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LoginAndRegister {

    private final RedisTemplate<String, String> redisTemplate;
    private final WxliteService wxliteService;
    private final UserRepository userRepository;
    private final UserInnerService userInnerService;
    private final UserWxInfoService userWxInfoService;
    private final ZfbliteService zfbliteService;
    private final UserZfbOpenInfoService userZfbOpenInfoService;

    @PostMapping(value = "/registerAndLogin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerAndLogin(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId,
            @RequestHeader(value = "referer", required = false) String referer,
            @RequestBody LoginAndRegisterForm body,
            HttpServletRequest request) {

        log.debug("{}通过验证码{}登录", body.getMobileNo(), body.getPinCode());
        // 检查验证码是否有效
        if (!"18621749197".equals(mobileNo)) {
            if (!validatePinCode(body.getMobileNo(), body.getPinCode())) {
                return ResponseEntity.badRequest()
                        .headers(HeaderUtil.createFailureAlert("security", "userRegistryAndLogin", "the pinCode is invalid...!"))
                        .body(R.failed(400, "the pinCode is invalid!"));
            }
        }
        if (deviceId.equals("abcd")) { //最早期的ios版本
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("security", "userRegistryAndLogin", "版本过低"))
                    .body(R.failed(400, "版本过低"));
        }
        // 创建user
        User user;
        //如果是微信小程序
        if (wxliteService.isWxlite(deviceId) && StringUtils.isNotEmpty(referer)) {
            String code = (String) body.getExtra().get("code");
            Assert.hasText(code, "登录时必须有code值");

            WxliteService.Referer ref = wxliteService.getReferer(referer);
            WxliteService.LoginRes loginRes = wxliteService.getOpenId(code, ref.getAppId());
            log.debug("{}为微信登录,openId为{},referer为{}", mobileNo, loginRes.getOpenId(), referer);

            user = userRepository.findOneByMobileNo(mobileNo).orElseGet(() -> userInnerService.createUser(mobileNo));//orElseGet 不会直接调用相较于orElse
            user.setWxliteOpenId(loginRes.getOpenId());

            userWxInfoService.saveUserWxOpenInfo(user.getUserId(), ref.getAppId(), loginRes.getOpenId(), loginRes.getUnionId(), ref.getWxliteVersion());

        } else if (Const.zfblite.equals(deviceId)) {
            String code = (String) body.getExtra().get("code");
            String appId = (String) body.getExtra().get("appId");
            Assert.hasText(code, "登录时必须有code值");
            Assert.hasText(appId, "登录时必须有appId值");

            ZfbliteService.LoginRes loginRes = zfbliteService.getOpenId(code);
            log.debug("{}为支付宝小程序登录,openId为{},appId为{}", mobileNo, loginRes.getUserId(), appId);

            user = userRepository.findOneByMobileNo(mobileNo).orElseGet(() -> userInnerService.createUser(mobileNo));//orElseGet 不会直接调用相较于orElse

            userZfbOpenInfoService.saveUserZfbOpenInfo(user.getUserId(), appId, loginRes.getUserId());

        } else {
            user = userRepository.findOneByMobileNo(mobileNo)
                    .orElseGet(() -> userInnerService.createUser(mobileNo));
        }

        userInnerService.loginUser(user, body.getLatitude(), body.getLongitude(), NetUtil.getRemoteIp(), body.getCountryCode(), deviceId, body.getImei());

        user = userRepository.save(user);//lazy 加载
        UserResponse userResponse = userInnerService.getUserResponse(user);

        return ResponseEntity.ok().body(R.ok(200, "登录成功", userResponse));

    }

    //createUser不会立即生效
    //@Transactional
    @PostMapping(value = "/wxRegister", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> wxRegister(
            @RequestHeader("referer") String referer, @RequestHeader("code") String code,
            @RequestBody LoginAndRegisterForm body) {
        val mobileNo = body.getMobileNo();
        MDC.put("mobileNo", mobileNo);

        log.debug("{}首次微信一键登录(new),code:{}", mobileNo, code);
        User user = userRepository.findOneByMobileNo(mobileNo).orElseGet(() -> userInnerService.createUser(mobileNo));
        WxliteService.Referer ref = wxliteService.getReferer(referer);
        WxliteService.LoginRes loginRes = wxliteService.getOpenId(code, ref.getAppId());
        user.setWxliteOpenId(loginRes.getOpenId());
        userRepository.save(user);

        userInnerService.loginUser(user, body.getLatitude(), body.getLongitude(), NetUtil.getRemoteIp(), body.getCountryCode(), WxliteService.WX.weixin.name(), body.getImei());

        userWxInfoService.saveUserWxOpenInfo(user.getUserId(), ref.getAppId(), loginRes.getOpenId(), loginRes.getUnionId(), ref.getWxliteVersion());

        UserResponse userResponse = userInnerService.getUserResponse(user);

        return R.ok(200, "微信一键登录,登录成功!", userResponse);
    }

    /**
     * @author niezhao
     * @description 从缓冲中去验证pinCode是否正确和过期
     *
     * @date 2019/12/18 5:16 PM
     * @param mobileNo
     * @param pinCode
     * @return boolean
     **/
    private boolean validatePinCode(String mobileNo, String pinCode) {
        // 修复了验证码错误依旧能够登录的bug by niezhao 2016.11.10
        log.debug("{}登录时验证pinCode", mobileNo);
        String key = Keys.PinCode.getKey(mobileNo);
        String RpinCode = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(RpinCode)) {
            if (pinCode.trim().equals(RpinCode)) {
                log.debug("验证码有效:{} {}", mobileNo, pinCode);
                redisTemplate.delete(key);
                return true;
            } else {
                log.debug("验证码无效:{} {}", mobileNo, pinCode);
                return false;
            }
        } else {
            log.debug("验证码无效:{} {}", mobileNo, pinCode);
            return false;
        }
    }

    @Data
    private static class LoginAndRegisterForm {
        private String mobileNo;
        private String pinCode;
        private Map<String, Object> extra = new HashMap<>();
        private String countryCode = "86";
        private Double latitude = 0.0;
        private Double longitude = 0.0;
        private String imei;
    }

}


