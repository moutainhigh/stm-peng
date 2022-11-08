package com.mainsteam.stm.alarm.obj;

import java.util.Date;
import java.util.List;

/**
 * @author cx
 */
@Deprecated
public class AlarmNotifyTemplet {
	private long tmpID;
	private String content;
	private String title;
	private AlarmProviderEnum provider;
	private String language;
	private String sysID;
	private Date updateTime;
	private List<AlarmNotifyTempletParamter> params;
	
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public List<AlarmNotifyTempletParamter> getParams() {
		return params;
	}
	public void setParams(List<AlarmNotifyTempletParamter> params) {
		this.params = params;
		for(AlarmNotifyTempletParamter p:this.params){
			p.setProvider(this.provider);
			p.setSysID(this.sysID);
			p.setTmpID(this.tmpID);
		}
	}
	public long getTmpID() {
		return tmpID;
	}
	public void setTmpID(long templetID) {
		this.tmpID = templetID;
	}
	public AlarmProviderEnum getProvider() {
		return provider;
	}
	public void setProvider(AlarmProviderEnum provider) {
		this.provider = provider;
	}
	public String getSysID() {
		return sysID;
	}
	public void setSysID(String sysID) {
		this.sysID = sysID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
