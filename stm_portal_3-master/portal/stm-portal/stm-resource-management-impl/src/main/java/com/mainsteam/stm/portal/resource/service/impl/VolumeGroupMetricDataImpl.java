package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.FullVolumeGroupColumnConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.obj.ResourceInstanceResult;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.VolumeGroupMetricDataApi;
import com.mainsteam.stm.portal.resource.bo.AddInstanceResult;
import com.mainsteam.stm.portal.resource.bo.LogicalVolumeMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.LogicalVolumeMetricDataPageBo;
import com.mainsteam.stm.portal.resource.bo.VolumeGroupMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.VolumeGroupMetricDataPageBo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.util.UnitTransformUtil;

public class VolumeGroupMetricDataImpl implements VolumeGroupMetricDataApi{
	private static final Log logger = LogFactory.getLog(VolumeGroupMetricDataImpl.class);
	
	@Resource
	private MetricDataService metricDataService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private ProfileService profileService;
	
	@Resource
	private ModulePropService modulePropService;
	
	@Resource
	private MetricStateService metricStateService;
	
	@Resource
	private CapacityService capacityService;
	
	private static String REAL_TIME_DATA_INSTANCE_ID_NAME = "instanceid";
	
	private static String VOLUME_GROUPS = ResourceTypeConsts.VOLUME_GROUPS;
	
	//用于扫描的指标
	private static String FULLVOLUMEGROUP= "fullVolumeGroup";
	
