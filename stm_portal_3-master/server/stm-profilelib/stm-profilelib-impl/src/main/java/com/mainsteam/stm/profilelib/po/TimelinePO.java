package com.mainsteam.stm.profilelib.po;

import java.util.Date;

public class TimelinePO {

	private long profileId;
	
	private long lineId;
	
	private String lineName;
	
	private String lineType;
	
	private Date startTime;
	
	private Date endTime;

	public long getProfileId() {
		return profileId;
	}

	public long getLineId() {
		return lineId;
	}

	public String getLineName() {
		return lineName;
	}

	public String getLineType() {
		return lineType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public void setLineId(long lineId) {
		this.lineId = lineId;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public void setLineType(String lineType) {
		this.lineType = lineType;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	
}
