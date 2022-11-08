package com.mainsteam.stm.webService.user;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


@WebService(name = "userWebServices", targetNamespace = "http://www.mainsteam.com/ms/userService/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface UserWebService {

	@WebMethod(action = "UpdateAppAcctSoap",operationName="UpdateAppAcctSoap")
	@WebResult
	public RequestInfo UpdateAppAcctSoap(RequestInfo requestInfo);
	
	@WebMethod(action = "addUser")
	@WebResult
	public String addUser(UserAgent userAgent);

	@WebMethod(action = "deleteUser")
	@WebResult
	public String deleteUser(UserAgent userAgent);
	
	@WebMethod(action = "updateUser")
	@WebResult
	public String updateUser(UserAgent userAgent);
}
