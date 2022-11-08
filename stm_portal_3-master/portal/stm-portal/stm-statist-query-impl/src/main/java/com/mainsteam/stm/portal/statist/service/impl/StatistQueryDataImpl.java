package com.mainsteam.stm.portal.statist.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
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
import com.mainsteam.stm.common.metric.report.InstanceMetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricDataReportService;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportQuery;
import com.mainsteam.stm.common.metric.report.MetricWithTypeForReport;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.ireport.ExportReportTemplate;
import com.mainsteam.stm.ireport.IreportFileTypeEnum;
import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.report.bo.Chart;
import com.mainsteam.stm.portal.report.bo.ChartData;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsData;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.portal.report.service.impl.ReportModelFillReport;
import com.mainsteam.stm.portal.statist.api.IStatistQueryDataApi;
import com.mainsteam.stm.portal.statist.bo.ChartBaseBo;
import com.mainsteam.stm.portal.statist.bo.ChartLineBo;
import com.mainsteam.stm.portal.statist.bo.ChartLineSeriesBo;
import com.mainsteam.stm.portal.statist.bo.ChartPieBo;
import com.mainsteam.stm.portal.statist.bo.ChartPieSeriesBo;
import com.mainsteam.stm.portal.statist.bo.ChartTableBo;
import com.mainsteam.stm.portal.statist.bo.ChartTableTdBo;
import com.mainsteam.stm.portal.statist.bo.ChartTitileBo;
import com.mainsteam.stm.portal.statist.bo.StatQChartBo;
import com.mainsteam.stm.portal.statist.bo.StatistQueryInstanceBo;
import com.mainsteam.stm.portal.statist.bo.StatistQueryMainBo;
import com.mainsteam.stm.portal.statist.bo.StatistQueryMetricBo;
import com.mainsteam.stm.portal.statist.dao.IStatistQueryDetailDao;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.util.UnitTransformUtil;

public class StatistQueryDataImpl implements IStatistQueryDataApi {

	private static final Log logger = LogFactory.getLog(StatistQueryDataImpl.class);

	@Resource
	private IStatistQueryDetailDao statistQueryDetailDao;
	
	@Resource
	private MetricDataReportService metricDataReportService;
	
	@Resource
	private MetricDataService metricDataService;

	@Resource
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private CapacityService capacityService;

	@Autowired
	private IDomainApi stm_system_DomainApi;

	@Resource
	private MetricStateService metricStateService;
	
	@Resource
	private MetricSummaryService  metricSummaryService;

	@Resource
	private IFileClientApi fileClient;
	
	private DateFormat parseDateTime = new SimpleDateFormat("MM-dd HH:mm");

	private static String INFO_REGEX_STR = ",";
	
	private static String DATA_REGEX_STR = "#--!!--#";
	
