package com.mainsteam.stm.portal.report.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.report.InstanceMetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportData;
import com.mainsteam.stm.portal.report.bo.Chart;
import com.mainsteam.stm.portal.report.bo.ChartData;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsData;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryInstance;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryMetric;
import com.mainsteam.stm.portal.report.bo.ReportTypeEnum;
import com.mainsteam.stm.portal.report.bo.Table;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.portal.report.engine.databean.ReportInstanceData;
import com.mainsteam.stm.portal.report.engine.databean.ReportMetricData;

public class ReportDataConvert {
	
	private ReportTypeEnum reportType;
	
	private ReportTemplateDirectory templateDir;
	
	//资源数量 和 排序
	private List<ReportTemplateDirectoryInstance> instanceList;
	//指标数量和排序
	private List<ReportTemplateDirectoryMetric> metricList;

	//包含了资源的基本属性，通过Instance ID 获取资源基本属性
	private Map<String,ReportTemplateDirectoryInstance> instanceMap;
	//包含了指标的基本属性，通过 metric ID获取资源基本属性
	private Map<String,ReportTemplateDirectoryMetric> metricMap;
	
	private List<InstanceMetricSummeryReportData> metricDataList;
	
	private Map<String,Map<String,MetricSummeryReportData>> metricDataMap;

	private List<ReportInstanceData> instanceDataList;
	
	private Map<String,ReportInstanceData> instanceDataMap;
	
	//Detall
	private Map<String,List<ReportInstanceData>> instanceDataListMap;
	
	
	private HashMap<String,String> metricDataAvgMap;
	//key instanceID, Char info
	private Map<String,String> instanceTimeMap;
	
	private String chartInfo;
	
	//---------table base info
	private boolean isDetail=false;
	private String tableType;
	private String tableSort;
	private boolean hasTime=false;
	private boolean isHaveChart=true;
	private String chartType;
	
	private static final String SEPARATOR=",";
	
	ReportDataConvert(ReportTemplateDirectory templateDir,List<InstanceMetricSummeryReportData> metricDataList,ReportTypeEnum reportType){
		this.templateDir=templateDir;
		this.metricDataList=metricDataList;
		this.reportType=reportType;
		this.isDetail=templateDir.getReportTemplateDirectoryIsDetail()==0;
		//顺序(1.表格在上2.图表在上)
		if(this.reportType.equals(ReportTypeEnum.PERFORMANCE_REPORT)){
			if(isDetail){
				hasTime=true;
				tableType="2";
				chartType="2";
			}else{
				tableType="1";
				chartType="1";
			}
			tableSort="1";
		}else if(reportType.equals(ReportTypeEnum.ALARM_REPORT)){
			tableSort="1";
		}else if(reportType.equals(ReportTypeEnum.TOPN_REPORT)){
			tableSort="2";
			tableType="3";
		}
		
		
		this.init();
	}
	
