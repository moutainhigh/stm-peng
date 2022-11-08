package com.mainsteam.stm.portal.report.convert;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.common.metric.report.InstanceMetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricDataReportService;
import com.mainsteam.stm.common.metric.report.MetricDataTopQuery;
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

public class PerformanceDataConvert extends BaseDataConvert{
	
	private final Log logger = LogFactory.getLog(PerformanceDataConvert.class);
	
	//详细数据列表 资源ID查询
	Map<Long,List<InstanceMetricSummeryReportData>> instanceDataListMap=new HashMap<Long, List<InstanceMetricSummeryReportData>>();
	
	
	public String chartInfo="";
	public String chartValue="";
	
	private MetricSummeryReportQuery query ;

	public PerformanceDataConvert(ReportTemplate reportTemplate,ReportTemplateDirectory reportTemplateDirectory){
		this.reportTemplate=reportTemplate;
		this.reportTemplateDirectory=reportTemplateDirectory;
		metricDataReportService = (MetricDataReportService) SpringBeanUtil.getObject("MetricDataReportService");
		metricDataService = (MetricDataService)SpringBeanUtil.getObject("metricDataService");
		init();
	}

	
	@Override
	public ReportDirectory getReportDirectory(){	
		query = new MetricSummeryReportQuery();
		query.setTimePeriods(timePeriods);
		query.setInstanceIDes(instanceIDes);
		query.setMetricIDes(metricIDes);
		
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
		//汇总数据
		Chapter chapterSum=new Chapter();
		chapterSum.setName("汇总数据");
		chapterSum.setSort("1");
		List<Table> tables=new ArrayList<Table>();
		tables.add(getSumTable());
		List<Chart> charts=getSumCharts();
		chapterSum.setTable(tables);
		chapterSum.setChart(charts);
		chapters.add(chapterSum);
		
		if(isDetail){
			//详细数据
			Chapter chapterDetail=new Chapter();
			chapterDetail.setName("详细数据");
			chapterDetail.setSort("1");
			chapterDetail.setTable(getDetailTables());
			chapterDetail.setChart(getDetailCharts());
			chapters.add(chapterDetail);
		}
		
		return chapters;
	}
	
	

	/**
	 * 汇总数据Table
	 * @return
	 */
	public Table getSumTable(){
		ColumnsTitle title=this.getColumnsTitle(false);
		ColumnsData columnsData=this.getSumColumnsData();
		
		Table sumTable=new Table();
		sumTable.setName("汇总数据");
		sumTable.setColumnsTitle(title);
		sumTable.setColumnsData(columnsData);

		return sumTable;
	}
	
	/**
	 * 详细数据Table
	 * @return
	 */
	public List<Table> getDetailTables(){
		List<Table> tables=new ArrayList<Table>();
		
		ColumnsTitle title=this.getColumnsTitle(true);
		Map<Long,ColumnsData> columnsDataMap=getDetailColumnsDataMap();
		for(ReportTemplateDirectoryInstance instance:instanceList){
			String tableName="IP地址："+instance.getInstanceIP()+" | 资源名称："+instance.getInstanceName()+" | 资源类型："+instance.getInstanceType();
			Table table=new Table();
			table.setName(tableName);
			table.setColumnsTitle(title);
			ColumnsData columnsData=columnsDataMap.get(instance.getInstanceId());
			if(columnsData==null){
				columnsData=new ColumnsData();
				columnsData.setTableData(getNullDetalTableDatas((metricList.size()*3)+1));
			}
			table.setColumnsData(columnsData);
			tables.add(table);
		}	

		return tables;
	}
	
