package com.mainsteam.stm.portal.resource.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.FileColumnConsts;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.caplib.dict.ValueTypeEnum;
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
import com.mainsteam.stm.portal.resource.api.FileMetricDataApi;
import com.mainsteam.stm.portal.resource.bo.AddInstanceResult;
import com.mainsteam.stm.portal.resource.bo.FileMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.FileMetricDataPageBo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.util.MetricDataUtil;
import com.mainsteam.stm.util.SecureUtil;
import com.mainsteam.stm.util.UnitTransformUtil;

public class FileMetricDataImpl implements FileMetricDataApi {

	private static final Log logger = LogFactory.getLog(FileMetricDataImpl.class);
	
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
	
	private static String SCAN_PARAMETER_KEY = "path";
	
	/**
	 * 获取指标实时数据
	 */
	@Override
	public FileMetricDataPageBo queryRealTimeMetricDatas(Long mainInstanceId) {
		//获取子资源
		List<Long> instanceIdsList = new ArrayList<Long>();
		
		List<FileMetricDataBo> resultDataList = new ArrayList<FileMetricDataBo>();
		
		FileMetricDataPageBo pageBo = new FileMetricDataPageBo();
		
		List<String> metricInformationSet = new ArrayList<String>();
		List<String> metricAvailabilitySet = new ArrayList<String>();
		ResourceMetricDef[] metricDefs = null;
		List<ResourceInstance> childInstanceList = null;
		List<ResourceInstance> childFileInstanceList = new ArrayList<ResourceInstance>();
		try {
			childInstanceList = resourceInstanceService.getChildInstanceByParentId(mainInstanceId);
			
			if(childInstanceList == null || childInstanceList.size() <= 0){
				if(logger.isDebugEnabled()){
					logger.debug("ChildInstanceList is null , mainInstanceId is " + mainInstanceId);
				}
				pageBo.setFileData(resultDataList);
				return pageBo;
			}
			
			int index = 0;
			for(int i = 0 ; i < childInstanceList.size() ; i ++){
				ResourceInstance instance = childInstanceList.get(i);

				if(!(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED))){
					//非监控资源不展现
					continue;
				}
				
				String resourceId = instance.getResourceId();
				ResourceDef def = capacityService.getResourceDefById(resourceId);
				if(def.getType().equals(ResourceTypeConsts.TYPE_FILE)){
					resultDataList.add(new FileMetricDataBo());
					childFileInstanceList.add(instance);
					//文件子资源
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
				logger.debug("Child file instanceList is null , mainInstanceId is " + mainInstanceId);
			}
			pageBo.setFileData(resultDataList);
			return pageBo;
		}
		
		for(ResourceMetricDef metricDef : metricDefs){
			if(metricDef.getMetricType().equals(MetricTypeEnum.AvailabilityMetric)){
				//可用性指标
				metricAvailabilitySet.add(metricDef.getId());
			}else if(metricDef.getMetricType().equals(MetricTypeEnum.InformationMetric)){
				//信息指标
				metricInformationSet.add(metricDef.getId());
			}
		}
		
		long[] instancesIds = new long[instanceIdsList.size()];
		
		for(int i = 0 ; i < instanceIdsList.size() ; i++){
			instancesIds[i] = instanceIdsList.get(i);
		}
		
		//正常子资源
		List<FileMetricDataBo> resultNormalDataList = new ArrayList<FileMetricDataBo>();
		//致命子资源
		List<FileMetricDataBo> resultCriticalDataList = new ArrayList<FileMetricDataBo>();
		//未知子资源
		List<FileMetricDataBo> resultUnknownDataList = new ArrayList<FileMetricDataBo>();
		for(int i = 0 ; i < resultDataList.size() ; i++){
			
			resultDataList.get(i).setResourceId(instancesIds[i]);
			
			//获取信息指标信息
			for(int j = 0 ; j < metricInformationSet.size() ; j ++){
				MetricData metricInfoData = metricDataService.getMetricInfoData(instancesIds[i], metricInformationSet.get(j));
				ResourceMetricDef def = capacityService.getResourceMetricDef(childFileInstanceList.get(i).getResourceId(), metricInformationSet.get(j));
				
				//文件名从资源实例获取
				try {
					ResourceInstance intance = resourceInstanceService.getResourceInstance(instancesIds[i]);
					if(intance == null){
						if(logger.isErrorEnabled()){
							logger.error("Get intance error,id : " + instancesIds[i]);
						}
						continue;
					}
					resultDataList.get(i).setFileName(intance.getShowName());
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
				case FileColumnConsts.FILE_PATH:
					resultDataList.get(i).setFilePath(informationMetricData);
					break;
				case FileColumnConsts.FILE_SIZE:
					resultDataList.get(i).setFileSizeValue(originalData);
					resultDataList.get(i).setFileSize(informationMetricData);
					break;
				case FileColumnConsts.FILE_MODIFYTIME:
					resultDataList.get(i).setFileModifyTime(informationMetricData);
					break;
				case "fileMD5":
					resultDataList.get(i).setFileMdCollect(informationMetricData);
					break;
//					try {
//						ModuleProp md5prop = modulePropService.getPropByInstanceAndKey(instancesIds[i], "fileMD5");
//						if(md5prop!=null){
//							String[] fileMD5 = md5prop.getValues();
//							resultDataList.get(i).setFileMdStandard(fileMD5[0]);
//							resultDataList.get(i).setFileMdCollect(informationMetricData);
//						}
//					} catch (InstancelibException e) {
//						e.printStackTrace();
//					}
					
				default:
					
					break;
				}
			}
			
			for(int j = 0 ; j < metricAvailabilitySet.size() ; j ++){
				MetricStateData avaliableData = metricStateService.getMetricState(instancesIds[i], metricAvailabilitySet.get(j));
				if(avaliableData == null){
					if(logger.isErrorEnabled()){
						logger.error("Get metricState error , instancesId = " + instancesIds[i] + ",metricId = " + metricAvailabilitySet.get(j));
					}
//					resultDataList.get(i).setFileAvail(MetricStateEnum.UNKOWN.toString());
					resultDataList.get(i).setFileAvail("yes");
					resultUnknownDataList.add(resultDataList.get(i));
					continue;
				}
				switch (metricAvailabilitySet.get(j)) {
				case FileColumnConsts.FILE_AVAIL:
					if(avaliableData.getState() == MetricStateEnum.NORMAL || avaliableData.getState() == MetricStateEnum.NORMAL_NOTHING ||
							   avaliableData.getState() == MetricStateEnum.NORMAL_UNKNOWN || avaliableData.getState() == MetricStateEnum.SERIOUS ||
							   avaliableData.getState() == MetricStateEnum.WARN){
						resultDataList.get(i).setFileAvail("yes");
						resultNormalDataList.add(resultDataList.get(i));
					}else if(avaliableData.getState() == MetricStateEnum.CRITICAL || avaliableData.getState() == MetricStateEnum.CRITICAL_NOTHING){
						resultDataList.get(i).setFileAvail("no");
						resultCriticalDataList.add(resultDataList.get(i));
					}else{
//						resultDataList.get(i).setFileAvail(MetricStateEnum.UNKOWN.toString());
						resultDataList.get(i).setFileAvail("yes");
						resultUnknownDataList.add(resultDataList.get(i));
					}
					break;
				default:
					break;
				}
			}
		}
		
		resultDataList = new ArrayList<FileMetricDataBo>();
		
		resultDataList.addAll(resultNormalDataList);
		resultDataList.addAll(resultCriticalDataList);
		resultDataList.addAll(resultUnknownDataList);
		
		pageBo.setFileData(resultDataList);
		pageBo.setRowCount(instancesIds.length);
		pageBo.setStartRow(0);
		pageBo.setTotalRecord(instancesIds.length);
		return pageBo;
	}
	
