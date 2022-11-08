package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.event.AlarmEventTemplateService;
import com.mainsteam.stm.alarm.obj.AlarmEventTemplate;
import com.mainsteam.stm.alarm.obj.AlarmEventTemplateEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.resource.ThresholdDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dsl.expression.dict.TerminalSymbolConst;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ProfileApi;
import com.mainsteam.stm.portal.resource.api.StrategyDetailApi;
import com.mainsteam.stm.portal.resource.bo.MetricUpdateThresholdParameter;
import com.mainsteam.stm.portal.resource.bo.PortalThreshold;
import com.mainsteam.stm.portal.resource.bo.ProfileBo;
import com.mainsteam.stm.portal.resource.bo.ProfileMetricBo;
import com.mainsteam.stm.portal.resource.bo.zTreeBo;
import com.mainsteam.stm.portal.resource.web.vo.ChildResourceStrategyPageVo;
import com.mainsteam.stm.portal.resource.web.vo.ChildResourceStrategyVo;
import com.mainsteam.stm.portal.resource.web.vo.MainResourceStrategyPageVo;
import com.mainsteam.stm.portal.resource.web.vo.MainResourceStrategyVo;
import com.mainsteam.stm.portal.resource.web.vo.MetricUpdateParameterVo;
import com.mainsteam.stm.portal.resource.web.vo.ProfileInfoVo;
import com.mainsteam.stm.portal.resource.web.vo.ResourceDefVo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.util.ResourceOrMetricConst;

/**
 * 
 * <li>文件名称: CustomResGroupAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月24日
 * @author   pengl
 */

@Controller
@RequestMapping("/portal/strategydetail")
public class StrategyDetailAction extends BaseAction {
	
	private static final Log logger = LogFactory.getLog(StrategyDetailAction.class);
	
	@Resource
	private CapacityService capacityService;
	
	@Autowired
	private StrategyDetailApi strategyDetailApi;
	
	@Autowired
	private ProfileApi profileApi;
	
	@Autowired
	private ProfileService profileService;
	
	@Resource
	private MetricStateService metricStateService;

	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private AlarmEventTemplateService alarmEventTemplateService;
	
	@Resource
	private TimelineService timelineService;
	
	@Value("${stm.resource.personalize.chlidProfile.MaxCount}")
	private int childProfileMaxCount;
	