	/**
	 * 获取汇总表格数据
	 * @return
	 */
	public ColumnsData getSumColumnsData(){
		List<InstanceMetricSummeryReportData> instanceMetricSummeryReportDatas=new ArrayList<InstanceMetricSummeryReportData>();
		
		//汇总数据
		MetricSummeryReportQuery sumQuery=new MetricSummeryReportQuery();
		sumQuery.setInstanceIDes(instanceIDes);
		sumQuery.setMetricIDes(metricIDes);
		sumQuery.setTimePeriods(timePeriods);
		instanceMetricSummeryReportDatas=metricDataReportService.findHistorySummaryData(sumQuery);

		ColumnsData columnsData=new ColumnsData();
		if(instanceMetricSummeryReportDatas==null || instanceMetricSummeryReportDatas.size()<=0){
			columnsData.setTableData(getNullTableDatas(metricList.size()*3));
		}else{
			columnsData.setTableData(getSumTableDatas(instanceMetricSummeryReportDatas));
		}

		return columnsData;
	}
	
	/**
	 * 按资源 ID 分组，获取资源详细数据
	 * @return
	 */
	public Map<Long,ColumnsData> getDetailColumnsDataMap(){
		
		Map<Long,ColumnsData> columnsDataMap=new HashMap<Long, ColumnsData>();
		
		if(cycle==1){//日报 24点 数据
			if(begin.endsWith(".5") || end.endsWith(".5")){
				query.setSummaryType(MetricSummaryType.HH);
				chartInfo=this.DAY_CHART_INFO_2;
				chartValue=this.DAY_CHART_VALUE_2;
			}else{
				query.setSummaryType(MetricSummaryType.H);
				chartInfo=this.DAY_CHART_INFO_1;
				chartValue=this.DAY_CHART_VALUE_1;
			}
			
		}else if(cycle==2){//周报 
			chartInfo=this.WEEK_CHART_INFO;
			chartValue=this.WEEK_CHART_VALUE;
			query.setSummaryType(MetricSummaryType.SH);
		}else if(cycle==3){//月报 31点数据
			chartInfo=this.MONTH_CHART_INFO;
			chartValue=this.MONTH_CHART_VALUE;
			query.setSummaryType(MetricSummaryType.D);
		}
		
		List<InstanceMetricSummeryReportData> instanceMetricSummeryReportDatas=metricDataReportService.findInstanceHistorySummaryData(this.query);
		if(instanceMetricSummeryReportDatas==null || instanceMetricSummeryReportDatas.size()<=0){
			logger.warn("Can't found detail data.");
		}else{
			
			DecimalFormat df = new DecimalFormat("0.00");
			
			for(ReportTemplateDirectoryInstance instance:instanceList){

				List<InstanceMetricSummeryReportData> subList=new ArrayList<InstanceMetricSummeryReportData>();
				
				for(InstanceMetricSummeryReportData instanceData:instanceMetricSummeryReportDatas){
					if(instanceData.getInstanceID()==instance.getInstanceId()){
						subList.add(instanceData);
					}
				}
				//按时间排序
				Collections.sort(subList,new Comparator<InstanceMetricSummeryReportData>(){
					@Override
					public int compare(InstanceMetricSummeryReportData d1,InstanceMetricSummeryReportData d2) {
						return d1.getEndTime().compareTo(d2.getEndTime());
					}
				});
				List<TableData> tableDatas=new ArrayList<TableData>();
				int cnt=1;
				for(InstanceMetricSummeryReportData instanceDetailData:subList){
					TableData tableData=new TableData();

					List<MetricSummeryReportData> metricSummeryReportDatas=instanceDetailData.getMetricData();
					Map<String,MetricSummeryReportData> map=new HashMap<String,MetricSummeryReportData>();
					for(MetricSummeryReportData metricData:metricSummeryReportDatas){
						map.put(metricData.getMetricID(), metricData);
					}
					StringBuilder value=new StringBuilder();
					value.append(cnt);
					value.append(SEPARATOR);
					for(ReportTemplateDirectoryMetric metric: metricList){
						MetricSummeryReportData data=map.get(metric.getMetricId());
						if(data!=null){
							
							String unit = metric.getMetricUnit();
							if(unit != null && unit.equals("bps")){
								
								DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); 
								
								value.append(data.getAvg() == null ? "null" : (decimalFormat.format(data.getAvg() / (1000D * 1000D))));
								value.append(SEPARATOR);
								value.append(data.getMax() == null ? "null" : (decimalFormat.format(data.getMax() / (1000D * 1000D))));
								value.append(SEPARATOR);
								value.append(data.getMin() == null ? "null" : (decimalFormat.format(data.getMin() / (1000D * 1000D))));
								value.append(SEPARATOR);
								
							}else if(unit != null && unit.equals("包/秒")){
								
								DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); 
								
								value.append(data.getAvg() == null ? "null" : (decimalFormat.format(data.getAvg())));
								value.append(SEPARATOR);
								value.append(data.getMax() == null ? "null" : (decimalFormat.format(data.getMax())));
								value.append(SEPARATOR);
								value.append(data.getMin() == null ? "null" : (decimalFormat.format(data.getMin())));
								value.append(SEPARATOR);
								
							}else{
								value.append(data.getAvg() == null ? "null" : df.format(data.getAvg()));
								value.append(SEPARATOR);
								value.append(data.getMax() == null ? "null" : df.format(data.getMax()));
								value.append(SEPARATOR);
								value.append(data.getMin() == null ? "null" : df.format(data.getMin()));
								value.append(SEPARATOR);
							}
							
						}else{
							value.append("null");
							value.append(SEPARATOR);
							value.append("null");
							value.append(SEPARATOR);
							value.append("null");
							value.append(SEPARATOR);
						}

					}
					String time=simpleDateFormat.format(instanceDetailData.getEndTime());
					value.append(time);
					tableData.setValue(value.toString());
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
	
	
	public List<Chart> getSumCharts(){
		List<Chart> charts=new ArrayList<Chart>();
		
		for(ReportTemplateDirectoryMetric metric:metricList){
			String metricId=metric.getMetricId();
			String metricName=metric.getMetricName();
			MetricTypeEnum metricType=metric.getMetricType();

			if(metricType!=null && metricType.equals(MetricTypeEnum.PerformanceMetric)){
				Chart chart=new Chart();
				chart.setName(metricName);
				chart.setType("1");
				
				MetricDataTopQuery metricDataTopQuery=new MetricDataTopQuery();
				metricDataTopQuery.setTimePeriods(timePeriods);
				metricDataTopQuery.setMetricID(metricId);
				metricDataTopQuery.setInstanceIDes(instanceIDes);
				metricDataTopQuery.setOrderByMax(true);
				
				//获取指标的柱状图数据
				List<MetricSummeryReportData> metricSummeryReportDatas=metricDataReportService.findTopSummaryData(metricDataTopQuery);
				List<ChartData> chartDatas=new ArrayList<ChartData>();
				
				if(metricSummeryReportDatas==null || metricSummeryReportDatas.size()<=0){
					for(ReportTemplateDirectoryInstance instance:instanceList){
						ChartData chartData=new ChartData();
						chartData.setIp(instance.getInstanceIP());
						chartData.setName(instance.getInstanceName());
						chartData.setValue("null");
						chartDatas.add(chartData);
					}
				}else{
					for(MetricSummeryReportData metricSummeryReportData:metricSummeryReportDatas){

						Float avg=metricSummeryReportData.getAvg();
						long instanceID=metricSummeryReportData.getInstanceID();

						ReportTemplateDirectoryInstance instance=instanceMap.get(instanceID);
						
						ChartData chartData=new ChartData();
						chartData.setIp(instance.getInstanceIP());
						chartData.setName(instance.getInstanceName());
						chartData.setValue(avg+"");
						
						chartDatas.add(chartData);
					}
				}

				chart.setChartData(chartDatas);
				charts.add(chart);
			}

		}
		
		return charts;
	}
	
	public List<Chart> getDetailCharts(){
		
		if(!isInstantQuery){
			//不是即时查询，设置时间
			
			List<TimePeriod> myTimePeriods=new ArrayList<TimePeriod>();
			TimePeriod myTimePeriod=new TimePeriod();

			String beginTime = reportTemplate.getReportTemplateBeginTime();
			String endTime = reportTemplate.getReportTemplateEndTime();
			if(cycle == 1){
				//日报
				Calendar calendar = Calendar.getInstance();
				if(reportTemplate.getReportTemplateFirstGenerateTime() == 1){
					 //次日生成,查询范围为前一天
					 calendar.add(Calendar.DATE,-1);
				}
				if(beginTime.endsWith(".5")){
					beginTime = beginTime.replace(".5", "");
					calendar.set(Calendar.MINUTE, 30);
				}else{
					calendar.set(Calendar.MINUTE, 0);
				}
				calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(beginTime));
				calendar.set(Calendar.SECOND, 0);
				
				myTimePeriod.setStartTime(calendar.getTime());
				
				if(endTime.endsWith(".5")){
					endTime = endTime.replace(".5", "");
					calendar.set(Calendar.MINUTE, 30);
				}else{
					calendar.set(Calendar.MINUTE, 0);
				}
				calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime));
				calendar.set(Calendar.SECOND, 0);
				
				myTimePeriod.setEndTime(calendar.getTime());
				
			}else if(cycle == 2){
				//周报
				Calendar calendar = Calendar.getInstance();
				if(reportTemplate.getReportTemplateFirstGenerateTime() == 1){
					 //下周生成,查询范围为前一周
					 calendar.add(Calendar.WEEK_OF_YEAR,-1);
				}
				
				int beginWeek = -1;
				int endWeek = -1;
				if(beginTime.contains(",")){
					//多天
					beginWeek = Integer.parseInt(beginTime.split(",")[0]);
					endWeek = Integer.parseInt(beginTime.split(",")[beginTime.split(",").length - 1]);
					
				}else{
					//单天
					beginWeek = Integer.parseInt(beginTime);
					endWeek = Integer.parseInt(beginTime);
					
				}
				
				//切换为Calendar周值
				if(beginWeek == 7){
					beginWeek = 1;
				}else{
					beginWeek++;
				}
				
				if(endWeek == 7){
					endWeek = 1;
				}else{
					endWeek++;
				}
				
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.DAY_OF_WEEK, beginWeek);
				
				myTimePeriod.setStartTime(calendar.getTime());
				
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
				calendar.set(Calendar.DAY_OF_WEEK, endWeek);
				
				myTimePeriod.setEndTime(calendar.getTime());
				
			}else if(cycle == 3){
				//月报
				Calendar calendar = Calendar.getInstance();
				if(reportTemplate.getReportTemplateFirstGenerateTime() == 1){
					 //次月生成,查询范围为前一月
					 calendar.add(Calendar.MONTH,-1);
				}
				
				calendar.set(Calendar.HOUR_OF_DAY,0);
				calendar.set(Calendar.MINUTE,0);
				calendar.set(Calendar.SECOND,0);
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(beginTime));
				
