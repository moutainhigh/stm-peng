package com.mainsteam.stm.portal.report.convert;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.alarm.event.report.InstanceAlarmEventReportService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.report.AvailableMetricDataReportService;
import com.mainsteam.stm.common.metric.report.MetricDataReportService;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricWithTypeForReport;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ReportDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryInstance;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryMetric;
import com.mainsteam.stm.portal.report.bo.ReportTypeEnum;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.util.SpringBeanUtil;

public abstract class BaseDataConvert {
	
	private final Log logger = LogFactory.getLog(BaseDataConvert.class);
	
	public ReportTemplate reportTemplate;
	public ReportTemplateDirectory reportTemplateDirectory;

	//报表名称
	public	String reportName;
	//模板类型,1.性能报告2.告警统计3.TOPN报告4.可用性报告5.综合性报告
	public int reportType;
	//报表周期：1.日报2.周报3.月报
	public int cycle;
	
	public Calendar calendar=Calendar.getInstance();
	//开始 时间计数
	public String begin="";
	//结束 时间计数
	public String end="";
	//报表统计 开始时间
	public Date startTime;
	//报表统计 结束时间
	public Date endTime;
	
	//记录是否即时查询
	public boolean isInstantQuery=false;
	
	//周 的勾选时间
	Map<Integer,Date> weekDateMap=new HashMap<Integer, Date>();
	
	List<TimePeriod> timePeriods=new ArrayList<TimePeriod>();
	
	DecimalFormat decimalFormat = new DecimalFormat("##.##");
	
	//第一生成时间:0.当前1.下一个 周期
	private static boolean isNext=false;
	//报表时间区间
	public String timeScope;
	
	//目录名称
	public String reportDirectoryName;
	//是否显示详细数据
	public boolean isDetail=false;

	//资源ID 查询条件
	public List<Long> instanceIDes=new ArrayList<Long>();
	//分析报表查询条件
	public List<String> metricIds=new ArrayList<String>();
	//指标  查询条件
	List<MetricWithTypeForReport> metricIDes=new ArrayList<MetricWithTypeForReport>();
	//资源数量 和 排序
	public List<ReportTemplateDirectoryInstance> instanceList;
	//包含了资源的基本属性，通过Instance ID 获取资源基本属性
	public Map<Long,ReportTemplateDirectoryInstance> instanceMap;
	
	//指标数量和排序
	public List<ReportTemplateDirectoryMetric> metricList;
	//包含了指标的基本属性，通过 metric ID获取资源基本属性
	public Map<String,ReportTemplateDirectoryMetric> metricMap;
	//指标报表数据
	public Map<String,Map<String,MetricSummeryReportData>> metricDataMap;
	
	//分隔符#--!!--#
	public static final String SEPARATOR="#--!!--#";
	//日期格式化
	public static final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public final String DAY_CHART_INFO_1="00:00,01:00,02:00,03:00,04:00,05:00,06:00,07:00,08:00,09:00,10:00,11:00,12:00,13:00,14:00,15:00,16:00,17:00,18:00,19:00,20:00,21:00,22:00,23:00,24:00";
	public final String DAY_CHART_INFO_2="01:00, ,02:00, ,03:00, ,04:00, ,05:00, ,06:00, ,07:00, ,08:00, ,09:00, ,10:00, ,11:00, ,12:00, ,13:00, ,14:00, ,15:00, ,16:00, ,17:00,  ,18:00, ,19:00, ,20:00, ,21:00, ,22:00, ,23:00, ,24:00";
	
	public final String DAY_CHART_VALUE_1="null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null";
	public final String DAY_CHART_VALUE_2="null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null";
	
	public final String WEEK_CHART_INFO="周一,6,12,18,周二,6,12,18,周三,6,12,18,周四,6,12,18,周五,6,12,18,周六,6,12,18,周日,6,12,18";
	public final String WEEK_CHART_VALUE="null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null";
	public final String MONTH_CHART_INFO="01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31";
	public final String MONTH_CHART_VALUE="null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null";
	
	//5分钟间隔边界
	public final long FIVE_MINUTES_BORDER = 2 * 60 * 60 * 1000;
	
