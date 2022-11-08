package com.mainsteam.stm.common.metric.dao;

import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.common.metric.obj.MetricData;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class MetricInfoDAOImplTest {

	@Autowired MetricInfoDAO metricInfoDAO;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	@Test
	public void testAddMetricInfoData(){
		Calendar cal=Calendar.getInstance();
		
		int size=10;
		int peroid=5;
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)-(size*peroid));
		
		for(int i=0;i<size;i++){
			MetricData data=new MetricData();
			data.setResourceInstanceId(10032);
			data.setMetricId("ifOutOctetsSpeed");
			data.setProfileId(2343l);
			data.setTimelineId(1232l);
			data.setCollectTime(cal.getTime());
			data.setData(new String[]{String.valueOf(Math.random()*10)});
			metricInfoDAO.addMetricInfoData(data);
			cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+peroid);
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
