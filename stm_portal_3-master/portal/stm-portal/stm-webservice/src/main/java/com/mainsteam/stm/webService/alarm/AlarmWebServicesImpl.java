package com.mainsteam.stm.webService.alarm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncService;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.state.thirdparty.ThirdPartyMetricStateService;
import com.mainsteam.stm.state.thirdparty.obj.ThirdPartyMetricStateData;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.util.Util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.alarm.api.IRemoteDataQueryRecordApi;
import com.mainsteam.stm.portal.alarm.bo.RemoteDataQueryRecord;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.webService.obj.Result;
import com.mainsteam.stm.webService.obj.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@WebService
public class AlarmWebServicesImpl implements AlarmWebServices {
	private static final Log logger = LogFactory
			.getLog(AlarmWebServicesImpl.class);

	@Resource(name = "org.apache.cxf.jaxws.context.WebServiceContextImpl")
    private WebServiceContext context;

	@Resource
	private AlarmService alarmService;
	
	@Resource(name="stm_system_resourceApi")
	private IResourceApi resourceApi;

	@Resource
	private AlarmEventService alarmEventService;

	@Resource
	private AlarmRuleService alarmRuleService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private IRemoteDataQueryRecordApi remoteDataQueryRecordService;

	@Resource
	private CapacityService capacityService;

	@Resource
	private ThirdPartyMetricStateService thirdPartyMetricStateService;
	@Autowired
	@Qualifier("dataSyncService")
	private DataSyncService dataSyncService;

	private final String[] portAbout = {ResourceTypeEnum.DATABASE.getValue(), ResourceTypeEnum.WEBSERVER.getValue(), ResourceTypeEnum.CACHESERVER.getValue(),
		DeviceTypeEnum.DNS.getValue(), DeviceTypeEnum.FTP.getValue(), DeviceTypeEnum.PORT.getValue(), DeviceTypeEnum.POP3.getValue(), DeviceTypeEnum.SMTP.getValue()};

	@Override
	public AlarmSyncResult syncAlarm(AlarmSyncBean alarmBean) {
		AlarmSyncResult rst = new AlarmSyncResult();
		AlarmSenderParamter asp = new AlarmSenderParamter();
		asp.setSourceID(alarmBean.getSourceID());
		asp.setSourceName(alarmBean.getSourceName());
		asp.setSourceIP(alarmBean.getSourceIP());
		asp.setDefaultMsg(alarmBean.getContent());
		asp.setDefaultMsgTitle(alarmBean.getContent());
		asp.setExt0(SysModuleEnum.OTHER.name());
//		DateFormat dateFormat = DateFormat.getDateInstance();
//		try {
//			dateFormat.parse(String.valueOf(alarmBean.getCreateTime()));
//		} catch (ParseException e) {
//			if(logger.isWarnEnabled()) {
//				logger.warn("Starts to alarm " + alarmBean.toString(), e);
//			}
//			rst.setResult(false);
//			rst.setMsg("Invalid date format");
//			return rst;
//		}
		asp.setGenerateTime(alarmBean.getCreateTime());
		asp.setProvider(AlarmProviderEnum.OTHER_SYS);
		asp.setSysID(SysModuleEnum.OTHER);
		asp.setLevel(toInstanceState(alarmBean.getAlarmLevel()));
		alarmService.notify(asp);


		rst.setResult(true);
		rst.setMsg("操作成功");
		return rst;
	}

