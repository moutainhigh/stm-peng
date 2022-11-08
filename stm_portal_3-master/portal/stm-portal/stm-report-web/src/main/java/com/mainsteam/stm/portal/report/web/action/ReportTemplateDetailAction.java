package com.mainsteam.stm.portal.report.web.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.report.api.ReportTemplateApi;
import com.mainsteam.stm.portal.report.bo.DefaultAlarmMetric;
import com.mainsteam.stm.portal.report.bo.DefaultAvailabilityMetric;
import com.mainsteam.stm.portal.report.bo.DefaultTopnAlarmMetric;
import com.mainsteam.stm.portal.report.bo.ReportResourceInstance;
import com.mainsteam.stm.portal.report.bo.ReportResourceMetric;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ResourceCategoryTree;

/**
 * 
 * <li>文件名称: CustomResGroupAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月17日
 * @author   pengl
 */

@Controller
@RequestMapping("/portal/report/reportTemplate")
public class ReportTemplateDetailAction extends BaseAction {
	@Resource
	private ReportTemplateApi reportTemplateApi;
	
	private static final Log logger = LogFactory.getLog(ReportTemplateDetailAction.class);
	
	/**
	 * 添加报表模板
	 * @param
	 * @return
	 */
	@RequestMapping("/addTemplate")
	public JSONObject addTemplate(String template,HttpSession session) {
		
		ILoginUser user = getLoginUser(session);
		
		ReportTemplate templateInstance = JSONObject.parseObject(template,ReportTemplate.class);
		long newTemplateId = -1;	
		
		newTemplateId = reportTemplateApi.addReportTemplate(templateInstance,user);
		
		return toSuccess(newTemplateId);
		
	}
	
	/**
	 * 修改报表模板
	 * @param
	 * @return
	 */
	@RequestMapping("/updateTemplate")
	public JSONObject updateTemplate(String template,HttpSession session) {
		
		ILoginUser user = getLoginUser(session);
		
		ReportTemplate templateInstance = JSONObject.parseObject(template,ReportTemplate.class);
		boolean isSuccess = true;	
		
		try {
			isSuccess = reportTemplateApi.updateReportTemplate(templateInstance,user);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			isSuccess = false;
		}
		
		return toSuccess(isSuccess);
		
	}
	
	/**
	 * 获取资源类型
	 */
	@RequestMapping("/getResourceCategoryList")
	public JSONObject getResourceCategoryList() {
		List<ResourceCategoryTree> categorys = reportTemplateApi.getAllResourceCategory();
		return toSuccess(categorys);
		
	}
	
	/**
	 * 获取子资源类型根据类别或者主资源
	 */
	@RequestMapping("/getChildResourceListByMainResourceOrCategory")
	public JSONObject getChildResourceListByMainResourceOrCategory(ResourceCategoryTree category) {
		List<ResourceCategoryTree> defs = new ArrayList<ResourceCategoryTree>();
		if(category.getType() == 1){
			//类别获取子资源
			defs = reportTemplateApi.getChildResourceByResourceCategory(category.getId());
		}else if(category.getType() == 2){
			//主资源获取子资源
			defs = reportTemplateApi.getChildResourceByMainResource(category.getId());
		}
		return toSuccess(defs);
		
	}
	
	/**
	 * 获取资源实例
	 */
	@RequestMapping("/getResourceInstanceList")
	public JSONObject getResourceInstanceList(String queryId,int type,Long domainId,int startNum,int pageSize,String content,HttpSession session) {
		List<ReportResourceInstance> defs = new ArrayList<ReportResourceInstance>();
		if(type == 1){
			//通过类别获取资源实例
			defs = reportTemplateApi.getResourceInstanceByCategoryId(queryId,domainId,startNum,pageSize,content,getLoginUser(session));
		}else if(type == 2){
			//通过资源获取资源实例
			defs = reportTemplateApi.getInstanceByResource(queryId,domainId,startNum,pageSize,content,getLoginUser(session));
		}
		return toSuccess(defs);
		
	}
	
