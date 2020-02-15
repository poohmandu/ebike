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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qdigo.ebike.api.service.third.address.AmapService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.R;
import com.qdigo.ebike.common.core.util.http.NetUtil;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.domain.entity.UserAddress;
import com.qdigo.ebike.usercenter.domain.entity.UserFeedback;
import com.qdigo.ebike.usercenter.repository.UserFeedbackRepository;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import com.qdigo.ebike.usercenter.service.inner.UserAddressService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Description: 
 * date: 2020/1/6 7:25 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/userInfo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserInfoResource {

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserAddressService addressService;
    private final AmapService amapService;
    private final UserService userService;
    private final UserFeedbackRepository feedbackRepository;

    /**
     * 修改手机号
     *
     * @param deviceId
     * @param accessToken
     * @param form
     * @return
     */
    @Transactional
    @AccessValidate
    @PostMapping(value = "/updateMobile", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> updateMobile(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody UserInfoMobileUpdateForm form) {

        //val newMobileNo = form.getNewMobileNo();
        //
        //if (userRepository.findOneByMobileNo(newMobileNo).isPresent()) {
        //    return ResponseEntity.ok().body(new BaseResponse(400, newMobileNo + "的手机号已经注册过电滴账号", null));
        //}
        //
        //val user = userRepository.findOneByMobileNo(mobileNo)
        //    .orElseThrow(() -> new NullPointerException("未查询到" + mobileNo + "的用户"));
        //user.setMobileNo(newMobileNo);
        //userRepository.save(user);

        return R.ok("成功修改用户信息");
    }

    @Transactional
    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> logout(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {
        userRepository.findOneByMobileNo(mobileNo).ifPresent(user -> {
            String key = Keys.AccessToken.getKey(mobileNo);
            redisTemplate.delete(key);
            user.setAccessToken("");
            userRepository.save(user);
        });
        return R.ok("成功注销");
    }

    @AccessValidate
    @GetMapping(value = "/getMessage", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getMessage(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        User user = userRepository.findOneByMobileNo(mobileNo).get();

        List<MyMessage> resList = new ArrayList<>();
        user.getUserRecord().forEach(e -> {
            MyMessage res = new MyMessage();
            res.setRecord(e.getRecord());
            res.setDate(e.getCurTime());
            resList.add(res);
        });
        return R.ok(200, "成功返回用户使用记录", resList);
    }

    @RequestMapping(value = "/feedback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> feedback(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Feedback form) {
        return userRepository.findOneByMobileNo(mobileNo).map(user -> {
            if (StringUtils.isNotBlank(form.getFeedback())) {
                UserFeedback feedback = new UserFeedback();
                feedback.setFeedback(form.getFeedback());
                feedback.setUser(user);
                feedback.setTime(new Date());
                feedbackRepository.save(feedback);
            }
            return R.ok();
        }).orElseGet(() -> R.ok(400, "请先登录,再反馈意见"));
    }


    @AccessValidate
    @PostMapping(value = "/userAddress", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> postUserAddress(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Location location) {
        String remoteIp = NetUtil.getRemoteIp();
        log.debug("{}用户上传的经纬度{},IP地址为:{}", mobileNo, location, remoteIp);

        Optional<UserAddress> optional = addressService.getUserAddress(location.getLatitude(), location.getLongitude(), mobileNo, remoteIp);

        return optional.map(userAddress -> R.ok(200, "成功更新用户位置信息", userAddress))
                .orElseGet(() -> R.ok(400, "更新位置信息失败"));
    }


    @Data
    private static class UserInfoMobileUpdateForm {
        private String identityNo;
        private String newMobileNo;
    }

    @Data
    private static class MyMessage {
        private String record;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date date;
    }


    @Data
    private static class Feedback {
        private String feedback;
    }

    @Data
    private static class Location {
        private double latitude;
        private double longitude;
    }

}

