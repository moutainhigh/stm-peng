package com.mainsteam.stm.event;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.state.engine.StateHandle;
import com.mainsteam.stm.state.obj.MetricStateChangeData;
import com.mainsteam.stm.state.obj.MetricStateData;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional 
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class AlarmEventHandleImplTest {
	 @Autowired StateHandle   alarmEventHandle;
	 @Autowired CapacityService capacityService;
	 
	 @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	 
	 @Test
	 public void testHandleMetricState_nomal(){
		 MetricStateChangeData cd=new MetricStateChangeData();
		 cd.setChangeNum(3);
		 
		 MetricStateData msd=new MetricStateData();
		 cd.setNewState(msd);
		 
		 
		 ResourceMetricDef mdef= capacityService.getResourceMetricDef("JuniperSwitch","ifnum");
		 cd.setMetricDef(mdef);
		 
		 ResourceMetricDef def=new ResourceMetricDef();
		 def.setAlert(true);
		 def.setName("ifName的名字");
		 def.setMetricType(MetricTypeEnum.AvailabilityMetric);
		 cd.setMetricDef(def);
		 
		 MetricCalculateData metricData=new MetricCalculateData();
		 metricData.setResourceInstanceId(666145);
		 cd.setMetricData(metricData);
		 
		 msd.setCollectTime(new Date());
		 msd.setInstanceID(666145);
		 msd.setMetricID("ifnum");
		 msd.setState(MetricStateEnum.NORMAL);
		 alarmEventHandle.onMetricStateChange(cd);
	 }
	 
	 @Test
	 public void testHandleMetricState_serious(){
		 MetricStateChangeData cd=new MetricStateChangeData();
		 cd.setChangeNum(3);
		 
		 MetricStateData msd=new MetricStateData();
		 cd.setNewState(msd);
		 
		 ResourceMetricDef mdef= capacityService.getResourceMetricDef("JuniperSwitch","ifnum");
		 cd.setMetricDef(mdef);
		 
		 MetricCalculateData metricData=new MetricCalculateData();
		 metricData.setResourceInstanceId(666145);
		 cd.setMetricData(metricData);
		 
		 msd.setCollectTime(new Date());
		 msd.setInstanceID(666145);
		 msd.setMetricID(mdef.getId());
		 msd.setState(MetricStateEnum.SERIOUS);
		 
		 Threshold th=new Threshold();
		 th.setExpressionOperator(">");
		 th.setThresholdValue("50");
		 cd.setThreshhold(th);
		 
		 alarmEventHandle.onMetricStateChange(cd);
	 }
	 
	
}
