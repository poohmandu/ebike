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

package com.qdigo.ebike.third.service.insurance.picc;

import com.qdigo.ebike.api.domain.dto.third.insurance.InsuranceParam;
import com.qdigo.ebike.api.domain.dto.third.insurance.InsuranceRecordDto;
import com.qdigo.ebike.api.service.third.insurance.EblService;
import com.qdigo.ebike.common.core.util.ConvertUtil;
import com.qdigo.ebike.common.core.util.FormatUtil;
import com.qdigo.ebike.third.domain.entity.InsuranceRecord;
import com.qdigo.ebike.third.repository.InsuranceRecordRepository;
import com.qdigo.ebike.third.service.insurance.picc.entity.ebl.*;
import com.qdigo.ebike.third.service.insurance.picc.entity.returninfo.ReturnInfo;
import com.qdigo.ebike.third.service.insurance.picc.insure.EcooperationWebService;
import com.qdigo.ebike.third.service.insurance.picc.util.BeanToXmlUtil;
import com.qdigo.ebike.third.service.insurance.picc.util.MD5Util;
import com.qdigo.ebike.third.service.insurance.picc.util.PinyinUtil;
import com.qdigo.ebike.third.service.insurance.picc.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Date;

/**
 * @author jiangchen
 * @date 2018/02/24
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EBLServiceImpl implements EblService {

    private static final String PLATFORM_CODE = "CPI000325";
    private static final String SUM_PREMIUM = "0.05";
    private static final String SECRET_KEY = "Picc89MK8yjq6h7j";
    private static final String SUM_AMOUNT = "210000";
    private static final String RATION_TYPE = "EBL3199001";

    private final InsuranceRecordRepository insuranceRecordRepository;

    @Transactional
    @Override
    public InsuranceRecordDto insure(InsuranceParam param) {

        // 接口地址
        String address = "http://www.epicc.com.cn/ecooperation/webservice/insure?wsdl";
        // 代理工厂
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        // 设置代理地址
        jaxWsProxyFactoryBean.setAddress(address);
        // 设置接口类型
        jaxWsProxyFactoryBean.setServiceClass(EcooperationWebService.class);
        // 创建一个代理接口实现
        EcooperationWebService service = (EcooperationWebService) jaxWsProxyFactoryBean.create();

        ApplyInfo applyInfo = new ApplyInfo();

        GeneralInfo generalInfo = new GeneralInfo();

        String uuid = UUIDUtil.get32UUID();
        generalInfo.setUUID(uuid);
        generalInfo.setPlateformCode(PLATFORM_CODE);
        String md5 = MD5Util.md5Encode(uuid + SUM_PREMIUM + SECRET_KEY, "utf-8");
        generalInfo.setMd5Value(md5);
        applyInfo.setGeneralInfo(generalInfo);

        PolicyInfos policyInfos = new PolicyInfos();
        PolicyInfo policyInfo = new PolicyInfo();
        InsuredPlan insuredPlan = new InsuredPlan();
        Applicant applicant = new Applicant();
        Insureds insureds = new Insureds();
        Insured insured = new Insured();

        policyInfo.setSerialNo("1");
        policyInfo.setRiskCode("EBL");
        policyInfo.setOperateTimes(FormatUtil.getCurTime());
        policyInfo.setStartDate(FormatUtil.getToday());
        policyInfo.setEndDate(FormatUtil.getTomorrowDate());
        policyInfo.setStartHour(FormatUtil.getCurHour());
        policyInfo.setEndHour(FormatUtil.getCurHour());
        policyInfo.setSumAmount(SUM_AMOUNT);
        policyInfo.setSumPremium(SUM_PREMIUM);
        policyInfo.setArguSolution("1");
        policyInfo.setQuantity("1");
        insuredPlan.setRationType(RATION_TYPE);
        policyInfo.setInsuredPlan(insuredPlan);
        applicant.setAppliName(param.getRealName());
        applicant.setAppliIdType("01");
        applicant.setAppliIdNo(param.getIdNo());
        applicant.setAppliIdMobile(param.getMobileNo());
        policyInfo.setApplicant(applicant);

        insured.setInsuredSeqNo("1");
        insured.setInsuredName(param.getRealName());
        insured.setEname(PinyinUtil.character2Pinyin(param.getRealName()));
        insured.setInsuredIdType("99");
        insured.setInsuredIdNo(param.getIdNo());
        String idNo = param.getIdNo();
        String birth = idNo.substring(6, 10) + "-" + idNo.substring(10, 12) + "-" + idNo.substring(12, 14);
        insured.setInsuredBirthday(birth);
        insured.setInsuredSex("1");
        insured.setInsuredIdMobile(param.getMobileNo());
        insureds.setInsured(insured);
        policyInfo.setInsureds(insureds);
        policyInfos.setPolicyInfo(policyInfo);

        applyInfo.setPolicyInfos(policyInfos);

        StringWriter sw = null;
        try {
            sw = BeanToXmlUtil.beanToXml(applyInfo);
            log.debug("请求的报文为：【\n{}】", sw.toString());
        } catch (JAXBException | FileNotFoundException e) {
            log.error("调用人保接口意外险时异常，异常原因如下:{}", e);
        }

        String datas = sw != null ? sw.toString() : null;
        String result = service.insureService("001001", datas);

        log.debug("投保人保意外伤害险的时候返回的报文为【{}】", result);

        ReturnInfo xmlToBean = null;
        try {
            xmlToBean = BeanToXmlUtil.xmlToBean(result, new ReturnInfo());
        } catch (JAXBException e) {
            log.error("xml转java bean失败:", e);
        }
        if (xmlToBean != null) {
            String returnCode = xmlToBean.getGeneralInfoReturn().getErrorCode();
            String retMsg = xmlToBean.getGeneralInfoReturn().getErrorMessage();
            if (!"00".equals(returnCode)) {
                log.debug("调用人保意外伤害险的接口失败，失败原因：【{}】", retMsg);
                return null;
            }
            String detailCode = xmlToBean.getPolicyInfoReturns().getPolicyInfoReturn().getSaveResult();
            String detailMsg = xmlToBean.getPolicyInfoReturns().getPolicyInfoReturn().getSaveMessage();
            if (!"00".equals(detailCode)) {
                log.debug("调用人保意外伤害险的接口失败，详细原因：【{}】", detailMsg);
                return null;
            }
            String policyNo = xmlToBean.getPolicyInfoReturns().getPolicyInfoReturn().getPolicyNo();
            InsuranceRecord insurance = new InsuranceRecord();
            insurance.setPolicyNo(policyNo);
            insurance.setErrorCode(returnCode);
            insurance.setErrorMsg(retMsg);
            insurance.setName(param.getRealName());
            insurance.setIdNo(param.getIdNo());
            insurance.setMobileNo(param.getMobileNo());
            insurance.setStartTime(new Date());
            insurance.setRideRecordId(param.getRideRecordId());
            insurance.setInsureType("人保意外伤害险");
            insurance.setOrderSn(xmlToBean.getGeneralInfoReturn().getUUID());
            insurance.setProductCode(RATION_TYPE);
            try {
                insurance.setEndTime(FormatUtil.yMdHms.parse(FormatUtil.y_M_d.format(new Date()) + " 23:59:59"));
            } catch (ParseException e) {
                log.error("解析时间异常:", e);
            }
            InsuranceRecord insuranceRecord = insuranceRecordRepository.save(insurance);
            return ConvertUtil.to(insuranceRecord, InsuranceRecordDto.class);
        } else {
            log.debug("调用人保意外伤害险的接口失败，返回报文异常");
        }
        return null;

    }
}
