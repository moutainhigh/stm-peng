package com.mainsteam.stm.common.metric.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.common.metric.dao.MetricSummaryDAO;
import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.common.metric.query.MetricSummaryQuery;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class MetricSummaryDAOTest {
	@Autowired MetricSummaryDAO metricSummaryDAO;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	
	@Test
	public void testAddCustomMetricSummary(){
		Calendar cal=Calendar.getInstance();
		Date now=cal.getTime();
		
		cal.set(Calendar.MONTH, 1);
		
		metricSummaryDAO.addCustomMetricSummary(cal.getTime(),now,MetricSummaryType.H);
	}
	
	@Test
	public void tesQueryCustomMetricSummary(){
		Calendar cal=Calendar.getInstance();
		Date now=cal.getTime();
		
		cal.set(Calendar.MONTH, 1);
		
		MetricSummaryQuery query=new MetricSummaryQuery();
		query.setEndTime(cal.getTime());
		query.setStartTime(now);
		query.setInstanceID(111l);
		query.setMetricID("1232");
		query.setSummaryType(MetricSummaryType.H);
		
		metricSummaryDAO.queryCustomMetricSummary(query);
	}

}
