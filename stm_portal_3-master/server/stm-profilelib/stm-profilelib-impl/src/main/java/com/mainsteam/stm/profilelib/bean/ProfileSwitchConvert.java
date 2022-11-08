package com.mainsteam.stm.profilelib.bean;

import java.util.HashMap;
import java.util.List;


public class ProfileSwitchConvert {

	private long lastProfileId;
	
	private long currentProfileId;
	
	private long parentInstanceId;

	
	/**
	 * 子资源切换模型对应的子资源（监控）
	 */
	private HashMap<String,List<Long>> childMonitor;
	
	
	public long getLastProfileId() {
		return lastProfileId;
	}

	public long getCurrentProfileId() {
		return currentProfileId;
	}

	public long getParentInstanceId() {
		return parentInstanceId;
	}

	public void setLastProfileId(long lastProfileId) {
		this.lastProfileId = lastProfileId;
	}

	public void setCurrentProfileId(long currentProfileId) {
		this.currentProfileId = currentProfileId;
	}

	public void setParentInstanceId(long parentInstanceId) {
		this.parentInstanceId = parentInstanceId;
	}
	
	public HashMap<String, List<Long>> getChildMonitor() {
		return childMonitor;
	}

	public void setChildMonitor(HashMap<String, List<Long>> childMonitor) {
		this.childMonitor = childMonitor;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(100);
		b.append("switch notice:[lastProfileId:");
		b.append(lastProfileId);
		b.append(" currentProfileId:");
		b.append(currentProfileId);
		b.append(" parentInstanceId:");
		b.append(parentInstanceId);
		b.append(" childMonitor:");
		b.append(childMonitor);
		b.append("]");
		return b.toString();
	}
	
}
