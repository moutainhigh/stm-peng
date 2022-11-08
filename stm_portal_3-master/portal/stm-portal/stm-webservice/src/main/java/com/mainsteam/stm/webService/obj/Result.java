package com.mainsteam.stm.webService.obj;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Result {
	/**
	 * 返回的状态号码
	 */
	@XmlElement(required = true)
	private String resultCode;
	/**
	 * 异常的消息编码
	 */
	@XmlElement(required = true)
	private String errorMsg;
	
	/**
	 * 返回数据
	 */
	@XmlElement(required = true)
	private Object data;
	

	
	public void setResultcodeEnum(ResultCodeEnum resultcodeEnum) {
		this.resultCode = resultcodeEnum.getResultCode();
		this.errorMsg = resultcodeEnum.getResultDecp();
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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	

}
