/**
 * 
 */
package com.mainsteam.stm.instancelib.dao;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;

/**
 * @author ziw
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:META-INF/services/public-*-beans.xml" ,
"classpath*:META-INF/services/server-collector-*-beans.xml" })
public class CollectorResourceInstanceServiceImplTest {

	@Resource
	private ResourceInstanceService instanceService;
	
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
	 * Test method for {@link com.mainsteam.stm.instancelib.service.impl.CollectorResourceInstanceServiceImpl#getResourceInstance(long)}.
	 */
	@Test
	public void testGetResourceInstance() {
		ResourceInstance instance = new ResourceInstance();
		instance.setId(1);
		try {
			instanceService.addResourceInstance(instance);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			assertNotNull(instanceService.getResourceInstance(instance.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
