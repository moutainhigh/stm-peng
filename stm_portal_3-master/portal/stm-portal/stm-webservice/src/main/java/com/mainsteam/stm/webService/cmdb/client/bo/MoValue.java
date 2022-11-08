package com.mainsteam.stm.webService.cmdb.client.bo;


public class MoValue {
	/**
	 * attribute所属MO的唯一标识
	 */
	private String moId;
	/**
	 * 管理对象类型唯一标识
	 */
	private String moTypeId;
	/**
	 * Mo信息发送端标识码
	 */
	private String source;
	
	/**
	 * 管理对象特征属性的值列表或子属性的值列表
	 */
	private AttributeValue[] attributes;

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}

	public String getMoTypeId() {
		return moTypeId;
	}

	public void setMoTypeId(String moTypeId) {
		this.moTypeId = moTypeId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public AttributeValue[] getAttributes() {
		return attributes;
	}

	public void setAttributes(AttributeValue[] attributes) {
		this.attributes = attributes;
	}
}
