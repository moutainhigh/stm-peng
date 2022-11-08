/**
 * 
 */
package com.mainsteam.stm.transfer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.transfer.config.TransferConfig;
import com.mainsteam.stm.transfer.config.TransferConfigManager;
import com.mainsteam.stm.transfer.obj.TransferDataType;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:META-INF/services/server-processer-transfer-config-beans.xml" })
public class TransferConfigManagerTest {

	@Resource
	private TransferConfigManager manager;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FileUtils.copyFile(new File(
				"src/test/resources/properties/transferBack.properties"),
				new File("src/test/resources/properties/transfer.properties"));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		FileUtils.copyFile(new File(
				"src/test/resources/properties/transferBack.properties"),
				new File("src/test/resources/properties/transfer.properties"));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.transfer.config.TransferConfigManager#getTransferConfig(com.mainsteam.stm.transfer.obj.TransferDataType)}
	 * .
	 */
	@Test
	public void testGetTransferConfig() {
		TransferDataType[] types = TransferDataType.values();
		for (TransferDataType transferDataType : types) {
			TransferConfig dataConfig = manager
					.getTransferConfig(transferDataType);
			assertNotNull(dataConfig);
			if (transferDataType == TransferDataType.MetricData) {
				assertEquals(50, dataConfig.getMaxThreads());
				assertEquals(10, dataConfig.getCoreThreads());
				assertEquals(50000, dataConfig.getTransferQueueMaxSize());
				assertEquals(true, dataConfig.isPersistable());
			} else {
				assertEquals(1, dataConfig.getMaxThreads());
				assertEquals(1, dataConfig.getCoreThreads());
				assertEquals(100, dataConfig.getTransferQueueMaxSize());
				assertEquals(false, dataConfig.isPersistable());
			}
		}
	}

	@Test
	public void testGetTransferConfigDynamic() {
		testGetTransferConfig();
		synchronized (this) {
			try {
				this.wait(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			FileUtils
					.copyFile(
							new File(
									"src/test/resources/properties/transferChange.properties"),
							new File(
									"src/test/resources/properties/transfer.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		synchronized (this) {
			try {
				this.wait(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (TransferDataType transferDataType : TransferDataType.values()) {
			TransferConfig dataConfig = manager
					.getTransferConfig(transferDataType);
			assertNotNull(dataConfig);
			if (transferDataType == TransferDataType.MetricData) {
				assertEquals(30, dataConfig.getMaxThreads());
				assertEquals(10, dataConfig.getCoreThreads());
				assertEquals(70000, dataConfig.getTransferQueueMaxSize());
				assertEquals(false, dataConfig.isPersistable());
			} else {
				assertEquals(20, dataConfig.getMaxThreads());
				assertEquals(10, dataConfig.getCoreThreads());
				assertEquals(1000, dataConfig.getTransferQueueMaxSize());
				assertEquals(true, dataConfig.isPersistable());
			}
		}
	}
}
