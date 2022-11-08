package com.mainsteam.stm.portal.inspect.web.utils.entity;

import java.util.List;

public class Item {

	private String name;
	private String description;
	private String referenceValue;
	private String value;
	private String checkingType;
	private String summary;
	private String status;
	private List<Item> items;

	public String getName() {
		return name == null ? "" : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description == null ? "" : description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReferenceValue() {
		return referenceValue == null ? "" : referenceValue;
	}

	public void setReferenceValue(String referenceValue) {
		this.referenceValue = referenceValue;
	}

	public String getValue() {
		return value == null ? "" : value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSummary() {
		return summary == null ? "" : summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getStatus() {
		return status == null ? "" : status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public String getCheckingType() {
		return checkingType == null ? "" : checkingType;
	}

	public void setCheckingType(String checkingType) {
		this.checkingType = checkingType;
	}
}
