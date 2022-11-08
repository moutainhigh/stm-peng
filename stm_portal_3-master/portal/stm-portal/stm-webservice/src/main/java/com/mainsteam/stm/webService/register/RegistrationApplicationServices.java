package com.mainsteam.stm.webService.register;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "registerServices", targetNamespace = "http://www.mainsteam.com/ms/registerService/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RegistrationApplicationServices {
	
	/**
	 * 门户注册到ITM
	 * @param registerJson
	 * @return 用户同步方法、注销方法的JSON
	 */
	@WebMethod(action = "registrationApplication")
	public String registrationApplication(@WebParam(name="registerJson")String registerJson);
	
	/**
	 * 从门户注销ITM
	 * @param cancelJson
	 */
	@WebMethod(action = "cancellationRegistration")
	public void cancellationRegistration(@WebParam(name="cancelJson")String cancelJson);
	
	
	/**
	 * 门户用户同步
	 * @param userJson
	 * @return
	 */
	@WebMethod(action = "addUser")
	@WebResult
	public String addUser(String userJson);
	
	/**
	 * 门户用户删除
	 * @param deleteUser
	 * @return
	 */
	@WebMethod(action = "deleteUser")
	@WebResult
	public String deleteUser(String userJson);
	
	/**
	 * 修改皮肤
	 * @param userJson
	 * @return
	 */
	@WebMethod(action = "changeSkin")
	@WebResult
	public String changeSkin(String userJson);
	
}
