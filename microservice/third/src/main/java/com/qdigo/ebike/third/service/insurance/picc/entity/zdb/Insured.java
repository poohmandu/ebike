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
// 生成时间: 2018.03.01 时间 07:08:48 PM CST
//


package com.qdigo.ebike.third.service.insurance.picc.entity.zdb;

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
 *         &lt;element ref="{}InsuredSeqNo"/>
 *         &lt;element ref="{}InsuredName"/>
 *         &lt;element ref="{}InsuredIdType"/>
 *         &lt;element ref="{}InsuredIdNo"/>
 *         &lt;element ref="{}InsuredAddress"/>
 *         &lt;element ref="{}InsuredIdMobile"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "insuredSeqNo",
        "insuredName",
        "insuredIdType",
        "insuredIdNo",
        "insuredAddress",
        "insuredIdMobile"
})
@XmlRootElement(name = "Insured")
public class Insured {

    @XmlElement(name = "InsuredSeqNo", required = true)
    protected String insuredSeqNo;
    @XmlElement(name = "InsuredName", required = true)
    protected String insuredName;
    @XmlElement(name = "InsuredIdType", required = true)
    protected String insuredIdType;
    @XmlElement(name = "InsuredIdNo", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String insuredIdNo;
    @XmlElement(name = "InsuredAddress", required = true)
    protected String insuredAddress;
    @XmlElement(name = "InsuredIdMobile", required = true)
    protected String insuredIdMobile;

    /**
     * 获取insuredSeqNo属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getInsuredSeqNo() {
        return insuredSeqNo;
    }

    /**
     * 设置insuredSeqNo属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInsuredSeqNo(String value) {
        this.insuredSeqNo = value;
    }

    /**
     * 获取insuredName属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getInsuredName() {
        return insuredName;
    }

    /**
     * 设置insuredName属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInsuredName(String value) {
        this.insuredName = value;
    }

    /**
     * 获取insuredIdType属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getInsuredIdType() {
        return insuredIdType;
    }

    /**
     * 设置insuredIdType属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInsuredIdType(String value) {
        this.insuredIdType = value;
    }

    /**
     * 获取insuredIdNo属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getInsuredIdNo() {
        return insuredIdNo;
    }

    /**
     * 设置insuredIdNo属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInsuredIdNo(String value) {
        this.insuredIdNo = value;
    }

    /**
     * 获取insuredAddress属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getInsuredAddress() {
        return insuredAddress;
    }

    /**
     * 设置insuredAddress属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInsuredAddress(String value) {
        this.insuredAddress = value;
    }

    /**
     * 获取insuredIdMobile属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getInsuredIdMobile() {
        return insuredIdMobile;
    }

    /**
     * 设置insuredIdMobile属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInsuredIdMobile(String value) {
        this.insuredIdMobile = value;
    }

}
