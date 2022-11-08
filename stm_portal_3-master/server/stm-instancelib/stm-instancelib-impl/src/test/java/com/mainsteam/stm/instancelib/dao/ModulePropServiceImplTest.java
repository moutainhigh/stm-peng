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

import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.ModulePropExtendService;
import com.mainsteam.stm.instancelib.util.PropCache;

/**
 * @author xiaoruqiang
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:META-INF/services/public-*-beans.xml" ,
	"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class ModulePropServiceImplTest extends DataSourceBasedDBTestCase {

	@Resource(name="modulePropService")
	private ModulePropService modulePropService;
	
	
	@Resource(name="modulePropService")
	private ModulePropExtendService modulePropExtendService;

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
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
				"src/test/resources/instancelib_modulepropvalue_dataset.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}

	@Override
	protected DataSource getDataSource() {
		return this.source;
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
	 * {@link com.mainsteam.stm.instancelib.impl.ModulePropDefServiceImpl#start()}
	 * .
	 */
	@Test
	public void testStart() {
	}

	@Test
	public void testAddProp() {
		long instanceId = -3;
		String key = "module_addkey3";
		String[] values = {"module_a", "module_b"};
	
		ModuleProp prop = new ModuleProp();
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);

		try {
			modulePropExtendService.addProp(prop);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			ModuleProp moduleProp = modulePropService.getPropByInstanceAndKey(
					instanceId, key);
			assertNotNull(moduleProp);
			
			assertEquals(moduleProp.getInstanceId(), instanceId);
			assertEquals(moduleProp.getKey(), key);
			assertArrayEquals(values, moduleProp.getValues());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			String[] dbValues = new String[values.length];
			int j = 0;
			for (int i = 0; i < rowCount; i++) {
				if (key.equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.MODULE.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues[j] = actualTable.getValue(i, "PROPVALUE").toString();
					j++;
				}
			}
			// 根据key 比较 values
			assertArrayEquals(values, dbValues);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testAddProps() {
		long instanceId = -2;
		String key = "module_addkey_2";
		String[] values = {"module_a", "module_b"};

		ModuleProp prop = new ModuleProp();
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);

		String key2 = "module_addkey_22";
		String[] values2 = {"module_a2", "module_b2"};

		ModuleProp prop2 = new ModuleProp();
		prop2.setInstanceId(instanceId);
		prop2.setKey(key2);
		prop2.setValues(values2);

		List<ModuleProp> addProps = new ArrayList<ModuleProp>();
		addProps.add(prop);
		addProps.add(prop2);

		try {
			modulePropExtendService.addProps(addProps);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			List<ModuleProp> moduleProps = modulePropService
					.getPropByInstanceId(instanceId);
			assertNotNull(moduleProps);
			for (ModuleProp moduleProp : moduleProps) {
				assertEquals(moduleProp.getInstanceId(), instanceId);
				if(moduleProp.getKey().equals(key)){
					assertArrayEquals(moduleProp.getValues(), values);	
				}else if(moduleProp.getKey().equals(key2)){
					assertArrayEquals(moduleProp.getValues(), values2);
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
		    String[] dbValues = new String[values.length];
			String[] dbValues2 = new String[values2.length];
			int j1 = 0;
			int j2 = 0;
			for (int i = 0; i < rowCount; i++) {
				if (prop.getKey().equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.MODULE.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues[j1] =actualTable.getValue(i, "PROPVALUE").toString();
					j1++;
				} else if (prop2.getKey().equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.MODULE.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues2[j2] = actualTable.getValue(i, "PROPVALUE").toString();
					j2++;
				}
			}
			// 根据key 比较 values
			assertArrayEquals(values, dbValues);
			assertArrayEquals(values2, dbValues2);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateProp() {
		String key = "moduleKey1";
		long instanceId = -1;
		ModuleProp prop = new ModuleProp();
		String[] values = {"moduleKey1_222", "moduleKey1_222424"};
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);
		try {
			modulePropService.updateProp(prop);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			ModuleProp moduleProp = modulePropService.getPropByInstanceAndKey(
					instanceId, key);
			assertNotNull(moduleProp);
			assertEquals(moduleProp.getInstanceId(), instanceId);
			assertEquals(moduleProp.getKey(), key);
				
			assertArrayEquals(moduleProp.getValues(), values);
				
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			String[] dbValues = new String[values.length];
			int j = 0;
			for (int i = 0; i < rowCount; i++) {
				if (key.equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.MODULE.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {

					dbValues[j] = actualTable.getValue(i, "PROPVALUE").toString();
					j++;
				}
			}
			// 根据key 比较 values
			assertArrayEquals(values, dbValues);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateProps() {
		String key = "moduleKey1";
		long instanceId = -1;
		
		ModuleProp prop = new ModuleProp();
		String[] values = {"moduleKey1_11", "moduleKey1_11"};
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);
		
		String key2 = "moduleKey2";
		ModuleProp prop2 = new ModuleProp();
		String[] values2 = {"moduleKey1_22", "moduleKey1_22"};
		prop2.setInstanceId(instanceId);
		prop2.setKey(key2);
		prop2.setValues(values2);
		
		List<ModuleProp> updateProps = new ArrayList<ModuleProp>();
		updateProps.add(prop);
		updateProps.add(prop2);
		try {
			modulePropService.updateProps(updateProps);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			List<ModuleProp> moduleProps = modulePropService
					.getPropByInstanceId(instanceId);
			assertNotNull(moduleProps);
			for (ModuleProp moduleProp : moduleProps) {
				assertEquals(moduleProp.getInstanceId(), instanceId);
				if(moduleProp.getKey().equals(key)){
					assertArrayEquals(moduleProp.getValues(), values);
				}else if(moduleProp.getKey().equals(key2)){
					assertArrayEquals(moduleProp.getValues(), values2);
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
			String[] dbValues = new String[values.length];
			String[] dbValues2 = new String[values2.length];
			int j0 = 0;
			int j1 = 0;
			for (int i = 0; i < rowCount; i++) {
				if (prop.getKey().equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.MODULE.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues[j0] = actualTable.getValue(i, "PROPVALUE").toString();
					j0 ++;
				} else if (prop2.getKey().equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.MODULE.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues2[j1] = actualTable.getValue(i, "PROPVALUE").toString();
					j1++;
				}
			}
			// 根据key 比较 values
			assertArrayEquals(values, dbValues);
			assertArrayEquals(values2, dbValues2);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void testGetPropByInstanceAndKey() {
		String key = "moduleKey1";
		long instanceId = -1;
		ModuleProp moduleProp = null;
		try {
			moduleProp = modulePropService.getPropByInstanceAndKey(instanceId,
					key);
			assertNotNull(moduleProp);
			assertEquals(key, moduleProp.getKey());
			assertEquals(instanceId, moduleProp.getInstanceId());
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
						&& PropTypeEnum.MODULE.toString().equals(
								actualTable.getValue(i, "propType"))) {

					dbValues.add(actualTable.getValue(i, "propValue")
							.toString());
				}
			}
			List<String> queryValues = new ArrayList<String>();
		
			queryValues.addAll(Arrays.asList(moduleProp.getValues()));
			
			assertEquals(queryValues, dbValues);
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
						&& PropTypeEnum.MODULE.toString().equals(
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
			List<ModuleProp> queryProps = modulePropService
					.getPropByInstanceId(instanceId);

			for (ModuleProp queryProp : queryProps) {
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
		String key = "moduleKey1";
		long instanceId = -1;
		try {
			modulePropExtendService.removePropByInstanceAndKey(instanceId, key);
			ModuleProp moduleProp = modulePropService.getPropByInstanceAndKey(
					instanceId, key);
			assertNull(moduleProp);
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
						&& PropTypeEnum.MODULE.toString().equals(
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
			modulePropExtendService.removePropByInstance(instanceId);
			List<ModuleProp> moduleProp = modulePropService
					.getPropByInstanceId(instanceId);
			assertNull(moduleProp);
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
						&& PropTypeEnum.MODULE.toString().equals(
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

	
}
