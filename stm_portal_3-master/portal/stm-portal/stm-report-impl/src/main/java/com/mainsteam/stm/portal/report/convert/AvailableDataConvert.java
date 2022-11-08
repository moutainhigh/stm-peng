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

import com.mainsteam.stm.alarm.event.report.InstanceAlarmEventReportQuery;
import com.mainsteam.stm.alarm.event.report.InstanceAlarmEventReportService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.report.AvailableMetricCountData;
import com.mainsteam.stm.common.metric.report.AvailableMetricCountQuery;
import com.mainsteam.stm.common.metric.report.AvailableMetricDataReportService;
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
import com.mainsteam.stm.portal.report.bo.Table;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.util.SpringBeanUtil;

public class AvailableDataConvert extends BaseDataConvert{

	private final Log logger = LogFactory.getLog(AvailableDataConvert.class);
	
	//不可用比率
	private boolean showAvailabilityRatio=true;
	//不可用次数
	private boolean showInvalidCount=true;
	//不可用时长
	private boolean showInvalidTime=true;
	//MTTR
	private boolean showMTTR=true;
	//MTBF
	private boolean showMTBF=true;
	
	//图标数据
	private List<ChartData> availabilityRateChartDatas=new ArrayList<ChartData>();
	private List<ChartData> invalidCountChartDatas=new ArrayList<ChartData>();
	private List<ChartData> invalidTimeChartDatas=new ArrayList<ChartData>();
	private List<ChartData> MTTRChartDatas=new ArrayList<ChartData>();
	private List<ChartData> MTBFChartDatas=new ArrayList<ChartData>();
	
	private List<Chart> charts=new ArrayList<Chart>();
	
	public AvailableDataConvert(ReportTemplate reportTemplate,ReportTemplateDirectory reportTemplateDirectory){
		this.reportTemplateDirectory=reportTemplateDirectory;
		this.reportTemplate=reportTemplate;
		this.reportTemplateDirectory=reportTemplateDirectory;
		this.availableReportService=(AvailableMetricDataReportService)SpringBeanUtil.getObject("availableMetricDataReportService");
		this.instanceAlarmEventReportService=(InstanceAlarmEventReportService)SpringBeanUtil.getObject("instanceAlarmEventReportService");
		init();

		showAvailabilityRatio=metricMap.get("availabilityRatio")!=null;
		showInvalidCount =metricMap.get("metricInvalidCount")!=null;
		showInvalidTime=metricMap.get("invalidTime")!=null;
		showMTTR=metricMap.get("mttr")!=null;
		showMTBF=metricMap.get("mtbf")!=null;
	}
	
	@Override
	public ReportDirectory getReportDirectory(){		
		ReportDirectory reportDirectory=new ReportDirectory();
		reportDirectory.setName(this.reportDirectoryName);
		reportDirectory.setType(String.valueOf(reportType));
		reportDirectory.setChapter(getChapters());
		return reportDirectory; 
	}
	
	/**
	 * 获取所有章节
	 * @return
	 */
	public List<Chapter> getChapters(){
		List<Chapter> chapters=new ArrayList<Chapter>();
		//汇总
		chapters.add(getSumChapter());
		if(isDetail){
			chapters.add(getDetailChapter());
		}
		return chapters;
	}
	/**
	 * 获取汇总数据表格章节
	 */
	public Chapter getSumChapter(){
		Chapter chapter=new Chapter();
		chapter.setName("汇总数据");
		chapter.setSort("1");
		
		List<Table> tables=new ArrayList<Table>();
		tables.add(getSumTable());
		chapter.setTable(tables);
		chapter.setChart(charts);
		
		return chapter;
	}

	/**
	 * 可用性告警
	 * @return
	 */
	public Chapter getDetailChapter(){
		Chapter chapter=new Chapter();
		chapter.setName("可用性告警");
		chapter.setSort("1");
		
		chapter.setTable(getAvailableAlarmTables());
		return chapter;
	}
	
