/**
 * 
 */
package com.mainsteam.stm.instancelib.dao;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import junit.framework.TestCase;

import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.DiscoverPropExtendService;
import com.mainsteam.stm.instancelib.util.PropCache;

/**
 * @author xiaoruqiang
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:META-INF/services/public-*-beans.xml" ,
	"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class DiscoverPropServiceImplTest extends TestCase {

	

	@Resource
	private PropDAO propDAO;
	
	
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
	
		super.setUp();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		// OK,do nothing.
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.instancelib.impl.DiscoverPropDefServiceImpl#start()}
	 * .
	 */
	@Test
	public void testStart() {
		// OK,do nothing.
	}

	@Test
	public void testT(){
		List<String> moduleKeys = new ArrayList<>();
		moduleKeys.add("ip");
		moduleKeys.add("macAddress");
		List<String> discoverKeys = new ArrayList<>();
		discoverKeys.add("ip");
		try {
			List<PropDO> propDOs = propDAO.getAllModuleAndDiscoverProp(moduleKeys, null);
			System.out.println("ff");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
