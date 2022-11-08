package com.mainsteam.stm.webService.sms;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "smsWebServices", targetNamespace = "http://www.mainsteam.com/ms/smsWebServices/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface SmsWebService {

	// 获取最近一小时所有告警信息
	@WebResult
	@WebMethod(action = "sendMessage")
	public SmsResult sendMessage(@WebParam()SmsBean smsBean);
}
