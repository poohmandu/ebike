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


import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.third.FraudVerify;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.order.thirdrecord.OrderThirdRecordService;
import com.qdigo.ebike.api.service.third.insurance.DataPayService;
import com.qdigo.ebike.api.service.third.insurance.HmbService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.domain.vo.UserResponse;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import com.qdigo.ebike.usercenter.service.inner.UserInnerService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by niezhao on 2016/11/25.
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/userInfo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AddIdentify {

    private final UserRepository userRepository;
    private final UserInnerService userService;
    private final AgentConfigService agentConfigService;
    private final DataPayService dataPayService;
    private final HmbService hmbService;
    private final OrderThirdRecordService orderThirdRecordService;

    @Transactional
    @AccessValidate
    @PostMapping(value = "/identifyFace", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> identifyFace(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body body) {

        List<User> idNoUsers = userRepository.findByIdNo(body.getIdNo());
        if (idNoUsers.size() > 1 && idNoUsers.stream().noneMatch(u -> u.getMobileNo().equals(mobileNo))) {
            log.debug("mobileNo:{},oldMobileNo:{},身份证被重复使用", mobileNo, idNoUsers);
            return R.ok(403, "该身份证不能被重复使用");
        }
        val realName = body.getRealName().trim();
        Long agentId = userService.getAgentId(mobileNo);
        AgentCfg config = agentConfigService.getAgentConfig(agentId);
        if (this.getAge(body.getIdNo()) < config.getAllowArrears()) {
            return R.ok(404, config.getAllowArrears() + "周岁以下禁止骑车");
        }

        val verify = dataPayService.identifyFace(mobileNo, realName, body.getIdNo(), body.getImageId());
        orderThirdRecordService.insert(new OrderThirdRecordService.Param(agentId, !verify.isError(), OrderThirdRecordService.API.identifyFace));

        if (!StringUtils.equals(verify.getBizCode(), "SYSTEM_002")) {
            if (verify.isError()) {
                return R.ok(401, "发生错误:" + verify.getMsg());
            }
            if (!verify.isSuccess()) {
                return R.ok(402, verify.getMsg());
            }
        }
        User user = userRepository.findOneByMobileNo(mobileNo).get();
        user.setRealName(realName);
        user.setIdNo(body.getIdNo());
        userRepository.save(user);
        UserResponse userResponse = userService.getUserResponse(user);

        return R.ok(200, "人脸比对身份认证完成", userResponse);
    }


    @Transactional
    @AccessValidate
    @PostMapping(value = "/addIdentify", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> addIdentify(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestBody Body body) {

        User user = userRepository.findOneByMobileNo(mobileNo).orElse(null);
        if (user == null) {
            return R.ok(400, "通过mobileNo" + mobileNo + "没有找到用户");
        }
        List<User> idNoUsers = userRepository.findByIdNo(body.getIdNo());
        if (idNoUsers.size() > 1 && idNoUsers.stream().noneMatch(u -> u.getMobileNo().equals(mobileNo))) {
            log.debug("mobileNo{},oldMobileNo{},身份证被重复使用", mobileNo, idNoUsers);
            return R.ok(403, "该身份证不能被重复使用");
        }
        val realName = body.getRealName().trim();
        if (user.getCountryCode().equals("86")) {

            Long agentId = userService.getAgentId(mobileNo);
            AgentCfg config = agentConfigService.getAgentConfig(agentId);
            if (this.getAge(body.getIdNo()) < config.getAllowAge()) {
                return R.ok(404, config.getAllowAge() + "周岁以下禁止骑车");
            }
            FraudVerify verify;
            if (config.getIdentifyType() == Const.IdentifyType.idCard) {
                verify = dataPayService.identifyIdCard(mobileNo, realName, body.getIdNo());
                orderThirdRecordService.insert(new OrderThirdRecordService.Param(agentId, !verify.isError(), OrderThirdRecordService.API.identifyIdCard));
            } else {
                verify = hmbService.identifyIdCard(mobileNo, realName, body.getIdNo());
            }
            if (!StringUtils.equals(verify.getBizCode(), "SYSTEM_002")) {
                if (verify.isError()) {
                    return R.ok(401, "发生错误:" + verify.getMsg());
                }
                if (!verify.isSuccess()) {
                    //身份信息不匹配,请输入与登录手机号相匹配且真实的姓名与身份证号
                    return R.ok(402, verify.getMsg());
                }
            }
        }
        user.setRealName(realName);
        user.setIdNo(body.getIdNo());

        userRepository.save(user);
        UserResponse userResponse = userService.getUserResponse(user);

        return R.ok(200, "身份认证完成", userResponse);
    }

    private int getAge(String idNo) {
        //截取出生年月日
        String birthday = idNo.substring(6, 14);
        //当前日期
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //计算年龄差
        Integer ageBit = Integer.parseInt(date) - Integer.parseInt(birthday);
        //当年龄差的长度大于4位时，即出生年份小于当前年份
        Integer personAge;
        if (ageBit.toString().length() > 4) {
            //截取掉后四位即为年龄
            personAge = Integer.parseInt(ageBit.toString().substring(0, ageBit.toString().length() - 4));
        } else {//当前年份出生，直接赋值为0岁
            personAge = 0;
        }
        log.debug("年龄为" + personAge + "岁");
        return personAge;
    }

    @Data
    private static class Body {
        private String realName;
        private String idNo;
        private String imageId;
    }

}
