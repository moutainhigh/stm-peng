package com.mainsteam.stm.webService.resource;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.caplib.plugin.PluginInitParameter;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.resource.ResourcePropertyDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.MetricSummaryService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.common.metric.query.MetricSummaryQuery;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.business.api.IBizServiceApi;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.MetricSetting;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.constants.UserConstants;
import com.mainsteam.stm.webService.obj.Result;
import com.mainsteam.stm.webService.obj.ResultCodeEnum;

@WebService
public class ResourceWebServicesImpl implements ResourceWebServices {

	private static final Logger logger = LoggerFactory
			.getLogger(ResourceWebServicesImpl.class);

	private static final String DEFUALT_PROFILE_DESC = "由WebService接口创建的个性化策略";

	private static final String PERSONALIZE_PROFILE_NAME = "个性化策略";

	private static final String PERSONALIZE_CHILD_PROFILE_NAME = "个性化子策略";
	
	private static final String IF_IN_OCTETS_SPEED = "ifInOctetsSpeed";
	
	private static final String IF_OUT_OCTETS_SPEED = "ifOutOctetsSpeed";

	@Resource
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private ProfileService profileService;

	@Resource
	private IUserApi stm_system_userApi;

	@Resource
	private CapacityService capacityService;

	@Resource
	private MetricDataService metricDataService;
	
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
	
	@Resource
	private MetricStateService metricStateService;
	
	@Resource
	private CustomMetricService customMetricService;

	@Resource
	private InstanceStateService instanceStateService;

	@Resource
	private IDomainApi domainApi;

	@Resource(name = "stm_system_resourceApi")
	private IResourceApi resourceApi;
	
	@Resource
	private MetricSummaryService  metricSummaryService;
	
	@Resource
	private AlarmEventService alarmEventService;
	
	@Resource
	private ICustomResourceGroupApi customResourceGroupApi;
	
	@Resource
	private IBizServiceApi bizServiceApi;

