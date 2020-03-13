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


import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import com.qdigo.ebike.usercenter.service.inner.UserInnerService;
import com.qdigo.ebike.usercenter.service.inner.UserStatusInnerService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Slf4j
@RestController
@RequestMapping("/v1.0/userInfo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UpdateUserInfo {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final UserInnerService userService;
    private final UserStatusInnerService userStatusInnerService;

    /**
     * 修改用户信息
     *
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param body
     * @return
     * @author niezhao
     */
    @AccessValidate
    @Transactional
    @PostMapping(value = "/updateInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> updateUserInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body body) {

        //val newMobileNo = body.getMobileNo();
        //if (!newMobileNo.equals(mobileNo) && userRepository.findOneByMobileNo(newMobileNo).isPresent()) {
        //    return ResponseEntity.ok().body(new BaseResponse(400, newMobileNo + "的手机号已经注册过电滴账号", null));
        //}
        //
        //User user = userRepository.findOneByMobileNo(mobileNo).orElse(null);
        //
        //user.setFullName(body.getUserName());
        //user.setMobileNo(newMobileNo);
        //
        //user = userRepository.save(user);
        //String studentAuth = userStudentService.getUserStudentAuthStatus(mobileNo);
        //
        //UserResponse res = UserResponse.build(user, studentAuth);

        return R.ok(200, "成功修改用户信息");
    }


    @Data
    private static class Body {
        private String userName;
        private String mobileNo;
    }
}
