package com.mainsteam.ms;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.14
 * 2015-05-29T13:42:00.453+08:00
 * Generated source version: 2.7.14
 * 
 */
@WebService(targetNamespace = "http://www.mainsteam.com/ms", name = "moServiceBus")
@XmlSeeAlso({ObjectFactory.class, com.mainsteam.ms.cms.schemas.ObjectFactory.class})
public interface MoServiceBus {

    @RequestWrapper(localName = "sendMoAttributeValue", targetNamespace = "http://www.mainsteam.com/ms", className = "com.mainsteam.ms.SendMoAttributeValue")
    @WebMethod
    @ResponseWrapper(localName = "sendMoAttributeValueResponse", targetNamespace = "http://www.mainsteam.com/ms", className = "com.mainsteam.ms.SendMoAttributeValueResponse")
    public void sendMoAttributeValue(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    ) throws WSException;
}
