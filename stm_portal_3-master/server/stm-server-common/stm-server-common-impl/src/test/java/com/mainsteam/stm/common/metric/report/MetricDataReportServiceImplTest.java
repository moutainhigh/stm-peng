package com.mainsteam.stm.common.metric.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.obj.TimePeriod;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/portal-*-beans.xml" })
public class MetricDataReportServiceImplTest {

	@Autowired MetricDataReportService metricDataReportService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	@Test
	public void testQuery(){
		
		MetricSummeryReportQuery query=new MetricSummeryReportQuery();

		Calendar cal=Calendar.getInstance();
		List<TimePeriod> pds=new ArrayList<TimePeriod>();
		query.setTimePeriods(pds);
		
		TimePeriod pd=new TimePeriod();
		pds.add(pd);
		cal.set(Calendar.MONTH, 6);
		pd.setStartTime(cal.getTime());
		cal.set(Calendar.MONTH, 11);
		pd.setEndTime(cal.getTime());
		
		pd=new TimePeriod();
		pds.add(pd);
		pd.setStartTime(cal.getTime());
		pd.setEndTime(new Date());
		
		List<Long> instanceIDes=new ArrayList<>();
		instanceIDes.add(13053l);
		instanceIDes.add(13166l);
		
		query.setInstanceIDes(instanceIDes);
		
		List<MetricWithTypeForReport> metric=new ArrayList<>();
		metric.add(new MetricWithTypeForReport("cpuRate",MetricTypeEnum.PerformanceMetric));
		metric.add(new MetricWithTypeForReport("memRate",MetricTypeEnum.PerformanceMetric));
//		metric.add(new MetricWithTypeForReport("cpuRate",MetricTypeEnum.PerformanceMetric));
//		metric.add(new MetricWithTypeForReport("cacheMissRate",MetricTypeEnum.PerformanceMetric));
//		metric.add(new MetricWithTypeForReport("appCpuRate",MetricTypeEnum.PerformanceMetric));
//		metric.add(new MetricWithTypeForReport("cpuNum",MetricTypeEnum.InformationMetric));
		
		query.setMetricIDes(metric);
		
		System.out.println("result:"+JSON.toJSONString(metricDataReportService.findHistorySummaryData(query)));
	}
	
	@Test
	public void testFindInstanceHistorySummaryData(){
		
		MetricSummeryReportQuery query=new MetricSummeryReportQuery();

		Calendar cal=Calendar.getInstance();
		List<TimePeriod> pds=new ArrayList<TimePeriod>();
		query.setTimePeriods(pds);
		
		TimePeriod pd=new TimePeriod();
		pds.add(pd);
		cal.set(Calendar.MONTH, 8);
		pd.setStartTime(cal.getTime());
		cal.set(Calendar.MONTH, 9);
		pd.setEndTime(cal.getTime());
		
		pd=new TimePeriod();
		pds.add(pd);
		pd.setStartTime(cal.getTime());
		pd.setEndTime(new Date());
		
		List<Long> instanceIDes=new ArrayList<>();
		instanceIDes.add(3643645l);
		instanceIDes.add(1232l);
		instanceIDes.add(333l);
		
		query.setInstanceIDes(instanceIDes);
		
		List<MetricWithTypeForReport> metric=new ArrayList<>();
		metric.add(new MetricWithTypeForReport("connThreadCount",MetricTypeEnum.PerformanceMetric));
		metric.add(new MetricWithTypeForReport("cpuRate",MetricTypeEnum.PerformanceMetric));
		metric.add(new MetricWithTypeForReport("cacheMissRate",MetricTypeEnum.PerformanceMetric));
		metric.add(new MetricWithTypeForReport("appCpuRate",MetricTypeEnum.PerformanceMetric));
		
		query.setMetricIDes(metric);
		
		System.out.println("result:"+JSON.toJSONString(metricDataReportService.findInstanceHistorySummaryData(query)));

	}
	
