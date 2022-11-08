package com.mainsteam.stm.common.metric.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.query.MetricHistoryDataQuery;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath:META-INF/services/server-processer-jpa-beans.xml",
"classpath:META-INF/services/server-processer-mybatis-beans.xml","classpath:server-processer-common-beans.xml"})
public class MetricDataDAOTest {

	@Autowired MetricDataDAO metricDataDAO;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	@Test
	public void testCountHistory(){
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 13);
		cal.set(Calendar.HOUR_OF_DAY,9);
		
		List<MetricSummaryData> data= metricDataDAO.countHistory(cal.getTime(),new Date(), "cpurate");
		System.out.println("Result:"+JSON.toJSONString(data));
	}
	
	@Test
	public void testQueryHistoryMetriData() throws Exception{
		MetricHistoryDataQuery query = new MetricHistoryDataQuery();
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		query.setEndTime(d.parse("2016-5-19 12:00:00"));
		query.setInstanceID(1052);
		query.setMetricID("cpuRate");
		query.setStartTime(d.parse("2016-5-18 12:00:00"));
		List<MetricData> metricDatas = metricDataDAO.queryHistoryMetricDatas(query);
		for (MetricData metricData : metricDatas) {
			System.out.println(metricData);
		}
	}
}
