package com.mainsteam.stm.rpc.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.rpc.server.OCRPCServerImpl;
import com.mainsteam.stm.rpc.server.RemoteServiceInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-*-beans.xml" })
public class OCRPCServerImplTest {

	@Resource
	private OCRPCServerImpl rpcImpl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		rpcImpl.start();
	}

	@After
	public void tearDown() throws Exception {
		 rpcImpl.close();
	}

	@Test
	public void testRegisterService() {
		try {
			rpcImpl.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		ShowMessageInf to = new ShowMessageInf();
		assertTrue(rpcImpl.registerService(to, ShowMessageInfMBean.class));
		assertFalse(rpcImpl.registerService(to, ShowMessageInfMBean.class));
	}

	@Test
	public void testUnregisterServiceObject() {
		ShowMessageInf to = new ShowMessageInf();
		assertTrue(rpcImpl.registerService(to, ShowMessageInfMBean.class));
		assertTrue(rpcImpl.unregisterService(to));
	}

	@Test
	public void testUnregisterServiceLong() {
		ShowMessageInf to = new ShowMessageInf();
		assertTrue(rpcImpl.registerService(to, ShowMessageInfMBean.class));
		List<RemoteServiceInfo> infos = rpcImpl.listService();
		assertTrue(infos.size() == 1);
		for (RemoteServiceInfo remoteServiceInfo : infos) {
			assertTrue(rpcImpl.unregisterService(remoteServiceInfo
					.getRemoteServiceId()));
			assertFalse(rpcImpl.unregisterService(remoteServiceInfo
					.getRemoteServiceId()));
		}
	}

	@Test
	public void testListService() {
		ShowMessageInf to = new ShowMessageInf();
		assertTrue(rpcImpl.registerService(to, ShowMessageInfMBean.class));
		List<RemoteServiceInfo> infos = rpcImpl.listService();
		assertTrue(infos.size() == 1);
		RemoteServiceInfo si = infos.get(0);
		assertEquals(to, si.getInstance());
		assertEquals(ShowMessageInfMBean.class.getName(), si.getClassName());
	}

	@Test
	public void testMBeanInvoke() {
		ShowMessageInf to = new ShowMessageInf();
		assertTrue(rpcImpl.registerService(to, ShowMessageInfMBean.class));
		try {
			JMXServiceURL url = new JMXServiceURL("jmxmp", "localhost", 9999);
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			// Get an MBeanServerConnection
			//
			System.out.println("\nGet an MBeanServerConnection");
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

			ObjectName mbeanName = new ObjectName("oc", "name",
					ShowMessageInfMBean.class.getName());
			ShowMessageInfMBean proxy = (ShowMessageInfMBean) MBeanServerInvocationHandler
					.newProxyInstance(mbsc, mbeanName,
							ShowMessageInfMBean.class, false);
			System.out.println("client print:"
					+ proxy.spellAndShow("1", "2", "3"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
			fail();
		}

	}

}

class ShowMessageInf implements ShowMessageInfMBean {
	@Override
	public String spellAndShow(String... array) {
		StringBuilder b = new StringBuilder();
		b.append('[');
		for (String s : array) {
			b.append(s).append(',');
		}
		b.deleteCharAt(b.length() - 1);
		b.append(']');
		System.out.println("Server print:" + b);
		return b.toString();
	}

}
