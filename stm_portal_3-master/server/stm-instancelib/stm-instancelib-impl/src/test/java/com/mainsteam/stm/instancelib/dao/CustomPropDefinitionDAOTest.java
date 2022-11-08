/**
 * 
 */
package com.mainsteam.stm.instancelib.dao;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
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

import com.mainsteam.stm.instancelib.dao.pojo.CustomPropDefinitionDO;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
"classpath*:META-INF/services/*-beans.xml" })
public class CustomPropDefinitionDAOTest {

	@Resource
	private CustomPropDefinitionDAO definitionDAO;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity\\cap_libs");
		System.setProperty("testCase","test");
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

	@Test
	public void testInsertCustomPropDefinitionDO() {
		CustomPropDefinitionDO cpd = new CustomPropDefinitionDO();
		cpd.setCategory("categoryTest");
		cpd.setKey("keyTest");
		cpd.setName("nameTest");
		cpd.setUpdateTime(new Date().getTime());
		try {
			definitionDAO.insertCustomPropDefinitionDO(cpd);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
