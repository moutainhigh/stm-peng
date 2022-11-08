package com.mainsteam.stm.webService.itsm;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "SendITMService",targetNamespace="http://schemas.xmlsoap.org/soap/envelope/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface SendITMService {

	@WebMethod
	public void sendITM(@WebParam(name="sendITM",targetNamespace="http://www.mainsteam.com/SendITM")SendITM sendITM);
	
}
