/**
 * 
 */
package com.mainsteam.stm.home.workbench.metric.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.MetricSummaryService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.common.metric.query.MetricSummaryQuery;
import com.mainsteam.stm.home.workbench.metric.api.IMetricDataApi;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.util.UnitTransformUtil;

/**
 * 
 * <li>文件名称: MetricDataServiceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年4月1日
 * @author  tandl
 */
@Service("stm_home_workbench_MetricDataApi")
public class MetricDataServiceImpl implements IMetricDataApi {
	private static Logger logger = Logger.getLogger(MetricDataServiceImpl.class);
	
	@Resource
	private MetricDataService metricDataService ;
	
	@Resource
	private MetricSummaryService  metricSummaryService;
	
	@Resource
	private CustomMetricService customMetricService;
	
	@Resource
	private CapacityService capacityService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
	@Resource
	private TimelineService timelineService;
	@Resource
	private ProfileService profileService;
	@Resource
	private MetricStateService metricStateService;
	
	
	@Override
	public Map<String, Object> getSummaryMetricData(long instanceId, String[] metricId, Date dateStart, Date dateEnd,MetricSummaryType metricSummaryType) {
		Map<String, Object> output =new HashMap<>();
		for(String id:metricId){
			List<MetricSummaryData>	summaryDataList = getSummaryMetricDataByDay(metricSummaryType,instanceId, id,dateStart,dateEnd);
			List<Map<String,Object>> list = handleSummaryMetricData(summaryDataList,dateStart,dateEnd);
			output.put(id, list);
		}
		return output;
	}