	@Override
	public AlarmSyncResult bindAlarmWithResource(AlarmBindResourceBean alarmBindResourceBean) {
		String message = "";
		boolean flag = false;
		AlarmSyncResult alarmSyncResult = new AlarmSyncResult();
		try{
			if(logger.isInfoEnabled()) {
				logger.info("Starts to bind resource with " + alarmBindResourceBean.toString());
			}
			AlarmSenderParamter alarmSenderParamter = new AlarmSenderParamter();
			alarmSenderParamter.setDefaultMsg(alarmBindResourceBean.getAlarmContent());
			alarmSenderParamter.setDefaultMsgTitle(alarmBindResourceBean.getAlarmContent());
			alarmSenderParamter.setSourceIP(alarmBindResourceBean.getIp());
			if(null != toInstanceState(alarmBindResourceBean.getAlarmLevel()))
				alarmSenderParamter.setLevel(toInstanceState(alarmBindResourceBean.getAlarmLevel()));
			else{
				if(logger.isWarnEnabled()) {
					logger.warn("Instance state is null, with " + alarmBindResourceBean.toString());
				}
				alarmSyncResult.setMsg("Can not matched instance state.");
				alarmSyncResult.setResult(false);
				return alarmSyncResult;
			}

			alarmSenderParamter.setSysID(SysModuleEnum.OTHER);
			alarmSenderParamter.setProvider(AlarmProviderEnum.OTHER_SYS);
			alarmSenderParamter.setGenerateTime(alarmBindResourceBean.getAlarmCreateTime().getTime());

			ResourceInstance matchedResourceInstance = null;
			CategoryDef matchedCategoryDef = null;

			if(StringUtils.isNotBlank(alarmBindResourceBean.getInstanceID())) { //如果传入了资源实例ID
				long instanceId = -1L;
				try{
					instanceId = Long.valueOf(alarmBindResourceBean.getInstanceID());
				}catch(Exception e){}
				if(instanceId == -1){
					alarmSyncResult.setMsg("Invalid instance id format.");
					alarmSyncResult.setResult(false);
					return alarmSyncResult;
				}
				matchedResourceInstance = resourceInstanceService.getResourceInstance(instanceId);
				if(null != matchedResourceInstance) {
					String resourceId = null;
					if(matchedResourceInstance.getParentId() > 0)
						resourceId = resourceInstanceService.getResourceInstance(matchedResourceInstance.getParentId()).getResourceId();
					else
						resourceId = matchedResourceInstance.getResourceId();
					ResourceDef resourceDef = null;
					if(null != resourceId)
						resourceDef = capacityService.getResourceDefById(resourceId);
					if(null != resourceDef) {
						matchedCategoryDef = resourceDef.getCategory();
					}else {
						alarmSyncResult.setMsg("Can not find resource category using instance id " + instanceId + ", resource id " + resourceId);
						alarmSyncResult.setResult(false);
						return alarmSyncResult;
					}

				}else {
					alarmSyncResult.setMsg("Can not find instance id using " + instanceId);
					alarmSyncResult.setResult(false);
					return alarmSyncResult;
				}

			}else{
				//关联设备
				CategoryDef parentCategoryDef = capacityService.getCategoryById(alarmBindResourceBean.getResourceType().getValue());

				if(parentCategoryDef !=null) {

					CategoryDef[] categoryDefs;
					if(ResourceTypeEnum.STANDARDSERVICE.equals(alarmBindResourceBean.getResourceType())) { // 标准服务
						CategoryDef categoryDef = capacityService.getCategoryById(alarmBindResourceBean.getDeviceType().getValue());
						categoryDefs = new CategoryDef[]{categoryDef};
					}else {
						categoryDefs = capacityService.getCategoryById(alarmBindResourceBean.getResourceType().getValue()).getChildCategorys();
					}

					for(CategoryDef categoryDef : categoryDefs) {

						try {
							List<ResourceInstance> resourceInstanceList = resourceInstanceService.getParentInstanceByCategoryId(categoryDef.getId());
							if(!resourceInstanceList.isEmpty()) {
								for(ResourceInstance resourceInstance : resourceInstanceList) {
									if(ArrayUtils.contains(resourceInstance.getDiscoverPropBykey("IP"), alarmBindResourceBean.getIp()) ||
											ArrayUtils.contains(resourceInstance.getModulePropBykey("IP"), alarmBindResourceBean.getIp())) {
										if(logger.isDebugEnabled()) {
											logger.debug("Match IP success, {" + alarmBindResourceBean.getIp() + ": "+resourceInstance.getId()+"}");
										}
										if(ArrayUtils.contains(portAbout, alarmBindResourceBean.getResourceType().getValue()) ||
												(alarmBindResourceBean.getDeviceType() !=null && ArrayUtils.contains(portAbout, alarmBindResourceBean.getDeviceType().getValue()))) {
											List<DiscoverProp> discoverPropList = resourceInstance.getDiscoverProps();
											boolean isPortMatched = false;
											if(discoverPropList !=null && !discoverPropList.isEmpty()) {
												for(DiscoverProp discoverProp : discoverPropList) {
													//匹配端口
													if(StringUtils.isNotBlank(alarmBindResourceBean.getParameter1())) {
														if(StringUtils.endsWithIgnoreCase(discoverProp.getKey(), "port") &&
																ArrayUtils.contains(resourceInstance.getDiscoverPropBykey(discoverProp.getKey()), alarmBindResourceBean.getParameter1())) {
															isPortMatched = true;
															if(logger.isDebugEnabled())
																logger.debug("Match Port success, {" + discoverProp.getKey() + ":" + alarmBindResourceBean.getParameter1() + "}");
														}

													}
												}
											}
											if(isPortMatched) {
												if(StringUtils.equals(alarmBindResourceBean.getResourceType().getValue(), ResourceTypeEnum.DATABASE.getValue())) {
													if(StringUtils.isNotBlank(alarmBindResourceBean.getParameter2())) {
														if(ArrayUtils.contains(resourceInstance.getDiscoverPropBykey("dbName"), alarmBindResourceBean.getParameter2())){
															if(logger.isDebugEnabled())
																logger.debug("Match database success, {" + resourceInstance.getId() + ":" + alarmBindResourceBean.getParameter2() + "}");
															matchedResourceInstance = resourceInstance;
															matchedCategoryDef = categoryDef;
														}
													}
												}else {
													matchedResourceInstance = resourceInstance;
													matchedCategoryDef = categoryDef;
												}
											}else {
												if(logger.isDebugEnabled()) {
													logger.debug("Match resource failed with port " + alarmBindResourceBean.toString());
												}
											}
										}else if(alarmBindResourceBean.getDeviceType() !=null &&
												StringUtils.equals(alarmBindResourceBean.getDeviceType().getValue(), DeviceTypeEnum.PROCESS.getValue())) {//匹配进程
											String installPaths[] = resourceInstance.getDiscoverPropBykey("InstallPath");
											if(installPaths !=null && installPaths.length > 0) {
												if(alarmBindResourceBean.getParameter1() !=null && StringUtils.equals(installPaths[0], alarmBindResourceBean.getParameter1())) {
													matchedResourceInstance = resourceInstance;
													matchedCategoryDef = categoryDef;
												}
											}else {
												if(logger.isInfoEnabled()) {
													logger.info("Matched process failed." + alarmBindResourceBean.toString());
												}
											}

										}else {
											matchedResourceInstance = resourceInstance;
											matchedCategoryDef = categoryDef;
										}
									}
								}
							}
						} catch (InstancelibException e) {
							if(logger.isWarnEnabled()) {
								logger.warn(e.getMessage(), e);
							}
						}
					}

				}else {
					if(logger.isWarnEnabled()) {
						logger.warn("Can not find resource category, " + alarmBindResourceBean.toString());
					}
					message = "Can not find resource category using " + alarmBindResourceBean.getResourceType();
				}
			}

			if(matchedResourceInstance !=null) {
				if(logger.isDebugEnabled())
					logger.debug("Match resource instance " + JSONObject.toJSONString(matchedResourceInstance));
				alarmSenderParamter.setSourceID(String.valueOf(matchedResourceInstance.getId()));
				alarmSenderParamter.setSourceName(matchedResourceInstance.getName());
				alarmSenderParamter.setExt0(matchedResourceInstance.getResourceId());
				if(null != matchedCategoryDef) {
					alarmSenderParamter.setExt1(matchedCategoryDef.getId());
					alarmSenderParamter.setExt2(matchedCategoryDef.getParentCategory().getId());
				}
				alarmSenderParamter.setExt3(alarmBindResourceBean.getAlarmID());
				alarmSenderParamter.setRecoverKeyValue(new String[]{String.valueOf(matchedResourceInstance.getId()), alarmBindResourceBean.getAlarmID()});

				alarmService.notify(alarmSenderParamter);

				//保存状态
				ThirdPartyMetricStateData thirdPartyMetricStateData = new ThirdPartyMetricStateData();
				thirdPartyMetricStateData.setInstanceID(matchedResourceInstance.getId());
				thirdPartyMetricStateData.setMetricID(alarmBindResourceBean.getAlarmID());
				MetricStateEnum metricStateEnum = toMetricState(alarmBindResourceBean.getAlarmLevel());
				if(null != metricStateEnum)
					thirdPartyMetricStateData.setState(metricStateEnum);
				thirdPartyMetricStateData.setUpdateTime(alarmSenderParamter.getGenerateTime());
				thirdPartyMetricStateService.updateIfExistsOrAddState(thirdPartyMetricStateData);

				DataSyncPO dataSyncPO = new DataSyncPO();
				dataSyncPO.setCreateTime(alarmSenderParamter.getGenerateTime());
				dataSyncPO.setUpdateTime(alarmSenderParamter.getGenerateTime());
				dataSyncPO.setType(DataSyncTypeEnum.OTHER_STATE);
				dataSyncPO.setData(JSONObject.toJSONString(thirdPartyMetricStateData));
				dataSyncService.save(dataSyncPO);

				message = "Bind resource success.";
				flag = true;
			}else {
				if(logger.isInfoEnabled()) {
					logger.info("Can not matches resource instance, " + alarmBindResourceBean.toString());
				}
				message = "Can not find resource, so bind failed";
			}
			alarmSyncResult.setMsg(message);
			alarmSyncResult.setResult(flag);
			return alarmSyncResult;

		}catch(Throwable throwable) {
			if(logger.isWarnEnabled()) {
				logger.warn(throwable.getMessage() + "," + alarmBindResourceBean.toString(), throwable);
			}
			message = "Occurs exception.";
			alarmSyncResult.setMsg(message);
			alarmSyncResult.setResult(false);
			return alarmSyncResult;
		}
	}

