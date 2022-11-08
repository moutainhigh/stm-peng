package com.mainsteam.stm.webService.sms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SmsResult")
public class SmsResult {
	@XmlElement(name = "result", required = true)
	private boolean result;
	@XmlElement(name = "msg", required = false)
	private String msg;
	@XmlElement(name = "resultCode", required = false)
	private String resultCode;
	@XmlElement(name = "errorMsg", required = false)
	private String errorMsg;
	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