	@Override
	public Map<String, Object> getMetricData(long instanceId, String[] metricId, Date dateStart, Date dateEnd) {
		Map<String, Object> output =new HashMap<>();
		for(String id:metricId){
			List<MetricData> metricDataList = getHistoryMetricDataByHour(instanceId, id,dateStart,dateEnd);
			List<Map<String,Object>> list = handleMetricData(metricDataList,dateStart,dateEnd);
			output.put(id, list);
		}
		return output;
	}
	
	
	@Override
	public List<Map<String, Object>> getMetricByType(Long instanceId, String metricType) {
		List<Map<String, Object>> metricList = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> metricMaps = new HashMap<String, Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 指标状态
		Map<String, MetricStateEnum> msdMaps = new HashMap<String, MetricStateEnum>();
		try {
			ResourceInstance instance = resourceInstanceService.getResourceInstance(instanceId);
			// 可用性指标List
			List<String> availabilityList = new ArrayList<String>();
			// 信息指标List
			List<String> InfoMetricList = new ArrayList<String>();
			// 性能指标List
			List<String> perfMetricList = new ArrayList<String>();
			List<String> perfMetricStateList = new ArrayList<String>();
			// 指标基本信息
			List<ProfileMetric> profileMetricList = profileService.getProfileMetricsByResourceInstanceId(instanceId);
			for (int i = 0; profileMetricList != null && i < profileMetricList.size(); i++) {
				ProfileMetric profileMetric = profileMetricList.get(i);
				Map<String, Object> metric = createMetric(instance,profileMetric);
				if (!metric.isEmpty()) {
					metric.put("isCustomMetric", false);
					if (MetricTypeEnum.PerformanceMetric.equals(metric.get("type"))
							&& MetricTypeEnum.PerformanceMetric.toString().equals(metricType)) {
						metricMaps.put(profileMetric.getMetricId(), metric);
						perfMetricList.add(profileMetric.getMetricId());
						perfMetricStateList.add(profileMetric.getMetricId());
					} else if (MetricTypeEnum.InformationMetric.equals(metric.get("type"))
							&& MetricTypeEnum.InformationMetric.toString().equals(metricType)) {
						metricMaps.put(profileMetric.getMetricId(), metric);
						InfoMetricList.add(profileMetric.getMetricId());
						// 初始化指标状态值
						msdMaps.put(profileMetric.getMetricId(),MetricStateEnum.NORMAL);
					} else if (MetricTypeEnum.AvailabilityMetric.equals(metric.get("type"))
							&& MetricTypeEnum.AvailabilityMetric.toString().equals(metricType)) {
						metricMaps.put(profileMetric.getMetricId(), metric);
						availabilityList.add(profileMetric.getMetricId());
					}
				}
			}
			// 自定义指标基本信息
			List<CustomMetric> customMetrics = customMetricService.getCustomMetricsByInstanceId(instanceId);
			for (int i = 0; customMetrics != null && i < customMetrics.size(); i++) {
				CustomMetric customMetric = customMetrics.get(i);
				Map<String, Object> metric = createMetric(instance,customMetric);
				if (!metric.isEmpty()) {
					metric.put("isCustomMetric", true);
					if (MetricTypeEnum.PerformanceMetric.equals(metric.get("type"))
							&& MetricTypeEnum.PerformanceMetric.toString().equals(metricType)) {
						metricMaps.put(customMetric.getCustomMetricInfo().getId(), metric);
						perfMetricStateList.add(customMetric.getCustomMetricInfo().getId());
					} else if (MetricTypeEnum.InformationMetric.equals(metric.get("type"))
							&& MetricTypeEnum.InformationMetric.toString().equals(metricType)) {
						metricMaps.put(customMetric.getCustomMetricInfo().getId(), metric);
						// 初始化指标状态值
						msdMaps.put(customMetric.getCustomMetricInfo().getId(),MetricStateEnum.NORMAL);
					} else if (MetricTypeEnum.AvailabilityMetric.equals(metric.get("type"))
							&& MetricTypeEnum.AvailabilityMetric.toString().equals(metricType)) {
						metricMaps.put(customMetric.getCustomMetricInfo().getId(), metric);
					}
				}
			}
			// 性能指标 状态查询
			if (!perfMetricStateList.isEmpty()) {
				List<Long> instanceIdList = new ArrayList<Long>();
				instanceIdList.add(instanceId);
				List<MetricStateData> msdList = metricStateService.findMetricState(instanceIdList, perfMetricStateList);
				for (int i = 0; msdList != null && i < msdList.size(); i++) {
					MetricStateData msd = msdList.get(i);
					msdMaps.put(msd.getMetricID(), msd.getState());
				}
			}
			// 查询指标值
			if (!InfoMetricList.isEmpty()) {
				//查询信息指标需要过滤  
				List<MetricData> infoMetrics = infoMetricQueryAdaptService
						.getMetricInfoDatas(instanceId, InfoMetricList.toArray(new String[InfoMetricList.size()]));
				for (int i = 0; i < infoMetrics.size(); i++) {
					MetricData infoMetric = infoMetrics.get(i);
					if (metricMaps.containsKey(infoMetric.getMetricId())) {
						Map<String, Object> allMetric = metricMaps
								.get(infoMetric.getMetricId());
						// 当前值
						String val = emptyFirstLastChar(infoMetric.getData());
						//val = "".equals(val) ? val : UnitTransformUtil.transform(val, (String) allMetric.get("unit"));
						allMetric.put("currentVal", val);
						// 采集时间
						allMetric.put("lastCollTime", sdf.format(infoMetric.getCollectTime()));
						// 指标状态
						if (msdMaps.containsKey(infoMetric.getMetricId())) {
							allMetric.put("status", msdMaps.get(infoMetric.getMetricId()) .toString());
						} else {
							allMetric.put("status",MetricStateEnum.NORMAL.toString());
						}
					}
				}
			} else if (!perfMetricList.isEmpty()) {
				// 查询性能指标采集数据
				MetricRealtimeDataQuery mrdq = new MetricRealtimeDataQuery();
				mrdq.setInstanceID(new long[] { instanceId });
				mrdq.setMetricID(perfMetricList .toArray(new String[perfMetricList.size()]));
				Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService
						.queryRealTimeMetricDatas(mrdq, 0, 1000);
				for (int i = 0; i < page.getDatas().size(); i++) {
					Map<String, ?> perfMetricMap = page.getDatas().get(i);
					perfMetricMap.remove("instanceid");
					if (!perfMetricMap.isEmpty()) {
						Iterator<String> iter = perfMetricMap.keySet().iterator();
						while (iter.hasNext()) {
							String metricId = iter.next();
							// 采集时间、策略ID、基线ID
							if (metricId.toLowerCase().endsWith("CollectTime".toLowerCase())
									|| metricId.toLowerCase().endsWith("ProfileId".toLowerCase())
									|| metricId.toLowerCase().endsWith("TimelineId".toLowerCase())) {
								continue;
							}
							if (metricMaps.containsKey(metricId)) {
								Map<String, Object> allMetric = metricMaps.get(metricId);
								// 当前值
								String currentVal = perfMetricMap.get(metricId) == null ? ""
										: perfMetricMap.get(metricId).toString();
								// 自定义指标不带单位
								//currentVal = UnitTransformUtil.transform(currentVal,(String) allMetric.get("unit"));
								allMetric.put("currentVal", currentVal);
								// 采集时间
								String time = perfMetricMap.get(metricId+ "CollectTime") == null ? "" 
										: sdf.format(perfMetricMap.get(metricId + "CollectTime"));
								allMetric.put("lastCollTime", time);
								// 指标状态
								if (msdMaps.containsKey(metricId)) {
									allMetric.put("status",msdMaps.get(metricId).toString());
								} else {
									allMetric.put("status",MetricStateEnum.NORMAL.toString());
								}
								// 是否为基线采集
								if (perfMetricMap.containsKey(metricId + "TimelineId")
										&& perfMetricMap.get(metricId+ "TimelineId") != null) {
									Long timelineId = null;
									if(perfMetricMap.get(metricId + "TimelineId") instanceof BigDecimal){
										logger.error("Object type is BigDecimal : " + metricId + "TimelineId");
										timelineId = ((BigDecimal)perfMetricMap.get(metricId + "TimelineId")).longValue();
									}else{
										timelineId = (Long)perfMetricMap.get(metricId + "TimelineId");
									}
									List<ProfileThreshold> proThreList = timelineService
											.getThresholdByTimelineIdAndMetricId(timelineId, metricId);
									// 基线有可能已被删除
									if (proThreList != null
											&& !proThreList.isEmpty()) {
										allMetric.put( "thresholds",
												createThresholds(proThreList, (String)allMetric.get("unit")));
									}
								}
							}
						}
					}
				}
			} else if (!availabilityList.isEmpty()) {
				// 可用性指标
				for (int i = 0; i < availabilityList.size(); i++) {
					String metricId = availabilityList.get(i);
					if (metricMaps.containsKey(metricId)) {
						Map<String, Object> allMetric = metricMaps.get(metricId);
						allMetric.put("status",queryMetricState(instanceId, metricId));
						MetricData md = metricDataService.getMetricAvailableData(instanceId, metricId);
						String currentVal = md == null || md.getData() == null ? "" : md.getData()[0];
						allMetric.put("currentVal", currentVal);
						String time = md == null || md.getCollectTime() == null ? "" : sdf.format(md.getCollectTime());
						allMetric.put("lastCollTime", time);
					}
				}
			}
			// 自定义指标、并组装返回数据
			Iterator<String> iter = metricMaps.keySet().iterator();
			while (iter.hasNext()) {
				String metricId = iter.next();
				Map<String, Object> allMetric = metricMaps.get(metricId);
				if (allMetric.containsKey("isCustomMetric") && (boolean) allMetric.get("isCustomMetric")) {
					MetricData customMetricData = metricDataService.getCustomerMetricData(instanceId, metricId);
					// 当前值
					String customCurrentVal = customMetricData == null || customMetricData.getData() == null 
							? "": customMetricData.getData()[0];
					//customCurrentVal = MetricTypeEnum.AvailabilityMetric .toString().equals(metricType)
					//		? customCurrentVal : UnitTransformUtil.transform(customCurrentVal, 
					//				(String) allMetric.get("unit"));
					
					allMetric.put("currentVal", customCurrentVal);
					// 采集时间
					String customTime = customMetricData == null || customMetricData.getCollectTime() == null ? ""
							: sdf.format(customMetricData.getCollectTime());
					allMetric.put("lastCollTime", customTime);
					// 指标状态
					if (msdMaps.containsKey(metricId)) {
						allMetric.put("status", msdMaps.get(metricId).toString());
					} else {
						allMetric.put("status",MetricStateEnum.NORMAL.toString());
					}
					// 自定义可用性指标状态
					if (MetricTypeEnum.AvailabilityMetric.toString().equals(metricType)) {
						allMetric.put("status",queryMetricState(instanceId, metricId));
					}
				}
				metricList.add(allMetric);
			}
		} catch (Exception e) {
			logger.error("getMetricByType", e);
		}
		return metricList;
	}
	

