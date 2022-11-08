package com.mainsteam.stm.webService.user.client;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2015-06-04T09:49:49.879+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebServiceClient(name = "BaseInforServiceBusImplService", 
                  targetNamespace = "http://impl.service.webservice.stsm.ms.mainsteam.com/")
public class UserServiceImpl extends Service {

    public final static QName SERVICE = new QName("http://impl.service.webservice.stsm.ms.mainsteam.com/", "BaseInforServiceBusImplService");
    public final static QName UserServiceImplPort = new QName("http://impl.service.webservice.stsm.ms.mainsteam.com/", "BaseInforServiceBusImplPort");

    public UserServiceImpl(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public UserServiceImpl(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public UserServiceImpl(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public UserServiceImpl(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns BaseInfoServiceBus
     */
    @WebEndpoint(name = "BaseInforServiceBusImplPort")
    public UserServiceApi getUserServiceImplPort() {
        return super.getPort(UserServiceImplPort, UserServiceApi.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns BaseInfoServiceBus
     */
    @WebEndpoint(name = "BaseInforServiceBusImplPort")
    public UserServiceApi getUserServiceImplPort(WebServiceFeature... features) {
        return super.getPort(UserServiceImplPort, UserServiceApi.class, features);
    }

}
