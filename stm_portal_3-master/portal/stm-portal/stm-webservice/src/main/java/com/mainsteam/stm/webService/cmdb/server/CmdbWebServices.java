package com.mainsteam.stm.webService.cmdb.server;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(name = "ConfigItemMOClientBus", targetNamespace = "http://www.mainsteam.com/ms")
//@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface CmdbWebServices {
	
	@WebMethod
	public String getMoType();
}
