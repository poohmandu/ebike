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

package com.qdigo.ebike.ordercenter.service.remote.journalaccount;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.order.JournalAccountDto;
import com.qdigo.ebike.api.service.order.journal.OrderJournalAccountService;
import com.qdigo.ebike.ordercenter.service.inner.JournalAccountInnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * description: 
 *
 * date: 2020/3/2 9:46 PM
 * @author niezhao
 */
@Slf4j
@RemoteService
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OrderJournalAccountServiceImpl implements OrderJournalAccountService {

    private final JournalAccountInnerService journalAccountInnerService;

    @Override
    public JournalAccountDto insert4Charge(Param param) {
        return journalAccountInnerService.insert4Charge(param);
    }

    @Override
    public JournalAccountDto insert4Ride(Param param) {
        return journalAccountInnerService.insert4Ride(param);
    }

    @Override
    public JournalAccountDto insert4LongRent(Param param) {
        return journalAccountInnerService.insert4LongRent(param);
    }
}
