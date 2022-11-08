package com.mainsteam.stm.portal.netflow.po;


public class InterfaceGroupPo {

	private Integer id;
	private String name;
	private String description;//用来存储查询条件interfaceGroupName
	private String interfaceIds;//用来存储查询顺序order

	public String getInterfaceIds() {
		return interfaceIds;
	}

	public void setInterfaceIds(String interfaceIds) {
		this.interfaceIds = interfaceIds;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
