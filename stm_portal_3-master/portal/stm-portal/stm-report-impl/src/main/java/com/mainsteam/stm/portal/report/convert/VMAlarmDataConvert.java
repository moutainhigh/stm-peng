package com.mainsteam.stm.portal.report.convert;

import java.util.ArrayList;
import java.util.Collection;
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
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
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

public class VMAlarmDataConvert extends BaseDataConvert{
	
	private final Log logger = LogFactory.getLog(AlarmDataConvert.class);
	
	//显示告警汇总
	private boolean showCount = true;
	//显示级别分布
	private boolean showLevel = true;
	//显示状态分布
	private boolean showState = true;
	
	//汇总饼图 默认 4 个
	List<Chart> sumCharts=new ArrayList<Chart>();
	//告警分布 饼图
	Chart alarmChart=new Chart();
	//级别分布 饼图
	Chart levelChart=new Chart();
	//状态分布 饼图
	Chart statusChart=new Chart();
	//未恢复告警 饼图
	Chart notRecoverChart=new Chart();
	
	InstanceAlarmEventReportQuery query=null;
	
	//告警分布，级别分布，状态分布
	public VMAlarmDataConvert(ReportTemplate reportTemplate,ReportTemplateDirectory reportTemplateDirectory){
		this.reportTemplate=reportTemplate;
		this.reportTemplateDirectory=reportTemplateDirectory;
		this.instanceAlarmEventReportService=(InstanceAlarmEventReportService)SpringBeanUtil.getObject("instanceAlarmEventReportService");
		
		init();
		
		
		if(metricMap.get("alarmCount")==null){
			showCount = false;
		}
		if(metricMap.get("levelDistribution")==null){
			showLevel = false;
		}
		if(metricMap.get("statusDistribution")==null){
			showState = false;
		}
	}

	
	@Override
	public ReportDirectory getReportDirectory(){
		query=new InstanceAlarmEventReportQuery();
		query.setInstanceIDes(this.instanceIDes);
		query.setTimePeriods(timePeriods); 
		
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
	
		//汇总数据
		Chapter chapterSum=new Chapter();
		chapterSum.setName("汇总数据");
		chapterSum.setSort("1");
		
		List<Table> tables=new ArrayList<Table>();
		

		//告警汇总
		Collection<InstanceAlarmEventReportData> alarmDatas=instanceAlarmEventReportService.findTotleAlarmReport(query);
		Table sumTable=this.getSumTable(alarmDatas);
		tables.add(sumTable);
		chapterSum.setTable(tables);
		chapterSum.setChart(sumCharts);
		chapters.add(chapterSum);

		//告警详细 表格
		if(isDetail){
			//详细数据
			Chapter chapterDetail=new Chapter();
			chapterDetail.setName("详细告警信息");
			chapterDetail.setSort("1");
			chapterDetail.setTable(getDetailTables());
			chapters.add(chapterDetail);
		}

		return chapters;
	}
	
	
	/**
	 * get alarm sumTable
	 * @param alarmDatas
	 * @return
	 */
	public Table getSumTable(Collection<InstanceAlarmEventReportData> alarmDatas){

		//级别汇总
		int sumCritical=0;
		int sumCerious=0;
		int sumWarn=0;
		int sumUnkown=0;
		
		//状态汇总
		int sumNotRecover=0;
		int sumRecover=0;
		
		List<TableData> tableDatas=new ArrayList<TableData>();
		List<ChartData> alarmChartDatas=new ArrayList<ChartData>();
		List<ChartData> notRecoverChartDatas=new ArrayList<ChartData>();
		
		if(alarmDatas==null || alarmDatas.size()<=0){
			int count=0;
			if(this.showCount){
				count+=1;
			}
			if(this.showLevel){
				count+=4;
			}
			if(this.showState){
				count+=2;
			}
			tableDatas=getNullTableDatas(count);
			
			for(ReportTemplateDirectoryInstance instance:instanceList){
				ChartData alarmChartData=new ChartData();
				alarmChartData.setIp(instance.getInstanceIP());
				alarmChartData.setName(instance.getInstanceName());
				alarmChartData.setValue("null");
				//告警数据
				alarmChartDatas.add(alarmChartData);
				//未回复数据
				notRecoverChartDatas.add(alarmChartData);
			}
			
		}else{
			Map<Long,InstanceAlarmEventReportData> alarmDataMap=new HashMap<Long, InstanceAlarmEventReportData>();
			for(InstanceAlarmEventReportData alarmData:alarmDatas){
				long instanceId=alarmData.getInstanceID();
				alarmDataMap.put(instanceId, alarmData);
			}
			
			int cnt=1;
			for(ReportTemplateDirectoryInstance instance:instanceList){
				InstanceAlarmEventReportData alarmData=alarmDataMap.get(instance.getInstanceId());
				
				StringBuilder value=new StringBuilder();
				value.append(cnt);
				value.append(SEPARATOR);
				value.append(instance.getInstanceIP());
				value.append(SEPARATOR);
				value.append(instance.getInstanceName());
				value.append(SEPARATOR);
				value.append(instance.getInstanceType());
				value.append(SEPARATOR);
				
				ChartData alarmChartData=new ChartData();
				alarmChartData.setIp(instance.getInstanceIP());
				alarmChartData.setName(instance.getInstanceName());
				
				ChartData notRecoverChartData=new ChartData();
				notRecoverChartData.setIp(instance.getInstanceIP());
				notRecoverChartData.setName(instance.getInstanceName());
				
				if(alarmData==null){
					if(this.showCount){
						value.append("null");
						value.append(SEPARATOR);
					}
					if(this.showLevel){
						value.append("null");
						value.append(SEPARATOR);
						value.append("null");
						value.append(SEPARATOR);
						value.append("null");
						value.append(SEPARATOR);
						value.append("null");
						value.append(SEPARATOR);
					}
					if(this.showState){
						value.append("null");
						value.append(SEPARATOR);
						value.append("null");
						value.append(SEPARATOR);
					}

					alarmChartData.setValue("null");
					notRecoverChartData.setValue("null");

				}else{
					//告警数量
					Integer alarmCount=null;
					
					//致命 
					Integer critical=alarmData.getCountCritical();
					sumCritical+=critical;
					//严重
					Integer serious=alarmData.getCountSerious();
					sumCerious+=serious;
					//警告
					Integer warn=alarmData.getCountWarn();
					sumWarn+=warn;
					
					//Not恢复 
					Integer notRecover=alarmData.getCountNotRecover();
					sumNotRecover+=notRecover;
					//恢复 
					Integer recover=alarmData.getCountRecover();
					sumRecover+=recover;
					
					alarmCount=critical+serious+warn;
					
					//告警数量
					if(this.showCount){
						value.append(alarmCount);
						value.append(SEPARATOR);
					}
					//级别
					if(this.showLevel){
						value.append(critical);
						value.append(SEPARATOR);
						value.append(serious);
						value.append(SEPARATOR);
						value.append(warn);
						value.append(SEPARATOR);
					}
					//状态
					if(this.showState){
						value.append(notRecover);
						value.append(SEPARATOR);
						value.append(recover);	
						value.append(SEPARATOR);
					}

					alarmChartData.setValue(String.valueOf(alarmCount));
					notRecoverChartData.setValue(String.valueOf(notRecover));
				}

				alarmChartDatas.add(alarmChartData);
				notRecoverChartDatas.add(notRecoverChartData);
				
				TableData tableData=new TableData();
				tableData.setValue(checkSeparator(value));
				tableDatas.add(tableData);
				cnt++;
			}
		}

		//级别分布 饼图
		List<ChartData> levelChartDatas=new ArrayList<ChartData>();
		ChartData chartData1=new ChartData();
		chartData1.setName(InstanceStateEnum.getValue(InstanceStateEnum.CRITICAL));
		chartData1.setColor("#790306");
		chartData1.setValue(String.valueOf(sumCritical));
		ChartData chartData2=new ChartData();
		chartData2.setName(InstanceStateEnum.getValue(InstanceStateEnum.SERIOUS));
		chartData2.setColor("#FE4600");
		chartData2.setValue(String.valueOf(sumCerious));
		ChartData chartData3=new ChartData();
		chartData3.setName(InstanceStateEnum.getValue(InstanceStateEnum.WARN));
		chartData3.setColor("#FDC607");
		chartData3.setValue(String.valueOf(sumWarn));
		levelChartDatas.add(chartData1);
		levelChartDatas.add(chartData2);
		levelChartDatas.add(chartData3);

		//状态分布 饼图
		List<ChartData> stateChartDatas=new ArrayList<ChartData>();
		ChartData chartDataA=new ChartData();
		chartDataA.setName("未恢复");
		chartDataA.setValue(String.valueOf(sumNotRecover));
		ChartData chartDataB=new ChartData();
		chartDataB.setName("已恢复");
		chartDataB.setValue(String.valueOf(sumRecover));
		stateChartDatas.add(chartDataA);
		stateChartDatas.add(chartDataB);
		
		//告警分布 饼图
		alarmChart.setName("告警分布");
		alarmChart.setType("3");
		alarmChart.setChartData(alarmChartDatas);
		
		//级别分布 饼图
		levelChart.setName("级别分布");
		levelChart.setType("3");
		levelChart.setChartData(levelChartDatas);
		
		//状态分布 饼图
		statusChart.setName("状态分布");
		statusChart.setType("3");
		statusChart.setChartData(stateChartDatas);
		
		//未恢复告警 饼图
		notRecoverChart.setName("未恢复告警");
		notRecoverChart.setType("3");
		notRecoverChart.setChartData(notRecoverChartDatas);

		
		if(this.showCount){
			sumCharts.add(alarmChart);
		}
		
		if(this.showLevel){
			sumCharts.add(levelChart);
		}
		
		if(this.showState){
			sumCharts.add(statusChart);
			sumCharts.add(notRecoverChart);
		}

		ColumnsTitle sumTitle= getSumColumnsTitle();
		ColumnsData columnsData=new ColumnsData();
		columnsData.setTableData(tableDatas);
		
		Table sumTable=new Table();
		sumTable.setName("告警汇总数据");
		sumTable.setColumnsTitle(sumTitle);
		sumTable.setColumnsData(columnsData);
		
		return sumTable;
	}
	

	public ColumnsTitle getSumColumnsTitle(){
		ColumnsTitle columnsTitle=new ColumnsTitle();
		List<Columns> columns=new ArrayList<Columns> ();
		columns.add(new Columns("序列号"));
		columns.add(new Columns("IP地址"));
		columns.add(new Columns("资源名称"));
		columns.add(new Columns("资源类型"));
		
		if(this.showCount){
			columns.add(new Columns("告警数量"));
		}
		if(this.showLevel){
			columns.add(new Columns("级别分布","致命,严重,警告"));
		}
		if(this.showState){
			columns.add(new Columns("状态分布","未恢复,已恢复"));
		}
		
		columnsTitle.setColumns(columns);
		return columnsTitle;
	}
	

	public List<Table> getDetailTables(){
		
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
						return d2.getCollectionTime().compareTo(d1.getCollectionTime());
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
					//CRITICAL(10), SERIOUS(8), WARN(6), UNKOWN(4), NORMAL(0);					  
					  
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
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}

