package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.List;

public class DiscCategoryVo implements Serializable {

	private static final long serialVersionUID = -3749310201347180950L;

	private String id;
	private String name;
	private String pid;
	private String type;
	private List<DiscCategoryVo> childcategorys;

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

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public List<DiscCategoryVo> getChildcategorys() {
		return childcategorys;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setChildcategorys(List<DiscCategoryVo> childcategorys) {
		this.childcategorys = childcategorys;
	}

}