	public Table getSumTable(){
		AvailableMetricCountQuery query=new AvailableMetricCountQuery();
		query.setInstanceIDes(instanceIDes);
		query.setTimePeriods(timePeriods);
	
		List<AvailableMetricCountData> availableSumDatas=availableReportService.findAvailableCount(query);
		
		//表格数据
		List<TableData> tableDatas=new ArrayList<TableData>();

		if(availableSumDatas==null || availableSumDatas.size()<=0){
			int count=0;
			if(showAvailabilityRatio){
				count++;
			}
			if(showInvalidCount){
				count++;
			}
			if(showInvalidTime){
				count++;
			}
			if(showMTTR){
				count++;
			}
			if(showMTBF){
				count++;
			}
			tableDatas=this.getNullTableDatas(count);
			for(ReportTemplateDirectoryInstance instance:instanceList){
				ChartData chartData=new ChartData();
				chartData.setIp(instance.getInstanceIP());
				chartData.setName(instance.getInstanceName());
				chartData.setValue("null");
				
				availabilityRateChartDatas.add(chartData);
				invalidTimeChartDatas.add(chartData);
				MTTRChartDatas.add(chartData);
				MTBFChartDatas.add(chartData);
			}
			
		}else{
			int cnt=1;
			for(AvailableMetricCountData availableSumData:availableSumDatas){
				StringBuilder value=new StringBuilder();
				long instanceId=availableSumData.getInstanceID();
				ReportTemplateDirectoryInstance instance=instanceMap.get(instanceId);
				
				String ip=instance.getInstanceIP();
				String name=instance.getInstanceName();
				String type=instance.getInstanceType();
				
				TableData tableData=new TableData();
				ChartData availabilityRateChartData = new ChartData();
				ChartData invalidCountChartData =new ChartData();
				ChartData invalidTimeChartData =new ChartData();
				ChartData MTTRChartData = new ChartData();
				ChartData MTBFChartData = new ChartData();
				
				availabilityRateChartData.setIp(ip);
				availabilityRateChartData.setName(name);
				invalidCountChartData.setIp(ip);
				invalidCountChartData.setName(name);
				invalidTimeChartData.setIp(ip);
				invalidTimeChartData.setName(name);
				MTTRChartData.setIp(ip);
				MTTRChartData.setName(name);
				MTBFChartData.setIp(ip);
				MTBFChartData.setName(name);
				
				value.append(cnt);
				value.append(SEPARATOR);
				value.append(ip);
				value.append(SEPARATOR);
				value.append(name);
				value.append(SEPARATOR);
				value.append(type);
				value.append(SEPARATOR);
				//可用性比率%
				if(showAvailabilityRatio){
					DecimalFormat decimalFormat = new DecimalFormat(".00");
					float availabilityRate=availableSumData.getAvailabilityRate() * 100;
					availabilityRate = Float.parseFloat(decimalFormat.format(availabilityRate));
					String availabilityRateString = "";
					if(availabilityRate == (int)availabilityRate){
						availabilityRateString = (int)availabilityRate + "";
					}else{
						availabilityRateString = availabilityRate + "";
					}
					value.append(availabilityRateString);
					value.append(SEPARATOR);
					availabilityRateChartData.setValue(availabilityRateString);
					availabilityRateChartDatas.add(availabilityRateChartData);
				}
				//不可用次数
				if(showInvalidCount){
					float invalidCount=availableSumData.getNotAvailabilityNum();
					String invalidCountString = "";
					if(invalidCount == (int)invalidCount){
						invalidCountString = (int)invalidCount + "";
					}else{
						invalidCountString = invalidCount + "";
					}
					value.append(invalidCountString);
					value.append(SEPARATOR);
					invalidCountChartData.setValue(invalidCountString);
					
					invalidCountChartDatas.add(invalidCountChartData);
				}
				//不可用时长(小时)
				if(showInvalidTime){
					float invalidTime=availableSumData.getNotAvailabilityDurationHour();
					String invalidTimeString = "";
					if(invalidTime == (int)invalidTime){
						invalidTimeString = (int)invalidTime + "";
					}else{
						invalidTimeString = invalidTime + "";
					}
					value.append(invalidTimeString);
					value.append(SEPARATOR);
					invalidTimeChartData.setValue(invalidTimeString);
					
					invalidTimeChartDatas.add(invalidTimeChartData);
				}
				//MTTR(小时)
				if(showMTTR){
					float MTTR=availableSumData.getMTTR();
					String MTTRString = "";
					if(MTTR == (int)MTTR){
						MTTRString = (int)MTTR + "";
					}else{
						MTTRString = MTTR + "";
					}
					value.append(MTTRString);
					value.append(SEPARATOR);
					MTTRChartData.setValue(MTTRString);
					MTTRChartDatas.add(MTTRChartData);
				}
				//MTBF(小时)
				if(showMTBF){
					float MTBF=availableSumData.getMTBF();
					String MTBFString = "";
					if(MTBF == (int)MTBF){
						MTBFString = (int)MTBF + "";
					}else{
						MTBFString = MTBF + "";
					}
					value.append(MTBFString);
					value.append(SEPARATOR);
					MTBFChartData.setValue(MTBFString);
					MTBFChartDatas.add(MTBFChartData);
				}	
				tableData.setValue(this.checkSeparator(value));
				tableDatas.add(tableData);
				cnt++;
			}
			
			// 补全没有查询到的资源实例
			for(ReportTemplateDirectoryInstance instance:instanceList){
				boolean flag = true;
				for(AvailableMetricCountData availableSumData:availableSumDatas){
					if(instance.getInstanceId() == availableSumData.getInstanceID()){
						flag = false;
						break;
					}
				}
				if(flag){
					String ip=instance.getInstanceIP();
					String name=instance.getInstanceName();
					String type=instance.getInstanceType();
					StringBuilder value=new StringBuilder();
					TableData tableData=new TableData();
					ChartData availabilityRateChartData = new ChartData();
					ChartData invalidCountChartData =new ChartData();
					ChartData invalidTimeChartData =new ChartData();
					ChartData MTTRChartData = new ChartData();
					ChartData MTBFChartData = new ChartData();
					
					availabilityRateChartData.setIp(ip);
					availabilityRateChartData.setName(name);
					invalidCountChartData.setIp(ip);
					invalidCountChartData.setName(name);
					invalidTimeChartData.setIp(ip);
					invalidTimeChartData.setName(name);
					MTTRChartData.setIp(ip);
					MTTRChartData.setName(name);
					MTBFChartData.setIp(ip);
					MTBFChartData.setName(name);
					value.append(cnt);
					value.append(SEPARATOR);
					value.append(ip);
					value.append(SEPARATOR);
					value.append(name);
					value.append(SEPARATOR);
					value.append(type);
					value.append(SEPARATOR);
					//可用性比率%
					if(showAvailabilityRatio){
						value.append("null");
						value.append(SEPARATOR);
						availabilityRateChartData.setValue("null");
						availabilityRateChartDatas.add(availabilityRateChartData);
					}
					//不可用次数
					if(showInvalidCount){
						value.append("null");
						value.append(SEPARATOR);
						invalidCountChartData.setValue("null");
						invalidCountChartDatas.add(invalidCountChartData);
					}
					//不可用时长(小时)
					if(showInvalidTime){
						value.append("null");
						value.append(SEPARATOR);
						invalidTimeChartData.setValue("null");
						invalidTimeChartDatas.add(invalidTimeChartData);
					}
					//MTTR(小时)
					if(showMTTR){
						value.append("null");
						value.append(SEPARATOR);
						MTTRChartData.setValue("null");
						MTTRChartDatas.add(MTTRChartData);
					}
					//MTBF(小时)
					if(showMTBF){
						value.append("null");
						value.append(SEPARATOR);
						MTBFChartData.setValue("null");
						MTBFChartDatas.add(MTBFChartData);
					}	
					tableData.setValue(this.checkSeparator(value));
					tableDatas.add(tableData);
					cnt++;
				}
			}
			
		}

		Chart availabilityRateChart=new Chart();
		availabilityRateChart.setName("可用性比率(%)");
		availabilityRateChart.setType("1");
		availabilityRateChart.setChartData(availabilityRateChartDatas);
		
		Chart invalidCountChart=new Chart();
		invalidCountChart.setName("不可用次数");
		invalidCountChart.setType("1");
		invalidCountChart.setChartData(invalidCountChartDatas);
		
		Chart invalidTimeChart=new Chart();
		invalidTimeChart.setName("不可用时长(小时)");
		invalidTimeChart.setType("1");
		invalidTimeChart.setChartData(invalidTimeChartDatas);
		
		Chart MTTRChart=new Chart();
		MTTRChart.setName("MTTR(小时)");
		MTTRChart.setType("1");
		MTTRChart.setChartData(MTTRChartDatas);
		
		Chart MTBFChart=new Chart();
		MTBFChart.setName("MTBF(小时)");
		MTBFChart.setType("1");
		MTBFChart.setChartData(MTBFChartDatas);
		
		if(showAvailabilityRatio){
			charts.add(availabilityRateChart);
		}
		if(showInvalidCount){
			charts.add(invalidCountChart);
		}
		if(showInvalidTime){
			charts.add(invalidTimeChart);
		}
		if(showMTBF){
			charts.add(MTBFChart);
		}
		if(showMTTR){
			charts.add(MTTRChart);
		}
		
		ColumnsTitle sumTitle= getSumColumnsTitle();
		ColumnsData columnsData=new ColumnsData();
		columnsData.setTableData(tableDatas);
		
		Table sumTable=new Table();
		sumTable.setName("可用性汇总数据");
		sumTable.setColumnsTitle(sumTitle);
		sumTable.setColumnsData(columnsData);
		
		return sumTable;
	}
	
