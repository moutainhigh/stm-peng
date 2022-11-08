/**
 * 
 */
package com.mainsteam.stm.portal.inspect.bo;

import java.util.List;

/**
 * <li>文件名称: BasicInfoBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月24日
 * @author   lil
 */
public class BasicInfoBo {
	
	// 表主键ID
	private Long id;
	// 巡检任务名称
	private String insepctPalnTaskName;
	// 巡检方式
	private String inspectPlanType;
	// 描述
	private String inspectPlanDescrible;
	// 域名称
	private String inspectPlanDomain;
	// 巡检人ID
	private String inspectPlanInspector;
	// 如果为自动，存一个时间JSON字符串
	private String inspectPlanTypeTime;
	// 在报表中是否允许报表内容被手动修改
	private String inspectRlanReportEditable;
	
	private String inspectTypeMonday;
	private String inspectTypeTuesday;
	private String inspectTypeWednesday;
	private String inspectTypeThursday;
	private String inspectTypeFriday;
	private String inspectTypeSaturday;
	private String inspectTypeSunday;
	
//	private String inspectTypeDate;
//	private String inspectTypeFirstMonth;
//	private String inspectTypeSecondMonth;
//	private String inspectTypeThirdMonth;
//	
//	private String inspectTypeMonth;
	
	List<BasicTypeMultiplyBo> lines;
	
	private String inspectTypeHour;
	private String inspectTypeMinute;
	private String inspectPlanDomainShowname;
	
	private List<Option> users;
	
