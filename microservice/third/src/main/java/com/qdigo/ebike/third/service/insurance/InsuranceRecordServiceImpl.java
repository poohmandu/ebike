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

package com.qdigo.ebike.third.service.insurance;

import com.qdigo.ebike.api.RemoteService;
import com.qdigo.ebike.api.domain.dto.third.insurance.InsuranceRecordDto;
import com.qdigo.ebike.api.service.third.insurance.InsuranceRecordService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.third.domain.entity.InsuranceRecord;
import com.qdigo.ebike.third.repository.InsuranceRecordRepository;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * description: 
 *
 * date: 2020/3/17 10:14 PM
 * @author niezhao
 */
@Slf4j
@RemoteService
public class InsuranceRecordServiceImpl implements InsuranceRecordService {

    @Resource
    private InsuranceRecordRepository insuranceRecordRepository;

    @Override
    public List<InsuranceRecordDto> findByMobileNoAndStartTimeAfter(String mobileNo, Date startTime) {
        List<InsuranceRecord> records = insuranceRecordRepository.findByMobileNoAndStartTimeAfter(mobileNo, startTime);
        return ConvertUtil.to(records, InsuranceRecordDto.class);
    }
}