	/**
	 * 修改指定阈值
	 */
	@Override
	public ResourceSyncResult syncMetricThreshold(ResourceSyncBean resourceBean) {

		logger.debug("Into syncMetricThreshold...");
		System.out.println("Into syncMetricThreshold...");

		Map<Long, List<ThresholdSyncBean>> dataMap = resourceBean
				.getThresholdData();

		ResourceSyncResult result = new ResourceSyncResult();

		List<ResultInstanceBean> resultList = new ArrayList<ResultInstanceBean>();

		if (dataMap == null) {
			result.setMsg("ResourceSyncBean is null");
			result.setResult(resultList);
		}

		Set<Long> instancesSet = dataMap.keySet();

		if (instancesSet == null || instancesSet.size() <= 0) {
			result.setMsg("ResourceSyncBean instances list is null");
			result.setResult(resultList);
		}

		for (Long instanceId : instancesSet) {

			// 遍历资源集合
			List<ThresholdSyncBean> thresholdList = dataMap.get(instanceId);

			if (thresholdList == null || thresholdList.size() <= 0) {
				continue;
			}

			ResourceInstance instance = null;
			try {
				instance = resourceInstanceService
						.getResourceInstance(instanceId);
			} catch (InstancelibException e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}

			if (instance == null) {
				logger.error("This instance is null , instanceid = "
						+ instanceId);
				System.out.println("This instance is null , instanceid = "
						+ instanceId);
				continue;
			}

			// 判断该资源实例当前是否监控状态
			if (!(instance.getLifeState().equals(
					InstanceLifeStateEnum.MONITORED) || instance.getLifeState()
					.equals(InstanceLifeStateEnum.NOT_MONITORED))) {
				// 该资源未被监控
				logger.error("This instance life state error,instanceid = "
						+ instanceId);
				System.out
						.println("This instance life state error,instanceid = "
								+ instanceId);
				continue;
			}

			// 判断该资源实例当前的监控策略
			ProfileInfo profileInfo = null;
			try {
				profileInfo = profileService
						.getBasicInfoByResourceInstanceId(instanceId);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
			if (profileInfo == null) {
				logger.debug("Instance is not monitor,instanceid = "
						+ instanceId);
				System.out.println("Instance is not monitor,instanceid = "
						+ instanceId);
			}

			// 判断当前策略是否个性化策略
			if (!(profileInfo.getProfileType()
					.equals(ProfileTypeEnum.PERSONALIZE))
					|| profileInfo == null) {
				// 默认策略或者自定义策略或者没有监控

				// 判断之前是否有过个性化策略
				long histroyProfileId = getHistoryPersonalizeProfile(instance);
				if (histroyProfileId > 0) {
					// 之前有个性化策略,监听个性化策略
					// 添加监控到之前的个性化策略
					addMonitorToHistoryPersonalizeProfile(histroyProfileId,
							instance);
				} else {
					// 之前没有个性化策略,创建并监听个性化策略
					addPersonalizeProfile(instance);
				}

			}

			List<String> resultMetricList = new ArrayList<String>();

			// 修改阈值
			for (ThresholdSyncBean thresholdBean : thresholdList) {
				List<ProfileThreshold> quertThresholdList = null;
				ProfileInfo info = null;
				try {
					quertThresholdList = profileService
							.getThresholdByInstanceIdAndMetricId(
									instance.getId(),
									thresholdBean.getMetricId());
					info = profileService
							.getBasicInfoByResourceInstanceId(instance.getId());
				} catch (ProfilelibException e) {
					logger.error(e.getMessage(), e);
					e.printStackTrace();
					continue;
				}
				if (quertThresholdList == null
						|| quertThresholdList.size() <= 0) {
					logger.error("Get threshold list error , instanceid = "
							+ instance.getId() + ",metricId = "
							+ thresholdBean.getMetricId());
					System.out
							.println("Get threshold list error , instanceid = "
									+ instance.getId() + ",metricId = "
									+ thresholdBean.getMetricId());
					continue;
				}

				List<Threshold> updateThresholdList = new ArrayList<Threshold>();

				for (ProfileThreshold threshold : quertThresholdList) {

					if (threshold.getPerfMetricStateEnum().equals(
							PerfMetricStateEnum.Minor)) {
						if (thresholdBean.getMinorValue() == null
								|| thresholdBean.getMinorValue().equals("")) {
							continue;
						}
						threshold.setThresholdExpression(thresholdBean.getMinorValue());
						Threshold newThreshold = new Threshold();
						BeanUtils.copyProperties(threshold, newThreshold);
						updateThresholdList.add(newThreshold);
						continue;
					}

					if (threshold.getPerfMetricStateEnum().equals(
							PerfMetricStateEnum.Major)) {
						if (thresholdBean.getMajorValue() == null
								|| thresholdBean.getMajorValue().equals("")) {
							continue;
						}
						threshold.setThresholdExpression(thresholdBean.getMajorValue());
						Threshold newThreshold = new Threshold();
						BeanUtils.copyProperties(threshold, newThreshold);
						updateThresholdList.add(newThreshold);
						continue;
					}

				}
				try {
					profileService.updateProfileMetricThreshold(
							info.getProfileId(), updateThresholdList);
				} catch (ProfilelibException e) {
					logger.error(e.getMessage(), e);
					continue;
				}

				resultMetricList.add(thresholdBean.getMetricId());

			}

			ResultInstanceBean resultIntance = new ResultInstanceBean();
			resultIntance.setInstanceId(instance.getId());
			resultIntance.setMetricIds(resultMetricList);

			resultList.add(resultIntance);

		}

		result.setMsg("Update finish!");
		result.setResult(resultList);

		return result;

	}

	/**
	 * 创建个性化策略
	 */
	private void addPersonalizeProfile(ResourceInstance instance) {

		// 判断该资源是主资源还是子资源
		long createProfileInstanceId = -1;
		String resourceId = "";
		if (instance.getParentId() <= 0) {
			// 主资源
			createProfileInstanceId = instance.getId();
			resourceId = instance.getResourceId();
		} else {
			// 子资源
			createProfileInstanceId = instance.getParentId();
			try {
				resourceId = resourceInstanceService.getResourceInstance(
						createProfileInstanceId).getResourceId();
			} catch (InstancelibException e) {
				logger.error(e.getMessage(), e);
			}
		}

		// 获取当前profile对象并修改profile信息
		Profile profile = null;
		try {
			profile = profileService.getEmptyPersonalizeProfile(resourceId,
					createProfileInstanceId);
		} catch (ProfilelibException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		if (profile == null) {
			return;
		}

		// 获取超级管理员用户
		List<User> adminUsers = stm_system_userApi
				.getUsersByType(UserConstants.USER_TYPE_SUPER_ADMIN);

		long createUserId = 1;

		if (adminUsers != null && adminUsers.size() > 0) {
			createUserId = adminUsers.get(0).getId();
		}

		// 修改主策略信息
		profile.getProfileInfo().setCreateUser(createUserId);
		profile.getProfileInfo().setUpdateTime(null);
		profile.getProfileInfo().setProfileDesc(DEFUALT_PROFILE_DESC);

		// 添加个性化策略
		try {
			profileService.createPersonalizeProfile(profile);
		} catch (ProfilelibException e) {
			logger.error(e.getMessage(), e);
		}

	}

	// 判断指定资源之前是否有个性化策略
	private long getHistoryPersonalizeProfile(ResourceInstance instance) {

		// 判断该资源是主资源还是子资源
		long createProfileInstanceId = -1;
		if (instance.getParentId() <= 0) {
			// 主资源
			createProfileInstanceId = instance.getId();
		} else {
			// 子资源
			createProfileInstanceId = instance.getParentId();
		}

		ProfileInfo historyPersonalProfile = null;

		try {
			historyPersonalProfile = profileService
					.getPersonalizeProfileBasicInfoByResourceInstanceId(createProfileInstanceId);
		} catch (ProfilelibException e) {
			logger.error(e.getMessage(), e);
		}

		if (historyPersonalProfile == null) {
			// 之前没有个性化策略
			return -1;
		}

		return historyPersonalProfile.getProfileId();
	}

	// 添加监控到之前的个性化策略
	private void addMonitorToHistoryPersonalizeProfile(long histroyProfileId,
			ResourceInstance instance) {

		// 判断资源是主资源还是子资源
		if (instance.getParentId() <= 0) {
			// 主资源
			List<Long> instanceIds = new ArrayList<Long>();
			instanceIds.add(instance.getId());
			try {
				profileService
						.addProfileInstance(histroyProfileId, instanceIds);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			// 子资源
			long parentId = instance.getParentId();
			// 先监控主资源
			List<Long> instanceIds = new ArrayList<Long>();
			instanceIds.add(parentId);
			try {
				profileService
						.addProfileInstance(histroyProfileId, instanceIds);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
			}

			// 监控子资源
			try {
				Profile profile = profileService
						.getProfilesById(histroyProfileId);
				boolean isContainChildProfile = false;
				if (profile.getChildren() == null
						|| profile.getChildren().size() <= 0) {
					logger.debug("Personalize profile have no child profile , profileId : "
							+ histroyProfileId);
					System.out
							.println("Personalize profile have no child profile , profileId : "
									+ histroyProfileId);
				} else {
					// 遍历子策略,找出符合类型的子策略
					for (Profile childProfile : profile.getChildren()) {
						if (childProfile.getProfileInfo().getResourceId()
								.equals(instance.getResourceId())) {
							// 找到相同的资源类型,添加该子资源到监控
							List<Long> childInstanceIds = new ArrayList<Long>();
							instanceIds.add(instance.getId());
							profileService.addProfileInstance(childProfile
									.getProfileInfo().getProfileId(),
									childInstanceIds);
							isContainChildProfile = true;
							break;
						}
					}
				}

				if (!isContainChildProfile) {
					// 没有找到符合类型的子策略
					// 创建子策略
					ProfileInfo childNewInfo = new ProfileInfo();
					childNewInfo.setProfileDesc(DEFUALT_PROFILE_DESC);
					childNewInfo.setProfileName(PERSONALIZE_PROFILE_NAME
							+ instance.getResourceId());
					childNewInfo.setProfileType(ProfileTypeEnum.PERSONALIZE);
					childNewInfo.setResourceId(instance.getResourceId());
					childNewInfo.setUpdateUser("");

					// 获取超级管理员用户
					List<User> adminUsers = stm_system_userApi
							.getUsersByType(UserConstants.USER_TYPE_SUPER_ADMIN);

					long createUserId = 1;

					if (adminUsers != null && adminUsers.size() > 0) {
						createUserId = adminUsers.get(0).getId();
					}

					childNewInfo.setCreateUser(createUserId);

					long nowAddChildProfileId = profileService
							.createChildProfile(histroyProfileId, childNewInfo);

					// 添加子资源到策略
					List<Long> childInstanceIds = new ArrayList<Long>();
					childInstanceIds.add(instance.getId());
					try {
						profileService.addProfileInstance(nowAddChildProfileId,
								childInstanceIds);
					} catch (ProfilelibException e) {
						logger.error(e.getMessage(), e);
					}

				}

			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
			}

		}

	}

	/**
	 * 获取所有资源类型
	 */
	@Override
	public String getResourceType() {

		Result result = new Result();

		// 从能力库获取所有模型
		List<ResourceDef> resourceList = capacityService.getResourceDefList();

		System.out.println("resourceList size : " + resourceList.size());
		logger.debug("resourceList size : " + resourceList.size());

		ResourceType[] resourceTypes = new ResourceType[resourceList.size()];

		for (int i = 0; i < resourceList.size(); i++) {
			ResourceDef resource = resourceList.get(i);
			if (resource == null) {
				continue;
			}
			ResourceType resourceType = new ResourceType();
			resourceType.setResourceTypeId(resource.getId());
			resourceType.setResourceTypeName(resource.getName());

			List<AttributeDefination> definationList = new ArrayList<AttributeDefination>();

			// 设置模型属性和发现属性

			// 获取模型属性
			ResourcePropertyDef[] propertyDefs = resource.getPropertyDefs();
			for (ResourcePropertyDef property : propertyDefs) {
				AttributeDefination deination = new AttributeDefination();
				deination.setAttributeId(property.getId());
				deination.setAttributeName(property.getName());
				deination.setValueType("String");
				deination.setDescription(property.getName());

				definationList.add(deination);
			}

			// 获取发现属性
			Map<String, PluginInitParameter[]> pluginMap = resource
					.getPluginInitParameterMap();
			Set<PluginInitParameter[]> pluginInitSet = new HashSet<>(
					pluginMap.values());
			for (PluginInitParameter[] plugins : pluginInitSet) {
				for (PluginInitParameter pluginParameter : plugins) {
					AttributeDefination deination = new AttributeDefination();
					deination.setAttributeId(pluginParameter.getId());
					deination.setAttributeName(pluginParameter.getName());
					deination.setValueType("String");
					deination.setDescription(pluginParameter.getName());

					definationList.add(deination);
				}
			}

			AttributeDefination[] definations = new AttributeDefination[definationList
					.size()];

			resourceType.setDefinations(definationList.toArray(definations));

			resourceTypes[i] = resourceType;

		}

		result.setData(resourceTypes);
		result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);

		return JSONObject.toJSONString(result);
	}

	@Override
	public String getAllCategory() {
		CategoryDef categoryDef = capacityService.getRootCategory();
		// 一级菜单
		CategoryDef[] baseCategoryDef = categoryDef.getChildCategorys();
		List<Map<String, String>> baseList = new ArrayList<Map<String, String>>();
		if (null != baseCategoryDef && baseCategoryDef.length > 0) {
			for (CategoryDef base : baseCategoryDef) {
				CategoryDef[] childCategoryDef = base.getChildCategorys();
				if (null != childCategoryDef && childCategoryDef.length > 0) {
					for (CategoryDef child : childCategoryDef) {
						Map<String, String> result = new HashMap<String, String>();
						result.put("id", child.getId());
						result.put("name", child.getName());
						result.put("pid", base.getId());
						result.put("type", base.getName());
						baseList.add(result);
					}
				}
			}
		}
		Result result = new Result();
		if (baseList.size() > 0) {
			result.setData(baseList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}
	
	public String getMetricDetailInfoByResourceId(String resourceId){
		Result result = new Result();
		Map<String, Object> baseMap = new HashMap<String, Object>();
		baseMap.put("id", resourceId);
		ResourceDef rdf = capacityService.getResourceDefById(resourceId);
		if(null==rdf){
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		baseMap.put("name", rdf.getName());
		baseMap.put("description", rdf.getDescription());
		baseMap.put("metrics", getResourceDefMetrics(rdf));
		
		List<Map<String, Object>> childrenMapList = new ArrayList<Map<String, Object>>();
		ResourceDef[] rdfChildren = rdf.getChildResourceDefs();
		if(null!=rdfChildren && rdfChildren.length>0){
			for(ResourceDef children:rdfChildren){
				Map<String, Object> childrenMap = new HashMap<String, Object>();
				childrenMap.put("id", children.getId());
				childrenMap.put("name", children.getName());
				childrenMap.put("description", children.getDescription());
				childrenMap.put("type", children.getType());
				childrenMap.put("metrics", getResourceDefMetrics(children));
				
				childrenMapList.add(childrenMap);
			}
		}
		baseMap.put("childResourceDef", childrenMapList);
		
		result.setData(baseMap);
		result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		return JSONObject.toJSONString(result);
	}
	
	private List<Map<String, Object>> getResourceDefMetrics(ResourceDef rdf){
		List<Map<String, Object>> metricMapList = new ArrayList<Map<String, Object>>();
		ResourceMetricDef[] rmdfArr = rdf.getMetricDefs();
		if(null!=rmdfArr && rmdfArr.length>0){
			for(ResourceMetricDef rmdf:rmdfArr){
				Map<String, Object> metricMap = new HashMap<String, Object>();
				metricMap.put("metricId", rmdf.getId());
				metricMap.put("metricName", rmdf.getName());
				metricMap.put("metricUnit", rmdf.getUnit());
				metricMap.put("metricType", rmdf.getMetricType().name());
				metricMapList.add(metricMap);
			}
		}
		return metricMapList;
	}
	
	@Override
	public String getAllCategoryDetailInfo() {
		CategoryDef categoryDef = capacityService.getRootCategory();
		
		// 一级菜单
		CategoryDef[] baseCategoryDef = categoryDef.getChildCategorys();
		List<Map<String, Object>> baseList = new ArrayList<Map<String, Object>>();
		if (null != baseCategoryDef && baseCategoryDef.length > 0) {
			for (CategoryDef base : baseCategoryDef) {
				parseCategoryTree(base,baseList);
			}
		}
		
		Result result = new Result();
		if (baseList.size() > 0) {
			result.setData(baseList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}
	
	private void parseCategoryTree(CategoryDef rootCategory,List<Map<String, Object>> resultList){
		CategoryDef[] childCategoryDef = rootCategory.getChildCategorys();
		if (null != childCategoryDef && childCategoryDef.length > 0) {
			for (CategoryDef child : childCategoryDef) {
				
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("id", child.getId());
				result.put("name", child.getName());
				result.put("pid", rootCategory.getId());
				result.put("type", rootCategory.getName());
				result.put("ifResourceId", false);
				resultList.add(result);
				
				parseCategoryTree(child,resultList);
			}
		}else{
			ResourceDef[] rdfArr = rootCategory.getResourceDefs();
			if(null != rdfArr && rdfArr.length > 0){
				for(ResourceDef rdf:rdfArr){
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("id", rdf.getId());
					result.put("name", rdf.getName());
					result.put("pid", rootCategory.getId());
					result.put("type", rootCategory.getName());
					result.put("ifResourceId", true);
					
//					List<Map<String, Object>> metricMapList = new ArrayList<Map<String, Object>>();
//					ResourceMetricDef[] rmdfArr = rdf.getMetricDefs();
//					
//					if(null!=rmdfArr && rmdfArr.length>0){
//						for(ResourceMetricDef rmdf:rmdfArr){
//							Map<String, Object> metricMap = new HashMap<String, Object>();
//							metricMap.put("metricId", rmdf.getId());
//							metricMap.put("metricName", rmdf.getName());
//							metricMap.put("metricUnit", rmdf.getUnit());
//							metricMap.put("metricType", rmdf.getMetricType().name());
//							metricMapList.add(metricMap);
//						}
//						result.put("metrics", metricMapList);
//					}
					
					resultList.add(result);
				}	
			}
		}
	}

	@Override
	public String getResourceBaseInfoByInstanceId(long instanceId){
		Result result = new Result();
		List<ResourceInstance> riList = new ArrayList<ResourceInstance>();

		try {
			ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
			if(null!=ri){
				riList.add(ri);
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMainResourceError_CODE);
			return JSONObject.toJSONString(result);
		}
		if (null != riList && riList.size() > 0) {
			List<Map<String, String>> listMap = null;
			try {
				listMap = setMetricValue(riList);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
				result.setData("");
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceMetricValueError_CODE);
				return JSONObject.toJSONString(result);
			}

			result.setData(listMap);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}
	
	
	@Override
	public String getAllResourceBaseInfo() {
		Result result = new Result();
		List<ResourceInstance> riList = null;

		try {
			riList = resourceInstanceService.getAllParentInstance();
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMainResourceError_CODE);
			return JSONObject.toJSONString(result);
		}
		if (null != riList && riList.size() > 0) {
			List<Map<String, String>> listMap = null;
			try {
				listMap = setMetricValue(riList);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
				result.setData("");
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceMetricValueError_CODE);
				return JSONObject.toJSONString(result);
			}

			result.setData(listMap);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}

	}

	/**
	 * 判断是否为合法IP
	 * 
	 * @return the ip
	 */
	private boolean isboolIp(String ipAddress) {
		String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	@Override
	public String getResourceByResourceIp(String ip) {
		Result result = new Result();
		// ip是否合法
		if (!isboolIp(ip)) {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_IPError_CODE);
			return JSONObject.toJSONString(result);
		}
		List<ResourceInstance> riList = null;
		List<ResourceInstance> riListTemp = null;
		try {
			riList = resourceInstanceService.getAllParentInstance();
			riListTemp = new ArrayList<ResourceInstance>();

			for (ResourceInstance ri : riList) {
				if (null != ri.getShowIP() && ri.getShowIP().equals(ip)) {
					riListTemp.add(ri);
				}
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMainResourceError_CODE);
			return JSONObject.toJSONString(result);
		}

		if (null != riListTemp && riListTemp.size() > 0) {
			List<Map<String, String>> listMap = null;
			try {
				listMap = setMetricValue(riListTemp);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
				result.setData("");
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceMetricValueError_CODE);
				return JSONObject.toJSONString(result);
			}
			result.setData(listMap);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}

	}

	@Override
	public String getResourceByResourceName(String name) {
		Result result = new Result();
		List<ResourceInstance> riList = null;
		List<ResourceInstance> riListTemp = new ArrayList<ResourceInstance>();

		try {
			riList = resourceInstanceService.getAllParentInstance();

			for (ResourceInstance ri : riList) {
				String resName = ri.getName().toLowerCase();
				String queryName = name.toLowerCase();

				if (resName.contains(queryName)) {
					riListTemp.add(ri);
				}
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMainResourceError_CODE);
			return JSONObject.toJSONString(result);
		}

		if (riListTemp.size() > 0) {
			List<Map<String, String>> listMap = null;
			try {
				listMap = setMetricValue(riListTemp);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
				result.setData("");
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceMetricValueError_CODE);
				return JSONObject.toJSONString(result);
			}
			result.setData(listMap);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	@Override
	public String getResourceByResourceId(String resourceId) {
		Result result = new Result();
		List<ResourceInstance> riList = null;

		try {
			riList = resourceInstanceService
					.getResourceInstanceByResourceId(resourceId);
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceByResourceIdError_CODE);
			return JSONObject.toJSONString(result);
		}

		if (null != riList && riList.size() > 0) {
			List<Map<String, String>> listMap = null;
			try {
				listMap = setMetricValue(riList);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
				result.setData("");
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceMetricValueError_CODE);
				return JSONObject.toJSONString(result);
			}
			result.setData(listMap);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}

	}

	@Override
	public String getResourceByCategoryId(String categoryId) {
		Result result = new Result();
		List<ResourceInstance> riList = null;

		try {
			riList = resourceInstanceService
					.getParentInstanceByCategoryId(categoryId);
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceByCategoryIdError_CODE);
			return JSONObject.toJSONString(result);
		}

		if (null != riList && riList.size() > 0) {
			List<Map<String, String>> listMap = null;
			try {
				listMap = setMetricValue(riList);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
				result.setData("");
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceMetricValueError_CODE);
				return JSONObject.toJSONString(result);
			}
			result.setData(listMap);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	@Override
	public String getResourceByResourceState(String state) {
		String queryState = "";
		switch (state) {
		case "CRITICAL":
			queryState = "CRITICAL";
			break;
		case "SERIOUS":
			queryState = "SERIOUS";
			break;
		case "WARN":
			queryState = "WARN";
			break;
		case "NORMAL":
			queryState = "NORMAL";
			break;
//		case "UNKOWN":
//			queryState = "UNKOWN";
//			break;
		default:
			queryState = "NORMAL";
			break;
		}

		Result result = new Result();
		List<ResourceInstance> riList = null;
		try {
			riList = resourceInstanceService.getAllParentInstance();
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMainResourceError_CODE);
			return JSONObject.toJSONString(result);
		}
		List<ResourceInstance> riListTemp = new ArrayList<ResourceInstance>();
		Map<Long, ResourceInstance> rinsMap = new HashMap<Long, ResourceInstance>();

		List<Long> instanceIdList = new ArrayList<Long>();
		for (int i = 0; i < riList.size(); i++) {
			ResourceInstance rins = riList.get(i);

			rinsMap.put(rins.getId(), rins);
			instanceIdList.add(rins.getId());
		}
		List<InstanceStateData> instanceStateDataList = instanceStateService
				.findStates(instanceIdList);
		for (InstanceStateData isd : instanceStateDataList) {
			if (null != isd.getState()
					&& queryState.equals(isd.getState().name())) {
				riListTemp.add(rinsMap.get(isd.getInstanceID()));
			}
		}

		if (riListTemp.size() > 0) {
			List<Map<String, String>> listMap = null;
			try {
				listMap = setMetricValue(riListTemp);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
				result.setData("");
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceMetricValueError_CODE);
				return JSONObject.toJSONString(result);
			}
			result.setData(listMap);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	private List<Map<String, String>> setMetricValue(
			List<ResourceInstance> resourceInstanceList)
			throws ProfilelibException {
		List<Map<String, String>> resourceInstanceMapList = new ArrayList<Map<String, String>>();
		if(null==resourceInstanceList || resourceInstanceList.size()==0){
			return resourceInstanceMapList;
		}
		
		// cpurate,memerate指标查询
		String[] metrics = { MetricIdConsts.METRIC_CPU_RATE,
				MetricIdConsts.METRIC_MEME_RATE };
		long[] instanceIdArray = new long[resourceInstanceList.size()];
		List<Long> instanceIdList = new ArrayList<Long>();

		// 初始化查询条件及指标相关信息
		for (int i = 0; i < resourceInstanceList.size(); i++) {
			ResourceInstance rins = resourceInstanceList.get(i);
			instanceIdArray[i] = rins.getId();
			instanceIdList.add(rins.getId());
		}

		// 设置指标查询条件
		MetricRealtimeDataQuery mrdq = new MetricRealtimeDataQuery();
		mrdq.setInstanceID(instanceIdArray);
		mrdq.setMetricID(metrics);
		List<Map<String, ?>> metricMapList = metricDataService
				.queryRealTimeMetricData(mrdq);
		// 组装查询回的指标数据
		Map<String, Map<String, ?>> reLoadMetricMap = new HashMap<String, Map<String, ?>>();
		for (int i = 0; i < metricMapList.size(); i++) {
			Map<String, ?> metricMap = metricMapList.get(i);
			if (metricMap.get("instanceid") != null) {
				String instanceId = metricMap.get("instanceid").toString();
				reLoadMetricMap.put(instanceId, metricMap);
			}
		}

		// 循环是否有这个资源实例的指标
		List<InstanceStateData> instanceStateDataList = instanceStateService
				.findStates(instanceIdList);
		for (int i = 0; i < resourceInstanceList.size(); i++) {
			Map<String, String> resourceInstanceMap = new HashMap<String, String>();
			ResourceInstance rins = resourceInstanceList.get(i);

			resourceInstanceMap.put("id", String.valueOf(rins.getId()));
			resourceInstanceMap.put("name", rins.getName());
			resourceInstanceMap.put("showName", rins.getShowName());
			resourceInstanceMap.put("ip", null == rins.getShowIP() ? ""
					: rins.getShowIP());

			resourceInstanceMap.put("resourceId",rins.getResourceId());
			resourceInstanceMap.put("categoryId",rins.getCategoryId());
			resourceInstanceMap.put("parentCategoryId",rins.getParentCategoryId());

			Domain domain = domainApi.get(rins.getDomainId());
			resourceInstanceMap.put("domain",
					null == domain ? "" : domain.getName());

			boolean flag = false;
			for (InstanceStateData is : instanceStateDataList) {
				if (is.getInstanceID() == rins.getId()) {
					flag = true;
					resourceInstanceMap.put("state", is.getState().name());
				}
			}
			if (!flag) {
//				resourceInstanceMap.put("state",InstanceStateEnum.UNKOWN.name());
				resourceInstanceMap.put("state",InstanceStateEnum.NORMAL.name());
			}

			Long instanceId = rins.getId();
			String resourceId = rins.getResourceId();

			boolean isMonitor_CPU = false, isMonitor_MEM = false;
			// 判断cpu内存是否已监控
			ProfileMetric cpuPMetric = profileService
					.getMetricByInstanceIdAndMetricId(instanceId,
							MetricIdConsts.METRIC_CPU_RATE);
			if (cpuPMetric != null && cpuPMetric.isMonitor()) {
				isMonitor_CPU = true;
			}
			ProfileMetric memPMetric = profileService
					.getMetricByInstanceIdAndMetricId(instanceId,
							MetricIdConsts.METRIC_MEME_RATE);
			if (memPMetric != null && memPMetric.isMonitor()) {
				isMonitor_MEM = true;
			}

			// 如果有查询回的指标数据
			if (reLoadMetricMap.containsKey(instanceId.toString())) {

				Map<String, ?> metricMap = reLoadMetricMap.get(instanceId
						.toString());
				// CPU指标值
				if (isMonitor_CPU && metricMap.containsKey(MetricIdConsts.METRIC_CPU_RATE)) {
					if (metricMap.get(MetricIdConsts.METRIC_CPU_RATE) != null) {
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(resourceId,MetricIdConsts.METRIC_CPU_RATE);

						String cpuRateValue = metricMap.get(MetricIdConsts.METRIC_CPU_RATE).toString();
						if(null!=rmDef){
							resourceInstanceMap.put("cpuRate",cpuRateValue+ (null == rmDef.getUnit() ? "" : rmDef.getUnit()));
						}else{
							resourceInstanceMap.put("cpuRate",cpuRateValue);
						}
					} else {
						resourceInstanceMap.put("cpuRate", "--");
					}
				} else if (!isMonitor_CPU) {
					resourceInstanceMap.put("cpuRate", "N/A");
				} else {
					resourceInstanceMap.put("cpuRate", "--");
				}
				// 内存指标值
				if (isMonitor_MEM && metricMap.containsKey(MetricIdConsts.METRIC_MEME_RATE)) {
					if (metricMap.get(MetricIdConsts.METRIC_MEME_RATE) != null) {
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(resourceId,MetricIdConsts.METRIC_MEME_RATE);
						
						String memRateValue = metricMap.get(MetricIdConsts.METRIC_MEME_RATE).toString();
						if(null!=rmDef){
							resourceInstanceMap.put("memRate",memRateValue+ (null == rmDef.getUnit() ? "" : rmDef.getUnit()));
						}else{
							resourceInstanceMap.put("memRate",memRateValue);
						}
					} else {
						resourceInstanceMap.put("memRate", "--");
					}
				} else if (!isMonitor_MEM) {
					resourceInstanceMap.put("memRate", "N/A");
				} else {
					resourceInstanceMap.put("memRate", "--");
				}
			} else {
				if (isMonitor_CPU) {
					resourceInstanceMap.put("cpuRate", "--");
				} else {
					resourceInstanceMap.put("cpuRate", "N/A");
				}
				if (isMonitor_MEM) {
					resourceInstanceMap.put("memRate", "--");
				} else {
					resourceInstanceMap.put("memRate", "N/A");
				}
			}
			resourceInstanceMapList.add(resourceInstanceMap);
		}
		return resourceInstanceMapList;
	}

	/**
	 * 获取所有已监控的资源基本信息
	 */
	@Override
	public String getAllMonitoredResourceBaseInfo() {
		Result result = new Result();
		List<ResourceInstance> riList = null;
		List<ResourceInstance> riListTemp = null;
		try {
			riList = resourceInstanceService.getAllParentInstance();
			riListTemp = new ArrayList<ResourceInstance>();
			for (ResourceInstance resInstance : riList) {
				// 资源为监控状态
				if (resInstance.getLifeState().equals(
						InstanceLifeStateEnum.MONITORED)) {
					riListTemp.add(resInstance);
				}
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMainResourceError_CODE);
			return JSONObject.toJSONString(result);
		}

		if (null != riListTemp && riListTemp.size() > 0) {
			List<Map<String, String>> listMap = null;
			try {
				listMap = setMetricValue(riListTemp);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
				result.setData("");
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceMetricValueError_CODE);
				return JSONObject.toJSONString(result);
			}
			result.setData(listMap);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	/**
	 * 获取所有未监控的资源基本信息
	 */
	@Override
	public String getAllNotMonitoredResourceBaseInfo() {
		Result result = new Result();
		List<ResourceInstance> riList = null;
		List<Map<String, String>> resourceInstanceMapList = new ArrayList<Map<String, String>>();
		try {
			riList = resourceInstanceService.getAllParentInstance();
			Map<String, String> resourceInstanceMap = null;
			for (ResourceInstance resInstance : riList) {
				// 资源为未监控状态
				if (resInstance.getLifeState().equals(
						InstanceLifeStateEnum.NOT_MONITORED)) {
					resourceInstanceMap = new HashMap<String, String>();
					resourceInstanceMap.put("id",
							String.valueOf(resInstance.getId()));
					resourceInstanceMap.put("name", resInstance.getName());
					resourceInstanceMap
							.put("ip", null == resInstance.getShowIP() ? ""
									: resInstance.getShowIP());

					CategoryDef cdef = capacityService
							.getCategoryById(resInstance.getCategoryId());
					resourceInstanceMap.put("monitorType", null == cdef ? ""
							: cdef.getName());

					Domain domain = domainApi.get(resInstance.getDomainId());
					resourceInstanceMap.put("domain", null == domain ? ""
							: domain.getName());

					resourceInstanceMapList.add(resourceInstanceMap);
				}
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMainResourceError_CODE);
			return JSONObject.toJSONString(result);
		}

		if (null != resourceInstanceMapList
				&& resourceInstanceMapList.size() > 0) {
			result.setData(resourceInstanceMapList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	/**
	 * 根据资源IP得到资源详细信息
	 */
	@Override
	public String getResourceDetailInfoByIp(String ip) {
		Result result = new Result();
		// ip是否合法
		if (!isboolIp(ip)) {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_IPError_CODE);
			return JSONObject.toJSONString(result);
		}
		List<ResourceInstance> riList = null;
		List<Map<String, String>> resourceInstanceMapList = new ArrayList<Map<String, String>>();
		Map<String, String> resourceInstanceMap = null;
		try {
			riList = resourceInstanceService.getAllParentInstance();

			for (ResourceInstance resInstance : riList) {
				if (null != resInstance.getShowIP()
						&& resInstance.getShowIP().equals(ip)) {
					resourceInstanceMap = new HashMap<String, String>();
					resourceInstanceMap.put("id",
							String.valueOf(resInstance.getId()));
					resourceInstanceMap.put("name",
							resInstance.getShowName() == null ? ""
									: resInstance.getShowName());
					resourceInstanceMap.put("ip", resInstance.getShowIP());
					resourceInstanceMap.put("resourceType", capacityService
							.getCategoryById(resInstance.getCategoryId())
							.getParentCategory().getId());
					resourceInstanceMap.put("resourceId",
							resInstance.getResourceId());
					// 可用性
					resourceInstanceMap.put("availability",
							queryInstanceState(resInstance));

					resourceInstanceMapList.add(resourceInstanceMap);
				}
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMainResourceError_CODE);
			return JSONObject.toJSONString(result);
		}

		if (null != resourceInstanceMapList
				&& resourceInstanceMapList.size() > 0) {
			result.setData(resourceInstanceMapList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	/**
	 * 查询资源实例可用性指标
	 * 
	 * @param name
	 * @param instance
	 * @return
	 */
	private String queryInstanceState(ResourceInstance instance) {
//		String state = InstanceStateEnum.NORMAL.toString();
		String state = InstanceStateEnum.UNKOWN.toString();
		if (InstanceLifeStateEnum.NOT_MONITORED.equals(instance.getLifeState())) {
			state = InstanceLifeStateEnum.NOT_MONITORED.toString();
		} else {
			InstanceStateData isd = instanceStateService.getState(instance
					.getId());
//			state = isd == null ? InstanceStateEnum.UNKOWN.toString() : isd.getState().toString();
			state = isd == null ? InstanceStateEnum.NORMAL.toString() : isd.getState().toString();
		}
		return state;
	}

	@Override
	public String getChildrenResourceById(String instanceId) {
		Result result = new Result();
		ResourceInstance instance = null;
		try {
			instance = resourceInstanceService.getResourceInstance(Long.parseLong(instanceId));
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceByInstanceIdError_CODE);
			return JSONObject.toJSONString(result);
		}

		if (null != instance) {
			List<Map<String, String>> listMap = null;
			List<ResourceInstance> childrenIns = instance.getChildren();
			try {
				listMap = setMetricValue(childrenIns);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(), e);
				result.setData("");
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryChildResourceMetricValueError_CODE);
				return JSONObject.toJSONString(result);
			}
			result.setData(listMap);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	@Override
	public String getResourceMetricByInstanceId(String instanceId) {
		Result result = new Result();
		List<Map<String, List<Map<String, Object>>>> metricList = new ArrayList<Map<String, List<Map<String, Object>>>>();
		Map<String, List<Map<String, Object>>> metricMaps = new HashMap<String, List<Map<String, Object>>>();
		try {
			Long insID = Long.parseLong(instanceId);
			ResourceInstance instance = resourceInstanceService
					.getResourceInstance(insID);
			// 信息指标List
			List<Map<String, Object>> infoMetricList = new ArrayList<Map<String, Object>>();
			// 性能指标List
			List<Map<String, Object>> perfMetricList = new ArrayList<Map<String, Object>>();
			// 可用性指标List
			List<Map<String, Object>> avaMetricList = new ArrayList<Map<String, Object>>();

			// 指标基本信息
			List<ProfileMetric> profileMetricList = profileService
					.getMetricByInstanceId(insID);
			for (int i = 0; profileMetricList != null
					&& i < profileMetricList.size(); i++) {
				ProfileMetric profileMetric = profileMetricList.get(i);
				Map<String, Object> metric = createMetric(instance,
						profileMetric);

				if (!metric.isEmpty()) {
					if (MetricTypeEnum.PerformanceMetric.equals(metric.get("type"))) {
						perfMetricList.add(metric);
					} else if (MetricTypeEnum.InformationMetric.equals(metric.get("type"))) {
						infoMetricList.add(metric);
					} else if (MetricTypeEnum.AvailabilityMetric.equals(metric.get("type"))) {
			            avaMetricList.add(metric);
			        }
				}
			}
			metricMaps.put("performanceMetric", perfMetricList);
			metricMaps.put("informationMetric", infoMetricList);
			metricMaps.put("availabilityMetric", avaMetricList);

			metricList.add(metricMaps);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceMetricValueError_CODE);
			return JSONObject.toJSONString(result);
		}
		if (null != metricList && metricList.size() > 0) {
			result.setData(metricList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	/**
	 * 封装一个指标的信息
	 * 
	 * @param instance
	 * @param profileMetric
	 * @return
	 */
	private Map<String, Object> createMetric(ResourceInstance instance,
			ProfileMetric profileMetric) {
		Map<String, Object> metric = new HashMap<String, Object>();
		String metricId = profileMetric.getMetricId();
		ResourceMetricDef rmd = capacityService.getResourceMetricDef(
				instance.getResourceId(), metricId);
		if (rmd != null && profileMetric.isMonitor()) {
			// id,名称
			
			metric.put("id", metricId);
			metric.put("text", rmd.getName());
			metric.put("unit", rmd.getUnit());
			metric.put("isAlarm", profileMetric.isAlarm());
			// 阈值
			List<ProfileThreshold> proThreList = profileMetric
					.getMetricThresholds();
			if (proThreList != null && proThreList.size() > 1) {
				metric.put("thresholds",
						createThresholds(proThreList, rmd.getUnit()));
			}
			// 指标状态
			switch (rmd.getMetricType()) {
			case PerformanceMetric:
				metric.put("type", MetricTypeEnum.PerformanceMetric);
				break;
			case InformationMetric:
				metric.put("type", MetricTypeEnum.InformationMetric);
				break;
		     case AvailabilityMetric:
		         metric.put("type", MetricTypeEnum.AvailabilityMetric);
		         break;
			default:
				metric.clear();
				break;
			}
		}
		return metric;
	}

	/**
	 * 创建指标域值字符串
	 * 
	 * @param proThreList
	 * @param unit
	 * @return
	 */
	private List<Map<Object, String>> createThresholds(List<ProfileThreshold> proThreList,
			String unit) {
		List<Map<Object, String>> threVal = new ArrayList<Map<Object,String>>();
		for (ProfileThreshold thre : proThreList) {
			if(thre.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Normal)){
				continue;
			}
			Map<Object, String> thresold = new HashMap<Object, String>();
			thresold.put("level", thre.getPerfMetricStateEnum().toString());
			thresold.put("expression", thre.getThresholdExpression());
			
			threVal.add(thresold);
		}
		return threVal;
	}

	@Override
	public List<ResourceBean> queryResources(String[] types) {
		List<ResourceBean> data = new ArrayList<>();
		for (int i = 0; i < types.length; i++) {
			try {
				List<ResourceInstance> list = this.resourceInstanceService
						.getParentInstanceByCategoryId(types[i]);
				if (list != null) {
					for (ResourceInstance r : list) {
						ResourceBean d = new ResourceBean();
						d.setId(r.getId());
						d.setName(r.getName());
						d.setIp(r.getShowIP());
						d.setType(types[i]);
						String[] sysObjectIds = r
								.getModulePropBykey("sysObjectID");
						if (sysObjectIds != null && sysObjectIds.length > 0
								&& sysObjectIds[0] != null) {
							DeviceType dt = this.capacityService
									.getDeviceType(sysObjectIds[0]);
							if (dt != null && dt.getVendorName() != null) {
								d.setVendorName(dt.getVendorName());
							}
						}
						if (!data.contains(d)) {
							data.add(d);
						}
					}
				}
			} catch (InstancelibException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	@Override
	public List<InterfaceBean> getIntercfaces(long resourceId) {
		List<InterfaceBean> data = new ArrayList<>();
		try {
			ResourceInstance resourceInstance = resourceInstanceService
					.getResourceInstance(resourceId);
			if (resourceInstance != null
					&& resourceInstance.getChildren() != null) {
				List<ResourceInstance> children = resourceInstance
						.getChildren();
				for (ResourceInstance in : children) {
					InterfaceBean inter = new InterfaceBean();
					data.add(inter);
					inter.setId(in.getId());
					inter.setName(in.getName());
					String[] values = in.getModulePropBykey("ifIndex");
					if (values != null) {
						inter.setIndex(Integer.parseInt(values[0]));
						String[] ifSpeeds = in.getModulePropBykey("ifSpeed");
						inter.setIfSpeed(ifSpeeds == null
								|| ifSpeeds.length == 0 ? null
								: (new BigDecimal(ifSpeeds[0])).longValue());
					}
					InstanceStateData isd = instanceStateService.getState(in
							.getId());
					if (isd != null) {
						if (isd.getState() != null) {
							if (isd.getState() != InstanceStateEnum.CRITICAL) {
//									&& isd.getState() != InstanceStateEnum.UNKOWN
								inter.setAvailable(true);
							}
						}
					}
				}
			}
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public String getSwitchPortNumberByInstanceId(String instanceId) {
		Result result = new Result();
		
		Long resourceInstanceId = Long.parseLong(instanceId);
		
		try {
			ResourceInstance switchInstance = resourceInstanceService.getResourceInstance(resourceInstanceId);
			if(switchInstance == null){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			if(!(switchInstance.getCategoryId().equals(CapacityConst.SWITCH))){
				//不属于交换机类型,不给予查询
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QUERYISNOTSWITCH_CODE);
				return JSONObject.toJSONString(result);
			}
			//获取交换机的所有子资源
			int interfaceInstanceNumber = 0;
			List<ResourceInstance> childrenInstances = switchInstance.getChildren();
			
			if(childrenInstances == null || childrenInstances.size() <= 0){
				result.setData(interfaceInstanceNumber);
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
				return JSONObject.toJSONString(result);
			}
			
			for(ResourceInstance child : childrenInstances){
				if(child.getChildType().equals(ResourceTypeConsts.TYPE_NETINTERFACE)){
					interfaceInstanceNumber++;
				}
			}
			
			result.setData(interfaceInstanceNumber);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			
		} catch (InstancelibException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ERROR_CODE);
			return JSONObject.toJSONString(result);
		}
		
		// TODO Auto-generated method stub
		return JSONObject.toJSONString(result);
	}

	@Override
	public String getSwitchStatusByInstanceId(String instanceId) {
		
		Result result = new Result();
		
		Long resourceInstanceId = Long.parseLong(instanceId);
		
		try {
			ResourceInstance switchInstance = resourceInstanceService.getResourceInstance(resourceInstanceId);
			if(switchInstance == null){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			if(!(switchInstance.getCategoryId().equals(CapacityConst.SWITCH))){
				//不属于交换机类型,不给予查询
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QUERYISNOTSWITCH_CODE);
				return JSONObject.toJSONString(result);
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ERROR_CODE);
			return JSONObject.toJSONString(result);
		}
		
		InstanceStateData state = instanceStateService.getState(resourceInstanceId);
		if(state == null){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		InstanceStateEnum stateEnum = state.getState();
		
		if(stateEnum.equals(InstanceStateEnum.UNKOWN)){
			result.setData("未连接");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		}else{
			result.setData("正常");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		}

		
		return JSONObject.toJSONString(result);
		
	}

	@Override
	public String getPortStatusByPortInstanceId(String instanceId) {
		Result result = new Result();
		
		Long resourceInstanceId = Long.parseLong(instanceId);

		try {
			ResourceInstance switchInstance = resourceInstanceService.getResourceInstance(resourceInstanceId);
			if(switchInstance == null){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			if(!(switchInstance.getChildType().equals(ResourceTypeConsts.TYPE_NETINTERFACE)) 
					|| !(switchInstance.getParentInstance().getCategoryId().equals(CapacityConst.SWITCH))){
				//不属于接口类型,不给予查询
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QUERYISNOTINTERFACE_CODE);
				return JSONObject.toJSONString(result);
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ERROR_CODE);
			return JSONObject.toJSONString(result);
		}
		
		InstanceStateData state = instanceStateService.getState(resourceInstanceId);
		if(state == null){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		InstanceStateEnum stateEnum = state.getState();
		
		if(stateEnum.equals(InstanceStateEnum.UNKOWN)){
			result.setData("未启用");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		}else if(stateEnum.equals(InstanceStateEnum.NORMAL)){
			result.setData("正常");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		}else{
			result.setData("故障");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		}

		
		return JSONObject.toJSONString(result);
	}

	@Override
	public String getSpeedByPortInstanceId(String instanceId) {
		
		Result result = new Result();
		
		Long resourceInstanceId = Long.parseLong(instanceId);

		ResourceInstance switchInstance = null;
		
		try {
			switchInstance = resourceInstanceService.getResourceInstance(resourceInstanceId);
			if(switchInstance == null){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			if(!(switchInstance.getChildType().equals(ResourceTypeConsts.TYPE_NETINTERFACE)) 
					|| !(switchInstance.getParentInstance().getCategoryId().equals(CapacityConst.SWITCH))){
				//不属于交换机类型,不给予查询
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QUERYISNOTSWITCH_CODE);
				return JSONObject.toJSONString(result);
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ERROR_CODE);
			return JSONObject.toJSONString(result);
		}
		
		MetricRealtimeDataQuery metricRealtimeDataQuery = new MetricRealtimeDataQuery();
		metricRealtimeDataQuery.setInstanceID(new long[]{resourceInstanceId});
		metricRealtimeDataQuery.setMetricID(new String[]{IF_IN_OCTETS_SPEED,IF_OUT_OCTETS_SPEED});
		
		//获取性能指标信息
		Page<Map<String,?>,MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(metricRealtimeDataQuery, 0, 1);
		
		if(page == null){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		List<Map<String,?>> realTimeDataList = page.getDatas();
		
		if(realTimeDataList == null || realTimeDataList.size() <= 0){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		Map<String,?> dataMap = realTimeDataList.get(0);
		
		if(dataMap == null || dataMap.size() <= 0){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		Map<String, String> resultMap = new HashMap<String, String>();
		
		ResourceMetricDef metricDef = capacityService.getResourceMetricDef(switchInstance.getResourceId(), IF_IN_OCTETS_SPEED);
		resultMap.put("unit", metricDef.getUnit());
		
		double speed = 0.0;
		
		Set<String> iterator = dataMap.keySet();
		for(String key : iterator){
			switch (key) {
			case IF_IN_OCTETS_SPEED:
				//接受速率
				speed += Double.parseDouble(dataMap.get(key).toString());
				break ;
			case IF_OUT_OCTETS_SPEED:
				//发送速率
				speed += Double.parseDouble(dataMap.get(key).toString());
				break ;
			default:
				break;
			}
		}
		
		resultMap.put("speed", speed + "");
		
		result.setData(resultMap);
		result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		
		return JSONObject.toJSONString(result);
	}

	@Override
	public String getSwitchInfoByInstanceId(String instanceId) {

		Result result = new Result();
		
		Long resourceInstanceId = Long.parseLong(instanceId);
		
		try {
			ResourceInstance switchInstance = resourceInstanceService.getResourceInstance(resourceInstanceId);
			if(switchInstance == null){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			if(!(switchInstance.getCategoryId().equals(CapacityConst.SWITCH))){
				//不属于交换机类型,不给予查询
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QUERYISNOTSWITCH_CODE);
				return JSONObject.toJSONString(result);
			}
			
			//获取sysoid模型属性
			String[] sysOid = switchInstance.getModulePropBykey(MetricIdConsts.SYS_OBJECT_ID);
			
			if(sysOid == null || sysOid.length <= 0){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			
			String oid = sysOid[0];
			
			DeviceType[] allTypes = capacityService.getAllDeviceTypes();
			
			if(allTypes == null || allTypes.length <= 0){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			
			Map<String, String> resultMap = new HashMap<String, String>();
			
			for(DeviceType type : allTypes){
				if(type.getSysOid().equals(oid)){
					
					//找到匹配的oid
					resultMap.put("vendorName", type.getVendorName());
					resultMap.put("modelNumber", type.getModelNumber());
					resultMap.put("sysOid", oid);
					resultMap.put("osType", type.getOsType());
					resultMap.put("vendorNameEn", type.getVendorNameEn());
					resultMap.put("series", type.getSeries());
					resultMap.put("type", type.getType().toString());
					
					break;
					
				}
			}
			
			result.setData(resultMap);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ERROR_CODE);
			return JSONObject.toJSONString(result);
		}
		
		return JSONObject.toJSONString(result);
	}
	
	
	
	
	@Override
	public String getResourceBaseInfoByCategoryId(String parentCategrayId,String childrenCategrayId){
		Result result = new Result();
		if(null==parentCategrayId || "".equals(parentCategrayId)){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ParentCatagoryIdNull_CODE);
			return JSONObject.toJSONString(result);
		}
		
		CategoryDef categoryDef = capacityService.getCategoryById(parentCategrayId);
		if(null==categoryDef){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ParentCatagoryIdError_CODE);
			return JSONObject.toJSONString(result);
		}
		
		List<ResourceInstance> riList ;
		CategoryDef[] cgds = categoryDef.getChildCategorys();
		
		if(null==cgds || cgds.length<1){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ParentCatagoryIdError_CODE);
			return JSONObject.toJSONString(result);
		}
		
		HashSet<String> cateIds = new HashSet<String>();
		if(null==childrenCategrayId || "".equals(childrenCategrayId)){
			//按主类型查
			for(CategoryDef cd:cgds){
				cateIds.add(cd.getId());
			}
			try {
				riList = resourceInstanceService.getParentInstanceByCategoryIds(cateIds);
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceByCategoryIDError_CODE);
				return JSONObject.toJSONString(result);
			}
		}else{
			boolean flag = false;
			for(CategoryDef cd:cgds){
				if(childrenCategrayId.equals(cd.getId())){
					flag = true;
					cateIds.add(cd.getId());
					break;
				}
			}
			if(flag){
				try {
					riList = resourceInstanceService.getParentInstanceByCategoryIds(cateIds);
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
					result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceByCategoryIDError_CODE);
					return JSONObject.toJSONString(result);
				}
			}else{
				result.setResultcodeEnum(ResultCodeEnum.RESULT_ParentAndChildrenCatagoryIdMissMacth_CODE);
				return JSONObject.toJSONString(result);
			}
		}
		
		if(null==riList || riList.size()<1){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		List<Map<String,String>> resultLsit = new ArrayList<Map<String,String>>();
		for(ResourceInstance riA:riList){
			Map<String,String> map = getResultMap(riA);
			resultLsit.add(map);
		}
		
		result.setData(resultLsit);
		result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		return JSONObject.toJSONString(result);
	}
	
	@Override
	public String getResourceBaseInfoByResourceGroupId(Long resourceGroupId,String parentCategrayId,String  childrenCategrayId){
		Result result = new Result();
		if(null==resourceGroupId || "".equals(resourceGroupId)){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ParentCatagoryIdNull_CODE);
			return JSONObject.toJSONString(result);
		}
		
		List<String> instanceIds = customResourceGroupApi.getCustomGroupInstanceIds(resourceGroupId);
		List<Long> instanceIds_Long = new ArrayList<Long>();
		for(String str:instanceIds){
			instanceIds_Long.add(Long.parseLong(str));
		}
		if(null==instanceIds || instanceIds.size()<1){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		List<ResourceInstance>  riListAfter = new ArrayList<ResourceInstance>();
		try {
			List<ResourceInstance> riList = resourceInstanceService.getResourceInstances(instanceIds_Long);
			
			if(null==riList || riList.size()<1){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceError_CODE);
				return JSONObject.toJSONString(result);
			}
			
			if(null!=parentCategrayId && !"".equals(parentCategrayId) && null!=childrenCategrayId && !"".equals(childrenCategrayId)){
				//检测主子类型匹配,返回
				for(ResourceInstance ri:riList){
					if(ri.getParentCategoryId().equals(parentCategrayId) && ri.getCategoryId().equals(childrenCategrayId)){
						riListAfter.add(ri);
					}
				}
			}else if(null!=parentCategrayId && !"".equals(parentCategrayId)){
				//按主类型返回
				for(ResourceInstance ri:riList){
					if(ri.getParentCategoryId().equals(parentCategrayId)){
						riListAfter.add(ri);
					}
				}
			}else if(null!=childrenCategrayId && !"".equals(childrenCategrayId)){
				//按子类型返回
				for(ResourceInstance ri:riList){
					if(ri.getCategoryId().equals(childrenCategrayId)){
						riListAfter.add(ri);
					}
				}
			}else{
				//返回全部
				riListAfter = riList;
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceError_CODE);
			return JSONObject.toJSONString(result);
		}
		
		if(null==riListAfter || riListAfter.size()<1){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		List<Map<String,String>> resultLsit = new ArrayList<Map<String,String>>();
		Set<String> instanceIdSet = new HashSet<String>();
		for(ResourceInstance riA:riListAfter){
			//去除未监控,重复资源
			if(riA.getLifeState() != InstanceLifeStateEnum.MONITORED){
				continue;
			}
			if(instanceIdSet.contains(String.valueOf(riA.getId()))){
				continue;
			}
			instanceIdSet.add(String.valueOf(riA.getId()));
			
			Map<String,String> map = getResultMap(riA);
			resultLsit.add(map);
		}
		
		result.setData(resultLsit);
		result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		return JSONObject.toJSONString(result);
	}
	
	@Override
	public String getMetricDataByInstanceIds(Long[] instanceIds ,String[] metricIds){
		Result result = new Result();
		if(instanceIds.length<1 || metricIds.length<1){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ParameterError_CODE);
			return JSONObject.toJSONString(result);
		}else if(instanceIds.length>100 || metricIds.length>20){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ParameterNumError_CODE);
			return JSONObject.toJSONString(result);
		}
		
		/*
		 * 1.根据instanceId策略,分拆指标
		 * */
		Map<String,Map<String,Set<ResourceMetricDef>>> categroyMap = new HashMap<String,Map<String,Set<ResourceMetricDef>>>();
			try {
				for(Long instanceId:instanceIds){
					String resourceId = "";
					ProfileInfo pfInfoMain = profileService.getBasicInfoByResourceInstanceId(instanceId);
					
					//筛选类型对应的指标
					if(null!=pfInfoMain){
						Profile pfMain = profileService.getProfilesById(pfInfoMain.getProfileId());
						resourceId = pfInfoMain.getResourceId();
						if(categroyMap.containsKey("parentResource"+resourceId)){
							continue;
						}
						
						ResourceDef rdfMain = capacityService.getResourceDefById(pfInfoMain.getResourceId());
				    	if(null!=rdfMain){
			    			MetricSetting mSet = pfMain.getMetricSetting();
			    			ResourceMetricDef[] rmdf = rdfMain.getMetricDefs();
			    			
			    			Set<ResourceMetricDef> perforList = new HashSet<ResourceMetricDef>();
			    			Set<ResourceMetricDef> avalibList = new HashSet<ResourceMetricDef>();
			    			Set<ResourceMetricDef> infoList = new HashSet<ResourceMetricDef>();
		    				for(ProfileMetric pm:mSet.getMetrics()){
		    					for(String metricStr:metricIds){
		    						if(pm.getMetricId().equals(metricStr) && pm.isMonitor()){
		    							for(ResourceMetricDef rmdef:rmdf){
		    								if(metricStr.equals(rmdef.getId())){
		    									if(rmdef.getMetricType()==MetricTypeEnum.AvailabilityMetric){
		    										avalibList.add(rmdef);
		    									}else if(rmdef.getMetricType()==MetricTypeEnum.InformationMetric){
		    										infoList.add(rmdef);
		    									}else if(rmdef.getMetricType()==MetricTypeEnum.PerformanceMetric){
		    										perforList.add(rmdef);
		    									}
		    									break;
		    								}
		    							}
		    							break;
			    					}
		    					}
		    				}
		    				//主资源对应的指标
	    					Map<String,Set<ResourceMetricDef>> cateMap = new HashMap<String,Set<ResourceMetricDef>>();
	    					cateMap.put("AvailabilityMetric", avalibList);
	    					cateMap.put("InformationMetric", infoList);
	    					cateMap.put("PerformanceMetric", perforList);
	        				categroyMap.put("parentResource"+resourceId, cateMap);
				    	}
					
				    List<Profile> pfList = pfMain.getChildren();
				    if(null!=pfList){
				    	for(Profile pFile:pfList){
				    		ProfileInfo pfInfo = pFile.getProfileInfo();
				    		//筛选类型对应的指标
							if(null!=pfInfo){
								ResourceDef rdfChild = capacityService.getResourceDefById(pfInfo.getResourceId());
						    	if(null!=rdfChild){
					    			MetricSetting mSet = pFile.getMetricSetting();
					    			ResourceMetricDef[] rmdf = rdfChild.getMetricDefs();
					    			
					    			Set<ResourceMetricDef> perforList = new HashSet<ResourceMetricDef>();
					    			Set<ResourceMetricDef> avalibList = new HashSet<ResourceMetricDef>();
					    			Set<ResourceMetricDef> infoList = new HashSet<ResourceMetricDef>();
				    				for(ProfileMetric pm:mSet.getMetrics()){
				    					for(String metricStr:metricIds){
				    						if(pm.getMetricId().equals(metricStr) && pm.isMonitor()){
				    							for(ResourceMetricDef rmdef:rmdf){
				    								if(metricStr.equals(rmdef.getId())){
				    									if(rmdef.getMetricType()==MetricTypeEnum.AvailabilityMetric){
				    										avalibList.add(rmdef);
				    									}else if(rmdef.getMetricType()==MetricTypeEnum.InformationMetric){
				    										infoList.add(rmdef);
				    									}else if(rmdef.getMetricType()==MetricTypeEnum.PerformanceMetric){
				    										perforList.add(rmdef);
				    									}
				    									break;
				    								}
				    							}
				    							break;
					    					}
				    					}
				    				}
				    				//子资源对应的指标
			    					Map<String,Set<ResourceMetricDef>> cateMap = new HashMap<String,Set<ResourceMetricDef>>();
			    					cateMap.put("AvailabilityMetric", avalibList);
			    					cateMap.put("InformationMetric", infoList);
			    					cateMap.put("PerformanceMetric", perforList);
			    					categroyMap.put(resourceId+rdfChild.getType(), cateMap);
						    	}
							}
					    }
				    }
				 }
			  }
			} catch (ProfilelibException e) {
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceProfileError_CODE);
				return JSONObject.toJSONString(result);
			}
			    
			/*
			 * 2.获取所有需查数据的instanceId,并查询数据
			 * */
			    List<Long> allQueryInstanceId = new ArrayList<Long>();
			    List<Long> instanceList = new ArrayList<Long>();
			    List<ResourceInstance> riList = new ArrayList<ResourceInstance>();
			    Map<String,List<ResourceInstance>> parentResourceInfoMap = new HashMap<String,List<ResourceInstance>>();
			    for(Long instanceId:instanceIds){
			    	instanceList.add(instanceId);
			    }
			    try {
			    	riList = resourceInstanceService.getResourceInstances(instanceList);
			    	
			    	for(ResourceInstance ri:riList){
			    		allQueryInstanceId.add(ri.getId());
			    		List<ResourceInstance> riChildList = ri.getChildren();
			    		if(null==riChildList) continue;
			    		for(ResourceInstance ric:riChildList){
			    			allQueryInstanceId.add(ric.getId());
			    		}
			    		parentResourceInfoMap.put(String.valueOf(ri.getId()), riChildList);
			    	}
				} catch (InstancelibException e) {
					result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceError_CODE);
					return JSONObject.toJSONString(result);
				}
			    
			    long[] instanceIdArray = new long[allQueryInstanceId.size()] ;
			    for(int x=0;x<allQueryInstanceId.size();x++){
			    	instanceIdArray[x] = allQueryInstanceId.get(x).longValue();
			    }
			    MetricRealtimeDataQuery mr = new MetricRealtimeDataQuery();
				mr.setMetricID(metricIds);
				mr.setInstanceID(instanceIdArray);
				//获取性能指标
				List<Map<String, ?>> perforMetricDataList = metricDataService.queryRealTimeMetricData(mr);
				if(null==perforMetricDataList){
					perforMetricDataList = new ArrayList<Map<String, ?>>();
				}
				
				//获取信息指标    查询信息指标需要过滤
//				List<MetricData> infoList = metricDataService.getMetricInfoDatas(instanceIdArray, metricIds);
				List<MetricData> infoList = infoMetricQueryAdaptService.getMetricInfoDatas(instanceIdArray, metricIds);
				if(null==infoList){
					infoList = new ArrayList<MetricData>();
				}
				
				List<String> metricList = new ArrayList<String>();
				for(String str:metricIds){
					metricList.add(str);
				}
				//获取可用性指标
				List<MetricStateData> avalibList = metricStateService.findMetricState(allQueryInstanceId, metricList);
				if(null==avalibList){
					avalibList = new ArrayList<MetricStateData>();
				}
			    
				Map<String,Map<String,?>> perforMetricDataMap = new HashMap<String,Map<String,?>>();
				Map<String,List<MetricData>> infoMap = new HashMap<String,List<MetricData>>();
				Map<String,List<MetricStateData>> avalibMap = new HashMap<String,List<MetricStateData>>();
				
				//将list转化为map,方便通过循环资源取数据
				for(Map<String, ?> map:perforMetricDataList){
					if(null!=map.get("instanceid") && !"".equals(map.get("instanceid"))){
						String instanceKey = map.get("instanceid").toString();
						perforMetricDataMap.put(instanceKey, map);
					}
				}
				for(MetricData md:infoList){
					String instanceKey = String.valueOf(md.getResourceInstanceId());
					if(infoMap.containsKey(instanceKey)){
						infoMap.get(instanceKey).add(md);
					}else{
						List<MetricData> infoTempList = new ArrayList<MetricData>();
						infoTempList.add(md);
						infoMap.put(instanceKey, infoTempList);
					}
				}
				for(MetricStateData msd:avalibList){
					String instanceKey = String.valueOf(msd.getInstanceID());
					if(avalibMap.containsKey(instanceKey)){
						avalibMap.get(instanceKey).add(msd);
					}else{
						List<MetricStateData> avalibTempList = new ArrayList<MetricStateData>();
						avalibTempList.add(msd);
						avalibMap.put(instanceKey, avalibTempList);
					}
				}
				
				
				/*
				 * 3.组装数据
				 * */
				List<Map<String,Object>> resutlList = new ArrayList<Map<String,Object>>();
				for(ResourceInstance ri:riList){
					String instanceId = String.valueOf(ri.getId());
					String resourceId = ri.getResourceId();
					Map<String,Object> resultListItem = new HashMap<String,Object>();
					List<Map<String,Object>> resultMetricList = new ArrayList<Map<String,Object>>();
					resultListItem.put("instanceId", instanceId);
					resultListItem.put("instanceName", ri.getName());
					resultListItem.put("instanceShowName", ri.getShowName());

					Map<String,Set<ResourceMetricDef>> parentInsMetric = categroyMap.get("parentResource"+resourceId);
					if(null==parentInsMetric){
						break;
					}
					Set<ResourceMetricDef> parentInsMetricPer = new HashSet<ResourceMetricDef>();
					Set<ResourceMetricDef> parentInsMetricInfo = new HashSet<ResourceMetricDef>();
					Set<ResourceMetricDef> parentInsMetricAval = new HashSet<ResourceMetricDef>();
					
					if(null!=parentInsMetric.get("PerformanceMetric")){
						parentInsMetricPer = parentInsMetric.get("PerformanceMetric");
					}
					if(null!=parentInsMetric.get("InformationMetric")){
						parentInsMetricInfo = parentInsMetric.get("InformationMetric");
					}
					if(null!=parentInsMetric.get("AvailabilityMetric")){
						parentInsMetricAval = parentInsMetric.get("AvailabilityMetric");
					}
					
					Map<String,?> valuePer = perforMetricDataMap.get(instanceId);
					List<MetricData> valueInfo = infoMap.get(instanceId);
					List<MetricStateData> valueAvalib = avalibMap.get(instanceId);
					
					for(ResourceMetricDef rmd:parentInsMetricPer){
						if(null==rmd || rmd.getId()==null || "".equals(rmd.getId())){
							continue;
						}
						String metricIdTemp = rmd.getId();
						Map<String,Object> resultListItemIn = new HashMap<String,Object>();
						resultListItemIn.put("metricId", metricIdTemp);
						resultListItemIn.put("ifChildrenMetric", false);
						resultListItemIn.put("metricName", rmd.getName());
						resultListItemIn.put("metricType", rmd.getMetricType().name());
						
						if(null!=valuePer && null!=valuePer.get(metricIdTemp)){
							resultListItemIn.put("metricData", valuePer.get(metricIdTemp).toString());
							String dateStr = valuePer.get(metricIdTemp+"CollectTime").toString();
							Date date = dateFormat(dateStr);
							resultListItemIn.put("collect_time", date.getTime());
							resultListItemIn.put("unit", rmd.getUnit());
						}
						
						resultMetricList.add(resultListItemIn);
					}
					for(ResourceMetricDef rmd:parentInsMetricInfo){
						if(null==rmd || rmd.getId()==null || "".equals(rmd.getId())){
							continue;
						}
						String metricIdTemp = rmd.getId();
						Map<String,Object> resultListItemIn = new HashMap<String,Object>();
						resultListItemIn.put("metricId", metricIdTemp);
						resultListItemIn.put("ifChildrenMetric", false);
						resultListItemIn.put("metricName", rmd.getName());
						resultListItemIn.put("metricType", rmd.getMetricType().name());
						
						for(MetricData md:valueInfo){
							if(md.getMetricId().equals(metricIdTemp)){
								resultListItemIn.put("metricData", parseArrayToString(md.getData()));
								resultListItemIn.put("collect_time", String.valueOf(md.getCollectTime().getTime()));
								resultListItemIn.put("unit", rmd.getUnit());
							}
						}
						
						resultMetricList.add(resultListItemIn);
					}
					for(ResourceMetricDef rmd:parentInsMetricAval){
						if(null==rmd || rmd.getId()==null || "".equals(rmd.getId())){
							continue;
						}
						String metricIdTemp = rmd.getId();
						Map<String,Object> resultListItemIn = new HashMap<String,Object>();
						resultListItemIn.put("metricId", metricIdTemp);
						resultListItemIn.put("ifChildrenMetric", false);
						resultListItemIn.put("metricName", rmd.getName());
						resultListItemIn.put("metricType", rmd.getMetricType().name());
						
						for(MetricStateData msd:valueAvalib){
							if(msd.getMetricID().equals(metricIdTemp)){
								resultListItemIn.put("metricData", msd.getState().name());
								resultListItemIn.put("collect_time", String.valueOf(msd.getCollectTime().getTime()));
								resultListItemIn.put("unit", "");
							}
						}
						resultMetricList.add(resultListItemIn);
					}
					
					resultListItem.put("metricInfo", resultMetricList);
					resutlList.add(resultListItem);
					
					//如果有子资源,拼装子资源数据
					List<ResourceInstance> childrenList = parentResourceInfoMap.get(String.valueOf(ri.getId()));
					if(null==childrenList){
						continue;
					}
					for(ResourceInstance ric:childrenList){
						String instanceIdChild = String.valueOf(ric.getId());
						String instanceNameChild = ric.getName();
						String instanceShowNameChild = ric.getShowName();
						String resourceTypeChild = ric.getChildType();
						Map<String,Set<ResourceMetricDef>> childInsMetric = categroyMap.get(resourceId+resourceTypeChild);
						if(null==childInsMetric){
							continue;
						}
						Set<ResourceMetricDef> childInsMetricPer = new HashSet<ResourceMetricDef>();
						Set<ResourceMetricDef> childInsMetricInfo = new HashSet<ResourceMetricDef>();
						Set<ResourceMetricDef> childInsMetricAval = new HashSet<ResourceMetricDef>();
						
						if(null!=childInsMetric.get("PerformanceMetric")){
							childInsMetricPer = childInsMetric.get("PerformanceMetric");
						}
						if(null!=childInsMetric.get("InformationMetric")){
							childInsMetricInfo = childInsMetric.get("InformationMetric");
						}
						if(null!=childInsMetric.get("AvailabilityMetric")){
							childInsMetricAval = childInsMetric.get("AvailabilityMetric");
						}
						
						
						Map<String,?> valuePerChild = perforMetricDataMap.get(instanceIdChild);
						List<MetricData> valueInfoChild = infoMap.get(instanceIdChild);
						List<MetricStateData> valueAvalibChild = avalibMap.get(instanceIdChild);
						
						for(ResourceMetricDef rmd:childInsMetricPer){
							if(null==rmd || rmd.getId()==null || "".equals(rmd.getId())){
								continue;
							}
							String metricIdTemp = rmd.getId();
							Map<String,Object> resultListItemIn = new HashMap<String,Object>();
							List<Map<String ,String>> resultListItemInIn = new ArrayList<Map<String ,String>>();
							boolean flagPer = true;
							for(Map<String,Object> map:resultMetricList){
								if(metricIdTemp.equals(map.get("metricId")) && "true".equals(map.get("ifChildrenMetric").toString())){
									resultListItemIn = map;
									resultListItemInIn = (List<Map<String ,String>>)map.get("rows");
									flagPer = false;
									break;
								}
							}
							if(flagPer){
								resultListItemIn.put("metricId", metricIdTemp);
								resultListItemIn.put("ifChildrenMetric", true);
								resultListItemIn.put("metricName", rmd.getName());
								resultListItemIn.put("metricType", rmd.getMetricType().name());
							}
							
							Map<String ,String> valuePerMap = new HashMap<String,String>();
							valuePerMap.put("instanceId", instanceIdChild);
							valuePerMap.put("instanceName", instanceNameChild);
							valuePerMap.put("instanceShowName", instanceShowNameChild);
							if(valuePerChild!=null && valuePerChild.get(metricIdTemp)!=null){
								valuePerMap.put("metricData", valuePerChild.get(metricIdTemp).toString());
								String dateStr = valuePerChild.get(metricIdTemp+"CollectTime").toString();
								Date date = dateFormat(dateStr);
								valuePerMap.put("collect_time", String.valueOf(date.getTime()));
								valuePerMap.put("unit", rmd.getUnit());
							}
							resultListItemInIn.add(valuePerMap);
							resultListItemIn.put("rows", resultListItemInIn);
							if(flagPer){
								resultMetricList.add(resultListItemIn);
							}
						}
						for(ResourceMetricDef rmd:childInsMetricInfo){
							if(null==rmd || rmd.getId()==null || "".equals(rmd.getId())){
								continue;
							}
							String metricIdTemp = rmd.getId();
							Map<String,Object> resultListItemIn = new HashMap<String,Object>();
							List<Map<String ,String>> resultListItemInIn = new ArrayList<Map<String ,String>>();
							boolean flagInfo = true;
							for(Map<String,Object> map:resultMetricList){
								if(metricIdTemp.equals(map.get("metricId")) && "true".equals(map.get("ifChildrenMetric").toString())){
									resultListItemIn = map;
									resultListItemInIn = (List<Map<String ,String>>)map.get("rows");
									flagInfo = false;
									break;
								}
							}
							if(flagInfo){
								resultListItemIn.put("metricId", metricIdTemp);
								resultListItemIn.put("ifChildrenMetric", true);
								resultListItemIn.put("metricName", rmd.getName());
								resultListItemIn.put("metricType", rmd.getMetricType().name());
							}
							
							Map<String ,String> valueInfoMap = new HashMap<String,String>();
							valueInfoMap.put("instanceId", instanceIdChild);
							valueInfoMap.put("instanceName", instanceNameChild);
							valueInfoMap.put("instanceShowName", instanceShowNameChild);
							for(MetricData md:valueInfoChild){
								if(md.getMetricId().equals(metricIdTemp)){
									valueInfoMap.put("metricData", parseArrayToString(md.getData()));
									valueInfoMap.put("collect_time", String.valueOf(md.getCollectTime().getTime()));
									valueInfoMap.put("unit", rmd.getUnit());
								}
							}
							resultListItemInIn.add(valueInfoMap);
							resultListItemIn.put("rows", resultListItemInIn);
							if(flagInfo){
								resultMetricList.add(resultListItemIn);
							}
						}
						for(ResourceMetricDef rmd:childInsMetricAval){
							if(null==rmd || rmd.getId()==null || "".equals(rmd.getId())){
								continue;
							}
							String metricIdTemp = rmd.getId();
							Map<String,Object> resultListItemIn = new HashMap<String,Object>();
							List<Map<String ,String>> resultListItemInIn = new ArrayList<Map<String ,String>>();
							boolean flagAva = true;
							for(Map<String,Object> map:resultMetricList){
								if(metricIdTemp.equals(map.get("metricId")) && "true".equals(map.get("ifChildrenMetric").toString())){
									resultListItemIn = map;
									resultListItemInIn = (List<Map<String ,String>>)map.get("rows");
									flagAva = false;
									break;
								}
							}
							if(flagAva){
								resultListItemIn.put("metricId", metricIdTemp);
								resultListItemIn.put("ifChildrenMetric", true);
								resultListItemIn.put("metricName", rmd.getName());
								resultListItemIn.put("metricType", rmd.getMetricType().name());
							}
							
							Map<String ,String> valueAvaMap = new HashMap<String,String>();
							valueAvaMap.put("instanceId", instanceIdChild);
							valueAvaMap.put("instanceName", instanceNameChild);
							valueAvaMap.put("instanceShowName", instanceShowNameChild);
							for(MetricStateData msd:valueAvalibChild){
								if(msd.getMetricID().equals(metricIdTemp)){
									valueAvaMap.put("metricData", msd.getState().name());
									valueAvaMap.put("collect_time", String.valueOf(msd.getCollectTime().getTime()));
									valueAvaMap.put("unit", "");
								}
							}
							resultListItemInIn.add(valueAvaMap);
							resultListItemIn.put("rows", resultListItemInIn);
							if(flagAva){
								resultMetricList.add(resultListItemIn);
							}
						}
					}
					
				}
				
				result.setData(resutlList);
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
				return JSONObject.toJSONString(result);
	}
	
	private static String parseArrayToString(String[] strArr){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<strArr.length;i++){
			if(i==0){
				sb.append(strArr[i]);
			}else{
				sb.append(","+strArr[i]);
			}
		}
		return sb.toString();
	}
	

	@Override
	public String getResourceStateByInstanceIds(Long[] instanceids){
		Result result = new Result();
		if(null==instanceids || instanceids.length<1){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ParameterError_CODE);
			return JSONObject.toJSONString(result);
		}
		List<Long> idList = new ArrayList<Long>();
		for(Long id:instanceids){
			idList.add(id);
		}
		List<InstanceStateData> isdList = instanceStateService.findStates(idList);
		if(null==isdList || isdList.size()==0){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceStateError_CODE);
			return JSONObject.toJSONString(result);
		}
		List<ResourceInstance>  riList = new ArrayList<ResourceInstance>();
		try {
			riList = resourceInstanceService.getResourceInstances(idList);
			if(null==riList || riList.size()==0){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_ParameterError_CODE);
				return JSONObject.toJSONString(result);
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(), e);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryResourceError_CODE);
			return JSONObject.toJSONString(result);
		}
		
		List<Map<String,String>> resourceInfoList = new ArrayList<Map<String,String>>();
		for(ResourceInstance ri:riList){
			Map<String,String> resourceInfo = new HashMap<String,String>();
			resourceInfo.put("instanceId", String.valueOf(ri.getId()));
			resourceInfo.put("ip", ri.getShowIP());
			resourceInfo.put("name", ri.getName());
			resourceInfo.put("parentCategrayId", ri.getParentCategoryId());
			resourceInfo.put("childrenCategrayId", ri.getCategoryId());
			resourceInfo.put("resourceId", ri.getResourceId());
			resourceInfo.put("showName", ri.getShowName());
			
			for(InstanceStateData isd:isdList){
				String availabState = "未知",alarmState="UNKNOWN";
				if(ri.getId()==isd.getInstanceID()){
					switch (isd.getState().name()) {
						case "CRITICAL":
						case "CRITICAL_NOTHING":
							availabState= "不可用";
							break;
						case "NORMAL":
						case "SERIOUS":
						case "WARN":
						case "NORMAL_CRITICAL":
						case "NORMAL_UNKNOWN":
						case "NORMAL_NOTHING":
							availabState= "可用";
							break;
						default :
							availabState= "未知";
						    break;
					}
					switch (isd.getState().name()) {
						case "CRITICAL":
						case "CRITICAL_NOTHING":
							alarmState= "CRITICAL";
							break;
						case "SERIOUS":
							alarmState= "SERIOUS";
							break;
						case "WARN":
							alarmState= "WARN";
							break;
						case "NORMAL":
						case "NORMAL_CRITICAL":
						case "NORMAL_UNKNOWN":
						case "NORMAL_NOTHING":
							alarmState= "NORMAL";
							break;
						default :
							alarmState= "UNKNOWN";
						    break;
					}
				}
				resourceInfo.put("availabState", availabState);
				resourceInfo.put("alarmState", alarmState);
			}
			resourceInfoList.add(resourceInfo);
		}
		result.setData(resourceInfoList);
		result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		return JSONObject.toJSONString(result);
	}
	
	@Override
	public String getResourceGroupByUserId(Long[] userId){
		Result result = new Result();
		if(null==userId || userId.length<1){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ParameterError_CODE);
			return JSONObject.toJSONString(result);
		}
		
		List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
		
		for(Long id:userId){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("userId", id);
			List<Map<String,String>> resourceGroupInfo = new ArrayList<Map<String,String>>();
			
			List<CustomGroupBo> list=customResourceGroupApi.getList(id);
			if(null!=list && list.size()>0){
				for(CustomGroupBo bo : list){
					Map<String,String> group = new HashMap<String,String>();
					group.put("resourceGroupId", String.valueOf(bo.getId()));
					group.put("resourceGroupName", bo.getName());
					resourceGroupInfo.add(group);
				}
			}
			
			map.put("resourceGroupInfo", resourceGroupInfo);
			mapList.add(map);
		}
		
		result.setData(mapList);
		result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		return JSONObject.toJSONString(result);
	}
	
//	@Override
//	public String getBizResourceByBizName(String bizName,boolean bool){
//		List<BizServiceBo> bsbList = bizServiceApi.getByName(bizName);
//		
//		Result result = new Result();
//		if(null==bsbList || bsbList.size()<1){
//			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
//			return JSONObject.toJSONString(result);
//		}
//		List<Map<String,Object>> bsbMapList = new ArrayList<Map<String,Object>>();
//		for(BizServiceBo bsb:bsbList){
//			Map<String,Object> mapBsb = new HashMap<String,Object>();
//			mapBsb.put("bizName", bsb.getName());
//			mapBsb.put("bizId", String.valueOf(bsb.getId()));
//			try {
//				bizServiceApi.update(bsb);
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//			}
//			mapBsb.put("bizState", bsb.getStatus());
//			
//			if(bool){
//				if(null != bsb.getTopology() && !"".equals(bsb.getTopology())){
//					String topology = bsb.getTopology();
//					topology = topology.replace("\\\"", "\\\'");
//					
//					Map mapTopo = (Map) JSON.parse(topology);
//					List<Map> containerList = JSONArray.parseArray(mapTopo.get("containers").toString(),Map.class);
//					List<Map> containers = new ArrayList<Map>();
//					getAllcontains(containerList, containers);
//					
//					List<Long> instances = new ArrayList<Long>();
//					for(Map conMap:containers){
//						if(StringUtils.isEmpty(conMap.get("data"))) continue;
//						Map dataMap =  (Map) conMap.get("data");
//						if("resource".equals(dataMap.get("type"))){
//							instances.add(Long.parseLong(dataMap.get("id").toString()));
//						}
//					}
//					if(instances.size()<1){
//						break;
//					}
//					try {
//						List<ResourceInstance> riList = resourceInstanceService.getResourceInstances(instances);
//						
//						List<Map<String,String>> riMapList = new ArrayList<Map<String,String>>();
//						for(ResourceInstance ri:riList){
//							Map<String,String> mapRi = new HashMap<String,String>();
//							mapRi.put("parentType", ri.getParentCategoryId());
//							mapRi.put("childrenType", ri.getCategoryId());
//							mapRi.put("ip", ri.getDiscoverIP());
//							mapRi.put("instanceId", String.valueOf(ri.getId()));
//							mapRi.put("bizNodeName", ri.getShowName());
//							
//							InstanceStateData isd = instanceStateService.getState(ri.getId());
//							mapRi.put("state", isd.getState().name());
//							
//							riMapList.add(mapRi);
//						}
//						mapBsb.put("resourceInfo", riMapList);
//					} catch (InstancelibException e) {
//						logger.error(e.getMessage(), e);
//					}
//				}
//			}
//			bsbMapList.add(mapBsb);
//		}
//		result.setData(bsbMapList);
//		result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
//		return JSONObject.toJSONString(result);
//	}
	
	@Override
	public String getInstanceRTCustomMetricData(Long instanceId,String metricId){
		Result result = new Result();
		MetricData md = metricDataService.getCustomerMetricData(instanceId, metricId);
		if(null==md){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		List<MetricData> mdList = new ArrayList<MetricData>();
		mdList.add(md);
		String unitName = getCustomUnit(instanceId,metricId);
		
		result.setData(returnMetricData(mdList,unitName));
		result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		return JSONObject.toJSONString(result);
	}
	
	@Override
	public String getInstanceCustomMetricDataByTime(Long instanceId,String metricId,String startTime,String endTime,String type){
		Result result = new Result();
		
		Date startDate = dateFormat(startTime);
		Date endDate = dateFormat(endTime);
		if(endDate.before(startDate)){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ParameterError_CODE);
			return JSONObject.toJSONString(result);
		}
		List<MetricSummaryData> summaryDataList;
		String unitName = getCustomUnit(instanceId,metricId);
		
		switch (type) {
		case "minute":
			
			List<MetricData> mdList = getCustomHistoryMetricDataByHour(instanceId,metricId,startDate,endDate);
			if(null==mdList || mdList.size()==0){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			
			result.setData(returnMetricData(mdList,unitName));
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
			
		case "hour":
			MetricSummaryType mstH = MetricSummaryType.H;
		    summaryDataList = getCustomSummaryMetricDataByDay(mstH,instanceId, metricId,startDate,endDate);
		    
		    if(null==summaryDataList || summaryDataList.size()==0){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			
			result.setData(returnSUMMetricData(summaryDataList,unitName));
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		case "day":
			MetricSummaryType mstD = MetricSummaryType.D;
		    summaryDataList = getCustomSummaryMetricDataByDay(mstD,instanceId, metricId,startDate,endDate);
			
		    if(null==summaryDataList || summaryDataList.size()==0){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			
			result.setData(returnSUMMetricData(summaryDataList,unitName));
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		default:
			
			List<MetricData> defaultList = getCustomHistoryMetricDataByHour(instanceId,metricId,startDate,endDate);
			if(null==defaultList || defaultList.size()==0){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			
			result.setData(returnMetricData(defaultList,unitName));
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		}
		
	}
	
	@Override
	public String getInstanceMetricDataByTime(Long instanceId,String metricId,String startTime,String endTime,String type){
		Result result = new Result();
		
		Date startDate = dateFormat(startTime);
		Date endDate = dateFormat(endTime);
		if(endDate.before(startDate)){
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ParameterError_CODE);
			return JSONObject.toJSONString(result);
		}
		List<MetricSummaryData> summaryDataList;
		String unitName = getUnit(instanceId,metricId);
		
		switch (type) {
		case "minute":
			
			List<MetricData> mdList = getHistoryMetricDataByHour(instanceId,metricId,startDate,endDate);
			if(null==mdList || mdList.size()==0){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			
			result.setData(returnMetricData(mdList,unitName));
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
			
		case "hour":
			MetricSummaryType mstH = MetricSummaryType.H;
		    summaryDataList = getSummaryMetricDataByDay(mstH,instanceId, metricId,startDate,endDate);
		    
		    if(null==summaryDataList || summaryDataList.size()==0){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			
			result.setData(returnSUMMetricData(summaryDataList,unitName));
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		case "day":
			MetricSummaryType mstD = MetricSummaryType.D;
		    summaryDataList = getSummaryMetricDataByDay(mstD,instanceId, metricId,startDate,endDate);
			
		    if(null==summaryDataList || summaryDataList.size()==0){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			
			result.setData(returnSUMMetricData(summaryDataList,unitName));
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		default:
			List<MetricData> defaultList = getCustomHistoryMetricDataByHour(instanceId,metricId,startDate,endDate);
			if(null==defaultList || defaultList.size()==0){
				result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
				return JSONObject.toJSONString(result);
			}
			
			result.setData(returnMetricData(defaultList,unitName));
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		}
		
	}
	
	@Override
	public String getResourceCustomMetricByInstanceId(String instanceId){
		Result result = new Result();
		List<CustomMetric>  cmList = new ArrayList<CustomMetric>();
		try {
			cmList = customMetricService.getCustomMetricsByInstanceId(Long.parseLong(instanceId));
		} catch (CustomMetricException e) {
			logger.error(e.getMessage(), e);
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMetricInfoError_CODE);
			return JSONObject.toJSONString(result);
		}
		return formatCustomeMetricInfo(cmList,result);
	}
	
	private String formatCustomeMetricInfo(List<CustomMetric>  cmList,Result result){
		
		if (null != cmList && cmList.size() > 0) {
			List<Map<String,Object>> cmMapList = new ArrayList<Map<String,Object>>();
			for(CustomMetric cm:cmList){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("customMetricInfo", cm.getCustomMetricInfo());
				List<CustomMetricThreshold> customThresholds = cm.getCustomMetricThresholds();
				List<Map<String, String>> list = new ArrayList<Map<String,String>>();
				if(customThresholds != null && customThresholds.size() > 0){
					for(CustomMetricThreshold threshold : customThresholds){
						if(threshold.getMetricState().equals(MetricStateEnum.NORMAL)
								|| threshold.getMetricState().equals(MetricStateEnum.CRITICAL)){
							continue;
						}
						
						Map<String, String> resultMap = new HashMap<String, String>();
						resultMap.put("level", threshold.getMetricState().toString());
						resultMap.put("expression", threshold.getThresholdExpression());
						
						list.add(resultMap);
					}
				}
				map.put("customMetricThresholds", list);
				cmMapList.add(map);
			}
			
			result.setData(cmMapList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}
	
	private List<Map<String,String>> returnMetricData(List<MetricData> mdList,String unitName){
		if(null==mdList){
			return null;
		}
		
		List<Map<String,String>> metricList = new ArrayList<Map<String,String>>();
		
		for(MetricData md:mdList){
			Map<String,String> map = new HashMap<String,String>();
			
			map.put("metricId", md.getMetricId());
			map.put("dataValue", formatStrArr(md.getData()));
			map.put("unit", unitName);
			map.put("collecTime", String.valueOf(md.getCollectTime().getTime()));
			
			metricList.add(map);
		}
		
		return metricList;
	}
	
	private List<Map<String,String>> returnSUMMetricData(List<MetricSummaryData> mdList,String unitName){
		if(null==mdList){
			return null;
		}
		
		//将数组顺序颠倒,以便数据按时间从先到后
	    List<MetricSummaryData> smHListAfter = new ArrayList<MetricSummaryData>();
	    for(int i=mdList.size()-1;i>-1;i--){
	    	smHListAfter.add(mdList.get(i));
	    }
		List<Map<String,String>> metricList = new ArrayList<Map<String,String>>();
		
		for(MetricSummaryData md:smHListAfter){
			Map<String,String> map = new HashMap<String,String>();
			
			map.put("metricId", md.getMetricId());
			map.put("dataValue", String.valueOf(md.getMetricData()));
			map.put("unit", unitName);
			map.put("startTime", String.valueOf(md.getStartTime().getTime()));
			map.put("endTime", String.valueOf(md.getEndTime().getTime()));
			
			metricList.add(map);
		}
		
		return metricList;
	}
	
	private String formatStrArr(String[] dataArr){
		if(null==dataArr || dataArr.length<1){
			return null;
		}
		String dataStr = dataArr[0];
		for(int i=1;i<dataArr.length;i++){
			dataStr += dataArr[i];
		}
		return dataStr;
	}
	
	private String getUnit(long instanceId,String metricId){
		try {
			ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
			String unitStr = "--";
			if(null != ri && null != ri.getResourceId()){
				ResourceMetricDef rd = capacityService.getResourceMetricDef(ri.getResourceId(), metricId);
				if(null!=rd){
					unitStr = rd.getUnit();
				}else{
					unitStr = getCustomUnit(instanceId,metricId);
				}
			}
			return unitStr;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "--";
		}
	}
	
	private String getCustomUnit(long instanceId,String metricId){
		try {
			ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
			String unitStr = "--";
			if(null != ri && null != ri.getResourceId()){
				List<CustomMetric> customMetrics = customMetricService.getCustomMetricsByInstanceId(instanceId);
				if(null!=customMetrics ){
					for(CustomMetric cmetric:customMetrics){
						CustomMetricInfo cmi = cmetric.getCustomMetricInfo();
						if(cmi.getId().equals(metricId)){
							unitStr = cmi.getUnit();
						}
					}
				}
			}
			return unitStr;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "--";
		}
	}
	
	private Date dateFormat(String date){
		SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		Date dateTime = new Date();
		try {
			dateTime = sdf.parse(date);
		} catch (ParseException e) {
			return new Date();
		}
		return dateTime;
	}
	
	private List<MetricData> getCustomHistoryMetricDataByHour(long instanceId ,String metricId ,Date dateStart , Date dateEnd ){
		try {
			
			return metricDataService.queryHistoryCustomerMetricData(metricId, instanceId, dateStart, dateEnd);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}
	
	private List<MetricData> getHistoryMetricDataByHour(long instanceId ,String metricId ,Date dateStart , Date dateEnd ){
		try {
			
			return metricDataService.queryHistoryMetricData(metricId, instanceId, dateStart, dateEnd);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}
	
	private List<MetricSummaryData> getCustomSummaryMetricDataByDay(MetricSummaryType msType ,long instanceId ,String metricId,Date dateStart , Date dateEnd){
		
		MetricSummaryQuery msq = new MetricSummaryQuery();
		msq.setSummaryType(msType);
		msq.setInstanceID(instanceId);
		msq.setMetricID(metricId);
		msq.setEndTime(dateEnd);
		msq.setStartTime(dateStart);
		
		try {
			
			return metricSummaryService.queryCustomMetricSummary(msq);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new ArrayList<MetricSummaryData>();
		}
		
	}
	
	private List<MetricSummaryData> getSummaryMetricDataByDay(MetricSummaryType msType ,long instanceId ,String metricId,Date dateStart , Date dateEnd){
		
		MetricSummaryQuery msq = new MetricSummaryQuery();
		msq.setSummaryType(msType);
		msq.setInstanceID(instanceId);
		msq.setMetricID(metricId);
		msq.setEndTime(dateEnd);
		msq.setStartTime(dateStart);
		try {
			
			return metricSummaryService.queryMetricSummary(msq);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new ArrayList<MetricSummaryData>();
		}
		
	}
	
	private SysModuleEnum[] getSysModuleEnum(String AlarmType[]){
		if(AlarmType.length<1) return null;
		
		SysModuleEnum[] smeArr = new SysModuleEnum[AlarmType.length];
		for(int i=0;i<AlarmType.length;i++){
			switch (AlarmType[i]) {
			case "ALL":
				SysModuleEnum[] smeArrAll = {SysModuleEnum.BUSSINESS,SysModuleEnum.CONFIG_MANAGER,SysModuleEnum.IP_MAC_PORT,SysModuleEnum.LINK,SysModuleEnum.MONITOR
				,SysModuleEnum.NETFLOW,SysModuleEnum.OTHER,SysModuleEnum.SYSLOG,SysModuleEnum.TRAP};
				return smeArrAll;
			case "BUSSINESS":
				smeArr[i] = SysModuleEnum.BUSSINESS;
				break;
			case "CONFIG_MANAGER":
				smeArr[i] = SysModuleEnum.CONFIG_MANAGER;
				break;
			case "IP_MAC_PORT":
				smeArr[i] = SysModuleEnum.IP_MAC_PORT;
				break;
			case "LINK":
				smeArr[i] = SysModuleEnum.LINK;
				break;
			case "MONITOR":
				smeArr[i] = SysModuleEnum.MONITOR;
				break;
			case "NETFLOW":
				smeArr[i] = SysModuleEnum.NETFLOW;
				break;
			case "OTHER":
				smeArr[i] = SysModuleEnum.OTHER;
				break;
			case "SYSLOG":
				smeArr[i] = SysModuleEnum.SYSLOG;
				break;
			case "TRAP":
				smeArr[i] = SysModuleEnum.TRAP;
				break;
			}
		}
		
		return smeArr;
	}
	
	private void getAllcontains(List<Map> containers,List<Map> list){
		if(null!=containers && containers.size()>0){
			for(int i=0;i<containers.size();i++){
				list.add(containers.get(i));
				List<Map> child = JSONArray.parseArray(containers.get(i).get("containers").toString(),Map.class);
				this.getAllcontains(child,list);
			}
		}
	}
	
	private Map<String,String> getResultMap(ResourceInstance ri){
		Map<String,String> map = new HashMap<String,String>();
		map.put("instanceId", String.valueOf(ri.getId()));
		map.put("parentCategrayId", ri.getParentCategoryId());
		map.put("childrenCategrayId", ri.getCategoryId());
		map.put("resourceId", ri.getResourceId());
		map.put("ip", ri.getShowIP());
		map.put("name", ri.getName());
		map.put("showName", ri.getShowName());
		
		return map;
	}
	
}
