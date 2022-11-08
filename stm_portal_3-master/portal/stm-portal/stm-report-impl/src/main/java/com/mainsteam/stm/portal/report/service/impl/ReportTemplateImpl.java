package com.mainsteam.stm.portal.report.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.api.BizUserRelApi;
import com.mainsteam.stm.portal.business.service.bo.BizMainBo;
import com.mainsteam.stm.portal.business.service.dao.IBizMainDao;
import com.mainsteam.stm.portal.business.report.api.BizSerReportListener;
import com.mainsteam.stm.portal.business.report.obj.BizSerMetric;
import com.mainsteam.stm.portal.business.report.obj.BizSerReportEvent;
import com.mainsteam.stm.portal.report.api.ReportApi;
import com.mainsteam.stm.portal.report.api.ReportTemplateApi;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.DefaultAlarmMetric;
import com.mainsteam.stm.portal.report.bo.DefaultAvailabilityMetric;
import com.mainsteam.stm.portal.report.bo.DefaultTopnAlarmMetric;
import com.mainsteam.stm.portal.report.bo.ReportResourceInstance;
import com.mainsteam.stm.portal.report.bo.ReportResourceMetric;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateExpand;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryInstance;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryMetric;
import com.mainsteam.stm.portal.report.bo.ReportTypeEnum;
import com.mainsteam.stm.portal.report.bo.ResourceCategoryTree;
import com.mainsteam.stm.portal.report.dao.IReportDao;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDao;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDirectoryDao;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDirectoryInstanceDao;
import com.mainsteam.stm.portal.report.dao.IReportTemplateDirectoryMetricDao;
import com.mainsteam.stm.portal.report.engine.ReportEngine;
import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryInstancePo;
import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryMetricPo;
import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryPo;
import com.mainsteam.stm.portal.report.po.ReportTemplatePo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.system.license.api.ILicenseApi;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.api.IDomainReferencerRelationshipApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.login.bo.LoginUser;
import com.mainsteam.stm.system.um.relation.bo.UserRole;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;

public class ReportTemplateImpl implements ReportTemplateApi,InstancelibInterceptor,BizSerReportListener,IDomainReferencerRelationshipApi{
	
	@Resource
	private IReportDao iReportDao;
	
	@Resource
	private IReportTemplateDao iReportTemplateDao;
	
	@Resource
	private IReportTemplateDirectoryDao iReportTemplateDirectoryDao;
	
	@Resource
	private IReportTemplateDirectoryInstanceDao iReportTemplateDirectoryInstanceDao;
	
	@Resource
	private IReportTemplateDirectoryMetricDao iReportTemplateDirectoryMetricDao;
	
	@Resource
	private ISequence ReportTemplateSeq;
	
	@Resource
	private ISequence ReportTemplateDirectorySeq;
	
	@Resource
	private ISequence ReportTemplateDirectoryInstanceSeq;
	
	@Resource
	private ISequence ReportTemplateDirectoryMetricSeq;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private IFileClientApi fileClient;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private ProfileService profileService;
	
	@Resource
	private IDomainApi domainApi;
	
	@Resource
	private IUserApi stm_system_userApi;
	
	@Resource
	private ReportApi ReportApi;
	
	@Resource
	private ReportEngine reportEngine;
	
	@Resource
	private IResourceApi stm_system_resourceApi;
	
	@Resource
	private IBizMainDao bizMainDao;
	
	@Resource
	private BizMainApi bizMainApi;
	
	@Autowired
	private ILicenseApi licenseApi;
	
	@Autowired
	private ISearchApi searchApi;
	
	@Resource
	private BizUserRelApi bizUserRelApi;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;
	
	private static final Log logger = LogFactory.getLog(ReportTemplateImpl.class);
	
	private static String DETAULT_TABLE_COLUMNS_COUNT = "1";
	
	/**
	 * 添加报表模板
	 */
	@Override
	public long addReportTemplate(ReportTemplate reportTemplate,ILoginUser user) {
		
		long newTemplateId = -1;
		
		ReportTypeEnum type = ReportTypeEnum.getReportTypeEnum(reportTemplate.getReportTemplateType());
		
		switch (type) {
			case PERFORMANCE_REPORT:
				newTemplateId = addPerformanceReportTemplate(reportTemplate,user);
				break;
			case ALARM_REPORT:
				newTemplateId = addAlarmReportTemplate(reportTemplate,user);
				break;
			case TOPN_REPORT:
				newTemplateId = addTopnReportTemplate(reportTemplate,user);
				break;
			case AVAILABILITY_REPORT:
				newTemplateId = addAvailabilityReportTemplate(reportTemplate,user);
				break;
			case TREND_REPORT:
				newTemplateId = addTrendReportTemplate(reportTemplate,user);
				break;
			case ANALYSIS_REPORT:
				newTemplateId = addAnalysisReportTemplate(reportTemplate,user);
				break;
			case BUSINESS_REPORT:
				newTemplateId = addBusinessReportTemplate(reportTemplate,user);
				break;
			case COMPREHENSIVE_REPORT:
				newTemplateId = addComprehensiveReportTemplate(reportTemplate,user);
				break;
			case VM_PERFORMANCE_REPORT:
				newTemplateId = addVMPerformanceReportTemplate(reportTemplate,user);
				break;	
			case VM_ALARM_REPORT:
				newTemplateId = addVMAlarmReportTemplate(reportTemplate,user);
				break;	
			default:
				break;
		}
		
		return newTemplateId;
	}

