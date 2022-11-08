package com.mainsteam.stm.webService.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AttributeDefination")
public class AttributeDefination {
	
	@XmlElement(name = "attributeId", required = true)
	private String attributeId;
	
	@XmlElement(name = "attributeName", required = true)
	private String attributeName;
	
	@XmlElement(name = "valueType", required = true)
	private String valueType;
	
	@XmlElement(name = "description", required = true)
	private String description;
	
	@XmlElement(name = "definations", required = true)
	private AttributeDefination[] definations;

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

	public AttributeDefination[] getDefinations() {
		return definations;
	}

	public void setDefinations(AttributeDefination[] definations) {
		this.definations = definations;
	}
	
	
	
}
