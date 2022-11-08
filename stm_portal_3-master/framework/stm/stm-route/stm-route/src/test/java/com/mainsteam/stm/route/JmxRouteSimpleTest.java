package com.mainsteam.stm.route;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.jmx.JmxUtil;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicClient;
import com.mainsteam.stm.route.logic.LogicServer;
import com.mainsteam.stm.route.physical.PhysicalServer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/portal-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class JmxRouteSimpleTest {

	@Resource
	private LogicServer logicServer;

	private JMXServiceURL jmxServiceURL;

	@Resource
	private LogicClient logicClient;

	@Resource
	private PhysicalServer physicalServer;

	private JmxRouteSimpleServerTest jmxRouteSimpleServerTest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		String ip = Inet4Address.getLocalHost().getHostAddress();
		jmxServiceURL = JmxUtil.newServerURL(ip, 9999);
		if (jmxRouteSimpleServerTest == null) {
			jmxRouteSimpleServerTest = new JmxRouteSimpleServerTest(
					logicServer, jmxServiceURL, physicalServer);
			jmxRouteSimpleServerTest.start();
		}
	}

	@After
	public void tearDown() throws Exception {
		jmxRouteSimpleServerTest.stop();
	}

	@Test
	public void test() {
		Map<String, Object> env = new HashMap<>();
		JMXConnector connector = null;
		try {
			RouteableJmxConnection connection = new RouteableJmxConnection(
					jmxServiceURL.getHost(), jmxServiceURL.getPort(),
					logicClient, LogicAppEnum.RPC_JMX_NODE_GROUP);
			env.put("jmx.remote.message.connection", connection);
			System.out.println("newJMXConnector start. ");
			connector = JmxUtil.newJMXConnector(jmxServiceURL.getHost(),
					jmxServiceURL.getPort(), env);
			System.out.println("connector=" + connector);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ObjectName name = JmxUtil.createName(TestJmxMBean.class);
		MBeanServerConnection jmxConnection;
		try {
			System.out.println("get a connection.");
			jmxConnection = connector.getMBeanServerConnection();
			System.out.println("get proxy obj.");
			TestJmxMBean proxyInstance = JMX.newMBeanProxy(jmxConnection, name,
					TestJmxMBean.class, false);
			System.out.println("runn proxy action.");
			String rs = proxyInstance.hello("ni hao.");
			System.out.println("return s=" + rs);
			connector.close();
		} catch (Throwable e) {
			e.printStackTrace();
			fail();
		}
	}
}

class TestJmx implements TestJmxMBean {
	@Override
	public String hello(String h) {
		System.out.println("recive " + h);
		return h + " return.";
	}
}
