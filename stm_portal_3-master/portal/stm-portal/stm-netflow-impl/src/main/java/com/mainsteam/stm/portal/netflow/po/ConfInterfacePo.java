package com.mainsteam.stm.portal.netflow.po;

import java.util.Date;

public class ConfInterfacePo {
	private Integer id;
	private Integer confDeviceNetflowId;
	private Long interfaceIndex;
	private String name;
	private String description;
	private Long inSpeed;
	private Long outSpeed;
	private Integer interfaceType;
	private Integer sampleRate;
	private String interfaceIp;
	private Integer expDirection;
	private Integer flowLatest;
	private Integer logLatest;
	private Date createDate;
	private Boolean checked;
	private Integer state;

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getConfDeviceNetflowId() {
		return confDeviceNetflowId;
	}

	public void setConfDeviceNetflowId(Integer confDeviceNetflowId) {
		this.confDeviceNetflowId = confDeviceNetflowId;
	}

	public Long getInterfaceIndex() {
		return interfaceIndex;
	}

	public void setInterfaceIndex(Long interfaceIndex) {
		this.interfaceIndex = interfaceIndex;
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

	public Long getInSpeed() {
		return inSpeed;
	}

	public void setInSpeed(Long inSpeed) {
		this.inSpeed = inSpeed;
	}

	public Long getOutSpeed() {
		return outSpeed;
	}

	public void setOutSpeed(Long outSpeed) {
		this.outSpeed = outSpeed;
	}

	public Integer getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(Integer interfaceType) {
		this.interfaceType = interfaceType;
	}

	public Integer getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(Integer sampleRate) {
		this.sampleRate = sampleRate;
	}

	public String getInterfaceIp() {
		return interfaceIp;
	}

	public void setInterfaceIp(String interfaceIp) {
		this.interfaceIp = interfaceIp;
	}

	public Integer getExpDirection() {
		return expDirection;
	}

	public void setExpDirection(Integer expDirection) {
		this.expDirection = expDirection;
	}

	public Integer getFlowLatest() {
		return flowLatest;
	}

	public void setFlowLatest(Integer flowLatest) {
		this.flowLatest = flowLatest;
	}

	public Integer getLogLatest() {
		return logLatest;
	}

	public void setLogLatest(Integer logLatest) {
		this.logLatest = logLatest;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