				myTimePeriod.setStartTime(calendar.getTime());
				
				calendar.set(Calendar.HOUR_OF_DAY,23);
				calendar.set(Calendar.MINUTE,59);
				calendar.set(Calendar.SECOND,59);
				if(endTime.equals("-1")){
					//最后一天
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				}else{
					calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endTime));
				}
				myTimePeriod.setEndTime(calendar.getTime());
				
			}
			
			myTimePeriods.add(myTimePeriod);
			query.setTimePeriods(myTimePeriods);
			
		}
		
		List<Chart> charts=new ArrayList<Chart>();
	
		Map<String, Map<Long, List<MetricSummeryReportData>>> rMetricDataMap=new HashMap<String, Map<Long,List<MetricSummeryReportData>>>();

		//判断查询范围间距
		long timeScopeDistance = query.getTimePeriods().get(0).getEndTime().getTime() - query.getTimePeriods().get(0).getStartTime().getTime();
		
		//记录查询汇总数据还是详情数据
		boolean isSummaryQuery = true;
		
		DateFormat parseDateTime = null;
		if(timeScopeDistance <= FIVE_MINUTES_BORDER){
			parseDateTime = new SimpleDateFormat("HH:mm");
			isSummaryQuery = false;
			//时间范围选择小于2小时，数据点为5分钟数据（具体的采值周期）
		}else if(timeScopeDistance <= HALF_HOUR_BORDER){
			//时间范围选择小于12小时，数据点为半小时数据
			parseDateTime = new SimpleDateFormat("HH:mm");
			query.setSummaryType(MetricSummaryType.HH);
		}else if(timeScopeDistance <= ONE_HOUR_BORDER){ 
			//时间范围选择小于24小时，数据点为1小时数据
			parseDateTime = new SimpleDateFormat("HH:mm");
			query.setSummaryType(MetricSummaryType.H);
		}else if(timeScopeDistance <= SIX_HOUR_BORDER){
			//时间范围选择小于1周，数据点为6小时数据
			parseDateTime = new SimpleDateFormat("MM-dd HH:mm");
			query.setSummaryType(MetricSummaryType.SH);
		}else if(timeScopeDistance > SIX_HOUR_BORDER){
			//时间范围选择大于1周，数据点为1天数据
			parseDateTime = new SimpleDateFormat("yyyy-MM-dd");
			query.setSummaryType(MetricSummaryType.D);
		}
		
		
		if(isSummaryQuery){
			
			//查询汇总数据
			rMetricDataMap=metricDataReportService.findInstanceMetricHistoryGroupByMetricID(query);

			if(rMetricDataMap==null || rMetricDataMap.isEmpty()){
				for(ReportTemplateDirectoryMetric metric:metricList){
					Chart chart=new Chart();
					chart.setName(metric.getMetricName());
					chart.setType("2");
					chart.setInfo(chartInfo);
					List<ChartData> chartDatas =new ArrayList<ChartData>();

					for(ReportTemplateDirectoryInstance instance:instanceList){
						ChartData chartData=new ChartData();
						chartData.setIp(instance.getInstanceIP());
						chartData.setName(instance.getInstanceName());
						chartData.setValue(chartValue);
						chartDatas.add(chartData);
					}
					chart.setChartData(chartDatas);
					charts.add(chart);
				}

			}else{
				for(ReportTemplateDirectoryMetric metric:metricList){
					String metricId=metric.getMetricId();
					Chart chart=new Chart();
					chart.setName(metric.getMetricName());
					chart.setType("2");
					
					StringBuilder xPoint = new StringBuilder();
					String xPointValue = null;
					
					List<ChartData> chartDatas =new ArrayList<ChartData>();	
					MetricTypeEnum metricType=metric.getMetricType();
					if(metricType!=null && metricType.equals(MetricTypeEnum.PerformanceMetric)){
						Map<Long, List<MetricSummeryReportData>> sumMap=rMetricDataMap.get(metricId);
						for(ReportTemplateDirectoryInstance instance:instanceList){
							List<MetricSummeryReportData> metricDatas=new ArrayList<MetricSummeryReportData>();
							if(sumMap!=null){
								 metricDatas=sumMap.get(instance.getInstanceId());
							}else{
								logger.error("SumMap is null , metricId = " + metricId);
							}
							
							if(metricDatas == null || metricDatas.size() <= 0){
								logger.error("MetricDatas is null , instanceId = " + instance.getInstanceId());
							}else{
								//按时间排序
								Collections.sort(metricDatas,new Comparator<MetricSummeryReportData>(){
									@Override
									public int compare(MetricSummeryReportData md1,MetricSummeryReportData md2) {
										// TODO Auto-generated method stub
										return md1.getEndTime().compareTo(md2.getEndTime());
									}
								});
								
								if(xPointValue == null || xPointValue.equals("")){
									for(MetricSummeryReportData data : metricDatas){
										xPoint.append(parseDateTime.format(data.getEndTime()));
										xPoint.append(",");
									}
									xPointValue = xPoint.substring(0, xPoint.length() - 1);
								}
							}
							
							ChartData chartData=new ChartData();
							chartData.setIp(instance.getInstanceIP());
							chartData.setName(instance.getInstanceName());
							chartData.setValue(getChartDataValue(metricDatas));
							chartDatas.add(chartData);
						}
						
						chart.setChartData(chartDatas);
						chart.setInfo(xPointValue);
						charts.add(chart);
					}
				}
				
			}
			
		}else{
			
			//查询详情数据，即采集频度点
			for(ReportTemplateDirectoryMetric metric:metricList){
				
				MetricTypeEnum metricType=metric.getMetricType();
				
				Chart chart=new Chart();
				chart.setName(metric.getMetricName());
				chart.setType("2");
				
				if(metricType!=null && metricType.equals(MetricTypeEnum.PerformanceMetric)){
					
					StringBuilder xPoint = new StringBuilder();
					String xPointValue = null;
					List<ChartData> chartDatas =new ArrayList<ChartData>();	
					for(ReportTemplateDirectoryInstance instance:instanceList){
						
						//查询详细数据
					    List<MetricData> metricDatas = metricDataService.queryHistoryMetricData(metric.getMetricId(), instance.getInstanceId(), 
								query.getTimePeriods().get(0).getStartTime(), 
								query.getTimePeriods().get(0).getEndTime());
					    
					    StringBuilder chartValue = new StringBuilder();
					    String chartValueString = "";
					    
					    
					    for(MetricData data : metricDatas){
					    	
					    	//获取值字符串
					    	if(data.getData() == null || data.getData().length <= 0){
					    		chartValue.append("null");
					    	}else{
					    		chartValue.append(data.getData()[0]);
					    	}
					    	chartValue.append(SEPARATOR);
					    	
					    	xPoint.append(parseDateTime.format(data.getCollectTime()));
							xPoint.append(",");
							xPointValue = xPoint.substring(0, xPoint.length() - 1);
					    	
					    }
					    
					    chartValueString = checkSeparator(chartValue);
					    
						ChartData chartData=new ChartData();
						chartData.setIp(instance.getInstanceIP());
						chartData.setName(instance.getInstanceName());
						chartData.setValue(chartValueString);
						chartDatas.add(chartData);
						
					}
					
					chart.setChartData(chartDatas);
					chart.setInfo(xPointValue);
					charts.add(chart);
				}
				
			}
			
		}
		
		return charts;
	
	}
	
	
	/**
	 * 获取表格头
	 * @return
	 */
	private ColumnsTitle getColumnsTitle(boolean detail){
		ColumnsTitle title=new ColumnsTitle();
		List<Columns> columns=new ArrayList<Columns> ();
		columns.add(new Columns("序列号"));
		if(!detail){
			columns.add(new Columns("IP地址"));
			columns.add(new Columns("资源名称"));
			columns.add(new Columns("资源类型"));
		}
		for(ReportTemplateDirectoryMetric metric:metricList){
			if(metric.getMetricUnit() != null && metric.getMetricUnit().equals("bps")){
				columns.add(new Columns(metric.getMetricName().replace("bps", "MBps"),"平均值,最大值,最小值"));
			}else{
				columns.add(new Columns(metric.getMetricName(),"平均值,最大值,最小值"));
			}
		}
		if(detail){
			columns.add(new Columns("时间"));
		}
		title.setColumns(columns);
		return title;
	}
	
	public String getTimeInfo(){
		StringBuilder timeInfoSB=new StringBuilder();
		
		Calendar calendar=Calendar.getInstance();
		if(this.cycle==1){
			calendar.setTime(startTime);
			int startDay=calendar.get(Calendar.DAY_OF_MONTH);
			int startHour=calendar.get(Calendar.HOUR_OF_DAY);
			
			calendar.setTime(endTime);
			int endDay=calendar.get(Calendar.DAY_OF_MONTH);
			int endHour=calendar.get(Calendar.HOUR_OF_DAY);
			
			if(startDay==endDay){
				for(int i=startHour;i<=endHour;i++){
					timeInfoSB.append(i+":00");
					timeInfoSB.append(SEPARATOR);
				}
			}else{
				for(int i=startHour;i<24;i++){
					timeInfoSB.append(i+":00");
					timeInfoSB.append(SEPARATOR);
				}
				timeInfoSB.append("次日 0:00");
				timeInfoSB.append(SEPARATOR);
				for(int i=1;i<=endHour;i++){
					timeInfoSB.append(i+":00");
					timeInfoSB.append(SEPARATOR);
				}
			}
			
		}else{
			calendar.setTime(endTime);
			int endDay=calendar.get(Calendar.DAY_OF_YEAR);
			calendar.setTime(startTime);
			int startDay=calendar.get(Calendar.DAY_OF_YEAR);
			for(int i=startDay;i<=endDay;i++){
				calendar.set(Calendar.DAY_OF_YEAR, i);
				int day=calendar.get(Calendar.DAY_OF_MONTH);
				if(day==1){
					int month=calendar.get(Calendar.MONTH);
					timeInfoSB.append(month+1+"月 1");
					timeInfoSB.append(SEPARATOR);
				}else{
					timeInfoSB.append(day);
					timeInfoSB.append(SEPARATOR);
				}
			}

		}

		return checkSeparator(timeInfoSB);
	}
	


	public String getChartDataValue(List<MetricSummeryReportData> metricDatas){
		
//		List<Integer> cntPoint=new ArrayList<Integer>();
		StringBuilder sb=new StringBuilder();
		
		if(metricDatas!=null && metricDatas.size()>0){
//			if(cycle==2){
//				String[] weeks=begin.split(",");
//				for(String week: weeks){
//					int weekCnt=Integer.parseInt(week)*4;
//					cntPoint.add(weekCnt);
//					cntPoint.add(--weekCnt);
//					cntPoint.add(--weekCnt);
//					cntPoint.add(--weekCnt);
//				}
//			}else{
//				int startCnt=-1;
//				int endCnt=-1;
//				if(begin.endsWith(".5") || end.endsWith(".5")){
//					Double startCntD=Double.valueOf(begin)/0.5;
//					Double endCntD=Double.valueOf(end)/0.5;
//					startCnt=startCntD.intValue();
//					endCnt=endCntD.intValue();
//				}else{
//					startCnt=Integer.parseInt(begin);
//					endCnt=Integer.parseInt(end);
//				}
//
//				for(int i=startCnt;i<=endCnt;i++){
//					cntPoint.add(i);
//				}
//			}
//			
//			for(int i=0;i<metricDatas.size();i++){
//				MetricSummeryReportData metricData=metricDatas.get(i);
//				if(cntPoint.contains(i)){
//					sb.append(metricData.getAvg());
//					sb.append(SEPARATOR);
//				}else{
//					sb.append("null");
//					sb.append(SEPARATOR);
//				}
//			}
			
			DecimalFormat df = new DecimalFormat("0.00");
			
			for(int i=0;i<metricDatas.size();i++){
				MetricSummeryReportData metricData=metricDatas.get(i);
				sb.append(metricData.getAvg() == null ? "null" : df.format(metricData.getAvg()));
				sb.append(SEPARATOR);
			}

		}
		
		return this.checkSeparator(sb);

	}
	

	
	public List<TableData> getSumTableDatas(List<InstanceMetricSummeryReportData> instanceMetricSummeryReportDatas){
		List<TableData> tableDatas=new ArrayList<TableData>();
		
		//资源的指标数据Map
		this.metricDataMap=new HashMap<String,Map<String,MetricSummeryReportData>>();
		for(InstanceMetricSummeryReportData reportSumData:instanceMetricSummeryReportDatas){
			long instanceId=reportSumData.getInstanceID();
			List<MetricSummeryReportData> instanceMetricDataList=reportSumData.getMetricData();
			Map<String,MetricSummeryReportData> instanceMetricDataMap=new HashMap<String,MetricSummeryReportData>();
			for(MetricSummeryReportData instanceMetricData:instanceMetricDataList){
				instanceMetricDataMap.put(instanceMetricData.getMetricID(), instanceMetricData);
			}
			metricDataMap.put(String.valueOf(instanceId), instanceMetricDataMap);			
		}
		
		DecimalFormat df = new DecimalFormat("0.00");
		
		int cnt=1;
		for(ReportTemplateDirectoryInstance instance:instanceList){
			TableData tableData=new TableData();
			
			StringBuilder value=new StringBuilder();
			value.append(cnt);
			value.append(SEPARATOR);
			value.append(instance.getInstanceIP());
			value.append(SEPARATOR);
			value.append(instance.getInstanceName());
			value.append(SEPARATOR);
			value.append(instance.getInstanceType());
			value.append(SEPARATOR);
			
			Map<String,MetricSummeryReportData> map=metricDataMap.get(String.valueOf(instance.getInstanceId()));
			for(ReportTemplateDirectoryMetric metric: metricList){
				String metricId=metric.getMetricId();

				MetricSummeryReportData data=null;

				if(map!=null){
					data=map.get(metricId);
					if(data==null){
						logger.error("Can't found Metric Data--"+metricId);
						value.append("null");
						value.append(SEPARATOR);
						value.append("null");
						value.append(SEPARATOR);
						value.append("null");
						value.append(SEPARATOR);
						continue;
					}
					try {
						
						String unit = metric.getMetricUnit();
						if(unit != null && unit.equals("bps")){
							
							DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); 
							
							value.append((decimalFormat.format(data.getAvg() / (1000D * 1000D))));
							value.append(SEPARATOR);
							value.append((decimalFormat.format(data.getMax() / (1000D * 1000D))));
							value.append(SEPARATOR);
							value.append((decimalFormat.format(data.getMin() / (1000D * 1000D))));
							value.append(SEPARATOR);
						}else if(unit != null && unit.equals("包/秒")){
							
							DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); 
							
							value.append(data.getAvg() == null ? "null" : (decimalFormat.format(data.getAvg())));
							value.append(SEPARATOR);
							value.append(data.getMax() == null ? "null" : (decimalFormat.format(data.getMax())));
							value.append(SEPARATOR);
							value.append(data.getMin() == null ? "null" : (decimalFormat.format(data.getMin())));
							value.append(SEPARATOR);
							
						}else{
							value.append(df.format(data.getAvg()));
							value.append(SEPARATOR);
							value.append(df.format(data.getMax()));
							value.append(SEPARATOR);
							value.append(df.format(data.getMin()));
							value.append(SEPARATOR);
						}
						
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						//data内取值为空或其他异常
						value.append("null");
						value.append(SEPARATOR);
						value.append("null");
						value.append(SEPARATOR);
						value.append("null");
						value.append(SEPARATOR);
					}
				}else{
					value.append("null");
					value.append(SEPARATOR);
					value.append("null");
					value.append(SEPARATOR);
					value.append("null");
					value.append(SEPARATOR);
				}
			}

			tableData.setValue(checkSeparator(value));
			tableDatas.add(tableData);
			cnt++;
		}
		
		return tableDatas;
	}
	
	
	public static void main(String[] args) {
		System.out.println("s,".substring(0, "s,".length()-1));

	}
	
	
}
