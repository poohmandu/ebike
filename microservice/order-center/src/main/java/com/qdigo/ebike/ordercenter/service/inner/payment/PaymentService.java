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

import com.qdigo.ebike.api.domain.dto.activity.scenic.BindStatus;
import com.qdigo.ebike.api.domain.dto.activity.scenic.EntityCardDto;
import com.qdigo.ebike.api.domain.dto.activity.scenic.EntityCardUserDto;
import com.qdigo.ebike.api.domain.dto.user.UserAccountDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.activity.scenic.EntityCardService;
import com.qdigo.ebike.api.service.agent.AgentConfigService;
import com.qdigo.ebike.api.service.order.ride.OrderRideService;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.ordercenter.domain.dto.ChargeBody;
import com.qdigo.ebike.ordercenter.domain.dto.PayBizType;
import com.qdigo.ebike.ordercenter.service.inner.longrent.TakeawayService;
import com.qdigo.ebike.ordercenter.service.remote.OrderLongRentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by niezhao on 2017/7/21.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PaymentService {

    private final ChargeService chargeService;
    private final AgentConfigService agentConfigService;
    private final EntityCardService entityCardService;
    private final TakeawayService takeawayService;
    private final OrderLongRentServiceImpl longRentService;
    private final OrderRideService rideService;

    //只有查询动作
    //@Transactional
    public ResponseDTO validateCreateCharge(ChargeBody chargeBody) {
        UserDto user = chargeBody.getUserDto();
        UserAccountDto accountDto = chargeBody.getUserAccountDto();
        String mobileNo = user.getMobileNo();
        int amount = chargeBody.getAmount();
        int payType = chargeBody.getPayType();
        PayBizType bizType = chargeBody.getBizType();
        Map<String, String> extra = chargeBody.getExtra();

        if (payType == Status.PayType.deposit.getVal()) {
            if (accountDto.getDeposit() > 0) {
                return new ResponseDTO(400, "您已支付过押金");
            }
        } else if (payType == Status.PayType.rent.getVal()) {
            if (bizType == PayBizType.balance) {

                val hasCharge = chargeService.hasRentCharges(accountDto.getUserAccountId());
                if (!hasCharge) {

                    val config = agentConfigService.getAgentConfig(user.getAgentId());
                    val noneDepositCharge = config.getNoneDepositFirstCharge();
                    val depositCharge = config.getDepositFirstCharge();

                    if (accountDto.getDeposit() <= 0 && FormatUtil.fenToYuan(amount) < noneDepositCharge) {
                        return new ResponseDTO(401, "非押金信用认证用户，首次至少需要充值" + noneDepositCharge + "元余额");
                    }
                    if (accountDto.getDeposit() > 0 && FormatUtil.fenToYuan(amount) < depositCharge) {
                        return new ResponseDTO(401, "押金用户,首次至少需要充值" + depositCharge + "元余额");
                    }
                }
            } else if (bizType == PayBizType.entityCard) {
                //402
                Assert.notNull(extra, "extra为null");
                String entityCardNo = extra.get("entityCardNo");
                EntityCardDto entityCardDto = entityCardService.getEntityCard(entityCardNo);
                if (entityCardDto == null) {
                    return new ResponseDTO(402, "无效的卡号" + entityCardNo);
                } else if (entityCardDto.getHotelId() == null) {
                    return new ResponseDTO(402, "该实体骑行卡还未激活");
                } else if (FormatUtil.fenToYuan(amount) != entityCardDto.getAmount()) {
                    return new ResponseDTO(402, "充值金额与实体骑行卡面额不符");
                } else if (!entityCardDto.isValid()) {
                    return new ResponseDTO(402, "该实体骑行卡已无效");
                } else if (System.currentTimeMillis() > entityCardDto.getEndTime().getTime()) {
                    return new ResponseDTO(402, "该实体骑行卡已过期");
                } else {
                    EntityCardUserDto entityCardUser = entityCardService.getEntityCardUser(user.getUserId(), entityCardDto.getEntityCardId());
                    if (entityCardUser != null && entityCardUser.getStatus() == BindStatus.paid) {
                        return new ResponseDTO(402, "卡号为" + entityCardNo + "的实体骑行卡已使用过一次,勿重复充值");
                    }
                }
                log.debug("检测到为实体卡充值:{}", entityCardNo);
            } else if (bizType == PayBizType.takeaway) {
                //403
                Assert.notNull(extra, "extra为null");
                String deviceId = extra.get("deviceId");
                String id = extra.get("id");
                ResponseDTO res = takeawayService.validateTakeawayService(user, accountDto.getBalance(),
                        deviceId, Long.valueOf(id), FormatUtil.fenToYuan(amount));
                if (res.isNotSuccess()) {
                    return res;
                }
            } else if (bizType == PayBizType.longRent) {
                //405
                Assert.notNull(extra, "extra为null");


                val hasLongRent = longRentService.hasLongRent(user.getUserId());
                if (hasLongRent) {
                    new ResponseDTO<>(405, "已经购买过了,有效时间内无法再次购买");
                }

                if (rideService.findRidingByMobileNo(mobileNo) != null) {
                    new ResponseDTO<>(405, "正在骑行中，无法购买长租卡");
                }

            } else if (bizType != null) {
                log.debug("检测到充值的业务类型为:" + bizType);
            } else {
                return new ResponseDTO(500, "未知的业务类型");
            }
        } else {
            return new ResponseDTO(500, "未知的支付类型");
        }

        return new ResponseDTO(200);

    }

}
