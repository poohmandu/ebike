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

package com.qdigo.ebike.usercenter.service.remote;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.service.user.UserCreditService;
import com.qdigo.ebike.usercenter.domain.entity.UserCredit;
import com.qdigo.ebike.usercenter.domain.entity.UserCreditRecord;
import com.qdigo.ebike.usercenter.repository.UserCreditRecordRepository;
import com.qdigo.ebike.usercenter.repository.UserCreditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;

/**
 * Created by niezhao on 2017/2/24.
 */
@Slf4j
@RemoteService
public class UserCreditServiceImpl implements UserCreditService {

    @Inject
    private UserCreditRecordRepository userCreditRecordRepository;
    @Inject
    private UserCreditRepository userCreditRepository;

    //加分 减分
    @Override
    @Transactional
    public boolean updateCreditInfo(Long userId, String event, int scoreChange) {
        try {
            UserCredit userCredit = userCreditRepository.findByUserUserId(userId);
            UserCreditRecord creditRecord = new UserCreditRecord();
            creditRecord.setEventInfo(event).setEventTime(new Date())
                    .setScoreChange(scoreChange).setUserCredit(userCredit);
            int score = userCredit.getScore() + creditRecord.getScoreChange();
            if (score < 0 || score > 200) {
                return false;
            }
            userCredit.setScore(score);
            userCreditRecordRepository.save(creditRecord);
            return true;
        } catch (Exception e) {
            log.error("用户信用分改变失败" + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

}
