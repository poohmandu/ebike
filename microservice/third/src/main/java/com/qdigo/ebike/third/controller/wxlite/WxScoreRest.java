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

package com.qdigo.ebike.third.controller.wxlite;

import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.third.wxlite.WxliteService;
import com.qdigo.ebike.api.service.third.wxlite.WxscoreService;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.api.service.user.UserStatusService;
import com.qdigo.ebike.common.core.util.R;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import com.qdigo.ebike.third.service.wxlite.WxScoreServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Slf4j
@RestController
@RequestMapping("/v1.0/wxlite/wxScore")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WxScoreRest {

    private final UserAccountService userAccountService;
    private final UserStatusService userStatusService;
    private final WxScoreServiceImpl wxScoreService;
    private final WxliteService wxliteService;
    private final UserService userService;

    //已上线
    @AccessValidate
    @GetMapping(value = "/serviceState")
    public R<Boolean> serviceState(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        boolean wxscoreEnable = userStatusService.getUserWxscoreEnable(mobileNo);
        UserAccountDto accountDto = userAccountService.findByMobileNo(mobileNo);
        if (wxscoreEnable) {
            accountDto.setWxscore("AVAILABLE");
        } else {
            accountDto.setWxscore("UNAVAILABLE");
        }
        userAccountService.update(accountDto);

        return R.ok(200, "成功获取用户微信支付分状态:" + wxscoreEnable, wxscoreEnable);
    }

    //app、小程序使用次接口
    @AccessValidate
    @GetMapping(value = "/wxpayScoreEnable")
    public R<?> wxpayScoreEnable(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken) {

        WxscoreService.WxscoreEnableRes res = wxScoreService.wxpayScoreEnable();

        return R.ok(200, "微信支付分开启服务", res);
    }

    @AccessValidate
    @GetMapping(value = "/startOrder")
    public R<?> startOrder(
            @RequestHeader("referer") String referer,
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestParam String openId) {

        WxliteService.Referer ref = wxliteService.getReferer(referer);
        if (StringUtils.isEmpty(openId)) {
            openId = this.openId(mobileNo, ref.getAppId());
        }
        //ResponseDTO<WxScoreService.WxscoreUseRes> startOrder = wxScoreService.startOrder(ref.getAppId(), openId);
        //if (startOrder.isNotSuccess()) {
        //    return startOrder.toResponse();
        //}
        return R.ok(200, "创建微信支付分免押金订单");
    }

    //@AccessValidate
    //@GetMapping(value = "/completeDepositOrder")
    //public R<Void> completeDepositOrder(
    //        @RequestHeader("referer") String referer,
    //        @RequestHeader("mobileNo") String mobileNo,
    //        @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
    //        @RequestHeader("accessToken") String accessToken,
    //        String outOrderNo) {
    //
    //    String appId = wxliteService.getAppId(deviceId);
    //    ResponseDTO<WxscoreOrder> responseDTO = wxScoreService.queryByOrderNo(outOrderNo, appId);
    //
    //    WxscoreOrder wxscoreOrder = responseDTO.getData();
    //    if (!wxscoreOrder.getState().equals(WxscoreService.State.USER_ACCEPTED.name())) {
    //        wxscoreDaoService.finishOrder(wxscoreOrder);
    //    } else {
    //        WxscoreOrder.Fee fee = wxscoreOrder.getFees().get(0);
    //        fee.setFee_amount(0);
    //
    //
    //        CompleteOrderParam param = CompleteOrderParam.builder()
    //                .appId(appId)
    //                .discounts(Lists.newArrayList())
    //                .fees(wxscoreOrder.getFees())
    //                //.realStartTime()
    //                .finishTicket(wxscoreOrder.getFinish_ticket())
    //                .outOrderNo(outOrderNo)
    //                .build();
    //
    //        ResponseDTO<Void> completeDepositOrder = wxScoreService.completeOrder(param);
    //    }
    //
    //
    //    return ResponseEntity.ok(new BaseResponse(200, "完结微信支付分免先享后付订单"));
    //}

    //@AccessValidate
    //@GetMapping(value = "/queryOrder")
    //public ResponseEntity<BaseResponse> queryOrder(
    //        @RequestHeader("referer") String referer,
    //        @RequestHeader("mobileNo") String mobileNo,
    //        @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
    //        @RequestHeader("accessToken") String accessToken,
    //        @RequestParam String outOrderNo) {
    //    WxliteService.Referer ref = wxliteService.getReferer(referer);
    //    ResponseDTO<?> responseDTO = wxScoreService.queryByOrderNo(outOrderNo, ref.getAppId());
    //    return responseDTO.toResponse();
    //}
    //
    //@AccessValidate
    //@GetMapping(value = "/syncOrder")
    //public ResponseEntity<BaseResponse> syncOrder(
    //        @RequestHeader("referer") String referer,
    //        @RequestHeader("mobileNo") String mobileNo,
    //        @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
    //        @RequestHeader("accessToken") String accessToken,
    //        @RequestParam String outOrderNo) {
    //    WxliteService.Referer ref = wxliteService.getReferer(referer);
    //    ResponseDTO<Void> responseDTO = wxScoreService.syncOrder(outOrderNo, ref.getAppId());
    //    return responseDTO.toResponse();
    //}
    //
    //@AccessValidate
    //@GetMapping(value = "/orderDetail")
    //public ResponseEntity<BaseResponse> orderDetail(
    //        @RequestHeader("referer") String referer,
    //        @RequestHeader("mobileNo") String mobileNo,
    //        @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
    //        @RequestHeader("accessToken") String accessToken,
    //        @RequestParam String outOrderNo) {
    //    WxScoreService.WxscoreDetailRes res = wxScoreService.wxscoreDetail(outOrderNo);
    //    return ResponseEntity.ok(new BaseResponse(200, "客户端查询微信支付分订单详情", res));
    //}

    private String openId(String mobileNo, String appId) {
        UserDto userDto = userService.findByMobileNo(mobileNo);
        return userService.getOpenInfo(userDto).stream()
                .filter(openInfo -> openInfo.getAppId().equals(appId))
                .map(UserService.OpenInfo::getOpenId)
                .findAny()
                .get();

    }

}
