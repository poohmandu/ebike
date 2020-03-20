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

package com.qdigo.ebike.ordercenter.service.remote.wxscore;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.order.RideDto;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.ConsumeDetail;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.FreeActivityDto;
import com.qdigo.ebike.api.domain.dto.order.ridefreeactivity.FreeType;
import com.qdigo.ebike.api.domain.dto.order.wxscore.WxscoreDto;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.CompleteOrderParam;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.StartOrderParam;
import com.qdigo.ebike.api.domain.dto.third.wx.wxscore.WxscoreOrder;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.order.wxscore.OrderWxscoreBizService;
import com.qdigo.ebike.api.service.third.wxlite.WxliteService;
import com.qdigo.ebike.api.service.third.wxlite.WxscoreService;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.domain.ResponseDTO;
import com.qdigo.ebike.common.core.errors.exception.QdigoBizException;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.ordercenter.domain.entity.wxscore.OrderWxscore;
import com.qdigo.ebike.ordercenter.repository.OrderWxscoreRepository;
import com.qdigo.ebike.ordercenter.service.inner.wxscore.WxscoreDaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * description: 
 *
 * date: 2020/3/17 11:39 PM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OrderWxscoreBizServiceImpl implements OrderWxscoreBizService {

    private final OrderWxscoreRepository orderWxscoreRepository;
    private final WxscoreService wxscoreService;
    private final WxscoreDaoService wxscoreDaoService;
    private final WxliteService wxliteService;
    private final UserService userService;

    @Override
    public WxscoreDto hasRideWxscoreOrder(Long rideRecordId) {
        OrderWxscore orderWxscore = orderWxscoreRepository.findByRideRecordId(rideRecordId);
        return ConvertUtil.to(orderWxscore, WxscoreDto.class);
    }

    private boolean wxscoreEnable(String userDeviceId, String wxscoreState) {
        if (!wxliteService.isWxlite(userDeviceId))
            return false;
        return "AVAILABLE".equals(wxscoreState);
    }

    @Transactional
    @Override
    public void startWxscoreOrder(WxsocreStart wxsocreStart) {
        try {
            UserDto userDto = wxsocreStart.getUserDto();
            AgentCfg config = wxsocreStart.getAgentCfg();
            RideDto rideDto = wxsocreStart.getRideDto();
            Long agentId = config.getAgentId();
            boolean wxscoreEnable = wxscoreEnable(userDto.getDeviceId(), wxsocreStart.getWxscoreEnable());
            if (!wxscoreEnable) {
                return;
            }
            String appId = wxliteService.getAppId(userDto.getDeviceId());
            String openId = userService.getOpenInfo(userDto).stream()
                    .filter(openInfo -> openInfo.getAppId().equals(appId))
                    .map(UserService.OpenInfo::getOpenId)
                    .findAny().get();

            String feeDesc = MessageFormat.format("每{0}分钟{1}元,每天{2}小时封顶",
                    rideDto.getUnitMinutes(), rideDto.getPrice(), config.getDayMaxHours());


            StartOrderParam param = new StartOrderParam()
                    .setAgentId(agentId)
                    .setAppId(appId)
                    .setFeeDesc(feeDesc)
                    .setOpenId(openId)
                    .setRideRecordId(rideDto.getRideRecordId())
                    .setUserId(userDto.getUserId());
            ResponseDTO<String> startOrder = wxscoreService.startOrder(param);
            String errMessage;
            if (startOrder.isNotSuccess()) {
                return;
            }
            String outOrderNo = startOrder.getData();
            ResponseDTO<WxscoreOrder> queryOrder = wxscoreService.queryByOrderNo(outOrderNo, appId);
            if (queryOrder.isNotSuccess()) {
                return;
            }
            //事务传播REQUIRES_NEW才能在这抛出异常否则在大事务抛出
            WxscoreOrder wxscoreOrder = queryOrder.getData();
            wxscoreDaoService.createOrder(wxscoreOrder);

        } catch (Exception e) {
            log.warn("微信支付分创单时发生错误并忽略:{}", e.getMessage());
        }

    }

    //@CatAnnotation
    @Override
    public void completeWxscoreOrder(WxscoreComplete wxscoreComplete) throws QdigoBizException {
        RideDto rideDto = wxscoreComplete.getRideDto();
        UserDto user = wxscoreComplete.getUserDto();
        WxscoreDto wxscoreDto = wxscoreComplete.getWxscoreDto();
        ConsumeDetail consumeDetail = wxscoreComplete.getConsumeDetail();
        List<WxscoreOrder.Discount> otherDiscounts = wxscoreComplete.getOtherDiscounts();
        Integer totalAmount = wxscoreComplete.getTotalAmount();

        String appId = wxliteService.getAppId(user.getDeviceId());

        String outOrderNo = wxscoreDto.getOutOrderNo();
        ResponseDTO<WxscoreOrder> responseDTO = wxscoreService.queryByOrderNo(outOrderNo, appId);
        if (responseDTO.isSuccess()) {
            WxscoreOrder orderDto = responseDTO.getData();
            String state = orderDto.getState();

            if (state.equals(WxscoreService.State.USER_PAID.name()) || state.equals(WxscoreService.State.FINISHED.name())) {
                log.debug("查询到微信支付分订单已发起过完结,状态为:{}", orderDto.getState());
                wxscoreDaoService.finishOrder(orderDto);
                return;
            }
            long freeTime = 0;
            int freeAmount = 0; //单位:分
            List<WxscoreOrder.Discount> discounts = new ArrayList<>();

            for (FreeActivityDto freeActivity : consumeDetail.getFreeActivities()) {
                if (freeActivity.getFreeType() == FreeType.time) {
                    freeTime += freeActivity.getFreeTime();
                    WxscoreOrder.Discount discount = new WxscoreOrder.Discount();
                    discount.setDiscount_name(freeActivity.getFreeActivity().getDes());
                    discount.setDiscount_desc(freeActivity.getNote());
                    discounts.add(discount);
                } else if (freeActivity.getFreeType() == FreeType.money) {
                    int fen = FormatUtil.yuanToFen(freeActivity.getFreeConsume());
                    freeAmount += fen;
                    WxscoreOrder.Discount discount = new WxscoreOrder.Discount();
                    discount.setDiscount_name(freeActivity.getFreeActivity().getDes());
                    discount.setDiscount_amount(fen);
                    discount.setDiscount_desc(freeActivity.getNote());
                    discounts.add(discount);
                }
            }
            if (otherDiscounts != null) {
                discounts.addAll(otherDiscounts);
            }
            int discountTotal = discounts.stream().filter(discount -> discount.getDiscount_amount() != null)
                    .mapToInt(WxscoreOrder.Discount::getDiscount_amount).sum();

            long realStartTime = rideDto.getStartTime().getTime() + freeTime;

            List<WxscoreOrder.Fee> fees = orderDto.getFees();

            WxscoreOrder.Fee rentFee = fees.stream().filter(fee -> fee.getFee_name().equals(WxscoreService.rent_fee_name)).findAny().get();
            rentFee.setFee_amount(totalAmount + discountTotal);//传原始消费金额
            rentFee.setFee_desc(null);
            rentFee.setFee_count(null);

            CompleteOrderParam param = new CompleteOrderParam()
                    .setAppId(appId)
                    .setDiscounts(discounts)
                    .setFees(fees)
                    .setRealStartTime(realStartTime)
                    .setFinishTicket(orderDto.getFinish_ticket())
                    .setOutOrderNo(outOrderNo);

            ResponseDTO<Void> completeDepositOrder = wxscoreService.completeOrder(param);
            if (completeDepositOrder.isNotSuccess()) {
                throw new QdigoBizException(completeDepositOrder.getMessage(), 406);
            }
            //后续数据库保存需要在回调中进行
        } else {
            throw new QdigoBizException(responseDTO.getMessage(), 406);
        }

    }

}