	public List<Table> getAvailableAlarmTables(){
	List<Table> tables=new ArrayList<Table>();
		
		ColumnsTitle title=this.getDetailColumnsTitle();
		Map<Long,ColumnsData> columnsDataMap=getDetailColumnsDataMap();
		for(ReportTemplateDirectoryInstance instance:instanceList){
			String tableName="IP地址："+instance.getInstanceIP()+" | 资源名称："+instance.getInstanceName()+" | 资源类型："+instance.getInstanceType();
			Table table=new Table();
			table.setName(tableName);
			table.setColumnsTitle(title);
			ColumnsData columnsData=columnsDataMap.get(instance.getInstanceId());
			if(columnsData==null){
				columnsData=new ColumnsData();
				columnsData.setTableData(getNullDetalTableDatas(5));
			}
			table.setColumnsData(columnsData);
			tables.add(table);
		}	

		return tables;
	}
	
	public Map<Long,ColumnsData> getDetailColumnsDataMap(){
		Map<Long,ColumnsData> columnsDataMap=new HashMap<Long, ColumnsData>();
		InstanceAlarmEventReportQuery query=new InstanceAlarmEventReportQuery();
		query.setInstanceIDes(this.instanceIDes);
		query.setTimePeriods(timePeriods);
		List<AlarmEvent> alarmEvents=instanceAlarmEventReportService.findTotleAlarmDetail(query);
		
		if(alarmEvents==null || alarmEvents.size()<=0){
			logger.warn("Can't found detail data.");
		}else{
			for(ReportTemplateDirectoryInstance instance:instanceList){
				List<AlarmEvent> subList=new ArrayList<AlarmEvent>();
				
				for(AlarmEvent alarmEvent:alarmEvents){
					if(instance.getInstanceId()==Integer.parseInt(alarmEvent.getSourceID())){
						subList.add(alarmEvent);
					}
				}
				Collections.sort(subList,new Comparator<AlarmEvent>(){
					@Override
					public int compare(AlarmEvent d1,AlarmEvent d2) {
						return d1.getCollectionTime().compareTo(d2.getCollectionTime());
					}
				});
				
				List<TableData> tableDatas=new ArrayList<TableData>();
				int cnt=1;
				for(AlarmEvent alarmEvent:subList){
					TableData tableData=new TableData();
					StringBuilder value=new StringBuilder();
					value.append(cnt);
					value.append(SEPARATOR);
					
					//级别
					String level=InstanceStateEnum.getValue(InstanceStateEnum.NORMAL);
					if(alarmEvent.getLevel().equals(InstanceStateEnum.CRITICAL)){
						level=InstanceStateEnum.getValue(InstanceStateEnum.CRITICAL);
					}else if(alarmEvent.getLevel().equals(InstanceStateEnum.SERIOUS)){
						level=InstanceStateEnum.getValue(InstanceStateEnum.SERIOUS);
					}else if(alarmEvent.getLevel().equals(InstanceStateEnum.WARN)){
						level=InstanceStateEnum.getValue(InstanceStateEnum.WARN);
					}else if(alarmEvent.getLevel().equals(InstanceStateEnum.NORMAL)){
						level=InstanceStateEnum.getValue(InstanceStateEnum.NORMAL);
					}
					value.append(level);
					value.append(SEPARATOR);
					//状态
					value.append(alarmEvent.isRecovered() ? "已恢复":"未恢复");
					value.append(SEPARATOR);
					//告警内容
					value.append(alarmEvent.getContent());
					value.append(SEPARATOR);
					//产生时间
					value.append(simpleDateFormat.format(alarmEvent.getCollectionTime()));
					tableData.setValue(value.toString());
					//adds
					tableDatas.add(tableData);
					cnt++;
				}
				
				long instanceId=instance.getInstanceId();
				ColumnsData columnsData=new ColumnsData();
				columnsData.setTableData(tableDatas);
				columnsDataMap.put(instanceId, columnsData);
			}	
		}
		
		return columnsDataMap;
	}
	
	


