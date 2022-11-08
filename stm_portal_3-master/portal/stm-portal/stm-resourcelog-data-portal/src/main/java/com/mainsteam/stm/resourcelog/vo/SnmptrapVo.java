package com.mainsteam.stm.resourcelog.vo;

import java.io.Serializable;

import com.mainsteam.stm.resourcelog.util.MibUtil;

public class SnmptrapVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4271604199889257957L;

	/**
	 * 设备IP
	 */
	private String addressIP;
	
	/**
	 * trap产生时间
	 */
	private String receiveTime;
	
	/**
	 * trap内容
	 */
	private String message;
	
	/**
	 * 设备类型OID
	 */
	private String enterprise;
	
	/**
	 * 普通类型
	 */
	private int genericType;
	
	/**
	 * 普通类型的名字
	 */
	private String commonTypeName;
	
	private int specificType;
	
	/**
	 * 具体trap的OID
	 */
	private String oID;
	
	private String trapName;

	private String trapType;

	//定制项目字段，vrops的告警ID
	private String alertID;
	//定制项目字段，vrops的操作类型
	private String operationType;

	public String getAddressIP() {
		return addressIP;
	}

	public void setAddressIP(String addressIP) {
		this.addressIP = addressIP;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}

	public int getGenericType() {
		return genericType;
	}

	public void setGenericType(int genericType) {
		this.genericType = genericType;
	}

	public int getSpecificType() {
		return specificType;
	}

	public void setSpecificType(int specificType) {
		this.specificType = specificType;
	}

	public String getoID() {
		return oID;
	}

	public void setoID(String oID) {
		this.oID = oID;
	}

	public String getCommonTypeName() {
		return commonTypeName;
	}

	public void setCommonTypeName(String commonTypeName) {
		this.commonTypeName = commonTypeName;
	}

	public String getTrapType() {
		return trapType;
	}

	public void setTrapType(String trapType) {
		this.trapType = trapType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getTrapName() {
		//通过MibUtil获取trap的名字
		oID = "."+this.getEnterprise() + ".6." + this.getSpecificType();
		try {
			trapName = MibUtil.getIsmName(oID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trapName;
	}

	public void setTrapName(String trapName) {
		this.trapName = trapName;
	}
}