	@Test
	public void testFindTopnData(){
		
		MetricDataTopQuery query=new MetricDataTopQuery();

		List<TimePeriod> pds=new ArrayList<TimePeriod>();
		query.setTimePeriods(pds);
		
		Calendar cal=Calendar.getInstance();
		
		TimePeriod pd=new TimePeriod();
		pds.add(pd);
		cal.set(Calendar.MONTH, 8);
		pd.setStartTime(cal.getTime());
		cal.set(Calendar.MONTH, 9);
		pd.setEndTime(cal.getTime());
		
		pd=new TimePeriod();
		pds.add(pd);
		pd.setStartTime(cal.getTime());
		pd.setEndTime(new Date());
		
		List<Long> instanceIDes=new ArrayList<>();
		instanceIDes.add(2955645l);
		instanceIDes.add(2957645l);
		instanceIDes.add(2957652l);
		instanceIDes.add(2955645l);
		query.setInstanceIDes(instanceIDes);
		query.setLimit(3);
		
		query.setMetricID("cpuRate");
		
		System.out.println("result:"+JSON.toJSONString(metricDataReportService.findTopSummaryData(query)));

	}
	
	
	@Test
	public void testFindInstanceMetricHistoryGroupByMetricID_halfHour(){
		MetricSummeryReportQuery query=new MetricSummeryReportQuery();

		Calendar cal=Calendar.getInstance();
		List<TimePeriod> pds=new ArrayList<TimePeriod>();
		query.setTimePeriods(pds);
		
		TimePeriod pd=new TimePeriod();
		pds.add(pd);
		cal.set(Calendar.DAY_OF_MONTH, 8);
		pd.setStartTime(cal.getTime());
		cal.set(Calendar.DAY_OF_MONTH, 9);
		pd.setEndTime(cal.getTime());
		
//		pd=new TimePeriod();
//		pds.add(pd);
//		pd.setStartTime(cal.getTime());
//		pd.setEndTime(new Date());
//		
		List<Long> instanceIDes=new ArrayList<>();
		instanceIDes.add(5012145l);
//		instanceIDes.add(4108145l);
		
		query.setInstanceIDes(instanceIDes);
		query.setSummaryType(MetricSummaryType.H);
		
		List<MetricWithTypeForReport> metric=new ArrayList<>();
//		metric.add(new MetricWithTypeForReport("connThreadCount",MetricTypeEnum.PerformanceMetric));
		metric.add(new MetricWithTypeForReport("cpuRate",MetricTypeEnum.PerformanceMetric));
//		metric.add(new MetricWithTypeForReport("cacheMissRate",MetricTypeEnum.PerformanceMetric));
//		metric.add(new MetricWithTypeForReport("appCpuRate",MetricTypeEnum.PerformanceMetric));
		
		query.setMetricIDes(metric);
		Map<String, Map<Long, List<MetricSummeryReportData>>>  map=metricDataReportService.findInstanceMetricHistoryGroupByMetricID(query);
		
		System.out.println("result:"+JSON.toJSONStringWithDateFormat(map,"yyyy-MM-dd HH:mm:ss"));
	}
	@Test
	public void testFindInstanceMetricHistoryGroupByMetricID_sixHour(){
		MetricSummeryReportQuery query=new MetricSummeryReportQuery();

		Calendar cal=Calendar.getInstance();
		List<TimePeriod> pds=new ArrayList<TimePeriod>();
		query.setTimePeriods(pds);
		
		TimePeriod pd=new TimePeriod();
		pds.add(pd);
		cal.set(Calendar.DAY_OF_MONTH, 14);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		pd.setStartTime(cal.getTime());
		cal.set(Calendar.DAY_OF_MONTH, 20);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		pd.setEndTime(cal.getTime());

		List<Long> instanceIDes=new ArrayList<>();
		instanceIDes.add(5759145l);
//		instanceIDes.add(5759145l);
		
		query.setInstanceIDes(instanceIDes);
		query.setSummaryType(MetricSummaryType.SH);
		
		List<MetricWithTypeForReport> metric=new ArrayList<>();
//		metric.add(new MetricWithTypeForReport("connThreadCount",MetricTypeEnum.PerformanceMetric));
		metric.add(new MetricWithTypeForReport("cpuRate",MetricTypeEnum.PerformanceMetric));
//		metric.add(new MetricWithTypeForReport("cacheMissRate",MetricTypeEnum.PerformanceMetric));
//		metric.add(new MetricWithTypeForReport("appCpuRate",MetricTypeEnum.PerformanceMetric));
		
		query.setMetricIDes(metric);
		Map<String, Map<Long, List<MetricSummeryReportData>>>  map=metricDataReportService.findInstanceMetricHistoryGroupByMetricID(query);
		
		System.out.println("result:"+JSON.toJSONStringWithDateFormat(map,"yyyy-MM-dd HH:mm:ss"));
	}
	@Test
	public void testFindInstanceMetricHistoryGroupByMetricID_day(){
		MetricSummeryReportQuery query=new MetricSummeryReportQuery();

		Calendar cal=Calendar.getInstance();
		List<TimePeriod> pds=new ArrayList<TimePeriod>();
		query.setTimePeriods(pds);
		
		TimePeriod pd=new TimePeriod();
		pds.add(pd);
		cal.set(Calendar.MONTH, 9);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		pd.setStartTime(cal.getTime());
		cal.set(Calendar.MONTH, 10);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		pd.setEndTime(cal.getTime());

		List<Long> instanceIDes=new ArrayList<>();
		instanceIDes.add(5012145l);
//		instanceIDes.add(4108145l);
		
		query.setInstanceIDes(instanceIDes);
		query.setSummaryType(MetricSummaryType.D);
		
		List<MetricWithTypeForReport> metric=new ArrayList<>();
//		metric.add(new MetricWithTypeForReport("connThreadCount",MetricTypeEnum.PerformanceMetric));
		metric.add(new MetricWithTypeForReport("cpuRate",MetricTypeEnum.PerformanceMetric));
//		metric.add(new MetricWithTypeForReport("cacheMissRate",MetricTypeEnum.PerformanceMetric));
//		metric.add(new MetricWithTypeForReport("appCpuRate",MetricTypeEnum.PerformanceMetric));
		
		query.setMetricIDes(metric);
		Map<String, Map<Long, List<MetricSummeryReportData>>>  map=metricDataReportService.findInstanceMetricHistoryGroupByMetricID(query);
		
		System.out.println("result:"+JSON.toJSONStringWithDateFormat(map,"yyyy-MM-dd HH:mm:ss"));
	}
}
