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
 *         &lt;element ref="{}AppliName"/>
 *         &lt;element ref="{}AppliIdType"/>
 *         &lt;element ref="{}AppliIdNo"/>
 *         &lt;element ref="{}AppliIdMobile"/>
 *         &lt;element ref="{}AppliAddress"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "appliName",
        "appliIdType",
        "appliIdNo",
        "appliIdMobile",
        "appliAddress"
})
@XmlRootElement(name = "Applicant")
public class Applicant {

    @XmlElement(name = "AppliName", required = true)
    protected String appliName;
    @XmlElement(name = "AppliIdType", required = true)
    protected String appliIdType;
    @XmlElement(name = "AppliIdNo", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String appliIdNo;
    @XmlElement(name = "AppliIdMobile", required = true)
    protected String appliIdMobile;
    @XmlElement(name = "AppliAddress", required = true)
    protected String appliAddress;

    /**
     * 获取appliName属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getAppliName() {
        return appliName;
    }

    /**
     * 设置appliName属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAppliName(String value) {
        this.appliName = value;
    }

    /**
     * 获取appliIdType属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getAppliIdType() {
        return appliIdType;
    }

    /**
     * 设置appliIdType属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAppliIdType(String value) {
        this.appliIdType = value;
    }

    /**
     * 获取appliIdNo属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getAppliIdNo() {
        return appliIdNo;
    }

    /**
     * 设置appliIdNo属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAppliIdNo(String value) {
        this.appliIdNo = value;
    }

    /**
     * 获取appliIdMobile属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getAppliIdMobile() {
        return appliIdMobile;
    }

    /**
     * 设置appliIdMobile属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAppliIdMobile(String value) {
        this.appliIdMobile = value;
    }

    /**
     * 获取appliAddress属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getAppliAddress() {
        return appliAddress;
    }

    /**
     * 设置appliAddress属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAppliAddress(String value) {
        this.appliAddress = value;
    }

}
