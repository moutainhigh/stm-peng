package com.mainsteam.stm.portal.report.convert;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.common.metric.report.InstanceMetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricDataReportService;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportQuery;
import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.portal.report.bo.Chapter;
import com.mainsteam.stm.portal.report.bo.Chart;
import com.mainsteam.stm.portal.report.bo.ChartData;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsData;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.ReportCycleEnum;
import com.mainsteam.stm.portal.report.bo.ReportDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryInstance;
import com.mainsteam.stm.portal.report.bo.Table;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.portal.report.engine.DataPoint;
import com.mainsteam.stm.portal.report.engine.TrendAnalysisUtil;
import com.mainsteam.stm.util.SpringBeanUtil;

/**
 * 趋势分析
 * 一个目录里面只会有一个table和多个折线图
 *
 */
public class TrendAnalysisDataConvert extends BaseDataConvert {
	private MetricSummeryReportQuery msrq;
	private Chapter chapter;
	private String UP_STRING = "↑";
	private String DOWN_STRING = "↓";
	private String FLAT_STRING = "-";
	private String NULL_STRING = "null";

	public TrendAnalysisDataConvert(ReportTemplate reportTemplate, ReportTemplateDirectory reportTemplateDirectory) {
		this.reportTemplate = reportTemplate;
		this.reportTemplateDirectory = reportTemplateDirectory;
		metricDataReportService = (MetricDataReportService) SpringBeanUtil.getObject("MetricDataReportService");
		init();

		msrq = new MetricSummeryReportQuery();
		msrq.setInstanceIDes(instanceIDes);
		msrq.setMetricIDes(metricIDes);
		this.chapter = new Chapter();
	}

	@Override
	public ReportDirectory getReportDirectory() {
		ReportDirectory rd = new ReportDirectory();
		rd.setName(reportTemplateDirectory.getReportTemplateDirectoryName());
		convertData();
		List<Chapter> chapterList = new ArrayList<Chapter>();
		chapterList.add(this.chapter);
		rd.setChapter(chapterList);
		return rd;
	}