	public FileMetricDataPageBo scanCurrentFileData(long mainInstanceId,String queryPath) {
		//获取子资源
		MetricData metricData = null;
		try {
			
			Map<String, String> paramMap = new HashMap<String,String>();
			paramMap.put(SCAN_PARAMETER_KEY, queryPath);
//			paramMap.put(SCAN_PARAMETER_KEY, "/");
			metricData = metricDataService.catchRealtimeMetricData(mainInstanceId, MetricIdConsts.FULL_FILE, paramMap);
			
		} catch (MetricExecutorException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e){
			logger.error(e.getMessage(),e);
		}
		
		if(metricData == null || metricData.getData() == null){
			if(logger.isErrorEnabled()){
				logger.error("Scan file error");
			}
			FileMetricDataPageBo pageBo = new FileMetricDataPageBo();
			pageBo.setFileData(new ArrayList<FileMetricDataBo>());
			pageBo.setRowCount(0);
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(0);
			return pageBo;
		}
		
		String[] dataString = metricData.getData();
		
		List<FileMetricDataBo> dataList = new ArrayList<FileMetricDataBo>();
		
		if(dataString == null || dataString.length <= 0){
			if(logger.isErrorEnabled()){
				logger.error("Scan file error");
			}
			FileMetricDataPageBo pageBo = new FileMetricDataPageBo();
			pageBo.setFileData(dataList);
			pageBo.setRowCount(dataList.size());
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(dataList.size());
			return pageBo;
		}
		
		List<Map<String, String>> parseTableResultSet = MetricDataUtil.parseTableResultSet(dataString);
		
		if(parseTableResultSet!=null && !parseTableResultSet.isEmpty()){
			for (int i = 0; i < parseTableResultSet.size(); i++) {
				FileMetricDataBo dataBo = new FileMetricDataBo();
				Map<String, String> mapOne = parseTableResultSet.get(i);
				Set<String> keySet = mapOne.keySet();
				for (String string : keySet) {
					switch (string) {
					case FileColumnConsts.FILE_MODIFYTIME:
						dataBo.setFileModifyTime(mapOne.get(string));
						break;
					case FileColumnConsts.FILE_NAME:
						dataBo.setFileName(mapOne.get(string));
						break;
					case FileColumnConsts.FILE_SIZE:
						dataBo.setFileSize(mapOne.get(string));
						break;

					default:
						break;
					}
				}
//				dataBo.setFileName(parseTableResultSet.get(i).get(FileColumnConsts.FILE_NAME));
//				dataBo.setFileSize(parseTableResultSet.get(i).get(FileColumnConsts.FILE_SIZE));
//				dataBo.setFileModifyTime(parseTableResultSet.get(i).get(FileColumnConsts.FILE_MODIFYTIME));
				dataList.add(dataBo);
			}
		}
		
//		String splitString = ",";
//		
//		for(int i = 1 ; i < dataString.length ; i ++){
//			FileMetricDataBo dataBo = new FileMetricDataBo();
//			dataList.add(dataBo);
//		}
//		
//		String[] dataTitles = dataString[0].split(splitString);
//		
//		for(int i = 0 ; i < dataTitles.length ; i ++){
//			
//			switch (dataTitles[i]) {
//			case FileColumnConsts.FILE_MODIFYTIME:
//				
//				//更新时间
//				for(int j = 1 ; j < dataString.length ; j ++){
//					String modifyTime = dataString[j].split(splitString)[i];
//					if(modifyTime.indexOf(".")>0){
//						modifyTime = modifyTime.substring(0, modifyTime.indexOf("."));
//					}
//					
//					dataList.get(j - 1).setFileModifyTime(modifyTime);
//				}
//				
//				break;
//			case FileColumnConsts.FILE_NAME:
//				
//				//名称
//				for(int j = 1 ; j < dataString.length ; j ++){
//					dataList.get(j - 1).setFileName(dataString[j].split(splitString)[i]);
//				}
//				
//				break;
//			case FileColumnConsts.FILE_SIZE:
//				
//				//文件大小
//				for(int j = 1 ; j < dataString.length ; j ++){
//					dataList.get(j - 1).setFileSizeValue(dataString[j].split(splitString)[i]);
//					String value = UnitTransformUtil.transform(dataString[j].split(splitString)[i], "Byte");
//					dataList.get(j - 1).setFileSize(value);
//				}
//				
//				break;
//
//			default:
//				break;
//			}
//			
//		}
		
		FileMetricDataPageBo pageBo = new FileMetricDataPageBo();
		pageBo.setFileData(dataList);
		pageBo.setRowCount(dataList.size());
		pageBo.setStartRow(0);
		pageBo.setTotalRecord(dataList.size());
		return pageBo;
	}

