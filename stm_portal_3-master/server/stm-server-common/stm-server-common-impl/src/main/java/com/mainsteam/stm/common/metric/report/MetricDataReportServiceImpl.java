package com.mainsteam.stm.common.metric.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.dao.MetricInfoDAO;
import com.mainsteam.stm.common.metric.dao.MetricSummaryDAO;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.obj.TimePeriod;

/**用于报表数据的接口
 * @author cx
 *
 */
public class MetricDataReportServiceImpl implements MetricDataReportService {
	private static final Log logger=LogFactory.getLog(MetricDataReportServiceImpl.class);

	private MetricSummaryDAO metricSummaryDAO;
	private MetricInfoDAO metricInfoDAO;
	
	public void setMetricSummaryDAO(MetricSummaryDAO metricSummaryDAO) {
		this.metricSummaryDAO = metricSummaryDAO;
	}
	public void setMetricInfoDAO(MetricInfoDAO metricInfoDAO) {
		this.metricInfoDAO = metricInfoDAO;
	}

	
	@Override
	public List<InstanceMetricSummeryReportData> findHistorySummaryData(MetricSummeryReportQuery query) {
		
		if(logger.isInfoEnabled())
			logger.info("start findHistorySummaryData:"+JSON.toJSONString(query,SerializerFeature.WriteDateUseDateFormat));
		
		List<MetricWithTypeForReport>  metricList=query.getMetricIDes();
		
		List<String> infoList=new ArrayList<>();
		
		for(MetricWithTypeForReport type:metricList){
			if(MetricTypeEnum.InformationMetric.equals(type.getType())){
				infoList.add(type.getMetricID());
			}
		}
		
		List<InstanceMetricSummeryReportData> list=metricSummaryDAO.findHistorySummaryDataForReport(query);
		
		if(infoList.size()>0){
			for(InstanceMetricSummeryReportData data:list){
				long instanceID=data.getInstanceID();
				List<MetricData> infoDataList=metricInfoDAO.getMetricInfoDatas(new long[]{instanceID},infoList.toArray(new String[]{}));
				for(MetricData md:infoDataList){
					MetricSummeryReportData rd=new MetricSummeryReportData();
					data.getMetricData().add(rd);
					rd.setMetricID(md.getMetricId());
					rd.setValue(md.getData());
				}
			}
		}
//		logger.warn("findHistorySummaryData:"+JSON.toJSONString(list,SerializerFeature.WriteDateUseDateFormat));
		if(logger.isInfoEnabled())
			logger.info("finish findHistorySummaryData query :"+JSON.toJSONString(list,SerializerFeature.WriteDateUseDateFormat));
		
		return list;
	}

	@Override
	public List<MetricSummeryReportData> findTopSummaryData(MetricDataTopQuery query){
		return metricSummaryDAO.findTopSummaryData(query);
	}

	@Override
	public List<InstanceMetricSummeryReportData> findInstanceHistorySummaryData(MetricSummeryReportQuery query) {

		List<String> pfList=new ArrayList<>();
		
		for(MetricWithTypeForReport type:query.getMetricIDes()){
			if(MetricTypeEnum.PerformanceMetric.equals(type.getType())){
				pfList.add(type.getMetricID());
			}
		}
		
		MetricSummaryType metricSummaryType = query.getSummaryType() == null ? MetricSummaryType.H : query.getSummaryType();
		
		List<MetricSummeryReportData> metricList = new ArrayList<>();
		for(TimePeriod tp:query.getTimePeriods()){
			List<MetricSummeryReportData> tmpList =metricSummaryDAO.findInstanceHistorySummaryData(pfList,query.getInstanceIDes(),tp.getStartTime(),tp.getEndTime(),metricSummaryType);
			metricList.addAll(tmpList);
		}
		
		
		List<InstanceMetricSummeryReportData> resultList=new ArrayList<>();
		//先按资源实例ID分组
		Map<Long,List<MetricSummeryReportData>> groupByInstanceMap=new HashMap<>();
		for(MetricSummeryReportData md:metricList){
			List<MetricSummeryReportData> mdList=groupByInstanceMap.get(md.getInstanceID());
			if(mdList==null){
				mdList=new ArrayList<>();
				groupByInstanceMap.put(md.getInstanceID(), mdList);
			}
			mdList.add(md);
		}
		//按每个结束时间点分组
		for(Entry<Long, List<MetricSummeryReportData>> ent:groupByInstanceMap.entrySet()){
			Map<Date,List<MetricSummeryReportData>> map=new HashMap<>();
			for(MetricSummeryReportData md:ent.getValue()){
				List<MetricSummeryReportData> mdList=map.get(md.getEndTime());
				if(mdList==null){
					mdList=new ArrayList<>();
					map.put(md.getEndTime(), mdList);
				}
				mdList.add(md);
			}
			
			for(Entry<Date, List<MetricSummeryReportData>> insEnt:map.entrySet()){
				InstanceMetricSummeryReportData data=new InstanceMetricSummeryReportData();
				data.setInstanceID(ent.getKey());
				data.setEndTime(insEnt.getKey());
				data.setMetricData(insEnt.getValue());
				resultList.add(data);
			}
		}
		return resultList;
	}
	
	
	public Map<String,Map<Long,List<MetricSummeryReportData>>> findInstanceMetricHistoryGroupByMetricID(MetricSummeryReportQuery query){
		
		//TODO 添加时间点
		Map<String,Map<Long,List<MetricSummeryReportData>>> resultMap=new HashMap<>();
		
		List<String> pfList=new ArrayList<>();
		for(MetricWithTypeForReport metric:query.getMetricIDes()){
			if(MetricTypeEnum.PerformanceMetric.equals(metric.getType())){
				pfList.add(metric.getMetricID());
			}
		}
		
		List<MetricSummeryReportData> metricList = new ArrayList<>();
		for(TimePeriod tp:query.getTimePeriods()){
			List<MetricSummeryReportData> tmpList =metricSummaryDAO.findInstanceHistorySummaryData(pfList,query.getInstanceIDes(),tp.getStartTime(),tp.getEndTime(),query.getSummaryType());
			metricList.addAll(tmpList);
		}
		
		
		Map<String,List<MetricSummeryReportData>> metricMap=new HashMap<>();
		for(MetricSummeryReportData data:metricList){
			List<MetricSummeryReportData> dataList=metricMap.get(data.getMetricID());
			if(dataList==null){
				dataList=new ArrayList<>();
				metricMap.put(data.getMetricID(), dataList);
			}
			dataList.add(data);
		}
		
		
		for(Entry<String,List<MetricSummeryReportData>> ent:metricMap.entrySet()){
			Map<Long,List<MetricSummeryReportData>> InstanceMap=new HashMap<>();
			for(MetricSummeryReportData data : ent.getValue()){
				List<MetricSummeryReportData> dataList=InstanceMap.get(data.getInstanceID());
				if(dataList==null){
					dataList=new ArrayList<>();
					InstanceMap.put(data.getInstanceID(), dataList);
				}
				dataList.add(data);
			}
			
			resultMap.put(ent.getKey(), InstanceMap);
		}
		return resultMap;
	}
	
}
