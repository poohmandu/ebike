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
 *         &lt;element ref="{}SerialNo"/>
 *         &lt;element ref="{}RiskCode"/>
 *         &lt;element ref="{}OperateTimes"/>
 *         &lt;element ref="{}StartDate"/>
 *         &lt;element ref="{}EndDate"/>
 *         &lt;element ref="{}StartHour"/>
 *         &lt;element ref="{}EndHour"/>
 *         &lt;element ref="{}SumAmount"/>
 *         &lt;element ref="{}SumPremium"/>
 *         &lt;element ref="{}ArguSolution"/>
 *         &lt;element ref="{}Quantity"/>
 *         &lt;element ref="{}Car_ShipNo"/>
 *         &lt;element ref="{}SeatNo"/>
 *         &lt;element ref="{}Extend1"/>
 *         &lt;element ref="{}Extend2"/>
 *         &lt;element ref="{}InsuredPlan"/>
 *         &lt;element ref="{}Applicant"/>
 *         &lt;element ref="{}Insureds"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "serialNo",
        "riskCode",
        "operateTimes",
        "startDate",
        "endDate",
        "startHour",
        "endHour",
        "sumAmount",
        "sumPremium",
        "arguSolution",
        "quantity",
        "carShipNo",
        "seatNo",
        "extend1",
        "extend2",
        "insuredPlan",
        "applicant",
        "insureds"
})
@XmlRootElement(name = "PolicyInfo")
public class PolicyInfo {

    @XmlElement(name = "SerialNo", required = true)
    protected String serialNo;
    @XmlElement(name = "RiskCode", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String riskCode;
    @XmlElement(name = "OperateTimes", required = true)
    protected String operateTimes;
    @XmlElement(name = "StartDate", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String startDate;
    @XmlElement(name = "EndDate", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String endDate;
    @XmlElement(name = "StartHour", required = true)
    protected String startHour;
    @XmlElement(name = "EndHour", required = true)
    protected String endHour;
    @XmlElement(name = "SumAmount", required = true)
    protected String sumAmount;
    @XmlElement(name = "SumPremium", required = true)
    protected String sumPremium;
    @XmlElement(name = "ArguSolution", required = true)
    protected String arguSolution;
    @XmlElement(name = "Quantity", required = true)
    protected String quantity;
    @XmlElement(name = "Car_ShipNo", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String carShipNo;
    @XmlElement(name = "SeatNo", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String seatNo;
    @XmlElement(name = "Extend1", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String extend1;
    @XmlElement(name = "Extend2", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String extend2;
    @XmlElement(name = "InsuredPlan", required = true)
    protected InsuredPlan insuredPlan;
    @XmlElement(name = "Applicant", required = true)
    protected Applicant applicant;
    @XmlElement(name = "Insureds", required = true)
    protected Insureds insureds;

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
     * 获取riskCode属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getRiskCode() {
        return riskCode;
    }

    /**
     * 设置riskCode属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRiskCode(String value) {
        this.riskCode = value;
    }

    /**
     * 获取operateTimes属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getOperateTimes() {
        return operateTimes;
    }

    /**
     * 设置operateTimes属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOperateTimes(String value) {
        this.operateTimes = value;
    }

    /**
     * 获取startDate属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * 设置startDate属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStartDate(String value) {
        this.startDate = value;
    }

    /**
     * 获取endDate属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * 设置endDate属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEndDate(String value) {
        this.endDate = value;
    }

    /**
     * 获取startHour属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getStartHour() {
        return startHour;
    }

    /**
     * 设置startHour属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStartHour(String value) {
        this.startHour = value;
    }

    /**
     * 获取endHour属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getEndHour() {
        return endHour;
    }

    /**
     * 设置endHour属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEndHour(String value) {
        this.endHour = value;
    }

    /**
     * 获取sumAmount属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getSumAmount() {
        return sumAmount;
    }

    /**
     * 设置sumAmount属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSumAmount(String value) {
        this.sumAmount = value;
    }

    /**
     * 获取sumPremium属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getSumPremium() {
        return sumPremium;
    }

    /**
     * 设置sumPremium属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSumPremium(String value) {
        this.sumPremium = value;
    }

    /**
     * 获取arguSolution属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getArguSolution() {
        return arguSolution;
    }

    /**
     * 设置arguSolution属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setArguSolution(String value) {
        this.arguSolution = value;
    }

    /**
     * 获取quantity属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * 设置quantity属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setQuantity(String value) {
        this.quantity = value;
    }

    /**
     * 获取carShipNo属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getCarShipNo() {
        return carShipNo;
    }

    /**
     * 设置carShipNo属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCarShipNo(String value) {
        this.carShipNo = value;
    }

    /**
     * 获取seatNo属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getSeatNo() {
        return seatNo;
    }

    /**
     * 设置seatNo属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSeatNo(String value) {
        this.seatNo = value;
    }

    /**
     * 获取extend1属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getExtend1() {
        return extend1;
    }

    /**
     * 设置extend1属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setExtend1(String value) {
        this.extend1 = value;
    }

    /**
     * 获取extend2属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getExtend2() {
        return extend2;
    }

    /**
     * 设置extend2属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setExtend2(String value) {
        this.extend2 = value;
    }

    /**
     * 获取insuredPlan属性的值。
     *
     * @return possible object is
     * {@link InsuredPlan }
     */
    public InsuredPlan getInsuredPlan() {
        return insuredPlan;
    }

    /**
     * 设置insuredPlan属性的值。
     *
     * @param value allowed object is
     *              {@link InsuredPlan }
     */
    public void setInsuredPlan(InsuredPlan value) {
        this.insuredPlan = value;
    }

    /**
     * 获取applicant属性的值。
     *
     * @return possible object is
     * {@link Applicant }
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * 设置applicant属性的值。
     *
     * @param value allowed object is
     *              {@link Applicant }
     */
    public void setApplicant(Applicant value) {
        this.applicant = value;
    }

    /**
     * 获取insureds属性的值。
     *
     * @return possible object is
     * {@link Insureds }
     */
    public Insureds getInsureds() {
        return insureds;
    }

    /**
     * 设置insureds属性的值。
     *
     * @param value allowed object is
     *              {@link Insureds }
     */
    public void setInsureds(Insureds value) {
        this.insureds = value;
    }

}
