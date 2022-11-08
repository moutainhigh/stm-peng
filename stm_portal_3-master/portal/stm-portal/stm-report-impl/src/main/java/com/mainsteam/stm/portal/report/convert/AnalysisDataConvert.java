package com.mainsteam.stm.portal.report.convert;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.common.metric.report.InstanceMetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricDataReportService;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportQuery;
import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.portal.report.bo.Chapter;
import com.mainsteam.stm.portal.report.bo.Chart;
import com.mainsteam.stm.portal.report.bo.ChartData;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsData;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.ReportDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryInstance;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryMetric;
import com.mainsteam.stm.portal.report.bo.Table;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.util.SpringBeanUtil;

public class AnalysisDataConvert extends BaseDataConvert{

	private final Log logger = LogFactory.getLog(AnalysisDataConvert.class);
	
	private List<ChartData> chartDatas=new ArrayList<ChartData>();
	private StringBuilder chartInfoSB=new StringBuilder();
	
	private ReportTemplateDirectoryInstance instance ;
	
	//报表类型为分析报表时0.平均值1.最大值2.最小值,为topn时1.性能2.告警
	private int analysisDataType=0;
	
	private Map<String, Float> currentValueMap=new HashMap<String, Float>();
	
	private MetricSummeryReportQuery sumQuery=null;
	//告警分布，级别分布，状态分布
	public AnalysisDataConvert(ReportTemplate reportTemplate,ReportTemplateDirectory reportTemplateDirectory){
		this.reportTemplate=reportTemplate;
		this.reportTemplateDirectory=reportTemplateDirectory;
		this.metricDataReportService = (MetricDataReportService) SpringBeanUtil.getObject("MetricDataReportService");
		this.analysisDataType=reportTemplateDirectory.getReportTemplateDirectoryMetricValueType();
		
		init();
		
		instance=this.instanceList.get(0);
		
		//时间初始化
		sumQuery=new MetricSummeryReportQuery();
		sumQuery.setInstanceIDes(instanceIDes);
		sumQuery.setMetricIDes(metricIDes);

		sumQuery.setTimePeriods(this.getAnalysisTimePeriods());
	}
	
	@Override
	public ReportDirectory getReportDirectory(){
		ReportDirectory reportDirectory=new ReportDirectory();
		reportDirectory.setName(reportDirectoryName);
		reportDirectory.setName(this.reportDirectoryName);
		reportDirectory.setType(String.valueOf(reportType));
		reportDirectory.setChapter(getChapters());
		return reportDirectory; 

	}
	
	/**
	 * 获取章节
	 * @return
	 */
	public List<Chapter> getChapters(){
		List<Chapter> chapters=new ArrayList<Chapter>();
		//分析报告
		Chapter chapter=new Chapter();
		chapter.setName("分析报告");
		chapter.setSort("1");
		List<Table> tables=new ArrayList<Table>();
		tables.add(getTable());
		chapter.setTable(tables);
		chapter.setChart(getCharts());
		chapters.add(chapter);
		return chapters;
	}
	
	public Table getTable(){
		ColumnsTitle title=this.getColumnsTitle();
		ColumnsData columnsData=this.getColumnsData();
		
		Table sumTable=new Table();
		String tableName="IP地址："+instance.getInstanceIP()+" | 资源名称："+instance.getInstanceName()+" | 资源类型："+instance.getInstanceType();
		sumTable.setName(tableName);
		sumTable.setColumnsTitle(title);
		sumTable.setColumnsData(columnsData);

		return sumTable;
	}
	public ColumnsData getColumnsData(){
		//汇总数据
		List<InstanceMetricSummeryReportData> instanceMetricSummeryReportDatas=new ArrayList<InstanceMetricSummeryReportData>();
		instanceMetricSummeryReportDatas=metricDataReportService.findHistorySummaryData(sumQuery);

		if(instanceMetricSummeryReportDatas==null || instanceMetricSummeryReportDatas.size()<=0){
			logger.warn("Can't not search the Analysis Data !");
			
		}else{
			InstanceMetricSummeryReportData instanceMetricData=instanceMetricSummeryReportDatas.get(0);
			List<MetricSummeryReportData> metricDatas=instanceMetricData.getMetricData();
			for(MetricSummeryReportData metricData:metricDatas){				
				Float currentValue=null;
				//0.平均值1.最大值2.最小值
				if(analysisDataType==0){
					currentValue=metricData.getAvg();
				}else if(analysisDataType==1){
					currentValue=metricData.getMax();
				}else if(analysisDataType==2){
					currentValue=metricData.getMin();
				}
				currentValueMap.put(metricData.getMetricID(), currentValue);
			}
			
		}
		
		List<TableData> tableDatas=new ArrayList<TableData>();
		
		StringBuilder chartValue1=new StringBuilder();
		StringBuilder chartValue2=new StringBuilder();
		StringBuilder chartValue3=new StringBuilder();
		
		for(int i=0;i<metricList.size();i+=2){
			ReportTemplateDirectoryMetric left=metricList.get(i);
			ReportTemplateDirectoryMetric right=null;
			
			StringBuilder value=new StringBuilder(50);
			/**
			 * 左表格----------------------------
			 * */
			getTableDataValue(chartValue1,chartValue2,chartValue3,value,left);

			if(i+1<metricList.size()){
				
				right=metricList.get(i+1);
				/**
				 * 右边表格----------------------------
				 * */
				getTableDataValue(chartValue1,chartValue2,chartValue3,value,right);
			}

			TableData tableData=new TableData();
			tableData.setValue(this.checkSeparator(value));
			tableDatas.add(tableData);
		}
		
		//当前
		ChartData chartData1=new ChartData();
		chartData1.setName("当前值");
		chartData1.setValue(checkSeparator(chartValue1));
		//期望
		ChartData chartData2=new ChartData();
		chartData2.setName("期望值");
		chartData2.setValue(checkSeparator(chartValue2));
		//差值
		ChartData chartData3=new ChartData();
		chartData3.setName("差值");
		chartData3.setValue(checkSeparator(chartValue3));

		chartDatas.add(chartData1);
		chartDatas.add(chartData2);
		chartDatas.add(chartData3);
		
		ColumnsData columnsData=new ColumnsData();
		columnsData.setTableData(tableDatas);
		return columnsData;
	}
	
