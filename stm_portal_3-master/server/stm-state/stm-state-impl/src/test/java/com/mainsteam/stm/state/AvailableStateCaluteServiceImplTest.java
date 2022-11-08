package com.mainsteam.stm.state;

import java.util.Date;
import java.util.HashMap;
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
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.engine.MetricDataPersistence;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessEngine;
import com.mainsteam.stm.state.obj.MetricStateData;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class AvailableStateCaluteServiceImplTest {

	@Autowired CapacityService capacityService;
	@Autowired AvailableStateCalculateServiceImpl instanceStateCalculateService;
	@Autowired MetricDataProcessEngine metricDataProcessEngine;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	@Test
	public void testCalculateState_notCal() throws Exception{
		MetricCalculateData metricData =new MetricCalculateData ();
		metricData.setResourceInstanceId(34616);
		metricData.setCollectTime(new Date());
		metricData.setMetricId("availability");
		metricData.setResourceId("windowssnmp");
		metricData.setMetricData(new String[]{"0"});
		
		Map<String,Object> context=new HashMap<String,Object>();
		
		ResourceMetricDef rmd=capacityService.getResourceMetricDef(metricData.getResourceId(), metricData.getMetricId());
		
		
		MetricDataPersistence ins=instanceStateCalculateService.process(metricData,rmd,null,context);
		System.out.println("testCalculateState:"+JSON.toJSONString(ins));
	}
	@Test
	public void testCalculateState() throws Exception{
		MetricCalculateData metricData =new MetricCalculateData ();
		metricData.setResourceInstanceId(2783152);
		metricData.setCollectTime(new Date());
		metricData.setMetricId("throughput");
		metricData.setResourceId("JuniperSwitch");
		metricData.setMetricData(new String[]{"1234.4"});
		
		Map<String,Object> context=new HashMap<String,Object>();
		MetricStateData mps=new MetricStateData();
		mps.setState(MetricStateEnum.WARN);
		
		ResourceMetricDef rmd=capacityService.getResourceMetricDef(metricData.getResourceId(), metricData.getMetricId());
		
		
		MetricDataPersistence ins=instanceStateCalculateService.process(metricData,rmd,null,context);
		System.out.println("testCalculateState:"+JSON.toJSONString(ins));
	}
	
	@Test
	public void testHandle(){
		MetricCalculateData metricData =new MetricCalculateData ();
		metricData.setResourceInstanceId(2783152);
		metricData.setCollectTime(new Date());
		metricData.setMetricId("throughput");
		metricData.setResourceId("JuniperSwitch");
		metricData.setMetricData(new String[]{"34.4"});
		metricDataProcessEngine.handle(metricData);
	}
}
