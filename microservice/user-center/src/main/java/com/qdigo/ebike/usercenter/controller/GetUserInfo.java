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

import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.commonaop.aspects.AccessAspect;
import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.domain.entity.UserAccount;
import com.qdigo.ebike.usercenter.domain.vo.UserResponse;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import com.qdigo.ebike.usercenter.service.inner.UserInnerService;
import com.qdigo.ebike.usercenter.service.inner.UserStatusInnerService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.inject.Inject;

@Slf4j
@RestController
@RequestMapping("/v1.0/userInfo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetUserInfo {

    private final UserRepository userRepository;
    private final UserInnerService userService;
    private final UserStatusInnerService userStatusInnerService;
    @Resource
    private AccessAspect accessAspect;

    /**
     * 获取用户信息
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @return
     * @author niezhao
     */
    @AccessValidate
    @GetMapping(value = "/getInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getUserInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        log.debug("获取用户{}的信息", mobileNo);
        User user = userRepository.findOneByMobileNo(mobileNo).orElse(null);

        UserResponse.UserInfo userInfo = userService.getUserResponse(user).toUserInfo();

        return R.ok(200, "成功获取用户信息", userInfo);
    }

    @GetMapping(value = "/getStep", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getStep(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {
        Step step;
        if (!accessAspect.validateAccessToken(mobileNo, accessToken)) {
            step = Step.builder().name(Status.Step.login.name()).val(Status.Step.login.getVal()).build();
            return R.ok(200, "成功获取用户认证进度", step);
        }
        User user = userRepository.findOneByMobileNo(mobileNo).get();

        Status.Step statusStep = userStatusInnerService.getStep(user);

        boolean wxscoreEnable = userStatusInnerService.getUserWxscoreEnableCache(user);
        UserAccount account = user.getAccount();
        double totalBalance = account.getBalance() + account.getGiftBalance();

        step = Step.builder()
                .name(statusStep.name())
                .val(statusStep.getVal())
                .wxscoreEnable(wxscoreEnable)
                .zmScoreEnable(userStatusInnerService.getUserScoreEnable(user))
                .studentEnable(userStatusInnerService.getUserStudentEnable(mobileNo))
                .totalBalance(totalBalance)
                .build();

        if (statusStep == Status.Step.finished && wxscoreEnable) {
            String outOrderNo = userStatusInnerService.hasNoFinishedWxscore(user);
            step.setOutOrderNo(outOrderNo);
        }

        return R.ok(200, "成功获取用户认证进度", step);
    }

    @Data
    @Builder
    private static class Step {
        private String name;
        private int val;
        private Boolean wxscoreEnable;
        private Boolean zmScoreEnable;
        private Boolean studentEnable;
        private double totalBalance;
        private String outOrderNo;
    }

}


