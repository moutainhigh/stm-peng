package com.mainsteam.stm.plugin.resin;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class ResinCollectUtilTest {

	@Test
	public void testGetAvailability() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetResDisplayName() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOS() {
		JBrokerParameter jbrokerConnInfo = mockJBrokerParameter();
		String os = ResinCollectUtil.getOS(jbrokerConnInfo);
		System.out.println(os);
	}

	JBrokerParameter mockJBrokerParameter() {
		JBrokerParameter connInfo = new JBrokerParameter();
		connInfo.setIp("172.16.7.199");
		connInfo.setPort(9999);
		connInfo.setUsername("monitorRole");
		connInfo.setPassword("QED");
		return connInfo;
	}

	@Test
	public void testGetHomeDir() {
		JBrokerParameter jbrokerConnInfo = mockJBrokerParameter();
		String dir = ResinCollectUtil.getHomeDir(jbrokerConnInfo);
		System.out.println(dir);
	}

	@Test
	public void testGetHttpPort() {
		JBrokerParameter jbrokerConnInfo = mockJBrokerParameter();
		String httpPort = ResinCollectUtil.getHttpPort(jbrokerConnInfo);
		System.out.println(httpPort);
	}

	@Test
	public void testGetThreadPoolOverView() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetJdbcConnDetail() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetWebAppDetail() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetHostName() {
		JBrokerParameter jbrokerConnInfo = mockJBrokerParameter();
		String hostName = ResinCollectUtil.getHostName(jbrokerConnInfo);
		System.out.println(hostName);
	}

	@Test
	public void testGetVersion() {
		JBrokerParameter jbrokerConnInfo = mockJBrokerParameter();
		String version = ResinCollectUtil.getVersion(jbrokerConnInfo);
		System.out.println(version);
	}

}
