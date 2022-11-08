package com.mainsteam.stm.portal.report.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryInstance;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryMetric;
import com.mainsteam.stm.portal.report.engine.ReportTask;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/portal-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class ReportEngineTest {

	@Resource
	private ReportEngine reportEngine;
	
	@BeforeClass
	public static void beforeClass(){
		System.setProperty("caplibs.path", "E:\\workspace\\cap_libs");
		System.setProperty("testCase", "case");
		
	}
	
	@Test
	public void test() {
		ReportTemplate reportTemplate=new ReportTemplate();
		reportTemplate.setReportTemplateId(1);
		reportTemplate.setReportTemplateType(1);
		//day:1   week:2   month:3
		reportTemplate.setReportTemplateCycle(2);
		reportTemplate.setReportTemplateName("Test-Report");

		reportTemplate.setReportTemplateCreateTime(new Date());
		//第一生成时间:0.当前1.下一个
		reportTemplate.setReportTemplateFirstGenerateTime(0);
		
		reportTemplate.setReportTemplateBeginTime("2,4,6");
		reportTemplate.setReportTemplateEndTime("22");
		
		reportTemplate.setReportTemplateFirstGenerateTime(0);
		reportTemplate.setReportTemplateSecondGenerateTime(23);
		
		List<ReportTemplateDirectory> directoryList=new ArrayList();
		ReportTemplateDirectory dir=new ReportTemplateDirectory();
		dir.setReportTemplateId(111);
		//0 y,1 N
		dir.setReportTemplateDirectoryIsDetail(0);
		dir.setReportTemplateDirectoryName("主机报表");
		
		//2955645
		
		List<ReportTemplateDirectoryInstance> directoryInstanceList
		= new ArrayList();
		List<ReportTemplateDirectoryMetric> rtdMetricList
		= new ArrayList();	
		ReportTemplateDirectoryMetric metric = new ReportTemplateDirectoryMetric();

		
		metric = new ReportTemplateDirectoryMetric();
		metric.setMetricId("cpuRate");
		metric.setMetricName("CPU 使用率");
		metric.setMetricType(MetricTypeEnum.PerformanceMetric);
		rtdMetricList.add(metric);
		
		

		
		
		ReportTemplateDirectoryInstance rtdInstance=new ReportTemplateDirectoryInstance();
		rtdInstance.setInstanceId(5012145);
		rtdInstance.setInstanceIP("127.0.0.1");
		rtdInstance.setInstanceName("Test Linux");
		rtdInstance.setInstanceType("Test resource Type");
		rtdInstance.setRtdMetricList(rtdMetricList);
		
		directoryInstanceList.add(rtdInstance);
		
		
		dir.setDirectoryInstanceList(directoryInstanceList);
		dir.setDirectoryMetricList(rtdMetricList);
		dir.setReportTemplateDirectoryResource("TTTTTT");
		
		directoryList.add(dir);
		
		reportTemplate.setDirectoryList(directoryList);
		
		ReportTask task=new ReportTask(reportTemplate);
		try {
		task.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		try {
//			reportEngine.startEngine(reportTemplate);
//			 
//			Thread.sleep(20000);
//			
//			System.out.println("Delete Job-"+reportTemplate.getReportTemplateId());
//			
//			reportEngine.stopEngine(reportTemplate.getReportTemplateId());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
		

	}

}
