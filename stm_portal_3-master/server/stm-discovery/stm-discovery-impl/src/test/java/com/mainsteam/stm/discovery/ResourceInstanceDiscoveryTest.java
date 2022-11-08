package com.mainsteam.stm.discovery;

import static org.junit.Assert.*;

import java.util.Arrays;
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

import com.mainsteam.stm.discovery.exception.InstanceDiscoveryException;
import com.mainsteam.stm.discovery.obj.DiscoveryParameter;
import com.mainsteam.stm.discovery.obj.ModelResourceInstance;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ 
		
		 "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-collector-*-beans.xml"})
public class ResourceInstanceDiscoveryTest {

	@Resource private InstanceCollectDiscover discover;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String username = System.getProperty("user.name");
		String oc4Path = "";
		if ("sunsht".equals(username)) {
			oc4Path = "/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/cap_libs";
		}else{
			oc4Path = "../../../Capacity/cap_libs";
		}
		System.setProperty("caplibs.path",oc4Path);
		System.setProperty("testCase", "true");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testDiscoveryHPUnix() {
		String resourceId = "HPUnix";
		DiscoveryParameter parameter = new DiscoveryParameter();

		parameter.setResourceId(resourceId);
		
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		discoveryInfos.put("IP", "172.16.7.250");
		discoveryInfos.put("port", "23");
		discoveryInfos.put("username", "tlq");
		discoveryInfos.put("password", "password");
		discoveryInfos.put("userprompt", "ogin:");
		discoveryInfos.put("passprompt", "assword:");
		discoveryInfos.put("opprompt", "$");
		parameter.setDiscoveryInfos(discoveryInfos);
		

		ModelResourceInstance instance = null;
		try {
			instance = discover.discovery(parameter);
		} catch (InstanceDiscoveryException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(instance);
		assertNotNull(instance.getName());
		assertNotNull(instance.getPropValues());
		System.out.println("resourceId:" + instance.getResourceId());
		System.out.println("categoryId:" + instance.getCategoryId());
		System.out.println("topInstance.getName()=" + instance.getName());
		Map<String, String[]> instanceProps = instance.getPropValues();
		assertTrue(instanceProps.size() > 0);
		
		for (String propKey : instanceProps.keySet()) {
			System.out.println(propKey + "=" + Arrays.asList(instanceProps.get(propKey)));
		}
		
		
		List<ModelResourceInstance> childrenInstances = instance.getChildren();
		assertNotNull(childrenInstances);
		assertTrue(childrenInstances.size()>0);
		for (ModelResourceInstance childInstance : childrenInstances) {
			assertNotNull(childInstance.getName());
			assertNotNull(childInstance.getPropValues());
			System.out.println();
			System.out.println("-----------------**********************---------------------");
			System.out.println("childInstance.getName()=" + childInstance.getName());
			instanceProps = childInstance.getPropValues();
			assertTrue(instanceProps.size() > 0);
			for (String propKey : instanceProps.keySet()) {
				System.out.println(propKey + "=" + Arrays.asList(instanceProps.get(propKey)));
			}
		}
	}
	@Test
	public void testDiscoveryCiscoSwitch() {
		String resourceId = "CiscoSwitch";
		DiscoveryParameter parameter = new DiscoveryParameter();
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		discoveryInfos.put("IP", "192.168.1.1");
		discoveryInfos.put("port", "161");
		discoveryInfos.put("community", "public");
		//discoveryInfos.put("Version", "2c");
		parameter.setDiscoveryInfos(discoveryInfos);
		parameter.setDiscoveryWay("snmp");
		parameter.setResourceId(resourceId);
		
		ModelResourceInstance instance = null;
		try {
			instance = discover.discovery(parameter);
		} catch (InstanceDiscoveryException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(instance);
		assertNotNull(instance.getName());
		assertNotNull(instance.getPropValues());
		System.out.println("resourceId:" + instance.getResourceId());
		System.out.println("categoryId:" + instance.getCategoryId());
		System.out.println("topInstance.getName()=" + instance.getName());
		Map<String, String[]> instanceProps = instance.getPropValues();
		assertTrue(instanceProps.size() > 0);
		for (String propKey : instanceProps.keySet()) {
			System.out.println(propKey + "=" + Arrays.asList(instanceProps.get(propKey)));
		}
		
		List<ModelResourceInstance> childrenInstances = instance.getChildren();
		assertNotNull(childrenInstances);
		assertTrue(childrenInstances.size()>0);
		for (ModelResourceInstance childInstance : childrenInstances) {
			assertNotNull(childInstance.getName());
			assertNotNull(childInstance.getPropValues());
			System.out.println();
			System.out.println("-----------------**********************---------------------");
			System.out.println("childInstance.getName()=" + childInstance.getName());
			instanceProps = childInstance.getPropValues();
			assertTrue(instanceProps.size() > 0);
			for (String propKey : instanceProps.keySet()) {
				System.out.println(propKey + "=" + Arrays.asList(instanceProps.get(propKey)));
			}
		}
	}
	
	@Test
	public void testDiscoveryMysql() {
		String resourceId = "MySQL";
		DiscoveryParameter parameter = new DiscoveryParameter();
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		discoveryInfos.put("IP", "172.16.7.200");
		discoveryInfos.put("port", "22");
		discoveryInfos.put("jdbcPort", "3306");
//		discoveryInfos.put("community", "public");
		discoveryInfos.put("dbtype", "MySQL");
		discoveryInfos.put("dbName", "mysql");
		discoveryInfos.put("dbUsername", "root");
		discoveryInfos.put("dbPassword", "root3306");
		discoveryInfos.put("username", "root");
		discoveryInfos.put("password", "password");
		discoveryInfos.put("inputInstallPath", "/usr/sbin/mysqld");

		//discoveryInfos.put("Version", "2c");
		parameter.setDiscoveryInfos(discoveryInfos);
		parameter.setDiscoveryWay("ssh");
		parameter.setResourceId(resourceId);
		
		ModelResourceInstance instance = null;
		try {
			instance = discover.discovery(parameter);
		} catch (InstanceDiscoveryException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(instance);
		assertNotNull(instance.getName());
		assertNotNull(instance.getPropValues());
		System.out.println("resourceId:" + instance.getResourceId());
		System.out.println("categoryId:" + instance.getCategoryId());
		System.out.println("topInstance.getName()=" + instance.getName());
		Map<String, String[]> instanceProps = instance.getPropValues();
		assertTrue(instanceProps.size() > 0);
		for (String propKey : instanceProps.keySet()) {
			System.out.println(propKey + "=" + Arrays.asList(instanceProps.get(propKey)));
		}
		
		List<ModelResourceInstance> childrenInstances = instance.getChildren();
		assertNotNull(childrenInstances);
		assertTrue(childrenInstances.size()>0);
		for (ModelResourceInstance childInstance : childrenInstances) {
			assertNotNull(childInstance.getName());
			assertNotNull(childInstance.getPropValues());
			System.out.println();
			System.out.println("-----------------**********************---------------------");
			System.out.println("childInstance.getName()=" + childInstance.getName());
			instanceProps = childInstance.getPropValues();
			assertTrue(instanceProps.size() > 0);
			for (String propKey : instanceProps.keySet()) {
				System.out.println(propKey + "=" + Arrays.asList(instanceProps.get(propKey)));
			}
		}
	}
	
	@Test
	public void testDiscoveryJuniperSwitch() {
		DiscoveryParameter parameter = new DiscoveryParameter();
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		discoveryInfos.put("IP", "172.16.10.1");
		discoveryInfos.put("port", "161");
		discoveryInfos.put("community", "public");
		//discoveryInfos.put("Version", "2c");
		parameter.setDiscoveryInfos(discoveryInfos);
		parameter.setDiscoveryWay("snmp");
		parameter.setResourceId("JuniperSwitch");
		
		ModelResourceInstance instance = null;
		try {
			instance = discover.discovery(parameter);
		} catch (InstanceDiscoveryException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(instance);
		assertNotNull(instance.getName());
		assertNotNull(instance.getPropValues());
		System.out.println("resourceId:" + instance.getResourceId());
		System.out.println("categoryId:" + instance.getCategoryId());
		System.out.println("topInstance.getName()=" + instance.getName());
		Map<String, String[]> instanceProps = instance.getPropValues();
		assertTrue(instanceProps.size() > 0);
		for (String propKey : instanceProps.keySet()) {
			System.out.println(propKey + "=" + Arrays.asList(instanceProps.get(propKey)));
		}
		
		List<ModelResourceInstance> childrenInstances = instance.getChildren();
		assertNotNull(childrenInstances);
		assertTrue(childrenInstances.size()>0);
		for (ModelResourceInstance childInstance : childrenInstances) {
			assertNotNull(childInstance.getName());
			assertNotNull(childInstance.getPropValues());
			System.out.println();
			System.out.println("-----------------**********************---------------------");
			System.out.println("childInstance.getName()=" + childInstance.getName());
			instanceProps = childInstance.getPropValues();
			assertTrue(instanceProps.size() > 0);
			for (String propKey : instanceProps.keySet()) {
				System.out.println(propKey + "=" + Arrays.asList(instanceProps.get(propKey)));
			}
		}
	}
}
