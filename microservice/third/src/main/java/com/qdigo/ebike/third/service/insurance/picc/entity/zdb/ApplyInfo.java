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
 *         &lt;element ref="{}GeneralInfo"/>
 *         &lt;element ref="{}PolicyInfos"/>
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
    "generalInfo",
    "policyInfos"
})
@XmlRootElement(name = "ApplyInfo")
public class ApplyInfo {

    @XmlElement(name = "GeneralInfo", required = true)
    protected GeneralInfo generalInfo;
    @XmlElement(name = "PolicyInfos", required = true)
    protected PolicyInfos policyInfos;

    /**
     * 获取generalInfo属性的值。
     *
     * @return
     *     possible object is
     *     {@link GeneralInfo }
     *
     */
    public GeneralInfo getGeneralInfo() {
        return generalInfo;
    }

    /**
     * 设置generalInfo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link GeneralInfo }
     *
     */
    public void setGeneralInfo(GeneralInfo value) {
        this.generalInfo = value;
    }

    /**
     * 获取policyInfos属性的值。
     *
     * @return
     *     possible object is
     *     {@link PolicyInfos }
     *
     */
    public PolicyInfos getPolicyInfos() {
        return policyInfos;
    }

    /**
     * 设置policyInfos属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link PolicyInfos }
     *
     */
    public void setPolicyInfos(PolicyInfos value) {
        this.policyInfos = value;
    }

}
