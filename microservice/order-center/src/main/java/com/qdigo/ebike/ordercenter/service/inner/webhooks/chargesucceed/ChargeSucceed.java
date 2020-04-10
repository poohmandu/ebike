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

package com.qdigo.ebike.ordercenter.service.inner.webhooks.chargesucceed;

import com.pingplusplus.model.Charge;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.constants.Status;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoneMatchException;
import com.qdigo.ebike.commonaop.annotations.Token;
import com.qdigo.ebike.ordercenter.repository.charge.OrderChargeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by niezhao on 2017/3/10.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ChargeSucceed {

    private final OrderChargeRepository chargeRepository;
    private final UserService userService;
    private final UserAccountService accountService;
    private final ChargeSucceedBizService succeedBizService;

    //事务传播默认为Propagation.REQUIRED ;
    //当被调用方法没有事务会新建一个事务
    @Token(key = {"orderNo"}, expireSeconds = 60)
    public boolean chargeSucceed(Charge charge) throws NoneMatchException {
        if (!charge.getPaid()) {
            log.debug("{}该订单还未支付", charge.getOrderNo());
            return false;
        }
        var orderCharge = chargeRepository.findById(charge.getId()).orElse(null);
        if (orderCharge != null) {
            log.debug("{}支付成功回调前,查询orderCharge是否已经成功:{}", orderCharge.getOrderNo(), orderCharge.isPaid());
            if (orderCharge.isPaid()) {
                return true;
            }
        }
        Map<String, Object> metadata = charge.getMetadata();
        val payType = ((Double) metadata.get("payType")).intValue();
        val mobileNo = (String) metadata.get("mobileNo");
        log.debug("用户{}，进行({})类型的支付{}分。", mobileNo, payType, charge.getAmount());

        val userDto = userService.findByMobileNo(mobileNo);
        val account = accountService.findByUserId(userDto.getUserId());

        BizParam bizParam = new BizParam(userDto, account, charge);

        //租金或者押金
        if (payType == Status.PayType.rent.getVal()) {

            succeedBizService.rentChargeBiz(bizParam);

        } else if (payType == Status.PayType.deposit.getVal()) {

            succeedBizService.depositChargeBiz(bizParam);

        } else {
            throw new RuntimeException("支付类型(payType)未知");
        }
        log.debug("{}成功完成支付回调函数,实际支付金额为{}分", mobileNo, charge.getAmount());
        return true;
    }


}
