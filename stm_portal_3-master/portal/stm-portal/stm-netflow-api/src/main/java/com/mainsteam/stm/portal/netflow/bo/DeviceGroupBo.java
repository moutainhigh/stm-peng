package com.mainsteam.stm.portal.netflow.bo;

public class DeviceGroupBo {

	private Integer id;
	private String name;
	private String description;//用力啊存储接收的查询条件DeviceGroupBo
	private String deviceIds;//用来存储排序的顺序order

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

	public String getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(String deviceIds) {
		this.deviceIds = deviceIds;
	}

}
