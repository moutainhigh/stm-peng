
package com.mainsteam.ms;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.mainsteam.ms package.
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

    private final static QName _SendMoAttributeValue_QNAME = new QName("http://www.mainsteam.com/ms", "sendMoAttributeValue");
    private final static QName _SendMoAttributeValueResponse_QNAME = new QName("http://www.mainsteam.com/ms", "sendMoAttributeValueResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.mainsteam.ms
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendMoAttributeValue }
     * 
     */
    public SendMoAttributeValue createSendMoAttributeValue() {
        return new SendMoAttributeValue();
    }

    /**
     * Create an instance of {@link SendMoAttributeValueResponse }
     * 
     */
    public SendMoAttributeValueResponse createSendMoAttributeValueResponse() {
        return new SendMoAttributeValueResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendMoAttributeValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mainsteam.com/ms", name = "sendMoAttributeValue")
    public JAXBElement<SendMoAttributeValue> createSendMoAttributeValue(SendMoAttributeValue value) {
        return new JAXBElement<SendMoAttributeValue>(_SendMoAttributeValue_QNAME, SendMoAttributeValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendMoAttributeValueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mainsteam.com/ms", name = "sendMoAttributeValueResponse")
    public JAXBElement<SendMoAttributeValueResponse> createSendMoAttributeValueResponse(SendMoAttributeValueResponse value) {
        return new JAXBElement<SendMoAttributeValueResponse>(_SendMoAttributeValueResponse_QNAME, SendMoAttributeValueResponse.class, null, value);
    }

}