	//半小时间隔边界
	public final long HALF_HOUR_BORDER = 12 * 60 * 60 * 1000;
	
	//一小时时间边界
	public final long ONE_HOUR_BORDER = 24 * 60 * 60 * 1000;
	
	//6小时时间边界
	public final long SIX_HOUR_BORDER = 7 * 24 * 60 * 60 * 1000;
	
	
	public InstanceAlarmEventReportService instanceAlarmEventReportService;
	public MetricDataReportService metricDataReportService;
	public MetricDataService metricDataService;
	public AvailableMetricDataReportService availableReportService;
	public ResourceInstanceService resourceInstanceService;

	
	public void init(){
		
		resourceInstanceService = (ResourceInstanceService) SpringBeanUtil.getObject("resourceInstanceService");
		BizMainApi bizMainApi = (BizMainApi) SpringBeanUtil.getObject("bizMainApi");
		
		this.reportType=reportTemplate.getReportTemplateType();
		this.cycle=reportTemplate.getReportTemplateCycle();
		this.begin=reportTemplate.getReportTemplateBeginTime();
		this.end=reportTemplate.getReportTemplateEndTime();
		this.reportName=reportTemplate.getReportTemplateName();
		int state=reportTemplate.getReportTemplateFirstGenerateTime();
		isNext=(state==1);
		
		if(reportType!=ReportTypeEnum.ANALYSIS_REPORT.getIndex() 
				&& reportType!=ReportTypeEnum.TREND_REPORT.getIndex()){
			//日报，月报
			if(cycle!=2){
				this.startTime = getReportDateTime(begin,false);
				this.endTime = getReportDateTime(end, true);
				
				TimePeriod timePeriod=new TimePeriod();
				timePeriod.setStartTime(startTime);
				timePeriod.setEndTime(endTime);
				this.timePeriods.add(timePeriod);
				
			}else{
				String[] weeks=begin.split(",");
				this.getWeekTimes(weeks);	
			}
		}

		this.reportDirectoryName=reportTemplateDirectory.getReportTemplateDirectoryName();
		this.instanceList=reportTemplateDirectory.getDirectoryInstanceList();
		
		if(this.instanceList == null || this.instanceList.size() <= 0){
			logger.error("InstanceList is empty , directory id : " + reportTemplateDirectory.getReportTemplateDirectoryId());
		}
		
		this.metricList=reportTemplateDirectory.getDirectoryMetricList();
		
		this.isDetail=reportTemplateDirectory.getReportTemplateDirectoryIsDetail()==0;
		
		List<ReportTemplateDirectoryInstance> resultInstance = new ArrayList<ReportTemplateDirectoryInstance>();
		
		//资源map
		this.instanceMap = new HashMap<Long,ReportTemplateDirectoryInstance>();
		for(ReportTemplateDirectoryInstance instance:instanceList){
			
			if(this.reportType == ReportTypeEnum.BUSINESS_REPORT.getIndex()){
				instance.setInstanceName(bizMainApi.getBasicInfo(instance.getInstanceId()).getName());
			}else{
			
				ResourceInstance instanceServer = null;
				try {
					if(instance == null){
						continue;
					}
					instanceServer = resourceInstanceService.getResourceInstance(instance.getInstanceId());
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
				
				if(instanceServer == null){
					continue;
				}
				
				instance.setInstanceIP(instanceServer.getShowIP());
				
				//优先显示显示名称
				if(instanceServer.getShowName() != null && !(instanceServer.getShowName().equals(""))){
					instance.setInstanceName(instanceServer.getShowName());
				}else{
					instance.setInstanceName(instanceServer.getName());
				}
			
			}
			
			this.instanceIDes.add(instance.getInstanceId());
			resultInstance.add(instance);
			instanceMap.put(instance.getInstanceId(), instance);
		}
		
		this.instanceList = resultInstance;
		
		//指标map
		this.metricMap=new HashMap<String,ReportTemplateDirectoryMetric>();
		for(ReportTemplateDirectoryMetric rtdMetric:metricList){
			String metricId = rtdMetric.getMetricId();
			MetricTypeEnum metricType=rtdMetric.getMetricType();
			if(metricType!=null){
				this.metricIDes.add(new MetricWithTypeForReport(metricId,MetricTypeEnum.PerformanceMetric));
			}
			metricIds.add(metricId);
			metricMap.put(metricId, rtdMetric);
		}

	}
	
	/**
	 * 获取 ReportDirectory 核心方法
	 * @return
	 */
	public abstract ReportDirectory getReportDirectory();
	
	/**
	 * getTimeScope
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String getTimeScope(){
		String startTimeStr=null;
		String endTimeStr=null;
		if(reportType!=ReportTypeEnum.ANALYSIS_REPORT.getIndex() 
				&& reportType!=ReportTypeEnum.TREND_REPORT.getIndex()){
			if(cycle==2){
				return "周:"+begin;
			}
			startTimeStr= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime);
			endTimeStr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime);
			
		}
		if(timePeriods!=null && timePeriods.size()>0){
			TimePeriod timePeriod=timePeriods.get(0);
			startTimeStr= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timePeriod.getStartTime());
			endTimeStr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timePeriod.getEndTime());
		}
		return startTimeStr+"---"+endTimeStr;
	}
	
	/**
	 * getReportDateTime
	 * @param time
	 * @param isEnd
	 * @return
	 */
	public  Date getReportDateTime(String time,boolean isEnd){
		
		calendar.setTimeInMillis(System.currentTimeMillis());
		//报表周期：1.日报2.周报3.月报
		if(cycle==1){
			//0,当天， 1.次日
			if(isNext){
			 calendar.add(Calendar.DATE,-1);
			}
			if(time.endsWith(".5")){
				calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time.split("\\.")[0]));
				calendar.set(Calendar.MINUTE,30);
			}else{
				calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time));
				calendar.set(Calendar.MINUTE,0);
			}
			calendar.set(Calendar.SECOND,0);

		}else if(cycle==3){
			if(isNext){
				calendar.add(Calendar.MONTH,-1);
			}
			//last
			if(time.equals("-1")){
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			}else{
				calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(time));
			}
			
			if(isEnd){
				calendar.set(Calendar.HOUR_OF_DAY,23);
				calendar.set(Calendar.MINUTE,59);
				calendar.set(Calendar.SECOND,59);
			}else{
				calendar.set(Calendar.HOUR_OF_DAY,0);
				calendar.set(Calendar.MINUTE,0);
				calendar.set(Calendar.SECOND,0);
			}
		}

		return calendar.getTime();
	}
	
	public Map<Integer,Date> getWeekTimes(String[] weeks){
		Calendar calendar=Calendar.getInstance();
//		calendar.setTimeInMillis(System.currentTimeMillis());
		if(isNext){
			calendar.add(Calendar.WEEK_OF_MONTH,-1);
		}
		
		for(String week:weeks){
			int time=getWeekCount(Integer.parseInt(week));
			if(time==1){
				calendar.add(Calendar.WEEK_OF_MONTH,1);
			}
			
			int weekCount=getWeekCount(Integer.parseInt(week));
			calendar.set(Calendar.DAY_OF_WEEK,weekCount);

			TimePeriod timePeriod=new TimePeriod();
			timePeriod.setStartTime(getWeekOfDayTime(false,calendar.getTime()));
			timePeriod.setEndTime(getWeekOfDayTime(true,calendar.getTime()));
			this.timePeriods.add(timePeriod);
			
			weekDateMap.put(time, calendar.getTime());			
		}
		
		return weekDateMap;
		
		
	}
	
	/**
	 * get WEEK
	 * @param cnt
	 * @return
	 */
	public static int getWeekCount(int cnt){
		int week=-1;
		switch (cnt) {
		case 1:
			week=Calendar.MONDAY;
			break;
		case 2:
			week=Calendar.TUESDAY;
			break;
		case 3:
			week=Calendar.WEDNESDAY;
			break;
		case 4:
			week=Calendar.THURSDAY;
			break;
		case 5:
			week=Calendar.FRIDAY;
			break;
		case 6:
			week=Calendar.SATURDAY;
			break;
		case 7:
			week=Calendar.SUNDAY;
			break;
		default:
			week=0;
			break;
		}
		return week;
	}

	/**
	 * 序列号表头
	 * @return
	 */
	public Columns getSerialNumberColunm(){
		Columns Column=new Columns();
		Column.setText("序列号");
		return Column;
	}
	
	/**
	 * 时间 表头
	 * @return
	 */
	public Columns getDateTimeColunm(){
		Columns Column=new Columns();
		Column.setText("时间");
		return Column;
	}
	
	public String getArrayString(String[] array){
		String end="";
		if(array!=null){
			StringBuilder sb=new StringBuilder();
			for(String str:array){
				sb.append(str);
				sb.append(SEPARATOR);
			}
			if(sb.lastIndexOf(SEPARATOR)!=-1){
				end=sb.substring(0,sb.lastIndexOf(SEPARATOR));
			}else{
				end=sb.toString();
			}
		}
		return end;
	}
	
	//获取周的天日期
	public Date getWeekOfDayTime(boolean end,Date date){
		calendar.setTime(date);
		if(end){
			calendar.set(Calendar.HOUR_OF_DAY,23);
			calendar.set(Calendar.MINUTE,59);
			calendar.set(Calendar.SECOND,59);
		}else{
			calendar.set(Calendar.HOUR_OF_DAY,0);
			calendar.set(Calendar.MINUTE,0);
			calendar.set(Calendar.SECOND,0);
		}
		return calendar.getTime();
		
	}
	
	public Date[] getDetailDates(){
		Date[] dateArray=new Date[2];
		if(cycle==1){
			//0,当天， 1.次日
			if(isNext){
			 calendar.add(Calendar.DATE,-1);
			}
			calendar.set(Calendar.HOUR_OF_DAY,0);
			calendar.set(Calendar.MINUTE,0);
			calendar.set(Calendar.SECOND,0);
			dateArray[0]=calendar.getTime();
			
			calendar.set(Calendar.HOUR_OF_DAY,24);
			calendar.set(Calendar.MINUTE,00);
			calendar.set(Calendar.SECOND,00);
			dateArray[1]=calendar.getTime();

		}else if(cycle==2){
			if(isNext){
				calendar.add(Calendar.WEEK_OF_YEAR,-1);
			}
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
			dateArray[0]=calendar.getTime();
			
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+6);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			dateArray[1]=calendar.getTime();
		}else if(cycle==3){
			if(isNext){
				calendar.add(Calendar.MONTH,-1);
			}
			//last
			calendar.set(Calendar.HOUR_OF_DAY,0);
			calendar.set(Calendar.MINUTE,0);
			calendar.set(Calendar.SECOND,0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			dateArray[0]=calendar.getTime();
			
			calendar.set(Calendar.HOUR_OF_DAY,23);
			calendar.set(Calendar.MINUTE,59);
			calendar.set(Calendar.SECOND,59);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			dateArray[1]=calendar.getTime();
		}
		return dateArray;
	}

	/**
	 * 填补空返回结果集的表格数据
	 * @param count
	 * @return
	 */
	public List<TableData> getNullTableDatas(int count){
		List<TableData> tableDatas=new ArrayList<TableData>();
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
			
			for(int i=0;i<count;i++){
				value.append("null");
				value.append(SEPARATOR);
			}
			
			tableData.setValue(checkSeparator(value));
			tableDatas.add(tableData);
			cnt++;
		}
		
		return tableDatas;
	}
	
	/**
	 * 获取空的详细数据
	 * @return
	 */
	public List<TableData> getNullDetalTableDatas(int count){
		List<TableData> tableDatas=new ArrayList<TableData>();
		TableData tableData=new TableData();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<count;i++){
			sb.append("null");
			sb.append(SEPARATOR);
		}
		tableData.setValue(this.checkSeparator(sb));
		tableDatas.add(tableData);
		return tableDatas;
	}
	
	/**
	 * 截取最后的分隔符
	 * @param value
	 * @return
	 */
	public String checkSeparator(StringBuilder value){
		if(value.toString().endsWith(SEPARATOR)){
			return value.substring(0, value.length()-SEPARATOR.length());
		}else{
			return value.toString();
		}
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public List<TimePeriod> getTimePeriods() {
		return timePeriods;
	}

	public void setTimePeriods(List<TimePeriod> timePeriods) {
		this.timePeriods = timePeriods;
	}

	public static void main(String[] args) {

	}
	
}
