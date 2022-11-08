package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mainsteam.stm.util.DateUtil;
/**
 * 
 * <li>文件名称: BackupPlanBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   liupeng
 */
public class BackupPlanBo implements Serializable{
	private static final long serialVersionUID = 4393402002522091062L;
	/**主键ID*/
	private Long id;
	/**计划名称*/
	private String name;
	/**计划开始时间*/
	private Date beginDate;
	/**计划结束时间*/
	private Date endDate;
	/**备份类型*/
	private Integer type;
	/**描述*/
	private String desc;
	/**创建人ID*/
	private Long entryId;
	/**创建人名称*/
	private String entryName;
	/**创建时间*/
	private Date entryTime;
	/**
	 * 计划对应的资源
	 */
	private List<Long> resourceIds = new ArrayList<Long>();
	private String month;
	private String week;
	private String day;
	private String hour;
	private String minute;
	
	public Long getEntryId() {
		return entryId;
	}
	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}
	
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
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
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
	public Date getEntryTime() {
		return entryTime;
	}
	public void setEntryTime(Date entryTime) {
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
	
	//modify by sunhailiang on 20170612
	public String getBeginDateStr() {
		return beginDate != null ?  DateUtil.format(beginDate,DateUtil.DEFAULT_DATE_FORMAT) : "";
	}
	public String getEndDateStr() {
		return endDate != null ? DateUtil.format(endDate,DateUtil.DEFAULT_DATE_FORMAT) : "";
	}
	public String getEntryTimeStr() {
		return entryTime != null ? DateUtil.format(entryTime,DateUtil.DEFAULT_DATETIME_FORMAT) : "";
	}
	public String getEntryName() {
		return entryName;
	}
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}
}	
