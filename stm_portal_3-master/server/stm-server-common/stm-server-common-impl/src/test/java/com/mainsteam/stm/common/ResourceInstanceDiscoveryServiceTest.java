package com.mainsteam.stm.common;

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
import com.mainsteam.stm.common.instance.ResourceInstanceDiscoveryService;
import com.mainsteam.stm.common.instance.obj.ResourceInstanceDiscoveryParameter;


@TransactionConfiguration
@Transactional 
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/portal-*-beans.xml" })
public class ResourceInstanceDiscoveryServiceTest {

	@Autowired ResourceInstanceDiscoveryService discoveryService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity\\cap_libs");
	}
	@Test
	public void testDiscovery() throws Exception{
		ResourceInstanceDiscoveryParameter parameter = new ResourceInstanceDiscoveryParameter();
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		discoveryInfos.put("IP", "172.16.10.1");
		discoveryInfos.put("port", "161");
		discoveryInfos.put("community", "public");
		// discoveryInfos.put("Version", "2c");
		parameter.setDiscoveryInfos(discoveryInfos);
		parameter.setDiscoveryWay("snmp");
		parameter.setResourceId("JuniperSwitch");
		
		parameter.setNodeGroupId(183001);
		
		discoveryService.discoveryResourceInstance(parameter);
	}
}
