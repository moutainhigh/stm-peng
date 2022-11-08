package com.mainsteam.stm.portal.inspect.web.vo;
 
public class InspectPlanBasicVo {
	// 表主键ID
	private Long id;
	// 巡检任务名称
	private String insepctPalnTaskName;
	// 巡检方式
	private int inspectPlanType;
	// 描述
	private String inspectPlanDescrible;
	// 域名称
	private String inspectPlanDomain;
	// 巡检人ID
	private Long inspectPlanInspector;
	// 在报表中是否显示报告生成时间
	private int inspectPlanReportProduceTimeShow;
	// 在报表中是否显示报告最后编辑时间
	private int inspectPlanReportModifyTimeShow;
	// 在报表中是否显示最后编辑人
	private int inspectPlanReportModifiorShow;
	// 在报表中是否允许报表内容被手动修改(on选中)
	private String inspectRlanReportEditable;
	// 资源类型名称的id
	private String inspectReportResourceName;
	// 业务名称的id
	private String inspectReportBusinessName;
	// 状态（是否启用）
	private int inspectPlanStatus;
	// 创建人ID
	private int inspectPlanCreator;
	// 最后编辑时间
	private String inspectPlanLastEditTime;
	// 如果为自动，存一个时间JSON字符串
	private String inspectPlanTypeTime;
	// 最后执行时间
	private String inspectPlanLastExecTime;

	private String inspectTypeMonday;
	private String inspectTypeTuesday;
	private String inspectTypeWednesday;
	private String inspectTypeThursday;
	private String inspectTypeFriday;
	private String inspectTypeSaturday;
	private String inspectTypeSunday;

	private String inspectTypeDate;
	private String inspectTypeFirstMonth;
	private String inspectTypeSecondMonth;
	private String inspectTypeThirdMonth;

	private String inspectTypeMonth;

	private String inspectTypeHour;
	private String inspectTypeMinute;

	private Integer[] inspectTypeMonthDate;
	private Integer[] inspectTypeMonthHour;
	private Integer[] inspectTypeMonthMinute;

	private Integer[] inspectTypeCustomYear;
	private Integer[] inspectTypeCustomMonth;
	private Integer[] inspectTypeCustomDate;
	private Integer[] inspectTypeCustomHour;
	private Integer[] inspectTypeCustomMinute;

	private String inspectCustomDate;

	public String getInspectCustomDate() {
		return inspectCustomDate;
	}

	public void setInspectCustomDate(String inspectCustomDate) {
		this.inspectCustomDate = inspectCustomDate;
	}

	public Integer[] getInspectTypeCustomYear() {
		return inspectTypeCustomYear;
	}

	public void setInspectTypeCustomYear(Integer[] inspectTypeCustomYear) {
		this.inspectTypeCustomYear = inspectTypeCustomYear;
	}

	public Integer[] getInspectTypeCustomMonth() {
		return inspectTypeCustomMonth;
	}

	public void setInspectTypeCustomMonth(Integer[] inspectTypeCustomMonth) {
		this.inspectTypeCustomMonth = inspectTypeCustomMonth;
	}

	public Integer[] getInspectTypeCustomDate() {
		return inspectTypeCustomDate;
	}

	public void setInspectTypeCustomDate(Integer[] inspectTypeCustomDate) {
		this.inspectTypeCustomDate = inspectTypeCustomDate;
	}

	public Integer[] getInspectTypeCustomHour() {
		return inspectTypeCustomHour;
	}

	public void setInspectTypeCustomHour(Integer[] inspectTypeCustomHour) {
		this.inspectTypeCustomHour = inspectTypeCustomHour;
	}

	public Integer[] getInspectTypeCustomMinute() {
		return inspectTypeCustomMinute;
	}

	public void setInspectTypeCustomMinute(Integer[] inspectTypeCustomMinute) {
		this.inspectTypeCustomMinute = inspectTypeCustomMinute;
	}

