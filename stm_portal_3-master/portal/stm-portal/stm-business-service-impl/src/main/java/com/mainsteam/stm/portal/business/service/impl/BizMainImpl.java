package com.mainsteam.stm.portal.business.service.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2.OrderField;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
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
import com.mainsteam.stm.home.screen.api.ITopoApi;
import com.mainsteam.stm.home.screen.bo.Biz;
import com.mainsteam.stm.instancelib.CompositeInstanceService;
import com.mainsteam.stm.instancelib.CustomModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CompositeInstance;
import com.mainsteam.stm.instancelib.obj.CompositeProp;
import com.mainsteam.stm.instancelib.obj.CustomModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.platform.file.bean.FileConstantEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizAlarmInfoApi;
import com.mainsteam.stm.portal.business.api.BizCanvasApi;
import com.mainsteam.stm.portal.business.api.BizCapMetricApi;
import com.mainsteam.stm.portal.business.api.BizEditApi;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.api.BizUserRelApi;
import com.mainsteam.stm.portal.business.bo.BizAlarmInfoBo;
import com.mainsteam.stm.portal.business.bo.BizBandwidthCapacityMetric;
import com.mainsteam.stm.portal.business.bo.BizCalculateCapacityMetric;
import com.mainsteam.stm.portal.business.bo.BizCanvasAutoBuildDataBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasLinkBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasNodeBo;
import com.mainsteam.stm.portal.business.bo.BizDatabaseCapacityMetric;
import com.mainsteam.stm.portal.business.bo.BizHealthHisBo;
import com.mainsteam.stm.portal.business.bo.BizInstanceNodeBo;
import com.mainsteam.stm.portal.business.bo.BizMainBo;
import com.mainsteam.stm.portal.business.bo.BizMainDataBo;
import com.mainsteam.stm.portal.business.bo.BizMetricDataBo;
import com.mainsteam.stm.portal.business.bo.BizMetricHistoryDataBo;
import com.mainsteam.stm.portal.business.bo.BizMetricHistoryValueBo;
import com.mainsteam.stm.portal.business.bo.BizNodeMetricRelBo;
import com.mainsteam.stm.portal.business.bo.BizResourceInstance;
import com.mainsteam.stm.portal.business.bo.BizStatusDefineParameter;
import com.mainsteam.stm.portal.business.bo.BizStoreCapacityMetric;
import com.mainsteam.stm.portal.business.dao.IBizAlarmInfoDao;
import com.mainsteam.stm.portal.business.dao.IBizCanvasDao;
import com.mainsteam.stm.portal.business.dao.IBizHealthHisDao;
import com.mainsteam.stm.portal.business.dao.IBizMainDao;
import com.mainsteam.stm.portal.business.report.api.BizSerReportListenerEngine;
import com.mainsteam.stm.portal.business.report.obj.BizSerMetric;
import com.mainsteam.stm.portal.business.report.obj.BizSerMetricEnum;
import com.mainsteam.stm.portal.business.report.obj.BizSerReport;
import com.mainsteam.stm.portal.business.report.obj.BizSerReportEvent;
import com.mainsteam.stm.portal.business.service.util.BizMetricDefine;
import com.mainsteam.stm.portal.business.service.util.BizNodeTypeDefine;
import com.mainsteam.stm.portal.business.service.util.BizStatusDefine;
import com.mainsteam.stm.portal.business.uitl.BizAlarmParameterDefine;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.topo.api.IMacApi;
import com.mainsteam.stm.util.UnitTransformUtil;
import com.mainsteam.stm.util.Util;

public class BizMainImpl implements BizMainApi,ITopoApi{

	@Resource
	private IBizMainDao bizMainDao;
	
	@Resource
	private BizEditApi bizEditApi;
	
	@Resource
	private IBizHealthHisDao bizHealthHisDao;
	
	@Resource
	private AlarmEventService alarmEventService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private IResourceApi stm_system_resourceApi;
	
	@Resource
	private IMacApi macApi;
	
	@Resource
	private InstanceStateService instanceStateService;
	
	@Resource
	private CompositeInstanceService compositeInstanceService;
	
	@Resource
	private BizCanvasApi bizCanvasApi;
	
	@Resource
	private IBizCanvasDao bizCanvasDao;
	
	@Resource
	private IBizAlarmInfoDao protalBizAlarmInfoDao;
	
	@Resource
	private BizAlarmInfoApi bizAlarmInfoApi;
	
	@Resource
	private IUserApi stm_system_userApi;
	
	@Resource
	private AlarmService alarmService;
	
	@Resource
	private BizCapMetricApi bizCapMetricApi;
	
	@Resource
	private MetricDataService metricDataService;
	
	@Resource
	private BizUserRelApi bizUserRelApi;
	
	@Resource
	private MetricStateService metricStateService;
	
	@Resource
	private ProfileService profileService;
	
	@Resource
	private BizSerReportListenerEngine bizSerReportListenerEngine;
	
	@Resource
	private CustomMetricService customMetricService;
	
	@Resource
	private CustomModulePropService customModulePropService;
	
	@Resource
	private MetricSummaryService  metricSummaryService;
	
	private static final Log logger = LogFactory.getLog(BizMainImpl.class);
	
	private final String STATUS_PARAMETER_BUSINESS_NAME = "????????????";

	/**
	 * ????????????????????????(????????????)
	 * @param bo
	 * @return
	 */
	@Override
	public long insertBasicInfo(BizMainBo bo) {
		// TODO Auto-generated method stub
		
		if(bizMainDao.checkBizNameIsExsit(bo.getName(), null) > 0){
			//?????????????????????
			return -1;
		}
		
		//????????????????????????
		CompositeInstance compositeInstance = new CompositeInstance();
		compositeInstance.setInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);
		compositeInstance.setName(bo.getName());
		
		List<CompositeProp> props = new ArrayList<CompositeProp>();
		compositeInstance.setProps(props);
		
		long newId = -1;
		
		try {
			newId = compositeInstanceService.addCompositeInstance(compositeInstance);
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
		}
		
		//????????????????????????
		BizAlarmInfoBo infoBo = new BizAlarmInfoBo();
		infoBo.setBizId(newId);
		infoBo.setDeathAlarmContent("${??????????????????}???????????????????????????${??????????????????}????????????????????????${??????????????????}??????????????????????????????${??????????????????}");
		infoBo.setDeathThreshold("20");
		infoBo.setSeriousAlarmContent("${??????????????????}?????????${?????????}?????????????????????${??????????????????}????????????????????????${??????????????????}??????????????????????????????${??????????????????}");
		infoBo.setSeriousThreshold("50");
		infoBo.setWarnAlarmContent("${??????????????????}?????????${?????????}?????????????????????${??????????????????}????????????????????????${??????????????????}??????????????????????????????${??????????????????}");
		infoBo.setWarnThreshold("80");
		infoBo.setNormalContent("${??????????????????}????????????????????????${?????????}???");
		bizAlarmInfoApi.insertBizAlarmInfo(infoBo);
		
		//??????????????????????????????
		BizCanvasBo canvas = new BizCanvasBo();
		canvas.setBizId(newId);
		bizCanvasDao.insertCanvasInfo(canvas);
		
