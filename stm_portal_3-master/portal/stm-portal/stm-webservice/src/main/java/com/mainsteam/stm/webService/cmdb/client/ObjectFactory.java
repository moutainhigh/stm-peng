package com.mainsteam.stm.webService.cmdb.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

	private final static QName _SendMosResponse_QNAME = new QName(
			"http://www.mainsteam.com/ms", "sendMosResponse");
	private final static QName _SendMos_QNAME = new QName(
			"http://www.mainsteam.com/ms", "sendMos");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: com.mainsteam.ms
	 * 
	 */
	public ObjectFactory() {
	}

	/**
     * Create an instance of {@link SendMosResponse }
     * 
     */
    public SendMosResponse createSendMosResponse() {
        return new SendMosResponse();
    }

    /**
     * Create an instance of {@link SendMos }
     * 
     */
    public SendMos createSendMos() {
        return new SendMos();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendMosResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mainsteam.com/ms", name = "sendMosResponse")
    public JAXBElement<SendMosResponse> createSendMosResponse(SendMosResponse value) {
        return new JAXBElement<SendMosResponse>(_SendMosResponse_QNAME, SendMosResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendMos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mainsteam.com/ms", name = "sendMos")
    public JAXBElement<SendMos> createSendMos(SendMos value) {
        return new JAXBElement<SendMos>(_SendMos_QNAME, SendMos.class, null, value);
    }

}
