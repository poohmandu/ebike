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
 *         &lt;element ref="{}GeneralInfoReturn"/>
 *         &lt;element ref="{}PolicyInfoReturns"/>
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
    "generalInfoReturn",
    "policyInfoReturns"
})
@XmlRootElement(name = "ReturnInfo")
public class ReturnInfo {

    @XmlElement(name = "GeneralInfoReturn", required = true)
    protected GeneralInfoReturn generalInfoReturn;
    @XmlElement(name = "PolicyInfoReturns", required = true)
    protected PolicyInfoReturns policyInfoReturns;

    /**
     * 获取generalInfoReturn属性的值。
     *
     * @return
     *     possible object is
     *     {@link GeneralInfoReturn }
     *
     */
    public GeneralInfoReturn getGeneralInfoReturn() {
        return generalInfoReturn;
    }

    /**
     * 设置generalInfoReturn属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link GeneralInfoReturn }
     *
     */
    public void setGeneralInfoReturn(GeneralInfoReturn value) {
        this.generalInfoReturn = value;
    }

    /**
     * 获取policyInfoReturns属性的值。
     *
     * @return
     *     possible object is
     *     {@link PolicyInfoReturns }
     *
     */
    public PolicyInfoReturns getPolicyInfoReturns() {
        return policyInfoReturns;
    }

    /**
     * 设置policyInfoReturns属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link PolicyInfoReturns }
     *
     */
    public void setPolicyInfoReturns(PolicyInfoReturns value) {
        this.policyInfoReturns = value;
    }

}
