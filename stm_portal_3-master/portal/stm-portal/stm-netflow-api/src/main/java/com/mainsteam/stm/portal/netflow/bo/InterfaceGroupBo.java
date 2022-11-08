package com.mainsteam.stm.portal.netflow.bo;

import java.util.List;

public class InterfaceGroupBo {

	private Integer id;
	private String name;
	private String description;
	private List<ConfInterfaceBo> interfaceBos;
	private String interfaceIds;

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

	public List<ConfInterfaceBo> getInterfaceBos() {
		return interfaceBos;
	}

	public void setInterfaceBos(List<ConfInterfaceBo> interfaceBos) {
		this.interfaceBos = interfaceBos;
	}

}
