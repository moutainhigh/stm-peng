package com.mainsteam.stm.portal.inspect.bo;

import java.io.Serializable;
import java.util.List;

public class InspectPlanBasicBo implements Serializable {

	private static final long serialVersionUID = -6291889555644731642L;
	// 表主键ID
	private Long id;
	/**
	 * inspectid_domainid_GROUP
	 */
	private Long inspectid;
	private Long domainid;
	// 巡检任务名称
	private String insepctPalnTaskName;
	// 巡检方式
	private Integer inspectPlanType;
	// 描述
	private String inspectPlanDescrible;
	// 域名称
	private String inspectPlanDomain;
	// 巡检人ID
	private Long inspectPlanInspector;
	// 在报表中是否显示报告生成时间
	private Integer inspectPlanReportProduceTimeShow;
	// 在报表中是否显示报告最后编辑时间
	private Integer inspectPlanReportModifyTimeShow;
	// 在报表中是否显示最后编辑人
	private Integer inspectPlanReportModifiorShow;
	// 在报表中是否允许报表内容被手动修改
	private Integer inspectRlanReportEditable;
	// 资源类型名称的id
	private String inspectReportResourceName;
	// 业务名称的id
	private String inspectReportBusinessName;
	// 状态（是否启用）
	private Integer inspectPlanStatus;
	// 创建人ID
	private Long inspectPlanCreator;
	// 最后编辑时间
	private String inspectPlanLastEditTime;
	// 如果为自动，存一个时间JSON字符串
	private String inspectPlanTypeTime;
	// 最后执行时间
	private String inspectPlanLastExecTime;
	// 创建人名称
	private String createUserName;
	// 巡检人名称
	private String inspectorName;

	private List<InspectPlanContentBo> contents;
	private String domainName;

	private Integer[] inspectPlanTypes;
	private String[] inspectPlanDomains;

	private String authorityUserId;
	private String[] authorityDomainIds;

	private String orderUserId;

	public String getOrderUserId() {
		return orderUserId;
	}

	public void setOrderUserId(String orderUserId) {
		this.orderUserId = orderUserId;
	}

	public String getAuthorityUserId() {
		return authorityUserId;
	}

	public void setAuthorityUserId(String authorityUserId) {
		this.authorityUserId = authorityUserId;
	}

	public String[] getAuthorityDomainIds() {
		return authorityDomainIds;
	}

	public void setAuthorityDomainIds(String[] authorityDomainIds) {
		this.authorityDomainIds = authorityDomainIds;
	}

	public String getInspectorName() {
		return inspectorName;
	}

	public void setInspectorName(String inspectorName) {
		this.inspectorName = inspectorName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String[] getInspectPlanDomains() {
		return inspectPlanDomains;
	}

	public void setInspectPlanDomains(String[] inspectPlanDomains) {
		this.inspectPlanDomains = inspectPlanDomains;
	}

	public Integer[] getInspectPlanTypes() {
		return inspectPlanTypes;
	}

	public void setInspectPlanTypes(Integer[] inspectPlanTypes) {
		this.inspectPlanTypes = inspectPlanTypes;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public List<InspectPlanContentBo> getContents() {
		return contents;
	}

	public void setContents(List<InspectPlanContentBo> contents) {
		this.contents = contents;
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

	public Integer getInspectPlanType() {
		return inspectPlanType;
	}

	public void setInspectPlanType(Integer inspectPlanType) {
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

	public Integer getInspectPlanReportProduceTimeShow() {
		return inspectPlanReportProduceTimeShow;
	}

	public void setInspectPlanReportProduceTimeShow(
			Integer inspectPlanReportProduceTimeShow) {
		this.inspectPlanReportProduceTimeShow = inspectPlanReportProduceTimeShow;
	}

	public Integer getInspectPlanReportModifyTimeShow() {
		return inspectPlanReportModifyTimeShow;
	}

	public void setInspectPlanReportModifyTimeShow(
			Integer inspectPlanReportModifyTimeShow) {
		this.inspectPlanReportModifyTimeShow = inspectPlanReportModifyTimeShow;
	}

	public Integer getInspectPlanReportModifiorShow() {
		return inspectPlanReportModifiorShow;
	}

	public void setInspectPlanReportModifiorShow(
			Integer inspectPlanReportModifiorShow) {
		this.inspectPlanReportModifiorShow = inspectPlanReportModifiorShow;
	}

	public Integer getInspectRlanReportEditable() {
		return inspectRlanReportEditable;
	}

	public void setInspectRlanReportEditable(Integer inspectRlanReportEditable) {
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

	public Integer getInspectPlanStatus() {
		return inspectPlanStatus;
	}

	public void setInspectPlanStatus(Integer inspectPlanStatus) {
		this.inspectPlanStatus = inspectPlanStatus;
	}

	public Long getInspectPlanCreator() {
		return inspectPlanCreator;
	}

	public void setInspectPlanCreator(Long inspectPlanCreator) {
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

	public Long getInspectid() {
		return inspectid;
	}

	public void setInspectid(Long inspectid) {
		this.inspectid = inspectid;
	}

	public Long getDomainid() {
		return domainid;
	}

	public void setDomainid(Long domainid) {
		this.domainid = domainid;
	}

}
