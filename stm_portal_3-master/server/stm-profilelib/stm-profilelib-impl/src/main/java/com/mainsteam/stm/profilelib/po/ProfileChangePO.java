package com.mainsteam.stm.profilelib.po;

import java.util.Date;


public class ProfileChangePO {

	private long profileChangeId;
	
	private String operateMode;

	private String source;
	
	private Date changeTime;
	
	private int operateState;
	
	private long profileId;
	
	private long timelineId;
	
	public long getTimelineId() {
		return timelineId;
	}

	public void setTimelineId(long timelineId) {
		this.timelineId = timelineId;
	}

	public long getProfileChangeId() {
		return profileChangeId;
	}

	public String getSource() {
		return source;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public void setProfileChangeId(long profileChangeId) {
		this.profileChangeId = profileChangeId;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public int getOperateState() {
		return operateState;
	}


	public void setOperateState(int operateState) {
		this.operateState = operateState;
	}


	public String getOperateMode() {
		return operateMode;
	}

	public void setOperateMode(String operateMode) {
		this.operateMode = operateMode;
	}


	public long getProfileId() {
		return profileId;
	}


	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	
}