	private void init(){
		metricDataAvgMap=new HashMap<String,String>();
		this.instanceList=templateDir.getDirectoryInstanceList();
		this.metricList=templateDir.getDirectoryMetricList();
		//资源map
		this.instanceMap = new HashMap<String,ReportTemplateDirectoryInstance>();
		for(ReportTemplateDirectoryInstance instance:instanceList){
			instanceMap.put(String.valueOf(instance.getInstanceId()), instance);
		}
		//指标map
		this.metricMap=new HashMap<String,ReportTemplateDirectoryMetric>();
		for(ReportTemplateDirectoryMetric metric:metricList){
			metricMap.put(metric.getMetricId(), metric);
		}
		
		//资源的指标数据Map
		metricDataMap=new HashMap<String,Map<String,MetricSummeryReportData>>();
		for(InstanceMetricSummeryReportData reportSumData:metricDataList){
			long instanceId=reportSumData.getInstanceID();
			List<MetricSummeryReportData> instanceMetricDataList=reportSumData.getMetricData();
			Map<String,MetricSummeryReportData> instanceMetricDataMap=new HashMap<String,MetricSummeryReportData>();
			for(MetricSummeryReportData instanceMetricData:instanceMetricDataList){
				instanceMetricDataMap.put(instanceMetricData.getMetricID(), instanceMetricData);
			}
			metricDataMap.put(String.valueOf(instanceId), instanceMetricDataMap);			
		}
		
		this.instanceDataList=new ArrayList<ReportInstanceData>();
		this.instanceDataMap=new HashMap<String,ReportInstanceData>();
		for(ReportTemplateDirectoryInstance instance:instanceList){
			ReportInstanceData instanceDate=new ReportInstanceData();
			
			long instanceId=instance.getInstanceId();
			instanceDate.setInstanceId(instanceId);
			instanceDate.setInstanceName(instance.getInstanceName());
			instanceDate.setInstanceType(instance.getInstanceType());
			instanceDate.setInstanceIP(instance.getInstanceIP());
			
			Map<String,MetricSummeryReportData> instanceMetricDataMap=metricDataMap.get(String.valueOf(instanceId));
			Map<String,ReportMetricData> metricDataMap=new HashMap<String,ReportMetricData>();
			for(ReportTemplateDirectoryMetric metric:metricList){
				ReportMetricData metricData=new ReportMetricData();
				String metricId=metric.getMetricId();
				
				metricData.setMetricId(metricId);
				metricData.setMetricName(metric.getMetricName());
				metricData.setMetricType(metric.getMetricType());
//				metricData.setMetricData(instanceMetricDataMap.get(metricId));
				
				metricDataMap.put(metricId, metricData);
			}
			
			instanceDate.setMetricDataMap(metricDataMap);
			instanceDataList.add(instanceDate);
			if(instanceDate!=null){
				instanceDataMap.put(String.valueOf(instanceId), instanceDate);
			}
		
		}
		
		instanceDataListMap=new HashMap<String,List<ReportInstanceData>>();
		for(ReportTemplateDirectoryInstance instance:instanceList){
			long instanceId=instance.getInstanceId();
			
			List<ReportInstanceData> subList=new ArrayList<ReportInstanceData>();
			StringBuilder infoSb=new StringBuilder();
			for(ReportInstanceData instanceData: instanceDataList){
				if(instanceId==instanceData.getInstanceId()){
					subList.add(instanceData);
					infoSb.append(instanceData.getCreateTime());
					infoSb.append(SEPARATOR);
				}
			}
			String info="";
			if(infoSb!=null && infoSb.lastIndexOf(SEPARATOR)!=-1){
				info=infoSb.substring(0,infoSb.lastIndexOf(SEPARATOR));
			}
			if(chartInfo==null){
				chartInfo=info;
			}
			
			instanceTimeMap.put(String.valueOf(instanceId), info);
			instanceDataListMap.put(String.valueOf(instanceId), subList);
		}

	}
	
	
	public Table getTable(){		
		Table table=new Table();
		table.setName(this.templateDir.getReportTemplateDirectoryResource());
//		table.setType(this.tableType);
//		table.setIsHaveChart(String.valueOf(isHaveChart));
		table.setColumnsTitle(this.getColumnsTitle());
//		table.setSort(tableSort);
		table.setColumnsData(this.getColumnsData());
//		table.setChart(this.getCharts());
		return table;
	}
	
	/**
	 * 获取表格头
	 * @return
	 */
	private ColumnsTitle getColumnsTitle(){
		ColumnsTitle title=new ColumnsTitle();
		List<Columns> columns=new ArrayList<Columns> ();
		columns.add(getSerialNumberColunm());
		for(ReportTemplateDirectoryMetric tableCount:metricList){
			Columns column=new Columns();
			MetricTypeEnum type=tableCount.getMetricType();
			String id=tableCount.getMetricId();
			if(type==null){
				if(id.equals("resourceName")){
					column.setText("资源名称");
				}else if(id.equals("resourceType")){
					column.setText("资源类型");
				}else if(id.equals("ipAddress")){
					column.setText("IP 地址");
				}
			}else{
				column.setText(tableCount.getMetricName());
			}
			if(type !=null && type.equals(MetricTypeEnum.PerformanceMetric)){
				column.setApart("true");
			}
			columns.add(column);
		}
		if(hasTime){
			columns.add(getDateTimeColunm());
		}
		title.setColumns(columns);
		return title;
	}
	
	
	private ColumnsData getColumnsData(){
		ColumnsData columnsData = new ColumnsData();
		List<TableData> tableDatas=new ArrayList<TableData>();
		
		int cnt=1;
		for(ReportInstanceData instanceData:instanceDataList){
			TableData tableData=new TableData();
			StringBuilder tableDataValues=new StringBuilder();
			//序列号
			tableDataValues.append(String.valueOf(cnt));
			tableDataValues.append(SEPARATOR);
			
			String createTime=instanceData.getCreateTime();
			
			Map<String, ReportMetricData> instanceMetricDataMap=instanceData.getMetricDataMap();
			for(ReportTemplateDirectoryMetric metric:metricList){
				String metricId=metric.getMetricId();
				MetricTypeEnum metricType=metric.getMetricType();
				if(metricType==null){
					if(metricId.equals("resourceName")){
						tableDataValues.append(instanceData.getInstanceName());
						tableDataValues.append(SEPARATOR);
					}else if(metricId.equals("resourceType")){
						tableDataValues.append(instanceData.getInstanceType());
						tableDataValues.append(SEPARATOR);
					}else if(metricId.equals("ipAddress")){
						tableDataValues.append(instanceData.getInstanceIP());
						tableDataValues.append(SEPARATOR);
					}
				}else{

					ReportMetricData metricData=instanceMetricDataMap.get(metricId);
					if(metricData==null){
						System.out.println("Can't found Metric--"+metricId);
						continue;
					}
//					MetricSummeryReportData data=metricData.getMetricData();
					if(metricType.equals(MetricTypeEnum.PerformanceMetric)){
						String avg="null";
						String max="null";
						String min="null";
						if(metricData!=null){
//							avg=String.valueOf(data.getAvg());
//							max=String.valueOf(data.getMax());
//							min=String.valueOf(data.getMin());
						}
						
						metricDataAvgMap.put(instanceData.getInstanceId()+metricId, avg);
						
						tableDataValues.append(avg);
						tableDataValues.append(SEPARATOR);
						tableDataValues.append(max);
						tableDataValues.append(SEPARATOR);
						tableDataValues.append(min);
						tableDataValues.append(SEPARATOR);
					}else{
//						String[] values=data.getValue();
//						tableDataValues.append(values);
						tableDataValues.append(SEPARATOR);
					}
				}
				
			}
			if(createTime!=null && !"".equals(createTime)){
				tableDataValues.append(createTime);
				tableDataValues.append(SEPARATOR);
			}
			String tableDataValue="";
			if(tableDataValues.lastIndexOf(SEPARATOR)!=-1){
				tableDataValue=tableDataValues.substring(0,tableDataValues.lastIndexOf(SEPARATOR));
			}
			tableData.setValue(tableDataValue);
			tableDatas.add(tableData);
			cnt++;
		}
		
		columnsData.setTableData(tableDatas);
		return columnsData;
	}
	
