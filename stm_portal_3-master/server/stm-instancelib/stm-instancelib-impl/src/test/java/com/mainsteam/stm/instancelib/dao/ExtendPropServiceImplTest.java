/**
 * 
 */
package com.mainsteam.stm.instancelib.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static org.junit.Assert.assertArrayEquals;

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

import com.mainsteam.stm.instancelib.ExtendPropService;
import com.mainsteam.stm.instancelib.obj.ExtendProp;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.ExatendPropExtendService;
import com.mainsteam.stm.instancelib.util.PropCache;

/**
 * @author xiaoruqiang
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:META-INF/services/public-*-beans.xml" ,
	"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class ExtendPropServiceImplTest extends DataSourceBasedDBTestCase {

	@Resource
	private ExtendPropService extendPropService;
	
	@Resource
	private ExatendPropExtendService exatendPropExtendService;

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
		propCache.remove(-1);
		propCache.remove(-2);
		propCache.remove(-3);
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
	 * {@link com.mainsteam.stm.instancelib.impl.ExtendPropDefServiceImpl#start()}
	 * .
	 */
	@Test
	public void testStart() {
		// OK,do nothing.
	}

	@Test
	public void testAddProp() {
		long instanceId = -3;
		String key = "extend_addkey3";
		String[] values = {"extend_a", "extend_b"};
	
		ExtendProp prop = new ExtendProp();
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);

		try {
			extendPropService.addProp(prop);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			ExtendProp extendProp = extendPropService.getPropByInstanceAndKey(
					instanceId, key);
			assertNotNull(extendProp);
			assertEquals(extendProp.getInstanceId(), instanceId);
			assertEquals(extendProp.getKey(), key);	
			assertArrayEquals(values, prop.getValues());
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
						&& PropTypeEnum.EXTEND.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {

					dbValues[j] = actualTable.getValue(i, "PROPVALUE")
							.toString();
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
		String key = "extend_addkey_2";
		String[] values = {"extend_a", "extend_b"};

		ExtendProp prop = new ExtendProp();
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);

		String key2 = "extend_addkey_22";
		String[] values2 = {"extend_a2", "extend_b2"};

		ExtendProp prop2 = new ExtendProp();
		prop2.setInstanceId(instanceId);
		prop2.setKey(key2);
		prop2.setValues(values2);

		List<ExtendProp> addProps = new ArrayList<ExtendProp>();
		addProps.add(prop);
		addProps.add(prop2);

		try {
			extendPropService.addProps(addProps);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			List<ExtendProp> extendProps = extendPropService
					.getPropByInstanceId(instanceId);
			assertNotNull(extendProps);
			for (ExtendProp extendProp : extendProps) {
				assertEquals(extendProp.getInstanceId(), instanceId);
				if(extendProp.getKey().equals(key)){
					assertArrayEquals(values, extendProp.getValues());
				}else if(extendProp.getKey().equals(key2)){
					assertArrayEquals(values2, extendProp.getValues());
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
						&& PropTypeEnum.EXTEND.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues[j0] = actualTable.getValue(i, "PROPVALUE")
							.toString();
					j0++;
				} else if (prop2.getKey().equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.EXTEND.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues2[j1] = actualTable.getValue(i, "PROPVALUE")
							.toString();
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
	public void testUpdateProp() {
		String key = "extendKey1";
		long instanceId = -1;
		
		ExtendProp prop = new ExtendProp();
		String[] values = {"extendKey1_222", "extendKey1_222424"};
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);
		try {
			exatendPropExtendService.updateProp(prop);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			ExtendProp extendProp = extendPropService.getPropByInstanceAndKey(
					instanceId, key);
			assertNotNull(extendProp);
			assertEquals(extendProp.getInstanceId(), instanceId);
			assertEquals(extendProp.getKey(), key);		
			assertArrayEquals(extendProp.getValues(), values);
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
						&& PropTypeEnum.EXTEND.toString().equals(
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
		String key = "extendKey1";
		long instanceId = -1;
		
		ExtendProp prop = new ExtendProp();
		String[] values = {"extendKey1_11", "extendKey1_11"};
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(values);
		
		String key2 = "extendKey2";
		ExtendProp prop2 = new ExtendProp();
		String[] values2 = {"extendKey1_22", "extendKey1_22"};
		prop2.setInstanceId(instanceId);
		prop2.setKey(key2);
		prop2.setValues(values2);
		
		List<ExtendProp> updateProps = new ArrayList<ExtendProp>();
		updateProps.add(prop);
		updateProps.add(prop2);
		try {
			exatendPropExtendService.updateProps(updateProps);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			List<ExtendProp> extendProps = extendPropService
					.getPropByInstanceId(instanceId);
			assertNotNull(extendProps);
			for (ExtendProp extendProp : extendProps) {
				assertEquals(extendProp.getInstanceId(), instanceId);
				if(extendProp.getKey().equals(key)){
					assertArrayEquals(extendProp.getValues(), values);	
				}else if(extendProp.getKey().equals(key2)){
					assertArrayEquals(extendProp.getValues(), values2);
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
						&& PropTypeEnum.EXTEND.toString().equals(
								actualTable.getValue(i, "PROPTYPE"))) {
					dbValues[j0] = actualTable.getValue(i, "PROPVALUE").toString();
					j0++;
				} else if (prop2.getKey().equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.EXTEND.toString().equals(
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
		String key = "extendKey2";
		long instanceId = -1;
		ExtendProp extendProp = null;
		try {
			extendProp = extendPropService.getPropByInstanceAndKey(instanceId,
					key);
			assertNotNull(extendProp);
			assertEquals(key, extendProp.getKey());
			assertEquals(instanceId, extendProp.getInstanceId());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			String[] dbValues = new String[extendProp.getValues().length];
			int j = 0;
			for (int i = 0; i < rowCount; i++) {
				if (key.equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())
						&& PropTypeEnum.EXTEND.toString().equals(
								actualTable.getValue(i, "propType"))) {

					dbValues[j] = actualTable.getValue(i, "propValue").toString();
					j ++;
				}
			}
			assertArrayEquals(extendProp.getValues(), dbValues);
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
						&& PropTypeEnum.EXTEND.toString().equals(
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
			List<ExtendProp> queryProps = extendPropService
					.getPropByInstanceId(instanceId);

			for (ExtendProp queryProp : queryProps) {
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
		String key = "extendKey1";
		long instanceId = -1;
		try {
			extendPropService.removePropByInstanceAndKey(instanceId, key);
			ExtendProp extendProp = extendPropService.getPropByInstanceAndKey(
					instanceId, key);
			assertNull(extendProp);
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
						&& PropTypeEnum.EXTEND.toString().equals(
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
			extendPropService.removePropByInstance(instanceId);
			List<ExtendProp> extendProp = extendPropService
					.getPropByInstanceId(instanceId);
			assertNull(extendProp);
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
						&& PropTypeEnum.EXTEND.toString().equals(
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
				"src/test/resources/instancelib_extendpropvalue_dataset.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}

	@Override
	protected DataSource getDataSource() {
		return this.source;
	}
}
