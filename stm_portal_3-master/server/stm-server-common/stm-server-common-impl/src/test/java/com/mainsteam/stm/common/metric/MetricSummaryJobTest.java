package com.mainsteam.stm.common.metric;

import java.util.Calendar;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.common.metric.obj.MetricSummaryType;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class MetricSummaryJobTest {
	@Autowired MetricSummaryJob job;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	@Test
	public void testCountMetricSummary(){
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		
		Date end=cal.getTime();
		
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-5);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		job.countMetricSummary(cal.getTime(),end, MetricSummaryType.D);
		
	}

}
