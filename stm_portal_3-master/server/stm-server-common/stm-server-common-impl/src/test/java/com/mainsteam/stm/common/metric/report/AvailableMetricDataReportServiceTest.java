package com.mainsteam.stm.common.metric.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.util.DateUtil;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/portal-*-beans.xml" })
public class AvailableMetricDataReportServiceTest {

	@Autowired AvailableMetricDataReportService reportService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	@Test
	public void testFind(){
		AvailableMetricCountQuery query=new AvailableMetricCountQuery();
		
		List<TimePeriod> pds=new ArrayList<TimePeriod>();
		query.setTimePeriods(pds);
		
		TimePeriod pd=new TimePeriod();
		
		pds.add(pd);
		pd.setStartTime(DateUtil.parseDateTime("2015-08-20 14:20:35"));
		pd.setEndTime(new Date());
		
		List<Long> instanceIDes=new ArrayList<>();
		instanceIDes.add(781l);
		
		query.setInstanceIDes(instanceIDes);
		
		List<AvailableMetricCountData> list= reportService.findAvailableCount(query);
		
		System.out.println("result:"+JSON.toJSONString(list));
	}
}
