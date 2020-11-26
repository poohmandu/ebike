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

package com.qdigo.ebike.ordercenter.service.inner.payment;

import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.*;
import com.pingplusplus.model.Charge;
import com.qdigo.ebike.api.domain.dto.activity.scenic.EntityCardDto;
import com.qdigo.ebike.api.service.activity.scenic.EntityCardService;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.ordercenter.domain.dto.ChargeBody;
import com.qdigo.ebike.ordercenter.domain.dto.PayBizType;
import com.qdigo.ebike.ordercenter.domain.entity.charge.OrderCharge;
import com.qdigo.ebike.ordercenter.repository.charge.OrderChargeRepository;
import com.qdigo.ebike.ordercenter.repository.dao.OrderChargeDao;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargeService {

    private final OrderChargeRepository chargeRepository;
    private final OrderChargeDao orderChargeDao;
    private final RedisTemplate<String, String> redisTemplate;
    private final EntityCardService entityCardService;

    /**
     * 创建 Charge
     * <p>
     * 创建 Charge 用户需要组装一个 map 对象作为参数传递给 Charge.create();
     * map 里面参数的具体说明请参考：https://pingxx.com/document/api#api-c-new
     *
     * @return Charge
     */
    @GlobalTransactional(rollbackFor = Throwable.class)
    @Transactional(rollbackFor = Throwable.class)
    public Charge createCharge(ChargeBody body) {
        // 设置 API Key
        Pingpp.apiKey = ConfigConstants.apiKey.getConstant();
        // 设置私钥路径，用于请求签名
        Pingpp.privateKeyPath = null;
        Map<String, Object> chargeMap = new HashMap<>();
        //将amount单位转化为 分
        chargeMap.put("amount", body.getAmount());//订单总金额, 人民币单位：分（如订单总金额为 1 元，此处请填 100）
        chargeMap.put("currency", "cny");//人民币
        chargeMap.put("subject", ConfigConstants.subject.getConstant());
        chargeMap.put("body", "电滴出行为您提供服务");//20161117

        String orderNo = this.createOrderNo(body.getPayType());

        chargeMap.put("order_no", orderNo);// 推荐使用 8-20 位，要求数字或字母，不允许其他字符
        chargeMap.put("channel", body.getChannel());// 支付使用的第三方支付渠道取值，请参考：https://www.pingxx.com/api#api-c-new
        chargeMap.put("client_ip", body.getClientIp()); // 发起支付请求客户端的 IP 地址，格式为 IPV4，如: 127.0.0.1
        Map<String, String> app = new HashMap<>();
        app.put("id", ConfigConstants.appId.getConstant());
        chargeMap.put("app", app);
        Map<String, String> extra = new HashMap<>();
        //open_id 为微信的open_id  buyer_account为用户的支付宝帐号
        if (Status.PayChannel.wx_pub.getVal().equals(body.getChannel()) ||
                Status.PayChannel.wx_lite.getVal().equals(body.getChannel())) {
            extra.put("open_id", body.getOpenId());
        } else if (Status.PayChannel.alipay_lite.getVal().equals(body.getChannel())) {
            extra.put("buyer_user_id", body.getOpenId());
        }
        chargeMap.put("extra", extra);
        // metadata 为用户自己定义 key-value
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("payType", body.getPayType());
        metadata.put("mobileNo", body.getUserDto().getMobileNo());
        if (body.getExtra() != null) {
            metadata.putAll(body.getExtra());
        }
        //支付租金时
        if (body.getPayType() == Status.PayType.rent.getVal()) {
            //回调时判断、不能处理枚举
            metadata.put("bizType", body.getBizType().name());
            if (body.getBizType() == PayBizType.entityCard) {
                log.debug("订单类型为实体卡");
                String entityCardNo = body.getExtra().get("entityCardNo");
                EntityCardDto entityCard = entityCardService.getEntityCard(entityCardNo);
                entityCardService.bindEntityCardUser(body.getUserDto().getUserId(), entityCard);
            } else if (body.getBizType() == PayBizType.takeaway) {
                log.debug("订单类型为外卖长租");
            } else if (body.getBizType() == PayBizType.membercard) {
                log.debug("订单类型为特权卡");
            } else if (body.getBizType() == PayBizType.longRent) {
                log.debug("订单类型为长租卡");
            }
        }
        chargeMap.put("metadata", metadata);
        //发起交易请求
        Charge charge;
        try {
            charge = Charge.create(chargeMap);
        } catch (AuthenticationException | InvalidRequestException | APIConnectionException | APIException | ChannelException | RateLimitException e) {
            log.error("调用ping++生成charge对象异常:" + e.getMessage());
            throw new RuntimeException("调用ping++生成charge对象异常:" + e.getMessage());
        }
        log.info("调用ping++返回参数chargeString:" + charge.toString());

        saveOrderCharge(charge, body.getUserAccountDto().getUserAccountId());
        return charge;
    }

    public OrderCharge fromCharge(Charge charge, Long userAccountId) {
        OrderCharge one = new OrderCharge()
                .setUserAccountId(userAccountId)
                .setChargeId(charge.getId())
                .setAmount(charge.getAmount())
                .setAmountRefunded(charge.getAmountRefunded())
                .setAmountSettle(charge.getAmountSettle())
                .setApp(charge.getApp().toString())
                .setBody(charge.getBody())
                .setChannel(charge.getChannel())
                .setChargeId(charge.getId())
                .setClientIp(charge.getClientIp())
                .setCurrency(charge.getCurrency())
                .setDescription(charge.getDescription())
                .setFailureCode(charge.getFailureCode())
                .setFailureMsg(charge.getFailureMsg())
                .setLivemode(charge.getLivemode())
                .setOrderNo(charge.getOrderNo())
                .setPaid(charge.getPaid())
                .setPayType(((Double) charge.getMetadata().get("payType")).intValue())
                .setRefunded(charge.getRefunded())
                .setSubject(charge.getSubject())
                .setTimeExpire(charge.getTimeExpire())
                .setTimePaid(charge.getTimePaid())
                .setTransactionNo(charge.getTransactionNo())
                .setCreated(charge.getCreated())
                .setTimeSettle(charge.getTimeSettle());
        if (charge.getExtra() == null) {
            one.setPayAccount("");
        } else if (Status.PayChannel.alipay.getVal().equals(charge.getChannel())) {
            one.setPayAccount((String) charge.getExtra().getOrDefault("buyer_user_id", ""));
        } else if (Status.PayChannel.wx.getVal().equals(charge.getChannel())) {
            one.setPayAccount((String) charge.getExtra().getOrDefault("open_id", ""));
        } else if (Status.PayChannel.wx_pub.getVal().equals(charge.getChannel())
                || Status.PayChannel.wx_lite.getVal().equals(charge.getChannel())) {
            one.setPayAccount((String) charge.getExtra().getOrDefault("open_id", ""));
        } else if (Status.PayChannel.alipay_lite.getVal().equals(charge.getChannel())) {
            one.setPayAccount((String) charge.getExtra().getOrDefault("buyer_user_id", ""));
        }
        return one;
    }

    public OrderCharge saveOrderCharge(Charge charge, Long userAccountId) {
        OrderCharge orderCharge = this.fromCharge(charge, userAccountId);
        return chargeRepository.save(orderCharge);
    }

    public boolean hasRentCharges(Long userAccountId) {
        return orderChargeDao.hasRentCharges(userAccountId);
        //return chargeRepository.findByUserAccountAndPayType(user.getAccount(), Status.PayType.rent.getVal()).size() > 0;
    }

    public String createOrderNo(int type) {
        val dateStr = FormatUtil.getCurDate();
        val key = Keys.OrderNo.getKey(dateStr);

        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().increment(key, 1);
        } else {
            redisTemplate.opsForValue().set(key, "1", 1, TimeUnit.DAYS);
        }
        val num = redisTemplate.opsForValue().get(key);
        log.info("今日的第{}个订单", num);
        val df = new DecimalFormat("000000");
        //0 161127 123456
        val orderNo = type + dateStr.substring(2) + df.format(Integer.parseInt(num));
        log.info("生成的订单号为" + orderNo);
        return orderNo;
    }


}
