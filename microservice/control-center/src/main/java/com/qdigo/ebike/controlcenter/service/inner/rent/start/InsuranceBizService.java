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

package com.qdigo.ebike.controlcenter.service.inner.rent.start;

import com.qdigo.ebike.api.domain.dto.agent.AgentCfg;
import com.qdigo.ebike.api.domain.dto.agent.AgentDto;
import com.qdigo.ebike.api.domain.dto.bike.BikeDto;
import com.qdigo.ebike.api.domain.dto.third.insurance.InsuranceParam;
import com.qdigo.ebike.api.domain.dto.third.insurance.InsuranceRecordDto;
import com.qdigo.ebike.api.domain.dto.user.UserDto;
import com.qdigo.ebike.api.service.third.insurance.BgbService;
import com.qdigo.ebike.api.service.third.insurance.EblService;
import com.qdigo.ebike.api.service.third.insurance.InsuranceRecordService;
import com.qdigo.ebike.api.service.third.sms.SmsService;
import com.qdigo.ebike.common.core.util.FormatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Date;

/**
 * description: 
 *
 * date: 2020/3/17 9:07 PM
 * @author niezhao
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class InsuranceBizService {

    private final BgbService highInsureService;
    private final EblService lowInsureService;
    private final InsuranceRecordService insuranceRecordService;
    private final SmsService smsService;


    @Transactional
    public void createInsurance(Long rideId, AgentCfg config, AgentDto agentDto, UserDto userDto, BikeDto bikeDto) {
        String policyNo = null;
        InsuranceParam param = InsuranceParam.builder()
                .city(agentDto.getCity()).idNo(userDto.getIdNo()).mobileNo(userDto.getMobileNo())
                .operationType(bikeDto.getOperationType()).province(agentDto.getAgentProvince())
                .realName(userDto.getRealName()).rideRecordId(rideId).build();

        if (config.isLowInsurance()) {
            InsuranceRecordDto insuranceRecord = lowInsureService.insure(param);
            if (insuranceRecord != null && StringUtils.equals(insuranceRecord.getErrorCode(), "00"))
                policyNo = insuranceRecord.getPolicyNo();
        }

        if (config.isHighInsurance()) {
            InsuranceRecordDto insuranceRecord = highInsureService.insure(param);
            if (insuranceRecord != null && StringUtils.equals(insuranceRecord.getErrorCode(), "1000")) {
                policyNo = insuranceRecord.getPolicyNo();
                if (StringUtils.isEmpty(policyNo))
                    policyNo = insuranceRecord.getOrderSn();
            }
        }

        //TODO 只有平安保险知道短信网址,isHighInsurance判断条件后续去掉,短信网址改为人保的
        if (config.isHighInsurance() && config.isInsuranceSms() && StringUtils.isNotEmpty(policyNo)) {
            smsService.sendInsuranceSms(param.getMobileNo(), policyNo, agentDto.getAgentId());
        }
    }

    public boolean validateInsurance(String mobileNo) {
        Date startTime;
        try {
            startTime = FormatUtil.yMdHms.parse(FormatUtil.y_M_d.format(new Date()) + " 00:00:00");
        } catch (ParseException e) {
            log.error("发生异常", e);
            return true;
        }
        return insuranceRecordService.findByMobileNoAndStartTimeAfter(mobileNo, startTime).stream()
                .noneMatch(insuranceRecord -> insuranceRecord.getErrorCode().equals("00") || // 人保
                        insuranceRecord.getErrorCode().equals("") ||  //海绵宝
                        insuranceRecord.getErrorCode().equals("1000"));
    }

}
