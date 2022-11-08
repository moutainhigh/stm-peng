package com.mainsteam.stm.portal.report.convert;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.alarm.event.report.InstanceAlarmEventReportData;
import com.mainsteam.stm.alarm.event.report.InstanceAlarmEventReportQuery;
import com.mainsteam.stm.alarm.event.report.InstanceAlarmEventReportService;
import com.mainsteam.stm.common.metric.report.MetricDataReportService;
import com.mainsteam.stm.common.metric.report.MetricDataTopQuery;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportData;
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

public class TopNDataConvert  extends BaseDataConvert{
	
	private final Log logger = LogFactory.getLog(TopNDataConvert.class);
	
	private boolean isAlarmTop=false;
	
	private int topN=10;
	
	private List<InstanceAlarmEventReportData> alarmDatas=null;
	
	public TopNDataConvert(ReportTemplate reportTemplate,ReportTemplateDirectory reportTemplateDirectory){
		this.reportTemplateDirectory=reportTemplateDirectory;
		this.reportTemplate=reportTemplate;
		this.instanceAlarmEventReportService=(InstanceAlarmEventReportService)SpringBeanUtil.getObject("instanceAlarmEventReportService");
		this.metricDataReportService = (MetricDataReportService) SpringBeanUtil.getObject("MetricDataReportService");
		init();

	}
	
	@Override
	public ReportDirectory getReportDirectory(){		 
		this.topN=reportTemplateDirectory.getReportTemplateDirectoryTopnCount();
		isAlarmTop=reportTemplateDirectory.getReportTemplateDirectoryMetricValueType()==2;

		if(isAlarmTop){
			InstanceAlarmEventReportQuery query=new InstanceAlarmEventReportQuery();
			query.setInstanceIDes(this.instanceIDes);
			query.setTimePeriods(timePeriods);
			
			//告警汇总
			alarmDatas=instanceAlarmEventReportService.findTotleAlarmReport(query);
		} 
		ReportDirectory reportDirectory=new ReportDirectory();
		reportDirectory.setName(this.reportDirectoryName);
		reportDirectory.setType(String.valueOf(reportType));
		reportDirectory.setChapter(getChapters());
		return reportDirectory; 
	}
	
	/**
	 * 性能Top 章节
	 * @return
	 */
	public List<Chapter> getChapters(){
		List<Chapter> chapters=new ArrayList<Chapter>();
		for(ReportTemplateDirectoryMetric metric:metricList){
			Chapter chapter=new Chapter();
			chapter.setName(metric.getMetricName());
			chapter.setSort("2");
			if(isAlarmTop){//告警Top
				initAlarmChapter(chapter,metric);
			}else{//性能Top
				initPerformanceChapter(chapter,metric);
			}
			
			chapters.add(chapter);
		}
		
		return chapters;
	}
	
