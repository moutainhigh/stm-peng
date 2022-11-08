
package com.mainsteam.stm.webService.user.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import com.mainsteam.stm.webService.cmdb.client.cms.schemas.WsException;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.mainsteam.stm.webService.user.client package. 
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

    private final static QName _SetUserResponse_QNAME = new QName("http://www.mainsteam.com/ms", "setUserResponse");
    private final static QName _WsException_QNAME = new QName("http://www.mainsteam.com/ms", "wsException");
    private final static QName _SetUser_QNAME = new QName("http://www.mainsteam.com/ms", "setUser");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.mainsteam.stm.webService.user.client
     * 
     */
    public ObjectFactory() {
    }


    /**
     * Create an instance of {@link SetUserResponse }
     * 
     */
    public SetUserResponse createSetUserResponse() {
        return new SetUserResponse();
    }

    /**
     * Create an instance of {@link WsException }
     * 
     */
    public WsException createWsException() {
        return new WsException();
    }

    /**
     * Create an instance of {@link SetUser }
     * 
     */
    public SetUser createSetUser() {
        return new SetUser();
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mainsteam.com/ms", name = "setUserResponse")
    public JAXBElement<SetUserResponse> createSetUserResponse(SetUserResponse value) {
        return new JAXBElement<SetUserResponse>(_SetUserResponse_QNAME, SetUserResponse.class, null, value);
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mainsteam.com/ms", name = "wsException")
    public JAXBElement<WsException> createWsException(WsException value) {
        return new JAXBElement<WsException>(_WsException_QNAME, WsException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mainsteam.com/ms", name = "setUser")
    public JAXBElement<SetUser> createSetUser(SetUser value) {
        return new JAXBElement<SetUser>(_SetUser_QNAME, SetUser.class, null, value);
    }
}
