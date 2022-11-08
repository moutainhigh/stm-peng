package com.mainsteam.stm.portal.config.web.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mainsteam.stm.portal.config.bo.BackupPlanBo;
import com.mainsteam.stm.util.DateUtil;

public class BackupPlanVo {
	/**主键ID*/
	private Long id;
	/**计划名称*/
	private String name;
	/**计划开始时间*/
	private String beginDate;
	/**计划结束时间*/
	private String endDate;
	/**备份类型*/
	private Integer type;
	/**描述*/
	private String desc;
	/**创建人ID*/
	private Long entryId;
	/**创建时间*/
	private String entryTime;
	/**
	 * 计划对应的资源
	 */
	private List<Long> resourceIds = new ArrayList<Long>();
	private String month;
	private String week;
	private String day;
	private String hour;
	private String minute;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Long getEntryId() {
		return entryId;
	}
	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}
	public String getEntryTime() {
		return entryTime;
	}
	public void setEntryTime(String entryTime) {
		this.entryTime = entryTime;
	}

	
	public List<Long> getResourceIds() {
		return resourceIds;
	}
	public void setResourceIds(List<Long> resourceIds) {
		this.resourceIds = resourceIds;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	public String getMinute() {
		return minute;
	}
	public void setMinute(String minute) {
		this.minute = minute;
	}
	public BackupPlanBo toBo(){
		BackupPlanBo bo = new BackupPlanBo();
		bo.setId(id);
		bo.setBeginDate(DateUtil.parseDate(beginDate, DateUtil.DEFAULT_DATE_FORMAT));
		bo.setEndDate(DateUtil.parseDate(endDate, DateUtil.DEFAULT_DATE_FORMAT));
		bo.setDay(day);
		bo.setDesc(desc);
		bo.setEntryTime(new Date());
		bo.setHour(hour);
		bo.setMinute(minute);
		bo.setMonth(month);
		bo.setName(name);
		bo.setType(type);
		bo.setWeek(week);
		bo.setResourceIds(resourceIds);
		return bo;
	}
}
