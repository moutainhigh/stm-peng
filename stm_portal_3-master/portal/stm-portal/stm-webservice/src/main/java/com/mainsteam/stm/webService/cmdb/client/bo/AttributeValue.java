package com.mainsteam.stm.webService.cmdb.client.bo;


public class AttributeValue {
	
	/**
	 * 管理对象特征属性数据的父实例标识ID
	 */
	private String instanceId;
	
	/**
	 * 管理对象特征属性唯一标识
	 */
	private String attributeId;
	
	/**
	 * 管理对象特征属性的值
	 */
	private String value;
	
	/**
	 * 管理对象特征属性的值列表或子属性的值列表
	 */
	private AttributeValue[] attributes;

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public AttributeValue[] getAttributes() {
		return attributes;
	}

	public void setAttributes(AttributeValue[] attributes) {
		this.attributes = attributes;
	}
}
