package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.Date;

import com.mainsteam.stm.profilelib.objenum.ProfileChangeEnum;

public class ProfieChange implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5759804145399581546L;

	private long profileChangeId;
	
	private long profileId;
	
	private long timelineId;
	
	private ProfileChangeEnum  profileChangeEnum;
	
	private String source;
	
	private Date changeTime;
	
	private boolean operateState;

	public long getProfileChangeId() {
		return profileChangeId;
	}

	public long getProfileId() {
		return profileId;
	}

	public ProfileChangeEnum getProfileChangeEnum() {
		return profileChangeEnum;
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

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public void setProfileChangeEnum(ProfileChangeEnum profileChangeEnum) {
		this.profileChangeEnum = profileChangeEnum;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public boolean isOperateState() {
		return operateState;
	}

	public void setOperateState(boolean operateState) {
		this.operateState = operateState;
	}
	
	public long getTimelineId() {
		return timelineId;
	}

	public void setTimelineId(long timelineId) {
		this.timelineId = timelineId;
	}
}
