/**
 * 
 */
package com.mainsteam.stm.bootstrap;

import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * @author ziw
 * 
 */
public class TestLancher {

	/**
	 * 
	 */
	public TestLancher() {
	}

	@Test
	public void testBootStrap() {
		Log logger = LogFactory.getLog(TestLancher.class);
		System.out.println(logger.getClass());
		System.setProperty("serverType", "collector");
		System.setProperty(BootStrapActor.VAR_CLASS_PATH,"src/test/resources;bin");
		System.setProperty(BootStrapActor.VAR_JAR_FILE_PATH, "lib");
		System.setProperty(BootStrapActor.VAR_STARTUP_CLASS,"com.mainsteam.stm.bootstrap.SpringStarter");
		try {
			BootStrapActor.main(new String[] { "paraemter_1", "paraemter_2" });
			
			System.out.println("BootStrapActor has running!");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
