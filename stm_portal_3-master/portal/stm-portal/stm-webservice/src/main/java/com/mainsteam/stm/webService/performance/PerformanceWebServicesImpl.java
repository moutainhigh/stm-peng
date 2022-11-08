package com.mainsteam.stm.webService.performance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.mainsteam.stm.common.metric.report.MetricDataReportService;
import com.mainsteam.stm.common.metric.report.MetricDataTopQuery;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportData;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.obj.TimePeriod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.state.obj.MetricStateData;

@WebService
public class PerformanceWebServicesImpl implements PerformanceWebServices{
	
	private static final Logger logger=LoggerFactory.getLogger(PerformanceWebServicesImpl.class);
	
	private static final String RESULT_NORMAL_CODE = "0000";
	
	private static final String RESULT_RESOURCE_TYPE_NULL_CODE = "2010";
	
	private static final String RESULT_RESOURCE_TYPE_NULL_MSG = "必须指定资源类型";
	
	private static final String RESULT_RESOURCE_TYPE_NOT_EXSIT_CODE = "2011";
	
	private static final String RESULT_RESOURCE_TYPE_NOT_EXSIT_MSG = "指定的资源类型不存在";
	
	private static final String RESULT_RESOURCE_ID_NULL_CODE = "2012";
	
	private static final String RESULT_RESOURCE_ID_NULL_MSG = "必须指定资源ID";
	
	private static final String RESULT_METRIC_ID_NULL_CODE = "2013";
	
	private static final String RESULT_METRIC_ID_NULL_MSG = "必须指定指标ID";
	
	private static final String RESULT_RESOURCE_NOT_EXSIT_CODE = "2014";
	
	private static final String RESULT_RESOURCE_NOT_EXSIT_MSG = "指定的资源不存在";
	
	private static final String RESULT_METRIC_NOT_EXSIT_CODE = "2015";
	
	private static final String RESULT_METRIC_NOT_EXSIT_MSG = "指定的指标不存在";
	
	private static final String RESULT_STRAT_TIME_NULL_CODE = "2016";
	
	private static final String RESULT_STRAT_TIME_NULL_MSG = "必须指定开始时间";
	
	private static final String RESULT_END_TIME_NULL_CODE = "2017";
	
	private static final String RESULT_END_TIME_NULL_MSG = "必须指定结束时间";
	
	private static final String RESULT_STRAT_TIME_NOT_VALIDATA_CODE = "2018";
	
	private static final String RESULT_STRAT_TIME_NOT_VALIDATA_MSG = "指定的开始时间格式不合法";
	
	private static final String RESULT_END_TIME_NOT_VALIDATA_CODE = "2019";
	
	private static final String RESULT_END_TIME_NOT_VALIDATA_MSG = "指定的结束时间格式不合法";
	
	private static final String RESULT_BEGIN_TIME_BEYOND_END_TIME_CODE = "2020";
	
	private static final String RESULT_BEGIN_TIME_BEYOND_END_TIME_MSG = "结束时间必须在开始时间之后";

