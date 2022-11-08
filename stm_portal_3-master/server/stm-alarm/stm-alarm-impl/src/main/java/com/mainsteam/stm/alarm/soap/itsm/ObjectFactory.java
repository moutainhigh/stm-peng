
package com.mainsteam.stm.alarm.soap.itsm;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.mainsteam.stm.alarm.soap.itsm package. 
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

    private final static QName _SendAlarms_QNAME = new QName("http://www.mainsteam.com/ms", "sendAlarms");
    private final static QName _SendAlarmsResponse_QNAME = new QName("http://www.mainsteam.com/ms", "sendAlarmsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.mainsteam.stm.alarm.soap.itsm
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendAlarms }
     * 
     */
    public SendAlarms createSendAlarms() {
        return new SendAlarms();
    }

    /**
     * Create an instance of {@link SendAlarmsResponse }
     * 
     */
    public SendAlarmsResponse createSendAlarmsResponse() {
        return new SendAlarmsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendAlarms }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mainsteam.com/ms", name = "sendAlarms")
    public JAXBElement<SendAlarms> createSendAlarms(SendAlarms value) {
        return new JAXBElement<SendAlarms>(_SendAlarms_QNAME, SendAlarms.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendAlarmsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mainsteam.com/ms", name = "sendAlarmsResponse")
    public JAXBElement<SendAlarmsResponse> createSendAlarmsResponse(SendAlarmsResponse value) {
        return new JAXBElement<SendAlarmsResponse>(_SendAlarmsResponse_QNAME, SendAlarmsResponse.class, null, value);
    }

}
