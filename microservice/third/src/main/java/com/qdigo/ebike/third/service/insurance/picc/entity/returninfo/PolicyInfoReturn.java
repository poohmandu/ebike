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

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>anonymous complex type的 Java 类。
 * <p>
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}SerialNo"/>
 *         &lt;element ref="{}PolicyNo"/>
 *         &lt;element ref="{}PolicyUrl"/>
 *         &lt;element ref="{}DownloadUrl"/>
 *         &lt;element ref="{}SaveResult"/>
 *         &lt;element ref="{}SaveMessage"/>
 *         &lt;element ref="{}SaveTimes"/>
 *         &lt;element ref="{}InsuredReturns"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "serialNo",
        "policyNo",
        "policyUrl",
        "downloadUrl",
        "saveResult",
        "saveMessage",
        "saveTimes",
        "insuredReturns"
})
@XmlRootElement(name = "PolicyInfoReturn")
public class PolicyInfoReturn {

    @XmlElement(name = "SerialNo", required = true)
    protected String serialNo;
    @XmlElement(name = "PolicyNo", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String policyNo;
    @XmlElement(name = "PolicyUrl", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String policyUrl;
    @XmlElement(name = "DownloadUrl", required = true)
    protected String downloadUrl;
    @XmlElement(name = "SaveResult", required = true)
    protected String saveResult;
    @XmlElement(name = "SaveMessage", required = true)
    protected String saveMessage;
    @XmlElement(name = "SaveTimes", required = true)
    protected String saveTimes;
    @XmlElement(name = "InsuredReturns", required = true)
    protected InsuredReturns insuredReturns;

    /**
     * 获取serialNo属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * 设置serialNo属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSerialNo(String value) {
        this.serialNo = value;
    }

    /**
     * 获取policyNo属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getPolicyNo() {
        return policyNo;
    }

    /**
     * 设置policyNo属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPolicyNo(String value) {
        this.policyNo = value;
    }

    /**
     * 获取policyUrl属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getPolicyUrl() {
        return policyUrl;
    }

    /**
     * 设置policyUrl属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPolicyUrl(String value) {
        this.policyUrl = value;
    }

    /**
     * 获取downloadUrl属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * 设置downloadUrl属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDownloadUrl(String value) {
        this.downloadUrl = value;
    }

    /**
     * 获取saveResult属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getSaveResult() {
        return saveResult;
    }

    /**
     * 设置saveResult属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSaveResult(String value) {
        this.saveResult = value;
    }

    /**
     * 获取saveMessage属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getSaveMessage() {
        return saveMessage;
    }

    /**
     * 设置saveMessage属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSaveMessage(String value) {
        this.saveMessage = value;
    }

    /**
     * 获取saveTimes属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getSaveTimes() {
        return saveTimes;
    }

    /**
     * 设置saveTimes属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSaveTimes(String value) {
        this.saveTimes = value;
    }

    /**
     * 获取insuredReturns属性的值。
     *
     * @return possible object is
     * {@link InsuredReturns }
     */
    public InsuredReturns getInsuredReturns() {
        return insuredReturns;
    }

    /**
     * 设置insuredReturns属性的值。
     *
     * @param value allowed object is
     *              {@link InsuredReturns }
     */
    public void setInsuredReturns(InsuredReturns value) {
        this.insuredReturns = value;
    }

}
