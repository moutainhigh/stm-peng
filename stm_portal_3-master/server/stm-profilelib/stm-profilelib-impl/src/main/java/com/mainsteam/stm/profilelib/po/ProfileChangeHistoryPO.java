package com.mainsteam.stm.profilelib.po;

import java.util.Date;


public class ProfileChangeHistoryPO {

	private long profileChangeHistoryId;
	
	private long profileChangeId;
	
	private int dcsGroupId;

	private int resultState;
	
	private Date operateTime;
	
	public long getProfileChangeHistoryId() {
		return profileChangeHistoryId;
	}

	public long getProfileChangeId() {
		return profileChangeId;
	}

	public int getDcsGroupId() {
		return dcsGroupId;
	}
	
	public int getResultState() {
		return resultState;
	}

	public void setResultState(int resultState) {
		this.resultState = resultState;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setProfileChangeHistoryId(long profileChangeHistoryId) {
		this.profileChangeHistoryId = profileChangeHistoryId;
	}

	public void setProfileChangeId(long profileChangeId) {
		this.profileChangeId = profileChangeId;
	}

	public void setDcsGroupId(int dcsGroupId) {
		this.dcsGroupId = dcsGroupId;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
}
