package com.mainsteam.stm.portal.business.service.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.report.AvailableMetricCountData;
import com.mainsteam.stm.common.metric.report.AvailableMetricCountQuery;
import com.mainsteam.stm.common.metric.report.AvailableMetricDataReportService;
import com.mainsteam.stm.instancelib.CompositeInstanceService;
import com.mainsteam.stm.instancelib.RelationService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.PathRelation;
import com.mainsteam.stm.instancelib.obj.Relation;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;
import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;
import com.mainsteam.stm.portal.business.dao.IBizServiceDao;
import com.mainsteam.stm.portal.business.dao.IBizStatusSelfDao;
import com.mainsteam.stm.portal.business.report.api.BizSerReportApi;
import com.mainsteam.stm.portal.business.report.dao.IBizSerReportDao;
import com.mainsteam.stm.portal.business.report.obj.BizSerMetric;
import com.mainsteam.stm.portal.business.report.obj.BizSerMetricEnum;
import com.mainsteam.stm.portal.business.report.obj.BizSerReport;

public class BizSerReportImpl implements BizSerReportApi{
	private static final Log logger = LogFactory.getLog(BizSerReportImpl.class);
	private IBizSerReportDao bizSerReportDao; 
	private IBizServiceDao bizServiceDao;
	private IBizStatusSelfDao bizStatusSelfDao;
	@Autowired
	private AvailableMetricDataReportService availableMetricDataReportServiceImpl;
	@Autowired
	private CompositeInstanceService compositeInstanceService;
	@Resource
	private AlarmEventService alarmEventService;
	@Resource
	private RelationService relationService;
	@Override
	public List<BizSerReport> getBizSerReports(String searchKey) {
		return bizSerReportDao.getBizSerReports(searchKey);
	}

	@Override
	public BizSerReport getBizSerReport(Long id) {
		return bizSerReportDao.getBizSerReport(id);
	}

