package com.mainsteam.stm.instancelib.dao;

import java.util.ArrayList;
import java.util.List;

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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

import com.mainsteam.stm.instancelib.CompositeInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.obj.CompositeInstance;
import com.mainsteam.stm.instancelib.obj.CompositeProp;
import com.mainsteam.stm.instancelib.obj.Instance;
import com.mainsteam.stm.instancelib.obj.InstanceRelation;
import com.mainsteam.stm.instancelib.obj.PathRelation;
import com.mainsteam.stm.instancelib.obj.Relation;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:META-INF/services/public-*-beans.xml" ,
	"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class CompositeInstanceServiceImplTest extends DataSourceBasedDBTestCase {

	@Resource(name="defaultDataSource")
	private DataSource source;

	
	@Resource
	private CompositeInstanceService compositeInstanceService;
	
	@Rule 
	public ExpectedException thrown = ExpectedException.none();

	//IMemcache<QueryCompositeInstance> cache = MemCacheFactory.getRemoteMemCache(QueryCompositeInstance.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity\\cap_libs");
		System.setProperty("testCase","test");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		//删除缓存中数据，方便测试
		//cache.delete(3333+"");
		super.tearDown();
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
				"src/test/resources/instancelib_composite_main_dataset.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}

	@Override
	protected DataSource getDataSource() {
		return this.source;
	}

	@Test
	public void testAddCompositeInstance() {
		
		CompositeInstance def = new CompositeInstance();

		def.setName("compositeName11");
		def.setInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);

		/*
		 * 添加实例集合
		 */
		List<Instance> addInstances = new ArrayList<>();
		Instance parentInstance = new Instance();
		parentInstance.setId(-23);
		parentInstance.setName("resource");
		Instance parentInstance2 = new Instance();
		parentInstance2.setId(3333);
		parentInstance2.setName("composite");
		addInstances.add(parentInstance2);
		def.setElements(addInstances);

		/*
		 * 添加属性
		 */
		List<CompositeProp> listProps = new ArrayList<>();
		
		CompositeProp prop = new CompositeProp();
		String key = "composite_custom_addkey3";
		String[] custom_values = {"composite_a","composite_b"};
		prop.setKey(key);
		prop.setValues(custom_values);
		listProps.add(prop);
		def.setProps(listProps);
		
		/*
		 * 添加关系
		 */
		List<Relation> relations = new ArrayList<>();
		PathRelation relation = new PathRelation();
		relation.setFromInstanceId(1111);
		relation.setFromInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);
		relation.setToInstanceId(11);
		relation.setToInstanceType(InstanceTypeEnum.RESOURCE);
		
		PathRelation relation1 = new PathRelation();
		relation1.setFromInstanceId(2222);
		relation1.setFromInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);
		relation1.setToInstanceId(22);
		relation1.setToInstanceType(InstanceTypeEnum.RESOURCE);
	
		relations.add(relation);
		relations.add(relation1);
		InstanceRelation instanceReatiom = new InstanceRelation();
		instanceReatiom.setRelations(relations);
		
		def.setInstanceReatiom(instanceReatiom);
		long instanceId = 0;
		try {
			instanceId = compositeInstanceService.addCompositeInstance(def);
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
		CompositeInstance newCompositeInstance = null;
		try {
			newCompositeInstance = compositeInstanceService
					.getCompositeInstance(instanceId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(newCompositeInstance);

		assertEquals(newCompositeInstance.getId(), def.getId());

		assertEquals(newCompositeInstance.getInstanceType(),
				def.getInstanceType());
		IDataSet actualDataset = null;
		try {
			actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_COMPOSITE_MAIN");
			int rowCount = actualTable.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {

					assertEquals(actualTable.getValue(i, "InstanceName"),
							def.getName());

					assertEquals(actualTable.getValue(i, "InstanceType")
							.toString(), def.getInstanceType().toString());

					find = true;
				}
			}

			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}

		/*
		 * 查找属性值
		 */
		int findCount = 0;

		try {
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (key.equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())) {
					findCount++;
				}
			}
			List<CompositeProp> props = newCompositeInstance.getProps();
			assertEquals(props.get(0).getValues().length, findCount);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		/*
		 * 查找实例集合值
		 */
		findCount = 0;
		try {
			ITable actualTable = actualDataset
					.getTable("stm_instancelib_collection");
			int rowCount = actualTable.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {
					for (Instance instance2 : newCompositeInstance
							.getElements()) {
						if (instance2.getId() == Long.parseLong(actualTable
								.getValue(i, "CONTAININSTANCEID").toString())) {
							if (instance2 instanceof ResourceInstance) {
								assertEquals(
										actualTable.getValue(i,
												"CONTAININSTANCETYPE")
												.toString(),
										InstanceTypeEnum.RESOURCE.toString());
							} else {
								assertEquals(
										actualTable.getValue(i,
												"CONTAININSTANCETYPE")
												.toString(),
										InstanceTypeEnum.BUSINESS_APPLICATION.toString());
							}
							findCount++;
						}
					}
				}
			}
			assertEquals(newCompositeInstance.getElements().size(), findCount);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
		/*
		 * 查找关系 
		 */
		findCount = 0;
		try {
			ITable actualTable = actualDataset
					.getTable("stm_instancelib_relation");
			int rowCount = actualTable.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {
					for (Relation realation : newCompositeInstance.getInstanceReatiom().getRelations()) {
						if(relation instanceof PathRelation){
							PathRelation temPathRelation = (PathRelation)realation;
							if(temPathRelation.getFromInstanceId() == Long.parseLong(actualTable.getValue(i,
									"FROMINSTANCEID").toString())){
								assertEquals(temPathRelation.getToInstanceId(),Long.parseLong(actualTable.getValue(i,
										"TOINSTANCEID").toString()));
								assertEquals(temPathRelation.getFromInstanceType().toString(),actualTable.getValue(i,
										"FROMINSTANCETYPE").toString());
								assertEquals(temPathRelation.getToInstanceType().toString(),actualTable.getValue(i,
										"TOINSTANCETYPE").toString());
								findCount++;
							}
						}
					}
				}
			}
			assertEquals(newCompositeInstance.getInstanceReatiom().getRelations().size(), findCount);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateCompositeInstance() {

		long instanceId = 3333;

		CompositeInstance def = new CompositeInstance();

		def.setId(instanceId);
		def.setName("compositeName44");
		def.setInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);

		/*
		 * 实例集合
		 */
		List<Instance> addInstances = new ArrayList<>();
		Instance parentInstance = new Instance();
		parentInstance.setId(11);
		parentInstance.setName("resource11");
		Instance parentInstance2 = new Instance();
		parentInstance2.setId(4444);
		parentInstance2.setName("composite2222");
		addInstances.add(parentInstance2);

		def.setElements(addInstances);

		/*
		 * 属性
		 */
		List<CompositeProp> listProps = new ArrayList<>();

		String key = "composite_custom_updatekey3";
		String[] custom_values = {"composite_update_custom_a","composite_update_custom_b"};

		CompositeProp prop = new CompositeProp();
		prop.setInstanceId(instanceId);
		prop.setKey(key);
		prop.setValues(custom_values);
		listProps.add(prop);


		def.setProps(listProps);

		try {
			compositeInstanceService.updateCompositeInstance(def);
		} catch (InstancelibException e) {
			e.printStackTrace();
		} 

		CompositeInstance newCompositeInstance = null;
		try {
			newCompositeInstance = compositeInstanceService
					.getCompositeInstance(instanceId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(newCompositeInstance);

		assertEquals(newCompositeInstance.getId(), def.getId());

		assertEquals(newCompositeInstance.getInstanceType(),
				def.getInstanceType());
		IDataSet actualDataset = null;
		try {
			actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_COMPOSITE_MAIN");
			int rowCount = actualTable.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {

					assertEquals(actualTable.getValue(i, "InstanceName"),
							def.getName());

					assertEquals(actualTable.getValue(i, "InstanceType")
							.toString(), def.getInstanceType().toString());

					find = true;
				}
			}

			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}

		/*
		 * 查找属性值
		 */
		int findCount = 0;
		int totalCount = custom_values.length;
		try {
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {
					findCount++;
				}
			}
			assertEquals(totalCount, findCount);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		/*
		 * 查找实例集合值
		 */
		findCount = 0;
		try {
			ITable actualTable = actualDataset
					.getTable("stm_instancelib_collection");
			int rowCount = actualTable.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {
					for (Instance instance2 : newCompositeInstance
							.getElements()) {
						if (instance2.getId() == Long.parseLong(actualTable
								.getValue(i, "CONTAININSTANCEID").toString())) {
							if (instance2 instanceof ResourceInstance) {
								assertEquals(
										actualTable.getValue(i,
												"CONTAININSTANCETYPE")
												.toString(),
										InstanceTypeEnum.RESOURCE.toString());
							} else {
								assertEquals(
										actualTable.getValue(i,
												"CONTAININSTANCETYPE")
												.toString(),
										InstanceTypeEnum.BUSINESS_APPLICATION.toString());
							}
							findCount++;
						}
					}
				}
			}
			assertEquals(newCompositeInstance.getElements().size(), findCount);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testRemoveCompositeInstance() {
		long instanceId = 4444;
		try {
			compositeInstanceService.removeCompositeInstance(instanceId);
			CompositeInstance deleteAfter = compositeInstanceService
					.getCompositeInstance(instanceId);
			assertNull(deleteAfter);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_COMPOSITE_MAIN");
			int rowCount = actualTable.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {
					find = true;
				}
			}
			assertFalse(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetCompositeInstance() throws InstancelibException, Exception {
		long instanceId = 2222;
		CompositeInstance instance = null;
		try {
			instance = compositeInstanceService
					.getCompositeInstance(instanceId);
			assertNotNull(instance);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		IDataSet actualDataset = null;
		/*
		 * 查询复合实例主表
		 */
		try {
			actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_COMPOSITE_MAIN");
			int rowCount = actualTable.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {

					assertEquals(actualTable.getValue(i, "InstanceName"),
							instance.getName());
					assertEquals(actualTable.getValue(i, "InstanceType")
							.toString(), instance.getInstanceType().toString());
					find = true;
				}
			}
			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}

		/*
		 * 验证复合实例下面的子实例
		 */
		List<Instance> listInstances = instance.getElements();
		

		assertNotNull(listInstances);

		int expectedChildResoureceCount = 0;

		int expectedChildCompositeCount = 0;
		// 查找某个复合实例下的资源实例个数，复合实例个数
		for (Instance instance2 : listInstances) {
			if (instance2 instanceof ResourceInstance) {
				expectedChildResoureceCount++;
			} else {
				expectedChildCompositeCount++;
			}
		}

		int actualChildResoureceCount = 0;

		int actualCompositeResoureceCount = 0;
		/*
		 * 查询复合实例集合表
		 */
		try {
			ITable actualTable = actualDataset
					.getTable("stm_instancelib_collection");
			int rowCount = actualTable.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {
					for (Instance instance2 : listInstances) {
						if (instance2.getId() == Long.parseLong(actualTable
								.getValue(i, "CONTAININSTANCEID").toString())) {
							if (instance2 instanceof ResourceInstance) {
								actualChildResoureceCount++;
								assertEquals(
										actualTable.getValue(i,
												"CONTAININSTANCETYPE")
												.toString(),
										InstanceTypeEnum.RESOURCE.toString());
							} else {
								actualCompositeResoureceCount++;
								assertEquals(
										actualTable.getValue(i,
												"CONTAININSTANCETYPE")
												.toString(),
										InstanceTypeEnum.BUSINESS_APPLICATION.toString());
							}

						}
					}
				}
			}

			assertEquals(expectedChildResoureceCount, actualChildResoureceCount);
			assertEquals(expectedChildCompositeCount,
					actualCompositeResoureceCount);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}

		/*
		 * 查找复合实例属性值，已经改复合实例子子实例属性值
		 */
		List<CompositeProp> CompositeProps = instance.getProps();

		assertNotNull(CompositeProps);
		int compositePropCount = 0;
		for (CompositeProp compositeProp : CompositeProps) {
			compositePropCount += compositeProp.getValues().length;
		}
		/*
		 * 查找数据库中复合实例属性值
		 */
		int findCompositePropCount = 0;
		try {
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			// boolean find = false;
			// List<String> dbValues = new ArrayList<String>();
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {
					findCompositePropCount++;
				}
			}
			// List<String> queryValues = new ArrayList<String>();
			// for (CustomProp customProp : customProps) {
			// queryValues.addAll(customProp.getValues());
			// }
			assertEquals(compositePropCount, findCompositePropCount);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
		//通过查询提供出去的类，防止用户再次用这个类做一些更新，插入操作。插入，更新，必须new一个实例对象
		thrown.expect(InstancelibRuntimeException.class);
		instance.setName("name");
	}

	@Test
	public void testGetCompositeInstanceByInstanceType() {

		InstanceTypeEnum type = InstanceTypeEnum.BUSINESS_APPLICATION;
		List<CompositeInstance> instances = null;
		try {
			instances = compositeInstanceService
					.getCompositeInstanceByInstanceType(type);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(instances);

		int findCount = 0;

		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_COMPOSITE_MAIN");
			int rowCount = actualTable.getRowCount();

			for (int i = 0; i < rowCount; i++) {
				for (CompositeInstance instance : instances) {
					if (instance.getId() == Long.parseLong(actualTable
							.getValue(i, "INSTANCEID").toString())) {
						assertEquals(actualTable.getValue(i, "InstanceName"),
								instance.getName());
						assertEquals(actualTable.getValue(i, "InstanceType")
								.toString(), instance.getInstanceType()
								.toString());
						findCount++;
					}
				}
			}
			assertEquals(findCount, instances.size());
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetCompositeInstanceLikeName() {
		String name = "Name";
		List<CompositeInstance> instances = null;
		try {
			instances = compositeInstanceService
					.getCompositeInstanceLikeName(name);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(instances);

		int findCount = 0;

		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_COMPOSITE_MAIN");
			int rowCount = actualTable.getRowCount();

			for (int i = 0; i < rowCount; i++) {
				for (CompositeInstance instance : instances) {
					if (instance.getId() == Long.parseLong(actualTable
							.getValue(i, "INSTANCEID").toString())) {
						assertEquals(actualTable.getValue(i, "InstanceName"),
								instance.getName());
						assertEquals(actualTable.getValue(i, "InstanceType")
								.toString(), instance.getInstanceType()
								.toString());
						findCount++;
					}
				}
			}
			assertEquals(findCount, instances.size());
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}
}
