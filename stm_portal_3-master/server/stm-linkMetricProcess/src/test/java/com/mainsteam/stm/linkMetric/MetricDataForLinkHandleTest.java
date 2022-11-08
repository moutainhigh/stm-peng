package com.mainsteam.stm.linkMetric;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.engine.MetricDataPersistence;
import com.mainsteam.stm.linkMetric.MetricDataForLinkHandle;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class MetricDataForLinkHandleTest {

	private Log logger=LogFactory.getLog(MetricDataForLinkHandleTest.class);
	@Autowired MetricDataForLinkHandle metricDataForLinkHandle;
	@Autowired CapacityService capacityService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../Capacity/cap_libs");
	}
	
	@Test
	public void testProcess() throws Exception{
		logger.info("testProcess start");
		
		MetricCalculateData metricData =new MetricCalculateData ();
		metricData.setResourceInstanceId(5832146);
		metricData.setCollectTime(new Date());
		metricData.setMetricId("ifInBandWidthUtil");
		metricData.setResourceId("JuniperSwitchNIC");
		metricData.setMetricData(new String[]{"34.4"});
		
		Map<String,Object> context=new HashMap<String,Object>();
		
		ResourceMetricDef rmd=capacityService.getResourceMetricDef(metricData.getResourceId(), metricData.getMetricId());
		
		
		MetricDataPersistence ins=metricDataForLinkHandle.process(metricData,rmd,null,context);
		logger.info("testCalculateState:"+JSON.toJSONString(ins));
	}
}
