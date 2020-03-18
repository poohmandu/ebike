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

//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2018.03.01 时间 07:06:42 PM CST
//


package com.qdigo.ebike.third.service.insurance.picc.entity.ebl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import java.math.BigDecimal;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the generated package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PlateformCode_QNAME = new QName("", "PlateformCode");
    private final static QName _InsuredSex_QNAME = new QName("", "InsuredSex");
    private final static QName _StartHour_QNAME = new QName("", "StartHour");
    private final static QName _BenefitIdType_QNAME = new QName("", "BenefitIdType");
    private final static QName _BenefitEName_QNAME = new QName("", "BenefitEName");
    private final static QName _InsuredName_QNAME = new QName("", "InsuredName");
    private final static QName _BenefitIdNo_QNAME = new QName("", "BenefitIdNo");
    private final static QName _AppliIdNo_QNAME = new QName("", "AppliIdNo");
    private final static QName _InsuredIdMobile_QNAME = new QName("", "InsuredIdMobile");
    private final static QName _StartDate_QNAME = new QName("", "StartDate");
    private final static QName _ArguSolution_QNAME = new QName("", "ArguSolution");
    private final static QName _InsuredIdNo_QNAME = new QName("", "InsuredIdNo");
    private final static QName _AppliIdMobile_QNAME = new QName("", "AppliIdMobile");
    private final static QName _AppliIdType_QNAME = new QName("", "AppliIdType");
    private final static QName _BenefitBirthday_QNAME = new QName("", "BenefitBirthday");
    private final static QName _EndHour_QNAME = new QName("", "EndHour");
    private final static QName _UUID_QNAME = new QName("", "UUID");
    private final static QName _SumAmount_QNAME = new QName("", "SumAmount");
    private final static QName _SumPremium_QNAME = new QName("", "SumPremium");
    private final static QName _OperateTimes_QNAME = new QName("", "OperateTimes");
    private final static QName _SerialNo_QNAME = new QName("", "SerialNo");
    private final static QName _InsuredSeqNo_QNAME = new QName("", "InsuredSeqNo");
    private final static QName _InsuredIdType_QNAME = new QName("", "InsuredIdType");
    private final static QName _Quantity_QNAME = new QName("", "Quantity");
    private final static QName _InsuredBirthday_QNAME = new QName("", "InsuredBirthday");
    private final static QName _EndDate_QNAME = new QName("", "EndDate");
    private final static QName _Ename_QNAME = new QName("", "Ename");
    private final static QName _RiskCode_QNAME = new QName("", "RiskCode");
    private final static QName _Md5Value_QNAME = new QName("", "Md5Value");
    private final static QName _RationType_QNAME = new QName("", "RationType");
    private final static QName _BenefitName_QNAME = new QName("", "BenefitName");
    private final static QName _AppliName_QNAME = new QName("", "AppliName");
    private final static QName _BenefitSex_QNAME = new QName("", "BenefitSex");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Benefit }
     */
    public Benefit createBenefit() {
        return new Benefit();
    }

    /**
     * Create an instance of {@link Benefits }
     */
    public Benefits createBenefits() {
        return new Benefits();
    }

    /**
     * Create an instance of {@link GeneralInfo }
     */
    public GeneralInfo createGeneralInfo() {
        return new GeneralInfo();
    }

    /**
     * Create an instance of {@link PolicyInfos }
     */
    public PolicyInfos createPolicyInfos() {
        return new PolicyInfos();
    }

    /**
     * Create an instance of {@link PolicyInfo }
     */
    public PolicyInfo createPolicyInfo() {
        return new PolicyInfo();
    }

    /**
     * Create an instance of {@link InsuredPlan }
     */
    public InsuredPlan createInsuredPlan() {
        return new InsuredPlan();
    }

    /**
     * Create an instance of {@link Applicant }
     */
    public Applicant createApplicant() {
        return new Applicant();
    }

    /**
     * Create an instance of {@link Insureds }
     */
    public Insureds createInsureds() {
        return new Insureds();
    }

    /**
     * Create an instance of {@link Insured }
     */
    public Insured createInsured() {
        return new Insured();
    }

    /**
     * Create an instance of {@link ApplyInfo }
     */
    public ApplyInfo createApplyInfo() {
        return new ApplyInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "PlateformCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPlateformCode(String value) {
        return new JAXBElement<String>(_PlateformCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "InsuredSex")
    public JAXBElement<String> createInsuredSex(String value) {
        return new JAXBElement<String>(_InsuredSex_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "StartHour")
    public JAXBElement<String> createStartHour(String value) {
        return new JAXBElement<String>(_StartHour_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "BenefitIdType")
    public JAXBElement<String> createBenefitIdType(String value) {
        return new JAXBElement<String>(_BenefitIdType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "BenefitEName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createBenefitEName(String value) {
        return new JAXBElement<String>(_BenefitEName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "InsuredName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createInsuredName(String value) {
        return new JAXBElement<String>(_InsuredName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "BenefitIdNo")
    public JAXBElement<String> createBenefitIdNo(String value) {
        return new JAXBElement<String>(_BenefitIdNo_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "AppliIdNo")
    public JAXBElement<String> createAppliIdNo(String value) {
        return new JAXBElement<String>(_AppliIdNo_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "InsuredIdMobile")
    public JAXBElement<String> createInsuredIdMobile(String value) {
        return new JAXBElement<String>(_InsuredIdMobile_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "StartDate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createStartDate(String value) {
        return new JAXBElement<String>(_StartDate_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "ArguSolution")
    public JAXBElement<String> createArguSolution(String value) {
        return new JAXBElement<String>(_ArguSolution_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "InsuredIdNo")
    public JAXBElement<String> createInsuredIdNo(String value) {
        return new JAXBElement<String>(_InsuredIdNo_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "AppliIdMobile")
    public JAXBElement<String> createAppliIdMobile(String value) {
        return new JAXBElement<String>(_AppliIdMobile_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "AppliIdType")
    public JAXBElement<String> createAppliIdType(String value) {
        return new JAXBElement<String>(_AppliIdType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "BenefitBirthday")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createBenefitBirthday(String value) {
        return new JAXBElement<String>(_BenefitBirthday_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "EndHour")
    public JAXBElement<String> createEndHour(String value) {
        return new JAXBElement<String>(_EndHour_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "UUID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createUUID(String value) {
        return new JAXBElement<String>(_UUID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "SumAmount")
    public JAXBElement<String> createSumAmount(String value) {
        return new JAXBElement<String>(_SumAmount_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "SumPremium")
    public JAXBElement<BigDecimal> createSumPremium(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SumPremium_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "OperateTimes")
    public JAXBElement<String> createOperateTimes(String value) {
        return new JAXBElement<String>(_OperateTimes_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "SerialNo")
    public JAXBElement<String> createSerialNo(String value) {
        return new JAXBElement<String>(_SerialNo_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "InsuredSeqNo")
    public JAXBElement<String> createInsuredSeqNo(String value) {
        return new JAXBElement<String>(_InsuredSeqNo_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "InsuredIdType")
    public JAXBElement<String> createInsuredIdType(String value) {
        return new JAXBElement<String>(_InsuredIdType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "Quantity")
    public JAXBElement<String> createQuantity(String value) {
        return new JAXBElement<String>(_Quantity_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "InsuredBirthday")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createInsuredBirthday(String value) {
        return new JAXBElement<String>(_InsuredBirthday_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "EndDate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createEndDate(String value) {
        return new JAXBElement<String>(_EndDate_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "Ename")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createEname(String value) {
        return new JAXBElement<String>(_Ename_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "RiskCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createRiskCode(String value) {
        return new JAXBElement<String>(_RiskCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "Md5Value")
    public JAXBElement<String> createMd5Value(String value) {
        return new JAXBElement<String>(_Md5Value_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "RationType")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createRationType(String value) {
        return new JAXBElement<String>(_RationType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "BenefitName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createBenefitName(String value) {
        return new JAXBElement<String>(_BenefitName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "AppliName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createAppliName(String value) {
        return new JAXBElement<String>(_AppliName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "BenefitSex")
    public JAXBElement<String> createBenefitSex(String value) {
        return new JAXBElement<String>(_BenefitSex_QNAME, String.class, null, value);
    }

}
