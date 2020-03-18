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

package com.qdigo.ebike.activitycenter.controller;

import com.google.common.collect.ImmutableMap;
import com.qdigo.ebike.activitycenter.domain.entity.Invite;
import com.qdigo.ebike.activitycenter.domain.entity.coupon.Coupon;
import com.qdigo.ebike.activitycenter.domain.entity.coupon.CouponTemplate;
import com.qdigo.ebike.activitycenter.repository.CouponRepository;
import com.qdigo.ebike.activitycenter.repository.InviteRepository;
import com.qdigo.ebike.activitycenter.service.inner.coupon.CouponInnerService;
import com.qdigo.ebike.activitycenter.service.inner.invite.InviteInnerService;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by niezhao on 2018/1/22.
 */
@Slf4j
@RestController
@RequestMapping("/v1.0/ebike/coupon")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CouponRest {

    private final UserService userService;
    private final InviteRepository inviteRepository;
    private final OrderRideService rideService;
    private final CouponInnerService couponInnerService;
    private final InviteInnerService inviteInnerService;
    private final CouponRepository couponRepository;


    @AccessValidate
    @GetMapping(value = "/onShare/{mobile}", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> onShare(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken, @PathVariable String mobile) {

        UserDto invitee = userService.findByMobileNo(mobileNo);

        Optional<Invite> inviteOptional = inviteRepository.findFirstByInviteeId(invitee.getUserId());
        if (inviteOptional.isPresent()) {
            return R.ok(400, "您已经被邀请过,不可重复领取");
        }
        RideDto rideDto = rideService.findAnyByMobileNo(mobileNo);
        if (rideDto != null) {
            return R.ok(401, "只有新用户才能领取哦");
        }

        UserDto inviter = userService.findByMobileNo(mobile);
        if (inviter == null) {
            return R.ok(402, "邀请者不存在");
        }
        if (inviter.getMobileNo().equals(invitee.getMobileNo())) {
            return R.ok(406, "邀请自己无效");
        }
        List<Coupon> inviteCoupons = couponInnerService.findInviteCoupons(inviter.getUserId());
        int number = Const.inviteReward;
        if (inviteCoupons == null) {
            return R.ok(404, "邀请活动不存在");
        } else {
            Integer maxCirculation = inviteCoupons.stream().findAny()
                    .map(coupon -> coupon.getCouponTemplate().getMaxCirculation())
                    .orElse(18);
            if (inviteCoupons.size() + number > maxCirculation) {
                return R.ok(403, "邀请者获得奖励已满");
            }
        }
        // 生成骑行券,邀请者赠给被邀请者 so 邀请者买单
        Long agentId = inviter.getAgentId();
        couponInnerService.createInviteCoupons(invitee.getUserId(), agentId, number);
        // 保存邀请关系
        inviteInnerService.createInvite(inviter.getUserId(), invitee.getUserId());
        return R.ok(200, "成功获得邀请奖励");
    }

    @AccessValidate
    @GetMapping(value = "/exchange/{no}", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> exchange(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken, @PathVariable String no) {

        UserDto inviter = userService.findByMobileNo(mobileNo);

        if (inviter == null) {
            return R.ok(402, "该兑换码不存在");
        }
        UserDto invitee = userService.findByMobileNo(mobileNo);
        if (inviter.getMobileNo().equals(invitee.getMobileNo())) {
            return R.ok(406, "不可以填写自己的兑换码");
        }
        Optional<Invite> inviteOptional = inviteRepository.findFirstByInviteeId(invitee.getUserId());
        if (inviteOptional.isPresent()) {
            return R.ok(400, "您已经被邀请过,不可重复领取");
        }
        RideDto rideDto = rideService.findAnyByMobileNo(mobileNo);
        if (rideDto != null) {
            return R.ok(401, "只有新用户才能领取哦");
        }
        List<Coupon> inviteCoupons = couponInnerService.findInviteCoupons(inviter.getUserId());
        int number = Const.inviteReward;
        if (inviteCoupons == null) {
            return R.ok(404, "邀请活动不存在");
        } else {
            Integer maxCirculation = inviteCoupons.stream().findAny()
                    .map(coupon -> coupon.getCouponTemplate().getMaxCirculation())
                    .orElse(18);
            if (inviteCoupons.size() + number > maxCirculation) {
                return R.ok(403, "邀请者获得奖励已满");
            }
        }
        // 生成骑行券
        Long agentId = inviter.getAgentId();
        couponInnerService.createInviteCoupons(invitee.getUserId(), agentId, Const.inviteReward);
        inviteInnerService.createInvite(inviter.getUserId(), invitee.getUserId());
        return R.ok(200, "成功获得邀请奖励");
    }

    @AccessValidate
    @GetMapping(value = "/inviteList", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> inviteList(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        UserDto inviter = userService.findByMobileNo(mobileNo);

        List<Invite> inviteList = inviteRepository.findByInviterId(inviter.getUserId());
        long total = inviteList.size();
        long finished = inviteList.stream().filter(Invite::isFinished).count();
        Map<String, Long> map = ImmutableMap.of("total", total, "finished", finished);
        return R.ok(200, "获得邀请信息", map);
    }


    @AccessValidate
    @GetMapping(value = "/couponSize", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> couponSize(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {
        UserDto user = userService.findByMobileNo(mobileNo);

        List<Coupon> coupons = couponRepository.findByUserId(user.getUserId());
        long count = coupons.stream().filter(Coupon::isValid).count();
        Map<String, Object> map = ImmutableMap.of("size", coupons.size(), "valid", count);
        return R.ok(200, "获得优惠券数量", map);
    }

    @AccessValidate
    @GetMapping(value = "/couponList", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> couponList(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @PageableDefault(size = 1, sort = {"valid", "endTime"}, direction = Sort.Direction.DESC) Pageable pageable) {
        UserDto user = userService.findByMobileNo(mobileNo);
        Page<Coupon> couponPage = couponRepository.findByUserId(user.getUserId(), pageable);
        Date now = new Date();
        Page<Item> result = couponPage.map(coupon -> {
            if (now.after(coupon.getEndTime()) && coupon.isValid()) {
                coupon.setValid(false);
                couponRepository.save(coupon);
            }
            Item item = new Item();
            CouponTemplate template = coupon.getCouponTemplate();
            item.setName(template.getName());
            item.setType(template.getType());
            item.setDst(template.getDst().val);
            item.setEndTime(FormatUtil.yMdHms.format(coupon.getEndTime()));
            item.setValid(coupon.isValid());
            item.setRedeemed(coupon.isRedeemed());
            item.setAmountOff(template.getAmountOff());
            item.setPercentOff(template.getPercentOff());
            return item;
        });
        return R.ok(200, "获得骑行券分页信息", result);
    }

    @Data
    private static class Item {
        private String name;
        private CouponTemplate.Type type;
        private String dst; //需要后端格式化
        private String endTime;
        private Boolean valid;
        private Boolean redeemed;
        private Double amountOff;
        private Double percentOff;
    }

}
