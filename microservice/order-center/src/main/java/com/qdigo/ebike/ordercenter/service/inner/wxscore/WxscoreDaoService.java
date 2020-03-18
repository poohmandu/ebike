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

package com.qdigo.ebike.ordercenter.service.inner.wxscore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.ordercenter.domain.entity.wxscore.OrderWxscore;
import com.qdigo.ebike.ordercenter.domain.entity.wxscore.OrderWxscoreDiscount;
import com.qdigo.ebike.ordercenter.domain.entity.wxscore.OrderWxscoreFee;
import com.qdigo.ebike.ordercenter.repository.OrderWxscoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WxscoreDaoService {

    @Resource
    private OrderWxscoreRepository orderWxscoreRepository;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    // 默认只有最外层的Transactional才会抛出数据库异常回滚
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createOrder(WxscoreOrder dto) {
        String decode;
        try {
            decode = URLDecoder.decode(dto.getAttach(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("发生错误:{}", e.getMessage());
            throw new RuntimeException(e);
        }
        JSONObject attach = JSON.parseObject(decode);
        Long userId = attach.getLong("userId");
        Long rideRecordId = attach.getLong("rideRecordId");
        Long agentId = attach.getLong("agentId");

        OrderWxscore orderWxscore = new OrderWxscore();
        orderWxscore.setAgentId(agentId);
        orderWxscore.setAppId(dto.getAppid());

        orderWxscore.setEndTime(0L);

        orderWxscore.setMchId(dto.getMchid());
        orderWxscore.setOutOrderNo(dto.getOut_order_no());
        orderWxscore.setRideRecordId(rideRecordId);
        orderWxscore.setRiskAmount(dto.getRisk_amount());
        orderWxscore.setServiceId(dto.getService_id());
        orderWxscore.setStartTime(System.currentTimeMillis());
        orderWxscore.setState(EnumUtils.getEnum(OrderWxscore.State.class, dto.getState()));
        orderWxscore.setTotalAmount(dto.getTotal_amount());
        orderWxscore.setUserId(userId);
        orderWxscore.setTransactionId("");

        if (dto.getFees() != null) {
            List<OrderWxscoreFee> fees = dto.getFees().stream().map(fee -> {
                OrderWxscoreFee wxscoreFee = new OrderWxscoreFee();
                wxscoreFee.setFeeAmount(fee.getFee_amount());
                wxscoreFee.setFeeCount(fee.getFee_count());
                wxscoreFee.setFeeDesc(fee.getFee_desc());
                wxscoreFee.setFeeName(fee.getFee_name());
                wxscoreFee.setOrder(orderWxscore);
                return wxscoreFee;
            }).collect(Collectors.toList());
            orderWxscore.setFees(fees);
        }

        if (dto.getDiscounts() != null) {
            List<OrderWxscoreDiscount> discounts = dto.getDiscounts().stream().map(discount -> {
                OrderWxscoreDiscount wxscoreDiscount = new OrderWxscoreDiscount();
                wxscoreDiscount.setDiscountAmount(discount.getDiscount_amount());
                wxscoreDiscount.setDiscountDesc(discount.getDiscount_desc());
                wxscoreDiscount.setDiscountName(discount.getDiscount_name());
                wxscoreDiscount.setOrder(orderWxscore);
                return wxscoreDiscount;
            }).collect(Collectors.toList());
            orderWxscore.setDiscounts(discounts);
        }

        orderWxscoreRepository.save(orderWxscore);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                log.debug("orderWxscore创建成功持久化到数据库,设置标志位");
                redisTemplate.opsForValue().set(Keys.flagWxscoreCreate.getKey(String.valueOf(userId)), orderWxscore.getOutOrderNo());
            }

        });
    }

    @Transactional
    public void finishOrder(WxscoreOrder dto) {
        String decode;
        try {
            decode = URLDecoder.decode(dto.getAttach(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("发生错误:{}", e.getMessage());
            throw new RuntimeException(e);
        }
        JSONObject attach = JSON.parseObject(decode);
        Long userId = attach.getLong("userId");
        Long rideRecordId = attach.getLong("rideRecordId");
        Long agentId = attach.getLong("agentId");

        OrderWxscore orderWxscore = orderWxscoreRepository.findById(dto.getOut_order_no()).orElse(null);
        orderWxscore.setAgentId(agentId);
        orderWxscore.setAppId(dto.getAppid());
        long endTime;
        try {
            endTime = FormatUtil.yyyyMMddHHmmss.parse(dto.getReal_service_end_time()).getTime();
        } catch (Exception e) {
            log.warn("解析实际结束时间时异常:{}", e.getMessage());
            endTime = System.currentTimeMillis();
        }
        orderWxscore.setEndTime(endTime);
        orderWxscore.setMchId(dto.getMchid());
        orderWxscore.setRideRecordId(rideRecordId);
        orderWxscore.setRiskAmount(dto.getRisk_amount());
        orderWxscore.setServiceId(dto.getService_id());
        orderWxscore.setState(EnumUtils.getEnum(OrderWxscore.State.class, dto.getState()));
        orderWxscore.setTotalAmount(dto.getTotal_amount());
        orderWxscore.setUserId(userId);
        if (dto.getFinish_transaction_id() != null)
            orderWxscore.setTransactionId(dto.getFinish_transaction_id());

        if (dto.getFees() != null) {
            List<OrderWxscoreFee> fees = dto.getFees().stream().map(fee -> {
                Optional<OrderWxscoreFee> optional = orderWxscore.getFees().stream()
                        .filter(orderFee -> orderFee.getFeeName().equals(fee.getFee_name())).findAny();
                OrderWxscoreFee orderWxscoreFee = optional.orElseGet(OrderWxscoreFee::new);
                orderWxscoreFee.setFeeAmount(fee.getFee_amount());
                orderWxscoreFee.setFeeCount(fee.getFee_count());
                orderWxscoreFee.setFeeDesc(fee.getFee_desc());
                orderWxscoreFee.setFeeName(fee.getFee_name());
                orderWxscoreFee.setOrder(orderWxscore);
                return orderWxscoreFee;
            }).collect(Collectors.toList());
            orderWxscore.setFees(fees);
        }

        if (dto.getDiscounts() != null) {
            List<OrderWxscoreDiscount> discounts = dto.getDiscounts().stream().map(discount -> {
                Optional<OrderWxscoreDiscount> optional = orderWxscore.getDiscounts().stream()
                        .filter(orderDiscount -> orderDiscount.getDiscountName().equals(discount.getDiscount_name())).findAny();

                OrderWxscoreDiscount orderWxscoreDiscount = optional.orElseGet(OrderWxscoreDiscount::new);
                orderWxscoreDiscount.setDiscountAmount(discount.getDiscount_amount());
                orderWxscoreDiscount.setDiscountDesc(discount.getDiscount_desc());
                orderWxscoreDiscount.setDiscountName(discount.getDiscount_name());
                orderWxscoreDiscount.setOrder(orderWxscore);
                return orderWxscoreDiscount;
            }).collect(Collectors.toList());

            orderWxscore.setDiscounts(discounts);
        }

        orderWxscoreRepository.save(orderWxscore);

        if (orderWxscore.getState() == OrderWxscore.State.USER_PAID ||
                orderWxscore.getState() == OrderWxscore.State.REVOKED ||
                orderWxscore.getState() == OrderWxscore.State.EXPIRED) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    log.debug("orderWxscore完结持久化到数据库,取消标志位");
                    redisTemplate.delete(Keys.flagWxscoreCreate.getKey(String.valueOf(userId)));
                }

            });
        }
    }

}