	/**
	 * 初始化性能 章节
	 * @param chapter
	 * @param rtdMetric
	 */
	public void initPerformanceChapter(Chapter chapter,ReportTemplateDirectoryMetric rtdMetric){
		String metricId = rtdMetric.getMetricId();
		String metricName=rtdMetric.getMetricName();
		String unit = rtdMetric.getMetricUnit();
		final boolean orderByMax=rtdMetric.getMetricSortType()==1;
		
		MetricDataTopQuery query=new MetricDataTopQuery();
		query.setTimePeriods(timePeriods);
		query.setInstanceIDes(instanceIDes);
		query.setLimit(topN);
		query.setMetricID(metricId);
		query.setOrderByMax(orderByMax);
		
		List<MetricSummeryReportData> metricDatas=metricDataReportService.findTopSummaryData(query);
		
		Table table=new Table();
		table.setName(metricName);
		table.setColumnsTitle(this.getTitle(metricName,unit));

		ColumnsData columnsData=new ColumnsData();
		List<TableData> tableDatas=new ArrayList<TableData>();
		List<ChartData> chartDatas=new ArrayList<ChartData>();
		
		if(metricDatas==null || metricDatas.size()<=0){
			tableDatas=getNullTableDatas(1);
			for(ReportTemplateDirectoryInstance instance:instanceList){
				ChartData chartData=new ChartData();
				chartData.setIp(instance.getInstanceIP());
				chartData.setName(instance.getInstanceName());
				chartData.setValue("null");
				chartDatas.add(chartData);
			}
		}else{
			for(ReportTemplateDirectoryInstance instance : instanceList){
				boolean flag = true;
				for(MetricSummeryReportData metricData : metricDatas){
					if(instance.getInstanceId() == metricData.getInstanceID()){
						flag = false;
						break;
					}
				}
				if(flag){
					MetricSummeryReportData metricData = new MetricSummeryReportData();
					metricData.setInstanceID(instance.getInstanceId());
					metricData.setMetricID(metricId);
					metricData.setAvg(null);
					metricDatas.add(metricData);
				}
			}
			Collections.sort(metricDatas, new Comparator<MetricSummeryReportData>(){
				@Override
				public int compare(MetricSummeryReportData dataA, MetricSummeryReportData dataB) {
					int compareResult = 0;
					if(dataA.getAvg() == null && dataB.getAvg() == null){
						compareResult = 0;
					}else if(dataA.getAvg() == null && dataB.getAvg() != null){
						compareResult = -1;
					}else if(dataA.getAvg() != null && dataB.getAvg() == null){
						compareResult = 1;
					}else{
						if(dataA.getAvg().equals(dataB.getAvg())){
							compareResult = 0;
						}else if(dataA.getAvg() > dataB.getAvg()){
							compareResult = 1;
						}else{
							compareResult = -1;
						}
					}
					if(orderByMax){
						if(compareResult < 0){
							compareResult = 1;
						}else if(compareResult > 0){
							compareResult = -1;
						}
					}
					return compareResult;
				}
			});
			for(int i = 0; i < metricDatas.size(); i++){
				MetricSummeryReportData metricData = metricDatas.get(i);
				ReportTemplateDirectoryInstance instance=instanceMap.get(metricData.getInstanceID());
				TableData tableData=new TableData();
				StringBuilder tableValue = new StringBuilder();
				tableValue.append(i + 1).append(SEPARATOR);
				tableValue.append(instance.getInstanceIP()).append(SEPARATOR);
				tableValue.append(instance.getInstanceName()).append(SEPARATOR);
				tableValue.append(instance.getInstanceType()).append(SEPARATOR);
				
				DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); 
				if(unit != null && unit.equals("bps")){
					tableValue.append(metricData.getAvg() == null ? "null" : (decimalFormat.format(metricData.getAvg() / (1000D * 1000D))));
				}else{
					tableValue.append(metricData.getAvg());
				}
				tableData.setValue(tableValue.toString());
				tableDatas.add(tableData);
				
				ChartData chartData=new ChartData();
				chartData.setIp(instance.getInstanceIP());
				chartData.setName(instance.getInstanceName());
				chartData.setValue(""+metricData.getAvg());
				chartDatas.add(chartData);
			}
		}

		//TopN
		if (tableDatas != null && tableDatas.size() > topN) {
			tableDatas = tableDatas.subList(0, topN);
			chartDatas = chartDatas.subList(0, topN);
		}
		
		List<Table> tables=new ArrayList<Table>();
		columnsData.setTableData(tableDatas);
		table.setColumnsData(columnsData);
		tables.add(table);
		
		List<Chart> charts=new ArrayList<Chart>();
		Chart chart=new Chart();
		chart.setName(metricName + "(" + unit + ")");
		chart.setType("1");
		chart.setChartData(chartDatas);
		charts.add(chart);
		
