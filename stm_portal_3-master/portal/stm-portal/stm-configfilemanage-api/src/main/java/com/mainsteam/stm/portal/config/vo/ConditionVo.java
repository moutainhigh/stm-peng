package com.mainsteam.stm.portal.config.vo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConditionVo {
	private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String ipOrName;
	private String timeRadio;
	private Date startTime;
	private Date endTime;
	private String queryTime;
	public String getIpOrName() {
		return ipOrName;
	}
	public void setIpOrName(String ipOrName) {
		this.ipOrName = ipOrName;
	}
	public String getTimeRadio() {
		return timeRadio;
	}
	public void setTimeRadio(String timeRadio) {
		this.timeRadio = timeRadio;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		if (startTime != null)
			try {
				this.startTime = FORMAT.parse(startTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		if(null!=endTime)
			try {
				this.endTime = FORMAT.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	}
	public String getQueryTime() {
		return queryTime;
	}
	public void setQueryTime(String queryTime) {
		this.queryTime = queryTime;
	}
}
