package com.mainsteam.stm.capbase;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

public class ResourceRespositoyTest {

	@Test
	public void testStart() throws Exception {
		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("log4j.properties"));
		String oc4Path = "E:\\oc\\Capacity\\cap_libs";
		String username = System.getProperty("user.name");
		if ("sunsht".equals(username)) {
			oc4Path = "/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/cap_libs";
		}
		System.setProperty("caplibs.path", oc4Path);
		new ResourceRespositoy().start();
	}

}
