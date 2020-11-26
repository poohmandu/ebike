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

package com.qdigo.ebike.activitycenter.service.inner.scenic;

import com.qdigo.ebike.activitycenter.domain.entity.scenic.EntityCard;
import com.qdigo.ebike.activitycenter.domain.entity.scenic.EntityCardUser;
import com.qdigo.ebike.activitycenter.repository.EntityCardRepository;
import com.qdigo.ebike.activitycenter.repository.EntityCardUserRepository;
import com.qdigo.ebike.activitycenter.repository.dao.EntityCardDao;
import com.qdigo.ebike.api.domain.dto.activity.scenic.BindStatus;
import com.qdigo.ebike.common.core.util.Ctx;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EntityCardInnerService {
    //180516100001   12位
    private final EntityCardRepository entityCardRepository;
    private final EntityCardDao entityCardDao;
    private final EntityCardUserRepository entityCardUserRepository;

    private static final DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
    private static final DecimalFormat decimal_format = new DecimalFormat("00000");

    private List<String> createEntityNo(int num) {
        List<String> noList = new ArrayList<>();
        EntityCard lastOne = entityCardDao.findLastOne();
        String nowStr = dateFormat.format(new Date());
        String type = "1";
        long first;
        if (lastOne != null && lastOne.getEntityCardNo().startsWith(nowStr)) {
            try {
                first = 1 + (Long) decimal_format.parse(StringUtils.substringAfter(lastOne.getEntityCardNo(), nowStr + type));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            first = 1;
        }
        for (int i = 0; i < num; i++) {
            String entityNo = nowStr + type + decimal_format.format(first + i);
            noList.add(entityNo);
        }
        return noList;
    }

    @Transactional
    public void createEntityCard(int num, double amount, double hotelAmount, double userAmount, long expireSeconds) {
        List<String> entityNoList = this.createEntityNo(num);
        List<EntityCard> entityCards = new ArrayList<>();
        Date now = new Date(Ctx.now());
        Date end = new Date(Ctx.now() + TimeUnit.SECONDS.toMillis(expireSeconds)); //默认两年后
        for (int i = 0; i < num; i++) {
            EntityCard entityCard = new EntityCard();
            entityCard.setAmount(amount);
            entityCard.setCreatedTime(now);
            entityCard.setEndTime(end);
            entityCard.setEntityCardNo(entityNoList.get(i));
            entityCard.setHotelAmount(hotelAmount);
            entityCard.setQRCode("http://www.qdigo.com/scan/index.html?ec=" + entityNoList.get(i));
            entityCard.setUserAmount(userAmount);
            entityCard.setValid(true);
            entityCards.add(entityCard);
        }
        if (!entityCards.isEmpty()) {
            entityCardRepository.saveAll(entityCards);
        }
    }

    public Optional<EntityCard> getEntityCard(final String entityCardNo) {
        if (StringUtils.isEmpty(entityCardNo)) {
            return Optional.empty();
        }
        return entityCardRepository.findFirstByEntityCardNo(entityCardNo);
    }

    public Optional<EntityCardUser> getEntityCardUser(long userId, long entityCardId) {
        return entityCardUserRepository.findFirstByUserIdAndEntityCardId(userId, entityCardId);
    }

    @Transactional
    public void bindEntityCardUser(long userId, EntityCard entityCard) {
        if (this.getEntityCardUser(userId, entityCard.getEntityCardId()).isPresent()) {
            return;
        }
        EntityCardUser cardUser = new EntityCardUser();
        cardUser.setEntityCardId(entityCard.getEntityCardId());
        cardUser.setHotelAmount(entityCard.getHotelAmount());
        cardUser.setHotelId(entityCard.getHotelId());
        cardUser.setScanTime(new Date(Ctx.now()));
        cardUser.setUserId(userId);
        cardUser.setUserAmount(entityCard.getUserAmount());
        cardUser.setStatus(BindStatus.scan);
        entityCardUserRepository.save(cardUser);
    }

    @Transactional
    public void updateEntityCardUserStatus(EntityCardUser entityCardUser, BindStatus status) {
        entityCardUser.setStatus(status);
        entityCardUser.setPayTime(new Date(Ctx.now()));
        entityCardUserRepository.save(entityCardUser);
    }

}
