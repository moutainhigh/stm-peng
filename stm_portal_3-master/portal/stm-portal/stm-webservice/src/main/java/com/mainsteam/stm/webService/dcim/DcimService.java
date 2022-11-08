package com.mainsteam.stm.webService.dcim;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "DcimService",targetNamespace="http://www.mainsteam.com/ms/dcimService/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DcimService {

    @WebMethod
    public String dcim(@WebParam(name = "dcim", targetNamespace = "http://www.mainsteam.com/Dcim") Dcim dcim);

    //批量查询资源的指标数据
    @WebMethod(action = "getMetricData")
    public String getMetricData(
            @WebParam(name="instanceIds")Long[] instanceIds);
}
