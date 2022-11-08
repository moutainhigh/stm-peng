package com.mainsteam.stm.portal.report.convert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.report.obj.BizSerMetric;
import com.mainsteam.stm.portal.business.report.obj.BizSerMetricEnum;
import com.mainsteam.stm.portal.business.report.obj.BizSerReport;
import com.mainsteam.stm.portal.report.bo.Chapter;
import com.mainsteam.stm.portal.report.bo.Chart;
import com.mainsteam.stm.portal.report.bo.ChartData;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsData;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.ReportDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.Table;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.util.SpringBeanUtil;

public class BusinessDataConvert extends BaseDataConvert{
	
	private final Log logger = LogFactory.getLog(BusinessDataConvert.class);
	//BizSerReport 业务对象
	//BizSerMetricEnum 指标枚举
	//BizSerReportApi----------------------报表数据获取
	
	public BizMainApi bizMainApi; 
	
	private List<BizSerMetric> bizSerMetricList;
	private List<BizSerReport> bizSerReportList;
	
	//初始化
	public BusinessDataConvert(ReportTemplate reportTemplate,ReportTemplateDirectory reportTemplateDirectory){
		this.reportTemplate=reportTemplate;
		this.reportTemplateDirectory=reportTemplateDirectory;
		
		bizMainApi = (BizMainApi) SpringBeanUtil.getObject("bizMainApi");
		init();

		bizSerMetricList = bizMainApi.getBizReportMetrics();
	}
	
	@Override
	public ReportDirectory getReportDirectory() {
		ReportDirectory reportDirectory=new ReportDirectory();
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
		//业务服务系统数据
		Chapter chapter=new Chapter();
		chapter.setName("业务服务系统");
		chapter.setSort("1");
		
		chapter.setTable(getTables());
		chapter.setChart(getCharts());
		chapters.add(chapter);
		return chapters;
	}
	/**
	 * 获取表格数据
	 * @return
	 */
	public List<Table>  getTables(){
		List<Table> tables=new ArrayList<Table>();
		Table table=new Table();
		table.setColumnsTitle(getColumnsTitle());
		table.setName("业务服务列表");
		
		ColumnsData columnsData=new ColumnsData();
		List<TableData> tableDataList = new ArrayList<TableData>();
		//need to do =========================
		
		try {
			bizSerReportList = bizMainApi.getBizReportData(instanceIDes, timePeriods);
		} catch (Exception e) {
			logger.error("businessDataConvert:", e);
			e.printStackTrace();
		}
		
		if(bizSerReportList == null || bizSerReportList.size() <= 0){
			return tables;
		}
		
		//组装 columnsData
		for (int i = 0; i < bizSerReportList.size(); i++) {
			BizSerReport bizSerReport = bizSerReportList.get(i);
			StringBuilder tableValue = new StringBuilder();
			tableValue.append(String.valueOf(i + 1)).append(SEPARATOR).append(bizSerReport.getName()).append(SEPARATOR);
			for (int j = 0; j < bizSerMetricList.size(); j++) {
				BizSerMetric bizSerMetric = bizSerMetricList.get(j);
				if(metricIds.contains(bizSerMetric.getId())){
					tableValue.append(getBizSerReportValue(bizSerReport, bizSerMetric.getId())).append(SEPARATOR);
				}
			}
			tableValue.replace(tableValue.lastIndexOf(SEPARATOR), tableValue.length(), "");
			TableData tableData = new TableData();
			tableData.setBizId(String.valueOf(bizSerReport.getId()));
			tableData.setValue(tableValue.toString());
			tableDataList.add(tableData);
		}
		//need to do =========================
		columnsData.setTableData(tableDataList);
		table.setColumnsData(columnsData);
		
		tables.add(table);
		return tables;
	}
	
	/**
	 * 获取柱状图数据
	 * @return
	 */
	public List<Chart> getCharts(){
		List<Chart> charts=new ArrayList<Chart>();
		
		for(BizSerMetric bizSerMetric : bizSerMetricList){
			if(metricIds.contains(bizSerMetric.getId())){
				Chart chart=new Chart();
				chart.setName(bizSerMetric.getName());
				chart.setType("1");
				List<ChartData> chartDatas=new ArrayList<ChartData>();
				for (int i = 0; i < bizSerReportList.size(); i++) {
					BizSerReport bizSerReport = bizSerReportList.get(i);
					ChartData chartData = new ChartData();
					chartData.setName(bizSerReport.getName());
					chartData.setValue(getBizSerReportValue(bizSerReport, bizSerMetric.getId()));
					chartDatas.add(chartData);
				}
				chart.setChartData(chartDatas);
				charts.add(chart);
			}
		}
		
		return charts;
	}
	
	
	/**
	 * 获取表格头
	 * @return
	 */
	private ColumnsTitle getColumnsTitle(){
		ColumnsTitle title=new ColumnsTitle();
		List<Columns> columns=new ArrayList<Columns> ();
		columns.add(new Columns("序列号"));
		columns.add(new Columns("业务名称"));
		for (int i = 0; i < bizSerMetricList.size(); i++) {
			BizSerMetric bizSerMetric = bizSerMetricList.get(i);
			if(metricIds.contains(bizSerMetric.getId())){
				Columns column = new Columns(bizSerMetric.getName());
				column.setBizMetricId(bizSerMetric.getId());
				columns.add(column);
			}
		}
		title.setColumns(columns);
		return title;
	}

	private String getBizSerReportValue(BizSerReport bizSerReport, String index){
		String value = "";
		BizSerMetricEnum BizSerMetricType = BizSerMetricEnum.getBizSerMetricEnum(index);
		switch (BizSerMetricType) {
		case AVAILABLE_RATE:
			value = String.valueOf(bizSerReport.getAvailableRate());
			break;
		case MTTR:
			value = String.valueOf(bizSerReport.getMttr());
			break;
		case MTBF:
			value = String.valueOf(bizSerReport.getMtbf());
			break;
		case OUTAGE_TIMES:
			value = String.valueOf(bizSerReport.getOutageTimes());
			break;
		case DOWNTIME:
			value = String.valueOf(bizSerReport.getDownTime());
			break;
		case WARN_NUM:
			value = String.valueOf(bizSerReport.getWarnNum());
			break;
		case UNRECOVERED_WARN_NUM:
			value = String.valueOf(bizSerReport.getUnrecoveredWarnNum());
			break;
		default:
			break;
		}
		return value;
	}
	
}
