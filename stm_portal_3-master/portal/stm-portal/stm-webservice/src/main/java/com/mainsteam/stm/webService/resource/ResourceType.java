package com.mainsteam.stm.webService.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ResourceType")
public class ResourceType {
	
	@XmlElement(name = "resourceTypeId", required = true)
	private String resourceTypeId;
	
	@XmlElement(name = "resourceTypeName", required = true)
	private String resourceTypeName;
	
	@XmlElement(name = "definations", required = true)
	private AttributeDefination[] definations;

	public String getResourceTypeId() {
		return resourceTypeId;
	}

	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

	public String getResourceTypeName() {
		return resourceTypeName;
	}

	public void setResourceTypeName(String resourceTypeName) {
		this.resourceTypeName = resourceTypeName;
	}

	public AttributeDefination[] getDefinations() {
		return definations;
	}

	public void setDefinations(AttributeDefination[] definations) {
		this.definations = definations;
	}

}
