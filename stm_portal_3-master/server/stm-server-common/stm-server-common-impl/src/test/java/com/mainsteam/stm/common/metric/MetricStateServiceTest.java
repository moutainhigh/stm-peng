package com.mainsteam.stm.common.metric;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.state.obj.MetricStateData;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/portal-*-beans.xml" })
public class MetricStateServiceTest {
	@Autowired MetricStateService metricValidService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	@Test
	public void testFindMetricState(){
		List<Long> idesc=new ArrayList<Long>();
		idesc.add(5759645l);
		idesc.add(123l);
		
		List<String> metricIDes=new ArrayList<String>();
		metricIDes.add("processAvail");
		
		List<MetricStateData> state=metricValidService.findMetricState(idesc, metricIDes);
		System.out.print("getMetricAvailableState:"+JSON.toJSONString(state));
	}
	
	@Test
	public void testAddMetricState(){
		MetricStateData state=new MetricStateData();
		state.setCollectTime(new Date());
		state.setInstanceID(12234l);
		state.setMetricID("cpuRate");
		state.setState(MetricStateEnum.NORMAL);
		state.setType(MetricTypeEnum.PerformanceMetric);
		metricValidService.updateMetricState(state);
	}
	
}