	@Override
	public AddInstanceResult addVolumeGroupMonitor(
			List<VolumeGroupMetricDataBo> logicalVolumeList,
			long mainInstanceId, ILoginUser user) {

		AddInstanceResult result = new AddInstanceResult();
		
		//重复子资源列表
		List<String> repeatInstanceList = new ArrayList<String>();
		//添加失败资源列表
		List<String> failInstanceList = new ArrayList<String>();
		
		//先查出主资源实例
		ResourceInstance mainInstance = null;
		try {
			mainInstance = resourceInstanceService.getResourceInstance(mainInstanceId);
		} catch (InstancelibException e2) {
			if(logger.isErrorEnabled()){
				logger.error(e2.getMessage(),e2);
			}
		}
		ResourceDef def = capacityService.getResourceDefById(mainInstance.getResourceId());
		String childResourceVolumeGroupId = def.getChildResourceIdByType(VOLUME_GROUPS);
		
		List<Long> instances = new ArrayList<Long>();
		
		for(int i = 0 ; i < logicalVolumeList.size() ; i++){
			VolumeGroupMetricDataBo volumeGroupMetricDataBo = logicalVolumeList.get(i);
			
			String volumeGroupValue = volumeGroupMetricDataBo.getVolumeGroupName();
			
			try {
				//先入库
				ResourceInstance childInstance = new ResourceInstance();
				childInstance.setChildren(null);
				childInstance.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
				childInstance.setCustomProps(null);
				childInstance.setDiscoverWay(mainInstance.getDiscoverWay());
				childInstance.setExtendProps(null);
				childInstance.setParentId(mainInstanceId);
				childInstance.setRepeatValidate(true);
				childInstance.setDomainId(mainInstance.getDomainId());
				List<ModuleProp> moduleProps = new ArrayList<ModuleProp>();
				
				
				ModuleProp moduleProcessId = new ModuleProp();
				moduleProcessId.setKey(FullVolumeGroupColumnConsts.VOLUMEGROUPNAME);
				String[] processIdValueList = new String[1];
				processIdValueList[0] = volumeGroupValue;
				moduleProcessId.setValues(processIdValueList);
				moduleProps.add(moduleProcessId);
				
				childInstance.setName(volumeGroupValue);
				childInstance.setShowName(volumeGroupValue);
				childInstance.setResourceId(childResourceVolumeGroupId);
				childInstance.setModuleProps(moduleProps);
				childInstance.setChildType(VOLUME_GROUPS);
				childInstance.setDiscoverNode(mainInstance.getDiscoverNode());
				
				ResourceInstanceResult resourceInstanceResult = resourceInstanceService.addResourceInstance(childInstance);
				
				if(resourceInstanceResult.isRepeat()){
					//已经有了该卷组
					//判断资源是否监控
					ResourceInstance addInstance = resourceInstanceService.getResourceInstance(resourceInstanceResult.getResourceInstanceId());
					if(addInstance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED){
						if(logger.isInfoEnabled()){
							logger.info("Add resource instance id = " + resourceInstanceResult.getResourceInstanceId());
						}
						instances.add(resourceInstanceResult.getResourceInstanceId());
					}
					
					if(addInstance.getLifeState() == InstanceLifeStateEnum.MONITORED){
						//重复卷组
						repeatInstanceList.add(volumeGroupValue);
					}
					
				}else{
					//没有重复
					if(logger.isInfoEnabled()){
						logger.info("Add resource instance id = " + resourceInstanceResult.getResourceInstanceId());
					}
					instances.add(resourceInstanceResult.getResourceInstanceId());
					
				}
			} catch (InstancelibException e) {
				//添加失败
				failInstanceList.add(volumeGroupValue);
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
		}
		//指定策略并加入监控
		try {
			if(instances != null && instances.size() > 0){
//				profileService.enableMonitor(instances);
				profileService.enableChildMonitorByParentInstanceId(mainInstanceId, instances);
			}
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return null;
		}
		
		result.setRepeatInstanceList(repeatInstanceList);
		result.setFailInstanceList(failInstanceList);
		
		return result;
	}
	
	@Override
	public VolumeGroupMetricDataPageBo queryRealTimeMetricDatas(
			Long mainInstanceId) {

		//获取子资源
		List<Long> instanceIdsList = new ArrayList<Long>();
		
		List<VolumeGroupMetricDataBo> resultDataList = new ArrayList<VolumeGroupMetricDataBo>();
		
		VolumeGroupMetricDataPageBo pageBo = new VolumeGroupMetricDataPageBo();
		
		List<String> metricPerformanceSet = new ArrayList<String>();
		List<String> metricInformationSet = new ArrayList<String>();
		List<String> metricAvailabilitySet = new ArrayList<String>();
		ResourceMetricDef[] metricDefs = null;
		List<ResourceInstance> childInstanceList = null;
		List<ResourceInstance> childVolumeGroupInstanceList = new ArrayList<ResourceInstance>();
		try {
			childInstanceList = resourceInstanceService.getChildInstanceByParentId(mainInstanceId);
			
			if(childInstanceList == null || childInstanceList.size() <= 0){
				if(logger.isDebugEnabled()){
					logger.debug("LogicalVolumeMetricData ChildInstanceList is null , mainInstanceId is " + mainInstanceId);
				}
				pageBo.setVolumeGroupData(resultDataList);
				return pageBo;
			}
			
			for(int i = 0 ; i < childInstanceList.size() ; i ++){
				ResourceInstance instance = childInstanceList.get(i);

				if(!(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED))){
					//非监控资源不展现
					continue;
				}
				String resourceId = instance.getResourceId();
				ResourceDef def = capacityService.getResourceDefById(resourceId);
				if(def.getType().equals(VOLUME_GROUPS)){
					resultDataList.add(new VolumeGroupMetricDataBo());
					childVolumeGroupInstanceList.add(instance);
					//进程子资源
					instanceIdsList.add(instance.getId());
					
					//添加指标
					if(metricDefs == null){
						metricDefs = def.getMetricDefs();
					}
					
				}
				
			}
		} catch (InstancelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		
		if(instanceIdsList == null || instanceIdsList.size() <= 0){
			if(logger.isDebugEnabled()){
				logger.debug("VolumeGroupMetricData Child process instanceList is null , mainInstanceId is " + mainInstanceId);
			}
			pageBo.setVolumeGroupData(resultDataList);
			return pageBo;
		}
		
		for(ResourceMetricDef metricDef : metricDefs){
			if(metricDef.getMetricType().equals(MetricTypeEnum.AvailabilityMetric)){
				//可用性指标
				metricAvailabilitySet.add(metricDef.getId());
			}else if(metricDef.getMetricType().equals(MetricTypeEnum.InformationMetric)){
				//信息指标
				metricInformationSet.add(metricDef.getId());
			}else if(metricDef.getMetricType().equals(MetricTypeEnum.PerformanceMetric)){
				//性能指标
				metricPerformanceSet.add(metricDef.getId());
			}
		}
		
		long[] instancesIds = new long[instanceIdsList.size()];
		
		for(int i = 0 ; i < instanceIdsList.size() ; i++){
			instancesIds[i] = instanceIdsList.get(i);
		}
		
		String[] metricIds = new String[metricPerformanceSet.size()];
		for(int i = 0 ; i < metricPerformanceSet.size() ; i++){
			metricIds[i] = metricPerformanceSet.get(i);
		}
		
		//无性能指标
//		MetricRealtimeDataQuery metricRealtimeDataQuery = new MetricRealtimeDataQuery();
//		metricRealtimeDataQuery.setInstanceID(instancesIds);
//		metricRealtimeDataQuery.setMetricID(metricIds);
//		
//		//获取性能指标信息
//		Page<Map<String,?>,MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(metricRealtimeDataQuery, 0, instancesIds.length);
//		List<Map<String,?>> realTimeDataList = page.getDatas();
		
		//正常子资源
		List<VolumeGroupMetricDataBo> resultNormalDataList = new ArrayList<VolumeGroupMetricDataBo>();
		//致命子资源
		List<VolumeGroupMetricDataBo> resultCriticalDataList = new ArrayList<VolumeGroupMetricDataBo>();
		//未知子资源
		List<VolumeGroupMetricDataBo> resultUnknownDataList = new ArrayList<VolumeGroupMetricDataBo>();
		for(int i = 0 ; i < resultDataList.size() ; i++){
			
			resultDataList.get(i).setInstanceId(instancesIds[i]);
			
//			realDataFor : for(Map<String,?> map : realTimeDataList){
//				
//				if(map.get(REAL_TIME_DATA_INSTANCE_ID_NAME).equals(instancesIds[i])){
//					Set<String> iterator = map.keySet();
//					for(String key : iterator){
//						switch (key) {
//						case FullLogicalVolumeColumnConsts.LOGICALLPS:
//							ResourceMetricDef def = capacityService.getResourceMetricDef(childVolumeGroupInstanceList.get(i).getResourceId(), key);
//							if(def == null){
//								if(logger.isErrorEnabled()){
//									logger.error("CapacityService.getResourceMetricDef is null : resourceid = " + childVolumeGroupInstanceList.get(i).getResourceId() + ",metricid = " + key);
//								}
//								break;
//							}
//							if(map.get(key) == null){
//								if(logger.isErrorEnabled()){
//									logger.error("Map.get(key) error,key = " + key);
//								}
//								break;
//							}
//							
//							break realDataFor;
//						default:
//							break;
//						}
//					}
//				}
//				
//			}
			
			//获取信息指标信息
			for(int j = 0 ; j < metricInformationSet.size() ; j ++){
				MetricData metricInfoData = metricDataService.getMetricInfoData(instancesIds[i], metricInformationSet.get(j));
				ResourceMetricDef def = capacityService.getResourceMetricDef(childVolumeGroupInstanceList.get(i).getResourceId(), metricInformationSet.get(j));
				
				//名称从资源实例获取
				try {
					ResourceInstance intance = resourceInstanceService.getResourceInstance(instancesIds[i]);
					if(intance == null){
						if(logger.isErrorEnabled()){
							logger.error("Get intance error,id : " + instancesIds[i]);
						}
						continue;
					}
					resultDataList.get(i).setVolumeGroupName(intance.getShowName());
				} catch (InstancelibException e) {
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
				}
				
				if(metricInfoData == null || metricInfoData.getData() == null || metricInfoData.getData().length <= 0){
					if(logger.isErrorEnabled()){
						logger.error("Get metricInfoData error , instancesId = " + instancesIds[i] + ",metricId = " + metricInformationSet.get(j));
					}
					continue;
				}
				
				String informationMetricData = new String("");
				for(String infoData : metricInfoData.getData()){
					if(infoData != null && !infoData.equals("")){
						informationMetricData += infoData + ",";
					}
				}
				
				if(informationMetricData.equals("")){
					if(logger.isErrorEnabled()){
						logger.error("Get metricInfoData error , instancesId = " + instancesIds[i] + ",metricId = " + metricInformationSet.get(j));
					}
					continue;
				}
				informationMetricData = informationMetricData.substring(0, informationMetricData.length() - 1);
				
				String originalData = informationMetricData;
				
				informationMetricData = UnitTransformUtil.transform(informationMetricData, def.getUnit());
				switch (metricInformationSet.get(j)) {
				case FullVolumeGroupColumnConsts.VOLUMEGROUPNAME:
					resultDataList.get(i).setVolumeGroupName(informationMetricData);
					break;
					
				case FullVolumeGroupColumnConsts.LVSNUMBER:
					resultDataList.get(i).setLvsNumber(informationMetricData);
					resultDataList.get(i).setLvsNumberValue(originalData);
					
					break;
				case FullVolumeGroupColumnConsts.PPSIZE:
					resultDataList.get(i).setPpSize(informationMetricData);
					resultDataList.get(i).setPpSizeValue(originalData);
					
					break;
				case FullVolumeGroupColumnConsts.PVSNUMBER:
					resultDataList.get(i).setPvsNumber(informationMetricData);
					resultDataList.get(i).setPvsNumberValue(originalData);
					
					break;
				case FullVolumeGroupColumnConsts.TOTALPPSIZE:
					resultDataList.get(i).setTotalPpSize(informationMetricData);
					resultDataList.get(i).setTotalPpSizeValue(originalData);
					
					break;
					
					
				default:
					break;
				}
			}
			
			//获取可用性指标信息
			for(int j = 0 ; j < metricAvailabilitySet.size() ; j ++){
				MetricStateData avaliableData = metricStateService.getMetricState(instancesIds[i], metricAvailabilitySet.get(j));
				if(avaliableData == null){
					if(logger.isErrorEnabled()){
						logger.error("Get metricState error , instancesId = " + instancesIds[i] + ",metricId = " + metricAvailabilitySet.get(j));
					}
//					resultDataList.get(i).setVolumeGroupState(MetricStateEnum.UNKOWN.toString());
					resultDataList.get(i).setVolumeGroupState("yes");
					resultUnknownDataList.add(resultDataList.get(i));
					continue;
				}
				switch (metricAvailabilitySet.get(j)) {
				case FullVolumeGroupColumnConsts.VOLUMEGROUPSTATE:
					if(avaliableData.getState() == MetricStateEnum.NORMAL || avaliableData.getState() == MetricStateEnum.NORMAL_NOTHING ||
							   avaliableData.getState() == MetricStateEnum.NORMAL_UNKNOWN || avaliableData.getState() == MetricStateEnum.SERIOUS ||
							   avaliableData.getState() == MetricStateEnum.WARN){
						resultDataList.get(i).setVolumeGroupState("yes");
						resultNormalDataList.add(resultDataList.get(i));
					}else if(avaliableData.getState() == MetricStateEnum.CRITICAL || avaliableData.getState() == MetricStateEnum.CRITICAL_NOTHING){
						resultDataList.get(i).setVolumeGroupState("no");
						resultCriticalDataList.add(resultDataList.get(i));
					}else{
//						resultDataList.get(i).setVolumeGroupState(MetricStateEnum.UNKOWN.toString());
//						resultUnknownDataList.add(resultDataList.get(i));
						resultDataList.get(i).setVolumeGroupState("yes");
						resultNormalDataList.add(resultDataList.get(i));
					}
					break;
				default:
					break;
				}
			}
		}
		
		resultDataList = new ArrayList<VolumeGroupMetricDataBo>();
		
		resultDataList.addAll(resultNormalDataList);
		resultDataList.addAll(resultCriticalDataList);
		resultDataList.addAll(resultUnknownDataList);
		
		pageBo.setVolumeGroupData(resultDataList);
		pageBo.setRowCount(instancesIds.length);
		pageBo.setStartRow(0);
		pageBo.setTotalRecord(instancesIds.length);
		return pageBo;
	
	}
	
	@Override
	public VolumeGroupMetricDataPageBo scanVolumeGroupData(long mainInstanceId) {
		//获取子资源
		MetricData metricData = null;
		try {
			metricData = metricDataService.catchRealtimeMetricData(mainInstanceId, FULLVOLUMEGROUP);
			
		} catch (MetricExecutorException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e){
			logger.error(e.getMessage(),e);
		}
		
		if(metricData == null || metricData.getData() == null){
			if(logger.isErrorEnabled()){
				logger.error("Scan volumegroup error");
			}
			VolumeGroupMetricDataPageBo pageBo = new VolumeGroupMetricDataPageBo();
			pageBo.setVolumeGroupData(new ArrayList<VolumeGroupMetricDataBo>());
			pageBo.setRowCount(0);
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(0);
			return pageBo;
		}
		
		String[] dataString = metricData.getData();
		
		List<VolumeGroupMetricDataBo> dataList = new ArrayList<VolumeGroupMetricDataBo>();
		
		if(dataString == null || dataString.length <= 0){
			if(logger.isErrorEnabled()){
				logger.error("Scan volumegroup error");
			}
			
			VolumeGroupMetricDataPageBo pageBo = new VolumeGroupMetricDataPageBo();
			pageBo.setVolumeGroupData(new ArrayList<VolumeGroupMetricDataBo>());
			pageBo.setRowCount(0);
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(0);
			return pageBo;
		}
		
		String splitString = ",";
		
		for(int i = 1 ; i < dataString.length ; i ++){
			VolumeGroupMetricDataBo dataBo = new VolumeGroupMetricDataBo();
			dataList.add(dataBo);
		}
		
		String[] dataTitles = dataString[0].split(splitString);
		
		for(int i = 0 ; i < dataTitles.length ; i ++){
			
			switch (dataTitles[i]) {
			case FullVolumeGroupColumnConsts.VOLUMEGROUPNAME:
				
				for(int j = 1 ; j < dataString.length ; j ++){
					dataList.get(j - 1).setVolumeGroupName(dataString[j].split(splitString)[i]);
				}
				
				break;

			default:
				break;
			}
		}
		
		VolumeGroupMetricDataPageBo pageBo = new VolumeGroupMetricDataPageBo();
		pageBo.setVolumeGroupData(dataList);
		pageBo.setRowCount(dataList.size());
		pageBo.setStartRow(0);
		pageBo.setTotalRecord(dataList.size());
		return pageBo;
	}
	
	//添加子策略
		private long addChildrenProfile(ProfileInfo mainProfileInfo,ILoginUser user){
			ProfileInfo childInfo = new ProfileInfo();
			ResourceDef def = capacityService.getResourceDefById(mainProfileInfo.getResourceId());
			String childResourceProcessId = def.getChildResourceIdByType(VOLUME_GROUPS);
			
			childInfo.setProfileDesc("自动添加的卷组子策略");
			childInfo.setProfileName("卷组");
			childInfo.setProfileType(mainProfileInfo.getProfileType());
			childInfo.setResourceId(childResourceProcessId);
			childInfo.setUpdateUser(user.getName());
			childInfo.setCreateUser(user.getId());
			Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
			String domainString = "";
			for(IDomain domain : domains){
				domainString += domain.getName() + ",";
			}
			childInfo.setUpdateUserDomain(domainString);
			
			long newProfileId = -1;
			
			try {
				newProfileId = profileService.createChildProfile(mainProfileInfo.getProfileId(), childInfo);
			} catch (ProfilelibException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
			return newProfileId;
			
		}
		
		@Override
		public boolean deleteVolumeGroupInstance(String volumeGroupInstanceIds) {
			
			List<Long> instanceIdList = new ArrayList<Long>();
			
			for (String id : volumeGroupInstanceIds.split(",")) {
				instanceIdList.add(Long.parseLong(id));
			}
			
			try {
				resourceInstanceService.removeChildResourceInstance(instanceIdList);
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
				return false;
			}
			
			
			return true;
		}
}
