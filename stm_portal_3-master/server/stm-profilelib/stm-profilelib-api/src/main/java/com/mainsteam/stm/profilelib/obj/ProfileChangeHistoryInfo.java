package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.Date;

public class ProfileChangeHistoryInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -238028305760413232L;
	/**
	 * 当前执行采集器的DCS
	 */
	private String DCS_GroupId;
	/**
	 * 执行时间
	 */
	private Date operateTime;
	/**
	 * 执行结果
	 */
	private boolean resultState;
	
	public String getDCS_GroupId() {
		return DCS_GroupId;
	}
	public Date getOperateTime() {
		return operateTime;
	}
	public boolean isResultState() {
		return resultState;
	}
	public void setDCS_GroupId(String dCS_GroupId) {
		DCS_GroupId = dCS_GroupId;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
	public void setResultState(boolean resultState) {
		this.resultState = resultState;
	}
	
}
