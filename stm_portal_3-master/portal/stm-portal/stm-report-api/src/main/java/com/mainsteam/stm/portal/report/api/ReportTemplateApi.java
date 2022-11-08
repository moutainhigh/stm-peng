package com.mainsteam.stm.portal.report.api;

import java.util.List;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.service.bo.BizMainBo;
import com.mainsteam.stm.portal.report.bo.ReportResourceInstance;
import com.mainsteam.stm.portal.report.bo.ReportResourceMetric;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateExpand;
import com.mainsteam.stm.portal.report.bo.ReportTypeEnum;
import com.mainsteam.stm.portal.report.bo.ResourceCategoryTree;

public interface ReportTemplateApi {
	
	/**
	 * 添加报表模板
	 */
	public long addReportTemplate(ReportTemplate reportTemplate,ILoginUser user);
	
	/**
	 * 删除报表模板及所有生成的报告
	 */
	public boolean removeReportTemplate(long reportTemplateId);
	
	/**
	 * 只删除报表模板
	 */
	public boolean removeReportTemplateOnly(long reportTemplateId);
	
	/**
	 * 根据ID查看报表模板信息
	 */
	public ReportTemplate getReportTemplateById(long reportTemplateId);
	
	/**
	 * 根据ID查看报表模板信息(无附带目录指标关系)
	 */
	public ReportTemplateExpand getSimpleReportTemplateById(long reportTemplateId);
	
	/**
	 * 获取单个报表模板用于及时报表
	 */
	public ReportTemplate getReportTemplateForCurrentReport(long reportTemplateId);
		
	/**
	 * 查找所有报表模板
	 */
	public List<ReportTemplateExpand> getAllReportTemplate(ILoginUser user);
	
	/**
	 * 根据资源类别ID获取主资源实例
	 */
	public List<ReportResourceInstance> getResourceInstanceByCategoryId(String categoryId,long domainId,int startNum,int pageSize,String content,ILoginUser user);
	
	/**
	 * 根据资源获取资源实例
	 */
	public List<ReportResourceInstance> getInstanceByResource(String resourceId,long domainId,int startNum,int pageSize,String content,ILoginUser user);
	
	/**
	 * 根据资源类别获取子资源
	 */
	public List<ResourceCategoryTree> getChildResourceByResourceCategory(String categoryId);
	
	/**
	 * 根据主资源获取子资源
	 */
	public List<ResourceCategoryTree> getChildResourceByMainResource(String resourceId);
	
	/**
	 * 过滤资源实例
	 */
	public List<ReportResourceInstance> filterResourceInstanceList(List<Long> instanceIds,String content,int startNum,int pageSize);
	
	/**
	 * 获取所有资源类别
	 */
	public List<ResourceCategoryTree> getAllResourceCategory();
	
	/**
	 * 根据资源ID查询指标列表
	 */
	public List<ReportResourceMetric> getMetricListByResource(List<String> resourceIdList,long instanceId,int reportType,int comprehensiveType);
	
	/**
	 * 根据资源实例ID查询指标列表
	 */
	public List<ReportResourceMetric> getMetricListByInstance(String[] instanceIdList,long instanceId,int reportType,int comprehensiveType);
	
	/**
	 * 修改报表模板
	 */
	public boolean updateReportTemplate(ReportTemplate reportTemplate,ILoginUser user);
	
	/**
	 * 恢复期望值到默认值
	 */
	public List<String> resetThresholdToDefault(List<String> metricIds,long instanceId);
	
	/**
	 * 根据type获取模板列表
	 */
	public List<ReportTemplate> getReportTemplateListByType(ReportTypeEnum type);
	
	/**
	 * 获取业务列表
	 */
	public List<BizMainBo> getBusinessList(ILoginUser user,String searchContent,int startNum,int pageSize,long domainId);
	
	/**
	 * 获取业务指标
	 */
	public List<ReportResourceMetric> getBusinessMetricList();
	
}
