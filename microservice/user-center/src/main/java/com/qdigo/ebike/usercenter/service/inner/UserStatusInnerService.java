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

import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import com.qdigo.ebike.api.service.order.longrent.OrderLongRentService;
import com.qdigo.ebike.api.service.third.wxlite.WxliteService;
import com.qdigo.ebike.api.service.third.wxlite.WxscoreService;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.Ctx;
import com.qdigo.ebike.commonaop.annotations.ThreadCache;
import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.domain.entity.UserAccount;
import com.qdigo.ebike.usercenter.domain.entity.UserStudent;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import com.qdigo.ebike.usercenter.repository.UserStudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.inject.Inject;

/**
 *
 * @author niezhao
 * @date 2017/9/7
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserStatusInnerService {

    private final UserStudentRepository userStudentRepository;
    private final UserRepository userRepository;
    private final WxliteService wxliteService;
    private final OrderLongRentService longRentService;
    private final RedisTemplate<String, String> redisTemplate;
    private final WxscoreService wxScoreService;
    @Resource
    private UserStatusInnerService self;
    @Resource
    private UserInnerService userInnerService;

    @Transactional
    public void discardUserStudentAuth(String mobileNo) {
        UserStudent userStudent = userStudentRepository.findByMobileNo(mobileNo);
        if (userStudent != null) {
            userStudent.setAuthStatus(Status.StuAuthStatus.discard);
            userStudentRepository.save(userStudent);
        }
    }

    @Transactional
    public void discardZmScore(String mobileNo) {
        userRepository.findOneByMobileNo(mobileNo).ifPresent(user -> {
            user.getAccount().setZmScore("");
            userRepository.save(user);
        });
    }

    public String getUserStudentAuthStatus(String mobileNo) {
        UserStudent student = userStudentRepository.findByMobileNo(mobileNo);
        String studentAuth;
        if (student != null) {
            studentAuth = student.getAuthStatus().name();
        } else {
            studentAuth = "";
        }
        return studentAuth;
    }

    @ThreadCache(key = "mobileNo")
    public boolean getUserStudentEnable(String mobileNo) {
        val stu = userStudentRepository.findByMobileNo(mobileNo);
        return stu != null && stu.getAuthStatus() == Status.StuAuthStatus.success;
    }

    @ThreadCache(key = "userId")
    public boolean getUserScoreEnable(User user) {
        String zmScore = user.getAccount().getZmScore();
        //修改过,现在只要不为空就算通过了芝麻信用
        return StringUtils.isNotBlank(zmScore);
    }


    //@CatAnnotation
    @ThreadCache(key = {"userId"})
    public boolean getUserWxscoreEnableCache(User user) {
        if (!wxliteService.isWxlite(user.getDeviceId()))
            return false;
        UserAccount account = user.getAccount();
        return account.getWxscore().equals("AVAILABLE");
    }

    // 微信支付分与长租卡互斥 这里只单纯做支付分判断
    //@CatAnnotation
    @ThreadCache(key = {"userId"})
    public boolean getUserWxscoreEnable(User user) {
        if (!wxliteService.isWxlite(user.getDeviceId()))
            return false;
        String appId = wxliteService.getAppId(user.getDeviceId());
        String openId = userInnerService.getCurWxOpenId(user);
        ResponseDTO<Boolean> dto = wxScoreService.userServiceState(appId, openId);
        return dto.isSuccess() && dto.getData();
    }

    //@CatAnnotation
    @ThreadCache(key = {"userId"})
    public String hasNoFinishedWxscore(User user) {
        String key = Keys.flagWxscoreCreate.getKey(String.valueOf(user.getUserId()));
        String outOrderNo = redisTemplate.opsForValue().get(key);
        if (outOrderNo == null) {
            return null;
        }
        String appId = wxliteService.getAppId(user.getDeviceId());
        ResponseDTO<WxscoreOrder> dto = wxScoreService.queryByOrderNo(outOrderNo, appId);
        if (dto.isSuccess()) {
            WxscoreOrder wxscoreOrder = dto.getData();
            String state = wxscoreOrder.getState();
            if (state.equals(WxscoreService.State.USER_PAID.name()) ||
                    state.equals(WxscoreService.State.REVOKED.name()) ||
                    state.equals(WxscoreService.State.EXPIRED.name())) {
                redisTemplate.delete(key);
                return null;
            }
        }
        return outOrderNo;
    }


    //默认已经登录
    //@CatAnnotation
    public Status.Step getStep(User user) {
        UserAccount account = user.getAccount();
        double totalBalance = account.getBalance() + account.getGiftBalance();
        Boolean wxscoreEnable = self.getUserWxscoreEnableCache(user);

        if (wxscoreEnable && totalBalance >= 0) //新用户为0
            return Status.Step.finished; //为负还是需要先补足

        Boolean zmScoreEnable = self.getUserScoreEnable(user);
        Boolean studentEnable = self.getUserStudentEnable(user.getMobileNo());
        log.debug("信用认证阶段,押金:{},芝麻信用分:{},学生认证:{}", account.getDeposit(), zmScoreEnable, studentEnable);
        if (!wxscoreEnable && account.getDeposit() <= 0 && !zmScoreEnable && !studentEnable)
            return Status.Step.deposit;

        OrderLongRentService.LongRentDto longRent = Ctx.get("longRent", () -> longRentService.findValidByUserId(user.getUserId()));
        if (account.getBalance() + account.getGiftBalance() <= 0 && longRent == null)
            return Status.Step.balance;

        return Status.Step.finished;
    }


}
