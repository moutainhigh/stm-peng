package com.mainsteam.stm.webService.user;

import java.io.Serializable;

public class RequestInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4680056292860535137L;

	private String code;
	
	private String sid;
	
	private String timestamp;
	
	private String serviceId;
	
	//user's account
	private String operatorId;
	
	private String operatorPwd;
	
	private String operatorIp;
	
	private String modifyMode;
	
	private String key;
	
	private String errCode;
	
	private String errDesc;
	
	private String userId;
	
	private String loginNo;
	
	private String rsp;
	
	private UserInfo uerInfo;
	
	public UserInfo getUerInfo() {
		return uerInfo;
	}

	public void setUerInfo(UserInfo uerInfo) {
		this.uerInfo = uerInfo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorPwd() {
		return operatorPwd;
	}

	public void setOperatorPwd(String operatorPwd) {
		this.operatorPwd = operatorPwd;
	}

	public String getOperatorIp() {
		return operatorIp;
	}

	public void setOperatorIp(String operatorIp) {
		this.operatorIp = operatorIp;
	}

	public String getModifyMode() {
		return modifyMode;
	}

	public void setModifyMode(String modifyMode) {
		this.modifyMode = modifyMode;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrDesc() {
		return errDesc;
	}

	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLoginNo() {
		return loginNo;
	}

	public void setLoginNo(String loginNo) {
		this.loginNo = loginNo;
	}

	public String getRsp() {
		return rsp;
	}

	public void setRsp(String rsp) {
		this.rsp = rsp;
	}
	
	
}
