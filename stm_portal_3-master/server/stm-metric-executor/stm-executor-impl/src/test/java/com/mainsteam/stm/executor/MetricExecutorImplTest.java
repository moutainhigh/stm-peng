/**
 * 
 */
package com.mainsteam.stm.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.executor.obj.CustomMetricPluginParameter;
import com.mainsteam.stm.executor.obj.DiscoveryMetricData;
import com.mainsteam.stm.executor.obj.MetricDiscoveryParameter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-collector-*-beans.xml"})
public class MetricExecutorImplTest {

	@Resource
	private MetricExecutorImpl executor;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.executor.MetricExecutorImpl#catchDiscoveryMetricDatas(java.util.List, java.lang.String)}
	 * .
	 */
	@Test
	public void testCatchDiscoveryMetricDatas() {
		List<MetricDiscoveryParameter> bindParameters = new ArrayList<>();
		MetricDiscoveryParameter p = new MetricDiscoveryParameter();
		p.setResourceId("JuniperSwitch");
		p.setMetricId("ifNum");
		Map<String, String> initParameter = new HashMap<String, String>();
		initParameter.put("IP", "172.16.10.1");
		initParameter.put("port", "161");
		initParameter.put("community", "public");
		p.setDiscoveryInfo(initParameter);
		bindParameters.add(p);

		List<DiscoveryMetricData> calculateDatas = null;
		try {
			calculateDatas = executor.catchDiscoveryMetricDatas(bindParameters,"snmp");
			for(MetricData m:calculateDatas){
				System.out.println("resourceID:"+m.getResourceId());
				System.out.println("ResourceInstanceId:"+m.getResourceInstanceId());
				System.out.println("MetricId:"+m.getMetricId());
			}
		} catch (MetricExecutorException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(calculateDatas);
		assertTrue(calculateDatas.size() > 0);
		assertEquals(bindParameters.size(), calculateDatas.size());
	}
	
	@Test
	public void testCatchRealtimeMetricData(){
		try {
			MetricData list=executor.catchRealtimeMetricData(388361,"cpuRate");
			
			ObjectMapper mapper = new ObjectMapper();
			System.out.println("find result:"+ mapper.writeValueAsString(list));


		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testcatchMetricDataWithCustomPlugin(){
		try {
			MetricData list=executor.catchRealtimeMetricData(388361,"cpuRate");
			
			ObjectMapper mapper = new ObjectMapper();
			System.out.println("find result:"+ mapper.writeValueAsString(list));


		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCatchRealtimeMetricData_transfer() throws Exception{
			MetricData list=executor.catchRealtimeMetricData(2001,"MacAddress");
			
			ObjectMapper mapper = new ObjectMapper();
			System.out.println("find result:"+ mapper.writeValueAsString(list));
	}
}
