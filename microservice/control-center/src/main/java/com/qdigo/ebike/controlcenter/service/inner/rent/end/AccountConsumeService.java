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

package com.qdigo.ebike.controlcenter.service.inner.rent.end;

import com.qdigo.ebike.api.domain.dto.order.rideforceend.ForceEndInfo;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.ConsumeDetail;
import com.qdigo.ebike.api.domain.dto.order.wxscore.WxscoreDto;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import com.qdigo.ebike.api.service.order.journal.OrderJournalAccountService;
import com.qdigo.ebike.api.service.order.ride.RideForceEndService;
import com.qdigo.ebike.api.service.order.ride.RideFreeActivityService;
import com.qdigo.ebike.api.service.order.wxscore.OrderWxscoreBizService;
import com.qdigo.ebike.api.service.user.UserAccountService;
import com.qdigo.ebike.common.core.errors.exception.QdigoBizException;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.controlcenter.domain.dto.rent.EndDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * description: 
 *
 * date: 2020/3/18 9:06 PM
 * @author niezhao
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AccountConsumeService {

    private final RideFreeActivityService freeActivityService;
    private final RideForceEndService forceEndService;
    private final OrderWxscoreBizService wxscoreBizService;
    private final UserAccountService accountService;
    private final OrderJournalAccountService journalAccountService;

    @Transactional
    public void finishConsume(EndDTO endDTO) throws QdigoBizException {
        val rideRecord = endDTO.getRideDto();
        val user = endDTO.getUserDto();
        val status = endDTO.getBikeStatusDto();
        var account = endDTO.getUserAccountDto();
        val agentCfg = endDTO.getAgentCfg();
        ConsumeDetail consumeDetail = endDTO.getOut().getConsumeDetail();

        //核销.会修改giftBalance、coupon
        RideFreeActivityService.ConsumeParam consumeParam = RideFreeActivityService.ConsumeParam.builder()
                .accountDto(account).agentCfg(agentCfg).rideDto(rideRecord).userDto(user)
                .freeActivities(consumeDetail.getFreeActivities()).build();
        val consumeResult = freeActivityService.consumeFreeActivities(consumeParam);
        account = consumeResult.getUserAccountDto();
        double rideRecordConsume = consumeDetail.getConsume(); //最终费用

        double forceEndConsume = 0;
        // 强制还车的费用不用加在微信免密支付上
        if (endDTO.isForceEnd()) {
            ForceEndInfo forceEndInfo = endDTO.getOut().getForceEndInfo();
            Assert.notNull(forceEndInfo, "强制还车信息为null");
            Assert.isTrue(forceEndInfo.isValid(), "结账时强制还车必须有效");
            forceEndConsume = forceEndInfo.getAmount();

            Assert.isTrue(forceEndConsume >= 0, "强制还车付费不能为负");
            rideRecordConsume += forceEndConsume;
            val createParam = RideForceEndService.CreateParam.builder().agentId(agentCfg.getAgentId()).lat(status.getLatitude())
                    .lng(status.getLongitude()).rideRecordId(rideRecord.getRideRecordId()).forceEndInfo(forceEndInfo).build();
            forceEndService.insert(createParam);
        }
        List<WxscoreOrder.Discount> otherDiscount = new ArrayList<>();

        //TODO: 赠送余额应该代理商买单
        double actualConsume;
        WxscoreDto wxscoreDto = endDTO.getOut().getWxscoreDto();
        if (wxscoreDto == null) {
            wxscoreDto = wxscoreBizService.hasRideWxscoreOrder(rideRecord.getRideRecordId());
        }
        val startBalance = account.getBalance();
        if (wxscoreDto != null) {
            log.debug("用户消费流程走微信支付分渠道");
            actualConsume = rideRecordConsume;
            double wxFinalConsume;
            List<WxscoreOrder.Discount> discounts = new ArrayList<>();
            if (startBalance > 0) {
                Assert.isTrue(rideRecordConsume >= 0, "消费金额不可能为负");
                WxscoreOrder.Discount discount = new WxscoreOrder.Discount();
                discount.setDiscount_name("电滴剩余金额");
                if (startBalance >= rideRecordConsume) {
                    wxFinalConsume = 0;
                    discount.setDiscount_amount(FormatUtil.yuanToFen(rideRecordConsume));
                    discount.setDiscount_desc("使用电滴小程序里余额消费" + FormatUtil.getMoney(rideRecordConsume) + "元");
                } else {
                    wxFinalConsume = rideRecordConsume - startBalance;
                    discount.setDiscount_amount(FormatUtil.yuanToFen(startBalance));
                    discount.setDiscount_desc("使用电滴小程序里余额消费" + FormatUtil.getMoney(startBalance) + "元");
                }
                discounts.add(discount);
                // 改变用户余额
                account.setBalance(FormatUtil.getMoney(startBalance - rideRecordConsume + wxFinalConsume));
                accountService.update(account);
            } else {
                wxFinalConsume = rideRecordConsume;
            }
            val wxscoreComplete = OrderWxscoreBizService.WxscoreComplete.builder().consumeDetail(consumeDetail)
                    .otherDiscounts(discounts).rideDto(rideRecord).totalAmount(FormatUtil.yuanToFen(wxFinalConsume))
                    .userDto(user).wxscoreDto(wxscoreDto).build();
            wxscoreBizService.completeWxscoreOrder(wxscoreComplete);
        } else {
            log.debug("用户消费流程走正常余额渠道");
            if (account.getBalance() < 0) {
                actualConsume = 0;
            } else {
                actualConsume = Math.min(rideRecordConsume, account.getBalance());
            }
            // 改变用户余额
            account.setBalance(FormatUtil.getMoney(startBalance - rideRecordConsume));
            accountService.update(account);
        }

        // 记录流水
        val param = OrderJournalAccountService.Param.builder().amount(-rideRecordConsume).startAccount(startBalance)
                .mobileNo(user.getMobileNo()).agentId(agentCfg.getAgentId()).rideRecordId(rideRecord.getRideRecordId()).build();
        journalAccountService.insert4Ride(param);

        rideRecord.setConsume(FormatUtil.getMoney(rideRecordConsume))
                .setActualConsume(actualConsume);
        log.debug("用户最终消费了{}元(含强制还车{}元),原钱包余额为{}元,实际支付:{}元", rideRecordConsume, forceEndConsume, startBalance, actualConsume);

    }


}
