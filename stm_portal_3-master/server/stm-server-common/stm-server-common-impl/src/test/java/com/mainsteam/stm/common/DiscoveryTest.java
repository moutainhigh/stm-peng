package com.mainsteam.stm.common;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.discovery.InstanceDiscoverMBean;
import com.mainsteam.stm.discovery.exception.InstanceDiscoveryException;
import com.mainsteam.stm.discovery.obj.DiscoveryParameter;
import com.mainsteam.stm.discovery.obj.ModelResourceInstance;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.rpc.client.OCRPCClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/portal-*-beans.xml" })
public class DiscoveryTest{

	@Resource
	private OCRPCClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity\\cap_libs");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@org.junit.Before
	public void setUp() throws Exception {
	}

	@org.junit.After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMBeanInvoke() throws IOException {
		final Node node = new Node();
		node.setIp("172.16.7.157");
		node.setPort(9901);
		// TODO Auto-generated method stub
		InstanceDiscoverMBean discoverMBean = client.getRemoteSerivce(node, InstanceDiscoverMBean.class);
		DiscoveryParameter parameter = new DiscoveryParameter();
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		discoveryInfos.put("IP", "172.16.10.1");
		discoveryInfos.put("port", "161");
		discoveryInfos.put("community", "public");
		// discoveryInfos.put("Version", "2c");
		parameter.setDiscoveryInfos(discoveryInfos);
		parameter.setDiscoveryWay("snmp");
		parameter.setResourceId("JuniperSwitch");
		ModelResourceInstance instance = null;
		try {
			instance = discoverMBean.discovery(parameter);
		} catch (InstanceDiscoveryException e) {
			e.printStackTrace();
			fail();
		}
		System.out.println("topInstance.getName()="+ instance.getName());
		Map<String, String[]> instanceProps = instance.getPropValues();
		assertTrue(instanceProps.size() > 0);
		
		System.out.println(JSON.toJSONString(instance));
	}
	
}
