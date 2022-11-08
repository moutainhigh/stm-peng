package com.mainsteam.stm.common.metric;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.common.metric.query.MetricSummaryQuery;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class MetricSummaryServiceTest {
	
	@Autowired MetricSummaryService metricSummaryService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	@Test
	public void testQuery(){
		List<MetricSummaryQuery> query=new ArrayList<MetricSummaryQuery>();
		MetricSummaryQuery q=new MetricSummaryQuery();
		q.setInstanceID(666145);
		q.setMetricID("cpuRate");
		q.setSummaryType(MetricSummaryType.D);
		
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.YEAR,cal.get(Calendar.YEAR)-1);
		q.setStartTime(cal.getTime());
		q.setEndTime(new Date());
		
		query.add(q);
		
		List<List<MetricSummaryData>> list=metricSummaryService.queryMetricSummary(query);
		System.out.println("testQuery"+JSON.toJSONString(list));
	}
}
