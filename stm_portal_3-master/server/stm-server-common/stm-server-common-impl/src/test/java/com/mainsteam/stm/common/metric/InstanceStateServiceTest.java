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
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/portal-*-beans.xml" })
public class InstanceStateServiceTest {
	@Autowired InstanceStateService resourceInstanceService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	@Test
	public void testCatchRealtimeMetricData() throws MetricExecutorException, InstancelibException{
		InstanceStateData state=resourceInstanceService.catchRealtimeMetricData(5759645l);
		System.out.println(JSON.toJSONString(state));
	}
	
	@Test
	public void testFindStates() throws MetricExecutorException, InstancelibException{
		List<Long> idesc=new ArrayList<Long>();
		idesc.add(5759645l);
		idesc.add(123l);
		
		List<InstanceStateData> state=resourceInstanceService.findStates(idesc);
		System.out.println(JSON.toJSONString(state));
	}
	
	@Test
	public void testAddInstanceState(){
		InstanceStateData state=new InstanceStateData();
		state.setCollectTime(new Date());
		state.setInstanceID(12234l);
		state.setCauseBymetricID("cpuRate");
		state.setState(InstanceStateEnum.NORMAL);
		resourceInstanceService.addState(state);
	}
}
