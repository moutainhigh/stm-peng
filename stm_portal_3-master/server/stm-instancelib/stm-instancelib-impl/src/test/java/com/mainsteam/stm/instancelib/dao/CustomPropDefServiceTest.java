/**
 * 
 */
package com.mainsteam.stm.instancelib.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

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

import com.mainsteam.stm.instancelib.CustomPropDefService;
import com.mainsteam.stm.instancelib.obj.CustomPropDefinition;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:META-INF/services/public-*-beans.xml" ,
	"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class CustomPropDefServiceTest extends DataSourceBasedDBTestCase {

	@Resource
	private CustomPropDefService propDefService;

	@Resource(name="defaultDataSource")
	private DataSource source;

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

	protected void setUpDatabaseConfig(DatabaseConfig config) {
		config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, new Integer(97));
		config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
	}

	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}

	protected DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.NONE;
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
	 * {@link com.mainsteam.stm.instancelib.service.impl.CustomPropDefServiceImpl#start()}
	 * .
	 */
	@Test
	public void testStart() {
		// OK,do nothing.
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.instancelib.service.impl.CustomPropDefServiceImpl#addCustomPropDefinition(com.mainsteam.stm.instancelib.obj.CustomPropDefinition)}
	 * .
	 */
	@Test
	public void testAddCustomPropDefinition() {
		CustomPropDefinition d = new CustomPropDefinition();
		d.setCategory("host");
		d.setKey("host_area");
		d.setName("主机所在区域");
		d.setUpdateTime(new Date());
		try {
			propDefService.addCustomPropDefinition(d);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_CUSTOMPROP");
			int rowCount = actualTable.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (d.getKey().equals(actualTable.getValue(i, "PROPKEY"))) {
					assertEquals(d.getCategory(),
							actualTable.getValue(i, "CATEGORY"));
					assertEquals(d.getName(), actualTable.getValue(i, "NAME"));
					assertEquals(
							d.getUpdateTime().getTime(),
							((BigInteger) actualTable.getValue(i, "UPDATETIME"))
									.longValue());
					find = true;
					break;
				}
			}
			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}

		try {
			CustomPropDefinition selectOne = propDefService
					.getCustomPropDefinitionByKey(d.getKey());
			assertNotNull(selectOne);
			assertEquals(d.getCategory(), selectOne.getCategory());
			assertEquals(d.getName(), selectOne.getName());
			assertEquals(d.getUpdateTime(), selectOne.getUpdateTime());
			assertEquals(d.getKey(), selectOne.getKey());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testAddCustomPropDefinitionList() {
		List<CustomPropDefinition> parameters = new ArrayList<>(2);
		CustomPropDefinition d = new CustomPropDefinition();
		d.setCategory("host");
		d.setKey("host_area");
		d.setName("主机所在区域");
		d.setUpdateTime(new Date());
		parameters.add(d);

		CustomPropDefinition d1 = new CustomPropDefinition();
		d1.setCategory("host1");
		d1.setKey("host_area1");
		d1.setName("主机所在区域1");
		d1.setUpdateTime(new Date());
		parameters.add(d1);
		try {
			propDefService.addCustomPropDefinition(parameters);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_CUSTOMPROP");
			int rowCount = actualTable.getRowCount();
			int find = 0;
			for (int i = 0; i < rowCount; i++) {
				if (d.getKey().equals(actualTable.getValue(i, "PROPKEY"))) {
					assertEquals(d.getCategory(), actualTable.getValue(i, "CATEGORY"));
					assertEquals(d.getName(), actualTable.getValue(i, "NAME"));
					assertEquals(
							d.getUpdateTime().getTime(),
							((BigInteger) actualTable.getValue(i, "UPDATETIME"))
									.longValue());
					find++;
				} else if (d1.getKey().equals(
						actualTable.getValue(i, "PROPKEY"))) {
					assertEquals(d1.getCategory(), actualTable.getValue(i, "CATEGORY"));
					assertEquals(d1.getName(), actualTable.getValue(i, "NAME"));
					assertEquals(
							d1.getUpdateTime().getTime(),
							((BigInteger) actualTable.getValue(i, "UPDATETIME"))
									.longValue());
					find++;
				}
			}
			assertEquals(parameters.size(), find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.instancelib.service.impl.CustomPropDefServiceImpl#updateCustomPropDefinition(com.mainsteam.stm.instancelib.obj.CustomPropDefinition)}
	 * .
	 */
	@Test
	public void testUpdateCustomPropDefinition() {
		CustomPropDefinition d = new CustomPropDefinition();
		d.setKey("readyKey1");
		d.setName("主机所在区域1");
		d.setUpdateTime(new Date());
		try {
			propDefService.updateCustomPropDefinition(d);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_CUSTOMPROP");
			int rowCount = actualTable.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (d.getKey().equals(actualTable.getValue(i, "PROPKEY"))) {
					assertEquals("host1", actualTable.getValue(i, "CATEGORY"));
					assertEquals(d.getName(), actualTable.getValue(i, "NAME"));
					assertEquals(
							d.getUpdateTime().getTime(),
							((BigInteger) actualTable.getValue(i, "UPDATETIME"))
									.longValue());
					find = true;
					break;
				}
			}
			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}

		try {
			CustomPropDefinition selectOne = propDefService
					.getCustomPropDefinitionByKey(d.getKey());
			assertNotNull(selectOne);
			assertEquals(d.getName(), selectOne.getName());
			assertEquals(d.getUpdateTime(), selectOne.getUpdateTime());
			assertEquals(d.getKey(), selectOne.getKey());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.instancelib.service.impl.CustomPropDefServiceImpl#getCustomPropDefinitionByKey(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetCustomPropDefinitionByKey() {
		String key1 = "readyKey1";
		CustomPropDefinition selectOne = null;
		try {
			selectOne = propDefService.getCustomPropDefinitionByKey(key1);
			assertNotNull(selectOne);
			assertEquals(key1, selectOne.getKey());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_CUSTOMPROP");
			int rowCount = actualTable.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (selectOne.getKey().equals(
						actualTable.getValue(i, "PROPKEY"))) {
					System.out.println("key="+selectOne.getKey());
					System.out.println("getCategory="+selectOne.getCategory());
					assertEquals(selectOne.getCategory(),
							actualTable.getValue(i, "CATEGORY"));
					assertEquals(selectOne.getName(),
							actualTable.getValue(i, "NAME"));
					assertEquals(
							selectOne.getUpdateTime().getTime(),
							((BigInteger) actualTable.getValue(i, "UPDATETIME"))
									.longValue());
					find = true;
					break;
				}
			}
			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}

	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.instancelib.service.impl.CustomPropDefServiceImpl#getCustomPropDefinitionsByKeys(java.util.List)}
	 * .
	 */
	@Test
	public void testGemoveCustomPropDefinitionsByKeys() {
		String key1 = "readyKey1";
		String key2 = "readyKey2";
		List<String> keys = new ArrayList<>(2);
		keys.add(key1);
		keys.add(key2);
		try {
			propDefService.removeCustomPropDefinitionByKey(keys);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_CUSTOMPROP");
			int rowCount = actualTable.getRowCount();
			assertTrue(rowCount <= 0);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		try {
			CustomPropDefinition selectOne = propDefService
					.getCustomPropDefinitionByKey(key1);
			assertNull(selectOne);
			selectOne = propDefService.getCustomPropDefinitionByKey(key2);
			assertNull(selectOne);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetCustomPropDefinitionsByKeys() {
		String key1 = "readyKey1";
		String key2 = "readyKey2";
		List<String> keys = new ArrayList<>(2);
		keys.add(key1);
		keys.add(key2);
		Map<String, CustomPropDefinition> map = null;
		try {
			map = propDefService.getCustomPropDefinitionsByKeys(keys);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull("getCustomPropDefinitionsByKeys return null map.", map);

		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_CUSTOMPROP");
			int rowCount = actualTable.getRowCount();
			int find = 0;
			for (int i = 0; i < rowCount; i++) {
				String keyFromDb = (String) actualTable.getValue(i, "PROPKEY");
				if (map.containsKey(keyFromDb)) {
					CustomPropDefinition selectOne = map.get(keyFromDb);
					
					System.out.println("key="+selectOne.getKey());
					System.out.println("getCategory="+selectOne.getCategory());
					
					assertEquals(selectOne.getCategory(),
							actualTable.getValue(i, "CATEGORY"));
					assertEquals(selectOne.getName(),
							actualTable.getValue(i, "NAME"));
					assertEquals(
							selectOne.getUpdateTime().getTime(),
							((BigInteger) actualTable.getValue(i, "UPDATETIME"))
									.longValue());
					find++;
				}
			}
			assertEquals("return key not all.", keys.size(), find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
				"src/test/resources/instancelib_customprop_dataset.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}

	@Override
	protected DataSource getDataSource() {
		return this.source;
	}
}
