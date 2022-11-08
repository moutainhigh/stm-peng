package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.mainsteam.stm.profilelib.objenum.ProfileChangeEnum;

public class ProfileChangeInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -395399722315466547L;
	private long profileChangeId;
	/**
	 * 策略改变具体类型
	 */
	private ProfileChangeEnum operateMode;
	/**
	 * 改变的值标识（根据ProfileChangeEnum判断,值一般有策略ID，基线ID，指标的ID）
	 */
	private String source;
	/**
	 * 最近一次修改时间
	 */
	private Date changeTime;
	/**
	 * 修改是否成功
	 */
	private boolean operateState;
	
	/**
	 * 具体的改变历史
	 */
	List<ProfileChangeHistoryInfo> profileChangeHistoryInfos;
	
	public ProfileChangeEnum getOperateMode() {
		return operateMode;
	}

	public String getSource() {
		return source;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public boolean isOperateState() {
		return operateState;
	}

	public List<ProfileChangeHistoryInfo> getProfileChangeHistoryInfos() {
		return profileChangeHistoryInfos;
	}

	public void setOperateMode(ProfileChangeEnum operateMode) {
		this.operateMode = operateMode;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public void setOperateState(boolean operateState) {
		this.operateState = operateState;
	}

	public void setProfileChangeHistoryInfos(
			List<ProfileChangeHistoryInfo> profileChangeHistoryInfos) {
		this.profileChangeHistoryInfos = profileChangeHistoryInfos;
	}

	public long getProfileChangeId() {
		return profileChangeId;
	}

	public void setProfileChangeId(long profileChangeId) {
		this.profileChangeId = profileChangeId;
	}

	
}