	@Override
	public StatQChartBo getStatQDataByStatQMainId(StatistQueryMainBo statQMainBo, String startTime, String endTime) {
		StatQChartBo statQDataBo = null;
		try {
			switch (statQMainBo.getType()) {
			case "1":
				statQDataBo = getStatQPerformanceData(statQMainBo, startTime, endTime);
				break;
			case "2":
				statQDataBo = getStatQPropertyCountData(statQMainBo);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getStatQDataByStatQMainId:", e);
		}
		return statQDataBo;
	}
	
	private StatQChartBo getStatQPerformanceData(StatistQueryMainBo statQMainBo, String startTime, String endTime) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date startDate = sdf.parse(startTime), endDate = sdf.parse(endTime);
		
		StatQChartBo statQDataBO = new StatQChartBo();
		
		SimpleDateFormat otherformatSdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		statQDataBO.setStartTime(otherformatSdf.format(startDate));
		statQDataBO.setEndTime(otherformatSdf.format(endDate));
		statQDataBO.setName(statQMainBo.getName());
		List<ChartBaseBo> chartBoList = new ArrayList<ChartBaseBo>();
		statQDataBO.setChartBoList(chartBoList);
		
		MetricSummeryReportQuery sumQuery = new MetricSummeryReportQuery();
		List<StatistQueryInstanceBo> statQInstList = statQMainBo.getStatQInstBoList();
		List<Long> instanceIdList = new ArrayList<Long>();
		for (int i = 0; statQInstList != null && i < statQInstList.size(); i++) {
			instanceIdList.add(statQInstList.get(i).getInstanceId());
		}
		sumQuery.setInstanceIDes(instanceIdList);
		List<StatistQueryMetricBo> statQMetricList = statQMainBo.getStatQMetricBoList();
		List<MetricWithTypeForReport> metricIdList = new ArrayList<MetricWithTypeForReport>();
		for (int i = 0; statQMetricList != null && i < statQMetricList.size(); i++) {
			metricIdList.add(new MetricWithTypeForReport(statQMetricList.get(i).getMetricId(), MetricTypeEnum.PerformanceMetric));
		}
		sumQuery.setMetricIDes(metricIdList);
		List<TimePeriod> timePeriodList = new ArrayList<TimePeriod>();
		TimePeriod tp = new TimePeriod();
		tp.setStartTime(startDate);
		tp.setEndTime(endDate);
		timePeriodList.add(tp);
		sumQuery.setTimePeriods(timePeriodList);
		
		List<InstanceMetricSummeryReportData> imsrdList = null;
		if(instanceIdList != null && !instanceIdList.isEmpty()){
			imsrdList = metricDataReportService.findHistorySummaryData(sumQuery);
		}
		for (int i = 0; i < instanceIdList.size(); i++) {
			Long instanceId = instanceIdList.get(i);
			// 查询资源数据
			ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
			if(ri == null){
				continue;
			}
			// 判断是否是子资源
			String resourceShowName = ri.getShowName() != null && !"".equals(ri.getShowName()) ? ri.getShowName() : ri.getName();
			if(ri.getParentId() != 0){
				ResourceInstance parentRi = resourceInstanceService.getResourceInstance(ri.getParentId());
				resourceShowName = parentRi != null ? parentRi.getShowName() + " -- " + resourceShowName : resourceShowName;
			}
			// 设置title
			ChartTitileBo chartTitleBo = new ChartTitileBo();
			chartTitleBo.setTitle(resourceShowName);
			chartBoList.add(chartTitleBo);
			
			List<InstanceMetricSummeryReportData> subImsrdList = new ArrayList<InstanceMetricSummeryReportData>();
			for (int j = 0; imsrdList != null && j < imsrdList.size(); j++) {
				if(instanceId.longValue() == imsrdList.get(j).getInstanceID()){
					subImsrdList.add(imsrdList.get(j));
				}
			}
			//按时间排序
			Collections.sort(subImsrdList, new Comparator<InstanceMetricSummeryReportData>(){
				@Override
				public int compare(InstanceMetricSummeryReportData d1, InstanceMetricSummeryReportData d2) {
					return d1.getEndTime().compareTo(d2.getEndTime());
				}
			});
			
			Map<Long, Map<String, MetricSummeryReportData>> msrdMapMap = new HashMap<Long, Map<String, MetricSummeryReportData>>();
			for(InstanceMetricSummeryReportData imsrd : subImsrdList){
				List<MetricSummeryReportData> msrdList = imsrd.getMetricData();
				Map<String,MetricSummeryReportData> msrdMap = new HashMap<String,MetricSummeryReportData>();
				for(MetricSummeryReportData msrd : msrdList){
					msrdMap.put(msrd.getMetricID(), msrd);
				}
				msrdMapMap.put(instanceId, msrdMap);
			}
			// 图表数据
			for (int j = 0; j < statQMetricList.size(); j++) {
				String metricId = statQMetricList.get(j).getMetricId();
				ResourceMetricDef rmd =	capacityService.getResourceMetricDef(ri.getResourceId(), metricId);
				// 表格数据
				List<ChartTableTdBo> ctData = new ArrayList<ChartTableTdBo>();
				ctData.add(new ChartTableTdBo(rmd.getName()));
				// 当前值
				MetricRealtimeDataQuery mrdq = new MetricRealtimeDataQuery();
				mrdq.setInstanceID(new long[]{instanceId});
				mrdq.setMetricID(new String[]{metricId});
				Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(mrdq, 0, 1000);
				String currentValue = page != null && page.getDatas().size() > 0 ? page.getDatas().get(0).get(metricId) != null 
										? page.getDatas().get(0).get(metricId).toString() : "null" : "null";
				
				if("null".equals(currentValue)){
					ctData.add(new ChartTableTdBo(currentValue));
				}else{
					ctData.add(new ChartTableTdBo(UnitTransformUtil.transform(currentValue, rmd.getUnit())));
				}
				
				
				MetricSummeryReportData msrd = msrdMapMap.get(instanceId).get(metricId);
				Long minfloat = -1L;
				Long maxfloat = -1L;
				Long avgfloat = -1L;
				if(msrd != null){
					minfloat = msrd.getMin().longValue();
					maxfloat = msrd.getMax().longValue();
					avgfloat = msrd.getAvg().longValue();
					ctData.add(new ChartTableTdBo(UnitTransformUtil.transform(String.valueOf(minfloat), rmd.getUnit())));
					ctData.add(new ChartTableTdBo(UnitTransformUtil.transform(String.valueOf(maxfloat), rmd.getUnit())));
					ctData.add(new ChartTableTdBo(UnitTransformUtil.transform(String.valueOf(avgfloat), rmd.getUnit())));
				}
				// categories
				List<String> categories = new ArrayList<String>();
				// 曲线图的值 data
				List<String> lineSeriesData = new ArrayList<String>();
				//查询汇总数据
				MetricSummaryQuery msq = new MetricSummaryQuery();
				msq.setSummaryType(MetricSummaryType.HH);
				msq.setInstanceID(instanceIdList.get(i));
				msq.setMetricID(metricId);
				msq.setEndTime(sdf.parse(endTime));
				msq.setStartTime(sdf.parse(startTime));
				List<MetricSummaryData> mdList = metricSummaryService.queryMetricSummary(msq);
//				List<MetricData> mdList = metricDataService.queryHistoryMetricData(metricId, instanceIdList.get(i), sdf.parse(startTime), sdf.parse(endTime));
				if(mdList == null){
					logger.error("QueryMetricSummary error , metricId : " + metricId + ",instanceId : " + instanceIdList.get(i));
					continue;
				}
				for(int x = mdList.size() - 1 ; x > 0 ; x--){
					MetricSummaryData md = mdList.get(x);
					if(md.getMetricData() == null){
						lineSeriesData.add("null");
					}else{
						if(maxfloat > 0 && md.getMaxMetricData() != null && md.getMaxMetricData().longValue() == maxfloat.longValue()){
							lineSeriesData.add(String.valueOf(md.getMaxMetricData()));
						}else if(minfloat > 0 && md.getMinMetricData() != null && md.getMinMetricData().longValue() == minfloat.longValue()){
							lineSeriesData.add(String.valueOf(md.getMinMetricData()));
						}else{
							lineSeriesData.add(String.valueOf(md.getMetricData()));
						}
					}
					categories.add(parseDateTime.format(md.getEndTime()));
				}
				// chart
				ChartLineSeriesBo lineSeriesBo = new ChartLineSeriesBo();
				lineSeriesBo.setName(rmd != null ? rmd.getName() + "(" + rmd.getUnit() + ")" : "");
				lineSeriesBo.setData(lineSeriesData);
				List<ChartLineSeriesBo> lineSeries = new ArrayList<ChartLineSeriesBo>();
				lineSeries.add(lineSeriesBo);
				ChartLineBo chartLineBo = new ChartLineBo();
				chartLineBo.setCategories(categories);
				chartLineBo.setSeries(lineSeries);
				chartBoList.add(chartLineBo);
				// table
				List<String> head = new ArrayList<String>();
				head.add("指标名称");head.add("当前值");head.add("最小值");head.add("最大值");head.add("平均值");
				List<List<ChartTableTdBo>> ctDataList = new ArrayList<List<ChartTableTdBo>>();
				ctDataList.add(ctData);
				ChartTableBo chartTableBo = new ChartTableBo();
				chartTableBo.setHead(head);
				chartTableBo.setData(ctDataList);
				chartBoList.add(chartTableBo);
			}
		}
		return statQDataBO;
	}
	
