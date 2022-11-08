package com.mainsteam.stm.webService.alarm;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AlarmQueryPageBean")
public class AlarmQueryPageBean {

	@XmlElement(name = "page", required = true)
	private int page = 1;
	@XmlElement(name = "rows", required = true)
	private int rows = 10;
	
	
	@XmlElement(name = "states")
	private List<MetricStateEnum> states;
	
	@XmlElement(name = "prentCategory")
    private String prentCategory;
	@XmlElement(name = "childCategory")
    private String childCategory;
	@XmlElement(name = "start")
	private Date start;
	@XmlElement(name = "end")
	private Date end;
	@XmlElement(name = "recovered")
	private Boolean recovered = false;
	@XmlElement(name = "monitorList")
	private SysModuleEnum[] monitorList;

	public int getStartRow() {
		return (this.page - 1) * rows;
	}

	public int getRowCount() {
		if (rows > 100) {
			rows = 100;
		}
		return rows;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public List<MetricStateEnum> getStates() {
		return states;
	}

	public void setStates(List<MetricStateEnum> states) {
		this.states = states;
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

	public Boolean getRecovered() {
		return recovered;
	}

	public void setRecovered(Boolean recovered) {
		this.recovered = recovered;
	}

	public SysModuleEnum[] getMonitorList() {
		return monitorList;
	}

	public void setMonitorList(SysModuleEnum[] monitorList) {
		this.monitorList = monitorList;
	}

	public String getPrentCategory() {
		return prentCategory;
	}

	public void setPrentCategory(String prentCategory) {
		this.prentCategory = prentCategory;
	}

	public String getChildCategory() {
		return childCategory;
	}

	public void setChildCategory(String childCategory) {
		this.childCategory = childCategory;
	}
	

}