	public Integer[] getInspectTypeMonthDate() {
		return inspectTypeMonthDate;
	}

	public void setInspectTypeMonthDate(Integer[] inspectTypeMonthDate) {
		this.inspectTypeMonthDate = inspectTypeMonthDate;
	}

	public Integer[] getInspectTypeMonthHour() {
		return inspectTypeMonthHour;
	}

	public void setInspectTypeMonthHour(Integer[] inspectTypeMonthHour) {
		this.inspectTypeMonthHour = inspectTypeMonthHour;
	}

	public Integer[] getInspectTypeMonthMinute() {
		return inspectTypeMonthMinute;
	}

	public void setInspectTypeMonthMinute(Integer[] inspectTypeMonthMinute) {
		this.inspectTypeMonthMinute = inspectTypeMonthMinute;
	}

	public String getInspectTypeMonday() {
		return inspectTypeMonday;
	}

	public void setInspectTypeMonday(String inspectTypeMonday) {
		this.inspectTypeMonday = inspectTypeMonday;
	}

	public String getInspectTypeTuesday() {
		return inspectTypeTuesday;
	}

	public void setInspectTypeTuesday(String inspectTypeTuesday) {
		this.inspectTypeTuesday = inspectTypeTuesday;
	}

	public String getInspectTypeWednesday() {
		return inspectTypeWednesday;
	}

	public void setInspectTypeWednesday(String inspectTypeWednesday) {
		this.inspectTypeWednesday = inspectTypeWednesday;
	}

	public String getInspectTypeThursday() {
		return inspectTypeThursday;
	}

	public void setInspectTypeThursday(String inspectTypeThursday) {
		this.inspectTypeThursday = inspectTypeThursday;
	}

	public String getInspectTypeFriday() {
		return inspectTypeFriday;
	}

	public void setInspectTypeFriday(String inspectTypeFriday) {
		this.inspectTypeFriday = inspectTypeFriday;
	}

	public String getInspectTypeSaturday() {
		return inspectTypeSaturday;
	}

	public void setInspectTypeSaturday(String inspectTypeSaturday) {
		this.inspectTypeSaturday = inspectTypeSaturday;
	}

	public String getInspectTypeSunday() {
		return inspectTypeSunday;
	}

	public void setInspectTypeSunday(String inspectTypeSunday) {
		this.inspectTypeSunday = inspectTypeSunday;
	}

	public String getInspectTypeDate() {
		return inspectTypeDate;
	}

	public void setInspectTypeDate(String inspectTypeDate) {
		this.inspectTypeDate = inspectTypeDate;
	}

	public String getInspectTypeFirstMonth() {
		return inspectTypeFirstMonth;
	}

	public void setInspectTypeFirstMonth(String inspectTypeFirstMonth) {
		this.inspectTypeFirstMonth = inspectTypeFirstMonth;
	}

	public String getInspectTypeSecondMonth() {
		return inspectTypeSecondMonth;
	}

	public void setInspectTypeSecondMonth(String inspectTypeSecondMonth) {
		this.inspectTypeSecondMonth = inspectTypeSecondMonth;
	}

	public String getInspectTypeThirdMonth() {
		return inspectTypeThirdMonth;
	}

	public void setInspectTypeThirdMonth(String inspectTypeThirdMonth) {
		this.inspectTypeThirdMonth = inspectTypeThirdMonth;
	}

	public String getInspectTypeMonth() {
		return inspectTypeMonth;
	}

	public void setInspectTypeMonth(String inspectTypeMonth) {
		this.inspectTypeMonth = inspectTypeMonth;
	}

	public String getInspectTypeHour() {
		return inspectTypeHour;
	}

	public void setInspectTypeHour(String inspectTypeHour) {
		this.inspectTypeHour = inspectTypeHour;
	}

	public String getInspectTypeMinute() {
		return inspectTypeMinute;
	}

