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

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author jiangchen
 * @date 2018/03/07
 */
@Service
@Slf4j
public class ZDBService {
    //
    //private static final String COMPANY = "qdigo";
    //private static final String PLATFORM_CODE = "CPI000325";
    //private static final String SUM_PREMIUM = "0.02";
    //private static final String SECRET_KEY = "Picc37mu63ht38mw";
    //private static final String SUM_AMOUNT = "210000";
    //private static final String RATION_TYPE = "ZDB3101001";
    //
    //@Inject
    //private BikeService bikeService;
    //@Inject
    //private UserRepository userRepository;
    //@Inject
    //private InsuranceRecordRepository insuranceRecordRepository;
    //
    //public InsuranceRecord insureZDBService(String imei, String mobileNo, long rideRecordId) {
    //
    //    // 接口地址
    //    String address = "http://partnertest.mypicc.com.cn/ecooperation/webservice/insure?wsdl";
    //    // 代理工厂
    //    JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
    //    // 设置代理地址
    //    jaxWsProxyFactoryBean.setAddress(address);
    //    // 设置接口类型
    //    jaxWsProxyFactoryBean.setServiceClass(EcooperationWebService.class);
    //    // 创建一个代理接口实现
    //    EcooperationWebService service = (EcooperationWebService) jaxWsProxyFactoryBean.create();
    //
    //    Bike bike = bikeService.findOneByImeiIdOrDeviceId(imei);
    //    User user = userRepository.findOneByMobileNo(mobileNo).orElse(null);
    //    if (user == null || user.getRealName() == null || user.getRealName().isEmpty()
    //        || user.getIdNo() == null || user.getIdNo().isEmpty()) {
    //        return null;
    //    }
    //
    //    ApplyInfo applyInfo = new ApplyInfo();
    //
    //    GeneralInfo generalInfo = new GeneralInfo();
    //
    //    String uuid = COMPANY + UUIDUtil.get24UUID();
    //    generalInfo.setUUID(uuid);
    //    generalInfo.setPlateformCode(PLATFORM_CODE);
    //    generalInfo.setMd5Value(MD5Util.md5Encode(uuid + SUM_PREMIUM + SECRET_KEY, "utf-8"));
    //    applyInfo.setGeneralInfo(generalInfo);
    //
    //    PolicyInfos policyInfos = new PolicyInfos();
    //    PolicyInfo policyInfo = new PolicyInfo();
    //    InsuredPlan insuredPlan = new InsuredPlan();
    //    Applicant applicant = new Applicant();
    //    Insureds insureds = new Insureds();
    //    Insured insured = new Insured();
    //
    //    policyInfo.setSerialNo("1");
    //    policyInfo.setRiskCode("ZDB");
    //    policyInfo.setOperateTimes(FormatUtil.getCurTime());
    //    policyInfo.setStartDate(FormatUtil.getToday());
    //    policyInfo.setEndDate(FormatUtil.getTomorrowDate());
    //    policyInfo.setStartHour("0");
    //    policyInfo.setEndHour("0");
    //    policyInfo.setSumAmount(SUM_AMOUNT);
    //    policyInfo.setSumPremium(SUM_PREMIUM);
    //    policyInfo.setArguSolution("1");
    //    policyInfo.setQuantity("1");
    //    policyInfo.setExtend1(bike.getDeviceId());
    //    insuredPlan.setRationType(RATION_TYPE);
    //    policyInfo.setInsuredPlan(insuredPlan);
    //    applicant.setAppliName("上海骑滴智能科技有限公司");
    //    applicant.setAppliIdType("37");
    //    applicant.setAppliIdNo("91310112MA1GBC9G4U");
    //    applicant.setAppliIdMobile("021-31778797");
    //    applicant.setAppliAddress("上海市金钟路968号凌空SOHO 11号505室");
    //    policyInfo.setApplicant(applicant);
    //    insured.setInsuredSeqNo("1");
    //    insured.setInsuredName(user.getRealName());
    //    insured.setInsuredIdType("01");
    //    insured.setInsuredIdNo(user.getIdNo());
    //    insured.setInsuredIdMobile(mobileNo);
    //    insureds.setInsured(insured);
    //    policyInfo.setInsureds(insureds);
    //    policyInfos.setPolicyInfo(policyInfo);
    //
    //    applyInfo.setPolicyInfos(policyInfos);
    //
    //    StringWriter sw = null;
    //    try {
    //        sw = BeanToXmlUtil.beanToXml(applyInfo);
    //        log.debug("请求的报文为：【\n{}】", sw.toString());
    //    } catch (JAXBException | FileNotFoundException e) {
    //        log.debug("调用人保接口三责险时异常，异常原因如下：\n{}", e);
    //    }
    //
    //    String datas = sw != null ? sw.toString() : null;
    //    String result = service.insureService("001001", datas);
    //
    //    log.debug("投保人保三责险的时候返回的报文为【{}】", result);
    //
    //    ReturnInfo xmlToBean = null;
    //    try {
    //        xmlToBean = xmlToBean(result, new ReturnInfo());
    //    } catch (JAXBException e) {
    //        e.printStackTrace();
    //    }
    //    if (xmlToBean != null) {
    //        String returnCode = xmlToBean.getGeneralInfoReturn().getErrorCode();
    //        String retMsg = xmlToBean.getGeneralInfoReturn().getErrorMessage();
    //        if (!"00".equals(returnCode)) {
    //            log.debug("调用人保三责险的接口失败，失败原因：【{}】", retMsg);
    //            return null;
    //        }
    //        String detailCode = xmlToBean.getPolicyInfoReturns().getPolicyInfoReturn().getSaveResult();
    //        String detailMsg = xmlToBean.getPolicyInfoReturns().getPolicyInfoReturn().getSaveMessage();
    //        if (!"00".equals(detailCode)) {
    //            log.debug("调用人保三责险的接口失败，详细原因：【{}】", detailMsg);
    //            return null;
    //        }
    //        String policyNo = xmlToBean.getPolicyInfoReturns().getPolicyInfoReturn().getPolicyNo();
    //        InsuranceRecord insurance = new InsuranceRecord();
    //        insurance.setPolicyNo(policyNo);
    //        insurance.setErrorCode(returnCode);
    //        insurance.setErrorMsg(retMsg);
    //        insurance.setName(user.getRealName());
    //        insurance.setIdNo(user.getIdNo());
    //        insurance.setMobileNo(mobileNo);
    //        insurance.setStartTime(new Date());
    //        insurance.setRideRecordId(rideRecordId);
    //        insurance.setInsureType("人保三方责任险");
    //        insurance.setOrderSn(xmlToBean.getGeneralInfoReturn().getUUID());
    //        insurance.setProductCode(RATION_TYPE);
    //        try {
    //            insurance.setEndTime(FormatUtil.yMdHms.parse(FormatUtil.y_M_d.format(new Date()) + " 23:59:59"));
    //        } catch (ParseException e) {
    //            log.error("解析时间异常:", e);
    //        }
    //        return insuranceRecordRepository.save(insurance);
    //    } else {
    //        log.debug("调用人保三责险的接口失败，返回报文异常");
    //    }
    //    return null;
    //}
}
