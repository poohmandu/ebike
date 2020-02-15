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

package com.qdigo.ebike.controlcenter.controller;

import com.qdigo.ebicycle.aop.token.AccessValidate;
import com.qdigo.ebicycle.domain.scenic.EntityCard;
import com.qdigo.ebicycle.domain.scenic.EntityCardUser;
import com.qdigo.ebicycle.domain.user.User;
import com.qdigo.ebicycle.o.context.Ctx;
import com.qdigo.ebicycle.o.ro.BaseResponse;
import com.qdigo.ebicycle.repository.userRepo.UserRepository;
import com.qdigo.ebicycle.service.activity.scenic.EntityCardService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1.0/ebike/entityCard")
public class EntityCardRest {

    @Inject
    private EntityCardService entityCardService;
    @Inject
    private UserRepository userRepository;

    @AccessValidate
    @GetMapping(value = "/getEntityCard", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEntityCard(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestParam String entityCardNo) {

        Optional<EntityCard> optional = entityCardService.getEntityCard(entityCardNo);
        if (!optional.isPresent()) {
            return ResponseEntity.ok(new BaseResponse(400, "该实体卡不存在"));
        }
        EntityCard entityCard = optional.get();
        Res res = new Res();
        res.setAmount(entityCard.getAmount());
        res.setEndTime(entityCard.getEndTime().getTime());
        res.setEntityCardNo(entityCard.getEntityCardNo());
        res.setUserAmount(entityCard.getUserAmount());
        return ResponseEntity.ok(new BaseResponse(200, "获得实体卡信息", res));
    }

    @AccessValidate
    @PostMapping(value = "/authEntityCard", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authEntityCard(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestParam String entityCardNo) {

        Optional<EntityCard> optional = entityCardService.getEntityCard(entityCardNo);
        if (!optional.isPresent()) {
            return ResponseEntity.ok(new BaseResponse(400, "无效的卡号" + entityCardNo));
        } else if (optional.get().getHotelId() == null) {
            return ResponseEntity.ok(new BaseResponse(400, "该实体骑行卡还未激活"));
        } else if (!optional.get().isValid()) {
            return ResponseEntity.ok(new BaseResponse(400, "该实体骑行卡已无效"));
        } else if (Ctx.now() > optional.get().getEndTime().getTime()) {
            return ResponseEntity.ok(new BaseResponse(400, "该实体骑行卡已过期"));
        } else {
            User user = userRepository.findOneByMobileNo(mobileNo).get();
            Optional<EntityCardUser> cardUserOptional = entityCardService.getEntityCardUser(user.getUserId(), optional.get().getEntityCardId());

            if (cardUserOptional.isPresent() && cardUserOptional.get().getStatus() == EntityCardUser.Status.paid) {
                return ResponseEntity.ok(new BaseResponse(400, "卡号为" + entityCardNo + "的实体骑行卡已使用过一次,勿重复充值"));
            }
        }
        return ResponseEntity.ok(new BaseResponse(200, entityCardNo + "实体卡可充值"));
    }

    @Data
    private static class Res {
        private String entityCardNo;
        private long endTime;
        private double amount;
        private double userAmount;
    }

}