	/**
	 * 获取策略信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getStrategyById")
	public JSONObject getStrategyById(Long strategyId) {
		
		ProfileBo bo = null;
		try {
			bo =  profileApi.getProfile(strategyId);
			
			if(bo.getProfileInfo().getProfileType().equals(ProfileTypeEnum.PERSONALIZE)){
				bo.setChildProfileMaxCount(childProfileMaxCount);
				ResourceInstance instance = resourceInstanceService.getResourceInstance(bo.getProfileInfo().getPersonalize_instanceId());
				if(instance != null){
					bo.setPersonalize_instanceDomainId(instance.getDomainId());
				}
			}
			
			if(bo.getProfileInfo().getProfileType().equals(ProfileTypeEnum.SPECIAL)){
				bo.setChildProfileMaxCount(childProfileMaxCount);
			}
			
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		} catch (InstancelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		
		return toSuccess(bo);
		
	}
	
	/**
	 * 获取策略阈值操作符信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getProfileMetricThresholdOperation")
	public JSONObject getProfileMetricThresholdOperation() {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		TerminalSymbolConst constValue = new TerminalSymbolConst();
		
		Set<String> logicalOperatorsSet = constValue.getLogicalOperators();
		List<Map<String, String>> logicalOperatorsList = new ArrayList<Map<String,String>>();
		if(logicalOperatorsSet != null && logicalOperatorsSet.size() > 0){
			
			for(String key : logicalOperatorsSet){
				Map<String, String> parameter = new HashMap<String, String>();
				parameter.put("id", key);
				parameter.put("name", constValue.getDescription(key));
				logicalOperatorsList.add(parameter);
			}
		}
		result.put("logicalOperators", logicalOperatorsList);
		
		Set<String> unaryComparisonOperatorsSet = constValue.getUnaryComparisonOperators();
		List<Map<String, String>> unaryComparisonOperatorsList = new ArrayList<Map<String,String>>();
		if(unaryComparisonOperatorsSet != null && unaryComparisonOperatorsSet.size() > 0){
			
			for(String key : unaryComparisonOperatorsSet){
				Map<String, String> parameter = new HashMap<String, String>();
				parameter.put("id", key);
				parameter.put("name", constValue.getDescription(key));
				unaryComparisonOperatorsList.add(parameter);
			}
		}
		result.put("unaryComparisonOperators", unaryComparisonOperatorsList);
		
		Set<String> binaryComparisonOperatorsSet = constValue.getBinaryComparisonOperators();
		List<Map<String, String>> binaryComparisonOperatorsList = new ArrayList<Map<String,String>>();
		if(binaryComparisonOperatorsSet != null && binaryComparisonOperatorsSet.size() > 0){
			
			for(String key : binaryComparisonOperatorsSet){
				Map<String, String> parameter = new HashMap<String, String>();
				parameter.put("id", key);
				parameter.put("name", constValue.getDescription(key));
				binaryComparisonOperatorsList.add(parameter);
			}
		}
		result.put("binaryComparisonOperators", binaryComparisonOperatorsList);
		
		return toSuccess(result);
		
	}
	
	/**
	 * 获取阈值信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getProfileMetricThreshold")
	public JSONObject getProfileMetricThreshold(Long strategyId,String metricId,Long timelineId) {
		
		List<ProfileThreshold> thresholds = null;
		try {
			if(timelineId != null && timelineId > 0){
				thresholds = timelineService.getThresholdByTimelineIdAndMetricId(timelineId, metricId);
			}else{
				thresholds = profileService.getThresholdByProfileIdAndMetricId(strategyId, metricId);
			}
		} catch (ProfilelibException e) {
			logger.error(e.getMessage(),e);
		}
		
		if(thresholds == null || thresholds.size() <= 0){
			return toSuccess(null);
		}
		
		List<PortalThreshold> portalThresholds = new ArrayList<PortalThreshold>();
		
		AlarmEventTemplate content = null;
		AlarmEventTemplate template = new AlarmEventTemplate();
		if(thresholds.get(0).getAlarmTemplate() == null || thresholds.get(0).getAlarmTemplate().equals("")){
			//取默认告警内容
			Profile profile = null;
			try {
				profile = profileService.getProfilesById(strategyId);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(),e);
			}
			ResourceMetricDef metricDef = capacityService.getResourceMetricDef(profile.getProfileInfo().getResourceId(), metricId);
			content = alarmEventTemplateService.getDefaultTemplate(profile.getProfileInfo().getParentProfileId() > 0 ? false : true, 
					SysModuleEnum.MONITOR, metricDef.getMetricType());
		}else{
			template.setUniqueKey(thresholds.get(0).getAlarmTemplate());
			content = alarmEventTemplateService.getTemplate(template);
		}
		

		for(ProfileThreshold threshold : thresholds){

			PortalThreshold result = new PortalThreshold();
			
			BeanUtils.copyProperties(threshold, result);
			
			if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Normal)){
				result.setAlarmContent(content.getContent().get(InstanceStateEnum.NORMAL));
			}else if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Minor)){
				result.setAlarmContent(content.getContent().get(InstanceStateEnum.WARN));
			}else if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Major)){
				result.setAlarmContent(content.getContent().get(InstanceStateEnum.SERIOUS));
			}
			
			portalThresholds.add(result);

		}
		
		return toSuccess(portalThresholds);
		
	}
	
	/**
	 * 获取默认阈值信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getDefaultProfileMetricThreshold")
	public JSONObject getDefaultProfileMetricThreshold(Long strategyId,String metricId,String level) {
		
		List<ProfileThreshold> thresholds = null;
		try {
			thresholds = profileService.getThresholdByProfileIdAndMetricId(strategyId, metricId);
		} catch (ProfilelibException e) {
			logger.error(e.getMessage(),e);
		}
		
		if(thresholds == null || thresholds.size() <= 0){
			return toSuccess(null);
		}
		
		Profile profile = null;
		try {
			profile = profileService.getProfilesById(strategyId);
		} catch (ProfilelibException e) {
			logger.error(e.getMessage(),e);
		}
		
		ResourceMetricDef metricDef = capacityService.getResourceMetricDef(profile.getProfileInfo().getResourceId(), metricId);
		
		ThresholdDef[] thresholdDefs = metricDef.getThresholdDefs();
		
		if(thresholds == null || thresholdDefs.length <= 0){
			return toSuccess(null);
		}
		
		AlarmEventTemplate content = alarmEventTemplateService.getDefaultTemplate(profile.getProfileInfo().getParentProfileId() > 0 ? false : true, 
				SysModuleEnum.MONITOR, metricDef.getMetricType());

		PortalThreshold result = new PortalThreshold();
		
		for(ProfileThreshold threshold : thresholds){
			
			if(!threshold.getPerfMetricStateEnum().toString().equals(level)){
				continue;
			}
			
			BeanUtils.copyProperties(threshold, result);
			
			if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Normal)){
				result.setAlarmContent(content.getContent().get(InstanceStateEnum.NORMAL));
			}else if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Minor)){
				result.setAlarmContent(content.getContent().get(InstanceStateEnum.WARN));
			}else if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Major)){
				result.setAlarmContent(content.getContent().get(InstanceStateEnum.SERIOUS));
			}
			
			for(ThresholdDef def : thresholdDefs){
				if(threshold.getPerfMetricStateEnum().equals(def.getState())){
					result.setThresholdExpression(def.getThresholdExpression());
					break;
				}
			}
			
		}
		
		return toSuccess(result);
		
	}
	
	/**
	 * 获取告警内容参数信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getAlarmContentParameter")
	public JSONObject getAlarmContentParameter() {
		
		List<Map<String, String>> parameters = new ArrayList<Map<String,String>>();
		
		for(AlarmEventTemplateEnum templateEnum : AlarmEventTemplateEnum.values()){
			Map<String, String> node = new HashMap<String, String>();
			node.put("id", templateEnum.getKey());
			node.put("nodeName", templateEnum.getValue());
			node.put("nodeDes", templateEnum.getDescription());
			
			parameters.add(node);
			
		}
		
		return toSuccess(parameters);
		
	}
	
	/**
	 * 根据资源ID获取使用当前策略的资源
	 * @param
	 * @return
	 */
	@RequestMapping("/getResourceNoStrategyVoByids")
	public JSONObject getResourceNoStrategyVoByids(String ids,Long domainId,HttpSession session){
		
		ILoginUser user = getLoginUser(session);
		
		MainResourceStrategyPageVo pageVo = new MainResourceStrategyPageVo();
		
		List<ResourceInstance> resourceInstances = strategyDetailApi.getResourceInstancesByIds(ids,domainId,user);
		
		List<MainResourceStrategyVo> voList = new ArrayList<MainResourceStrategyVo>();
		
		
		for(ResourceInstance instance : resourceInstances){
			
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info != null){
				MainResourceStrategyVo vo = this.getVoNoStrategy(instance);
				vo.setLifeState(instance.getLifeState().toString());
				vo.setStrategyID(info.getProfileId());
				vo.setStrategyName(info.getProfileName());
				
				voList.add(vo);
			}else{
				logger.error("profileInfo is empty:" + instance.getId());
			}
		}
		
		pageVo.setResources(voList);
		