	/**
	 * 只删除报表模板
	 */
	public boolean removeReportTemplateOnly(long reportTemplateId) {
		boolean isSuccess = true;
		List<Long> directoryIds = iReportTemplateDirectoryDao
				.selectDirectoryIdByTemplateId(reportTemplateId);
		try {
			reportEngine.stopEngine(reportTemplateId);
			if (iReportTemplateDao.logicDelete(reportTemplateId) != 1) {
				isSuccess = false;
				return isSuccess;
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
			return false;
		}

//		// 删除报表模板目录
//		for (long direcotryId : directoryIds) {
//			if (iReportTemplateDirectoryDao.del(direcotryId) != 1) {
//				isSuccess = false;
//				return isSuccess;
//			}
//			// 删除目录和资源实例的关系
//			if (iReportTemplateDirectoryInstanceDao
//					.deleteInstanceRelationByDirectoryId(direcotryId) != 1) {
//				return false;
//			}
//			// 删除目录和指标的关系
//			if (iReportTemplateDirectoryMetricDao
//					.deleteMetricRelationByDirectoryId(direcotryId) != 1) {
//				return false;
//			}
//		}
		return isSuccess;
	}
	
	/**
	 * 删除报表模板及所有生成的报告
	 */
	@Override
	public boolean removeReportTemplate(long reportTemplateId) {
		boolean isSuccess = true;
		List<Long> directoryIds = iReportTemplateDirectoryDao
				.selectDirectoryIdByTemplateId(reportTemplateId);
		// 停止job
		try {
			reportEngine.stopEngine(reportTemplateId);
			// 删除报表模板
			if (iReportTemplateDao.logicDelete(reportTemplateId) != 1) {
				isSuccess = false;
				return isSuccess;
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
			return false;
		}

//		// 删除报表模板目录
//		for (long direcotryId : directoryIds) {
//			if (iReportTemplateDirectoryDao.del(direcotryId) != 1) {
//				isSuccess = false;
//				return isSuccess;
//			}
//			// 删除目录和资源实例的关系
//			if (iReportTemplateDirectoryInstanceDao
//					.deleteInstanceRelationByDirectoryId(direcotryId) != 1) {
//				logger.info("资源删除失败");
//				return false;
//			}
//			// 删除目录和指标的关系
//			if (iReportTemplateDirectoryMetricDao
//					.deleteMetricRelationByDirectoryId(direcotryId) != 1) {
//				logger.info("指标删除失败");
//				return false;
//			}
//		}
		// 删除目录对应的报表及报表文件,并且删除新,旧模板文件
		if (!ReportApi.removeReportByTemplateId(reportTemplateId)) {
			logger.info("文件删除失败");
			return false;
		}

		// //取消资源操作
		logger.info("执行报表资源删除.......");
		ResourceBizRel model = new ResourceBizRel();
		model.setBizId(reportTemplateId);
		searchApi.delSearchReport(model);
		return isSuccess;
	}

	/**
	 * 根据ID查看报表模板信息(无附带目录指标关系)
	 */
	public ReportTemplateExpand getSimpleReportTemplateById(long reportTemplateId){
		ReportTemplatePo templatePo = iReportTemplateDao.get(reportTemplateId);
		ReportTemplateExpand template = new ReportTemplateExpand();
		BeanUtils.copyProperties(templatePo, template);
		
		User user = stm_system_userApi.get(templatePo.getReportTemplateCreateUserId());
		if(null!=user){
			template.setReportTemplateCreateUserName(user.getName());
		}else{
			template.setReportTemplateCreateUserName("用户已被删除!");
		}
		
		if(template.getReportTemplateDomainId()>0){
			Domain domain = domainApi.get(template.getReportTemplateDomainId());
			if(null!=domain){
				template.setReportTemplateDomainName(domain.getName());
			}else{
				template.setReportTemplateDomainName("该域已被删除!");
			}
		
		}
		return template;
	}
	
	/**
	 * 获取单个报表模板
	 */
	@Override
	public ReportTemplate getReportTemplateById(long reportTemplateId) {
		ReportTemplatePo templatePo = iReportTemplateDao.get(reportTemplateId);
		
		if(templatePo == null){
			return null;
		}
		
		//查出报表目录
		List<ReportTemplateDirectory> reportTemplateDirectoryList = new ArrayList<ReportTemplateDirectory>();
		ReportTemplateDirectoryPo reportTemplateDirectoryCondionPo = new ReportTemplateDirectoryPo();
		reportTemplateDirectoryCondionPo.setReportTemplateId(reportTemplateId);
		List<ReportTemplateDirectoryPo> reportTemplateDirectoryPos = iReportTemplateDirectoryDao.select(reportTemplateDirectoryCondionPo);
		for(ReportTemplateDirectoryPo directoryPo : reportTemplateDirectoryPos){
			
			//查出目录和实例的对应关系
			List<ReportTemplateDirectoryInstance> ReportTemplateDirectoryInstanceList = new ArrayList<ReportTemplateDirectoryInstance>();
			ReportTemplateDirectoryInstancePo reportTemplateDirectoryInstanceCondionPo = new ReportTemplateDirectoryInstancePo();
			reportTemplateDirectoryInstanceCondionPo.setReportTemplateDirectoryId(directoryPo.getReportTemplateDirectoryId());
			List<ReportTemplateDirectoryInstancePo> reportTemplateDirectoryInstancePos = iReportTemplateDirectoryInstanceDao.select(reportTemplateDirectoryInstanceCondionPo);
			for(ReportTemplateDirectoryInstancePo instanceRelationPo : reportTemplateDirectoryInstancePos){
				ReportTemplateDirectoryInstance instanceRelation = new ReportTemplateDirectoryInstance();
				BeanUtils.copyProperties(instanceRelationPo, instanceRelation);
				ReportTemplateDirectoryInstanceList.add(instanceRelation);
			}
			
			//查出目录和指标的对应关系
			List<ReportTemplateDirectoryMetric> ReportTemplateDirectoryMetricList = new ArrayList<ReportTemplateDirectoryMetric>();
			ReportTemplateDirectoryMetricPo reportTemplateDirectoryMetricCondionPo = new ReportTemplateDirectoryMetricPo();
			reportTemplateDirectoryMetricCondionPo.setReportTemplateDirectoryId(directoryPo.getReportTemplateDirectoryId());
			List<ReportTemplateDirectoryMetricPo> reportTemplateDirectoryMetricPos = iReportTemplateDirectoryMetricDao.select(reportTemplateDirectoryMetricCondionPo);
			for(ReportTemplateDirectoryMetricPo metricRelationPo : reportTemplateDirectoryMetricPos){
				ReportTemplateDirectoryMetric metricRelation = new ReportTemplateDirectoryMetric();
				BeanUtils.copyProperties(metricRelationPo, metricRelation);
				ReportTemplateDirectoryMetricList.add(metricRelation);
			}
			
			ReportTemplateDirectory templateDirectory = new ReportTemplateDirectory();
			BeanUtils.copyProperties(directoryPo, templateDirectory);
			templateDirectory.setDirectoryInstanceList(ReportTemplateDirectoryInstanceList);
			templateDirectory.setDirectoryMetricList(ReportTemplateDirectoryMetricList);
			
			reportTemplateDirectoryList.add(templateDirectory);
			
		}
		
		ReportTemplate template = new ReportTemplate();
		BeanUtils.copyProperties(templatePo, template);
		template.setDirectoryList(reportTemplateDirectoryList);
		return template;
	}

	/**
	 * 获取单个报表模板用于及时报表
	 */
	public ReportTemplate getReportTemplateForCurrentReport(long reportTemplateId) {
		
		String resourceId = "";
		ReportTemplate rtl = getReportTemplateById(reportTemplateId);
		
//		List<ReportTemplateDirectory> rtdList = new ArrayList<ReportTemplateDirectory>();
//		List<ReportTemplateDirectoryInstance> rtdiList = new ArrayList<ReportTemplateDirectoryInstance>();
//		List<ReportTemplateDirectoryMetric> rtdmList = new ArrayList<ReportTemplateDirectoryMetric>();
		
		for(ReportTemplateDirectory rtd:rtl.getDirectoryList()){
			
			List<ReportTemplateDirectoryInstance> directoryInstanceList = rtd.getDirectoryInstanceList();
			
			if(directoryInstanceList == null || directoryInstanceList.size() <= 0){
				continue;
			}
			
			for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
				
				ResourceInstanceBo instance = stm_system_resourceApi.getResource(directoryInstance.getInstanceId());
				directoryInstance.setInstanceIP(instance.getDiscoverIP());
				if(instance.getShowName() == null || instance.getShowName().equals("")){
					directoryInstance.setInstanceName(instance.getName());
				}else{
					directoryInstance.setInstanceName(instance.getShowName());
				}
				directoryInstance.setInstanceType(rtd.getReportTemplateDirectoryResource());
				resourceId = instance.getResourceId();
				
			}
			
			for(ReportTemplateDirectoryMetric directoryMetric:rtd.getDirectoryMetricList()){
				ResourceMetricDef def = capacityService.getResourceMetricDef(resourceId,directoryMetric.getMetricId());
				if(def != null){
					String unitValue = "";
					if(null!=def.getUnit()){
						if(rtl.getReportTemplateType() == (ReportTypeEnum.TOPN_REPORT.getIndex()) || (rtl.getReportTemplateType()==ReportTypeEnum.COMPREHENSIVE_REPORT.getIndex()&&rtd.getReportTemplateDirectoryType()==ReportTypeEnum.TOPN_REPORT.getIndex())){
							directoryMetric.setMetricUnit(def.getUnit());
						}else{
							directoryMetric.setMetricUnit(def.getUnit());
							unitValue = "("+def.getUnit()+")";
						}
					}
					directoryMetric.setMetricName(def.getName()+unitValue);
					directoryMetric.setMetricType(def.getMetricType());
				}else{
					//告警topn没有指标名称
					if((rtl.getReportTemplateType() == (ReportTypeEnum.TOPN_REPORT.getIndex())
							&& rtd.getReportTemplateDirectoryMetricValueType() == 2) || 
							 ((rtl.getReportTemplateType()==ReportTypeEnum.COMPREHENSIVE_REPORT.getIndex()&&rtd.getReportTemplateDirectoryType()==ReportTypeEnum.TOPN_REPORT.getIndex())
							 &&rtd.getReportTemplateDirectoryMetricValueType() == 2)){
						DefaultTopnAlarmMetric defaultTopnAlarmMetric = new DefaultTopnAlarmMetric();
						List<ReportResourceMetric> reportResourceMetricList = defaultTopnAlarmMetric.getMetricData();
						for(int i = 0; i < reportResourceMetricList.size(); i++){
							ReportResourceMetric reportResourceMetric = reportResourceMetricList.get(i);
							if(reportResourceMetric.getId().equals(directoryMetric.getMetricId())){
								directoryMetric.setMetricName(reportResourceMetric.getName());
								break;
							}
						}
					}else{
						if(logger.isErrorEnabled()){
							logger.error("ResourceMetricDef is null ,resourceId = " + resourceId + ",MetricId = " + directoryMetric.getMetricId());
						}
					}
				}
				if(rtl.getReportTemplateType()==ReportTypeEnum.ANALYSIS_REPORT.getIndex() || (rtl.getReportTemplateType()==ReportTypeEnum.COMPREHENSIVE_REPORT.getIndex()&&rtd.getReportTemplateDirectoryType()==ReportTypeEnum.ANALYSIS_REPORT.getIndex())){
					if(def != null){
						ProfileMetric profileMetric = null;
						try {
							profileMetric = profileService.getMetricByInstanceIdAndMetricId(directoryInstanceList.get(0).getInstanceId(), directoryMetric.getMetricId());
						} catch (ProfilelibException e) {
							if(logger.isErrorEnabled()){
								logger.error(e.getMessage(),e);
							}
						}
						//找到正常状态最大边界
						List<ProfileThreshold> thresholdList = profileMetric.getMetricThresholds();
						for(ProfileThreshold threshlod : thresholdList){
							if(threshlod.getPerfMetricStateEnum() == PerfMetricStateEnum.Minor){
								directoryMetric.setMetricCurThreshold(Integer.parseInt(threshlod.getThresholdValue()));
								break;
							}
						}
				}
			 }
			}
		}
		return rtl;
	}
	
	/**
	 * 获取所有报表模板
	 */
	@Override
	public List<ReportTemplateExpand> getAllReportTemplate(ILoginUser user) {
		List<ReportTemplateExpand> templateList = new ArrayList<ReportTemplateExpand>();
		//获取当前登录用户
		List<Long> userIdList = new ArrayList<Long>();
		
		if(user.isCommonUser() && !user.isManagerUser() && !user.isDomainUser()){
			//普通用户只能查询自己创建的报表
			userIdList.add(user.getId());
			
		}else if(user.isDomainUser() || user.isManagerUser()){
			//域管理员能看到域下所有用户创建的报表
			Set<IDomain> userDomain = user.getDomains(ILoginUser.RIGHT_REPORT);
			Set<Long>  userIdSet = new HashSet<Long>();
			
			for(IDomain ido:userDomain){
				List<UserRole> userRoleList = domainApi.getUserRolesByDomainId(ido.getId());
				for(UserRole ur:userRoleList){
					userIdSet.add(ur.getUserId());
				}
			}
			userIdList = new ArrayList<Long>(userIdSet);
			
		}else if(user.isSystemUser()){
			//系统管理员能看到所有的报表
			
		}
		
		List<ReportTemplatePo> templatePoList = iReportTemplateDao.select(null);
		for(ReportTemplatePo templatePo : templatePoList){
			if(templatePo.getReportTemplateIsDelete()!=0){
				continue;
			}
			if(stm_system_userApi.get(templatePo.getReportTemplateCreateUserId()) == null){
				continue;
			}
			
			if(userIdList == null || userIdList.size() <= 0){
				ReportTemplateExpand template = new ReportTemplateExpand();
				BeanUtils.copyProperties(templatePo, template);
				User userT = stm_system_userApi.get(templatePo.getReportTemplateCreateUserId());
				if(null!=userT){
					template.setReportTemplateCreateUserName(userT.getName());
				}else{
					template.setReportTemplateCreateUserName("用户已被删除!");
				}
				if(template.getReportTemplateDomainId()>0){
					Domain domain = domainApi.get(template.getReportTemplateDomainId());
					if(null!=domain){
						template.setReportTemplateDomainName(domain.getName());
					}else{
						template.setReportTemplateDomainName("该域已被删除!");
					}
				}
				
				templateList.add(template);
			}else{
				if(userIdList.contains(templatePo.getReportTemplateCreateUserId())){
					ReportTemplateExpand template = new ReportTemplateExpand();
					BeanUtils.copyProperties(templatePo, template);
					User userTe = stm_system_userApi.get(templatePo.getReportTemplateCreateUserId());
					if(null!=userTe){
						template.setReportTemplateCreateUserName(userTe.getName());
					}else{
						template.setReportTemplateCreateUserName("用户已被删除!");
					}
					if(template.getReportTemplateDomainId()>0){
						Domain domain = domainApi.get(template.getReportTemplateDomainId());
						if(null!=domain){
							template.setReportTemplateDomainName(domain.getName());
						}else{
							template.setReportTemplateDomainName("该域已被删除!");
						}
					}
					templateList.add(template);
				}
			}
		}
		return templateList;
	}

	/**
	 * 根据资源类别ID获取主资源实例
	 */
	@Override
	public List<ReportResourceInstance> getResourceInstanceByCategoryId(String categoryId,long domainId,int startNum,int pageSize,String content,ILoginUser user) {
		List<ReportResourceInstance> instanceList = new ArrayList<ReportResourceInstance>();
		CategoryDef def = capacityService.getCategoryById(categoryId);
		if(def == null){
			if(logger.isErrorEnabled()){
				logger.error("Get categoryDef is null,categoryId = " + categoryId);
			}
			return instanceList;
		}
		
		//获取叶子类别
		List<CategoryDef> leafCategoryList = capacityService.getLeafCategoryList(def);
		List<String> leafCategoryNameList = new ArrayList<String>();
		
		if(leafCategoryList == null){
			if(logger.isErrorEnabled()){
				logger.error("Get leafCategryList is null,categoryId = " + categoryId);
			}
			return instanceList;
		}
		
		for(CategoryDef leafDef : leafCategoryList){
			leafCategoryNameList.add(leafDef.getId());
		}
		
		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		queryBo.setCategoryIds(leafCategoryNameList);
		List<Long> domainIds = new ArrayList<Long>();
		domainIds.add(domainId);
		queryBo.setDomainIds(domainIds);
		List<ResourceInstanceBo> instances = stm_system_resourceApi.getResources(queryBo);
		if(instances != null && instances.size() > 0){
			for(ResourceInstanceBo instance : instances){
				//判断实例是否监控和是否属于当前用户域
				if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
					continue;
				}
				
				ReportResourceInstance reportInstance = new ReportResourceInstance();
				BeanUtils.copyProperties(instance, reportInstance);
				if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
					reportInstance.setShowName(instance.getName());
				}
				
				ResourceDef resourceDef = capacityService.getResourceDefById(reportInstance.getResourceId());
				if(resourceDef == null){
					if(logger.isErrorEnabled()){
						logger.error("Get resourceDef is null ,resourceId : " + reportInstance.getResourceId());
					}
				}else{
					reportInstance.setResourceName(resourceDef.getName());
					
					if(content != null && !content.trim().equals("")){
						if(!reportInstance.getShowName().toLowerCase().contains(content.toLowerCase()) && 
								!reportInstance.getDiscoverIP().toLowerCase().contains(content.toLowerCase()) && 
								!reportInstance.getResourceName().toLowerCase().contains(content.toLowerCase())){
							continue;
						}
					}
					
					instanceList.add(reportInstance);
				}
			}
		}
		
		if((startNum + pageSize) > instanceList.size()){
			instanceList = instanceList.subList(startNum, instanceList.size());
		}else{
			instanceList = instanceList.subList(startNum, (startNum + pageSize));
		}
		
