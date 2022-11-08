package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.List;

public class AddInstanceResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6265338074559209215L;
	
	private List<String> repeatInstanceList;
	
	private List<String> failInstanceList;

	public List<String> getRepeatInstanceList() {
		return repeatInstanceList;
	}

	public void setRepeatInstanceList(List<String> repeatInstanceList) {
		this.repeatInstanceList = repeatInstanceList;
	}

	public List<String> getFailInstanceList() {
		return failInstanceList;
	}

	public void setFailInstanceList(List<String> failInstanceList) {
		this.failInstanceList = failInstanceList;
	}
	
	

}
