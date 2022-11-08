package com.mainsteam.stm.common.instance;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.common.exception.ResourceInstanceDiscoveryException;
import com.mainsteam.stm.common.instance.ResourceInstanceDiscoveryService;
import com.mainsteam.stm.common.instance.obj.ResourceInstanceDiscoveryParameter;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/portal-*-beans.xml" })
public class ResourceInstanceDiscoveryServiceTest {
	private static final Log logger=LogFactory.getLog(ResourceInstanceDiscoveryServiceTest.class);
	@Autowired ResourceInstanceDiscoveryService discoveryService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	@Test
	public void testDiscoveryResourceInstance() throws ResourceInstanceDiscoveryException{
		
		ResourceInstanceDiscoveryParameter parameter = new ResourceInstanceDiscoveryParameter();
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		discoveryInfos.put("IP", "172.16.10.1");
		discoveryInfos.put("port", "161");
		discoveryInfos.put("community", "public");
		// discoveryInfos.put("Version", "2c");
		parameter.setDiscoveryInfos(discoveryInfos);
		parameter.setDiscoveryWay("snmp");
		parameter.setResourceId("JuniperSwitch");
		parameter.setNodeGroupId(1002);
		logger.info(JSON.toJSONString(discoveryService.discoveryResourceInstance(parameter)));
	}
	
	@Test
	public void testDiscoveryInstance_Mysql() throws ResourceInstanceDiscoveryException{
		
		ResourceInstanceDiscoveryParameter parameter = new ResourceInstanceDiscoveryParameter();
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		discoveryInfos.put("IP", "172.16.7.200");
		discoveryInfos.put("port", "22");
		discoveryInfos.put("jdbcPort", "3306");
//		discoveryInfos.put("dbtype", "MySQL");
		discoveryInfos.put("dbName", "mysql");
		discoveryInfos.put("dbUsername", "root");
		discoveryInfos.put("dbPassword", "root3306");
		discoveryInfos.put("username", "root");
		discoveryInfos.put("password", "password");
		parameter.setDiscoveryInfos(discoveryInfos);
		parameter.setDiscoveryWay("SSH");
		parameter.setResourceId("MySQL");
		parameter.setNodeGroupId(183001);
		logger.info(JSON.toJSONString(discoveryService.discoveryResourceInstance(parameter)));
	}

}
