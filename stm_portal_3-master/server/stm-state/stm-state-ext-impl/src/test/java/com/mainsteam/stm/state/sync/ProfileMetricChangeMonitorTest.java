package com.mainsteam.stm.state.sync;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.common.metric.sync.MetricProfileChange;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.state.sync.ProfileMetricChangeMonitor;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class ProfileMetricChangeMonitorTest {
	@Autowired ProfileMetricChangeMonitor profileMetricChangeMonitor;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	
	@Test
	@Rollback(false)
	public void testNotify() throws BaseException{
		
		MetricProfileChange paramter=new MetricProfileChange();
		paramter.setIsAlarm(true);
		paramter.setMetricID("cpuRate");
		paramter.setProfileID(1111495);
		profileMetricChangeMonitor.notify(paramter);
	}
	@Test
	@Rollback(false)
	public void testNotify_false() throws BaseException{
		
		MetricProfileChange paramter=new MetricProfileChange();
		paramter.setIsAlarm(false);
		paramter.setMetricID("cpuRate");
		paramter.setProfileID(1111495);
		profileMetricChangeMonitor.notify(paramter);
	}
}
