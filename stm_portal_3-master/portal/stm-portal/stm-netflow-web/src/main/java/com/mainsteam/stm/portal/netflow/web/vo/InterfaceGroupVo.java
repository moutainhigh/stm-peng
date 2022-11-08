package com.mainsteam.stm.portal.netflow.web.vo;

import com.mainsteam.stm.portal.netflow.bo.ConfInterfaceBo;
import com.mainsteam.stm.portal.netflow.bo.InterfaceGroupBo;

public class InterfaceGroupVo {

	private Integer id;
	private String name;
	private String description;
	private String interfaceNames;

	public InterfaceGroupVo() {
	}

	public InterfaceGroupVo(InterfaceGroupBo bo) {
		this.setId(bo.getId());
		this.setName(bo.getName());
		this.setDescription(bo.getDescription());
		if (bo.getInterfaceBos() != null) {
			StringBuilder names = new StringBuilder();
			for (ConfInterfaceBo confInterfaceBo : bo.getInterfaceBos()) {
				names.append(confInterfaceBo.getName()).append(",");
			}
			if (names.length() > 0) {
				this.setInterfaceNames(names.substring(0, names.length() - 1));
			}
		}
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

	public String getInterfaceNames() {
		return interfaceNames;
	}

	public void setInterfaceNames(String interfaceNames) {
		this.interfaceNames = interfaceNames;
	}

}