	private List<Map<String, Object>> handleSummaryMetricData(List<MetricSummaryData> summaryDataList, Date dateStart,
			Date dateEnd) {
		List<Map<String,Object>> resultData = new ArrayList<>();
		for (MetricSummaryData msd : summaryDataList) {
			Map<String,Object> mapData = new HashMap<>();
			mapData.put("data", msd.getMetricData());
			mapData.put("collectTime", msd.getEndTime().getTime());
			resultData.add(mapData);
		}
		return resultData;
	}
	
	private List<Map<String, Object>> handleMetricData(List<MetricData> metricDataList, Date dateStart, Date dateEnd) {
		List<Map<String,Object>> resultData = new ArrayList<>();
		for (MetricData msd : metricDataList) {
			Map<String,Object> mapData = new HashMap<>();
			String[] data = msd.getData();
			
			if(data != null && data.length > 0)
				mapData.put("data", data[0]);
			else
				mapData.put("data", null);
			mapData.put("collectTime", msd.getCollectTime().getTime());
			resultData.add(mapData);
		}
		return resultData;
	}
	
	
	private List<MetricData> getHistoryMetricDataByHour(long instanceId ,String metricId ,Date dateStart , Date dateEnd ){
		try {
			if(ifCustomMetric( instanceId , metricId)){
				return metricDataService.queryHistoryCustomerMetricData(metricId, instanceId, dateStart, dateEnd);
			}else{
				return metricDataService.queryHistoryMetricData(metricId, instanceId, dateStart, dateEnd);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new ArrayList<MetricData>();
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
			if(ifCustomMetric( instanceId , metricId)){
				return metricSummaryService.queryCustomMetricSummary(msq);
			}else{
				return metricSummaryService.queryMetricSummary(msq);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new ArrayList<MetricSummaryData>();
		}
		
	}
	
	private boolean ifCustomMetric(long instanceId ,String metricId){
		try {
			List<CustomMetric> customMetrics = customMetricService.getCustomMetricsByInstanceId(instanceId);
			if(null!=customMetrics){
				for(CustomMetric cmetric:customMetrics){
					if(cmetric.getCustomMetricInfo().getId().equals(metricId)){
						 return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;
		}
		return false;
	}

	/**
	 * 封装一个指标的信息
	 * 
	 * @param instance
	 * @param profileMetric
	 * @return
	 */
	private Map<String, Object> createMetric(ResourceInstance instance,ProfileMetric profileMetric) {
		Map<String, Object> metric = new HashMap<String, Object>();
		String metricId = profileMetric.getMetricId();
		ResourceMetricDef rmd = capacityService.getResourceMetricDef(
				instance.getResourceId(), metricId);
		if (rmd != null && profileMetric.isMonitor() && rmd.isDisplay()) {
			// id,名称
			metric.put("id", metricId);
			metric.put("text", rmd.getName());
			metric.put("unit", rmd.getUnit());
			metric.put("isTable", rmd.isTable());
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
	 * 封装一个指标的信息
	 * 
	 * @param instance
	 * @param customMetric
	 * @return
	 */
	private Map<String, Object> createMetric(ResourceInstance instance,
			CustomMetric customMetric) {
		Map<String, Object> metric = new HashMap<String, Object>();
		CustomMetricInfo cmi = customMetric.getCustomMetricInfo();
		if (cmi.isMonitor()) {
			// id,名称
			metric.put("id", cmi.getId());
			metric.put("text", cmi.getName());
			metric.put("unit", cmi.getUnit());
			metric.put("isTable", false);
			metric.put("isAlarm", cmi.isAlert());
			// 阈值
			List<CustomMetricThreshold> cmts = customMetric
					.getCustomMetricThresholds();
			if (cmts != null && cmts.size() > 1) {
				metric.put("thresholds",
						createCustomThresholds(cmts, cmi.getUnit()));
			}
			// 指标状态
			switch (cmi.getStyle()) {
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

	private String queryMetricState(long instanceId, String metricId) {
		MetricStateData msd = metricStateService.getMetricState(instanceId,
				metricId);
		String state = msd == null ? MetricStateEnum.NORMAL.toString() : msd.getState().toString();
		return state;
	}
	
	/**
	 * 把字符串数组转换成数符串
	 * 
	 * @param data
	 * @return
	 */
	private String emptyFirstLastChar(String[] data) {
		if (data == null || data.length == 0) {
			return "";
		} else {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < data.length; i++) {
				if (data[i] != null && !"".equals(data[i].trim())) {
					str.append(data[i].trim());
					if (i < data.length - 1) {
						str.append(" , ");
					}
				}
			}
			return str.toString();
		}
	}
	
	/**
	 * 创建指标域值字符串
	 * 
	 * @param proThreList
	 * @param unit
	 * @return
	 */
	private String createThresholds(List<ProfileThreshold> proThreList,
			String unit) {
		String minor = "", major = "";
		for (ProfileThreshold thre : proThreList) {
			switch (thre.getPerfMetricStateEnum()) {
			case Minor:
				minor = thre.getThresholdValue();
				break;
			case Major:
				major = thre.getThresholdValue();
				break;
			default:
				break;
			}
		}
		String threVal = "['" + unit + "'," + minor + "," + major + "]";
		return threVal;
	}
	
	/**
	 * 创建自定义指标域值字符串
	 * 
	 * @param cmts
	 * @param unit
	 * @return
	 */
	private String createCustomThresholds(List<CustomMetricThreshold> cmts,
			String unit) {
		String minor = "", major = "";
		for (int j = 0; j < cmts.size(); j++) {
			CustomMetricThreshold cmt = cmts.get(j);
			switch (cmt.getMetricState()) {
			case WARN:
				minor = cmt.getThresholdValue();
				break;
			case SERIOUS:
				major = cmt.getThresholdValue();
				break;
			default:
				break;
			}
		}
		String threVal = "['" + unit + "'," + minor + "," + major + "]";
		return threVal;
	}
	
}