		chapter.setTable(tables);
		chapter.setChart(charts);		
	}
	

	public void initAlarmChapter(Chapter chapter,ReportTemplateDirectoryMetric metric){
		final boolean sort=metric.getMetricSortType()==0;
		final String metricId=metric.getMetricId();
		
		String metricName=metric.getMetricName();
		String unit = metric.getMetricUnit();
		// 补全数据
		for(ReportTemplateDirectoryInstance instance : instanceList){
			boolean flag = true;
			for(InstanceAlarmEventReportData alarmData:alarmDatas){
				if(instance.getInstanceId() == alarmData.getInstanceID()){
					flag = false;
					break;
				}
			}
			if(flag){
				InstanceAlarmEventReportData alarmData = new InstanceAlarmEventReportData();
				alarmData.setInstanceID(instance.getInstanceId());
				alarmData.setCountCritical(0);
				alarmData.setCountNormal(0);
				alarmData.setCountNotRecover(0);
				alarmData.setCountRecover(0);
				alarmData.setCountSerious(0);
				alarmData.setCountTotle(0);
				alarmData.setCountWarn(0);
				alarmDatas.add(alarmData);
			}
		}
		//排序
		Collections.sort(alarmDatas,new Comparator<InstanceAlarmEventReportData>(){
			@Override
			public int compare(InstanceAlarmEventReportData eventA, InstanceAlarmEventReportData eventB) {
				Integer a=eventA.getCountTotle();
				Integer b=eventA.getCountTotle();
				
				if(metricId.equals("alarmAllCount")){
					a=eventA.getCountTotle();
					b=eventB.getCountTotle();
				}else if(metricId.equals("deadlyCount")){
					a=eventA.getCountCritical();
					b=eventB.getCountCritical();
				}else if(metricId.equals("seriousCount")){
					a=eventA.getCountSerious();
					b=eventB.getCountSerious();
				}else if(metricId.equals("warningCount")){
					a=eventA.getCountWarn();
					b=eventB.getCountWarn();
				}
				
				if(sort){
					return a.compareTo(b);
				}else{
					return b.compareTo(a);
				}
				
			}
		});
		
		//TopN
		if(alarmDatas!=null && alarmDatas.size() > topN){
			alarmDatas=alarmDatas.subList(0, topN);
		}

		List<TableData> tableDatas=new ArrayList<TableData>();
		List<ChartData> chartDatas=new ArrayList<ChartData>();
		
		int cnt=1;
		for(InstanceAlarmEventReportData alarmData:alarmDatas){
			long instanceId=alarmData.getInstanceID();
			
			ReportTemplateDirectoryInstance instance=instanceMap.get(instanceId);
			
			String ip=instance.getInstanceIP();
			String name=instance.getInstanceName();
			String type=instance.getInstanceType();
			
			int count=0;
			if(metricId.equals("alarmAllCount")){
				count=alarmData.getCountTotle();

			}else if(metricId.equals("deadlyCount")){
				count=alarmData.getCountCritical();

			}else if(metricId.equals("seriousCount")){
				count=alarmData.getCountSerious();

			}else if(metricId.equals("warningCount")){
				count=alarmData.getCountWarn();

			}
			
			StringBuilder value=new StringBuilder();
			value.append(cnt);
			value.append(SEPARATOR);
			value.append(ip);
			value.append(SEPARATOR);
			value.append(name);
			value.append(SEPARATOR);
			value.append(type);
			value.append(SEPARATOR);
			value.append(count);
			
			TableData tableData=new TableData();
			tableData.setValue(value.toString());
			
			ChartData chartData=new ChartData();
			chartData.setIp(ip);
			chartData.setName(name);
			chartData.setValue(String.valueOf(count));

			tableDatas.add(tableData);
			chartDatas.add(chartData);
			cnt++;
		}
		
		ColumnsData columnsData=new ColumnsData();
		columnsData.setTableData(tableDatas);
		
		List<Table> tables=new ArrayList<Table>();
		Table table=new Table();
		table.setName(metricName);
		table.setColumnsTitle(getTitle(metricName,"个"));
		table.setColumnsData(columnsData);
		tables.add(table);
		
		List<Chart> charts=new ArrayList<Chart>();
		Chart chart=new Chart();
		chart.setName(metricName);
		chart.setType("1");
		chart.setChartData(chartDatas);
		charts.add(chart);
		
		chapter.setTable(tables);
		chapter.setChart(charts);
	}
	
	public ColumnsTitle getTitle(String metricName,String unit){
		ColumnsTitle columnsTitle=new ColumnsTitle();
		List<Columns> columns=new ArrayList<Columns>();
		columns.add(new Columns("序列号"));
		columns.add(new Columns("IP地址"));
		columns.add(new Columns("资源名称"));
		columns.add(new Columns("资源类型"));
		if(unit != null && unit.equals("bps")){
			columns.add(new Columns(metricName + "(MBps)"));
		}else{
			columns.add(new Columns(metricName + "(" + unit + ")"));
		}
		
		columnsTitle.setColumns(columns);
		return columnsTitle;
		
	}
	
	public static void main(String[] args) {
 
		
	}

}