		return instanceList;
	}

	/**
	 * 根据资源获取资源实例
	 */
	@Override
	public List<ReportResourceInstance> getInstanceByResource(String resourceId,long domainId,int startNum,int pageSize,String content,ILoginUser user) {
		List<ReportResourceInstance> instances = new ArrayList<ReportResourceInstance>();
		
		if(resourceId.contains(",")){
			//有多个资源
			int index = 0;
			
			out : for(String singleId : resourceId.split(",")){
				try {
					List<ResourceInstance> instanceList = resourceInstanceService.getResourceInstanceByResourceId(singleId);
					if(instanceList == null || instanceList.size() <= 0){
						continue;
					}
					List<Long> mainResourceIntanceIds = new ArrayList<Long>();
					Set<Long> mainResourceIntanceIdsSet = new HashSet<Long>();
					for(ResourceInstance instance : instanceList){
						//找出所有主资源实例
						mainResourceIntanceIdsSet.add(instance.getParentId());
					}
					mainResourceIntanceIds = new ArrayList<Long>(mainResourceIntanceIdsSet);
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					List<Long> domainIds = new ArrayList<Long>();
					domainIds.add(domainId);
					queryBo.setDomainIds(domainIds);
					List<Long> accessMainResourceIntancesIds = stm_system_resourceApi.accessFilter(queryBo, mainResourceIntanceIds);
					
					ResourceDef def = capacityService.getResourceDefById(singleId);
					if(def == null){
						if(logger.isErrorEnabled()){
							logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = " + singleId);
						}
						continue;
					}
					
					for(int i = 0 ; i < instanceList.size() ; i++){
						
						if(instances.size() >= pageSize){
							break out;
						}
						
						ResourceInstance instance = instanceList.get(i);
						
						//判断该子资源是否属于用户
						if(!accessMainResourceIntancesIds.contains(instance.getParentId())){
							continue;
						}
						//判断实例是否监控和是否属于当前用户域
						if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
							continue;
						}
						
						ReportResourceInstance reportInstance = new ReportResourceInstance();
						reportInstance.setId(instance.getId());
						reportInstance.setResourceId(singleId);
						reportInstance.setShowName(instance.getShowName());
						if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
							reportInstance.setShowName(instance.getName());
						}

						reportInstance.setResourceName(def.getName());
						
						reportInstance.setDiscoverIP(resourceInstanceService.getResourceInstance(instance.getParentId()).getShowIP());
						
						if(content != null && !content.trim().equals("")){
							if(!reportInstance.getShowName().toLowerCase().contains(content.toLowerCase()) && 
									(reportInstance.getDiscoverIP()!= null && !reportInstance.getDiscoverIP().toLowerCase().contains(content.toLowerCase())) && 
									!reportInstance.getResourceName().toLowerCase().contains(content.toLowerCase())){
								continue;
							}
						}
						
						if(index < startNum){
							index++;
							continue;
						}
						
						instances.add(reportInstance);
					}
				} catch (InstancelibException e) {
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
				}
			}
		}else{
			//一个资源
			try {
				ResourceDef resourceDef = capacityService.getResourceDefById(resourceId);
				if(resourceDef.getParentResourceDef() != null){
					//子资源
					List<ResourceInstance> instanceList = resourceInstanceService.getResourceInstanceByResourceId(resourceId);
					if(instanceList == null || instanceList.size() <= 0){
						return instances;
					}
					List<Long> mainResourceIntanceIds = new ArrayList<Long>();
					Set<Long> mainResourceIntanceIdsSet = new HashSet<Long>();
					
					int index = 0;
					
					for(ResourceInstance instance : instanceList){
						//找出所有主资源实例
						mainResourceIntanceIdsSet.add(instance.getParentId());
					}
					mainResourceIntanceIds = new ArrayList<Long>(mainResourceIntanceIdsSet);
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					List<Long> domainIds = new ArrayList<Long>();
					domainIds.add(domainId);
					queryBo.setDomainIds(domainIds);
					List<Long> accessMainResourceIntancesIds = stm_system_resourceApi.accessFilter(queryBo, mainResourceIntanceIds);
					if(instanceList != null && instanceList.size() >0){
						for(ResourceInstance instance : instanceList){
							
							if(instances.size() >= pageSize){
								break;
							}
							
							//判断该子资源是否属于用户
							if(!accessMainResourceIntancesIds.contains(instance.getParentId())){
								continue;
							}
							//判断实例是否监控和是否属于当前用户域
							if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED || domainId != instance.getDomainId()){
								continue;
							}
							ReportResourceInstance reportInstance = new ReportResourceInstance();
							BeanUtils.copyProperties(instance, reportInstance);
							if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
								reportInstance.setShowName(instance.getName());
							}
							
							ResourceDef def = capacityService.getResourceDefById(reportInstance.getResourceId());
							if(def == null){
								if(logger.isErrorEnabled()){
									logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = " + reportInstance.getResourceId());
								}
								continue;
							}
							reportInstance.setResourceName(def.getName());
							
							if(content != null && !content.trim().equals("")){
								if(!reportInstance.getShowName().toLowerCase().contains(content.toLowerCase()) && 
										!reportInstance.getDiscoverIP().toLowerCase().contains(content.toLowerCase()) && 
										!reportInstance.getResourceName().toLowerCase().contains(content.toLowerCase())){
									continue;
								}
							}
							
							if(instance.getParentId() > 0){
								reportInstance.setDiscoverIP(resourceInstanceService.getResourceInstance(instance.getParentId()).getShowIP());
							}
							
							if(index < startNum){
								index++;
								continue;
							}
							
							instances.add(reportInstance);
						}
					}
				}else{
					//主资源
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					List<Long> domainIds = new ArrayList<Long>();
					domainIds.add(domainId);
					queryBo.setModuleId(resourceId);
					queryBo.setDomainIds(domainIds);
					List<ResourceInstanceBo> instanceList = stm_system_resourceApi.getResources(queryBo);
					if(instanceList != null && instanceList.size() >0){
						
						int index = 0;
						
						for(ResourceInstanceBo instance : instanceList){
							
							if(instances.size() >= pageSize){
								break;
							}
							
							//判断实例是否监控和是否属于当前用户域
							if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
								continue;
							}
							ReportResourceInstance reportInstance = new ReportResourceInstance();
							BeanUtils.copyProperties(instance, reportInstance);
							if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
								reportInstance.setShowName(instance.getName());
							}
							ResourceDef def = capacityService.getResourceDefById(reportInstance.getResourceId());
							if(def == null){
								if(logger.isErrorEnabled()){
									logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = " + reportInstance.getResourceId());
								}
								continue;
							}
							reportInstance.setResourceName(def.getName());
							
							if(content != null && !content.trim().equals("")){
								if(!reportInstance.getShowName().toLowerCase().contains(content.toLowerCase()) && 
										!reportInstance.getDiscoverIP().toLowerCase().contains(content.toLowerCase()) && 
										!reportInstance.getResourceName().toLowerCase().contains(content.toLowerCase())){
									continue;
								}
							}
							
							if(instances.size() >= pageSize){
								break;
							}
							
							if(index < startNum){
								index++;
								continue;
							}
							
							instances.add(reportInstance);
						}
					}
				}
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
		}
		
		return instances;
	}

	/**
	 * 根据资源类别获取子资源
	 */
	@Override
	public List<ResourceCategoryTree> getChildResourceByResourceCategory(String categoryId) {
		Map<String, ResourceCategoryTree> childResourceMap = new HashMap<String, ResourceCategoryTree>();
		
		CategoryDef def = capacityService.getCategoryById(categoryId);
		
		this.loadChildResource(def,childResourceMap);
		
		List<ResourceCategoryTree> childResourceList = new ArrayList<ResourceCategoryTree>(childResourceMap.values());
		return childResourceList;
	}

	/**
	 * 过滤资源实例
	 */
	@Override
	public List<ReportResourceInstance> filterResourceInstanceList(List<Long> instanceIds,String content,int startNum,int pageSize) {
		List<ReportResourceInstance> childInstances = new ArrayList<ReportResourceInstance>();
		
		int index = 0;
		
		for(long instanceId : instanceIds){
			
			if(childInstances.size() >= pageSize){
				break;
			}
			
			ResourceInstanceBo instance = stm_system_resourceApi.getResource(instanceId);
			ReportResourceInstance reportInstance = new ReportResourceInstance();
			BeanUtils.copyProperties(instance, reportInstance);
			if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
				reportInstance.setShowName(instance.getName());
			}
			ResourceDef def = capacityService.getResourceDefById(reportInstance.getResourceId());
			if(def == null){
				if(logger.isErrorEnabled()){
					logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = " + reportInstance.getResourceId());
				}
				continue;
			}
			reportInstance.setResourceName(def.getName());
			if(reportInstance.getShowName().toLowerCase().contains(content.toLowerCase()) || reportInstance.getDiscoverIP().toLowerCase().contains(content.toLowerCase()) || reportInstance.getResourceName().toLowerCase().contains(content.toLowerCase())){
				
				if(index < startNum){
					index++;
					continue;
				}
				
				childInstances.add(reportInstance);
			}
			
		}
		return childInstances;
	}

	/**
	 * 获取所有资源类别
	 */
	@Override
	public List<ResourceCategoryTree> getAllResourceCategory() {
		List<ResourceCategoryTree> category = new ArrayList<ResourceCategoryTree>();
		
		this.loadResourceCategory(category,capacityService.getRootCategory());
		return category;
	}
	
	private void loadResourceCategory(List<ResourceCategoryTree> resourceCategory,CategoryDef def){
		if(!def.isDisplay() && !def.getId().equals(CapacityConst.VM)){
			return;
		}
		if(!licenseCapacityCategory.isAllowCategory(def.getId())){
			return;
		}
		ResourceCategoryTree category = new ResourceCategoryTree();
		category.setId(def.getId());
		category.setName(def.getName());
		if (null != def.getParentCategory()) {
			category.setPid(def.getParentCategory().getId());
			category.setState("closed");
			category.setType(1);
			resourceCategory.add(category);
		}
		// 判断是否还有child category
		if (null != def.getChildCategorys()) {
			CategoryDef[] categoryDefs = def.getChildCategorys();
			for (int i = 0; i < categoryDefs.length; i++) {
				if(categoryDefs[i].isDisplay() || categoryDefs[i].getId().equals(CapacityConst.VM))
					loadResourceCategory(resourceCategory,categoryDefs[i]);
			}
		}else{
			if(null != def.getResourceDefs()){
				ResourceDef[] resourceDefs = def.getResourceDefs();
				for (int i = 0; i < resourceDefs.length; i++) {
					ResourceDef resourceDef = resourceDefs[i];
					ResourceCategoryTree resource = new ResourceCategoryTree();
					resource.setId(resourceDef.getId());
					resource.setName(resourceDef.getName());
					resource.setType(2);
					resource.setPid(def.getId());
					resourceCategory.add(resource);
				}
			}
		}
	}
	
	private void loadChildResource(CategoryDef def,Map<String, ResourceCategoryTree> childResourceMap){
		if(null != def.getChildCategorys()){
			for(CategoryDef childDef : def.getChildCategorys()){
				loadChildResource(childDef,childResourceMap);
			}
		}else{
			ResourceDef[] mainDefs = def.getResourceDefs();
			if(mainDefs == null){
				if(logger.isErrorEnabled()){
					logger.error("Get main resource error,categoryId = " + def.getId());
				}
				return;
			}
			
			for(ResourceDef parentResource : mainDefs){
				
				ResourceDef[] childDefs = parentResource.getChildResourceDefs();
				if(childDefs == null){
					if(logger.isErrorEnabled()){
						logger.error("ParentResource.getChildResourceDefs() is null,resourceId = " + parentResource.getId());
					}
					continue;
				}
				for(ResourceDef childResource : childDefs){
					if(!childResourceMap.containsKey(childResource.getName())){
						ResourceCategoryTree resultDef = new ResourceCategoryTree();
						resultDef.setId(childResource.getId());
						resultDef.setName(childResource.getName());
						childResourceMap.put(childResource.getName(), resultDef);
					}else{
						String id = childResourceMap.get(childResource.getName()).getId();
						id += "," + childResource.getId();
						childResourceMap.get(childResource.getName()).setId(id);
					}
				}
				
			}
		}
	}

	@Override
	public List<ResourceCategoryTree> getChildResourceByMainResource(String resourceId) {
		List<ResourceCategoryTree> childResourceList = new ArrayList<ResourceCategoryTree>();
		ResourceDef mainDef = capacityService.getResourceDefById(resourceId);
		if(mainDef == null){
			if(logger.isErrorEnabled()){
				logger.error("Get main resource error,resourceId = " + resourceId);
			}
			return childResourceList;
		}
		
			
		ResourceDef[] childDefs = mainDef.getChildResourceDefs();
		
		if(childDefs == null || childDefs.length <= 0){
			if(logger.isErrorEnabled()){
				logger.error("Get child resource error,MainResourceId = " + resourceId);
			}
			return childResourceList;
		}
		
		for(ResourceDef childResource : childDefs){
			ResourceCategoryTree resultDef = new ResourceCategoryTree();
			resultDef.setId(childResource.getId());
			resultDef.setName(childResource.getName());
			childResourceList.add(resultDef);
		}
			
		return childResourceList;
	}

	/**
	 * 根据资源ID查询指标列表
	 */
	@Override
	public List<ReportResourceMetric> getMetricListByResource(List<String> resourceIdList,long instanceId,int reportType,int comprehensiveType) {
		Map<String, List<ReportResourceMetric>> metricMap = new HashMap<String, List<ReportResourceMetric>>();
		
		for(String resourceId : resourceIdList){
			ResourceDef def = capacityService.getResourceDefById(resourceId);
			if(def == null){
				if(logger.isErrorEnabled()){
					logger.error("CapacityService.getResourceDefById is null,resourceId : " + resourceId);
				}
				continue;
			}
			Map<Long, ReportResourceMetric> performanceMetricMap = new TreeMap<Long, ReportResourceMetric>();
			for(ResourceMetricDef metricDef : def.getMetricDefs()){
				if(metricDef.getMetricType() == MetricTypeEnum.PerformanceMetric){
					ReportResourceMetric metric = new ReportResourceMetric();
					BeanUtils.copyProperties(metricDef, metric);
					metric.setMetricSort(-1);
					if(reportType == ReportTypeEnum.ANALYSIS_REPORT.getIndex() || (reportType == ReportTypeEnum.COMPREHENSIVE_REPORT.getIndex() && comprehensiveType == ReportTypeEnum.ANALYSIS_REPORT.getIndex())){
						//分析报告
						ProfileMetric profileMetric = null;
						try {
							profileMetric = profileService.getMetricByInstanceIdAndMetricId(instanceId, metricDef.getId());
						} catch (ProfilelibException e) {
							if(logger.isErrorEnabled()){
								logger.error(e.getMessage(),e);
							}
						}
						if(profileMetric == null){
							if(logger.isErrorEnabled()){
								logger.error("Get profileMetric error,instanceid : " + instanceId + ",metricId : " + metricDef.getId());
							}
							continue;
						}
						//找到正常状态最大边界
						List<ProfileThreshold> thresholdList = profileMetric.getMetricThresholds();
						for(ProfileThreshold threshlod : thresholdList){
							if(threshlod.getPerfMetricStateEnum() == PerfMetricStateEnum.Minor){
								metric.setMetricExpectValue(threshlod.getThresholdValue());
							}
						}
					}
					if(metricDef.getUnit() != null && !metricDef.getUnit().trim().equals("")){
						metric.setName(metric.getName() + "(" + metricDef.getUnit() + ")");
						metric.setUnit(metricDef.getUnit());
					}
					performanceMetricMap.put(Long.parseLong(metricDef.getDisplayOrder()), metric);
				}
			}
			metricMap.put(resourceId, new ArrayList<ReportResourceMetric>(performanceMetricMap.values()));
		}
		
		//取所有集合的交集
		List<ReportResourceMetric> metricList = new ArrayList<ReportResourceMetric>();
		
		boolean isFirst = true;
		
		for(String resourceId : metricMap.keySet()){
			
			if(isFirst){
				metricList = metricMap.get(resourceId);
				isFirst = false;
				continue;
			}
			
			List<ReportResourceMetric> metricLists = metricMap.get(resourceId);
			metricList.retainAll(metricLists);
			
		}
		return metricList;
	}
	
	/**
	 * 根据资源ID查询指标列表
	 */
	@Override
	public List<ReportResourceMetric> getMetricListByInstance(String[] resourceIdList,long instanceId,int reportType,int comprehensiveType) {
		
		Set<String> resourceList = new HashSet<String>();
		
		for(String resourceInstanceId : resourceIdList){
			try {
				if(resourceInstanceService.getResourceInstance(Long.parseLong(resourceInstanceId)) != null){
					resourceList.add(resourceInstanceService.getResourceInstance(Long.parseLong(resourceInstanceId)).getResourceId());
				}
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
			}
		}
		
		List<String> ids = new ArrayList<String>(resourceList);
		
		return getMetricListByResource(ids, instanceId, reportType, comprehensiveType);
	}
	
	private long addPerformanceReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		
		
		long newTemplateId = -1;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//先添加报表模板基本信息
		long templateId = ReportTemplateSeq.next();
		
		//添加报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(!addPerformanceSubReportTemplate(reportTemplateDirectory,templateId,rmm)){
				if(logger.isDebugEnabled()){
					logger.debug("addPerformanceSubReportTemplate() error...");
				}
				return newTemplateId;
			}
		}
		
		long modelId = rmm.writeAndComplieJrxmlFile();
		
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateId(templateId);
		templatePo.setReportTemplateCreateTime(new Date());
		templatePo.setReportTemplateCreateUserId(user.getId());
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateId(templateId);
		if(iReportTemplateDao.insert(templatePo) != 1){
			if(logger.isDebugEnabled()){
				logger.debug("IReportTemplateDao.insert(templatePo) error...");
			}
			return newTemplateId;
		}else{
			newTemplateId = templateId;
		}
		
		if(reportTemplate.getReportTemplateStatus() == 0){
			//开始执行job
			this.startJob(reportTemplate);
		}
		return newTemplateId;
		
	}
	
	private boolean addPerformanceSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		
		long templateDirectoryId = ReportTemplateDirectorySeq.next();
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
		directoryPo.setReportTemplateId(templateId);
		if(iReportTemplateDirectoryDao.insert(directoryPo) != 1){
			if(logger.isDebugEnabled()){
				logger.debug("IReportTemplateDirectoryDao.insert(directoryPo) error...");
			}
			return false;
		}
		if(!addPerformanceInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
		
	}
	
	private long addAlarmReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		long newTemplateId = -1;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		rmm.addTitleReport();
		//先添加报表模板基本信息
		long templateId = ReportTemplateSeq.next();
		
		//添加报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(!addAlarmSubReportTemplate(reportTemplateDirectory,templateId,rmm)){
				return newTemplateId;
			}
		}
		long modelId = rmm.writeAndComplieJrxmlFile();
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateId(templateId);
		templatePo.setReportTemplateCreateTime(new Date());
		templatePo.setReportTemplateCreateUserId(user.getId());
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateId(templateId);
		if(iReportTemplateDao.insert(templatePo) != 1){
			return newTemplateId;
		}else{
			newTemplateId = templateId;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//开始执行job
			this.startJob(reportTemplate);
		}
		return newTemplateId;
		
	}
	
	private boolean addAlarmSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		rmm.addTitleReport();
		
		long templateDirectoryId = ReportTemplateDirectorySeq.next();
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
		directoryPo.setReportTemplateId(templateId);
		if(iReportTemplateDirectoryDao.insert(directoryPo) != 1){
			return false;
		}
		
		if(!addAlarmInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	//添加TOPN模板
	private long addTopnReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		long newTemplateId = -1;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//先添加报表模板基本信息
		long templateId = ReportTemplateSeq.next();
		
		//添加报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(!addTopnSubReportTemplate(reportTemplateDirectory,templateId,rmm)){
				return newTemplateId;
			}
		}
		
		long modelId = rmm.writeAndComplieJrxmlFile();
		
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateId(templateId);
		templatePo.setReportTemplateCreateTime(new Date());
		templatePo.setReportTemplateCreateUserId(user.getId());
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateId(templateId);
		if(iReportTemplateDao.insert(templatePo) != 1){
			return newTemplateId;
		}else{
			newTemplateId = templateId;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//开始执行job
			this.startJob(reportTemplate);
		}
		return newTemplateId;
		
	}
	
	private boolean addTopnSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		long templateDirectoryId = ReportTemplateDirectorySeq.next();
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
		directoryPo.setReportTemplateId(templateId);
		if(iReportTemplateDirectoryDao.insert(directoryPo) != 1){
			return false;
		}
		
		if(!addTopnInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	//添加可用性报告
	private long addAvailabilityReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		long newTemplateId = -1;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//先添加报表模板基本信息
		long templateId = ReportTemplateSeq.next();
		
		//添加报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(!addAvailabilitySubReportTemplate(reportTemplateDirectory,templateId,rmm)){
				return newTemplateId;
			}
		}
		
		long modelId = rmm.writeAndComplieJrxmlFile();
		
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateId(templateId);
		templatePo.setReportTemplateCreateTime(new Date());
		templatePo.setReportTemplateCreateUserId(user.getId());
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateId(templateId);
		if(iReportTemplateDao.insert(templatePo) != 1){
			return newTemplateId;
		}else{
			newTemplateId = templateId;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//开始执行job
			this.startJob(reportTemplate);
		}
		return newTemplateId;
		
	
	}
	
	private boolean addAvailabilitySubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		long templateDirectoryId = ReportTemplateDirectorySeq.next();
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
		directoryPo.setReportTemplateId(templateId);
		if(iReportTemplateDirectoryDao.insert(directoryPo) != 1){
			return false;
		}
		
		if(!addAvailabilityInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	//添加趋势报告
	private long addTrendReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		long newTemplateId = -1;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//先添加报表模板基本信息
		long templateId = ReportTemplateSeq.next();
		
		//添加报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(!addTrendSubReportTemplate(reportTemplateDirectory,templateId,rmm)){
				return newTemplateId;
			}
		}
		long modelId = rmm.writeAndComplieJrxmlFile();
		
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateId(templateId);
		templatePo.setReportTemplateCreateTime(new Date());
		templatePo.setReportTemplateCreateUserId(user.getId());
		templatePo.setReportTemplateModelName(modelId + "");
		if(templatePo.getReportTemplateCycle() == 1){
			//日报
			templatePo.setReportTemplateBeginTime("0");
			templatePo.setReportTemplateEndTime("24");
		}else if(templatePo.getReportTemplateCycle() == 2){
			//周报
			templatePo.setReportTemplateBeginTime("1,2,3,4,5,6,7");
		}else if(templatePo.getReportTemplateCycle() == 3){
			//月报
			templatePo.setReportTemplateBeginTime("1");
			templatePo.setReportTemplateEndTime("-1");
		}
		reportTemplate.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateId(templateId);
		if(iReportTemplateDao.insert(templatePo) != 1){
			return newTemplateId;
		}else{
			newTemplateId = templateId;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//开始执行job
			this.startJob(reportTemplate);
		}
		return newTemplateId;
		
	}
	
	private boolean addTrendSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		long templateDirectoryId = ReportTemplateDirectorySeq.next();
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
		directoryPo.setReportTemplateId(templateId);
		if(iReportTemplateDirectoryDao.insert(directoryPo) != 1){
			return false;
		}
		
		if(!addTrendInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	//添加分析模板
	private long addAnalysisReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		long newTemplateId = -1;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//先添加报表模板基本信息
		long templateId = ReportTemplateSeq.next();
		
		//添加报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(!addAnalysisSubReportTemplate(reportTemplateDirectory,templateId,rmm)){
				return newTemplateId;
			}
		}
		
		long modelId = rmm.writeAndComplieJrxmlFile();
		
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateId(templateId);
		templatePo.setReportTemplateCreateTime(new Date());
		templatePo.setReportTemplateCreateUserId(user.getId());
		templatePo.setReportTemplateModelName(modelId + "");
		if(templatePo.getReportTemplateCycle() == 1){
			//日报
			templatePo.setReportTemplateBeginTime("0");
			templatePo.setReportTemplateEndTime("24");
		}else if(templatePo.getReportTemplateCycle() == 2){
			//周报
			templatePo.setReportTemplateBeginTime("1,2,3,4,5,6,7");
		}else if(templatePo.getReportTemplateCycle() == 3){
			//月报
			templatePo.setReportTemplateBeginTime("1");
			templatePo.setReportTemplateEndTime("-1");
		}
		reportTemplate.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateId(templateId);
		if(iReportTemplateDao.insert(templatePo) != 1){
			return newTemplateId;
		}else{
			newTemplateId = templateId;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//开始执行job
			this.startJob(reportTemplate);
		}
		return newTemplateId;
		
	}
	
	private boolean addAnalysisSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){

		
		long templateDirectoryId = ReportTemplateDirectorySeq.next();
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
		directoryPo.setReportTemplateId(templateId);
		if(iReportTemplateDirectoryDao.insert(directoryPo) != 1){
			return false;
		}
		
		if(!addAnalysisInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
		
	}
	
	private long addBusinessReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		
		long newTemplateId = -1;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//先添加报表模板基本信息
		long templateId = ReportTemplateSeq.next();
		
		//添加报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(!addBusinessSubReportTemplate(reportTemplateDirectory,templateId,rmm)){
				if(logger.isDebugEnabled()){
					logger.debug("addBusinessSubReportTemplate() error...");
				}
				return newTemplateId;
			}
		}
		
		long modelId = rmm.writeAndComplieJrxmlFile();
		
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateId(templateId);
		templatePo.setReportTemplateCreateTime(new Date());
		templatePo.setReportTemplateCreateUserId(user.getId());
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateId(templateId);
		if(iReportTemplateDao.insert(templatePo) != 1){
			if(logger.isDebugEnabled()){
				logger.debug("IReportTemplateDao.insert(templatePo) error...");
			}
			return newTemplateId;
		}else{
			newTemplateId = templateId;
		}
		
		if(reportTemplate.getReportTemplateStatus() == 0){
			//开始执行job
			this.startJob(reportTemplate);
		}
		return newTemplateId;
		
	}
	
	private boolean addBusinessSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){

		long templateDirectoryId = ReportTemplateDirectorySeq.next();
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
		directoryPo.setReportTemplateId(templateId);
		if(iReportTemplateDirectoryDao.insert(directoryPo) != 1){
			if(logger.isDebugEnabled()){
				logger.debug("IReportTemplateDirectoryDao.insert(directoryPo) error...");
			}
			return false;
		}
		if(!addBusinessInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
		
	}
	
	//增加性能报表(虚拟化)
	private long addVMPerformanceReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		
		long newTemplateId = -1;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//先添加报表模板基本信息
		long templateId = ReportTemplateSeq.next();
		
		//添加报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(!addVMPerformanceSubReportTemplate(reportTemplateDirectory,templateId,rmm)){
				if(logger.isDebugEnabled()){
					logger.debug("addPerformanceSubReportTemplate() error...");
				}
				return newTemplateId;
			}
		}
		
		long modelId = rmm.writeAndComplieJrxmlFile();
		
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateId(templateId);
		templatePo.setReportTemplateCreateTime(new Date());
		templatePo.setReportTemplateCreateUserId(user.getId());
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateId(templateId);
		if(iReportTemplateDao.insert(templatePo) != 1){
			if(logger.isDebugEnabled()){
				logger.debug("IReportTemplateDao.insert(templatePo) error...");
			}
			return newTemplateId;
		}else{
			newTemplateId = templateId;
		}
		
		if(reportTemplate.getReportTemplateStatus() == 0){
			//开始执行job
			this.startJob(reportTemplate);
		}
		return newTemplateId;
		
	}
	
	private boolean addVMPerformanceSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		
		long templateDirectoryId = ReportTemplateDirectorySeq.next();
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
		directoryPo.setReportTemplateId(templateId);
		if(iReportTemplateDirectoryDao.insert(directoryPo) != 1){
			if(logger.isDebugEnabled()){
				logger.debug("IReportTemplateDirectoryDao.insert(directoryPo) error...");
			}
			return false;
		}
		if(!addVMPerformanceInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
		
	}
	
	private boolean addVMPerformanceInstanceAndMetricRelation(ReportTemplateDirectory reportTemplateDirectory,long templateDirectoryId,ReportModelMain rmm){
		String resourceId = "";
		
		//添加报表目录实例关系
		List<ReportTemplateDirectoryInstance> directoryInstanceList = reportTemplateDirectory.getDirectoryInstanceList();
		for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
			ResourceInstanceBo instance = stm_system_resourceApi.getResource(directoryInstance.getInstanceId());
			directoryInstance.setInstanceIP(instance.getDiscoverIP());
			if(instance.getShowName() == null || instance.getShowName().equals("")){
				directoryInstance.setInstanceName(instance.getName());
			}else{
				directoryInstance.setInstanceName(instance.getShowName());
			}
			directoryInstance.setInstanceType(reportTemplateDirectory.getReportTemplateDirectoryResource());
			resourceId = instance.getResourceId();
			ReportTemplateDirectoryInstancePo directoryInstancePo = new ReportTemplateDirectoryInstancePo();
			BeanUtils.copyProperties(directoryInstance, directoryInstancePo);
			directoryInstancePo.setReportDirectoryInstanceId(ReportTemplateDirectoryInstanceSeq.next());
			directoryInstancePo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) != 1){
				if(logger.isDebugEnabled()){
					logger.debug("IReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) error...");
				}
				return false;
			}
		}
		List<ReportTemplateDirectoryMetric> directoryMetricList = reportTemplateDirectory.getDirectoryMetricList();
		rmm.addTitleReport();
		List<Columns> columnsList = new ArrayList<Columns>();
		ColumnsTitle columnsTitle = new ColumnsTitle();
		
		List<Columns> columnsDetailList = new ArrayList<Columns>();
		ColumnsTitle columnsDetailTitle = new ColumnsTitle();
		columnsDetailList.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
		
		for(int i = 0 ; i < 4 ; i ++){
			Columns columns = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
			columnsList.add(columns);
		}
		
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			ResourceMetricDef def = capacityService.getResourceMetricDef(resourceId,directoryMetric.getMetricId());
			if(def != null){
				directoryMetric.setMetricName(def.getName() + "(" + def.getUnit() + ")");
				directoryMetric.setMetricType(def.getMetricType());
			}else{
				if(logger.isErrorEnabled()){
					logger.error("ResourceMetricDef is null ,resourceId = " + resourceId + ",MetricId = " + directoryMetric.getMetricId());
				}
			}
			Columns columns = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
			columns.setApart("3");
			columnsDetailList.add(columns);
			columnsList.add(columns);
		}
		
		columnsDetailList.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
		columnsDetailTitle.setColumns(columnsDetailList);
		
		columnsTitle.setColumns(columnsList);
		rmm.addTitleReport();
		rmm.addTableReport(columnsTitle);
		
		//添加报表目录指标关系
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			rmm.addStackedBarReport(directoryInstanceList.size());
			
			ReportTemplateDirectoryMetricPo directoryMetricPo = new ReportTemplateDirectoryMetricPo();
			BeanUtils.copyProperties(directoryMetric, directoryMetricPo);
			directoryMetricPo.setReportDirectoryMetricId(ReportTemplateDirectoryMetricSeq.next());
			directoryMetricPo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryMetricDao.insert(directoryMetricPo) != 1){
				if(logger.isDebugEnabled()){
					logger.debug("IReportTemplateDirectoryMetricDao.insert(directoryMetricPo) error...");
				}
				return false;
			}
		}
		
		if(reportTemplateDirectory.getReportTemplateDirectoryIsDetail() == 0){
			//需要详情
			rmm.addTitleReport();
			for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
				rmm.addTableReport(columnsDetailTitle);
			}
			for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
				rmm.addLineReport(directoryInstanceList.size());
			}
		}
		return true;
	}
	
	//添加告警报表(虚拟化)
	private long addVMAlarmReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		long newTemplateId = -1;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		rmm.addTitleReport();
		//先添加报表模板基本信息
		long templateId = ReportTemplateSeq.next();
		
		//添加报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(!addVMAlarmSubReportTemplate(reportTemplateDirectory,templateId,rmm)){
				return newTemplateId;
			}
		}
		long modelId = rmm.writeAndComplieJrxmlFile();
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateId(templateId);
		templatePo.setReportTemplateCreateTime(new Date());
		templatePo.setReportTemplateCreateUserId(user.getId());
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateId(templateId);
		if(iReportTemplateDao.insert(templatePo) != 1){
			return newTemplateId;
		}else{
			newTemplateId = templateId;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//开始执行job
			this.startJob(reportTemplate);
		}
		return newTemplateId;
		
	}
	
	private boolean addVMAlarmSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		rmm.addTitleReport();
		
		long templateDirectoryId = ReportTemplateDirectorySeq.next();
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
		directoryPo.setReportTemplateId(templateId);
		if(iReportTemplateDirectoryDao.insert(directoryPo) != 1){
			return false;
		}
		
		if(!addVMAlarmInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	private boolean addVMAlarmInstanceAndMetricRelation(ReportTemplateDirectory reportTemplateDirectory,long templateDirectoryId,ReportModelMain rmm){
		String resourceId = "";
		
		//添加报表目录实例关系
		List<ReportTemplateDirectoryInstance> directoryInstanceList = reportTemplateDirectory.getDirectoryInstanceList();
		for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
			ResourceInstanceBo instance = stm_system_resourceApi.getResource(directoryInstance.getInstanceId());
			directoryInstance.setInstanceIP(instance.getDiscoverIP());
			if(instance.getShowName() == null || instance.getShowName().equals("")){
				directoryInstance.setInstanceName(instance.getName());
			}else{
				directoryInstance.setInstanceName(instance.getShowName());
			}
			directoryInstance.setInstanceType(reportTemplateDirectory.getReportTemplateDirectoryResource());
			resourceId = instance.getResourceId();
			
			ReportTemplateDirectoryInstancePo directoryInstancePo = new ReportTemplateDirectoryInstancePo();
			BeanUtils.copyProperties(directoryInstance, directoryInstancePo);
			directoryInstancePo.setReportDirectoryInstanceId(ReportTemplateDirectoryInstanceSeq.next());
			directoryInstancePo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) != 1){
				return false;
			}
		}
		List<ReportTemplateDirectoryMetric> directoryMetricList = reportTemplateDirectory.getDirectoryMetricList();
		
		List<Columns> columnsList = new ArrayList<Columns>();
		ColumnsTitle columnsTitle = new ColumnsTitle();
		
		for(int i = 0 ; i < 4 ; i ++){
			columnsList.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
		}
		
		//告警数量
		Columns columns1 = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
		columns1.setApart("1");
		columnsList.add(columns1);
		
		//级别分布
		Columns columns2 = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
		columns2.setApart("3");
		columnsList.add(columns2);
		
		//状态分布
		//告警数量
		Columns columns3 = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
		columns3.setApart("2");
		columnsList.add(columns3);
		
		//添加报表目录指标关系
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			
			//从固定指标中取值
			DefaultAlarmMetric defaultMetric = new DefaultAlarmMetric();
			directoryMetric.setMetricName(defaultMetric.getNameById(directoryMetric.getMetricId()));
			
			ReportTemplateDirectoryMetricPo directoryMetricPo = new ReportTemplateDirectoryMetricPo();
			BeanUtils.copyProperties(directoryMetric, directoryMetricPo);
			directoryMetricPo.setReportDirectoryMetricId(ReportTemplateDirectoryMetricSeq.next());
			directoryMetricPo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryMetricDao.insert(directoryMetricPo) != 1){
				return false;
			}
		}
		
		columnsTitle.setColumns(columnsList);
		rmm.addTitleReport();
		rmm.addTableReport(columnsTitle);

		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			
			//如果是状态分布,多加个图
			if(directoryMetric.getMetricId().equals("statusDistribution")){
				rmm.addPieReport();
			}
			rmm.addPieReport();
		}
		
		if(reportTemplateDirectory.getReportTemplateDirectoryIsDetail() == 0){
			//需要详情
			List<Columns> columnsList2 = new ArrayList<Columns>();
			ColumnsTitle columnsTitle2 = new ColumnsTitle();
			for(int i = 0 ; i < 5 ; i ++){
				columnsList2.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
			}
			columnsTitle2.setColumns(columnsList2);
			rmm.addTitleReport();
			for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
				rmm.addTableReport(columnsTitle2);
			}
		}
		return true;
	}
	
	//添加综合性报表
	private long addComprehensiveReportTemplate(ReportTemplate reportTemplate,ILoginUser user){

		long newTemplateId = -1;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//先添加报表模板基本信息
		long templateId = ReportTemplateSeq.next();
		
		//添加报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			
			ReportTypeEnum type = ReportTypeEnum.getReportTypeEnum(reportTemplateDirectory.getReportTemplateDirectoryType());
			
			switch (type) {
			case PERFORMANCE_REPORT:
				if(!addPerformanceSubReportTemplate(reportTemplateDirectory, templateId, rmm)){
					return newTemplateId;
				}
				break;
			case ALARM_REPORT:
				if(!addAlarmSubReportTemplate(reportTemplateDirectory, templateId, rmm)){
					return newTemplateId;
				}
				break;
			case TOPN_REPORT:
				if(!addTopnSubReportTemplate(reportTemplateDirectory, templateId, rmm)){
					return newTemplateId;
				}
				break;
			case AVAILABILITY_REPORT:
				if(!addAvailabilitySubReportTemplate(reportTemplateDirectory, templateId, rmm)){
					return newTemplateId;
				}
				break;
			case TREND_REPORT:
				if(!addTrendSubReportTemplate(reportTemplateDirectory,templateId,rmm)){
					return newTemplateId;
				}
				break;
			case ANALYSIS_REPORT:
				if(!addAnalysisSubReportTemplate(reportTemplateDirectory,templateId,rmm)){
					return newTemplateId;
				}
				break;
			default:
				break;
			}
			
		}
		long modelId = rmm.writeAndComplieJrxmlFile();
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateId(templateId);
		templatePo.setReportTemplateCreateTime(new Date());
		templatePo.setReportTemplateCreateUserId(user.getId());
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateId(templateId);
		if(iReportTemplateDao.insert(templatePo) != 1){
			return newTemplateId;
		}else{
			newTemplateId = templateId;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//开始执行job
			this.startJob(reportTemplate);
		}
		return newTemplateId;
	}
	
	private boolean updatePerformanceReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		
		
		boolean isSuccess = true;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//修改报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		List<Long> directoryIdList = new ArrayList<Long>();
		directoryIdList = iReportTemplateDirectoryDao.selectDirectoryIdByTemplateId(reportTemplate.getReportTemplateId());
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(reportTemplateDirectory.getReportTemplateDirectoryId() > 0){
				//存在的目录
				directoryIdList.remove(reportTemplateDirectory.getReportTemplateDirectoryId());
			}
			if(!updatePerformanceSubReportTemplate(reportTemplateDirectory,reportTemplate.getReportTemplateId(),rmm)){
				if(logger.isDebugEnabled()){
					logger.debug("addPerformanceSubReportTemplate() error...");
				}
				isSuccess = false;
				return isSuccess;
			}
		}
		if(!deleteTemplateDirectorys(directoryIdList)){
			isSuccess = false;
			return isSuccess;
		}
		long modelId = rmm.writeAndComplieJrxmlFile();
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		if(iReportTemplateDao.update(templatePo) != 1){
			if(logger.isDebugEnabled()){
				logger.debug("IReportTemplateDao.update(templatePo) error...");
			}
			isSuccess = false;
			return isSuccess;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//修改job
			this.updateJob(reportTemplate);
		}else{
			//停止job
			try {
				reportEngine.stopEngine(reportTemplate.getReportTemplateId());
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(),e);
			} catch (SchedulerException e) {
				logger.error(e.getMessage(),e);
			}
		}
		return isSuccess;
		
	}
	
	private boolean updatePerformanceSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){

		long templateDirectoryId = addOrUpdateDirectory(reportTemplateDirectory, templateId);
		
		if(templateDirectoryId <= 0){
			return false;
		}
		
		if(!addPerformanceInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	private boolean updateAlarmReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		boolean isSuccess = true;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		rmm.addTitleReport();
		
		//修改报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		List<Long> directoryIdList = new ArrayList<Long>();
		directoryIdList = iReportTemplateDirectoryDao.selectDirectoryIdByTemplateId(reportTemplate.getReportTemplateId());
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(reportTemplateDirectory.getReportTemplateDirectoryId() > 0){
				//存在的目录
				directoryIdList.remove(reportTemplateDirectory.getReportTemplateDirectoryId());
			}
			if(!updateAlarmSubReportTemplate(reportTemplateDirectory,reportTemplate.getReportTemplateId(),rmm)){
				isSuccess = false;
				return isSuccess;
			}
		}
		
		if(!deleteTemplateDirectorys(directoryIdList)){
			isSuccess = false;
			return isSuccess;
		}
		long modelId = rmm.writeAndComplieJrxmlFile();
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		if(iReportTemplateDao.update(templatePo) != 1){
			isSuccess = false;
			return isSuccess;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//修改job
			this.updateJob(reportTemplate);
		}
		return isSuccess;
		
	}
	
	private boolean updateAlarmSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		rmm.addTitleReport();
		
		long templateDirectoryId = addOrUpdateDirectory(reportTemplateDirectory, templateId);
		
		if(templateDirectoryId <= 0){
			return false;
		}
		
		if(!addAlarmInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	private boolean updateTopnReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		boolean isSuccess = true;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		//修改报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		List<Long> directoryIdList = new ArrayList<Long>();
		directoryIdList = iReportTemplateDirectoryDao.selectDirectoryIdByTemplateId(reportTemplate.getReportTemplateId());
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(reportTemplateDirectory.getReportTemplateDirectoryId() > 0){
				//存在的目录
				directoryIdList.remove(reportTemplateDirectory.getReportTemplateDirectoryId());
			}
			if(!updateTopnSubReportTemplate(reportTemplateDirectory,reportTemplate.getReportTemplateId(),rmm)){
				isSuccess = false;
				return isSuccess;
			}
		}
		
		if(!deleteTemplateDirectorys(directoryIdList)){
			isSuccess = false;
			return isSuccess;
		}
		long modelId = rmm.writeAndComplieJrxmlFile();
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		if(iReportTemplateDao.update(templatePo) != 1){
			isSuccess = false;
			return isSuccess;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//修改job
			this.updateJob(reportTemplate);
		}
		return isSuccess;
		
	}
	
	private boolean updateTopnSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		long templateDirectoryId = addOrUpdateDirectory(reportTemplateDirectory, templateId);
		
		if(templateDirectoryId <= 0){
			return false;
		}
		
		if(!addTopnInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	private boolean updateAvailabilityReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		boolean isSuccess = true;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//修改报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		List<Long> directoryIdList = new ArrayList<Long>();
		directoryIdList = iReportTemplateDirectoryDao.selectDirectoryIdByTemplateId(reportTemplate.getReportTemplateId());
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(reportTemplateDirectory.getReportTemplateDirectoryId() > 0){
				//存在的目录
				directoryIdList.remove(reportTemplateDirectory.getReportTemplateDirectoryId());
			}
			if(!updateAvailabilitySubReportTemplate(reportTemplateDirectory,reportTemplate.getReportTemplateId(),rmm)){
				isSuccess = false;
				return isSuccess;
			}
		}
		
		if(!deleteTemplateDirectorys(directoryIdList)){
			isSuccess = false;
			return isSuccess;
		}
		long modelId = rmm.writeAndComplieJrxmlFile();
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		if(iReportTemplateDao.update(templatePo) != 1){
			isSuccess = false;
			return isSuccess;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			
			//修改job
			this.updateJob(reportTemplate);
		}
		return isSuccess;
		
	
	}
	
	private boolean updateAvailabilitySubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		long templateDirectoryId = addOrUpdateDirectory(reportTemplateDirectory, templateId);
		
		if(templateDirectoryId <= 0){
			return false;
		}
		
		if(!addAvailabilityInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	private boolean updateTrendReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		boolean isSuccess = true;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//修改报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		List<Long> directoryIdList = new ArrayList<Long>();
		directoryIdList = iReportTemplateDirectoryDao.selectDirectoryIdByTemplateId(reportTemplate.getReportTemplateId());
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(reportTemplateDirectory.getReportTemplateDirectoryId() > 0){
				//存在的目录
				directoryIdList.remove(reportTemplateDirectory.getReportTemplateDirectoryId());
			}
			if(!updateTrendSubReportTemplate(reportTemplateDirectory,reportTemplate.getReportTemplateId(),rmm)){
				isSuccess = false;
				return isSuccess;
			}
		}
		
		if(!deleteTemplateDirectorys(directoryIdList)){
			isSuccess = false;
			return isSuccess;
		}
		long modelId = rmm.writeAndComplieJrxmlFile();
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateModelName(modelId + "");
		templatePo.setReportTemplateBeginTime("0");
		reportTemplate.setReportTemplateModelName(modelId + "");
		if(iReportTemplateDao.update(templatePo) != 1){
			isSuccess = false;
			return isSuccess;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//修改job
			this.updateJob(reportTemplate);
		}
		return isSuccess;
		
	}
	
	private boolean updateTrendSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		long templateDirectoryId = addOrUpdateDirectory(reportTemplateDirectory, templateId);
		
		if(templateDirectoryId <= 0){
			return false;
		}
		
		if(!addTrendInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	private boolean updateAnalysisReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		boolean isSuccess = true;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//修改报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		List<Long> directoryIdList = new ArrayList<Long>();
		directoryIdList = iReportTemplateDirectoryDao.selectDirectoryIdByTemplateId(reportTemplate.getReportTemplateId());
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(reportTemplateDirectory.getReportTemplateDirectoryId() > 0){
				//存在的目录
				directoryIdList.remove(reportTemplateDirectory.getReportTemplateDirectoryId());
			}
			if(!updateAnalysisSubReportTemplate(reportTemplateDirectory,reportTemplate.getReportTemplateId(),rmm)){
				isSuccess = false;
				return isSuccess;
			}
		}
		
		if(!deleteTemplateDirectorys(directoryIdList)){
			isSuccess = false;
			return isSuccess;
		}
		long modelId = rmm.writeAndComplieJrxmlFile();
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateBeginTime("0");
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		if(iReportTemplateDao.update(templatePo) != 1){
			isSuccess = false;
			return isSuccess;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//修改job
			this.updateJob(reportTemplate);
		}
		return isSuccess;
		
	}
	
	private boolean updateAnalysisSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){
		long templateDirectoryId = addOrUpdateDirectory(reportTemplateDirectory, templateId);
		
		if(templateDirectoryId <= 0){
			return false;
		}
		
		if(!addAnalysisInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	private boolean updateBusinessReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		
		
		boolean isSuccess = true;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//修改报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		List<Long> directoryIdList = new ArrayList<Long>();
		directoryIdList = iReportTemplateDirectoryDao.selectDirectoryIdByTemplateId(reportTemplate.getReportTemplateId());
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(reportTemplateDirectory.getReportTemplateDirectoryId() > 0){
				//存在的目录
				directoryIdList.remove(reportTemplateDirectory.getReportTemplateDirectoryId());
			}
			if(!updateBusinessSubReportTemplate(reportTemplateDirectory,reportTemplate.getReportTemplateId(),rmm)){
				if(logger.isDebugEnabled()){
					logger.debug("addPerformanceSubReportTemplate() error...");
				}
				isSuccess = false;
				return isSuccess;
			}
		}
		if(!deleteTemplateDirectorys(directoryIdList)){
			isSuccess = false;
			return isSuccess;
		}
		long modelId = rmm.writeAndComplieJrxmlFile();
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		if(iReportTemplateDao.update(templatePo) != 1){
			if(logger.isDebugEnabled()){
				logger.debug("IReportTemplateDao.update(templatePo) error...");
			}
			isSuccess = false;
			return isSuccess;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//修改job
			this.updateJob(reportTemplate);
		}
		return isSuccess;
		
	}
	
	private boolean updateBusinessSubReportTemplate(ReportTemplateDirectory reportTemplateDirectory,long templateId,ReportModelMain rmm){

		long templateDirectoryId = addOrUpdateDirectory(reportTemplateDirectory, templateId);
		
		if(templateDirectoryId <= 0){
			return false;
		}
		
		if(!addBusinessInstanceAndMetricRelation(reportTemplateDirectory, templateDirectoryId, rmm)){
			return false;
		}
		return true;
	
	}
	
	private boolean updateComprehensiveReportTemplate(ReportTemplate reportTemplate,ILoginUser user){
		boolean isSuccess = true;
		
		ReportModelMain rmm = new ReportModelMain(user.getId() + "", fileClient);
		
		rmm.addTitleReport();
		
		//修改报表目录
		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		List<Long> directoryIdList = new ArrayList<Long>();
		directoryIdList = iReportTemplateDirectoryDao.selectDirectoryIdByTemplateId(reportTemplate.getReportTemplateId());
		for(ReportTemplateDirectory reportTemplateDirectory : directoryList){
			if(reportTemplateDirectory.getReportTemplateDirectoryId() > 0){
				//存在的目录
				directoryIdList.remove(reportTemplateDirectory.getReportTemplateDirectoryId());
			}
			
			ReportTypeEnum type = ReportTypeEnum.getReportTypeEnum(reportTemplateDirectory.getReportTemplateDirectoryType());
			
			switch (type) {
			case PERFORMANCE_REPORT:
				if(!updatePerformanceSubReportTemplate(reportTemplateDirectory, reportTemplate.getReportTemplateId(), rmm)){
					isSuccess = false;
					return isSuccess;
				}
				break;
			case ALARM_REPORT:
				if(!updateAlarmSubReportTemplate(reportTemplateDirectory, reportTemplate.getReportTemplateId(), rmm)){
					isSuccess = false;
					return isSuccess;
				}
				break;
			case TOPN_REPORT:
				if(!updateTopnSubReportTemplate(reportTemplateDirectory, reportTemplate.getReportTemplateId(), rmm)){
					isSuccess = false;
					return isSuccess;
				}
				break;
			case AVAILABILITY_REPORT:
				if(!updateAvailabilitySubReportTemplate(reportTemplateDirectory, reportTemplate.getReportTemplateId(), rmm)){
					isSuccess = false;
					return isSuccess;
				}
				break;
			case TREND_REPORT:
				if(!updateTrendSubReportTemplate(reportTemplateDirectory,reportTemplate.getReportTemplateId(),rmm)){
					isSuccess = false;
					return isSuccess;
				}
				break;
			case ANALYSIS_REPORT:
				if(!updateAnalysisSubReportTemplate(reportTemplateDirectory,reportTemplate.getReportTemplateId(),rmm)){
					isSuccess = false;
					return isSuccess;
				}
				break;
			default:
				break;
			}
			
		}
		
		if(!deleteTemplateDirectorys(directoryIdList)){
			isSuccess = false;
			return isSuccess;
		}
		
		long modelId = rmm.writeAndComplieJrxmlFile();
		
		ReportTemplatePo templatePo = new ReportTemplatePo();
		BeanUtils.copyProperties(reportTemplate, templatePo);
		templatePo.setReportTemplateModelName(modelId + "");
		reportTemplate.setReportTemplateModelName(modelId + "");
		if(iReportTemplateDao.update(templatePo) != 1){
			isSuccess = false;
			return isSuccess;
		}
		if(reportTemplate.getReportTemplateStatus() == 0){
			//修改job
			this.updateJob(reportTemplate);
		}
		return isSuccess;
	}
	
	/**
	 * 修改报表模板
	 */
	@Override
	public boolean updateReportTemplate(ReportTemplate reportTemplate,ILoginUser user) {
		
		boolean isSuccess = false;
		
		ReportTypeEnum type = ReportTypeEnum.getReportTypeEnum(reportTemplate.getReportTemplateType());
		
		switch (type) {
			case PERFORMANCE_REPORT:
				isSuccess = updatePerformanceReportTemplate(reportTemplate,user);
				break;
			case ALARM_REPORT:
				isSuccess = updateAlarmReportTemplate(reportTemplate,user);
				break;
			case TOPN_REPORT:
				isSuccess = updateTopnReportTemplate(reportTemplate,user);
				break;
			case AVAILABILITY_REPORT:
				isSuccess = updateAvailabilityReportTemplate(reportTemplate,user);
				break;
			case TREND_REPORT:
				isSuccess = updateTrendReportTemplate(reportTemplate,user);
				break;
			case ANALYSIS_REPORT:
				isSuccess = updateAnalysisReportTemplate(reportTemplate, user);
				break;
			case BUSINESS_REPORT:
				isSuccess = updateBusinessReportTemplate(reportTemplate, user);
				break;
			case COMPREHENSIVE_REPORT:
				isSuccess = updateComprehensiveReportTemplate(reportTemplate, user);
				break;
			case VM_PERFORMANCE_REPORT:
				isSuccess = updatePerformanceReportTemplate(reportTemplate,user);
				
				break;	
			case VM_ALARM_REPORT:
				isSuccess = updateAlarmReportTemplate(reportTemplate,user);
				break;	
			default:
				isSuccess = false;
				break;
		}
		
		return isSuccess;
	}
	
	private boolean deleteTemplateDirectorys(List<Long> directoryIdList){
		if(directoryIdList.size() > 0){
			//删除已不在的目录
			for(long deleteTemplateDirectoryId : directoryIdList){
				if(iReportTemplateDirectoryDao.del(deleteTemplateDirectoryId) <= 0){
					if(logger.isDebugEnabled()){
						logger.debug("iReportTemplateDirectoryDao.del(deleteTemplateDirectoryId) error...");
					}
					return false;
				}
				//先删除关系
				if(iReportTemplateDirectoryInstanceDao.deleteInstanceRelationByDirectoryId(deleteTemplateDirectoryId) <= 0){
					if(logger.isDebugEnabled()){
						logger.debug("iReportTemplateDirectoryInstanceDao.deleteInstanceRelationByDirectoryId(templateDirectoryId) error...");
					}
					return false;
				}
				if(iReportTemplateDirectoryMetricDao.deleteMetricRelationByDirectoryId(deleteTemplateDirectoryId) <= 0){
					if(logger.isDebugEnabled()){
						logger.debug("iReportTemplateDirectoryMetricDao.deleteMetricRelationByDirectoryId(templateDirectoryId) error...");
					}
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean addPerformanceInstanceAndMetricRelation(ReportTemplateDirectory reportTemplateDirectory,long templateDirectoryId,ReportModelMain rmm){
		String resourceId = "";
		
		//添加报表目录实例关系
		List<ReportTemplateDirectoryInstance> directoryInstanceList = reportTemplateDirectory.getDirectoryInstanceList();
		for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
			ResourceInstanceBo instance = stm_system_resourceApi.getResource(directoryInstance.getInstanceId());
			directoryInstance.setInstanceIP(instance.getDiscoverIP());
			if(instance.getShowName() == null || instance.getShowName().equals("")){
				directoryInstance.setInstanceName(instance.getName());
			}else{
				directoryInstance.setInstanceName(instance.getShowName());
			}
			directoryInstance.setInstanceType(reportTemplateDirectory.getReportTemplateDirectoryResource());
			resourceId = instance.getResourceId();
			ReportTemplateDirectoryInstancePo directoryInstancePo = new ReportTemplateDirectoryInstancePo();
			BeanUtils.copyProperties(directoryInstance, directoryInstancePo);
			directoryInstancePo.setReportDirectoryInstanceId(ReportTemplateDirectoryInstanceSeq.next());
			directoryInstancePo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) != 1){
				if(logger.isDebugEnabled()){
					logger.debug("IReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) error...");
				}
				return false;
			}
		}
		List<ReportTemplateDirectoryMetric> directoryMetricList = reportTemplateDirectory.getDirectoryMetricList();
		rmm.addTitleReport();
		List<Columns> columnsList = new ArrayList<Columns>();
		ColumnsTitle columnsTitle = new ColumnsTitle();
		
		List<Columns> columnsDetailList = new ArrayList<Columns>();
		ColumnsTitle columnsDetailTitle = new ColumnsTitle();
		columnsDetailList.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
		
		for(int i = 0 ; i < 4 ; i ++){
			Columns columns = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
			columnsList.add(columns);
		}
		
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			ResourceMetricDef def = capacityService.getResourceMetricDef(resourceId,directoryMetric.getMetricId());
			if(def != null){
				directoryMetric.setMetricName(def.getName() + "(" + def.getUnit() + ")");
				directoryMetric.setMetricType(def.getMetricType());
			}else{
				if(logger.isErrorEnabled()){
					logger.error("ResourceMetricDef is null ,resourceId = " + resourceId + ",MetricId = " + directoryMetric.getMetricId());
				}
			}
			Columns columns = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
			columns.setApart("3");
			columnsDetailList.add(columns);
			columnsList.add(columns);
		}
		
		columnsDetailList.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
		columnsDetailTitle.setColumns(columnsDetailList);
		
		columnsTitle.setColumns(columnsList);
		rmm.addTitleReport();
		rmm.addTableReport(columnsTitle);
		
		//添加报表目录指标关系
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			rmm.addStackedBarReport(directoryInstanceList.size());
			
			ReportTemplateDirectoryMetricPo directoryMetricPo = new ReportTemplateDirectoryMetricPo();
			BeanUtils.copyProperties(directoryMetric, directoryMetricPo);
			directoryMetricPo.setReportDirectoryMetricId(ReportTemplateDirectoryMetricSeq.next());
			directoryMetricPo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryMetricDao.insert(directoryMetricPo) != 1){
				if(logger.isDebugEnabled()){
					logger.debug("IReportTemplateDirectoryMetricDao.insert(directoryMetricPo) error...");
				}
				return false;
			}
		}
		
		if(reportTemplateDirectory.getReportTemplateDirectoryIsDetail() == 0){
			//需要详情
			rmm.addTitleReport();
			for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
				rmm.addTableReport(columnsDetailTitle);
			}
			for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
				rmm.addLineReport(directoryInstanceList.size());
			}
		}
		return true;
	}
	
	private boolean addAlarmInstanceAndMetricRelation(ReportTemplateDirectory reportTemplateDirectory,long templateDirectoryId,ReportModelMain rmm){
		String resourceId = "";
		
		//添加报表目录实例关系
		List<ReportTemplateDirectoryInstance> directoryInstanceList = reportTemplateDirectory.getDirectoryInstanceList();
		for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
			ResourceInstanceBo instance = stm_system_resourceApi.getResource(directoryInstance.getInstanceId());
			directoryInstance.setInstanceIP(instance.getDiscoverIP());
			if(instance.getShowName() == null || instance.getShowName().equals("")){
				directoryInstance.setInstanceName(instance.getName());
			}else{
				directoryInstance.setInstanceName(instance.getShowName());
			}
			directoryInstance.setInstanceType(reportTemplateDirectory.getReportTemplateDirectoryResource());
			resourceId = instance.getResourceId();
			
			ReportTemplateDirectoryInstancePo directoryInstancePo = new ReportTemplateDirectoryInstancePo();
			BeanUtils.copyProperties(directoryInstance, directoryInstancePo);
			directoryInstancePo.setReportDirectoryInstanceId(ReportTemplateDirectoryInstanceSeq.next());
			directoryInstancePo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) != 1){
				return false;
			}
		}
		List<ReportTemplateDirectoryMetric> directoryMetricList = reportTemplateDirectory.getDirectoryMetricList();
		
		List<Columns> columnsList = new ArrayList<Columns>();
		ColumnsTitle columnsTitle = new ColumnsTitle();
		
		for(int i = 0 ; i < 4 ; i ++){
			columnsList.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
		}
		
		//告警数量
		Columns columns1 = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
		columns1.setApart("1");
		columnsList.add(columns1);
		
		//级别分布
		Columns columns2 = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
		columns2.setApart("3");
		columnsList.add(columns2);
		
		//状态分布
		//告警数量
		Columns columns3 = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
		columns3.setApart("2");
		columnsList.add(columns3);
		
		//添加报表目录指标关系
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			
			//从固定指标中取值
			DefaultAlarmMetric defaultMetric = new DefaultAlarmMetric();
			directoryMetric.setMetricName(defaultMetric.getNameById(directoryMetric.getMetricId()));
			
			ReportTemplateDirectoryMetricPo directoryMetricPo = new ReportTemplateDirectoryMetricPo();
			BeanUtils.copyProperties(directoryMetric, directoryMetricPo);
			directoryMetricPo.setReportDirectoryMetricId(ReportTemplateDirectoryMetricSeq.next());
			directoryMetricPo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryMetricDao.insert(directoryMetricPo) != 1){
				return false;
			}
		}
		
		columnsTitle.setColumns(columnsList);
		rmm.addTitleReport();
		rmm.addTableReport(columnsTitle);

		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			
			//如果是状态分布,多加个图
			if(directoryMetric.getMetricId().equals("statusDistribution")){
				rmm.addPieReport();
			}
			rmm.addPieReport();
		}
		
		if(reportTemplateDirectory.getReportTemplateDirectoryIsDetail() == 0){
			//需要详情
			List<Columns> columnsList2 = new ArrayList<Columns>();
			ColumnsTitle columnsTitle2 = new ColumnsTitle();
			for(int i = 0 ; i < 5 ; i ++){
				columnsList2.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
			}
			columnsTitle2.setColumns(columnsList2);
			rmm.addTitleReport();
			for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
				rmm.addTableReport(columnsTitle2);
			}
		}
		return true;
	}
	
	private boolean addTopnInstanceAndMetricRelation(ReportTemplateDirectory reportTemplateDirectory,long templateDirectoryId,ReportModelMain rmm){
		String resourceId = "";
		
		//添加报表目录实例关系
		List<ReportTemplateDirectoryInstance> directoryInstanceList = reportTemplateDirectory.getDirectoryInstanceList();
		for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
			ResourceInstanceBo instance = stm_system_resourceApi.getResource(directoryInstance.getInstanceId());
			directoryInstance.setInstanceIP(instance.getDiscoverIP());
			if(instance.getShowName() == null || instance.getShowName().equals("")){
				directoryInstance.setInstanceName(instance.getName());
			}else{
				directoryInstance.setInstanceName(instance.getShowName());
			}
			directoryInstance.setInstanceType(reportTemplateDirectory.getReportTemplateDirectoryResource());
			resourceId = instance.getResourceId();
			
			ReportTemplateDirectoryInstancePo directoryInstancePo = new ReportTemplateDirectoryInstancePo();
			BeanUtils.copyProperties(directoryInstance, directoryInstancePo);
			directoryInstancePo.setReportDirectoryInstanceId(ReportTemplateDirectoryInstanceSeq.next());
			directoryInstancePo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) != 1){
				return false;
			}
		}
		List<ReportTemplateDirectoryMetric> directoryMetricList = reportTemplateDirectory.getDirectoryMetricList();
		rmm.addTitleReport();
		
		//添加报表目录指标关系
		
		int indexCount = 0;
		if(reportTemplateDirectory.getReportTemplateDirectoryMetricValueType() == 1){
			//性能
			indexCount = 5;
		}else{
			//告警
			indexCount = 5;
		}
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			rmm.addTitleReport();
			rmm.addStackedBarReport(directoryInstanceList.size());
			
			List<Columns> columnsList = new ArrayList<Columns>();
			ColumnsTitle columnsTitle = new ColumnsTitle();
			
			for(int i = 0 ; i < indexCount ; i ++){
				columnsList.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
			}
			
			columnsTitle.setColumns(columnsList);
			rmm.addTableReport(columnsTitle);
			
			if(reportTemplateDirectory.getReportTemplateDirectoryMetricValueType() == 2){
				//告警从固定指标群中获取
				DefaultTopnAlarmMetric topnMetric = new DefaultTopnAlarmMetric();
				directoryMetric.setMetricName(topnMetric.getNameById(directoryMetric.getMetricId()));
				directoryMetric.setMetricUnit("");
			}else{
				//性能
				ResourceMetricDef def = capacityService.getResourceMetricDef(resourceId,directoryMetric.getMetricId());
				if(def != null){
					directoryMetric.setMetricName(def.getName());
					directoryMetric.setMetricUnit(def.getUnit());
					directoryMetric.setMetricType(def.getMetricType());
				}else{
					if(logger.isErrorEnabled()){
						logger.error("ResourceMetricDef is null ,resourceId = " + resourceId + ",MetricId = " + directoryMetric.getMetricId());
					}
				}
				
			}
			
			
			ReportTemplateDirectoryMetricPo directoryMetricPo = new ReportTemplateDirectoryMetricPo();
			BeanUtils.copyProperties(directoryMetric, directoryMetricPo);
			directoryMetricPo.setReportDirectoryMetricId(ReportTemplateDirectoryMetricSeq.next());
			directoryMetricPo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryMetricDao.insert(directoryMetricPo) != 1){
				return false;
			}
		}
		return true;
	}
	
	private boolean addAvailabilityInstanceAndMetricRelation(ReportTemplateDirectory reportTemplateDirectory,long templateDirectoryId,ReportModelMain rmm){
		String resourceId = "";
		
		//添加报表目录实例关系
		List<ReportTemplateDirectoryInstance> directoryInstanceList = reportTemplateDirectory.getDirectoryInstanceList();
		for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
			ResourceInstanceBo instance = stm_system_resourceApi.getResource(directoryInstance.getInstanceId());
			directoryInstance.setInstanceIP(instance.getDiscoverIP());
			if(instance.getShowName() == null || instance.getShowName().equals("")){
				directoryInstance.setInstanceName(instance.getName());
			}else{
				directoryInstance.setInstanceName(instance.getShowName());
			}
			directoryInstance.setInstanceType(reportTemplateDirectory.getReportTemplateDirectoryResource());
			resourceId = instance.getResourceId();
			
			ReportTemplateDirectoryInstancePo directoryInstancePo = new ReportTemplateDirectoryInstancePo();
			BeanUtils.copyProperties(directoryInstance, directoryInstancePo);
			directoryInstancePo.setReportDirectoryInstanceId(ReportTemplateDirectoryInstanceSeq.next());
			directoryInstancePo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) != 1){
				return false;
			}
		}
		List<ReportTemplateDirectoryMetric> directoryMetricList = reportTemplateDirectory.getDirectoryMetricList();
		rmm.addTitleReport();
		
		List<Columns> columnsList = new ArrayList<Columns>();
		ColumnsTitle columnsTitle = new ColumnsTitle();
		
		for(int i = 0 ; i < 4 ; i ++){
			columnsList.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
		}
		
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			DefaultAvailabilityMetric availabilityMetric = new DefaultAvailabilityMetric();
			directoryMetric.setMetricName(availabilityMetric.getNameById(directoryMetric.getMetricId()));
			columnsList.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
		}
		columnsTitle.setColumns(columnsList);
		rmm.addTitleReport();
		rmm.addTableReport(columnsTitle);
		
		//添加报表目录指标关系
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			
			rmm.addStackedBarReport(directoryInstanceList.size());
			
			ReportTemplateDirectoryMetricPo directoryMetricPo = new ReportTemplateDirectoryMetricPo();
			BeanUtils.copyProperties(directoryMetric, directoryMetricPo);
			directoryMetricPo.setReportDirectoryMetricId(ReportTemplateDirectoryMetricSeq.next());
			directoryMetricPo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryMetricDao.insert(directoryMetricPo) != 1){
				return false;
			}
		}
		
		List<Columns> columnsList2 = new ArrayList<Columns>();
		ColumnsTitle columnsTitle2 = new ColumnsTitle();
		
		for(int i = 0 ; i < 5 ; i ++){
			columnsList2.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
		}
		columnsTitle2.setColumns(columnsList2);
		
		if(reportTemplateDirectory.getReportTemplateDirectoryIsDetail() == 0){
			//需要详情
			rmm.addTitleReport();
			for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
				rmm.addTableReport(columnsTitle2);
			}
		}
		return true;
	}
	
	private boolean addTrendInstanceAndMetricRelation(ReportTemplateDirectory reportTemplateDirectory,long templateDirectoryId,ReportModelMain rmm){
		String resourceId = "";
		
		List<ReportTemplateDirectoryMetric> directoryMetricList = reportTemplateDirectory.getDirectoryMetricList();
		rmm.addTitleReport();
		//添加报表目录实例关系
		List<ReportTemplateDirectoryInstance> directoryInstanceList = reportTemplateDirectory.getDirectoryInstanceList();
		
		List<Columns> columnsList = new ArrayList<Columns>();
		ColumnsTitle columnsTitle = new ColumnsTitle();
		
		for(int i = 0 ; i < 4 ; i++){
			columnsList.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
		}
		
		Columns columns = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
		columns.setApart("4");
		columnsList.add(columns);
		
		Columns columns2 = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
		columns2.setApart("2");
		columnsList.add(columns2);
		
		columnsTitle.setColumns(columnsList);
		rmm.addTitleReport();
		rmm.addTableReport(columnsTitle);
		
		for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
			
			//一个资源一个图，加两条趋势虚线(上周和本周)
			rmm.addLineReport(3);
			ResourceInstanceBo instance = stm_system_resourceApi.getResource(directoryInstance.getInstanceId());
			directoryInstance.setInstanceIP(instance.getDiscoverIP());
			if(instance.getShowName() == null || instance.getShowName().equals("")){
				directoryInstance.setInstanceName(instance.getName());
			}else{
				directoryInstance.setInstanceName(instance.getShowName());
			}
			directoryInstance.setInstanceType(reportTemplateDirectory.getReportTemplateDirectoryResource());
			resourceId = instance.getResourceId();
			
			ReportTemplateDirectoryInstancePo directoryInstancePo = new ReportTemplateDirectoryInstancePo();
			BeanUtils.copyProperties(directoryInstance, directoryInstancePo);
			directoryInstancePo.setReportDirectoryInstanceId(ReportTemplateDirectoryInstanceSeq.next());
			directoryInstancePo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) != 1){
				return false;
			}
		}
		
		//添加报表目录指标关系
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			ResourceMetricDef def = capacityService.getResourceMetricDef(resourceId,directoryMetric.getMetricId());
			if(def != null){
				directoryMetric.setMetricName(def.getName() + "(" + def.getUnit() + ")");
				directoryMetric.setMetricType(def.getMetricType());
			}else{
				if(logger.isErrorEnabled()){
					logger.error("ResourceMetricDef is null ,resourceId = " + resourceId + ",MetricId = " + directoryMetric.getMetricId());
				}
			}
			
			ReportTemplateDirectoryMetricPo directoryMetricPo = new ReportTemplateDirectoryMetricPo();
			BeanUtils.copyProperties(directoryMetric, directoryMetricPo);
			directoryMetricPo.setReportDirectoryMetricId(ReportTemplateDirectoryMetricSeq.next());
			directoryMetricPo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryMetricDao.insert(directoryMetricPo) != 1){
				return false;
			}
		}
		return true;
	}
	
	private boolean addAnalysisInstanceAndMetricRelation(ReportTemplateDirectory reportTemplateDirectory,long templateDirectoryId,ReportModelMain rmm){
		String resourceId = "";
		
		//添加报表目录实例关系
		List<ReportTemplateDirectoryInstance> directoryInstanceList = reportTemplateDirectory.getDirectoryInstanceList();
		for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
			ResourceInstanceBo instance = stm_system_resourceApi.getResource(directoryInstance.getInstanceId());
			directoryInstance.setInstanceIP(instance.getDiscoverIP());
			if(instance.getShowName() == null || instance.getShowName().equals("")){
				directoryInstance.setInstanceName(instance.getName());
			}else{
				directoryInstance.setInstanceName(instance.getShowName());
			}
			directoryInstance.setInstanceType(reportTemplateDirectory.getReportTemplateDirectoryResource());
			resourceId = instance.getResourceId();
			
			ReportTemplateDirectoryInstancePo directoryInstancePo = new ReportTemplateDirectoryInstancePo();
			BeanUtils.copyProperties(directoryInstance, directoryInstancePo);
			directoryInstancePo.setReportDirectoryInstanceId(ReportTemplateDirectoryInstanceSeq.next());
			directoryInstancePo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) != 1){
				return false;
			}
		}
		List<ReportTemplateDirectoryMetric> directoryMetricList = reportTemplateDirectory.getDirectoryMetricList();
		rmm.addTitleReport();
		
		List<Columns> columnsList = new ArrayList<Columns>();
		ColumnsTitle columnsTitle = new ColumnsTitle();
		//添加报表目录指标关系
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			ResourceMetricDef def = capacityService.getResourceMetricDef(resourceId,directoryMetric.getMetricId());
			if(def != null){
				directoryMetric.setMetricName(def.getName() + "(" + def.getUnit() + ")");
				directoryMetric.setMetricType(def.getMetricType());
				
				ProfileMetric profileMetric = null;
				try {
					profileMetric = profileService.getMetricByInstanceIdAndMetricId(directoryInstanceList.get(0).getInstanceId(), directoryMetric.getMetricId());
				} catch (ProfilelibException e) {
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
				}
				//找到正常状态最大边界
				List<ProfileThreshold> thresholdList = profileMetric.getMetricThresholds();
				int firstThreshold = 0;
				int thirdThrshold = 0;
				for(ProfileThreshold threshlod : thresholdList){
					if(threshlod.getPerfMetricStateEnum() == PerfMetricStateEnum.Normal){
						firstThreshold = Integer.parseInt(threshlod.getThresholdValue());
						break;
					}
					if(threshlod.getPerfMetricStateEnum() == PerfMetricStateEnum.Minor){
						directoryMetric.setMetricCurThreshold(Integer.parseInt(threshlod.getThresholdValue()));
						break;
					}
					if(threshlod.getPerfMetricStateEnum() == PerfMetricStateEnum.Major){
						thirdThrshold = Integer.parseInt(threshlod.getThresholdValue());
						break;
					}
				}
				if(firstThreshold > thirdThrshold){
					directoryMetric.setMetricThresholdDirection(1);
				}else{
					directoryMetric.setMetricThresholdDirection(0);
				}
				
			}else{
				if(logger.isErrorEnabled()){
					logger.error("ResourceMetricDef is null ,resourceId = " + resourceId + ",MetricId = " + directoryMetric.getMetricId());
				}
			}
			
			ReportTemplateDirectoryMetricPo directoryMetricPo = new ReportTemplateDirectoryMetricPo();
			BeanUtils.copyProperties(directoryMetric, directoryMetricPo);
			directoryMetricPo.setReportDirectoryMetricId(ReportTemplateDirectoryMetricSeq.next());
			directoryMetricPo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryMetricDao.insert(directoryMetricPo) != 1){
				return false;
			}
		}
		
		Columns columns = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
		columns.setApart("8");
		columnsList.add(columns);
		
		columnsTitle.setColumns(columnsList);
		rmm.addTitleReport();
		rmm.addTableReport(columnsTitle);
		
		//三个连续的柱子
		rmm.addBarReport(3);
		return true;
	}
	
	private boolean addBusinessInstanceAndMetricRelation(ReportTemplateDirectory reportTemplateDirectory,long templateDirectoryId,ReportModelMain rmm){
		
		//添加报表目录实例关系
		List<ReportTemplateDirectoryInstance> directoryInstanceList = reportTemplateDirectory.getDirectoryInstanceList();
		for(ReportTemplateDirectoryInstance directoryInstance : directoryInstanceList){
			BizMainBo bizMain = bizMainDao.getBasicInfo(directoryInstance.getInstanceId());
			directoryInstance.setInstanceName(bizMain.getName());
			ReportTemplateDirectoryInstancePo directoryInstancePo = new ReportTemplateDirectoryInstancePo();
			BeanUtils.copyProperties(directoryInstance, directoryInstancePo);
			directoryInstancePo.setReportDirectoryInstanceId(ReportTemplateDirectoryInstanceSeq.next());
			directoryInstancePo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) != 1){
				if(logger.isDebugEnabled()){
					logger.debug("IReportTemplateDirectoryInstanceDao.insert(directoryInstancePo) error...");
				}
				return false;
			}
		}
		List<ReportTemplateDirectoryMetric> directoryMetricList = reportTemplateDirectory.getDirectoryMetricList();
		rmm.addTitleReport();
		List<Columns> columnsList = new ArrayList<Columns>();
		ColumnsTitle columnsTitle = new ColumnsTitle();
		
		List<Columns> columnsDetailList = new ArrayList<Columns>();
		columnsDetailList.add(new Columns(null,DETAULT_TABLE_COLUMNS_COUNT));
		
		for(int i = 0 ; i < 2 ; i ++){
			Columns columns = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
			columnsList.add(columns);
		}
		
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			Columns columns = new Columns(null,DETAULT_TABLE_COLUMNS_COUNT);
			columnsDetailList.add(columns);
			columnsList.add(columns);
		}
		
		columnsTitle.setColumns(columnsList);
		rmm.addTitleReport();
		rmm.addTableReport(columnsTitle);
		
		//添加报表目录指标关系
		for(ReportTemplateDirectoryMetric directoryMetric : directoryMetricList){
			rmm.addStackedBarReport(directoryInstanceList.size());
			
			ReportTemplateDirectoryMetricPo directoryMetricPo = new ReportTemplateDirectoryMetricPo();
			BeanUtils.copyProperties(directoryMetric, directoryMetricPo);
			directoryMetricPo.setReportDirectoryMetricId(ReportTemplateDirectoryMetricSeq.next());
			directoryMetricPo.setReportTemplateDirectoryId(templateDirectoryId);
			if(iReportTemplateDirectoryMetricDao.insert(directoryMetricPo) != 1){
				if(logger.isDebugEnabled()){
					logger.debug("IReportTemplateDirectoryMetricDao.insert(directoryMetricPo) error...");
				}
				return false;
			}
		}
		
		return true;
	}
	
	private long addOrUpdateDirectory(ReportTemplateDirectory reportTemplateDirectory,long templateId){
		ReportTemplateDirectoryPo directoryPo = new ReportTemplateDirectoryPo();
		BeanUtils.copyProperties(reportTemplateDirectory, directoryPo);
		long templateDirectoryId = -1;
		if(directoryPo.getReportTemplateDirectoryId() <= 0){
			//添加该目录
			templateDirectoryId = ReportTemplateDirectorySeq.next();
			directoryPo.setReportTemplateDirectoryId(templateDirectoryId);
			directoryPo.setReportTemplateId(templateId);
			if(iReportTemplateDirectoryDao.insert(directoryPo) != 1){
				if(logger.isDebugEnabled()){
					logger.debug("IReportTemplateDirectoryDao.insert(directoryPo) error...");
				}
				return templateDirectoryId;
			}
		}else{
			//修改该目录
			templateDirectoryId = directoryPo.getReportTemplateDirectoryId();
			if(iReportTemplateDirectoryDao.update(directoryPo) != 1){
				if(logger.isDebugEnabled()){
					logger.debug("IReportTemplateDirectoryDao.update(directoryPo) error...");
				}
				return templateDirectoryId;
			}
			//先删除关系
			if(iReportTemplateDirectoryMetricDao.deleteMetricRelationByDirectoryId(templateDirectoryId) <= 0){
				if(logger.isDebugEnabled()){
					logger.debug("iReportTemplateDirectoryMetricDao.deleteMetricRelationByDirectoryId(templateDirectoryId) error...");
				}
				return templateDirectoryId;
			}
			if(iReportTemplateDirectoryInstanceDao.deleteInstanceRelationByDirectoryId(templateDirectoryId) <= 0){
				if(logger.isDebugEnabled()){
					logger.debug("iReportTemplateDirectoryInstanceDao.deleteInstanceRelationByDirectoryId(templateDirectoryId) zero...");
				}
				return templateDirectoryId;
			}
		}
		return templateDirectoryId;
	}

	/**
	 * 恢复期望值到默认值
	 */
	@Override
	public List<String> resetThresholdToDefault(List<String> metricIds,long instanceId) {
		List<String> thresholdValues = new ArrayList<String>();
		
		for(String metricId : metricIds){
			
			ProfileMetric profileMetric = null;
			try {
				profileMetric = profileService.getMetricByInstanceIdAndMetricId(instanceId, metricId);
			} catch (ProfilelibException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
			//找到正常状态最大边界
			List<ProfileThreshold> thresholdList = profileMetric.getMetricThresholds();
			for(ProfileThreshold threshlod : thresholdList){
				if(threshlod.getPerfMetricStateEnum() == PerfMetricStateEnum.Minor){
					thresholdValues.add(threshlod.getThresholdValue());
				}
			}
			
		}
		return thresholdValues;
	}
	
	//开始执行job
	private void startJob(ReportTemplate reportTemplate){
		//调度任务.....
		try {
			reportEngine.startEngine(reportTemplate);
		} catch (ClassNotFoundException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		} catch (InstancelibException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		} catch (SchedulerException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		}
//		ReportTask reportTask = new ReportTask(reportTemplate);
//		try {
//			//Job 任务开始，创建XML数据文件
//			reportTask.start();
//		} catch (Exception e) {
//			logger.error("ReportTask start() error , message : " + e.getMessage());
//		}
	}
	
	//更新执行job
	private void updateJob(ReportTemplate reportTemplate){
		//调度任务.....
		try {
			reportEngine.updateEngine(reportTemplate);
		} catch (ClassNotFoundException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		} catch (InstancelibException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		} catch (SchedulerException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		}
	}

	/**
	 * 获取业务列表
	 * @update 获取业务列表时增加域的查询条件 20161205 dfw
	 */
	@Override
	public List<BizMainBo> getBusinessList(ILoginUser user,String searchContent,int startNum,int pageSize,long domainId) {
//		List<BizMainBo> bizAllMainList = bizMainDao.getBizListForSearch(searchContent);
		BizMainBo bo = new BizMainBo();
		bo.setName(searchContent);
		bo.setDomainId(domainId);
		List<BizMainBo> bizMainList = bizMainDao.getBizList(bo);
		if(bizMainList == null){
			return new ArrayList<BizMainBo>();
		}
		//权限查询....
		List<BizMainBo> bizList = new ArrayList<BizMainBo>();
		for(BizMainBo main : bizMainList){
			if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
				continue;
			}
			//TODO
			bizList.add(main);
		}
		List<BizMainBo> resultBusiness = new ArrayList<BizMainBo>();
		
		if((startNum + pageSize) > bizList.size()){
			resultBusiness = bizList.subList(startNum, bizList.size());
		}else{
			resultBusiness = bizList.subList(startNum, (startNum + pageSize));
		}
		
		return resultBusiness;
	}

	/**
	 * 获取业务指标
	 */
	@Override
	public List<ReportResourceMetric> getBusinessMetricList() {
		
		List<BizSerMetric> metricList = bizMainApi.getBizReportMetrics();
		
		List<ReportResourceMetric> reportMetricList = new ArrayList<ReportResourceMetric>();
		
		if(metricList == null || metricList.size() <= 0){
			return reportMetricList; 
		}
		
		for(BizSerMetric metric : metricList){
			ReportResourceMetric reportMetric = new ReportResourceMetric();
			reportMetric.setId(metric.getId());
			reportMetric.setName(metric.getName());
			reportMetricList.add(reportMetric);
		}
		
		return reportMetricList;
	}
	
	/**
	 * 根据type获取模板列表
	 */
	@Override
	public List<ReportTemplate> getReportTemplateListByType(ReportTypeEnum type){
		List<ReportTemplatePo> templatePoList = iReportTemplateDao.getReportTemplateListByType(type.getIndex());
		List<ReportTemplate> reportTemplateList = new ArrayList<ReportTemplate>();
		for(ReportTemplatePo po:templatePoList){
			ReportTemplate template = new ReportTemplate();
			BeanUtils.copyProperties(po, template);
			reportTemplateList.add(template);
		}
		
		return reportTemplateList;
	}

	@Override
	public void listen(BizSerReportEvent event) {
		List<Long> deleteIds = (List<Long>)event.getSourceIds();
		if(logger.isInfoEnabled()){
			logger.info("Into BizSerReportListener to deleteBusinessRelation ! ids length : " + deleteIds.size());
		}
		// deleteIds 需要删除的业务实例Id 集合
		long[] ids = new long[deleteIds.size()];
		for(int i = 0 ; i < deleteIds.size() ; i ++){
			ids[i] = deleteIds.get(i);
		}
		//先获取拥有该业务的报表模板
		List<Long> templateIds = iReportTemplateDirectoryDao.selectTemplateIdByTemplateId(ids);
		
		//删除报表和目录的对应关系
		iReportTemplateDirectoryInstanceDao.deleteInstanceRelationByInstanceId(ids);
		
		//修改正在运行的job任务
		if(templateIds != null && templateIds.size() > 0){
			for(Long id : templateIds){
				ReportTemplate tempalte = getReportTemplateById(id);
				if(tempalte == null){
					continue;
				}
				if(tempalte.getReportTemplateType() == ReportTypeEnum.BUSINESS_REPORT.getIndex()){
					LoginUser user = new LoginUser();
					user.setId(1L);
					updateReportTemplate(tempalte, user);
				}
			}
		}
	}

	@Override
	public boolean checkDomainIsRel(long domainId) {
		// TODO Auto-generated method stub
		
		List<ReportTemplatePo> templatePoList = iReportTemplateDao.select(null);
		
		if(templatePoList == null || templatePoList.size() <= 0){
			return false;
		}
		
		//判断所有报表模板是否有使用该域的
		for(ReportTemplatePo template : templatePoList){
			
			if(template.getReportTemplateDomainId() >= 0 && template.getReportTemplateDomainId() == domainId){
				return true;
			}
			
		}
		
		
		return false;
	}

	@Override
	public void interceptor(InstancelibEvent instancelibEvent) throws Exception {
		// TODO Auto-generated method stub
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_DELETE_EVENT){
			List<Long> deleteIds = (List<Long>)instancelibEvent.getSource();
			if(deleteIds == null || deleteIds.size() <= 0){
				return;
			}
			if(logger.isInfoEnabled()){
				logger.info("Into ReportTemplateImpl to deleteReportAndResourceRelation ! ids length : " + deleteIds.size());
			}
			// deleteIds 需要删除的资源实例Id 集合
			//删除自定义资源组关系
			long[] ids = new long[deleteIds.size()];
			for(int i = 0 ; i < deleteIds.size() ; i ++){
				ids[i] = deleteIds.get(i);
			}
			//先获取拥有该资源的报表模板
			List<Long> templateIds = iReportTemplateDirectoryDao.selectTemplateIdByTemplateId(ids);
			
			//删除报表和目录的对应关系
			iReportTemplateDirectoryInstanceDao.deleteInstanceRelationByInstanceId(ids);
			
			//修改正在运行的job任务
			if(templateIds != null && templateIds.size() > 0){
				for(Long id : templateIds){
					ReportTemplate tempalte = getReportTemplateById(id);
					if(tempalte == null){
						continue;
					}
					if(tempalte.getReportTemplateType() != ReportTypeEnum.BUSINESS_REPORT.getIndex()){
						LoginUser user = new LoginUser();
						user.setId(1L);
						updateReportTemplate(tempalte, user);
					}
				}
			}
				
		}
		
	}
	
}
