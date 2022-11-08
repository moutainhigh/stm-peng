package com.mainsteam.stm.state;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
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
public class PerformanceStateCaluteServiceImplTest {

	private static final Log logger=LogFactory.getLog(PerformanceStateCaluteServiceImplTest.class);
	@Autowired PerformanceStateCaluteServiceImpl instanceStateCalculateService;
	@Autowired MetricDataProcessEngine metricDataProcessEngine;
	@Autowired CapacityService capacityService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	@Test
	@Rollback(false)
	public void testCalculateState_notCal() {
		Thread th1=new Thread(new Runnable() {
			@Override public void run() {
				MetricCalculateData metricData =new MetricCalculateData ();
				metricData.setResourceInstanceId(2955645);
				metricData.setCollectTime(new Date());
				metricData.setMetricId("cpuRate");
				metricData.setResourceId("MySQL");
				metricData.setMetricData(new String[]{"100"});
				
				ResourceMetricDef rmd=capacityService.getResourceMetricDef(metricData.getResourceId(), metricData.getMetricId());
				
				Map<String,Object> context=new HashMap<String,Object>();
				try {
					MetricDataPersistence ins = instanceStateCalculateService.process(metricData,rmd,null,context);
					ins.saveData();
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				logger.info("th1 run finished");
			}
		});
		th1.setDaemon(false);
		th1.start();
		
		Thread th2=	new Thread(new Runnable() {
			@Override public void run() {
				MetricCalculateData metricData =new MetricCalculateData ();
				metricData.setResourceInstanceId(2955645);
				metricData.setCollectTime(new Date());
				metricData.setMetricId("appCpuRate");
				metricData.setResourceId("MySQL");
				metricData.setMetricData(new String[]{"93"});
				
				ResourceMetricDef rmd=capacityService.getResourceMetricDef(metricData.getResourceId(), metricData.getMetricId());
				
				Map<String,Object> context=new HashMap<String,Object>();
				try {
					MetricDataPersistence ins = instanceStateCalculateService.process(metricData,rmd,null,context);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				logger.info("th2 run finished");
				
			}
		});
		th2.setDaemon(false);
		th2.start();

		try {
			Thread.sleep(20*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testCalculateState() throws Exception{

		System.out.println("testCalculateState:");
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
