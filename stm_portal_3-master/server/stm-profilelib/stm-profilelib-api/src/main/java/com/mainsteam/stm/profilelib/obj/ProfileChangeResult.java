package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.Date;

public class ProfileChangeResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5957000010491350450L;

	private long ProfileChangeResultId;
	
	private long profileChangeId;
	
	private int dcsGroupId;
	
	private boolean resultState;
	
	private Date changeTime;
	
	public long getProfileChangeResultId() {
		return ProfileChangeResultId;
	}

	public long getProfileChangeId() {
		return profileChangeId;
	}

	public int getDcsGroupId() {
		return dcsGroupId;
	}

	public boolean isResultState() {
		return resultState;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public void setProfileChangeResultId(long profileChangeResultId) {
		ProfileChangeResultId = profileChangeResultId;
	}

	public void setProfileChangeId(long profileChangeId) {
		this.profileChangeId = profileChangeId;
	}

	public void setDcsGroupId(int dcsGroupId) {
		this.dcsGroupId = dcsGroupId;
	}

	public void setResultState(boolean resultState) {
		this.resultState = resultState;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	
}
