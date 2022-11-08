package com.mainsteam.stm.alarm.obj;

/**
 * @author cx
 */
@Deprecated
public class AlarmNotifyTempletParamter {
	private String name;
	private String desc;
	private long tmpID;
	private AlarmProviderEnum provider;
	private String sysID;
	
	public String getSysID() {
		return sysID;
	}
	public void setSysID(String sysID) {
		this.sysID = sysID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public AlarmProviderEnum getProvider() {
		return provider;
	}
	public void setProvider(AlarmProviderEnum provider) {
		this.provider = provider;
	}
	public long getTmpID() {
		return tmpID;
	}
	public void setTmpID(long tmpID) {
		this.tmpID = tmpID;
	}
}
