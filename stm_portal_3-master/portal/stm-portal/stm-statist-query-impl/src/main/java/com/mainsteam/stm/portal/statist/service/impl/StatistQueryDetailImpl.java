package com.mainsteam.stm.portal.statist.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.beans.BeanUtils;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.service.impl.ReportModelMain;
import com.mainsteam.stm.portal.statist.api.IStatistQueryDetailApi;
import com.mainsteam.stm.portal.statist.bo.StatistQueryInstanceBo;
import com.mainsteam.stm.portal.statist.bo.StatistQueryMainBo;
import com.mainsteam.stm.portal.statist.bo.StatistQueryMetricBo;
import com.mainsteam.stm.portal.statist.bo.StatistResourceCategoryBo;
import com.mainsteam.stm.portal.statist.bo.StatistResourceInstanceBo;
import com.mainsteam.stm.portal.statist.bo.StatistResourceMetricBo;
import com.mainsteam.stm.portal.statist.dao.IStatistQueryDetailDao;
import com.mainsteam.stm.portal.statist.po.StatistQueryInstancePo;
import com.mainsteam.stm.portal.statist.po.StatistQueryMainPo;
import com.mainsteam.stm.portal.statist.po.StatistQueryMetricPo;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;

public class StatistQueryDetailImpl implements IStatistQueryDetailApi, InstancelibListener {

	private static final Log logger = LogFactory.getLog(StatistQueryDetailImpl.class);

	@Resource
	private IFileClientApi fileClient;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private IResourceApi stm_system_resourceApi;

	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private IStatistQueryDetailDao statistQueryDetailDao;

	@Resource
	private ISequence stm_statist_query_main_seq;
	
	@Resource
	private ISequence stm_statist_query_instance_seq;
	
	@Resource
	private ISequence stm_statist_query_metric_seq;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;
	
	@Override
	public List<StatistResourceCategoryBo> getAllResourceCategory() {
		List<StatistResourceCategoryBo> category = new ArrayList<StatistResourceCategoryBo>();
		this.loadResourceCategory(category, capacityService.getRootCategory());
		return category;
	}

	@Override
	public List<StatistResourceCategoryBo> getChildResourceByResourceCategory(String categoryId) {
		Map<String, StatistResourceCategoryBo> childResourceMap = new HashMap<String, StatistResourceCategoryBo>();
		
		CategoryDef def = capacityService.getCategoryById(categoryId);
		
		this.loadChildResource(def, childResourceMap);
		
		List<StatistResourceCategoryBo> childResourceList = new ArrayList<StatistResourceCategoryBo>(childResourceMap.values());
		return childResourceList;
	}

	@Override
	public List<StatistResourceCategoryBo> getChildResourceByMainResource(String resourceId) {
		List<StatistResourceCategoryBo> childResourceList = new ArrayList<StatistResourceCategoryBo>();
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
			StatistResourceCategoryBo resultDef = new StatistResourceCategoryBo();
			resultDef.setId(childResource.getId());
			resultDef.setName(childResource.getName());
			childResourceList.add(resultDef);
		}
			