	public void convertData() {
		List<Table> tableList = new ArrayList<Table>();
		List<Chart> chartList = new ArrayList<Chart>();
		
		List<TableData> tableDataList = new ArrayList<TableData>();
		/******************************数据查询start********************************************/
		// before Last Week / month -- 计算上周趋势
		timePeriods=new ArrayList<TimePeriod>();
		TimePeriod timePeriod=new TimePeriod();
		if(cycle == 2){
			Calendar beforeLastDate = getBeforeLastWeek();
			timePeriod.setStartTime(beforeLastDate.getTime());
			timePeriod.setEndTime(getWeekLastTime(beforeLastDate));
			msrq.setSummaryType(MetricSummaryType.SH);
		}else{
			Calendar beforeLastDate = getBeforeLastMonth();
			timePeriod.setStartTime(beforeLastDate.getTime());
			timePeriod.setEndTime(getMonthLastTime(beforeLastDate));
			msrq.setSummaryType(MetricSummaryType.D);
		}
		timePeriods.add(timePeriod);
		msrq.setTimePeriods(timePeriods);
		List<InstanceMetricSummeryReportData> beforeLastImsrList = metricDataReportService.findInstanceHistorySummaryData(msrq);
		// 对时间进行排序
		Collections.sort(beforeLastImsrList, new Comparator<InstanceMetricSummeryReportData>() {
			@Override
			public int compare(InstanceMetricSummeryReportData arg0, InstanceMetricSummeryReportData arg1) {
				return arg0.getEndTime().compareTo(arg1.getEndTime());
			}
		});
		// last Week -- 计算本周趋势、列表显示、图表显示
		timePeriods = new ArrayList<TimePeriod>();
		timePeriod=new TimePeriod();
		if(cycle == 2){
			Calendar lastDate = getLastWeek();
			timePeriod.setStartTime(lastDate.getTime());
			timePeriod.setEndTime(getWeekLastTime(lastDate));
			msrq.setSummaryType(MetricSummaryType.SH);
		}else{
			Calendar lastDate = getLastMonth();
			timePeriod.setStartTime(lastDate.getTime());
			timePeriod.setEndTime(getMonthLastTime(lastDate));
			msrq.setSummaryType(MetricSummaryType.D);
		}
		timePeriods.add(timePeriod);
		msrq.setTimePeriods(timePeriods);
		List<InstanceMetricSummeryReportData> lastImsrList = metricDataReportService.findInstanceHistorySummaryData(msrq);
		// 对时间进行排序
		Collections.sort(lastImsrList, new Comparator<InstanceMetricSummeryReportData>() {
			@Override
			public int compare(InstanceMetricSummeryReportData arg0, InstanceMetricSummeryReportData arg1) {
				return arg0.getEndTime().compareTo(arg1.getEndTime());
			}
		});
		/******************************数据查询end********************************************/
		// 数据组装
		for(int i = 0; i < instanceList.size(); i ++){
			ReportTemplateDirectoryInstance rtdi = instanceList.get(i);
			// 表格数据
			TableData tableData = new TableData();
			List<String> tableDataValue = new ArrayList<String>();
			// 图表
			Chart chart = new Chart();
			chart.setType("2");
			chart.setName(rtdi.getInstanceName());
			if(cycle == 2){
				chart.setInfo(getInfo4Week());
			}else{
				chart.setInfo(getInfo4Month());
			}
			List<ChartData> chartDataList = new ArrayList<ChartData>();
			ChartData chartData = new ChartData();
			chartData.setName("采集数据");
			ChartData lastTrendAnalysisChartData = new ChartData();
			ChartData thisTrendAnalysisChartData = new ChartData();
			if(cycle == 2){
				lastTrendAnalysisChartData.setName("上周趋势");
				thisTrendAnalysisChartData.setName("本周趋势");
			}else{
				lastTrendAnalysisChartData.setName("上月趋势");
				thisTrendAnalysisChartData.setName("本月趋势");
			}
			// 图表采集数据
			List<String> chartCollDataList = new ArrayList<String>();
			// 图表计算数据
			List<String> lastCalcDataList = new ArrayList<String>();
			List<String> thisCalcDataList = new ArrayList<String>();
			// 表格资源基本信息
			tableDataValue.add(String.valueOf(i + 1));
			tableDataValue.add(rtdi.getInstanceIP());
			tableDataValue.add(rtdi.getInstanceName());
			tableDataValue.add(rtdi.getInstanceType());
			long instanceId = rtdi.getInstanceId();
			
			/**************************************图表数据start******************************************/
			// 获取上上周开始值、结束值
			TrendAnalysisUtil lastLine = new TrendAnalysisUtil();
			List<DataPoint> dataPointList = new ArrayList<DataPoint>();
			int x = 0;
			for (int j = 0; j < beforeLastImsrList.size(); j++) {
				InstanceMetricSummeryReportData imsr = beforeLastImsrList.get(j);
				if(instanceId != imsr.getInstanceID()){
					continue;
				}
				Float avg = imsr.getMetricData().get(0).getAvg();
				// 上上周采集值
//				Float tmp = (float) (Math.random() * 100);
//				dataPointList.add(new DataPoint(x++, tmp));
//				chartCollDataList.add(String.valueOf(tmp));
				chartCollDataList.add(String.valueOf(avg));
				// 上上周、上周无计算数据
				lastCalcDataList.add(NULL_STRING);
				thisCalcDataList.add(NULL_STRING);
				if(avg != null){
					dataPointList.add(new DataPoint(x++, avg));
				}
			}
			// 计算的上周趋势值
			Float lastBeginDataPoint = null, lastEndDataPoint = null;
			if(!dataPointList.isEmpty() || dataPointList.size() >= 2){
				DataPoint[] dataPoints = null;
				if(cycle == 2){
					dataPoints = lastLine.getBeginEndPoint(dataPointList, ReportCycleEnum.WEEK);
				}else{
					dataPoints = lastLine.getBeginEndPoint(dataPointList, ReportCycleEnum.MONTH);
				}
				lastBeginDataPoint = dataPoints[0].y;
				lastEndDataPoint = dataPoints[dataPoints.length - 1].y;
				Float beforeLastDValue = lastEndDataPoint - lastBeginDataPoint;
				// 计算的上周开始值
				lastCalcDataList.add(String.valueOf(lastBeginDataPoint));
				thisCalcDataList.add(NULL_STRING);
				// 计算的上周中间值
				int lastPointCnt = getLastPointCnt();
				for(int j = 1; j < lastPointCnt - 1; j ++){
					Float currentValue = (beforeLastDValue / (lastPointCnt - 1) * j) + lastBeginDataPoint;
					lastCalcDataList.add(String.valueOf(currentValue));
					thisCalcDataList.add(NULL_STRING);
				}
				// 计算的结束值
				lastCalcDataList.add(String.valueOf(lastEndDataPoint));
				thisCalcDataList.add(NULL_STRING);
			}else{
				int lastPointCnt = getLastPointCnt();
				for(int j = 0; j < lastPointCnt; j ++){
					lastCalcDataList.add(NULL_STRING);
					thisCalcDataList.add(NULL_STRING);
				}
			}
			/*************************************上上周、上周可爱的分割线**************************************/
			// 获取上周开始值、结束值
			dataPointList.clear();
			// 初始化图表上周数据
			for(int j = 0; j < lastImsrList.size(); j ++){
				InstanceMetricSummeryReportData imsr = lastImsrList.get(j);
				if(instanceId != imsr.getInstanceID()){
					continue;
				}
				Float avg = imsr.getMetricData().get(0).getAvg();
				// 采集值
//				Float tmp = (float) (Math.random() * 100);
//				dataPointList.add(new DataPoint(x++, tmp));
//				chartCollDataList.add(String.valueOf(tmp));
				chartCollDataList.add(String.valueOf(avg));
				if(avg != null){
					dataPointList.add(new DataPoint(x++, avg));
				}
			}
			// 计算的本周趋势值
			Float thisBeginDataPoint = null, thisEndDataPoint = null;
			if(!dataPointList.isEmpty() || dataPointList.size() >= 2){
				DataPoint[] dataPoints = null;
				if(cycle == 2){
					dataPoints = lastLine.getBeginEndPoint(dataPointList, ReportCycleEnum.WEEK);
				}else{
					dataPoints = lastLine.getBeginEndPoint(dataPointList, ReportCycleEnum.MONTH);
				}
				thisBeginDataPoint = dataPoints[0].y;
				thisEndDataPoint = dataPoints[dataPoints.length - 1].y;
				Float lastDValue = thisEndDataPoint - thisBeginDataPoint;
				// 计算的开始值、无采集开始值
				lastCalcDataList.add(NULL_STRING);
				thisCalcDataList.add(String.valueOf(thisBeginDataPoint));
				chartCollDataList.add(NULL_STRING);
				// 计算的中间值、无采集中间值
				int thisPointCnt = getThisPointCnt();
				for(int j = 1; j < thisPointCnt - 1; j ++){
					Float currentValue = (lastDValue / (thisPointCnt - 1) * j) + thisBeginDataPoint;
					lastCalcDataList.add(NULL_STRING);
					thisCalcDataList.add(String.valueOf(currentValue));
					chartCollDataList.add(NULL_STRING);
				}
				// 计算的结束值、无采集结束值
				lastCalcDataList.add(NULL_STRING);
				thisCalcDataList.add(String.valueOf(thisEndDataPoint));
				chartCollDataList.add(NULL_STRING);
			}else{
				int thisPointCnt = getThisPointCnt();
				for(int j = 0; j < thisPointCnt; j ++){
					lastCalcDataList.add(NULL_STRING);
					thisCalcDataList.add(NULL_STRING);
					chartCollDataList.add(NULL_STRING);
				}
			}
			/**************************************表格数据start******************************************/
			DecimalFormat decimalFormat = new DecimalFormat("##.##");
			// 上周表格数据
			if(lastBeginDataPoint != null && lastEndDataPoint != null){
				tableDataValue.add(String.valueOf(lastBeginDataPoint));
				tableDataValue.add(String.valueOf(lastEndDataPoint));
				if(lastBeginDataPoint == 0){
					tableDataValue.add(NULL_STRING);
				}else{
					Float trendanalysisValue = (lastEndDataPoint - lastBeginDataPoint) / lastBeginDataPoint * 100;
					tableDataValue.add(decimalFormat.format(trendanalysisValue) + "%");
				}
				if(lastEndDataPoint > lastBeginDataPoint){
					tableDataValue.add(UP_STRING);
				}else if(lastEndDataPoint.equals(lastBeginDataPoint)){
					tableDataValue.add(FLAT_STRING);
				}else {
					tableDataValue.add(DOWN_STRING);
				}
			}else{
				tableDataValue.add(NULL_STRING);
				tableDataValue.add(NULL_STRING);
				tableDataValue.add(NULL_STRING);
				tableDataValue.add(NULL_STRING);
			}
			// 本周表格数据
			if(thisBeginDataPoint != null && thisEndDataPoint != null){
				Float trendanalysisValue = (thisEndDataPoint - thisBeginDataPoint) / thisBeginDataPoint * 100;
				tableDataValue.add(decimalFormat.format(trendanalysisValue) + "%");
				if(thisEndDataPoint > thisBeginDataPoint){
					tableDataValue.add(UP_STRING);
				}else if(thisEndDataPoint.equals(thisBeginDataPoint)){
					tableDataValue.add(FLAT_STRING);
				}else{
					tableDataValue.add(DOWN_STRING);
				}
			}else{
				tableDataValue.add(NULL_STRING);
				tableDataValue.add(NULL_STRING);
			}
			/**************************************表格数据end******************************************/
			// 表格数据
			String value_table = emptyFirstLastChar(tableDataValue.toArray(new String[tableDataValue.size()]));
			tableData.setValue(value_table);
			tableDataList.add(tableData);
			// 图表数据
			String value_collData = emptyFirstLastChar(chartCollDataList.toArray(new String[chartCollDataList.size()]));
			chartData.setValue(value_collData);
			String value_lastCalcData = emptyFirstLastChar(lastCalcDataList.toArray(new String[lastCalcDataList.size()]));
			lastTrendAnalysisChartData.setValue(value_lastCalcData);
			String value_thisCalcData = emptyFirstLastChar(thisCalcDataList.toArray(new String[thisCalcDataList.size()]));
			thisTrendAnalysisChartData.setValue(value_thisCalcData);
			chartDataList.add(chartData);
			chartDataList.add(lastTrendAnalysisChartData);
			chartDataList.add(thisTrendAnalysisChartData);
			chart.setChartData(chartDataList);
			chartList.add(chart);
		}
		Table table = new Table();
		table.setColumnsTitle(getColumnsTitle());
		ColumnsData columnsData = new ColumnsData();
		columnsData.setTableData(tableDataList);
		table.setColumnsData(columnsData);
		tableList.add(table);
		
		if(this.reportTemplateDirectory != null && this.reportTemplateDirectory.getDirectoryMetricList() != null && 
				this.reportTemplateDirectory.getDirectoryMetricList().size() > 0){
			this.chapter.setName(this.reportTemplateDirectory.getDirectoryMetricList().get(0).getMetricName());
		}
		
		this.chapter.setTable(tableList);
		this.chapter.setChart(chartList);
	}