	public void setInspectTypeMinute(String inspectTypeMinute) {
		this.inspectTypeMinute = inspectTypeMinute;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInsepctPalnTaskName() {
		return insepctPalnTaskName;
	}

	public void setInsepctPalnTaskName(String insepctPalnTaskName) {
		this.insepctPalnTaskName = insepctPalnTaskName;
	}

	public int getInspectPlanType() {
		return inspectPlanType;
	}

	public void setInspectPlanType(int inspectPlanType) {
		this.inspectPlanType = inspectPlanType;
	}

	public String getInspectPlanDescrible() {
		return inspectPlanDescrible;
	}

	public void setInspectPlanDescrible(String inspectPlanDescrible) {
		this.inspectPlanDescrible = inspectPlanDescrible;
	}

	public String getInspectPlanDomain() {
		return inspectPlanDomain;
	}

	public void setInspectPlanDomain(String inspectPlanDomain) {
		this.inspectPlanDomain = inspectPlanDomain;
	}

	public Long getInspectPlanInspector() {
		return inspectPlanInspector;
	}

	public void setInspectPlanInspector(Long inspectPlanInspector) {
		this.inspectPlanInspector = inspectPlanInspector;
	}

	public int getInspectPlanReportProduceTimeShow() {
		return inspectPlanReportProduceTimeShow;
	}

	public void setInspectPlanReportProduceTimeShow(
			int inspectPlanReportProduceTimeShow) {
		this.inspectPlanReportProduceTimeShow = inspectPlanReportProduceTimeShow;
	}

	public int getInspectPlanReportModifyTimeShow() {
		return inspectPlanReportModifyTimeShow;
	}

	public void setInspectPlanReportModifyTimeShow(
			int inspectPlanReportModifyTimeShow) {
		this.inspectPlanReportModifyTimeShow = inspectPlanReportModifyTimeShow;
	}

	public int getInspectPlanReportModifiorShow() {
		return inspectPlanReportModifiorShow;
	}

	public void setInspectPlanReportModifiorShow(
			int inspectPlanReportModifiorShow) {
		this.inspectPlanReportModifiorShow = inspectPlanReportModifiorShow;
	}

	public String getInspectRlanReportEditable() {
		return inspectRlanReportEditable;
	}

	public void setInspectRlanReportEditable(String inspectRlanReportEditable) {
		this.inspectRlanReportEditable = inspectRlanReportEditable;
	}

	public String getInspectReportResourceName() {
		return inspectReportResourceName;
	}

	public void setInspectReportResourceName(String inspectReportResourceName) {
		this.inspectReportResourceName = inspectReportResourceName;
	}

	public String getInspectReportBusinessName() {
		return inspectReportBusinessName;
	}

	public void setInspectReportBusinessName(String inspectReportBusinessName) {
		this.inspectReportBusinessName = inspectReportBusinessName;
	}

	public int getInspectPlanStatus() {
		return inspectPlanStatus;
	}

	public void setInspectPlanStatus(int inspectPlanStatus) {
		this.inspectPlanStatus = inspectPlanStatus;
	}

	public int getInspectPlanCreator() {
		return inspectPlanCreator;
	}

	public void setInspectPlanCreator(int inspectPlanCreator) {
		this.inspectPlanCreator = inspectPlanCreator;
	}

	public String getInspectPlanLastEditTime() {
		return inspectPlanLastEditTime;
	}

	public void setInspectPlanLastEditTime(String inspectPlanLastEditTime) {
		this.inspectPlanLastEditTime = inspectPlanLastEditTime;
	}

	public String getInspectPlanTypeTime() {
		return inspectPlanTypeTime;
	}

	public void setInspectPlanTypeTime(String inspectPlanTypeTime) {
		this.inspectPlanTypeTime = inspectPlanTypeTime;
	}

	public String getInspectPlanLastExecTime() {
		return inspectPlanLastExecTime;
	}

	public void setInspectPlanLastExecTime(String inspectPlanLastExecTime) {
		this.inspectPlanLastExecTime = inspectPlanLastExecTime;
	}

}
