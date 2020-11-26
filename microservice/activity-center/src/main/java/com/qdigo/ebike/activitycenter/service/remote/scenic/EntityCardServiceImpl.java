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

package com.qdigo.ebike.activitycenter.service.remote.scenic;

import com.qdigo.ebike.activitycenter.domain.entity.scenic.EntityCard;
import com.qdigo.ebike.activitycenter.domain.entity.scenic.EntityCardUser;
import com.qdigo.ebike.activitycenter.service.inner.scenic.EntityCardInnerService;
import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.activity.scenic.BindStatus;
import com.qdigo.ebike.api.domain.dto.activity.scenic.EntityCardDto;
import com.qdigo.ebike.api.domain.dto.activity.scenic.EntityCardUserDto;
import com.qdigo.ebike.api.service.activity.scenic.EntityCardService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

/**
 * description: 
 *
 * date: 2020/4/4 11:45 PM
 * @author niezhao
 */
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EntityCardServiceImpl implements EntityCardService {

    private final EntityCardInnerService entityCardInnerService;

    @Override
    public EntityCardDto getEntityCard(String entityCardNo) {
        EntityCard entityCard = entityCardInnerService.getEntityCard(entityCardNo).orElse(null);
        return ConvertUtil.to(entityCard, EntityCardDto.class);
    }

    @Override
    public EntityCardUserDto getEntityCardUser(long userId, long entityCardId) {
        Optional<EntityCardUser> optional = entityCardInnerService.getEntityCardUser(userId, entityCardId);
        return optional.map(entityCardUser -> ConvertUtil.to(entityCardUser, EntityCardUserDto.class))
                .orElse(null);
    }

    @Override
    public void bindEntityCardUser(Long userId, EntityCardDto entityCard) {
        EntityCard to = ConvertUtil.to(entityCard, EntityCard.class);
        entityCardInnerService.bindEntityCardUser(userId, to);
    }

    @Override
    @Transactional
    public void updateEntityCardUserStatus(EntityCardUserDto entityCardUser, String status) {
        EntityCardUser to = ConvertUtil.to(entityCardUser, EntityCardUser.class);
        entityCardInnerService.updateEntityCardUserStatus(to, BindStatus.valueOf(status));
    }

}
