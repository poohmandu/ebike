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
// 生成时间: 2018.03.06 时间 03:41:20 PM CST
//


package com.qdigo.ebike.third.service.insurance.picc.entity.returninfo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


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

    private final static QName _PolicyNo_QNAME = new QName("", "PolicyNo");
    private final static QName _PlateformCode_QNAME = new QName("", "PlateformCode");
    private final static QName _SerialNo_QNAME = new QName("", "SerialNo");
    private final static QName _SaveTimes_QNAME = new QName("", "SaveTimes");
    private final static QName _PolicyUrl_QNAME = new QName("", "PolicyUrl");
    private final static QName _InsuredSeqNo_QNAME = new QName("", "InsuredSeqNo");
    private final static QName _CheckMessage_QNAME = new QName("", "CheckMessage");
    private final static QName _SaveResult_QNAME = new QName("", "SaveResult");
    private final static QName _SaveMessage_QNAME = new QName("", "SaveMessage");
    private final static QName _CheckResult_QNAME = new QName("", "CheckResult");
    private final static QName _ErrorCode_QNAME = new QName("", "ErrorCode");
    private final static QName _UUID_QNAME = new QName("", "UUID");
    private final static QName _ErrorMessage_QNAME = new QName("", "ErrorMessage");
    private final static QName _DownloadUrl_QNAME = new QName("", "DownloadUrl");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InsuredReturns }
     */
    public InsuredReturns createInsuredReturns() {
        return new InsuredReturns();
    }

    /**
     * Create an instance of {@link InsuredReturn }
     */
    public InsuredReturn createInsuredReturn() {
        return new InsuredReturn();
    }

    /**
     * Create an instance of {@link GeneralInfoReturn }
     */
    public GeneralInfoReturn createGeneralInfoReturn() {
        return new GeneralInfoReturn();
    }

    /**
     * Create an instance of {@link PolicyInfoReturn }
     */
    public PolicyInfoReturn createPolicyInfoReturn() {
        return new PolicyInfoReturn();
    }

    /**
     * Create an instance of {@link PolicyInfoReturns }
     */
    public PolicyInfoReturns createPolicyInfoReturns() {
        return new PolicyInfoReturns();
    }

    /**
     * Create an instance of {@link ReturnInfo }
     */
    public ReturnInfo createReturnInfo() {
        return new ReturnInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "PolicyNo")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPolicyNo(String value) {
        return new JAXBElement<String>(_PolicyNo_QNAME, String.class, null, value);
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
    @XmlElementDecl(namespace = "", name = "SerialNo")
    public JAXBElement<String> createSerialNo(String value) {
        return new JAXBElement<String>(_SerialNo_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "SaveTimes")
    public JAXBElement<String> createSaveTimes(String value) {
        return new JAXBElement<String>(_SaveTimes_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "PolicyUrl")
    public JAXBElement<String> createPolicyUrl(String value) {
        return new JAXBElement<String>(_PolicyUrl_QNAME, String.class, null, value);
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
    @XmlElementDecl(namespace = "", name = "CheckMessage")
    public JAXBElement<String> createCheckMessage(String value) {
        return new JAXBElement<String>(_CheckMessage_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "SaveResult")
    public JAXBElement<String> createSaveResult(String value) {
        return new JAXBElement<String>(_SaveResult_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "SaveMessage")
    public JAXBElement<String> createSaveMessage(String value) {
        return new JAXBElement<String>(_SaveMessage_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "CheckResult")
    public JAXBElement<String> createCheckResult(String value) {
        return new JAXBElement<String>(_CheckResult_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "ErrorCode")
    public JAXBElement<String> createErrorCode(String value) {
        return new JAXBElement<String>(_ErrorCode_QNAME, String.class, null, value);
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
    @XmlElementDecl(namespace = "", name = "ErrorMessage")
    public JAXBElement<String> createErrorMessage(String value) {
        return new JAXBElement<String>(_ErrorMessage_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "DownloadUrl")
    public JAXBElement<String> createDownloadUrl(String value) {
        return new JAXBElement<String>(_DownloadUrl_QNAME, String.class, null, value);
    }

}