	/**
	 * 获取指标
	 */
	@RequestMapping("/getMetricListByResourceId")
	public JSONObject getMetricListByResourceId(String resourceIdList,Long instanceId,Integer reportType,Integer comprehensiveType) {
		List<ReportResourceMetric> defs = new ArrayList<ReportResourceMetric>();
		Set<String> idSet = new HashSet<String>();
		if(resourceIdList.contains(",")){
			for(String id : resourceIdList.split(",")){
				idSet.add(id);
			}
		}else{
			idSet.add(resourceIdList);
		}
		List<String> ids = new ArrayList<String>(idSet);
		defs = reportTemplateApi.getMetricListByResource(ids,instanceId,reportType,comprehensiveType);
		return toSuccess(defs);
		
	}
	
	/**
	 * 获取指标
	 */
	@RequestMapping("/getMetricListByInstanceId")
	public JSONObject getMetricListByInstanceId(String instanceIdList,Long instanceId,Integer reportType,Integer comprehensiveType) {
		List<ReportResourceMetric> defs = new ArrayList<ReportResourceMetric>();
		defs = reportTemplateApi.getMetricListByInstance(instanceIdList.split(","),instanceId,reportType,comprehensiveType);
		return toSuccess(defs);
		
	}
	
	/**
	 * 过滤资源实例
	 */
	@RequestMapping("/filterInstanceInfoByContent")
	public JSONObject filterInstanceInfoByContent(String instanceIds,String content,int startNum,int pageSize) {
		List<ReportResourceInstance> defs = new ArrayList<ReportResourceInstance>();
		List<Long> ids = new ArrayList<Long>();
		if(instanceIds.contains(",")){
			for(String id : instanceIds.split(",")){
				ids.add(Long.parseLong(id));
			}
		}else{
			ids.add(Long.parseLong(instanceIds));
		}
		defs = reportTemplateApi.filterResourceInstanceList(ids, content,startNum,pageSize);
		return toSuccess(defs);
		
	}
	
	/**
	 * 根据ID获取模板详情
	 */
	@RequestMapping("/getTemplateDetailByTemplateId")
	public JSONObject getTemplateDetailByTemplateId(Long templateId) {
		return toSuccess(reportTemplateApi.getReportTemplateById(templateId));
		
	}
	
	/**
	 * 获取固定告警指标数据
	 */
	@RequestMapping("/getDefaultAlarmMetricData")
	public JSONObject getDefaultAlarmMetricData() {
		return toSuccess(new DefaultAlarmMetric().getMetricData());
	}
	
	/**
	 * 获取固定Topn告警指标数据
	 */
	@RequestMapping("/getDefaultTopnAlarmMetricData")
	public JSONObject getDefaultTopnAlarmMetricData() {
		return toSuccess(new DefaultTopnAlarmMetric().getMetricData());
	}
	
	/**
	 * 获取固定可用性指标数据
	 */
	@RequestMapping("/getDefaultAvailabilityMetricData")
	public JSONObject getDefaultAvailabilityMetricData() {
		return toSuccess(new DefaultAvailabilityMetric().getMetricData());
	}
	
	/**
	 * 恢复期望值到默认值
	 */
	@RequestMapping("/resetThresholdToDefaultValue")
	public JSONObject resetThresholdToDefaultValue(String metricIds,long instanceId) {
		List<String> thresholdValues = new ArrayList<String>();
		List<String> ids = new ArrayList<String>();
		if(metricIds.contains(",")){
			for(String id : metricIds.split(",")){
				ids.add(id);
			}
		}else{
			ids.add(metricIds);
		}
		
		thresholdValues = reportTemplateApi.resetThresholdToDefault(ids, instanceId);
		return toSuccess(thresholdValues);
		
	}
	
	/**
	 * 获取业务列表
	 * @update 获取业务列表增加域参数 20161205 dfw
	 */
	@RequestMapping("/getBusinessList")
	public JSONObject getBusinessList(HttpSession session,String searchContent,int startNum,int pageSize,long domainId){
		return toSuccess(reportTemplateApi.getBusinessList(getLoginUser(session),searchContent,startNum,pageSize,domainId));
	}
	
	/**
	 * 获取业务指标列表
	 */
	@RequestMapping("/getBusinessMetricList")
	public JSONObject getBusinessMetricList(String searchContent){
		List<ReportResourceMetric> metricList = reportTemplateApi.getBusinessMetricList();
		return toSuccess(metricList);
	}
	
}
