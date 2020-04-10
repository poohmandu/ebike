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

package com.qdigo.ebike.ordercenter.message.charge;

import com.qdigo.ebike.api.domain.dto.activity.scenic.BindStatus;
import com.qdigo.ebike.api.domain.dto.activity.scenic.EntityCardDto;
import com.qdigo.ebike.api.domain.dto.activity.scenic.EntityCardUserDto;
import com.qdigo.ebike.api.domain.dto.agent.AgentTakeawayConfigDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.activity.scenic.EntityCardService;
import com.qdigo.ebike.api.service.agent.AgentTakeawayConfigService;
import com.qdigo.ebike.api.service.bike.BikeService;
import com.qdigo.ebike.api.service.order.longrent.OrderLongRentService;
import com.qdigo.ebike.common.core.constants.Const;
import com.qdigo.ebike.common.core.constants.Keys;
import com.qdigo.ebike.common.core.errors.exception.runtime.NoneMatchException;
import com.qdigo.ebike.common.core.util.ArithUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.ordercenter.service.remote.OrderLongRentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ChargeSuccessEventListener {


    private final EntityCardService entityCardService;
    private final AgentTakeawayConfigService takeawayConfigService;
    private final BikeService bikeService;
    private final OrderLongRentServiceImpl longRentService;
    private final RedisTemplate<String, String> redisTemplate;


    //参考 https://blog.csdn.net/java_collect/article/details/81156529#3_EventListener_103
    //@TransactionalEventListener 不和发布事件的方法在同一个事务内
    @EventListener
    public void onEntityCardChargeEvent(EntityCardChargeEvent event) throws NoneMatchException {
        val entityCardNo = event.getEntityCardNo();
        val user = event.getUser();
        val account = event.getAccount();
        EntityCardDto entityCardDto = entityCardService.getEntityCard(entityCardNo);
        if (entityCardDto != null) {
            double userAmount = entityCardDto.getUserAmount();
            EntityCardUserDto entityCardUser = entityCardService.getEntityCardUser(user.getUserId(), entityCardDto.getEntityCardId());
            if (entityCardUser == null) {
                throw new NoneMatchException(user.getMobileNo() + "没有对应的实体卡绑定关系");
            }
            account.setGiftBalance(account.getGiftBalance() + entityCardUser.getUserAmount());

            entityCardService.updateEntityCardUserStatus(entityCardUser, BindStatus.paid.name());

        } else {
            throw new NoneMatchException(user.getMobileNo() + "骑行卡不存在:" + entityCardNo);
        }
    }

    @EventListener
    public void onTakeawayChargeEvent(TakeawayChargeEvent event) {
        val user = event.getUser();
        val account = event.getAccount();
        Long id = Long.valueOf(event.getId());
        AgentTakeawayConfigDto config = takeawayConfigService.findById(id);

        account.setBalance(FormatUtil.getMoney(ArithUtil.sub(account.getBalance(), config.getPrice())));

        BikeDto bikeDto = bikeService.findByDeviceId(event.getDeviceId());

        Date now = new Date();
        val longRentDto = new OrderLongRentService.LongRentDto()
                .setAgentId(bikeDto.getAgentId()).setConsume(config.getPrice())
                .setEndTime(DateUtils.addDays(now, config.getDay()))
                .setLongRentType(Const.LongRentType.takeaway).setPrice(config.getPrice())
                .setStartTime(now).setUserId(user.getUserId()).setImei(bikeDto.getImeiId());
        longRentService.create(longRentDto);
    }

    @EventListener
    public void onArrearsChargeEvent(ArrearsChargeEvent event) {
        UserDto user = event.getUser();
        String mobileNo = user.getMobileNo();
        log.debug("{}用户补欠款充值", mobileNo);
        String key = Keys.flagArrearsCharge.getKey(mobileNo);
        redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()), Const.arrearsChargeMinutes, TimeUnit.MINUTES);
    }

    @EventListener
    public void onLongRentChargeEvent(LongRentChargeEvent event) {
        Date now = new Date();
        String longRentType = event.getLongRentType();
        val longRentDto = new OrderLongRentService.LongRentDto()
                .setAgentId(event.getUser().getAgentId()).setConsume(event.getPrice())
                .setEndTime(new Date(System.currentTimeMillis() + OrderLongRentService.milliseconds(Const.LongRentType.valueOf(longRentType))))
                .setLongRentType(Const.LongRentType.valueOf(longRentType)).setPrice(event.getPrice())
                .setStartTime(now).setUserId(event.getUser().getUserId());

        longRentService.create(longRentDto);
    }
}
