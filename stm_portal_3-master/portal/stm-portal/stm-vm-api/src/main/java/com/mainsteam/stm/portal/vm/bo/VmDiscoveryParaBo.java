package com.mainsteam.stm.portal.vm.bo;

import java.io.Serializable;

public class VmDiscoveryParaBo implements Serializable{
	private static final long serialVersionUID = -3061446276486010214L;
	private String discoveryType;
	private Long domainId;
	private Integer DCS;
	private String IP;
	private String userName;
	private String password;
	private Long instanceId;
	private String dataJson;
	public String getDiscoveryType() {
		return discoveryType;
	}
	public void setDiscoveryType(String discoveryType) {
		this.discoveryType = discoveryType;
	}
	public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	public Integer getDCS() {
		return DCS;
	}
	public void setDCS(Integer dCS) {
		DCS = dCS;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}
	public String getDataJson() {
		return dataJson;
	}
	public void setDataJson(String dataJson) {
		this.dataJson = dataJson;
	}
	
	private String regeion; //aliyunVm所在域
	private String project; //kylinVm所属项目
	public String getRegeion() {
		return regeion;
	}
	public void setRegeion(String regeion) {
		this.regeion = regeion;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	
}