	private static final String SUCCESS_CODE = "200";
	private static final String SUCCESS_MSG = "extracts performance data successfully.";
	private static final String FAILED_NULL_CATEGORY = "201";
	private static final String FAILED_NULL_CATEGORY_MSG = "must have resource category.";
	private static final String FAILED_WRONG_CATEGORY = "207";
	private static final String FAILED_WRONG_CATEGORY_MSG = "category must be 'Oracle' or 'WebLogic'.";
	private static final String FAILED_NULL_METRIC = "208";
	private static final String FAILED_NULL_METRIC_MSG = "must have metric id.";
	private static final String FAILED_WRONG_METRIC = "209";
	private static final String FAILD_WRONG_METRIC_MSG = "invalid metric id, must be e.g.,Jvm_mem_rate,throughput,session_per_second,block_lock_count.";
	private static final String ORACLE_NOT_MATCHED = "210";
	private static final String ORACLE_NOT_MATCHED_MSG = "category 'Oracle' must have these 'block_lock_count' or 'session_per_second' metrics.";
	private static final String WEBLOGIC_NOT_MATCHED = "211";
	private static final String WEBLOG_MOT_MATCHED_MSG = "category 'WebLogic' must have these 'Jvm_mem_rate' & 'throughput' metrics.";
	private static final String FAILED_NULL_LIMITS = "202";
	private static final String FAILED_NULL_LIMITS_MSG = "must have top N limits.";
	private static final String INVALID_LIMITS = "203";
	private static final String INVALID_LIMITS_MSG = "limits must be a valid number.";
	private static final String FAILED_NULL_STARTTIME_MSG = "must have start time.";
	private static final String FAILED_NULL_STARTTIME = "204";
	private static final String INVALID_DATE_SYNTAX = "215";
	private static final String INVALID_DATE_SYNTAX_MSG = "must have a valid date format,e.g.,2017-01-01 12:00:00.";
	private static final String INVALID_DATE_START_END = "205";
	private static final String INVALID_DATE_START_END_MSG = "end time must be after than starts time.";
	private static final String FAILED_FIND_RESOURCE = "212";
	private static final String SERVER_ERROR = "206";
	private static final String SERVER_ERROR_MSG = "server error.";
	private static final String FAILED_NO_DATAS = "213";
	private static final String FAILED_NO_DATAS_MSG = "operating successfully, but no summary datas.";
	private static final int MUSTOVERZERO = 0;
	private static final String FAILED_GREATER_THAN_ZERO = "214";
	private static final String GREATER_THAN_ZERO_MSG = "limits must be greater than 0.";
	private static final String FAILED_INSTANCE_MONITORED = "216";
	private static final String FAILED_INSTANCE_MONITORED_MSG = "resource instances are not monitored.";

	@Resource
	private CapacityService capacityService;
	
	@Resource
	private MetricDataService metricDataService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private MetricStateService metricStateService;
	
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;

	@Resource(name="MetricDataReportService")
	private MetricDataReportService metricDataReportService;
	
