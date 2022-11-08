package com.mainsteam.stm.webService.cmdb.server;

import java.util.List;


public class AttributeDefination {
	/**
	 * 管理对象特征属性唯一标识
	 */
	private String attributeId;  
	/**
	 * 管理对象特征属性名称
	 */
	private String attributeName;
	/**
	 * 指标值类型
	 */
	private String valueType;
	/**
	 * 描述信息
	 */
	private String description;
	/**
	 * 管理对象特征属性定义信息列表或子属性
	 */
	private List<AttributeDefination> definations;

	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<AttributeDefination> getDefinations() {
		return definations;
	}

	public void setDefinations(List<AttributeDefination> definations) {
		this.definations = definations;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof AttributeDefination) {
			AttributeDefination attri = (AttributeDefination)obj;
			//比较ID一样 则相等
			if (attri.attributeId.equals(this.attributeId)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.attributeId.hashCode();
	}
}