	public void getTableDataValue(StringBuilder chartValue1,StringBuilder chartValue2,StringBuilder chartValue3,StringBuilder value,ReportTemplateDirectoryMetric metric){
		//指标ID
		String metricId = metric.getMetricId();
		//指标名称
		String metricName=metric.getMetricName();
		chartInfoSB.append(metricName);
		
		if(metric.getMetricUnit() != null && metric.getMetricUnit().equals("bps")){
			metricName = metricName.replace("bps", "MBps");
		}
		chartInfoSB.append(",");
		
		//当前值
		Float currentValue=currentValueMap.get(metricId);
		//期望值
		Float expectValue=new Float(metric.getMetricExpectValue());
		//差值
		Float diffValue = null;
		//阈值方向
		int thresholdDirection = metric.getMetricThresholdDirection();
		if(currentValue!=null && expectValue!=null ){
			if(thresholdDirection == 0){
				//越小越好
				diffValue = currentValue - expectValue;
				
			}else{
				//越大越好
				diffValue = expectValue - currentValue;
			}
		}
		
		String unit = metric.getMetricUnit();
		
		chartValue1.append(currentValue == null ? null : decimalFormat.format(currentValue.floatValue()));
		chartValue1.append(SEPARATOR);
		chartValue2.append(expectValue == null ? null : decimalFormat.format(expectValue.floatValue()));
		chartValue2.append(SEPARATOR);
		chartValue3.append(diffValue == null ? null : decimalFormat.format(diffValue.floatValue()));
		chartValue3.append(SEPARATOR);
		
		if(unit != null && unit.equals("bps")){
			
			DecimalFormat decimalFormatForBps = new DecimalFormat("#,##0.00"); 
			
			value.append(metricName);
			value.append(SEPARATOR);
			value.append(currentValue == null ? null : decimalFormatForBps.format(currentValue / (1000D * 1000D)));
			value.append(SEPARATOR);
			value.append(decimalFormatForBps.format(expectValue.floatValue() / (1000D * 1000D)));
			value.append(SEPARATOR);
			value.append(diffValue == null ? null : (decimalFormatForBps.format(diffValue / (1000D * 1000D))));
			value.append(SEPARATOR);

		}else{
			
			value.append(metricName);
			value.append(SEPARATOR);
			value.append(currentValue == null ? null : decimalFormat.format(currentValue.floatValue()));
			value.append(SEPARATOR);
			value.append(decimalFormat.format(expectValue.floatValue()));
			value.append(SEPARATOR);
			value.append(diffValue == null ? null : decimalFormat.format(diffValue.floatValue()));
			value.append(SEPARATOR);
		}
	}
	
	
	public List<Chart> getCharts(){
		
		Chart chart=new Chart();
		chart.setInfo(chartInfoSB.substring(0,chartInfoSB.length()-1));
		chart.setName("分析报表图表");
		chart.setType("4");

		chart.setChartData(chartDatas);
		
		List<Chart> charts=new ArrayList<Chart>();
		charts.add(chart);
		return charts;
	}

	

	public ColumnsTitle getColumnsTitle(){
		ColumnsTitle columnsTitle=new ColumnsTitle();
		List<Columns> columns=new ArrayList<Columns>();
		/*
		String title="IP地址："+instance.getInstanceIP()+"    资源名称："+instance.getInstanceName()+"    资源类型："+instance.getInstanceType();
		Columns column=new Columns("指标名称,当前值,期望值,差值,指标名称,当前值,期望值,差值");
		*/
		columns.add(new Columns("指标名称"));
		columns.add(new Columns("当前值"));
		columns.add(new Columns("期望值"));
		columns.add(new Columns("差值"));
		columns.add(new Columns("指标名称"));
		columns.add(new Columns("当前值"));
		columns.add(new Columns("期望值"));
		columns.add(new Columns("差值"));
		columnsTitle.setColumns(columns);
		return columnsTitle;
	}
	
	public List<TimePeriod> getAnalysisTimePeriods(){

		timePeriods=new ArrayList<TimePeriod>();

		TimePeriod timePeriod=new TimePeriod();
		Calendar calendar=Calendar.getInstance();
		if(cycle==1){//前一天汇总
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			calendar.add(Calendar.SECOND, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			//前一天开始
			timePeriod.setStartTime(calendar.getTime());
			
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.SECOND, -1);
			//前一天结束
			timePeriod.setEndTime(calendar.getTime());
			
		}else if(cycle==2){//上一周汇总
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			calendar.add(Calendar.WEEK_OF_YEAR, -1);// 一周
			//上一周 开始
			timePeriod.setStartTime(calendar.getTime());
			calendar.add(Calendar.DAY_OF_YEAR, 7);// 一周
			calendar.set(Calendar.SECOND, -1);
			//上一周 结束
			timePeriod.setEndTime(calendar.getTime());
		}else if(cycle==3){//上一个月汇总
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.MONTH, -1);
			//上一个 开始
			timePeriod.setStartTime(calendar.getTime());
			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.SECOND, -1);
			//上一个 结束
			timePeriod.setEndTime(calendar.getTime());
		}
		timePeriods.add(timePeriod);
		return timePeriods;
	}
	
	
}