	private List<Chart> getCharts(){
		//type 1.柱状图2.折线图3.饼状图
		List<Chart> charts=new ArrayList<>();
		for(ReportTemplateDirectoryMetric metric:metricList){
			String metricId =metric.getMetricId();
			String metricName=metric.getMetricName();
			MetricTypeEnum metricType=metric.getMetricType();
			if(metricType!=null && metricType.equals(MetricTypeEnum.PerformanceMetric)){
				Chart chart=new Chart();
				chart.setName(metricName);
				chart.setType(chartType);

				chart.setInfo(chartInfo);

				if(isDetail){
					chart.setChartData(getDetailChartDatas(metricId));
				}else{
					chart.setChartData(getChartDatas(metricId));
				}
				charts.add(chart);
			}
			
			
			
		}
		
		return charts;
	}
	
	/**
	 * 获取一个指标的Char Data
	 * @param reportDates
	 * @param metricId
	 * @return
	 */
	private List<ChartData> getChartDatas(String metricId){
		List<ChartData> chartDatas=new ArrayList<>();
		int cnt=1;

		for(ReportInstanceData instanceData:instanceDataList){
			ChartData chartData=new ChartData();

			String name=instanceData.getInstanceName();
			String ip=instanceData.getInstanceIP();
			
			Map<String, ReportMetricData> metricDataMap=instanceData.getMetricDataMap();
			ReportMetricData reportMetricData=metricDataMap.get(metricId);
//			MetricSummeryReportData data=reportMetricData.getMetricData();
//			float avg=data.getAvg();
			
			chartData.setIp(ip);
			chartData.setName(cnt+"."+name);
//			chartData.setValue(String.valueOf(avg));
	
			chartDatas.add(chartData);
			
			cnt++;
		}
		return chartDatas;
	}
	
	private List<ChartData> getDetailChartDatas(String metricId){
		List<ChartData> chartDatas=new ArrayList<>();
		Map<String,String> sumMetricMap=new HashMap<String,String>();
		for(ReportTemplateDirectoryInstance instance:instanceList){
			long instanceId=instance.getInstanceId();
			StringBuilder sb=new StringBuilder();
			for(ReportInstanceData instanceData:instanceDataList){
				if(instanceId==instanceData.getInstanceId()){
					ReportMetricData reportMetricData=instanceData.getMetricDataMap().get(metricId);
//					MetricSummeryReportData data=reportMetricData.getMetricData();
//					float avg=data.getAvg();
//					sb.append(avg);
//					sb.append(SEPARATOR);
				}
				
			}
			String value="";
			if(sb!=null && sb.lastIndexOf(SEPARATOR)!=-1){
				value=sb.substring(0, sb.lastIndexOf(SEPARATOR));
			}
			sumMetricMap.put(String.valueOf(instanceId), value);
		}
		
		int cnt=1;
		for(ReportTemplateDirectoryInstance instance:instanceList){
			long instanceId=instance.getInstanceId();
			ChartData chartData=new ChartData();
			
			ReportInstanceData instanceData=instanceDataMap.get(String.valueOf(instanceId));
			String ip=instanceData.getInstanceIP();
			String name=instanceData.getInstanceName();
			chartData.setIp(ip);
			chartData.setName(cnt+"."+name);
			
			chartData.setValue(sumMetricMap.get(String.valueOf(instanceId)));
			
			chartDatas.add(chartData);
			cnt++;
		}
		
		
		return chartDatas;
	}
	
	/**
	 * 序列号表头
	 * @return
	 */
	private Columns getSerialNumberColunm(){
		Columns Column=new Columns();
		Column.setText("序列号");
		return Column;
	}
	
	/**
	 * 时间 表头
	 * @return
	 */
	private Columns getDateTimeColunm(){
		Columns Column=new Columns();
		Column.setText("时间");
		return Column;
	}
	
	public static void main(String[] args) {
	}
}
