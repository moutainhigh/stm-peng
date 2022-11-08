package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.mainsteam.stm.util.MetricDataUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.ProcessColumnConsts;
import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.obj.ResourceInstanceResult;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ProcessMetricDataApi;
import com.mainsteam.stm.portal.resource.bo.AddInstanceResult;
import com.mainsteam.stm.portal.resource.bo.ProcessDeadLockDataBo;
import com.mainsteam.stm.portal.resource.bo.ProcessDeadLockDataPageBo;
import com.mainsteam.stm.portal.resource.bo.ProcessMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.ProcessMetricDataPageBo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.util.SecureUtil;
import com.mainsteam.stm.util.UnitTransformUtil;

public class ProcessMetricDataImpl implements ProcessMetricDataApi {

	private static final Log logger = LogFactory.getLog(ProcessMetricDataImpl.class);
	
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
	
	/**
	 * 获取指标实时数据
	 */
	@Override
	public ProcessMetricDataPageBo queryRealTimeMetricDatas(Long mainInstanceId) {
		//获取子资源
		List<Long> instanceIdsList = new ArrayList<Long>();
		
		List<ProcessMetricDataBo> resultDataList = new ArrayList<ProcessMetricDataBo>();
		
		ProcessMetricDataPageBo pageBo = new ProcessMetricDataPageBo();
		
		List<String> metricPerformanceSet = new ArrayList<String>();
		List<String> metricInformationSet = new ArrayList<String>();
		List<String> metricAvailabilitySet = new ArrayList<String>();
		ResourceMetricDef[] metricDefs = null;
		List<ResourceInstance> childInstanceList = null;
		List<ResourceInstance> childProcessInstanceList = new ArrayList<ResourceInstance>();
		try {
			childInstanceList = resourceInstanceService.getChildInstanceByParentId(mainInstanceId);
			
			if(childInstanceList == null || childInstanceList.size() <= 0){
				if(logger.isDebugEnabled()){
					logger.debug("ChildInstanceList is null , mainInstanceId is " + mainInstanceId);
				}
				pageBo.setProcessData(resultDataList);
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
				if(def.getType().equals(ResourceTypeConsts.TYPE_PROCESS)){
					resultDataList.add(new ProcessMetricDataBo());
					childProcessInstanceList.add(instance);
					String[] modulePropList = instance.getModulePropBykey(ProcessColumnConsts.PROPERTY_PROCESSREMARK);
					String[] modulePropPid = instance.getModulePropBykey(ProcessColumnConsts.PROCESS_PID);
					//进程PID+进程名
					String[] modulePropIdName = instance.getModulePropBykey(ProcessColumnConsts.PROPERTY_INSTANCEIDKEYVALUES);
					//进程路径
					//String[] modulePropPath = instance.getModulePropBykey(ProcessColumnConsts.PROCESS_CMD);
					
					if (null!=modulePropIdName) {
						for (String moduleIdName : modulePropIdName) {
							if(moduleIdName.indexOf("processId")!= -1){
								if(null!=modulePropPid){
									for (String modulePid : modulePropPid) {
										resultDataList.get(index).setPid(modulePid);
									}
								}
							}else{
								resultDataList.get(index).setPid("--");
							}
						}	
					}
					
					/*if(null!=modulePropPath){
						logger.error("modulePropPath = "+modulePropPath);
						for(String moduleIdPath : modulePropPath){
							resultDataList.get(index).setProcessPath(moduleIdPath);
						}
					}*/
					
					if(null!=modulePropList){
						for(String moduleProp : modulePropList){
							resultDataList.get(index).setProcessRemark(moduleProp);
							index ++;
						}
					}

					
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
				logger.debug("Child process instanceList is null , mainInstanceId is " + mainInstanceId);
			}
			pageBo.setProcessData(resultDataList);
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
		
		MetricRealtimeDataQuery metricRealtimeDataQuery = new MetricRealtimeDataQuery();
		metricRealtimeDataQuery.setInstanceID(instancesIds);
		metricRealtimeDataQuery.setMetricID(metricIds);
		
		//获取性能指标信息
		Page<Map<String,?>,MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(metricRealtimeDataQuery, 0, instancesIds.length);
		List<Map<String,?>> realTimeDataList = page.getDatas();
		
		//正常子资源
		List<ProcessMetricDataBo> resultNormalDataList = new ArrayList<ProcessMetricDataBo>();
		//致命子资源
		List<ProcessMetricDataBo> resultCriticalDataList = new ArrayList<ProcessMetricDataBo>();
		//未知子资源
		List<ProcessMetricDataBo> resultUnknownDataList = new ArrayList<ProcessMetricDataBo>();
		for(int i = 0 ; i < resultDataList.size() ; i++){
			
			resultDataList.get(i).setResourceId(instancesIds[i]);
			
			realDataFor : for(Map<String,?> map : realTimeDataList){
				
				if(map.get(REAL_TIME_DATA_INSTANCE_ID_NAME).toString().equals(String.valueOf(instancesIds[i]))){
					Set<String> iterator = map.keySet();
					for(String key : iterator){
						switch (key) {
						case ProcessColumnConsts.PROCESS_CPU:
							ResourceMetricDef def = capacityService.getResourceMetricDef(childProcessInstanceList.get(i).getResourceId(), key);
							if(def == null){
								if(logger.isErrorEnabled()){
									logger.error("CapacityService.getResourceMetricDef is null : resourceid = " + childProcessInstanceList.get(i).getResourceId() + ",metricid = " + key);
								}
								break;
							}
							if(map.get(key) == null){
								if(logger.isErrorEnabled()){
									logger.error("Map.get(key) error,key = " + key);
								}
								break;
							}
							resultDataList.get(i).setProcessCPUAvgRateValue(map.get(key).toString());
							resultDataList.get(i).setProcessCPUAvgRate(map.get(key).toString() + def.getUnit());
							break ;
							
						case ProcessColumnConsts.PROCESS_PHYSICALMEM:
							ResourceMetricDef defPhy = capacityService.getResourceMetricDef(childProcessInstanceList.get(i).getResourceId(), key);
							if(defPhy == null){
								if(logger.isErrorEnabled()){
									logger.error("CapacityService.getResourceMetricDef is null : resourceid = " + childProcessInstanceList.get(i).getResourceId() + ",metricid = " + key);
								}
								break;
							}
							if(map.get(key) == null){
								if(logger.isErrorEnabled()){
									logger.error("Map.get(key) error,key = " + key);
								}
								break;
							}
							resultDataList.get(i).setPhysicalMemoryValue(map.get(key).toString());
							resultDataList.get(i).setPhysicalMemory(UnitTransformUtil.transform(map.get(key).toString() , defPhy.getUnit()));
							break ;
							
						case ProcessColumnConsts.PROCESS_VIRTUALMEM:
							ResourceMetricDef defVir = capacityService.getResourceMetricDef(childProcessInstanceList.get(i).getResourceId(), key);
							if(defVir == null){
								if(logger.isErrorEnabled()){
									logger.error("CapacityService.getResourceMetricDef is null : resourceid = " + childProcessInstanceList.get(i).getResourceId() + ",metricid = " + key);
								}
								break;
							}
							if(map.get(key) == null){
								if(logger.isErrorEnabled()){
									logger.error("Map.get(key) error,key = " + key);
								}
								break;
							}
							resultDataList.get(i).setVisualMemoryValue(map.get(key).toString());
							resultDataList.get(i).setVisualMemory(UnitTransformUtil.transform(map.get(key).toString() , defVir.getUnit()));
							break ;
							
						default:
							break;
						}
					}
				}
				
			}
			
			//获取信息指标信息
			for(int j = 0 ; j < metricInformationSet.size() ; j ++){		
				MetricData metricInfoData = metricDataService.getMetricInfoData(instancesIds[i], metricInformationSet.get(j));
				ResourceMetricDef def = capacityService.getResourceMetricDef(childProcessInstanceList.get(i).getResourceId(), metricInformationSet.get(j));
				
				//进程名称从资源实例获取
				try {
					ResourceInstance intance = resourceInstanceService.getResourceInstance(instancesIds[i]);
					if(intance == null){
						if(logger.isErrorEnabled()){
							logger.error("Get intance error,id : " + instancesIds[i]);
						}
						continue;
					}
					resultDataList.get(i).setProcessCommand(intance.getShowName());
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
				//进程路径
				case ProcessColumnConsts.PROCESS_CMD:
					resultDataList.get(i).setProcessPath(informationMetricData);
					break;
				
//				case ProcessColumnConsts.PROCESS_PID:
//					resultDataList.get(i).setPid(informationMetricData);
//					break;
				case ProcessColumnConsts.PROCESS_NAME:
					break;
//					改为性能指标
//				case ProcessColumnConsts.PROCESS_VIRTUALMEM:
//					resultDataList.get(i).setVisualMemoryValue(originalData);
//					resultDataList.get(i).setVisualMemory(informationMetricData);
//					break;
//				case ProcessColumnConsts.PROCESS_PHYSICALMEM:
//					resultDataList.get(i).setPhysicalMemoryValue(originalData);
//					resultDataList.get(i).setPhysicalMemory(informationMetricData);
//					break;
				case ProcessColumnConsts.PROCESS_STATE:
					break;
				case ProcessColumnConsts.PROCESS_STARTTIME:
					break;

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
//					resultDataList.get(i).setProcessAvail(MetricStateEnum.UNKOWN.toString());
					resultDataList.get(i).setProcessAvail("yes");
					resultUnknownDataList.add(resultDataList.get(i));
					continue;
				}
				switch (metricAvailabilitySet.get(j)) {
				case ProcessColumnConsts.PROCESS_AVAIL:
					
					if(avaliableData.getState() == MetricStateEnum.NORMAL || avaliableData.getState() == MetricStateEnum.NORMAL_NOTHING ||
					   avaliableData.getState() == MetricStateEnum.NORMAL_UNKNOWN || avaliableData.getState() == MetricStateEnum.SERIOUS ||
					   avaliableData.getState() == MetricStateEnum.WARN){
						resultDataList.get(i).setProcessAvail("yes");
						resultNormalDataList.add(resultDataList.get(i));
					}else if(avaliableData.getState() == MetricStateEnum.CRITICAL || avaliableData.getState() == MetricStateEnum.CRITICAL_NOTHING){
						resultDataList.get(i).setProcessAvail("no");
						resultCriticalDataList.add(resultDataList.get(i));
					}else{
//						resultDataList.get(i).setProcessAvail(MetricStateEnum.UNKOWN.toString());
						resultDataList.get(i).setProcessAvail("yes");
						resultUnknownDataList.add(resultDataList.get(i));
					}
					break;
				default:
					break;
				}
			}
		}
		
		resultDataList = new ArrayList<ProcessMetricDataBo>();
		
		resultDataList.addAll(resultNormalDataList);
		resultDataList.addAll(resultCriticalDataList);
		resultDataList.addAll(resultUnknownDataList);
		
		pageBo.setProcessData(resultDataList);
		pageBo.setRowCount(instancesIds.length);
		pageBo.setStartRow(0);
		pageBo.setTotalRecord(instancesIds.length);
		return pageBo;
	}
	
	public ProcessMetricDataPageBo scanCurrentProcessData(long mainInstanceId) {
		//获取子资源
		MetricData metricData = null;
		try {
			metricData = metricDataService.catchRealtimeMetricData(mainInstanceId, MetricIdConsts.FULL_PROCESS);
			
		} catch (MetricExecutorException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e){
			logger.error(e.getMessage(),e);
		}
		
		if(metricData == null || metricData.getData() == null){
			if(logger.isErrorEnabled()){
				logger.error("Scan process error");
			}
			ProcessMetricDataPageBo pageBo = new ProcessMetricDataPageBo();
			pageBo.setProcessData(new ArrayList<ProcessMetricDataBo>());
			pageBo.setRowCount(0);
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(0);
			return pageBo;
		}
		
		String[] dataString = metricData.getData();
		JSONArray jsonPstr = new JSONArray();
		
		for (String processData : dataString) {
			jsonPstr = JSON.parseArray(processData);
		}
		
		JSONArray jsonFirst = (JSONArray)jsonPstr.get(0);
		
		List<ProcessMetricDataBo> dataList = new ArrayList<ProcessMetricDataBo>();
		
		if(dataString == null || dataString.length <= 0){
			if(logger.isErrorEnabled()){
				logger.error("Scan process error");
			}
			ProcessMetricDataPageBo pageBo = new ProcessMetricDataPageBo();
			pageBo.setProcessData(dataList);
			pageBo.setRowCount(dataList.size());
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(dataList.size());
			return pageBo;
		}
		
		for(int i = 1 ; i < jsonPstr.size() ; i ++){
			ProcessMetricDataBo dataBo = new ProcessMetricDataBo();
			dataList.add(dataBo);
		}
		
		//
		for (int i = 0; i < jsonFirst.size(); i++) {
			switch ((String)jsonFirst.get(i)) {
				case ProcessColumnConsts.PROCESS_PID:
					//pid
					for(int j = 1 ; j < jsonPstr.size() ; j ++){
						JSONArray arrL = (JSONArray)jsonPstr.get(j);
						dataList.get(j - 1).setPid((String)arrL.get(i));
					}
					
					break;
				case ProcessColumnConsts.PROCESS_USER:
					
					//用户
					
					break;
				case ProcessColumnConsts.PROCESS_NAME:
					//命令
					for(int j = 1 ; j < jsonPstr.size() ; j ++){
						JSONArray arrL = (JSONArray)jsonPstr.get(j);
						dataList.get(j - 1).setProcessCommand((String)arrL.get(i));
					}
					
					break;
				case ProcessColumnConsts.PROCESS_CMD:
					//命令行
					for(int j = 1 ; j < jsonPstr.size() ; j ++){
						JSONArray arrL = (JSONArray)jsonPstr.get(j);
						dataList.get(j - 1).setProcessPath((String)arrL.get(i));
					}
					
					break;
				case ProcessColumnConsts.PROCESS_PHYSICALMEM:
					//实际驻留内存
					for(int j = 1 ; j < jsonPstr.size() ; j ++){
						JSONArray arrL = (JSONArray)jsonPstr.get(j);
						dataList.get(j - 1).setPhysicalMemoryValue((String)arrL.get(i));
						String transform = UnitTransformUtil.transform((String)arrL.get(i), "KB");
						dataList.get(j - 1).setPhysicalMemory(transform);
					}
					
					break;
				case ProcessColumnConsts.PROCESS_VIRTUALMEM:
					//虚拟内存
					for(int j = 1 ; j < jsonPstr.size() ; j ++){
						JSONArray arrL = (JSONArray)jsonPstr.get(j);
						dataList.get(j - 1).setVisualMemoryValue((String)arrL.get(i));
						String transform = UnitTransformUtil.transform((String)arrL.get(i), "KB");
						dataList.get(j - 1).setVisualMemory(transform);
					}
					
					break;
					case ProcessColumnConsts.PROCESS_MEM:
					
					//内存利用率
					
					break;
					case ProcessColumnConsts.PROCESS_STATE:
					
					//状态
					
					break;
				case ProcessColumnConsts.PROCESS_STARTTIME:
					
					//启动时间
					break;
				case ProcessColumnConsts.PROCESS_CPU:
					//cpu利用率
					for(int j = 1 ; j < jsonPstr.size() ; j ++){
						JSONArray arrL = (JSONArray)jsonPstr.get(j);
						dataList.get(j - 1).setProcessCPUAvgRateValue((String)arrL.get(i));
						dataList.get(j - 1).setProcessCPUAvgRate((String)arrL.get(i));
					}
					
					break;
			}
		}
		
		String splitString = ",";
		
//		for(int i = 1 ; i < dataString.length ; i ++){
//			ProcessMetricDataBo dataBo = new ProcessMetricDataBo();
//			dataList.add(dataBo);
//		}
//		
//		String[] dataTitles = dataString[0].split(splitString);
//		
//		for(int i = 0 ; i < dataTitles.length ; i ++){	
//			switch (dataTitles[i]) {
//			case ProcessColumnConsts.PROCESS_PID:
//				
//				//pid
//				for(int j = 1 ; j < dataString.length ; j ++){
//					dataList.get(j - 1).setPid(dataString[j].split(splitString)[i]);
//				}
//				
//				break;
//			case ProcessColumnConsts.PROCESS_USER:
//				
//				//用户
//				
//				break;
//			case ProcessColumnConsts.PROCESS_PHYSICALMEM:
//				
//				//实际驻留内存
//				for(int j = 1 ; j < dataString.length ; j ++){
//					dataList.get(j - 1).setPhysicalMemoryValue(dataString[j].split(splitString)[i]);
//					String value = UnitTransformUtil.transform(dataString[j].split(splitString)[i], "KB");
//					dataList.get(j - 1).setPhysicalMemory(value);
//				}
//				
//				break;
//			case ProcessColumnConsts.PROCESS_VIRTUALMEM:
//				
//				//虚拟内存
//				for(int j = 1 ; j < dataString.length ; j ++){
//					dataList.get(j - 1).setVisualMemoryValue(dataString[j].split(splitString)[i]);
//					String value = UnitTransformUtil.transform(dataString[j].split(splitString)[i], "KB");
//					dataList.get(j - 1).setVisualMemory(value);
//				}
//				
//				break;
//			case ProcessColumnConsts.PROCESS_CPU:
//				
//				//cpu利用率
//				for(int j = 1 ; j < dataString.length ; j ++){
//					dataList.get(j - 1).setProcessCPUAvgRateValue(dataString[j].split(splitString)[i]);
//					dataList.get(j - 1).setProcessCPUAvgRate(dataString[j].split(splitString)[i]);
//				}
//				
//				break;
//			case ProcessColumnConsts.PROCESS_MEM:
//				
//				//内存利用率
//				
//				break;
//			case ProcessColumnConsts.PROCESS_STATE:
//				
//				//状态
//				
//				break;
//			case ProcessColumnConsts.PROCESS_STARTTIME:
//				
//				//启动时间
//				break;
//			case ProcessColumnConsts.PROCESS_NAME:
//				
//				//命令
//				for(int j = 1 ; j < dataString.length ; j ++){
//					dataList.get(j - 1).setProcessCommand(dataString[j].split(splitString)[i]);
//				}
//				
//				break;
//			
//			case ProcessColumnConsts.PROCESS_CMD:
//				
//				//命令行
//				for(int j = 1 ; j < dataString.length ; j ++){
//					dataList.get(j - 1).setProcessPath(dataString[j].split(",")[i]);
//				}
//				
//				break;
//
//			default:
//				break;
//			}
//			
//		}
		
		ProcessMetricDataPageBo pageBo = new ProcessMetricDataPageBo();
		pageBo.setProcessData(dataList);
		pageBo.setRowCount(dataList.size());
		pageBo.setStartRow(0);
		pageBo.setTotalRecord(dataList.size());
		return pageBo;
	}
	
	/**
	 * 僵死进程
	 * @param mainInstanceId
	 * @return ProcessMetricDataPageBo
	 */
	public ProcessMetricDataPageBo diedProcessData(long mainInstanceId){
		//获取子资源
		MetricData metricData = null;
		try {
			metricData = metricDataService.catchRealtimeMetricData(mainInstanceId, "fulldiedProcess");
		} catch (MetricExecutorException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e){
			logger.error(e.getMessage(),e);
		}
		
		if(metricData == null || metricData.getData() == null){
			if(logger.isErrorEnabled()){
				logger.error("no diedProcess");
			}
			ProcessMetricDataPageBo pageBo = new ProcessMetricDataPageBo();
			pageBo.setProcessData(new ArrayList<ProcessMetricDataBo>());
			pageBo.setRowCount(0);
			pageBo.setStartRow(0);
			pageBo.setTotalRecord(0);
			return pageBo;
		}
		
		String[] dataString = metricData.getData();
		List<ProcessMetricDataBo> dataList = new ArrayList<ProcessMetricDataBo>();
		
		for(int i = 1 ; i < dataString.length ; i ++){
			ProcessMetricDataBo dataBo = new ProcessMetricDataBo();
			dataList.add(dataBo);
		}
		
		String[] dataTitles = dataString[0].split(",");
		
		for(int i = 0 ; i < dataTitles.length ; i ++){
			
			switch (dataTitles[i]) {
			case ProcessColumnConsts.PROCESS_PID:
				
				//pid
				for(int j = 1 ; j < dataString.length ; j ++){
					dataList.get(j - 1).setPid(dataString[j].split(",")[i]);
				}
				
				break;
			case ProcessColumnConsts.PROCESS_USER:
				
				//用户
				
				break;
			case ProcessColumnConsts.PROCESS_PHYSICALMEM:
				
				//固定内存
				for(int j = 1 ; j < dataString.length ; j ++){
					dataList.get(j - 1).setPhysicalMemoryValue(dataString[j].split(",")[i]);
					String value = UnitTransformUtil.transform(dataString[j].split(",")[i], "KB");
					dataList.get(j - 1).setPhysicalMemory(value);
				}
				
				break;
			case ProcessColumnConsts.PROCESS_VIRTUALMEM:
				
				//虚拟内存
				for(int j = 1 ; j < dataString.length ; j ++){
					dataList.get(j - 1).setVisualMemoryValue(dataString[j].split(",")[i]);
					String value = UnitTransformUtil.transform(dataString[j].split(",")[i], "KB");
					dataList.get(j - 1).setVisualMemory(value);
				}
				
				break;
			case ProcessColumnConsts.PROCESS_CPU:
				
				//cpu利用率
				for(int j = 1 ; j < dataString.length ; j ++){
					dataList.get(j - 1).setProcessCPUAvgRateValue(dataString[j].split(",")[i]);
					dataList.get(j - 1).setProcessCPUAvgRate(dataString[j].split(",")[i]);
				}
				
				break;
			case ProcessColumnConsts.PROCESS_MEM:
				
				//内存利用率
				
				break;
			case ProcessColumnConsts.PROCESS_STATE:
				
				//状态
				
				break;
			case ProcessColumnConsts.PROCESS_STARTTIME:
				
				//启动时间
				break;
			case ProcessColumnConsts.PROCESS_NAME:
				
				//命令
				for(int j = 1 ; j < dataString.length ; j ++){
					dataList.get(j - 1).setProcessCommand(dataString[j].split(",")[i]);
				}
				
				break;

			default:
				break;
			}
		}
		
		ProcessMetricDataPageBo pageBo = new ProcessMetricDataPageBo();
		pageBo.setProcessData(dataList);
		pageBo.setRowCount(dataList.size());
		pageBo.setStartRow(0);
		pageBo.setTotalRecord(dataList.size());
		return pageBo;
	}

	@Override
	public AddInstanceResult addProcessMonitor(List<ProcessMetricDataBo> processList,long mainInstanceId,int type,ILoginUser user) {
		
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
		String childResourceProcessId = def.getChildResourceIdByType(ResourceTypeConsts.TYPE_PROCESS);
		
		List<Long> instances = new ArrayList<Long>();
		
		for(int i = 0 ; i < processList.size() ; i++){
			ProcessMetricDataBo processMetricDataBo = processList.get(i);
			
			String processValue = "";
			if(type == 0){
				//进程名
				processValue = ProcessColumnConsts.PROPERTY_PROCESSCOMMAND + "=" + processMetricDataBo.getProcessCommand();
			}else{
				//PID + 进程名
				processValue = ProcessColumnConsts.PROPERTY_PROCESSID + "=" + processMetricDataBo.getPid() + ";" + ProcessColumnConsts.PROPERTY_PROCESSCOMMAND + "=" + processMetricDataBo.getProcessCommand();
			}
			
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
				modulePropInstanceIdKeyValues.setKey(ProcessColumnConsts.PROPERTY_INSTANCEIDKEYVALUES);
				
				String[] processValueList = new String[1];
				processValueList[0] = processValue;
				modulePropInstanceIdKeyValues.setValues(processValueList);
				moduleProps.add(modulePropInstanceIdKeyValues);
				
				ModuleProp modulePropIdentity = new ModuleProp();
				modulePropIdentity.setKey("INST_IDENTY_KEY");
				String[] identityList = new String[1];
				identityList[0] = SecureUtil.md5Encryp(processValue);
				modulePropIdentity.setValues(identityList);
				moduleProps.add(modulePropIdentity);
				
				ModuleProp moduleProcessId = new ModuleProp();
				moduleProcessId.setKey(ProcessColumnConsts.PROPERTY_PROCESSID);
				String[] processIdValueList = new String[1];
				processIdValueList[0] = processMetricDataBo.getPid();
				moduleProcessId.setValues(processIdValueList);
				moduleProps.add(moduleProcessId);
				
				ModuleProp moduleProcessName = new ModuleProp();
				moduleProcessName.setKey(ProcessColumnConsts.PROPERTY_PROCESSREMARK);
				String[] processNameValueList = new String[1];
				processNameValueList[0] = processMetricDataBo.getProcessRemark();
				moduleProcessName.setValues(processNameValueList);
				moduleProps.add(moduleProcessName);
				
				ModuleProp moduleProcessCommand = new ModuleProp();
				moduleProcessCommand.setKey(ProcessColumnConsts.PROPERTY_PROCESSCOMMAND);
				String[] processCommandValueList = new String[1];
				processCommandValueList[0] = processMetricDataBo.getProcessCommand();
				moduleProcessCommand.setValues(processCommandValueList);
				moduleProps.add(moduleProcessCommand);
				
				//路径
				ModuleProp moduleProcessPath = new ModuleProp();
				moduleProcessPath.setKey(ProcessColumnConsts.PROCESS_CMD);
				String[] processPathValueList = new String[1];
				processPathValueList[0] = processMetricDataBo.getProcessPath();
				moduleProcessPath.setValues(processPathValueList);
				moduleProps.add(moduleProcessPath);
				
				childInstance.setName(processMetricDataBo.getProcessCommand());
				childInstance.setShowName(processMetricDataBo.getProcessCommand());
				childInstance.setResourceId(childResourceProcessId);
				childInstance.setModuleProps(moduleProps);
				childInstance.setChildType(ResourceTypeConsts.TYPE_PROCESS);
				childInstance.setDiscoverNode(mainInstance.getDiscoverNode());
				
				ResourceInstanceResult resourceInstanceResult = resourceInstanceService.addResourceInstance(childInstance);
				
				if(resourceInstanceResult.isRepeat()){
					//已经有了该进程
					
					//判断资源是否监控
					ResourceInstance addInstance = resourceInstanceService.getResourceInstance(resourceInstanceResult.getResourceInstanceId());
					if(addInstance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED){
						if(logger.isInfoEnabled()){
							logger.info("Add resource instance id = " + resourceInstanceResult.getResourceInstanceId());
						}
						instances.add(resourceInstanceResult.getResourceInstanceId());
					}
					
					if(addInstance.getLifeState() == InstanceLifeStateEnum.MONITORED){
						//重复进程
						repeatInstanceList.add(processMetricDataBo.getProcessCommand());
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
				failInstanceList.add(processMetricDataBo.getProcessCommand());
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
		String childResourceProcessId = def.getChildResourceIdByType(ResourceTypeConsts.TYPE_PROCESS);
		
		childInfo.setProfileDesc("自动添加的进程子策略");
		childInfo.setProfileName("进程");
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

	/**
	 * 更新进程子资源的备注
	 */
	@Override
	public boolean updateProcessInstanceRemark(long mainInstanceId,String newRemark) {
		ModuleProp prop = new ModuleProp();
		prop.setInstanceId(mainInstanceId);
		prop.setKey(ProcessColumnConsts.PROPERTY_PROCESSREMARK);
		String[] list = new String[1];
		list[0] = newRemark;
		prop.setValues(list);
		
		try {
			modulePropService.updateProp(prop);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean deleteProcessInstance(String processInstanceIds) {
		
		List<Long> instanceIdList = new ArrayList<Long>();
		
		for (String id : processInstanceIds.split(",")) {
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

	@Override
	public ProcessDeadLockDataPageBo deadLockInfoData(long mainInstanceId) {
		//获取子资源
		MetricData metricData = null;
		try {
			metricData = metricDataService.catchRealtimeMetricData(mainInstanceId, "deadlockTable");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		if(metricData == null || metricData.getData() == null || metricData.getData().length == 1){
			if(logger.isErrorEnabled()){
				logger.error("no deadLockProcess");
			}
			ProcessDeadLockDataPageBo pago = new ProcessDeadLockDataPageBo();
			pago.setProcessData(new ArrayList<ProcessDeadLockDataBo>());
			pago.setRowCount(0);
			pago.setStartRow(0);
			pago.setTotalRecord(0);
			return pago;
		}
		
		String[] dataString = metricData.getData();
		List<ProcessDeadLockDataBo> dataList = new ArrayList<ProcessDeadLockDataBo>();
        List<Map<String, String>> maps = null;
		if(dataString != null && dataString.length > 0){
            maps = MetricDataUtil.parseTableResultSet(dataString);
        }
        //for (int i = 1; i < maps.size(); i++) {
		//	ProcessDeadLockDataBo dataBo = new ProcessDeadLockDataBo();
		//	dataList.add(dataBo);
		//}
        //String[] dataTitles = dataString[0].split(",");
        ProcessDeadLockDataBo dataBo = null;
		for (int i = 0;maps != null && i < maps.size(); i++) {
            dataBo = new ProcessDeadLockDataBo();
            dataList.add(dataBo);
            Map<String, String> stringStringMap = maps.get(i);
            Set<String> strings = stringStringMap.keySet();
            for(String key : strings){
                switch (key.toUpperCase()) {
                    case "SID":
                        //进程ID
                            dataBo.setSid(stringStringMap.get(key));
                        break;
                    case "USERNAME":
                        //数据库用户
                        dataBo.setDataBaseUserName(stringStringMap.get(key));
                        break;
                    case "LOCKWAIT":
                        //死锁状态
                        dataBo.setDataBaseLockWait(stringStringMap.get(key));
                        break;
                    case "STATUS":
                        //状态
                        dataBo.setDataBaseStatus(stringStringMap.get(key));
                        break;
                    case "MACHINE":
                        //死锁语句所在的机器
                        dataBo.setDataBaseMachine(stringStringMap.get(key));
                        break;
                    case "PROGRAM":
                        //死锁语句所在的应用程序
                        dataBo.setDataBaseProgram(stringStringMap.get(key));
                        break;

                    default:
                        break;
                }
            }

		}
		
		ProcessDeadLockDataPageBo pago = new ProcessDeadLockDataPageBo();
		pago.setProcessData(dataList);
		pago.setRowCount(dataList.size());
		pago.setStartRow(0);
		pago.setTotalRecord(dataList.size());
		
		return pago;
	}
	
}
