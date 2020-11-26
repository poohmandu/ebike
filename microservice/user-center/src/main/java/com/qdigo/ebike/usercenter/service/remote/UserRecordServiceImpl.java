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
import com.qdigo.ebike.api.service.user.UserRecordService;
import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.domain.entity.UserRecord;
import com.qdigo.ebike.usercenter.repository.UserRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/17 6:25 PM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserRecordServiceImpl implements UserRecordService {

    private final UserRecordRepository userRecordRepository;

    @Override
    @Transactional
    public void insertUserRecord(Long userId, String record) {
        log.debug("用户:{}记录信息:{}", userId, record);
        UserRecord userRecord = new UserRecord();
        userRecord.setRecord(record);
        userRecord.setUser(new User().setUserId(userId));
        userRecordRepository.save(userRecord);
    }
}
