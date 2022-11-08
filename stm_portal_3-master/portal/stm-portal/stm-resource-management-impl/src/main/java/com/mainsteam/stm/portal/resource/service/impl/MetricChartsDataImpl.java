package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricSummaryService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.common.metric.query.MetricSummaryQuery;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.portal.resource.api.IMetricChartsDataApi;
import com.mainsteam.stm.portal.resource.bo.HighChartsDataBo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.util.DateUtil;

public class MetricChartsDataImpl implements IMetricChartsDataApi{

	private static Logger logger = Logger.getLogger(MetricChartsDataImpl.class);
	
	@Resource
	private CapacityService capacityService;
	@Resource
	private MetricSummaryService  metricSummaryService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private MetricDataService metricDataService ;
	@Resource
	private CustomMetricService customMetricService;
	@Resource
	private ProfileService profileService;
	/**
	 * highCharts
	 * 
	 * @return
	 */
	public HighChartsDataBo getHighChartsData(String highChartsQueryType,int highChartsQueryNum,long instanceId ,String metricId){
		
		List<MetricData> metricDataList = new ArrayList<MetricData>();
		List<MetricSummaryData> summaryDataList = new ArrayList<MetricSummaryData>();
		HighChartsDataBo hcdb = new HighChartsDataBo();
				
		Date dateEnd = new Date();
		Date dateStart = new Date();
		//设置图表x轴单位展现形式
		String xAxisStringType = "";
		List<Date>  dateList = new ArrayList<Date>();
		
		//指标采集频率是否在一小时内flag
		boolean oneHourFlag = true;
		switch (highChartsQueryType) {
		case "hour": 
			int frequencyTimeTemp = getFrequencyTime(instanceId,metricId);
			dateStart = getDateStart(highChartsQueryType,highChartsQueryNum,dateEnd);
//			if(frequencyTimeTemp == 0){
//				//取值时发生错误
//				oneHourFlag = false;
//			}else 
			if(frequencyTimeTemp == 60*60){
				//采集频度大于1小时
				oneHourFlag = false;
			}else if(0<frequencyTimeTemp && frequencyTimeTemp<60*60){
				metricDataList = getHistoryMetricDataByHour(instanceId, metricId,dateStart,dateEnd);
			}
			
			
			//获得该时间段时间数组
//			dateList = getDateArrayByMinite(dateStart,dateEnd);
//		    dateList = getDateArrayByTime(dateStart,dateEnd,frequencyTimeTemp,metricDataList);
		    
			xAxisStringType = "min";
			
			hcdb = getChartsDataByMetricData(metricDataList,xAxisStringType,dateStart,dateEnd);
			hcdb.setDateStart(dateStart);
			hcdb.setMinTimeType(xAxisStringType);
			
			break;
		case "day": dateStart = getDateStart("day",highChartsQueryNum,dateEnd);
		    MetricSummaryType mst = highChartsQueryNum==1?MetricSummaryType.H:MetricSummaryType.D;
		    xAxisStringType = highChartsQueryNum==1?"hour":"day";
		    if(highChartsQueryNum==1){
		    	//获得该时间段时间数组
		    	dateList = getDateArrayByHour(dateStart,dateEnd);
		    }else{
		    	//获得该时间段时间数组
		    	dateList = getDateArrayByDay(dateStart,dateEnd);
		    }
		    summaryDataList = getSummaryMetricDataByDay(mst,instanceId, metricId,dateStart,dateEnd);
		    
		    hcdb = getChartsDataByMetricSummaryData(summaryDataList,xAxisStringType,dateList);
			hcdb.setDateStart(dateStart);
			hcdb.setMinTimeType(xAxisStringType);
			break;
		
		}
		
		
		try {
			if(oneHourFlag){
				ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
				
				if(null != ri && null != ri.getResourceId()){
					String unitStr = "--";
					ResourceMetricDef rd = capacityService.getResourceMetricDef(ri.getResourceId(), metricId);
					if(null!=rd){
						unitStr = rd.getUnit();
					}else{
						List<CustomMetric> customMetrics = customMetricService.getCustomMetricsByInstanceId(instanceId);
						if(null!=customMetrics ){
							for(CustomMetric cmetric:customMetrics){
								CustomMetricInfo cmi = cmetric.getCustomMetricInfo();
								if(cmi.getId().equals(metricId)){
									unitStr = cmi.getUnit();
								}
							}
						}
					}
				    hcdb.setMetricUnitName(unitStr);
				}
			}else{
					hcdb.setMetricUnitName("moreThanOneHour");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		
		return hcdb;
	}
	
	/**
	 * 获取该指标采集频度,秒数
	 * 
	 * @return
	 */
	private int getFrequencyTime(long instanceId ,String metricId){
		String frequencyStr = "";
		try{
			CustomMetric cMetric = getCustomMetric( instanceId , metricId);
			if(null!=cMetric){
				frequencyStr = cMetric.getCustomMetricInfo().getFreq().name();
			}else{
				List<ProfileMetric> listProfileMetric = profileService.getMetricByInstanceId(instanceId);
				for(ProfileMetric pfm:listProfileMetric){
					if(pfm.getMetricId().equals(metricId)){
//						FrequentEnum.valueOf(pfm.getDictFrequencyId());
						frequencyStr = pfm.getDictFrequencyId();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
		
		switch(frequencyStr){
		case  "sec30":
			return 30;
		case  "min1":
			return 1*60;
		case  "min5":
			return 5*60;
		case  "min10":
			return 10*60;
		case  "min30":
			return 30*60;
		default :
			return 60*60;
		}
		
	}
	
	/**
	 * 获取data
	 * 
	 * @return
	 */
	public HighChartsDataBo getHighChartsDataByTime(long instanceId ,String metricId,Date dateStart,Date dateEnd){
		//计算两个时间的间隔         不足1hour   小于24hour    大于一天
		Calendar caStart = Calendar.getInstance();
		Calendar caEnd = Calendar.getInstance();
		caStart.setTime(dateStart);
		caEnd.setTime(dateEnd);
		
		long dateSub = caEnd.getTimeInMillis() - caStart.getTimeInMillis();
		String type = "";
		long num = dateSub/(60*60*1000);
		
		if(num>30){
			type = "day";
		}else if(num>1){
			type = "hour";
		}else{
			type = "min";
		}
		//type="day";

		/** 未用的局部变量
		String[] xAxis = new String[0];
		double[] dataDouble = new double[0];
		String[] dataStrArr = new String[0];
		*/
		//取出时间段内的数据
		List<MetricData> metricDataList = new ArrayList<MetricData>();
		List<MetricSummaryData> summaryDataList = new ArrayList<MetricSummaryData>();
		HighChartsDataBo hcdb = new HighChartsDataBo();
		List<Date>  dateList = new ArrayList<Date>();
		
		//指标采集频率是否在一小时内flag
		boolean oneHourFlag = true;
		switch (type) {
		case "hour":
			//获得该时间段时间数组
			dateList = getDateArrayByHour(dateStart,dateEnd);
			summaryDataList = getSummaryMetricDataByDay(MetricSummaryType.H,instanceId, metricId,dateStart,dateEnd);
			
			hcdb = getChartsDataByMetricSummaryData(summaryDataList,type,dateList);
			hcdb.setMinTimeType(type);
			break;
        case "day":
        	//获得该时间段时间数组
        	dateList = getDateArrayByDay(dateStart,dateEnd);
        	summaryDataList = getSummaryMetricDataByDay(MetricSummaryType.D,instanceId, metricId,dateStart,dateEnd);
        	
        	hcdb = getChartsDataByMetricSummaryData(summaryDataList,type,dateList);
			hcdb.setMinTimeType(type);
			break;
        case "min":
        	int frequencyTimeTemp = getFrequencyTime(instanceId,metricId);
			
//        	if(frequencyTimeTemp == 0){
//				//取值时发生错误
//				oneHourFlag = false;
//			}else 
			if(frequencyTimeTemp == 60*60){
				//采集频度大于1小时
				oneHourFlag = false;
			}else if(0<frequencyTimeTemp && frequencyTimeTemp<60*60){
				metricDataList = getHistoryMetricDataByHour(instanceId, metricId,dateStart,dateEnd);
			}
        	
        	//获得该时间段时间数组
//		    dateList = getDateArrayByTime(dateStart,dateEnd,frequencyTimeTemp,metricDataList);
			
        	hcdb = getChartsDataByMetricData(metricDataList,type,dateStart,dateEnd);
    		hcdb.setMinTimeType(type);
			break;
		}
		
		try {
			if(oneHourFlag){
				ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
			
				if(null != ri && null != ri.getResourceId()){
					String unitStr = "--";
					ResourceMetricDef rd = capacityService.getResourceMetricDef(ri.getResourceId(), metricId);
					if(null!=rd){
						unitStr = rd.getUnit();
					}else{
						List<CustomMetric> customMetrics = customMetricService.getCustomMetricsByInstanceId(instanceId);
						for(CustomMetric cmetric:customMetrics){
							CustomMetricInfo cmi = cmetric.getCustomMetricInfo();
							if(cmi.getId().equals(metricId)){
								unitStr = cmi.getUnit();
							}
						}
					}
				    hcdb.setMetricUnitName(unitStr);
				}
			}else{
					hcdb.setMetricUnitName("moreThanOneHour");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return hcdb;
	}
	
	public HighChartsDataBo getSpecialMetricChartsData(long instanceId ,String[] metricId,Date dateStart,Date dateEnd,String metricDataType){
		

				Calendar caStart = Calendar.getInstance();
				Calendar caEnd = Calendar.getInstance();
				caStart.setTime(dateStart);
				caEnd.setTime(dateEnd);
				
				String type = metricDataType;
				
				//取出时间段内的数据
				List<MetricData> metricDataList = new ArrayList<MetricData>();
				List<MetricSummaryData> summaryDataList = new ArrayList<MetricSummaryData>();
				HighChartsDataBo hcdb = new HighChartsDataBo();
				
				switch (type) {
		        case "halfHour":
		        	for(String id:metricId){
		        		summaryDataList = getSummaryMetricDataByDay(MetricSummaryType.HH,instanceId, id,dateStart,dateEnd);
		        		List<Map<String,String>> list = handleChartsSummaryData(summaryDataList,dateStart,dateEnd);
		        		hcdb.getDataMap().put(id, list);
		        	}
					break;
				case "hour":
					for(String id:metricId){
		        		summaryDataList = getSummaryMetricDataByDay(MetricSummaryType.H,instanceId, id,dateStart,dateEnd);
		        		List<Map<String,String>> list = handleChartsSummaryData(summaryDataList,dateStart,dateEnd);
		        		hcdb.getDataMap().put(id, list);
		        	}
					
//					int hourNum = 60*60*1000;
//					if(null!=summaryDataList && summaryDataList.size()>0){
//					//需要进行数据补全
//						List<MetricSummaryData> metricSummaryDataListAfter = new ArrayList<MetricSummaryData>();
//						metricSummaryDataListAfter.add(summaryDataList.get(0));
//					for(int i=0;i<summaryDataList.size()-1;i++){
//						MetricSummaryData md1 = summaryDataList.get(i);
//						MetricSummaryData md2 = summaryDataList.get(i+1);
//						
//						long dateNum2 = md2.getEndTime().getTime(),dateNum1 = md1.getEndTime().getTime();
//						long timeNum = dateNum1-dateNum2;
//						
//						Float metricData1 = md1.getMetricData();
//						Float metricData2 =md2.getMetricData();
//						
//						Float metricDataValue = metricData1-metricData2;
//						
//						if(timeNum>hourNum){
//							int metricDataNum = (int)timeNum/hourNum;
//							
//							Float addValue = metricDataValue/metricDataNum;
//							
//							for(int x=0;x<metricDataNum;x++){
//								MetricSummaryData md = new MetricSummaryData();
//								md.setEndTime(new Date(dateNum1-(hourNum*(x+1))));
//								md.setMetricData(metricData1-(addValue*x));
////								md.setMetricData(new Float(0));
//								metricSummaryDataListAfter.add(md);
//							}
//						}
//						metricSummaryDataListAfter.add(md2);
//					}
//					summaryDataList = metricSummaryDataListAfter;
//				}
//					hcdb = getChartsDataByMetricSummaryData(summaryDataList,type,dateList);
//					hcdb.setMinTimeType(type);
					
					break;
		        case "sixHour":
		        	for(String id:metricId){
		        		summaryDataList = getSummaryMetricDataByDay(MetricSummaryType.SH,instanceId, id,dateStart,dateEnd);
		        		List<Map<String,String>> list = handleChartsSummaryData(summaryDataList,dateStart,dateEnd);
		        		hcdb.getDataMap().put(id, list);
		        	}
		        	
					break;
		        case "min":
		        	for(String id:metricId){
		        		metricDataList = getHistoryMetricDataByHour(instanceId, id,dateStart,dateEnd);
						
			        	//去除采集值为null的metricData
			        	List<MetricData> metricDataListAfter = new ArrayList<MetricData>();
			        	if(metricDataList != null && !metricDataList.isEmpty()){
			        		for(MetricData md:metricDataList){
				        		if(null!=md.getData() && null!=md.getData()[0]){
				        			metricDataListAfter.add(md);
				        		}
				        	}
			        	}
			        	
			        	List<Map<String,String>> list =  handleChartsHistoryData(metricDataListAfter,type,dateStart,dateEnd);
			        	hcdb.getDataMap().put(id, list);
		        	}
		        	
					break;
				}
				
				try {
					ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
				
					if(null != ri && null != ri.getResourceId()){
						String unitStr = "--";
						ResourceMetricDef rd = capacityService.getResourceMetricDef(ri.getResourceId(), metricId[0]);
						if(null!=rd){
							unitStr = rd.getUnit();
						}else{
							List<CustomMetric> customMetrics = customMetricService.getCustomMetricsByInstanceId(instanceId);
							if(customMetrics != null && !customMetrics.isEmpty()){
								for(CustomMetric cmetric:customMetrics){
									CustomMetricInfo cmi = cmetric.getCustomMetricInfo();
									if(cmi.getId().equals(metricId[0])){
										unitStr = cmi.getUnit();
									}
								}	
							}
							
						}
					    hcdb.setMetricUnitName(unitStr);
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				
				return hcdb;
	}
	
	private static Date getDateStart(String type,int num , Date dataEnd){
		Date dateStart = null;
		switch (type) {
		case "hour": dateStart = DateUtil.subHour(dataEnd, num); 
		     break;
		case "day":  dateStart = DateUtil.subDay(dataEnd, num);
		     break;
		}
		return dateStart;
	}
	
	private List<Map<String,String>> handleChartsSummaryData(List<MetricSummaryData> metricDataList,Date dateStart,Date dateEnd){
		List<Map<String,String>> resultData = new ArrayList<Map<String,String>>();
		
		//有几个数据点，显示几个数据点的时间
		String[] xAxis = new String[metricDataList.size()+2];//2  6
		//有几个数据点，显示几个数据点的数据
		String[] dataDoubleStr = new String[metricDataList.size()+2];
		
		
		//数组头部，尾部添加
		Map<String,String> mapDataStart = new HashMap<String,String>();
		mapDataStart.put("data", "null");
		mapDataStart.put("collectTime", String.valueOf(dateStart.getTime()));
		resultData.add(mapDataStart);
		
		//根据返回的数据确定前台显示的x,y轴
		int length = metricDataList.size();
		Float maxMetricData = null;
		Float minMetricData = null;
		long maxDate = 0;
		long minDate = 0;
		if(metricDataList.size()>0){
			maxMetricData = metricDataList.get(0).getMaxMetricData();
			minMetricData = metricDataList.get(0).getMinMetricData();
			maxDate = metricDataList.get(0).getEndTime().getTime();
			minDate = metricDataList.get(0).getEndTime().getTime();
		}
		for(int i=length-1;i>-1;i--){
			String value ;
			MetricSummaryData msd = metricDataList.get(i);
			if(null!=msd.getMaxMetricData()){
				if(msd.getMaxMetricData()>maxMetricData){
					maxMetricData = msd.getMaxMetricData();
					maxDate = msd.getEndTime().getTime();
				}
			}
			if(null!=msd.getMinMetricData()){
				if(msd.getMinMetricData()<minMetricData){
					minMetricData=msd.getMinMetricData();
					minDate = msd.getEndTime().getTime();
				}
			}
			if(null!=msd.getMetricData()){
				value = String.valueOf(msd.getMetricData());
			}else{
				value = "null";
			}
			Map<String,String> mapData = new HashMap<String,String>();
			mapData.put("data", value);
			mapData.put("collectTime", String.valueOf(msd.getEndTime().getTime()));
			resultData.add(mapData);
		}
		Map<String,String> mapDataEnd = new HashMap<String,String>();
		mapDataEnd.put("data", "null");
		mapDataEnd.put("collectTime", String.valueOf(dateEnd.getTime()));
		mapDataEnd.put("MaxMetricData", maxMetricData==null?"":String.valueOf(maxMetricData));
		mapDataEnd.put("MinMetricData", minMetricData==null?"":String.valueOf(minMetricData));
		mapDataEnd.put("maxDate", String.valueOf(maxDate));
		mapDataEnd.put("minDate", String.valueOf(minDate));
		resultData.add(mapDataEnd);
		
		return resultData;
	}
	
	private List<Map<String,String>> handleChartsHistoryData(List<MetricData> metricDataList,String xAxisStringType,Date dateStart,Date dateEnd){
		List<Map<String,String>> resultData = new ArrayList<Map<String,String>>();
		
		
		//数组头部，尾部添加
		Map<String,String> mapDataStart = new HashMap<String,String>();
		mapDataStart.put("data", "null");
		mapDataStart.put("collectTime", String.valueOf(dateStart.getTime()));
		resultData.add(mapDataStart);
		
		//根据返回的数据确定前台显示的x,y轴
		Float maxMetricData = null;
		Float minMetricData = null;
		if(metricDataList.size()>0){
			maxMetricData = Float.valueOf(metricDataList.get(0).getData()[0]);
			minMetricData = Float.valueOf(metricDataList.get(0).getData()[0]);
		}
		for(int i=0;i<metricDataList.size();i++){
			String value;
			MetricData md = metricDataList.get(i);
			if(null!=md.getData()&& null!=md.getData()[0]){
				Float dataItem = Float.valueOf(md.getData()[0]);
				if(dataItem>maxMetricData){
					maxMetricData=dataItem;
				}
				if(dataItem<minMetricData){
					minMetricData=dataItem;
				}
			}
			
			if(null!=metricDataList.get(i).getData() && null!=metricDataList.get(i).getData()[0]){
				value = metricDataList.get(i).getData()[0];
			}else{
				value = "null";
			}
			
			Map<String,String> mapData = new HashMap<String,String>();
			mapData.put("data", value);
			mapData.put("collectTime", String.valueOf(metricDataList.get(i).getCollectTime().getTime()));
			resultData.add(mapData);
		}
		Map<String,String> mapDataEnd = new HashMap<String,String>();
		mapDataEnd.put("data", "null");
		mapDataEnd.put("collectTime", String.valueOf(dateEnd.getTime()));
		mapDataEnd.put("MaxMetricData", maxMetricData==null?"":String.valueOf(maxMetricData));
		mapDataEnd.put("MinMetricData", minMetricData==null?"":String.valueOf(minMetricData));
		resultData.add(mapDataEnd);
		return resultData;
	}
	
	private HighChartsDataBo getChartsSummaryDataByMetricData(List<MetricSummaryData> metricDataList,Date dateStart,Date dateEnd){
		HighChartsDataBo hcdb = new HighChartsDataBo();
		//有几个数据点，显示几个数据点的时间
		String[] xAxis = new String[metricDataList.size()+2];//2  6
		//有几个数据点，显示几个数据点的数据
		String[] dataDoubleStr = new String[metricDataList.size()+2];
		
		
		//数组头部，尾部添加
		xAxis[0] = String.valueOf(dateStart.getTime());
		xAxis[metricDataList.size()+1] = String.valueOf(dateEnd.getTime());
		dataDoubleStr[0] = "null";
		dataDoubleStr[metricDataList.size()+1] = "null";
		
		//根据返回的数据确定前台显示的x,y轴
		int length = metricDataList.size();
		for(int i=length-1;i>-1;i--){
			int arrPosition = length-i;
			if(null!=metricDataList.get(i).getMetricData()){
				dataDoubleStr[arrPosition] = String.valueOf(metricDataList.get(i).getMetricData());
			}else{
				dataDoubleStr[arrPosition] = "null";
			}
			xAxis[arrPosition] = String.valueOf(metricDataList.get(i).getEndTime().getTime());
		}
		
		hcdb.setxAxis(xAxis);
		hcdb.setDataDoubleStr(dataDoubleStr);
//		hcdb.setTimezoneOffset(dateStart.getTimezoneOffset());
		
		return hcdb;
	}
	
	private HighChartsDataBo getChartsDataByMetricData(List<MetricData> metricDataList,String xAxisStringType,Date dateStart,Date dateEnd){
		HighChartsDataBo hcdb = new HighChartsDataBo();
		//有几个数据点，显示几个数据点的时间
		String[] xAxis = new String[metricDataList.size()+2];//2  6
		//有几个数据点，显示几个数据点的数据
		String[] dataDoubleStr = new String[metricDataList.size()+2];
		
		
		//数组头部，尾部添加
		xAxis[0] = String.valueOf(dateStart.getTime());
//		xAxis[1] = String.valueOf(dateStart.getTime()+1000*60*3);
//		xAxis[2] = String.valueOf(dateStart.getTime()+1000*60*17);
//		xAxis[3] = String.valueOf(dateStart.getTime()+1000*60*21);
//		xAxis[4] = String.valueOf(dateStart.getTime()+1000*60*29);
		xAxis[metricDataList.size()+1] = String.valueOf(dateEnd.getTime());
		dataDoubleStr[0] = "null";
//		dataDoubleStr[1] = "7.23";
//		dataDoubleStr[2] = "13.5";
//		dataDoubleStr[3] = "20";
//		dataDoubleStr[4] = "19.5";
		dataDoubleStr[metricDataList.size()+1] = "null";
		
		//根据返回的数据确定前台显示的x,y轴
		for(int i=0;i<metricDataList.size();i++){
			if(null!=metricDataList.get(i).getData() && null!=metricDataList.get(i).getData()[0]){
				dataDoubleStr[i+1] = metricDataList.get(i).getData()[0];
			}else{
				dataDoubleStr[i+1] = "null";
			}
			xAxis[i+1] = String.valueOf(metricDataList.get(i).getCollectTime().getTime());
		}
		
		hcdb.setxAxis(xAxis);
		hcdb.setDataDoubleStr(dataDoubleStr);
//		hcdb.setTimezoneOffset(dateStart.getTimezoneOffset());
		
		return hcdb;
	}
	
//	private HighChartsDataBo getChartsDataByMetricData(List<MetricData> metricDataList,String xAxisStringType,List<Date>  dateList){
//		HighChartsDataBo hcdb = new HighChartsDataBo();
//		//有几个数据点，显示几个数据点的时间
//		String[] xAxis = new String[metricDataList.size()];
//		//绘制全部时间点
//		String[] xAxisFull = new String[dateList.size()];
//		//有几个数据点，显示几个数据点的数据
//		double[] dataDouble = new double[metricDataList.size()];
//		String[] dataDoubleStr = new String[metricDataList.size()];
//		//绘制全部时间点《该时间点无数据则值为‘null’
//		String[] dataStrArr = new String[dateList.size()];
//		
//		//根据返回的数据确定前台显示的x,y轴
//		for(int i=0;i<metricDataList.size();i++){
//			if(null!=metricDataList.get(i).getData() && null!=metricDataList.get(i).getData()[0]){
//				dataDouble[i] = Double.parseDouble(metricDataList.get(i).getData()[0]);
//				dataDoubleStr[i] = metricDataList.get(i).getData()[0];
//			}else{
//				dataDouble[i] = 0.0;
//				dataDoubleStr[i] = "null";
//			}
//			String str = xAxisDateFormat(metricDataList.get(i).getCollectTime(),xAxisStringType);//DateUtil.format(metricDataList.get(i).getCollectTime());
//			
//			xAxis[i] = str;
//		}
//		//根据时间段确定前台显示的x,y轴
//		for(int x=0;x<dateList.size();x++){
//			xAxisFull[x] = xAxisDateFormat(dateList.get(x),xAxisStringType);
//			dataStrArr[x] = "null";
//			
//			if("min".equals(xAxisStringType)){
//				for(MetricData md:metricDataList){
//					//采集时间转化为整数分钟
//					if(0>md.getCollectTime().compareTo(addSECOND(dateList.get(x),5)) && 
//							   0<md.getCollectTime().compareTo(addSECOND(dateList.get(x),-5))){
//						if(null!=md.getData() && null!=md.getData()[0]){
//							dataStrArr[x] = md.getData()[0];
//						}
//					}
//				}
//			}else{
//				for(MetricData md:metricDataList){
//					if(0==dateList.get(x).compareTo(md.getCollectTime())){
//						if(null!=md.getData() && null!=md.getData()[0]){
//							dataStrArr[x] = md.getData()[0];
//						}
//					}
//				}
//			}
//			
//		}
//		
////		hcdb.setDataDouble(dataDouble);
//		hcdb.setDataStrArr(dataStrArr);
//		hcdb.setxAxisFull(xAxisFull);
//		if(metricDataList.size()==0){
//			hcdb.setxAxis(xAxisFull);
//			hcdb.setDataDoubleStr(dataStrArr);
//		}else{
//			hcdb.setxAxis(xAxis);
//			hcdb.setDataDoubleStr(dataDoubleStr);
//		}
//		
//		
//		
//		return hcdb;
//	}
	
	private HighChartsDataBo getChartsDataByMetricSummaryData(List<MetricSummaryData> metricDataList,String xAxisStringType,List<Date>  dateList){
		HighChartsDataBo hcdb = new HighChartsDataBo();
		String[] xAxis = new String[metricDataList.size()];
		String[] xAxisFull = new String[dateList.size()];
		double[] dataDouble = new double[metricDataList.size()];
		String[] dataDoubleStr = new String[metricDataList.size()];
		String[] dataStrArr = new String[dateList.size()];
		
		//根据返回的数据确定前台显示的x,y轴
		for(int i=0;i<metricDataList.size();i++){
			if(null!=metricDataList.get(i).getMetricData()){
				dataDouble[i] = metricDataList.get(i).getMetricData().doubleValue();
				dataDoubleStr[i] = String.valueOf(metricDataList.get(i).getMetricData());
			}else{
				dataDouble[i] = 0.0;
				dataDoubleStr[i] = "null";
			}
			String str = xAxisDateFormat(metricDataList.get(i).getEndTime(),xAxisStringType);//DateUtil.format(metricDataList.get(i).getCollectTime());
			
			xAxis[i] = str;
		}
		//根据时间段确定前台显示的x,y轴
		for(int x=0;x<dateList.size();x++){
			xAxisFull[x] = xAxisDateFormat(dateList.get(x),xAxisStringType);
			dataStrArr[x] = "null";
			
			if("hour".equals(xAxisStringType)){
				for(MetricSummaryData md:metricDataList){
					//采集时间转化为整数分钟
					if(0>md.getEndTime().compareTo(getRoundMiniteByNum(dateList.get(x),5)) && 
					   0<md.getEndTime().compareTo(getRoundMiniteByNum(dateList.get(x),-5))
							){
						if(null!=md.getMetricData()){
							dataStrArr[x] = String.valueOf(md.getMetricData());
						}
					}
				}
			}else{
				for(MetricSummaryData md:metricDataList){
					if(0>md.getEndTime().compareTo(getRoundMiniteByNum(dateList.get(x),30)) && 
							   0<md.getEndTime().compareTo(getRoundMiniteByNum(dateList.get(x),-30))){
						if(null!=md.getMetricData()){
							dataStrArr[x] = String.valueOf(md.getMetricData());
						}
					}
				}
			}
			
		}
		
//		hcdb.setDataDouble(dataDouble);
		hcdb.setDataStrArr(dataStrArr);
//		hcdb.setxAxisFull(xAxisFull);
		hcdb.setxAxis(xAxis);
		hcdb.setDataDoubleStr(dataDoubleStr);
		
		return hcdb;
	}
	
	
//	private String xAxisDateFormat(Date date,String xAxisStringType){
//		String[] HMS = DateUtil.format(date).split(" "); //2014-9-9 12:12:12
//		String[] dateStr = HMS[0].split("-"); 
//		String[] time = HMS[1].split(":"); 
//		
//		switch (xAxisStringType) {
//		case "min":
//			return time[0]+":"+time[1];
//        case "hour":
//        	return dateStr[2]+"/"+time[0]+":"+time[1];
//        case "day":
//        	return HMS[0].substring(5);
//		}
//		return null;
//	}
	
	private String xAxisDateFormat(Date date,String xAxisStringType){
		Calendar calendar=Calendar.getInstance();   
		calendar.setTime(date); 
		
		switch (xAxisStringType) {
		case "min":
			return calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
        case "hour":
        	return calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
        case "day":
        	return (calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
		}
		return null;
	}

//	private String[] getDateStrArrayByHour(Date dateStart,Date dateEnd){
//		
//		Date date = getRoundHourByNum(dateStart , 1);
//		
//		List<String> list = new ArrayList<String>();
//		while (dateEnd.compareTo(date) > 0) {
//			list.add(DateUtil.format(date));
//			date = DateUtil.addHour(date, 1);
//		}
//		String[] strArr = new String[list.size()];
//		for(int i=0;i<list.size();i++){
//			strArr[i] = list.get(i);
//		}
//		return strArr;
//	}
//	private String[] getDateStrArrayByDay(Date dateStart,Date dateEnd){
//		
//		Date date = getRoundDayByNum(dateStart , 1);
//		
//		List<String> list = new ArrayList<String>();
//		while (dateEnd.compareTo(date) > 0) {
//			list.add(DateUtil.format(date));
//			date = DateUtil.addDay(date, 1);
//		}
//		String[] strArr = new String[list.size()];
//		for(int i=0;i<list.size();i++){
//			strArr[i] = list.get(i);
//		}
//		return strArr;
//	}
	
//	private List<Date> getDateArrayByTime(Date dateStart,Date dateEnd,int frequencyTime , List<MetricData> metricDataList){
//		
//		Date dateStartRound = getRoundMiniteByNum(dateStart , 0);
//		List<Date> listDate = new ArrayList<Date>();
//		
//		if(metricDataList.size()>0){
//			Date dateSource = metricDataList.get(0).getCollectTime();
//			//找到离开始时间最近的，应采集的点的时间
//			while(addSECOND(dateSource,-frequencyTime).after(dateStartRound)){
//				dateSource = addSECOND(dateSource,-frequencyTime);
//			}
//			while(dateSource.before(dateEnd)){
//				listDate.add(dateSource);
//				dateSource = addSECOND(dateSource,frequencyTime);
//			}
//		}else{
//			dateStartRound = addSECOND(dateStartRound,frequencyTime);
//			while(dateStartRound.before(dateEnd)){
//				listDate.add(dateStartRound);
//				dateStartRound = addSECOND(dateStartRound,frequencyTime);
//			}
//		}
//		
//		return listDate;
//	}
//	private List<Date> getDateArrayByMinite(Date dateStart,Date dateEnd){
//			
//			Date date = getRoundMiniteByNum(dateStart , 0);
//			
//			List<Date> list = new ArrayList<Date>();
//			while (dateEnd.compareTo(date) > 0) {
//				list.add(date);
//				date = addMinite(date, 1);
//			}
//			return list;
//	}
    private List<Date> getDateArrayByHour(Date dateStart,Date dateEnd){
		
		Date date = getRoundHourByNum(dateStart , 1);
		
		List<Date> list = new ArrayList<Date>();
		while (dateEnd.compareTo(date) > 0) {
			list.add(date);
			date = DateUtil.addHour(date, 1);
		}
		return list;
	}
	private List<Date> getDateArrayByDay(Date dateStart,Date dateEnd){
		
		Date date = getRoundDayByNum(dateStart , 1);
		
		List<Date> list = new ArrayList<Date>();
		while (dateEnd.compareTo(date) > 0) {
			list.add(date);
			date = DateUtil.addDay(date, 1);
		}
		return list;
	}
	
	//获得与指定时间的整数分钟相差整数分钟的时间
//	private static Date getRoundMiniteByNum(Date date,int num){
//		
//		String[] HMS = DateUtil.format(date).split(" "); //2014-9-9 12:12:12
//		String[] time = HMS[1].split(":"); 
//		Date dateRound = DateUtil.parseDateTime(HMS[0]+" "+time[0]+":"+time[1]+":00");
//		
//		return addMinite(dateRound,num);
//	}
	private static Date getRoundMiniteByNum(Date date,int num){
		Calendar calendar=Calendar.getInstance();   
		calendar.setTime(date); 
		calendar.set(Calendar.SECOND,0);
		return addMinite(calendar.getTime(),num);
	}
	
//	private static Date getRoundSecondeByNum(Date date,int num){
//		
//		String[] HMS = DateUtil.format(date).split(" "); //2014-9-9 12:12:12
//		String[] time = HMS[1].split(":"); 
//		Date dateRound = DateUtil.parseDateTime(HMS[0]+" "+time[0]+":"+time[1]+":00");
//		
//		return addSECOND(dateRound,num);
//	}
	private static Date addMinite(Date date,int minite){
		Calendar calendar=Calendar.getInstance();   
		calendar.setTime(date); 
		calendar.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE)+minite);
		return calendar.getTime();
	}
//	private static Date addSECOND(Date date,int second){
//		Calendar calendar=Calendar.getInstance();   
//		calendar.setTime(date); 
//		calendar.set(Calendar.SECOND,calendar.get(Calendar.SECOND)+second);
//		return calendar.getTime();
//	}
	//获得与指定时间的整数小时相差整数小时的时间
//	private static Date getRoundHourByNum(Date date,int num){
//		
//		String[] HMS = DateUtil.format(date).split(" "); //2014-9-9 12:12:12
//		String[] time = HMS[1].split(":"); 
//		Date dateRound = DateUtil.parseDateTime(HMS[0]+" "+time[0]+":00:00");
//		
//		return DateUtil.addHour(dateRound, num);
//	}
	private static Date getRoundHourByNum(Date date,int num){
	   Calendar calendar=Calendar.getInstance();   
	   calendar.setTime(date); 
	   calendar.set(Calendar.MINUTE,0);
	   calendar.set(Calendar.SECOND,0);
		return DateUtil.addHour(calendar.getTime(), num);
	}
	//获得与指定时间的整数天相差整数天的时间     2014-9-9 12:12:12 ->   2014-9-9 00:00:00
//	private static Date getRoundDayByNum(Date date,int num){
//		
//		String[] HMS = DateUtil.format(date).split(" "); 
//		Date dateRound = DateUtil.parseDateTime(HMS[0]+" 00:00:00");
//		
//		return DateUtil.addDay(dateRound, num);
//	}
	private static Date getRoundDayByNum(Date date,int num){
	   Calendar calendar=Calendar.getInstance();   
	   calendar.setTime(date); 
	   calendar.set(Calendar.HOUR_OF_DAY,0);
	   calendar.set(Calendar.MINUTE,0);
	   calendar.set(Calendar.SECOND,0);
	   return DateUtil.addDay(calendar.getTime(), num);
	}
	
	
//	//YYYY-MM-DD HH:MM:SS
//	private String parseDateStrToMINITE(Date date){
//		String HMS = DateUtil.format(date).split(" ")[1];
//		String[] time = HMS.split(":");
//		String str = time[0]+":"+time[1];
//		return str;
//	}
//	private String parseDateStrToHOUR(Date date){
//		String HMS = DateUtil.format(date).split(" ")[1];
//		String[] time = HMS.split(":");
//		String str = time[0]+":"+time[1];
//		return str;     //2014-01-19 19:12:12
//	}
//	private String parseDateStrToDAY(Date date){
//		
//		return null;
//	}
	
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
	
	private CustomMetric getCustomMetric(long instanceId ,String metricId){
		try {
			List<CustomMetric> customMetrics = customMetricService.getCustomMetricsByInstanceId(instanceId);
			if(null!=customMetrics){
				for(CustomMetric cmetric:customMetrics){
					if(cmetric.getCustomMetricInfo().getId().equals(metricId)){
						 return cmetric;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
		return null;
	}

	@Override
	public HighChartsDataBo getSpecialMetricChartData(long instanceId,
			String[] metricId, Date dateStart, Date dateEnd,
			String metricDataType) {
		
	
				Calendar caStart = Calendar.getInstance();
				Calendar caEnd = Calendar.getInstance();
				caStart.setTime(dateStart);
				caEnd.setTime(dateEnd);
				
				String type = metricDataType;
				
				//取出时间段内的数据
				List<MetricData> metricDataList = new ArrayList<MetricData>();
				List<MetricSummaryData> summaryDataList = new ArrayList<MetricSummaryData>();
				HighChartsDataBo hcdb = new HighChartsDataBo();
				
				switch (type) {
		        case "halfHour":
		        	for(String id:metricId){
		        		summaryDataList = getSummaryMetricDataByDay(MetricSummaryType.HH,instanceId, id,dateStart,dateEnd);
		        		List<Map<String,String>> list = handleChartsSummaryData(summaryDataList,dateStart,dateEnd);
		        		hcdb.getDataMap().put("merticValue", list);
		        	}
					break;
				case "hour":
					for(String id:metricId){
		        		summaryDataList = getSummaryMetricDataByDay(MetricSummaryType.H,instanceId, id,dateStart,dateEnd);
		        		List<Map<String,String>> list = handleChartsSummaryData(summaryDataList,dateStart,dateEnd);
		        		hcdb.getDataMap().put("merticValue", list);
		        	}
					

					
					break;
		        case "sixHour":
		        	for(String id:metricId){
		        		summaryDataList = getSummaryMetricDataByDay(MetricSummaryType.SH,instanceId, id,dateStart,dateEnd);
		        		List<Map<String,String>> list = handleChartsSummaryData(summaryDataList,dateStart,dateEnd);
		        		hcdb.getDataMap().put("merticValue", list);
		        	}
		        	
					break;
		        case "min":
		        	for(String id:metricId){
		        		metricDataList = getHistoryMetricDataByHour(instanceId, id,dateStart,dateEnd);
						
			        	//去除采集值为null的metricData
			        	List<MetricData> metricDataListAfter = new ArrayList<MetricData>();
			        	if(metricDataList!=null){
			        		for(MetricData md:metricDataList){
			        			
				        		if(null!=md.getData() && null!=md.getData()[0]){
				        			metricDataListAfter.add(md);
				        		}
				        	}
			        	}
			        	
			        	List<Map<String,String>> list =  handleChartsHistoryData(metricDataListAfter,type,dateStart,dateEnd);
			        	hcdb.getDataMap().put("merticValue", list);
		        	}
		        	
					break;
				}
				
				try {
					ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
				
					if(null != ri && null != ri.getResourceId()){
						String unitStr = "--";
						ResourceMetricDef rd = capacityService.getResourceMetricDef(ri.getResourceId(), metricId[0]);
						if(null!=rd){
							unitStr = rd.getUnit();
						}else{
							List<CustomMetric> customMetrics = customMetricService.getCustomMetricsByInstanceId(instanceId);
							for(CustomMetric cmetric:customMetrics){
								CustomMetricInfo cmi = cmetric.getCustomMetricInfo();
								if(cmi.getId().equals(metricId[0])){
									unitStr = cmi.getUnit();
								}
							}
						}
					    hcdb.setMetricUnitName(unitStr);
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				
				return hcdb;
	}
}
