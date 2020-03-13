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

package com.qdigo.ebike.activitycenter.service.inner.invite;

import com.qdigo.ebike.api.domain.dto.third.wx.wxpush.InviteResult;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.third.push.PushService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.activitycenter.domain.entity.Invite;
import com.qdigo.ebike.activitycenter.repository.InviteRepository;
import com.qdigo.ebike.activitycenter.service.inner.coupon.CouponService;
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
    private final PushService pushService;

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
                    InviteResult inviteResult = InviteResult.builder().invitee(FormatUtil.formatMobileNo(user.getMobileNo()))
                            .inviter(FormatUtil.formatMobileNo(inviter.getMobileNo()))
                            .reward(Const.inviteReward + "张1元骑行券")
                            .note("你邀请的新用户开始了骑行,你获得了骑行券。请在有效时间内使用")
                            .validDate(FormatUtil.y_M_d.format(new Date(System.currentTimeMillis() + 604800000)) + "前")
                            .build();
                    PushService.Param param = PushService.Param.builder().mobileNo(inviter.getMobileNo())
                            .pushType(Const.PushType.inviteFinished).alert("你邀请的新用户开始了骑行,你获得了骑行券")
                            .data(inviteResult).deviceId(inviter.getDeviceId())
                            .build();
                    pushService.pushNotation(param);
                })
                .peek(invite -> invite.setFinished(true))
                .collect(Collectors.toList());

        if (!invites.isEmpty()) {
            inviteRepository.saveAll(invites);
        }
    }

}
