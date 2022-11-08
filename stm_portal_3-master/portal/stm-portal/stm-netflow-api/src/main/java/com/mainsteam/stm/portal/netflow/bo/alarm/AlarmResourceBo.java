package com.mainsteam.stm.portal.netflow.bo.alarm;

public class AlarmResourceBo {
	private String id;
	private String name;
	private String prefix;
	private String subfix;
	private String resourceIds;

	public String getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSubfix() {
		return subfix;
	}

	public void setSubfix(String subfix) {
		this.subfix = subfix;
	}

}
