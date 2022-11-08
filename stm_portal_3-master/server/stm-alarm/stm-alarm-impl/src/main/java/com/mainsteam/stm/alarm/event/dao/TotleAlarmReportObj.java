package com.mainsteam.stm.alarm.event.dao;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;

public class TotleAlarmReportObj {
	
	private int cnt;
	private InstanceStateEnum level;
	private String sourceID;
	private int notCover;
	private int cover;
	
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public int getNotCover() {
		return notCover;
	}
	public void setNotCover(int notCover) {
		this.notCover = notCover;
	}
	public int getCover() {
		return cover;
	}
	public void setCover(int cover) {
		this.cover = cover;
	}
	public InstanceStateEnum getLevel() {
		return level;
	}
	public void setLevel(InstanceStateEnum level) {
		this.level = level;
	}
	public String getSourceID() {
		return sourceID;
	}
	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}
}
