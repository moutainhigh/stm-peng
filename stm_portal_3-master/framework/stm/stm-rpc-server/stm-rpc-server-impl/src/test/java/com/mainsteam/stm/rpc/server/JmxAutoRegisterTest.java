package com.mainsteam.stm.rpc.server;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml"})
public class JmxAutoRegisterTest {

	@Resource(name="OCRPCServer")
	private OCRPCServer server;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void test() {
		List<RemoteServiceInfo> infos = server.listService();
		assertNotNull(infos);
		assertTrue(infos.size()>0);
		boolean find = false;
		for (RemoteServiceInfo remoteServiceInfo : infos) {
			System.out.println(remoteServiceInfo.getInstance().getClass());
			if(remoteServiceInfo.getInstance().getClass().equals(AutoRegister.class)){
				find = true;
				break;
			}
		}
		assertTrue(find);
		assertTrue(server.isStarted());
	}
}
