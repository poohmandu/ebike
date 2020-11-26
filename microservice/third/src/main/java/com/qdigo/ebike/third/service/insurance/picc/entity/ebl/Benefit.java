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
 *         &lt;element ref="{}BenefitName"/>
 *         &lt;element ref="{}BenefitEName"/>
 *         &lt;element ref="{}BenefitSex"/>
 *         &lt;element ref="{}BenefitIdType"/>
 *         &lt;element ref="{}BenefitIdNo"/>
 *         &lt;element ref="{}BenefitBirthday"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "benefitName",
        "benefitEName",
        "benefitSex",
        "benefitIdType",
        "benefitIdNo",
        "benefitBirthday"
})
@XmlRootElement(name = "Benefit")
public class Benefit {

    @XmlElement(name = "BenefitName", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String benefitName;
    @XmlElement(name = "BenefitEName", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String benefitEName;
    @XmlElement(name = "BenefitSex", required = true)
    protected String benefitSex;
    @XmlElement(name = "BenefitIdType", required = true)
    protected String benefitIdType;
    @XmlElement(name = "BenefitIdNo", required = true)
    protected String benefitIdNo;
    @XmlElement(name = "BenefitBirthday", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String benefitBirthday;

    /**
     * 获取benefitName属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getBenefitName() {
        return benefitName;
    }

    /**
     * 设置benefitName属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBenefitName(String value) {
        this.benefitName = value;
    }

    /**
     * 获取benefitEName属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getBenefitEName() {
        return benefitEName;
    }

    /**
     * 设置benefitEName属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBenefitEName(String value) {
        this.benefitEName = value;
    }

    /**
     * 获取benefitSex属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getBenefitSex() {
        return benefitSex;
    }

    /**
     * 设置benefitSex属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBenefitSex(String value) {
        this.benefitSex = value;
    }

    /**
     * 获取benefitIdType属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getBenefitIdType() {
        return benefitIdType;
    }

    /**
     * 设置benefitIdType属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBenefitIdType(String value) {
        this.benefitIdType = value;
    }

    /**
     * 获取benefitIdNo属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getBenefitIdNo() {
        return benefitIdNo;
    }

    /**
     * 设置benefitIdNo属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBenefitIdNo(String value) {
        this.benefitIdNo = value;
    }

    /**
     * 获取benefitBirthday属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getBenefitBirthday() {
        return benefitBirthday;
    }

    /**
     * 设置benefitBirthday属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBenefitBirthday(String value) {
        this.benefitBirthday = value;
    }

}
