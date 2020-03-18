
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

package com.qdigo.ebike.third.service.insurance.picc.insure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>insureService complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="insureService"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="interfaceNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="datas" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsureService", propOrder = {
    "interfaceNo",
    "datas"
})
public class InsureService {

    protected String interfaceNo;
    protected String datas;

    /**
     * 获取interfaceNo属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInterfaceNo() {
        return interfaceNo;
    }

    /**
     * 设置interfaceNo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInterfaceNo(String value) {
        this.interfaceNo = value;
    }

    /**
     * 获取datas属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDatas() {
        return datas;
    }

    /**
     * 设置datas属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDatas(String value) {
        this.datas = value;
    }

}