		return toSuccess(pageVo);
		
	}
	
	/**
	 * 根据资源ID获取未使用当前策略的资源
	 * @param
	 * @return
	 */
	@RequestMapping("/getResourceExceptVoByids")
	public JSONObject getResourceExceptVoByids(String ids,String resourceId,Integer state,Long domainId,String searchContent,HttpSession session){
		
		ILoginUser user = getLoginUser(session);
		
		MainResourceStrategyPageVo pageVo = new MainResourceStrategyPageVo();
		
		List<ResourceInstanceBo> resourceInstances = strategyDetailApi.getExceptResourceInstancesByIds(ids,resourceId,state,domainId,searchContent,user);
		
		List<MainResourceStrategyVo> voList = new ArrayList<MainResourceStrategyVo>();
		
		for(ResourceInstanceBo instance : resourceInstances){

			MainResourceStrategyVo vo = this.getVoNoStrategy(instance);
			vo.setLifeState(instance.getLifeState().toString());
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null){
				vo.setStrategyID(-1);
				vo.setStrategyName("-");

			}else{
				vo.setStrategyID(info.getProfileId());
				vo.setStrategyName(info.getProfileName());
			}
			
			voList.add(vo);
			
		}
		
		pageVo.setResources(voList);
		
		return toSuccess(pageVo);
		
	}
	
	/**
	 * 获取所有符合条件的资源
	 * @param
	 * @return
	 */
	@RequestMapping("/getMainProfileResourceInstances")
	public JSONObject getMainProfileResourceInstances(String resourceId,Long domainId,Long profileId,Integer start,Integer pageSize,HttpSession session){
		ILoginUser user = getLoginUser(session);
		
		MainResourceStrategyPageVo pageVo = new MainResourceStrategyPageVo();
		
		List<ResourceInstanceBo> resourceInstances = strategyDetailApi.getMainProfileResourceInstances(resourceId,0,domainId,"",user);
		
		pageVo.setTotalRecord(resourceInstances.size());
		
		Set<Long> domainIds = new HashSet<Long>();
		
		List<Long> instanceIds = new ArrayList<Long>();
		
		//判断当前策略是否监控所有该资源类型下的设备
		boolean isSelectAll = true;
		for(ResourceInstanceBo instance : resourceInstances){
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null || info.getProfileId() != profileId){
				isSelectAll = false;
			}else if(info.getProfileId() == profileId){
				domainIds.add(instance.getDomainId());
				instanceIds.add(instance.getId());
			}
		}
		
		List<MainResourceStrategyVo> voList = new ArrayList<MainResourceStrategyVo>();
		
		//分页处理
		List<ResourceInstanceBo> resultResourceInstance = new ArrayList<ResourceInstanceBo>();
		
		if((start + pageSize) > resourceInstances.size()){
			resultResourceInstance = resourceInstances.subList(start, resourceInstances.size());
		}else{
			resultResourceInstance = resourceInstances.subList(start, (start + pageSize));
		}
		
		for(ResourceInstanceBo instance : resultResourceInstance){

			MainResourceStrategyVo vo = this.getVoNoStrategy(instance);
			vo.setLifeState(instance.getLifeState().toString());
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null){
				vo.setStrategyID(-1);
				vo.setStrategyName("-");

			}else{
				vo.setStrategyID(info.getProfileId());
				vo.setStrategyName(info.getProfileName());
			}
			
			voList.add(vo);
			
		}
		
		pageVo.setSelectAll(isSelectAll);
		pageVo.setDomainIds(domainIds);
		pageVo.setInstanceIds(instanceIds);
		pageVo.setResources(voList);
		
		return toSuccess(pageVo);
		
	}
	
	/**
	 * 获取所有符合条件的资源(带查询条件)
	 * @param
	 * @return
	 */
	@RequestMapping("/getMainProfileResourceInstancesByCondion")
	public JSONObject getMainProfileResourceInstancesByCondion(String resourceId,Integer state,Long domainId,String searchContent,
			Long profileId,String curUserCheckedResource,Integer start,Integer pageSize,HttpSession session){
		
		
		ILoginUser user = getLoginUser(session);
		
		MainResourceStrategyPageVo pageVo = new MainResourceStrategyPageVo();
		
		if(curUserCheckedResource == null || curUserCheckedResource.split(",") == null){
			logger.error("Get user current select resource error!");
			return toSuccess(pageVo);
		}
		
		String[] userSelectResource = curUserCheckedResource.split(",");
		
		List<ResourceInstanceBo> resourceInstances = strategyDetailApi.getMainProfileResourceInstances(resourceId,state,domainId,searchContent,user);
		
		pageVo.setTotalRecord(resourceInstances.size());
		
		Set<Long> domainIds = new HashSet<Long>();
		
		List<Long> instanceIds = new ArrayList<Long>();
		
		//判断当前策略是否监控所有该资源类型下的设备
		boolean isSelectAll = true;
		out : for(ResourceInstanceBo instance : resourceInstances){
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info != null && info.getProfileId() == profileId){
				domainIds.add(instance.getDomainId());
				instanceIds.add(instance.getId());
			}
			if(isSelectAll){
				for(String selectId : userSelectResource){
					if(selectId == null || selectId.equals("") || selectId.trim().equals("")){
						continue;
					}
					if(Long.parseLong(selectId) == instance.getId()){
						continue out;
					}
				}
				isSelectAll = false;
			}
		}
		
		List<MainResourceStrategyVo> voList = new ArrayList<MainResourceStrategyVo>();
		
		//分页处理
		List<ResourceInstanceBo> resultResourceInstance = new ArrayList<ResourceInstanceBo>();
		
		if((start + pageSize) > resourceInstances.size()){
			resultResourceInstance = resourceInstances.subList(start, resourceInstances.size());
		}else{
			resultResourceInstance = resourceInstances.subList(start, (start + pageSize));
		}
		
		for(ResourceInstanceBo instance : resultResourceInstance){

			MainResourceStrategyVo vo = this.getVoNoStrategy(instance);
			vo.setLifeState(instance.getLifeState().toString());
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null){
				vo.setStrategyID(-1);
				vo.setStrategyName("-");

			}else{
				vo.setStrategyID(info.getProfileId());
				vo.setStrategyName(info.getProfileName());
			}
			
			voList.add(vo);
			
		}
		
		pageVo.setSelectAll(isSelectAll);
		pageVo.setDomainIds(domainIds);
		pageVo.setInstanceIds(instanceIds);
		pageVo.setResources(voList);
		
		return toSuccess(pageVo);
		
	}
	
	/**
	 * 获取所有符合条件的资源ID及域集合
	 * @param
	 * @return
	 */
	@RequestMapping("/getMainProfileResourceInstancesId")
	public JSONObject getMainProfileResourceInstancesId(String resourceId,Integer state,Long domainId,String searchContent,HttpSession session){
		
		ILoginUser user = getLoginUser(session);
		
		List<ResourceInstanceBo> resourceInstances = strategyDetailApi.getMainProfileResourceInstances(resourceId,state,domainId,searchContent,user);
		
		List<Long> ids = new ArrayList<Long>();
		
		Set<Long> domainIds = new HashSet<Long>();
		
		for(ResourceInstanceBo bo : resourceInstances){
			domainIds.add(bo.getDomainId());
			ids.add(bo.getId());
		}
		
		Map<String, Collection<Long>> result = new HashMap<String, Collection<Long>>();
		
		result.put("intancesIds", ids);
		result.put("domainIds", domainIds);
		
		return toSuccess(result);
		
	}
	
	/**
	 * 获取指定id集合的资源列表
	 * @return
	 */
	@RequestMapping("/getRightChildResourceDef")
	public JSONObject getRightResourceDef(String ids,Long domainId,HttpSession session){
		
		ILoginUser user = getLoginUser(session);
		
		List<ResourceInstance> resourceInstanceList = strategyDetailApi.getChildResourceInstancesByIds(ids,domainId,user);
		
		List<zTreeBo> treeList = strategyDetailApi.getZTreeBoListByChildResourceInstance(resourceInstanceList,null);
		
		return toSuccess(JSONObject.toJSON(treeList));
		
		
	}
	
	/**
	 * 获取除去指定id集合的资源列表
	 * @return
	 */
	@RequestMapping("/getLeftChildResourceDef")
	public JSONObject getLeftResourceDef(String ids,String resourceId,Long mainProfileId,Long domainId,String interfaceState,HttpSession session){
		ILoginUser user = getLoginUser(session);
		
		List<ResourceInstance> resourceInstanceList = strategyDetailApi.getExceptChildResourceInstancesByIds(ids,resourceId,mainProfileId,domainId,user);
		
		List<zTreeBo> treeList = strategyDetailApi.getZTreeBoListByChildResourceInstance(resourceInstanceList, interfaceState);
		
		return toSuccess(JSONObject.toJSON(treeList));
		
		
	}
	
	/**
	 * 为子策略展示子资源
	 * @return
	 */
	@RequestMapping("/getChildResourceForChildProfile")
	public JSONObject getChildResourceForChildProfile(Long mainInstanceId,String resourceId,Long domainId,String interfaceState,
			Integer start,Integer pageSize,HttpSession session){
		ILoginUser user = getLoginUser(session);
		
		List<ResourceInstance> resourceInstanceList = strategyDetailApi.getChildResourceInstancesByParentId(mainInstanceId,resourceId,domainId,user,interfaceState,null);
		
		ChildResourceStrategyPageVo pageVo = new ChildResourceStrategyPageVo();
		
		List<ChildResourceStrategyVo> voList = new ArrayList<ChildResourceStrategyVo>();
		
		//分页处理
		List<ResourceInstance> resultResourceInstance = new ArrayList<ResourceInstance>();
		
		if((start + pageSize) > resourceInstanceList.size()){
			resultResourceInstance = resourceInstanceList.subList(start, resourceInstanceList.size());
		}else{
			resultResourceInstance = resourceInstanceList.subList(start, (start + pageSize));
		}
		
		for(ResourceInstance instance : resultResourceInstance){
			
			ChildResourceStrategyVo vo = new ChildResourceStrategyVo();
			vo.setResourceId(instance.getId());
			if(interfaceState != null && !interfaceState.equals("")){
				//临时获取接口状态
				if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
					MetricStateData data = metricStateService.getMetricState(instance.getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
					if(data != null){
						if(data.getState().equals(MetricStateEnum.CRITICAL)){
							vo.setInterfaceState("0");
						}else{
							vo.setInterfaceState("1");
						}
					}
				}else{
					vo.setInterfaceState(instance.getModulePropBykey(ResourceOrMetricConst.RESOURCE_AVAILABILITY)[0]);
				}
			}
			if(instance.getShowName() != null && !(instance.getShowName().equals(""))){
				vo.setResourceName(instance.getShowName());
			}else{
				vo.setResourceName(instance.getName());
			}
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null){
				vo.setStrategyID(-1);
			}else{
				vo.setStrategyID(info.getProfileId());
				vo.setStrategyName(info.getProfileName());
			}
			
			voList.add(vo);
			
		}
		
		pageVo.setTotalRecord(resourceInstanceList.size());
		pageVo.setResources(voList);
		
		return toSuccess(JSONObject.toJSON(pageVo));
		
		
	}
	
	/**
	 * 为子策略展示子资源
	 * @return
	 */
	@RequestMapping("/getChildResourceForPersonalizeChildProfile")
	public JSONObject getChildResourceForPersonalizeChildProfile(Long childProfileId,Long mainInstanceId,String resourceId,Long domainId,String interfaceState,String searchContent,
			Integer start,Integer pageSize,HttpSession session){
		ILoginUser user = getLoginUser(session);
		
		List<ResourceInstance> resourceInstanceList = strategyDetailApi.getChildResourceInstancesByParentId(mainInstanceId,resourceId,domainId,user,interfaceState,searchContent);
		
		//判断当前策略是否监控所有该资源类型下的设备
		
		List<Long> instanceIds = new ArrayList<Long>();
		
		boolean isSelectAll = true;
		for(ResourceInstance instance : resourceInstanceList){
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null || info.getProfileId() != childProfileId){
				isSelectAll = false;
			}else if(info.getProfileId() == childProfileId){
				instanceIds.add(instance.getId());
			}
		}
		
		ChildResourceStrategyPageVo pageVo = new ChildResourceStrategyPageVo();
		
		List<ChildResourceStrategyVo> voList = new ArrayList<ChildResourceStrategyVo>();
		
		//分页处理
		List<ResourceInstance> resultResourceInstance = new ArrayList<ResourceInstance>();
		
		if((start + pageSize) > resourceInstanceList.size()){
			resultResourceInstance = resourceInstanceList.subList(start, resourceInstanceList.size());
		}else{
			resultResourceInstance = resourceInstanceList.subList(start, (start + pageSize));
		}
		
		for(ResourceInstance instance : resultResourceInstance){
			
			ChildResourceStrategyVo vo = new ChildResourceStrategyVo();
			vo.setResourceId(instance.getId());
			if(interfaceState != null && !interfaceState.equals("")){
				//临时获取接口状态
				if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
					MetricStateData data = metricStateService.getMetricState(instance.getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
					if(data != null){
						if(data.getState().equals(MetricStateEnum.CRITICAL)){
							vo.setInterfaceState("0");
						}else{
							vo.setInterfaceState("1");
						}
					}else{
						vo.setInterfaceState("1");
					}
				}else{
					vo.setInterfaceState(instance.getModulePropBykey(ResourceOrMetricConst.RESOURCE_AVAILABILITY)[0]);
				}
			}
			if(instance.getShowName() != null && !(instance.getShowName().equals(""))){
				vo.setResourceName(instance.getShowName());
			}else{
				vo.setResourceName(instance.getName());
			}
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null){
				vo.setStrategyID(-1);
				vo.setStrategyName("-");
			}else{
				vo.setStrategyID(info.getProfileId());
				vo.setStrategyName(info.getProfileName());
			}
			
			voList.add(vo);
			
		}
		
		if(isSelectAll){
			pageVo.setSelectStatus(2);
		}else{
			pageVo.setSelectStatus(0);
		}
		pageVo.setTotalRecord(resourceInstanceList.size());
		pageVo.setResources(voList);
		pageVo.setResourceIds(instanceIds);
		
		return toSuccess(JSONObject.toJSON(pageVo));
		
		
	}
	
	/**
	 * 为子策略展示子资源(网络接口)
	 * @return
	 */
	@RequestMapping("/getChildResourceForChildProfileInInterface")
	public JSONObject getChildResourceForChildProfileInInterface(Long mainInstanceId,String resourceId,Long domainId,String interfaceState,
			Integer start,Integer pageSize,HttpSession session){
		ILoginUser user = getLoginUser(session);
		
		List<ResourceInstance> resourceInstanceList = strategyDetailApi.getChildResourceInstancesByParentId(mainInstanceId,resourceId,domainId,user,interfaceState,null);
		
		ChildResourceStrategyPageVo pageVo = new ChildResourceStrategyPageVo();
		
		List<ChildResourceStrategyVo> voList = new ArrayList<ChildResourceStrategyVo>();
		
		List<Long> resourceInstanceIdList = new ArrayList<Long>();
		
		for(ResourceInstance instance : resourceInstanceList){
			resourceInstanceIdList.add(instance.getId());
		}
		
		//分页处理
		List<ResourceInstance> resultResourceInstance = new ArrayList<ResourceInstance>();
		
		if((start + pageSize) > resourceInstanceList.size()){
			resultResourceInstance = resourceInstanceList.subList(start, resourceInstanceList.size());
		}else{
			resultResourceInstance = resourceInstanceList.subList(start, (start + pageSize));
		}
		
		for(ResourceInstance instance : resultResourceInstance){
			
			ChildResourceStrategyVo vo = new ChildResourceStrategyVo();
			vo.setResourceId(instance.getId());
			if(interfaceState != null && !interfaceState.equals("")){
				//临时获取接口状态
				if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
					MetricStateData data = metricStateService.getMetricState(instance.getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
					if(data != null){
						if(data.getState().equals(MetricStateEnum.CRITICAL)){
							vo.setInterfaceState("0");
						}else{
							vo.setInterfaceState("1");
						}
					}
				}else{
					vo.setInterfaceState(instance.getModulePropBykey(ResourceOrMetricConst.RESOURCE_AVAILABILITY)[0]);
				}
			}
			if(instance.getShowName() != null && !(instance.getShowName().equals(""))){
				vo.setResourceName(instance.getShowName());
			}else{
				vo.setResourceName(instance.getName());
			}
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null){
				vo.setStrategyID(-1);
			}else{
				vo.setStrategyID(info.getProfileId());
				vo.setStrategyName(info.getProfileName());
			}
			
			voList.add(vo);
			
		}
		
		pageVo.setTotalRecord(resourceInstanceList.size());
		pageVo.setResources(voList);
		pageVo.setResourceIds(resourceInstanceIdList);
		
		return toSuccess(JSONObject.toJSON(pageVo));
		
		
	}
	
	/**
	 * 为个性化子策略展示子资源(网络接口)
	 * @return
	 */
	@RequestMapping("/getChildResourceForPersonalizeChildProfileInInterface")
	public JSONObject getChildResourceForPersonalizeChildProfileInInterface(Long childProfileId,Long mainInstanceId,String resourceId,Long domainId,String interfaceState,
			Integer start,Integer pageSize,HttpSession session){
		ILoginUser user = getLoginUser(session);
		
		List<ResourceInstance> resourceInstanceList = strategyDetailApi.getChildResourceInstancesByParentId(mainInstanceId,resourceId,domainId,user,interfaceState,null);
		
		ChildResourceStrategyPageVo pageVo = new ChildResourceStrategyPageVo();
		
		List<ChildResourceStrategyVo> voList = new ArrayList<ChildResourceStrategyVo>();
		
		boolean isSelectAll = true;
		for(ResourceInstance instance : resourceInstanceList){
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null || info.getProfileId() != childProfileId){
				isSelectAll = false;
				break;
			}
		}
		
		//分页处理
		List<ResourceInstance> resultResourceInstance = new ArrayList<ResourceInstance>();
		
		if((start + pageSize) > resourceInstanceList.size()){
			resultResourceInstance = resourceInstanceList.subList(start, resourceInstanceList.size());
		}else{
			resultResourceInstance = resourceInstanceList.subList(start, (start + pageSize));
		}
		
		for(ResourceInstance instance : resultResourceInstance){
			
			ChildResourceStrategyVo vo = new ChildResourceStrategyVo();
			vo.setResourceId(instance.getId());
			if(interfaceState != null && !interfaceState.equals("")){
				//临时获取接口状态
				if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
					MetricStateData data = metricStateService.getMetricState(instance.getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
					if(data != null){
						if(data.getState().equals(MetricStateEnum.CRITICAL)){
							vo.setInterfaceState("0");
						}else{
							vo.setInterfaceState("1");
						}
					}
				}else{
					vo.setInterfaceState(instance.getModulePropBykey(ResourceOrMetricConst.RESOURCE_AVAILABILITY)[0]);
				}
			}
			if(instance.getShowName() != null && !(instance.getShowName().equals(""))){
				vo.setResourceName(instance.getShowName());
			}else{
				vo.setResourceName(instance.getName());
			}
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null){
				vo.setStrategyID(-1);
			}else{
				vo.setStrategyID(info.getProfileId());
				vo.setStrategyName(info.getProfileName());
			}
			
			voList.add(vo);
			
		}
		
		if(isSelectAll){
			pageVo.setSelectStatus(2);
		}else{
			pageVo.setSelectStatus(0);
		}
		pageVo.setTotalRecord(resourceInstanceList.size());
		pageVo.setResources(voList);
		
		return toSuccess(JSONObject.toJSON(pageVo));
		
		
	}
	
	/**
	 * 为子策略展示子资源(获取所有子资源ID)
	 * @return
	 */
	@RequestMapping("/getChildResourceIdForChildProfile")
	public JSONObject getChildResourceIdForChildProfile(Long mainInstanceId,String resourceId,Long domainId,String interfaceState,HttpSession session){
		ILoginUser user = getLoginUser(session);
		
		List<Long> result = new ArrayList<>();
		
		List<ResourceInstance> resourceInstanceList = strategyDetailApi.getChildResourceInstancesByParentId(mainInstanceId,resourceId,domainId,user,interfaceState,null);
		
		for(ResourceInstance instance : resourceInstanceList){
			
			result.add(instance.getId());
			
		}
		
		return toSuccess(JSONObject.toJSON(result));
		
		
	}
	
	/**
	 * 为子策略展示子资源过滤名称
	 * @return
	 */
	@RequestMapping("/getChildResourceInstancesByResourceName")
	public JSONObject getChildResourceInstancesByResourceName(Long mainInstanceId,String resourceId,Long domainId,String interfaceState,
			Integer start,Integer pageSize,HttpSession session,String resourceName){
		ILoginUser user = getLoginUser(session);
		
		List<ResourceInstance> resourceInstanceList = strategyDetailApi.getChildResourceInstancesByResourceName(mainInstanceId,resourceId,domainId,user,interfaceState,null,resourceName);
		ChildResourceStrategyPageVo pageVo = new ChildResourceStrategyPageVo();
		
		List<ChildResourceStrategyVo> voList = new ArrayList<ChildResourceStrategyVo>();
		
//		//分页处理
//		List<ResourceInstance> resultResourceInstance = new ArrayList<ResourceInstance>();
//		System.out.println("start = "+start);
//		System.out.println("pageSize = "+start);
//		System.out.println("Size = "+resourceInstanceList.size());
//		if((start + pageSize) > resourceInstanceList.size()){
//			if(resourceInstanceList.size() < start || resourceInstanceList.size() <pageSize){
//				resultResourceInstance = resourceInstanceList;
//			}else{
//				if(start == resourceInstanceList.size()){
//					resultResourceInstance = resourceInstanceList.subList(0, start);
//				}else{
//					resultResourceInstance = resourceInstanceList.subList(start, resourceInstanceList.size());
//				}
//			}
//		}else{
//			resultResourceInstance = resourceInstanceList.subList(start, (start + pageSize));
//		}
//		System.out.println("resultResourceInstance = "+resultResourceInstance);
		//分页处理
		List<ResourceInstance> resultResourceInstance = new ArrayList<ResourceInstance>();
		
		if((start + pageSize) > resourceInstanceList.size()){
			resultResourceInstance = resourceInstanceList.subList(start, resourceInstanceList.size());
		}else{
			resultResourceInstance = resourceInstanceList.subList(start, (start + pageSize));
		}
		for(ResourceInstance instance : resultResourceInstance){
			
			ChildResourceStrategyVo vo = new ChildResourceStrategyVo();
			vo.setResourceId(instance.getId());
			if(interfaceState != null && !interfaceState.equals("")){
				//临时获取接口状态
				if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
					MetricStateData data = metricStateService.getMetricState(instance.getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
					if(data != null){
						if(data.getState().equals(MetricStateEnum.CRITICAL)){
							vo.setInterfaceState("0");
						}else{
							vo.setInterfaceState("1");
						}
					}
				}else{
					vo.setInterfaceState(instance.getModulePropBykey(ResourceOrMetricConst.RESOURCE_AVAILABILITY)[0]);
				}
			}
			if(instance.getShowName() != null && !(instance.getShowName().equals(""))){
				vo.setResourceName(instance.getShowName());
			}else{
				vo.setResourceName(instance.getName());
			}
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null){
				vo.setStrategyID(-1);
			}else{
				vo.setStrategyID(info.getProfileId());
				vo.setStrategyName(info.getProfileName());
			}
			
			voList.add(vo);
			
		}
		
		pageVo.setTotalRecord(resourceInstanceList.size());
		pageVo.setResources(voList);
		
		return toSuccess(JSONObject.toJSON(pageVo));
		
		
	}
	
	/**
	 * 为子策略展示所有符合条件的主资源
	 * @param
	 * @return
	 */
	@RequestMapping("/getMainProfileResourceInstancesForChildProfile")
	public JSONObject getMainProfileResourceInstancesForChildProfile(String resourceId,Integer state,Long domainId,String searchContent,Long mainProfileId,
			Integer start,Integer pageSize,String childResourceId,Long childProfileId,HttpSession session){
		
		ILoginUser user = getLoginUser(session);
		
		MainResourceStrategyPageVo pageVo = new MainResourceStrategyPageVo();
		
		List<ResourceInstanceBo> resourceInstances = strategyDetailApi.getMainProfileResourceInstances(resourceId,state,domainId,searchContent,user);
		
		List<MainResourceStrategyVo> voList = new ArrayList<MainResourceStrategyVo>();
		
		for(ResourceInstanceBo instance : resourceInstances){
			
			List<ResourceInstance> childResourceInstanceList = strategyDetailApi.getChildResourceInstancesByParentId(instance.getId(),childResourceId,domainId,user,null,null);
			
			MainResourceStrategyVo vo = this.getVoNoStrategy(instance);
			
			//子资源选中情况，0为全未选中，1为部分选中，2为全部选中
			long selectCount = 0;
			
			for(ResourceInstance childInstance : childResourceInstanceList){
				ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(childInstance.getId());
				if(info != null && info.getProfileId() == childProfileId){
					selectCount++;
				}
			}
			
			if(childProfileId == -1){
				vo.setSelectStatus(-1);
			}else{
				if(selectCount == childResourceInstanceList.size()){
					//全选中
					vo.setSelectStatus(2);
				}else if(selectCount == 0){
					vo.setSelectStatus(0);
				}else{
					
					vo.setSelectStatus(1);
				}
			}
			
			vo.setLifeState(instance.getLifeState().toString());
			ProfileInfo info = strategyDetailApi.getProfileInfoByResourceId(instance.getId());
			if(info == null || info.getProfileId() != mainProfileId){
				continue;
			}else{
				vo.setStrategyID(info.getProfileId());
				vo.setStrategyName(info.getProfileName());
			}
			
			voList.add(vo);
			
		}
		
		if((start + pageSize) > voList.size()){
			voList = voList.subList(start, voList.size());
		}else{
			voList = voList.subList(start, (start + pageSize));
		}
		
		pageVo.setResources(voList);
		
		return toSuccess(pageVo);
		
	}
	
	/**
	 * 删除子策略
	 * @param profileId
	 * @return
	 */
	@RequestMapping("/removeProfileById")
	public JSONObject removeProfileById(Long profileId){
		
		return toSuccess(strategyDetailApi.removeChildProfileByIds(profileId));
		
	}
	
	/**
	 * 批量删除子策略
	 * @param profileId
	 * @return
	 */
	@RequestMapping("/removeProfileByIdList")
	public JSONObject removeProfileByIdList(String profileIds,HttpSession session){
		
		List<Long> list = new ArrayList<Long>();
		
		String[] profileArray = profileIds.split(",");
		
		for(String id : profileArray){
			list.add(Long.parseLong(id));
		}
		
		return toSuccess(strategyDetailApi.removeChildProfileByIdList(list));
		
	}
	
	/**
	 * 获取所有默认主策略
	 * @return
	 */
	@RequestMapping("/getMainDefaultProfile")
	public JSONObject getMainDefaultProfile(Long resourceId){
		List<ProfileInfo> list = null;
		try {
			list = strategyDetailApi.getAllDefautProfileInfo(resourceId);
		} catch (Exception e) {
			// TODO: handle exception
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(),e);
			}
		}
		return toSuccess(list);
	}
	
	/**
	 * 获取所有默认主策略
	 * @return
	 */
	@RequestMapping("/getChildProfileType")
	public JSONObject getChildProfileType(String resourceId){
		
		List<ResourceDef> resourceDefList = strategyDetailApi.getChildProfileTypeByResourceId(resourceId);
		List<ResourceDefVo> voList = new ArrayList<ResourceDefVo>();
		for(ResourceDef def : resourceDefList){
			
			voList.add(this.resourceDefBoToResourceDefVo(def));
			
		}
		
		return toSuccess(voList);
		
	}
	
	/**
	 * 添加子策略
	 * @return
	 */
	@RequestMapping("/addChildProfile")
	public JSONObject addChildProfile(ProfileInfoVo info,HttpSession session){
		
		ILoginUser user = getLoginUser(session);
		
		ProfileInfo parentInfo = new ProfileInfo();
		parentInfo.setProfileId(info.getProfileParentId());
		
//		Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
//		String domainString = "";
//		for(IDomain domain : domains){
//			domainString += domain.getName() + ",";
//		}
		
		ProfileInfo childInfo = new ProfileInfo();
		childInfo.setProfileName(info.getProfileName());
		childInfo.setResourceId(info.getResourceId());
		childInfo.setProfileDesc(info.getProfileDesc());
		childInfo.setUpdateUser(user.getName());
//		childInfo.setUpdateUserDomain(domainString);
		
		ProfileTypeEnum type = ProfileTypeEnum.SPECIAL;
		
		if(info.getProfileTypeMapping() == 1){
			type = ProfileTypeEnum.SPECIAL;
		}else if(info.getProfileTypeMapping() == 2){
			type = ProfileTypeEnum.PERSONALIZE;
		}
		
		boolean isSuccess = strategyDetailApi.addChildProfile(info.getProfileParentId(), childInfo, type,parentInfo);
		
		return toSuccess(isSuccess);
		
	}
	
	/**
	 * 修改策略基本信息
	 */
	@RequestMapping("/updateBasicProfileInfo")
	public JSONObject updateBasicProfileInfo(ProfileInfo info,HttpSession session){
		
		ILoginUser user = getLoginUser(session);
		
		info.setUpdateUser(user.getName());
		
		boolean isSuccess = strategyDetailApi.updateProfileBasicInfo(info);
		
		return toSuccess(isSuccess);
		
	}
	
	/**
	 * 更新策略的资源
	 */
	@RequestMapping("/updateProfileResourceByIdList")
	public JSONObject updateProfileResourceByIdList(ProfileInfoVo info,HttpSession session){
		
		ProfileInfo basicInfo = new ProfileInfo();
		basicInfo.setProfileId(info.getProfileId());
		ILoginUser user = getLoginUser(session);
		basicInfo.setUpdateUser(user.getName());
		
		List<Long> instances = new ArrayList<Long>();
		List<Long> reduceInstances = new ArrayList<Long>();
		
		String[] resourceIds = null;
		String[] reduceInstancesIds = null;
		
		boolean isSuccess = false;
		
		if(info.getResources() != null && !info.getResources().trim().equals("")){
			
			if(info.getResources().contains(",")){
				
				resourceIds = info.getResources().split(",");
				
			}else{
				
				resourceIds = new String[]{info.getResources()};
				
			}
			
			for(int i = 0 ; i < resourceIds.length ; i ++){
				
				instances.add(Long.parseLong(resourceIds[i]));
				
			}
			
		}
		
		if(info.getReduceResources() != null && !info.getReduceResources().trim().equals("")){
			
			if(info.getReduceResources().contains(",")){
				
				reduceInstancesIds = info.getReduceResources().split(",");
				
			}else{
				
				reduceInstancesIds = new String[]{info.getReduceResources()};
				
			}
			
			for(int i = 0 ; i < reduceInstancesIds.length ; i ++){
				
				reduceInstances.add(Long.parseLong(reduceInstancesIds[i]));
				
			}
			
		}
		
		isSuccess = strategyDetailApi.addAndDeleteProfileResource(info.getProfileId(), instances, reduceInstances,basicInfo);
		
		
		return toSuccess(isSuccess);
		
	}
	
	/**
	 * 更新指标信息
	 */
	@RequestMapping("/updateMetricInfo")
	public JSONObject updateMetricInfo(String metricString,HttpSession session){
		
		
		MetricUpdateParameterVo metricVo = JSONObject.parseObject(metricString, MetricUpdateParameterVo.class);
		ProfileInfo info = new ProfileInfo();
		info.setProfileId(metricVo.getProfileId());
		ILoginUser user = getLoginUser(session);
		info.setUpdateUser(user.getName());
		
		if(metricVo.getAlarmsMap() != null){
			strategyDetailApi.updateProfileMetricAlarm(metricVo.getProfileId(), metricVo.getAlarmsMap(),info);
		}
		
		if(metricVo.getFlappingValueMap() != null){
			strategyDetailApi.updateProfileMetricAlarmFlapping(metricVo.getProfileId(), metricVo.getFlappingValueMap(),info);
		}

		if(metricVo.getFrequencyValueMap() != null){
			strategyDetailApi.updateProfileMetricFrequency(metricVo.getProfileId(), metricVo.getFrequencyValueMap(),info);
		}
		
		if(metricVo.getMonitorMap() != null){
			strategyDetailApi.updateProfileMetricMonitor(metricVo.getProfileId(), metricVo.getMonitorMap(),info);
		}
		
		List<ProfileMetricBo> metricList = new ArrayList<ProfileMetricBo>();
		
		try {
			metricList = profileApi.getProfileMetrics(metricVo.getProfileId());
		} catch (ProfilelibException e) {
			logger.error(e.getMessage());
		}
		
		return toSuccess(metricList);
	}
	
	/**
	 * 更新指标信息
	 */
	@RequestMapping("/updateMetricThresholdInfo")
	public JSONObject updateMetricThresholdInfo(String metricString,HttpSession session){
		
		MetricUpdateThresholdParameter metricThreshold = JSONObject.parseObject(metricString, MetricUpdateThresholdParameter.class);
		ProfileInfo info = new ProfileInfo();
		info.setProfileId(metricThreshold.getProfileId());
		ILoginUser user = getLoginUser(session);
		info.setUpdateUser(user.getName());
		
		return toSuccess(strategyDetailApi.updateProfileMetricThreshold(metricThreshold.getThresholdsMap(),info));
		
	}
	
	/**
	 * 将指定资源实例添加进默认策略
	 */
	@RequestMapping("/addProfileIntoDefultProfile")
	public JSONObject addProfileIntoDefultProfile(Long resourceInstanceId,Long profileId,HttpSession session){
		
		ProfileInfo info = new ProfileInfo();
		info.setProfileId(profileId);
		ILoginUser user = getLoginUser(session);
		info.setUpdateUser(user.getName());
		
		boolean isSuccess = strategyDetailApi.addProfileIntoDefult(resourceInstanceId,info);
		
		return toSuccess(isSuccess);
		
	}
	
	/**
	 * 将指定资源实例添加进之前的个性化策略
	 */
	@RequestMapping("/addInstanceIntoHistoryPersonalProfile")
	public JSONObject addInstanceIntoHistoryPersonalProfile(Long profileId,Long instanceId,HttpSession session){
		
		ProfileInfo info = new ProfileInfo();
		info.setProfileId(profileId);
		ILoginUser user = getLoginUser(session);
		info.setUpdateUser(user.getName());
		
		boolean isSuccess = strategyDetailApi.addInstanceIntoHistoryPersonalProfile(profileId,instanceId,info);
		
		return toSuccess(isSuccess);
		
	}
	
	/**
	 * 将指定资源实例添加进自定义策略
	 */
	@RequestMapping("/addProfileIntoSpecialProfile")
	public JSONObject addProfileIntoSpecialProfile(Long resourceInstanceId, Long profileId,HttpSession session){
		
		ProfileInfo info = new ProfileInfo();
		info.setProfileId(profileId);
		ILoginUser user = getLoginUser(session);
		info.setUpdateUser(user.getName());
		
		boolean isSuccess = strategyDetailApi.addProfileIntoSpecial(resourceInstanceId, profileId,info);
		
		return toSuccess(isSuccess);
		
	}
	
	/**
	 * 添加一个个性化策略并加入监控
	 */
	@RequestMapping("/addPersonalizeProfileAndMonitor")
	public JSONObject addPersonalizeProfileAndMonitor(String profile,HttpSession session){
		
		Profile profileEntity = JSONObject.parseObject(profile, Profile.class);
		
		long newProfileId = strategyDetailApi.addPersonalizeProfile(profileEntity,getLoginUser(session));
		
		return toSuccess(newProfileId);
		
	}
	
	/**
	 * 删除一个个性化策略
	 */
	@RequestMapping("/updateProfile")
	public JSONObject updateProfile(Long mainProfileId,String profileInfo,String profileInfoVo,String metricString,Long updateTime,HttpSession session){

		ProfileInfo basicInfo = new ProfileInfo();
		ILoginUser user = getLoginUser(session);
		basicInfo.setUpdateUser(user.getName());
		basicInfo.setUpdateTime(new Date(updateTime));
		
		boolean isSuccess = true;
		if(profileInfo != null && !profileInfo.equals("null")){
			
			ProfileInfo info = JSONObject.parseObject(profileInfo, ProfileInfo.class);
			info.setUpdateUser(user.getName());
			info.setUpdateTime(new Date(updateTime));
			isSuccess = strategyDetailApi.updateProfileBasicInfo(info);
			
			if(!isSuccess){
				return toSuccess(isSuccess);
			}
			
		}
		
		if(profileInfoVo != null && !profileInfoVo.equals("null")){
			
			List<Long> instances = new ArrayList<Long>();
			List<Long> reduceInstances = new ArrayList<Long>();
			
			String[] resourceIds = null;
			String[] reduceInstancesIds = null;
			
			ProfileInfoVo infoVo = JSONObject.parseObject(profileInfoVo, ProfileInfoVo.class);
			
			if(infoVo.getResources() != null && !infoVo.getResources().trim().equals("")){
				
				if(infoVo.getResources().contains(",")){
					
					resourceIds = infoVo.getResources().split(",");
					
				}else{
					
					resourceIds = new String[]{infoVo.getResources()};
					
				}
				
				for(int i = 0 ; i < resourceIds.length ; i ++){
					
					instances.add(Long.parseLong(resourceIds[i]));
					
				}
				
			}
			
			if(infoVo.getReduceResources() != null && !infoVo.getReduceResources().trim().equals("")){
				
				if(infoVo.getReduceResources().contains(",")){
					
					reduceInstancesIds = infoVo.getReduceResources().split(",");
					
				}else{
					
					reduceInstancesIds = new String[]{infoVo.getReduceResources()};
					
				}
				
				for(int i = 0 ; i < reduceInstancesIds.length ; i ++){
					
					reduceInstances.add(Long.parseLong(reduceInstancesIds[i]));
					
				}
				
			}
			basicInfo.setProfileId(infoVo.getProfileId());
			isSuccess = strategyDetailApi.addAndDeleteProfileResource(infoVo.getProfileId(), instances, reduceInstances,basicInfo);
			
			if(!isSuccess){
				return toSuccess(isSuccess);
			}
		}
		
		if(metricString != null && !metricString.equals("null")){
			
			MetricUpdateParameterVo metricVo = JSONObject.parseObject(metricString, MetricUpdateParameterVo.class);
			basicInfo.setProfileId(metricVo.getProfileId());
			if(metricVo.getAlarmsMap() != null){
				isSuccess = strategyDetailApi.updateProfileMetricAlarm(metricVo.getProfileId(), metricVo.getAlarmsMap(),basicInfo);
			}
			
			if(metricVo.getFlappingValueMap() != null){
				isSuccess = strategyDetailApi.updateProfileMetricAlarmFlapping(metricVo.getProfileId(), metricVo.getFlappingValueMap(),basicInfo);
			}
			
			if(metricVo.getFrequencyValueMap() != null){
				isSuccess = strategyDetailApi.updateProfileMetricFrequency(metricVo.getProfileId(), metricVo.getFrequencyValueMap(),basicInfo);
			}
			
			if(metricVo.getMonitorMap() != null){
				isSuccess = strategyDetailApi.updateProfileMetricMonitor(metricVo.getProfileId(), metricVo.getMonitorMap(),basicInfo);
			}
		}
		
		if(isSuccess){
			ProfileBo bo = null;
			try {
				bo =  profileApi.getProfile(mainProfileId);
			} catch (ProfilelibException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
			return toSuccess(bo);
		}else{
			return toSuccess(isSuccess);
		}
		
		
	}
	
	/**
	 * 获取个性化策略profile
	 * @param
	 * @return
	 */
	@RequestMapping("/getEmptyPersonalizeProfile")
	public JSONObject getEmptyPersonalizeProfile(String resourceId,Long instanceId) {
		
		ProfileBo bo = null;
		
		bo =  profileApi.getPersonalizeProfile(resourceId,instanceId);
		
		return toSuccess(bo);
		
	}
	@RequestMapping("/getInterfaceState")
	public JSONObject getInterfaceState(String mainInstanceId,String type) {

		/*
		String[] childInstanceIdArr = childrenInstanceId.split(",");
		for (int i = 0; childInstanceIdArr != null && i < childInstanceIdArr.length; i++) {
			if (StringUtil.isNumber(childInstanceIdArr[i])) {
				childIdList.add(Long.parseLong(childInstanceIdArr[i]));
			}
		}
		*/
		List<MetricData> msg = strategyDetailApi.getInterfaceState(Long.parseLong(mainInstanceId),type);
		return toSuccess(msg);
	}
	private ResourceDefVo resourceDefBoToResourceDefVo(ResourceDef resourceDef){

		ResourceDefVo resourceDefVo = new ResourceDefVo();
		resourceDefVo.setId(resourceDef.getId());
		resourceDefVo.setName(resourceDef.getName());
		
		return resourceDefVo;
		
	}
	
	public MainResourceStrategyVo getVoNoStrategy(Object object){
		
		MainResourceStrategyVo vo = new MainResourceStrategyVo();
		
		if(object instanceof ResourceInstanceBo){
			ResourceInstanceBo instance = (ResourceInstanceBo)object;
			vo.setResourceId(instance.getId());
			vo.setResourceIp(instance.getDiscoverIP());
			vo.setDomainId(instance.getDomainId());
			if(instance.getShowName() == null){
				vo.setResourceShowName("");
			}else{
				vo.setResourceShowName(instance.getShowName());
			}
		}else if(object instanceof ResourceInstance){
			ResourceInstance instance = (ResourceInstance)object;
			vo.setResourceId(instance.getId());
			vo.setResourceIp(instance.getShowIP());
			vo.setDomainId(instance.getDomainId());
			if(instance.getShowName() == null){
				vo.setResourceShowName("");
			}else{
				vo.setResourceShowName(instance.getShowName());
			}
		}
		
		return vo;
		
	}
	
}
