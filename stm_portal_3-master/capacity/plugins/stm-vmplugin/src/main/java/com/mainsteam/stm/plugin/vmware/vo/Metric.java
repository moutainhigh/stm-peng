package com.mainsteam.stm.plugin.vmware.vo;

public class Metric {
	
	private String[] titles;
	
	private long executeRealTime;

	public String[] getTitles() {
		return titles;
	}

	public void setTitles(String[] titles) {
		this.titles = titles;
	}
	
	public long getExecuteRealTime() {
		return executeRealTime;
	}

	public void setExecuteRealTime(long executeRealTime) {
		this.executeRealTime = executeRealTime;
	}

	public void addTitles(String[] titles) {
		
	}
	
	public void addRow(String index, String[] values){
		
	}
	
}
