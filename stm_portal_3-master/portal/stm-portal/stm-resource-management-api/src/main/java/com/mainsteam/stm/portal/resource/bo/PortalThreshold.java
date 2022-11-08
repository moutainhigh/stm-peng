package com.mainsteam.stm.portal.resource.bo;

import com.mainsteam.stm.profilelib.obj.ProfileThreshold;

public class PortalThreshold extends ProfileThreshold{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6166445410598997975L;
	private String alarmContent;

	public String getAlarmContent() {
		return alarmContent;
	}

	public void setAlarmContent(String alarmContent) {
		this.alarmContent = alarmContent;
	}
	
}
