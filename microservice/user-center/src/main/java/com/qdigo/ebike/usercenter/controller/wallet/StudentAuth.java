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

package com.qdigo.ebike.usercenter.controller.wallet;

import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.domain.entity.UserStudent;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import com.qdigo.ebike.usercenter.repository.UserStudentRepository;
import com.qdigo.ebike.usercenter.service.inner.UserStatusInnerService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by niezhao on 2017/9/5.
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/ebike/student")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class StudentAuth {

    private final UserRepository userRepository;
    private final UserStudentRepository userStudentRepository;
    private final UserStatusInnerService userStatusInnerService;

    @AccessValidate
    @GetMapping(value = "/stuAuthInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> stuAuthInfo(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        User user = userRepository.findOneByMobileNo(mobileNo).get();
        UserStudent student = userStudentRepository.findByMobileNo(mobileNo);
        if (student == null) {
            return R.ok(201, "该用户还没有认证");
        }

        Map<String, String> map = new ImmutableMap.Builder<String, String>()
                .put("mobileNo", mobileNo)
                .put("realName", user.getRealName())
                .put("address", MessageFormat.format("{0} {1} {2}", student.getProvince(), student.getCity(), student.getDistrict()))
                .put("stuNo", student.getStudentNo())
                .put("schoolName", student.getSchoolName())
                .put("stuImg", student.getStuIdImg())
                .build();

        return R.ok(200, "获取student信息", map);
    }

    @PostMapping(value = "/discardStuAuth", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> discardStuAuth(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        userStatusInnerService.discardUserStudentAuth(mobileNo);
        return R.ok(200, "取消学生认证");
    }

    @PostMapping(value = "/studentAuth", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> studentAuth(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body body) {

        if (body.getStuIdImg().isEmpty()) {
            return R.ok(400, "必须上传相应图片");
        }

        UserStudent stu = userStudentRepository.findByMobileNo(mobileNo);
        if (stu == null) {
            stu = new UserStudent();
        }
        stu.setStuIdImg(body.getStuIdImg())
                .setAuthStatus(Status.StuAuthStatus.pending)
                .setCity(body.getCity())
                .setDistrict(body.getDistrict())
                .setMobileNo(mobileNo)
                .setProvince(body.getProvince())
                .setSchoolName(body.getSchoolName())
                .setStudentNo(body.getStudentNo())
                .setFailMsg("")
                .setApplyStartTime(new Date());

        userStudentRepository.save(stu);

        return R.ok(200, "成功保存学生信息，等待审核通过", stu);
    }

    @Data
    private static class Body {
        private String studentNo;
        private String province;
        private String city;
        private String district;
        private String schoolName;
        private String stuIdImg;
    }


}
