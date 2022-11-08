package com.mainsteam.stm.portal.report.bo;

import java.io.Serializable;

public class ResourceCategoryTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2049530617464897780L;
	private String id;
	private String name;
	private String state;
	private int type;
	private String pid;

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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