	@Override
	public String getMetricList(String resourceTypeId) {
		
		//结果对象
		Result result = new Result();
		
		if(resourceTypeId == null || resourceTypeId.equals("")){
			result.setErrorMsg(RESULT_RESOURCE_TYPE_NULL_MSG);
			result.setResultCode(RESULT_RESOURCE_TYPE_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		ResourceDef def = capacityService.getResourceDefById(resourceTypeId);
		
		if(def == null){
			result.setErrorMsg(RESULT_RESOURCE_TYPE_NOT_EXSIT_MSG);
			result.setResultCode(RESULT_RESOURCE_TYPE_NOT_EXSIT_CODE);
			return JSONObject.toJSONString(result);
		}
		
		ResourceMetricDef[] metricDefs = def.getMetricDefs();
		
		if(metricDefs == null){
			result.setErrorMsg(RESULT_RESOURCE_TYPE_NOT_EXSIT_MSG);
			result.setResultCode(RESULT_RESOURCE_TYPE_NOT_EXSIT_CODE);
			return JSONObject.toJSONString(result);
		}
		
		Metric[] metrics = new Metric[metricDefs.length];
		for(int i = 0 ; i < metricDefs.length ; i ++){
			
			if(!(metricDefs[i].getMetricType().equals(MetricTypeEnum.PerformanceMetric))){
				//只需要性能指标
				continue;
			}
			
			Metric metric = new Metric();
			metric.setMetricId(metricDefs[i].getId());
			metric.setMetricName(metricDefs[i].getName());
			metric.setUnit(metricDefs[i].getUnit());
			metric.setMetricType(metricDefs[i].getMetricType().name());
			metrics[i] = metric;
		}
		
		result.setData(metrics);
		result.setResultCode(RESULT_NORMAL_CODE);
		
		return JSONObject.toJSONString(result);
	}
	
	@Override
	public String getAllMetricListByResourceId(String resourceId) {
		
		//结果对象
		Result result = new Result();
		
		if(resourceId == null || resourceId.equals("")){
			result.setErrorMsg(RESULT_RESOURCE_TYPE_NULL_MSG);
			result.setResultCode(RESULT_RESOURCE_TYPE_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		ResourceDef def = capacityService.getResourceDefById(resourceId);
		
		if(def == null){
			result.setErrorMsg(RESULT_RESOURCE_TYPE_NOT_EXSIT_MSG);
			result.setResultCode(RESULT_RESOURCE_TYPE_NOT_EXSIT_CODE);
			return JSONObject.toJSONString(result);
		}
		
		ResourceMetricDef[] metricDefs = def.getMetricDefs();
		
		if(metricDefs == null){
			result.setErrorMsg(RESULT_RESOURCE_TYPE_NOT_EXSIT_MSG);
			result.setResultCode(RESULT_RESOURCE_TYPE_NOT_EXSIT_CODE);
			return JSONObject.toJSONString(result);
		}
		
		Metric[] metrics = new Metric[metricDefs.length];
		for(int i = 0 ; i < metricDefs.length ; i ++){
			
			Metric metric = new Metric();
			metric.setMetricId(metricDefs[i].getId());
			metric.setMetricName(metricDefs[i].getName());
			metric.setUnit(metricDefs[i].getUnit());
			metric.setMetricType(metricDefs[i].getMetricType().name());
			metrics[i] = metric;
		}
		
		result.setData(metrics);
		result.setResultCode(RESULT_NORMAL_CODE);
		
		return JSONObject.toJSONString(result);
	}

	@Override
	public String getCurrentPerformanceMetricData(String instanceId,String metricId) {
		
		//结果对象
		Result result = new Result();
		
		if(instanceId == null || instanceId.equals("")){
			result.setErrorMsg(RESULT_RESOURCE_ID_NULL_MSG);
			result.setResultCode(RESULT_RESOURCE_ID_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		if(metricId == null || metricId.equals("")){
			result.setErrorMsg(RESULT_METRIC_ID_NULL_MSG);
			result.setResultCode(RESULT_METRIC_ID_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		MetricValue metricValue = new MetricValue();
		
		long resourceInstanceId = Long.parseLong(instanceId);
		
		MetricRealtimeDataQuery metricRealtimeDataQuery = new MetricRealtimeDataQuery();
		metricRealtimeDataQuery.setInstanceID(new long[]{resourceInstanceId});
		metricRealtimeDataQuery.setMetricID(new String[]{metricId});
		
		//获取性能指标信息
		List<Map<String,?>> realTimeDataList = metricDataService.queryRealTimeMetricData(metricRealtimeDataQuery);
		
		List<Long> insList = new ArrayList<Long>();
		List<String> metricList = new ArrayList<String>();
		insList.add(Long.parseLong(instanceId));
		metricList.add(metricId);
		
		List<MetricStateData> msdList = metricStateService.findMetricState(insList, metricList);
		if(null!=msdList && msdList.size()>0){
			MetricStateData msd = msdList.get(0);
			if(null!=msd.getState()){
				metricValue.setMetricState(formatStatePer(msd.getState().name()));
			}else{
//				metricValue.setMetricState(MetricStateEnum.UNKOWN.name());
				metricValue.setMetricState(MetricStateEnum.NORMAL.name());
			}
		}
		
		
		for(Map<String,?> dataMap : realTimeDataList){
			Set<String> iterator = dataMap.keySet();
			for(String key : iterator){
				if(key.equals(metricId)){
					metricValue.setMetricId(metricId);
					if(null!=dataMap.get(key)){
						metricValue.setValue(dataMap.get(key).toString());
					}else{
						metricValue.setValue("");
					}
				}
				if(key.equals(metricId + "CollTime")){
					if(null!=dataMap.get(key)){
						metricValue.setCollectTime(dataMap.get(key).toString());
					}else{
						metricValue.setCollectTime(null);
					}
				}
			}
		}
		
		result.setData(metricValue);
		result.setResultCode(RESULT_NORMAL_CODE);
		
		return JSONObject.toJSONString(result);
	}
	
	@Override
	public String getCurrentAvailabilityMetricData(String instanceId,String metricId) {
		
		//结果对象
		Result result = new Result();
		
		if(instanceId == null || instanceId.equals("")){
			result.setErrorMsg(RESULT_RESOURCE_ID_NULL_MSG);
			result.setResultCode(RESULT_RESOURCE_ID_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		if(metricId == null || metricId.equals("")){
			result.setErrorMsg(RESULT_METRIC_ID_NULL_MSG);
			result.setResultCode(RESULT_METRIC_ID_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		MetricValue metricValue = new MetricValue();
		
		List<Long> insList = new ArrayList<Long>();
		List<String> metricList = new ArrayList<String>();
		insList.add(Long.parseLong(instanceId));
		metricList.add(metricId);
		
		List<MetricStateData> msdList = metricStateService.findMetricState(insList, metricList);
		if(null!=msdList && msdList.size()>0){
			MetricStateData msd = msdList.get(0);
			metricValue.setMetricId(msd.getMetricID());
			if(null!=msd.getState()){
				metricValue.setMetricState(formatStateAVA(msd.getState().name()));
				metricValue.setValue(msd.getState().name());
			}else{
//				metricValue.setMetricState(MetricStateEnum.UNKOWN.name());
//				metricValue.setValue(MetricStateEnum.UNKOWN.name());
				metricValue.setMetricState(MetricStateEnum.NORMAL.name());
				metricValue.setValue(MetricStateEnum.NORMAL.name());
			}
			metricValue.setCollectTime(String.valueOf(msd.getCollectTime().getTime()));
		}
		
		
		result.setData(metricValue);
		result.setResultCode(RESULT_NORMAL_CODE);
		
		return JSONObject.toJSONString(result);
	}
	
	@Override
	public String getCurrentInformationMetricData(String instanceId,String metricId) {
		
		//结果对象
		Result result = new Result();
		
		if(instanceId == null || instanceId.equals("")){
			result.setErrorMsg(RESULT_RESOURCE_ID_NULL_MSG);
			result.setResultCode(RESULT_RESOURCE_ID_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		if(metricId == null || metricId.equals("")){
			result.setErrorMsg(RESULT_METRIC_ID_NULL_MSG);
			result.setResultCode(RESULT_METRIC_ID_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		MetricValue metricValue = new MetricValue();
		Long instanceIdL = new Long(instanceId);
		long[] instanceIdArray = new long[]{instanceIdL.longValue()};
		String[] metricIds = new String[]{metricId};
		
		//获取信息指标    查询信息指标需要过滤
//		List<MetricData> infoList = metricDataService.getMetricInfoDatas(instanceIdArray, metricIds);
		List<MetricData> infoList = infoMetricQueryAdaptService.getMetricInfoDatas(instanceIdArray, metricIds);
		
		if(null!=infoList && infoList.size()>0){
			MetricData msd = infoList.get(0);
			metricValue.setMetricId(msd.getMetricId());
			if(null!=msd.getData()){
				metricValue.setValue(parseArrayToString(msd.getData()));
			}else{
				metricValue.setValue("");
			}
			metricValue.setCollectTime(String.valueOf(msd.getCollectTime().getTime()));
		}
		
		result.setData(metricValue);
		result.setResultCode(RESULT_NORMAL_CODE);
		
		return JSONObject.toJSONString(result);
	}

	
	@Override
	public String getHistoryPerformanceMetricData(String instanceId, String[] metricIds, String startTime,String endTime) {
		
		//结果对象
		Result result = new Result();
		
		if(instanceId == null || instanceId.equals("")){
			result.setErrorMsg(RESULT_RESOURCE_ID_NULL_MSG);
			result.setResultCode(RESULT_RESOURCE_ID_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		if(metricIds == null || metricIds.length <= 0){
			result.setErrorMsg(RESULT_METRIC_NOT_EXSIT_MSG);
			result.setResultCode(RESULT_METRIC_NOT_EXSIT_CODE);
			return JSONObject.toJSONString(result);
		}
		
		if(startTime == null || startTime.equals("")){
			result.setErrorMsg(RESULT_STRAT_TIME_NULL_MSG);
			result.setResultCode(RESULT_STRAT_TIME_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		if(endTime == null || endTime.equals("")){
			result.setErrorMsg(RESULT_END_TIME_NULL_MSG);
			result.setResultCode(RESULT_END_TIME_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
		
		long startTimeStamp = -1;
		
		try {
			startTimeStamp = Long.parseLong(startTime);
		} catch (Exception e) {
			result.setErrorMsg(RESULT_STRAT_TIME_NOT_VALIDATA_MSG);
			result.setResultCode(RESULT_STRAT_TIME_NOT_VALIDATA_CODE);
			logger.error(e.getMessage(),e);
			return JSONObject.toJSONString(result);
		}
		
		long endTimeStamp = -1;
		
		try {
			endTimeStamp = Long.parseLong(endTime);
		} catch (Exception e) {
			result.setErrorMsg(RESULT_END_TIME_NOT_VALIDATA_MSG);
			result.setResultCode(RESULT_END_TIME_NOT_VALIDATA_CODE);
			logger.error(e.getMessage(),e);
			return JSONObject.toJSONString(result);
		}
		
		Date startDate = null;
		
		try {
			startDate = new Date(startTimeStamp);
		} catch (Exception e) {
			result.setErrorMsg(RESULT_STRAT_TIME_NOT_VALIDATA_MSG);
			result.setResultCode(RESULT_STRAT_TIME_NOT_VALIDATA_CODE);
			logger.error(e.getMessage(),e);
			return JSONObject.toJSONString(result);
		}
		
		Date endDate = null;
		
		try {
			endDate = new Date(endTimeStamp);
		} catch (Exception e) {
			result.setErrorMsg(RESULT_END_TIME_NOT_VALIDATA_MSG);
			result.setResultCode(RESULT_END_TIME_NOT_VALIDATA_CODE);
			logger.error(e.getMessage(),e);
			return JSONObject.toJSONString(result);
		}
		
		ResourcePerformance performance = new ResourcePerformance();
		
		long resourceInstanceId = Long.parseLong(instanceId);
		
		ResourceInstance instance = null;
		try {
			instance = resourceInstanceService.getResourceInstance(resourceInstanceId);
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
		}
		
		if(instance == null){
			result.setErrorMsg(RESULT_RESOURCE_NOT_EXSIT_MSG);
			result.setResultCode(RESULT_RESOURCE_NOT_EXSIT_CODE);
			return JSONObject.toJSONString(result);
		}
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		List<MetricValue> metricValueList = new ArrayList<MetricValue>();
		
		for (String metricId : metricIds) {
			List<MetricData> metricDatas = metricDataService.queryHistoryMetricData(metricId, resourceInstanceId, startDate, endDate);
			for(MetricData data : metricDatas){
				MetricValue value = new MetricValue();
				value.setCollectTime(format.format(data.getCollectTime()));
				value.setMetricId(metricId);
				StringBuffer sb = new StringBuffer();
				for(String singleData : data.getData()){
					sb.append(",").append(singleData);
				}
				String sbValue = sb.toString();
				if(sbValue != null && !sbValue.equals("")){
					sbValue = sbValue.substring(1);
				}
				value.setValue(sbValue);
				
				metricValueList.add(value);
			}
		}
		
		MetricValue[] values = new MetricValue[metricValueList.size()];
		
		performance.setPerfId(System.currentTimeMillis() + "");
		performance.setSource(instance.getDiscoverNode());
		performance.setResourceId(instanceId);
		performance.setMetricValues(metricValueList.toArray(values));
		
		result.setData(performance);
		
		return JSONObject.toJSONString(result);
	}

	@Override
	public PerformanceDataBean getTopNPerformance(String category, String metricId, String limits, String startTime, String endTime) {
		PerformanceDataBean performanceDataBean = new PerformanceDataBean(SUCCESS_CODE, SUCCESS_MSG);

		if(StringUtils.isBlank(category)) {
			performanceDataBean.setErrorCode(FAILED_NULL_CATEGORY);
			performanceDataBean.setErrorMsg(FAILED_NULL_CATEGORY_MSG);
			return performanceDataBean;
		}else {
			if(!ResourceCategoryEnum.contains(category)){
				performanceDataBean.setErrorCode(FAILED_WRONG_CATEGORY);
				performanceDataBean.setErrorMsg(FAILED_WRONG_CATEGORY_MSG);
				return performanceDataBean;
			}
		}
		if(StringUtils.isBlank(metricId)) {
			performanceDataBean.setErrorCode(FAILED_NULL_METRIC);
			performanceDataBean.setErrorMsg(FAILED_NULL_METRIC_MSG);
			return performanceDataBean;
		}else {
			if(!MetricEnum.contains(metricId)) {
				performanceDataBean.setErrorCode(FAILED_WRONG_METRIC);
				performanceDataBean.setErrorMsg(FAILD_WRONG_METRIC_MSG);
				return performanceDataBean;
			}
		}
		MetricEnum metric = MetricEnum.valueOf(metricId.toUpperCase());
		ResourceCategoryEnum categoryEnum = ResourceCategoryEnum.valueOf(category.toUpperCase());
		if(ResourceCategoryEnum.ORACLE == categoryEnum) { //oracle类型对应指标
			if(MetricEnum.BLOCK_LOCK_COUNT != metric && MetricEnum.SESSION_PER_SECOND != metric) {
				performanceDataBean.setErrorCode(ORACLE_NOT_MATCHED);
				performanceDataBean.setErrorCode(ORACLE_NOT_MATCHED_MSG);
			}
		}else if(ResourceCategoryEnum.WEBLOGIC == categoryEnum){ //weblogic类型对应哪些指标
			if(MetricEnum.JVM_MEM_RATE != metric && MetricEnum.THROUGHPUT != metric) {
				performanceDataBean.setErrorCode(WEBLOGIC_NOT_MATCHED);
				performanceDataBean.setErrorCode(WEBLOG_MOT_MATCHED_MSG);
			}
		}
		Integer limit = -1;
		if(StringUtils.isBlank(limits)) {
			performanceDataBean.setErrorMsg(FAILED_NULL_LIMITS_MSG);
			performanceDataBean.setErrorCode(FAILED_NULL_LIMITS);
			return performanceDataBean;
		}
		try{
			limit = Integer.parseInt(limits);
		}catch (Exception e){
			performanceDataBean.setErrorCode(INVALID_LIMITS);
			performanceDataBean.setErrorMsg(INVALID_LIMITS_MSG);
			return performanceDataBean;
		}
		if(limit <= MUSTOVERZERO) {
			performanceDataBean.setErrorCode(FAILED_GREATER_THAN_ZERO);
			performanceDataBean.setErrorMsg(GREATER_THAN_ZERO_MSG);
			return performanceDataBean;
		}
		if(StringUtils.isBlank(startTime)) {
			performanceDataBean.setErrorMsg(FAILED_NULL_STARTTIME_MSG);
			performanceDataBean.setErrorCode(FAILED_NULL_STARTTIME);
			return performanceDataBean;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date starts = null;
		Date ends = null;
		try{
			starts = dateFormat.parse(startTime);
			if(StringUtils.isNotBlank(endTime)) {
				ends = dateFormat.parse(endTime);
			}else {
				ends = new Date();
			}
		}catch (Exception e) {
			performanceDataBean.setErrorCode(INVALID_DATE_SYNTAX);
			performanceDataBean.setErrorMsg(INVALID_DATE_SYNTAX_MSG);
			return performanceDataBean;
		}
		if(starts.after(ends)) {
			performanceDataBean.setErrorCode(INVALID_DATE_START_END);
			performanceDataBean.setErrorMsg(INVALID_DATE_START_END_MSG);
			return performanceDataBean;
		}
		try{
			MetricDataTopQuery metricDataTopQuery = new MetricDataTopQuery();
			metricDataTopQuery.setLimit(limit);
			metricDataTopQuery.setOrderByMax(true);
			List<TimePeriod> timePeriods = new ArrayList<>(1);
			TimePeriod timePeriod = new TimePeriod();
			timePeriod.setStartTime(starts);
			timePeriod.setEndTime(ends);
			timePeriods.add(timePeriod);
			metricDataTopQuery.setMetricID(metric.getName());
			metricDataTopQuery.setTimePeriods(timePeriods);
			List<ResourceInstance> resourceInstanceList = resourceInstanceService.getParentInstanceByCategoryId(categoryEnum.getName());
			if(null == resourceInstanceList || resourceInstanceList.isEmpty()) {
				performanceDataBean.setErrorCode(FAILED_FIND_RESOURCE);
				performanceDataBean.setErrorMsg("can' find resources using category '"+category+"'");
				return performanceDataBean;
			}else {
				List<Long> ids = new ArrayList<Long>(resourceInstanceList.size());
				Map<Long, String[]> instancParams = new HashMap<>(resourceInstanceList.size());
				for(ResourceInstance resourceInstance : resourceInstanceList) {
					if(resourceInstance.getLifeState() == InstanceLifeStateEnum.MONITORED){
						ids.add(resourceInstance.getId());
						instancParams.put(resourceInstance.getId(), new String[]{resourceInstance.getShowIP(),
								StringUtils.isBlank(resourceInstance.getShowName())? resourceInstance.getName() : resourceInstance.getShowName()});

					}
				}
				if(ids.isEmpty()) {
					performanceDataBean.setErrorCode(FAILED_INSTANCE_MONITORED);
					performanceDataBean.setErrorMsg(FAILED_INSTANCE_MONITORED_MSG);
					return performanceDataBean;
				}
				metricDataTopQuery.setInstanceIDes(ids);
				List<MetricSummeryReportData> metricSummeryReportDatas = metricDataReportService.findTopSummaryData(metricDataTopQuery);
				if(null != metricSummeryReportDatas && !metricSummeryReportDatas.isEmpty()) {
					List<PerformanceInfo> data = new ArrayList<>(metricSummeryReportDatas.size());
					for(MetricSummeryReportData metricSummeryReportData : metricSummeryReportDatas) {
						String[] params = instancParams.get(metricSummeryReportData.getInstanceID());
						PerformanceInfo performanceInfo = new PerformanceInfo(params[1], params[0],
								String.valueOf(metricSummeryReportData.getAvg()));
						data.add(performanceInfo);
					}
					performanceDataBean.setData(data);
				}else {
					performanceDataBean.setErrorCode(FAILED_NO_DATAS);
					performanceDataBean.setErrorMsg(FAILED_NO_DATAS_MSG);
				}
			}
		}catch (Exception e) {
			if(logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
			performanceDataBean.setErrorCode(SERVER_ERROR);
			performanceDataBean.setErrorMsg(SERVER_ERROR_MSG);
			return performanceDataBean;
		}
		return performanceDataBean;
	}


	private String formatStatePer(String stateName){
		String stateStr = "";
		switch (stateName) {
			case "CRITICAL":
			case "CRITICAL_NOTHING":
				stateStr= "CRITICAL";
				break;
			case "SERIOUS":
				stateStr= "SERIOUS";
				break;
			case "WARN":
				stateStr= "WARN";
				break;
			case "NORMAL":
			case "NORMAL_CRITICAL":
			case "NORMAL_UNKNOWN":
			case "NORMAL_NOTHING":
				stateStr= "NORMAL";
				break;
			default :
				stateStr= "UNKNOWN";
			    break;
		}
		return stateStr;
	}
	
	private String formatStateAVA(String stateName){
		String stateStr = "";
		  switch (stateName) {
			case "CRITICAL":
			case "CRITICAL_NOTHING":
				stateStr = "不可用";
				break;
			case "NORMAL":
			case "SERIOUS":
			case "WARN":
			case "NORMAL_CRITICAL":
			case "NORMAL_UNKNOWN":
			case "NORMAL_NOTHING":
				stateStr = "可用";
				break;
			default :
				stateStr = "未知";
			    break;
			}
		  return stateStr;
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
}
