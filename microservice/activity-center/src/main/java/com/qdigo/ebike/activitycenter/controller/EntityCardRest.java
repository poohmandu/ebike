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

package com.qdigo.ebike.activitycenter.controller;


import com.qdigo.ebike.activitycenter.domain.entity.scenic.EntityCard;
import com.qdigo.ebike.activitycenter.domain.entity.scenic.EntityCardUser;
import com.qdigo.ebike.activitycenter.service.inner.scenic.EntityCardService;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.user.UserService;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.util.Ctx;
import com.qdigo.ebike.commonaop.annotations.AccessValidate;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1.0/ebike/entityCard")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EntityCardRest {

    private final EntityCardService entityCardService;
    private final UserService userService;

    @AccessValidate
    @GetMapping(value = "/getEntityCard", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> getEntityCard(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestParam String entityCardNo) {

        Optional<EntityCard> optional = entityCardService.getEntityCard(entityCardNo);
        if (!optional.isPresent()) {
            return R.ok(400, "该实体卡不存在");
        }
        EntityCard entityCard = optional.get();
        Res res = new Res();
        res.setAmount(entityCard.getAmount());
        res.setEndTime(entityCard.getEndTime().getTime());
        res.setEntityCardNo(entityCard.getEntityCardNo());
        res.setUserAmount(entityCard.getUserAmount());
        return R.ok(200, "获得实体卡信息", res);
    }

    @AccessValidate
    @PostMapping(value = "/authEntityCard", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<?> authEntityCard(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestParam String entityCardNo) {

        Optional<EntityCard> optional = entityCardService.getEntityCard(entityCardNo);
        if (!optional.isPresent()) {
            return R.ok("无效的卡号" + entityCardNo);
        } else if (optional.get().getHotelId() == null) {
            return R.ok(400, "该实体骑行卡还未激活");
        } else if (!optional.get().isValid()) {
            return R.ok(400, "该实体骑行卡已无效");
        } else if (Ctx.now() > optional.get().getEndTime().getTime()) {
            return R.ok(400, "该实体骑行卡已过期");
        } else {
            UserDto userDto = userService.findByMobileNo(mobileNo);
            Optional<EntityCardUser> cardUserOptional = entityCardService.getEntityCardUser(userDto.getUserId(), optional.get().getEntityCardId());

            if (cardUserOptional.isPresent() && cardUserOptional.get().getStatus() == EntityCardUser.Status.paid) {
                return R.ok(400, "卡号为" + entityCardNo + "的实体骑行卡已使用过一次,勿重复充值");
            }
        }
        return R.ok(200, entityCardNo + "实体卡可充值");
    }

    @Data
    private static class Res {
        private String entityCardNo;
        private long endTime;
        private double amount;
        private double userAmount;
    }

}