	@Override
	public File fillStatQChart(StatistQueryMainBo statQMainBo, String startTime, String endTime, String type) throws Exception{
		StatQChartBo statQDataBO = new StatQChartBo();
		if("1".equals(statQMainBo.getType())){
			statQDataBO = getStatQPerformanceData(statQMainBo, startTime, endTime);
		}else if("2".equals(statQMainBo.getType())){
			statQDataBO = getStatQPropertyCountData(statQMainBo);
		}
		FileModel fileModel = fileClient.getFileModelByID(statQMainBo.getiReportId());
		ReportModelFillReport rmf = new ReportModelFillReport(fileModel);
		Date d= new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startStr=	sdf.format(d);
	//	rmf.fillTitleReport(statQDataBO.getName() + "  (" + startTime + " -- " + endTime+")");
		if("1".equals(statQMainBo.getType())){//性能
			rmf.fillTitleReport(statQDataBO.getName() + "  (" + startTime + " -- " + endTime+")");
		}else{
			rmf.fillTitleReport(statQDataBO.getName()+"   ("+startStr+")");
		}
		
		List<ChartBaseBo> baseChartList = statQDataBO.getChartBoList();
		for (int i = 0; baseChartList != null && i < baseChartList.size(); i++) {
			ChartBaseBo chartBaseBo = baseChartList.get(i);
			// 线性图
			if(chartBaseBo instanceof ChartLineBo){
				ChartLineBo lineBo = (ChartLineBo)chartBaseBo;
				Chart chart = new Chart();
				chart.setName(lineBo.getTitle());
				
				chart.setInfo(StringUtils.join(lineBo.getCategories().toArray(), INFO_REGEX_STR));
				
				List<ChartData> chartDataList = new ArrayList<ChartData>();
				for (int j = 0; lineBo.getSeries() != null && j < lineBo.getSeries().size(); j++) {
					ChartLineSeriesBo seriesBo = lineBo.getSeries().get(j);
					ChartData chartData = new ChartData();
					chartData.setName(seriesBo.getName());
					chartData.setValue(StringUtils.join(seriesBo.getData().toArray(), DATA_REGEX_STR));
					chartDataList.add(chartData);
				}
				chart.setChartData(chartDataList);
				
				rmf.fillLineReport(chart);
				// 饼图
			}else if(chartBaseBo instanceof ChartPieBo){
				ChartPieBo pieBo = (ChartPieBo)chartBaseBo;
				Chart chart = new Chart();
				chart.setName(pieBo.getTitle());
				
				List<ChartData> chartDataList = new ArrayList<ChartData>();
				for (int j = 0; j < pieBo.getSeries().size(); j++) {
					ChartPieSeriesBo seriesBo = pieBo.getSeries().get(j);
					ChartData chartData = new ChartData();
					chartData.setName(seriesBo.getName());
					chartData.setValue(String.valueOf(seriesBo.getY()));
					chartDataList.add(chartData);
				}
				chart.setChartData(chartDataList);
				
				rmf.fillPieReport4StatQuery(chart);
				// 表格
			}else if(chartBaseBo instanceof ChartTableBo){
				ChartTableBo tableBo = (ChartTableBo)chartBaseBo;
				
				ColumnsTitle columnsTitle = new ColumnsTitle();
				List<Columns> columnList = new ArrayList<Columns>();
				for (int j = 0; j < tableBo.getHead().size(); j++) {
					Columns columns = new Columns(tableBo.getHead().get(j));
					columnList.add(columns);
				}
				columnsTitle.setColumns(columnList);
				
				ColumnsData columnsData = new ColumnsData();
				List<TableData> tableDataList = new ArrayList<TableData>();
				for (int j = 0; j < tableBo.getData().size(); j++) {
					List<ChartTableTdBo> tdBoList = tableBo.getData().get(j);
					List<String> trList = new ArrayList<String>();
					for (int k = 0; k < tdBoList.size(); k++) {
						trList.add(tdBoList.get(k).getText());
					}
					TableData tableData = new TableData();
					tableData.setValue(StringUtils.join(trList.toArray(), DATA_REGEX_STR));
					tableDataList.add(tableData);
				}
				columnsData.setTableData(tableDataList);
				
				rmf.fillTableReport("", columnsTitle, columnsData);
				// 标题
			}else if(chartBaseBo instanceof ChartTitileBo){
				ChartTitileBo titleBo = (ChartTitileBo)chartBaseBo;
				rmf.fillTitleReport(titleBo.getTitle());
			}
		}
		JasperPrint jp = rmf.fillReportEnd();
		ExportReportTemplate ert = new ExportReportTemplate();
		
		IreportFileTypeEnum ift = IreportFileTypeEnum.EXCEL;
		switch (type) {
		case "pdf":
			ift = IreportFileTypeEnum.PDF;
			break;
		case "word":
			ift = IreportFileTypeEnum.WORD;
			break;
		case "excel":
			ift = IreportFileTypeEnum.EXCEL;
			break;
		}
		
		return ert.exportFileByType(ift, jp);
	}