	@Override
	public AddInstanceResult addFileMonitor(List<FileMetricDataBo> fileList,long mainInstanceId,String filePath,ILoginUser user) {
		
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
		String childResourceFileId = def.getChildResourceIdByType(ResourceTypeConsts.TYPE_FILE);
		
		List<Long> instances = new ArrayList<Long>();
		
		for(int i = 0 ; i < fileList.size() ; i++){
			FileMetricDataBo fileMetricDataBo = fileList.get(i);
			
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
				
				ModuleProp modulePropInstanceIdKeyValues = new ModuleProp();
				modulePropInstanceIdKeyValues.setKey(FileColumnConsts.FILE_PATH);
				String[] fileValueList = new String[1];
				
				String fileValue_0 = "";
				if(fileMetricDataBo.getFileName().startsWith("/")){
					//特殊处理,文件名以"/"开头
					fileValue_0 = fileMetricDataBo.getFileName();
				}else if(filePath.endsWith("/")){
					//filePath不能以/结尾,否则这里出错,前台处理
					fileValue_0 = filePath + fileMetricDataBo.getFileName();
				}else{
					fileValue_0 = filePath + "/" + fileMetricDataBo.getFileName();
				}
				fileValueList[0] = fileValue_0;
				modulePropInstanceIdKeyValues.setValues(fileValueList);
				moduleProps.add(modulePropInstanceIdKeyValues);
				
				ModuleProp modulePropIdentity = new ModuleProp();
				modulePropIdentity.setKey("INST_IDENTY_KEY");
				String[] identityValueList = new String[1];
				identityValueList[0] = SecureUtil.md5Encryp(fileValue_0);
				modulePropIdentity.setValues(identityValueList);
				moduleProps.add(modulePropIdentity);
				
				childInstance.setName(fileMetricDataBo.getFileName());
				childInstance.setShowName(fileMetricDataBo.getFileName());
				childInstance.setResourceId(childResourceFileId);
				childInstance.setModuleProps(moduleProps);
				childInstance.setChildType(ResourceTypeConsts.TYPE_FILE);
				childInstance.setDiscoverNode(mainInstance.getDiscoverNode());
				
				ResourceInstanceResult resourceInstanceResult = resourceInstanceService.addResourceInstance(childInstance);
				
				if(resourceInstanceResult.isRepeat()){
					//已经有了该文件
					
					//判断资源是否监控
					ResourceInstance addInstance = resourceInstanceService.getResourceInstance(resourceInstanceResult.getResourceInstanceId());
					if(addInstance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED){
						if(logger.isInfoEnabled()){
							logger.info("Add resource instance id = " + resourceInstanceResult.getResourceInstanceId());
						}
						instances.add(resourceInstanceResult.getResourceInstanceId());
					}
					
					if(addInstance.getLifeState() == InstanceLifeStateEnum.MONITORED){
						//重复文件
						repeatInstanceList.add(fileMetricDataBo.getFileName());
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
				failInstanceList.add(fileMetricDataBo.getFileName());
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
	
	//添加子策略
	private long addChildrenProfile(ProfileInfo mainProfileInfo,ILoginUser user){
		ProfileInfo childInfo = new ProfileInfo();
		ResourceDef def = capacityService.getResourceDefById(mainProfileInfo.getResourceId());
		String childResourceFileId = def.getChildResourceIdByType(ResourceTypeConsts.TYPE_FILE);
		
		childInfo.setProfileDesc("自动添加的文件子策略");
		childInfo.setProfileName("文件");
		childInfo.setProfileType(mainProfileInfo.getProfileType());
		childInfo.setResourceId(childResourceFileId);
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
	public boolean deleteFileInstance(String fileInstanceIds) {
		
		List<Long> instanceIdList = new ArrayList<Long>();
		
		for (String id : fileInstanceIds.split(",")) {
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
