
package com.mainsteam.stm.portal.threed.xfire.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "DataStreamHttpPort", targetNamespace = "http://service.webservice.monitoring.uinv.com")
@SOAPBinding(use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DataStreamPortType {


    @WebMethod(operationName = "closeAlarm", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String closeAlarm(
        @WebParam(name = "str", targetNamespace = "http://service.webservice.monitoring.uinv.com")
        String str);

    @WebMethod(operationName = "pushAlarm", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String pushAlarm(
        @WebParam(name = "str", targetNamespace = "http://service.webservice.monitoring.uinv.com")
        String str);

    @WebMethod(operationName = "pushAlarm1", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String pushAlarm1(
        @WebParam(name = "str", targetNamespace = "http://service.webservice.monitoring.uinv.com")
        String str,
        @WebParam(name = "rule", targetNamespace = "http://service.webservice.monitoring.uinv.com")
        boolean rule);

    @WebMethod(operationName = "pushMonitor1", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String pushMonitor1(
        @WebParam(name = "str", targetNamespace = "http://service.webservice.monitoring.uinv.com")
        String str,
        @WebParam(name = "rule", targetNamespace = "http://service.webservice.monitoring.uinv.com")
        boolean rule);

    @WebMethod(operationName = "addRackEquipment", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String addRackEquipment(
        @WebParam(name = "str", targetNamespace = "http://service.webservice.monitoring.uinv.com")
        String str);

    @WebMethod(operationName = "getNodeTree", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String getNodeTree();

    @WebMethod(operationName = "getNodeAppendTypeForTree", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String getNodeAppendTypeForTree();

    @WebMethod(operationName = "updateRackEquipment", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String updateRackEquipment(
        @WebParam(name = "str", targetNamespace = "http://service.webservice.monitoring.uinv.com")
        String str);

    @WebMethod(operationName = "closeMonitor", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String closeMonitor(
        @WebParam(name = "str", targetNamespace = "http://service.webservice.monitoring.uinv.com")
        String str);

    @WebMethod(operationName = "deleteRackEquipment", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String deleteRackEquipment(
        @WebParam(name = "str", targetNamespace = "http://service.webservice.monitoring.uinv.com")
        String str);

    @WebMethod(operationName = "pushMonitor", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String pushMonitor(
        @WebParam(name = "str", targetNamespace = "http://service.webservice.monitoring.uinv.com")
        String str);

    @WebMethod(operationName = "getProductInfo", action = "")
    @WebResult(name = "out", targetNamespace = "http://service.webservice.monitoring.uinv.com")
    public String getProductInfo();

}
