
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

package com.qdigo.ebike.third.service.insurance.picc.print;

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

    private final static QName _PrintPolicyService_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "printPolicyService");
    private final static QName _PrintPolicyServiceResponse_QNAME = new QName("http://webservice.ecooperation.epicc.com.cn/", "printPolicyServiceResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cn.com.epicc.ecooperation.webservice
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PrintPolicyService }
     *
     */
    public PrintPolicyService createPrintPolicyService() {
        return new PrintPolicyService();
    }

    /**
     * Create an instance of {@link PrintPolicyServiceResponse }
     *
     */
    public PrintPolicyServiceResponse createPrintPolicyServiceResponse() {
        return new PrintPolicyServiceResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrintPolicyService }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "printPolicyService")
    public JAXBElement<PrintPolicyService> createPrintPolicyService(PrintPolicyService value) {
        return new JAXBElement<PrintPolicyService>(_PrintPolicyService_QNAME, PrintPolicyService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrintPolicyServiceResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://webservice.ecooperation.epicc.com.cn/", name = "printPolicyServiceResponse")
    public JAXBElement<PrintPolicyServiceResponse> createPrintPolicyServiceResponse(PrintPolicyServiceResponse value) {
        return new JAXBElement<PrintPolicyServiceResponse>(_PrintPolicyServiceResponse_QNAME, PrintPolicyServiceResponse.class, null, value);
    }

}
