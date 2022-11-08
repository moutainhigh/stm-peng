package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

public class VolumeParameterVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3017917523111091949L;
	
	
	private String volumeList;
	
	private long mainInstanceId;
	
	private int type;

	public String getVolumeList() {
		return volumeList;
	}

	public void setVolumeList(String volumeList) {
		this.volumeList = volumeList;
	}

	public long getMainInstanceId() {
		return mainInstanceId;
	}

	public void setMainInstanceId(long mainInstanceId) {
		this.mainInstanceId = mainInstanceId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	
}