		//??????????????????????????????
		BizCanvasNodeBo canvasNode = new BizCanvasNodeBo();
		canvasNode.setInstanceId(newId);
		canvasNode.setBizId(newId);
		canvasNode.setNodeType(BizNodeTypeDefine.BUSINESS_TYPE);
		canvasNode.setShowName(bo.getName());
		if(bo.getFileId() <= 0){
			bo.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_MAIN_IMG_1.getFileId());
		}
		canvasNode.setFileId(bo.getFileId());
		canvasNode.setNodeStatus(0);
		
		bizCanvasApi.insertCanvasNode(canvasNode);
		
		bo.setId(newId);
		bizMainDao.insertBasicInfo(bo);
		
		return newId;
		
	}

	/**
	 * ???????????????????????????????????????
	 * @param id
	 * @return
	 */
	@Override
	public BizMainBo getBasicInfo(long id) {
		// TODO Auto-generated method stub
		return bizMainDao.getBasicInfo(id);
	}
	
	/**
	 * ???????????????????????????????????????
	 * @param id
	 * @return
	 */
	@Override
	public String getCanvasStatusDefine(long id) {
		// TODO Auto-generated method stub
		
		String statusDefine = bizMainDao.getCanvasStatusDefine(id);
		
		if(statusDefine == null || statusDefine.equals("") || statusDefine.trim().equals("")){
			return statusDefine;
		}
		
		String patternParameter = "\\$\\{.*?\\}";
		Pattern pattern = Pattern.compile(patternParameter);
		
		Matcher matcher = pattern.matcher(statusDefine);
		while (matcher.find()) {
			long bizNodeId = Long.parseLong(matcher.group(0).replace("${", "").replace("}", ""));
			statusDefine = statusDefine.replace(bizNodeId + "",bizCanvasApi.getCanvasNodeById(bizNodeId).getShowName());
		}
		
		return statusDefine;
	}

	private Page<BizMainBo, Object> getPageListOrderHealthScore(ILoginUser user, int startNum, int pageSize){
		List<BizMainBo> bizAllMainList = bizMainDao.getAllList();
		
		Page<BizMainBo, Object> page = new Page<BizMainBo, Object>();
		
		if(bizAllMainList == null || bizAllMainList.size() <= 0){
			page.setTotalRecord(0);
			page.setDatas(bizAllMainList);
			return page;
		}
		
		List<BizMainBo> bizMainList = new ArrayList<BizMainBo>();
		
		for(BizMainBo mainBo : bizAllMainList){
			//????????????
			if(!bizUserRelApi.checkUserView(user.getId(), mainBo.getId())){
				continue;
			}
			BizHealthHisBo health = bizHealthHisDao.getBizHealthHis(mainBo.getId());
			if(health == null){
				mainBo.setHealth(100);
				mainBo.setStatus(BizStatusDefine.NORMAL_STATUS);
			}else{
				mainBo.setHealth(health.getBizHealth());
				mainBo.setStatus(health.getBizStatus());
			}
			bizMainList.add(mainBo);
		}
		
		//??????????????????
		Collections.sort(bizMainList, new Comparator<BizMainBo>() {
			@Override
			public int compare(BizMainBo o1, BizMainBo o2) {
				// TODO Auto-generated method stub
				if(o1.getHealth() > o2.getHealth()){
					return 1;
				}else if(o1.getHealth() < o2.getHealth()){
					return -1;
				}else{
					return 0;
				}
			}
		});
		
		page.setTotalRecord(bizMainList.size());
		
		//????????????
		if((startNum + pageSize) > bizMainList.size()){
			if(startNum <= bizMainList.size()){
				bizMainList = bizMainList.subList(startNum, bizMainList.size());
			}
		}else{
			bizMainList = bizMainList.subList(startNum, (startNum + pageSize));
		}
		
		page.setDatas(bizMainList);
		
		return page;
	}
	
	/**
	 * ????????????????????????(????????????)
	 * @return
	 */
	@Override
	public Page<BizMainBo, Object> getPageListForSummary(ILoginUser user, int startNum,
			int pageSize) {
		
		Page<BizMainBo, Object> bizMainList = getPageListOrderHealthScore(user,startNum,pageSize);
		
		return bizMainList;
	}
	
	/**
	 * ????????????top5????????????(????????????)
	 * @return
	 */
	@Override
	public List<BizMetricDataBo> getTopFiveResponseTime(ILoginUser user) {
		
		List<BizMetricDataBo> metricDataList = new ArrayList<BizMetricDataBo>();
		
		List<BizMainBo> bizMainList = bizMainDao.getAllList();
		
		if(bizMainList == null || bizMainList.size() <= 0){
			return metricDataList;
		}
		
		
		for(BizMainBo mainBo : bizMainList){
			
			//????????????
			if(!bizUserRelApi.checkUserView(user.getId(), mainBo.getId())){
				continue;
			}
			
			BizMetricDataBo metricData = new BizMetricDataBo();
			metricData.setBizId(mainBo.getId());
			metricData.setBizName(mainBo.getName());
			
			//??????????????????
			List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(mainBo.getId(), BizMetricDefine.RESPONSE_TIME);
			
			if(capMetricList == null){
				capMetricList = new ArrayList<Long>();
			}
			
			Set<Long> urlList = bizCanvasApi.getResponeTimeMetricInstanceList(mainBo.getId());
			
			if(urlList == null || urlList.size() <= 0){
				metricData.setResponseTime("0");
			}else{
				
				List<Double> datas = new ArrayList<Double>();
				
				for(long urlId : urlList){
					if(capMetricList.contains(urlId)){
						continue;
					}
					
					MetricRealtimeDataQuery metricRealtimeDataQuery = new MetricRealtimeDataQuery();
					metricRealtimeDataQuery.setInstanceID(new long[]{urlId});
					metricRealtimeDataQuery.setMetricID(new String[]{BizMetricDefine.RESPONSE_TIME});
					
					Page<Map<String,?>,MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(metricRealtimeDataQuery, 0, 1);
					List<Map<String,?>> realTimeDataList = page.getDatas();
					
					if(realTimeDataList == null || realTimeDataList.size() <= 0){
						continue;
					}
					
					Map<String,?> map =	realTimeDataList.get(0);
					
					if(map.get(BizMetricDefine.RESPONSE_TIME) == null){
						continue;
					}
					
					datas.add(Double.parseDouble(map.get(BizMetricDefine.RESPONSE_TIME).toString()));
				}
				
				if(datas.size() <= 0){
					metricData.setResponseTime("0");
				}else{
					Collections.sort(datas);
					metricData.setResponseTime(datas.get(datas.size() - 1) + "");
				}
				
			}
			
			
			metricDataList.add(metricData);
		}
		
		//??????????????????
		Collections.sort(metricDataList, new Comparator<BizMetricDataBo>() {
			
			@Override
			public int compare(BizMetricDataBo o1, BizMetricDataBo o2) {
				if(Double.parseDouble(o1.getResponseTime()) < Double.parseDouble(o2.getResponseTime())){
					return 1;
				}else if(Double.parseDouble(o1.getResponseTime()) > Double.parseDouble(o2.getResponseTime())){
					return -1;
				}else{
					return 0;
				}
			}
		});
		
		//????????????
		if(5 > metricDataList.size()){
			metricDataList = metricDataList.subList(0, metricDataList.size());
		}else{
			metricDataList = metricDataList.subList(0, 5);
		}
		
		return metricDataList;
	}
	
	/**
	 * ????????????????????????(????????????)
	 * @return
	 */
	@Override
	public List<BizMetricDataBo> getBizAlarmCount(ILoginUser user,Date startTime,Date endTime) {
		
		List<BizMetricDataBo> metricDataList = new ArrayList<BizMetricDataBo>();
		
		List<BizMainBo> bizMainList = bizMainDao.getAllList();
		
		if(bizMainList == null || bizMainList.size() <= 0){
			return metricDataList;
		}
		
		for(BizMainBo mainBo : bizMainList){
			
			if(!bizUserRelApi.checkUserView(user.getId(), mainBo.getId())){
				//????????????
				continue;
			}
			
			BizMetricDataBo metricData = new BizMetricDataBo();
			metricData.setBizId(mainBo.getId());
			metricData.setBizName(mainBo.getName());
			
			List<String> queryId = new ArrayList<String>();
			queryId.add(mainBo.getId() + "");
			
			AlarmEventQuery2 query = new AlarmEventQuery2();
			AlarmEventQueryDetail queryDetail = new AlarmEventQueryDetail();
			queryDetail.setSysID(SysModuleEnum.BUSSINESS);
			queryDetail.setRecovered(false);
			queryDetail.setSourceIDes(queryId);
			queryDetail.setStart(startTime);
			queryDetail.setEnd(endTime);
			
			List<AlarmEventQueryDetail> filter = new ArrayList<AlarmEventQueryDetail>();
			filter.add(queryDetail);
			
			query.setFilters(filter);
			List<AlarmEvent> eventList = alarmEventService.findAlarmEvent(query);
			
			metricData.setAlarmCount(eventList == null ? 0 : eventList.size());
			
			metricDataList.add(metricData);
		}
		
		return metricDataList;
	}
	
	/**
	 * ????????????????????????????????????(????????????)
	 * @return
	 */
	@Override
	public List<BizMetricDataBo> getPageListRunInfo(ILoginUser user,Date startTime,Date endTime) {
		List<BizMetricDataBo> metricDataList = new ArrayList<BizMetricDataBo>();
		
		List<BizMainBo> bizAllMainList = bizMainDao.getAllList();
		
		if(bizAllMainList == null || bizAllMainList.size() <= 0){
			return metricDataList;
		}
		
		List<BizMainBo> bizMainList = new ArrayList<BizMainBo>();
		
		List<String> idList = new ArrayList<>();
		//???????????????????????????????????????
		for(BizMainBo main : bizAllMainList){
			//????????????
			if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
				continue;
			}
			idList.add(main.getId() + "");
			bizMainList.add(main);
		}
		List<AlarmEvent> eventList = getEventList(idList, startTime, endTime);
		
		
		for(BizMainBo main : bizMainList){
			
			//?????????????????????????????????????????????
			long runTime = 0;
			long createTime = main.getCreateTime().getTime();
			if(endTime.getTime() > createTime){
				if(startTime.getTime() < createTime){
					runTime = (endTime.getTime() - createTime) / 1000;
				}else{
					runTime = (endTime.getTime() - startTime.getTime()) / 1000;
				}
			}
			
			BizMetricDataBo metricData = new BizMetricDataBo();
			metricData.setBizId(main.getId());
			metricData.setBizName(main.getName());
			
			//??????????????????
			//?????????????????????????????????????????????????????????
			long criticalTime = 0;
			
			//?????????????????????????????????????????????????????????
			int criticalCount = 0;
			List<AlarmEvent> curEventList = new ArrayList<AlarmEvent>();
			if(eventList != null && eventList.size() > 0){
				for(AlarmEvent event : eventList){
					if(event.getSourceID().equals(main.getId() + "")){
						curEventList.add(event);
					}
				}
			}
			
			boolean lastIsCritical = false;
			
			if(curEventList != null && curEventList.size() > 0){
				for(int i = 0 ; i < curEventList.size() ; i++){
					AlarmEvent event = curEventList.get(i);
					if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
						if(i > 0){
							AlarmEvent nextEvent = curEventList.get(i - 1);
							criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
						}else{
							criticalTime += (endTime.getTime() - event.getCollectionTime().getTime()) / 1000;
							lastIsCritical = true;
						}
						criticalCount++;
					}
				}
			}
			
			//?????????
			BizHealthHisBo health = bizHealthHisDao.getBizHealthHis(main.getId());
			if(health == null){
				metricData.setHealthScore(100);
			}else{
				metricData.setHealthScore(health.getBizHealth());
			}
			
			int status = 0;
			
			if(health != null){
				status = health.getBizStatus();
			}
			
			metricData.setStatus(status);
			
			if(criticalCount == 0 && status == BizStatusDefine.DEATH_STATUS){
				criticalTime = runTime;
				criticalCount++;
				lastIsCritical = true;
			}
			
			//???????????????
			metricData.setAvailableRate(getAvailableRateMetric(runTime,criticalTime));
			
			//MTBF
			metricData.setMTBF(getMTBFMetric(runTime, criticalTime, lastIsCritical ? criticalCount : criticalCount + 1));
			
			//MTTR
			metricData.setMTTR(getMTTRMetric(criticalTime, criticalCount));
			
			//????????????
			metricData.setOutageTimes(getOutageTimesMetric(criticalCount));
			
			//????????????
			metricData.setDownTime(getDownTimeMetric(criticalTime));
			
			metricDataList.add(metricData);
			
		}
		
		Collections.sort(metricDataList, new Comparator<BizMetricDataBo>() {

			@Override
			public int compare(BizMetricDataBo o1, BizMetricDataBo o2) {
				// TODO Auto-generated method stub
				if(o1.getHealthScore() < o2.getHealthScore()){
					return -1;
				}
				
				if(o1.getHealthScore() > o2.getHealthScore()){
					return 1;
				}
				
				return 0;
				
			}
			
		});
		
		return metricDataList;
	}
	
	/**
	 * ??????????????????????????????(????????????)
	 * @return
	 */
	@Override
	public BizMainDataBo getRunInfoForDetail(long bizId,Date startTime,Date endTime) {
		
		List<String> idList = new ArrayList<>();
		idList.add(bizId + "");
		List<AlarmEvent> eventList = getEventList(idList, startTime, endTime);
		
		BizMainDataBo metricData = new BizMainDataBo();
		
		//??????????????????
		//?????????????????????????????????????????????????????????
		long criticalTime = 0;
		
		//?????????????????????????????????????????????????????????
		//?????????????????????????????????????????????????????????
		int criticalCount = 0;
		List<AlarmEvent> curEventList = new ArrayList<AlarmEvent>();
		if(eventList != null && eventList.size() > 0){
			for(AlarmEvent event : eventList){
				if(event.getSourceID().equals(bizId + "")){
					curEventList.add(event);
				}
			}
		}
		
		boolean lastIsCritical = false;
		
		if(curEventList != null && curEventList.size() > 0){
			for(int i = 0 ; i < curEventList.size() ; i++){
				AlarmEvent event = curEventList.get(i);
				if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
					if(i > 0){
						AlarmEvent nextEvent = curEventList.get(i - 1);
						criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
					}else{
						criticalTime += (endTime.getTime() - event.getCollectionTime().getTime()) / 1000;
						lastIsCritical = true;
					}
					criticalCount++;
				}
			}
		}
		
		Date createTime = bizMainDao.getCreateTime(bizId);
		
		//?????????????????????????????????????????????
		long runTime = 0;
		if(endTime.getTime() > createTime.getTime()){
			if(startTime.getTime() < createTime.getTime()){
				runTime = (endTime.getTime() - createTime.getTime()) / 1000;
			}else{
				runTime = (endTime.getTime() - startTime.getTime()) / 1000;
			}
		}
		
		//??????????????????????????????
		metricData.setTotalRunTime(createTime.getTime() + "");
		
		BizHealthHisBo hisBo = bizHealthHisDao.getBizHealthHis(bizId);
		
		if(hisBo == null){
			//??????
			metricData.setBizStatus(0);
			
			//?????????
			metricData.setHealthScore(100);
		}else{
			//??????
			metricData.setBizStatus(hisBo.getBizStatus());
			
			//?????????
			metricData.setHealthScore(hisBo.getBizHealth());
		}
		
		if(criticalCount == 0 && metricData.getBizStatus() == BizStatusDefine.DEATH_STATUS){
			criticalTime = runTime;
			criticalCount++;
			lastIsCritical = true;
		}
		
		//???????????????
		metricData.setAvailableRate(getAvailableRateMetric(runTime,criticalTime));
		
		//MTBF
		metricData.setMTBF(getMTBFMetric(runTime, criticalTime, lastIsCritical ? criticalCount : criticalCount + 1));
		
		//MTTR
		metricData.setMTTR(getMTTRMetric(criticalTime, criticalCount));
		
		//????????????
		metricData.setOutageTimes(getOutageTimesMetric(criticalCount));
		
		//????????????
		metricData.setDownTime(getDownTimeMetric(criticalTime));
			
		return metricData;
	}
	
	/**
	 * ????????????????????????(????????????)
	 * @return
	 */
	@Override
	public Page<BizCalculateCapacityMetric, Object> getCalculateCapacityInfo(ILoginUser user,int startNum,int pageSize) {
		
		Page<BizCalculateCapacityMetric, Object> page = new Page<BizCalculateCapacityMetric, Object>();
		
		List<BizCalculateCapacityMetric> capacityMetric = new ArrayList<BizCalculateCapacityMetric>();
		
		List<BizMainBo> bizAllMainList = bizMainDao.getAllList();
		
		List<BizMainBo> bizMainList = new ArrayList<BizMainBo>();
		
		//????????????.....
		for(BizMainBo main : bizAllMainList){
			if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
				continue;
			}
			bizMainList.add(main);
		}
		
		if(bizMainList == null || bizMainList.size() <= 0){
			page.setTotalRecord(0);
			page.setDatas(capacityMetric);
			return page;
		}
		
		page.setTotalRecord(bizMainList.size());
		
		for(BizMainBo main : bizMainList){
			
			BizCalculateCapacityMetric metricData = new BizCalculateCapacityMetric();
			metricData.setBizId(main.getId());
			metricData.setBizName(main.getName());
			
			//??????????????????
			getCalculateCapacity(metricData,main.getId());
			
			capacityMetric.add(metricData);
			
		}
		
		Collections.sort(capacityMetric, new Comparator<BizCalculateCapacityMetric>() {

			@Override
			public int compare(BizCalculateCapacityMetric o1, BizCalculateCapacityMetric o2) {
				// TODO Auto-generated method stub
				if(Double.parseDouble(o1.getCpuRate()) > Double.parseDouble(o2.getCpuRate())){
					return -1;
				}
				
				if(Double.parseDouble(o1.getCpuRate()) < Double.parseDouble(o2.getCpuRate())){
					return 1;
				}
				
				return 0;
				
			}
			
		});
		
		//????????????
		if((startNum + pageSize) > capacityMetric.size()){
			capacityMetric = capacityMetric.subList(startNum, capacityMetric.size());
		}else{
			capacityMetric = capacityMetric.subList(startNum, (startNum + pageSize));
		}
		
		page.setDatas(capacityMetric);
		
		return page;
	}
	
	private void getCalculateCapacity(BizCalculateCapacityMetric metricData,long bizId){
		
		//??????????????????
		List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(bizId, BizMetricDefine.HOST_CAPACITY);
		
		if(capMetricList == null){
			capMetricList = new ArrayList<Long>();
		}
		
		Set<Long> urlList = bizCanvasApi.getCalculateMetricInstanceList(bizId);
		
		if(urlList == null || urlList.size() <= 0){
			metricData.setCpuRate("0");
		}else{
			
			List<Double> datas = new ArrayList<Double>();
			
			for(long urlId : urlList){
				if(capMetricList.contains(urlId)){
					continue;
				}
				InstanceStateData state = instanceStateService.getStateAdapter(urlId);
				if(state != null && state.getState().equals(InstanceStateEnum.CRITICAL)){
					continue;
				}
				MetricData instanceMetricData = metricDataService.getMetricPerformanceData(urlId, MetricIdConsts.METRIC_CPU_RATE);
				if(instanceMetricData == null){
					continue;
				}
				String[] value = instanceMetricData.getData();
				if(value != null && value.length > 0){
					if(value[0] != null && !value[0].equals("")){
						datas.add(Double.parseDouble(value[0]));
					}
				}
			}
			
			if(datas.size() <= 0){
				metricData.setCpuRate("0");
			}else{
				Collections.sort(datas);
				metricData.setCpuRate(datas.get(datas.size() - 1) + "");
			}
			
		}
		
	}
	
	/**
	 * ????????????????????????(????????????)
	 * @return
	 */
	@Override
	public Page<BizStoreCapacityMetric, Object> getStoreCapacityInfo(ILoginUser user,int startNum,int pageSize) {
		
		Page<BizStoreCapacityMetric, Object> page = new Page<BizStoreCapacityMetric, Object>();
		
		List<BizStoreCapacityMetric> capacityMetric = new ArrayList<BizStoreCapacityMetric>();
		
		List<BizMainBo> bizAllMainList = bizMainDao.getAllList();

		List<BizMainBo> bizMainList = new ArrayList<BizMainBo>();
		
		//????????????.....
		for(BizMainBo main : bizAllMainList){
			if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
				continue;
			}
			bizMainList.add(main);
		}
		
		if(bizMainList == null || bizMainList.size() <= 0){
			page.setDatas(capacityMetric);
			page.setTotalRecord(0);
			return page;
		}
		
		page.setTotalRecord(bizMainList.size());
		
		for(BizMainBo main : bizMainList){
			
			BizStoreCapacityMetric metricData = new BizStoreCapacityMetric();
			metricData.setBizId(main.getId());
			metricData.setBizName(main.getName());
			
			//??????????????????
			getStoreCapacity(metricData,main.getId());
			
			capacityMetric.add(metricData);
			
		}
		
		Collections.sort(capacityMetric, new Comparator<BizStoreCapacityMetric>() {

			@Override
			public int compare(BizStoreCapacityMetric o1, BizStoreCapacityMetric o2) {
				// TODO Auto-generated method stub
				if(Double.parseDouble(o1.getUseRate()) > Double.parseDouble(o2.getUseRate())){
					return -1;
				}
				
				if(Double.parseDouble(o1.getUseRate()) < Double.parseDouble(o2.getUseRate())){
					return 1;
				}
				
				return 0;
				
			}
			
		});
		
		//????????????
		if((startNum + pageSize) > capacityMetric.size()){
			capacityMetric = capacityMetric.subList(startNum, capacityMetric.size());
		}else{
			capacityMetric = capacityMetric.subList(startNum, (startNum + pageSize));
		}
		
		page.setDatas(capacityMetric);
		
		return page;
	}
	
	private void getStoreCapacity(BizStoreCapacityMetric metricData,long bizId){
		
		double totalSize = 0;
		double alreadyUseSize = 0;
		
		//??????????????????
		List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(bizId, BizMetricDefine.STORAGE_CAPACITY);
		
		if(capMetricList == null){
			capMetricList = new ArrayList<Long>();
		}
		
		Set<Long> urlList = bizCanvasApi.getStoreMetricInstanceList(bizId);
		
		if(urlList == null || urlList.size() <= 0){
			metricData.setTotalStore("0MB");
			metricData.setUseStore("0MB");
			metricData.setUseRate("0");
		}else{
			
			for(long urlId : urlList){
				
				if(capMetricList.contains(urlId)){
					continue;
				}
				
				ResourceInstance instance = null;
				try {
					instance = resourceInstanceService.getResourceInstance(urlId);
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
				
				//??????????????????????????????????????????
				if(instance.getChildType() != null && instance.getChildType().equals("Partition")){
					//??????
					totalSize += getStoreCapacityValue(instance, "fileSysTotalSize");
					alreadyUseSize += getStoreCapacityValue(instance, "fileSysUsedSize");
				}else if(instance.getCategoryId().equals("VirtualStorage")){
					//vm??????
					totalSize += getStoreCapacityValue(instance, "DataStorageVolume");
					alreadyUseSize += getStoreCapacityValue(instance, "DataStorageUsedSpace");
				}else if(instance.getCategoryId().equals("XenSRs")){
					//xen??????
					totalSize += getStoreCapacityValue(instance, "physicalSize");
					alreadyUseSize += getStoreCapacityValue(instance, "physicalUtilisation");
				}else if(instance.getCategoryId().equals("FusionComputeDataStores")){
					//????????????
					totalSize += getStoreCapacityValue(instance, "physicalSize");
					alreadyUseSize += getStoreCapacityValue(instance, "physicalUtilisation");
				}
				
			}				

			
		}
		
		DecimalFormat df = new DecimalFormat("0.00");
		metricData.setTotalStore(transUnit(totalSize,df));
		if(alreadyUseSize != 0){
			metricData.setUseStore(transUnit(alreadyUseSize,df));
			metricData.setUseRate(df.format((alreadyUseSize / totalSize) * 100) + "");
		}else{
			metricData.setUseStore("0MB");
			metricData.setUseRate("0");
		}
		
	}
	
	private String transUnit(double value,DecimalFormat df){
		if(value > 1024D * 1024D * 1024D){
			return df.format(value / (1024D * 1024D * 1024D)) + "PB";
		}else if(value > 1024D * 1024D){
			return df.format(value / (1024D * 1024D)) + "TB";
		}else if(value > 1024D){
			return df.format(value / (1024D)) + "GB";
		}
		return df.format(value) + "MB";
	}
	
	private double getStoreCapacityValue(ResourceInstance instance,String metricId){
		ResourceMetricDef metricDef = capacityService.getResourceMetricDef(instance.getResourceId(), metricId);
		String unit = "MB";
		if(metricDef != null && metricDef.getUnit() != null && !metricDef.getUnit().equals("")){
			unit = metricDef.getUnit();
		}
		MetricData sizeMetricData = metricDataService.getMetricInfoData(instance.getId(), metricId);
		if(sizeMetricData == null){
			return 0;
		}
		String[] value = sizeMetricData.getData();
		if(value != null && value.length > 0){
			if(value[0] != null && !value[0].equals("")){
				return transUnitToMB(unit, Double.parseDouble(value[0]));
			}
		}
		
		return 0;
	}
	
	private double transUnitToMB(String unit,double value){
		switch (unit) {
		case "Byte":
			return value / (1024D * 1024D);
		case "KB":
			return value / 1024D;
		case "MB":
			return value;
		case "GB":
			return value * 1024D;
		case "TB":
			return value * 1024D * 1024D;
		default:
			return value;
		}
	}
	
	/**
	 * ???????????????????????????(????????????)
	 * @return
	 */
	@Override
	public Page<BizDatabaseCapacityMetric, Object> getDatabaseCapacityInfo(ILoginUser user,int startNum,int pageSize) {
		
		Page<BizDatabaseCapacityMetric, Object> page = new Page<BizDatabaseCapacityMetric, Object>();
		
		List<BizDatabaseCapacityMetric> capacityMetric = new ArrayList<BizDatabaseCapacityMetric>();
		
		List<BizMainBo> bizAllMainList = bizMainDao.getAllList();
		//????????????.....
		List<BizMainBo> bizMainList = new ArrayList<BizMainBo>();
		for(BizMainBo main : bizAllMainList){
			if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
				continue;
			}
			bizMainList.add(main);
		}
		
		if(bizMainList == null || bizMainList.size() <= 0){
			page.setDatas(capacityMetric);
			page.setTotalRecord(0);
			return page;
		}
		
		page.setTotalRecord(bizMainList.size());
		
		for(BizMainBo main : bizMainList){
			
			BizDatabaseCapacityMetric metricData = new BizDatabaseCapacityMetric();
			metricData.setBizId(main.getId());
			metricData.setBizName(main.getName());
			
			//?????????????????????
			getDatabaseCapacity(metricData,main.getId());
			
			capacityMetric.add(metricData);
			
		}
		
		Collections.sort(capacityMetric, new Comparator<BizDatabaseCapacityMetric>() {

			@Override
			public int compare(BizDatabaseCapacityMetric o1, BizDatabaseCapacityMetric o2) {
				// TODO Auto-generated method stub
				if(Double.parseDouble(o1.getUseRate()) > Double.parseDouble(o2.getUseRate())){
					return -1;
				}
				
				if(Double.parseDouble(o1.getUseRate()) < Double.parseDouble(o2.getUseRate())){
					return 1;
				}
				
				return 0;
				
			}
			
		});
		
		//????????????
		if((startNum + pageSize) > capacityMetric.size()){
			capacityMetric = capacityMetric.subList(startNum, capacityMetric.size());
		}else{
			capacityMetric = capacityMetric.subList(startNum, (startNum + pageSize));
		}
		
		page.setDatas(capacityMetric);
		
		return page;
	}
	
	private void getDatabaseCapacity(BizDatabaseCapacityMetric metricData,long bizId){
		
		double totalSize = 0;
		double alreadyUseSize = 0;
		
		//?????????????????????
		List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(bizId, BizMetricDefine.DATABASE_CAPACITY);
		
		if(capMetricList == null){
			capMetricList = new ArrayList<Long>();
		}
		
		Set<Long> urlList = bizCanvasApi.getDatabaseMetricInstanceList(bizId);
		
		if(urlList == null || urlList.size() <= 0){
			metricData.setTotalTableSpace("0MB");
			metricData.setUseTableSpace("0MB");
			metricData.setUseRate("0");
		}else{
			
			for(long urlId : urlList){
				
				if(capMetricList.contains(urlId)){
					continue;
				}
				
				MetricData sizeMetricData = metricDataService.getMetricInfoData(urlId, "tableSpaceSize");
				if(sizeMetricData == null){
					continue;
				}
				ResourceMetricDef metricDef = null;
				try {
					metricDef = capacityService.getResourceMetricDef(resourceInstanceService.getResourceInstance(urlId).getResourceId(), "tableSpaceSize");
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
				String[] value = sizeMetricData.getData();
				MetricData utilMetricData = metricDataService.getMetricPerformanceData(urlId, "tableSpaceUtil");
				if(utilMetricData == null){
					continue;
				}
				if(value != null && value.length > 0){
					if(value[0] != null && !value[0].equals("")){
						double size = unitMBTrans(Double.parseDouble(value[0]), metricDef.getUnit());
						totalSize += size;
						String[] utilValue = utilMetricData.getData();
						if(utilValue != null && utilValue.length > 0){
							if(utilValue[0] != null && !utilValue[0].equals("") && !utilValue[0].equals("0.00")){
								alreadyUseSize += size * Double.parseDouble(utilValue[0]) / 100;
							}
						}
					}
				}
			}
			
		}
		
		DecimalFormat df = new DecimalFormat("0.00");
		metricData.setTotalTableSpace(transUnit(totalSize, df));
		metricData.setUseTableSpace(transUnit(alreadyUseSize,df));
		if(alreadyUseSize != 0){
			metricData.setUseRate(df.format((alreadyUseSize / totalSize) * 100) + "");
		}else{
			metricData.setUseRate("0");
		}
		
	}
	
	/**
	 * ???????????????MB
	 * @return
	 */
	private Double unitMBTrans(double value,String unit){
		
		if(unit.equals("KB")){
			return value / 1024;
		}else{
			return value;
		}
		
	}
	
	/**
	 * ????????????????????????(????????????)
	 * @return
	 */
	@Override
	public Page<BizBandwidthCapacityMetric, Object> getBandwidthCapacityInfo(ILoginUser user,int startNum,int pageSize) {
		
		Page<BizBandwidthCapacityMetric, Object> page = new Page<BizBandwidthCapacityMetric, Object>();
		
		List<BizBandwidthCapacityMetric> capacityMetric = new ArrayList<BizBandwidthCapacityMetric>();
		
		List<BizMainBo> bizAllMainList = bizMainDao.getAllList();
		//????????????.....
		List<BizMainBo> bizMainList = new ArrayList<BizMainBo>();
		for(BizMainBo main : bizAllMainList){
			if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
				continue;
			}
			bizMainList.add(main);
		}
		
		if(bizMainList == null || bizMainList.size() <= 0){
			page.setDatas(capacityMetric);
			page.setTotalRecord(0);
			return page;
		}
		
		page.setTotalRecord(bizMainList.size());
		
		for(BizMainBo main : bizMainList){
			
			BizBandwidthCapacityMetric metricData = new BizBandwidthCapacityMetric();
			metricData.setBizId(main.getId());
			metricData.setBizName(main.getName());
			
			//?????????????????????
			getBandwidthCapacity(metricData,main.getId());
			
			capacityMetric.add(metricData);
			
		}
		
		Collections.sort(capacityMetric, new Comparator<BizBandwidthCapacityMetric>() {

			@Override
			public int compare(BizBandwidthCapacityMetric o1, BizBandwidthCapacityMetric o2) {
				// TODO Auto-generated method stub
				if(Double.parseDouble(o1.getUseRate()) > Double.parseDouble(o2.getUseRate())){
					return -1;
				}
				
				if(Double.parseDouble(o1.getUseRate()) < Double.parseDouble(o2.getUseRate())){
					return 1;
				}
				
				return 0;
				
			}
			
		});
		
		//????????????
		if((startNum + pageSize) > capacityMetric.size()){
			capacityMetric = capacityMetric.subList(startNum, capacityMetric.size());
		}else{
			capacityMetric = capacityMetric.subList(startNum, (startNum + pageSize));
		}
		
		page.setDatas(capacityMetric);
		
		return page;
	}
	
	private void getBandwidthCapacity(BizBandwidthCapacityMetric metricData,long bizId){
		
		double totalSize = 0;
		double alreadyUseSize = 0;
		
		//??????????????????
		List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(bizId, BizMetricDefine.BANDWIDTH_CAPACITY);
		
		if(capMetricList == null){
			capMetricList = new ArrayList<Long>();
		}
		
		Set<Long> urlList = bizCanvasApi.getBandwidthMetricInstanceList(bizId);
		
		if(urlList == null || urlList.size() <= 0){
			metricData.setTotalFlow("0bps");
			metricData.setUseFlow("0bps");
			metricData.setUseRate("0");
		}else{
			
			for(long urlId : urlList){
				
				if(capMetricList.contains(urlId)){
					continue;
				}
				
				String[] value = null;
				//?????????????????????????????????????????????
				boolean isHaveDefined = false;
				List<CustomModuleProp> allModuleProp = customModulePropService.getCustomModuleProp();
				if(allModuleProp != null && allModuleProp.size() > 0){
					  for(CustomModuleProp cmp:allModuleProp){
						  if(cmp.getInstanceId() == urlId && cmp.getKey().equals("ifSpeed")){
							  isHaveDefined = true;
							  value = new String[]{cmp.getUserValue()};
							  break;
						  }
					  }
				}
				
				if(!isHaveDefined){
					MetricData sizeMetricData = metricDataService.getMetricInfoData(urlId, "ifSpeed");
					if(sizeMetricData == null){
						continue;
					}
					value = sizeMetricData.getData();
				}
				
				
				
				MetricData utilMetricData = metricDataService.getMetricPerformanceData(urlId, "ifBandWidthUtil");
				if(utilMetricData == null){
					continue;
				}
				if(value != null && value.length > 0){
					if(value[0] != null && !value[0].equals("")){
						double size = Double.parseDouble(value[0]);
						totalSize += size;
						String[] utilValue = utilMetricData.getData();
						if(utilValue != null && utilValue.length > 0){
							if(utilValue[0] != null && !utilValue[0].equals("") && !utilValue[0].equals("0.00")){
								alreadyUseSize += size * Double.parseDouble(utilValue[0]) / 100;
							}
						}
					}
				}
			}
			
		}
		
		DecimalFormat df = new DecimalFormat("0.00");
		metricData.setTotalFlow(UnitTransformUtil.transform(df.format(totalSize), "bps"));
		if(alreadyUseSize != 0){
			metricData.setUseFlow(UnitTransformUtil.transform(df.format(alreadyUseSize), "bps"));
			metricData.setUseRate(df.format((alreadyUseSize / totalSize) * 100) + "");
		}else{
			metricData.setUseFlow("0bps");
			metricData.setUseRate("0");
		}
		
	}

	/**
	 * ????????????????????????(????????????)
	 * @param user ????????????
	 * @param status ????????????
	 * @param startTime ????????????????????????
	 * @param endTime ????????????????????????
	 * @param queryName ??????????????????
	 * @param startNum ??????????????????
	 * @param pageSize ??????????????????
	 * @return
	 */
	@Override
	public Page<BizMainDataBo, Object> getPageListForGrid(ILoginUser user,int status,Date startTime,Date endTime,
			String queryName, int startNum,int pageSize) {
		
		Page<BizMainDataBo, Object> pageResult = new Page<BizMainDataBo, Object>();
		
		List<BizMainDataBo> resultList = new ArrayList<BizMainDataBo>();
		
		List<BizMainBo> bizMainList = bizMainDao.getAllList();
		List<BizMainBo> bizQueryList = new ArrayList<BizMainBo>();
		
		if(bizMainList == null || bizMainList.size() <= 0){
			pageResult.setDatas(resultList);
			pageResult.setTotalRecord(0);
			return pageResult;
		}
		
		for(BizMainBo main : bizMainList){
			//????????????
			if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
				continue;
			}
			
			//????????????
			if(queryName != null && !queryName.equals("") && !(main.getName().toUpperCase().contains(queryName.toUpperCase()))){
				continue;
			}
			
			BizHealthHisBo healthHis = bizHealthHisDao.getBizHealthHis(main.getId());
			int curStatus = 0;
			if(healthHis == null){
				curStatus = 0;
				main.setStatus(0);
				main.setHealth(100);
			}else{
				curStatus = healthHis.getBizStatus();
				main.setStatus(healthHis.getBizStatus());
				main.setHealth(healthHis.getBizHealth());
			}
			
			if(status >= 0){
				//??????????????????
				if(curStatus != status){
					continue;
				}
			}
			
			bizQueryList.add(main);
		}
		
		if(bizQueryList == null || bizQueryList.size() <= 0){
			pageResult.setDatas(resultList);
			pageResult.setTotalRecord(0);
			return pageResult;
		}

		pageResult.setTotalRecord(bizQueryList.size());
		
		//????????????
		if((startNum + pageSize) > bizQueryList.size()){
			bizQueryList = bizQueryList.subList(startNum, bizQueryList.size());
		}else{
			bizQueryList = bizQueryList.subList(startNum, (startNum + pageSize));
		}
		
		List<String> idList = new ArrayList<String>();
		for(BizMainBo main : bizQueryList){
			idList.add(main.getId() + "");
		}
		
		//???????????????????????????????????????
		List<AlarmEvent> eventList = getEventList(idList, startTime, endTime);
		
		for(BizMainBo main : bizQueryList){
			
			resultList.add(getSingleBizRunInfo(eventList,main,startTime,endTime));
			
		}
		
		pageResult.setDatas(resultList);
		
		return pageResult;
	}
	

	/**
	 * ????????????????????????????????????(??????tooltip)
	 * @return
	 */
	@Override
	public BizMainDataBo getRunInfoForTooltip(long bizId) {
		
		BizMainBo main = bizMainDao.getBasicInfo(bizId);
		
		BizHealthHisBo healthHis = bizHealthHisDao.getBizHealthHis(bizId);
		if(healthHis == null){
			main.setStatus(0);
			main.setHealth(100);
		}else{
			main.setStatus(healthHis.getBizStatus());
			main.setHealth(healthHis.getBizHealth());
		}
		
		List<String> idList = new ArrayList<String>();
		idList.add(bizId + "");
		
		Date endTime = new Date();
		Date startTime = new Date(endTime.getTime() - 7 * 24 * 60 * 60 * 1000);
		
		//???????????????????????????????????????
		List<AlarmEvent> eventList = getEventList(idList, startTime, endTime);
		
		return getSingleBizRunInfo(eventList, main, startTime, endTime);
		
	}
	
	private BizMainDataBo getSingleBizRunInfo(List<AlarmEvent> eventList,BizMainBo main,Date startTime,Date endTime){
		
		BizMainDataBo mainData = new BizMainDataBo();
		
		mainData.setBizId(main.getId());
		mainData.setBizName(main.getName());
		mainData.setManagerName(main.getManagerName());
		mainData.setHealthScore(main.getHealth());
		mainData.setBizStatus(main.getStatus());
		
		if(main.getManagerId() > 0){
			mainData.setPhone(stm_system_userApi.get(main.getManagerId()).getMobile());
		}
		
		//?????????????????????????????????????????????
		long runTime = 0; 
		long createTime = main.getCreateTime().getTime();
		if(endTime.getTime() > createTime){
			if(startTime.getTime() < createTime){
				runTime = (endTime.getTime() - createTime) / 1000;
			}else{
				runTime = (endTime.getTime() - startTime.getTime()) / 1000;
			}
		}
		
		//??????????????????
		//?????????????????????????????????????????????????????????
		long criticalTime = 0;
		
		//?????????????????????????????????????????????????????????
		int criticalCount = 0;
		List<AlarmEvent> curEventList = new ArrayList<AlarmEvent>();
		if(eventList != null && eventList.size() > 0){
			for(AlarmEvent event : eventList){
				if(event.getSourceID().equals(main.getId() + "")){
					curEventList.add(event);
				}
			}
		}
		
		boolean lastIsCritical = false;
		
		if(curEventList != null && curEventList.size() > 0){
			for(int i = 0 ; i < curEventList.size() ; i++){
				AlarmEvent event = curEventList.get(i);
				if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
					if(i > 0){
						AlarmEvent nextEvent = curEventList.get(i - 1);
						criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
					}else{
						criticalTime += (endTime.getTime() - event.getCollectionTime().getTime()) / 1000;
						lastIsCritical = true;
					}
					criticalCount++;
				}
			}
		}
		
		if(criticalCount == 0 && main.getStatus() == BizStatusDefine.DEATH_STATUS){
			criticalTime = runTime;
			criticalCount++;
			lastIsCritical = true;
		}
		
		//???????????????
		mainData.setAvailableRate(getAvailableRateMetric(runTime,criticalTime));
		
		//MTBF
		mainData.setMTBF(getMTBFMetric(runTime, criticalTime, lastIsCritical ? criticalCount : criticalCount + 1));
		
		//MTTR
		mainData.setMTTR(getMTTRMetric(criticalTime, criticalCount));
		
		//????????????
		mainData.setOutageTimes(getOutageTimesMetric(criticalCount));
		
		return mainData;
	}
	
	
	//??????????????????????????????????????????
	private List<AlarmEvent> getEventList(List<String> idList,Date startTime,Date endTime){
		
		AlarmEventQuery2 query = new AlarmEventQuery2();
		AlarmEventQueryDetail queryDetail = new AlarmEventQueryDetail();
		queryDetail.setSysID(SysModuleEnum.BUSSINESS);
		queryDetail.setSourceIDes(idList);
		queryDetail.setStart(startTime);
		queryDetail.setEnd(endTime);
		List<AlarmEventQueryDetail> detailList = new ArrayList<AlarmEventQueryDetail>();
		detailList.add(queryDetail);
		query.setFilters(detailList);
		List<AlarmEvent> eventList = alarmEventService.findAlarmEvent(query);
		
		return eventList;
	}
	
	//?????????????????????
	private String getAvailableRateMetric(long runTimeLong,long criticalTimeLong){
		if(runTimeLong == 0){
			return "0%";
		}
		DecimalFormat df = new DecimalFormat("0.00");
		double runTime = runTimeLong;
		double criticalTime = criticalTimeLong;
		return df.format(((runTime - criticalTime) / runTime * 100)) + "%";
	}
	
	//?????????????????????
	private float getAvailableRateMetricValue(long runTimeLong,long criticalTimeLong){
		if(runTimeLong == 0){
			return 0;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		double runTime = runTimeLong;
		double criticalTime = criticalTimeLong;
		return Float.parseFloat(df.format(((runTime - criticalTime) / runTime * 100)));
	}
	
	//??????MTBF
	private String getMTBFMetric(long runTimeLong,long criticalTimeLong,long criticalCountLong){
		if(criticalCountLong == 0){
			return "0??????";
		}
		DecimalFormat df = new DecimalFormat("0.00");
		double runTime = runTimeLong;
		double criticalTime = criticalTimeLong;
		double criticalCount = criticalCountLong;
		return timeUnitTrans(df, (runTime - criticalTime) / criticalCount);
	}
	
	//??????MTBF
	private float getMTBFMetricValue(long runTimeLong,long criticalTimeLong,long criticalCountLong){
		if(criticalCountLong == 0){
			return 0;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		double runTime = runTimeLong;
		double criticalTime = criticalTimeLong;
		double criticalCount = criticalCountLong;
		return Float.parseFloat(df.format((runTime - criticalTime) / criticalCount / (60 * 60 * 24)));
	}
	
	//??????MTTR
	private String getMTTRMetric(long criticalTimeLong,long criticalCountLong){
		if(criticalCountLong == 0){
			return "0(??????)";
		}
		DecimalFormat df = new DecimalFormat("0.00");
		double criticalTime = criticalTimeLong;
		double criticalCount = criticalCountLong;
		return timeUnitTrans(df, criticalTime / criticalCount);
	}
	
	//??????MTTR
	private float getMTTRMetricValue(long criticalTimeLong,long criticalCountLong){
		if(criticalCountLong == 0){
			return 0;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		double criticalTime = criticalTimeLong;
		double criticalCount = criticalCountLong;
		return Float.parseFloat(df.format(criticalTime / criticalCount / (60 * 60)));
	}

	//??????????????????
	private String getOutageTimesMetric(long criticalCount){
		return criticalCount + "";
	}
	
	//??????????????????
	private int getOutageTimesMetricValue(long criticalCount){
		return (int)criticalCount;
	}
	
	//??????????????????
	private String getDownTimeMetric(long criticalTimeLong){
		DecimalFormat df = new DecimalFormat("0.00");
		double criticalTime = criticalTimeLong;
		return timeUnitTrans(df, criticalTime);
	}
	
	//??????????????????
	private float getDownTimeMetricValue(long criticalTimeLong){
		DecimalFormat df = new DecimalFormat("0.00");
		double criticalTime = criticalTimeLong;
		return Float.parseFloat(df.format(criticalTime / (60 * 60)));
	}

	@Override
	public boolean updateBizStatusDefine(BizMainBo main) {
		//???????????????
		int result = bizMainDao.updateBizStatusDefine(main);
		calculateBizHealth(main.getId(), 4, false, null,-1);
		return result > 0;
	}
	
	private String timeUnitTrans(DecimalFormat df,double value){
		
		if(value > 24 * 60 * 60){
			return df.format(value / (24 * 60 * 60)) + "(???)";
		}else if(value > 60 * 60){
			return df.format(value / (60 * 60)) + "(??????)";
		}else if(value > 60){
			return df.format(value / (60)) + "(??????)";
		}else{
			return df.format(value) + "(???)";
		}
		
	}

	/**
	 * IP????????????????????????
	 * @return
	 */
	@Override
	public List<BizResourceInstance> getInstancesByAutoBuild(ILoginUser user,String ip) {

		List<BizResourceInstance> result = new ArrayList<BizResourceInstance>();
		
		try {
			List<ResourceInstanceBo> resourceList = stm_system_resourceApi.getResources(user);
			if(resourceList == null){
				return result;
			}
			
			ResourceInstance hostResource = null;
			
			for(ResourceInstanceBo resourceInstance : resourceList){
				
				if(resourceInstance.getCategoryId().equals(CapacityConst.URL)){
					//url??????
					//url??????????????????????????????url???????????????ip??????
					ResourceInstance instance = resourceInstanceService.getResourceInstance(resourceInstance.getId());
					
					String urls[] = instance.getModulePropBykey("Url");
					
					if(urls != null && urls.length > 0){
						String url = urls[0];
						if(url.contains(ip)){
							BizResourceInstance bizInstance = new BizResourceInstance();
							BeanUtils.copyProperties(resourceInstance, bizInstance);
							bizInstance.setResourceName(CapacityConst.URL);
							result.add(bizInstance);
						}
					}
					
				}else if(resourceInstance.getCategoryId().equals("FTP")){
					
					ResourceInstance instance = resourceInstanceService.getResourceInstance(resourceInstance.getId());
					
					String urls[] = instance.getModulePropBykey("ip");
					
					if(urls != null && urls.length > 0){
						String url = urls[0];
						if(url.contains(ip)){
							BizResourceInstance bizInstance = new BizResourceInstance();
							BeanUtils.copyProperties(resourceInstance, bizInstance);
							bizInstance.setResourceName("FTP");
							result.add(bizInstance);
						}
					}
					
				}else{
					
					if(resourceInstance.getDiscoverIP() != null && resourceInstance.getDiscoverIP().equals(ip)){
						BizResourceInstance bizInstance = new BizResourceInstance();
						BeanUtils.copyProperties(resourceInstance, bizInstance);
						bizInstance.setResourceName(capacityService.getResourceDefById(resourceInstance.getResourceId()).getName());
						
						if(capacityService.getCategoryById(resourceInstance.getCategoryId()).getParentCategory().getId().equals(CapacityConst.HOST)){
							//??????????????????
							hostResource = resourceInstanceService.getResourceInstance(resourceInstance.getId());
						}
						
						result.add(bizInstance);
					}
				}
				
				
			}
			
			//???????????????????????????????????????
			
			//1.???????????????????????????????????????
			List<String> ips = macApi.getUpdeviceIpsByHostIp(ip);
			
			if(ips == null || ips.size() <= 0){
				
				
				if(hostResource != null){
					//2.??????????????????????????????mac??????????????????????????????
					String[] macAddress = hostResource.getModulePropBykey(MetricIdConsts.METRIC_MACADDRESS);
					if(macAddress == null || macAddress.length <= 0){
						logger.debug("Don't find the up network in host : " + ip);
						return result;
					}
					ips = macApi.getUpdeviceIpsByHostMac(macAddress[0]);
				}
				
			}
			
			if(ips != null && ips.size() > 0){
				//??????ip????????????????????????????????????
				for(ResourceInstanceBo instance : resourceList){
					for(String singleIp : ips){
						if(instance.getDiscoverIP() != null && instance.getDiscoverIP().equals(singleIp) && 
								capacityService.getCategoryById(instance.getCategoryId()).getParentCategory().getId().equals(CapacityConst.NETWORK_DEVICE)){
							BizResourceInstance bizInstance = new BizResourceInstance();
							BeanUtils.copyProperties(instance, bizInstance);
							bizInstance.setResourceName(capacityService.getResourceDefById(instance.getResourceId()).getName());
							result.add(bizInstance);
							break;
						}
					}
				}
			}
			
			
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
		}

		return result;
	}

	/**
	 * ????????????????????????
	 */
	@Override
	public List<BizResourceInstance> getInstancesByManualBuild(ILoginUser user,String searchContent) {
		
		List<BizResourceInstance> resultList = new ArrayList<BizResourceInstance>();
		
		List<ResourceInstanceBo> resourcesList = stm_system_resourceApi.getResources(user);
		
		if(resourcesList == null || resourcesList.size() <= 0){
			return resultList;
		}
		
		if(searchContent == null || searchContent.trim().equals("")){
			return resultList;
		}
		
		for(ResourceInstanceBo bo : resourcesList){
			
			boolean isAddInstance = false;
			
			if(bo.getDiscoverIP() != null && bo.getDiscoverIP().contains(searchContent)){
				isAddInstance = true;
			}
			
			if(bo.getShowName() != null && bo.getShowName().toUpperCase().contains(searchContent.toUpperCase())){
				isAddInstance = true;
			}
			
			if(isAddInstance){
				BizResourceInstance bizInstance = new BizResourceInstance();
				BeanUtils.copyProperties(bo, bizInstance);
				bizInstance.setResourceName(capacityService.getResourceDefById(bo.getResourceId()).getName());
				resultList.add(bizInstance);
			}
			
		}
		
		return resultList;
	}

	/**
	 * ??????????????????????????????
	 */
	@Override
	public List<BizMainBo> getBussinessByManualBuild(ILoginUser user,long bizId,String searchContent) {
		List<BizMainBo> bizAllMainList = bizMainDao.getBizListForSearch(searchContent);
		List<BizMainBo> resultList= new ArrayList<BizMainBo>();
		
		//????????????....
		List<BizMainBo> bizList = new ArrayList<BizMainBo>();
		for(BizMainBo main : bizAllMainList){
			if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
				continue;
			}
			bizList.add(main);
		}
		
		//?????????????????????(????????????)??????????????????????????????
		List<BizCanvasNodeBo> loopNode = new ArrayList<BizCanvasNodeBo>();
		List<BizCanvasNodeBo> nodeList = bizCanvasApi.getCanvasNode(bizId);
		if(nodeList != null && nodeList.size() > 0){
			out : for(BizMainBo bo : bizList){
				for(BizCanvasNodeBo node : nodeList){
					if(node.getInstanceId() == node.getBizId()){
						continue;
					}
					if(bo.getId() == node.getInstanceId() && node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE){
						continue out;
					}
				}
				BizCanvasNodeBo node = new BizCanvasNodeBo();
				node.setInstanceId(bo.getId());
				node.setShowName(bo.getName());
				node.setAttr(bo.getRemark());
				loopNode.add(node);
			}
		}
		
		//??????????????????????????????????????????
		checkNodeIsLoop(loopNode,bizId);
		for(BizCanvasNodeBo node : loopNode){
			if(node.getNodeStatus() != -2){
				BizMainBo main = new BizMainBo();
				main.setId(node.getInstanceId());
				main.setName(node.getShowName());
				main.setRemark(node.getAttr());
				resultList.add(main);
			}
		}
		
		return resultList;
	}

	/**
	 * ???????????????????????????????????????
	 * @param bizId
	 * @param instanceIds
	 * @return
	 */
	@Override
	public BizCanvasAutoBuildDataBo autoBuildBussiness(long bizId, Long[] instanceIds) {

		BizCanvasAutoBuildDataBo data = new BizCanvasAutoBuildDataBo();
		
		List<List<BizCanvasNodeBo>> canvasNodeList = new ArrayList<List<BizCanvasNodeBo>>();
		List<BizCanvasLinkBo> canvasLinkList = new ArrayList<BizCanvasLinkBo>();
		
		List<BizCanvasNodeBo> firstRow = new ArrayList<BizCanvasNodeBo>();
		List<BizCanvasNodeBo> secondRow = new ArrayList<BizCanvasNodeBo>();
		List<BizCanvasNodeBo> thirdRow = new ArrayList<BizCanvasNodeBo>();
		List<BizCanvasNodeBo> fourthRow = new ArrayList<BizCanvasNodeBo>();
		
		long newNodeId = -1;
		
		//????????????????????????????????????????????????????????????
		List<BizCanvasNodeBo> queryNodes = bizCanvasApi.getCanvasNode(bizId);
		if(queryNodes != null && queryNodes.size() > 0){
			
			for(BizCanvasNodeBo node : queryNodes){
				if(node.getBizId() == node.getInstanceId() && node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE){
					
					newNodeId = node.getId();
					
					break;
				}
			}
			
		}
		
		List<Long> repeatNodeIdList = new ArrayList<Long>();
		
		for(long id : instanceIds){
			ResourceInstance instance = null;
			try {
				instance = resourceInstanceService.getResourceInstance(id);
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
			}
			
			if(instance == null){
				logger.error("Get instance error , instance id : " + id);
				continue;
			}
			
			if(instance.getCategoryId() == null){
				logger.error("Get instance category error , instance id : " + id);
				continue;
			}
			
			CategoryDef categoryDef = capacityService.getCategoryById(instance.getCategoryId());
			
			if(categoryDef == null){
				logger.error("Get categoryDef error , category id : " + instance.getCategoryId());
				continue;
			}
			
			BizCanvasNodeBo canvasNode = new BizCanvasNodeBo();
			boolean isRepeat = false;
			for(BizCanvasNodeBo node : queryNodes){
				if(node.getInstanceId() == id && node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
					canvasNode = node;
					repeatNodeIdList.add(node.getId());
					isRepeat = true;
					break;
				}
			}
			if(!isRepeat){
				//???????????????
				canvasNode.setInstanceId(id);
				canvasNode.setBizId(bizId);
				canvasNode.setNodeType(BizNodeTypeDefine.INSTANCE_TYPE);
				canvasNode.setShowName(instance.getShowName());
				int nodeStatus = BizStatusDefine.NORMAL_STATUS;
				InstanceStateData instanceStateData = instanceStateService.getStateAdapter(id);
				if(instanceStateData != null){
					nodeStatus = statusTransform(instanceStateData.getState());
				}
				canvasNode.setNodeStatus(nodeStatus);
				
				long newId = bizCanvasApi.insertCanvasNode(canvasNode);
				if(newId > 0){
					//????????????
					canvasNode.setId(newId);
				}else{
					logger.error("Add bizCanvasNodeBo error , node name : " + instance.getShowName());
					continue;
				}
			}
			
			//??????????????????
			/**
			 *      ?????????	URL
			 * 		?????????	????????????????????????J2EE??????????????????Web??????????????????????????????URL?????????????????????????????????????????????Domino????????????????????????
			 *		?????????	???????????????????????????
			 *		?????????	?????????????????????snmp??????
			 *
			 */
			if(categoryDef.getId().equals(CapacityConst.URL)){
				//?????????
				firstRow.add(canvasNode);
			}else if(categoryDef.getParentCategory().getId().equals(CapacityConst.HOST) || 
					categoryDef.getParentCategory().getId().equals(CapacityConst.HARDWARE) ||
					categoryDef.getParentCategory().getParentCategory().getId().equals(CapacityConst.VM)){
				//?????????
				thirdRow.add(canvasNode);
			}else if(categoryDef.getParentCategory().getId().equals(CapacityConst.NETWORK_DEVICE) || 
					categoryDef.getParentCategory().getId().equals(CapacityConst.SNMPOTHERS)){
				//?????????
				fourthRow.add(canvasNode);
			}else{
				//?????????
				secondRow.add(canvasNode);
			}
			
			
		}
		
		//?????????????????????????????????????????????????????????????????????
		BizMainBo mainbo = getBasicInfo(bizId);
		if(mainbo.getStatusDefine() == null || mainbo.getStatusDefine().equals("")){
			//?????????????????????
			calculateBizHealth(bizId,1,false,null,-1);
		}
		
		if(firstRow != null && firstRow.size() > 0){
			canvasNodeList.add(firstRow);
		}
		
		if(secondRow != null && secondRow.size() > 0){
			canvasNodeList.add(secondRow);
		}
		
		if(thirdRow != null && thirdRow.size() > 0){
			canvasNodeList.add(thirdRow);
		}
		
		if(fourthRow != null && fourthRow.size() > 0){
			canvasNodeList.add(fourthRow);
		}
		
		boolean isHaveThirdRow = false;
		boolean isAreadyAddUrlLink = false;
		
		if(thirdRow != null && thirdRow.size() > 0){
			isHaveThirdRow = true;
		}
		
		/**
		 * ???	????????????URL?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
???	     *		????????????????????????URL??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
???	     *		????????????????????????????????????????????????????????????????????????
		 */
		for(int i = 0 ; i < canvasNodeList.size() ; i ++){
			List<BizCanvasNodeBo> nodes = canvasNodeList.get(i);
			if(!isAreadyAddUrlLink && nodes != null && nodes.size() > 0){
				for(BizCanvasNodeBo node : nodes){
					
					//???????????????
					BizCanvasLinkBo link = new BizCanvasLinkBo();
					link.setFromNode(newNodeId);
					link.setToNode(node.getId());
					
					long newLinkId = bizCanvasApi.insertCanvasLink(link);
					if(newLinkId > 0){
						//????????????
						link.setId(newLinkId);
						canvasLinkList.add(link);
					}else{
						logger.error("Add bizCanvasLinkBo error or link already exsit");
					}
					
				}
				isAreadyAddUrlLink = true;
			}
			if((i + 1) < canvasNodeList.size() && canvasNodeList.get(i + 1) != null && canvasNodeList.get(i + 1).size() > 0){
				if(!isHaveThirdRow && (i + 1) == (canvasNodeList.size() - 1)){
					continue;
				}
				List<BizCanvasNodeBo> nextNodes = canvasNodeList.get(i + 1);
				for(BizCanvasNodeBo node : nodes){
					for(BizCanvasNodeBo nextNode : nextNodes){
						BizCanvasLinkBo link = new BizCanvasLinkBo();
						link.setFromNode(node.getId());
						link.setToNode(nextNode.getId());
						long newLinkId = bizCanvasApi.insertCanvasLink(link);
						if(newLinkId > 0){
							//????????????
							link.setId(newLinkId);
							canvasLinkList.add(link);
						}else{
							logger.error("Add bizCanvasLinkBo error or link already exsit");
						}
					}
				}
			}
		}
		
		List<List<BizCanvasNodeBo>> resultCanvasNodeList = new ArrayList<List<BizCanvasNodeBo>>();
		
		if(repeatNodeIdList != null && repeatNodeIdList.size() > 0){
			List<BizCanvasNodeBo> first = new ArrayList<BizCanvasNodeBo>();
			for(BizCanvasNodeBo node : firstRow){
				if(!repeatNodeIdList.contains(node.getId())){
					first.add(node);
				}
			}
			resultCanvasNodeList.add(first);
			List<BizCanvasNodeBo> second = new ArrayList<BizCanvasNodeBo>();
			for(BizCanvasNodeBo node : secondRow){
				if(!repeatNodeIdList.contains(node.getId())){
					second.add(node);
				}
			}
			resultCanvasNodeList.add(second);
			List<BizCanvasNodeBo> third = new ArrayList<BizCanvasNodeBo>();
			for(BizCanvasNodeBo node : thirdRow){
				if(!repeatNodeIdList.contains(node.getId())){
					third.add(node);
				}
			}
			resultCanvasNodeList.add(third);
			List<BizCanvasNodeBo> fourth = new ArrayList<BizCanvasNodeBo>();
			for(BizCanvasNodeBo node : fourthRow){
				if(!repeatNodeIdList.contains(node.getId())){
					fourth.add(node);
				}
			}
			resultCanvasNodeList.add(fourth);
		}else{
			resultCanvasNodeList.add(firstRow);
			resultCanvasNodeList.add(secondRow);
			resultCanvasNodeList.add(thirdRow);
			resultCanvasNodeList.add(fourthRow);
		}
		
		data.setNodes(resultCanvasNodeList);
		data.setLinks(canvasLinkList);
		
		return data;
	}

	@Override
	public int updateBasicInfo(BizMainBo main,String oldName) {
		
		if(bizMainDao.checkBizNameIsExsit(main.getName(),oldName) > 0){
			//??????????????????????????????
			return -1;
		}
		
		main.setUpdateTime(new Date());
		
		CompositeInstance compositeInstance = new CompositeInstance();
		compositeInstance.setInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);
		if(null != main.getName()) {
			compositeInstance.setName(main.getName());
		}
		compositeInstance.setId(main.getId());
		
		try {
			compositeInstanceService.updateCompositeInstance(compositeInstance);
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
		}
		int count = bizMainDao.updateBasicInfo(main);
		
		if(!(main.getName().equals(oldName)) || main.getFileId() > 0){
			//??????????????????
			BizCanvasNodeBo node = new BizCanvasNodeBo();
			
			node.setInstanceId(main.getId());
			node.setShowName(main.getName());
			node.setFileId(main.getFileId());
			bizCanvasDao.updateCanvasNodeBaseInfoByBizId(node);
		}
		
		// TODO Auto-generated method stub
		return count;
	}

	@Override
	public boolean deleteBiz(long[] ids) {
		
		List<Long> bizIdList = new ArrayList<Long>();
		
		for(long id : ids){
			bizIdList.add(id);
		}
		
		calculateBizHealthForMultipleBiz(bizIdList);
		
		return true;
	}
	
	/**
	 * ????????????????????????????????????
	 * @param bizIdList
	 */
	private void calculateBizHealthForMultipleBiz(List<Long> bizIdList){
		
		List<Long[]> bizNodeRelationList = new ArrayList<Long[]>();
		
		//????????????????????????????????????????????????????????????????????????????????????
		List<BizMainBo> statusDefineList = bizMainDao.getAllStatusDefineList();
		
		Map<Long, BizCanvasNodeBo> defineStatusDeleteNodesBizIds = new HashMap<Long, BizCanvasNodeBo>();
		
		for(BizMainBo status : statusDefineList){
			
			if(bizIdList.contains(status.getId())){
				continue;
			}
			
			if(status.getStatusDefine() == null || status.getStatusDefine().equals("") || status.getStatusDefine().trim().equals("")){
				//????????????????????????
				List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(status.getId());
				if(nodes != null && nodes.size() > 0){
					for(BizCanvasNodeBo node : nodes){
						if(node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE && node.getInstanceId() != status.getId()){
							bizNodeRelationList.add(new Long[]{status.getId(),node.getInstanceId()});
						}
					}
				}
			}else{
				//??????????????????????????????
				String patternParameter = "\\$\\{.*?\\}";
				Pattern pattern = Pattern.compile(patternParameter);
				
				String newStatusDefine = new String(status.getStatusDefine());
				
				newStatusDefine = newStatusDefine.replaceAll(" ", "");
				newStatusDefine = newStatusDefine.toUpperCase();
				
				boolean isUpdate = false;
				
				Matcher matcher = pattern.matcher(status.getStatusDefine());
				while (matcher.find()) {
					long bizNodeId = Long.parseLong(matcher.group(0).replace("${", "").replace("}", ""));
					BizCanvasNodeBo nodeBo = bizCanvasDao.getCanvasNode(bizNodeId);
					if(nodeBo.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE){
						bizNodeRelationList.add(new Long[]{status.getId(),bizNodeId});
						if(bizIdList.contains(nodeBo.getInstanceId())){
							isUpdate = true;
							newStatusDefine = newStatusDefine.replaceAll("\\$\\{" + bizNodeId + "\\}", "");
							if(!defineStatusDeleteNodesBizIds.containsKey(status.getId())){
								defineStatusDeleteNodesBizIds.put(status.getId(), nodeBo);
							}
						}
					}
				}
				
				//???????????????????????????
				if(isUpdate){
					if(!newStatusDefine.contains("$")){
						newStatusDefine = "";
					}else{
						newStatusDefine = newStatusDefine.replaceAll("\\,([1-5]{1}\\*)?\\,", ",")
								.replaceAll("\\,([1-5]{1}\\*)?\\)", ")")
								.replaceAll("\\(([1-5]{1}\\*)?\\,", "(");
						while (newStatusDefine.contains("AND()") || newStatusDefine.contains("AVG()") || newStatusDefine.contains("OR()")) {
							newStatusDefine = newStatusDefine.replaceAll("\\,(AND|OR|AVG)\\(\\)\\,", ",")
									.replaceAll("\\((AND|OR|AVG)\\(\\)\\,", "(")
									.replaceAll("\\,(AND|OR|AVG)\\(\\)\\)", ")");
						}
					}
					
					BizMainBo main = new BizMainBo();
					main.setId(status.getId());
					main.setStatusDefine(newStatusDefine);
					
					bizMainDao.updateBizStatusDefine(main);
					
					status.setStatusDefine(newStatusDefine);
				}
				
			}
		}
		
		//????????????????????????????????????????????????????????????????????????
		List<List<Long>> mapsList = getBizChangeOrder(bizNodeRelationList);
		List<Long> orderTempList = new ArrayList<Long>();
		
		//???????????????????????????????????????????????????????????????????????????????????????????????????
		for(List<Long> map : mapsList){
			List<Long> retainList = new ArrayList<Long>(map);
			retainList.retainAll(bizIdList);
			if(retainList != null && retainList.size() > 0){
				//??????????????????????????????????????????
				orderTempList.addAll(map);
			}
		}
		
		for(long id : bizIdList){
			if(bizMainDao.deleteBizById(id) <= 0){
				return;
			}
			//??????????????????
			bizCanvasApi.deleteCanvas(id);
			
			//?????????????????????????????????????????????
			bizCanvasDao.deleteCanvasBizNodeByBizId(id);
			
			//?????????????????????????????????
			bizHealthHisDao.deleteHealthHisByBizId(id);
			
			//??????????????????
			bizAlarmInfoApi.deleteInfo(id);
			
			//??????????????????
			try {
				compositeInstanceService.removeCompositeInstance(id);
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
			}
			
		}
		//????????????????????????
		bizCapMetricApi.deleteInfo(bizIdList);
		
		//??????????????????
		bizUserRelApi.deleteBizById(bizIdList,null);
		
		//??????????????????????????????
		BizSerReportEvent event = new BizSerReportEvent(new BizSerReport());
		event.setSourceIds(bizIdList);
		bizSerReportListenerEngine.handleListen(event);
		
		List<Long> orderList = new ArrayList<Long>();
		for(Long orderId : orderTempList){
			//????????????????????????
			if(!bizIdList.contains(orderId)){
				orderList.add(orderId);
			}
		}
		
		//?????????????????????????????????????????????????????????
		calulateBizAndNotifyAlarm(orderList, statusDefineList);
		
	}
	
	/**
	 * ?????????????????????????????????
	 * @param bizIdList
	 */
	@Override
	public void calculateBizHealthForDeleteMainInstances(List<Long> instanceIds){
		
		List<Long[]> bizNodeRelationList = new ArrayList<Long[]>();
		
		//????????????????????????????????????????????????????????????????????????????????????
		List<BizMainBo> statusDefineList = bizMainDao.getAllStatusDefineList();
		
		for(long instanceId : instanceIds){
			BizCanvasNodeBo bizNode = new BizCanvasNodeBo();
			bizNode.setInstanceId(instanceId);
			bizNode.setNodeType(BizNodeTypeDefine.DELETE_INSTANCE_TYPE);
			bizCanvasDao.updateCanvasNodeTypeInfo(bizNode);
		}
		
		Set<Long> checkBizList = new HashSet<Long>();
		
		for(BizMainBo status : statusDefineList){
			
			
			if(status.getStatusDefine() == null || status.getStatusDefine().equals("") || status.getStatusDefine().trim().equals("")){
				//????????????????????????
				List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(status.getId());
				if(nodes != null && nodes.size() > 0){
					for(BizCanvasNodeBo node : nodes){
						if((node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE || node.getNodeType() == BizNodeTypeDefine.DELETE_INSTANCE_TYPE)  
								&& instanceIds.contains(node.getInstanceId())){
							checkBizList.add(node.getBizId());
						}
						if(node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE && node.getInstanceId() != status.getId()){
							bizNodeRelationList.add(new Long[]{status.getId(),node.getInstanceId()});
						}
					}
				}
			}else{
				
				//??????????????????????????????
				String patternParameter = "\\$\\{.*?\\}";
				Pattern pattern = Pattern.compile(patternParameter);
				
				String newStatusDefine = new String(status.getStatusDefine());
				
				newStatusDefine = newStatusDefine.replaceAll(" ", "");
				newStatusDefine = newStatusDefine.toUpperCase();
				
				boolean isUpdate = false;
				
				Matcher matcher = pattern.matcher(status.getStatusDefine());
				while (matcher.find()) {
					long bizNodeId = Long.parseLong(matcher.group(0).replace("${", "").replace("}", ""));
					BizCanvasNodeBo nodeBo = bizCanvasDao.getCanvasNode(bizNodeId);
					if(instanceIds.contains(nodeBo.getInstanceId())){
						checkBizList.add(nodeBo.getBizId());
						isUpdate = true;
						newStatusDefine = newStatusDefine.replaceAll("\\$\\{" + bizNodeId + "\\}", "");
					}
					if(nodeBo.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE){
						bizNodeRelationList.add(new Long[]{status.getId(),bizNodeId});
					}
				}
				
				//???????????????????????????
				if(isUpdate){
					if(!newStatusDefine.contains("$")){
						newStatusDefine = "";
					}else{
						newStatusDefine = newStatusDefine.replaceAll("\\,([1-5]{1}\\*)?\\,", ",")
								.replaceAll("\\,([1-5]{1}\\*)?\\)", ")")
								.replaceAll("\\(([1-5]{1}\\*)?\\,", "(");
						while (newStatusDefine.contains("AND()") || newStatusDefine.contains("AVG()") || newStatusDefine.contains("OR()")) {
							newStatusDefine = newStatusDefine.replaceAll("\\,(AND|OR|AVG)\\(\\)\\,", ",")
									.replaceAll("\\((AND|OR|AVG)\\(\\)\\,", "(")
									.replaceAll("\\,(AND|OR|AVG)\\(\\)\\)", ")");
						}
					}
					
					BizMainBo main = new BizMainBo();
					main.setId(status.getId());
					main.setStatusDefine(newStatusDefine);
					
					bizMainDao.updateBizStatusDefine(main);
					
					status.setStatusDefine(newStatusDefine);
				}
				
			}
		}
		
		//????????????????????????????????????????????????????????????????????????
		List<List<Long>> mapsList = getBizChangeOrder(bizNodeRelationList);
		List<Long> orderTempList = new ArrayList<Long>();
		
		//???????????????????????????????????????????????????????????????????????????????????????????????????
		for(List<Long> map : mapsList){
			List<Long> retainList = new ArrayList<Long>(map);
			retainList.retainAll(checkBizList);
			if(retainList != null && retainList.size() > 0){
				//??????????????????????????????????????????
				orderTempList.addAll(map);
			}
		}
		
		for(long checkId : checkBizList){
			if(!orderTempList.contains(checkId)){
				orderTempList.add(checkId);
			}
		}
		
		//?????????????????????????????????????????????????????????
		calulateBizAndNotifyAlarm(orderTempList, statusDefineList);
		
	}
	
	//????????????????????????????????????????????????
	private List<List<Long[]>> loopZeroStartPoint(List<Long[]> topoList,List<List<Long[]>> resultBizId,Set<Long> queryNode,List<Long[]> curGroup){
		
		if(topoList == null || topoList.size() <= 0){
			if(curGroup != null && curGroup.size() > 0){
				resultBizId.add(curGroup);
			}
			return resultBizId;
		}
		
		//??????
		List<Long[]> group = new ArrayList<Long[]>();
		if(curGroup != null && curGroup.size() > 0){
			group = curGroup;
		}
		if(topoList != null && topoList.size() > 0){
			if(queryNode == null){
				queryNode = new HashSet<Long>();
				queryNode.add(topoList.get(0)[0]);
				queryNode.add(topoList.get(0)[1]);
				group.add(topoList.get(0));
				topoList.remove(0);
			}
			boolean isNextArray = true;
			for(int i = 0 ; i < topoList.size() ; i++){
				Long[] nodeArray = topoList.get(i);
				if(queryNode.contains(nodeArray[0]) || queryNode.contains(nodeArray[1])){
					isNextArray = false;
					queryNode.add(nodeArray[0]);
					queryNode.add(nodeArray[1]);
					group.add(nodeArray);
					topoList.remove(i);
					break;
				}
			}
			if(isNextArray){
				resultBizId.add(group);
				return loopZeroStartPoint(topoList, resultBizId, null ,null);
			}else{
				return loopZeroStartPoint(topoList, resultBizId, queryNode,group);
			}
		}

		return null;
	}
	
	private int statusTransform(InstanceStateEnum state){
		
		if(state.equals(InstanceStateEnum.CRITICAL)){
			return BizStatusDefine.DEATH_STATUS;
		}else if(state.equals(InstanceStateEnum.SERIOUS)){
			return BizStatusDefine.SERIOUS_STATUS;
		}else if(state.equals(InstanceStateEnum.WARN)){
			return BizStatusDefine.WARN_STATUS;
		}else{
			return BizStatusDefine.NORMAL_STATUS;
		}
		
	}

	/**
	 * ????????????????????????????????????????????????
	 */
	@Override
	public List<BizStatusDefineParameter> getBizStatusDefineParameter(long bizId) {
		
		List<BizStatusDefineParameter> parameterList = new ArrayList<BizStatusDefineParameter>();
		
		List<BizCanvasNodeBo> nodeList = bizCanvasApi.getCanvasNode(bizId);
		
		if(nodeList == null || nodeList.size() <= 0){
			return parameterList;
		}
		
		List<BizCanvasNodeBo> loopNode = new ArrayList<BizCanvasNodeBo>();
		
		for(BizCanvasNodeBo node : nodeList){
			
			//??????????????????????????????????????????
			if(node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE){
				
				if(node.getInstanceId() == bizId){
					//??????
					BizStatusDefineParameter parameter = new BizStatusDefineParameter();
					parameter.setNodeId(node.getId());
					parameter.setNodeName(node.getShowName());
					parameter.setNodeType(node.getNodeType());
					parameter.setNodeTypeName(STATUS_PARAMETER_BUSINESS_NAME);
					parameter.setType(-1);
					
					parameterList.add(parameter);
				}else{
					//??????????????????????????????????????????
					loopNode.add(node);
				}
				
			}else if(node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
				
				//??????????????????
				BizStatusDefineParameter parameter = new BizStatusDefineParameter();
				parameter.setNodeId(node.getId());
				parameter.setNodeName(node.getShowName());
				parameter.setNodeType(node.getNodeType());
				try {
					ResourceInstance instance = resourceInstanceService.getResourceInstance(node.getInstanceId());
					if(instance == null){
						continue;
					}
					String instanceResource =instance.getResourceId();
					parameter.setNodeTypeName(capacityService.getResourceDefById(instanceResource).getName());
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
				
				parameterList.add(parameter);
				
			}
			
		}
		
		//??????????????????????????????????????????
		checkNodeIsLoop(loopNode,bizId);
		for(BizCanvasNodeBo node : loopNode){
			BizStatusDefineParameter parameter = new BizStatusDefineParameter();
			parameter.setNodeId(node.getId());
			parameter.setNodeName(node.getShowName());
			parameter.setNodeType(node.getNodeType());
			parameter.setNodeTypeName(STATUS_PARAMETER_BUSINESS_NAME);
			if(node.getNodeStatus() == -2){
				parameter.setType(-2);
			}
			parameterList.add(parameter);
		}
		
		return parameterList;
	}
	
	/**
	 * ??????????????????????????????????????????????????????????????????????????????????????????
	 * @param nodeId
	 * @param bizId
	 * @return
	 */
	private void checkNodeIsLoop(List<BizCanvasNodeBo> loopNode,long bizId){

		List<Long[]> topoList = new ArrayList<Long[]>();
		
		List<BizMainBo> bizMainList = bizMainDao.getAllStatusDefineList();
		
		if(bizMainList == null || bizMainList.size() <= 0){
			return;
		}
		
		for(BizMainBo main : bizMainList){
			
			String statusDefine = main.getStatusDefine();
			
			if(statusDefine == null || statusDefine.equals("") || statusDefine.trim().equals("")){
				
				//???????????????????????????
				//?????????????????????????????????????????????
				List<BizCanvasNodeBo> nodeList = bizCanvasApi.getCanvasNode(main.getId());
				for(BizCanvasNodeBo node : nodeList){
					if(node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE && main.getId() != node.getInstanceId()){
						topoList.add(new Long[]{main.getId(),node.getInstanceId()});
					}
				}
				
			}else{
				
				//??????????????????????????????
			    String patternParameter = "\\$\\{.*?\\}";
			    Pattern pattern = Pattern.compile(patternParameter);
	
			    Matcher matcher = pattern.matcher(statusDefine);
			    while (matcher.find()) {
			    	long nodeId = Long.parseLong(matcher.group(0).replace("${", "").replace("}", ""));
			    	BizCanvasNodeBo nodeBo = bizCanvasApi.getCanvasNodeById(nodeId);
			    	if(nodeBo.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE){
			    		topoList.add(new Long[]{main.getId(),nodeId});
			    	}
			    }
				
			}
			
		}
		
		//?????????????????????????????????????????????
		for(BizCanvasNodeBo bizNode : loopNode){
			List<Long[]> topoListParameter = new ArrayList<Long[]>(topoList);
			topoListParameter.add(new Long[]{bizId,bizNode.getInstanceId()});
			//??????nodeStatus?????????????????????????????????????????????
			if(!iterator(topoListParameter,bizId,bizId)){
				bizNode.setNodeStatus(0);
			}else{
				bizNode.setNodeStatus(-2);
			}
		}
		
	}
	
	private boolean iterator(List<Long[]> topoList,long iteratorNode, long bizId){
		for(Long[] link : topoList){
			
			if(link[0] == iteratorNode){
				if(link[1] == bizId){
					return true;
				}
				if(iterator(topoList,link[1],bizId)){
					return true;
				}
			}else{
				continue;
			}
			
			
		}
		return false;
	}

	/**
	 * ???????????????????????????
	 */
	@Override
	public List<BizMainBo> getAllBizList(ILoginUser user) {
		
		List<BizMainBo> bizAllMainList = bizMainDao.getAllList();
		
		//??????????????????
		List<BizMainBo> bizList = new ArrayList<BizMainBo>();
		for(BizMainBo main : bizAllMainList){
			if(checkEditPermissions(user, main)){
				bizList.add(main);
			}
		}
		
		return bizList;
	}
	
	/**
	 * ??????????????????????????????
	 * @param loginUserId
	 * @param main
	 * @return
	 */
	private boolean checkEditPermissions(ILoginUser user,BizMainBo main){

		//???????????????????????????????????????????????????
		if(user.isSystemUser()){
			//???????????????????????????????????????????????????????????????
			return true;
		}else{
			
			if(user.isDomainUser()){
				//?????????????????????????????????????????????
				if(main.getCreateId() == user.getId() || main.getManagerId() == user.getId()){
					return true;
				}
			}else{
				if(main.getManagerId() == user.getId()){
					//???????????????????????????????????????
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	/**
	 * ???????????????????????????????????????
	 * @return
	 */
	@Override
	public List<BizMainBo> getAllPermissionsInfoList() {
		// TODO Auto-generated method stub
		return bizMainDao.getAllPermissionsInfoList();
	}
	
	/**
	 * ?????????????????????
	 * type:1.????????????2.????????????3.????????????4.???????????????????????????5.???????????????????????????
	 * isAddChildBiz:???????????????
	 * canvasNode:???????????????
	 * updateInstanceId:???????????????
	 */
	@Override
	public void calculateBizHealth(long mainBizId,int type,boolean isAddChildBiz,BizCanvasNodeBo canvasNode,long updateInstanceId) {
		
		List<Long[]> bizNodeRelationList = new ArrayList<Long[]>();
		
		//????????????????????????????????????????????????????????????????????????????????????
		List<BizMainBo> statusDefineList = bizMainDao.getAllStatusDefineList();
		
		for(BizMainBo status : statusDefineList){
			if(status.getStatusDefine() == null || status.getStatusDefine().equals("") || status.getStatusDefine().trim().equals("")){
				//????????????????????????
				List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(status.getId());
				if(nodes != null && nodes.size() > 0){
					for(BizCanvasNodeBo node : nodes){
						if(node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE && node.getInstanceId() != status.getId()){
							bizNodeRelationList.add(new Long[]{status.getId(),node.getInstanceId()});
						}
					}
				}
			}else{
				//?????????????????????
				//??????????????????????????????
				String patternParameter = "\\$\\{.*?\\}";
				Pattern pattern = Pattern.compile(patternParameter);
				
				Matcher matcher = pattern.matcher(status.getStatusDefine());
				while (matcher.find()) {
					long bizNodeId = Long.parseLong(matcher.group(0).replace("${", "").replace("}", ""));
					BizCanvasNodeBo nodeBo = bizCanvasDao.getCanvasNode(bizNodeId);
					if(nodeBo.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE){
						bizNodeRelationList.add(new Long[]{status.getId(),bizNodeId});
					}
				}
			}
		}
		
		//????????????????????????
		List<Long> orderList = loopZeroStartPointByNode(bizNodeRelationList , new ArrayList<Long>() , mainBizId);
		
		//??????????????????
		calulateBizAndNotifyAlarm(orderList, statusDefineList);

	}
	
	private String generateAlarmTitle(int newAlarmLevel,String name){
		if(newAlarmLevel == BizStatusDefine.NORMAL_STATUS){
			return "?????????" + name + "???????????????";
		}else{
			return "?????????" + name + "???????????????";
		}
	}
	
	private String generateBizAlarmContent(BizAlarmInfoBo alarmInfo,int alarmLevel,BizAlarmParameterDefine alarmParameterDefine){
		
		String alarmParameter = "";
		
		
		if(alarmLevel == BizStatusDefine.DEATH_STATUS){
			alarmParameter = alarmInfo.getDeathAlarmContent();
		}else if(alarmLevel == BizStatusDefine.SERIOUS_STATUS){
			alarmParameter = alarmInfo.getSeriousAlarmContent();
		}else if(alarmLevel == BizStatusDefine.WARN_STATUS){
			alarmParameter = alarmInfo.getWarnAlarmContent();
		}else{
			alarmParameter = alarmInfo.getNormalContent();
		}
		String alarmContent = new String(alarmParameter);
		
	    String patternParameter = "\\$\\{.*?\\}";
	    Pattern pattern = Pattern.compile(patternParameter);

	    Matcher matcher = pattern.matcher(alarmParameter);
	    while (matcher.find()) {
	    	String parameterName = matcher.group(0).replace("${", "").replace("}", "");
	    	if(parameterName.equals(BizAlarmParameterDefine.ALARM_NODE_CONTENT)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getAlarmNodeContent());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.ALARM_NODE_NAME)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getAlarmNodeName());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.ALARM_NODE_TYPE)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getAlarmNodeType());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.BIZ_ALARM_LEVEL)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getBizAlarmLevel());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.BIZ_HEALTH)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getBizHealth());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.BIZ_MANAGER)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getBizManager());
	    	}else if(parameterName.equals(BizAlarmParameterDefine.BIZ_NAME)){
	    		alarmContent = alarmContent.replace("${" + parameterName + "}", alarmParameterDefine.getBizName());
	    	}
	    }
		
		return alarmContent;
	}
	
	/**
	 * ??????????????????????????????
	 */
	private void addAlarm(String content,String alarmTitle,BizMainBo bizMain,int newState){
		
		AlarmSenderParamter alarmSenderParamter = new AlarmSenderParamter();
		alarmSenderParamter.setDefaultMsg(content);
		alarmSenderParamter.setDefaultMsgTitle(alarmTitle);
		alarmSenderParamter.setGenerateTime(new Date());
		alarmSenderParamter.setLevel(getInstanceState(newState));
		alarmSenderParamter.setProvider(AlarmProviderEnum.OC4);
		alarmSenderParamter.setSysID(SysModuleEnum.BUSSINESS);
		alarmSenderParamter.setExt0("????????????");
		alarmSenderParamter.setExt2(SysModuleEnum.BUSSINESS.name());
		alarmSenderParamter.setProfileID(bizMain.getId());
		alarmSenderParamter.setSourceID(String.valueOf(bizMain.getId()));
		alarmSenderParamter.setSourceName(bizMain.getName());
		alarmSenderParamter.setRuleType(AlarmRuleProfileEnum.biz_profile);
		alarmSenderParamter.setRecoverKeyValue(new String[]{String.valueOf(bizMain.getId())});
		alarmService.notify(alarmSenderParamter);
		logger.info("portal.business.state.addAlarm(business service id="+bizMain.getId()+") successful");
		
	}
	
	private InstanceStateEnum getInstanceState(int state){
		if(state == BizStatusDefine.DEATH_STATUS){
			return InstanceStateEnum.CRITICAL;
		}else if(state == BizStatusDefine.SERIOUS_STATUS){
			return InstanceStateEnum.SERIOUS;
		}else if(state == BizStatusDefine.WARN_STATUS){
			return InstanceStateEnum.WARN;
		}else if(state == BizStatusDefine.NORMAL_STATUS){
			return InstanceStateEnum.NORMAL;
		}
		return InstanceStateEnum.NORMAL;
	}
	
	private int getInstanceState(InstanceStateEnum state){
		if(state == InstanceStateEnum.CRITICAL){
			return BizStatusDefine.DEATH_STATUS;
		}else if(state == InstanceStateEnum.SERIOUS){
			return BizStatusDefine.SERIOUS_STATUS;
		}else if(state == InstanceStateEnum.WARN){
			return BizStatusDefine.WARN_STATUS;
		}else if(state == InstanceStateEnum.NORMAL){
			return BizStatusDefine.NORMAL_STATUS;
		}
		return BizStatusDefine.NORMAL_STATUS;
	}
	
	private int getInstanceState(MetricStateEnum state){
		if(state == MetricStateEnum.CRITICAL){
			return BizStatusDefine.DEATH_STATUS;
		}else if(state == MetricStateEnum.SERIOUS){
			return BizStatusDefine.SERIOUS_STATUS;
		}else if(state == MetricStateEnum.WARN){
			return BizStatusDefine.WARN_STATUS;
		}else if(state == MetricStateEnum.NORMAL){
			return BizStatusDefine.NORMAL_STATUS;
		}
		return BizStatusDefine.NORMAL_STATUS;
	}
	
	private String getAlarmInfoByLevel(int alarmLevel){
		if(alarmLevel == BizStatusDefine.DEATH_STATUS){
			return InstanceStateEnum.getValue(InstanceStateEnum.CRITICAL);
		}else if(alarmLevel == BizStatusDefine.SERIOUS_STATUS){
			return InstanceStateEnum.getValue(InstanceStateEnum.SERIOUS);
		}else if(alarmLevel == BizStatusDefine.WARN_STATUS){
			return InstanceStateEnum.getValue(InstanceStateEnum.WARN);
		}else{
			return InstanceStateEnum.getValue(InstanceStateEnum.NORMAL);
		}
	//	return InstanceStateEnum.getValue(state);
	}
	
	//???????????????????????????????????????
	private int getAlarmLevelByScore(int score,BizAlarmInfoBo alarmInfo){
		
		if(score <= Integer.parseInt(alarmInfo.getDeathThreshold())){
			return BizStatusDefine.DEATH_STATUS;
		}else if(score <= Integer.parseInt(alarmInfo.getSeriousThreshold())){
			return BizStatusDefine.SERIOUS_STATUS;
		}else if(score <= Integer.parseInt(alarmInfo.getWarnThreshold())){
			return BizStatusDefine.WARN_STATUS;
		}else{
			return BizStatusDefine.NORMAL_STATUS;
		}
		
	}
	
	private String getScore(String statusHealth,int deathScore){
		
		statusHealth = statusHealth.replaceAll(" ", "");
	    
	    statusHealth = statusHealth.toUpperCase();
		
		String content = "";
		
		String curExpression = "";
		
		String newS = new String(statusHealth);
		
		boolean isAdapt = false;
		
		for(int i = 0 ; i < statusHealth.length() ; i++){
			char a = statusHealth.charAt(i);
			if(a == '('){
				content = "";
				isAdapt = true;
				String temp = statusHealth.substring(0, i);
				int substringIndex_1 = temp.lastIndexOf(",") < 0 ? 0 : temp.lastIndexOf(",") + 1;
				int substringIndex_2 = temp.lastIndexOf("(") + 1;
				curExpression = temp.substring(substringIndex_1 > substringIndex_2 ? substringIndex_1 : substringIndex_2, temp.length());
			}else if(a == ')'){
				if(isAdapt){
					if(!content.equals("")){
						if(curExpression.equals("OR") || curExpression.equals("AVG")){
							int weight = 0;
							int sum = 0;
							for(String node : content.split(",")){
								if(node.contains("-1")){
									continue;
								}
								if(node.contains("*")){
									weight += Integer.parseInt(node.split("\\*")[0]);
									sum += Integer.parseInt(node.split("\\*")[0]) * Integer.parseInt(node.split("\\*")[1]);
								}else{
									weight++;
									sum += Integer.parseInt(node);
								}
							}
							int value = 100;
							if(weight > 0){
								value = sum / weight;
							}
							newS = newS.replace(curExpression + "(" + content + ")", value + "");
						}else if(curExpression.equals("AND")){
							boolean isUseWeight = true;
							List<Integer> list = new ArrayList<Integer>();
							for(String node : content.split(",")){
								if(node.contains("-1")){
									continue;
								}
								if(node.contains("*")){
									if(Integer.parseInt(node.split("\\*")[1]) <= deathScore){
										//????????????????????????AND
										isUseWeight = false;
									}
									list.add(Integer.parseInt(node.split("\\*")[1]));
								}else{
									if(Integer.parseInt(node) <= deathScore){
										//????????????????????????AND
										isUseWeight = false;
									}
									list.add(Integer.parseInt(node));
								}
								
							}
							if(isUseWeight){
								//????????????????????????????????????
								int weight = 0;
								int sum = 0;
								for(String node : content.split(",")){
									if(node.contains("-1")){
										continue;
									}
									if(node.contains("*")){
										weight += Integer.parseInt(node.split("\\*")[0]);
										sum += Integer.parseInt(node.split("\\*")[0]) * Integer.parseInt(node.split("\\*")[1]);
									}else{
										weight++;
										sum += Integer.parseInt(node);
									}
								}
								int value = 100;
								if(weight > 0){
									value = sum / weight;
								}
								newS = newS.replace(curExpression + "(" + content + ")",value + "");
							}else{
								int value = 100;
								if(list != null && list.size() > 0){
									Collections.sort(list);
									value = list.get(0);
								}
								newS = newS.replace(curExpression + "(" + content + ")",value  + "");
							}
						}
					}
					content = "";
					isAdapt = false;
				}
			}else{
				content += a;
			}
			
		}
		
		if(newS.contains("(")){
			return getScore(newS,deathScore);
		}else{
			return newS;
		}
		
		
	}
	
	//????????????????????????????????????
	private int getScoreByAlarmLevel(int alarmLevel,BizAlarmInfoBo alarmInfo){
		
		if(alarmLevel == BizStatusDefine.NORMAL_STATUS){
			return 100;
		}else if(alarmLevel == BizStatusDefine.WARN_STATUS){
			return Integer.parseInt(alarmInfo.getWarnThreshold());
		}else if(alarmLevel == BizStatusDefine.SERIOUS_STATUS){
			return Integer.parseInt(alarmInfo.getSeriousThreshold());
		}else if(alarmLevel == BizStatusDefine.DEATH_STATUS){
			return Integer.parseInt(alarmInfo.getDeathThreshold());
		}else{
			return 100;
		}
		
	}
	
	private List<Long> loopZeroStartPointByNode(List<Long[]> topoList,List<Long> resultBizId,long loopNode){
		resultBizId.add(loopNode);
		for(Long[] node : topoList){
			if(node[1] != loopNode){
				continue;
			}
			return loopZeroStartPointByNode(topoList,resultBizId,node[0]);
		}
		return resultBizId;
	}
	
	/**
	 * ?????????????????????????????????????????????
	 */
	@Override
	public void updateStatusDefineByDeleteNode(List<Long> deleteIds, long bizId,BizCanvasNodeBo firstDeleteNode) {
		
		String statusDefine = bizMainDao.getCanvasStatusDefine(bizId);
		
		if(statusDefine == null || statusDefine.equals("") || statusDefine.trim().equals("")){
			//???????????????????????????????????????????????????????????????????????????
			if(firstDeleteNode != null){
				calculateBizHealth(bizId, 2, false, firstDeleteNode,-1);
			}
		}else{
			
			boolean deleteIdAffectBizStatus = false;
			
			statusDefine = statusDefine.replaceAll(" ", "");
			statusDefine = statusDefine.toUpperCase();
			
			for(long nodeId : deleteIds){
				
				if(statusDefine.contains(nodeId + "")){
					deleteIdAffectBizStatus = true;
				}
				
				statusDefine = statusDefine.replaceAll("\\$\\{" + nodeId + "\\}", "");
				
			}
			
			if(!statusDefine.contains("$")){
				statusDefine = "";
			}else{
				statusDefine = statusDefine.replaceAll("\\,([1-5]{1}\\*)?\\,", ",")
						.replaceAll("\\,([1-5]{1}\\*)?\\)", ")")
						.replaceAll("\\(([1-5]{1}\\*)?\\,", "(");
				while (statusDefine.contains("AND()") || statusDefine.contains("AVG()") || statusDefine.contains("OR()")) {
					statusDefine = statusDefine.replaceAll("\\,(AND|OR|AVG)\\(\\)\\,", ",")
							.replaceAll("\\((AND|OR|AVG)\\(\\)\\,", "(")
							.replaceAll("\\,(AND|OR|AVG)\\(\\)\\)", ")");
				}
			}
			
			BizMainBo main = new BizMainBo();
			main.setId(bizId);
			main.setStatusDefine(statusDefine);
			
			bizMainDao.updateBizStatusDefine(main);
			
			if(deleteIdAffectBizStatus && firstDeleteNode != null){
				
				//???????????????
				calculateBizHealth(bizId, 2, false, firstDeleteNode,-1);
			}
			
		}
		
	}
	
	@Override
	public List<BizInstanceNodeBo> getHealthDetail(long bizId) {
		
		List<BizInstanceNodeBo> result = new ArrayList<BizInstanceNodeBo>();
		
		List<BizInstanceNodeBo> nodes = bizCanvasDao.getInstanceNodesByBiz(bizId);
		
		List<BizCanvasNodeBo> bizNodes = bizCanvasDao.getBusinessNodesByBiz(bizId);
		
		if((nodes == null || nodes.size() <= 0) && (bizNodes == null || bizNodes.size() <= 0)){
			return result;
		}
		
		if(nodes != null && nodes.size() > 0){
			
			for(BizInstanceNodeBo node : nodes){
				
				//??????????????????
				try {
					ResourceInstance mainInstance = resourceInstanceService.getResourceInstance(node.getInstanceId());
					if(mainInstance != null){
						ResourceDef resourceDef = capacityService.getResourceDefById(mainInstance.getResourceId());
						if(resourceDef != null){
							node.setShowName(resourceDef.getCategory().getName() + "    " + node.getShowName());
						}
					}
				} catch (InstancelibException e1) {
					logger.error(e1.getMessage(),e1);
				}
				
				if(node.getType() == 2){
					
					//???????????????
					List<BizNodeMetricRelBo> rels = node.getBind();
					
					for(BizNodeMetricRelBo rel : rels){
						try {
							ResourceInstance childInstance = resourceInstanceService.getResourceInstance(rel.getChildInstanceId());
							rel.setName(childInstance.getShowName() == null ? childInstance.getName() : childInstance.getShowName());
							if(childInstance.getLifeState() != InstanceLifeStateEnum.MONITORED){
								rel.setStatus(BizStatusDefine.NONE_STATUS);
							}else{
								InstanceStateData instanceStateData = instanceStateService.getStateAdapter(rel.getChildInstanceId());
								if(instanceStateData == null){
									rel.setStatus(BizStatusDefine.NORMAL_STATUS);
								}else{
									rel.setStatus(getInstanceState(instanceStateData.getState()));
								}
							}
						} catch (InstancelibException e) {
							logger.error(e.getMessage(),e);
						}
					}
					
				}else if(node.getType() == 3){
					
					//????????????
					List<BizNodeMetricRelBo> rels = node.getBind();
					
					for(BizNodeMetricRelBo rel : rels){
						if(rel.getChildInstanceId() > 0){
							//??????????????????
							try {
								ResourceInstance childInstance = resourceInstanceService.getResourceInstance(rel.getChildInstanceId());
								ResourceMetricDef metricDef = capacityService.getResourceMetricDef(childInstance.getResourceId(), rel.getMetricId());
								rel.setName(childInstance.getName() + "    " + metricDef.getName());
								ProfileMetric profileMetric = profileService.getMetricByInstanceIdAndMetricId(rel.getChildInstanceId(), rel.getMetricId());
								if(profileMetric != null && !profileMetric.isMonitor()){
									rel.setStatus(BizStatusDefine.NONE_STATUS);
								}else{
									MetricStateData state = metricStateService.getMetricState(rel.getChildInstanceId(), rel.getMetricId());
									if(state == null){
										rel.setStatus(BizStatusDefine.NORMAL_STATUS);
									}else{
										rel.setStatus(getInstanceState(state.getState()));
									}
								}
							} catch (InstancelibException e) {
								logger.error(e.getMessage(),e);
							} catch (ProfilelibException e) {
								logger.error(e.getMessage(),e);
							}
						}else{
							//??????????????????
							try {
								int curState = BizStatusDefine.NORMAL_STATUS;
								ResourceInstance instance = resourceInstanceService.getResourceInstance(node.getInstanceId());
								ResourceMetricDef metricDef = capacityService.getResourceMetricDef(instance.getResourceId(), rel.getMetricId());
								if(metricDef == null){
									//??????????????????????????????
									try {
										CustomMetric customMetric = customMetricService.getCustomMetric(rel.getMetricId());
										if(customMetric != null){
											rel.setName(instance.getShowName() + "    " + customMetric.getCustomMetricInfo().getName());
											if(!customMetric.getCustomMetricInfo().isMonitor()){
												curState = BizStatusDefine.NONE_STATUS;
											}
										}
									} catch (CustomMetricException e) {
										logger.error(e.getMessage(),e);
									}
								}else{
									rel.setName(instance.getShowName() + "    " + metricDef.getName());
									ProfileMetric profileMetric = profileService.getMetricByInstanceIdAndMetricId(node.getInstanceId(), rel.getMetricId());
									if(profileMetric != null && !profileMetric.isMonitor()){
										curState = BizStatusDefine.NONE_STATUS;
									}
								}
								MetricStateData state = metricStateService.getMetricState(node.getInstanceId(), rel.getMetricId());
								if(state != null){
									rel.setStatus(getInstanceState(state.getState()));
								}else{
									rel.setStatus(curState);
								}
							} catch (InstancelibException e) {
								logger.error(e.getMessage(),e);
							} catch (ProfilelibException e) {
								logger.error(e.getMessage(),e);
							}
						}
					}
					
				}
				
				result.add(node);
			}
		}
		
		
		if(bizNodes != null && bizNodes.size() > 0){
			for(BizCanvasNodeBo node : bizNodes){
				BizInstanceNodeBo nodeBo = new BizInstanceNodeBo();
				nodeBo.setType(1);
				nodeBo.setId(node.getId());
				nodeBo.setNodeStatus(node.getNodeStatus());
				nodeBo.setShowName("????????????    " + node.getShowName());
				
				result.add(nodeBo);
			}
		}
		
		return result;
	}
	
	/**
	 * ???????????????????????????????????????
	 * @param bizId
	 * @return
	 */
	@Override
	public List<Object> getCapacityMetric(long bizId) {
		
		List<Object> capacity = new ArrayList<Object>();

		BizCalculateCapacityMetric calcualte = new BizCalculateCapacityMetric();
		
		BizDatabaseCapacityMetric database = new BizDatabaseCapacityMetric();
		
		BizStoreCapacityMetric store = new BizStoreCapacityMetric();
		
		BizBandwidthCapacityMetric bandwidth = new BizBandwidthCapacityMetric();
		
		getCalculateCapacity(calcualte,bizId);
		
		getDatabaseCapacity(database, bizId);
		
		getStoreCapacity(store, bizId);
		
		getBandwidthCapacity(bandwidth, bizId);
		
		capacity.add(calcualte);
		capacity.add(store);
		capacity.add(database);
		capacity.add(bandwidth);
		
		return capacity;
	}

	/**
	 * ???????????????????????????
	 * @return
	 */
	@Override
	public BizMetricHistoryDataBo getHealthHistoryData(long bizId,Date startTime, Date endTime,String timeType) {

		BizMetricHistoryDataBo data = new BizMetricHistoryDataBo();
		
		List<BizMetricHistoryValueBo> values = new ArrayList<BizMetricHistoryValueBo>();
		
		BizAlarmInfoBo alarmInfo = bizAlarmInfoApi.getAlarmInfoById(bizId);
		
		data.setThreshold(alarmInfo.getWarnThreshold() + "," + alarmInfo.getSeriousThreshold());
		data.setUnit("???");
		BizHealthHisBo healthBo = bizHealthHisDao.getHealthFrontFirstScopeByStartTime(bizId, startTime);
		if(healthBo != null){
			data.setFrontFirstHealth(healthBo.getBizHealth());
		}else{
			data.setFrontFirstHealth(100);
		}
		
		BizHealthHisBo bizHis = bizHealthHisDao.getBizHealthHis(bizId);
		
		if(bizHis != null){
			data.setStatus(bizHis.getBizStatus());
			data.setCurValue(bizHis.getBizHealth() + "");
			data.setLastCollect(bizHis.getBizChangeTime());
		}else{
			data.setCurValue("100");
			data.setLastCollect(null);
		}
		
		List<BizHealthHisBo> healthList = bizHealthHisDao.getHealthByTimeScope(bizId, startTime, endTime);
		
		if(timeType.equals("min")){
			
			if(healthList != null && healthList.size() > 0){
				for(BizHealthHisBo health : healthList){
					BizMetricHistoryValueBo value = new BizMetricHistoryValueBo();
					
					value.setTime(health.getBizChangeTime());
					value.setValue(health.getBizHealth());
					
					values.add(value);
				}
			}
			
		}else{
			
			long scope = 0;
			
			switch (timeType) {
			case "halfHour":
				scope = 1000 * 60 * 60;
				break;
			case "hour":
				scope = 1000 * 60 * 60 * 6;
				break;
			case "sixHour":
				scope = 1000 * 60 * 60 * 12;
				break;
			default:
				scope = 1000 * 60 * 60;
				break;
			}
			
			
			if(healthList != null && healthList.size() > 0){
				
				Map<Integer, List<BizHealthHisBo>> healthSummaryMap = new HashMap<Integer, List<BizHealthHisBo>>();
				
				for(BizHealthHisBo health : healthList){
					
					int index = (int)((health.getBizChangeTime().getTime() - startTime.getTime()) / scope);
					
					List<BizHealthHisBo> list = healthSummaryMap.get(index);
					if(list == null || list.size() <= 0){
						list = new ArrayList<BizHealthHisBo>();
						list.add(health);
						healthSummaryMap.put(index, list);
					}else{
						healthSummaryMap.get(index).add(health);
					}
					
				}
				
				if(healthSummaryMap.size() > 0){

					Set<Integer> keySet = healthSummaryMap.keySet();
					
					if(keySet != null && keySet.size() > 0){
						
						TreeSet<Integer> treeSet = new TreeSet<Integer>(keySet);
						
						for(int key : treeSet){
							
							List<BizHealthHisBo> list = healthSummaryMap.get(key);
							
							if(list == null || list.size() <= 0){
								continue;
							}
							
							BizMetricHistoryValueBo value = new BizMetricHistoryValueBo();
							
							long healthSum = 0;
							
							for(BizHealthHisBo his : list){
								healthSum += his.getBizHealth();
							}
							
							value.setValue((int)(healthSum / list.size()));
							long summaryTime = key * scope + startTime.getTime();
							value.setTime(new Date(summaryTime));
							
							values.add(value);
							
						}
						
					}
					
				}
				
			}
		}
		
		data.setValues(values);

		return data;
	}
	
	/**
	 * ????????????????????????????????????????????????????????????
	 * @return
	 */
	@Override
	public List<BizMetricHistoryValueBo> getHealthHistoryDataForHomeMetric(long bizId,Date startTime, Date endTime) {

		List<BizMetricHistoryValueBo> values = new ArrayList<BizMetricHistoryValueBo>();
		
		List<BizHealthHisBo> healthList = bizHealthHisDao.getHealthByTimeScope(bizId, startTime, endTime);
		
		if(healthList != null && healthList.size() > 0){
			for(BizHealthHisBo health : healthList){
				BizMetricHistoryValueBo value = new BizMetricHistoryValueBo();
				
				value.setTime(health.getBizChangeTime());
				value.setValue(health.getBizHealth());
				
				values.add(value);
			}
		}
		
		return values;
	}

	/**
	 * ??????????????????????????????
	 * @param bizId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public BizMetricHistoryDataBo getResponseTimeHistoryData(long bizId,Date startTime, Date endTime,String timeType) {
		// TODO Auto-generated method stub
		BizMetricHistoryDataBo historyData = new BizMetricHistoryDataBo();
		
		List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(bizId, BizMetricDefine.RESPONSE_TIME);
		
		Set<Long> urlList = bizCanvasApi.getResponeTimeMetricInstanceList(bizId);
		
		if(urlList == null || urlList.size() <= 0){
			return historyData;
		}else{
			
			Map<Long, MetricData> dataMap = new TreeMap<Long, MetricData>();
			Map<Long, MetricSummaryData> dataSummaryMap = new TreeMap<Long, MetricSummaryData>();
			
			List<BizMetricHistoryValueBo> values = new ArrayList<BizMetricHistoryValueBo>();
			
			String curCollectTime = null;
			Double curCollectValue = null;
			
			long curInstance = 0;
			
			if(capMetricList == null || capMetricList.size() <= 0){
				
				for(long urlId : urlList){
					
					if(timeType.equals("min")){
						
						List<MetricData> metricDatas = metricDataService.queryHistoryMetricData(BizMetricDefine.RESPONSE_TIME, urlId, startTime, endTime);
						
						if(metricDatas != null && metricDatas.size() > 0){
							for(MetricData data : metricDatas){
								if(dataMap.keySet().contains(data.getCollectTime().getTime())){
									double mapValue = Double.parseDouble(dataMap.get(data.getCollectTime().getTime()).getData()[0]);
									double curValue = Double.parseDouble(data.getData()[0]);
									if(mapValue < curValue){
										dataMap.put(data.getCollectTime().getTime(), data);
									}
								}else{
									dataMap.put(data.getCollectTime().getTime(), data);
								}
							}
						}
					}else{
						setUrlDataMap(dataSummaryMap, timeType, urlId, startTime, endTime);
					}
					
					MetricRealtimeDataQuery metricRealtimeDataQuery = new MetricRealtimeDataQuery();
					metricRealtimeDataQuery.setInstanceID(new long[]{urlId});
					metricRealtimeDataQuery.setMetricID(new String[]{BizMetricDefine.RESPONSE_TIME});
					
					Page<Map<String,?>,MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(metricRealtimeDataQuery, 0, 1);
					List<Map<String,?>> realTimeDataList = page.getDatas();
					
					if(realTimeDataList == null || realTimeDataList.size() <= 0){
						continue;
					}
					
					Map<String,?> map =	realTimeDataList.get(0);
					
					if(map.get(BizMetricDefine.RESPONSE_TIME) == null){
						continue;
					}
					
					if(curCollectValue != null){
						if(curCollectValue > Double.parseDouble(map.get(BizMetricDefine.RESPONSE_TIME).toString())){
							curInstance = urlId;
							curCollectTime = map.get(BizMetricDefine.RESPONSE_TIME + "CollTime").toString();
							curCollectValue = Double.parseDouble(map.get(BizMetricDefine.RESPONSE_TIME).toString());
						}
					}else{
						curInstance = urlId;
						curCollectTime = map.get(BizMetricDefine.RESPONSE_TIME + "CollTime").toString();
						curCollectValue = Double.parseDouble(map.get(BizMetricDefine.RESPONSE_TIME).toString());
					}
					
				}
			}else{
				for(Long urlId : urlList){
					if(capMetricList.contains(urlId)){
						continue;
					}
					if(timeType.equals("min")){
						List<MetricData> metricDatas = metricDataService.queryHistoryMetricData(BizMetricDefine.RESPONSE_TIME, urlId, startTime, endTime);
						
						if(metricDatas != null && metricDatas.size() > 0){
							for(MetricData data : metricDatas){
								if(dataMap.keySet().contains(data.getCollectTime().getTime())){
									double mapValue = Double.parseDouble(dataMap.get(data.getCollectTime().getTime()).getData()[0]);
									double curValue = Double.parseDouble(data.getData()[0]);
									if(mapValue < curValue){
										dataMap.put(data.getCollectTime().getTime(), data);
									}
								}else{
									dataMap.put(data.getCollectTime().getTime(), data);
								}
							}
						}
					}else{
						setUrlDataMap(dataSummaryMap, timeType, urlId, startTime, endTime);
					}
					
					MetricRealtimeDataQuery metricRealtimeDataQuery = new MetricRealtimeDataQuery();
					metricRealtimeDataQuery.setInstanceID(new long[]{urlId});
					metricRealtimeDataQuery.setMetricID(new String[]{BizMetricDefine.RESPONSE_TIME});
					
					Page<Map<String,?>,MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(metricRealtimeDataQuery, 0, 1);
					List<Map<String,?>> realTimeDataList = page.getDatas();
					
					if(realTimeDataList == null || realTimeDataList.size() <= 0){
						continue;
					}
					
					Map<String,?> map =	realTimeDataList.get(0);
					
					if(map.get(BizMetricDefine.RESPONSE_TIME) == null){
						continue;
					}
					
					if(curCollectValue != null){
						if(curCollectValue > Double.parseDouble(map.get(BizMetricDefine.RESPONSE_TIME).toString())){
							curInstance = urlId;
							curCollectTime = map.get(BizMetricDefine.RESPONSE_TIME + "CollTime").toString();
							curCollectValue = Double.parseDouble(map.get(BizMetricDefine.RESPONSE_TIME).toString());
						}
					}else{
						curInstance = urlId;
						curCollectTime = map.get(BizMetricDefine.RESPONSE_TIME + "CollTime").toString();
						curCollectValue = Double.parseDouble(map.get(BizMetricDefine.RESPONSE_TIME).toString());
					}
				}
			}
			
			MetricStateData stateData = metricStateService.getMetricState(curInstance, BizMetricDefine.RESPONSE_TIME);
			
			if(stateData == null){
				historyData.setStatus(BizStatusDefine.NORMAL_STATUS);
			}else{
				historyData.setStatus(getInstanceState(stateData.getState()));
			}
			
			if(curCollectValue == null){
				historyData.setCurValue("");
				historyData.setLastCollect(null);
			}else{
				historyData.setCurValue(curCollectValue.toString());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					historyData.setLastCollect(sdf.parse(curCollectTime));
				} catch (ParseException e) {
					logger.error(e.getMessage(),e);
				}
			}
			historyData.setUnit("ms");
			
			if(timeType.equals("min")){
				
				for(long key : dataMap.keySet()){
					MetricData data = dataMap.get(key);
					
					BizMetricHistoryValueBo value = new BizMetricHistoryValueBo();
					
					value.setTime(data.getCollectTime());
					value.setValue(Double.valueOf(data.getData()[0]).intValue());
					
					values.add(value);
				}
				
			}else{
				
				for(long key : dataSummaryMap.keySet()){
					MetricSummaryData data = dataSummaryMap.get(key);
					
					BizMetricHistoryValueBo value = new BizMetricHistoryValueBo();
					
					value.setTime(data.getEndTime());
					value.setValue(data.getMetricData().intValue());
					
					values.add(value);
				}
				
			}
			
			historyData.setValues(values);
			
		}
		
		return historyData;
	}
	
	/**
	 * ??????????????????????????????
	 * @param bizId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public List<BizMetricHistoryValueBo> getResponseTimeHistoryDataForHomeMetric(long bizId,Date startTime, Date endTime,MetricSummaryType mtyp) {
		// TODO Auto-generated method stub
		List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(bizId, BizMetricDefine.RESPONSE_TIME);
		
		Set<Long> urlList = bizCanvasApi.getResponeTimeMetricInstanceList(bizId);
		
		List<BizMetricHistoryValueBo> values = new ArrayList<BizMetricHistoryValueBo>();
		
		if(urlList == null || urlList.size() <= 0){
			return values;
		}else{
			
			Map<Long, MetricSummaryData> dataSummaryMap = new TreeMap<Long, MetricSummaryData>();
			
			if(capMetricList == null || capMetricList.size() <= 0){
				
				for(long urlId : urlList){
					
					MetricSummaryQuery msq = new MetricSummaryQuery();
					msq.setSummaryType(mtyp);
					msq.setInstanceID(urlId);
					msq.setMetricID(BizMetricDefine.RESPONSE_TIME);
					msq.setEndTime(endTime);
					msq.setStartTime(startTime);
					
					List<MetricSummaryData> dataList = metricSummaryService.queryMetricSummary(msq);
					
					if(dataList != null && dataList.size() > 0){
						for(MetricSummaryData data : dataList){
							
							if(dataSummaryMap.keySet().contains(data.getEndTime().getTime())){
								double mapValue = dataSummaryMap.get(data.getEndTime().getTime()).getMetricData();
								double curValue = data.getMetricData();
								if(mapValue < curValue){
									dataSummaryMap.put(data.getEndTime().getTime(), data);
								}
							}else{
								dataSummaryMap.put(data.getEndTime().getTime(), data);
							}
						}
					}
					
				}
			}else{
				for(Long urlId : urlList){
					if(capMetricList.contains(urlId)){
						continue;
					}
					
					MetricSummaryQuery msq = new MetricSummaryQuery();
					msq.setSummaryType(mtyp);
					msq.setInstanceID(urlId);
					msq.setMetricID(BizMetricDefine.RESPONSE_TIME);
					msq.setEndTime(endTime);
					msq.setStartTime(startTime);
					
					List<MetricSummaryData> dataList = metricSummaryService.queryMetricSummary(msq);
					
					if(dataList != null && dataList.size() > 0){
						for(MetricSummaryData data : dataList){
							
							if(dataSummaryMap.keySet().contains(data.getEndTime().getTime())){
								double mapValue = dataSummaryMap.get(data.getEndTime().getTime()).getMetricData();
								double curValue = data.getMetricData();
								if(mapValue < curValue){
									dataSummaryMap.put(data.getEndTime().getTime(), data);
								}
							}else{
								dataSummaryMap.put(data.getEndTime().getTime(), data);
							}
						}
					}
					
				}
			}
			
			for(long key : dataSummaryMap.keySet()){
				MetricSummaryData data = dataSummaryMap.get(key);
				
				BizMetricHistoryValueBo value = new BizMetricHistoryValueBo();
				
				value.setTime(data.getEndTime());
				value.setValue(data.getMetricData().intValue());
				
				values.add(value);
			}
			
		}
		
		return values;
	}
	
	private void setUrlDataMap(Map<Long, MetricSummaryData> dataSummaryMap,String timeType,long instanceId,Date startTime, Date endTime){
		
		MetricSummaryQuery msq = new MetricSummaryQuery();
		switch (timeType) {
		case "halfHour":
			msq.setSummaryType(MetricSummaryType.HH);
			break;
		case "hour":
			msq.setSummaryType(MetricSummaryType.H);
			break;
		case "sixHour":
			msq.setSummaryType(MetricSummaryType.SH);
			break;
		default:
			msq.setSummaryType(MetricSummaryType.HH);
			break;
		}
		
		msq.setInstanceID(instanceId);
		msq.setMetricID(BizMetricDefine.RESPONSE_TIME);
		msq.setEndTime(endTime);
		msq.setStartTime(startTime);
		
		List<MetricSummaryData> dataList = metricSummaryService.queryMetricSummary(msq);
		
		if(dataList != null && dataList.size() > 0){
			for(MetricSummaryData data : dataList){
				
				if(dataSummaryMap.keySet().contains(data.getEndTime().getTime())){
					double mapValue = dataSummaryMap.get(data.getEndTime().getTime()).getMetricData();
					double curValue = data.getMetricData();
					if(mapValue < curValue){
						dataSummaryMap.put(data.getEndTime().getTime(), data);
					}
				}else{
					dataSummaryMap.put(data.getEndTime().getTime(), data);
				}
			}
		}
		
		
	}
	
	@Override
	public List<BizSerMetric> getBizReportMetrics() {
		List<BizSerMetric> metrics = new ArrayList<BizSerMetric>(); 
		for(BizSerMetricEnum b:BizSerMetricEnum.values()){
			BizSerMetric metric = new BizSerMetric();
			metric.setId(b.getIndex());
			metric.setName(b.getName());
			metrics.add(metric);
		}
		return metrics;
	}
	
	@Override
	public List<BizSerReport> getBizReportData(List<Long> ids,List<TimePeriod> timePeriods) {
		
		List<BizSerReport> reportDataList = new ArrayList<BizSerReport>();
		
		for(Long id : ids){
			BizSerReport report = new BizSerReport();
			BizMainBo main = bizMainDao.getBasicInfo(id);
			int bizStatus = bizHealthHisDao.getBizStatus(id);
			report.setId(main.getId());
			report.setName(main.getName());
			report.setUserName(main.getManagerName());
			
			//?????????????????????????????????????????????
			long runTime = 0;
			
			//??????????????????
			//?????????????????????????????????????????????????????????
			long criticalTime = 0;
			
			//?????????????????????????????????????????????????????????
			int criticalCount = 0;
			
			int eventCount = 0;
			int unRecoveredCount = 0;
			
			boolean lastIsCritical = false;
			
			List<String> idList = new ArrayList<String>();
			idList.add(id + "");
			for(TimePeriod time : timePeriods){
				
				lastIsCritical = false;
				AlarmEventQuery2 query = new AlarmEventQuery2();
				AlarmEventQueryDetail queryDetail = new AlarmEventQueryDetail();
				queryDetail.setSysID(SysModuleEnum.BUSSINESS);
				queryDetail.setSourceIDes(idList);
				queryDetail.setStart(time.getStartTime());
				queryDetail.setEnd(time.getEndTime());
				List<AlarmEventQueryDetail> detailList = new ArrayList<AlarmEventQueryDetail>();
				detailList.add(queryDetail);
				query.setFilters(detailList);
				List<AlarmEvent> eventList = alarmEventService.findAlarmEvent(query);
				

				long createTime = main.getCreateTime().getTime();
				if(time.getEndTime().getTime() > createTime){
					if(time.getStartTime().getTime() < createTime){
						runTime += (time.getEndTime().getTime() - createTime) / 1000;
					}else{
						runTime += (time.getEndTime().getTime() - time.getStartTime().getTime()) / 1000;
					}
				}
				
				List<AlarmEvent> curEventList = new ArrayList<AlarmEvent>();

				if(eventList != null && eventList.size() > 0){
					for(AlarmEvent event : eventList){
						if(event.getSourceID().equals(id + "")){
							
							curEventList.add(event);
						}
					}
				}
				
				if(curEventList != null && curEventList.size() > 0){
					for(int i = 0 ; i < curEventList.size() ; i++){
						AlarmEvent event = curEventList.get(i);
						eventCount++;
						if(!event.isRecovered()){
							unRecoveredCount++;
						}
						if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
							if(i > 0){
								AlarmEvent nextEvent = curEventList.get(i - 1);
								criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
							}else{
								criticalTime += (time.getEndTime().getTime() - event.getCollectionTime().getTime()) / 1000;
								lastIsCritical = true;
							}
							criticalCount++;
						}
					}
				}else{
					if(bizStatus == BizStatusDefine.DEATH_STATUS){
						criticalTime += (time.getEndTime().getTime() - time.getStartTime().getTime()) / 1000;
						criticalCount++;
					}
				}
				
			}
			
			//???????????????
			report.setAvailableRate(getAvailableRateMetricValue(runTime,criticalTime));
			
			//MTBF
			report.setMtbf(getMTBFMetricValue(runTime, criticalTime, lastIsCritical ? criticalCount : criticalCount + 1));
			
			//MTTR
			report.setMttr(getMTTRMetricValue(criticalTime, criticalCount));
			
			//????????????
			report.setOutageTimes(getOutageTimesMetricValue(criticalCount));
			
			//????????????
			report.setDownTime(getDownTimeMetricValue(criticalTime));
			
			//????????????
			report.setWarnNum(eventCount);
			
			//?????????????????????
			report.setUnrecoveredWarnNum(unRecoveredCount);
			
			reportDataList.add(report);
			
		}
		
		return reportDataList;
	}
	
	/**
	 * ?????????????????????????????????
	 * @param childInstanceId
	 */
	@Override
	public void deleteChildInstanceChangeBizStatus(List<Long> childInstanceId) {
		// TODO Auto-generated method stub
		
		logger.error("DeleteChildInstanceChangeBizStatus begin , size : " + childInstanceId.size());
		
		List<Long> chageNodeIds = new ArrayList<Long>();
		
		Set<Long> changeInstanceList = new HashSet<Long>();
		
		Set<Long> checkNodeId = new HashSet<Long>();

		for(long childId : childInstanceId){
			ResourceInstance instance = null;
			try {
				instance = resourceInstanceService.getResourceInstance(childId);
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
			}
			if(instance != null && instance.getParentId() > 0){
				changeInstanceList.add(instance.getParentId());
			}
		}
		
		for(long instanceId : changeInstanceList){
			
			//??????????????????ID?????????????????????????????????????????????????????????ID
			List<BizInstanceNodeBo> allInstanceNode = bizCanvasDao.getAllInstanceNode();
			if(allInstanceNode != null && allInstanceNode.size() > 0){
				for(BizInstanceNodeBo instanceNode : allInstanceNode){
					if(instanceNode.getInstanceId() == instanceId){
						
						checkNodeId.add(instanceNode.getId());
						
					}
				}
			}

		}
		
		//?????????????????????
		bizCanvasDao.deleteBindRelationByChildInstance(childInstanceId);
		
		changeBizNodeStatus(checkNodeId, chageNodeIds);
		
		if(chageNodeIds == null || chageNodeIds.size() <= 0){
			return;
		}
		
		//??????????????????
		List<Long> checkBizIdList = new ArrayList<Long>();
		
		List<Long[]> bizNodeRelationList = new ArrayList<Long[]>();
		
		//????????????????????????????????????????????????????????????????????????????????????
		List<BizMainBo> statusDefineList = bizMainDao.getAllStatusDefineList();
		
		for(BizMainBo status : statusDefineList){
			if(status.getStatusDefine() == null || status.getStatusDefine().equals("") || status.getStatusDefine().trim().equals("")){
				//????????????????????????
				if(bizCanvasDao.checkNodeByBizId(status.getId(), chageNodeIds) > 0){
					checkBizIdList.add(status.getId());
				}
				List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(status.getId());
				if(nodes != null && nodes.size() > 0){
					for(BizCanvasNodeBo node : nodes){
						if(node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE && node.getInstanceId() != status.getId()){
							bizNodeRelationList.add(new Long[]{status.getId(),node.getInstanceId()});
						}
					}
				}
			}else{
				//?????????????????????
				for(long nodeId : chageNodeIds){
					if(status.getStatusDefine().contains("{" + nodeId  + "}")){
						checkBizIdList.add(status.getId());
					    break;
						
					}
				}
				//??????????????????????????????
				String patternParameter = "\\$\\{.*?\\}";
				Pattern pattern = Pattern.compile(patternParameter);
				
				Matcher matcher = pattern.matcher(status.getStatusDefine());
				while (matcher.find()) {
					long bizNodeId = Long.parseLong(matcher.group(0).replace("${", "").replace("}", ""));
					BizCanvasNodeBo nodeBo = bizCanvasDao.getCanvasNode(bizNodeId);
					if(nodeBo.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE){
						bizNodeRelationList.add(new Long[]{status.getId(),bizNodeId});
					}
				}
			}
		}
		
		//????????????????????????????????????????????????????????????????????????
		List<List<Long>> mapsList = getBizChangeOrder(bizNodeRelationList);
		List<Long> orderList = new ArrayList<Long>();
		
		//???????????????????????????????????????????????????????????????????????????????????????????????????
		for(List<Long> map : mapsList){
			List<Long> retainList = new ArrayList<Long>(map);
			retainList.retainAll(checkBizIdList);
			if(retainList != null && retainList.size() > 0){
				//??????????????????????????????????????????
				orderList.addAll(map);
			}
		}
		
		for(long checkId : checkBizIdList){
			if(!orderList.contains(checkId)){
				orderList.add(checkId);
			}
		}
		
		//?????????????????????????????????????????????????????????
		calulateBizAndNotifyAlarm(orderList, statusDefineList);
		
	}
	
	@Override
	public List<BizMainBo> getBizsForHome(ILoginUser user) {
		List<BizMainBo> bizs = new ArrayList<BizMainBo>();
		try {
			
			List<BizMainBo> bizAllMainList = bizMainDao.getAllList();
			
			//????????????....
			List<BizMainBo> bizList = new ArrayList<BizMainBo>();
			for(BizMainBo main : bizAllMainList){
				if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
					continue;
				}
				bizList.add(main);
			}
			
			for(BizMainBo bo : bizList){
				BizMainBo b = new BizMainBo();
				b.setId(bo.getId());
				b.setName(bo.getName());
				b.setManagerName(bo.getManagerName());
				bizs.add(b);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return bizs;
	}
	
	@Override
	public List<Biz> getBizs(ILoginUser user, HttpServletRequest request) {
		List<Biz> bizs = new ArrayList<Biz>();
		try {
			
			List<BizMainBo> bizAllMainList = bizMainDao.getAllList();
			
			//????????????....
			List<BizMainBo> bizList = new ArrayList<BizMainBo>();
			for(BizMainBo main : bizAllMainList){
				if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
					continue;
				}
				bizList.add(main);
			}
			
			for(BizMainBo bo : bizList){
				Biz b = new Biz();
				b.setBizId(bo.getId());
				b.setBizType(Biz.BIZ_TYPE_BIZ);
				b.setTitle(bo.getName());
				bizs.add(b);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return bizs;
	}
	
	@Override
	public List<BizMainBo> getAllViewBiz(ILoginUser user) {
			
		List<BizMainBo> bizAllMainList = bizMainDao.getAllList();
		
		//????????????....
		List<BizMainBo> bizList = new ArrayList<BizMainBo>();
		for(BizMainBo main : bizAllMainList){
			if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
				continue;
			}
			main.setStatus(bizHealthHisDao.getBizStatus(main.getId()));
			bizList.add(main);
		}
		
		return bizList;
	}
	
	@Override
	public List<BizMainBo> getBizListByInstanceId(ILoginUser user,long instanceId) {
		
		List<BizMainBo> bizList = new ArrayList<BizMainBo>();
		List<Long> bizIdList = bizCanvasDao.getBizIdsByInstanceId(instanceId);
		if(bizIdList == null || bizIdList.size() <= 0){
			return bizList;
		}
		
		for(long id : bizIdList){
			if(!bizUserRelApi.checkUserView(user.getId(), id)){
				continue;
			}
			BizMainBo main = bizMainDao.getBasicInfo(id);
			main.setStatus(bizHealthHisDao.getBizStatus(id));
			main.setStatusDefine(null);
			if(main.getManagerId() > 0){
				main.setStatusDefine(stm_system_userApi.get(main.getManagerId()).getEmail() + "&" + stm_system_userApi.get(main.getManagerId()).getMobile());
			}
			bizList.add(main);
		}
		
		return bizList;
	}

	@Override
	public int getBizCountByManagerId(long manangerId) {
		// TODO Auto-generated method stub
		return bizMainDao.getBizCountByManagerId(manangerId);
	}

	@Override
	public void instanceMonitorChangeBiz(List<Long> mainInstanceId) {

		logger.error("***************cancelMainInstanceMonitorChangeBiz*******************");
		
		Set<Long> changeInstanceList = new HashSet<Long>();
		
		for(long id : mainInstanceId){
			try {
				ResourceInstance instance = resourceInstanceService.getResourceInstance(id);
				if(instance.getChildren() == null || instance.getChildren().size() <= 0){
					//?????????
					changeInstanceList.add(instance.getParentId());
				}else{
					changeInstanceList.add(instance.getId());
				}
				
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
			}
		}
		
		List<Long> chageNodeIds = new ArrayList<Long>();
		
		Set<Long> checkNodeId = new HashSet<Long>();
		
		for(long instanceId : changeInstanceList){
			
			//??????????????????ID?????????????????????????????????????????????????????????ID
			List<BizInstanceNodeBo> allInstanceNode = bizCanvasDao.getAllInstanceNode();
			if(allInstanceNode != null && allInstanceNode.size() > 0){
				for(BizInstanceNodeBo instanceNode : allInstanceNode){
					if(instanceNode.getInstanceId() == instanceId){
						
						checkNodeId.add(instanceNode.getId());
						
					}
				}
			}

		}
		
		changeBizNodeStatus(checkNodeId, chageNodeIds);
		
		if(chageNodeIds.size() > 0){
			changeBizStateFromAlarmOrMonitorChange(chageNodeIds, 2);
		}
		
	}
	
	private void changeBizStateFromAlarmOrMonitorChange(List<Long> chageNodeIds,int type){
		
		List<Long> checkBizIdList = new ArrayList<Long>();
		
		List<Long[]> bizNodeRelationList = new ArrayList<Long[]>();
		
		//????????????????????????????????????????????????????????????????????????????????????
		List<BizMainBo> statusDefineList = bizMainDao.getAllStatusDefineList();
		
		for(BizMainBo status : statusDefineList){
			if(status.getStatusDefine() == null || status.getStatusDefine().equals("") || status.getStatusDefine().trim().equals("")){
				//????????????????????????
				if(bizCanvasDao.checkNodeByBizId(status.getId(), chageNodeIds) > 0){
					checkBizIdList.add(status.getId());
				}
				List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(status.getId());
				if(nodes != null && nodes.size() > 0){
					for(BizCanvasNodeBo node : nodes){
						if(node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE && node.getInstanceId() != status.getId()){
							bizNodeRelationList.add(new Long[]{status.getId(),node.getInstanceId()});
						}
					}
				}
			}else{
				//?????????????????????
				for(long nodeId : chageNodeIds){
					if(status.getStatusDefine().contains("{" + nodeId  + "}")){
						checkBizIdList.add(status.getId());
					    break;
						
					}
				}
				//??????????????????????????????
				String patternParameter = "\\$\\{.*?\\}";
				Pattern pattern = Pattern.compile(patternParameter);
				
				Matcher matcher = pattern.matcher(status.getStatusDefine());
				while (matcher.find()) {
					long bizNodeId = Long.parseLong(matcher.group(0).replace("${", "").replace("}", ""));
					BizCanvasNodeBo nodeBo = bizCanvasDao.getCanvasNode(bizNodeId);
					if(nodeBo.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE){
						bizNodeRelationList.add(new Long[]{status.getId(),bizNodeId});
					}
				}
			}
		}
		
		//????????????????????????????????????????????????????????????????????????
		List<List<Long>> mapsList = getBizChangeOrder(bizNodeRelationList);
		List<Long> orderList = new ArrayList<Long>();
		
		//???????????????????????????????????????????????????????????????????????????????????????????????????
		for(List<Long> map : mapsList){
			List<Long> retainList = new ArrayList<Long>(map);
			retainList.retainAll(checkBizIdList);
			if(retainList != null && retainList.size() > 0){
				//??????????????????????????????????????????
				orderList.addAll(map);
			}
		}
		
		for(long checkId : checkBizIdList){
			if(!orderList.contains(checkId)){
				orderList.add(checkId);
			}
		}
		
		//?????????????????????????????????????????????????????????
		calulateBizAndNotifyAlarm(orderList, statusDefineList);
		
	}
	
	@Override
	public void metricMonitorOrAlarmChange(long profileId,List<String> metrics){

		logger.error("MetricMonitorOrAlarmChange begin");
		
		List<Long> changeNodeIds = new ArrayList<Long>();
		
		List<Long> instanceIds = new ArrayList<Long>();
		try {
			instanceIds = profileService.getResourceInstanceByProfileId(profileId);
		} catch (ProfilelibException e1) {
			logger.error(e1.getMessage(),e1);
		}
		
		if(instanceIds == null || instanceIds.size() <= 0){
			return;
		}
		
		if(metrics == null || metrics.size() <= 0){
			return;
		}
		
		Set<Long> allCheckNode = new HashSet<Long>();
		
		Set<Long> changeInstanceList = new HashSet<Long>();
		
		for(long id : instanceIds){
			
			ResourceInstance instance = null;
			try {
				instance = resourceInstanceService.getResourceInstance(id);
			} catch (InstancelibException e) {
				logger.error(e.getMessage(),e);
			}
			
			if(instance == null){
				continue;
			}
			
			//??????????????????????????????
			if(instance.getParentId() > 0){
				//?????????
				changeInstanceList.add(instance.getParentId());
			}else{
				//?????????
				changeInstanceList.add(instance.getId());
			}
			
		}
		
		if(changeInstanceList.size() <= 0){
			return;
		}
		
		for(long instanceId : changeInstanceList){
			
			//??????????????????ID?????????????????????????????????????????????????????????ID
			List<BizInstanceNodeBo> allInstanceNode = bizCanvasDao.getAllInstanceNode();
			if(allInstanceNode != null && allInstanceNode.size() > 0){
				for(BizInstanceNodeBo instanceNode : allInstanceNode){
					if(instanceNode.getInstanceId() == instanceId){
						
						allCheckNode.add(instanceNode.getId());
						
					}
				}
			}

		}
		
		changeBizNodeStatus(allCheckNode, changeNodeIds);
		
		if(changeNodeIds.size() > 0){
			changeBizStateFromAlarmOrMonitorChange(changeNodeIds, 1);
		}
		
	
	}
	
	private void changeBizNodeStatus(Set<Long> nodeIds,List<Long> chageNodeIds){
		
		for(long nodeId : nodeIds){
			
			logger.error("Check node status, id : " + nodeId);
			
			BizInstanceNodeBo canvasNode = bizCanvasDao.getInstanceNodeAndRelation(nodeId);
			List<BizNodeMetricRelBo> relationList = canvasNode.getBind();
			
			//????????????????????????
			int curHighestState = BizStatusDefine.NONE_STATUS;
			
			//??????????????????????????????
			boolean isAllCancelMonitor = true;
			
			if(canvasNode.getType() == 3){
				//????????????????????????
				for(BizNodeMetricRelBo rel : relationList){
					long queryInstanceId = rel.getChildInstanceId() > 0 ? rel.getChildInstanceId() : canvasNode.getInstanceId();
					try {
						ProfileMetric profileMetric = profileService.getMetricByInstanceIdAndMetricId(queryInstanceId, rel.getMetricId());
						CustomMetric customMetric = null;
						if(profileMetric == null && rel.getChildInstanceId() <= 0){
							//??????????????????????????????
							try {
								customMetric = customMetricService.getCustomMetric(rel.getMetricId());
							} catch (CustomMetricException e) {
								logger.error(e.getMessage(),e);
							}
							if(customMetric != null && customMetric.getCustomMetricInfo() != null && customMetric.getCustomMetricInfo().isMonitor()){
								isAllCancelMonitor = false;
							}
						}else if(profileMetric != null && profileMetric.isMonitor()){
							isAllCancelMonitor = false;
						}
						
						if(rel.getChildInstanceId() > 0){
							//??????????????????
							if(profileMetric == null || !profileMetric.isMonitor()){
								continue;
							}
						}else{
							//??????????????????
							if(profileMetric == null){
								if(customMetric == null || customMetric.getCustomMetricInfo() == null || !customMetric.getCustomMetricInfo().isMonitor()){
									continue;
								}
							}else if(!profileMetric.isMonitor()){
								continue;
							}
						}
						
					} catch (ProfilelibException e) {
						logger.error(e.getMessage(),e);
					}
					MetricStateData metricStateData = metricStateService.getMetricState(queryInstanceId, rel.getMetricId());
					int curState = BizStatusDefine.NORMAL_STATUS;
					if(metricStateData != null){
						curState = getInstanceState(metricStateData.getState());
					}
					if(curState > curHighestState){
						curHighestState = curState;
						if(curHighestState == BizStatusDefine.DEATH_STATUS){
							break;
						}
					}
					
				}
			}else if(canvasNode.getType() == 2){
				for(BizNodeMetricRelBo rel : relationList){
					ResourceInstance childInstance = null;
					try {
						childInstance = resourceInstanceService.getResourceInstance(rel.getChildInstanceId());
					} catch (InstancelibException e) {
						logger.error(e.getMessage(),e);
					}
					if(isAllCancelMonitor){
						if(childInstance != null && childInstance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
							isAllCancelMonitor = false;
						}
					}
					if(childInstance == null || childInstance.getLifeState() != InstanceLifeStateEnum.MONITORED){
						continue;
					}
					InstanceStateData stateData = instanceStateService.getStateAdapter(rel.getChildInstanceId());
					int curState = -1;
					if(stateData == null){
						curState = BizStatusDefine.NORMAL_STATUS;
					}else{
						curState = getInstanceState(stateData.getState());
					}
					if(curState > curHighestState){
						curHighestState = curState;
						if(curHighestState == BizStatusDefine.DEATH_STATUS){
							break;
						}
					}
				}
			}else if(canvasNode.getType() == 1){
				try {
					ResourceInstance instance = resourceInstanceService.getResourceInstance(canvasNode.getInstanceId());
					if(instance != null && instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
						isAllCancelMonitor = false;
						curHighestState = BizStatusDefine.NORMAL_STATUS;
					}
					InstanceStateData stateData = instanceStateService.getStateAdapter(canvasNode.getInstanceId());
					if(stateData != null){
						curHighestState = getInstanceState(stateData.getState());
					}
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
			}
			
			if(isAllCancelMonitor){
				curHighestState = BizStatusDefine.NONE_STATUS;
			}
			
			if(curHighestState != canvasNode.getNodeStatus()){
				//????????????????????????
				BizCanvasNodeBo bizNode = new BizCanvasNodeBo();
				bizNode.setId(canvasNode.getId());
				bizNode.setNodeStatus(curHighestState);
				bizNode.setStatusTime(new Date());
				logger.error("Update node status: " + curHighestState);
				bizCanvasDao.updateCanvasNodeStatusInfoByNodeId(bizNode);
				
				chageNodeIds.add(nodeId);
			}
			
		}
	}
	
	private List<List<Long>> getBizChangeOrder(List<Long[]> topoList){
		
		List<List<Long>> result = new ArrayList<List<Long>>();
		
		List<List<Long[]>> resultBizId = new ArrayList<List<Long[]>>();
		
		resultBizId = loopZeroStartPoint(topoList, resultBizId, null, null);
		
		if(resultBizId != null && resultBizId.size() > 0){
			for(List<Long[]> list : resultBizId){
				List<Long> singleResult = new ArrayList<Long>();
				singleResult = getGroupOrder(list, singleResult);
				result.add(singleResult);
			}
		}
		
		return result;
		
	}
	
	private List<Long> getGroupOrder(List<Long[]> list,List<Long> result){
		
		if(list == null || list.size() <= 0){
			return result;
		}
		
		long orderId = -1;
		out : for(Long[] node : list){
			long id = node[1];
			for(Long[] node_2 : list){
				if(node_2[0] == id){
					continue out;
				}
			}
			orderId = id;
			result.add(id);
			break out;
		}
	
		List<Long[]> newList = new ArrayList<Long[]>();
		out : for(Long[] node : list){
			if(node[1] == orderId){
				for(Long[] node_2 : list){
					if((node[0].equals(node_2[0]) || node[0].equals(node_2[1])) && node_2[1] != orderId){
						continue out;
					}
				}
				result.add(node[0]);
			}else{
				newList.add(node);
			}
		}
		
		return getGroupOrder(newList, result);
	
	}
	
	private void calulateBizAndNotifyAlarm(List<Long> orderList,List<BizMainBo> statusDefineList){
		
		Map<Long, String> healthChangeMap = new HashMap<Long, String>();
		//?????????????????????????????????????????????????????????
		for(long bizId : orderList){
			
			//??????????????????????????????
			BizAlarmInfoBo alarmInfo = new BizAlarmInfoBo();
			alarmInfo.setBizId(bizId);
			alarmInfo = protalBizAlarmInfoDao.getAlarmInfo(alarmInfo);
			
			for(BizMainBo status : statusDefineList){
				if(bizId == status.getId()){
					
					int newAlarmLevel = 0;
					int newHealthScore = 100;
					String alarmNodeName = "";
					String alarmNodeType = "";
					int alarmNodeTypeInt = 0;
					long alarmNodeId = 0;
					String alarmNodeContent = "";
					long alarmNodeInstanceId = 0;
					
					if(status.getStatusDefine() == null || status.getStatusDefine().equals("") || status.getStatusDefine().trim().equals("")){
						//????????????????????????
						List<BizCanvasNodeBo> nodes = bizCanvasDao.getCanvasNodes(status.getId());
						if(nodes != null && nodes.size() > 0){
							for(BizCanvasNodeBo node : nodes){
								if((node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE || node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE) 
										&& node.getInstanceId() != status.getId()){
									if(node.getNodeStatus() > newAlarmLevel){
										//????????????????????????
										newAlarmLevel = node.getNodeStatus();
										alarmNodeName = node.getShowName();
										alarmNodeTypeInt = node.getNodeType();
										alarmNodeInstanceId = node.getInstanceId();
										alarmNodeId = node.getId();
									}
								}
							}
							
							newHealthScore = getScoreByAlarmLevel(newAlarmLevel, alarmInfo);
							
						}
					}else{
						//?????????????????????
						String statusHealth = status.getStatusDefine();
					    String patternParameter = "\\$\\{.*?\\}";
					    Pattern pattern = Pattern.compile(patternParameter);
			
					    Matcher matcher = pattern.matcher(statusHealth);
					    int minimum = 100;
					    while (matcher.find()) {
					    	long bizNodeId = Long.parseLong(matcher.group(0).replace("${", "").replace("}", ""));
					    	if(!statusHealth.contains("" + bizNodeId)){
					    		continue;
					    	}
					    	BizCanvasNodeBo nodeBo = bizCanvasDao.getCanvasNode(bizNodeId);
					    	if(nodeBo.getNodeStatus() == BizStatusDefine.NONE_STATUS || nodeBo.getNodeType() == BizNodeTypeDefine.DELETE_INSTANCE_TYPE){
					    		//??????????????????
					    		statusHealth = statusHealth.replaceAll("\\$\\{" + bizNodeId + "\\}", "-1");
					    	}else{
					    		int curScore = getScoreByAlarmLevel(nodeBo.getNodeStatus(),alarmInfo);
					    		if(curScore < minimum){
					    			alarmNodeTypeInt = nodeBo.getNodeType();
					    			alarmNodeInstanceId = nodeBo.getInstanceId();
					    			alarmNodeName = nodeBo.getShowName();
					    			alarmNodeId = nodeBo.getId();
					    		}
					    		statusHealth = statusHealth.replaceAll("\\$\\{" + bizNodeId + "\\}", curScore + "");
					    	}
					    }
					    
					    //???????????????
					    newHealthScore = Integer.parseInt(getScore(statusHealth,Integer.parseInt(alarmInfo.getDeathThreshold())));
					    newAlarmLevel = getAlarmLevelByScore(newHealthScore, alarmInfo);
					}
					
					//???????????????????????????????????????????????????
					BizCanvasNodeBo bizNode = new BizCanvasNodeBo();
					bizNode.setInstanceId(bizId);
					bizNode.setNodeType(BizNodeTypeDefine.BUSINESS_TYPE);
					bizNode.setNodeStatus(newAlarmLevel);
					bizNode.setStatusTime(new Date());
					bizCanvasDao.updateCanvasNodeStatusInfo(bizNode);
					
					BizHealthHisBo oldHealthHis = bizHealthHisDao.getBizHealthHis(bizId);
					if(oldHealthHis == null){
						oldHealthHis = new BizHealthHisBo();
						oldHealthHis.setBizHealth(100);
						oldHealthHis.setBizStatus(0);
					}
					
					BizHealthHisBo healthHis = new BizHealthHisBo();
					healthHis.setBizChangeTime(new Date());
					healthHis.setBizHealth(newHealthScore);
					healthHis.setBizStatus(newAlarmLevel);
					healthHis.setBizId(bizId);
					bizHealthHisDao.insertHealthHis(healthHis);
					
					healthChangeMap.put(bizId, oldHealthHis.getBizHealth() + "," + newHealthScore);
					
					logger.error("check bizId : " + bizId + ",old status : " + oldHealthHis.getBizStatus() + ", new status : " + newAlarmLevel);
					
					if(oldHealthHis.getBizStatus() != newAlarmLevel){
						//???????????????????????????
						
						if(newAlarmLevel > 0){
							if(alarmNodeTypeInt == BizNodeTypeDefine.BUSINESS_TYPE){
								alarmNodeType =  "????????????";
								if(healthChangeMap.get(alarmNodeInstanceId) == null){
									BizHealthHisBo childBizHealthHis = bizHealthHisDao.getBizHealthHis(alarmNodeInstanceId);
									if(childBizHealthHis == null){
										childBizHealthHis = new BizHealthHisBo();
										childBizHealthHis.setBizHealth(100);
									}
									alarmNodeContent = "???????????????" + alarmNodeName + "????????????????????????" + childBizHealthHis.getBizHealth() + "???";
								}else{
									alarmNodeContent = "???????????????" + alarmNodeName + "?????????????????????" + 
											healthChangeMap.get(alarmNodeInstanceId).split(",")[0] + "????????????" + healthChangeMap.get(alarmNodeInstanceId).split(",")[1] + "???";
								}
							}else{
								ResourceInstance instance = null;
								try {
									instance = resourceInstanceService.getResourceInstance(alarmNodeInstanceId);
									alarmNodeType = capacityService.getResourceDefById(instance.getResourceId()).getName();
									alarmNodeContent = queryNodeLastAlarm(alarmNodeId,alarmNodeInstanceId);
								} catch (InstancelibException e) {
									logger.error(e.getMessage(),e);
								} catch (Exception e) {
									logger.error(e.getMessage(),e);
								}
							}
						}
						
						
						BizAlarmParameterDefine alarmParameterDefine = new BizAlarmParameterDefine();
						
						BizMainBo mainInfo = bizMainDao.getBasicInfo(bizId);
						
						if(newAlarmLevel > 0){
							alarmParameterDefine.setAlarmNodeContent(alarmNodeContent);
							alarmParameterDefine.setAlarmNodeName(alarmNodeName);
							alarmParameterDefine.setAlarmNodeType(alarmNodeType);
							alarmParameterDefine.setBizAlarmLevel(getAlarmInfoByLevel(newAlarmLevel));
						}else{
							alarmParameterDefine.setAlarmNodeContent("");
							alarmParameterDefine.setAlarmNodeName("");
							alarmParameterDefine.setAlarmNodeType("");
							alarmParameterDefine.setBizAlarmLevel("");
						}
						alarmParameterDefine.setBizHealth(newHealthScore + "");
						alarmParameterDefine.setBizManager(mainInfo.getManagerName());
						alarmParameterDefine.setBizName(mainInfo.getName());
						
						String alarmTitle = generateAlarmTitle(newAlarmLevel,mainInfo.getName());
						
						String alarmContent = generateBizAlarmContent(alarmInfo,newAlarmLevel,alarmParameterDefine);
						addAlarm(alarmContent,alarmTitle, mainInfo, newAlarmLevel);
					}
					
					
					break;
				}
			}
		}		
	}
	
	private String queryNodeLastAlarm(long nodeId,long instanceId){
		
		AlarmEventQuery2 eq = new AlarmEventQuery2();
		List<AlarmEventQueryDetail> filters=new ArrayList<>();
		AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
		detail.setSysID(SysModuleEnum.MONITOR);
		detail.setRecovered(false);
		
		//????????????ID????????????????????????
		List<String> sList = new  ArrayList<String>();
		BizInstanceNodeBo node = bizCanvasDao.getInstanceNode(nodeId);
		
		List<BizNodeMetricRelBo> metricbos = null;
		
		if(node!=null){
			if(node.getType()==1){//?????????
				
				List<ResourceInstance> resourcechildList = null;
				try {
					resourcechildList = resourceInstanceService.getChildInstanceByParentId(node.getInstanceId());
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
				for(int j = 0; resourcechildList != null && j < resourcechildList.size(); j++){
					ResourceInstance  riList = resourcechildList.get(j);
					if(riList.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
						sList.add(String.valueOf(riList.getId()));
					}
				}
				sList.add(String.valueOf(node.getInstanceId()));
			}else if(node.getType() == 2){//?????????
				
				List<BizNodeMetricRelBo> bos = bizCanvasDao.getChildInstanceBindRelation(node.getId());
				if(bos!=null){
					for (BizNodeMetricRelBo bizNodeMetricRelBo : bos) {
						sList.add(String.valueOf(bizNodeMetricRelBo.getChildInstanceId()));
					}
				}
			}else if(node.getType()==3){//??????
				
				metricbos = bizCanvasDao.getChildInstanceMetricBindRelation(node.getId());
				if(metricbos!=null){
					for (BizNodeMetricRelBo bizNodeMetricRelBo : metricbos) {
						if(bizNodeMetricRelBo.getChildInstanceId() <= 0){
							sList.add(String.valueOf(node.getInstanceId()));
						}else{
							sList.add(String.valueOf(bizNodeMetricRelBo.getChildInstanceId()));
						}
					}
				}
			
			}
		}
		
		detail.setSourceIDes(sList);
		
		filters.add(detail);
		eq.setFilters(filters);
		eq.setOrderASC(true);
		eq.setOrderFieldes(new OrderField[]{OrderField.LEVEL});
		List<AlarmEvent> events= alarmEventService.findAlarmEvent(eq);
		
		String alarmContent = "";
		
		if(node.getType() == 3){
			//????????????
			if(events != null && events.size() > 0){
				for(AlarmEvent event : events){
					for(BizNodeMetricRelBo metric : metricbos){
						if(metric.getChildInstanceId() <= 0){
							//??????????????????
							if(event.getSourceID().equals(String.valueOf(node.getInstanceId())) && event.getExt3().equals(metric.getMetricId())){
								alarmContent = event.getContent();
								break;
							}
						}else{
							//??????????????????
							if(event.getSourceID().equals(String.valueOf(metric.getChildInstanceId())) && event.getExt3().equals(metric.getMetricId())){
								alarmContent = event.getContent();
								break;
							}
						}
					}
				}
			}
		}else if(node.getType() == 1){
			//?????????
			if(events != null && events.size() > 0){
				 InstanceStateData stateData = instanceStateService.getStateAdapter(instanceId);
			
				 if(stateData == null){
					 alarmContent = events.get(0).getContent();
				 }else{
					 for(AlarmEvent event : events){
						 if(Long.parseLong(event.getSourceID()) == stateData.getCauseByInstance()){
							 alarmContent = event.getContent();
							 break;
						 }
					 }
					 
					 if(alarmContent.equals("")){
						 alarmContent = events.get(0).getContent();
					 }
				 }
				 
			}
		}else{
			//?????????
			if(events != null && events.size() > 0){
				alarmContent = events.get(0).getContent();
			}
		}
		
		return alarmContent;
		
	}
	
	public static void main(String[] args) {
		BizMainImpl impl = new BizMainImpl();
		
		List<Long[]> topoList = new ArrayList<Long[]>();
		
		topoList.add(new Long[]{14L,15L});
		topoList.add(new Long[]{14L,31L});
		topoList.add(new Long[]{15L,31L});
		topoList.add(new Long[]{1516L,3147L});
		topoList.add(new Long[]{1515L,3001L});
		topoList.add(new Long[]{151L,314L});
		topoList.add(new Long[]{1360L,3001L});
		topoList.add(new Long[]{1610L,3001L});
		topoList.add(new Long[]{1516L,3001L});
		
		topoList.add(new Long[]{1607L,3001L});
		
		
		
		
		List<List<Long>> resultBizId = new ArrayList<List<Long>>();
		
		resultBizId = impl.getBizChangeOrder(topoList);
		
		for(List<Long> a : resultBizId){
			System.out.println("****");
			for(Long b : a){
				System.out.println(b);
			}
		}
		
//		topoList.add(new Long[]{1L,2L});
//		topoList.add(new Long[]{2L,4L});
//		topoList.add(new Long[]{1L,4L});
//		topoList.add(new Long[]{3L,4L});
//		topoList.add(new Long[]{1L,3L});
//		
//		List<Long> list = impl.loopZeroStartPointByNode(topoList,new ArrayList<Long>(),2);
//		
//		for(Long s : list){
//			System.out.println(s);
//		}
		
//		String statusDefine = "or(${102503},3*${102501},2*${102503},3*${102504},4*${102505},5*${102501})";
//		statusDefine = statusDefine.toUpperCase();
//		
//		List<Long> deleteIds = new ArrayList<Long>();
//		deleteIds.add(102503L);
//		
//		for(long nodeId : deleteIds){
//			
//			statusDefine = statusDefine.replaceAll("\\$\\{" + nodeId + "\\}", "");
//			
//		}
//		
//		if(!statusDefine.contains("$")){
//			statusDefine = "";
//		}else{
//			statusDefine = statusDefine.replaceAll("\\,([1-5]{1}\\*)?\\,", ",")
//					.replaceAll("\\,([1-5]{1}\\*)?\\)", ")")
//					.replaceAll("\\(([1-5]{1}\\*)?\\,", "(");
//			while (statusDefine.contains("AND()") || statusDefine.contains("AVG()") || statusDefine.contains("OR()")) {
//				statusDefine = statusDefine.replaceAll("\\,(AND|OR|AVG)\\(\\)\\,", ",")
//						.replaceAll("\\((AND|OR|AVG)\\(\\)\\,", "(")
//						.replaceAll("\\,(AND|OR|AVG)\\(\\)\\)", ")");
//			}
//		}
//		
//			
//		
//		System.out.println("statusDefine : " + statusDefine);
		
//		topoList.add(new Long[]{10L,8L});
//		
//		boolean result = impl.iterator(topoList, 10, 10);
//		System.out.println("result : " + result);
	}

	@Override
	public Page<BizMainDataBo, Object> getPageListForGridOrder(ILoginUser user,
			int status, Date startTime, Date endTime, String queryName,
			int startNum, int pageSize, String order, String sort) {
		
		Page<BizMainDataBo, Object> pageResult = new Page<BizMainDataBo, Object>();
		
		List<BizMainDataBo> resultList = new ArrayList<BizMainDataBo>();
		
		List<BizMainBo> bizMainList = bizMainDao.getAllList();
		List<BizMainBo> bizQueryList = new ArrayList<BizMainBo>();
		
		if(bizMainList == null || bizMainList.size() <= 0){
			pageResult.setDatas(resultList);
			pageResult.setTotalRecord(0);
			return pageResult;
		}
		
		for(BizMainBo main : bizMainList){
			//????????????
			if(!bizUserRelApi.checkUserView(user.getId(), main.getId())){
				continue;
			}
			
			//????????????
			if(queryName != null && !queryName.equals("") && !(main.getName().toUpperCase().contains(queryName.toUpperCase()))){
				continue;
			}
			
			BizHealthHisBo healthHis = bizHealthHisDao.getBizHealthHis(main.getId());
			int curStatus = 0;
			if(healthHis == null){
				curStatus = 0;
				main.setStatus(0);
				main.setHealth(100);
			}else{
				curStatus = healthHis.getBizStatus();
				main.setStatus(healthHis.getBizStatus());
				main.setHealth(healthHis.getBizHealth());
			}
			
			if(status >= 0){
				//??????????????????
				if(curStatus != status){
					continue;
				}
			}
			
			bizQueryList.add(main);
		}
		
		if(bizQueryList == null || bizQueryList.size() <= 0){
			pageResult.setDatas(resultList);
			pageResult.setTotalRecord(0);
			return pageResult;
		}

		pageResult.setTotalRecord(bizQueryList.size());
		
		//????????????
		if((startNum + pageSize) > bizQueryList.size()){
			bizQueryList = bizQueryList.subList(startNum, bizQueryList.size());
		}else{
			bizQueryList = bizQueryList.subList(startNum, (startNum + pageSize));
		}
		
		List<String> idList = new ArrayList<String>();
		for(BizMainBo main : bizQueryList){
			idList.add(main.getId() + "");
		}
		
		//???????????????????????????????????????
		List<AlarmEvent> eventList = getEventList(idList, startTime, endTime);
		
		for(BizMainBo main : bizQueryList){
			
			resultList.add(getSingleBizRunInfo(eventList,main,startTime,endTime));
			
		}
		List<BizMainDataBo> list=	sort(resultList, sort, order);
		pageResult.setDatas(list);
		
		return pageResult;
	}
	public List<BizMainDataBo> sort(List<BizMainDataBo> bos,
			final String field, String order) {
		if (!Util.isEmpty(order)) {
			if ("ASC".equals(order.toUpperCase())) {
				Collections.sort(bos, new Comparator<BizMainDataBo>() {
					@Override
					public int compare(BizMainDataBo bo1, BizMainDataBo bo2) {
                  
                       
                       if("bizName".equals(field)){
                    	   if(bo1.getBizName() == null && bo2.getBizName() == null){
                    		   return 0;
                    	   }else if(bo1.getBizName() == null && bo2.getBizName() != null){
                    		   return -1;
                    	   }else if(bo1.getBizName() != null && bo2.getBizName() == null){
                    		   return 1;
                    	   }else{
                    		   if(bo1.getBizName().compareToIgnoreCase(bo2.getBizName()) == 0){
                    			   return 0;
                    		   }else if(bo1.getBizName().compareToIgnoreCase(bo2.getBizName()) > 0){
                    			   return 1; 
                    		   }else{
                    			   return -1; 
                    		   }
                    	   }
                       }else if("healthScore".equals(field)){
                    	   if(String.valueOf(bo1.getHealthScore()) == null && String.valueOf(bo2.getHealthScore()) == null){
                    		   return 0;
                    	   }else if(String.valueOf(bo1.getHealthScore()) == null && String.valueOf(bo2.getHealthScore()) != null){
                    		   return -1;
                    	   }else if(String.valueOf(bo1.getHealthScore()) != null && String.valueOf(bo2.getHealthScore()) == null){
                    		   return 1;
                    	   }else{
                    		   if(bo1.getHealthScore()==bo2.getHealthScore()){
                    			   return 0;
                    		   }else if(bo1.getHealthScore()>bo2.getHealthScore()){
                    			   return 1; 
                    		   }else{
                    			   return -1; 
                    		   }
                    	   }
                    	   
                       }else if("availableRate".equals(field)){
                    	   String  avail1=null;
                    	   String  avail2=null;
                    	   if(bo1.getAvailableRate()!=null && bo1.getAvailableRate()!=""){
                  			 avail1= bo1.getAvailableRate().substring(0,bo1.getAvailableRate().length()-1);
                  		   }
                  		   if(bo2.getAvailableRate()!=null && bo2.getAvailableRate()!=""){
                  			   avail2= bo2.getAvailableRate().substring(0,bo2.getAvailableRate().length()-1);
                  		   }
                    	   if(bo1.getAvailableRate() == null && bo2.getAvailableRate() == null){
                    		   return 0;
                    	   }else if(bo1.getAvailableRate() == null && bo2.getAvailableRate() != null){
                    		   return -1;
                    	   }else if(bo1.getAvailableRate() != null && bo2.getAvailableRate() == null){
                    		   return 1;
                    	   }else{
                    		   if(Float.parseFloat(avail1)==Float.parseFloat(avail2)){
                    			   return 0;
                    		   }else if(Float.parseFloat(avail1)>Float.parseFloat(avail2)){
                    			   return 1; 
                    		   }else{
                    			   return -1; 
                    		   }
                    	   }
                    	   
                       }else if("mttr".equals(field)){
                    	   String  mttr1=null;
                    	   String  mttr2=null;
                    	   if(bo1.getMTTR()!=null && bo1.getMTTR()!=""){
                   			String[] str=	bo1.getMTTR().split("\\(");
                   			if(str.length!=0){
                   				mttr1= str[0];	
                   			}
                   		   
                 		   }
                 		   if(bo1.getMTTR()!=null && bo2.getMTTR()!=""){
                 				String[] str=	bo2.getMTTR().split("\\(");
                 				if(str.length!=0){
                 					mttr2= str[0];
                   			}
                 				
                 		   }
                    	   if(mttr1 == null && mttr2 == null){
                    		   return 0;
                    	   }else if(mttr1 == null && mttr2 != null){
                    		   return -1;
                    	   }else if(mttr1 != null && mttr2 == null){
                    		   return 1;
                    	   }else{
                    		   if(Float.parseFloat(mttr1)==Float.parseFloat(mttr2)){
                    			   return 0;
                    		   }else if(Float.parseFloat(mttr1)>Float.parseFloat(mttr2)){
                    			   return 1; 
                    		   }else{
                    			   return -1; 
                    		   }
                    	   }
                    	   
                       }else if("mtbf".equals(field)){
                    	   String  mtbf1=null;
                    	   String  mtbf2=null;
                    	   if(bo1.getMTBF()!=null && bo1.getMTBF()!=""){
                   			String[] str=	bo1.getMTBF().split("\\(");
                   			if(str.length!=0){
                   			mtbf1= str[0];
                   			}
                 		   }
                 		   if(bo1.getMTBF()!=null && bo2.getMTBF()!=""){
                 				String[] str=	bo2.getMTBF().split("\\(");
                 				if(str.length!=0){
                 				mtbf2= str[0];
                 				}
                 		   }
                    	   if(mtbf1 == null && mtbf2 == null){
                    		   return 0;
                    	   }else if(mtbf1 == null && mtbf2 != null){
                    		   return -1;
                    	   }else if(mtbf1 != null && mtbf2 == null){
                    		   return 1;
                    	   }else{
                    		   if(Float.parseFloat(mtbf1)==Float.parseFloat(mtbf2)){
                    			   return 0;
                    		   }else if(Float.parseFloat(mtbf1)>Float.parseFloat(mtbf2)){
                    			   return 1; 
                    		   }else{
                    			   return -1; 
                    		   }
                    	   }
                    	   
                       }else if("outageTimes".equals(field)){
                    	   if(bo1.getOutageTimes() == null && bo2.getOutageTimes() == null){
                    		   return 0;
                    	   }else if(bo1.getOutageTimes() == null && bo2.getOutageTimes() != null){
                    		   return -1;
                    	   }else if(bo1.getOutageTimes() != null && bo2.getOutageTimes() == null){
                    		   return 1;
                    	   }else{
                    		   if(Integer.parseInt(bo1.getOutageTimes())==Integer.parseInt(bo2.getOutageTimes())){
                    			   return 0;
                    		   }else if(Integer.parseInt(bo1.getOutageTimes())>Integer.parseInt(bo2.getOutageTimes())){
                    			   return 1; 
                    		   }else{
                    			   return -1; 
                    		   }
                    	   }
                    	   
                       }else if("managerName".equals(field)){
                    	   if(bo1.getManagerName() == null && bo2.getManagerName() == null){
                    		   return 0;
                    	   }else if(bo1.getManagerName() == null && bo2.getManagerName() != null){
                    		   return -1;
                    	   }else if(bo1.getManagerName() != null && bo2.getManagerName() == null){
                    		   return 1;
                    	   }else{
                    		   if(bo1.getManagerName().compareToIgnoreCase(bo2.getManagerName()) == 0){
                    			   return 0;
                    		   }else if(bo1.getManagerName().compareToIgnoreCase(bo2.getManagerName()) > 0){
                    			   return 1; 
                    		   }else{
                    			   return -1; 
                    		   }
                    	   }
                    	   
                       }else if("phone".equals(field)){
                    	   if(bo1.getPhone() == null && bo2.getPhone() == null){
                    		   return 0;
                    	   }else if(bo1.getPhone() == null && bo2.getPhone() != null){
                    		   return -1;
                    	   }else if(bo1.getPhone() != null && bo2.getPhone() == null){
                    		   return 1;
                    	   }else{
                    		   if(bo1.getPhone().compareToIgnoreCase(bo2.getPhone()) == 0){
                    			   return 0;
                    		   }else if(bo1.getPhone().compareToIgnoreCase(bo2.getPhone()) > 0){
                    			   return 1; 
                    		   }else{
                    			   return -1; 
                    		   }
                    	   }
                    	   
                       }
                       
                       
                       
                      
						return 0;
					}

				});
				
			}else{
				Collections.sort(bos, new Comparator<BizMainDataBo>() {
					@Override
					public int compare(BizMainDataBo bo1, BizMainDataBo bo2) {
                  
                       
                       if("bizName".equals(field)){
                    	   if(bo1.getBizName() == null && bo2.getBizName() == null){
                    		   return 0;
                    	   }else if(bo1.getBizName() == null && bo2.getBizName() != null){
                    		   return 1;
                    	   }else if(bo1.getBizName() != null && bo2.getBizName() == null){
                    		   return -1;
                    	   }else{
                    		   if(bo1.getBizName().compareToIgnoreCase(bo2.getBizName()) == 0){
                    			   return 0;
                    		   }else if(bo1.getBizName().compareToIgnoreCase(bo2.getBizName()) > 0){
                    			   return -1; 
                    		   }else{
                    			   return 1; 
                    		   }
                    	   }
                       }else  if("healthScore".equals(field)){
                    	   if(String.valueOf(bo1.getHealthScore()) == null && String.valueOf(bo2.getHealthScore()) == null){
                    		   return 0;
                    	   }else if(String.valueOf(bo1.getHealthScore()) == null && String.valueOf(bo2.getHealthScore()) != null){
                    		   return 1;
                    	   }else if(String.valueOf(bo1.getHealthScore()) != null && String.valueOf(bo2.getHealthScore()) == null){
                    		   return -1;
                    	   }else{
                    		   if(bo1.getHealthScore()==bo2.getHealthScore()){
                    			   return 0;
                    		   }else if(bo1.getHealthScore()>bo2.getHealthScore()){
                    			   return -1; 
                    		   }else{
                    			   return 1; 
                    		   }
                    	   }
                       }else  if("availableRate".equals(field)){
                    	   String  avail1=null;
                    	   String  avail2=null;
                    	   if(bo1.getAvailableRate()!=null && bo1.getAvailableRate()!=""){
                  			 avail1= bo1.getAvailableRate().substring(0,bo1.getAvailableRate().length()-1);
                  		   }
                  		   if(avail1!=null && bo2.getAvailableRate()!=""){
                  			   avail2= bo2.getAvailableRate().substring(0,bo2.getAvailableRate().length()-1);
                  		   }
                    	   if(avail1 == null && avail2 == null){
                    		   return 0;
                    	   }else if(avail1 == null && avail2 != null){
                    		   return 1;
                    	   }else if(avail1 != null && avail2 == null){
                    		   return -1;
                    	   }else{
                    		 
                    		   if(Float.parseFloat(avail1)==Float.parseFloat(avail2)){
                    			   return 0;
                    		   }else if(Float.parseFloat(avail1)>=Float.parseFloat(avail2)){
                    			   return -1; 
                    		   }else{
                    			   return 1; 
                    		   }
                    	   }
                       }else  if("mttr".equals(field)){
                    	   String  mttr1=null;
                    	   String  mttr2=null;
                    	   if(bo1.getMTTR()!=null && bo1.getMTTR()!=""){
                    			String[] str=	bo1.getMTTR().split("\\(");
                    			if(str.length!=0){
                    				mttr1= str[0];	
                    			}
                    		   
                  		   }
                  		   if(bo1.getMTTR()!=null && bo2.getMTTR()!=""){
                  				String[] str=	bo2.getMTTR().split("\\(");
                  				if(str.length!=0){
                  					mttr2= str[0];
                    			}
                  				
                  		   }
                    	   
                    	   if(mttr1 == null && mttr2 == null){
                    		   return 0;
                    	   }else if(mttr1 == null && mttr2 != null){
                    		   return 1;
                    	   }else if(mttr1 != null && mttr2 == null){
                    		   return -1;
                    	   }else{
                    		   if(Float.parseFloat(mttr1)==Float.parseFloat(mttr2)){
                    			   return 0;
                    		   }else if(Float.parseFloat(mttr1)>Float.parseFloat(mttr2)){
                    			   return -1; 
                    		   }else{
                    			   return 1; 
                    		   }
                    	   }
                       }else  if("mtbf".equals(field)){
                    	   String  mtbf1=null;
                    	   String  mtbf2=null;
                    	   if(bo1.getMTBF()!=null && bo1.getMTBF()!=""){
                      			String[] str=	bo1.getMTBF().split("\\(");
                      			if(str.length!=0){
                      				mtbf1= str[0];
                    			}
                      			
                    		   }
                    		   if(bo1.getMTBF()!=null && bo2.getMTBF()!=""){
                    				String[] str=	bo2.getMTBF().split("\\(");
                    				if(str.length!=0){
                    					mtbf2= str[0];
                        			}
                    				
                    		   }
                    	   if(mtbf1 == null && mtbf2 == null){
                    		   return 0;
                    	   }else if(mtbf1 == null && mtbf2 != null){
                    		   return 1;
                    	   }else if(mtbf1 != null && mtbf2 == null){
                    		   return -1;
                    	   }else{
                    		   if(Float.parseFloat(mtbf1)==Float.parseFloat(mtbf2)){
                    			   return 0;
                    		   }else if(Float.parseFloat(mtbf1)>Float.parseFloat(mtbf2)){
                    			   return -1; 
                    		   }else{
                    			   return 1; 
                    		   }
                    	   }
                       }else  if("outageTimes".equals(field)){
                    	   if(bo1.getOutageTimes() == null && bo2.getOutageTimes() == null){
                    		   return 0;
                    	   }else if(bo1.getOutageTimes() == null && bo2.getOutageTimes() != null){
                    		   return 1;
                    	   }else if(bo1.getOutageTimes() != null && bo2.getOutageTimes() == null){
                    		   return -1;
                    	   }else{
                    		   if(Integer.parseInt(bo1.getOutageTimes())==Integer.parseInt(bo2.getOutageTimes())){
                    			   return 0;
                    		   }else if(Integer.parseInt(bo1.getOutageTimes())>Integer.parseInt(bo2.getOutageTimes())){
                    			   return -1; 
                    		   }else{
                    			   return 1; 
                    		   }
                    	   }
                       }else  if("managerName".equals(field)){
                    	   if(bo1.getManagerName() == null && bo2.getManagerName() == null){
                    		   return 0;
                    	   }else if(bo1.getManagerName() == null && bo2.getManagerName() != null){
                    		   return 1;
                    	   }else if(bo1.getManagerName() != null && bo2.getManagerName() == null){
                    		   return -1;
                    	   }else{
                    		   if(bo1.getManagerName().compareToIgnoreCase(bo2.getManagerName()) == 0){
                    			   return 0;
                    		   }else if(bo1.getManagerName().compareToIgnoreCase(bo2.getManagerName()) > 0){
                    			   return -1; 
                    		   }else{
                    			   return 1; 
                    		   }
                    	   }
                       }else  if("phone".equals(field)){
                    	   if(bo1.getPhone() == null && bo2.getPhone() == null){
                    		   return 0;
                    	   }else if(bo1.getPhone() == null && bo2.getPhone() != null){
                    		   return 1;
                    	   }else if(bo1.getPhone() != null && bo2.getPhone() == null){
                    		   return -1;
                    	   }else{
                    		   if(bo1.getPhone().compareToIgnoreCase(bo2.getPhone()) == 0){
                    			   return 0;
                    		   }else if(bo1.getPhone().compareToIgnoreCase(bo2.getPhone()) > 0){
                    			   return -1; 
                    		   }else{
                    			   return 1; 
                    		   }
                    	   }
                       }
                       
                       
                       
                      
						return 0;
					}

				});
			}
			
		}
		return bos;
	}

	@Override
	public Map<String, Object> getFocusMetricDataForHome(Long bizId,String metric) {
		
		if(metric == null || metric.equals("")){
			return null;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Map<String, Object> result = new HashMap<String,Object>();
		
		Date endTime = new Date();
		
		Calendar calendar = Calendar.getInstance(); 
		calendar.add(Calendar.DATE, -7);
		Date startTime = calendar.getTime();
		
		switch (metric) {
		case BizMetricDefine.AVAILAVLE_RATE:
			
			List<String> ids = new ArrayList<String>();
			ids.add(bizId + "");
			
			List<AlarmEvent> eventList = getEventList(ids, startTime, endTime);
			
			BizMainBo bizInfo = bizMainDao.getBasicInfo(bizId);
			
			//?????????????????????????????????????????????
			long runTime = 0;
			long createTime = bizInfo.getCreateTime().getTime();
			if(endTime.getTime() > createTime){
				if(startTime.getTime() < createTime){
					runTime = (endTime.getTime() - createTime) / 1000;
				}else{
					runTime = (endTime.getTime() - startTime.getTime()) / 1000;
				}
			}
			
			//??????????????????
			//?????????????????????????????????????????????????????????
			long criticalTime = 0;
			
			//?????????????????????????????????????????????????????????
			int criticalCount = 0;
			List<AlarmEvent> curEventList = new ArrayList<AlarmEvent>();
			if(eventList != null && eventList.size() > 0){
				for(AlarmEvent event : eventList){
					if(event.getSourceID().equals(bizId)){
						curEventList.add(event);
					}
				}
			}
			
			if(curEventList != null && curEventList.size() > 0){
				for(int i = 0 ; i < curEventList.size() ; i++){
					AlarmEvent event = curEventList.get(i);
					if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
						if(i > 0){
							AlarmEvent nextEvent = curEventList.get(i - 1);
							criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
						}else{
							criticalTime += (endTime.getTime() - event.getCollectionTime().getTime()) / 1000;
						}
						criticalCount++;
					}
				}
			}
			
			//?????????
			BizHealthHisBo health = bizHealthHisDao.getBizHealthHis(bizId);
			
			int status = 0;
			
			if(health != null){
				status = health.getBizStatus();
			}
			
			if(criticalCount == 0 && status == BizStatusDefine.DEATH_STATUS){
				criticalTime = runTime;
				criticalCount++;
			}
			
			//???????????????
			result.put("currentVal", getAvailableRateMetricValue(runTime,criticalTime));
			result.put("id", BizMetricDefine.AVAILAVLE_RATE);
			result.put("isAlarm", false);
			result.put("isCustomMetric", false);
			result.put("isTable", false);
			result.put("lastCollTime", dateFormat.format(new Date()));
			result.put("status", "NORMAL");
			result.put("text", BizMetricDefine.AVAILAVLE_RATE_NAME);
			result.put("thresholds", "['%',0,100]");
			result.put("type", "PerformanceMetric");
			result.put("unit", "%");
			
			break;
		case BizMetricDefine.BIZ_HEALTH_STATUS:
			
			ids = new ArrayList<String>();
			ids.add(bizId + "");
			
			bizInfo = bizMainDao.getBasicInfo(bizId);
			
			//?????????
			health = bizHealthHisDao.getBizHealthHis(bizId);
			if(health == null){
				result.put("currentVal", 100);
				result.put("status", "NORMAL");
			}else{
				result.put("currentVal", health.getBizHealth());
				if(health.getBizStatus() == 3){
					result.put("status", "CRITICAL");
				}else if(health.getBizStatus() == 2){
					result.put("status", "SERIOUS");
				}else if(health.getBizStatus() == 1){
					result.put("status", "WARN");
				}else{
					result.put("status", "NORMAL");
				}
			}
			
			//???????????????
			result.put("id", BizMetricDefine.BIZ_HEALTH_STATUS);
			result.put("isAlarm", true);
			result.put("isCustomMetric", false);
			result.put("isTable", false);
			result.put("lastCollTime", dateFormat.format(health.getBizChangeTime()));
			result.put("text", BizMetricDefine.BIZ_HEALTH_STATUS_NAME);
			result.put("thresholds", "['???',0,100]");
			result.put("type", "PerformanceMetric");
			result.put("unit", "???");

			break;
		case BizMetricDefine.MTBF:
			
			ids = new ArrayList<String>();
			ids.add(bizId + "");
			
			eventList = getEventList(ids, startTime, endTime);
			
			bizInfo = bizMainDao.getBasicInfo(bizId);
			
			//?????????????????????????????????????????????
			runTime = 0;
			createTime = bizInfo.getCreateTime().getTime();
			if(endTime.getTime() > createTime){
				if(startTime.getTime() < createTime){
					runTime = (endTime.getTime() - createTime) / 1000;
				}else{
					runTime = (endTime.getTime() - startTime.getTime()) / 1000;
				}
			}
			
			//??????????????????
			//?????????????????????????????????????????????????????????
			criticalTime = 0;
			
			//?????????????????????????????????????????????????????????
			criticalCount = 0;
			curEventList = new ArrayList<AlarmEvent>();
			if(eventList != null && eventList.size() > 0){
				for(AlarmEvent event : eventList){
					if(event.getSourceID().equals(bizId)){
						curEventList.add(event);
					}
				}
			}
			
			boolean lastIsCritical = false;
			
			if(curEventList != null && curEventList.size() > 0){
				for(int i = 0 ; i < curEventList.size() ; i++){
					AlarmEvent event = curEventList.get(i);
					if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
						if(i > 0){
							AlarmEvent nextEvent = curEventList.get(i - 1);
							criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
						}else{
							criticalTime += (endTime.getTime() - event.getCollectionTime().getTime()) / 1000;
							lastIsCritical = true;
						}
						criticalCount++;
					}
				}
			}
			
			//?????????
			health = bizHealthHisDao.getBizHealthHis(bizId);
			
			status = 0;
			
			if(health != null){
				status = health.getBizStatus();
			}
			
			if(criticalCount == 0 && status == BizStatusDefine.DEATH_STATUS){
				criticalTime = runTime;
				criticalCount++;
				lastIsCritical = true;
			}
			
			result.put("currentVal", getMTBFMetricValue(runTime, criticalTime, lastIsCritical ? criticalCount : criticalCount + 1));
			result.put("id", BizMetricDefine.MTBF);
			result.put("isAlarm", false);
			result.put("isCustomMetric", false);
			result.put("isTable", false);
			result.put("lastCollTime", dateFormat.format(new Date()));
			result.put("text", BizMetricDefine.MTBF_NAME);
			result.put("status", "NORMAL");
			result.put("thresholds", "['???',0,100]");
			result.put("type", "PerformanceMetric");
			result.put("unit", "???");
			
			break;
		case BizMetricDefine.MTTR:
			
			ids = new ArrayList<String>();
			ids.add(bizId + "");
			
			eventList = getEventList(ids, startTime, endTime);
			
			bizInfo = bizMainDao.getBasicInfo(bizId);
			
			//?????????????????????????????????????????????
			runTime = 0;
			createTime = bizInfo.getCreateTime().getTime();
			if(endTime.getTime() > createTime){
				if(startTime.getTime() < createTime){
					runTime = (endTime.getTime() - createTime) / 1000;
				}else{
					runTime = (endTime.getTime() - startTime.getTime()) / 1000;
				}
			}
			
			//??????????????????
			//?????????????????????????????????????????????????????????
			criticalTime = 0;
			
			//?????????????????????????????????????????????????????????
			criticalCount = 0;
			curEventList = new ArrayList<AlarmEvent>();
			if(eventList != null && eventList.size() > 0){
				for(AlarmEvent event : eventList){
					if(event.getSourceID().equals(bizId)){
						curEventList.add(event);
					}
				}
			}
			
			if(curEventList != null && curEventList.size() > 0){
				for(int i = 0 ; i < curEventList.size() ; i++){
					AlarmEvent event = curEventList.get(i);
					if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
						if(i > 0){
							AlarmEvent nextEvent = curEventList.get(i - 1);
							criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
						}else{
							criticalTime += (endTime.getTime() - event.getCollectionTime().getTime()) / 1000;
						}
						criticalCount++;
					}
				}
			}
			
			//?????????
			health = bizHealthHisDao.getBizHealthHis(bizId);
			
			status = 0;
			
			if(health != null){
				status = health.getBizStatus();
			}
			
			if(criticalCount == 0 && status == BizStatusDefine.DEATH_STATUS){
				criticalTime = runTime;
				criticalCount++;
			}
			
			result.put("currentVal", getMTTRMetricValue(criticalTime, criticalCount));
			result.put("id", BizMetricDefine.MTTR);
			result.put("isAlarm", false);
			result.put("isCustomMetric", false);
			result.put("isTable", false);
			result.put("lastCollTime", dateFormat.format(new Date()));
			result.put("text", BizMetricDefine.MTTR_NAME);
			result.put("status", "NORMAL");
			result.put("thresholds", "['??????',0,100]");
			result.put("type", "PerformanceMetric");
			result.put("unit", "??????");
			
			break;
		case BizMetricDefine.RESPONSE_TIME:
			
			ids = new ArrayList<String>();
			ids.add(bizId + "");
			
			bizInfo = bizMainDao.getBasicInfo(bizId);
			//??????????????????
			List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(bizId, BizMetricDefine.RESPONSE_TIME);
			
			if(capMetricList == null){
				capMetricList = new ArrayList<Long>();
			}
			
			Set<Long> urlList = bizCanvasApi.getResponeTimeMetricInstanceList(bizId);
			
			if(urlList == null || urlList.size() <= 0){
				result.put("currentVal", Double.valueOf(0));
			}else{
				
				List<Double> datas = new ArrayList<Double>();
				
				for(long urlId : urlList){
					if(capMetricList.contains(urlId)){
						continue;
					}
					
					MetricRealtimeDataQuery metricRealtimeDataQuery = new MetricRealtimeDataQuery();
					metricRealtimeDataQuery.setInstanceID(new long[]{urlId});
					metricRealtimeDataQuery.setMetricID(new String[]{BizMetricDefine.RESPONSE_TIME});
					
					Page<Map<String,?>,MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(metricRealtimeDataQuery, 0, 1);
					List<Map<String,?>> realTimeDataList = page.getDatas();
					
					if(realTimeDataList == null || realTimeDataList.size() <= 0){
						continue;
					}
					
					Map<String,?> map =	realTimeDataList.get(0);
					
					if(map.get(BizMetricDefine.RESPONSE_TIME) == null){
						continue;
					}
					
					datas.add(Double.parseDouble(map.get(BizMetricDefine.RESPONSE_TIME).toString()));
					
				}
				
				if(datas.size() <= 0){
					result.put("currentVal", Double.valueOf(0));
				}else{
					Collections.sort(datas);
					result.put("currentVal", datas.get(datas.size() - 1));
				}
				
			}
			
			result.put("id", BizMetricDefine.RESPONSE_TIME);
			result.put("isAlarm", false);
			result.put("isCustomMetric", false);
			result.put("isTable", false);
			result.put("lastCollTime", dateFormat.format(new Date()));
			result.put("text", BizMetricDefine.RESPONSE_TIME_NAME);
			result.put("status", "NORMAL");
			result.put("thresholds", "['ms',0,100]");
			result.put("type", "PerformanceMetric");
			result.put("unit", "ms");
			
			break;
		case BizMetricDefine.BANDWIDTH_CAPACITY:
			
			ids = new ArrayList<String>();
			ids.add(bizId + "");
			
			bizInfo = bizMainDao.getBasicInfo(bizId);

			double totalSize = 0;
			double alreadyUseSize = 0;
			
			//??????????????????
			capMetricList = bizCapMetricApi.getByBizIdAndMetric(bizId, BizMetricDefine.BANDWIDTH_CAPACITY);
			
			if(capMetricList == null){
				capMetricList = new ArrayList<Long>();
			}
			
			urlList = bizCanvasApi.getBandwidthMetricInstanceList(bizId);
			
			if(urlList == null || urlList.size() <= 0){
				result.put("currentVal", Double.valueOf(0));
			}else{
				
				for(long urlId : urlList){
					
					if(capMetricList.contains(urlId)){
						continue;
					}
					
					String[] value = null;
					//?????????????????????????????????????????????
					boolean isHaveDefined = false;
					List<CustomModuleProp> allModuleProp = customModulePropService.getCustomModuleProp();
					if(allModuleProp != null && allModuleProp.size() > 0){
						  for(CustomModuleProp cmp:allModuleProp){
							  if(cmp.getInstanceId() == urlId && cmp.getKey().equals("ifSpeed")){
								  isHaveDefined = true;
								  value = new String[]{cmp.getUserValue()};
								  break;
							  }
						  }
					}
					
					if(!isHaveDefined){
						MetricData sizeMetricData = metricDataService.getMetricInfoData(urlId, "ifSpeed");
						if(sizeMetricData == null){
							continue;
						}
						value = sizeMetricData.getData();
					}
					
					
					
					MetricData utilMetricData = metricDataService.getMetricPerformanceData(urlId, "ifBandWidthUtil");
					if(utilMetricData == null){
						continue;
					}
					if(value != null && value.length > 0){
						if(value[0] != null && !value[0].equals("")){
							double size = Double.parseDouble(value[0]);
							totalSize += size;
							String[] utilValue = utilMetricData.getData();
							if(utilValue != null && utilValue.length > 0){
								if(utilValue[0] != null && !utilValue[0].equals("") && !utilValue[0].equals("0.00")){
									alreadyUseSize += size * Double.parseDouble(utilValue[0]) / 100;
								}
							}
						}
					}
				}
				
			}
			
			if(alreadyUseSize != 0){
				result.put("currentVal", (alreadyUseSize / totalSize) * 100);
			}else{
				result.put("currentVal", Double.valueOf(0));
			}
			
			result.put("id", BizMetricDefine.BANDWIDTH_CAPACITY);
			result.put("isAlarm", false);
			result.put("isCustomMetric", false);
			result.put("isTable", false);
			result.put("lastCollTime", dateFormat.format(new Date()));
			result.put("text", BizMetricDefine.BANDWIDTH_CAPACITY_NAME);
			result.put("status", "NORMAL");
			result.put("thresholds", "['%',0,100]");
			result.put("type", "PerformanceMetric");
			result.put("unit", "%");
			
			break;
		case BizMetricDefine.DATABASE_CAPACITY:
			
			ids = new ArrayList<String>();
			ids.add(bizId + "");
			
			bizInfo = bizMainDao.getBasicInfo(bizId);

			totalSize = 0;
			alreadyUseSize = 0;
			
			//?????????????????????
			capMetricList = bizCapMetricApi.getByBizIdAndMetric(bizId, BizMetricDefine.DATABASE_CAPACITY);
			
			if(capMetricList == null){
				capMetricList = new ArrayList<Long>();
			}
			
			urlList = bizCanvasApi.getDatabaseMetricInstanceList(bizId);
			
			if(urlList == null || urlList.size() <= 0){
				result.put("currentVal", Double.valueOf(0));
			}else{
				
				for(long urlId : urlList){
					
					if(capMetricList.contains(urlId)){
						continue;
					}
					
					MetricData sizeMetricData = metricDataService.getMetricInfoData(urlId, "tableSpaceSize");
					if(sizeMetricData == null){
						continue;
					}
					ResourceMetricDef metricDef = null;
					try {
						metricDef = capacityService.getResourceMetricDef(resourceInstanceService.getResourceInstance(urlId).getResourceId(), "tableSpaceSize");
					} catch (InstancelibException e) {
						logger.error(e.getMessage(),e);
					}
					String[] value = sizeMetricData.getData();
					MetricData utilMetricData = metricDataService.getMetricPerformanceData(urlId, "tableSpaceUtil");
					if(utilMetricData == null){
						continue;
					}
					if(value != null && value.length > 0){
						if(value[0] != null && !value[0].equals("")){
							double size = unitMBTrans(Double.parseDouble(value[0]), metricDef.getUnit());
							totalSize += size;
							String[] utilValue = utilMetricData.getData();
							if(utilValue != null && utilValue.length > 0){
								if(utilValue[0] != null && !utilValue[0].equals("") && !utilValue[0].equals("0.00")){
									alreadyUseSize += size * Double.parseDouble(utilValue[0]) / 100;
								}
							}
						}
					}
				}
				
			}
			
			if(alreadyUseSize != 0){
				result.put("currentVal", (alreadyUseSize / totalSize) * 100);
			}else{
				result.put("currentVal", Double.valueOf(0));
			}
			
			result.put("id", BizMetricDefine.DATABASE_CAPACITY);
			result.put("isAlarm", false);
			result.put("isCustomMetric", false);
			result.put("isTable", false);
			result.put("lastCollTime", dateFormat.format(new Date()));
			result.put("text", BizMetricDefine.DATABASE_CAPACITY_NAME);
			result.put("status", "NORMAL");
			result.put("thresholds", "['%',0,100]");
			result.put("type", "PerformanceMetric");
			result.put("unit", "%");
			
			break;
		case BizMetricDefine.HOST_CAPACITY:
			
			ids = new ArrayList<String>();
			ids.add(bizId + "");
			
			bizInfo = bizMainDao.getBasicInfo(bizId);

			//??????????????????
			capMetricList = bizCapMetricApi.getByBizIdAndMetric(bizId, BizMetricDefine.HOST_CAPACITY);
			
			if(capMetricList == null){
				capMetricList = new ArrayList<Long>();
			}
			
			urlList = bizCanvasApi.getCalculateMetricInstanceList(bizId);
			
			if(urlList == null || urlList.size() <= 0){
				result.put("currentVal", Double.valueOf(0));
			}else{
				
				List<Double> datas = new ArrayList<Double>();
				
				for(long urlId : urlList){
					if(capMetricList.contains(urlId)){
						continue;
					}
					InstanceStateData state = instanceStateService.getStateAdapter(urlId);
					if(state != null && state.getState().equals(InstanceStateEnum.CRITICAL)){
						continue;
					}
					MetricData instanceMetricData = metricDataService.getMetricPerformanceData(urlId, MetricIdConsts.METRIC_CPU_RATE);
					if(instanceMetricData == null){
						continue;
					}
					String[] value = instanceMetricData.getData();
					if(value != null && value.length > 0){
						if(value[0] != null && !value[0].equals("")){
							datas.add(Double.parseDouble(value[0]));
						}
					}
				}
				
				if(datas.size() <= 0){
					result.put("currentVal", Double.valueOf(0));
				}else{
					Collections.sort(datas);
					result.put("currentVal", datas.get(datas.size() - 1));
				}
				
			}
			
			//???????????????
			result.put("id", BizMetricDefine.HOST_CAPACITY);
			result.put("isAlarm", false);
			result.put("isCustomMetric", false);
			result.put("isTable", false);
			result.put("lastCollTime", dateFormat.format(new Date()));
			result.put("text", BizMetricDefine.HOST_CAPACITY_NAME);
			result.put("status", "NORMAL");
			result.put("thresholds", "['%',0,100]");
			result.put("type", "PerformanceMetric");
			result.put("unit", "%");
			
			break;
		case BizMetricDefine.STORAGE_CAPACITY:
			
			ids = new ArrayList<String>();
			ids.add(bizId + "");
			
			bizInfo = bizMainDao.getBasicInfo(bizId);

			totalSize = 0;
			alreadyUseSize = 0;
			
			//??????????????????
			capMetricList = bizCapMetricApi.getByBizIdAndMetric(bizId, BizMetricDefine.STORAGE_CAPACITY);
			
			if(capMetricList == null){
				capMetricList = new ArrayList<Long>();
			}
			
			urlList = bizCanvasApi.getStoreMetricInstanceList(bizId);
			
			if(urlList == null || urlList.size() <= 0){
				result.put("currentVal", Double.valueOf(0));
			}else{
				
				for(long urlId : urlList){
					
					if(capMetricList.contains(urlId)){
						continue;
					}
					
					ResourceInstance instance = null;
					try {
						instance = resourceInstanceService.getResourceInstance(urlId);
					} catch (InstancelibException e) {
						logger.error(e.getMessage(),e);
					}
					
					//??????????????????????????????????????????
					if(instance.getChildType() != null && instance.getChildType().equals("Partition")){
						//??????
						totalSize += getStoreCapacityValue(instance, "fileSysTotalSize");
						alreadyUseSize += getStoreCapacityValue(instance, "fileSysUsedSize");
					}else if(instance.getCategoryId().equals("VirtualStorage")){
						//vm??????
						totalSize += getStoreCapacityValue(instance, "DataStorageVolume");
						alreadyUseSize += getStoreCapacityValue(instance, "DataStorageUsedSpace");
					}else if(instance.getCategoryId().equals("XenSRs")){
						//xen??????
						totalSize += getStoreCapacityValue(instance, "physicalSize");
						alreadyUseSize += getStoreCapacityValue(instance, "physicalUtilisation");
					}else if(instance.getCategoryId().equals("FusionComputeDataStores")){
						//????????????
						totalSize += getStoreCapacityValue(instance, "physicalSize");
						alreadyUseSize += getStoreCapacityValue(instance, "physicalUtilisation");
					}
					
				}				

				
			}
			
			if(alreadyUseSize != 0){
				result.put("currentVal", Double.valueOf((alreadyUseSize / totalSize) * 100));
			}else{
				result.put("currentVal", Double.valueOf(0));
			}
			
			result.put("id", BizMetricDefine.STORAGE_CAPACITY);
			result.put("isAlarm", false);
			result.put("isCustomMetric", false);
			result.put("isTable", false);
			result.put("lastCollTime", dateFormat.format(new Date()));
			result.put("text", BizMetricDefine.STORAGE_CAPACITY_NAME);
			result.put("status", "NORMAL");
			result.put("thresholds", "['%',0,100]");
			result.put("type", "PerformanceMetric");
			result.put("unit", "%");
			
			break;
		case BizMetricDefine.DOWN_TIME:
			
			ids = new ArrayList<String>();
			ids.add(bizId + "");
			
			eventList = getEventList(ids, startTime, endTime);
			
			bizInfo = bizMainDao.getBasicInfo(bizId);
			
			//?????????????????????????????????????????????
			runTime = 0;
			createTime = bizInfo.getCreateTime().getTime();
			if(endTime.getTime() > createTime){
				if(startTime.getTime() < createTime){
					runTime = (endTime.getTime() - createTime) / 1000;
				}else{
					runTime = (endTime.getTime() - startTime.getTime()) / 1000;
				}
			}
			
			//??????????????????
			//?????????????????????????????????????????????????????????
			criticalTime = 0;
			
			//?????????????????????????????????????????????????????????
			criticalCount = 0;
			curEventList = new ArrayList<AlarmEvent>();
			if(eventList != null && eventList.size() > 0){
				for(AlarmEvent event : eventList){
					if(event.getSourceID().equals(bizId)){
						curEventList.add(event);
					}
				}
			}
			
			if(curEventList != null && curEventList.size() > 0){
				for(int i = 0 ; i < curEventList.size() ; i++){
					AlarmEvent event = curEventList.get(i);
					if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
						if(i > 0){
							AlarmEvent nextEvent = curEventList.get(i - 1);
							criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
						}else{
							criticalTime += (endTime.getTime() - event.getCollectionTime().getTime()) / 1000;
						}
						criticalCount++;
					}
				}
			}
			
			//?????????
			health = bizHealthHisDao.getBizHealthHis(bizId);
			
			status = 0;
			
			if(health != null){
				status = health.getBizStatus();
			}
			
			if(criticalCount == 0 && status == BizStatusDefine.DEATH_STATUS){
				criticalTime = runTime;
				criticalCount++;
			}

			result.put("currentVal", getDownTimeMetricValue(criticalTime));
			result.put("id", BizMetricDefine.DOWN_TIME);
			result.put("isAlarm", false);
			result.put("isCustomMetric", false);
			result.put("isTable", false);
			result.put("lastCollTime", dateFormat.format(new Date()));
			result.put("text", BizMetricDefine.DOWN_TIME_NAME);
			result.put("status", "NORMAL");
			result.put("thresholds", "['??????',0,100]");
			result.put("type", "PerformanceMetric");
			result.put("unit", "??????");
			
			break;
			
		case BizMetricDefine.OUTAGE_TIMES:
			
			ids = new ArrayList<String>();
			ids.add(bizId + "");
			
			eventList = getEventList(ids, startTime, endTime);

			bizInfo = bizMainDao.getBasicInfo(bizId);
			
			//??????????????????
			
			//?????????????????????????????????????????????????????????
			criticalCount = 0;
			curEventList = new ArrayList<AlarmEvent>();
			if(eventList != null && eventList.size() > 0){
				for(AlarmEvent event : eventList){
					if(event.getSourceID().equals(bizId)){
						curEventList.add(event);
					}
				}
			}
			
			if(curEventList != null && curEventList.size() > 0){
				for(int i = 0 ; i < curEventList.size() ; i++){
					AlarmEvent event = curEventList.get(i);
					if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
						criticalCount++;
					}
				}
			}
			
			//?????????
			health = bizHealthHisDao.getBizHealthHis(bizId);
			
			status = 0;
			
			if(health != null){
				status = health.getBizStatus();
			}
			
			if(criticalCount == 0 && status == BizStatusDefine.DEATH_STATUS){
				criticalCount++;
			}
			
			result.put("currentVal", criticalCount);
			result.put("id", BizMetricDefine.OUTAGE_TIMES);
			result.put("isAlarm", false);
			result.put("isCustomMetric", false);
			result.put("isTable", false);
			result.put("lastCollTime", dateFormat.format(new Date()));
			result.put("text", BizMetricDefine.OUTAGE_TIMES_NAME);
			result.put("status", "NORMAL");
			result.put("thresholds", "['???',0,100]");
			result.put("type", "PerformanceMetric");
			result.put("unit", "???");
			
			break;

		default:
			return null;
		}
		
		return result;
	}
	
	@Override
	public Map<String, Object> getMetricTopForHome(ILoginUser user,String resource,String metric, Integer top,final String sortMethod) {
		
		if(resource == null || resource.equals("")){
			return null;
		}
		
		if(metric == null || metric.equals("")){
			return null;
		}
		
		String[] bizIds = resource.split(",");
		
		if(bizIds == null || bizIds.length <= 0){
			return null;
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		Date endTime = new Date();
		
		Calendar calendar = Calendar.getInstance(); 
		calendar.add(Calendar.DATE, -7);
		Date startTime = calendar.getTime();
		
		List<Map<String, Object>> itemList = new ArrayList<Map<String,Object>>();
		List<String> categoriesList = new ArrayList<String>();
		
		switch (metric) {
		case BizMetricDefine.AVAILAVLE_RATE:
			
			List<String> ids = new ArrayList<String>();
			
			if(resource.equals("-1")){
				//???????????????????????????
				List<BizMainBo> list = bizMainDao.getAllList();
				for(BizMainBo biz : list){
					if(!bizUserRelApi.checkUserView(user.getId(), biz.getId())){
						continue;
					}
					ids.add(biz.getId() + "");
				}
			}else{
				for(String bizId : bizIds){
					if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
						continue;
					}
					ids.add(bizId);
				}
			}
			
			List<AlarmEvent> eventList = getEventList(ids, startTime, endTime);
			
			for(String bizId : ids){
				
				if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
					continue;
				}
				
				BizMainBo bizInfo = bizMainDao.getBasicInfo(Long.parseLong(bizId));
				
				//?????????????????????????????????????????????
				long runTime = 0;
				long createTime = bizInfo.getCreateTime().getTime();
				if(endTime.getTime() > createTime){
					if(startTime.getTime() < createTime){
						runTime = (endTime.getTime() - createTime) / 1000;
					}else{
						runTime = (endTime.getTime() - startTime.getTime()) / 1000;
					}
				}
				
				//??????????????????
				//?????????????????????????????????????????????????????????
				long criticalTime = 0;
				
				//?????????????????????????????????????????????????????????
				int criticalCount = 0;
				List<AlarmEvent> curEventList = new ArrayList<AlarmEvent>();
				if(eventList != null && eventList.size() > 0){
					for(AlarmEvent event : eventList){
						if(event.getSourceID().equals(bizId)){
							curEventList.add(event);
						}
					}
				}
				
				if(curEventList != null && curEventList.size() > 0){
					for(int i = 0 ; i < curEventList.size() ; i++){
						AlarmEvent event = curEventList.get(i);
						if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
							if(i > 0){
								AlarmEvent nextEvent = curEventList.get(i - 1);
								criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
							}else{
								criticalTime += (endTime.getTime() - event.getCollectionTime().getTime()) / 1000;
							}
							criticalCount++;
						}
					}
				}
				
				//?????????
				BizHealthHisBo health = bizHealthHisDao.getBizHealthHis(Long.parseLong(bizId));
				
				int status = 0;
				
				if(health != null){
					status = health.getBizStatus();
				}
				
				if(criticalCount == 0 && status == BizStatusDefine.DEATH_STATUS){
					criticalTime = runTime;
					criticalCount++;
				}
				
				//???????????????
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("name", bizInfo.getName());
				item.put("resourceInstanceId", bizInfo.getId());
				item.put("rawStatus", "NORMAL");
				item.put("y", getAvailableRateMetricValue(runTime,criticalTime));
				itemList.add(item);
				
			}
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					if(sortMethod.equals("desc")){
						//??????
						if((float)o1.get("y") > (float)o2.get("y")){
							return -1;
						}else if((float)o1.get("y") < (float)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}else{
						if((float)o1.get("y") < (float)o2.get("y")){
							return -1;
						}else if((float)o1.get("y") > (float)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}
				}
			});
			
			break;
		case BizMetricDefine.BIZ_HEALTH_STATUS:
			ids = new ArrayList<String>();
			if(resource.equals("-1")){
				//???????????????????????????
				List<BizMainBo> list = bizMainDao.getAllList();
				for(BizMainBo biz : list){
					if(!bizUserRelApi.checkUserView(user.getId(), biz.getId())){
						continue;
					}
					ids.add(biz.getId() + "");
				}
			}else{
				for(String bizId : bizIds){
					if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
						continue;
					}
					ids.add(bizId);
				}
			}
			
			
			for(String bizId : ids){
				
				Map<String, Object> item = new HashMap<String, Object>();
				
				if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
					continue;
				}
				
				BizMainBo bizInfo = bizMainDao.getBasicInfo(Long.parseLong(bizId));
				
				//?????????
				BizHealthHisBo health = bizHealthHisDao.getBizHealthHis(Long.parseLong(bizId));
				if(health == null){
					item.put("y", 100);
					item.put("rawStatus", "NORMAL");
				}else{
					item.put("y", health.getBizHealth());
					if(health.getBizStatus() == 3){
						item.put("rawStatus", "CRITICAL");
					}else if(health.getBizStatus() == 2){
						item.put("rawStatus", "SERIOUS");
					}else if(health.getBizStatus() == 1){
						item.put("rawStatus", "WARN");
					}else{
						item.put("rawStatus", "NORMAL");
					}
				}
				
				//???????????????
				item.put("name", bizInfo.getName());
				item.put("resourceInstanceId", bizInfo.getId());
				itemList.add(item);
				
			}
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					if(sortMethod.equals("desc")){
						if((int)o1.get("y") > (int)o2.get("y")){
							return -1;
						}else if((int)o1.get("y") < (int)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}else{
						if((int)o1.get("y") < (int)o2.get("y")){
							return -1;
						}else if((int)o1.get("y") > (int)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}
				}
			});
			
			break;
		case BizMetricDefine.MTBF:
			
			ids = new ArrayList<String>();
			
			if(resource.equals("-1")){
				//???????????????????????????
				List<BizMainBo> list = bizMainDao.getAllList();
				for(BizMainBo biz : list){
					if(!bizUserRelApi.checkUserView(user.getId(), biz.getId())){
						continue;
					}
					ids.add(biz.getId() + "");
				}
			}else{
				for(String bizId : bizIds){
					if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
						continue;
					}
					ids.add(bizId);
				}
			}
			
			eventList = getEventList(ids, startTime, endTime);
			
			for(String bizId : ids){
				
				if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
					continue;
				}
				
				BizMainBo bizInfo = bizMainDao.getBasicInfo(Long.parseLong(bizId));
				
				//?????????????????????????????????????????????
				long runTime = 0;
				long createTime = bizInfo.getCreateTime().getTime();
				if(endTime.getTime() > createTime){
					if(startTime.getTime() < createTime){
						runTime = (endTime.getTime() - createTime) / 1000;
					}else{
						runTime = (endTime.getTime() - startTime.getTime()) / 1000;
					}
				}
				
				//??????????????????
				//?????????????????????????????????????????????????????????
				long criticalTime = 0;
				
				//?????????????????????????????????????????????????????????
				int criticalCount = 0;
				List<AlarmEvent> curEventList = new ArrayList<AlarmEvent>();
				if(eventList != null && eventList.size() > 0){
					for(AlarmEvent event : eventList){
						if(event.getSourceID().equals(bizId)){
							curEventList.add(event);
						}
					}
				}
				
				boolean lastIsCritical = false;
				
				if(curEventList != null && curEventList.size() > 0){
					for(int i = 0 ; i < curEventList.size() ; i++){
						AlarmEvent event = curEventList.get(i);
						if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
							if(i > 0){
								AlarmEvent nextEvent = curEventList.get(i - 1);
								criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
							}else{
								criticalTime += (endTime.getTime() - event.getCollectionTime().getTime()) / 1000;
								lastIsCritical = true;
							}
							criticalCount++;
						}
					}
				}
				
				//?????????
				BizHealthHisBo health = bizHealthHisDao.getBizHealthHis(Long.parseLong(bizId));
				
				int status = 0;
				
				if(health != null){
					status = health.getBizStatus();
				}
				
				if(criticalCount == 0 && status == BizStatusDefine.DEATH_STATUS){
					criticalTime = runTime;
					criticalCount++;
					lastIsCritical = true;
				}
				
				//???????????????
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("name", bizInfo.getName());
				item.put("resourceInstanceId", bizInfo.getId());
				item.put("rawStatus", "NORMAL");
				item.put("y", getMTBFMetricValue(runTime, criticalTime, lastIsCritical ? criticalCount : criticalCount + 1));
				itemList.add(item);
				
			}
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					if(sortMethod.equals("desc")){
						//??????
						if((float)o1.get("y") > (float)o2.get("y")){
							return -1;
						}else if((float)o1.get("y") < (float)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
						
					}else{
						if((float)o1.get("y") < (float)o2.get("y")){
							return -1;
						}else if((float)o1.get("y") > (float)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}
				}
			});
			
			break;
		case BizMetricDefine.MTTR:
			
			ids = new ArrayList<String>();
			
			if(resource.equals("-1")){
				//???????????????????????????
				List<BizMainBo> list = bizMainDao.getAllList();
				for(BizMainBo biz : list){
					if(!bizUserRelApi.checkUserView(user.getId(), biz.getId())){
						continue;
					}
					ids.add(biz.getId() + "");
				}
			}else{
				for(String bizId : bizIds){
					if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
						continue;
					}
					ids.add(bizId);
				}
			}
			
			eventList = getEventList(ids, startTime, endTime);
			
			for(String bizId : ids){
				
				if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
					continue;
				}
				
				BizMainBo bizInfo = bizMainDao.getBasicInfo(Long.parseLong(bizId));
				
				//?????????????????????????????????????????????
				long runTime = 0;
				long createTime = bizInfo.getCreateTime().getTime();
				if(endTime.getTime() > createTime){
					if(startTime.getTime() < createTime){
						runTime = (endTime.getTime() - createTime) / 1000;
					}else{
						runTime = (endTime.getTime() - startTime.getTime()) / 1000;
					}
				}
				
				//??????????????????
				//?????????????????????????????????????????????????????????
				long criticalTime = 0;
				
				//?????????????????????????????????????????????????????????
				int criticalCount = 0;
				List<AlarmEvent> curEventList = new ArrayList<AlarmEvent>();
				if(eventList != null && eventList.size() > 0){
					for(AlarmEvent event : eventList){
						if(event.getSourceID().equals(bizId)){
							curEventList.add(event);
						}
					}
				}
				
				if(curEventList != null && curEventList.size() > 0){
					for(int i = 0 ; i < curEventList.size() ; i++){
						AlarmEvent event = curEventList.get(i);
						if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
							if(i > 0){
								AlarmEvent nextEvent = curEventList.get(i - 1);
								criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
							}else{
								criticalTime += (endTime.getTime() - event.getCollectionTime().getTime()) / 1000;
							}
							criticalCount++;
						}
					}
				}
				
				//?????????
				BizHealthHisBo health = bizHealthHisDao.getBizHealthHis(Long.parseLong(bizId));
				
				int status = 0;
				
				if(health != null){
					status = health.getBizStatus();
				}
				
				if(criticalCount == 0 && status == BizStatusDefine.DEATH_STATUS){
					criticalTime = runTime;
					criticalCount++;
				}
				
				//???????????????
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("name", bizInfo.getName());
				item.put("resourceInstanceId", bizInfo.getId());
				item.put("rawStatus", "NORMAL");
				item.put("y", getMTTRMetricValue(criticalTime, criticalCount));
				itemList.add(item);
				
			}
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					if(sortMethod.equals("desc")){
						//??????
						if((float)o1.get("y") > (float)o2.get("y")){
							return -1;
						}else if((float)o1.get("y") < (float)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}else{
						if((float)o1.get("y") < (float)o2.get("y")){
							return -1;
						}else if((float)o1.get("y") > (float)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}
				}
			});
			
			break;
		case BizMetricDefine.RESPONSE_TIME:
			
			ids = new ArrayList<String>();
			
			if(resource.equals("-1")){
				//???????????????????????????
				List<BizMainBo> list = bizMainDao.getAllList();
				for(BizMainBo biz : list){
					if(!bizUserRelApi.checkUserView(user.getId(), biz.getId())){
						continue;
					}
					ids.add(biz.getId() + "");
				}
			}else{
				for(String bizId : bizIds){
					if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
						continue;
					}
					ids.add(bizId);
				}
			}
			
			for(String bizId : ids){
				
				if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
					continue;
				}
				
				Map<String, Object> item = new HashMap<String, Object>();
				
				BizMainBo bizInfo = bizMainDao.getBasicInfo(Long.parseLong(bizId));
				//??????????????????
				List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(Long.parseLong(bizId), BizMetricDefine.RESPONSE_TIME);
				
				if(capMetricList == null){
					capMetricList = new ArrayList<Long>();
				}
				
				Set<Long> urlList = bizCanvasApi.getResponeTimeMetricInstanceList(Long.parseLong(bizId));
				
				if(urlList == null || urlList.size() <= 0){
					item.put("y", Double.valueOf(0));
				}else{
					
					List<Double> datas = new ArrayList<Double>();
					
					for(long urlId : urlList){
						if(capMetricList.contains(urlId)){
							continue;
						}
						MetricRealtimeDataQuery metricRealtimeDataQuery = new MetricRealtimeDataQuery();
						metricRealtimeDataQuery.setInstanceID(new long[]{urlId});
						metricRealtimeDataQuery.setMetricID(new String[]{BizMetricDefine.RESPONSE_TIME});
						
						Page<Map<String,?>,MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(metricRealtimeDataQuery, 0, 1);
						List<Map<String,?>> realTimeDataList = page.getDatas();
						
						if(realTimeDataList == null || realTimeDataList.size() <= 0){
							continue;
						}
						
						Map<String,?> map =	realTimeDataList.get(0);
						
						if(map.get(BizMetricDefine.RESPONSE_TIME) == null){
							continue;
						}
						
						datas.add(Double.parseDouble(map.get(BizMetricDefine.RESPONSE_TIME).toString()));
					}
					
					if(datas.size() <= 0){
						item.put("y", Double.valueOf(0));
					}else{
						Collections.sort(datas);
						item.put("y", datas.get(datas.size() - 1));
					}
					
				}
				//???????????????
				item.put("name", bizInfo.getName());
				item.put("rawStatus", "NORMAL");
				item.put("resourceInstanceId", bizInfo.getId());
				itemList.add(item);
			}
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					if(sortMethod.equals("desc")){
						//??????
						if((Double)o1.get("y") > (Double)o2.get("y")){
							return -1;
						}else if((Double)o1.get("y") < (Double)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}else{
						if((Double)o1.get("y") < (Double)o2.get("y")){
							return -1;
						}else if((Double)o1.get("y") > (Double)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}
				}
			});
			
			break;
		case BizMetricDefine.BANDWIDTH_CAPACITY:
			
			ids = new ArrayList<String>();
			
			if(resource.equals("-1")){
				//???????????????????????????
				List<BizMainBo> list = bizMainDao.getAllList();
				for(BizMainBo biz : list){
					if(!bizUserRelApi.checkUserView(user.getId(), biz.getId())){
						continue;
					}
					ids.add(biz.getId() + "");
				}
			}else{
				for(String bizId : bizIds){
					if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
						continue;
					}
					ids.add(bizId);
				}
			}
			
			for(String bizId : ids){
				
				if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
					continue;
				}
				
				Map<String, Object> item = new HashMap<String, Object>();
				
				BizMainBo bizInfo = bizMainDao.getBasicInfo(Long.parseLong(bizId));

				double totalSize = 0;
				double alreadyUseSize = 0;
				
				//??????????????????
				List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(Long.parseLong(bizId), BizMetricDefine.BANDWIDTH_CAPACITY);
				
				if(capMetricList == null){
					capMetricList = new ArrayList<Long>();
				}
				
				Set<Long> urlList = bizCanvasApi.getBandwidthMetricInstanceList(Long.parseLong(bizId));
				
				if(urlList == null || urlList.size() <= 0){
					item.put("y", Double.valueOf(0));
				}else{
					
					for(long urlId : urlList){
						
						if(capMetricList.contains(urlId)){
							continue;
						}
						
						String[] value = null;
						//?????????????????????????????????????????????
						boolean isHaveDefined = false;
						List<CustomModuleProp> allModuleProp = customModulePropService.getCustomModuleProp();
						if(allModuleProp != null && allModuleProp.size() > 0){
							  for(CustomModuleProp cmp:allModuleProp){
								  if(cmp.getInstanceId() == urlId && cmp.getKey().equals("ifSpeed")){
									  isHaveDefined = true;
									  value = new String[]{cmp.getUserValue()};
									  break;
								  }
							  }
						}
						
						if(!isHaveDefined){
							MetricData sizeMetricData = metricDataService.getMetricInfoData(urlId, "ifSpeed");
							if(sizeMetricData == null){
								continue;
							}
							value = sizeMetricData.getData();
						}
						
						
						
						MetricData utilMetricData = metricDataService.getMetricPerformanceData(urlId, "ifBandWidthUtil");
						if(utilMetricData == null){
							continue;
						}
						if(value != null && value.length > 0){
							if(value[0] != null && !value[0].equals("")){
								double size = Double.parseDouble(value[0]);
								totalSize += size;
								String[] utilValue = utilMetricData.getData();
								if(utilValue != null && utilValue.length > 0){
									if(utilValue[0] != null && !utilValue[0].equals("") && !utilValue[0].equals("0.00")){
										alreadyUseSize += size * Double.parseDouble(utilValue[0]) / 100;
									}
								}
							}
						}
					}
					
				}
				
				if(alreadyUseSize != 0){
					item.put("y", (alreadyUseSize / totalSize) * 100);
				}else{
					item.put("y", Double.valueOf(0));
				}
				
				//???????????????
				item.put("name", bizInfo.getName());
				item.put("rawStatus", "NORMAL");
				item.put("resourceInstanceId", bizInfo.getId());
				itemList.add(item);
			}
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					if(sortMethod.equals("desc")){
						//??????
						if((Double)o1.get("y") > (Double)o2.get("y")){
							return -1;
						}else if((Double)o1.get("y") < (Double)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}else{
						if((Double)o1.get("y") < (Double)o2.get("y")){
							return -1;
						}else if((Double)o1.get("y") > (Double)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}
				}
			});
			
			break;
		case BizMetricDefine.DATABASE_CAPACITY:
			
			ids = new ArrayList<String>();
			
			if(resource.equals("-1")){
				//???????????????????????????
				List<BizMainBo> list = bizMainDao.getAllList();
				for(BizMainBo biz : list){
					if(!bizUserRelApi.checkUserView(user.getId(), biz.getId())){
						continue;
					}
					ids.add(biz.getId() + "");
				}
			}else{
				for(String bizId : bizIds){
					if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
						continue;
					}
					ids.add(bizId);
				}
			}
			
			for(String bizId : ids){
				
				if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
					continue;
				}
				
				Map<String, Object> item = new HashMap<String, Object>();
				
				BizMainBo bizInfo = bizMainDao.getBasicInfo(Long.parseLong(bizId));

				double totalSize = 0;
				double alreadyUseSize = 0;
				
				//?????????????????????
				List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(Long.parseLong(bizId), BizMetricDefine.DATABASE_CAPACITY);
				
				if(capMetricList == null){
					capMetricList = new ArrayList<Long>();
				}
				
				Set<Long> urlList = bizCanvasApi.getDatabaseMetricInstanceList(Long.parseLong(bizId));
				
				if(urlList == null || urlList.size() <= 0){
					item.put("y", Double.valueOf(0));
				}else{
					
					for(long urlId : urlList){
						
						if(capMetricList.contains(urlId)){
							continue;
						}
						
						MetricData sizeMetricData = metricDataService.getMetricInfoData(urlId, "tableSpaceSize");
						if(sizeMetricData == null){
							continue;
						}
						ResourceMetricDef metricDef = null;
						try {
							metricDef = capacityService.getResourceMetricDef(resourceInstanceService.getResourceInstance(urlId).getResourceId(), "tableSpaceSize");
						} catch (InstancelibException e) {
							logger.error(e.getMessage(),e);
						}
						String[] value = sizeMetricData.getData();
						MetricData utilMetricData = metricDataService.getMetricPerformanceData(urlId, "tableSpaceUtil");
						if(utilMetricData == null){
							continue;
						}
						if(value != null && value.length > 0){
							if(value[0] != null && !value[0].equals("")){
								double size = unitMBTrans(Double.parseDouble(value[0]), metricDef.getUnit());
								totalSize += size;
								String[] utilValue = utilMetricData.getData();
								if(utilValue != null && utilValue.length > 0){
									if(utilValue[0] != null && !utilValue[0].equals("") && !utilValue[0].equals("0.00")){
										alreadyUseSize += size * Double.parseDouble(utilValue[0]) / 100;
									}
								}
							}
						}
					}
					
				}
				
				if(alreadyUseSize != 0){
					item.put("y", (alreadyUseSize / totalSize) * 100);
				}else{
					item.put("y", Double.valueOf(0));
				}
				
				//???????????????
				item.put("name", bizInfo.getName());
				item.put("rawStatus", "NORMAL");
				item.put("resourceInstanceId", bizInfo.getId());
				itemList.add(item);
			}
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					if(sortMethod.equals("desc")){
						//??????
						if((Double)o1.get("y") > (Double)o2.get("y")){
							return -1;
						}else if((Double)o1.get("y") < (Double)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}else{
						if((Double)o1.get("y") < (Double)o2.get("y")){
							return -1;
						}else if((Double)o1.get("y") > (Double)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}
				}
			});
			
			break;
		case BizMetricDefine.HOST_CAPACITY:
			
			ids = new ArrayList<String>();
			
			if(resource.equals("-1")){
				//???????????????????????????
				List<BizMainBo> list = bizMainDao.getAllList();
				for(BizMainBo biz : list){
					if(!bizUserRelApi.checkUserView(user.getId(), biz.getId())){
						continue;
					}
					ids.add(biz.getId() + "");
				}
			}else{
				for(String bizId : bizIds){
					if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
						continue;
					}
					ids.add(bizId);
				}
			}
			
			for(String bizId : ids){
				
				if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
					continue;
				}
				
				Map<String, Object> item = new HashMap<String, Object>();
				
				BizMainBo bizInfo = bizMainDao.getBasicInfo(Long.parseLong(bizId));

				//??????????????????
				List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(Long.parseLong(bizId), BizMetricDefine.HOST_CAPACITY);
				
				if(capMetricList == null){
					capMetricList = new ArrayList<Long>();
				}
				
				Set<Long> urlList = bizCanvasApi.getCalculateMetricInstanceList(Long.parseLong(bizId));
				
				if(urlList == null || urlList.size() <= 0){
					item.put("y", Double.valueOf(0));
				}else{
					
					List<Double> datas = new ArrayList<Double>();
					
					for(long urlId : urlList){
						if(capMetricList.contains(urlId)){
							continue;
						}
						InstanceStateData state = instanceStateService.getStateAdapter(urlId);
						if(state != null && state.getState().equals(InstanceStateEnum.CRITICAL)){
							continue;
						}
						MetricData instanceMetricData = metricDataService.getMetricPerformanceData(urlId, MetricIdConsts.METRIC_CPU_RATE);
						if(instanceMetricData == null){
							continue;
						}
						String[] value = instanceMetricData.getData();
						if(value != null && value.length > 0){
							if(value[0] != null && !value[0].equals("")){
								datas.add(Double.parseDouble(value[0]));
							}
						}
					}
					
					if(datas.size() <= 0){
						item.put("y", Double.valueOf(0));
					}else{
						Collections.sort(datas);
						item.put("y", datas.get(datas.size() - 1));
					}
					
				}
				
				//???????????????
				item.put("name", bizInfo.getName());
				item.put("rawStatus", "NORMAL");
				item.put("resourceInstanceId", bizInfo.getId());
				itemList.add(item);
			}
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					if(sortMethod.equals("desc")){
						//??????
						if((Double)o1.get("y") > (Double)o2.get("y")){
							return -1;
						}else if((Double)o1.get("y") < (Double)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}else{
						if((Double)o1.get("y") < (Double)o2.get("y")){
							return -1;
						}else if((Double)o1.get("y") > (Double)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}
				}
			});
			
			break;
		case BizMetricDefine.STORAGE_CAPACITY:
			
			ids = new ArrayList<String>();
			
			if(resource.equals("-1")){
				//???????????????????????????
				List<BizMainBo> list = bizMainDao.getAllList();
				for(BizMainBo biz : list){
					if(!bizUserRelApi.checkUserView(user.getId(), biz.getId())){
						continue;
					}
					ids.add(biz.getId() + "");
				}
			}else{
				for(String bizId : bizIds){
					if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
						continue;
					}
					ids.add(bizId);
				}
			}
			
			for(String bizId : ids){
				
				if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
					continue;
				}
				
				Map<String, Object> item = new HashMap<String, Object>();
				
				BizMainBo bizInfo = bizMainDao.getBasicInfo(Long.parseLong(bizId));

				double totalSize = 0;
				double alreadyUseSize = 0;
				
				//??????????????????
				List<Long> capMetricList = bizCapMetricApi.getByBizIdAndMetric(Long.parseLong(bizId), BizMetricDefine.STORAGE_CAPACITY);
				
				if(capMetricList == null){
					capMetricList = new ArrayList<Long>();
				}
				
				Set<Long> urlList = bizCanvasApi.getStoreMetricInstanceList(Long.parseLong(bizId));
				
				if(urlList == null || urlList.size() <= 0){
					item.put("y", Double.valueOf(0));
				}else{
					
					for(long urlId : urlList){
						
						if(capMetricList.contains(urlId)){
							continue;
						}
						
						ResourceInstance instance = null;
						try {
							instance = resourceInstanceService.getResourceInstance(urlId);
						} catch (InstancelibException e) {
							logger.error(e.getMessage(),e);
						}
						
						//??????????????????????????????????????????
						if(instance.getChildType() != null && instance.getChildType().equals("Partition")){
							//??????
							totalSize += getStoreCapacityValue(instance, "fileSysTotalSize");
							alreadyUseSize += getStoreCapacityValue(instance, "fileSysUsedSize");
						}else if(instance.getCategoryId().equals("VirtualStorage")){
							//vm??????
							totalSize += getStoreCapacityValue(instance, "DataStorageVolume");
							alreadyUseSize += getStoreCapacityValue(instance, "DataStorageUsedSpace");
						}else if(instance.getCategoryId().equals("XenSRs")){
							//xen??????
							totalSize += getStoreCapacityValue(instance, "physicalSize");
							alreadyUseSize += getStoreCapacityValue(instance, "physicalUtilisation");
						}else if(instance.getCategoryId().equals("FusionComputeDataStores")){
							//????????????
							totalSize += getStoreCapacityValue(instance, "physicalSize");
							alreadyUseSize += getStoreCapacityValue(instance, "physicalUtilisation");
						}
						
					}				

					
				}
				
				if(alreadyUseSize != 0){
					item.put("y", Double.valueOf((alreadyUseSize / totalSize) * 100));
				}else{
					item.put("y", Double.valueOf(0));
				}
				
				//???????????????
				item.put("name", bizInfo.getName());
				item.put("rawStatus", "NORMAL");
				item.put("resourceInstanceId", bizInfo.getId());
				itemList.add(item);
			}
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					if(sortMethod.equals("desc")){
						//??????
						if((Double)o1.get("y") > (Double)o2.get("y")){
							return -1;
						}else if((Double)o1.get("y") < (Double)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}else{
						if((Double)o1.get("y") < (Double)o2.get("y")){
							return -1;
						}else if((Double)o1.get("y") > (Double)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}
				}
			});
			
			break;
		case BizMetricDefine.DOWN_TIME:
			
			ids = new ArrayList<String>();
			
			if(resource.equals("-1")){
				//???????????????????????????
				List<BizMainBo> list = bizMainDao.getAllList();
				for(BizMainBo biz : list){
					if(!bizUserRelApi.checkUserView(user.getId(), biz.getId())){
						continue;
					}
					ids.add(biz.getId() + "");
				}
			}else{
				for(String bizId : bizIds){
					if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
						continue;
					}
					ids.add(bizId);
				}
			}
			
			eventList = getEventList(ids, startTime, endTime);
			
			for(String bizId : ids){
				
				if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
					continue;
				}
				
				BizMainBo bizInfo = bizMainDao.getBasicInfo(Long.parseLong(bizId));
				
				//?????????????????????????????????????????????
				long runTime = 0;
				long createTime = bizInfo.getCreateTime().getTime();
				if(endTime.getTime() > createTime){
					if(startTime.getTime() < createTime){
						runTime = (endTime.getTime() - createTime) / 1000;
					}else{
						runTime = (endTime.getTime() - startTime.getTime()) / 1000;
					}
				}
				
				//??????????????????
				//?????????????????????????????????????????????????????????
				long criticalTime = 0;
				
				//?????????????????????????????????????????????????????????
				int criticalCount = 0;
				List<AlarmEvent> curEventList = new ArrayList<AlarmEvent>();
				if(eventList != null && eventList.size() > 0){
					for(AlarmEvent event : eventList){
						if(event.getSourceID().equals(bizId)){
							curEventList.add(event);
						}
					}
				}
				
				if(curEventList != null && curEventList.size() > 0){
					for(int i = 0 ; i < curEventList.size() ; i++){
						AlarmEvent event = curEventList.get(i);
						if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
							if(i > 0){
								AlarmEvent nextEvent = curEventList.get(i - 1);
								criticalTime += (nextEvent.getCollectionTime().getTime() - event.getCollectionTime().getTime()) / 1000;
							}else{
								criticalTime += (endTime.getTime() - event.getCollectionTime().getTime()) / 1000;
							}
							criticalCount++;
						}
					}
				}
				
				//?????????
				BizHealthHisBo health = bizHealthHisDao.getBizHealthHis(Long.parseLong(bizId));
				
				int status = 0;
				
				if(health != null){
					status = health.getBizStatus();
				}
				
				if(criticalCount == 0 && status == BizStatusDefine.DEATH_STATUS){
					criticalTime = runTime;
					criticalCount++;
				}
				
				//???????????????
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("name", bizInfo.getName());
				item.put("rawStatus", "NORMAL");
				item.put("resourceInstanceId", bizInfo.getId());
				item.put("y", getDownTimeMetricValue(criticalTime));
				itemList.add(item);
				
			}
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					if(sortMethod.equals("desc")){
						//??????
						if((float)o1.get("y") > (float)o2.get("y")){
							return -1;
						}else if((float)o1.get("y") < (float)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}else{
						if((float)o1.get("y") < (float)o2.get("y")){
							return -1;
						}else if((float)o1.get("y") > (float)o2.get("y")){
							return 1;
						}else{
							return 0;
						}
					}
				}
			});
			break;
			
		case BizMetricDefine.OUTAGE_TIMES:
			
			ids = new ArrayList<String>();
			
			if(resource.equals("-1")){
				//???????????????????????????
				List<BizMainBo> list = bizMainDao.getAllList();
				for(BizMainBo biz : list){
					if(!bizUserRelApi.checkUserView(user.getId(), biz.getId())){
						continue;
					}
					ids.add(biz.getId() + "");
				}
			}else{
				for(String bizId : bizIds){
					if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
						continue;
					}
					ids.add(bizId);
				}
			}
			
			eventList = getEventList(ids, startTime, endTime);
			
			for(String bizId : ids){
				
				if(!bizUserRelApi.checkUserView(user.getId(), Long.parseLong(bizId))){
					continue;
				}
				
				BizMainBo bizInfo = bizMainDao.getBasicInfo(Long.parseLong(bizId));
				
				//??????????????????
				
				//?????????????????????????????????????????????????????????
				int criticalCount = 0;
				List<AlarmEvent> curEventList = new ArrayList<AlarmEvent>();
				if(eventList != null && eventList.size() > 0){
					for(AlarmEvent event : eventList){
						if(event.getSourceID().equals(bizId)){
							curEventList.add(event);
						}
					}
				}
				
				if(curEventList != null && curEventList.size() > 0){
					for(int i = 0 ; i < curEventList.size() ; i++){
						AlarmEvent event = curEventList.get(i);
						if(event.getLevel().equals(InstanceStateEnum.CRITICAL)){
							criticalCount++;
						}
					}
				}
				
				//?????????
				BizHealthHisBo health = bizHealthHisDao.getBizHealthHis(Long.parseLong(bizId));
				
				int status = 0;
				
				if(health != null){
					status = health.getBizStatus();
				}
				
				if(criticalCount == 0 && status == BizStatusDefine.DEATH_STATUS){
					criticalCount++;
				}
				
				//???????????????
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("name", bizInfo.getName());
				item.put("rawStatus", "NORMAL");
				item.put("resourceInstanceId", bizInfo.getId());
				item.put("y", criticalCount);
				itemList.add(item);
				
			}
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					// TODO Auto-generated method stub
					if(sortMethod.equals("desc")){
						//??????
						if(Float.parseFloat(o1.get("y") + "") > Float.parseFloat(o2.get("y") + "")){
							return -1;
						}else if(Float.parseFloat(o1.get("y") + "") < Float.parseFloat(o2.get("y") + "")){
							return 1;
						}else{
							return 0;
						}
					}else{
						if(Float.parseFloat(o1.get("y") + "") < Float.parseFloat(o2.get("y") + "")){
							return -1;
						}else if(Float.parseFloat(o1.get("y") + "") > Float.parseFloat(o2.get("y") + "")){
							return 1;
						}else{
							return 0;
						}
					}
				}
			});
			break;

		default:
			return null;
		}
		
		if(itemList.size() > top){
			itemList = itemList.subList(0, top);
		}
		
		for(Map<String, Object> map : itemList){
			categoriesList.add(map.get("name") + "");
		}
		
		result.put("categories", categoriesList);
		result.put("data", itemList);
		
		return result;
	}

	@Override
	public List<BizMetricHistoryValueBo> getHistoryDataForHomeMetric(long bizId, Date startTime, Date endTime, MetricSummaryType mtyp,
			String metricId) {
		if(metricId.equals(BizMetricDefine.BIZ_HEALTH_STATUS)){
			return this.getHealthHistoryDataForHomeMetric(bizId, startTime, endTime);
		}else if(metricId.equals(BizMetricDefine.RESPONSE_TIME)){
			return this.getResponseTimeHistoryDataForHomeMetric(bizId, startTime, endTime, mtyp);
		}else{
			return null;
		}
	}
	


}
