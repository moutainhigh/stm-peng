package com.mainsteam.stm.common.state.dao;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.state.obj.MetricStateData;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/portal-*-beans.xml" })
public class MetricStateDAOImplTest {
	@Autowired MetricStateDAO metricStateDAO;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	@Test
	public void testUpdateAvailableState(){
		MetricStateData mas=new MetricStateData();
		mas.setCollectTime(new Date());
		mas.setInstanceID(12322);
		mas.setMetricID("ifNum");
		mas.setState(MetricStateEnum.CRITICAL);
		metricStateDAO.updateMetricState(mas);
	}
	
	@Test
	public void testUpdatePerformanceState(){
		MetricStateData mas=new MetricStateData();
		mas.setCollectTime(new Date());
		mas.setInstanceID(12322);
		mas.setMetricID("ifNum");
		mas.setState(MetricStateEnum.SERIOUS);
		metricStateDAO.updateMetricState(mas);
	}
	
	@Test
	public void testGetAllInstanceState(){
		metricStateDAO.getAllInstanceState();
	}
}
