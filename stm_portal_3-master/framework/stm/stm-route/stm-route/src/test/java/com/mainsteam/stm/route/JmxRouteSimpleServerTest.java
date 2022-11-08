package com.mainsteam.stm.route;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;

import com.mainsteam.stm.jmx.JmxUtil;
import com.mainsteam.stm.route.logic.LogicServer;
import com.mainsteam.stm.route.physical.PhysicalServer;

public class JmxRouteSimpleServerTest {

	private MBeanServer mBeanServer;

	private JMXConnectorServer connectorServer;

	private JMXServiceURL jmxServiceURL;

	private LogicServer logicServer;

	private PhysicalServer physicalServer;

	public JmxRouteSimpleServerTest(LogicServer logicServer,
			JMXServiceURL jmxServiceURL, PhysicalServer physicalServer) {
		this.logicServer = logicServer;
		this.jmxServiceURL = jmxServiceURL;
		this.physicalServer = physicalServer;
	}

	public void start() throws Exception {
		mBeanServer = ManagementFactory.getPlatformMBeanServer();
		String ip = this.jmxServiceURL.getHost();
		int port = this.jmxServiceURL.getPort();

		Map<String, Object> env = new HashMap<>();
		RouteableJmxSocketConnectionServer jmxSocketConnectionServer = new RouteableJmxSocketConnectionServer(
				jmxServiceURL, env, logicServer);
		env.put("jmx.remote.message.connection.server",
				jmxSocketConnectionServer);
		this.physicalServer.setConfig(ip, port);
		this.physicalServer.startServer();
		this.connectorServer = JmxUtil.newConnectorServer(mBeanServer, ip,
				port, env);
		this.connectorServer.start();
		registerMBean();
	}

	public void stop() throws IOException {
		this.connectorServer.stop();
		this.physicalServer.stopServer();
	}

	public void registerMBean() {
		System.out.println("-----start server ----------------.");
		TestJmx t = new TestJmx();
		ObjectName name = JmxUtil.createName(TestJmxMBean.class);
		try {
			mBeanServer.registerMBean(t, name);
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
			fail();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
			fail();
		}
	}
}
