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

package com.qdigo.ebike.api.service.third.wxlite;

import com.alibaba.fastjson.JSONObject;
import com.qdigo.ebike.api.ApiRoute;
import com.qdigo.ebike.api.domain.dto.ResponseDTO;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.CompleteOrderParam;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.StartOrderParam;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Description:
 *   微信支付分相关
 *   参考:https://pay.weixin.qq.com/wiki/doc/apiv3/payscore.php?chapter=12_2&index=1
 * date: 2020/1/7 5:00 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@FeignClient(name = "third", contextId = "wxsocre")
public interface WxscoreService {

    String rent_fee_name = "电滴出行租车骑行费用";

    @PostMapping(ApiRoute.Third.Wxscore.decryptAes256ToString)
    String decryptAes256ToString(@RequestParam("associated_data") String associated_data, @RequestParam("ciphertext") String ciphertext,
                                 @RequestParam("nonce") String nonce) throws GeneralSecurityException, IOException;

    @PostMapping(ApiRoute.Third.Wxscore.userServiceState)
    ResponseDTO<Boolean> userServiceState(@RequestParam("appId") String appId, @RequestParam("openId") String openId);

    @PostMapping(ApiRoute.Third.Wxscore.queryByOrderNo)
    ResponseDTO<WxscoreOrder> queryByOrderNo(@RequestParam("outOrderNo") String outOrderNo, @RequestParam("appId") String appId);

    @PostMapping(ApiRoute.Third.Wxscore.startOrder)
    ResponseDTO<String> startOrder(@RequestBody StartOrderParam param);

    @PostMapping(ApiRoute.Third.Wxscore.buildUseRes)
    WxscoreUseRes buildUseRes(@RequestBody JSONObject json);

    @PostMapping(ApiRoute.Third.Wxscore.completeOrder)
    ResponseDTO<Void> completeOrder(@RequestBody CompleteOrderParam param);

    @PostMapping(ApiRoute.Third.Wxscore.syncOrder)
    ResponseDTO<Void> syncOrder(@RequestParam("outOrderNo") String outOrderNo, @RequestParam("appId") String appId);

    @PostMapping(ApiRoute.Third.Wxscore.wxpayScoreEnable)
    WxscoreEnableRes wxpayScoreEnable();

    @PostMapping(ApiRoute.Third.Wxscore.wxscoreDetail)
    WxscoreDetailRes wxscoreDetail(@RequestParam("out_order_no") String out_order_no);

    @Getter
    @Builder
    class WxscoreEnableRes {
        private String mch_id;
        private String service_id;
        private String out_request_no;
        private String timestamp;
        private String nonce_str;
        private String sign_type;
        private String sign;
    }

    @Getter
    @Builder
    class WxscoreUseRes {
        private String mch_id;
        private String packageStr;
        private String timestamp;
        private String nonce_str;
        private String sign_type;
        private String sign;
    }

    @Getter
    @Builder
    class WxscoreDetailRes {
        private String mch_id;
        private String service_id;
        private String out_order_no;
        private String timestamp;
        private String nonce_str;
        private String sign_type;
        private String sign;
    }

    enum State {
        CREATED, //商户下单已受理
        USER_ACCEPTED,//用户成功使用服务
        FINISHED, //商户完结订单
        USER_PAID, //用户订单支付成功
        REVOKED, //商户撤销订单
        EXPIRED //订单已失效. “商户下单已受理”状态超过1小时未变动，则订单失效
    }

}
