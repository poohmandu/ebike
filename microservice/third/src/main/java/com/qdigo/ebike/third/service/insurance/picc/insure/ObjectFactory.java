
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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the cn.com.epicc.ecooperation.webservice package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TransitService_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "transitService");
    private final static QName _TransitServiceResponse_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "transitServiceResponse");
    private final static QName _ModifyService_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "modifyService");
    private final static QName _ModifyServiceResponse_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "modifyServiceResponse");
    private final static QName _ShortModifyService_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "shortModifyService");
    private final static QName _ShortModifyServiceResponse_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "shortModifyServiceResponse");
    private final static QName _PrintProofService_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "printProofService");
    private final static QName _PrintProofServiceResponse_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "printProofServiceResponse");
    private final static QName _InsureService_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "insureService");
    private final static QName _InsureServiceResponse_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "insureServiceResponse");
    private final static QName _ShortInsureService_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "shortInsureService");
    private final static QName _ShortInsureServiceResponse_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "shortInsureServiceResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cn.com.epicc.ecooperation.webservice
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TransitService }
     *
     */
    public TransitService createTransitService() {
        return new TransitService();
    }

    /**
     * Create an instance of {@link TransitServiceResponse }
     *
     */
    public TransitServiceResponse createTransitServiceResponse() {
        return new TransitServiceResponse();
    }

    /**
     * Create an instance of {@link ModifyService }
     *
     */
    public ModifyService createModifyService() {
        return new ModifyService();
    }

    /**
     * Create an instance of {@link ModifyServiceResponse }
     *
     */
    public ModifyServiceResponse createModifyServiceResponse() {
        return new ModifyServiceResponse();
    }

    /**
     * Create an instance of {@link ShortModifyService }
     *
     */
    public ShortModifyService createShortModifyService() {
        return new ShortModifyService();
    }

    /**
     * Create an instance of {@link ShortModifyServiceResponse }
     *
     */
    public ShortModifyServiceResponse createShortModifyServiceResponse() {
        return new ShortModifyServiceResponse();
    }

    /**
     * Create an instance of {@link PrintProofService }
     *
     */
    public PrintProofService createPrintProofService() {
        return new PrintProofService();
    }

    /**
     * Create an instance of {@link PrintProofServiceResponse }
     *
     */
    public PrintProofServiceResponse createPrintProofServiceResponse() {
        return new PrintProofServiceResponse();
    }

    /**
     * Create an instance of {@link InsureService }
     *
     */
    public InsureService createInsureService() {
        return new InsureService();
    }

    /**
     * Create an instance of {@link InsureServiceResponse }
     *
     */
    public InsureServiceResponse createInsureServiceResponse() {
        return new InsureServiceResponse();
    }

    /**
     * Create an instance of {@link ShortInsureService }
     *
     */
    public ShortInsureService createShortInsureService() {
        return new ShortInsureService();
    }

    /**
     * Create an instance of {@link ShortInsureServiceResponse }
     *
     */
    public ShortInsureServiceResponse createShortInsureServiceResponse() {
        return new ShortInsureServiceResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransitService }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "transitService")
    public JAXBElement<TransitService> createTransitService(TransitService value) {
        return new JAXBElement<TransitService>(_TransitService_QNAME, TransitService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransitServiceResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "transitServiceResponse")
    public JAXBElement<TransitServiceResponse> createTransitServiceResponse(TransitServiceResponse value) {
        return new JAXBElement<TransitServiceResponse>(_TransitServiceResponse_QNAME, TransitServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModifyService }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "modifyService")
    public JAXBElement<ModifyService> createModifyService(ModifyService value) {
        return new JAXBElement<ModifyService>(_ModifyService_QNAME, ModifyService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModifyServiceResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "modifyServiceResponse")
    public JAXBElement<ModifyServiceResponse> createModifyServiceResponse(ModifyServiceResponse value) {
        return new JAXBElement<ModifyServiceResponse>(_ModifyServiceResponse_QNAME, ModifyServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShortModifyService }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "shortModifyService")
    public JAXBElement<ShortModifyService> createShortModifyService(ShortModifyService value) {
        return new JAXBElement<ShortModifyService>(_ShortModifyService_QNAME, ShortModifyService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShortModifyServiceResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "shortModifyServiceResponse")
    public JAXBElement<ShortModifyServiceResponse> createShortModifyServiceResponse(ShortModifyServiceResponse value) {
        return new JAXBElement<ShortModifyServiceResponse>(_ShortModifyServiceResponse_QNAME, ShortModifyServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrintProofService }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "printProofService")
    public JAXBElement<PrintProofService> createPrintProofService(PrintProofService value) {
        return new JAXBElement<PrintProofService>(_PrintProofService_QNAME, PrintProofService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrintProofServiceResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "printProofServiceResponse")
    public JAXBElement<PrintProofServiceResponse> createPrintProofServiceResponse(PrintProofServiceResponse value) {
        return new JAXBElement<PrintProofServiceResponse>(_PrintProofServiceResponse_QNAME, PrintProofServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsureService }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "insureService")
    public JAXBElement<InsureService> createInsureService(InsureService value) {
        return new JAXBElement<InsureService>(_InsureService_QNAME, InsureService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsureServiceResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "insureServiceResponse")
    public JAXBElement<InsureServiceResponse> createInsureServiceResponse(InsureServiceResponse value) {
        return new JAXBElement<InsureServiceResponse>(_InsureServiceResponse_QNAME, InsureServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShortInsureService }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "shortInsureService")
    public JAXBElement<ShortInsureService> createShortInsureService(ShortInsureService value) {
        return new JAXBElement<ShortInsureService>(_ShortInsureService_QNAME, ShortInsureService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShortInsureServiceResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "shortInsureServiceResponse")
    public JAXBElement<ShortInsureServiceResponse> createShortInsureServiceResponse(ShortInsureServiceResponse value) {
        return new JAXBElement<ShortInsureServiceResponse>(_ShortInsureServiceResponse_QNAME, ShortInsureServiceResponse.class, null, value);
    }

}
