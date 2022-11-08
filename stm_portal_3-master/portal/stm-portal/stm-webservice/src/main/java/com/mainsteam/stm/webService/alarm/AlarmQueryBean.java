package com.mainsteam.stm.webService.alarm;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;

public class AlarmQueryBean {

	private Date start;
	private Date end;
	private SysModuleEnum[] sme;
	private List<MetricStateEnum> mseList;
	private String resourceId;
	private String resourceType;
	private List<String> insList;
	private boolean recovered = false;
	
	public AlarmQueryBean(SysModuleEnum[] sme){
		this.sme = sme;
	}
	
	public String getResourceType() {
		return resourceType;
	}


	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}


	public boolean isRecovered() {
		return recovered;
	}
	public void setRecovered(boolean recovered) {
		this.recovered = recovered;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public SysModuleEnum[] getSme() {
		return sme;
	}
	public void setSme(SysModuleEnum[] sme) {
		this.sme = sme;
	}
	public List<MetricStateEnum> getMseList() {
		return mseList;
	}
	public void setMseList(List<MetricStateEnum> mseList) {
		this.mseList = mseList;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public List<String> getInsList() {
		return insList;
	}
	public void setInsList(List<String> insList) {
		this.insList = insList;
	}
}