	/**
	 * @return the users
	 */
	public List<Option> getUsers() {
		return users;
	}
	/**
	 * @param users the users to set
	 */
	public void setUsers(List<Option> users) {
		this.users = users;
	}
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the insepctPalnTaskName
	 */
	public String getInsepctPalnTaskName() {
		return insepctPalnTaskName;
	}
	/**
	 * @param insepctPalnTaskName the insepctPalnTaskName to set
	 */
	public void setInsepctPalnTaskName(String insepctPalnTaskName) {
		this.insepctPalnTaskName = insepctPalnTaskName;
	}
	/**
	 * @return the inspectPlanType
	 */
	public String getInspectPlanType() {
		return inspectPlanType;
	}
	/**
	 * @param inspectPlanType the inspectPlanType to set
	 */
	public void setInspectPlanType(String inspectPlanType) {
		this.inspectPlanType = inspectPlanType;
	}
	/**
	 * @return the inspectPlanDescrible
	 */
	public String getInspectPlanDescrible() {
		return inspectPlanDescrible;
	}
	/**
	 * @param inspectPlanDescrible the inspectPlanDescrible to set
	 */
	public void setInspectPlanDescrible(String inspectPlanDescrible) {
		this.inspectPlanDescrible = inspectPlanDescrible;
	}
	/**
	 * @return the inspectPlanDomain
	 */
	public String getInspectPlanDomain() {
		return inspectPlanDomain;
	}
	/**
	 * @param inspectPlanDomain the inspectPlanDomain to set
	 */
	public void setInspectPlanDomain(String inspectPlanDomain) {
		this.inspectPlanDomain = inspectPlanDomain;
	}
	/**
	 * @return the inspectPlanInspector
	 */
	public String getInspectPlanInspector() {
		return inspectPlanInspector;
	}
	/**
	 * @param inspectPlanInspector the inspectPlanInspector to set
	 */
	public void setInspectPlanInspector(String inspectPlanInspector) {
		this.inspectPlanInspector = inspectPlanInspector;
	}
	/**
	 * @return the inspectPlanTypeTime
	 */
	public String getInspectPlanTypeTime() {
		return inspectPlanTypeTime;
	}
	/**
	 * @param inspectPlanTypeTime the inspectPlanTypeTime to set
	 */
	public void setInspectPlanTypeTime(String inspectPlanTypeTime) {
		this.inspectPlanTypeTime = inspectPlanTypeTime;
	}
	/**
	 * @return the inspectRlanReportEditable
	 */
	public String getInspectRlanReportEditable() {
		return inspectRlanReportEditable;
	}
	/**
	 * @param inspectRlanReportEditable the inspectRlanReportEditable to set
	 */
	public void setInspectRlanReportEditable(String inspectRlanReportEditable) {
		this.inspectRlanReportEditable = inspectRlanReportEditable;
	}
	/**
	 * @return the inspectTypeMonday
	 */
	public String getInspectTypeMonday() {
		return inspectTypeMonday;
	}
	/**
	 * @param inspectTypeMonday the inspectTypeMonday to set
	 */
	public void setInspectTypeMonday(String inspectTypeMonday) {
		this.inspectTypeMonday = inspectTypeMonday;
	}
	/**
	 * @return the inspectTypeTuesday
	 */
	public String getInspectTypeTuesday() {
		return inspectTypeTuesday;
	}
	/**
	 * @param inspectTypeTuesday the inspectTypeTuesday to set
	 */
	public void setInspectTypeTuesday(String inspectTypeTuesday) {
		this.inspectTypeTuesday = inspectTypeTuesday;
	}
	/**
	 * @return the inspectTypeWednesday
	 */
	public String getInspectTypeWednesday() {
		return inspectTypeWednesday;
	}
	/**
	 * @param inspectTypeWednesday the inspectTypeWednesday to set
	 */
	public void setInspectTypeWednesday(String inspectTypeWednesday) {
		this.inspectTypeWednesday = inspectTypeWednesday;
	}
	/**
	 * @return the inspectTypeThursday
	 */
	public String getInspectTypeThursday() {
		return inspectTypeThursday;
	}
	/**
	 * @param inspectTypeThursday the inspectTypeThursday to set
	 */
	public void setInspectTypeThursday(String inspectTypeThursday) {
		this.inspectTypeThursday = inspectTypeThursday;
	}
	/**
	 * @return the inspectTypeFriday
	 */
	public String getInspectTypeFriday() {
		return inspectTypeFriday;
	}
	/**
	 * @param inspectTypeFriday the inspectTypeFriday to set
	 */
	public void setInspectTypeFriday(String inspectTypeFriday) {
		this.inspectTypeFriday = inspectTypeFriday;
	}
	/**
	 * @return the inspectTypeSaturday
	 */
	public String getInspectTypeSaturday() {
		return inspectTypeSaturday;
	}
	/**
	 * @param inspectTypeSaturday the inspectTypeSaturday to set
	 */
	public void setInspectTypeSaturday(String inspectTypeSaturday) {
		this.inspectTypeSaturday = inspectTypeSaturday;
	}
	/**
	 * @return the inspectTypeSunday
	 */
	public String getInspectTypeSunday() {
		return inspectTypeSunday;
	}
	/**
	 * @param inspectTypeSunday the inspectTypeSunday to set
	 */
	public void setInspectTypeSunday(String inspectTypeSunday) {
		this.inspectTypeSunday = inspectTypeSunday;
	}
	/**
	 * @return the inspectTypeHour
	 */
	public String getInspectTypeHour() {
		return inspectTypeHour;
	}
	/**
	 * @param inspectTypeHour the inspectTypeHour to set
	 */
	public void setInspectTypeHour(String inspectTypeHour) {
		this.inspectTypeHour = inspectTypeHour;
	}
	/**
	 * @return the inspectTypeMinute
	 */
	public String getInspectTypeMinute() {
		return inspectTypeMinute;
	}
	/**
	 * @param inspectTypeMinute the inspectTypeMinute to set
	 */
	public void setInspectTypeMinute(String inspectTypeMinute) {
		this.inspectTypeMinute = inspectTypeMinute;
	}
	/**
	 * @return the lines
	 */
	public List<BasicTypeMultiplyBo> getLines() {
		return lines;
	}
	/**
	 * @param lines the lines to set
	 */
	public void setLines(List<BasicTypeMultiplyBo> lines) {
		this.lines = lines;
	}
	public String getInspectPlanDomainShowname() {
		return inspectPlanDomainShowname;
	}
	public void setInspectPlanDomainShowname(String inspectPlanDomainShowname) {
		this.inspectPlanDomainShowname = inspectPlanDomainShowname;
	}

}
