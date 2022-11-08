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

import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.CustomPropExtendService;
import com.mainsteam.stm.instancelib.util.PropCache;

/**
 * @author xiaoruqiang
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:META-INF/services/public-*-beans.xml" ,
	"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class CustomPropServiceImplTest extends DataSourceBasedDBTestCase {

	@Resource(name="customPropService")
	private CustomPropService customPropService;
	
	@Resource(name="customPropService")
	private CustomPropExtendService customPropExtendService;

	@Resource(name="defaultDataSource")
	private DataSource source;

	@Resource(name="propCache")
	private PropCache propCache;
	
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
		propCache.remove(-3);
		propCache.remove(-2);
		propCache.remove(-1);
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

	@Test
	public void testAddProp() {
		long instanceId = -3;
		String key = "custom_addkey3";
		String[] values = {"custom_a", "custom_b"};
	
		CustomProp prop = new CustomProp();
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);

		try {
			customPropService.addProp(prop);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			CustomProp customProp = customPropService.getPropByInstanceAndKey(
					instanceId, key);
			assertNotNull(customProp);
		
			assertEquals(customProp.getInstanceId(), instanceId);
			assertEquals(customProp.getKey(), key);
				
			assertArrayEquals(customProp.getValues(), values);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			List<String> dbValues = new ArrayList<String>();
			for (int i = 0; i < rowCount; i++) {
				if (key.equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.CUSTOM.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {

					dbValues.add(actualTable.getValue(i, "PROPVALUE")
							.toString());
				}   
			}
			// 根据key 比较 values
			assertEquals(Arrays.asList(values), dbValues);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testAddProps() {
		long instanceId = -2;
		String key = "custom_addkey_2";
		String[] values = {"custom_a", "custom_b"};

		CustomProp prop = new CustomProp();
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);

		String key2 = "custom_addkey_22";
		String[] values2 = {"custom_a2", "custom_b2"};

		CustomProp prop2 = new CustomProp();
		prop2.setInstanceId(instanceId);
		prop2.setKey(key2);
		prop2.setValues(values2);

		List<CustomProp> addProps = new ArrayList<CustomProp>();
		addProps.add(prop);
		addProps.add(prop2);

		try {
			customPropService.addProps(addProps);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			List<CustomProp> customProps = customPropService
					.getPropByInstanceId(instanceId);
			assertNotNull(customProps);
			for (CustomProp customProp : customProps) {
				assertEquals(customProp.getInstanceId(), instanceId);
				if(customProp.getKey().equals(key)){
					assertArrayEquals(customProp.getValues(), values);	
				}else if(customProp.getKey().equals(key2)){
					assertArrayEquals(customProp.getValues(), values2);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			List<String> dbValues = new ArrayList<String>();
			List<String> dbValues2 = new ArrayList<String>();
			for (int i = 0; i < rowCount; i++) {
				if (prop.getKey().equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.CUSTOM.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues.add(actualTable.getValue(i, "PROPVALUE")
							.toString());

				} else if (prop2.getKey().equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.CUSTOM.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues2.add(actualTable.getValue(i, "PROPVALUE")
							.toString());
				}
			}
			// 根据key 比较 values
			assertEquals(Arrays.asList(values), dbValues);
			assertEquals(Arrays.asList(values2), dbValues2);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateProp() {
		String key = "customKey1";
		long instanceId = -1;
		
		CustomProp prop = new CustomProp();
		String[] values ={"customKey1_222", "customKey1_222424"};
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);
		try {
			customPropExtendService.updateProp(prop);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			CustomProp customProp = customPropService.getPropByInstanceAndKey(
					instanceId, key);
			assertNotNull(customProp);
			assertEquals(customProp.getInstanceId(), instanceId);
			assertEquals(customProp.getKey(), key);	
			assertArrayEquals(customProp.getValues(), values);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			List<String> dbValues = new ArrayList<String>();
			for (int i = 0; i < rowCount; i++) {
				if (key.equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.CUSTOM.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {

					dbValues.add(actualTable.getValue(i, "PROPVALUE")
							.toString());
				}
			}
			// 根据key 比较 values
			assertEquals(Arrays.asList(values), dbValues);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateProps() {
		String key = "customKey1";
		long instanceId = -1;
		
		CustomProp prop = new CustomProp();
		String[] values = {"customKey1_11", "customKey1_11"};
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);
		
		String key2 = "customKey2";
		CustomProp prop2 = new CustomProp();
		String[] values2 = {"customKey1_22", "customKey1_22"};
		prop2.setInstanceId(instanceId);
		prop2.setKey(key2);
		prop2.setValues(values2);
		
		List<CustomProp> updateProps = new ArrayList<CustomProp>();
		updateProps.add(prop);
		updateProps.add(prop2);
		try {
			customPropExtendService.updateProps(updateProps);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			List<CustomProp> customProps = customPropService
					.getPropByInstanceId(instanceId);
			assertNotNull(customProps);
			for (CustomProp customProp : customProps) {
				assertEquals(customProp.getInstanceId(), instanceId);
				if(customProp.getKey().equals(key)){
					assertArrayEquals(customProp.getValues(), values);	
				}else if(customProp.getKey().equals(key2)){
					assertArrayEquals(customProp.getValues(), values2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			List<String> dbValues = new ArrayList<String>();
			List<String> dbValues2 = new ArrayList<String>();
			for (int i = 0; i < rowCount; i++) {
				if (prop.getKey().equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.CUSTOM.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues.add(actualTable.getValue(i, "PROPVALUE")
							.toString());

				} else if (prop2.getKey().equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.CUSTOM.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues2.add(actualTable.getValue(i, "PROPVALUE")
							.toString());
				}
			}
			// 根据key 比较 values
			assertEquals(Arrays.asList(values), dbValues);
			assertEquals(Arrays.asList(values2), dbValues2);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void testGetPropByInstanceAndKey() {
		String key = "customKey1";
		long instanceId = -1;
		CustomProp customProp = null;
		try {
			customProp = customPropService.getPropByInstanceAndKey(instanceId,
					key);
			assertNotNull(customProp);
			assertEquals(key, customProp.getKey());
			assertEquals(instanceId, customProp.getInstanceId());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			// boolean find = false;
			List<String> dbValues = new ArrayList<String>();
			for (int i = 0; i < rowCount; i++) {
				if (key.equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.CUSTOM.toString().equals(
								actualTable.getValue(i, "propType"))) {

					dbValues.add(actualTable.getValue(i, "propValue")
							.toString());
				}
			}
			assertEquals(Arrays.asList(customProp.getValues()), dbValues);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetPropByInstanceId() {

		long instanceId = -1;

		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			// 获取数据库中instanceId= 1的值
			Map<String, List<String>> mapList = new HashMap<String, List<String>>();
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())
						&& PropTypeEnum.CUSTOM.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					String key = actualTable.getValue(i, "PROPKEY").toString();
					if (mapList.containsKey(key)) {
						mapList.get(key)
								.add(actualTable.getValue(i, "PROPVALUE")
										.toString());
					} else {
						List<String> list = new ArrayList<String>();
						list.add(actualTable.getValue(i, "PROPVALUE")
								.toString());
						mapList.put(key, list);
					}
				}
			}

			// 测试方法
			List<CustomProp> queryProps = customPropService
					.getPropByInstanceId(instanceId);

			for (CustomProp queryProp : queryProps) {
				for (Map.Entry<String, List<String>> dbMap : mapList.entrySet()) {
					// 通过key 比较 values
					if (dbMap.getKey().equals(queryProp.getKey())) {
						assertEquals(dbMap.getValue(), Arrays.asList(queryProp.getValues()));
					}
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
	}

	@Test
	public void testRemovePropByInstanceAndKey() {
		String key = "customKey1";
		long instanceId = -1;
		try {
			customPropService.removePropByInstanceAndKey(instanceId, key);
			CustomProp customProp = customPropService.getPropByInstanceAndKey(
					instanceId, key);
			assertNull(customProp);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			boolean find = false;
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (key.equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.CUSTOM.toString().equals(
								actualTable.getValue(i, "propType"))) {

					find = true;
					break;
				}
			}
			assertFalse(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testRemovePropByInstance() {
		long instanceId = -1;
		try {
			customPropService.removePropByInstance(instanceId);
			List<CustomProp> customProp = customPropService
					.getPropByInstanceId(instanceId);
			assertNull(customProp);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			boolean find = false;
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())
						&& PropTypeEnum.CUSTOM.toString().equals(
								actualTable.getValue(i, "propType"))) {

					find = true;
					break;
				}
			}
			assertFalse(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
				"src/test/resources/instancelib_custompropvalue_dataset.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}

	@Override
	protected DataSource getDataSource() {
		return this.source;
	}
}
