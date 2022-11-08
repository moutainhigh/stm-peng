package com.mainsteam.stm.portal.netflow.bo;

public class IpGroupBo {

	private Integer id;
	private String name;
	private String ips;//用来存储查询的顺序order
	private String description;//用来存储查询条件ipGroupName

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

	public String getIps() {
		return ips;
	}

	public void setIps(String ips) {
		this.ips = ips;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
