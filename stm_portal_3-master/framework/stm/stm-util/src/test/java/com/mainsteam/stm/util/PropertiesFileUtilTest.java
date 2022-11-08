/**
 * 
 */
package com.mainsteam.stm.util;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author ziw
 *
 */
public class PropertiesFileUtilTest {
	
	private String propFileName = "com.mainsteam.stm.util.test.test.properties";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.mainsteam.stm.util.PropertiesFileUtil#getProperties(java.lang.ClassLoader, java.lang.String)}.
	 */
	@Test
	public void testGetPropertiesClassLoaderString() {
		Properties p = PropertiesFileUtil.getProperties(this.getClass().getClassLoader(), propFileName);
		assertNotNull(p);
		assertEquals("aValue",p.getProperty("a"));
	}

	/**
	 * Test method for {@link com.mainsteam.stm.util.PropertiesFileUtil#getProperties(java.lang.String)}.
	 */
	@Test
	public void testGetPropertiesString() {
		Properties p = PropertiesFileUtil.getProperties(propFileName);
		assertNotNull(p);
		assertEquals("aValue",p.getProperty("a"));
	}

}
