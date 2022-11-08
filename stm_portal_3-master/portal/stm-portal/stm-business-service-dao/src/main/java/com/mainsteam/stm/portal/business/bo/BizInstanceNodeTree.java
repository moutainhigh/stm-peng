package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.List;

public class BizInstanceNodeTree implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 500407239859169434L;

	private String id;
	
	private String pid;
	
	//监控状态0.已监控1.未监控
	private int status;
	
	private String name;
	
	private List<BizInstanceNodeTree> children;

	public List<BizInstanceNodeTree> getChildren() {
		return children;
	}

	public void setChildren(List<BizInstanceNodeTree> children) {
		this.children = children;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