	public ColumnsTitle getSumColumnsTitle(){
		ColumnsTitle columnsTitle=new ColumnsTitle();
		List<Columns> columns=new ArrayList<Columns> ();
		columns.add(new Columns("序列号"));
		columns.add(new Columns("IP地址"));
		columns.add(new Columns("资源名称"));
		columns.add(new Columns("资源类型"));
		if(showAvailabilityRatio){
			columns.add(new Columns("可用性比率%"));
		}
		if(showInvalidCount){
			columns.add(new Columns("不可用次数"));
		}
		if(showInvalidTime){
			columns.add(new Columns("不可用时长(小时)"));
		}
		if(showMTBF){
			columns.add(new Columns("MTTR(小时)"));
		}
		if(showMTTR){
			columns.add(new Columns("MTBF(小时)"));
		}
		
		columnsTitle.setColumns(columns);
		return columnsTitle;
	}
	
	//序号  	级别	状态	告警内容	产生时间
	private ColumnsTitle getDetailColumnsTitle(){
		ColumnsTitle columnsTitle=new ColumnsTitle();
		List<Columns> columns=new ArrayList<Columns>();
		columns.add(new Columns("序号"));
		columns.add(new Columns("级别"));
		columns.add(new Columns("状态"));
		columns.add(new Columns("告警内容"));
		columns.add(new Columns("产生时间"));
		columnsTitle.setColumns(columns);
		return columnsTitle;
	}

}