	@Override
	public List<BizSerReport> getBizSerReports(List<Long> ids,List<String> metricIds,List<TimePeriod> timePeriods) {
		//??????????????????????????????
		List<BizSerReport> results = bizSerReportDao.getBizSerReports(ids.toArray(new Long[]{}));
		for(BizSerReport b : results){
			getMetricData(b.getId(), b, metricIds, timePeriods);
		}
		return results;
	}
	/**
	 * ??????????????????
	 * @param id
	 * @param bizSerReport
	 * @param metricIds
	 * @param timePeriods
	 */
	private void getMetricData(Long id,BizSerReport bizSerReport,List<String> metricIds,List<TimePeriod> timePeriods){
		try {
			AvailableMetricCountQuery query = new AvailableMetricCountQuery();
			query.setTimePeriods(timePeriods);
			List<Relation> relations = relationService.getRelationByInstanceId(id);
			List<Long> instanceIDs = new ArrayList<Long>();
			if(null!=relations && relations.size()>0){
				for(Relation relation: relations){
					if(relation instanceof PathRelation){
						PathRelation pathRelation = (PathRelation) relation;
						if(pathRelation.getInstanceId()==id
								&& pathRelation.getFromInstanceType().equals(InstanceTypeEnum.BUSINESS_APPLICATION)
								&& pathRelation.getToInstanceType().equals(InstanceTypeEnum.RESOURCE)){
							instanceIDs.add(pathRelation.getToInstanceId());
						}
					} 
				}
			}
			query.setInstanceIDes(instanceIDs);
			BizServiceBo bizServiceBo = bizServiceDao.get(id);
			boolean defaultCalc = true;//??????????????????
			boolean deathRelationCalc = true;//??????????????????????????????????????????
			if("1".equals(bizServiceBo.getStatus_type())){
				defaultCalc = false;//?????????????????????
				if("1".equals(bizServiceBo.getDeath_relation()))
					deathRelationCalc = false;//??????????????????????????????????????????
			}
			if(null!=timePeriods && timePeriods.size()>0
					&& null!=instanceIDs && instanceIDs.size()>0){
				List<AvailableMetricCountData> availableMetricCountDatas =
						availableMetricDataReportServiceImpl.findAvailableCount(query);
				boolean hasMetricDatas = false;
				if(null!=availableMetricCountDatas && availableMetricCountDatas.size()>0){
					hasMetricDatas = true;
				}
				for(String s: metricIds){
					switch (s) {
					case "0"://AVAILABLE_RATE
						/***
						 *?????????
						 *???????????????=??????????????????/????????????????????????????????????????????????????????????????????????????????????
						 *??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
						 * 
						 */
						if(hasMetricDatas){
							if(defaultCalc){
								BizSerReportImpl.SortByMethodName(availableMetricCountDatas, "availabilityRate","asc");
							}else{
								if(deathRelationCalc){
									BizSerReportImpl.SortByMethodName(availableMetricCountDatas, "availabilityRate","asc");
								}else{
									BizSerReportImpl.SortByMethodName(availableMetricCountDatas, "availabilityRate","desc");
								}
							}
							bizSerReport.setAvailableRate(availableMetricCountDatas.get(0).getAvailabilityRate());
						}
						break;
					case "1"://MTTR
						/**
						 * MTTR
						 *????????????????????????????????????MTTR????????????MTTR??????????????????
						 *??????????????????????????????????????????????????????MTTR?????????MTTR?????????????????????????????????????????????????????????????????????????????????????????????
						 */
						if(hasMetricDatas){
							if(defaultCalc){
								BizSerReportImpl.SortByMethodName(availableMetricCountDatas, "MTTR","desc");
							}else{
								if(deathRelationCalc){
									BizSerReportImpl.SortByMethodName(availableMetricCountDatas, "MTTR","asc");
								}else{
									BizSerReportImpl.SortByMethodName(availableMetricCountDatas, "MTTR","desc");
								}
							}
							bizSerReport.setMttr(availableMetricCountDatas.get(0).getMTTR());
						}
						break;
					case "2"://MTBF
						/**
						 * MTBF
						 *????????????????????????????????????MTBF????????????MTBF??????????????????
						 *??????????????????????????????????????????????????????MTBF?????????MTBF?????????????????????????????????????????????????????????????????????????????????????????????
						 */
						if(hasMetricDatas){
							if(defaultCalc){
								BizSerReportImpl.SortByMethodName(availableMetricCountDatas, "MTBF","asc");
							}else{
								if(deathRelationCalc){
									BizSerReportImpl.SortByMethodName(availableMetricCountDatas, "MTBF","desc");
								}{
									BizSerReportImpl.SortByMethodName(availableMetricCountDatas, "MTBF","asc");
								}
							}
							bizSerReport.setMtbf(availableMetricCountDatas.get(0).getMTBF());
						}
						break;
					case "3"://OUTAGE_TIMES
						getOutageTimesOrDownTime(id, bizSerReport, timePeriods, true);
						break;
					case "4"://DOWNTIME
						getOutageTimesOrDownTime(id, bizSerReport, timePeriods, false);
						break;
					case "5"://WARN_NUM
						getWarnNumOrUnrecoveredWarnNum(id, bizSerReport, timePeriods,true);
						break;
					case "6"://UNRECOVERED_WARN_NUM
						getWarnNumOrUnrecoveredWarnNum(id, bizSerReport, timePeriods,false);
						break;
					default:
						break;
					}
				}
			}
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ?????????????????????????????????
	 * @param id
	 * @param bizSerReport
	 * @param timePeriods
	 * @param OutageTimes
	 */
	private void getOutageTimesOrDownTime(Long id,BizSerReport bizSerReport,
			List<TimePeriod> timePeriods,boolean OutageTimes){
		
		//??????????????????????????????????????????
//		AvailableMetricCountQuery query = new AvailableMetricCountQuery();
//		query.setTimePeriods(timePeriods);
//		List<Long> instanceIDes = new ArrayList<Long>();
//		instanceIDes.add(id);
//		query.setInstanceIDes(instanceIDes);
//		List<AvailableMetricCountData> datas = availableMetricDataReportServiceImpl.findAvailableCount(query);
//		if(null!=datas && datas.size()>0){
//			if(OutageTimes){
//				bizSerReport.setOutageTimes(datas.get(0).getNotAvailabilityNum());
//			}else{
//				bizSerReport.setDownTime(datas.get(0).getNotAvailabilityDurationHour());
//			}
//		}
		//???????????????????????????????????????????????????
		if(OutageTimes){
			List<String> ids = new ArrayList<String>();
			ids.add(String.valueOf(id));
			int count = 0;
			for(TimePeriod timePeriod : timePeriods){
				count += alarmEventService.countAlarmEvent(ids, SysModuleEnum.BUSSINESS, 
						new InstanceStateEnum[]{InstanceStateEnum.CRITICAL}, timePeriod.getStartTime(), timePeriod.getEndTime(), null);
			}
			bizSerReport.setOutageTimes(count);
		}else{
			long timeMillis = 0l;
			for(TimePeriod timePeriod : timePeriods){
				AlarmEventQuery query = new AlarmEventQuery();
				java.util.Date startTime = timePeriod.getStartTime();
				java.util.Date endTime = timePeriod.getEndTime();
				query.setStart(startTime);
				query.setEnd(endTime);
				SysModuleEnum[] enums={SysModuleEnum.BUSSINESS};
				query.setSysIDes(enums);
				List<String> sourceIDes = new ArrayList<String>();
				sourceIDes.add(String.valueOf(id));
				if(sourceIDes.size()>0) query.setSourceIDes(sourceIDes);
				Page<AlarmEvent, AlarmEventQuery> page = alarmEventService.findAlarmEvent(query, 0, 10000);
				if(null!=page&&null!=page.getDatas()&&page.getDatas().size()>0){
					List<AlarmEvent> events = page.getDatas();
					for(int i=events.size()-1;i>=0;i--){
						if(i==events.size()-1){
							if(events.get(i).getContent().contains("???????????????")){
								timeMillis += (events.get(i).getCollectionTime().getTime()-startTime.getTime());
							}
						}else{
							if(events.get(i).getContent().contains("????????????")){
								if(i-1>=0){
									timeMillis += (events.get(i-1).getCollectionTime().getTime()-events.get(i).getCollectionTime().getTime());
								}else{
									timeMillis += (endTime.getTime()-events.get(i).getCollectionTime().getTime());
								}
							}
						}
					}
				} 
			}
			bizSerReport.setDownTime(Long.valueOf(timeMillis/1000/3600).floatValue());
		}
	}
	/**
	 * ??????????????????????????????????????????
	 * @param id
	 * @param bizSerReport
	 * @param timePeriods
	 * @param warnNum
	 */
	private void getWarnNumOrUnrecoveredWarnNum(Long id,BizSerReport bizSerReport,List<TimePeriod> timePeriods,boolean warnNum){
		List<String> ids = new ArrayList<String>();
		ids.add(String.valueOf(id));
		int count = 0;
		for(TimePeriod timePeriod : timePeriods){
			count += alarmEventService.countAlarmEvent(ids, SysModuleEnum.BUSSINESS, 
					InstanceStateEnum.values(), timePeriod.getStartTime(), timePeriod.getEndTime(), warnNum?null:false);
		}
		if(warnNum){
			bizSerReport.setWarnNum(count);
		}else{
			bizSerReport.setUnrecoveredWarnNum(count);
		}
	}
	/**
	 * ????????????
	 * @param availableMetricCountDatas
	 * @param methodName 
	 * @param desc asc ???????????????
	 * @return
	 */
	public static List<AvailableMetricCountData> SortByMethodName(
			List<AvailableMetricCountData> availableMetricCountDatas,final String methodName,final String desc){
		Comparator<AvailableMetricCountData> comparator = new Comparator<AvailableMetricCountData>() {
			@Override
			public int compare(AvailableMetricCountData o1,
					AvailableMetricCountData o2) {
				if("availabilityRate".equals(methodName)){
					return new Float((o1.getAvailabilityRate()-o2.getAvailabilityRate())*("desc".equals(desc)?-1:1)).intValue();
				}else if("notAvailabilityDurationHour".equals(methodName)){
					return new Float((o1.getNotAvailabilityDurationHour()-o2.getNotAvailabilityDurationHour())*("desc".equals(desc)?-1:1)).intValue();
				}else if("notAvailabilityNum".equals(methodName)){
					return new Float((o1.getNotAvailabilityNum()-o2.getNotAvailabilityNum())*("desc".equals(desc)?-1:1)).intValue();
				}else if("MTTR".equals(methodName)){
					return new Float((o1.getMTTR()-o2.getMTTR())*("desc".equals(desc)?-1:1)).intValue();
				}else if("MTBF".equals(methodName)){
					return new Float((o1.getMTBF()-o2.getMTBF())*("desc".equals(desc)?-1:1)).intValue();
				}
				return 0;
			}
		};
		Collections.sort(availableMetricCountDatas, comparator);
		return availableMetricCountDatas;
	}
	@Override
	public List<BizSerReport> getBizSerReports(Long[] ids) {
		return bizSerReportDao.getBizSerReports(ids);
	}

	@Override
	public List<BizSerMetric> getBizSerMetrics() {
		List<BizSerMetric> metrics = new ArrayList<BizSerMetric>(); 
		for(BizSerMetricEnum b:BizSerMetricEnum.values()){
			BizSerMetric metric = new BizSerMetric();
			metric.setId(b.getIndex());
			metric.setName(b.getName());
			metrics.add(metric);
		}
		return metrics;
	}

	public IBizSerReportDao getBizSerReportDao() {
		return bizSerReportDao;
	}

	public void setBizSerReportDao(IBizSerReportDao bizSerReportDao) {
		this.bizSerReportDao = bizSerReportDao;
	}
	
	public IBizServiceDao getBizServiceDao() {
		return bizServiceDao;
	}

	public void setBizServiceDao(IBizServiceDao bizServiceDao) {
		this.bizServiceDao = bizServiceDao;
	}

	public IBizStatusSelfDao getBizStatusSelfDao() {
		return bizStatusSelfDao;
	}

	public void setBizStatusSelfDao(IBizStatusSelfDao bizStatusSelfDao) {
		this.bizStatusSelfDao = bizStatusSelfDao;
	}
}
