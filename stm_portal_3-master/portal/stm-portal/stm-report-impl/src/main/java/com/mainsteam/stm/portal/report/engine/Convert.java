package com.mainsteam.stm.portal.report.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportData;
import com.mainsteam.stm.portal.report.bo.Chart;
import com.mainsteam.stm.portal.report.bo.ChartData;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryMetric;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.portal.report.engine.databean.ReportInstanceData;
import com.mainsteam.stm.portal.report.engine.databean.ReportMetricData;

public class Convert {
	
	private static final String SEPARATOR=",";
	
	
	private List<Long> instanceIDes;
	//固定表头
	private List<ReportTemplateDirectoryMetric> tableMetrics;
	
	
	private List<ReportInstanceData> instanceDatas;
	private StringBuilder charInfo=new StringBuilder();
	private HashMap<String,String> metricDataAvgMap;
	
	private Map<String,ReportInstanceData> sumInstanceDataMap=new HashMap<String,ReportInstanceData>();
	
	Convert(List<Long> instanceIDes,List<ReportTemplateDirectoryMetric> tableMetrics,List<ReportInstanceData> instanceDatas){
		this.instanceIDes=instanceIDes;
		this.tableMetrics=tableMetrics;
		this.instanceDatas=instanceDatas;
		metricDataAvgMap=new HashMap<String,String>();
	}
	
	
	
	public ColumnsTitle getColumnsTitle(boolean hasTime){
		ColumnsTitle title=new ColumnsTitle();
		List<Columns> columns=new ArrayList<Columns> ();
		columns.add(getSerialNumberColunm());
		for(ReportTemplateDirectoryMetric tableCount:tableMetrics){
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
	
	public List<TableData> getPerformanceTableData(){
		
		List<TableData> tableDatas=new ArrayList<TableData>();
		
		int cnt=1;
		for(ReportInstanceData instanceData:instanceDatas){
			TableData tableData=new TableData();
			StringBuilder tableDataValues=new StringBuilder();
			//序列号
			tableDataValues.append(String.valueOf(cnt));
			tableDataValues.append(SEPARATOR);

			String createTime=instanceData.getCreateTime();
			if(createTime!=null && !"".equals(createTime)){
				charInfo.append(createTime);
				charInfo.append(SEPARATOR);
			}
			
			
			Map<String, ReportMetricData> metricDataMap= instanceData.getMetricDataMap();
			for(ReportTemplateDirectoryMetric tableMetric :tableMetrics){
				String id=tableMetric.getMetricId();
				MetricTypeEnum metricType=tableMetric.getMetricType();
				if(metricType==null){
					if(id.equals("resourceName")){
						tableDataValues.append(instanceData.getInstanceName());
						tableDataValues.append(SEPARATOR);
					}else if(id.equals("resourceType")){
						tableDataValues.append(instanceData.getInstanceType());
						tableDataValues.append(SEPARATOR);
					}else if(id.equals("ipAddress")){
						tableDataValues.append(instanceData.getInstanceIP());
						tableDataValues.append(SEPARATOR);
					}
				}else{
					ReportMetricData metric=metricDataMap.get(id);
					if(metric==null){
						System.out.println("Can't found Metric--"+id);
						continue;
					}
					MetricSummeryReportData metricData=null;
//							metric.getMetricData();
					if(metricType.equals(MetricTypeEnum.PerformanceMetric)){
						String avg="null";
						String max="null";
						String min="null";
						if(metricData!=null){
							avg=String.valueOf(metricData.getAvg());
							max=String.valueOf(metricData.getMax());
							min=String.valueOf(metricData.getMin());
						}
						metricDataAvgMap.put(instanceData.getInstanceId()+id, avg);
						
						tableDataValues.append(avg);
						tableDataValues.append(SEPARATOR);
						tableDataValues.append(max);
						tableDataValues.append(SEPARATOR);
						tableDataValues.append(min);
						tableDataValues.append(SEPARATOR);
					}else{
						if(metricData != null){
							String[] values=metricData.getValue();
							if(values != null && values.length > 0){
								for(String value : values){
									tableDataValues.append(value);
								}
							}
							tableDataValues.append(SEPARATOR);
						}
					}
					
				}
				
			}
			
			if(createTime!=null && !"".equals(createTime)){
				tableDataValues.append(createTime);
				tableDataValues.append(SEPARATOR);
			}
			tableData.setValue(tableDataValues.substring(0,tableDataValues.lastIndexOf(SEPARATOR)));
			tableDatas.add(tableData);
			cnt++;
		}
		
		return tableDatas;
	}
	
	public List<Chart> getPerformanceTableCharts(int type,boolean isDetail){
		//type 1.柱状图2.折线图3.饼状图
		List<Chart> charts=new ArrayList<>();
		for(ReportTemplateDirectoryMetric tableCount:tableMetrics){
			String metricId =tableCount.getMetricId();
			String metricName=tableCount.getMetricName();
			MetricTypeEnum metricType=tableCount.getMetricType();
			if(metricType!=null && metricType.equals(MetricTypeEnum.PerformanceMetric)){
				Chart chart=new Chart();
				chart.setName(metricName);
				chart.setType(String.valueOf(type));
				String charStr=null;
				if(charInfo!=null && charInfo.lastIndexOf(SEPARATOR)!=-1){
					charStr=charInfo.substring(0, charInfo.lastIndexOf(SEPARATOR));
				}
				chart.setInfo(charStr);
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

		for(ReportInstanceData instanceData:instanceDatas){
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
		for(long instanceId:instanceIDes){
			StringBuilder sb=new StringBuilder();
			for(ReportInstanceData instanceData:instanceDatas){
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
		for(long instanceId:instanceIDes){
			ChartData chartData=new ChartData();
			
			ReportInstanceData instanceData=sumInstanceDataMap.get(String.valueOf(instanceId));
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
	
	public static void main(String[] a){
		String s="aa,vv,cc,";

		System.out.println(s.substring(0,s.lastIndexOf(",")));

	}



	public Map<String, ReportInstanceData> getSumInstanceDataMap() {
		return sumInstanceDataMap;
	}



	public void setSumInstanceDataMap(
			Map<String, ReportInstanceData> sumInstanceDataMap) {
		this.sumInstanceDataMap = sumInstanceDataMap;
	}
	
	
	
}