	private InstanceStateEnum toInstanceState(AlarmStateEnum alarmStateEnum) {
		if (alarmStateEnum == AlarmStateEnum.CRITICAL) {
			return InstanceStateEnum.CRITICAL;
		} else if (alarmStateEnum == AlarmStateEnum.NORMAL) {
			return InstanceStateEnum.NORMAL;
		} else if (alarmStateEnum == AlarmStateEnum.SERIOUS) {
			return InstanceStateEnum.SERIOUS;
		} else if (alarmStateEnum == AlarmStateEnum.WARN) {
			return InstanceStateEnum.WARN;
		}
		return null;
	}

	private MetricStateEnum toMetricState(AlarmStateEnum alarmStateEnum) {
		if(alarmStateEnum == AlarmStateEnum.NORMAL)
			return MetricStateEnum.NORMAL;
		else if(alarmStateEnum == AlarmStateEnum.SERIOUS)
			return MetricStateEnum.SERIOUS;
		else if(alarmStateEnum == AlarmStateEnum.WARN)
			return MetricStateEnum.WARN;
		return null;
	}


	@Override
	public String getAllAlarmInCurrentOnehour() {

		AlarmEventQuery aeq = new AlarmEventQuery();

		Date end = new Date();
		Date start = new Date(end.getTime() - 60 * 60 * 1000);

		aeq.setStart(start);
		aeq.setEnd(end);

		List<AlarmEvent> aeList = alarmEventService.findAlarmEvent(aeq);
		Result result = new Result();

		if (null != aeList && aeList.size() > 0) {
			List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
			for (AlarmEvent ae : aeList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(ae.getEventID()));
				map.put("alarmState", ae.getLevel().name());
				map.put("content", ae.getContent());
				map.put("ip", null == ae.getSourceIP() ? "" : ae.getSourceIP());
				map.put("resourceType", ae.getExt2());
				map.put("collectionTime",
						String.valueOf(ae.getCollectionTime().getTime()));
				map.put("alarmCollectionSource", ae.getSourceName());
				mapList.add(map);
			}

			result.setData(mapList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}

	}

