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

package com.qdigo.ebike.controlcenter.service.invite;

import com.qdigo.ebicycle.domain.user.User;
import com.qdigo.ebicycle.repository.userRepo.UserRepository;
import com.qdigo.ebicycle.service.push.PushService;
import com.qdigo.ebicycle.service.push.WxlitePush;
import com.qdigo.ebicycle.service.util.FormatUtil;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.controlcenter.domain.entity.Invite;
import com.qdigo.ebike.controlcenter.repository.InviteRepository;
import com.qdigo.ebike.controlcenter.service.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by niezhao on 2018/1/24.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class InviteService {

    private final InviteRepository inviteRepository;
    private final CouponService couponService;
    private final UserService userService;

    @Inject
    private UserRepository userRepository;
    @Inject
    private UserService userService;
    @Inject
    private PushService pushService;

    @Transactional
    public void createInvite(long inviterId, long inviteeId) {
        Invite invite = new Invite();
        invite.setCreated(System.currentTimeMillis());
        invite.setFinished(false);
        invite.setInviteeId(inviteeId);
        invite.setInviterId(inviterId);
        inviteRepository.save(invite);
    }

    @Transactional //扫码时使用
    public void finishInvite(UserDto user) {
        // 被多个人邀请
        Long agentId = user.getAgentId();

        List<Invite> invites = inviteRepository.findByInviteeIdAndFinishedIsFalse(user.getUserId()).parallelStream()
                .peek(invite -> {
                    long inviterId = invite.getInviterId();
                    couponService.createInviteCoupons(inviterId, agentId, Const.inviteReward);

                    UserDto inviter = userService.findById(inviterId);

                    WxlitePush.InviteResult inviteResult = new WxlitePush.InviteResult();
                    inviteResult.setInvitee(FormatUtil.formatMobileNo(user.getMobileNo()));
                    inviteResult.setInviter(FormatUtil.formatMobileNo(inviter.getMobileNo()));
                    inviteResult.setReward(Const.inviteReward + "张1元骑行券");
                    inviteResult.setNote("你邀请的新用户开始了骑行,你获得了骑行券。请在有效时间内使用");
                    inviteResult.setValidDate(FormatUtil.y_M_d.format(new Date(System.currentTimeMillis() + 604800000)) + "前");
                    pushService.pushNotation(inviter, "你邀请的新用户开始了骑行,你获得了骑行券", Const.PushType.inviteFinished, inviteResult);
                })
                .peek(invite -> invite.setFinished(true))
                .collect(Collectors.toList());

        if (!invites.isEmpty()) {
            inviteRepository.saveAll(invites);
        }
    }

}