	/**
	 * 获取表格头
	 * 
	 * @return
	 */
	private ColumnsTitle getColumnsTitle() {
		ColumnsTitle title = new ColumnsTitle();
		List<Columns> columns = new ArrayList<Columns>();
		columns.add(new Columns("序列号"));
		columns.add(new Columns("IP地址"));
		columns.add(new Columns("资源名称"));
		columns.add(new Columns("资源类型"));
		if(cycle == 2){
			columns.add(new Columns("上周", "初始值,结束值,趋势比率,趋势"));
			columns.add(new Columns("本周", "趋势比率,趋势"));
		}else{
			columns.add(new Columns("上月", "初始值,结束值,趋势比率,趋势"));
			columns.add(new Columns("本月", "趋势比率,趋势"));
		}
		title.setColumns(columns);
		return title;
	}

	/**
	 * 获取上上周周一的时间
	 * 
	 * @return
	 */
	private Calendar getBeforeLastWeek() {
		Calendar cal = getCurrentTime();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.WEEK_OF_YEAR, -2);// 两周
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal;
	}

	/**
	 * 获取上周周一的时间
	 * 
	 * @return
	 */
	private Calendar getLastWeek() {
		Calendar cal = getCurrentTime();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.WEEK_OF_YEAR, -1);// 一周
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal;
	}

	/**
	 * 获取上周周一的时间
	 * 
	 * @return
	 */
	private Calendar getThisWeek() {
		Calendar cal = getCurrentTime();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal;
	}
	
	/**
	 * 获取一周时间的最后一秒的时间
	 * 
	 * @param cal
	 * @return
	 */
	private Date getWeekLastTime(Calendar cal) {
		Calendar newCal = Calendar.getInstance();
		newCal.setTime(cal.getTime());
		newCal.add(Calendar.DAY_OF_YEAR, 7);
		newCal.add(Calendar.SECOND, -1);
		return newCal.getTime();
	}

	/**
	 * 获取上上月一号的时间
	 * 
	 * @return
	 */
	private Calendar getBeforeLastMonth() {
		Calendar cal = getCurrentTime();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MONTH, -2);// 两月
		return cal;
	}

	/**
	 * 获取上月一号的时间
	 * 
	 * @return
	 */
	private Calendar getLastMonth() {
		Calendar cal = getCurrentTime();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MONTH, -1);// 一月
		return cal;
	}

	/**
	 * 获取上月一号的时间
	 * 
	 * @return
	 */
	private Calendar getThisMonth() {
		Calendar cal = getCurrentTime();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal;
	}
	/**
	 * 获取一月时间的最后一秒的时间
	 * 
	 * @param cal
	 * @return
	 */
	private Date getMonthLastTime(Calendar cal) {
		Calendar newCal = Calendar.getInstance();
		newCal.setTime(cal.getTime());
		newCal.add(Calendar.MONTH, 1);
		newCal.add(Calendar.SECOND, -1);
		return newCal.getTime();
	}
	// 获取月报表的info信息
	private String getInfo4Month(){
		StringBuffer info = new StringBuffer();
		Calendar start = getBeforeLastMonth();
		Calendar end = Calendar.getInstance();
		end.setTime(getMonthLastTime(getThisMonth()));
		for (int counter = 0; start.compareTo(end) <= 0; start.add(Calendar.DAY_OF_MONTH, 1)) {
			String date = new SimpleDateFormat("yyyy-MM-dd").format(start.getTime());
			if(start.get(Calendar.DAY_OF_MONTH) == 1 && counter != 0){
				info.append(" ");
			}
			if(counter % 4 == 0){
				info.append(date);
			}
			info.append(",");
			counter++;
		}
		return info.substring(0, info.length() - 1);
	}
	// 获取周报表的info信息
	private String getInfo4Week(){
		StringBuffer info = new StringBuffer();
		Calendar start = getBeforeLastWeek();
		Calendar end = Calendar.getInstance();
		end.setTime(getWeekLastTime(getThisWeek()));
		for (int counter = 0; start.compareTo(end) <= 0; start.add(Calendar.HOUR_OF_DAY, 6)) {
			String date = null;
			if(start.get(Calendar.HOUR_OF_DAY) == 0){
				date = new SimpleDateFormat("yyyy-MM-dd").format(start.getTime());
			}else{
				date = new SimpleDateFormat("yyyy-MM-dd HH").format(start.getTime());
			}
			if(start.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
					&& start.get(Calendar.HOUR_OF_DAY) == 0 && counter != 0){
				info.append(" ");
			}
			if(start.get(Calendar.HOUR_OF_DAY) == 0){
				info.append(date);
			}
			info.append(",");
			counter ++;
		}
		return info.substring(0, info.length() - 1);
	}
	// 获取上周或上月的数据点数
	private int getLastPointCnt(){
		int pointCnt = 0;
		Calendar start = getLastWeek();
		Calendar end = Calendar.getInstance();
		end.setTime(getWeekLastTime(start));
		if(cycle == 3){
			start = getLastMonth();
			end.setTime(getMonthLastTime(start));
		}
		while(start.compareTo(end) <= 0){
			pointCnt++;
			if(cycle == 3){
				start.add(Calendar.DAY_OF_MONTH, 1);
			}else{
				start.add(Calendar.HOUR_OF_DAY, 6);
			}
		}
		return pointCnt;
	}
	// 获取本周或本月的数据点数
	private int getThisPointCnt(){
		int pointCnt = 0;
		Calendar start = getThisWeek();
		Calendar end = Calendar.getInstance();
		end.setTime(getWeekLastTime(start));
		if(cycle == 3){
			start = getThisMonth();
			end.setTime(getMonthLastTime(start));
		}
		while(start.compareTo(end) <= 0){
			pointCnt++;
			if(cycle == 3){
				start.add(Calendar.DAY_OF_MONTH, 1);
			}else{
				start.add(Calendar.HOUR_OF_DAY, 6);
			}
		}
		return pointCnt;
	}
	/**
	 * 报表开始时间
	 * @return
	 */
	private Calendar getCurrentTime(){
		Calendar cal = Calendar.getInstance();
		if(reportType == 8){
			cal.setTime(startTime);
		}
		return cal;
	}
	private String emptyFirstLastChar(String[] data){
		if(data == null || data.length == 0){
			return "";
		}else{
			String str = Arrays.toString(data).replace(", ", SEPARATOR);
			return str.substring(str.indexOf("[") + 1, str.lastIndexOf("]"));
		}
	}
}