		return childResourceList;
	}

	@Override
	public List<StatistResourceInstanceBo> getResourceInstanceByCategoryId(String categoryId, Long domainId, ILoginUser user) {
		List<StatistResourceInstanceBo> instanceList = new ArrayList<StatistResourceInstanceBo>();
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
		if(domainId != null && domainId.longValue() != 0){
			domainIds.add(domainId);
		}
		if(!domainIds.isEmpty()){
			queryBo.setDomainIds(domainIds);
		}
		List<ResourceInstanceBo> instances = stm_system_resourceApi.getResources(queryBo);
		if(instances != null && instances.size() > 0){
			for(ResourceInstanceBo instance : instances){
				//判断实例是否监控和是否属于当前用户域
				if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
					continue;
				}
				StatistResourceInstanceBo reportInstance = new StatistResourceInstanceBo();
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
					instanceList.add(reportInstance);
				}
			}
		}
		return instanceList;
	}

	@Override
	public List<StatistResourceInstanceBo> getInstanceByResource(String resourceId, Long domainId, ILoginUser user) {
		List<StatistResourceInstanceBo> instances = new ArrayList<StatistResourceInstanceBo>();
		
		if(resourceId.contains(",")){
			try{
				//有多个资源
				HashSet<String> resourceIdSet = new HashSet<String>(Arrays.asList(resourceId.split(",")));
				List<ResourceInstance> instanceList = resourceInstanceService.getResourceInstanceByResourceId(resourceIdSet);
				List<Long> mainResourceIntanceIds = new ArrayList<Long>();
				Set<Long> mainResourceIntanceIdsSet = new HashSet<Long>();
				//找出所有主资源实例
				for(ResourceInstance instance : instanceList){
					mainResourceIntanceIdsSet.add(instance.getParentId());
				}
				mainResourceIntanceIds = new ArrayList<Long>(mainResourceIntanceIdsSet);
				ResourceQueryBo queryBo = new ResourceQueryBo(user);
				List<Long> domainIds = new ArrayList<Long>();
				if(domainId != null && domainId.longValue() != 0){
					domainIds.add(domainId);
				}
				if(!domainIds.isEmpty()){
					queryBo.setDomainIds(domainIds);
				}
				List<Long> accessMainResourceIntancesIds = stm_system_resourceApi.accessFilter(queryBo, mainResourceIntanceIds);
				for (int i = 0; instanceList != null && i < instanceList.size(); i++) {
					ResourceInstance instance = instanceList.get(i);
					// 查询模型名称
					ResourceDef def = capacityService.getResourceDefById(instance.getResourceId());
					if(def == null){
						if(logger.isErrorEnabled()){
							logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = " + instance.getResourceId());
						}
						continue;
					}
					//判断该子资源是否属于用户
					if(!accessMainResourceIntancesIds.contains(instance.getParentId())){
						continue;
					}
					//判断实例是否监控和是否属于当前用户域
					if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
						continue;
					}
					StatistResourceInstanceBo reportInstance = new StatistResourceInstanceBo();
					reportInstance.setId(instance.getId());
					reportInstance.setResourceId(instance.getResourceId());
					reportInstance.setShowName(instance.getShowName() != null && !"".equals(instance.getShowName()) ? instance.getShowName() : instance.getName());
					reportInstance.setResourceName(def.getName());
					reportInstance.setDiscoverIP(resourceInstanceService.getResourceInstance(instance.getParentId()).getShowIP());
					instances.add(reportInstance);
				}
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
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
					for(ResourceInstance instance : instanceList){
						//找出所有主资源实例
						mainResourceIntanceIdsSet.add(instance.getParentId());
					}
					mainResourceIntanceIds = new ArrayList<Long>(mainResourceIntanceIdsSet);
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					List<Long> domainIds = new ArrayList<Long>();
					if(domainId != null && domainId.longValue() != 0){
						domainIds.add(domainId);
					}
					if(!domainIds.isEmpty()){
						queryBo.setDomainIds(domainIds);
					}
					List<Long> accessMainResourceIntancesIds = stm_system_resourceApi.accessFilter(queryBo, mainResourceIntanceIds);
					if(instanceList != null && instanceList.size() >0){
						for(ResourceInstance instance : instanceList){
							//判断该子资源是否属于用户
							if(!accessMainResourceIntancesIds.contains(instance.getParentId())){
								continue;
							}
							//判断实例是否监控和是否属于当前用户域
							if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
								continue;
							}
							StatistResourceInstanceBo reportInstance = new StatistResourceInstanceBo();
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
							if(instance.getParentId() > 0){
								reportInstance.setDiscoverIP(resourceInstanceService.getResourceInstance(instance.getParentId()).getShowIP());
							}
							instances.add(reportInstance);
						}
					}
				}else{
					//主资源
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					List<Long> domainIds = new ArrayList<Long>();
					if(domainId != null && domainId.longValue() != 0){
						domainIds.add(domainId);
					}
					if(!domainIds.isEmpty()){
						queryBo.setDomainIds(domainIds);
					}
					queryBo.setModuleId(resourceId);
					List<ResourceInstanceBo> instanceList = stm_system_resourceApi.getResources(queryBo);
					if(instanceList != null && instanceList.size() >0){
						for(ResourceInstanceBo instance : instanceList){
							//判断实例是否监控和是否属于当前用户域
							if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
								continue;
							}
							StatistResourceInstanceBo reportInstance = new StatistResourceInstanceBo();
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

	@Override
	public List<StatistResourceMetricBo> getMetricListByResource(List<String> resourceIdList, long instanceId, String statQType) {
		Map<String, List<StatistResourceMetricBo>> metricMap = new HashMap<String, List<StatistResourceMetricBo>>();
		
		for(String resourceId : resourceIdList){
			ResourceDef def = capacityService.getResourceDefById(resourceId);
			if(def == null){
				if(logger.isErrorEnabled()){
					logger.error("CapacityService.getResourceDefById is null,resourceId : " + resourceId);
				}
				continue;
			}
			Map<Long, StatistResourceMetricBo> performanceMetricMap = new TreeMap<Long, StatistResourceMetricBo>();
			for(ResourceMetricDef metricDef : def.getMetricDefs()){
				if(metricDef.getMetricType() == MetricTypeEnum.PerformanceMetric){
					StatistResourceMetricBo metric = new StatistResourceMetricBo();
					BeanUtils.copyProperties(metricDef, metric);
					metric.setMetricSort(-1);
					if(metricDef.getUnit() != null && !metricDef.getUnit().trim().equals("")){
						metric.setName(metric.getName() + "(" + metricDef.getUnit() + ")");
						metric.setUnit(metricDef.getUnit());
					}
					performanceMetricMap.put(Long.parseLong(metricDef.getDisplayOrder()), metric);
				}
			}
			metricMap.put(resourceId, new ArrayList<StatistResourceMetricBo>(performanceMetricMap.values()));
		}
		
		//取所有集合的交集
		List<StatistResourceMetricBo> metricList = new ArrayList<StatistResourceMetricBo>();
		
		for(String resourceId : metricMap.keySet()){
			if(metricList.size() <= 0){
				metricList = metricMap.get(resourceId);
				continue;
			}
			List<StatistResourceMetricBo> metricLists = metricMap.get(resourceId);
			metricList.retainAll(metricLists);
		}
		return metricList;
	}

	@Override
	public int insertStatQMain(StatistQueryMainBo statQMainBo, Long userId) {
		long statQMainId = stm_statist_query_main_seq.next();
		statQMainBo.setId(statQMainId);
		statQMainBo.setCreateUserId(userId);
		statQMainBo.setCreateTime(new Date());
		// 生成ireport模版
		statQMainBo.setiReportId(createIReport4StatQ(statQMainBo, userId));
		int insertNum = statistQueryDetailDao.insertStatQMain(statQMainBo2Po(statQMainBo));
		
		if(statQMainBo.getInstanceIdList() != null){
			for (int i = 0; i < statQMainBo.getInstanceIdList().size(); i++) {
				Long instanceId = statQMainBo.getInstanceIdList().get(i);
				if(instanceId == null || instanceId == 0){
					continue;
				}
				StatistQueryInstanceBo statQInstBo = new StatistQueryInstanceBo();
				statQInstBo.setId(stm_statist_query_instance_seq.next());
				statQInstBo.setStatQMainId(statQMainId);
				statQInstBo.setInstanceId(instanceId);
				insertNum += statistQueryDetailDao.insertStatQInstance(sQInstanceBo2Po(statQInstBo));
			}
		}
		if(statQMainBo.getMetricIdList() != null){
			for (int i = 0; i < statQMainBo.getMetricIdList().size(); i++) {
				String metricId = statQMainBo.getMetricIdList().get(i);
				if(metricId == null || "".equals(metricId)){
					continue;
				}
				StatistQueryMetricBo statQMetricBo = new StatistQueryMetricBo();
				statQMetricBo.setId(stm_statist_query_metric_seq.next());
				statQMetricBo.setStatQMainId(statQMainId);
				statQMetricBo.setMetricId(metricId);
				insertNum += statistQueryDetailDao.insertStatQMetric(sQMetricBo2Po(statQMetricBo));
			}
		}
		
		return insertNum;
	}
	
	@Override
	public List<StatistQueryMainBo> getAllSQMain(ILoginUser user) {
		List<StatistQueryMainBo> boList = new ArrayList<StatistQueryMainBo>();
		List<StatistQueryMainPo> poList = statistQueryDetailDao.getAllSQMain();
		for (int i = 0; poList != null && i < poList.size(); i++) {
			boList.add(statQMainPo2Bo(poList.get(i)));
		}
		return boList;
	}

	@Override
	public StatistQueryMainBo getSQMainByStatQId(Long statQId) {
		StatistQueryMainBo statQMainBo = statQMainPo2Bo(statistQueryDetailDao.getSQMainByStatQId(statQId));
		if(statQMainBo != null){
			List<StatistQueryInstanceBo> statQInstBoList = new ArrayList<StatistQueryInstanceBo>();
			List<StatistQueryInstancePo> statQInstPoList = statistQueryDetailDao.getSQInstanceByStatQId(statQId);
			for (int i = 0; statQInstPoList != null && i < statQInstPoList.size(); i++) {
				statQInstBoList.add(sQInstancePo2Bo(statQInstPoList.get(i)));
			}
			statQMainBo.setStatQInstBoList(statQInstBoList);
			List<StatistQueryMetricBo> statQMetricBoList = new ArrayList<StatistQueryMetricBo>();
			List<StatistQueryMetricPo> statQMetricPoList = statistQueryDetailDao.getSQMetricByStatQId(statQId);
			for (int i = 0; statQMetricPoList != null && i < statQMetricPoList.size(); i++) {
				statQMetricBoList.add(sQMetricPo2Bo(statQMetricPoList.get(i)));
			}
			statQMainBo.setStatQMetricBoList(statQMetricBoList);
		}
		return statQMainBo;
	}

	@Override
	public int delSQMainByStatQId(Long statQMainId) {
		return statistQueryDetailDao.delSQMainByStatQId(statQMainId);
	}

	@Override
	public int updateStatQMain(StatistQueryMainBo statQMainBo, Long userId) {
		long statQMainId = statQMainBo.getId();
		// 重新生成ireport模版
		statQMainBo.setiReportId(createIReport4StatQ(statQMainBo, userId));
		int insertNum = statistQueryDetailDao.updateStatQMain(statQMainBo2Po(statQMainBo));

		statistQueryDetailDao.delSQInstanceByStatQId(statQMainId);
		if(statQMainBo.getInstanceIdList() != null){
			for (int i = 0; i < statQMainBo.getInstanceIdList().size(); i++) {
				Long instanceId = statQMainBo.getInstanceIdList().get(i);
				if(instanceId == null || instanceId == 0){
					continue;
				}
				StatistQueryInstanceBo statQInstBo = new StatistQueryInstanceBo();
				statQInstBo.setId(stm_statist_query_instance_seq.next());
				statQInstBo.setStatQMainId(statQMainId);
				statQInstBo.setInstanceId(instanceId);
				insertNum += statistQueryDetailDao.insertStatQInstance(sQInstanceBo2Po(statQInstBo));
			}
		}
		
		statistQueryDetailDao.delSQMetricByStatQId(statQMainId);
		if(statQMainBo.getMetricIdList() != null){
			for (int i = 0; i < statQMainBo.getMetricIdList().size(); i++) {
				String metricId = statQMainBo.getMetricIdList().get(i);
				if(metricId == null || "".equals(metricId)){
					continue;
				}
				StatistQueryMetricBo statQMetricBo = new StatistQueryMetricBo();
				statQMetricBo.setId(stm_statist_query_metric_seq.next());
				statQMetricBo.setStatQMainId(statQMainId);
				statQMetricBo.setMetricId(metricId);
				insertNum += statistQueryDetailDao.insertStatQMetric(sQMetricBo2Po(statQMetricBo));
			}
		}
		return insertNum;
	}
	
	private void loadResourceCategory(List<StatistResourceCategoryBo> resourceCategory, CategoryDef def) {
//		if (!def.isDisplay()) {
//			return;
//		}
		if(!licenseCapacityCategory.isAllowCategory(def.getId())){
			return;
		}
		StatistResourceCategoryBo category = new StatistResourceCategoryBo();
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
				if (categoryDefs[i].isDisplay() || "VM".equals(categoryDefs[i].getId())){
					loadResourceCategory(resourceCategory, categoryDefs[i]);
				}
			}
		} else {
			if (null != def.getResourceDefs()) {
				ResourceDef[] resourceDefs = def.getResourceDefs();
				for (int i = 0; i < resourceDefs.length; i++) {
					ResourceDef resourceDef = resourceDefs[i];
					StatistResourceCategoryBo resource = new StatistResourceCategoryBo();
					resource.setId(resourceDef.getId());
					resource.setName(resourceDef.getName());
					resource.setType(2);
					resource.setPid(def.getId());
					resourceCategory.add(resource);
				}
			}
		}
	}

	private void loadChildResource(CategoryDef def,Map<String, StatistResourceCategoryBo> childResourceMap){
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
						StatistResourceCategoryBo resultDef = new StatistResourceCategoryBo();
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

	private Long createIReport4StatQ(StatistQueryMainBo statQMainBo, Long userId){
		ReportModelMain rmm = new ReportModelMain(String.valueOf(userId), fileClient);
		// 主标题
		rmm.addTitleReport();
		// 性能统计
		if("1".equals(statQMainBo.getType())){
			for (int i = 0; statQMainBo.getInstanceIdList() != null && i < statQMainBo.getInstanceIdList().size(); i++) {
				// 资源标题
				rmm.addTitleReport();
				for (int j = 0; statQMainBo.getMetricIdList() != null && j < statQMainBo.getMetricIdList().size(); j++) {
					// 线性图
					rmm.addLineReport(1);
					// 表格
					ColumnsTitle columnsTitle = new ColumnsTitle();
					List<Columns> columsList = new ArrayList<Columns>();
					columsList.add(new Columns("指标名称", "1"));
					columsList.add(new Columns("当前值", "1"));
					columsList.add(new Columns("最小值", "1"));
					columsList.add(new Columns("最大值", "1"));
					columsList.add(new Columns("平均值", "1"));
					columnsTitle.setColumns(columsList);
					rmm.addTableReport(columnsTitle);
				}
			}
		}else{
			// 资产统计
			// 表头信息
			List<Columns> columsList = new ArrayList<Columns>();
			columsList.add(new Columns("设备名称", "1"));
			columsList.add(new Columns("IP地址", "1"));
			columsList.add(new Columns("类型", "1"));
			columsList.add(new Columns("域名称", "1"));
			for (int i = 0; statQMainBo.getMetricIdList() != null && i < statQMainBo.getMetricIdList().size(); i++) {
				// 饼图
				rmm.addPieReport();
				switch (statQMainBo.getMetricIdList().get(i)) {
				case "cpuRate":
					columsList.add(new Columns("CPU平均利用率", "1"));
					break;
				case "memRate":
					columsList.add(new Columns("内存利用率", "1"));
					break;
				case "DiskUsagePercentage":
					columsList.add(new Columns("磁盘使用百分比", "1"));
					break;
				}
			}
			// 表格
			ColumnsTitle columnsTitle = new ColumnsTitle();
			columnsTitle.setColumns(columsList);
			rmm.addTableReport(columnsTitle);
		}
		return rmm.writeAndComplieJrxmlFile();
	}

	private StatistQueryMainPo statQMainBo2Po(StatistQueryMainBo statQMainBo){
		StatistQueryMainPo statQMainPo = new StatistQueryMainPo();
		BeanUtils.copyProperties(statQMainBo, statQMainPo);
		return statQMainPo;
	}

	private StatistQueryMainBo statQMainPo2Bo(StatistQueryMainPo statQMainPo){
		StatistQueryMainBo statQMainBo = new StatistQueryMainBo();
		BeanUtils.copyProperties(statQMainPo, statQMainBo);
		return statQMainBo;
	}

	private StatistQueryMetricPo sQMetricBo2Po(StatistQueryMetricBo statQMetricBo){
		StatistQueryMetricPo statQMetricPo = new StatistQueryMetricPo();
		BeanUtils.copyProperties(statQMetricBo, statQMetricPo);
		return statQMetricPo;
	}
	
	private StatistQueryMetricBo sQMetricPo2Bo(StatistQueryMetricPo statQMetricPo){
		StatistQueryMetricBo statQMetricBo = new StatistQueryMetricBo();
		BeanUtils.copyProperties(statQMetricPo, statQMetricBo);
		return statQMetricBo;
	}

	private StatistQueryInstancePo sQInstanceBo2Po(StatistQueryInstanceBo statQInstanceBo){
		StatistQueryInstancePo statQInstancePo = new StatistQueryInstancePo();
		BeanUtils.copyProperties(statQInstanceBo, statQInstancePo);
		return statQInstancePo;
	}
	
	private StatistQueryInstanceBo sQInstancePo2Bo(StatistQueryInstancePo statQInstancePo){
		StatistQueryInstanceBo statQInstanceBo = new StatistQueryInstanceBo();
		BeanUtils.copyProperties(statQInstancePo, statQInstanceBo);
		return statQInstanceBo;
	}

	@Override
	public void listen(InstancelibEvent instancelibEvent) throws Exception {
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_DELETE_EVENT){
			List<Long> deleteIds = (List<Long>)instancelibEvent.getSource();
			if(deleteIds == null || deleteIds.size() <= 0){
				return;
			}
			List<Long> statQMainIdList = statistQueryDetailDao.getSQMainIdByInstIdList(deleteIds);
			for (int i = 0; statQMainIdList != null && i < statQMainIdList.size(); i++) {
				StatistQueryMainBo statQMainBo = getSQMainByStatQId(statQMainIdList.get(i));
				List<Long> instIdList = statQMainBo.getInstanceIdList(), newInstIdList = new ArrayList<Long>();
				for (int j = 0; instIdList != null && j < instIdList.size(); j++) {
					if(!deleteIds.contains(instIdList.get(j))){
						newInstIdList.add(instIdList.get(j));
					}
				}
				statQMainBo.setInstanceIdList(newInstIdList);
				if(newInstIdList.isEmpty()){
					statQMainBo.setMetricIdList(null);
				}
				updateStatQMain(statQMainBo, statQMainBo.getCreateUserId());
			}
		}
	}

	@Override
	public boolean isExistsStatQMainName(StatistQueryMainBo statQMainBo) {
		int count = statistQueryDetailDao.countSQMainByTypeAndName(statQMainBo2Po(statQMainBo));
		return count > 0 ? true : false;
	}

}
