package com.mainsteam.stm.resourcelog.data.bigData;


/**
 * Created by John on 2014/12/8.
 */
////////////////////////////////////////////////////////////////////////////
public class LogDataLine{
    private String hostIp = "";
    private long dateTime = 0l;
    private String component = "";
    private String level = "";
    private String content = "";


    public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
