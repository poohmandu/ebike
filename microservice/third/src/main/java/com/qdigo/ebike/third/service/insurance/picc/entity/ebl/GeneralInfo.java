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
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}UUID"/>
 *         &lt;element ref="{}PlateformCode"/>
 *         &lt;element ref="{}Md5Value"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "uuid",
    "plateformCode",
    "md5Value"
})
@XmlRootElement(name = "GeneralInfo")
public class GeneralInfo {

    @XmlElement(name = "UUID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String uuid;
    @XmlElement(name = "PlateformCode", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String plateformCode;
    @XmlElement(name = "Md5Value", required = true)
    protected String md5Value;

    /**
     * 获取uuid属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * 设置uuid属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUUID(String value) {
        this.uuid = value;
    }

    /**
     * 获取plateformCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPlateformCode() {
        return plateformCode;
    }

    /**
     * 设置plateformCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPlateformCode(String value) {
        this.plateformCode = value;
    }

    /**
     * 获取md5Value属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMd5Value() {
        return md5Value;
    }

    /**
     * 设置md5Value属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMd5Value(String value) {
        this.md5Value = value;
    }

}