	private StatQChartBo getStatQPropertyCountData(StatistQueryMainBo statQMainBo) throws Exception{
		StatQChartBo statQDataBO = new StatQChartBo();
		statQDataBO.setName(statQMainBo.getName());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		statQDataBO.setStartTime(sdf.format(new Date()));
		
		List<ChartBaseBo> chartBoList = new ArrayList<ChartBaseBo>();
		statQDataBO.setChartBoList(chartBoList);
		
		List<StatistQueryInstanceBo> statQInstList = statQMainBo.getStatQInstBoList();
		long[] instanceIdArray = new long[statQInstList == null ? 0 : statQInstList.size()];
		List<Long> instanceIdList = new ArrayList<Long>();
		for (int i = 0; statQInstList != null && i < statQInstList.size(); i++) {
			instanceIdArray[i] = statQInstList.get(i).getInstanceId();
			instanceIdList.add(statQInstList.get(i).getInstanceId());
		}
		
		List<StatistQueryMetricBo> statQMetricList = statQMainBo.getStatQMetricBoList();
		String[] metricIdArray = new String[statQMetricList == null ? 0 : statQMetricList.size()];
		for (int i = 0; statQMetricList != null && i < statQMetricList.size(); i++) {
			metricIdArray[i] = statQMetricList.get(i).getMetricId();
		}
		if(instanceIdArray.length == 0 || metricIdArray.length == 0){
			return statQDataBO;
		}
		

		// 批量查询指标状态
		Map<Long, Map<String, MetricStateEnum>> iMStateMap = new HashMap<Long, Map<String, MetricStateEnum>>();
		List<MetricStateData> msdList = metricStateService.findMetricState(instanceIdList, Arrays.asList(metricIdArray));
		for(int i = 0; msdList != null && i < msdList.size(); i++){
			MetricStateData msd = msdList.get(i);
			Long instanceId = msd.getInstanceID();
			Map<String, MetricStateEnum> mStateMap = null;
			if(iMStateMap.containsKey(instanceId)){
				mStateMap = iMStateMap.get(instanceId);
			}else{
				mStateMap = new HashMap<String, MetricStateEnum>();
				iMStateMap.put(instanceId, mStateMap);
			}
			mStateMap.put(msd.getMetricID(), msd.getState());
		}
		// 资源指标数据
		Map<Long, Map<String, String>> instMapMetricMap = new HashMap<Long, Map<String,String>>();
		
		MetricRealtimeDataQuery mrdQuery = new MetricRealtimeDataQuery();
		mrdQuery.setInstanceID(instanceIdArray);
		mrdQuery.setMetricID(metricIdArray);
		List<Map<String, ?>> metricDataList = metricDataService.queryRealTimeMetricData(mrdQuery);
		for (int i = 0; metricDataList != null && i < metricDataList.size(); i++) {
			Map<String, ?> metricData = metricDataList.get(i);
			Map<String, String> metricMap = null;
			if(!instMapMetricMap.containsKey(metricData.get("instanceid"))){
				metricMap = new HashMap<String, String>();
				instMapMetricMap.put(Long.valueOf(metricData.get("instanceid").toString()), metricMap);
			}else{
				metricMap = instMapMetricMap.get(Long.valueOf(metricData.get("instanceid").toString()));
			}
			for(String metricId : metricIdArray){
				if(metricData.containsKey(metricId) && metricData.get(metricId) != null){
					metricMap.put(metricId, metricData.get(metricId).toString());
				}else{
					metricMap.put(metricId, "null");
				}
			}
		}
		// domain data
		Map<Long, String> domainMap = new HashMap<Long, String>();
		List<Domain> domainList = stm_system_DomainApi.getAllDomains();
		for (int i = 0; domainList != null && i < domainList.size(); i++) {
			Domain domain = domainList.get(i);
			domainMap.put(domain.getId(), domain.getName());
		}
		
		// 数据组装
		List<String> tableHead = new ArrayList<String>();
		tableHead.add("设备名称");tableHead.add("IP地址");tableHead.add("类型");tableHead.add("域名称");
		
		List<ResourceInstance> riList = resourceInstanceService.getResourceInstances(instanceIdList);
		Map<Long, List<ChartTableTdBo>> tableDataMap = new HashMap<Long, List<ChartTableTdBo>>();
		for(String metricId : metricIdArray){
			String metricName = null;
			ChartPieSeriesBo pieSeriesBo = new ChartPieSeriesBo();
			ChartPieSeriesBo otherPieSeriesBo = new ChartPieSeriesBo();
			switch (metricId) {
			case "cpuRate":
				metricName = "CPU平均利用率";
				pieSeriesBo.setName("已使用CPU资源");
				otherPieSeriesBo.setName("剩余CPU资源");
				break;
			case "memRate":
				metricName = "内存利用率";
				pieSeriesBo.setName("已使用内存资源");
				otherPieSeriesBo.setName("剩余内存资源");
				break;
			case "DiskUsagePercentage":
				metricName = "磁盘使用百分比";
				pieSeriesBo.setName("已使用磁盘资源");
				otherPieSeriesBo.setName("剩余磁盘资源");
				break;
			}
			// table head
			tableHead.add(metricName);
			// chart data
			double pieSeriesY = 0D, otherPieSeriesY = 0D;
			for (int i = 0; riList != null && i < riList.size(); i++) {
				ResourceInstance ri = riList.get(i);
				ResourceDef resourceDef = capacityService.getResourceDefById(ri.getResourceId());
				List<ChartTableTdBo> tableData = null;
				if(tableDataMap.containsKey(ri.getId())){
					tableData = tableDataMap.get(ri.getId());
				} else {
					tableData = new ArrayList<ChartTableTdBo>();
					tableData.add(new ChartTableTdBo(ri.getShowName()));
					tableData.add(new ChartTableTdBo(ri.getShowIP()));
					tableData.add(new ChartTableTdBo(resourceDef != null ? resourceDef.getName() : ""));
					tableData.add(new ChartTableTdBo(domainMap.get(ri.getDomainId())));
					tableDataMap.put(ri.getId(), tableData);
				}
				// td data
				ChartTableTdBo tdDataValue = new ChartTableTdBo();
				tdDataValue.setType("progress");
				
				if(iMStateMap.containsKey(ri.getId()) && iMStateMap.get(ri.getId()).containsKey(metricId)){
					tdDataValue.setMetricStatColor(getMetricStateColor(iMStateMap.get(ri.getId()).get(metricId)));
				}
				
				Map<String, String> metricMap = instMapMetricMap.get(ri.getId());
				if(metricMap != null && metricMap.containsKey(metricId)){
					String value = metricMap.get(metricId);
					if(!"null".equals(value) && !"".equals(value)){
						pieSeriesY += Double.valueOf(value);
						otherPieSeriesY += 100 - Double.valueOf(value);
						tdDataValue.setText(value + "%");
					}else{
						tdDataValue.setText("null");
					}
				} else {
					tdDataValue.setText("null");
				}
				// 如果没有磁盘利用率的指标则查询分区子资源
				if("DiskUsagePercentage".equals(metricId) && capacityService.getResourceMetricDef(ri.getResourceId(), "DiskUsagePercentage") == null){
					String childType = "Partition";
					String childMetricId = "fileSysRate";
					List<Long> childInstIdList = new ArrayList<Long>();
					List<ResourceInstance> childRi = ri.getChildren();
					for (int j = 0; childRi != null && j < childRi.size(); j++) {
						if(childType.equals(childRi.get(j).getChildType())){
							childInstIdList.add(childRi.get(j).getId());
						}
					}
					if(!childInstIdList.isEmpty() && !"VMWareVM".equals(ri.getResourceId())){
						List<Double> childFileRateList = new ArrayList<Double>();
						long[] childInstIdArray = new long[childInstIdList.size()];
						for (int j = 0; j < childInstIdArray.length; j++) {
							childInstIdArray[j] = childInstIdList.get(j);
						}
						MetricRealtimeDataQuery mrd = new MetricRealtimeDataQuery();
						mrd.setInstanceID(childInstIdArray);
						mrd.setMetricID(new String[]{childMetricId});
						List<Map<String, ?>> childMetricDataList = metricDataService.queryRealTimeMetricData(mrd);
						for (int j = 0; childMetricDataList != null && j < childMetricDataList.size(); j++) {
							Map<String, ?> childMetricData = childMetricDataList.get(j);
							if(childMetricData.containsKey(childMetricId) && childMetricData.get(childMetricId) != null){
								childFileRateList.add(Double.valueOf(childMetricData.get(childMetricId).toString()));
							}
						}
						if(!childFileRateList.isEmpty()){
							double amountChildFRate = 0D;
							for (int j = 0; j < childFileRateList.size(); j++) {
								amountChildFRate += childFileRateList.get(j);
							}
							double childRatePer = new BigDecimal(amountChildFRate/childFileRateList.size()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							pieSeriesY += childRatePer;
							otherPieSeriesY += 100 - childRatePer;
							tdDataValue.setText(childRatePer + "%");
							tdDataValue.setMetricStatColor(getMetricStateColor(MetricStateEnum.NORMAL));
						}
					}else if("VMWareVM".equals(ri.getResourceId())){
						Double usedSpace = null, diskCapacity = null;
						List<MetricData> mdList = metricDataService.getMetricInfoDatas(ri.getId(), new String[]{"DiskUsedSpace", "GuestDiskCapacity"});
						for (int j = 0; mdList != null && j < mdList.size(); j++) {
							MetricData md = mdList.get(j);
							switch (md.getMetricId()) {
							case "DiskUsedSpace":
								if(md.getData() != null && md.getData().length > 0 && md.getData()[0] != null && !"".equals(md.getData()[0])){
									usedSpace = Double.valueOf(md.getData()[0]);
								}
							break;
							case "GuestDiskCapacity":
								if(md.getData() != null && md.getData().length > 0 && md.getData()[0] != null && !"".equals(md.getData()[0])){
									diskCapacity = Double.valueOf(md.getData()[0]);
								}
							break;
							}
						}
						if(usedSpace != null && diskCapacity != null){
							double percent = new BigDecimal(usedSpace/diskCapacity).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							pieSeriesY += percent;
							otherPieSeriesY += 100 - percent;
							tdDataValue.setText(percent + "%");
							tdDataValue.setMetricStatColor(getMetricStateColor(MetricStateEnum.NORMAL));
						}
					}
				}
				tableData.add(tdDataValue);
			}
			pieSeriesBo.setY(pieSeriesY == 0 && otherPieSeriesY == 0 ? null : pieSeriesY);
			otherPieSeriesBo.setY(otherPieSeriesY == 0 && pieSeriesY == 0 ? null : otherPieSeriesY);
			// chart data
			List<ChartPieSeriesBo> pieSeriesBoList = new ArrayList<ChartPieSeriesBo>();
			pieSeriesBoList.add(pieSeriesBo);
			pieSeriesBoList.add(otherPieSeriesBo);
			// chart object
			ChartPieBo chartPieBo = new ChartPieBo();
			chartPieBo.setTitle(metricName);
			chartPieBo.setSeries(pieSeriesBoList);
			chartBoList.add(chartPieBo);
		}
		// table data
		List<List<ChartTableTdBo>> tableDataList = new ArrayList<List<ChartTableTdBo>>();
		for(Long instanceId : tableDataMap.keySet()){
			tableDataList.add(tableDataMap.get(instanceId));
		}
		// table object
		ChartTableBo chartTableBo = new ChartTableBo();
		chartTableBo.setHead(tableHead);
		chartTableBo.setData(tableDataList);
		chartBoList.add(chartTableBo);
		return statQDataBO;
	}
	private static String getMetricStateColor(MetricStateEnum stateEnum) {
		String ise = "gray";
		if(stateEnum != null){
			switch (stateEnum) {
			case CRITICAL:
				ise = "red";
				break;
			case SERIOUS:
				ise = "orange";
				break;
			case WARN:
				ise = "yellow";
				break;
			case NORMAL:
			case NORMAL_NOTHING:
				ise = "green";
				break;
			default:
				ise = "gray";
				break;
			}
		}
		return ise;
	}
}