	@Override
	public String getAlarmByAlarmLevel(String level) {
		AlarmEventQuery aeq = new AlarmEventQuery();
		List<MetricStateEnum> mseList = new ArrayList<MetricStateEnum>();
		switch (level) {
		case "CRITICAL":
			mseList.add(MetricStateEnum.CRITICAL);
			break;
		case "NORMAL":
			mseList.add(MetricStateEnum.NORMAL);
			break;
		case "SERIOUS":
			mseList.add(MetricStateEnum.SERIOUS);
			break;
		case "UNKOWN":
			mseList.add(MetricStateEnum.UNKOWN);
			break;
		case "WARN":
			mseList.add(MetricStateEnum.WARN);
			break;
		default:
			mseList.add(MetricStateEnum.NORMAL);
			break;
		}
		aeq.setStates(mseList);

		List<AlarmEvent> aeList = alarmEventService.findAlarmEvent(aeq);
		Result result = new Result();

		if (null != aeList && aeList.size() > 0) {
			List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
			for (AlarmEvent ae : aeList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(ae.getEventID()));
				map.put("alarmState", ae.getLevel().name());
				map.put("content", ae.getContent());
				map.put("ip", null == ae.getSourceIP() ? "" : ae.getSourceIP());
				map.put("resourceType", ae.getExt2());
				map.put("collectionTime",
						String.valueOf(ae.getCollectionTime().getTime()));
				map.put("alarmCollectionSource", ae.getSourceName());
				mapList.add(map);
			}

			result.setData(mapList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	@Override
	public String getAlarmByTime(String start, String end) {
		Date startTime = null;
		Date endTime = null;
		Result result = new Result();
		try {
			Long startLong = Long.valueOf(start);
			Long endLong = Long.valueOf(end);

			if (endLong.longValue() < startLong.longValue()) {
				result.setData("");
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryEndDateBeforeStartDateError_CODE);
				return JSONObject.toJSONString(result);
			}

			startTime = new Date(startLong);
			endTime = new Date(Long.valueOf(endLong));
		} catch (Exception e) {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_DateFormatError_CODE);
			return JSONObject.toJSONString(result);
		}

		AlarmEventQuery aeq = new AlarmEventQuery();
		aeq.setStart(startTime);
		aeq.setEnd(endTime);
		List<AlarmEvent> aeList = alarmEventService.findAlarmEvent(aeq);

		if (null != aeList && aeList.size() > 0) {
			List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
			for (AlarmEvent ae : aeList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(ae.getEventID()));
				map.put("alarmState", ae.getLevel().name());
				map.put("content", ae.getContent());
				map.put("ip", null == ae.getSourceIP() ? "" : ae.getSourceIP());
				map.put("resourceType", ae.getExt2());
				map.put("collectionTime",
						String.valueOf(ae.getCollectionTime().getTime()));
				map.put("alarmCollectionSource", ae.getSourceName());
				mapList.add(map);
			}

			result.setData(mapList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	@Override
	public String getAlarmByAlarmType(String AlarmType) {
		AlarmEventQuery aeq = new AlarmEventQuery();

		SysModuleEnum[] smeArr = new SysModuleEnum[1];
		switch (AlarmType) {
		case "BUSSINESS":
			smeArr[0] = SysModuleEnum.BUSSINESS;
			break;
		case "CONFIG_MANAGER":
			smeArr[0] = SysModuleEnum.CONFIG_MANAGER;
			break;
		case "IP_MAC_PORT":
			smeArr[0] = SysModuleEnum.IP_MAC_PORT;
			break;
		case "LINK":
			smeArr[0] = SysModuleEnum.LINK;
			break;
		case "MONITOR":
			smeArr[0] = SysModuleEnum.MONITOR;
			break;
		case "NETFLOW":
			smeArr[0] = SysModuleEnum.NETFLOW;
			break;
		case "OTHER":
			smeArr[0] = SysModuleEnum.OTHER;
			break;
		case "SYSLOG":
			smeArr[0] = SysModuleEnum.SYSLOG;
			break;
		case "TRAP":
			smeArr[0] = SysModuleEnum.TRAP;
			break;
		}

		aeq.setSysIDes(smeArr);
		List<AlarmEvent> aeList = alarmEventService.findAlarmEvent(aeq);
		Result result = new Result();

		if (null != aeList && aeList.size() > 0) {
			List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
			int i = 0;
			for (AlarmEvent ae : aeList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(ae.getEventID()));
				map.put("alarmState", ae.getLevel().name());
				map.put("content", ae.getContent());
				map.put("ip", null == ae.getSourceIP() ? "" : ae.getSourceIP());
				map.put("resourceType", ae.getExt2());
				map.put("collectionTime",
						String.valueOf(ae.getCollectionTime().getTime()));
				map.put("alarmCollectionSource", ae.getSourceName());
				if (i < 100) {
					mapList.add(map);
				}

				i++;
			}

			result.setData(mapList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	@Override
	public String getAlarmByResourceType(String resourceId) {

		AlarmEventQuery aeq = new AlarmEventQuery();
		aeq.setExt2(resourceId);
		List<AlarmEvent> aeList = alarmEventService.findAlarmEvent(aeq);
		Result result = new Result();

		if (null != aeList && aeList.size() > 0) {
			List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
			for (AlarmEvent ae : aeList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(ae.getEventID()));
				map.put("alarmState", ae.getLevel().name());
				map.put("content", ae.getContent());
				map.put("ip", null == ae.getSourceIP() ? "" : ae.getSourceIP());
				map.put("resourceType", ae.getExt2());
				map.put("collectionTime",
						String.valueOf(ae.getCollectionTime().getTime()));
				map.put("alarmCollectionSource", ae.getSourceName());
				mapList.add(map);
			}

			result.setData(mapList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	@Override
	public String getAlarmByInstanceId(String instanceId) {
		AlarmEventQuery aeq = new AlarmEventQuery();

		List<String> inIds = new ArrayList<String>();
		inIds.add(instanceId);
		aeq.setSourceIDes(inIds);
		List<AlarmEvent> aeList = alarmEventService.findAlarmEvent(aeq);
		Result result = new Result();

		if (null != aeList && aeList.size() > 0) {
			List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
			for (AlarmEvent ae : aeList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(ae.getEventID()));
				map.put("alarmState", ae.getLevel().name());
				map.put("content", ae.getContent());
				map.put("ip", null == ae.getSourceIP() ? "" : ae.getSourceIP());
				map.put("resourceType", ae.getExt2());
				map.put("collectionTime",
						String.valueOf(ae.getCollectionTime().getTime()));
				map.put("alarmCollectionSource", ae.getSourceName());
				mapList.add(map);
			}

			result.setData(mapList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}

	@Override
	public List<AlarmRule> getAlarmRulesByProfileIdByNetflow(long id) {
		return alarmRuleService.getAlarmRulesByProfileId(id,
				AlarmRuleProfileEnum.netFlow);
	}

	@Override
	public void deleteAlarmRuleById(long[] ids) {
		alarmRuleService.deleteAlarmRuleById(ids);
	}

	@Override
	public void notify(AlarmNotifyBean alarmNotify) {
		AlarmSenderParamter asp = new AlarmSenderParamter();
		asp.setProfileID(0);
		asp.setRuleType(AlarmRuleProfileEnum.netFlow);
		asp.setProvider(AlarmProviderEnum.OC4);
		asp.setSysID(SysModuleEnum.NETFLOW);
		asp.setSourceID(alarmNotify.getSourceId());
		asp.setSourceName(alarmNotify.getSourceName());
		if (alarmNotify.getSubfix() != null
				&& !"".equals(alarmNotify.getSubfix())
				&& "1".equals(alarmNotify.getSubfix())) {// 对应为，1对应严重，2对应告警
			asp.setLevel(InstanceStateEnum.SERIOUS);
		} else {
			asp.setLevel(InstanceStateEnum.WARN);
		}
		asp.setDefaultMsgTitle(alarmNotify.getDefaultMsgTitle());
		asp.setDefaultMsg(alarmNotify.getDefaultMsg());
		asp.setGenerateTime(alarmNotify.getGenerateTime());
		asp.setNotifyRules(alarmNotify.getNotifyRules());
		alarmService.notify(asp);
	}
	
	@Override
	public AlarmParentResourceDownResult getParentResourceCriticalAlarm(){
		MessageContext ctx = context.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) ctx.get(AbstractHTTPDestination.HTTP_REQUEST);
		
		AlarmParentResourceDownResult result = new AlarmParentResourceDownResult();
		List<AlarmParentResourceDownResultBean> mapList = new ArrayList<AlarmParentResourceDownResultBean>();
		SysModuleEnum[] monitorList = {SysModuleEnum.MONITOR};
		String queryState = "false";
		
		List<AlarmEvent> aeList = findAlarmEvent(monitorList);
		if (null != aeList && aeList.size() > 0) {
			RemoteDataQueryRecord rdqr = getLastAlarmIdByRequest(request);
			Long sendAlarmId = rdqr.getLastAlarmId();
			Long largestAlarmId = sendAlarmId;
			
			for (AlarmEvent ae : aeList) {
				if(sendAlarmId.longValue() < ae.getEventID()){
					if(largestAlarmId.longValue() < ae.getEventID()){
						largestAlarmId = ae.getEventID();
					}
					
					AlarmParentResourceDownResultBean alarm = new AlarmParentResourceDownResultBean();
					alarm.setAlarmId(String.valueOf(ae.getEventID()));
					alarm.setSourceIP(null == ae.getSourceIP() ? "" : ae.getSourceIP());
					alarm.setSourceName(ae.getSourceName());
					
					Date dateCollect = ae.getCollectionTime();
					if(null!=dateCollect){
						alarm.setCollectionTime(String.valueOf(ae.getCollectionTime().getTime()));
					}
					alarm.setAlarmContent(ae.getContent());
					mapList.add(alarm);
				}
			}
			rdqr.setLastAlarmId(largestAlarmId);
			
			if(mapList.size()>0){
				String msg = "alarmEvent query success!!!";
				queryState = "true";
				
				if(!updateRemoteDataQueryRecord(request,rdqr)){
					logger.error("alarmEvent query success , but updateRemoteDataQueryRecord false !!!");
				}
				
				result.setInfo(mapList, queryState, msg);
				return result;
			}
		}
		String msg = "alarmEvent is null!!!";
		result.setInfo(mapList, queryState, msg);
		return result;
	}
	
	
	private List<AlarmEvent> findAlarmEvent(SysModuleEnum[] sysModules){
		List<ResourceInstance> riList = null;
		try {
			riList = resourceInstanceService.getAllParentInstance();
			
			if(null==riList){
				return null;
			}
		} catch (InstancelibException e) {
			String errorMsg = "load ParentInstance error!!!";
			logger.error(errorMsg);
			logger.error(e.getMessage());
			
			return null;
		}
		List<String> strList = new ArrayList<String>();
		for(ResourceInstance ri:riList){
			if(ri.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
				strList.add(String.valueOf(ri.getId()));
			}
		}
		List<MetricStateEnum>  mseList = getMetricStateEnum("down");
		
		AlarmEventQuery2 eq = new AlarmEventQuery2();
		List<AlarmEventQueryDetail> filters=new ArrayList<>();
		for(SysModuleEnum sme:sysModules){
			AlarmEventQueryDetail detail=new AlarmEventQueryDetail();
			detail.setSysID(sme);
			detail.setSourceIDes(strList);
			detail.setStates(mseList);
			detail.setRecovered(false);
//			detail.setExt1(vo.getChildCategory());
//			detail.setExt2(vo.getPrentCategory());
			
			filters.add(detail);
		}
		eq.setFilters(filters);
		
		return alarmEventService.findAlarmEvent(eq);
	}
	
	private RemoteDataQueryRecord getLastAlarmIdByRequest(HttpServletRequest request){
		String host = request.getRemoteHost();
		if(null==host){
			RemoteDataQueryRecord rdqr = new RemoteDataQueryRecord();
			rdqr.setLastAlarmId(new Long(0));
			return rdqr;
		}
		List<RemoteDataQueryRecord>  rdqrList = remoteDataQueryRecordService.loadByIp(host);
		if(null==rdqrList || rdqrList.size()==0){
			RemoteDataQueryRecord rdqr = new RemoteDataQueryRecord();
			rdqr.setLastAlarmId(new Long(0));
			return rdqr;
		}
		return rdqrList.get(0);
	}
	
	private boolean updateRemoteDataQueryRecord(HttpServletRequest request,RemoteDataQueryRecord rdqr){
		String host = request.getRemoteHost();
		int port = request.getRemotePort();
		
		rdqr.setRemoteIp(host);
		rdqr.setRemotePort(port);
		rdqr.setQueryTime(new Date());
		
		if(null==rdqr.getRecordId()){
			return 1==remoteDataQueryRecordService.add(rdqr);
		}else{
			return 1==remoteDataQueryRecordService.update(rdqr);
		}
	}
	
	private static List<MetricStateEnum> getMetricStateEnum(String metricStateEnumString) {
		List<MetricStateEnum> ise = new ArrayList<MetricStateEnum>();
		if (null == metricStateEnumString) {
			return ise;
		} else {
			switch (metricStateEnumString) {
			case "all":
				break;
			case "down":
				ise.add(MetricStateEnum.CRITICAL);
				
				break;
			case "metric_error":
				ise.add(MetricStateEnum.SERIOUS);
				
				break;
			case "metric_warn":
				ise.add(MetricStateEnum.WARN);
				
				break;
			case "metric_recover":
				ise.add(MetricStateEnum.NORMAL);
				
				break;
			case "metric_unkwon":
				ise.add(MetricStateEnum.UNKOWN);
				
				break;
			}
		}
		return ise;
	}
	
	
	  public static void writeProperties(String keyname,String keyvalue ,Properties props,String filePath) {       
	        try {
	            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
	            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
	        	
	        	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	        	URL url = loader.getResource(filePath);
	        	File file = new File(url.getFile());
	            OutputStream fos = new FileOutputStream(file);
	            props.setProperty(keyname, keyvalue);
	            // 以适合使用 load 方法加载到 Properties 表中的格式，
	            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
	            props.store(fos, "Update '" + keyname + "' value");
	        } catch (IOException e) {
	        	e.printStackTrace();
	            System.err.println("属性文件更新错误");
	        }
	    }

	@Override
	public String getAlarmListByPage(AlarmQueryPageBean alarmQueryPage) {
		
		Result result=new Result();
		
		int startRow=alarmQueryPage.getStartRow();
		int rowCount=alarmQueryPage.getRowCount();
		SysModuleEnum[] monitorList=alarmQueryPage.getMonitorList();
		
		//默认的告警类型
		if(monitorList==null || monitorList.length==0){
			SysModuleEnum[] monitorListBase = {SysModuleEnum.MONITOR,SysModuleEnum.BUSSINESS,SysModuleEnum.IP_MAC_PORT,SysModuleEnum.LINK};
			monitorList=monitorListBase;
		}
		
		AlarmEventQuery2 alarmEventQuery = new AlarmEventQuery2();

		List<ResourceInstanceBo> rbList = resourceApi.getAllParentInstance();
		
		List<AlarmEventQueryDetail> queryDetailList=new ArrayList<AlarmEventQueryDetail>();	

		for(SysModuleEnum sysModule:monitorList){
			AlarmEventQueryDetail queryDetail=new AlarmEventQueryDetail();
			queryDetail.setRecovered(alarmQueryPage.getRecovered());
			queryDetail.setStates(alarmQueryPage.getStates());
			queryDetail.setExt1(alarmQueryPage.getChildCategory());
			queryDetail.setExt2(alarmQueryPage.getPrentCategory());
			queryDetail.setStart(alarmQueryPage.getStart());
			queryDetail.setEnd(alarmQueryPage.getEnd());
			queryDetail.setSysID(sysModule);

			try {
				if(sysModule.equals(SysModuleEnum.MONITOR)){
					List<String> resourceIdList = new  ArrayList<String>();
					Set<Long> parentInstanceIdSet = new HashSet<Long>();
					List<Long> parentInstanceIdList = new ArrayList<Long>();
					for(ResourceInstanceBo rb : rbList){
						//删除状态,未监控状态时不显示
						if(null != rb.getLifeState() && rb.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
							parentInstanceIdSet.add(rb.getId());
							parentInstanceIdList.add(rb.getId());
						}
					}
					if(!parentInstanceIdSet.isEmpty()){
						List<Long> childInstanceIds = resourceInstanceService.getAllChildrenInstanceIdbyParentId(parentInstanceIdSet,InstanceLifeStateEnum.MONITORED);
						for(int j = 0; childInstanceIds != null && j < childInstanceIds.size(); j ++){
							resourceIdList.add(childInstanceIds.get(j).toString());
						}
					}
					for(int j = 0; j < parentInstanceIdList.size(); j++){
						resourceIdList.add(parentInstanceIdList.get(j).toString());
					}
					queryDetail.setSourceIDes(resourceIdList);
				}else if(sysModule.equals(SysModuleEnum.LINK)){
					List<String> resourceIdList = new  ArrayList<String>();
					List<ResourceInstance> linkResourceInstList = resourceInstanceService.getAllResourceInstancesForLink();
					for (int j = 0; linkResourceInstList != null && j < linkResourceInstList.size(); j++) {
						ResourceInstance ri = linkResourceInstList.get(j);
						if(ri.getLifeState() == InstanceLifeStateEnum.MONITORED){
							resourceIdList.add(String.valueOf(ri.getId()));
						}
					}
					if(!resourceIdList.isEmpty()){
						queryDetail.setSourceIDes(resourceIdList);
					}
				}
			} catch (Exception e) {
				logger.error("getChildrenResource ", e); 
				result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMainResourceError_CODE);
				return JSONObject.toJSONString(result);
			}
			
			queryDetailList.add(queryDetail);
		}
		
		alarmEventQuery.setFilters(queryDetailList);
		
		Page<AlarmEvent, AlarmEventQuery2> page = alarmEventService.findAlarmEvent(alarmEventQuery,  startRow, rowCount);
		
		Map<String,Object> pageMap=new HashMap<String,Object> ();
		pageMap.put("alermList", page.getDatas());
		pageMap.put("totalRecord", page.getTotalRecord());
		result.setData(pageMap);
		result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
		
		return JSONObject.toJSONString(result);
		
	}

	@Override
	public String getNotRecoveredAlarmByInstanceId(String instanceId) {
		// TODO Auto-generated method stub
		AlarmEventQueryDetail detail=new AlarmEventQueryDetail();
		AlarmEventQuery2 AlarmEventQuery2 = new AlarmEventQuery2();
		List<String> numlist = new ArrayList<String>();
		numlist.add(instanceId);
		detail.setSourceIDes(numlist);
		detail.setRecovered(false);
		List<AlarmEventQueryDetail> ListAlarmEvent = new ArrayList<AlarmEventQueryDetail>();
		ListAlarmEvent.add(detail);
		AlarmEventQuery2.setFilters(ListAlarmEvent);
		List<AlarmEvent> aeList = alarmEventService.findAlarmEvent(AlarmEventQuery2);
		Result result = new Result();

		if (null != aeList && aeList.size() > 0) {
			List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
			for (AlarmEvent ae : aeList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(ae.getEventID()));
				map.put("alarmState", ae.getLevel().name());
				map.put("content", ae.getContent());
				map.put("ip", null == ae.getSourceIP() ? "" : ae.getSourceIP());
				map.put("resourceType", ae.getExt2());
				map.put("collectionTime",
						String.valueOf(ae.getCollectionTime().getTime()));
				map.put("alarmCollectionSource", ae.getSourceName());
				mapList.add(map);
			}

			result.setData(mapList);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE);
			return JSONObject.toJSONString(result);
		} else {
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE);
			return JSONObject.toJSONString(result);
		}
	}
}
